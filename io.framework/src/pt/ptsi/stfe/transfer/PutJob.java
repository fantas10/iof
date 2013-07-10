/**
 * 
 */
package pt.ptsi.stfe.transfer;

import static pt.ptsi.stfe.archive.CopyJob.PROPERTIES.move;
import static pt.ptsi.stfe.io.engine.xml.IOXmlDestinations.PROPERTIES.DESTINATIONS;
import static pt.ptsi.stfe.io.engine.xml.IOXmlSources.PROPERTIES.SOURCES;
import static pt.ptsi.stfe.io.engine.xml.IOXmlTransfer.PROPERTIES.password;
import static pt.ptsi.stfe.io.engine.xml.IOXmlTransfer.PROPERTIES.protocol;
import static pt.ptsi.stfe.io.engine.xml.IOXmlTransfer.PROPERTIES.server;
import static pt.ptsi.stfe.io.engine.xml.IOXmlTransfer.PROPERTIES.username;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import pt.ptsi.stfe.archive.ComplexFileFilter;
import pt.ptsi.stfe.archive.CopyJob;
import pt.ptsi.stfe.archive.SourceLocation;
import pt.ptsi.stfe.io.engine.PropertyMap;
import pt.ptsi.stfe.io.engine.jobs.IOJobException;
import pt.ptsi.stfe.io.engine.jobs.JobResult;
import pt.ptsi.stfe.io.engine.jobs.JobResult.EExitCodes;
import pt.ptsi.stfe.io.engine.xml.IOXmlDestinations;
import pt.ptsi.stfe.io.engine.xml.IOXmlTransfer;
import pt.ptsi.stfe.io.engine.xml.WildcardParser;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * FTP Put or MPut Job, to copy one or multiple files from the local machine to the remote machine<br/>
 * Optionally deletes the local files (performing a move file)
 * 
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direção de Explorção - Serviço de Factura Electrónica
 *
 */
public class PutJob extends CopyJob {

	public final static String FTP_SEPARATOR = "/";

	
	/**
	 * 
	 */
	public PutJob() {
	}
	
	/**
	 * 
	 * @param channel
	 * @param dir
	 * @return
	 * @throws SftpException 
	 */
	public static boolean existsRemoteDirectory(ChannelSftp channel, String path) {
		try {
			channel.ls(path);
		} catch (SftpException e) {
			return false; // non-existent path
		}
		return true;
	}
	
	/**
	 * 
	 * @param channel
	 * @param dir
	 * @return
	 */
	public static void createRemoteDirectories(ChannelSftp channel, String destinationDir) throws SftpException {
		StringTokenizer st = new StringTokenizer(destinationDir, FTP_SEPARATOR);
		String tokenPath = null;
		String path = new String (FTP_SEPARATOR);
		while (st.hasMoreTokens()) {
			tokenPath = st.nextToken();
			path = path.concat(tokenPath).concat(FTP_SEPARATOR);
			if (!existsRemoteDirectory(channel, path)) {
				channel.mkdir(path);
				System.out.println("created " + path);
			}
		}
	}
	
