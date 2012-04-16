package geogebra.common.util;

import java.util.Set;
import java.util.TreeSet;

/**
 * Common logging class
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */

public abstract class GeoGebraLogger {
	
	/**
	 * Logging level
	 */
	public class Level {
		/**
		 * Log level priority 
		 */
		int priority;
		/**
		 * Category text
		 */
		String text;
		
		/**
		 * Creates a logging level 
		 * @param priority Log level priority
		 * @param text Category text
		 */
		Level(int priority, String text) {
			this.priority = priority;
			this.text = text;
		}

		/**
		 * Message priority, the lower the more problematic.
		 * @return the priority
		 */
		public int getPriority() {
			return priority;
		}
	}

	private static Set<String> reportedImplementationNeeded = new TreeSet<String>();
	
	public Level EMERGENCY = new Level(0, "EMERGENCY");
	public Level ALERT = new Level(1, "ALERT");
	public Level CRITICAL = new Level(2, "CRITICAL");
	public Level ERROR = new Level(3, "ERROR");
	public Level WARN = new Level(4, "WARN");
	public Level NOTICE = new Level(5, "NOTICE");
	public Level INFO = new Level(7, "INFO");
	public Level DEBUG = new Level(8, "DEBUG");
	public Level TRACE = new Level(9, "TRACE"); 
	/**
	 * Logging destinations
	 */
	public enum LogDestination {/**
	 * Send logging to file. A file name must also be set by using the setLogFile() method.
	 */
	FILE, /**
	 * Sends logging to console. Messages <= ERROR will be written to STDERR, others to STDOUT
	 * in desktop mode; sends log messages via GWT.log to the Eclipse console in web
	 * development mode. 
	 */
	CONSOLE, /**
	 * Sends logging to the web console (available by pressing CTRL-SHIFT-J in Google Chrome,
	 * or CTRL-SHIFT-K in Firefox) in web development or production mode. Not available
	 * in desktop mode.
	 */
	WEB_CONSOLE, /**
	 * Sends logging to CONSOLE and WEB_CONSOLE as well (if available).
	 */
	CONSOLES}
	
	private Level logLevel = DEBUG; // default
	private LogDestination logDestination = LogDestination.CONSOLES; // default;
	private boolean timeShown = true; // default
	private boolean callerShown = true; // default
	
	/**
	 * Sets the current logging level
	 * @param logLevel the logging level to set
	 */
	public void setLogLevel(Level logLevel) {
		this.logLevel = logLevel;
	}
	
	/**
	 * Returns the current logging level
	 * @return the current level
	 */
	public Level getLogLevel() {
		return logLevel;
	}

	/**
	 * Sets the logger destination (FILE, CONSOLE, WEB_CONSOLE, CONSOLES)
	 * @param logDestination the destination
	 */
	public void setLogDestination(LogDestination logDestination) {
		this.logDestination = logDestination;
	}

	/**
	 * Returns the logger destination (FILE, CONSOLE, WEB_CONSOLE, CONSOLES)
	 * @return the destination
	 */
	public LogDestination getLogDestination() {
		return logDestination;
	}

	/**
	 * Reports if the timestamp is printed for logging
	 * @return if the names are printed
	 */
	public boolean isTimeShown() {
		return timeShown;
	}

	/**
	 * Sets if the report time of the log message should be printed for logging.
	 * May not be available for all platforms (returns empty string when not). 
	 * @param timeShown if the timestamp should be printed
	 */
	public void setTimeShown(boolean timeShown) {
		this.timeShown = timeShown;
	}

	/**
	 * Reports if the caller class and method names are printed for logging
	 * @return if the names are printed
	 */
	public boolean isCallerShown() {
		return callerShown;
	}

	/**
	 * Sets if the caller class and method names should be printed for logging 
	 * @param callerShown if the names should be printed
	 */
	public void setCallerShown(boolean callerShown) {
		this.callerShown = callerShown;
	}

	/**
	 * Prints a log message if the logLevel is set to <= level
	 * and stores those classes which have no implementation
	 * (simply checks if the message starts with "implementation needed")
	 * @param level logging level
	 * @param message the log message
	 */
	public void log(Level level, String message) {
		if (logLevel.getPriority() >= level.getPriority()) {
			String caller = "";
			if (callerShown) {
				caller = getCaller();
				if (message.length() >= 21) {
					if (message.toLowerCase().substring(0, 21)
						.equals("implementation needed")) {
							if (!reportedImplementationNeeded.contains(caller))
								reportedImplementationNeeded.add(caller);
							}
					}
				caller += ": ";
				}
			String logEntry = getTimeInfo() + level.text + ": " + caller + message;
			print(logEntry, level);
		}
	}
	
	/**
	 * Prints the log entry, which is usually the full message with timestamp,
	 * the logging level and the caller class
	 * @param logEntry the full log entry
	 * @param level logging level
	 */
	protected abstract void print(String logEntry, Level level);
	
	/**
	 * Sets the log file name (if FILE logging is available)
	 * @param logFileName the name of the log file
	 */
	public void setLogFile(String logFileName) {
		// Implementation overrides this in some applications.
	}
	
	/**
	 * Returns the current time in human readable format (for debugging),
	 * appended by a space character if non-empty
	 * @return the timestamp
	 */
	protected String getTimeInfo() {
		return "";
		// Implementation overrides this in some applications.  
	}
	
	/**
	 * Returns some memory related information (for debugging)
	 * @return the memory info text
	 */
	public String getMemoryInfo() {
		return "";
		// Implementation overrides this in some applications.		
	}
	
	/**
	 * Returns the caller class and method names 
	 * @return the full Java class and method name
	 */
	public String getCaller() {
		String callerMethodName = null;
		String callerClassName = null;
		try {
			Throwable t = new Throwable();
			StackTraceElement[] elements = t.getStackTrace();

			// String calleeMethod = elements[0].getMethodName();
			callerMethodName = elements[3].getMethodName();
			callerClassName = elements[3].getClassName();
			} catch (Throwable t) {
				// do nothing here; we are probably running Web in Opera
				return "?";
		}
		return callerClassName + "." + callerMethodName;
	}
}
