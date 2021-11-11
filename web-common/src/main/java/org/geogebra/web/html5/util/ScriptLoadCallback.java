package org.geogebra.web.html5.util;

/**
 * Callback for script loading
 */
public interface ScriptLoadCallback {
	/** Run when script is loaded */
	void onLoad();

	/** Run when script fails to load */
	void onError();

	/**
	 * Prevent runing load callback if not already loaded
	 */
	void cancel();

}
