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

import org.jdom2.Element;
import org.jdom2.JDOMException;

import pt.ptsi.stfe.io.engine.PropertyMap;

/**
 * 
 * 
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class IOXmlDestinations implements IOXmlConfig {
	
	public enum PROPERTIES implements E_IOXmlProperties { 
		
		DESTINATIONS ("destinations"),  // mandatory
		TO("to");					   // mandatory
		
		private final String v;
		
		PROPERTIES(String value) { 	this.v = value; }

		public final String value() { return v; }
	}
	
	public enum DestinationExists {
		replace, update
	}

	private final boolean checkLocation;
	
	/**
	 * 
	 */
	public IOXmlDestinations(boolean checkLocation) {
		this.checkLocation = checkLocation;
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.xml.IOXmlConfig#parseConfiguration(org.jdom.Element)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PropertyMap parseConfiguration(Element destinationsElement) throws JDOMException, IOException, ClassNotFoundException,	InstantiationException, IllegalAccessException {
		
		PropertyMap jobInfo = new PropertyMap();
		
		// handle Destinations:
		List<Element> destinations = destinationsElement.getChildren();
		//
		File [] destination = new File [destinations.size()];
		//
		int index = 0;
		Element to = null;
		File fileTo = null;
		for (Iterator<?> i = destinations.iterator(); i.hasNext(); ) {
			to = (Element) i.next();
			fileTo = new File(to.getValue());
			if ((checkLocation) && (fileTo == null || !(fileTo.isDirectory() || fileTo.isFile()))) {
				throw new IOException("IOXmlDestinations: Wrong argument: " + fileTo + " doesn't represent a valid file or directory");
			}
			destination[index] = fileTo;
			index += 1;
		}
		jobInfo.consume(PROPERTIES.DESTINATIONS, destination);
		//
		return jobInfo;
	}

}
