package geogebra.web.awt;

import geogebra.common.main.AbstractApplication;

public class Point extends geogebra.common.awt.Point {
	
	private geogebra.web.openjdk.awt.geom.Point impl;

	public Point() {
		impl = new geogebra.web.openjdk.awt.geom.Point();
	}
	
	public Point(int x, int y) {
		impl = new geogebra.web.openjdk.awt.geom.Point(x, y);
	}
	
	
	public int getX() {
		return (int)impl.getX();
	}

	
	public int getY() {
		return (int)impl.getY();
	}

	
	public void setX(double x) {
		impl.setLocation(x, getY());
	}

	
	public void setY(double y) {
		impl.setLocation(getX(), y);
	}

	
	public double distance(geogebra.common.awt.Point q) {
		return impl.distance(q.getX(), q.getY());
	}

	
    public double distance(double x, double y) {
    	return impl.distance(x, y);
    }
}
