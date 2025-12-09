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
