package org.geogebra.common.kernel.geos.properties;

import java.util.Locale;

import org.geogebra.common.util.debug.Log;

public enum TextAlignment {
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
	public static TextAlignment fromString(String align) {
		try {
			return valueOf(align.toUpperCase(Locale.US));
		} catch (RuntimeException e) {
			Log.warn("Invalid alignment");
		}
		return TextAlignment.LEFT;
	}
}
