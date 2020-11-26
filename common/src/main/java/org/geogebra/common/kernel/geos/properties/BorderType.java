package org.geogebra.common.kernel.geos.properties;

import java.util.Locale;

public enum BorderType {
	ALL, INNER, OUTER, NONE, MIXED;

	@Override
	public String toString() {
		return name().toLowerCase(Locale.US);
	}

	/**
	 * @param borderType
	 *            string from carota
	 * @return border type value
	 */
	public static BorderType fromString(String borderType) {
		try {
			return valueOf(borderType.toUpperCase(Locale.US));
		} catch (RuntimeException e) {
			// Invalid alignment
			return null;
		}
	}
}
