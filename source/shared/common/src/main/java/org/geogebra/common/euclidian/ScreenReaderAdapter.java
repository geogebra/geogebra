package org.geogebra.common.euclidian;

import org.geogebra.common.annotation.MissingDoc;

/**
 * Passes text to native screen reader.
 */
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
	 *            text to be read
	 */
    void readDelayed(String text);

	@MissingDoc
	void cancelReadDelayed();
}
