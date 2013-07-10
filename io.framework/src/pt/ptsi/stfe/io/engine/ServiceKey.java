/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/03/17
 */
package pt.ptsi.stfe.io.engine;

import java.io.File;
import java.io.Serializable;

/**
 * Service ID should be a sequence of :
 * <p>
 *   [<i>domainName</i>]:[<i>machineName</i>]:serviceName:serviceFile_Checksum </li>
 * </p>
 * 
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public final class ServiceKey implements IOKey, Serializable {

	// exposed attributes
	private final IODomain domain;
	private final String machineName;
	private final String serviceName;
	private File completeFilepath = null;
	
	// internal attributes
	private final StringBuilder combinedKey = new StringBuilder();
	private long key = 0;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param domain
	 * @param machineName
	 * @param serviceName
	 * @param fileHash
	 * @param filePath
	 */
	public ServiceKey(IODomain domain, String machineName, String serviceName, File filePath) {
		this.domain = domain;
		this.machineName = machineName;
		this.serviceName = serviceName;
		this.completeFilepath = filePath;
		//
		this.key = filePath.hashCode(); // use the hash for full path hashing (@see documentation)
		//
		combinedKey.append("[").append(domain.getDomainName().getKey()).append("]")
			.append(":").append("[").append(getMachineName()).append("]")
			.append(":").append(getServiceName())
			.append(":").append(this.key);
	}


	/**
	 * @return the completeFilepath
	 */
	public File getCompleteFilepath() {
		return completeFilepath;
	}

	/**
	 * @param completeFilepath the completeFilepath to set
	 */
	public void setCompleteFilepath(File completeFilepath) {
		this.completeFilepath = completeFilepath;
	}

	/**
	 * @return the domainName
	 */
	public IODomain getDomain() {
		return domain;
	}

	/**
	 * @return the machineName
	 */
	public String getMachineName() {
		return machineName;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return combinedKey.toString();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ServiceKey) {
			ServiceKey newObj = (ServiceKey) obj;
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
	 * @see pt.ptsi.stfe.io.engine.IOKey#getKey()
	 */
	@Override
	public String getKey() {
		return this.toString();
	}
	
	
	
}
