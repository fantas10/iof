/**
 * Servi�o Factura Electr�nica
 * PT � Sistemas de Informa��o, S.A. 
 * 
 * io.framework
 * 2011/03/29
 */
package pt.ptsi.stfe.io.engine.jobs;

import org.quartz.Job;

/**
 * Base unit interface for a chain of jobs
 * <br>
 * Classes implementing this interface will schedule this JobChain as a Quartz {@link Job}
 * 
 * <br>
 * @see IOJob
 * 
 * @author Nuno P. Louren�o <nuno-p-lourenco@telecom.pt>
 *  Direc��o de Explora��o - Servi�o de Factura Electr�nica
 *  www.ptsi.pt
 *
 */
public interface JobChain<V extends IOJob> extends Job {
	
}
