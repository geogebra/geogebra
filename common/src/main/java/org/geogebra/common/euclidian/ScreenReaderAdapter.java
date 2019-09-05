package org.geogebra.common.euclidian;

public interface ScreenReaderAdapter {

	/**
	 * Read text if possible.
	 * 
	 * @param text
	 *            text
	 */
	void readText(String text);

    void readDelayed(String text);
}
