/**
 * Serviço Factura Electrónica
 * PT @ Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/04/01
 */
package pt.ptsi.stfe.io.engine.xml;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import pt.ptsi.stfe.archive.SourceLocation;
import pt.ptsi.stfe.io.engine.PropertyMap;

/**
 * 
 * 
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class IOXmlSources implements IOXmlConfig {

	public enum PROPERTIES implements E_IOXmlProperties {
		
		SOURCES ("sources"),  	// mandatory
		FROM("from"),			// mandatory
		GAUGE("gauge"),   		// optional: -1 or n > 0
		PASSWORD ("password");  // optional: string (mainly used for compressed zip files) 
		
		private final String v;
		
		PROPERTIES(String value) { this.v = value;}

		public final String value() { return v;} 
	}
	
	private final boolean checkLocation;
	
	/**
	 * 
	 */
	public IOXmlSources(boolean checkLocation) {
		this.checkLocation = checkLocation;
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.xml.IOXmlConfig#parseConfiguration(org.jdom.Element)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PropertyMap parseConfiguration(Element sourcesElement) throws JDOMException, IOException, ClassNotFoundException,	InstantiationException, IllegalAccessException {
		
		PropertyMap jobInfo = new PropertyMap();
		
		// handle Sources:
		List<Element> sources = sourcesElement.getChildren();
		//
		SourceLocation [] srcLocations = new SourceLocation [sources.size()];
		//
		int index = 0;
		Element from = null;
		Attribute gauge = null;
		Attribute password = null;
		SourceLocation srcLoc = null;
		File fLocation = null;
		for (Iterator<Element> i = sources.iterator(); i.hasNext(); ) {
			from = i.next();
			
			srcLoc = new SourceLocation();
			
			// handle gauge
			gauge = from.getAttribute(PROPERTIES.GAUGE.value());
			if (gauge != null) { // because is optional
				srcLoc.setGauge(gauge.getIntValue());
			}
			
			// handle password 
			password = from.getAttribute(PROPERTIES.PASSWORD.value());
			if (password != null) {
				srcLoc.setPassword(password.getValue().trim());
			}
			
			// handle location
			fLocation = new File(from.getValue());
			if (checkLocation && (fLocation == null || !(fLocation.isDirectory() || fLocation.isFile()))) {
				throw new IOException("IOXmlSources: Wrong argument: " + fLocation.getName() + " doesn't represent a valid file or directory");
			}
			srcLoc.setLocationString(from.getValue());
			srcLoc.setLocation(fLocation);
			srcLocations[index] = srcLoc;
			index += 1;
		}
		jobInfo.consume(PROPERTIES.SOURCES, srcLocations);
		//
		return jobInfo;
	}

}
