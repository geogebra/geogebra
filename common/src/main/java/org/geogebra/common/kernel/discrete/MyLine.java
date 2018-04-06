package org.geogebra.common.kernel.discrete;

import org.geogebra.common.awt.GPoint2D;

public class MyLine {
	public GPoint2D p1;
	public GPoint2D p2;

	public MyLine(GPoint2D p1, GPoint2D p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public double lengthSquared() {
		return GPoint2D.distanceSq(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

}
