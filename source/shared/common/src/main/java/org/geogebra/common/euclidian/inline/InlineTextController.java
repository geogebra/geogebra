package org.geogebra.common.euclidian.inline;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.draw.HasTextFormat;

/**
 * Controller for the inline text editor.
 */
public interface InlineTextController extends HasTextFormat {

	@MissingDoc
	boolean updateFontSize();

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
	 * Bring to foreground and move caret to a position.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
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
	 *           graphics
	 */
	void draw(GGraphics2D g2);

	/**
	 * @param x  x-coordinate in pixels
	 * @param y y-coordinate in pixels
	 * @return link URL at given coordinates
	 */
	String urlByCoordinate(int x, int y);

	@MissingDoc
	void updateContentIfChanged();

	@MissingDoc
	void saveContent();

	/**
	 * Set affine transform.
	 * @param angle rotation angle
	 * @param sx horizontal scale factor
	 * @param sy vertical scale factor
	 */
	void setTransform(double angle, double sx, double sy);

	@MissingDoc
	boolean isEditing();
}
