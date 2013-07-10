/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/08/02
 */
package pt.ptsi.stfe.io.engine.gc;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import pt.ptsi.stfe.io.engine.IOEngine;
import pt.ptsi.stfe.io.engine.IOService;
import pt.ptsi.stfe.io.engine.ServiceKey;
import pt.ptsi.stfe.io.engine.ServiceMonitor;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class SWTEngineWindow extends ApplicationWindow {

	private TreeViewer treeViewer = null;
	private final IOEngine runningEngine;
	
	
	/**
	 * @param parentShell
	 */
	public SWTEngineWindow(Shell parentShell, IOEngine engine) {
		super(parentShell);
		addStatusLine();
		this.runningEngine = engine;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		getShell().setText("IOF Engine");
		Image icon = new Image(getShell().getDisplay(), "resources/email-icon-16x16.png");
		getShell().setImage(icon);		
		parent.setSize(700,800);
		//
		Composite mainCmp = new Composite(parent, SWT.LEFT);
		GridLayout gridLayout = new GridLayout();
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		mainCmp.setLayout(gridLayout);
		mainCmp.setLayoutData(gridData);
		
		ToolBar toolBar = new ToolBar(mainCmp, SWT.FLAT | SWT.RIGHT);
		//
		Image logo = new Image(getShell().getDisplay(), "resources/email-icon-96x96.png");
		
		// body
		SashForm sashForm = new SashForm(mainCmp, SWT.HORIZONTAL | SWT.NULL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// TreeViewer
		TreeViewer tv = new TreeViewer(sashForm);
		tv.setContentProvider(new ServiceTreeContentProvider());
		tv.setLabelProvider(new ServiceTreeLabelProvider(getShell()));
		//
		Iterator<FileAlterationObserver> observers = runningEngine.getFsMonitor().getObservers().iterator();
		FileAlterationObserver obs = null;
		FileAlterationListener obsListener = null;
		Map<ServiceKey, IOService> services = null;
		while (observers.hasNext()) {
			obs = observers.next();
			Iterator<FileAlterationListener> listeners = obs.getListeners().iterator();
			while (listeners.hasNext()) {
				obsListener = listeners.next();
				if (obsListener instanceof ServiceMonitor) {
					ServiceMonitor sm = (ServiceMonitor) obsListener;
					services = sm.getServiceMap();
				}
			}
		}
		if ((services != null) && (services.size() > 0)) {
			Iterator<ServiceKey> skit = services.keySet().iterator();
			while (skit.hasNext())  {
				System.out.println(skit.next().getServiceName()); 
			}
			tv.setInput(services);
		}
		//
		Group g = new Group(sashForm, SWT.SHADOW_IN);
		g.setText("Info");
		GridLayout gLayout = new GridLayout(2, false);
		g.setLayout(gLayout);
		g.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
		//
		CLabel clabel = new CLabel(g, SWT.LEFT );
		clabel.setForeground(new Color(getShell().getDisplay(), 0xFF, 0, 0xAB));
		clabel.setText("Property 1");
		//
		Text clabel2 = new Text(g, SWT.CENTER );
		clabel2.setForeground(new Color(getShell().getDisplay(), 0xFF, 0, 0xAB));
		clabel2.setText("Value 1");
		clabel2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		clabel2.setEditable(false);
		//
		CLabel clabel3 = new CLabel(g, SWT.CENTER);
		clabel3.setForeground(new Color(getShell().getDisplay(), 0xFF, 0, 0xAB));
		clabel3.setText("Property 2");
		//
		Text clabel4 = new Text(g, SWT.CENTER );
		clabel4.setForeground(new Color(getShell().getDisplay(), 0xFF, 0, 0xAB));
		clabel4.setText("Value 2");
		clabel4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		clabel4.setEditable(false);
		//
		StyledText st = new StyledText(g, SWT.CENTER);
		st.setBackground(new Color(getShell().getDisplay(), 0, 0xFF, 0xAB));
		st.setText("---- ola ----");
		new Label(g, SWT.NONE);
		sashForm.setWeights(new int[] {269, 152});
		//
		setStatus("IOEngine status bar");
		
		return sashForm;
	}

	/**
	 * @return the treeViewer
	 */
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

}