	/**
	 * 
	 * @param ftpClient
	 * @param dir
	 * @return
	 */
	public static void createRemoteDirectories(FTPClient ftpClient, String destinationDir) throws IOException {
		StringTokenizer st = new StringTokenizer(destinationDir, FTP_SEPARATOR);
		String tokenPath = null;
		String path = new String (FTP_SEPARATOR);
		while (st.hasMoreTokens()) {
			tokenPath = st.nextToken();
			path = path.concat(tokenPath).concat(FTP_SEPARATOR);
			try {
				ftpClient.changeDirectory(path);
			} catch (FTPException ex) {
				ex.printStackTrace();
				try {
					ftpClient.createDirectory(path);
				} catch (IllegalStateException | FTPIllegalReplyException
						| FTPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FTPIllegalReplyException e) {
				// TODO
				
			}
		}
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.archive.CopyJob#parseConfiguration(org.jdom2.Element)
	 */
	@Override
	public PropertyMap parseConfiguration(Element configElement) throws JDOMException, IOException, ClassNotFoundException,	IllegalAccessException, InstantiationException {
		PropertyMap config = super.parseConfiguration(configElement);
		// specific:
		IOXmlTransfer xmlTransfer = new IOXmlTransfer();
		config.consumeAll(xmlTransfer.parseConfiguration(configElement));
		// 
		// handle special destinations (not a standard File System, so no use for class File
		Element destElement = configElement.getChild(DESTINATIONS.value());
		List<Element> destinations = destElement.getChildren();
		//
		String [] destination = new String [destinations.size()];
		//
		int index = 0;
		Element to = null;
		String fileDesc = null;
		for (Iterator<?> i = destinations.iterator(); i.hasNext(); ) {
			to = (Element) i.next();
			fileDesc = to.getValue();
			destination[index] = fileDesc;
			index += 1;
		}
		config.consume(IOXmlDestinations.PROPERTIES.DESTINATIONS, destination); // overwrites super.parse()
		//
		return config; 
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.archive.CopyJob#doWork(pt.ptsi.stfe.io.engine.PropertyMap)
	 */
	@Override
	protected JobResult doWork(PropertyMap jobInfo) throws IOJobException {
		//
		JobResult result = new JobResult(getJobKey());
		SourceLocation [] sources =   (SourceLocation[]) jobInfo.get(SOURCES);
		String [] destinations = (String []) jobInfo.get(DESTINATIONS);
		//
		IOFileFilter ioFilter = new ComplexFileFilter(jobInfo);
		boolean isMove = jobInfo.getBoolean(move);
		//
		result.getTimer().start();
		int ncopied = 0;
		debug("fired execution @" + result.getTimer().getStartTime());
		TransferInfo userInfo = new TransferInfo(jobInfo.getString(server), jobInfo.getString(username), jobInfo.getString(password));
		//
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
					// list location on source
					Collection<File> files = (Collection<File>) FileUtils.listFiles(source.getLocation(), ioFilter, null); // null, for no recursive
					int index = 0;
					String destination = null;
					//
					try {
						
						for (File f : files) {
							if (source.getGauge() == -1 || index < source.getGauge()) {
								for (String dest : destinations) {
									destination = WildcardParser.parse(dest, f.getName());
									//
									PutJob.createRemoteDirectories(ftp, destination); 
									//
									trace("going to change (" + jobInfo.getString(protocol) + ") directory to " + destination);
									ftp.changeDirectory(destination);
									debug("changed directory (" + jobInfo.getString(protocol) + ") to  " + destination );
									trace("going to upload " + f.getName());
									ftp.upload(f);
									debug("transfered (" + jobInfo.getString(protocol) + ") " + f.getName() + " from " + f.getPath() + " to " + destination);
									result.appendDetail("ftp transfered " + f.getName() + " from local " + source.getLocationString() + " to remote " + destination);
									ncopied++;
									//
									if (isMove) { // delete the source file ?
										if (!FileUtils.deleteQuietly(f)) {
											error("Could not delete file " + f.getAbsolutePath());
										}
									}
								}
							}else {
								// gauge limit reached
								break; 						
							}
							index++;
						}
						debug(((isMove) ? "moved " : "copied ") + index + " files sucessfully from " + source.getLocation().getName() + " in " + result.getTimer().partial());
						result.setMessage(((isMove) ? "moved " : "copied ") + index + " files sucessfully from " + source.getLocation().getName() + " in " + result.getTimer().partial());
						
					} catch (IOException | ParseException | FTPAbortedException | FTPDataTransferException | FTPException | FTPIllegalReplyException e) {
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
						} catch (FTPException | FTPIllegalReplyException e) {
							// TODO ???
						}
					}
				}
			} catch (IOException | FTPException | FTPIllegalReplyException e) {
				result.getTimer().stop();
				result.setExitCode(EExitCodes.ERROR);				
				result.setMessage(e.getMessage());
				error(e);
				throw new IOJobException(result, e);
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
				session.setConfig("StrictHostKeyChecking", "no");
				session.connect();
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
				// list location on source
				Collection<File> files = (Collection<File>) FileUtils.listFiles(source.getLocation(), ioFilter, null); // null, for no recursive
				int index = 0;
				String destinationFile = null;
				String destination = null;
				//
				try {
					for (File f : files) {
						if (source.getGauge() == -1 || index < source.getGauge()) {
							for (String dest : destinations) {
								destination = WildcardParser.parse(dest, f.getName());
								destinationFile = Paths.get(destination, f.getName()).toString(); 
								// 
								PutJob.createRemoteDirectories(sftp, destination); 
								//
								sftp.put(f.getAbsolutePath(), destinationFile);
								debug("transfered (" + jobInfo.getString(protocol) + ") " + f.getName() + " from " + f.getPath() + " to " + destination);
								ncopied++;
								//
								if (isMove) { // delete the source file ?
									if (!FileUtils.deleteQuietly(f)) {
										error("Could not delete file " + f.getAbsolutePath());
									}
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
				} catch (SftpException | ParseException ioe) {
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