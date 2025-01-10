package org.geogebra.common.main.settings;

/**
 * Those settings should implement this interface which can be reset to their default values.
 */
public interface Resettable {

	/**
	 * Resets the default values.
	 */
	void resetDefaults();
}
