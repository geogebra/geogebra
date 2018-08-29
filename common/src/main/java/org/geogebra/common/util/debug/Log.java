package org.geogebra.common.util.debug;

import java.util.Set;
import java.util.TreeSet;

//import org.geogebra.common.main.Feature;

/**
 * Common logging class
 * 
 * @author Zoltan Kovacs
 */

public abstract class Log {

	/** logger */
	private static volatile Log logger;
	private static Object lock = new Object();

	private static Set<String> reportedImplementationNeeded = new TreeSet<>();
	/** emergency */
	public final Level EMERGENCY = new Level(0, "EMERGENCY");
	/** alert */
	public final Level ALERT = new Level(1, "ALERT");
	/** critical */
	public final Level CRITICAL = new Level(2, "CRITICAL");
	/** error */
	public final Level ERROR = new Level(3, "ERROR");
	/** warning */
	public final Level WARN = new Level(4, "WARN");
	/** notice */
	public final Level NOTICE = new Level(5, "NOTICE");
	/** information */
	public final Level INFO = new Level(7, "INFO");
	/** debugging (default) */
	public final Level DEBUG = new Level(8, "DEBUG");
	/** trace */
	public final Level TRACE = new Level(9, "TRACE");
	/** in case keepLog = true, this sets max length of in-memory log */
	public static final int LOGFILE_MAXLENGTH = 10000;

	private final StringBuilder memoryLog = new StringBuilder();
	private Level logLevel = DEBUG; // default
	private LogDestination logDestination = LogDestination.CONSOLE; // default;
	private boolean timeShown = true; // default
	private boolean callerShown = true; // default
	private boolean levelShown = true; // default
	/** whether to keep log in memory */
	protected boolean keepLog = false;
	private boolean reading = false;

	/**
	 * Logging level
	 */
	public static class Level {
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
		 * 
		 * @param priority
		 *            Log level priority
		 * @param text
		 *            Category text
		 */
		Level(int priority, String text) {
			this.priority = priority;
			this.text = text;
		}

		/**
		 * Message priority, the lower the more problematic.
		 * 
		 * @return the priority
		 */
		public int getPriority() {
			return priority;
		}
	}

	/**
	 * The entire log since starting the application.
	 * 
	 * @return the entire log
	 */
	final public static StringBuilder getEntireLog() {
		return logger.getEntireLogImpl();
	}

	private StringBuilder getEntireLogImpl() {
		return memoryLog;
	}

	/**
	 * Logging destinations
	 */
	public enum LogDestination {
		/**
		 * Send logging to file. A file name must also be set by using the
		 * setLogFile() method.
		 */
		FILE,
		/**
		 * Sends logging to console. Messages <= ERROR will be written to
		 * STDERR, others to STDOUT in desktop mode; sends log messages via
		 * GWT.log to the Eclipse console in web development mode.
		 */
		CONSOLE
	}

	/**
	 * Sets the current logging level
	 * 
	 * @param logLevel
	 *            the logging level to set
	 */
	public void setLogLevel(Level logLevel) {
		this.logLevel = logLevel;
	}

	/**
	 * Sets the current logging level
	 * 
	 * @param logLevel
	 *            the logging level to set
	 */
	final public static void setLogLevel(String logLevel) {
		logger.setLogLevelImpl(logLevel);
	}

	private void setLogLevelImpl(String logLevel) {
		if (logLevel == null) {
			return;
		}
		if ("ALERT".equals(logLevel)) {
			this.logLevel = ALERT;
		}
		if ("EMERGENCY".equals(logLevel)) {
			this.logLevel = EMERGENCY;
		}
		if ("CRITICAL".equals(logLevel)) {
			this.logLevel = CRITICAL;
		}
		if ("ERROR".equals(logLevel)) {
			this.logLevel = ERROR;
		}
		if ("WARN".equals(logLevel)) {
			this.logLevel = WARN;
		}
		if ("INFO".equals(logLevel)) {
			this.logLevel = INFO;
		}
		if ("NOTICE".equals(logLevel)) {
			this.logLevel = NOTICE;
		}
		if ("DEBUG".equals(logLevel)) {
			this.logLevel = DEBUG;
		}
		if ("TRACE".equals(logLevel)) {
			this.logLevel = TRACE;
		}
	}

	/**
	 * Returns the current logging level
	 * 
	 * @return the current level
	 */
	public Level getLogLevel() {
		return logLevel;
	}

	/**
	 * Sets the logger destination (FILE, CONSOLE, WEB_CONSOLE, CONSOLES)
	 * 
	 * @param logDestination
	 *            the destination
	 */
	final public static void setLogDestination(LogDestination logDestination) {
		logger.setLogDestinationImpl(logDestination);
	}

	protected void setLogDestinationImpl(LogDestination logDestination) {
		this.logDestination = logDestination;
	}

	/**
	 * Returns the logger destination (FILE, CONSOLE, WEB_CONSOLE, CONSOLES)
	 * 
	 * @return the destination
	 */
	final public static LogDestination getLogDestination() {
		return logger.getLogDestinationImpl();
	}

