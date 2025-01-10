package org.geogebra.gwtutil;

/**
 * Callback for script loading
 */
public interface ScriptLoadCallback {
	/** Run when script is loaded */
	void onLoad();

	/** Run when script fails to load */
	void onError();

	/**
	 * Prevent running load callback if not already loaded
	 */
	void cancel();

}
