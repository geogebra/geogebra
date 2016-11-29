package org.geogebra.common.awt;

public abstract class GColorN {


	public abstract int getRed();

	public abstract int getBlue();

	public abstract int getGreen();

	public abstract int getAlpha();

	@Override
	final public String toString() {
		return getColorString(this);
	}

	public static String getColorString(GColorN fillColor) {
		return "rgba(" + fillColor.getRed() + "," + fillColor.getGreen() + ","
				+ fillColor.getBlue() + "," + (fillColor.getAlpha() / 255d)
				+ ")";
	}

}
