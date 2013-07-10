/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/03/29
 */
package pt.ptsi.stfe.io.engine.jobs;

import java.io.Serializable;

import pt.ptsi.stfe.io.engine.IOKey;
import pt.ptsi.stfe.io.engine.ServiceKey;

/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public final class JobKey implements IOKey, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String jobName;
	private final ServiceKey serviceOwnerKey;
	private final Class<IOJob> typeClass;
	
	// internal attributes
	private final StringBuilder combinedKey = new StringBuilder();
	
	
	/**
	 * 
	 */
	public JobKey() {
		this.jobName = "Job"+Long.toString(System.currentTimeMillis());
		this.serviceOwnerKey = null;
		this.typeClass = null;
	}
	
	public JobKey (ServiceKey serviceKey, String jobName, Class<IOJob> type) {
		this.jobName = jobName;
		this.serviceOwnerKey = serviceKey;
		this.typeClass = type;
		//
		combinedKey.append(serviceKey.toString()).append("/")
			.append(this.jobName).toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JobKey) {
			JobKey newObj = (JobKey) obj;
			return this.toString().equals(newObj.toString());
		}
		return super.equals(obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return combinedKey.toString();
	}

	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * @return the serviceOwnerKey
	 */
	public ServiceKey getServiceOwnerKey() {
		return serviceOwnerKey;
	}

	/**
	 * @return the typeClass
	 */
	public Class<IOJob> getTypeClass() {
		return typeClass;
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.IOKey#getKey()
	 */
	@Override
	public String getKey() {
		return this.toString();
	}
	
	
}
