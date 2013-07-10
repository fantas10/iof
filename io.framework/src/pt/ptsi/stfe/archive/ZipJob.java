/**
 * Serviço Factura Electrónica
 * PT  Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/03/30
 */
package pt.ptsi.stfe.archive;

import static pt.ptsi.stfe.io.engine.xml.IOXmlDestinations.PROPERTIES.DESTINATIONS;
import static pt.ptsi.stfe.io.engine.xml.IOXmlSources.PROPERTIES.SOURCES;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import pt.ptsi.stfe.io.engine.PropertyMap;
import pt.ptsi.stfe.io.engine.jobs.IOJobException;
import pt.ptsi.stfe.io.engine.jobs.JobResult;
import pt.ptsi.stfe.io.engine.jobs.JobResult.EExitCodes;
import pt.ptsi.stfe.io.engine.xml.WildcardParser;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileOutputStream;
import de.schlichtherle.truezip.file.TVFS;
import de.schlichtherle.truezip.fs.FsOutputOption;
import de.schlichtherle.truezip.fs.FsSyncException;
import de.schlichtherle.truezip.fs.FsSyncWarningException;

/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electronica
 *  www.ptsi.pt
 *
 */
public class ZipJob extends CopyJob {
	
	enum PROPERTIES {
		
		move ("deleteOnSource"); // optional
		
		private final String v;
		
		PROPERTIES(String value) { this.v = value; }

		public final String value() { return v; }
	}
	
	/**
	 * 
	 */
	public ZipJob() {
		prependLog = new String(ZipJob.class.getName());
	}

	/**
	 * 
	 * @param src
	 * @param dest
	 * @param move
	 * @return number of zipped files
	 * @throws IOException
	 */
	protected int zipByTrueZipMethod(Path src, File dest, boolean move) throws IOException {
		int zipped = 0;
		//
		Path parentDir = Paths.get(dest.getParentFile().toURI()); 
		if (Files.notExists(parentDir)) {
			Files.createDirectories(parentDir);
		}
		//
		TFile destination = new TFile(new File(dest, src.getFileName().toString()));
		//
		TConfig config = TConfig.push();
		try (InputStream is = new FileInputStream(src.toFile());
				TFileOutputStream tos = new TFileOutputStream(destination)) {
			//
			config.setOutputPreferences(config.getOutputPreferences().set(FsOutputOption.GROW));
			//
			int length = 0;
			byte [] buffer = new byte[1024];
			while ((length = is.read(buffer)) > -1) {
				tos.write(buffer, 0, length);
			}
			tos.flush();
			zipped+=1;
		} finally {
			config.close();
			TVFS.umount(destination);
		}
		// 
		if (move) { // delete the source file ?
			if (!FileUtils.deleteQuietly(src.toFile())) {
				error("Could not delete file " + src.toFile().getAbsolutePath());
			}
		}
		return zipped;
	}
	
	
	
	/**
	 * 
	 * @param src
	 * @param dest
	 * @param move
	 * @throws IOException
	 * @deprecated use zipByTrueZipMethod
	 */
	protected void zipByFsMethod(Path src, File dest, boolean move) throws IOException, ParseException {
		final Map<String, String> env = new HashMap<>();
		FileSystem zipFS = null;
		env.put("create", "true");

		URI uriDestination = URI.create("jar:file:"+Paths.get(dest.toURI()).toUri().getPath());
		//
		Path parentDir = Paths.get(dest.getParentFile().toURI()); 
		if (Files.notExists(parentDir)) {
			Files.createDirectories(parentDir);
		}
		try {
			zipFS = FileSystems.newFileSystem(uriDestination, env);
			//zipFS.provider().checkAccess(pDestination, AccessMode.WRITE);
			//
			final Path target = zipFS.getPath(src.toFile().getName());
			if (move) {
				Files.move(src, target, StandardCopyOption.REPLACE_EXISTING);
			} else {
				Files.copy(src, target, StandardCopyOption.REPLACE_EXISTING);
			}
		} finally {
			if (zipFS != null) zipFS.close();
		}
	}

	
	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.archive.ArchiveJob#doWork(pt.ptsi.stfe.io.engine.PropertyMap)
	 */
	@Override
	protected JobResult doWork(PropertyMap context) throws IOJobException {
		JobResult result = new JobResult(getJobKey());
		//
		SourceLocation [] sources =   (SourceLocation[]) context.get(SOURCES);
		File [] destinations = (File []) context.get(DESTINATIONS);
		//
		IOFileFilter ioFilter = new ComplexFileFilter(context);
		//
		boolean move = context.getBoolean(PROPERTIES.move);
		//
		result.getTimer().start();		
		debug("fired execution @" + result.getTimer().getStartTime());
		//
		int index = 0;
		int zipped = 0;
		//
		for (SourceLocation source : sources) {
			Iterator<File> files = FileUtils.listFiles(source.getLocation(), ioFilter, null).iterator();
			//
			while (files.hasNext()) {
				if (source.getGauge() == -1 || index < source.getGauge()) {
					//
					try {
						final Path src = Paths.get(files.next().toURI());
						for (File dest : destinations) {
						//
							String destName = dest.getAbsolutePath();
						
							destName = WildcardParser.parse(destName, src.getFileName().toString());
							dest = new File(destName);
							//
							zipped += zipByTrueZipMethod(src, dest, move);
							//zipByFsMethod(src, dest, move);
							//
							debug("Zipped from " + src.toString() + " to " + dest.getName() + ((move) ? " and deleted original " : " and kept original") + " in " + result.getTimer().partial());
							result.appendDetail("zipped from " + src.toString() + " to " + dest.getAbsolutePath());
						}
					} catch (Exception e) {
						result.setExitCode(EExitCodes.ERROR);
						result.setMessage(e.getMessage());
						result.getTimer().stop();
						error(e.getMessage(), e);
						throw new IOJobException(result, e);
					}						
				} else {
					// gauge limit reached
					break; 
				}
				index += 1;	
			}
		}
		result.getTimer().stop();
		//
		boolean warningIssues = false;
		try {

			//TVFS.sync(FsSyncOptions.SYNC);
			TVFS.umount();
		} catch (FsSyncWarningException fse) {
			result.appendDetail("Zip Synching issues: " + fse.getMessage() );
			warn(fse.getMessage(), fse);
			warningIssues = true;
		} catch (FsSyncException e) {
			result.setExitCode(EExitCodes.ERROR);
			result.setMessage(e.getMessage());
			warn(e.getMessage(), e);
			throw new IOJobException(result, e);
		}
		
		info("Ended Zip Session : zipped " + zipped + " files... Took " + result.getTimer().delta());		
		result.setMessage("Ended Zip Session : zipped " +zipped + " files... Took " + result.getTimer().delta());
		//
		result.setExitCode((zipped > 0) ? (warningIssues ? EExitCodes.WARNING : EExitCodes.OK ) : EExitCodes.NOACTION);
		//
		return result;
	}	
}