/**
 * 
 */
package pt.ptsi.stfe.io.engine.xml;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.quartz.CronExpression;

import pt.ptsi.stfe.io.engine.PropertyMap;

/**
 * @author xpts912
 *
 */
public class IOXmlScheduling implements IOXmlConfig {
	
	public final static String START_AT_DATEFORMAT = "yyyy-MM-dd HH:mm:SS";

	public enum PROPERTIES implements E_IOXmlProperties { 
		
		//
		START_AT ("startAt"),  // used in both : optional : default = now()
		DATE ("date"),		   // used in both : optional : default = now()
		IMMEDIATE ("immediate"), // used in both : optional : value = now()
		// one of the following (CRON vs INTERVAL)
		CRON_EXPRESSION ("cronExpression"), // used in cron : mandatory for cron
		INTERVAL ("interval"),				// used in simple : mandatory for simple
		INTERVAL_IN ("in"),					// used in simple : mandatory for simple
		INTERVAL_VALUE ("value");			// used in simple : optional : default=1
		//
		
		private final String v;
		
		PROPERTIES(String value) { 	this.v = value; }

		public final String value() { return v; }
	}
	
	public enum  TimeType {
		HOURS, MINUTES, SECONDS;
	}
	
	/**
	 * 
	 */
	public IOXmlScheduling() {
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.xml.IOXmlConfig#parseConfiguration(org.jdom.Element)
	 */
	@Override
	public PropertyMap parseConfiguration(Element schElement) throws JDOMException, IOException, ClassNotFoundException,	InstantiationException, IllegalAccessException {
		
		PropertyMap xmlInfo = new PropertyMap();

		Element startAtElement = schElement.getChild(PROPERTIES.START_AT.value());
		if (startAtElement != null) { // optional
			String startAt = startAtElement.getAttributeValue(PROPERTIES.DATE.value());
			if (!PROPERTIES.IMMEDIATE.value().equals(startAt))  {
				SimpleDateFormat sdf = new SimpleDateFormat(START_AT_DATEFORMAT);
				try {
					xmlInfo.consume(PROPERTIES.DATE, sdf.parse(startAt));
				} catch (ParseException e) {
					e.printStackTrace();
					throw new IOException(e.getMessage(), e);
				}
			} // else we don't put anything since alternative is create a now() date!
		}
		// Check of Cron or interval
		Element cronElement = schElement.getChild(PROPERTIES.CRON_EXPRESSION.value());
		Element intervalElement = schElement.getChild(PROPERTIES.INTERVAL.value());
		if (cronElement != null && intervalElement == null) {
			if (!CronExpression.isValidExpression(cronElement.getValue())) {
				throw new IOException(PROPERTIES.CRON_EXPRESSION.value() + " is not a valid cron expression");
			} else {
				xmlInfo.consume(PROPERTIES.CRON_EXPRESSION, cronElement.getValue());	
			}
		} else if (cronElement == null && intervalElement != null) {
			String intervalIn = intervalElement.getAttributeValue(PROPERTIES.INTERVAL_IN.value());
			switch (intervalIn) {
				case "hours": 
					xmlInfo.consume(PROPERTIES.INTERVAL_IN, TimeType.HOURS);
					break;
				case "minutes":
					xmlInfo.consume(PROPERTIES.INTERVAL_IN, TimeType.MINUTES);
					break;
				case "seconds":
					xmlInfo.consume(PROPERTIES.INTERVAL_IN, TimeType.SECONDS);
					break;
			}
			xmlInfo.consume(PROPERTIES.INTERVAL_VALUE, intervalElement.getAttributeValue(PROPERTIES.INTERVAL_VALUE.value()));
		} else {
			throw new IOException("Incorrect values in scheduling: only one of 'cronExpression' or 'interval' should be present");
		}
		
		return xmlInfo;
	}

}
