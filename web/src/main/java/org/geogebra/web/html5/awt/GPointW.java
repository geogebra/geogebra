package org.geogebra.web.html5.awt;

public class GPointW extends org.geogebra.common.awt.GPoint {

	private org.geogebra.ggbjdk.java.awt.geom.Point impl;

	public GPointW() {
		impl = new org.geogebra.ggbjdk.java.awt.geom.Point();
	}

	public GPointW(int x, int y) {
		impl = new org.geogebra.ggbjdk.java.awt.geom.Point(x, y);
	}

	public int getX() {
		return (int) impl.getX();
	}

	public int getY() {
		return (int) impl.getY();
	}

	public void setX(double x) {
		impl.setLocation(x, getY());
	}

	public void setY(double y) {
		impl.setLocation(getX(), y);
	}

	public double distance(org.geogebra.common.awt.GPoint q) {
		return impl.distance(q.getX(), q.getY());
	}

	public double distance(double x, double y) {
		return impl.distance(x, y);
	}
}
