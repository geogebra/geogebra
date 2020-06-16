package org.geogebra.common.gui.inputfield;

public interface HasLastItem {

	/**
	 * @return last output as string
	 */
	String getLastItem();

	/**
	 * @return True if the last item is a simple number, otherwise false.
	 */
	boolean isLastItemSimpleNumber();

	/**
	 * @return True if the last item is a GeoText, otherwise false.
	 */
	boolean isLastItemText();
}
