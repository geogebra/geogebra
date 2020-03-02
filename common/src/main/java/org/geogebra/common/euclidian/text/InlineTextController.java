package org.geogebra.common.euclidian.text;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;

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

	void setAngle(double angle);

	/**
	 * Put the editor behind the canvas
	 */
	void setBackground(boolean b);

	boolean isBackground();

	/**
	 * Set the cursor to the given position
	 */
	void setCursor(int x, int y);

	/**
	 * @param key
	 *            property name
	 * @param val
	 *            property value
	 */
	void format(String key, Object val);

	/**
	 * Set content from geo
	 */
	void updateContent();

	/**
	 * @param key
	 *           format property name
	 * @param fallback
	 *           fomat value
	 * @param <T>
	 *           fallback if not set or multiple values
	 * @return format value
	 */
	<T> T getFormat(String key, T fallback);

	/**
	 * @return hyperlink url, if no url empty string
	 */
	String getHyperLinkURL();

	/**
	 * @return the plaintext representation of the hyperlink range
	 */
	String getHyperlinkRangeText();

	/**
	 * @param g2
	 * 	          graphics
	 * @param transform
	 *            transform w.r.t. top left corner, does not include padding
	 */
	void draw(GGraphics2D g2, GAffineTransform transform);

	/**
	 * Inserts formatted hyperlink at the current selection
	 */
	void insertHyperlink(String url, String text);

	/**
	 * @param url
	 *         (absolute) link URL
	 */
	void setHyperlinkUrl(String url);

	/**
	 * Changes selected text to bullet or numbered list
	 * @param listType - either "bullet" or "number"
	 */
	void switchListTo(String listType);

	/**
	 * Returns the style of selected text
	 * @return "number" or "bullet"
	 */
	String getListStyle();

	String urlByCoordinate(int x, int y);
}
