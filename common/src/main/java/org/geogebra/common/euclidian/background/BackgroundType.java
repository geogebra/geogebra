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

	/** SVG as background */
	SVG(4);
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

}
