package org.geogebra.common.awt;

/**
 * class to wrap native Color objects in eg GColorD wraps java.awt.Color
 *
 */
public abstract class GColorN {


	/**
	 * @return red (0-255)
	 */
	public abstract int getRed();

	/**
	 * @return blue (0-255)
	 */
	public abstract int getBlue();

	/**
	 * @return green (0-255)
	 */
	public abstract int getGreen();

	/**
	 * @return alpha (0-255)
	 */
	public abstract int getAlpha();

	@Override
	final public String toString() {
		return "rgba(" + getRed() + "," + getGreen() + "," + getBlue() + ","
				+ (getAlpha() / 255d) + ")";
	}


}
