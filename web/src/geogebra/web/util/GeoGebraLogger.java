package geogebra.web.util;

import com.google.gwt.core.client.GWT;

/**
 * GeoGebraLogger implementation for the web platform
 * @author Zoltan Kovacs <zoltan@geogebra.org>

 */
public class GeoGebraLogger extends geogebra.common.util.GeoGebraLogger {

	/**
	 * Constructor
	 */
	public GeoGebraLogger() {
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
					GWT.log(logEntry);
					return;
					}
	}
	
	private native void printWebConsole(String s) /*-{
		$wnd.console.log(s);
		}-*/;
}
