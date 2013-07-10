/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/01/24
 */
package pt.ptsi.stfe.io.engine.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pt.ptsi.stfe.io.engine.IOKey;
import pt.ptsi.stfe.util.ExecutionTimer;

/**
 * TODO:
 * 		specialized logger appender 
 * 
 * 		JobResult appender (for hierarchy adding)
 * 
 * 		Should append other JobResults for JobChain 
 * 
 * 
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class JobResult implements Serializable {

	
	private final List<JobResult> results = new ArrayList<>();
	
	private final IOKey key;
	
	private String message = new String();
	
	private StringBuilder details = new StringBuilder();
	
	private final ExecutionTimer timer = new ExecutionTimer();
	
	/**
	 * Exit Codes enumeration
	 */
	public static enum EExitCodes {
		
		NOACTION (1),
		
		OK (0),
		
		ERROR (-2),
		
		FATAL (-3),
		
		WARNING (-1);
		
		int value;
		
		EExitCodes(int value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public final int getValue() {
			return value;
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private EExitCodes exitCode = EExitCodes.OK;
	
	/**
	 * service key or job key
	 * @param jobKey
	 */
	public JobResult(IOKey key) {
		this.key = key;
	}
	
	/**
	 * 
	 * @param 
	 */
	public JobResult() {
		key = new IOKey() {
			
			@Override
			public String getKey() {
				return "dummy-key";
			}
		};
	}
	
	
	
	/**
	 * @return the exitCode
	 */
	public EExitCodes getExitCode() {
		return exitCode;
	}



	/**
	 * @param exitCode the exitCode to set
	 */
	public void setExitCode(EExitCodes exitCode) {
		this.exitCode = exitCode;
	}



	/**
	 * 
	 * @param anotherResult
	 */
	public void appendResult(JobResult anotherResult) {
		results.add(anotherResult);
		exitCode = anotherResult.getExitCode();
	}

	/**
	 * @return the results
	 */
	public final List<JobResult> getResults() {
		return results;
	}

	/**
	 * Gets the associated {@link IOKey} (Service ou Job)
	 * 
	 * @return the key
	 */
	public final IOKey getKey() {
		return key;
	}

	/**
	 * gets the jobResult master message
	 * @return the message
	 */
	public final String getMessage() {
		return message;
	}

	/**
	 * sets the jobResult master message
	 * @param message the message to set
	 */
	public final void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 
	 * @return the timer
	 */
	public final ExecutionTimer getTimer() {
		return timer;
	}
	
	/**
	 * Appends a detail message on current <code>JobResult</code>
	 * @param detail
	 */
	public void appendDetail(String detail) {
		this.details.append(detail);
		this.details.append('\n');
	}

	/**
	 * Get job detail messages 
	 * @return the details
	 */
	public final String getDetails() {
		return details.toString();
	}
	
	
	
	
}
