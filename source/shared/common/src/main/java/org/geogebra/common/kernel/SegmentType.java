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
	 * curveto using aux point as intersection of tangents, assumes angle &lt;=
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
