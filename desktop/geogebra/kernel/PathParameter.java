/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * @author Markus Hohenwarter
 */
public class PathParameter {
	
	double t;
	private int pathType = -1;
	
	public PathParameter() {
		t = Double.NaN;
	}
	
	public PathParameter(PathParameter pp){
		set(pp);
	}
	
	public PathParameter(double t) {
		this.t = t;
	}
	
	final public void set(PathParameter pp) {
		t = pp.t;
		pathType = pp.pathType;
	}

	public final int getPathType() {
		return pathType;
	}

	public final void setPathType(int pathType) {
		this.pathType = pathType;
	}

	public final double getT() {
		return t;
	}

	public final void setT(double t) {
		this.t = t;
	}
}
