package org.geogebra.common.awt;

/**
 * @author micro
 *
 */
public abstract class GColorN {


	public abstract int getRed();

	public abstract int getBlue();

	public abstract int getGreen();

	public abstract int getAlpha();

	@Override
	final public String toString() {
		return "rgba(" + getRed() + "," + getGreen() + "," + getBlue() + ","
				+ (getAlpha() / 255d) + ")";
	}


}
