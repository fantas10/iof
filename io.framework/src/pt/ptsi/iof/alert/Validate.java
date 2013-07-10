/**
 * 
 */
package pt.ptsi.iof.alert;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import pt.ptsi.stfe.io.engine.jobs.AbstractJob;

/**
 * @author eddie
 *
 */
public abstract class Validate extends AbstractJob {

	/**
	 * 
	 */
	public Validate() {
	}
	
	

	/**
	 * Validates quantity based on Regex values
	 * <br>
	 * <li>I		I, positive integer, exactly that value</li>
	 * <li>I+		I, positive integer, value or above</li>
	 * <li>I+n		I, positive integer, value or above n times</li>
	 * <li>I-		I, positive integer, value or below</li>
	 * <li>I-n		I, positive integer, value or below n times</li>
	 * <br>
	 * 
	 * @param countExpr
	 * @param fileCount
	 * @see Pattern
	 * @return
	 */
	public boolean validateCountCondition(String countExpr, int fileCount) {
		String pattern = "(\\d+)|(\\d+\\+|\\d+\\+\\d+)|(\\d+\\-|\\d+\\-\\d+)";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(countExpr);
		if (m.matches()) {
			System.out.println("Matched");
			String [] values = countExpr.split("\\-|\\+");
			int countVal = Integer.parseInt(values[0]);
			int delta = (values.length == 3) ? Integer.parseInt(values[2]) : 0;
			if (countExpr.contains("-")) {
				return (fileCount <= countVal) && (fileCount >= (countVal - delta )); 
			} else if (countExpr.contains("+")) {
				return (fileCount <= countVal + delta) && (fileCount >= (countVal)); 
			} else { // default
				return (Integer.parseInt(countExpr) == fileCount);
			}
		} else {
			throw new PatternSyntaxException("not parseable", pattern, 0);
		}
	}

}
