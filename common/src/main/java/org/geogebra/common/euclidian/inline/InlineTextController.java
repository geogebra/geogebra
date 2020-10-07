package org.geogebra.common.euclidian.inline;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.draw.HasTextFormat;

/**
 * Controller for the inline text editor.
 */
public interface InlineTextController extends HasTextFormat {

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

	void setAngle(double angle);

	void toForeground(int x, int y);

	/**
	 * Put the editor behind the canvas
	 */
	void toBackground();

	/**
	 * Set content from geo
	 */
	void updateContent();

	/**
	 * @param g2
	 * 	          graphics
	 * @param transform
	 *            transform w.r.t. top left corner, does not include padding
	 */
	void draw(GGraphics2D g2, GAffineTransform transform);

	String urlByCoordinate(int x, int y);

	void updateContentIfChanged();

	void saveContent();
}
