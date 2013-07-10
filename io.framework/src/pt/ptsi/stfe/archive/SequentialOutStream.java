/**
 * 
 */
package pt.ptsi.stfe.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;

/**
 * @author XPTS912
 *
 */
public class SequentialOutStream implements ISequentialOutStream {

	private final File destinationFile;
	private final int[] hash = new int[] { 0 };
	
	/**
	 * 
	 */
	public SequentialOutStream(File dest) {
		this.destinationFile = dest;
	}

	/* (non-Javadoc)
	 * @see net.sf.sevenzipjbinding.ISequentialOutStream#write(byte[])
	 */
	@Override
	public int write(byte[] data) throws SevenZipException {
		SevenZipException sevenZipException = null;
		hash[0] ^= Arrays.hashCode(data);
		//
		try (OutputStream os = new FileOutputStream(getDestinationFile(), true)) {
			os.write(data);
			os.flush();
			os.close();
			
		} catch (IOException ioe) {
			 sevenZipException = new SevenZipException(ioe.getMessage(), ioe);
			 throw sevenZipException;
		}
		return data.length;
	}

	/**
	 * @return the destinationFile
	 */
	public final File getDestinationFile() {
		return destinationFile;
	}

	/**
	 * @return the hash
	 */
	public final int[] getHash() {
		return hash;
	}
	
	

}
