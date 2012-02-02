/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel;

/**
 * @author Markus Hohenwarter
 */
public class PathParameter {
	
	public double t;
	private int pathType = -1;
	
	private boolean isDefined; //tells if parameters have been fed with "real" numbers

	
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
		if (Double.isNaN(t)){
			isDefined=false;
			return;
		}
		
		this.t = t;
		isDefined=true;
	}
	
	
}
