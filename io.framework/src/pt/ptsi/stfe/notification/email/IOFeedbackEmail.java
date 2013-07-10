/**
 * 
 */
package pt.ptsi.stfe.notification.email;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import pt.ptsi.stfe.io.engine.IODomain.EmailInfo;
import pt.ptsi.stfe.io.engine.IODomain.RemoteInfo;
import pt.ptsi.stfe.io.engine.ServiceKey;
import pt.ptsi.stfe.io.engine.jobs.JobKey;
import pt.ptsi.stfe.io.engine.jobs.JobResult;
import sun.util.locale.StringTokenIterator;

/**
 * @author XPTS912
 *
 */
public class IOFeedbackEmail {
	
	static Logger log = Logger.getLogger(IOFeedbackEmail.class);
	
	private static final Path templatePath = Paths.get("resources/email/iof_report.html");
	private final StringBuilder subject;
	private final ServiceKey serviceKey;
	
	private URL feedbackImage = null;

	/**
	 * 
	 */
	public IOFeedbackEmail(ServiceKey key) {
		this.serviceKey = key;
		this.subject = new StringBuilder("[IOF] ")
			.append(key.getDomain().getDomainName()).append(" -> ")
			.append(key.getMachineName().toUpperCase()).append(" -> ")
			.append(key.getServiceName());
	}
	
	/**
	 * 
	 * @param result
	 * @return
	 * @throws Exception
	 */
	protected String prepareBodyTemplate(JobResult result) throws Exception {
		StringBuilder sText = new StringBuilder();
		String htmlText = null;
		try (BufferedReader readerTemplate = new BufferedReader(new FileReader(templatePath.toFile()))) {
			String line = null;
			while ((line = readerTemplate.readLine()) != null) {
				sText.append(line);
			}
			htmlText = sText.toString();
			//
			Velocity.init();
			VelocityContext vContext = new VelocityContext();
			switch (result.getExitCode()) {
				case ERROR:
				case FATAL:
					vContext.put("feedbackColor", "#FF0000");
					this.feedbackImage = new File("resources/Error-icon.png").toURI().toURL();
					vContext.put("feedbackImage", "");
					break;
				case OK:
					vContext.put("feedbackColor", "#00CC00");
					this.feedbackImage = new File("resources/Accept-icon.png").toURI().toURL();
					break;
				case WARNING:
				case NOACTION:
					break;
				default:
					vContext.put("feedbackColor", "#FFFF99");
					this.feedbackImage = new File("resources/Accept-icon.png").toURI().toURL();
					break;
			}
			vContext.put("domain", this.serviceKey.getDomain().getDomainName());
			vContext.put("machineName", this.serviceKey.getMachineName());
			vContext.put("serviceName", this.serviceKey.getServiceName());
			vContext.put("completeFilePath", this.serviceKey.getCompleteFilepath());
			vContext.put("shortMessage", result.getMessage());
			StringBuilder body = new StringBuilder();
			List<JobResult> results = result.getResults();
			for (JobResult r : results) {
				body.append("<strong>").append(r.getExitCode().name() ).append("</strong>").append("&nbsp;&nbsp;").append( ((JobKey)r.getKey()).getJobName() ).append("&nbsp;&nbsp;-&nbsp;&nbsp;").append(r.getMessage());
				body.append("<br/>");
				// print detailed messages for each result
				String details [] = r.getDetails().split("\\r?\\n");
				for (String d : details) {
					body.append("<font size='-1' color='#555555'><i>").append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").
						append( d ).append("</i></font>");
					body.append("<br/>");
				}
			}
			vContext.put("text", body.toString());
			//
			StringWriter writer = new StringWriter();
			Velocity.evaluate(vContext, writer, "feedbackEmail", htmlText);
			htmlText = writer.getBuffer().toString();
		}
		
		return htmlText;
	}

	
	/**
	 * 
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public int sendFeedback(JobResult result) throws Exception {
		//
		RemoteInfo remoteInfo = serviceKey.getDomain().getRemoteInformation();
		if (remoteInfo != null) {
			EmailInfo emailInfo = remoteInfo.getEmailInfo();
			/*
			Hashtable<String, String> env = new Hashtable<>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, remoteInfo.getContextJndiFactory());
			env.put(Context.PROVIDER_URL, remoteInfo.getContextServer());
			env.put(Context.SECURITY_PRINCIPAL, remoteInfo.getContextUser());
			env.put(Context.SECURITY_CREDENTIALS, remoteInfo.getContextPassword());
			InitialContext ic = new InitialContext(env);
			Session session = (Session) ic.lookup(emailInfo.getJndiObject());
			*/
			// define message:
			Properties props = new Properties();
			props.put("mail.smtp.host", emailInfo.getSmtpServer());
			Session session = Session.getInstance(props);
			//
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailInfo.getSender(), emailInfo.getSenderDescription()));
			message.setReplyTo(new Address [] {new InternetAddress(emailInfo.getReplyTo()) } );
			message.addRecipients(Message.RecipientType.TO, emailInfo.getTo());
			message.addRecipients(Message.RecipientType.CC, emailInfo.getCc());
			//message.addRecipients(Message.RecipientType.BCC, emailInfo.getBcc());
			message.setSubject(subject.toString());
			// Create a related multi-part to combine the parts
			BodyPart msgBodyPart = new MimeBodyPart();
			String html = prepareBodyTemplate(result);
			//
			msgBodyPart.setContent(html, "text/html");
			MimeMultipart multipart = new MimeMultipart("related");
			multipart.addBodyPart(msgBodyPart);
			//
			msgBodyPart = new MimeBodyPart();
			URL serverIcon = new File("resources/server-icon.png").toURI().toURL();
			DataSource uds = new URLDataSource(serverIcon);
			msgBodyPart.setDataHandler(new DataHandler(uds));
			msgBodyPart.setHeader("Content-ID", "<servericon>");
			multipart.addBodyPart(msgBodyPart);
			//
			msgBodyPart = new MimeBodyPart();
			uds = new URLDataSource(this.feedbackImage);
			msgBodyPart.setDataHandler(new DataHandler(uds));
			msgBodyPart.setHeader("Content-ID", "<feedbackImage>");
			multipart.addBodyPart(msgBodyPart);
			//
			msgBodyPart = new MimeBodyPart();
			URL logo = new File("resources/logo_ptsi.gif").toURI().toURL();
			uds = new URLDataSource(logo);
			msgBodyPart.setDataHandler(new DataHandler(uds));
			msgBodyPart.setHeader("Content-ID", "<ptsilogo>");
			multipart.addBodyPart(msgBodyPart);
			// 
			message.setContent(multipart);
			//
			Transport.send(message);
			log.info("Email sent for service " + serviceKey + " feedback.");
			//
		} else {
			log.error("No email info on Domain " + serviceKey.getDomain().getDomainName() + " to be able to send emails!");
		}
		
		//
		
		return 0;
	}
	


}
