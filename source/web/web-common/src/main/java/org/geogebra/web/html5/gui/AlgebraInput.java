package org.geogebra.web.html5.gui;

/**
 * Plain text algebra input.
 */
public interface AlgebraInput {

	/**
	 * Adjust width to match the applet.
	 * @param appletWidth applet width
	 */
	void setInputFieldWidth(int appletWidth);

	/**
	 * Set input content.
	 * @param string content
	 */
	void setText(String string);

	/**
	 * Focus the input element.
	 */
	void requestFocus();

}
