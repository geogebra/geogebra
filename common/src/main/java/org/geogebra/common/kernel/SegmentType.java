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
	/** aux point for arc_to */
	AUXILIARY

}
