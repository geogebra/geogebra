package org.geogebra.common.euclidian.inline;

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

	void removeFromDom();

	void update();

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

	void toBackground();

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

	void insertRowAbove();

	void insertRowBelow();

	void insertColumnLeft();

	void insertColumnRight();

	void removeRow();

	void removeColumn();

	/**
	 * @param borderThickness border thickness in pixels
	 */
	void setBorderThickness(int borderThickness);

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

	void saveContent();

	boolean isSingleCellSelection();

	boolean hasSelection();

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
