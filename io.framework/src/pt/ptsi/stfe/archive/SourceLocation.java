/**
 * Servi�o Factura Electr�nica
 * PT � Sistemas de Informa��o, S.A. 
 * 
 * io.framework
 * 2011/04/04
 */
package pt.ptsi.stfe.archive;

import java.io.File;
import java.io.Serializable;

import pt.ptsi.stfe.io.engine.xml.IOXmlSources;

/**
 * Encapsulates {@link File} Object and {@link IOXmlSources.PROPERTIES.GAUGE} value
 * 
 * @author Nuno P. Louren�o <nuno-p-lourenco@telecom.pt>
 *  Direc��o de Explora��o - Servi�o de Factura Electr�nica
 *  www.ptsi.pt
 *
 */
public class SourceLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File fromLocation = null;
	private String fromLocationString = null;
	
	/**
	 * gauge is to limit number of source files listed from this location
	 * (ex: we only want 5 files per each session)
	 */
	private int gauge = -1; // default is... no gauge
	
	/**
	 * mainly used for compressed files (zip)
	 */
	private String password = ""; // default is... no password
	
	/**
	 * 
	 */
	public SourceLocation() {
	}

	/**
	 * @return the fromFile
	 */
	public File getLocation() {
		return fromLocation;
	}

	/**
	 * @param fromFile the fromFile to set
	 */
	public void setLocation(File fromLocation) {
		this.fromLocation = fromLocation;
	}

	/**
	 * 
	 * @return
	 */
	public String getLocationString() {
		return fromLocationString;
	}

	/**
	 * 
	 * @param fromLocationString
	 */
	public void setLocationString(String fromLocationString) {
		this.fromLocationString = fromLocationString;
	}

	/**
	 * @return the gauge
	 */
	public int getGauge() {
		return gauge;
	}

	/**
	 * @param gauge the gauge to set
	 */
	public void setGauge(int gauge) {
		this.gauge = gauge;
	}

	/**
	 * @return the password
	 */
	public final String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public final void setPassword(String password) {
		this.password = password;
	}
	
	

}
