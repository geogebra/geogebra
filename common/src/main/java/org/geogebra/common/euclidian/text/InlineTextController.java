package org.geogebra.common.euclidian.text;

/**
 * Controller for the inline text editor.
 */
public interface InlineTextController {

	/**
	 * Create the inline text editor.
	 */
	void create();

	/**
	 * Discard the inline text editor.
	 */
	void discard();

	/**
	 * Set the location of the text editor.
	 *
	 * @param x top coordinate
	 * @param y left coordinate
	 */
	void setLocation(int x, int y);

	/**
	 * Set the width of the editor.
	 *
	 * @param width width
	 */
	void setWidth(int width);

	/**
	 * Set the height of the editor.
	 *
	 * @param height height
	 */
	void setHeight(int height);

	/**
	 * Put the editor behind the canvas
	 */
	void toBackground();

	/**
	 * Bring the editor to the foreground and start editing
	 */
	void toForeground();

	void format(String key, Object val);
}
