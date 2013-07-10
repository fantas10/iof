/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/01/20
 */
package pt.ptsi.stfe.io.engine;

import static pt.ptsi.stfe.io.engine.IOService.PROPERTIES.IOService;
import static pt.ptsi.stfe.io.engine.IOService.PROPERTIES.IOServiceJobs;
import static pt.ptsi.stfe.io.engine.jobs.IOJob.PROPERTIES.IOJobKey;
import static pt.ptsi.stfe.io.engine.xml.IOXmlScheduling.PROPERTIES.CRON_EXPRESSION;
import static pt.ptsi.stfe.io.engine.xml.IOXmlScheduling.PROPERTIES.DATE;
import static pt.ptsi.stfe.io.engine.xml.IOXmlScheduling.PROPERTIES.INTERVAL_IN;
import static pt.ptsi.stfe.io.engine.xml.IOXmlScheduling.PROPERTIES.INTERVAL_VALUE;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import pt.ptsi.stfe.io.engine.jobs.IOJob;
import pt.ptsi.stfe.io.engine.jobs.IONature;
import pt.ptsi.stfe.io.engine.jobs.JobKey;
import pt.ptsi.stfe.io.engine.jobs.SequentialJobChain;
import pt.ptsi.stfe.io.engine.xml.IOXmlConfig;
import pt.ptsi.stfe.io.engine.xml.IOXmlScheduling;
import pt.ptsi.stfe.io.engine.xml.IOXmlScheduling.TimeType;

