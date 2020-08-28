package org.geogebra.desktop.awt;

import java.awt.Dimension;

import org.geogebra.common.awt.GDimension;

public class GDimensionD extends GDimension {
	private Dimension impl;

	public GDimensionD(Dimension dim) {
		impl = dim;
	}

	public GDimensionD(int a, int b) {
		impl = new Dimension(a, b);
	}

	public GDimensionD() {
		impl = new Dimension();
	}

	@Override
	public int getWidth() {
		return impl.width;
	}

	@Override
	public int getHeight() {
		return impl.height;
	}

	/**
	 * @param d
	 *            dimension, must be of the type geogebra.awt.Dimension
	 * @return AWT implementation wrapped in d
	 */
	public static Dimension getAWTDimension(GDimension d) {

		if (!(d instanceof GDimensionD)) {
			return null;
		}

		return ((GDimensionD) d).impl;
	}

	@Override
	public final boolean equals(Object e) {
		if (e instanceof GDimension) {
			return getWidth() == ((GDimension) e).getWidth()
					&& getHeight() == ((GDimension) e).getHeight();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getHeight() + 37 * getWidth();
	}

}
