/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/01/20
 */
package pt.ptsi.stfe.io.engine.jobs;

import java.util.concurrent.Callable;

import pt.ptsi.stfe.io.engine.PropertyMap;
import pt.ptsi.stfe.io.engine.ServiceKey;
import pt.ptsi.stfe.io.engine.xml.IOXmlConfig;


/**
 * Represents active unit Job Instance, executes Job Lifecycle.
 * 
 * @see Callable#call()
 * 
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public interface IOJob extends IOXmlConfig, Callable<JobResult> {
	
	// properties
	enum PROPERTIES {
		IOJobKey
	}
	
	/**
	 * 
	 * @return String
	 */
	public String getScope();
	
	/**
	 * 
	 * @return IONature
	 */
	public IONature getNature();
	
	
	/**
	 * Returns the unique <code>JobKey</code>
	 * <br>
	 * The {@link JobKey} is combined with the service owner (ServiceKey), 
	 * <br>resulting in the pair "ServiceKey/JobKey"
	 * 
	 * @see ServiceKey
	 * @return {@link JobKey}
	 */
	public JobKey getJobKey();
	
	
	/**
	 * 
	 * @param config
	 */
	public void setJobConfig(PropertyMap config);
	

}
