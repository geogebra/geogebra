/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package org.geogebra.common.kernel.kernelND;

/**
 * Quadric types
 *
 */
public interface GeoQuadricNDConstants {
	/** type: single point*/
	public static final int QUADRIC_SINGLE_POINT = 1;
	/** type: intersecting lines*/
	public static final int QUADRIC_INTERSECTING_LINES = 2;
	/** type: ellipsoid*/
	public static final int QUADRIC_ELLIPSOID = 3;
	/** type: sphere*/
	public static final int QUADRIC_SPHERE = 4;
	/** type: hyperboloid*/
	public static final int QUADRIC_HYPERBOLOID = 5;
	/** type: empty quadric*/
	public static final int QUADRIC_EMPTY = 6;
	/** type: double line*/
	public static final int QUADRIC_DOUBLE_LINE = 7;
	/** type: parallel lines */
	public static final int QUADRIC_PARALLEL_LINES = 8;
	/** type: paraboloid*/
	public static final int QUADRIC_PARABOLOID = 9;
	/** type: line*/
	public static final int QUADRIC_LINE = 10;
	/** type: cone*/
	public static final int QUADRIC_CONE = 30;
	/** type: cylinder*/
	public static final int QUADRIC_CYLINDER = 31;
	/** temporary type: not classified*/
	public static final int QUADRIC_NOT_CLASSIFIED = 32;
}
