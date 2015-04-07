/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoCommonTangents.java, dsun48 [6/26/2011]
 *
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
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Two tangents through point P to conic section c
 */
public class AlgoCommonTangents extends AlgoCommonTangentsND {

	public AlgoCommonTangents(Construction cons, String[] labels, GeoConicND c,
			GeoConicND c2) {
		super(cons, labels, c, c2);
	}

	@Override
	protected void createPoints(Construction cons) {
		P = new GeoPoint[2];
		P[0] = new GeoPoint(cons);
		P[1] = new GeoPoint(cons);
	}

	@Override
	protected void setCoordsAsPoint(int index, double x, double y) {
		((GeoPoint) P[index]).setCoords(x, y, 1);
	}

	@Override
	protected void setCoordsAsVector(int index, double x, double y) {
		((GeoPoint) P[index]).setCoords(x, y, 0);
	}

	@Override
	protected void initTangents() {

		tangents = new GeoLine[2 + 2];
		tangents[0] = new GeoLine(cons);
		tangents[1] = new GeoLine(cons);
		((GeoLine) tangents[0]).setStartPoint((GeoPoint) P[0]);
		((GeoLine) tangents[1]).setStartPoint((GeoPoint) P[0]);

		tangents[0 + 2] = new GeoLine(cons);
		tangents[1 + 2] = new GeoLine(cons);
		((GeoLine) tangents[0 + 2]).setStartPoint((GeoPoint) P[1]);
		((GeoLine) tangents[1 + 2]).setStartPoint((GeoPoint) P[1]);

	}

	@Override
	protected AlgoIntersectND createAlgo(Construction cons, GeoPointND p,
			GeoLine line, GeoConicND conic) {
		conic.polarLine((GeoPoint) p, line);
		return new AlgoIntersectLineConic(cons, line, (GeoConic) conic);
	}

	/**
	 * Inits the helping interesection algorithm to take the current position of
	 * the lines into account. This is important so the the tangent lines are
	 * not switched after loading a file
	 */
	@Override
	public void initForNearToRelationship() {
		AlgoTangentPoint.initForNearToRelationship(tangentPoints, tangents[0],
				algoIntersect);
		AlgoTangentPoint.initForNearToRelationship(tangentPoints2, tangents[2],
				algoIntersect2);
	}

	@Override
	protected void updatePolarLines() {
		c[0].polarLine((GeoPoint) P[0], polar);
		c[1].polarLine((GeoPoint) P[1], polar2);
	}

	@Override
	protected boolean isIntersectionPointIncident(int index, GeoConicND conic) {
		return conic.isIntersectionPointIncident((GeoPoint) P[index],
				Kernel.MIN_PRECISION);
	}

	@Override
	protected void updateTangents(GeoPointND[] tangentPoints, int index) {
		// calc tangents through tangentPoints
		GeoVec3D.lineThroughPoints((GeoPoint) P[index],
				(GeoPoint) tangentPoints[0], (GeoLine) tangents[0 + 2 * index]);
		GeoVec3D.lineThroughPoints((GeoPoint) P[index],
				(GeoPoint) tangentPoints[1], (GeoLine) tangents[1 + 2 * index]);
	}

	@Override
	protected void setTangentFromPolar(int i, GeoLine line) {
		((GeoLine) tangents[i]).setCoords(line);
	}

	@Override
	protected double getMidpointX(int csIndex, int mpIndex) {
		return c[mpIndex].b.getX();
	}

	@Override
	protected double getMidpointY(int csIndex, int mpIndex) {
		return c[mpIndex].b.getY();
	}

}

// Local Variables:
// indent-tabs-mode: nil
// c-basic-offset: 4
// tab-width: 4
// End:
// vim: set expandtab shiftwidth=4 softtabstop=4 tabstop=4
