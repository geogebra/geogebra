package org.geogebra.common.euclidian.plot;

/** ways to overcome discontinuity */
public enum Gap {
	/** draw a line */
	LINE_TO,
	/** skip it */
	MOVE_TO,
	/** follow along bottom of screen */
	RESET_XMIN,
	/** follow along left side of screen */
	RESET_YMIN,
	/** follow along top of screen */
	RESET_XMAX,
	/** follow along right side of screen */
	RESET_YMAX,
	/** go to corner (for cartesian curves) */
	CORNER,
}
