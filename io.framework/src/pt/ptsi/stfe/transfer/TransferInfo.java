/**
 * 
 */
package pt.ptsi.stfe.transfer;

import java.io.Serializable;

import com.jcraft.jsch.UserInfo;

/**
 * @author eddie
 *
 */
public final class TransferInfo implements Serializable, UserInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1L;

	private final String server;
	private final String username;
	private final String password;
	
	/**
	 * 
	 */
	public TransferInfo(String server, String username, String password) {
		this.server = server;
		this.username = username;
		this.password = password;
	}
	
	@Override
	public void showMessage(String message) {}
	
	@Override
	public boolean promptYesNo(String message) {
		return false;
	}
	
	@Override
	public boolean promptPassword(String message) {
		return true;
	}
	
	@Override
	public boolean promptPassphrase(String message) {
		return false;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String getPassphrase() {
		return password;
	}

	public String getServer() {
		return server;
	}

	public String getUsername() {
		return username;
	}
	
	

}
