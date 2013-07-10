/**
 * 
 */
package pt.ptsi.stfe.io.engine;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 */
public class DomainFileFilter implements FileFilter {

	public static final String XML_ENCODING = "ISO-8859-1";
	private final static String regex = "(\\w|.+)-domain.xml$";
	
	/**
	 * 
	 */
	public DomainFileFilter() {
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
				&& Pattern.matches(DomainFileFilter.regex, pathname.getName().toLowerCase())
			);
	}

}
