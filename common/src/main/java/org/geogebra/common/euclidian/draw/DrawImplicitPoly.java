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
package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.implicit.GeoImplicit;

/**
 * Draw GeoImplicitPoly on euclidian view
 */
public class DrawImplicitPoly extends DrawLocus {

	private GeoImplicit implicitPoly;

	// private int fillSign; //0=>no filling, only curve -1=>fill the negativ
	// part, 1=>fill positiv part

	/**
	 * Creates new drawable for implicit polynomial
	 * 
	 * @param view
	 *            view
	 * @param implicitPoly
	 *            implicit polynomial
	 */
	public DrawImplicitPoly(EuclidianView view, GeoImplicit implicitPoly) {
		super(view, implicitPoly.getLocus(), CoordSys.XOY);
		this.view = view;
		this.implicitPoly = implicitPoly;
		this.geo = implicitPoly.toGeoElement();
		update();
	}

	@Override
	public GArea getShape() {
		return AwtFactory.getPrototype().newArea();
	}

	/**
	 * Returns the poly to be draw (might not be equal to geo, if this is part
	 * of bigger geo)
	 * 
	 * @return poly
	 */
	public GeoImplicit getPoly() {
		return implicitPoly;
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		if (implicitPoly.getDeg() == 0) {
			return false;
		}
		return super.hit(x, y, hitThreshold);
	}

}
