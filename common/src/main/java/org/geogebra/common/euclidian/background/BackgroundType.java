package org.geogebra.common.euclidian.background;

/**
 * Types of MOW backgrounds.
 * *
 * 
 * @author laszlso
 *
 */
public enum BackgroundType {
	/** no ruler as background */
	NONE(0),

	/** horizontal lines */
	RULER(1),

	/** small squares */
	SQUARE_SMALL(2),

	/** big squares */
	SQUARE_BIG(3),

	/** Elementary 1/2 background */
	ELEMENTARY12(5),

	/** Elementary 1/2 with house background */
	ELEMENTARY12_HOUSE(6),

	/** Elementary 3/4 background */
	ELEMENTARY34(7),

	/** Music background */
	MUSIC(8),

	/** General SVG as background */
	SVG(9);

	private int value;

	BackgroundType(int value) {
		this.value = value;
	}

	/**
	 * @return the value of the enum.
	 */
	public int value() {
		return value;
	}

	/**
	 * Checks that the given enum value refers to an SVG file.
	 * 
	 * @param bg
	 *            enum to check.
	 * @return if it is SVG or not.
	 */
	public static boolean isSVG(BackgroundType bg) {
		return bg.value() >= ELEMENTARY12.value();
	}

	/**
	 * Converts int to enum
	 * 
	 * @param v
	 *            the int value to convert to.
	 * @return the converted enum.
	 */
	public static BackgroundType fromInt(int v) {
		for (BackgroundType bt : values()) {
			if (bt.value == v) {
				return bt;
			}
		}
		return BackgroundType.NONE;
	}

}
