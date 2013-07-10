/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2010/09/03
 */
package pt.ptsi.stfe.transfer;

/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public enum ETransferProtocol {
	
	FTP ("FTP", 21),
	
	SFTP ("SFTP", 22),
	
	FTPS ("FTPS", 21); // negotiable - port range
	
	private final int port;
	private final String protocol;
	
	/**
	 * Constructs a valid Transfer Protocol
	 * @param port
	 */
	ETransferProtocol(String protocol, int port) {
		this.port = port;
		this.protocol = protocol;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}
	
}
