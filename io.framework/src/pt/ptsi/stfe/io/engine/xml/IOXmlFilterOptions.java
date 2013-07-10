/**
 * Servi�o Factura Electr�nica
 * PT � Sistemas de Informa��o, S.A. 
 * 
 * io.framework
 * 2011/04/03
 */
package pt.ptsi.stfe.io.engine.xml;

import java.io.IOException;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import pt.ptsi.stfe.io.engine.PropertyMap;

/**
 * @author Nuno P. Louren�o <nuno-p-lourenco@telecom.pt>
 *  Direc��o de Explora��o - Servi�o de Factura Electr�nica
 *  www.ptsi.pt
 *
 */
public class IOXmlFilterOptions implements IOXmlConfig {

	// PROPS: all are optional
	public enum PROPERTIES implements E_IOXmlProperties {
		FILTEROPTIONS("filterOptions"),
		FILTER("filter"), 
		SIZE("size"), 
		SIZEDIRECTION("direction"), 
		AGE("age"), 
		AGETYPE("type"), 
		AGEDIRECTION("direction");
		
		private final String v;
		
		PROPERTIES(String value) { 	this.v = value; }

		public final String value() { return v; }
	
	}

	
	public enum SizeOrientation {
		atLeast, atMost
	}
	
	public enum AgeType {
		created, modified
	}
	
	public enum AgeOrientation {
		olderThan, newerThan
	}
	
	/**
	 * 
	 */
	public IOXmlFilterOptions() {
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.xml.IOXmlConfig#parseConfiguration(org.jdom.Element)
	 */
	@Override
	public PropertyMap parseConfiguration(Element configElement) throws JDOMException, IOException, ClassNotFoundException,InstantiationException, IllegalAccessException {
		PropertyMap filterInfo = new PropertyMap();
		
		// handle filter:
		Element filterElement = configElement.getChild(PROPERTIES.FILTER.value());
		if (filterElement != null) { // we have optional regex filter
			filterInfo.consume(PROPERTIES.FILTER, filterElement.getValue().trim());
		}
		
		// handle size:
		Element sizeElement = configElement.getChild(PROPERTIES.SIZE.value());
		if (sizeElement != null) { // we have optional size filter
			filterInfo.consume(PROPERTIES.SIZE, Long.parseLong(sizeElement.getValue().trim()));
			String sizeOrientation = sizeElement.getAttribute(PROPERTIES.SIZEDIRECTION.value()).getValue().trim();
			if (SizeOrientation.atMost.toString().equals(sizeOrientation)) {
				filterInfo.consume(PROPERTIES.SIZEDIRECTION, SizeOrientation.atMost);
			} else {
				filterInfo.consume(PROPERTIES.SIZEDIRECTION, SizeOrientation.atLeast);
			}
			
		}
		
		// handle age:
		Element ageElement = configElement.getChild(PROPERTIES.AGE.value());
		if (ageElement != null) { // we have optional age filter
			filterInfo.consume(PROPERTIES.AGE, Integer.parseInt(ageElement.getValue().trim()));
			String ageType = ageElement.getAttributeValue(PROPERTIES.AGETYPE.value()).trim();
			if (AgeType.modified.toString().equals(ageType)) {
				filterInfo.consume(PROPERTIES.AGETYPE, AgeType.modified);
			} else {
				filterInfo.consume(PROPERTIES.AGETYPE, AgeType.created);
			}
			String ageOrientation = ageElement.getAttributeValue(PROPERTIES.AGEDIRECTION.value()).trim();
			if (AgeOrientation.newerThan.toString().equals(ageOrientation)) {
				filterInfo.consume(PROPERTIES.AGEDIRECTION, AgeOrientation.newerThan);
			} else {
				filterInfo.consume(PROPERTIES.AGEDIRECTION, AgeOrientation.olderThan);
			}
		}		

		return filterInfo;
	}

}
