/**
 * 
 */
package pt.ptsi.iof.alert;

import java.io.IOException;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import pt.ptsi.stfe.io.engine.PropertyMap;
import pt.ptsi.stfe.io.engine.jobs.IOJobException;
import pt.ptsi.stfe.io.engine.jobs.JobResult;
import pt.ptsi.stfe.io.engine.jobs.JobResult.EExitCodes;
import pt.ptsi.stfe.io.engine.xml.IOXmlValidate;

/**
 * @author eddie
 *
 */
public class ValidateFileSet extends Validate {

	/**
	 * 
	 */
	public ValidateFileSet() {
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.xml.IOXmlConfig#parseConfiguration(org.jdom2.Element)
	 */
	@Override
	public PropertyMap parseConfiguration(Element configElement) throws JDOMException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		PropertyMap props = new PropertyMap();
		//
		Element filesetElement = configElement.getChild(IOXmlValidate.PROPERTIES.FILESET.value());
		IOXmlValidate handleFileset = new IOXmlValidate();
		props.consumeAll(handleFileset.parseConfiguration(filesetElement));
		//
		debug("parsed correctly");
		return props;
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.jobs.AbstractJob#doWork(pt.ptsi.stfe.io.engine.PropertyMap)
	 */
	@Override
	protected JobResult doWork(PropertyMap context) throws IOJobException {
		JobResult result = new JobResult(getJobKey());
		result.getTimer().start();
		debug("fired execution @" + result.getTimer().getStartTime());
		//
		
		//
		result.getTimer().stop();
		result.setExitCode(EExitCodes.OK);
		result.setMessage("ended execution, took " + result.getTimer().delta());
		debug("ended execution, took " + result.getTimer().delta());
		//
		return result;
	}

}
