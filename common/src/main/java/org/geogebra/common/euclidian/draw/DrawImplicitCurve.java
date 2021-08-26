/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.implicit.GeoImplicit;

/**
 * Draw GeoImplicitCurve on euclidian view
 */
public class DrawImplicitCurve extends DrawLocus {

	private GeoImplicit implicitCurve;

	// private int fillSign; //0=>no filling, only curve -1=>fill the negativ
	// part, 1=>fill positiv part

	/**
	 * Creates new drawable for implicit Curvenomial
	 * 
	 * @param view
	 *            view
	 * @param implicitCurve
	 *            implicit Curvenomial
	 */
	public DrawImplicitCurve(EuclidianView view, GeoImplicit implicitCurve) {
		super(view, implicitCurve.getLocus(),
				implicitCurve.getTransformedCoordSys());
		this.view = view;
		this.implicitCurve = implicitCurve;
		this.geo = implicitCurve.toGeoElement();
		update();
	}

	@Override
	public GArea getShape() {
		return AwtFactory.getPrototype().newArea();
	}

	/**
	 * Returns the Curve to be draw (might not be equal to geo, if this is part
	 * of bigger geo)
	 * 
	 * @return Curve
	 */
	public GeoImplicit getCurve() {
		return implicitCurve;
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		if (!implicitCurve.isDefined()) {
			return false;
		}
		return super.hit(x, y, hitThreshold);
	}

}
