package geogebra.common.util.debug;

/**
 * @author gabor
 * 
 * Uses console.profile where possible.
 * Abstract implementation, because of Common usages.
 *
 */
public abstract class GeoGebraProfiler {
	
	/**
	 * @param label
	 * 
	 * see: https://developers.google.com/chrome-developer-tools/docs/console-api#consoleprofilelabel
	 */
	public abstract void profile(String label);
	
	/**
	 * see: https://developers.google.com/chrome-developer-tools/docs/console-api#consoleprofilelabel
	 */
	public abstract void profileEnd();
	
	
	/**
	 * @param label
	 * 
	 * see: https://developers.google.com/chrome-developer-tools/docs/console-api#consoleprofilelabel
	 */
	public abstract void time(String label);
	
	/**
	 * @param label
	 * 
	 * see: https://developers.google.com/chrome-developer-tools/docs/console-api#consoleprofilelabel	 * 
	 */
	public abstract void timeEnd();
}
