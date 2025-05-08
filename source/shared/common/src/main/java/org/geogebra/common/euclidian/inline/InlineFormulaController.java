package org.geogebra.common.euclidian.inline;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.awt.GColor;

/**
 * Inline formula controller.
 */
public interface InlineFormulaController {

	/**
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	void setLocation(int x, int y);

	/**
	 * @param width width
	 */
	void setWidth(int width);

	/**
	 * @param height height
	 */
	void setHeight(int height);

	/**
	 * @param angle rotation angle
	 */
	void setAngle(double angle);

	/**
	 * @param sx horizontal scale
	 * @param sy vertical scale
	 */
	void setScale(double sx, double sy);

	/**
	 * Bring to foreground and move the caret.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	void toForeground(int x, int y);

	@MissingDoc
	void toBackground();

	/**
	 * @param content formula content
	 */
	void updateContent(String content);

	/**
	 * @param objectColor text color
	 */
	void setColor(GColor objectColor);

	/**
	 * @param fontSize font size
	 */
	void setFontSize(int fontSize);

	@MissingDoc
	boolean isInForeground();

	@MissingDoc
	void discard();

	@MissingDoc
	String getText();

	/**
	 * @param minHeight minimal height in pixels
	 */
	void setMinHeight(int minHeight);
}
