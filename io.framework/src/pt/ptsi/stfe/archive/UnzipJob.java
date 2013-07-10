/**
 * 
 */
package pt.ptsi.stfe.archive;

import static pt.ptsi.stfe.io.engine.xml.IOXmlDestinations.PROPERTIES.DESTINATIONS;
import static pt.ptsi.stfe.io.engine.xml.IOXmlSources.PROPERTIES.SOURCES;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import pt.ptsi.stfe.io.engine.PropertyMap;
import pt.ptsi.stfe.io.engine.jobs.IOJobException;
import pt.ptsi.stfe.io.engine.jobs.JobResult;
import pt.ptsi.stfe.io.engine.jobs.JobResult.EExitCodes;

/**
 * @author XPTS912
 *
 */
public class UnzipJob extends CopyJob {

	enum PROPERTIES {
		
		move ("deleteOnSource"); // optional
		
		private final String v;
		
		PROPERTIES(String value) { this.v = value; }

		public final String value() { return v; }
	}
	
	/**
	 * 
	 */
	public UnzipJob() {
		prependLog = new String(UnzipJob.class.getName());
	}

	
	/**
	 * This method is used to unzip a password protected zip file.
	 * 
	 * @param sourceZipFile
	 *            of type String indicating the source zip file
	 * @param destinationDir
	 *            of type String indicating the directory where the zip file
	 *            will be extracted.
	 * @param password
	 *            of type String indicating the password.
	 *            
	 * @return number of files extracted
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public int unzip(final String sourceZipFile, final String destinationDir, final String password) throws IOException {
		int count = 0;
		ISevenZipInArchive inArchive = null;
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(sourceZipFile, "r")){
			//
			try {
				inArchive = SevenZip.openInArchive(null, // autodetect archive type
						new RandomAccessFileInStream(randomAccessFile));
				
				// Getting simple interface of the archive inArchive
				ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
				for (final ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {

					File destination = new File(destinationDir, item.getPath());
					
					if (destination.exists()) {
						Files.delete(Paths.get(destination.toURI()));
						warn("Destination file " + destination.getAbsolutePath() + " already exists. It WILL be OVERWRITTEN!");
					}
					
					if (!item.isFolder()) {
						Path dir = Paths.get(destination.getParentFile().toURI());
						if (Files.notExists(dir)) {
							Files.createDirectories(dir);
						}
						
						ExtractOperationResult result = null;
						ISequentialOutStream outStream = new SequentialOutStream(destination);
						if (item.isEncrypted()) {
							result = item.extractSlow(outStream, password);
						} else {
							result = item.extractSlow(outStream);
						}
						if (result == ExtractOperationResult.OK) {
							debug("Extracted " + item.getPath() + " to " + destinationDir + "; Size=" + item.getSize() + "; Method=" + item.getMethod());
							count += 1;
						} else {
							error("Errors extracting " + item.getPath() + ". error code="+result.name());
						}
					}
				}
			} catch (SevenZipException sevenZipException) {
				throw new IOException(sevenZipException);
			} finally {
				if (inArchive != null) {
					try {
						inArchive.close();
					} catch (SevenZipException e) {
						error("Error closing archive " + inArchive , e);
					}
				}				
			}
		} 
		
		return count;
	}

	
	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.archive.CopyJob#doWork(pt.ptsi.stfe.io.engine.PropertyMap)
	 */
	@Override
	protected JobResult doWork(PropertyMap jobInfo) throws IOJobException {
		final JobResult result = new JobResult(getJobKey());
		//
		SourceLocation [] sources =   (SourceLocation[]) jobInfo.get(SOURCES);
		File [] destinations = (File []) jobInfo.get(DESTINATIONS);
		//
		IOFileFilter ioFilter = new ComplexFileFilter(jobInfo);
		//
		boolean deleteOnSource = jobInfo.getBoolean(PROPERTIES.move);
		//
		result.getTimer().start();		
		debug("fired execution @" + result.getTimer().getStartTime());
		//
		int index = 0;
		int count = 0;
		for (SourceLocation source : sources) {
			Iterator<File> sourceFiles = FileUtils.listFiles(source.getLocation(), ioFilter, null).iterator();
			while (sourceFiles.hasNext()) {
				// we have a sourceZip File to unzip...
				if (source.getGauge() == -1 || index < source.getGauge()) {
					try {
						final File srcFile = sourceFiles.next();
						final Path src = Paths.get(srcFile.toURI());
					
						for (File dest : destinations) {
	
							final Path destDir = Paths.get(dest.toURI());
							if (Files.notExists(destDir)) {
								Files.createDirectories(destDir);
							}
							//
							count += unzip(src.toString(), destDir.toString(), source.getPassword());
							result.appendDetail("Unzipped from " + src.toString() + " to " + dest.getAbsolutePath());
							//
						}
						// delete the sourceZip File? 
						if (deleteOnSource) {
							Files.delete(src);
						}
						debug (((deleteOnSource) ? " deleted source zip file " + src : " kept original source zip file " + src));
					} catch (IOException ioe) {
						result.setExitCode(EExitCodes.ERROR);
						result.getTimer().stop();
						result.setMessage(ioe.getMessage());
						error(ioe.getMessage(), ioe);
						IOJobException ex = new IOJobException(result, ioe);
						throw ex;
					}
					//
					
				} else {
					// gauge limit reached
					break; 
				}
				index +=1;
			}
		}
		//
		result.getTimer().stop();
		info("Ended Unzip Session : unzipped " + count + " files... Took " + result.getTimer().delta());	
		result.setMessage("Ended Unzip Session : unzipped " + count + " files... Took " + result.getTimer().delta());
		//
		result.setExitCode((count > 0) ? EExitCodes.OK : EExitCodes.NOACTION);		
		return result;
	}
	
	

}
