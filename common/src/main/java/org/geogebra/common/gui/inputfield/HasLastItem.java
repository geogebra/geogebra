package org.geogebra.common.gui.inputfield;

public interface HasLastItem {

	/**
	 * @return last output as string
	 */
	String getLastItem();

	/**
	 * @return The last output as string.
	 * It doesn't have brackets around it if the last output is a GeoText or a simple number,
	 * otherwise it's between brackets.
	 */
	String getLastItemWithOptionalBrackets();

}
