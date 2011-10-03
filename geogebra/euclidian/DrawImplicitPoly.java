/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawImplicitPoly.java
 *
 * Created on 03. June 2010, 12:21
 */
package geogebra.euclidian;

import geogebra.kernel.implicit.GeoImplicitPoly;

import java.awt.geom.Area;

/**
 * Draw GeoImplicitPoly on euclidian view
 */
public class DrawImplicitPoly extends DrawLocus {
	
	private GeoImplicitPoly implicitPoly;
	private int fillSign; //0=>no filling, only curve -1=>fill the negativ part, 1=>fill positiv part
	
	public DrawImplicitPoly(EuclidianView view,GeoImplicitPoly implicitPoly) {
		super(view,implicitPoly.locus);
		this.view=view;
    	hitThreshold = view.getCapturingThreshold();
		this.implicitPoly = implicitPoly;
		this.geo=implicitPoly;
		update();
	}
	
	public Area getShape(){
		return new Area();
	}
	/**
	 * Returns the poly to be draw
	 * (might not be equal to geo, if this is part of bigger geo)
	 * @return poly
	 */
	public GeoImplicitPoly getPoly() {
		return implicitPoly;
	}

}
