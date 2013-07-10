/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/08/02
 */
package pt.ptsi.stfe.io.engine.gc;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import pt.ptsi.stfe.io.engine.IOService;
import pt.ptsi.stfe.io.engine.ServiceKey;
import pt.ptsi.stfe.io.engine.ServiceMonitor;
import pt.ptsi.stfe.io.engine.jobs.JobKey;

/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class ServiceTreeContentProvider implements ITreeContentProvider {

	TreeViewer tv;
	
	/**
	 * 
	 */
	public ServiceTreeContentProvider() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object arg0) {
		if (arg0 instanceof IOService) {
			return ((IOService) arg0).getJobs().toArray();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object arg0) {
		if (arg0 instanceof IOService) return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object arg0) {
		if ((arg0 instanceof Map) && !(((Map<ServiceKey, IOService>) arg0).isEmpty())) {
			Map<ServiceKey, IOService> services = (Map<ServiceKey, IOService>) arg0;
			if (services != null && !services.isEmpty()) {
				return services.values().toArray();
			}
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		tv = (TreeViewer) arg0;

	}

}
