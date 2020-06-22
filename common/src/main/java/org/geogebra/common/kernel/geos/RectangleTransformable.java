package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.kernelND.GeoElementND;

public interface RectangleTransformable extends GeoElementND {

	/**
	 * @return min width in pixels, depends on content
	 */
	double getMinWidth();

	/**
	 * @return min height in pixels, depends on content
	 */
	double getMinHeight();

	/**
	 * Get the height of the element.
	 *
	 * @return height
	 */
	double getHeight();

	/**
	 * Get the widht of the element.
	 *
	 * @return width
	 */
	double getWidth();

	/**
	 * @return rotation angle in radians
	 */
	double getAngle();

	/**
	 * Get the location of the text.
	 *
	 * @return location
	 */
	GPoint2D getLocation();

	/**
	 * Set the width of the element.
	 *
	 * @param width element width in pixels
	 */
	void setWidth(double width);

	/**
	 * Set the height of the element.
	 *
	 * @param height height in pixels
	 */
	void setHeight(double height);

	/**
	 * @param angle rotation angle in radians
	 */
	void setAngle(double angle);

	/**
	 * @param location
	 *            on-screen location
	 */
	void setLocation(GPoint2D location);
}
