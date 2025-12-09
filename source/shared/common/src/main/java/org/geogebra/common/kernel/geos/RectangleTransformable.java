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

package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Element with bounding rectangle that can be transformed.
 */
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
	 * Get the width of the element.
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
	 * Set bounding rectangle size
	 * @param width width in pixels
	 * @param height height in pixels
	 */
	void setSize(double width, double height);

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
