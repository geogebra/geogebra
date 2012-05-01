package geogebra.common.awt.font;

import geogebra.common.awt.Graphics2D;
import geogebra.common.awt.Rectangle2D;

/**
 * Interface for classes compatible with java.awt.font.TextLayout
 */
public interface TextLayout {

	/**
	 * @return width of the text in pixels
	 */
	float getAdvance();

	/**
	 * @return bounding rectangle
	 */
	Rectangle2D getBounds();
	/**
	 * 
	 * @return distance between baseline and top of the highest character
	 */
	float getAscent();

	/**
	 * Draw this text in graphics
	 * @param g2 graphics
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	void draw(Graphics2D g2, int x, int y);
	
	/**
	 * @return distance between baseline and deepest bottom of a character
	 */
	float getDescent();

}
