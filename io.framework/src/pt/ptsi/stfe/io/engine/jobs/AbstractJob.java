/**
 * Servi�o Factura Electr�nica
 * PT � Sistemas de Informa��o, S.A. 
 * 
 * io.framework
 * 2011/03/18
 */
package pt.ptsi.stfe.io.engine.jobs;

import org.apache.log4j.Logger;

import pt.ptsi.stfe.io.engine.PropertyMap;

import static pt.ptsi.stfe.io.engine.jobs.IOJob.PROPERTIES.IOJobKey;

/**
 * Base class for archive utility jobs
 * 
 * @author Nuno P. Louren�o <nuno-p-lourenco@telecom.pt>
 *  Direc��o de Explora��o - Servi�o de Factura Electr�nica
 *  www.ptsi.pt
 *
 */
public abstract class AbstractJob implements InternalJob, ExtensionJob  {
	
	private static Logger log = Logger.getLogger(AbstractJob.class);
	
	protected String prependLog = new String("");
	
	private PropertyMap jobConfig = null;
	
	private JobKey jobKey = null;
	private IONature nature = null;
	private String scope = null;
	
	/**
	 * 
	 */
	public AbstractJob() {
	}
	

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.jobs.IOJob#getJobKey()
	 */
	@Override
	public JobKey getJobKey() {
		return jobKey;
	}



	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.jobs.IOJob#setJobConfig(java.util.Map)
	 */
	@Override
	public void setJobConfig(PropertyMap config) {
		this.jobConfig = config;
	}


	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public JobResult call() throws Exception {

		this.jobKey = (JobKey) jobConfig.get(IOJobKey);
		this.scope = null;
		
		prependLog = new StringBuilder()
			.append( (jobKey != null) ? jobKey.toString() : "" )
			.append(": ").toString();
		
		JobResult result = doWork(jobConfig);
		
		return result; // TODO Handle later
	}


	/**
	 * 
	 */
	protected abstract JobResult doWork(PropertyMap context) throws IOJobException;


	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.jobs.IOJob#getNature()
	 */
	@Override
	public IONature getNature() {
		return nature;
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.jobs.IOJob#getScope()
	 */
	@Override
	public String getScope() {
		return scope;
	}



	/**
	 * @param message
	 * @see org.apache.log4j.Category#debug(java.lang.Object)
	 */
	public void debug(Object message) {
		log.debug(prependLog+message);
	}



	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#error(java.lang.Object, java.lang.Throwable)
	 */
	public void error(Object message, Throwable t) {
		log.error(prependLog+message, t);
	}



	/**
	 * @param message
	 * @see org.apache.log4j.Category#error(java.lang.Object)
	 */
	public void error(Object message) {
		log.error(prependLog+message);
	}



	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#fatal(java.lang.Object, java.lang.Throwable)
	 */
	public void fatal(Object message, Throwable t) {
		log.fatal(prependLog+message, t);
	}



	/**
	 * @param message
	 * @see org.apache.log4j.Category#fatal(java.lang.Object)
	 */
	public void fatal(Object message) {
		log.fatal(prependLog+message);
	}



	/**
	 * @param message
	 * @see org.apache.log4j.Category#info(java.lang.Object)
	 */
	public void info(Object message) {
		log.info(prependLog+message);
	}


	/**
	 * @param message
	 * @see org.apache.log4j.Category#info(java.lang.Object)
	 */
	public void trace(Object message) {
		log.trace(prependLog+message);
	}

	
	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#warn(java.lang.Object, java.lang.Throwable)
	 */
	public void warn(Object message, Throwable t) {
		log.warn(prependLog+message, t);
	}



	/**
	 * @param message
	 * @see org.apache.log4j.Category#warn(java.lang.Object)
	 */
	public void warn(Object message) {
		log.warn(prependLog+message);
	}
}
