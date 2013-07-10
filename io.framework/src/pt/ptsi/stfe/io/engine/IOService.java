/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2010/09/06
 */
package pt.ptsi.stfe.io.engine;

import java.util.Set;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import pt.ptsi.stfe.io.engine.jobs.JobChain;
import pt.ptsi.stfe.io.engine.jobs.JobKey;
import pt.ptsi.stfe.io.engine.xml.IOXmlConfig;

/**
 * IO Service base Interface
 * <br>
 * Will schedule its jobs in a {@link JobChain}
 * 
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public interface IOService extends IOXmlConfig {
	
	// properties
	public enum PROPERTIES {
		
		IOService ("service"),  			// mandatory
		IOServiceJobs("jobList");			// mandatory
		
		private final String v;
		
		PROPERTIES(String value) { 	this.v = value; }

		public final String value() { return v; }
	}
	
	
	/**
	 * XPath IOService root expression
	 */
	public static final String XPATH_SERVICE_ROOT = "/service";
	
	/**
	 * Devolve o resultado da validação do serviço XML
	 * -- Possivelmente contra um XSD/XSchema
	 * @return
	 */
	public boolean isValid();

		

	/**
	 * Service Name
	 * <p>
	 * 	As specified in /service/@name
	 * </p>
	 * 
	 * @return String description
	 */
	public String getName();
	
	/**
	 * Service Description
	 * <p>
	 * 	As specified in /service/@description
	 * </p>
	 * 
	 * @return String description
	 */
	public String getDescription();
	
	/**
	 * Service Domain<br/>
	 * 
	 * If no domain is specified return null
	 * @return
	 */
	public IODomain getDomain();
	
	/**
	 * Gets the unique service Id
	 * 
	 * 
	 * @return the serviceKey
	 */
	public ServiceKey getServiceId();

	/**
	 * Schedule this service and all of its jobs (one or more) in chain
	 * 
	 * @param engineScheduler
	 * @throws SchedulerException
	 */
	public void scheduleJobChain(Scheduler engineScheduler) throws SchedulerException;
	
	
	/**
	 * Unschedule this service and all of its jobs from chain.
	 * <br/> Waits for all jobs to finish
	 * 
	 * @param engineScheduler
	 */
	public void unscheduleJobChain(Scheduler engineScheduler) throws SchedulerException;
	
	
	/**
	 * 
	 * @return
	 */
	public Set<JobKey> getJobs();
}
