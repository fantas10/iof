/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/02/25
 */
package pt.ptsi.stfe.io.engine.jobs;

import java.io.IOException;

/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class IOJobException extends IOException {

	private final JobResult result;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9163051196241244892L;

	/**
	 * 
	 */
	public IOJobException(JobResult result) {
		super(result.getMessage());
		this.result = result;
	}



	/**
	 * @param message
	 * @param cause
	 */
	public IOJobException(JobResult result, Throwable cause) {
		super(result.getMessage(), cause);
		this.result = result;
	}



	/**
	 * @return the result
	 */
	public JobResult getResult() {
		return result;
	}
	
	

}
