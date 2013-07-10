/**
 * Servi�o Factura Electr�nica
 * PT � Sistemas de Informa��o, S.A. 
 * 
 * io.framework
 * 2011/04/04
 */
package pt.ptsi.stfe.io.engine;

/**
 * JobFactory for commons jobs (i.e., jobs from the standard IOF Framework)
 * 
 * 
 * @author Nuno P. Louren�o <nuno-p-lourenco@telecom.pt>
 *  Direc��o de Explora��o - Servi�o de Factura Electr�nica
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
