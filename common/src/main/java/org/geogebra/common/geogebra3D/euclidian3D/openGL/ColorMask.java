package org.geogebra.common.geogebra3D.euclidian3D.openGL;

/**
 * Class for describing color masks
 *
 */
final public class ColorMask {
	/** all color channels off */
	static final public int NONE = 0;
	/** all color channels on */
	static final public int ALL = 1;
	/** red color channel on (+ alpha) */
	static final public int RED = 2;
	/** blue color channel on (+ alpha) */
	static final public int BLUE = 3;
	/** blue and green color channels on (+ alpha) */
	static final public int BLUE_AND_GREEN = 4;
	/** alpha channel on (for web) */
	static final public int ALPHA = 5;
	/** color mask alternative states length (except web specific type) */
	static final public int LENGTH_OTHER_THAN_WEB = 5;

	/**
	 * 
	 * @param colorMask
	 *            color mask
	 * @return true if this color mask turns on red channel
	 */
	static public boolean getRed(final int colorMask) {
		switch (colorMask) {
		case NONE:
		case ALPHA:
		case BLUE:
		case BLUE_AND_GREEN:
			return false;
		default:
			return true;
		}
	}

	/**
	 * 
	 * @param colorMask
	 *            color mask
	 * @return true if this color mask turns on green channel
	 */
	static public boolean getGreen(final int colorMask) {
		switch (colorMask) {
		case NONE:
		case ALPHA:
		case RED:
		case BLUE:
			return false;
		default:
			return true;
		}
	}

	/**
	 * 
	 * @param colorMask
	 *            color mask
	 * @return true if this color mask turns on blue channel
	 */
	static public boolean getBlue(final int colorMask) {
		switch (colorMask) {
		case NONE:
		case ALPHA:
		case RED:
			return false;
		default:
			return true;
		}
	}

	/**
	 * 
	 * @param colorMask
	 *            color mask
	 * @return true if this color mask turns on alpha channel
	 */
	static public boolean getAlpha(final int colorMask) {
		return colorMask != NONE;
	}
}