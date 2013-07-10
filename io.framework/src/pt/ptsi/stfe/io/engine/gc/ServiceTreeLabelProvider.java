/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/08/02
 */
package pt.ptsi.stfe.io.engine.gc;

import java.io.File;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import pt.ptsi.stfe.io.engine.IOService;
import pt.ptsi.stfe.io.engine.jobs.JobKey;

/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class ServiceTreeLabelProvider implements ILabelProvider {

	private final Shell shell;
	
	/**
	 * 
	 */
	public ServiceTreeLabelProvider(Shell shell) {
		this.shell = shell;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object arg0) {
		if (arg0 instanceof IOService) {
			if (arg0 != null) {
				return new Image(shell.getDisplay(), "resources/icon-service.png");
			}
		}
		if (arg0 instanceof JobKey) {
			if (arg0 != null) {
				 return new Image(shell.getDisplay(), "resources/icon-job.png");
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object arg0) {
		if (arg0 instanceof IOService) {
			if (arg0 != null) {
				return ((IOService) arg0).getName();
			}
		}
		if (arg0 instanceof JobKey) {
			if (arg0 != null) {
				return ((JobKey) arg0).getJobName();
			}
		}		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener arg0) {

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub

	}

}
