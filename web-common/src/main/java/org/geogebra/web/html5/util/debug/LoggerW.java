package org.geogebra.web.html5.util.debug;

import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.util.AppletParameters;

import elemental2.dom.Console;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

/**
 * GeoGebraLogger implementation for the web platform
 * 
 * @author Zoltan Kovacs
 */
public class LoggerW extends Log {

	@Override
	public void print(Level level, Object logEntry) {
		if (logEntry instanceof Throwable) {
			printErrorMessage((Throwable) logEntry);
			return;
		}

		Console console = DomGlobal.console;

		switch (level) {
		case INFO:
			if (Browser.hasProperty(console, "info")) {
				DomGlobal.console.info(logEntry);
			}
			break;
		case WARN:
			if (Browser.hasProperty(console, "warn")) {
				DomGlobal.console.warn(logEntry);
			}
			break;
		case ERROR:
			if (Browser.hasProperty(console, "error")) {
				DomGlobal.console.error(logEntry);
			}
			break;
		case TRACE:
			if (Browser.hasProperty(console, "trace")) {
				DomGlobal.console.trace(logEntry);
			}
			break;
		default:
		case DEBUG:
			if (Browser.hasProperty(console, "log")) {
				DomGlobal.console.log(logEntry);
			}
			break;
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
			Log.setLogLevel(NavigatorUtil.getUrlParameter("logLevel"));
		}
	}

	/**
	 * @param thrown exception
	 */
	public static void printErrorMessage(Object thrown) {
		// This contains the stacktrace in gwt dev mode.
		Object backingJsObject = Js.asPropertyMap(thrown).nestedGet("backingJsObject.stack");
		if (Browser.hasProperty(DomGlobal.console, "error")) {
			if (Js.isTruthy(backingJsObject)) {
				DomGlobal.console.error(backingJsObject);
			} else {
				DomGlobal.console.error(thrown);
			}
		}
	}

	public static void loaded(String string) {
		Log.debug("Loaded: " + string);
	}
}
