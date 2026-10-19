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

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.draw.DrawInline;

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
	 * @param sx horizontal scale
	 * @param sy vertical scale
	 */
	void setTransform(double angle, double sx, double sy);

	/**
	 * Bring to foreground and move the caret.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	void toForeground(int x, int y);

	/**
	 * Bring widget to the background.
	 * @param trigger cause of the suspension
	 */
	void toBackground(DrawInline.SuspensionTrigger trigger);

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

	/**
	 * @return whether the widget is in the foreground (editable)
	 */
	boolean isInForeground();

	/**
	 * Remove widget from the UI completely.
	 */
	void discard();

	/**
	 * @return editor content, using GGB syntax
	 */
	String getText();

	/**
	 * @param minHeight minimal height in pixels
	 */
	void setMinHeight(int minHeight);
}
