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
 * Quadric types
 *
 */
public interface GeoQuadricNDConstants {
	/** type: single point */
	public static final int QUADRIC_SINGLE_POINT = 1;
	/** type: intersecting lines */
	public static final int QUADRIC_INTERSECTING_LINES = 2;
	/** type: ellipsoid */
	public static final int QUADRIC_ELLIPSOID = 3;
	/** type: sphere */
	public static final int QUADRIC_SPHERE = 4;
	/** type: hyperboloid */
	public static final int QUADRIC_HYPERBOLOID = 5;
	/** type: empty quadric */
	public static final int QUADRIC_EMPTY = 6;
	/** type: double line */
	public static final int QUADRIC_DOUBLE_LINE = 7;
	/** type: parallel lines */
	public static final int QUADRIC_PARALLEL_LINES = 8;
	/** type: paraboloid */
	public static final int QUADRIC_PARABOLOID = 9;
	/** type: line */
	public static final int QUADRIC_LINE = 10;
	/** type: cone */
	public static final int QUADRIC_CONE = 30;
	/** type: cylinder */
	public static final int QUADRIC_CYLINDER = 31;
	/** temporary type: not classified */
	public static final int QUADRIC_NOT_CLASSIFIED = 32;
	/** type : one plane */
	public static final int QUADRIC_PLANE = 33;
	/** type : parallel planes */
	public static final int QUADRIC_PARALLEL_PLANES = 34;
	/** type : intersecting planes */
	public static final int QUADRIC_INTERSECTING_PLANES = 35;
	/** type: hyperboloid one sheet */
	public static final int QUADRIC_HYPERBOLOID_ONE_SHEET = 36;
	/** type: hyperboloid one sheet */
	public static final int QUADRIC_HYPERBOLOID_TWO_SHEETS = 37;
	/** type: parabolic cylinder */
	public static final int QUADRIC_PARABOLIC_CYLINDER = 38;
	/** type: hyperbolic cylinder */
	public static final int QUADRIC_HYPERBOLIC_CYLINDER = 39;
	/** type: hyperbolic paraboloid */
	public static final int QUADRIC_HYPERBOLIC_PARABOLOID = 40;

}
