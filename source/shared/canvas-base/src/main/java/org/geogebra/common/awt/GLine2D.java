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

package org.geogebra.common.awt;

/**
 * 2D line.
 */
public interface GLine2D extends GShape {

	/**
	 * @param x1 start point's x-coordinate
	 * @param y1 start point's y-coordinate
	 * @param x2 end point's x-coordinate
	 * @param y2 end point's y-coordinate
	 */
	void setLine(double x1, double y1, double x2, double y2);

	/**
	 * @return the start point
	 */
	GPoint2D getP1();

	/**
	 * @return the end point
	 */
	GPoint2D getP2();

	/**
	 * @return x-coordinate of the start point
	 */
	double getX1();

	/**
	 * @return y-coordinate of the start point
	 */
	double getY1();

	/**
	 * @return x-coordinate of the end point
	 */
	double getX2();

	/**
	 * @return y-coordinate of the end point
	 */
	double getY2();
}
