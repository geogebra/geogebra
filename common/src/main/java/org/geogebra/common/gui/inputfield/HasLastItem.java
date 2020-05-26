package org.geogebra.common.gui.inputfield;

public interface HasLastItem {

	/**
	 * @return last output as string
	 */
	String getLastItem();

	/**
	 * @return last output between brackets as string
	 */
	String getLastItemWithBrackets();

}
