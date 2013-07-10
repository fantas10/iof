/**
 * 
 */


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import pt.ptsi.iof.alert.Validate;
import pt.ptsi.stfe.io.engine.PropertyMap;
import pt.ptsi.stfe.io.engine.jobs.IOJobException;
import pt.ptsi.stfe.io.engine.jobs.JobResult;
import pt.ptsi.stfe.transfer.PutJob;
import utils.system;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;
import com.jcraft.jsch.ChannelSftp.LsEntry;


import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;


/**
 * @author XPTS912
 *
 */
public class TestMain {

	/**
	 * 
	 * @param zipFile
	 * @param destination
	 */
	public static void sevenZipExtract(final File zipFile, final File destination) {
		RandomAccessFile ras = null;
		ISevenZipInArchive inArchive = null;
		try {
			ras = new RandomAccessFile(zipFile, "r");
			inArchive = SevenZip.openInArchive(null, // auto-detect 
					new RandomAccessFileInStream(ras));
			
			// getting simple interface
			ISimpleInArchive sia = inArchive.getSimpleInterface();
			for (final ISimpleInArchiveItem item : sia.getArchiveItems()) {
				//
				final Path destDir = Paths.get(destination.toURI());
				if (Files.notExists(destDir)) {
					Files.createDirectories(destDir);
				}
				//
				final int[] hash = new int[] { 0 };
				ExtractOperationResult result = item.extractSlow(new ISequentialOutStream() {
					
					@Override
					public int write(byte[] data) throws SevenZipException {
						try {
							hash[0] ^= Arrays.hashCode(data);
							try (OutputStream os = new FileOutputStream(new File(destDir.toString(), item.getPath()), true)) {
								os.write(data);
								os.flush();
								os.close();
							}
							
						} catch (IOException e) {
								e.printStackTrace();
						}
						
						return data.length;
					}
				}, "cLip");
				
				System.out.println(result.toString() + " , " + hash[0]);
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inArchive.close();
				ras.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
    /**
     * Creates a zip file at the specified path with the contents of the specified directory.
     * NB:
     *
     * @param directoryPath The path of the directory where the archive will be created. eg. c:/temp
     * @param zipPath The full path of the archive to create. eg. c:/temp/archive.zip
     * @throws IOException If anything goes wrong
     */
	/*
    public static void createZip(String directoryPath, String zipPath) throws IOException {
        FileOutputStream fOut = null;
        BufferedOutputStream bOut = null;
        ZipArchiveOutputStream tOut = null;
        String base = "";
        try {
        	//
            fOut = new FileOutputStream(new File(zipPath));
            bOut = new BufferedOutputStream(fOut);
            tOut = new ZipArchiveOutputStream(bOut);
            File[] files = new File(directoryPath).listFiles();
            //
            for (File f : files) {
                String entryName = base + f.getName();
                ZipArchiveEntry zipEntry = new ZipArchiveEntry(f, entryName);
                tOut.putArchiveEntry(zipEntry);
                IOUtils.copy(new FileInputStream(f), tOut);
                tOut.closeArchiveEntry();                
            }
            //
        } finally {
            tOut.finish();
            tOut.close();
            bOut.close();
            fOut.close();
        }
 
    }*/
    
    /**
     * 
     * @param directoryPath
     * @param zipPath
     * @throws IOException
     */
    /*
    public static void createTZip(String directoryPath, String zipPath) throws IOException {
    	TFile.setDefaultArchiveDetector(
    			  new TArchiveDetector(
    			  "zip",
    			  new ZipDriver(IOPoolLocator.SINGLETON)));
    	//
    	File[] files = new File(directoryPath).listFiles();
    	FsPath path = new FsPath(new File(zipPath));
    	TFile tFile = new TFile(path);
    	//
    	if (!(tFile.exists() && tFile.isArchive())) {
    		tFile.createNewFile();
    	}
    	//
    	TConfig config = TConfig.push();
    	
    	TFile entry = null;
    	TFile zipFile = null;
    	try {
    		config.setOutputPreferences(config.getOutputPreferences().set(FsOutputOption.GROW));
    		for (File f : files) {
    			entry = new TFile(f);
    			zipFile = new TFile(tFile, entry.getName());
    			

    			TFileOutputStream out = new TFileOutputStream(tFile);
				try {
				    TFile.cp(f, out);
				} finally {
				    out.close(); // ALWAYS close the resource here!
				}

    			//entry.cp_p(zipFile);
    			//tos = new TFileOutputStream(entry);
    			//TFile.cp_p(entry, zipFile);
    			//break;
    			System.out.println("isArchive? "+zipFile.isArchive());
    			TFile.umount();
    			break;
    		}
    		
    	} catch (EOFException eof) {
    		System.out.println(eof.getMessage());
    		System.out.println(eof.getLocalizedMessage());
    		eof.printStackTrace();
    	} finally {
    		config.close();
    		tFile.compact();
    		//TFile.umount(zipFile);
    	}
    }*/

    
    /**
     * 
     * @param directoryPath
     * @param zipPath
     * @throws IOException
     * @throws URISyntaxException 
     */
	public static void createJDK7Zip(String directoryPath, String zipPath) throws IOException{
		//
		File[] files = new File(directoryPath).listFiles();
		//
		final Path path = Paths.get(zipPath);
		final URI uri = URI.create("jar:file:" + path.toUri().getPath());
		//
		final Map<String, String> env = new HashMap<>();
		if (!Files.exists(path)) {
			env.put("create", "true");
		}
		
		try (FileSystem zipFS = FileSystems.newFileSystem(uri, env)) {
			//zipFS.provider().checkAccess(path, AccessMode.WRITE);
			for (File f : files) {
				final Path source = Paths.get(f.toURI());
				final Path target = zipFS.getPath(f.getName());
				// copy to
				//Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
				Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);  //if move enabled
			}
			zipFS.close();
		}
			
		/*
		FileSystem zipFS = FileSystems.newFileSystem(uri, env);
		final Path root = zipFS.getPath("/");
		for (File f : files) {
			final Path src = Paths.get(f.getAbsolutePath());
			final Path dest = zipFS.getPath(src.toString());
			final Path destParent = dest.getParent();
			if (Files.notExists(destParent)) {
				Files.createDirectories(destParent);
			}
			Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
		}
		zipFS.close();
		*/
		/*
		
		
		
		
		
		
		
		
		Path zipFilePath = null;
		//
		if (!Files.exists(Paths.get(zipPath))) {
			zipFilePath = Files.createFile(Paths.get(zipPath));
		} else {
			zipFilePath = Paths.get(zipPath);
		}
		//		
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		//
		ZipEntry entry = null;
		for (File f : files) {
			try {
				fos = new FileOutputStream(zipFilePath.toFile(), true);
				zos = new ZipOutputStream(fos);
				entry = new ZipEntry(f.getName());
				//
				zos.putNextEntry(entry);
				//
				long read = Files.copy(Paths.get(f.getAbsolutePath()), zos);
				System.out.println(read);
				zos.flush();
				zos.closeEntry();
			} finally {
				zos.finish();
				fos.close();
				zos.close();
			}
		}*/	
	}
	

	/**
	 * 
	 * @param rawWildcard
	 * @param name file name (optional)
	 * @return
	 */
	public static final String replaceWildcard(String rawWildcard, String ...filename) {
		// strip wildcard reserved chars - *{<expr>}
		String wildcard = rawWildcard.substring(2, rawWildcard.length()-1);
		String wType = wildcard.substring(0, 4);
		String wVariant = wildcard.substring(4);
		//
		String solution = wildcard;
		//
		switch (wType) {
			case "Date":
				if (wVariant.length() < 2) {
					System.out.println("ERROR in Wildcard DATE Format ("+wVariant+")");
				} else {
					Date now = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat(wVariant.substring(1));
					solution = sdf.format(now);
				}
				break;
			case "File":
				if (wVariant.length() == 0) { // Fullname
					solution = filename[0];
				} else if (wVariant.contains(",")) { // isInterval
					String []interval = wVariant.substring(1, wVariant.length()-1).split(",");
					solution = filename[0].substring(Integer.parseInt(interval[0]), Integer.parseInt(interval[1]));						
				} else { // isStartPoint
					int start = Integer.parseInt(wVariant.substring(1, wVariant.length()-1));
					solution = filename[0].substring(start);
				}
				break;
			default:
				System.out.println("ERROR: no intiendo!");
				break;
		}
		//
		return solution;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception  {
		/*
		String source = "C:/Temp/project/iof/destination3";
		String dest = "C:/Temp/project/iof/destination/eddie.zip";
		//
		createJDK7Zip(source, dest);
		*/
		
		Validate v = new Validate() {
			@Override
			public PropertyMap parseConfiguration(Element configElement)
					throws JDOMException, IOException, ClassNotFoundException,
					InstantiationException, IllegalAccessException {
				return null;
			}
			
			@Override
			protected JobResult doWork(PropertyMap context) throws IOJobException {
				return null;
			}
		};
		boolean value = v.validateCountCondition("5+", 6);
		System.out.println(value ? "validated!" : "validation failed..." );
		
		boolean result = Pattern.matches("^GTE_BLG_\\d{10}\\d{8}.dat$", "GTE_BLG_000002088_20130705.dat");
		System.out.println(result);
		
		System.out.println("Path = " + Paths.get("/feleadtr/input", "wireless", "xpto.dat").toString());
		System.exit(0);
		
		/**
		 * IO Pattern Testing
		 */
		String regex = "^(\\w+)_(\\w+)_(\\d+).xml$";
		//String expression = "C:/Temp/project/iof/destination/*{Date.yyyy}/*{Date.MM}/PTEMPRESA.*{File[15,17]}-*{File[20,24]}.zip";
		String expression = "B:/BackupMulticert/500k/TMN/*{Date.yyyyMM}/*{File[0,12]}/*{File}";
		String filename = "500KWLN75NO102012_PN020000002_504615947.xml";
		//
		System.out.println( Pattern.matches(regex, filename));
		//
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(expression);
		//
		int i = 0;
		String solution = null;
		while (m.find()) {
			solution = replaceWildcard(m.group(), filename);
			expression = expression.replace(m.group(), solution);
			i+=1;
		}
		//
		System.out.println(expression);

		
		
		
		/**
		 * SevenZip testing
		 */
		/*
		File zipFile = new File("C:\\Temp\\project\\eDocsIOF\\sourceNFS\\Test\\PTCASA.CLIP.10.032012.EMAIL.RES.zip");
		File destinationDir = new File("C:\\Temp\\project\\eDocsIOF\\sourceNFS\\Test");
		TestMain.sevenZipExtract(zipFile, destinationDir);
	
		Pattern p = Pattern.compile("/tbmtmn/tmn2v.do(.+)");
		Matcher m = p.matcher("https://facturaelectronica-qa.telecom.pt/tbmtmn/tmn2v.do?tokenId=Qk0glknDeprE2fOxUFqQUUJNIJZJw3qalCAaMhYNOC0XAAsi5%2BwLxqYpGCRavDGwJglSDgLLfbhbVVQXirnvMDa%2FFKA4O97Uh3Y%2BCvezvH7nOIjSvM9lAg%3D%3D");
		if (m.find()) {
			System.out.println(m.groupCount());
			System.out.println(m.group());
		}	
		*/
		
		/**
		 * Jsch testing
		 */
		
		// 10.50.7.115
		// user: xoli093
		// pass: l2k"NMV3
		// sftp: port 22
		
		// libraries to use:
		/*
		JSch jsch = new JSch();	
		String user = "xoli093";
		final String pass = "l2k\"NMV3";
		String host = "10.50.7.115";
		
		try {
			Session session = jsch.getSession(user, host, 22);
			session.setPassword(pass);
			Properties config = new Properties();
			config.setProperty("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			
			
			Channel channel = session.openChannel("sftp");
			channel.connect();
			
			Vector<LsEntry> result =  ((ChannelSftp)channel).ls("/tmp");
			for (LsEntry path : result) {
				System.out.println(path.getFilename());
			}
					
			((ChannelSftp)channel).exit();
			session.disconnect();
					
					
		} catch (Exception e ) {
			e.printStackTrace();
		} */
		
	}

}
