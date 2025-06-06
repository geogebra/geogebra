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
