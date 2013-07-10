/**
 * Servi�o Factura Electr�nica
 * PT � Sistemas de Informa��o, S.A. 
 * 
 * io.framework
 * 2010/09/06
 */
package pt.ptsi.stfe.io.engine;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * @author Nuno P. Louren�o <nuno-p-lourenco@telecom.pt>
 *  Direc��o de Explora��o - Servi�o de Factura Electr�nica
 *  www.ptsi.pt
 *
 */
public class ServiceFileFilter implements FileFilter {

	public static final String XML_ENCODING = "ISO-8859-1";
	private final static String regex = "(\\w|.+)-service.xml$";
	
	/**
	 * 
	 */
	public ServiceFileFilter() {
	}

	
	
	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File pathname) {
		return ( // Is a file
				pathname.isFile()
				 // Is readable
				&& pathname.canRead()
				 // Is not size 0
				&& pathname.length() > 0L
				 // mathes service pattern name
				&& Pattern.matches(ServiceFileFilter.regex, pathname.getName().toLowerCase())
			);
	}
}
