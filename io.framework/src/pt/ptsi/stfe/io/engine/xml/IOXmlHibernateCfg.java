/**
 * 
 */
package pt.ptsi.stfe.io.engine.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import pt.ptsi.stfe.io.engine.PropertyMap;

/**
 * @author XPTS912
 *
 */
public class IOXmlHibernateCfg implements IOXmlConfig {

	public enum PROPERTIES implements E_IOXmlProperties { 
		
		DATASOURCE ("datasource"),		// mandatory
			DATASOURCE_NAME ("name"),	// mandatory
			DATASOURCE_TYPE ("type"),	// mandatory
		HIBERNATE_PROPERTIES ("properties"),  	// mandatory
			HIBERNATE_PROPERTY ("property"),		// mandatory, at least some
			HIBERNATE_PROPERTY_NAME ("name"),		
			
		HIBERNATE_MAPPINGS ("mappings"),			// mandatory
		HIBERNATE_MAPPING ("mapping");
		
		private final String v;
		
		PROPERTIES(String value) { 	this.v = value; }

		public final String value() { return v; }
	}
	
	/**
	 * 
	 */
	public IOXmlHibernateCfg() {
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.xml.IOXmlConfig#parseConfiguration(org.jdom2.Element)
	 */
	@Override
	public PropertyMap parseConfiguration(Element dsElement) throws JDOMException, IOException, ClassNotFoundException,InstantiationException, IllegalAccessException {
		PropertyMap config = new PropertyMap();
		//
		// handle dataSource name & type (mandatory)
		String dsName = dsElement.getAttributeValue(PROPERTIES.DATASOURCE_NAME.value());
		String dsType = dsElement.getAttributeValue(PROPERTIES.DATASOURCE_NAME.value());
		//
		if (dsName == null || dsType == null || dsName.isEmpty() || dsType.isEmpty()) {
			throw new IOException("DataSource name or type cannot be empty!");
		}
		config.consume(PROPERTIES.DATASOURCE_NAME, dsName);
		config.consume(PROPERTIES.DATASOURCE_TYPE, dsType);
		// handle Properties
		List<Element> xmlProperties = dsElement.getChild(PROPERTIES.HIBERNATE_PROPERTIES.value()).getChildren();
		String propertyKey= null;
		String propertyValue = null;
		Properties props = new Properties();
		for (Element xmlProp : xmlProperties) {
			propertyKey = xmlProp.getAttributeValue(PROPERTIES.HIBERNATE_PROPERTY_NAME.value());
			propertyValue = xmlProp.getTextTrim();
			props.put(propertyKey, propertyValue);
		}
		config.consume(PROPERTIES.HIBERNATE_PROPERTIES, props);
		// handle hibernate mappings
		List<Element> xmlMappings = dsElement.getChild (PROPERTIES.HIBERNATE_MAPPINGS.value()).getChildren();
		List<File> mappings = new ArrayList<File>();
		File fMapping = null;
		for (Element xmlMap : xmlMappings) {
			fMapping = new File(xmlMap.getTextTrim());
			if (fMapping.exists()) {
				mappings.add(fMapping);
			} else {
				throw new IOException("Mapping " + fMapping.getName() + " doesn't exist!!");
			}
		}
		config.consume(PROPERTIES.HIBERNATE_MAPPINGS, mappings);
		//
		return config;
	}

}
