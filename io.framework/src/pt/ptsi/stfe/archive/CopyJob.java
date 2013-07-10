/**
 * Serviço Factura Electrónica
 * PT Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/03/18
 */
package pt.ptsi.stfe.archive;

import static pt.ptsi.stfe.io.engine.xml.IOXmlDestinations.PROPERTIES.DESTINATIONS;
import static pt.ptsi.stfe.io.engine.xml.IOXmlFilterOptions.PROPERTIES.FILTEROPTIONS;
import static pt.ptsi.stfe.io.engine.xml.IOXmlSources.PROPERTIES.SOURCES;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
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
import pt.ptsi.stfe.io.engine.xml.IOXmlDestinations;
import pt.ptsi.stfe.io.engine.xml.IOXmlFilterOptions;
import pt.ptsi.stfe.io.engine.xml.IOXmlSources;
import pt.ptsi.stfe.io.engine.xml.WildcardParser;


/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *  
 *  TODO : Recursive scanning not implemented. Should we implement it? -- Other Job probably
 *
 */
public class CopyJob extends AbstractJob {
	
	public enum PROPERTIES {
		
		move ("deleteOnSource"); // optional
		
		private final String v;
		
		PROPERTIES(String value) { this.v = value; }

		public final String value() { return v; }
	}
	
	/**
	 * 
	 */
	public CopyJob() {
		prependLog = new String(CopyJob.class.getName());
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.IOXmlConfig#parseConfiguration(org.jdom.Element)
	 */
	@Override
	public PropertyMap parseConfiguration(Element configElement) throws JDOMException, IOException, ClassNotFoundException,	IllegalAccessException, InstantiationException {
		PropertyMap jobInfo = new PropertyMap();
		
		// handle Sources:
		Element sourcesElement = configElement.getChild(SOURCES.value());
		IOXmlSources handleSources = new IOXmlSources(true);
		jobInfo.consumeAll( handleSources.parseConfiguration(sourcesElement) );
		
		// handle Destinations
		Element destElement = configElement.getChild(DESTINATIONS.value());
		IOXmlDestinations handleDestinations = new IOXmlDestinations(false);
		jobInfo.consumeAll( handleDestinations.parseConfiguration(destElement) );
		
		// handle filters (optional)
		Element filterElement = configElement.getChild(FILTEROPTIONS.value());
		if (filterElement != null) {
			IOXmlFilterOptions handleFilters = new IOXmlFilterOptions();
			jobInfo.consumeAll( handleFilters.parseConfiguration(filterElement) );
		}
		
		// handle deleteOnSource?
		Element deleteOnSource = configElement.getChild(PROPERTIES.move.value());
		Boolean move = (deleteOnSource == null) ? false : new Boolean(deleteOnSource.getValue());
		jobInfo.consume(PROPERTIES.move, move);
		//
		debug("parsed correctly");
		//
		return jobInfo;
	}
	
	

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.archive.ArchiveJob#doWork(java.util.Map)
	 */
	@Override
	protected JobResult doWork(PropertyMap jobInfo) throws IOJobException {
		JobResult result = new JobResult(getJobKey());
		//
		SourceLocation [] sources =   (SourceLocation[]) jobInfo.get(SOURCES);
		File [] destinations = (File []) jobInfo.get(DESTINATIONS);
		//
		IOFileFilter ioFilter = new ComplexFileFilter(jobInfo);
/*		if (ioFilter == null) {
			// no file filter supplied
			ioFilter = TrueFileFilter.TRUE;
		}*/
		boolean move = jobInfo.getBoolean(PROPERTIES.move);
		//
		result.getTimer().start();
		debug("fired execution @" + result.getTimer().getStartTime());
		//
		int ncopied = 0;
		for (SourceLocation source : sources) {
			// list location on source
			Collection<File> files = (Collection<File>) FileUtils.listFiles(source.getLocation(), ioFilter, null); // null, for no recursive
			int index = 0;
			File destinationDir = null;
			File destinationFile = null;
			String destination = null;
			//
			try {
				for (File f : files) {
					if (source.getGauge() == -1 || index < source.getGauge()) {
						for (File dest : destinations) {
							destination = WildcardParser.parse(dest.getAbsolutePath(), f.getName());
							destinationDir = new File(destination);
							final Path destDir = Paths.get(destinationDir.toURI());
							if (Files.notExists(destDir)) {
								Files.createDirectories(destDir);
							}
							//
							destinationFile = new File(destinationDir, f.getName());
							FileUtils.copyFile(f, destinationFile);
							debug("copy " + f.getName() + " from " + f.getPath() + " to " + destinationDir.getAbsolutePath());
							result.appendDetail("copy " + f.getName() + " from " + f.getPath() + " to " + destinationDir.getAbsolutePath());

							ncopied+=1;
							// TODO apply resulting File Handler (Visitor API?)
						}
						if (move) { // delete the source file ?
							if (!FileUtils.deleteQuietly(f)) {
								error("Could not delete file " + f.getAbsolutePath());
							}
						}
						index += 1;	
					} else {
						// gauge limit reached
						break; 
					}
				}
				debug(((move) ? "moved " : "copied ") + index + " files sucessfully from " + source.getLocation().getName() + " in " + result.getTimer().partial());
				result.setMessage(((move) ? "moved " : "copied ") + index + " files sucessfully from " + source.getLocation().getName() + " in " + result.getTimer().partial());
			} catch (IOException | ParseException ioe) {
				result.getTimer().stop();
				result.setExitCode(EExitCodes.ERROR);				
				result.setMessage(ioe.getMessage());
				error(ioe);
				throw new IOJobException(result, ioe);
			}			
		}
		result.getTimer().stop();
		result.setExitCode((ncopied > 0) ? EExitCodes.OK : EExitCodes.NOACTION);
		result.setMessage("ended execution, took " + result.getTimer().delta());
		debug("ended execution, took " + result.getTimer().delta());
		//
		return result;
	}

}
