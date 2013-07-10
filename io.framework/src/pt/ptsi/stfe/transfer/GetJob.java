/**
 * 
 */
package pt.ptsi.stfe.transfer;

import static pt.ptsi.stfe.archive.CopyJob.PROPERTIES.move;
import static pt.ptsi.stfe.io.engine.xml.IOXmlDestinations.PROPERTIES.DESTINATIONS;
import static pt.ptsi.stfe.io.engine.xml.IOXmlFilterOptions.PROPERTIES.FILTEROPTIONS;
import static pt.ptsi.stfe.io.engine.xml.IOXmlSources.PROPERTIES.SOURCES;
import static pt.ptsi.stfe.io.engine.xml.IOXmlTransfer.PROPERTIES.password;
import static pt.ptsi.stfe.io.engine.xml.IOXmlTransfer.PROPERTIES.protocol;
import static pt.ptsi.stfe.io.engine.xml.IOXmlTransfer.PROPERTIES.server;
import static pt.ptsi.stfe.io.engine.xml.IOXmlTransfer.PROPERTIES.username;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import pt.ptsi.stfe.archive.CopyJob;
import pt.ptsi.stfe.archive.SourceLocation;
import pt.ptsi.stfe.io.engine.PropertyMap;
import pt.ptsi.stfe.io.engine.jobs.IOJobException;
import pt.ptsi.stfe.io.engine.jobs.JobResult;
import pt.ptsi.stfe.io.engine.jobs.JobResult.EExitCodes;
import pt.ptsi.stfe.io.engine.xml.IOXmlDestinations;
import pt.ptsi.stfe.io.engine.xml.IOXmlFilterOptions;
import pt.ptsi.stfe.io.engine.xml.IOXmlSources;
import pt.ptsi.stfe.io.engine.xml.IOXmlTransfer;
import pt.ptsi.stfe.io.engine.xml.WildcardParser;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;


/**
 * @author eddie
 *
 */
public class GetJob extends CopyJob {
	
	/**
	 * 
	 */
	public GetJob() {
	}

	@Override
	public PropertyMap parseConfiguration(Element configElement) throws JDOMException, IOException, ClassNotFoundException,
			IllegalAccessException, InstantiationException {

		PropertyMap jobInfo = new PropertyMap();
		
		// handle Sources:
		Element sourcesElement = configElement.getChild(SOURCES.value());
		IOXmlSources handleSources = new IOXmlSources(false);
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
		// specific:
		IOXmlTransfer xmlTransfer = new IOXmlTransfer();
		jobInfo.consumeAll(xmlTransfer.parseConfiguration(configElement));
		debug("parsed correctly");		
		//
		return jobInfo;
	}
	

