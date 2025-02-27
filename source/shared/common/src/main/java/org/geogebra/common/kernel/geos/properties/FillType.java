package org.geogebra.common.kernel.geos.properties;

/**
 * Fill types of elements
 * 
 * need to be in menu order here
 * 
 * the order here gives the integer for the XML so new ones MUST be added at the
 * end
 * 
 * @author Giulliano Bellucci
 */
public enum FillType {

	/**
	 * Simple fill (color+opacity)
	 * 
	 */
	STANDARD(false),
	/**
	 * Hatched fill
	 */
	HATCH(true),
	/**
	 * Crosshatched fill
	 */
	CROSSHATCHED(true),
	/**
	 * Chessboard fill, upright or diagonal
	 */
	CHESSBOARD(true),
	/**
	 * Dotted fill
	 */
	DOTTED(true),
	/**
	 * Honeycomb fill
	 */
	HONEYCOMB(true),
	/**
	 * Brick fill
	 */
	BRICK(true),
	/**
	 * Weaving fill
	 */
	WEAVING(true),
	/**
	 * Unicode symbols fill
	 */
	SYMBOLS(true),
	/**
	 * Image background
	 */
	IMAGE(false);

	private final boolean hatch;

	FillType(boolean hatch) {
		this.hatch = hatch;
	}

	/**
	 * @return whether this is hatch or something else (image, standard)
	 */
	public boolean isHatch() {
		return hatch;
	}
}