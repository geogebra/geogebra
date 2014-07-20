/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.geogebra3D.kernel3D.geos;

import geogebra.common.geogebra3D.kernel3D.MyPoint3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoLocusND;

/**
 * Locus of points
 * @author Markus
 */
public class GeoLocus3D extends GeoLocusND<MyPoint3D> {


	/**
	 * Creates new locus
	 * @param c construction
	 */
	public GeoLocus3D(Construction c) {
		super(c);
	}

	
	@Override
	protected GeoLocus3D newGeoLocus(){
		return new GeoLocus3D(cons);
	}


	/**
	 * Adds a new point (x,y,z) to the end of the point list of this locus.
	 * 
	 * @param x x-coord
	 * @param y y-coord
	 * @param z z-coord
	 * @param lineTo
	 *            true to draw a line to (x,y,z); false to only move to (x,y,z)
	 */
	public void insertPoint(double x, double y, double z, boolean lineTo) {
		myPointList.add(new MyPoint3D(x, y, z, lineTo));
	}







}
