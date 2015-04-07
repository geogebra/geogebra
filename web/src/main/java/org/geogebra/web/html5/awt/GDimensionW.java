package org.geogebra.web.html5.awt;

public class GDimensionW extends org.geogebra.common.awt.GDimension {

	private org.geogebra.ggbjdk.java.awt.geom.Dimension impl;

	public GDimensionW(org.geogebra.ggbjdk.java.awt.geom.Dimension dim) {
		impl = dim;
	}

	public GDimensionW(int w, int h) {
		impl = new org.geogebra.ggbjdk.java.awt.geom.Dimension(w, h);
	}

	@Override
	public int getWidth() {
		return (int) impl.getWidth();
	}

	@Override
	public int getHeight() {
		return (int) impl.getHeight();
	}

	public void setWidth(int w) {
		impl.width = w;
	}

	public void setHeight(int h) {
		impl.height = h;
	}

}