/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public final class ServiceImpl implements IOService {

	final static Logger log = IOEngine.logger;
	
	private final ServiceKey key;
	private final Document xmlDocument;
	private String description;
	
	/**
	 * Collection of JobKey's (unique job identification) with it's config <code>JobDataMap</code>
	 */
	private final Map<JobKey, PropertyMap> serviceJobs;
	
	private PropertyMap schedulingInfo = null;


	/**
	 * 
	 * @param doc xml document
	 * @param name service name
	 * @param domain domain - optional
	 * @param machine server's address or IP
	 * @param hash hashkey that identifies the service file
	 */
	public ServiceImpl(Document doc, ServiceKey key) {
		this.xmlDocument = doc;
		this.key = key;
		//this.serviceJobs = new HashMap<JobKey, PropertyMap>();
		this.serviceJobs = new LinkedHashMap<JobKey, PropertyMap>();
	}

	
	
	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.IOService#getServiceId()
	 */
	@Override
	public ServiceKey getServiceId() {
		return key;
	}



	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.IOService#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}
	
	

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}



	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.IOService#getDomain()
	 */
	@Override
	public IODomain getDomain() {
		return key.getDomain();
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.IOService#getName()
	 */
	@Override
	public String getName() {
		return key.getServiceName();
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.IOService#isValid()
	 */
	@Override
	public boolean isValid() {
		// TODO this always returns true - if schema fails should be false!
		return true;
	}


	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.IOXmlConfig#parseConfiguration(org.jdom.Element)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PropertyMap parseConfiguration(Element configElement)
			throws JDOMException, IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		
		
		this.description = configElement.getAttribute("description").getValue();
		// process nodes
		List<?> nodes = configElement.getChildren();
		// 
		Element nodeElement;
		String nodeName;
		IOXmlConfig xmlJobConfig = null;
		String scope = null;
		String jobName = null;
		JobKey jobKey = null;
		IONature nature = null;
		Class type;
		PropertyMap jobInfo = null;
		for (Object oNode : nodes) {
			// valid element
			if (oNode instanceof Element) {
				nodeElement = (Element) oNode;
				nodeName = nodeElement.getName();
				// top level service Elements 
				if ("jobList".equals(nodeName)) {
					Element jobElem = null;
					boolean sequential = "sequential".equals(nodeElement.getAttribute("type").getValue());
					// handle JobList and Nature of it's jobs
					List<?> jobNodes = nodeElement.getChildren();
					for (Object j : jobNodes) {
						jobElem = (Element) j;
						scope = jobElem.getAttributeValue("scope");
						jobName = jobElem.getAttributeValue("name");
						//
						if ("internal".equals(scope) || "extension".equals(scope)) {
							// Uses framework for Jobs API 
							log.trace("Checking for type "+jobElem.getAttributeValue("type"));
							type = (Class<IOXmlConfig>) Class.forName(jobElem.getAttributeValue("type"));
							log.trace("Got type "+jobElem.getAttributeValue("type"));
							//
							jobKey = new JobKey(getServiceId(), jobName, (Class<IOJob>) type );
							//
							xmlJobConfig = (IOXmlConfig) type.newInstance();
							log.debug("Got new instance from " + xmlJobConfig.getClass().getName());
							// put this service in job Context
							jobInfo = xmlJobConfig.parseConfiguration(jobElem);
							jobInfo.consume(IOJobKey, jobKey);
							//
							this.serviceJobs.put(jobKey, jobInfo);
							log.debug("job " + jobKey + " harvested...");
							//
						} else if ("external".equals(scope)) {
							log.fatal("EXTERNAL scope NOT IMPLEMENTED YET... job -NOT- Harvested...");
						} else {
							log.fatal("SCOPE NOT Recognized..." + scope);
						}
					}
					
					
				} else if ("scheduling".equals(nodeName)) {
					// configure whole service scheduling
					log.debug("Found Scheduling...");
					Element schedElem = (Element) nodeElement;
					IOXmlScheduling xmlSched = new IOXmlScheduling();
					//
					this.schedulingInfo = xmlSched.parseConfiguration(schedElem);;
					
				} else if ("notification".equals(nodeName)) {
					// configure Notification
					log.debug("Found Notification...");
					
				} else if ("log".equals(nodeName)) {
					// configure Log Services for Service @see Log4J
					
					log.debug("Found Log Handlers...");

					
				} else {
					// TODO Exception, invalid XML block
					// Should not happen here, since Schema Validation should fail
					log.error("Found unrecognized node in service parsing ("+getServiceId()+"). Should not happen against Schema Validation. Node: "+nodeName);
					throw new JDOMException("Found unrecognized node in service parsing ("+getServiceId()+"). Should not happen against Schema Validation. Node: "+nodeName);
				}
			}
		}		

		return null; //no return here
	}


	
	
	

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.IOService#scheduleJobChain(org.quartz.Scheduler)
	 */
	@Override
	public void scheduleJobChain(Scheduler engineScheduler) throws SchedulerException {
		
		JobDataMap serviceInfo = new JobDataMap();
		serviceInfo.put(IOService.value(), this.getServiceId());
		serviceInfo.put(IOServiceJobs.value(), this.serviceJobs);
		
		org.quartz.JobKey quartzKey = new org.quartz.JobKey(getServiceId().getKey(), getServiceId().getMachineName());
		JobDetail jd = JobBuilder.newJob(SequentialJobChain.class)
				.withIdentity(quartzKey)
				.usingJobData(serviceInfo)
				.build();
		
		TriggerKey triggerKey = new TriggerKey(getServiceId()+"_Trigger", getServiceId().getMachineName());
		Trigger trigger = null;
		ScheduleBuilder<?> sb = null;
		// Scheduling
		if (this.schedulingInfo != null) {
			
			String strCron = this.schedulingInfo.getString(CRON_EXPRESSION);
			if (strCron != null) {
				CronExpression cron;
				try {
					cron = new CronExpression(strCron);
					sb = CronScheduleBuilder.cronSchedule(cron)
							.inTimeZone(IOEngine.DEFAULT_TIME_ZONE)
							.withMisfireHandlingInstructionFireAndProceed();				
				} catch (ParseException e) {
					log.error(e); // will not happen since is validated in IOXmlScheduling 
				}
			} else {
				//
				int intervalValue = this.schedulingInfo.getInt(INTERVAL_VALUE);
				TimeType intervalIn = (TimeType) this.schedulingInfo.getEnumObject(INTERVAL_IN);
				switch (intervalIn) {
					case HOURS:
						sb = SimpleScheduleBuilder.repeatHourlyForever().withIntervalInHours(intervalValue);
						break;
					case MINUTES:
						sb = SimpleScheduleBuilder.repeatMinutelyForever().withIntervalInMinutes(intervalValue);
						break;
					case SECONDS:
						sb = SimpleScheduleBuilder.repeatSecondlyForever().withIntervalInSeconds(intervalValue);
						break;
				}
			}
			
			trigger = TriggerBuilder.newTrigger()
					.withIdentity(triggerKey)
					.withSchedule(sb)
					.forJob(jd)
					.startAt( (this.schedulingInfo.getDate(DATE) != null) ? this.schedulingInfo.getDate(DATE) : new Date())
					.build();			
		} else {
			// Default: no scheduling, Run Once, immediately!
			log.debug("Service "+getServiceId() + " scheduled for defaults: Run Once and Immediately!");
			sb = SimpleScheduleBuilder.simpleSchedule();
			trigger = TriggerBuilder.newTrigger()
					.withIdentity(triggerKey)
					.forJob(jd)
					.build();
		}
		
		// Schedule IOEngine
		engineScheduler.scheduleJob(jd, trigger);		
	}



	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.IOService#unscheduleJobChain(org.quartz.Scheduler)
	 */
	@Override
	public void unscheduleJobChain(Scheduler engineScheduler) throws SchedulerException {
		
		// Unschedule this service's Jobs...
		TriggerKey triggerKey = new TriggerKey(getServiceId()+"_Trigger", getServiceId().getMachineName());
		engineScheduler.unscheduleJob(triggerKey);
		
		// garbage collect serviceJobs
		this.serviceJobs.clear();
	}



	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.IOService#getJobs()
	 */
	@Override
	public Set<JobKey> getJobs() {
		return this.serviceJobs.keySet();
	}
	
	
}
