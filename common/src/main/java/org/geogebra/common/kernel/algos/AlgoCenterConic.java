/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoMidpointConic.java
 *
 * Created on 11. November 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Center of a conic section.
 */
public class AlgoCenterConic extends AlgoCenterQuadricND {

	/**
	 * Constructor
	 * 
	 * @param cons
	 * @param label
	 * @param c
	 */
	public AlgoCenterConic(Construction cons, String label, GeoConicND c) {
		super(cons, label, c);
	}

	@Override
	public GeoPointND newGeoPoint(Construction cons) {
		return new GeoPoint(cons);
	}

	@Override
	public GeoPointND getPoint() {
		return midpoint;
	}

	@Override
	public void setCoords() {

		switch (c.type) {
		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:
		case GeoConicNDConstants.CONIC_HYPERBOLA:
		case GeoConicNDConstants.CONIC_SINGLE_POINT:
		case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
			GeoVec2D b = ((GeoConicND) c).b;
			setCoords(b.getX(), b.getY());
			break;

		default:
			// midpoint undefined
			midpoint.setUndefined();
		}
	}

	/**
	 * set the coords of the midpoint
	 * 
	 * @param x
	 * @param y
	 */
	protected void setCoords(double x, double y) {
		midpoint.setCoords(x, y, 1.0d);
	}

}
