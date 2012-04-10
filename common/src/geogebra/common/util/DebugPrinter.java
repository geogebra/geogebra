package geogebra.common.util;

/**
 * Common logging class
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public abstract class DebugPrinter {
	
	/**
	 * Sets if debug messages must be shown in production mode 
	 */
	public static boolean DEBUG_IN_PRODUCTION = false;
	
	/**
	 * Prints a debug message
	 * @param s The string to be displayed by the logger
	 * @param info Leading info string
	 * @param level Logging level
	 */
	public abstract void print(String s, String info, int level);
	
	/**
	 * Prints a simple debug message
	 * @param s The string to be displayed by the logger
	 */
	public abstract void print(String s);
	
	/**
	 * Prints a detailed debug message
	 * @param s The string to be displayed by the logger
	 * @param showMemory if memory info to be shown
	 * @param showTime if time details to be shown 
	 * @param level Logging level
	 */
	public abstract void debug(String s, boolean showMemory, boolean showTime, int level);
}
