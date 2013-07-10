/**
 * 
 */
package pt.ptsi.stfe.io.engine.jobs;

import java.io.IOException;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import pt.ptsi.stfe.io.engine.PropertyMap;
import pt.ptsi.stfe.io.engine.jobs.JobResult.EExitCodes;

/**
 * @author XPTS912
 *
 */
public class PingJob extends AbstractJob {

	/**
	 * 
	 */
	public PingJob() {
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.xml.IOXmlConfig#parseConfiguration(org.jdom2.Element)
	 */
	@Override
	public PropertyMap parseConfiguration(Element configElement) throws JDOMException, IOException, ClassNotFoundException,	InstantiationException, IllegalAccessException {
		return new PropertyMap(); // empty
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
		result.getTimer().stop();
		result.setExitCode(EExitCodes.OK);
		result.setMessage("ping from " + getJobKey().getServiceOwnerKey().getMachineName()+"!");
		debug("ended execution, took " + result.getTimer().delta());
		return result;
	}

}
