package org.geogebra.common.euclidian.controller;

/**
 * Different modes of a multitouch-event.
 */
public enum MultitouchMode {
	/**
	 * Scale x-axis (two TouchStartEvents on the x-axis).
	 */
	zoomX,

	/**
	 * Scale y-axis (two TouchStartEvents on the y-axis).
	 */
	zoomY,

	/**
	 * Scale a circle or ellipsis with three points or an ellipsis with 5
	 * points.
	 */
	circle3Points,

	/**
	 * Scale a circle with 2 points.
	 */
	circle2Points,

	/**
	 * Scale a circle given with midpoint and a number-input as radius.
	 */
	circleRadius,

	/**
	 * Scale a circle given as input formula.
	 */
	circleFormula,

	/**
	 * Zooming.
	 */
	view,

	/**
	 * Move a line with two fingers.
	 */
	moveLine;
}
