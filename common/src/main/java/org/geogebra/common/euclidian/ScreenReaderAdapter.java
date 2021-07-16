package org.geogebra.common.euclidian;

public interface ScreenReaderAdapter {

	/**
	 * Read text if possible.
	 * 
	 * @param text
	 *            text to be read
	 */
	void readText(String text);

	/**
	 * Read text if possible after a short delay
	 *
	 * @param text
	 *            tect to be read
	 */
    void readDelayed(String text);
}
