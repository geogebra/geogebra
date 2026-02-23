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
	int QUADRIC_SINGLE_POINT = 1;
	/** type: intersecting lines */
	int QUADRIC_INTERSECTING_LINES = 2;
	/** type: ellipsoid */
	int QUADRIC_ELLIPSOID = 3;
	/** type: sphere */
	int QUADRIC_SPHERE = 4;
	/** type: hyperboloid */
	int QUADRIC_HYPERBOLOID = 5;
	/** type: empty quadric */
	int QUADRIC_EMPTY = 6;
	/** type: double line */
	int QUADRIC_DOUBLE_LINE = 7;
	/** type: parallel lines */
	int QUADRIC_PARALLEL_LINES = 8;
	/** type: paraboloid */
	int QUADRIC_PARABOLOID = 9;
	/** type: line */
	int QUADRIC_LINE = 10;
	/** type: cone */
	int QUADRIC_CONE = 30;
	/** type: cylinder */
	int QUADRIC_CYLINDER = 31;
	/** temporary type: not classified */
	int QUADRIC_NOT_CLASSIFIED = 32;
	/** type : one plane */
	int QUADRIC_PLANE = 33;
	/** type : parallel planes */
	int QUADRIC_PARALLEL_PLANES = 34;
	/** type : intersecting planes */
	int QUADRIC_INTERSECTING_PLANES = 35;
	/** type: hyperboloid one sheet */
	int QUADRIC_HYPERBOLOID_ONE_SHEET = 36;
	/** type: hyperboloid one sheet */
	int QUADRIC_HYPERBOLOID_TWO_SHEETS = 37;
	/** type: parabolic cylinder */
	int QUADRIC_PARABOLIC_CYLINDER = 38;
	/** type: hyperbolic cylinder */
	int QUADRIC_HYPERBOLIC_CYLINDER = 39;
	/** type: hyperbolic paraboloid */
	int QUADRIC_HYPERBOLIC_PARABOLOID = 40;

}
