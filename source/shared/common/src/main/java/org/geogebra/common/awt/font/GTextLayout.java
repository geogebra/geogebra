package org.geogebra.common.awt.font;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle2D;

/**
 * Interface for classes compatible with java.awt.font.TextLayout
 */
public interface GTextLayout {

	/**
	 * @return width of the text in pixels
	 */
	double getAdvance();

	/**
	 * @return bounding rectangle
	 */
	GRectangle2D getBounds();

	/**
	 * 
	 * @return distance between baseline and top of the highest character
	 */
	double getAscent();

	/**
	 * Draw this text in graphics
	 * 
	 * @param g2
	 *            graphics
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 */
	void draw(GGraphics2D g2, int x, int y);

	/**
	 * @return distance between baseline and deepest bottom of a character
	 */
	double getDescent();

}
