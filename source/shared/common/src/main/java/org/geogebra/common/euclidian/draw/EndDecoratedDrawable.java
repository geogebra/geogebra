package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;

public interface EndDecoratedDrawable {
	/**
	 * @return screen x-coordinate of start point
	 */
	double getX1();

	/**
	 * @return screen x-coordinate of end point
	 */
	double getX2();

	/**
	 * @return screen y-coordinate of start point
	 */
	double getY1();

	/**
	 * @return screen y-coordinate of end point
	 */
	double getY2();

	/**
	 * Set style for highlighting.
	 */
	void setHighlightingStyle(GGraphics2D g2);

	/**
	 * @return shape without decoration
	 */
	GShape getLine();

	/**
	 * Set style for default drawing.
	 * @param g2 graphics
	 */
	void setBasicStyle(GGraphics2D g2);

	/**
	 * @param isStart whether to consider the first segment
	 * @return angle between x-axis and first or last segment
	 */
	double getAngle(boolean isStart);

	/**
	 * @return stroke with no dash pattern
	 */
	GBasicStroke getDecoStroke();

	/**
	 * @return whether the shape is highlighted
	 */
	boolean isHighlighted();
}