	protected LogDestination getLogDestinationImpl() {
		return logDestination;
	}

	/**
	 * Reports if the timestamp is printed for logging
	 * 
	 * @return if the names are printed
	 */
	public boolean isTimeShown() {
		return timeShown;
	}

	/**
	 * Sets if the report time of the log message should be printed for logging.
	 * May not be available for all platforms (returns empty string when not).
	 * 
	 * @param timeShown
	 *            if the timestamp should be printed
	 */
	final public static void setTimeShown(boolean timeShown) {
		logger.setTimeShownImpl(timeShown);
	}

	protected void setTimeShownImpl(boolean timeShown) {
		this.timeShown = timeShown;
	}

	/**
	 * Reports if the caller class and method names are printed for logging
	 * 
	 * @return if the names are printed
	 */
	public boolean isCallerShown() {
		return callerShown;
	}

	/**
	 * Sets if the caller class and method names should be printed for logging
	 * 
	 * @param callerShown
	 *            if the names should be printed
	 */
	public static void setCallerShown(boolean callerShown) {
		logger.callerShown = callerShown;
	}

	/**
	 * @return the levelShown
	 */
	public boolean isLevelShown() {
		return levelShown;
	}

	/**
	 * @param levelShown
	 *            the levelShown to set
	 */
	public static void setLevelShown(boolean levelShown) {
		logger.levelShown = levelShown;
	}

	/**
	 * Prints a log message if the logLevel is set to <= level and stores those
	 * classes which have no implementation (simply checks if the message starts
	 * with "implementation needed")
	 * 
	 * @param level
	 *            logging level
	 * @param logMessage
	 *            the log message
	 * @param depth
	 *            depth in stacktrace
	 */
	public void log(Level level, String logMessage, int depth) {
		String message = logMessage;
		if (message == null) {
			message = "<null>";
		}

		if (logLevel.getPriority() >= level.getPriority()) {
			String caller = "";
			if (callerShown) {
				caller = getCaller(depth);
				handleImplementationNeeded(caller, message);
				caller += ": ";
			}
			String timeInfo = "";
			if (timeShown) {
				timeInfo = getTimeInfo();
				if (!"".equals(timeInfo)) {
					timeInfo += " ";
				}
			}
			// Creating logEntry
			String logEntry = timeInfo;
			if (levelShown) {
				logEntry += level.text + ": ";
			}
			logEntry += caller + message;
			print(logEntry, level);
			// In desktop logging, preserve the entire log in memory as well:
			if (keepLog) {
				if (memoryLog.length() > LOGFILE_MAXLENGTH) {
					memoryLog.setLength(0);
				}
				memoryLog.append(logEntry);
				memoryLog.append("\n");
			}
		}
	}

	private static void handleImplementationNeeded(String caller,
			String message) {
		if (message.length() >= 21
				&& message.toLowerCase().substring(0, 21)
						.equals("implementation needed")) {
			if (!reportedImplementationNeeded.contains(caller)) {
				reportedImplementationNeeded.add(caller);
			}

		}

	}

	/**
	 * Prints the log entry, which is usually the full message with timestamp,
	 * the logging level and the caller class
	 * 
	 * @param logEntry
	 *            the full log entry
	 * @param level
	 *            logging level
	 */
	protected abstract void print(String logEntry, Level level);

	/**
	 * Returns the current time in human readable format (for debugging)
	 * 
	 * @return the timestamp
	 */
	final public static String getTimeInfo() {
		return logger.getTimeInfoImpl();
	}

	protected String getTimeInfoImpl() {
		return "";
		// Implementation overrides this in some applications.
	}

	/**
	 * Returns some memory related information (for debugging)
	 * 
	 * @return the memory info text
	 */
	public String getMemoryInfo() {
		return "";
		// Implementation overrides this in some applications.
	}

	/**
	 * Returns the caller class and method names
	 * 
	 * @param depth
	 *            depth in stacktrace
	 * 
	 * @return the full Java class and method name
	 */
	public String getCaller(int depth) {
		String callerMethodName = null;
		String callerClassName = null;
		int callerLineNumber;

		try {
			Throwable t = new Throwable();
			StackTraceElement[] elements = t.getStackTrace();
			// String calleeMethod = elements[0].getMethodName();
			if (elements[depth] == null) {
				return "?";
			}
			callerMethodName = elements[depth].getMethodName();
			callerClassName = elements[depth].getClassName();
			callerLineNumber = elements[depth].getLineNumber();
			if ("Unknown".equals(callerClassName)) {
				/*
				 * In web production mode the GWT compile rewrites the code very
				 * thoroughly. We are doing some intuitive hacking here to
				 * explode the method name; since other information (class name,
				 * line number) is unavailable.
				 */

				// PRETTY style
				// safari:
				if ("$fillInStackTrace".equals(callerMethodName)) {
					if (elements.length < 10) {
						return "?";
					}
					return elements[9].getMethodName();
				}
				// gecko1_8
				if ("fillInStackTrace".equals(callerMethodName)) {
					if (elements.length < 11) {
						return "?";
					}
					return elements[10].getMethodName();
				}
				// TODO: Maybe other user agents could be supported.

				// OBFUSCATED style
				return callerMethodName;
			}

		} catch (Throwable t) {
			// do nothing here; we are probably running Web in Opera
			return "?";
		}
		return callerClassName + "." + callerMethodName + "[" + callerLineNumber
				+ "]";
	}

