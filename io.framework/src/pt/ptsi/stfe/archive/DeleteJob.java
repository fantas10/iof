/**
 * Serviço Factura Electrónica
 * PT @ Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/03/18
 */
package pt.ptsi.stfe.archive;

import static pt.ptsi.stfe.io.engine.xml.IOXmlFilterOptions.PROPERTIES.FILTEROPTIONS;
import static pt.ptsi.stfe.io.engine.xml.IOXmlSources.PROPERTIES.SOURCES;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import pt.ptsi.stfe.io.engine.PropertyMap;
import pt.ptsi.stfe.io.engine.jobs.AbstractJob;
import pt.ptsi.stfe.io.engine.jobs.IOJobException;
import pt.ptsi.stfe.io.engine.jobs.JobResult;
import pt.ptsi.stfe.io.engine.jobs.JobResult.EExitCodes;
import pt.ptsi.stfe.io.engine.xml.IOXmlFilterOptions;
import pt.ptsi.stfe.io.engine.xml.IOXmlSources;


/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class DeleteJob extends AbstractJob {

	/**
	 * 
	 */
	public DeleteJob() {
		prependLog = new String(DeleteJob.class.getName());
	}
	
	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.xml.IOXmlConfig#parseConfiguration(org.jdom.Element)
	 */
	@Override
	public PropertyMap parseConfiguration(Element configElement) throws JDOMException, IOException, ClassNotFoundException,	InstantiationException, IllegalAccessException {
		PropertyMap jobInfo = new PropertyMap();
		
		// handle Sources:
		Element sourcesElement = configElement.getChild(SOURCES.value());
		IOXmlSources handleSources = new IOXmlSources(true);
		jobInfo.consumeAll( handleSources.parseConfiguration(sourcesElement) );		
		
		// handle filters (optional)
		Element filterElement = configElement.getChild(FILTEROPTIONS.value());
		if (filterElement != null) {
			IOXmlFilterOptions handleFilters = new IOXmlFilterOptions();
			jobInfo.consumeAll( handleFilters.parseConfiguration(filterElement) );
		}
		debug("parsed correctly");
		return jobInfo;
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.archive.ArchiveJob#doWork(pt.ptsi.stfe.io.engine.PropertyMap)
	 */
	@Override
	protected JobResult doWork(PropertyMap context) throws IOJobException {
		JobResult result = new JobResult(getJobKey());
		//
		SourceLocation [] sources =   (SourceLocation[]) context.get(SOURCES);
		//
		IOFileFilter ioFilter = new ComplexFileFilter(context);
		//
		result.getTimer().start();
		debug("fired execution @" + result.getTimer().getStartTime());
		//
		boolean deleted = false;
		int index = 0;
		try {
			for (SourceLocation source : sources) {
				Collection<File> files = (Collection<File>) FileUtils.listFiles(source.getLocation(), ioFilter, null); // null, for no recursive
				index = 0;
				
				for (File f : files) {
					if (source.getGauge() == -1 || index < source.getGauge()) {
						
						deleted = FileUtils.deleteQuietly(f);
						result.appendDetail( ((deleted) ? "deleted " : " could NOT delete ") + f.getAbsolutePath());
						
						if (deleted) index += 1;	
					} else {
						// gauge limit reached
						break; 
					}
				}
				debug("deleted " + index + " files sucessfully from " + source.getLocation().getName() + ". Process took " + result.getTimer().partial());
			}
		} catch (Exception e) {
			result.getTimer().stop();
			result.setExitCode(EExitCodes.ERROR);
			result.setMessage(e.getMessage());
			error(e.getMessage(), e);
			throw new IOJobException(result, e);
		}
		result.getTimer().stop();
		result.setExitCode((index > 0) ? EExitCodes.OK : EExitCodes.NOACTION);
		result.setMessage("ended execution, took " + result.getTimer().delta());
		debug("ended execution, took " + result.getTimer().delta());
		//
		return result;
	}

}
