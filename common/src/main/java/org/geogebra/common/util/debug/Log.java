package org.geogebra.common.util.debug;

import java.util.Arrays;
import java.util.Locale;

/**
 * Common logging class
 * 
 * @author Zoltan Kovacs
 */

public abstract class Log {

	/** logger */
	private static volatile Log logger;
	private static Object lock = new Object();

	public enum Level {
		EMERGENCY,
		ALERT,
		CRITICAL,
		ERROR,
		WARN,
		NOTICE,
		INFO,
		DEBUG,
		TRACE
	}

	/** in case keepLog = true, this sets max length of in-memory log */
	public static final int LOGFILE_MAXLENGTH = 10000;

	protected final StringBuilder memoryLog = new StringBuilder();
	protected Level logLevel = Level.DEBUG; // default
	protected LogDestination logDestination = LogDestination.CONSOLE; // default;
	protected boolean callerShown = true; // default
	protected boolean levelShown = true; // default
	private boolean reading = false;

	/**
	 * The entire log since starting the application.
	 * 
	 * @return the entire log
	 */
	final public static StringBuilder getEntireLog() {
		return logger.memoryLog;
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

		try {
			this.logLevel = Level.valueOf(logLevel.toUpperCase(Locale.US));
		} catch (IllegalArgumentException e) {
			this.logLevel = Level.DEBUG;
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

	private void log(Level level, Object message) {
		if (logLevel.ordinal() >= level.ordinal()) {
			if (message instanceof int[]) {
				print(level, Arrays.toString((int[]) message));
			} else if (message instanceof double[]) {
				print(level, Arrays.toString((double[]) message));
			} else if (message instanceof Object[]) {
				print(level, Arrays.deepToString((Object[]) message));
			} else if (message instanceof HasDebugString) {
				print(level, ((HasDebugString) message).getDebugString());
			} else {
				print(level, message);
			}
		}
	}

	/**
	 * Prints a log message if the logLevel is set to <= level
	 * 
	 * @param level
	 *            logging level
	 * @param logMessage
	 *            the log message
	 */
	public abstract void print(Level level, Object logMessage);

	/**
	 * Prints debugging message, level NOTICE
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void notice(String message) {
		if (logger != null) {
			logger.log(Level.NOTICE, message);
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
		if (logger != null) {
			logger.log(Level.DEBUG, s);
		}
	}

	/**
	 * Prints debugging message, level INFO
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void info(String message) {
		if (logger != null) {
			logger.log(Level.INFO, message);
		}
	}

	/**
	 * Prints debugging message, level ERROR
	 * 
	 * @param message
	 *            message to be printed
	 */
	public static void error(Object message) {
		if (logger != null) {
			logger.log(Level.ERROR, message);
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
			logger.log(Level.WARN, message);
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
			logger.log(Level.EMERGENCY, message);
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
			logger.log(Level.ALERT, message);
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
			logger.log(Level.TRACE, message);
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
			logger.log(Level.CRITICAL, message);
		}
	}

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
