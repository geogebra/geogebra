package org.geogebra.common.euclidian;

public interface TableController {
	void removeFromDom();

	void format(String key, Object val);

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

	void setAngle(double angle);
}
