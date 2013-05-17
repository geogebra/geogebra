package geogebra.html5.awt;

public class GDimensionW extends geogebra.common.awt.GDimension {

	private geogebra.web.openjdk.awt.geom.Dimension impl;
	
	public GDimensionW(geogebra.web.openjdk.awt.geom.Dimension dim) {
		impl = dim;
	}
	
	public GDimensionW(int w, int h) {
		impl = new geogebra.web.openjdk.awt.geom.Dimension(w, h);
	}
	@Override
	public int getWidth() {
		return (int)impl.getWidth();
	}

	@Override
	public int getHeight() {
		return (int)impl.getHeight();
	}
	
	public void setWidth(int w) {
		impl.width = w;
	}
	
	public void setHeight(int h) {
		impl.height = h;
	}

}
