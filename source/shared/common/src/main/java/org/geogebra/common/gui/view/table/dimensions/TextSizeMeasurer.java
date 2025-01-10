package org.geogebra.common.gui.view.table.dimensions;

/** Measures text size for the Table Values view. */
public interface TextSizeMeasurer {

	/**
	 * Get the width of the text.
	 * @param text text to measure
	 * @return the width of the text
	 */
	public int getWidth(String text);
}
