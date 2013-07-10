/**
 * 
 */
package pt.ptsi.stfe.archive;

import java.io.IOException;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import pt.ptsi.stfe.io.engine.PropertyMap;
import pt.ptsi.stfe.io.engine.jobs.AbstractJob;
import pt.ptsi.stfe.io.engine.jobs.IOJobException;
import pt.ptsi.stfe.io.engine.jobs.JobResult;

/**
 * @author XPTS912
 *
 */
public class ListJob extends AbstractJob {

	/**
	 * 
	 */
	public ListJob() {
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.xml.IOXmlConfig#parseConfiguration(org.jdom2.Element)
	 */
	@Override
	public PropertyMap parseConfiguration(Element configElement) throws JDOMException, IOException, ClassNotFoundException,	InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.jobs.AbstractJob#doWork(pt.ptsi.stfe.io.engine.PropertyMap)
	 */
	@Override
	protected JobResult doWork(PropertyMap context) throws IOJobException {
		// TODO Auto-generated method stub
		return null;
	}

}
