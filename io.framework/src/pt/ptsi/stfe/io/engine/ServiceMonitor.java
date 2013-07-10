/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/03/28
 */
package pt.ptsi.stfe.io.engine;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.JDOMFactory;
import org.jdom2.SlimJDOMFactory;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.DefaultSAXHandlerFactory;
import org.jdom2.input.sax.SAXHandlerFactory;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import pt.ptsi.stfe.io.engine.xml.IOXmlConfig;

/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class ServiceMonitor extends Observable implements FileAlterationListener {

	static Logger logger = Logger.getLogger(ServiceMonitor.class);
	
	/**
	 * Map containing the services
	 */
	private final Map<ServiceKey, IOService> serviceMap;
	/**
	 * Reference to domainMap
	 */
	private final Map<DomainKey, IODomain> domainMap;
	private final String hostMachine;
	private final Scheduler scheduler;
	
	/**
	 * 
	 */
	public ServiceMonitor(String machine, Scheduler scheduler, Map<DomainKey, IODomain> domainMap) {
		// init serviceMap
		this.serviceMap = new HashMap<ServiceKey, IOService>();
		this.hostMachine = machine;
		this.scheduler = scheduler;
		this.domainMap = domainMap;
	}
	
	
	/**
	 * Parses XML serviceFile configuration
	 * 
	 * @see IOXmlConfig#parseConfiguration(Element)
	 * 
	 * @param serviceFile 
	 * @return {@link IOService}: the newly created service
	 * @throws IOException
	 * @throws JDOMException
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	private IOService parseService(File serviceFile) throws IOException, JDOMException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		IOService ioService = null;
		
		// TODO Check XML against a Schema - change below to true
		
		//System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");
		
		XMLReaderSAX2Factory factory = new XMLReaderSAX2Factory(false);
		SAXHandlerFactory saxHandlerFactory = new DefaultSAXHandlerFactory();
		JDOMFactory jdomFactory = new SlimJDOMFactory();
		//XMLReaderJDOMFactory xjdomFactory = XMLReaders.NONVALIDATING;
		
		//
		/*
		XMLReader xmlReader = xjdomFactory.createXMLReader();
		SAXHandlerFactory saxHandlerFactory = new DefaultSAXHandlerFactory();
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		JDOMFactory jdomFactory = new SlimJDOMFactory();
		*/
		//
		SAXBuilder parser = new SAXBuilder(factory, saxHandlerFactory, jdomFactory);
		//parser.setJDOMFactory(factory)
		// builder.setFeature( "http://apache.org/xml/features/validation/schema", true);
		// builder.setProperty( "http://apache.org/xml/properties/schema/external-schemaLocation",   "http://www.w3.org/2001/12/soap-envelope soap-envelope.xsd" + " " + "http://kevinj.develop.com/weblog/weblog.xsd weblog.xsd");
		
		Document document = parser.build(serviceFile);
		Element serviceElement = document.getRootElement();
		final String xDomain = serviceElement.getAttributeValue("domain");
		IOKey domainKey = new DomainKey(xDomain);
		// check if domain exists
		IODomain domain = domainMap.get(domainKey);
		if (domain == null) {
			logger.warn("Domain is null, non-existent or non valid, using default domain!");
			domain = IODomain.getDefaultDomain();
		}
		//
		ServiceKey serviceKey = new ServiceKey(domain, hostMachine, serviceElement.getAttribute("name").getValue(), serviceFile); 
		//
		ioService = new ServiceImpl(document, serviceKey);
		//
		ioService.parseConfiguration(serviceElement);
		//
		return ioService;

	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onDirectoryChange(java.io.File)
	 */
	@Override
	public void onDirectoryChange(File directory) {
		// TODO Auto-generated method stub
		logger.warn("No Implementation for Directory Change " + directory.getAbsolutePath());
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onDirectoryCreate(java.io.File)
	 */
	@Override
	public void onDirectoryCreate(File directory) {
		// TODO Auto-generated method stub
		// do nothing , but launch warning
		logger.warn("No Implementation for New Directory Created " + directory.getAbsolutePath());

	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onDirectoryDelete(java.io.File)
	 */
	@Override
	public void onDirectoryDelete(File directory) {
		// Directory Deleted?? Stop Everything and shutdown and THROW FATAL!...
		logger.warn("No Implementation for Directory Delete!! " + directory.getAbsolutePath());

	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onFileChange(java.io.File)
	 */
	@Override
	public void onFileChange(File file) {
		// Service update <=> Delete and update map
		IOService service, newService = null;
		ServiceKey serviceKey, newServiceKey = null;
		File sf = null;
		Iterator<IOService> serviceIterator = serviceMap.values().iterator();
		while (serviceIterator.hasNext()) {
			service = serviceIterator.next();
			serviceKey = service.getServiceId();
			sf = serviceKey.getCompleteFilepath();
			if (sf.exists() && sf.isFile() && sf.equals(file)) { //must be same file, since it's a change
				// 
				try {
					// unschedule first
					service.unscheduleJobChain(scheduler);
					serviceKey = null;
					service = null;
					//
					newService = parseService(file);
					newServiceKey = newService.getServiceId();
					//
					// update with the new service
					serviceMap.put(newServiceKey, newService);
					//
					// schedule service
					newService.scheduleJobChain(scheduler);
					logger.info("Service " + newService.getServiceId() + " was updated @ " + new Date());		
					//
					break; // break loop (service updated!)
					
					
				} catch (Exception e) {
					logger.error("Could not update service " + service.getServiceId() );
					logger.error(e);
				}

				//
			}
		}
		//
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onFileCreate(java.io.File)
	 */
	@Override
	public void onFileCreate(File file) {
		IOService ioService = null;
		
		try {
			ioService = parseService(file);
			
			// add metadata file to map
			serviceMap.put(ioService.getServiceId(), ioService);
			//
			// schedule service
			ioService.scheduleJobChain(scheduler);
			
			//
			logger.info("Service " + ioService.getServiceId() + " added! , current number of services provisioned: " + this.serviceMap.size());
			
		} catch (Exception e) {
			if (ioService != null && serviceMap.containsKey(ioService.getServiceId())) {
				serviceMap.remove(ioService.getServiceId());
				logger.error("Service " + ioService.getServiceId() + " not added!");
			}
			logger.error("The following file wasn't processed as a service or wasn't a valid file " + file.getPath());
			logger.error(e.getMessage(), e);
		} // should not throw anything from here

		
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onFileDelete(java.io.File)
	 */
	@Override
	public void onFileDelete(File file) {
		// Service removal
		String serviceName = null;
		IOService service = null;
		ServiceKey serviceKey = null;
		File sf = null;
		Iterator<IOService> serviceIterator = serviceMap.values().iterator();
		while (serviceIterator.hasNext()) {
			service = serviceIterator.next();
			serviceKey = service.getServiceId();
			serviceName = serviceKey.toString();
			sf = serviceKey.getCompleteFilepath();
			if ((sf.equals(file)) && (!(sf.exists() && sf.isFile()))) {
				// remove from scheduler
				try {
					
					service.unscheduleJobChain(scheduler);
					serviceKey = null;
					service = null;
					
				} catch (SchedulerException e) {
					logger.error("Could not unschedule service " + serviceName );
					logger.error(e);
				}
				// 
				
				// remove this service
				serviceIterator.remove();
				//
				logger.info("Service " + serviceName + " was removed @ " + new Date());				
				// 
				break;
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onStart(org.apache.commons.io.monitor.FileAlterationObserver)
	 */
	@Override
	public void onStart(FileAlterationObserver observer) {
		//logger.debug("ServiceMonitor: Scanning for files @ "+observer.getDirectory().getAbsolutePath()+"...		(Number of services provisioned = " + serviceMap.size()+")");
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onStop(org.apache.commons.io.monitor.FileAlterationObserver)
	 */
	@Override
	public void onStop(FileAlterationObserver observer) {}


	/**
	 * @return the serviceMap
	 */
	public Map<ServiceKey, IOService> getServiceMap() {
		return serviceMap;
	}
	
	

}
