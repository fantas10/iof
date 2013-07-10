/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
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
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public interface JobChain<V extends IOJob> extends Job {
	
}
