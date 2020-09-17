package org.geogebra.common.kernel.geos.properties;

import java.util.Locale;

public enum HorizontalAlignment {
	LEFT, CENTER, RIGHT;

	@Override
	public String toString() {
		return name().toLowerCase(Locale.US);
	}

	/**
	 * @param align
	 *            string from XML
	 * @return alignment value
	 */
	public static HorizontalAlignment fromString(String align) {
		try {
			return valueOf(align.toUpperCase(Locale.US));
		} catch (RuntimeException e) {
			// Invalid alignment
			return null;
		}
	}
}
