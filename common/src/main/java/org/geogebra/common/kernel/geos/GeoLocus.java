/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

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



	public void pointChanged(GeoPointND P) {
		
		Coords coords = P.getCoordsInD2().getInhomCoordsInSameDimension();
		setChangingPoint(P);
		 
		// this updates closestPointParameter and closestPointIndex
		MyPoint closestPoint = getClosestPoint();

		PathParameter pp = P.getPathParameter();
		// Application.debug(pp.t);
		if (closestPoint != null) {
			coords.setX(closestPoint.x);// (1 - closestPointParameter) * locusPoint.x +
									// closestPointParameter * locusPoint2.x;
			coords.setY(closestPoint.y);// (1 - closestPointParameter) * locusPoint.y +
									// closestPointParameter * locusPoint2.y;
			coords.setZ(1.0);
			pp.t = closestPointIndex + closestPointParameter;
		}
		
		 P.setCoords2D(coords.getX(), coords.getY(), coords.getZ());
		 P.updateCoordsFrom2D(false);
		 P.updateCoords();
	}


	@Override
	protected GeoSegmentND newGeoSegment(){
		GeoSegment segment = new GeoSegment(cons);
		GeoPoint p1 = new GeoPoint(cons);
		GeoPoint p2 = new GeoPoint(cons);
		segment.setStartPoint(p1);
		segment.setEndPoint(p2);
		
		return segment;
	}
	
	private Coords changingPoint;
	
	@Override
	protected void setChangingPoint(GeoPointND P){
		changingPoint = P.getCoordsInD2().getInhomCoordsInSameDimension();
	}
	
	@Override
	protected double changingPointDistance(GeoSegmentND segment){
		return ((GeoSegment) segment).distance(changingPoint.getX(), changingPoint.getY());
	}

	@Override
	protected double getChangingPointParameter(GeoSegmentND segment){
		return ((GeoSegment) segment).getParameter(changingPoint.getX(), changingPoint.getY());
	}
	
	@Override
	public boolean hasLineOpacity() {
		return true;
	}
	
	@Override
	final public HitType getLastHitType(){
		return HitType.ON_BOUNDARY;
	}

}
