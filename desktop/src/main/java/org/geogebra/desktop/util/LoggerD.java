package org.geogebra.desktop.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.geogebra.common.util.debug.Log;

/**
 * Web implementation for the GeoGebraLogger
 * 
 * @author Zoltan Kovacs 
 */
public class LoggerD extends Log {

	private File logFile = null; // default
	private BufferedWriter logFileWriter = null;

	/**
	 * Constructor
	 */
	public LoggerD() {
		keepLog = true;
	}

	@Override
	protected String getTimeInfoImpl() {
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
		return calendar.get(Calendar.HOUR_OF_DAY) + ":" + minS + ":" + secS
				+ "." + msecS;
	}

	@Override
	public String getMemoryInfo() {
		long usedK = (Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory()) / 1024;
		return ("\n free memory: ") + Runtime.getRuntime().freeMemory()
				+ " total memory: " + Runtime.getRuntime().totalMemory()
				+ " max memory: " + Runtime.getRuntime().maxMemory()
				+ "\n used memory (total-free): " + usedK + "K";
	}

	/**
	 * Sets the log file name (if FILE logging is available)
	 * 
	 * @param logFileName
	 *            the name of the log file
	 */
	public void setLogFileImpl(String logFileName) {
		if (logFile != null && logFileWriter != null) {
			try {
				logFileWriter.close();
			} catch (IOException e) {
				log(WARN, "Previous log file cannot be closed", 1);
			}
		}
		logFile = new File(logFileName);
		try {
			logFileWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(logFile), "UTF-8"));
		} catch (IOException e) {
			log(WARN, "Log file " + logFileName + "cannot be opened", 1);
		}
	}

	@Override
	protected void print(String logEntry, Level level) {
		if (getLogDestination() == LogDestination.FILE) {
			if (logFileWriter != null) {
				try {
					logFileWriter.append(logEntry + "\n");
					logFileWriter.flush();
					return;
				} catch (IOException e) {
					// Falling back to use CONSOLE instead:
					setLogDestination(LogDestination.CONSOLE);
					log(WARN, "Error writing log file", 1);
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
			} else {
				System.out.println(logEntry);
			}
			return;
		}
	}

	@Override
	public void doPrintStacktrace(String message) {
		try {
			// message null check done in caller
			throw new Exception(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
