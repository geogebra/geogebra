package org.geogebra.web.html5.util.debug;

import java.util.Date;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.util.AppletParameters;

import com.google.gwt.user.client.Window;

/**
 * GeoGebraLogger implementation for the web platform
 * 
 * @author Zoltan Kovacs
 */
public class LoggerW extends Log {

	/**
	 * Constructor
	 */
	public LoggerW() {
		// needed for IE9
		initConsole();
	}

	/**
	 * Make sure $wnd.console exists (eg for IE9)
	 * 
	 * http://stackoverflow.com/questions/5472938/does-ie9-support-console-log-
	 * and-is-it-a-real-function
	 */
	public static native void initConsole() /*-{
		if (!$wnd.console) {
			$wnd.console = {};
		}
		if (!$wnd.console.log) {
			$wnd.console.log = function() {
			};
		}
	}-*/;

	@Override
	protected String getTimeInfoImpl() {
		Date date = new Date();
		return date.getHours() + ":" + date.getMinutes() + ":"
				+ date.getSeconds();
	}

	@Override
	protected void print(String logEntry, Level level) {
		if (getLogDestination() == LogDestination.FILE) {
			setLogDestination(LogDestination.CONSOLE);
			log(WARN,
					"FILE logging is not supported in web, falling back to use CONSOLES instead",
			        1);
			print(logEntry, level);
			return;
		}
		if (getLogDestination() == LogDestination.CONSOLE) {
			// don't change this to Application.debug!!
			if (level == ERROR) {
				printWebConsoleError(logEntry);
			} else {
				printWebConsole(logEntry);
			}
			return;
		}
	}

	private native void printWebConsole(String s) /*-{
		$wnd.console.log(s);
	}-*/;

	private native static void printWebConsoleError(String s) /*-{
		$wnd.console.error(s);
	}-*/;

	/**
	 * Prints a log message if the logLevel is set to <= level and stores those
	 * classes which have no implementation (simply checks if the message starts
	 * with "implementation needed")
	 * 
	 * @param level
	 *            logging level
	 * @param message
	 *            the log message
	 */
	@Override
	public void log(Level level, String message, int depth) {
		if (Browser.isFirefox()) {
			super.log(level, "\n" + message, depth);
		} else {
			super.log(level, message, depth);
		}
	}

	/**
	 * Start logger if parameters allow it.
	 * 
	 * @param article
	 *            parameters
	 */
	public static void startLogger(AppletParameters article) {
		if (article.getDataParamShowLogging()) {
			Log.setLogger(new LoggerW());
			Log.setLogDestination(LogDestination.CONSOLE);
			Log.setLogLevel(Window.Location.getParameter("logLevel"));
		} else {
			// make sure $wnd.console works in IE9
			LoggerW.initConsole();
		}
	}

	@Override
	public native void doPrintStacktrace(String message)/*-{
		if ($wnd.console && $wnd.console.trace) {
			$wnd.console.trace(message);
		}
	}-*/;

	@Override
	protected native void doPrintStacktrace(Throwable t)/*-{
		if ($wnd.console && $wnd.console.trace) {
			$wnd.console.log(t["backingJsObject_1_g$"] || t);
		}
	}-*/;

	public static void loaded(String string) {
		debug("Loaded: " + string);
	}
}
