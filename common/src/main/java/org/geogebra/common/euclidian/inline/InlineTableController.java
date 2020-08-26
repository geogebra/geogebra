package org.geogebra.common.euclidian.inline;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.draw.HasTextFormat;

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

	void setAngle(double angle);

	void removeFromDom();

	void update();

	boolean isInEditMode();

	void draw(GGraphics2D g2, GAffineTransform transform);

	void toForeground(int x, int y);

	void toBackground();

	void updateContent();

	void setBackgroundColor(GColor bgColor);

	String urlByCoordinate(int x, int y);

	void insertRowAbove();

	void insertRowBelow();

	void insertColumnLeft();

	void insertColumnRight();

	void removeRow();

	void removeColumn();

	void setWrapping(String setting);

	String getWrapping();
}
