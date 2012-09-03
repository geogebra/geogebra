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
package geogebra.common.euclidian.draw;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.implicit.GeoImplicitPoly;

/**
 * Draw GeoImplicitPoly on euclidian view
 */
public class DrawImplicitPoly extends DrawLocus {
	
	private GeoImplicitPoly implicitPoly;
	// private int fillSign; //0=>no filling, only curve -1=>fill the negativ part, 1=>fill positiv part
	
	/**
	 * Creates new drawable for implicit polynomial
	 * @param view view
	 * @param implicitPoly implicit polynomial
	 */
	public DrawImplicitPoly(EuclidianView view,GeoImplicitPoly implicitPoly) {
		super(view, implicitPoly.getLocus());
		this.view=view;
    	hitThreshold = view.getCapturingThreshold();
		this.implicitPoly = implicitPoly;
		this.geo=implicitPoly;
		update();
	}
	
	@Override
	public geogebra.common.awt.GArea getShape(){
		return geogebra.common.factories.AwtFactory.prototype.newArea();
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
