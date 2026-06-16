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
	LINE_TO(0),
	/** moveto */
	MOVE_TO(1),
	/**
	 * curveto using aux point as intersection of tangents, assumes angle &lt;=
	 * pi/2
	 */
	ARC_TO(2),
	/**
	 * needed for pen stroke, uses bezier curve
	 */
	CURVE_TO(3),
	/** control point for curve_to */
	CONTROL(4),
	/** aux point for arc_to */
	AUXILIARY(5);

	/**
	 * Stable integer code for XML serialization. Unlike {@link #ordinal()}, this
	 * value is fixed regardless of the order values appear in the enum declaration.
	 */
	public final int code;

	SegmentType(int code) {
		this.code = code;
	}

	/**
	 * Reverse lookup by {@link #code}.
	 * @param code the code as stored in XML
	 * @return the matching type, or {@link #LINE_TO} as a safe fallback
	 */
	public static SegmentType fromCode(int code) {
		for (SegmentType type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		return LINE_TO;
	}
}
