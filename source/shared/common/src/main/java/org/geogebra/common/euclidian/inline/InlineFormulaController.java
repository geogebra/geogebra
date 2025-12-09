/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
