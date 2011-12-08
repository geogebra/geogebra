/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.util.MyMath;



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
		return AbstractKernel.isEqual(x, px, AbstractKernel.MIN_PRECISION) &&
			   AbstractKernel.isEqual(y, py, AbstractKernel.MIN_PRECISION);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public double distance(MyPoint p) {
		return MyMath.length(p.x - x, p.y - y);
	}

	public GeoPoint2 getGeoPoint(Construction cons) {
		return new GeoPoint2(cons, null, x, y, 1.0);
	}
}
