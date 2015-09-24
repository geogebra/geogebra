/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.geogebra3D.kernel3D.MyPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

/**
 * Locus of points
 * 
 * @author Markus
 */
public class GeoLocus3D extends GeoLocusND<MyPoint3D> {

	/**
	 * Creates new locus
	 * 
	 * @param c
	 *            construction
	 */
	public GeoLocus3D(Construction c) {
		super(c);
	}

	@Override
	protected GeoLocus3D newGeoLocus() {
		return new GeoLocus3D(cons);
	}

	/**
	 * Adds a new point (x,y,z) to the end of the point list of this locus.
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 * @param lineTo
	 *            true to draw a line to (x,y,z); false to only move to (x,y,z)
	 */
	public void insertPoint(double x, double y, double z, boolean lineTo) {
		myPointList.add(new MyPoint3D(x, y, z, lineTo));
	}

	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	public void pointChanged(GeoPointND P) {

		Coords coords = P.getInhomCoordsInD3();
		setChangingPoint(P);

		// this updates closestPointParameter and closestPointIndex
		MyPoint closestPoint = getClosestPoint();

		PathParameter pp = P.getPathParameter();
		// Application.debug(pp.t);
		if (closestPoint != null) {
			coords.setX(closestPoint.x);// (1 - closestPointParameter) *
										// locusPoint.x +
			// closestPointParameter * locusPoint2.x;
			coords.setY(closestPoint.y);// (1 - closestPointParameter) *
										// locusPoint.y +
			// closestPointParameter * locusPoint2.y;
			coords.setZ(closestPoint.getZ());
			coords.setW(1.0);
			pp.t = closestPointIndex + closestPointParameter;
		}

		P.setCoords(coords, false);
		P.updateCoords();
	}

	@Override
	protected GeoSegmentND newGeoSegment() {
		GeoSegment3D segment = new GeoSegment3D(cons);
		/*
		 * GeoPoint p1 = new GeoPoint(cons); GeoPoint p2 = new GeoPoint(cons);
		 * segment.setStartPoint(p1); segment.setEndPoint(p2);
		 */

		return segment;
	}

	private GeoPointND changingPoint;
	private Coords changingCoords;

	@Override
	protected void setChangingPoint(GeoPointND P) {
		changingPoint = P;
		changingCoords = P.getInhomCoordsInD3();
	}

	@Override
	protected double changingPointDistance(GeoSegmentND segment) {
		GeoSegment3D seg = (GeoSegment3D) segment;

		double t = seg.getParamOnLine(changingPoint);
		if (t < 0) {
			t = 0;
		} else if (t > 1) {
			t = 1;
		}

		Coords project = seg.getPoint(t);

		Coords coords = changingCoords;

		if (changingPoint.isGeoElement3D()) {
			if (((GeoPoint3D) changingPoint).hasWillingCoords()) {

				coords = ((GeoPoint3D) changingPoint).getWillingCoords();

				if (((GeoPoint3D) changingPoint).hasWillingDirection()) {
					return project.distLine(coords,
							((GeoPoint3D) changingPoint).getWillingDirection());
				}
			}
		}

		return coords.distance(project);
	}

	@Override
	protected double getChangingPointParameter(GeoSegmentND segment) {
		return ((GeoSegment3D) segment).getParamOnLine(changingPoint);
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_BOUNDARY;
	}

	public ValueType getValueType() {
		return ValueType.PARAMETRIC3D;
	}
}
