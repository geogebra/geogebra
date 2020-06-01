package org.geogebra.common.euclidian.inline;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;

public interface InlineTableController {

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

	void removeFromDom();

	void update();

	void draw(GGraphics2D g2, GAffineTransform transform);

	void toForeground(int x, int y);

	void toBackground();

	void updateContent();
}
