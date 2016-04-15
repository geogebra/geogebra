package org.geogebra.desktop.awt;

import java.awt.Point;

import org.geogebra.common.awt.GPoint;

public class GPointD extends GPoint {

	public GPointD(double x, double y) {
		super((int) x, (int) y);
	}

	public GPointD() {
		super();
	}

	public Point getAwtPoint() {
		return new Point(x, y);
	}

}
