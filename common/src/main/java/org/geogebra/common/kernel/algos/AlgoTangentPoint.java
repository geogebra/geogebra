/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.AlgoIntersectND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Two tangents through point P to conic section c
 */
public class AlgoTangentPoint extends AlgoTangentPointND {

	public AlgoTangentPoint(Construction cons, String[] labels, GeoPointND P,
			GeoConicND c) {
		super(cons, labels, P, c);
	}

	@Override
	protected boolean isIntersectionPointIncident() {
		return c.isIntersectionPointIncident((GeoPoint) P, Kernel.MIN_PRECISION)
				|| P.getIncidenceList().contains(c);
	}

	@Override
	protected void setPolar() {
		// the tangents are computed by intersecting the
		// polar line of P with c
		polar = new GeoLine(cons);
		c.polarLine((GeoPoint) P, polar);
		algoIntersect = new AlgoIntersectLineConic(cons, polar, (GeoConic) c);
		// this is only an internal Algorithm that shouldn't be in the
		// construction list
		cons.removeFromConstructionList(algoIntersect);
		tangentPoints = algoIntersect.getIntersectionPoints();
	}

	@Override
	protected void setTangentFromPolar(int i) {
		((GeoLine) tangents[i]).setCoords(polar);
	}

	@Override
	protected void setTangents() {
		tangents = new GeoLine[2];
		tangents[0] = new GeoLine(cons);
		tangents[1] = new GeoLine(cons);
		((GeoLine) tangents[0]).setStartPoint((GeoPoint) P);
		((GeoLine) tangents[1]).setStartPoint((GeoPoint) P);
	}

	// Made public for LocusEqu
	public GeoPoint getPoint() {
		return (GeoPoint) P;
	}

	// Made public for LocusEqu
	public GeoConic getConic() {
		return (GeoConic) c;
	}

	/**
	 * Inits the helping interesection algorithm to take the current position of
	 * the lines into account. This is important so the the tangent lines are
	 * not switched after loading a file
	 */
	@Override
	public void initForNearToRelationship() {
		// if first tangent point is not on first tangent,
		// we switch the intersection points

		initForNearToRelationship(tangentPoints, tangents[0], algoIntersect);
	}

	/**
	 * Inits the helping interesection algorithm to take the current position of
	 * the lines into account. This is important so the the tangent lines are
	 * not switched after loading a file
	 *
	 * @param tangentPoints
	 *            tangent points
	 * @param tangent
	 *            tangent line
	 * @param algoIntersect
	 *            algo used
	 */
	static public void initForNearToRelationship(GeoPointND[] tangentPoints,
			GeoLineND tangent, AlgoIntersectND algoIntersect) {
		// if first tangent point is not on first tangent,
		// we switch the intersection points

		GeoPoint firstTangentPoint = (GeoPoint) tangentPoints[0];

		if (!((GeoLine) tangent).isOnFullLine(firstTangentPoint,
				Kernel.MIN_PRECISION)) {
			algoIntersect.initForNearToRelationship();

			// remember first point
			double px = firstTangentPoint.x;
			double py = firstTangentPoint.y;
			double pz = firstTangentPoint.z;

			// first = second
			algoIntersect.setIntersectionPoint(0, tangentPoints[1]);

			// second = first
			tangentPoints[1].setCoords(px, py, pz);
			algoIntersect.setIntersectionPoint(1, tangentPoints[1]);
		}
	}

	@Override
	protected void updatePolarLine() {
		c.polarLine((GeoPoint) P, polar);
	}

	@Override
	protected void updateTangents() {
		// calc tangents through tangentPoints
		GeoVec3D.lineThroughPoints((GeoPoint) P, (GeoPoint) tangentPoints[0],
				(GeoLine) tangents[0]);
		GeoVec3D.lineThroughPoints((GeoPoint) P, (GeoPoint) tangentPoints[1],
				(GeoLine) tangents[1]);
	}

}
