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
 * Stroke style.
 */
public interface GBasicStroke {
	public static final int CAP_BUTT = 0; // Java & GWT
	public static final int CAP_ROUND = 1; // Java & GWT
	public static final int CAP_SQUARE = 2; // Java & GWT
	public static final int JOIN_MITER = 0; // Java
	public static final int JOIN_ROUND = 1; // Java
	public static final int JOIN_BEVEL = 2; // Java

	/**
	 * Contour of given shape as a shape.
	 * @param shape source shape
	 * @param capacity initial number of points
	 * @return stroke shape
	 */
	GShape createStrokedShape(GShape shape, int capacity);

	/**
	 * @return end cap type
	 */
	int getEndCap();

	/**
	 * @return mitre limit
	 */
	double getMiterLimit();

	/**
	 * @return line join type
	 */
	int getLineJoin();

	/**
	 * @return line width in pixels
	 */
	double getLineWidth();

	/**
	 * @return dashed line pattern
	 */
	double[] getDashArray();

}
