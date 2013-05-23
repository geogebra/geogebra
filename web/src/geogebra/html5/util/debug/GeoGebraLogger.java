package geogebra.html5.util.debug;

import geogebra.html5.Browser;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * GeoGebraLogger implementation for the web platform
 * @author Zoltan Kovacs <zoltan@geogebra.org>

 */
public class GeoGebraLogger extends geogebra.common.util.debug.GeoGebraLogger {

	/**
	 * Constructor
	 */
	public GeoGebraLogger() {
		
		// needed for IE9
		initConsole();
	}
	
	/**
	 * Make sure $wnd.console exists (eg for IE9) 
	 * 
	 * http://stackoverflow.com/questions/5472938/does-ie9-support-console-log-and-is-it-a-real-function
	 */
	public static native void initConsole() /*-{
		
	if (!$wnd.console) {
		$wnd.console = {};
	}
	if (!$wnd.console.log) {
		$wnd.console.log = function () { };
	}
	
}-*/;

	@Override
	protected String getTimeInfo() {
		Date date = new Date();
		return DateTimeFormat.getFormat("HH:mm:ss.SSS").format(date);
	}

	@Override
	public void setLogFile(String logFileName) {
		log(WARN, "FILE logging is not supported in web, falling back to use CONSOLES instead");
		setLogDestination(LogDestination.CONSOLES);
	}

	@Override
	protected void print(String logEntry, Level level) {
		if (getLogDestination() == LogDestination.WEB_CONSOLE ||
				getLogDestination() == LogDestination.CONSOLES) {
			printWebConsole(logEntry);
		}
		if (getLogDestination() == LogDestination.FILE) {
			setLogDestination(LogDestination.CONSOLES);
			log(WARN, "FILE logging is not supported in desktop, falling back to use CONSOLES instead");
			print (logEntry, level);
			return;
		}
		if (getLogDestination() == LogDestination.CONSOLE ||
				getLogDestination() == LogDestination.CONSOLES) {
			// don't change this to Application.debug!!
			GWT.log(logEntry);
			return;
		}
	}

	private native void printWebConsole(String s) /*-{
		$wnd.console.log(s);
		}-*/;


	/**
	 * Prints a log message if the logLevel is set to <= level
	 * and stores those classes which have no implementation
	 * (simply checks if the message starts with "implementation needed")
	 * @param level logging level
	 * @param message the log message
	 */
	@Override
	public void log(Level level, String message) {
		if (Browser.isFirefox())
			super.log(level, "\n"+message);
		else
			super.log(level, message);
	}
	
	
	}
