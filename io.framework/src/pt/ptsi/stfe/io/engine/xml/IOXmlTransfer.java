/**
 * Serviço Factura Electrónica
 * PT @ Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2013/05/31
 */
package pt.ptsi.stfe.io.engine.xml;

import java.io.IOException;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import pt.ptsi.stfe.io.engine.PropertyMap;

public class IOXmlTransfer implements IOXmlConfig {

	public enum PROPERTIES implements E_IOXmlProperties {
		
		site ("site"),
		protocol ("protocol"),
		server("server"),
		username("username"),
		password("password");
		
		private final String v;
		
		PROPERTIES(String value) { this.v = value; }

		public final String value() { return v; }
	}
	
	public IOXmlTransfer() {
	}

	@Override
	public PropertyMap parseConfiguration(Element configElement)
			throws JDOMException, IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		
		PropertyMap jobInfo = new PropertyMap();
		
		Element siteElement = configElement.getChild(PROPERTIES.site.value());
		jobInfo.consume(PROPERTIES.server, siteElement.getAttributeValue(PROPERTIES.server.value()));
		jobInfo.consume(PROPERTIES.username, siteElement.getAttributeValue(PROPERTIES.username.value()));
		jobInfo.consume(PROPERTIES.password, siteElement.getAttributeValue(PROPERTIES.password.value()));	
		jobInfo.consume(PROPERTIES.protocol, siteElement.getAttributeValue(PROPERTIES.protocol.value()));

		
		return jobInfo;
	}

}
