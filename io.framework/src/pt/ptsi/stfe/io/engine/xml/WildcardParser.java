/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/04/12
 */
package pt.ptsi.stfe.io.engine.xml;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Wildcard is a <code>*{<i>expression</i>}</code> 
 * 
 * <p>
 * 	Expressions:<br>
 * <li>Date expressions using a {@link SimpleDateFormat} type expression
 * <ul>ex: *{Date.MM}</ul></li> 
 * <li>{@link File} names or substring of {@link File} names
 * <ul>
 * 		<u>examples: </u><br>
 * 			*{File} - complete file name<br>
 * 			*{File[0,5]} - substring from 0 to 5 cols in file name<br>
 * 			*{File[0,5]}*{File[8,10]} - substring from 0 to 5 cols and from 8 to 10 cols in file name<br>
 * 			*{File[3]} - substring from 3 cols to end</ul>
 * </li>
 * </p>
 * 
 * <br/>
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class WildcardParser {
	
	final static String WC_REGEX = "(\\*\\{Date.\\w+\\})|(\\*\\{File\\})|(\\*\\{File(\\[\\d+,\\d+\\]|\\[\\d+\\])*\\})";
	
	private static Logger log = Logger.getRootLogger();
	
	/**
	 * 
	 */
	private WildcardParser() {
	}
	
	/**
	 * 
	 * @param rawWildcard
	 * @param name file name (optional)
	 * @return solution - expression with result replaced from wildcard
	 */
	static final String replaceWildcard(String rawWildcard, String ...source) throws ParseException {
		// strip wildcard reserved chars - *{<expr>}
		String wildcard = rawWildcard.substring(2, rawWildcard.length()-1);
		String wType = wildcard.substring(0, 4);
		String wVariant = wildcard.substring(4);
		//
		String solution = wildcard;
		//
		String error = null;
		switch (wType) {
			case "Date":
				if (wVariant.length() < 2) {
					error = "ERROR in Wildcard DATE Format ("+wVariant+") expression is too short...";
					log.error(error);
					throw new ParseException(error, 0);
				} else {
					Date now = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat(wVariant.substring(1));
					solution = sdf.format(now);
				}
				break;
			case "File":
				try {
					if (wVariant.length() == 0) { 
						// Fullname
						solution = source[0];
					} else if (wVariant.contains(",")) { 
						// isInterval
						String []interval = wVariant.substring(1, wVariant.length()-1).split(",");
						solution = source[0].substring(Integer.parseInt(interval[0]), Integer.parseInt(interval[1]));						
					} else { 
						// isStartPoint
						int start = Integer.parseInt(wVariant.substring(1, wVariant.length()-1));
						solution = source[0].substring(start);
					}
				} catch (Exception e) {
					error = "ERROR in Wildcard FILE Format ("+wVariant+") " + e.getMessage();
					log.error(error);
					throw new ParseException(error, 0);
				}
				break;
			default:
				// should not get here...
				error = "WildcardParser: Unknown Wildcard";
				log.error(error);
				throw new ParseException(error, 0);
		}
		//
		return solution;
	}
	
	/**
	 * Parses expression with wildcards and returns the solution (the resolved expression)<br>
	 * If no wildcards present, just returns the input expression
	 * 
	 * @param expression
	 * @param filename
	 * @return full solution
	 * @throws ParseException
	 */
	public static String parse(String expression, String ...sourceName) throws ParseException {
		//
		Pattern p = Pattern.compile(WC_REGEX);
		Matcher m = p.matcher(expression);
		//
		String solution = null;
		while (m.find()) {
			solution = replaceWildcard(m.group(), sourceName);
			expression = expression.replace(m.group(), solution);
		}
		//
		return expression;
	}

}
