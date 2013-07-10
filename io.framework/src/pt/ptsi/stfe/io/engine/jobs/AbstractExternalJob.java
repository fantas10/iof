/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/01/24
 */
package pt.ptsi.stfe.io.engine.jobs;

import java.util.Properties;

import org.apache.log4j.Logger;

import pt.ptsi.stfe.io.engine.PropertyMap;


/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public abstract class AbstractExternalJob implements ExternalJob {

	public static Logger extJobLogger = Logger.getLogger(AbstractExternalJob.class);
	
	private final Properties arguments;
	
	private JobKey jobKey = null;
	
	private PropertyMap jobConfig = null;
	
	private IONature nature = null;
	
	/**
	 * 
	 */
	public AbstractExternalJob() {
		arguments = new Properties();
	}
	
	


	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public JobResult call() throws Exception {
		try {
			// TODO Auto-generated method stub
			// Transfer xml @args and pass them ordered (as in xml)
			runExternal();
			
		} catch (IOJobException jobEx) {
			// TODO: Log some stuff, reset some vars and throw generic
			//
			throw new Exception(jobEx.getMessage(), jobEx);
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.jobs.IOJob#getJobKey()
	 */
	@Override
	public JobKey getJobKey() {
		return jobKey;
	}





	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.jobs.IOJob#setJobConfig(pt.ptsi.stfe.io.engine.PropertyMap)
	 */
	@Override
	public void setJobConfig(PropertyMap config) {
		this.jobConfig = config;
	}


	/**
	 * 
	 * @param arguments
	 * @throws IOJobException
	 */
	public abstract void runExternal() throws IOJobException;

	
	
	
	/**
	 * @return the arguments
	 */
	public Properties getArguments() {
		return arguments;
	}

	@Override
	public IONature getNature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getScope() {
		// TODO Auto-generated method stub
		return null;
	}

}
