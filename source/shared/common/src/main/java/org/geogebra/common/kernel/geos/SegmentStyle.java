package org.geogebra.common.kernel.geos;

import java.util.Locale;

public enum SegmentStyle {
	DEFAULT, LINE, SQUARE_OUTLINE, SQUARE, ARROW,
	ARROW_FILLED, CIRCLE_OUTLINE, CIRCLE;

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
		return equals(SQUARE_OUTLINE) || equals(CIRCLE_OUTLINE);
	}

	/**
	 *
	 * @return if default
	 */
	public boolean isDefault() {
		return equals(DEFAULT);
	}
}
