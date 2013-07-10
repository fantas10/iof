/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2010/09/03
 */
package pt.ptsi.stfe.io.engine;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;


/**
 * Classe base do motor de parsing de ficheiros de serviços.
 * 
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class IOEngine extends Thread {

	public final static TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();
	
	//private final long IOENGINE_TRIGGER_INTERVAL = 60L*1000L; // 1 minute
	public final static long IOENGINE_TRIGGER_INTERVAL = 1000L; // 1 secs
	public static final String PROP_SERVICE_LOCATION = "SERVICE_LOCATION";
	
	static Logger logger = Logger.getLogger(IOEngine.class);

	/**
	 * The engine job Scheduler
	 */
	private Scheduler engineScheduler;
	
	/**
	 * The engine service Monitor 
	 */
	private FileAlterationMonitor fsMonitor;
	
	// life cycle vars
	volatile boolean ctrlc = false;
	volatile boolean engineQuit = false;
	

	File serviceDirectory = null;
	private Properties config = null;
	
	
	private String machineName = null;
	
	/**
	 * 
	 * @param config
	 */
	public IOEngine() {
		super.setDaemon(true);
		super.setName("IOEngine");
	}

	/**
	 * 
	 * @param initProperties
	 */
	public void init(Properties initProperties) {
		
		// External Properties
		this.config = initProperties;
		String sDirectory = config.getProperty(PROP_SERVICE_LOCATION, "config/config.properties");
		
		this.serviceDirectory = new File(sDirectory);
		// compute computername
		try {
			this.machineName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// use IP Address
			try {
				this.machineName = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				logger.error(e1.getMessage(), e1);
			}
		}
		//
		if (serviceDirectory.isDirectory()) {
			
			// Schedule the IOEngine
			try {
				
				// create a scheduler instance
				this.engineScheduler = StdSchedulerFactory.getDefaultScheduler();

				// start it...
				this.engineScheduler.start();
				
				// create a serviceDirectory Monitor
				this.fsMonitor = new FileAlterationMonitor(IOENGINE_TRIGGER_INTERVAL);
				this.fsMonitor.start();
				
			} catch (Exception e) {
				logger.error("Error initializing IOEngine: " + e.getMessage());
				e.printStackTrace();
			}

			
		} else {
			logger.error("Service Location doesn't point to a valid directory " + serviceDirectory.getPath());
		}
	}
	
	/**
	 * 
	 * @throws SchedulerException
	 */
	public void pauseScheduler() throws Exception {
		this.fsMonitor.stop();
		this.engineScheduler.pauseAll();
	}
	
	/**
	 * 
	 * @throws SchedulerException
	 */
	public void resumeScheduler() throws Exception {
		this.engineScheduler.resumeAll();
		this.fsMonitor.start();
	}
	
	
	/**
	 * @throws SchedulerException 
	 * 
	 */
	public void start() {
		super.start();
		// Scan and maintain domain configuration(s)
		FileAlterationObserver domainObserver = new FileAlterationObserver(serviceDirectory, new DomainFileFilter(), IOCase.SENSITIVE);
		DomainMonitor domainMonitor = new DomainMonitor();
		domainObserver.addListener(domainMonitor);
		
		// Scan and maintain service(s)
		FileAlterationObserver serviceObserver = new FileAlterationObserver(serviceDirectory, new ServiceFileFilter(), IOCase.SENSITIVE);
		ServiceMonitor serviceMonitor = new ServiceMonitor(this.machineName, this.engineScheduler, domainMonitor.getDomainMap());
		serviceObserver.addListener(serviceMonitor);
		//
		fsMonitor.addObserver(domainObserver);
		fsMonitor.addObserver(serviceObserver);
		// 
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		
		super.run();
		logger.debug("===> ENTERING ENGINE MAIN LOOP...");
		
		//  IO Engine Main LOOP
		while (!ctrlc && !engineQuit) {
			
			try {
				// TODO : Listen for lifecycle events

				// sleep for some time
				sleep(IOENGINE_TRIGGER_INTERVAL);
			
			} catch (InterruptedException ie) {
				logger.error(ie.getMessage(), ie);
				ie.printStackTrace();
			}
			//
		} // while loop
		
		logger.debug("===> ENGINE QUITING NOW...");
		//
		// Close All
		try {
			this.fsMonitor.stop();
			this.engineScheduler.shutdown(true);
		} catch (Exception e) {
			logger.fatal("Couldn't Stop engine scheduler...", e);
		}
		logger.info("===> STOPPED Engine & Monitor With Success!!...");
	}

	/**
	 * @return the engineScheduler
	 */
	public final Scheduler getEngineScheduler() {
		return engineScheduler;
	}

	/**
	 * @return the fsMonitor
	 */
	public final FileAlterationMonitor getFsMonitor() {
		return fsMonitor;
	}
	
	
}
