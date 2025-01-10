package org.geogebra.common.util;

/**
 * Component that supports text editing
 */
public interface TextObject {
	/**
	 * @return editable content
	 */
	String getText();

	/**
	 * @param text
	 *            editable content
	 */
	void setText(String text);

	/**
	 * @param visible
	 *            whether this should be visible
	 */
	void setVisible(boolean visible);

	/**
	 * @param editable
	 *            whether this can be edited
	 */
	void setEditable(boolean editable);
}