	@Override
	protected JobResult doWork(PropertyMap jobInfo) throws IOJobException {
		//
		JobResult result = new JobResult(getJobKey());
		SourceLocation [] sources =   (SourceLocation[]) jobInfo.get(SOURCES);
		File [] destinations = (File []) jobInfo.get(DESTINATIONS);
		//
		// TODO: Filter is not only REGEX, adapt for remaining filter types
		//IOFileFilter ioFilter = new ComplexFileFilter(jobInfo);
		boolean isMove = jobInfo.getBoolean(move);
		//
		result.getTimer().start();
		int ncopied = 0;
		debug("fired execution @" + result.getTimer().getStartTime());
		TransferInfo userInfo = new TransferInfo(jobInfo.getString(server), jobInfo.getString(username), jobInfo.getString(password));
		if (ETransferProtocol.FTP.toString().equalsIgnoreCase(jobInfo.getString(protocol))) {
			final FTPClient ftp = new FTPClient();
			
			try {
				ftp.connect(userInfo.getServer(), ETransferProtocol.FTP.getPort());
				ftp.login(userInfo.getUsername(), userInfo.getPassword());
				String [] status = ftp.serverStatus();
				for (String s : status) {
					trace(s);
				}
				for (SourceLocation source : sources) {
					int index = 0;
					File destinationDir = null;
					String destination = null;
					ftp.changeDirectory(source.getLocationString());
					trace("changed to directory " + source.getLocationString());
					FTPFile [] files = ftp.list();
					trace("Found " + files.length + " leadtracking files on " + source.getLocationString());
					for (FTPFile fp : files) {
						trace(fp.getName());
					}
					String regex = jobInfo.getString(IOXmlFilterOptions.PROPERTIES.FILTER);
					for (FTPFile f : files) {
						if (source.getGauge() == -1 || index < source.getGauge()) {
							if (Pattern.matches(regex, f.getName())) {
								for (File dest : destinations) {
									destination = WildcardParser.parse(dest.getAbsolutePath(), f.getName());
									destinationDir = new File(destination);
									final Path destDir = Paths.get(destinationDir.toURI());
									if (Files.notExists(destDir)) {
										//Files.createDirectories(destDir);
										debug("created " + destDir.toString());
									}
									ftp.download(f.getName(), new File(destinationDir, f.getName()));
									ncopied++;
									debug("transfered (" + jobInfo.getString(protocol) + ") " + f.getName() + " from " + source.getLocationString() + " to " + destinationDir.getAbsolutePath());
									result.appendDetail("ftp transfered " + f.getName() + " from remote " + source.getLocationString() + " to local " + destinationDir.getAbsolutePath());
								}						
								//
								if (isMove) { // delete the source file ?
									ftp.deleteFile(f.getName());
									debug("deleted source " + f.getName());
								}
							}

						} else {
							// gauge limit reached
							break; 						
						}
						index++;
					}

				}
				
				
			} catch (IOException | ParseException | FTPException | FTPIllegalReplyException | FTPAbortedException | FTPListParseException | FTPDataTransferException e) {
				result.getTimer().stop();
				result.setExitCode(EExitCodes.ERROR);				
				result.setMessage(e.getMessage());
				error(e);
				throw new IOJobException(result, e);
			} finally {
				try {
					ftp.logout();
				if (ftp.isConnected()) 
					ftp.disconnect(true);
				} catch (IOException | FTPException | FTPIllegalReplyException e) {}
			}
			
		} else if (ETransferProtocol.FTPS.toString().equalsIgnoreCase(jobInfo.getString(protocol))) {
			// ERROR: not implemented
		} else if (ETransferProtocol.SFTP.toString().equalsIgnoreCase(jobInfo.getString(protocol))) {
			// SFTP //
			JSch jsch = new JSch();
			Session session = null;
			Channel channel = null;
			try {
				session = jsch.getSession(userInfo.getUsername(), userInfo.getServer(), 22);
				session.setUserInfo(userInfo);
				// http://sourceforge.net/apps/mediawiki/jsch/index.php?title=Configuration_options
				session.setConfig("StrictHostKeyChecking", "no");
				session.connect();
				System.out.println("Session isConnected " + session.isConnected());
				//
				channel = session.openChannel(jobInfo.getString(protocol));
				channel.connect();

			} catch (JSchException e) {
				result.getTimer().stop();
				result.setExitCode(EExitCodes.ERROR);				
				result.setMessage(e.getMessage());
				error(e);
				throw new IOJobException(result, e);
			}
			//
			ChannelSftp sftp = (ChannelSftp) channel;
			for (SourceLocation source : sources) {
				int index = 0;
				File destinationDir = null;
				String destination = null;
				try {
					sftp.cd(source.getLocation().getAbsolutePath());
					@SuppressWarnings("unchecked")
					List<LsEntry> lsOutput = sftp.ls(".");
					String regex = jobInfo.getString(IOXmlFilterOptions.PROPERTIES.FILTER);
					InputStream get = null;
					for (LsEntry entry : lsOutput) {
						if (source.getGauge() == -1 || index < source.getGauge()) {
							
							if (Pattern.matches(regex, entry.getFilename())) {
								get = sftp.get(entry.getFilename());
								for (File dest : destinations) {
									destination = WildcardParser.parse(dest.getAbsolutePath(), entry.getFilename());
									destinationDir = new File(destination);
									final Path destDir = Paths.get(destinationDir.toURI());
									if (Files.notExists(destDir)) {
										//Files.createDirectories(destDir);
										debug("created " + destDir.toString());
									}
									try (FileOutputStream fos = new FileOutputStream(new File(destinationDir, entry.getFilename()))) {
										IOUtils.copy(get, fos);
										debug("transfered (" + jobInfo.getString(protocol) + ") " + entry.getFilename() + " from " + source.getLocation().getPath() + " to " + destinationDir.getAbsolutePath());
										ncopied++;
									}	
								}						
								//
								if (isMove) { // delete the source file ?
									debug("deleting source " + entry.getFilename());
									sftp.rm(entry.getFilename());
								}
							}
						} else {
							// gauge limit reached
							break; 						
						}
						index++;
					}

					debug(((isMove) ? "moved " : "copied ") + index + " files sucessfully from " + source.getLocation().getName() + " in " + result.getTimer().partial());
					result.setMessage(((isMove) ? "moved " : "copied ") + index + " files sucessfully from " + source.getLocation().getName() + " in " + result.getTimer().partial());
				} catch (SftpException | ParseException | IOException ioe) {
					result.getTimer().stop();
					result.setExitCode(EExitCodes.ERROR);				
					result.setMessage(ioe.getMessage());
					error(ioe);
					throw new IOJobException(result, ioe);
				}	
			}
			channel.disconnect();
			session.disconnect();

		} else {
			// ERROR: not implemented
		}
		//
		result.getTimer().stop();
		result.setExitCode((ncopied > 0) ? EExitCodes.OK : EExitCodes.NOACTION);
		result.setMessage("ended execution, took " + result.getTimer().delta());
		debug("ended execution, took " + result.getTimer().delta());
		//
		return result;
	}
	
	

}
