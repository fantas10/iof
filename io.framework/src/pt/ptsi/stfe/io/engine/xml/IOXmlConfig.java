/**
 * Servi�o Factura Electr�nica
 * PT � Sistemas de Informa��o, S.A. 
 * 
 * io.framework
 * 2011/03/17
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
public abstract interface IOXmlConfig {

	/**
	 * Validates and parse config part from xml meta file and <br/>
	 * encapsulates config info in a Map built from the config item (or the root Element from the xml file)
	 * 
	 * @param configElement
	 * @return Job Config Map created from xml meta data
	 * @throws JDOMException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public PropertyMap parseConfiguration(Element configElement) throws JDOMException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException;
}
