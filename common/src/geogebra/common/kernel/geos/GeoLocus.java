/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.MyPoint;

/**
 * Locus of points
 * @author Markus
 */
public class GeoLocus extends GeoLocusND<MyPoint> {


	/**
	 * Creates new locus
	 * @param c construction
	 */
	public GeoLocus(Construction c) {
		super(c);
	}

	
	@Override
	protected GeoLocus newGeoLocus(){
		return new GeoLocus(cons);
	}


	/**
	 * Adds a new point (x,y) to the end of the point list of this locus.
	 * 
	 * @param x x-coord
	 * @param y y-coord
	 * @param lineTo
	 *            true to draw a line to (x,y); false to only move to (x,y)
	 */
	public void insertPoint(double x, double y, boolean lineTo) {
		myPointList.add(new MyPoint(x, y, lineTo));
	}








}
