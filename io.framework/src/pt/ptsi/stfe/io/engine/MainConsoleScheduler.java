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


import static pt.ptsi.stfe.io.engine.jobs.JobResult.EExitCodes;

/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class MainConsoleScheduler {

	static Logger logger = Logger.getLogger(IOEngine.class);
	
	IOEngine ioEngine = null;
	
	final Properties config;
	
	/**
	 * 
	 * 
	 * @param props
	 * @param console is console or client graphic interface
	 */
	public MainConsoleScheduler(Properties props) {
		this.config = props;
	}
	
	/**
	 * 
	 */
	void launchEngine() {

		ioEngine = new IOEngine();
		ioEngine.init(this.config);
		//
		ioEngine.start();
		
	}
	
	/**
	 * 
	 */
	void shutdownEngine() {
		try {
			ioEngine.engineQuit = true;
		} catch (Exception e) {
			logger.fatal("io/framework:IOEngine couldn't shutdown gracefully", e);
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("io/framework:IOEngine start daemon");
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
		try {
			Properties config = new Properties();
			readerConfig = new FileReader(configFile);
			config.load(readerConfig);
			//
			//
			MainConsoleScheduler consoleScheduler = new MainConsoleScheduler(config);
			consoleScheduler.launchEngine();
			//
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
		logger.info("io/framework:IOEngine shutting down daemon");
		System.exit(EExitCodes.OK.getValue());		
	}

}
