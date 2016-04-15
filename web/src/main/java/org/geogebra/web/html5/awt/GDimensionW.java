package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GDimension;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;

public class GDimensionW extends GDimension {

	private Dimension impl;

	public GDimensionW(Dimension dim) {
		impl = dim;
	}

	public GDimensionW(int w, int h) {
		impl = new Dimension(w, h);
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
