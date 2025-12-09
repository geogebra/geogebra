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

package org.geogebra.common.kernel.kernelND;

/**
 * Conic types
 */
public interface GeoConicNDConstants extends GeoQuadricNDConstants {

	/** single point type */
	public static final int CONIC_SINGLE_POINT = QUADRIC_SINGLE_POINT;
	/** intersecting lines type */
	public static final int CONIC_INTERSECTING_LINES = QUADRIC_INTERSECTING_LINES;
	/** ellipse type */
	public static final int CONIC_ELLIPSE = QUADRIC_ELLIPSOID;
	/** circle type */
	public static final int CONIC_CIRCLE = QUADRIC_SPHERE;
	/** hyperbola type */
	public static final int CONIC_HYPERBOLA = QUADRIC_HYPERBOLOID;
	/** empty conic type */
	public static final int CONIC_EMPTY = QUADRIC_EMPTY;
	/** double line type */
	public static final int CONIC_DOUBLE_LINE = QUADRIC_DOUBLE_LINE;
	/** parallel lines type */
	public static final int CONIC_PARALLEL_LINES = QUADRIC_PARALLEL_LINES;
	/** parabola type */
	public static final int CONIC_PARABOLA = QUADRIC_PARABOLOID;
	/** line type */
	public static final int CONIC_LINE = QUADRIC_LINE;

	// -- FOR GEO CONIC PART

	/** conic arc */
	public static final int CONIC_PART_ARC = 1;
	/** conic sector */
	public static final int CONIC_PART_SECTOR = 2;
	/** conic arcs and */
	public static final int CONIC_PART_ARCS = 3;

}