	/**
	 * Prints debugging message, level DEBUG
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void debug(String message) {
		if (logger != null) {
			logger.log(logger.DEBUG, message);
		}
	}

	/**
	 * Prints special debugging message, level DEBUG
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void debugSpecial(String message) {
		if (logger != null) {

			for (int i = 0; i < message.length(); i++) {
				char c = message.charAt(i);
				if (c != '.' && c != '-' && c != 'e' && c != '+'
						&& (c < '0' || c > '9')) {
					logger.log(logger.DEBUG,
							"Problem with decimal point " + message);
					return;
				}
			}

		}
	}

	/**
	 * @param message
	 *            debug message
	 * @param depth
	 *            stacktace depth in which to look (4 if you want to see direct
	 *            caller, 5 for one above)
	 */
	public static void debug(String message, int depth) {
		if (logger != null) {
			logger.log(logger.DEBUG, message, depth);
		}
	}

	/**
	 * @param message
	 *            error message
	 * @param depth
	 *            stacktace depth in which to look (4 if you want to see direct
	 *            caller, 5 for one above)
	 */
	public static void error(String message, int depth) {
		if (logger != null) {
			logger.log(logger.ERROR, message, depth);
		}
	}

	/**
	 * Prints debugging message, level NOTICE
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void notice(String message) {
		if (logger != null) {
			logger.log(logger.NOTICE, message);
		}
	}

	/**
	 * Prints debugging message, level DEBUG Special debugging format is used
	 * for expression values
	 * 
	 * @param s
	 *            object to be printed
	 */
	public static void debug(Object s) {
		if (s instanceof double[]) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < ((double[]) s).length; i++) {
				sb.append(((double[]) s)[i]);
				sb.append(',');
			}
			debug(sb.toString());
			return;
		}
		if (s instanceof HasDebugString) {
			debug(((HasDebugString) s).getDebugString(), 4);
			return;
		}

		if (s instanceof Throwable && logger != null) {
			logger.doPrintStacktrace((Throwable) s);
			return;
		}
		if (s == null) {
			debug("<null>", 5);
		} else {
			debug(s.toString(), 5);
		}
	}

	/**
	 * @param s
	 *            exception
	 */
	protected void doPrintStacktrace(Throwable s) {
		s.printStackTrace();

	}

	private void log(Level level, String message) {
		log(level, message, 4);
	}

	/**
	 * Prints debugging message, level INFO
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void info(String message) {
		if (logger != null) {
			logger.log(logger.INFO, message);
		}
	}

	/**
	 * Prints debugging message, level ERROR
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void error(String message) {
		if (logger != null) {
			logger.log(logger.ERROR, message);
		}
	}

	/**
	 * Prints debugging message, level WARN
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void warn(String message) {
		if (logger != null) {
			logger.log(logger.WARN, message);
		}
	}

	/**
	 * Prints debugging message, level EMERGENCY
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void emergency(String message) {
		if (logger != null) {
			logger.log(logger.EMERGENCY, message);
		}
	}

	/**
	 * Prints debugging message, level ALERT
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void alert(String message) {
		if (logger != null) {
			logger.log(logger.ALERT, message);
		}
	}

	/**
	 * Prints debugging message, level TRACE
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void trace(String message) {
		if (logger != null) {
			logger.log(logger.TRACE, message);
		}
	}

	/**
	 * Prints debugging message, level CRITICAL
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void critical(String message) {
		if (logger != null) {
			logger.log(logger.CRITICAL, message);
		}
	}

	/**
	 * 
	 * @param message
	 *            content to be printed on top of the trace
	 */
	public static void printStacktrace(String message) {
		if (logger != null) {
			logger.doPrintStacktrace(
					message == null ? "<null>" : message);
		}
	}

	/**
	 * @param message
	 *            message at the top of the trace
	 */
	public abstract void doPrintStacktrace(String message);

	/**
	 * @param log
	 *            sets the logger to this
	 */
	public static void setLogger(Log log) {
		synchronized (lock) {
			logger = log;
		}

	}

	/**
	 * @return active (singleton) logger
	 */
	public static Log getLogger() {
		return logger;
	}

	/**
	 * @param string
	 *            send screen reader text to log
	 */
	public static void read(String string) {
		logger.doRead(string);
	}

	private void doRead(String string) {
		if (reading) {
			error(string);
		}
	}

	/**
	 * @param b
	 *            whether to print screen reader output
	 */
	public void setReading(boolean b) {
		reading = true;
	}

}
