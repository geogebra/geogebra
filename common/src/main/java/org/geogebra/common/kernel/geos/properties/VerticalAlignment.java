package org.geogebra.common.kernel.geos.properties;

import java.util.Locale;

public enum VerticalAlignment {
	TOP, MIDDLE, BOTTOM;

	@Override
	public String toString() {
		return name().toLowerCase(Locale.US);
	}

	/**
	 * @param align
	 *            string from carota
	 * @return alignment value
	 */
	public static VerticalAlignment fromString(String align) {
		try {
			return valueOf(align.toUpperCase(Locale.US));
		} catch (RuntimeException e) {
			// Invalid alignment
			return null;
		}
	}
}
