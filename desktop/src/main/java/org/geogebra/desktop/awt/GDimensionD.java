package org.geogebra.desktop.awt;

public class GDimensionD extends org.geogebra.common.awt.GDimension {
	private java.awt.Dimension impl;

	public GDimensionD(java.awt.Dimension dim) {
		impl = dim;
	}

	public GDimensionD(int a, int b) {
		impl = new java.awt.Dimension(a, b);
	}

	public GDimensionD() {
		impl = new java.awt.Dimension();
	}

	@Override
	public int getWidth() {
		return impl.width;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return impl.height;
	}

	/**
	 * @param d
	 *            dimension, must be of the type geogebra.awt.Dimension
	 * @return AWT implementation wrapped in d
	 */
	public static java.awt.Dimension getAWTDimension(
			org.geogebra.common.awt.GDimension d) {
		if (!(d instanceof GDimensionD))
			return null;
		return ((GDimensionD) d).impl;
	}

}
