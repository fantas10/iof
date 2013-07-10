/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2012/04/19
 */
package pt.ptsi.stfe.io.engine;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.JDOMFactory;
import org.jdom2.SlimJDOMFactory;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.DefaultSAXHandlerFactory;
import org.jdom2.input.sax.SAXHandlerFactory;
import org.jdom2.input.sax.XMLReaderSAX2Factory;

import pt.ptsi.stfe.io.engine.IODomain.EmailInfo;
import pt.ptsi.stfe.io.engine.IODomain.LocalInfo;
import pt.ptsi.stfe.io.engine.IODomain.RemoteInfo;


/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 */
public class DomainMonitor extends Observable implements FileAlterationListener {

	private final Map<DomainKey, IODomain> domainMap;
	
	static Logger logger = Logger.getLogger(DomainMonitor.class);
	
	/**
	 * 
	 */
	public DomainMonitor() {
		this.domainMap = new HashMap<DomainKey, IODomain>();
	}
	
	/**
	 * 
	 * @param domainFile
	 * @return
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	private IODomain parseDomain(File domainFile) throws JDOMException, IOException {
		IODomain domain = null;
		//
		// TODO Check XML against a Schema - change below to true
		XMLReaderSAX2Factory factory = new XMLReaderSAX2Factory(false);
		SAXHandlerFactory saxHandlerFactory = new DefaultSAXHandlerFactory();
		JDOMFactory jdomFactory = new SlimJDOMFactory();
		//
		SAXBuilder parser = new SAXBuilder(factory, saxHandlerFactory, jdomFactory);
		Document document = parser.build(domainFile);
		Element domainElement = document.getRootElement();
		final String domainName = domainElement.getAttributeValue("name");
		String domainPrefix = domainElement.getAttributeValue("prefix");
		domain = new IODomain(domainName, domainFile);
		// 
		// Local info
		Element localElement = domainElement.getChild("local");
		if (localElement != null) {
			LocalInfo localInformation = new LocalInfo();
			localInformation.setClusterAddress(localElement.getChildText("clusterAddress"));
			domain.setLocalInformation(localInformation);
		}
		
		// Server info
		Element remoteElement = domainElement.getChild("remote");
		if (remoteElement != null) {
			RemoteInfo remoteInformation = new RemoteInfo();
			//
			Element serverElement = remoteElement.getChild("server");
			// remote is optional, but when having remote should have 'server' and 'context'
			if (serverElement != null) {
				remoteInformation.setServerName(serverElement.getAttributeValue("name"));
				Element contextElement = serverElement.getChild("context");
				remoteInformation.setContextServer(contextElement.getAttributeValue("server"));
				remoteInformation.setContextUser(contextElement.getAttributeValue("user"));
				remoteInformation.setContextPassword(contextElement.getAttributeValue("password"));
				remoteInformation.setContextJndiFactory(contextElement.getAttributeValue("jndiFactory"));
				//
			} else {
				throw new IOException("REMOTE element present, should haver SERVER and CONTEXT");
			}
			Element emailElement = remoteElement.getChild("email");
			// email is optional
			if (emailElement != null) {
				EmailInfo emailInformation = new EmailInfo();
				emailInformation.setSmtpServer(emailElement.getChildTextTrim("mailserver"));
				Element senderElement = emailElement.getChild("sender");
				emailInformation.setSender(senderElement.getTextTrim());
				emailInformation.setSenderDescription(senderElement.getAttributeValue("description"));
				emailInformation.setReplyTo(emailElement.getChildTextTrim("replyTo"));
				emailInformation.setTo(emailElement.getChildTextTrim("to"));
				emailInformation.setCc(emailElement.getChildTextTrim("cc"));
				remoteInformation.setEmailInfo(emailInformation);
			}
			domain.setRemoteInformation(remoteInformation);
		}
		//
		return domain;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onStart(org.apache.commons.io.monitor.FileAlterationObserver)
	 */
	@Override
	public void onStart(FileAlterationObserver observer) {
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onDirectoryCreate(java.io.File)
	 */
	@Override
	public void onDirectoryCreate(File directory) {
		// TODO Auto-generated method stub
		logger.warn("No Implementation for New Directory Created " + directory.getAbsolutePath());
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onDirectoryChange(java.io.File)
	 */
	@Override
	public void onDirectoryChange(File directory) {
		// TODO Auto-generated method stub
		logger.warn("No Implementation for Directory Change " + directory.getAbsolutePath());
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onDirectoryDelete(java.io.File)
	 */
	@Override
	public void onDirectoryDelete(File directory) {
		// TODO Auto-generated method stub
		logger.warn("No Implementation for Directory Delete!! " + directory.getAbsolutePath());
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onFileCreate(java.io.File)
	 */
	@Override
	public void onFileCreate(File file) {
		IODomain newDomain = null;
		try {
			newDomain = parseDomain(file);
			this.domainMap.put(newDomain.getDomainName(), newDomain);
			logger.info("Domain " + newDomain.getDomainName().getKey() + " was created @ " + new Date());
		} catch (IOException | JDOMException ioe) {
			logger.error("Error parsing new domain ("+file.getName()+") configuration " + ioe.getMessage(), ioe);
		}

	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onFileChange(java.io.File)
	 */
	@Override
	public void onFileChange(File file) {
		IODomain domain, newDomain = null;
		Iterator<IODomain> domainIterator = domainMap.values().iterator();
		File f = null;
		while (domainIterator.hasNext()) {
			domain = domainIterator.next();
			f = domain.getDomainFile();
			if (f.exists() && f.isFile() && f.equals(file)) { //must be same file, since it's a change
				try {
					newDomain = parseDomain(f);
					domainMap.put(newDomain.getDomainName(), newDomain);
					logger.info("Domain " + newDomain.getDomainName().getKey() + " was updated @ " + new Date());		
					//
					break; // break loop (domain updated!)
					//
				} catch (IOException | JDOMException ioe) {
					logger.error("Error parsing new domain ("+domain.getDomainName().getKey()+") configuration " + ioe.getMessage(), ioe);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onFileDelete(java.io.File)
	 */
	@Override
	public void onFileDelete(File file) {
		IODomain domain = null;
		Iterator<IODomain> domainIterator = domainMap.values().iterator();
		while (domainIterator.hasNext()) {
			domain = domainIterator.next();
			if (domain.getDomainFile().equals(file) && !file.isFile()) {
				domainIterator.remove();
				logger.warn("Domain " + domain.getDomainName().getKey() + " was removed @ " + new Date());	
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.io.monitor.FileAlterationListener#onStop(org.apache.commons.io.monitor.FileAlterationObserver)
	 */
	@Override
	public void onStop(FileAlterationObserver observer) {
	}

	/**
	 * @return the domainMap
	 */
	public Map<DomainKey, IODomain> getDomainMap() {
		return domainMap;
	}

	
}
