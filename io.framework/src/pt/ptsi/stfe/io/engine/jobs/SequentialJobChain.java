/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/03/30
 */
package pt.ptsi.stfe.io.engine.jobs;

import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import pt.ptsi.stfe.io.engine.IOEngine;
import pt.ptsi.stfe.io.engine.IOService.PROPERTIES;
import pt.ptsi.stfe.io.engine.jobs.JobResult.EExitCodes;
import pt.ptsi.stfe.io.engine.PropertyMap;
import pt.ptsi.stfe.io.engine.ServiceKey;
import pt.ptsi.stfe.notification.email.IOFeedbackEmail;

/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class SequentialJobChain implements JobChain<IOJob> {

	static Logger log = Logger.getLogger(IOEngine.class);
	
	
	/**
	 * 
	 */
	public SequentialJobChain() {
	}

	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		ServiceKey serviceKey = (ServiceKey) context.getJobDetail().getJobDataMap().get(PROPERTIES.IOService.value());
		Map<JobKey, PropertyMap> jobs = (Map<JobKey, PropertyMap>) context.getJobDetail().getJobDataMap().get(PROPERTIES.IOServiceJobs.value());
		
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		
		JobResult chainResult = new JobResult(serviceKey);
		chainResult.getTimer().start();
		PropertyMap propertyMap = null;
		// since is sequential, iterate in order over jobs
		for (JobKey jobkey : jobs.keySet()) {
			
			propertyMap = jobs.get(jobkey);
		
			Class<IOJob> type = jobkey.getTypeClass();
			
			try {	
				IOJob job = (IOJob) type.newInstance();
				
				// bridge between JobDataMap (Quartz) and PropertyMap (IOF)
				//job.setJobConfig(new PropertyMap(jobDataMap.getWrappedMap()));
				job.setJobConfig(propertyMap);
				
				// synchronous call
				JobResult result = job.call();
				//
				chainResult.setExitCode(result.getExitCode()); // always use last Exit Code!
				chainResult.appendResult(result);
				switch (result.getExitCode()) {
					case NOACTION:
						// TODO should not advance? do not generate error...
						log.warn(result.getMessage());				
						chainResult.getTimer().partial();	
						break;
					case ERROR:
						// TODO abort job chain and notify
						log.error(result.getMessage());
						chainResult.getTimer().stop();
						return;
					case FATAL:
						// TODO abort job chain and notify 
						log.fatal(result.getMessage());
						chainResult.getTimer().stop();
						return;
					case OK:
						// continue work
						log.debug(result.getMessage());
						chainResult.getTimer().partial();						
						break;						
					case WARNING:
						// continue work
						log.warn(result.getMessage());
						chainResult.getTimer().partial();						
						break;
				}
				
			} catch (Exception e) {
				log.error(jobkey + ": " +e.getMessage());
				//
				if (e instanceof IOJobException) {
					JobResult brokeResult = ((IOJobException) e).getResult();
					chainResult.appendResult(brokeResult);
					chainResult.setMessage(brokeResult.getMessage());
				} else {
					chainResult.setMessage(e.getMessage());
				}
				chainResult.getTimer().stop();
				chainResult.setExitCode(EExitCodes.ERROR);
				
				log.error("Service " + serviceKey + " DIDN'T FINISH but took " + (chainResult.getTimer().delta()) + " . with status="+chainResult.getExitCode());
				//
				IOFeedbackEmail feedbackEmail = new IOFeedbackEmail(serviceKey);
				try {
					feedbackEmail.sendFeedback(chainResult);
				} catch (Exception e1) {
					log.fatal("COULD NOT SEND FEEDBACK EMAIL!");
					e1.printStackTrace();
					throw new JobExecutionException("COULD NOT SEND FEEDBACK EMAIL!", e1);
					
				}
				throw new JobExecutionException(e);
			} 
		}
		chainResult.getTimer().stop();
		chainResult.setMessage("Service " + serviceKey + " Workflow took : " + (chainResult.getTimer().delta()) + " . with status="+chainResult.getExitCode());
		log.info("Service " + serviceKey + " Workflow took : " + (chainResult.getTimer().delta()) + " . with status="+chainResult.getExitCode());
		IOFeedbackEmail feedbackEmail = new IOFeedbackEmail(serviceKey);
		try {
			if (chainResult.getExitCode().getValue() <= 0) {
				feedbackEmail.sendFeedback(chainResult);
			}
		} catch (Exception e1) {
			log.fatal("COULD NOT SEND FEEDBACK EMAIL!");
			e1.printStackTrace();
			throw new JobExecutionException("COULD NOT SEND FEEDBACK EMAIL!", e1);
			
		}
		context.setResult(chainResult);
	}
}
