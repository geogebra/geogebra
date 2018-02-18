package org.geogebra.web.html5.util;

/**
 * Callback for script loading
 */
public interface ScriptLoadCallback {
	/** Run when script is loaded */
	public void onLoad();

	/** Run when script fails to load */
	public void onError();

	/**
	 * Prevent runing load callback if not already loaded
	 */
	public void cancel();

}
