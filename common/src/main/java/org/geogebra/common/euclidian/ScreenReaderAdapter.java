package org.geogebra.common.euclidian;

public interface ScreenReaderAdapter {

	/**
	 * Read text if possible.
	 * 
	 * @param textString
	 *            text
	 */
	void readText(String textString);

	/**
	 * Force read given text
	 * 
	 * @param textString
	 *            text
	 */
	void readTextImmediate(String textString);

}
