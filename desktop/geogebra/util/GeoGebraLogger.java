package geogebra.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Web implementation for the GeoGebraLogger 
 */
public class GeoGebraLogger extends geogebra.common.util.GeoGebraLogger {

	private File logFile = null; // default
	private FileWriter logFileWriter = null;
	
	/**
	 * Constructor
	 */
	public GeoGebraLogger() {
	}

	@Override
	protected String getTimeInfo() {
		Calendar calendar = new GregorianCalendar();
		int min = calendar.get(Calendar.MINUTE);
		String minS = (min < 10) ? "0" + min : "" + min;
		int sec = calendar.get(Calendar.SECOND);
		String secS = (sec < 10) ? "0" + sec : "" + sec;
		int msec = calendar.get(Calendar.MILLISECOND);
		String msecS = (msec < 100) ? "0" + msec : "" + msec;
		if (msec < 10) {
			msecS = "0" + msecS;
		}
		return calendar.get(Calendar.HOUR_OF_DAY) + ":" + minS + ":" + secS + "." + msecS;
	}
	
	@Override
	public String getMemoryInfo() {
		long usedK = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
				.freeMemory()) / 1024;
		return ("\n free memory: ") + Runtime.getRuntime().freeMemory()
				+ " total memory: " + Runtime.getRuntime().totalMemory()
				+ " max memory: " + Runtime.getRuntime().maxMemory()
				+ "\n used memory (total-free): " + usedK + "K";
	}
	
	@Override
	public void setLogFile(String logFileName) {
		if (logFile != null && logFileWriter != null) {
			try {
				logFileWriter.close();
			} catch (IOException e) {
				log(WARN, "Previous log file cannot be closed");
			}
		}
		logFile = new File(logFileName);
		try {
			logFileWriter = new FileWriter(logFile);
		} catch (IOException e) {
			log(WARN, "Log file " + logFileName + "cannot be opened");
		}		
	}
	
	@Override
	protected void print(String logEntry, Level level) {
		if (getLogDestination() == LogDestination.WEB_CONSOLE ||
				getLogDestination() == LogDestination.CONSOLES) {
			// This is not supported in desktop.
			// Falling back to use CONSOLE instead:
			setLogDestination(LogDestination.CONSOLE);
			log(WARN, "WEB_CONSOLE logging is not supported in desktop, falling back to use CONSOLE instead");
		}
		if (getLogDestination() == LogDestination.FILE) {
			if (logFileWriter != null) {
				try {
					logFileWriter.append(logEntry + "\n");
					logFileWriter.flush();
					return;
				} catch (IOException e) {
					// Falling back to use CONSOLE instead:
					setLogDestination(LogDestination.CONSOLE);
					log(WARN, "Error writing log file");
					// Trying again (recursive call):
					print(logEntry, level);
					return;
				}
			}
			setLogDestination(LogDestination.CONSOLE);
		}
		if (getLogDestination() == LogDestination.CONSOLE) {
			if (level.getPriority() <= ERROR.getPriority()) {
				System.err.println(logEntry);
				}
			else {
				System.out.println(logEntry);
			}
			return;
		}
	}
}
