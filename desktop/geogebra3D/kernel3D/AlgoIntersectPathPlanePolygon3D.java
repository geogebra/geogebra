/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoIntersectLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.CoordMatrixUtil;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;

import java.util.TreeMap;

/**
 * Algo for intersection of a line with the interior of a polygon
 * 
 * @author matthieu
 */
public class AlgoIntersectPathPlanePolygon3D extends AlgoIntersectPathLinePolygon3D {

	protected GeoPlane3D plane;

	/**
	 * common constructor
	 * 
	 * @param c
	 * @param labels
	 * @param plane
	 * @param p
	 */
	public AlgoIntersectPathPlanePolygon3D(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolygon p) {

		super(c,labels,plane,p);

	}

	@Override
	protected void setFirstInput(GeoElement geo){
		this.plane = (GeoPlane3D) geo;
	}

	@Override
	protected GeoElement getFirstInput(){
		return plane;
	}

	@Override
	protected void addStartEndPoints(TreeMap<Double, Coords> newCoords){
		// no start/end points
	}
	
	
	@Override
	protected void setIntersectionLine(){

    	Coords[] intersection = CoordMatrixUtil.intersectPlanes(
    			plane.getCoordSys().getMatrixOrthonormal(),
    			p.getCoordSys().getMatrixOrthonormal());

		o1 = intersection[0];
		d1 = intersection[1];	
	}
	

    @Override
	protected boolean checkParameter(double t1){
    	return true; //nothing to check here
    }

}
