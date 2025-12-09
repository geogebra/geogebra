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

package org.geogebra.common.kernel.geos;

import java.util.Locale;

/**
 * Segment end decorations.
 */
public enum SegmentStyle {
	DEFAULT, LINE, ARROW, CROWS_FOOT,
	ARROW_OUTLINE, ARROW_FILLED, CIRCLE_OUTLINE, CIRCLE,
	SQUARE_OUTLINE, SQUARE, DIAMOND_OUTLINE, DIAMOND;

	@Override
	public String toString() {
		return name().toLowerCase(Locale.US);
	}

	/**
	 * @param segmentStyle
	 *            string representation of segment style
	 * @return segment style value
	 */
	public static SegmentStyle fromString(String segmentStyle) {
		try {
			return valueOf(segmentStyle.toUpperCase(Locale.US));
		} catch (RuntimeException e) {
			// Invalid alignment
			return null;
		}
	}

	/**
	 *
	 * @return if segment style is outlined or not.
	 */
	public boolean isOutline() {
		return equals(SQUARE_OUTLINE) || equals(CIRCLE_OUTLINE)
				|| equals(ARROW_OUTLINE) || equals(DIAMOND_OUTLINE);
	}

	/**
	 *
	 * @return if default
	 */
	public boolean isDefault() {
		return equals(DEFAULT);
	}
}
