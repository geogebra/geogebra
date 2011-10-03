/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;



public class MyPoint {
	public double x, y;
	public boolean lineTo;

	public MyPoint(double x, double y, boolean lineTo) {
		this.x = x;
		this.y = y;
		this.lineTo = lineTo;
	}
	
	public double distSqr(double px, double py) {
		double vx = px - x;
        double vy = py - y;        
        return vx*vx + vy*vy;
	}
	
	public boolean isEqual(double px, double py) {
		return Kernel.isEqual(x, px, Kernel.MIN_PRECISION) &&
			   Kernel.isEqual(y, py, Kernel.MIN_PRECISION);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public double distance(MyPoint p) {
		return GeoVec2D.length(p.x - x, p.y - y);
	}

	public GeoPoint getGeoPoint(Construction cons) {
		return new GeoPoint(cons, null, x, y, 1.0);
	}
}
