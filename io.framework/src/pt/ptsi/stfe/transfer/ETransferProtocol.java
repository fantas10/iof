/**
 * Servi�o Factura Electr�nica
 * PT � Sistemas de Informa��o, S.A. 
 * 
 * io.framework
 * 2010/09/03
 */
package pt.ptsi.stfe.transfer;

/**
 * @author Nuno P. Louren�o <nuno-p-lourenco@telecom.pt>
 *  Direc��o de Explora��o - Servi�o de Factura Electr�nica
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
