/**
 * 
 */
package pt.ptsi.stfe.io.engine.jobs;

import java.io.IOException;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import pt.ptsi.stfe.io.engine.PropertyMap;

/**
 * Execute Java Programs Externally
 * 
 * @author XPTS912
 *
 */
public class JavaJob extends AbstractExternalJob {

	/**
	 * 
	 */
	public JavaJob() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.jobs.IOJob#getJobKey()
	 */
	@Override
	public JobKey getJobKey() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.jobs.IOJob#setJobConfig(pt.ptsi.stfe.io.engine.PropertyMap)
	 */
	@Override
	public void setJobConfig(PropertyMap config) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.xml.IOXmlConfig#parseConfiguration(org.jdom.Element)
	 */
	@Override
	public PropertyMap parseConfiguration(Element configElement)
			throws JDOMException, IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.jobs.AbstractExternalJob#runExternal()
	 */
	@Override
	public void runExternal() throws IOJobException {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
