/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/04/04
 */
package pt.ptsi.stfe.io.engine;

/**
 * JobFactory for commons jobs (i.e., jobs from the standard IOF Framework)
 * 
 * 
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
enum JobFactory {
	
		
	// IO Archive Jobs
	CopyJob(pt.ptsi.stfe.archive.CopyJob.class),
	DeleteJob(pt.ptsi.stfe.archive.DeleteJob.class),
	ZipJob(pt.ptsi.stfe.archive.ZipJob.class),
	UnzipJob(pt.ptsi.stfe.archive.UnzipJob.class);
	
	private final Class<?> clazz;
	
	JobFactory(Class<?> c) { 	this.clazz = c; }

	public final Class<?> getJobClass() { return this.clazz; }		
	
}
