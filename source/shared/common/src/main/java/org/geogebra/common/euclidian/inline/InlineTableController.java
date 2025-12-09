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
import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.properties.BorderType;

/**
 * Inline table controller.
 */
public interface InlineTableController extends HasTextFormat {

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
	void setWidth(double width);

	/**
	 * Set the height of the editor.
	 *
	 * @param height height
	 */
	void setHeight(double height);

	@MissingDoc
	void removeFromDom();

	@MissingDoc
	void update();

	@MissingDoc
	boolean isInEditMode();

	/**
	 * Draw to graphics.
	 * @param g2 graphics
	 * @param transform affine transform
	 */
	void draw(GGraphics2D g2, GAffineTransform transform);

	/**
	 * Bring to foreground and move caret to given position.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	void toForeground(int x, int y);

	@MissingDoc
	void toBackground();

	@MissingDoc
	void updateContent();

	/**
	 * @param bgColor background color
	 */
	void setBackgroundColor(GColor bgColor);

	/**
	 * @return background color
	 */
	GColor getBackgroundColor();

	/**
	 * @param x  x-coordinate in pixels
	 * @param y y-coordinate in pixels
	 * @return link URL at given coordinates
	 */
	String urlByCoordinate(int x, int y);

	@MissingDoc
	void insertRowAbove();

	@MissingDoc
	void insertRowBelow();

	@MissingDoc
	void insertColumnLeft();

	@MissingDoc
	void insertColumnRight();

	@MissingDoc
	void removeRow();

	@MissingDoc
	void removeColumn();

	/**
	 * @param borderThickness border thickness in pixels
	 */
	void setBorderThickness(int borderThickness);

	@MissingDoc
	int getBorderThickness();

	/**
	 * @param borderType border style
	 */
	void setBorderStyle(BorderType borderType);

	/**
	 * @return border style
	 */
	BorderType getBorderStyle();

	/**
	 * @param setting one of "wrap", "clip"
	 */
	void setWrapping(String setting);

	/**
	 * @return one of "wrap", "clip"
	 */
	String getWrapping();

	/**
	 * @param setting one of "None", "Up", "Down"
	 */
	void setRotation(String setting);

	/**
	 * @return one of "None", "Up", "Down"
	 */
	String getRotation();

	/**
	 * Set heading color
	 * @param color color
	 * @param isRow true for row heading, false for column
	 */
	void setHeading(GColor color, boolean isRow);

	@MissingDoc
	void saveContent();

	@MissingDoc
	boolean isSingleCellSelection();

	@MissingDoc
	boolean hasSelection();

	@MissingDoc
	int getSelectedColumn();

	/**
	 * @param angle angle
	 * @param sx horizontal scale
	 * @param sy vertical scale
	 */
	void setTransform(double angle, double sx, double sy);

	/**
	 * @param x pixel x-coordinate of the hit
	 * @param y pixel y-coordinate of the hit
	 */
	void setHitCell(double x, double y);
}
