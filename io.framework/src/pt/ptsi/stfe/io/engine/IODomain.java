/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/01/20
 */
package pt.ptsi.stfe.io.engine;

import java.io.File;
import java.util.StringTokenizer;

import sun.util.locale.StringTokenIterator;


/**
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class IODomain {

	private final DomainKey domainName;
	
	private LocalInfo localInformation = null;
	private RemoteInfo remoteInformation = null;
	private final File domainFile;
	
	public static final IODomain getDefaultDomain() {
		return new IODomain("default", null);
	}
	
	/**
	 * 
	 */
	public IODomain(final String domainName, File domainFile) {
		this.domainFile = domainFile;
		this.domainName = new DomainKey(domainName);
	}

	/**
	 * @return the domainName
	 */
	public DomainKey getDomainName() {
		return domainName;
	}
	
	
	/**
	 * @return the domainFile
	 */
	public File getDomainFile() {
		return domainFile;
	}

	/**
	 * @return the localInformation
	 */
	public LocalInfo getLocalInformation() {
		return localInformation;
	}

	/**
	 * @param localInformation the localInformation to set
	 */
	public void setLocalInformation(LocalInfo localInformation) {
		this.localInformation = localInformation;
	}

	/**
	 * @return the remoteInformation
	 */
	public RemoteInfo getRemoteInformation() {
		return remoteInformation;
	}

	/**
	 * @param remoteInformation the remoteInformation to set
	 */
	public void setRemoteInformation(RemoteInfo remoteInformation) {
		this.remoteInformation = remoteInformation;
	}




	/**
	 * 
	 */
	public static class LocalInfo {
		
		private String clusterAddress = null;

		/**
		 * @return the clusterAddress
		 */
		public String getClusterAddress() {
			return clusterAddress;
		}

		/**
		 * @param clusterAddress the clusterAddress to set
		 */
		public void setClusterAddress(String clusterAddress) {
			this.clusterAddress = clusterAddress;
		}
	}
	
	/**
	 * 
	 */
	public static class RemoteInfo {
		private String serverName = null;
		private String contextServer = null;
		private String contextUser = null;
		private String contextPassword = null;
		private String contextJndiFactory = null;
		private EmailInfo emailInfo = null;
		/**
		 * @return the serverName
		 */
		public String getServerName() {
			return serverName;
		}
		/**
		 * @param serverName the serverName to set
		 */
		public void setServerName(String serverName) {
			this.serverName = serverName;
		}
		/**
		 * @return the contextServer
		 */
		public String getContextServer() {
			return contextServer;
		}
		/**
		 * @param contextServer the contextServer to set
		 */
		public void setContextServer(String contextServer) {
			this.contextServer = contextServer;
		}
		/**
		 * @return the contextUser
		 */
		public String getContextUser() {
			return contextUser;
		}
		/**
		 * @param contextUser the contextUser to set
		 */
		public void setContextUser(String contextUser) {
			this.contextUser = contextUser;
		}
		/**
		 * @return the contextPassword
		 */
		public String getContextPassword() {
			return contextPassword;
		}
		/**
		 * @param contextPassword the contextPassword to set
		 */
		public void setContextPassword(String contextPassword) {
			this.contextPassword = contextPassword;
		}
		/**
		 * @return the contextJndiFactory
		 */
		public String getContextJndiFactory() {
			return contextJndiFactory;
		}
		/**
		 * @param contextJndiFactory the contextJndiFactory to set
		 */
		public void setContextJndiFactory(String contextJndiFactory) {
			this.contextJndiFactory = contextJndiFactory;
		}
		/**
		 * @return the emailInfo
		 */
		public EmailInfo getEmailInfo() {
			return emailInfo;
		}
		/**
		 * @param emailInfo the emailInfo to set
		 */
		public void setEmailInfo(EmailInfo emailInfo) {
			this.emailInfo = emailInfo;
		}
		
	}
	
	/**
	 * 
	 */
	public static class EmailInfo {
		private String smtpServer = null;
		private String sender = null;
		private String senderDescription = null;
		private String replyTo = null;
		private String to = null;
		private String cc = null;
		private String bcc = null;

		
		
		/**
		 * @return the smtpServer
		 */
		public String getSmtpServer() {
			return smtpServer;
		}
		
		/**
		 * @param smtpServer the smtpServer to set
		 */
		public void setSmtpServer(String smtpServer) {
			this.smtpServer = smtpServer;
		}
		
		/**
		 * @return the sender
		 */
		public String getSender() {
			return sender;
		}
		/**
		 * @param sender the sender to set
		 */
		public void setSender(String sender) {
			this.sender = sender;
		}
		/**
		 * @return the senderDescription
		 */
		public String getSenderDescription() {
			return senderDescription;
		}
		/**
		 * @param senderDescription the senderDescription to set
		 */
		public void setSenderDescription(String senderDescription) {
			this.senderDescription = senderDescription;
		}
		/**
		 * @return the replyTo
		 */
		public String getReplyTo() {
			return replyTo;
		}
		
		/**
		 * @param replyTo the replyTo to set
		 */
		public void setReplyTo(String replyTo) {
			this.replyTo = replyTo;
		}
		
		/**
		 * @return the to
		 */
		public String getTo() {
			return to;
		}
	
		
		/**
		 * @param to the to to set
		 */
		public void setTo(String to) {
			this.to = to;
		}
		/**
		 * @return the cc
		 */
		public String getCc() {
			return cc;
		}
		/**
		 * @param cc the cc to set
		 */
		public void setCc(String cc) {
			this.cc = cc;
		}
		/**
		 * @return the bcc
		 */
		public String getBcc() {
			return bcc;
		}
		/**
		 * @param bcc the bcc to set
		 */
		public void setBcc(String bcc) {
			this.bcc = bcc;
		}
		
		
	}

}
