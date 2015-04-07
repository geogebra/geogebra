package org.geogebra.desktop.awt;

public class GPointD extends org.geogebra.common.awt.GPoint {

	public GPointD(double x, double y) {
		super((int) x, (int) y);
	}

	public GPointD() {
		super();
	}

	public java.awt.Point getAwtPoint() {
		return new java.awt.Point(x, y);
	}

}
