/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.geogebra3D.kernel3D.MyPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Locus of points
 * 
 * @author Markus
 */
public class GeoLocus3D extends GeoLocusND<MyPoint3D> {
	private GeoPointND changingPoint;
	private Coords changingCoords;

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

	@Override
	public void insertPoint(double x, double y, double z, boolean lineTo) {
		myPointList.add(new MyPoint3D(x, y, z, lineTo ? SegmentType.LINE_TO
				: SegmentType.MOVE_TO));
	}

	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	@Override
	public boolean hasFillType() {
		return false;
	}

	@Override
	public void pointChanged(GeoPointND P) {

		Coords coords = P.getInhomCoordsInD3();
		setChangingPoint(P);

		// this updates closestPointParameter and closestPointIndex
		MyPoint closestPoint = getClosestPoint();

		PathParameter pp = P.getPathParameter();
		if (closestPoint != null) {
			coords.setX(closestPoint.x); // (1 - closestPointParameter) *
										// locusPoint.x +
			// closestPointParameter * locusPoint2.x;
			coords.setY(closestPoint.y); // (1 - closestPointParameter) *
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
	public ValueType getValueType() {
		return ValueType.PARAMETRIC3D;
	}
}
