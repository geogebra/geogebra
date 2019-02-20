package org.geogebra.common.kernel.geos.properties;

/**
 * Fill types of elements
 * 
 * @author Giulliano Bellucci
 */
public enum FillType {

	/**
	 * Simple fill (color+opacity)
	 * 
	 * need to be in menu order here
	 * 
	 * the integer is used in the XML so can't be changed
	 */
	STANDARD(0, false),
	/**
	 * Hatched fill
	 */
	HATCH(1, true),
	/**
	 * Crosshatched fill
	 */
	CROSSHATCHED(2, true),
	/**
	 * Chessboard fill, upright or diagonal
	 */
	CHESSBOARD(3, true),
	/**
	 * Dotted fill
	 */
	DOTTED(4, true),
	/**
	 * Honeycomb fill
	 */
	HONEYCOMB(5, true),
	/**
	 * Brick fill
	 */
	BRICK(6, true),
	/**
	 * Weaving fill
	 */
	WEAVING(9, true),
	/**
	 * Unicode symbols fill
	 */
	SYMBOLS(7, true),
	/**
	 * Image background
	 */
	IMAGE(8, false);

	private int value;
	private boolean hatch;

	/**
	 * @return value for XML
	 */
	public int getValue() {
		return value;
	}

	private FillType(int value, boolean hatch) {
		this.value = value;
		this.hatch = hatch;
	}

	/**
	 * @return whether this is hatch or something else (image, standard)
	 */
	public boolean isHatch() {
		return hatch;
	}
}