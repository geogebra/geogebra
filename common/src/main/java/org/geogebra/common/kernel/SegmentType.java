package org.geogebra.common.kernel;

/**
 * Type of point for general path drawing
 */
public enum SegmentType {
	/** lineto */
	LINE_TO,
	/** moveto */
	MOVE_TO,
	/**
	 * curveto using aux point as intersection of tangents, assumes angle <=
	 * pi/2
	 */
	ARC_TO,
	/**
	 * needed for pen stroke, uses bezier curve
	 */
	CURVE_TO,
	/** control point for curve_to */
	CONTROL,
	/** aux point for arc_to */
	AUXILIARY
}
