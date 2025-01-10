package org.geogebra.common.euclidian.inline;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.properties.BorderType;

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

	void draw(GGraphics2D g2, GAffineTransform transform);

	void toForeground(int x, int y);

	void toBackground();

	void updateContent();

	void setBackgroundColor(GColor bgColor);

	GColor getBackgroundColor();

	String urlByCoordinate(int x, int y);

	void insertRowAbove();

	void insertRowBelow();

	void insertColumnLeft();

	void insertColumnRight();

	void removeRow();

	void removeColumn();

	void setBorderThickness(int borderThickness);

	int getBorderThickness();

	void setBorderStyle(BorderType borderType);

	BorderType getBorderStyle();

	void setWrapping(String setting);

	String getWrapping();

	void setRotation(String setting);

	String getRotation();

	void setHeading(GColor color, boolean isRow);

	void saveContent();

	boolean isSingleCellSelection();

	boolean hasSelection();

	int getSelectedColumn();

	void setTransform(double angle, double sx, double sy);

	/**
	 * @param x pixel x-coordinate of the hit
	 * @param y pixel y-coordinate of the hit
	 */
	void setHitCell(double x, double y);
}
