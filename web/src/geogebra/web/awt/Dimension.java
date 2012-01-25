package geogebra.web.awt;

public class Dimension extends geogebra.common.awt.Dimension {

	private geogebra.web.openjdk.awt.geom.Dimension impl;
	
	public Dimension(geogebra.web.openjdk.awt.geom.Dimension dim) {
		impl = dim;
	}
	
	public Dimension(int w, int h) {
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

}
