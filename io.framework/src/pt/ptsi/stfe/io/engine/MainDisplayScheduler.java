/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2010/07/22
 */
package pt.ptsi.stfe.io.engine;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import pt.ptsi.stfe.io.engine.gc.SWTEngineWindow;
import pt.ptsi.stfe.io.engine.jobs.JobResult.EExitCodes;


/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class MainDisplayScheduler {

	/**
	 * Main Application Window
	 */
	static SWTEngineWindow win = null;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Logger logger = MainConsoleScheduler.logger;
		
		logger.info("io/framework:IOEngineSWT start daemon");
		File configFile = null;
		if (args.length < 1) {
			// try to look in default location
			configFile = new File("config/config.properties");
		} else {
			configFile = new File(args[0]);
		}
		 
		if (!configFile.isFile()) {
			logger.error("Wrong arguments, usage $ IOEngine @propertiesFile");
			System.exit(EExitCodes.ERROR.getValue());
		}
		//
		FileReader readerConfig = null;
		Properties config = new Properties();
		try {
			readerConfig = new FileReader(configFile);
			config.load(readerConfig);
			//
		} catch (IOException ioe) {
			logger.error("Error processing config file " + ioe.getMessage());
			System.exit(EExitCodes.ERROR.getValue());
		} finally {
			if (readerConfig != null)
				try {
					readerConfig.close();
				} catch (IOException e) {}
		}
		
		Display display = new Display();
		final Shell shell = new Shell(display);
		//Image image = new Image(display, "resources/email-icon-16x16.png");
		Image image = new Image(display, "resources/logo_pt_tiny.gif");
		final Tray tray = display.getSystemTray();
		if (tray == null) {
			logger.error("io/framework:IOEngineSWT The System Tray is no available!");
			System.exit(EExitCodes.ERROR.getValue());
		} else {
			MainConsoleScheduler consoleScheduler = new MainConsoleScheduler(config);
			consoleScheduler.launchEngine();
			//
			MainDisplayScheduler.win = new SWTEngineWindow(shell, consoleScheduler.ioEngine);
			win.setBlockOnOpen(true);
			//
			//
			final TrayItem item = new TrayItem(tray, SWT.NONE);
		    item.setToolTipText("IOF Agent");

			item.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					if ((win.getShell() != null) && !win.getShell().isDisposed()) {
						System.out.println(win.getShell().isVisible());
					}
					win.open();
				}
			});
			item.addListener(SWT.DefaultSelection, new Listener() {
				public void handleEvent(Event event) {
					if ((win.getShell() != null) && !win.getShell().isDisposed()) {
						System.out.println(win.getShell().isVisible());
					}
					win.open();
				}
			});
			final Menu menu = new Menu(shell, SWT.POP_UP);
			MenuItem mConsole = new MenuItem(menu, SWT.PUSH);
			mConsole.setText("Console");
			mConsole.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					if ((win.getShell() != null) && !win.getShell().isDisposed()) {
						System.out.println(win.getShell().isVisible());
					}
					win.open();
				}
			});
			MenuItem mQuit = new MenuItem(menu, SWT.PUSH);
			mQuit.setText("Exit");
			mQuit.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					if ((win.getShell() != null) && !win.getShell().isDisposed()) {
						win.close(); // also disposes
					}
					shell.dispose();
				}
			});
			item.addListener(SWT.MenuDetect, new Listener() {
				public void handleEvent(Event event) {
					menu.setVisible(true);
				}
			});
			item.setImage(image);
			//
							
		    while (!shell.isDisposed()) {
		      if (!display.readAndDispatch())
		        display.sleep();
		    }
		    
		    
		    consoleScheduler.shutdownEngine();
		    try {
				consoleScheduler.ioEngine.join();
			} catch (InterruptedException e) {
				logger.fatal("Could not wait for Engine shutdown...", e);
			}
			//
		    image.dispose();
		    display.dispose();				
			//

			}
			//

		logger.info("io/framework:IOEngineSWT shutting down daemon");
		System.exit(EExitCodes.OK.getValue());			
	}

}
