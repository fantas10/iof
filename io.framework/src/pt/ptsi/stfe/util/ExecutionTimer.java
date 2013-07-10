/**
 * 
 */
package pt.ptsi.stfe.util;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import pt.ptsi.stfe.io.engine.IOEngine;

/**
 * Class to time execution periods. Behaves like a chronometer with partial times or delta times
 * 
 * @author XPTS912
 */
public class ExecutionTimer {

	final static Locale ptLocale = new Locale("pt", "PT");
	public final static NumberFormat defaultFORMAT = NumberFormat.getNumberInstance(ptLocale);
	public final static DateFormat dateFORMAT = new SimpleDateFormat("HH:mm:ss SSS", ptLocale); 
	long start = 0l;
	long end = 0l;
	private List<Long> ticker = new ArrayList<Long>();
	
	/**
	 * 
	 */
	public ExecutionTimer() {
		dateFORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
		start = 0l;
		end = 0l;
		
	}
	
	/**
	 * Starts the timer. Must be invoked to start the timer otherwise all results will be compromised.
	 */
	public void start() {
		start = System.currentTimeMillis();
		ticker = new ArrayList<Long>();
		ticker.add(start);
	}
	
	/**
	 * Stops the timer
	 */
	public void stop() {
		end = System.currentTimeMillis();
		ticker.add(end);
	}
	
	/**
	 * Ticks a partial time returning the time between partial and start time
	 */
	public String partial() {
		long lastTick = ticker.get(ticker.size()-1);
		long now = System.currentTimeMillis();
		
		Date partial = new Date(now-lastTick);
		ticker.add(now);
		//
		return dateFORMAT.format(partial);
	}
	
	
	/**
	 * Ticks a partial time returning the time between partial and start time
	 * 
	 * @param pattern a <code>NumberFormat</code> Pattern
	 * @return
	 */
	public String partial(String pattern) {
		long lastTick = ticker.get(ticker.size()-1);
		DateFormat dateFormat = new SimpleDateFormat(pattern);
		long now = System.currentTimeMillis();
		
		Date partial = new Date(now-lastTick);
		ticker.add(now);
		//
		return dateFormat.format(partial);
	}
	
	/**
	 * Stops timer and return delta time interval from starting point to finish
	 * 
	 * @para pattern a <code>NumberFormat</code> Pattern
	 * @return 
	 */
	public String delta(String pattern) {
		stop();
		DateFormat dateFormat = new SimpleDateFormat(pattern);
		Date delta = new Date(end-start);
		//
		return dateFormat.format(delta);
	}
	
	/**
	 * Stops timer and return delta time interval from starting point to finish
	 * 
	 * @return
	 */
	public String delta() {
		stop();
		Date delta = new Date(end-start);
		//
		return dateFORMAT.format(delta);
	}
	 

	/**
	 * Returns the start instant
	 * 
	 * @return the start
	 */
	public String getStartTime() {
		// for TimeZone bug...
		DateFormat df = (DateFormat) dateFORMAT.clone();
		df.setTimeZone(TimeZone.getDefault());
		return df.format(new Date(start));
	}
	
	/**
	 * Returns the time between start and end time and all partials inbetween
	 * @return
	 */
	public long[] getTimes() {
		long[] times = new long[ticker.size()];
		int i = 0;
		for (Long tick : ticker) {
			times[i] = tick;
			i++;
		}
		return times;
	}
}
