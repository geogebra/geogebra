package org.geogebra.desktop.awt;

import org.geogebra.common.awt.GPoint;

public class GPointD extends GPoint {

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
