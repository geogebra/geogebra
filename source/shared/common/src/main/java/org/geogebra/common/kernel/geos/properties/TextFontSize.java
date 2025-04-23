package org.geogebra.common.kernel.geos.properties;

/**
 * Relative font sizes.
 */
public enum TextFontSize {
	EXTRA_SMALL("ExtraSmall"),
	VERY_SMALL("VerySmall"),
	SMALL("Small"),
	MEDIUM("Medium"),
	LARGE("Large"),
	VERY_LARGE("VeryLarge"),
	EXTRA_LARGE("ExtraLarge");

	private final String name;

	TextFontSize(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
