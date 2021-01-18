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
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.advanced.AlgoSlopeField;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Locus of points
 * 
 * @author Markus
 */
public class GeoLocus extends GeoLocusND<MyPoint> {
	private Coords changingPoint;
	private boolean drawArrows = false;

	/**
	 * Creates new locus
	 * @param c construction
	 */
	public GeoLocus(Construction c) {
		super(c);
	}

	@Override
	protected GeoLocus newGeoLocus() {
		return new GeoLocus(cons);
	}

	/**
	 * Adds a new point (x,y) to the end of the point list of this locus.
	 * @param x x-coord
	 * @param y y-coord
	 * @param segmentType used segment type
	 */
	public void insertPoint(double x, double y, SegmentType segmentType) {
		myPointList.add(new MyPoint(x, y, segmentType));
	}

	/**
	 * @param coords changed point
	 * @param pp path parameter
	 */
	public void pointChanged(Coords coords, PathParameter pp) {
		changingPoint = coords;
		// this updates closestPointParameter and closestPointIndex
		MyPoint closestPoint = getClosestPoint();

		// Application.debug(pp.t);
		if (closestPoint != null) {
			coords.setX(closestPoint.x); // (1 - closestPointParameter) *
			// locusPoint.x +
			// closestPointParameter * locusPoint2.x;
			coords.setY(closestPoint.y); // (1 - closestPointParameter) *
			// locusPoint.y +
			// closestPointParameter * locusPoint2.y;
			coords.setZ(1.0);
			pp.t = closestPointIndex + closestPointParameter;
		}
	}

	@Override
	public void pointChanged(GeoPointND P) {

		Coords coords = P.getCoordsInD2().getInhomCoordsInSameDimension();
		pointChanged(coords, P.getPathParameter());

		P.setCoords2D(coords.getX(), coords.getY(), coords.getZ());
		P.updateCoordsFrom2D(false);
		P.updateCoords();
	}

	@Override
	protected GeoSegmentND newGeoSegment() {
		GeoSegment segment = new GeoSegment(cons);
		GeoPoint p1 = new GeoPoint(cons);
		GeoPoint p2 = new GeoPoint(cons);
		segment.setStartPoint(p1);
		segment.setEndPoint(p2);

		return segment;
	}

	@Override
	protected void setChangingPoint(GeoPointND P) {
		changingPoint = P.getCoordsInD2().getInhomCoordsInSameDimension();
	}

	@Override
	protected double changingPointDistance(GeoSegmentND segment) {
		return ((GeoSegment) segment).distance(changingPoint.getX(),
				changingPoint.getY());
	}

	@Override
	protected double getChangingPointParameter(GeoSegmentND segment) {
		return ((GeoSegment) segment).getParameter(changingPoint.getX(),
				changingPoint.getY());
	}

	@Override
	public boolean hasLineOpacity() {
		return true;
	}

	/**
	 * @return whether there are arrows drawn
	 */
	public boolean hasDrawArrows() {
		return getParentAlgorithm() instanceof AlgoSlopeField;
	}

	@Override
	public void setVisualStyle(GeoElement geo, boolean setAuxiliaryProperty) {
		super.setVisualStyle(geo, setAuxiliaryProperty);
		if (geo instanceof GeoLocus) {
			drawArrows = ((GeoLocus) geo).isDrawArrows();
			drawAsArrows(drawArrows);
		}
	}

	@Override
	public ValueType getValueType() {
		return ValueType.PARAMETRIC2D;
	}

	@Override
	public boolean isGeoLocusable() {
		return true;
	}

	/**
	 * @param checked whether checkbox is selected
	 */
	public void drawAsArrows(boolean checked) {
		if (getParentAlgorithm() instanceof AlgoSlopeField) {
			drawArrows = checked;
			getParentAlgorithm().euclidianViewUpdate();
		}
	}

	@Override
	public boolean isDrawArrows() {
		return drawArrows;
	}

}
