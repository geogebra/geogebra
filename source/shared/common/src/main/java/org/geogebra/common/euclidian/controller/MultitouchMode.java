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
