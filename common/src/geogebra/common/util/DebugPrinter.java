package geogebra.common.util;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Common logging class
 */
public abstract class DebugPrinter {
	
	/**
	 *  
	 */
	public static boolean DEBUG_IN_PRODUCTION = false;
	
	/**
	 * @param s The string to be displayed by the logger
	 * @param info Leading info string
	 * @param level Logging level
	 */
	public abstract void print(String s, String info, int level);
	
	/**
	 * @param s The string to be displayed by the logger
	 */
	public abstract void print(String s);
	
	/**
	 * @param s The string to be displayed by the logger
	 * @param showMemory if memory info to be shown
	 * @param showTime if time details to be shown 
	 * @param level Logging level
	 */
	public abstract void debug(String s, boolean showMemory, boolean showTime, int level);
}
