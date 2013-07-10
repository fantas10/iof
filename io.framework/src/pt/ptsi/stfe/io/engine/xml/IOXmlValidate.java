package pt.ptsi.stfe.io.engine.xml;

import java.io.IOException;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import pt.ptsi.stfe.io.engine.PropertyMap;
import pt.ptsi.stfe.io.engine.xml.IOXmlFilterOptions.AgeOrientation;
import pt.ptsi.stfe.io.engine.xml.IOXmlFilterOptions.AgeType;
import pt.ptsi.stfe.io.engine.xml.IOXmlFilterOptions.PROPERTIES;

public class IOXmlValidate implements IOXmlConfig {
	
	// PROPS
	/**
	 * 
	 * @see IOXmlFilterOptions.AgeOrientation
	 */
	public enum PROPERTIES implements E_IOXmlProperties {
		VALIDATE ("validate"),
		FILESET ("fileset"),
		CONDITION ("condition"),
		COUNT ("count"),
		AGE ("age"),
		AGETYPE ("agetype"),
		AGEDIRECTION ("direction");
		
		private final String v;
		
		PROPERTIES(String value) { 	this.v = value; }

		public final String value() { return v; }
		
	}
	
	public enum Condition {
		or, and, xor;
	}
	
	public enum AgeType {
		created, modified
	}
	
	public enum AgeOrientation {
		olderThan, newerThan
	}
	

	public IOXmlValidate() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public PropertyMap parseConfiguration(Element configElement) throws JDOMException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		PropertyMap filterInfo = new PropertyMap();
		
		// FILESET
		Element fileset = configElement.getChild(PROPERTIES.FILESET.value());
		if (fileset != null) {
			
			// handle count
			Element countElement = fileset.getChild(PROPERTIES.COUNT.value());
			if (countElement != null) {
				filterInfo.consume(PROPERTIES.COUNT, countElement.getValue());
			}
			//
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
			
		}
		
		return filterInfo;
	}

}
