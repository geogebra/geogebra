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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoCommonTangentsND;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.AlgoIntersectND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Two tangents through point P to conic section c
 */
public class AlgoCommonTangents3D extends AlgoCommonTangentsND {

	public AlgoCommonTangents3D(Construction cons, String[] labels,
			GeoConicND c, GeoConicND c2) {
		super(cons, labels, c, c2);
	}

	@Override
	protected void createPoints(Construction cons) {
		P = new GeoPoint3D[2];
		P[0] = new GeoPoint3D(cons);
		P[1] = new GeoPoint3D(cons);

		coords2D = new Coords[2];
		coords2D[0] = new Coords(3);
		coords2D[1] = new Coords(3);

		midpointProjected = new Coords[2][];
		midpointProjected[0] = new Coords[2];
		midpointProjected[1] = new Coords[2];

	}

	@Override
	protected void setCoordsAsPoint(int index, double x, double y) {

		coords2D[index].setX(x);
		coords2D[index].setY(y);
		coords2D[index].setZ(1);

		((GeoPoint3D) P[index])
				.setCoords(c[index].getCoordSys().getPoint(x, y));
	}

	@Override
	protected void setCoordsAsVector(int index, double x, double y) {

		coords2D[index].setX(x);
		coords2D[index].setY(y);
		coords2D[index].setZ(0);

		((GeoPoint3D) P[index]).setCoords(c[index].getCoordSys()
				.getVector(x, y));

	}

	@Override
	protected void initTangents() {

		tangents = new GeoLine3D[2 + 2];
		tangents[0] = new GeoLine3D(cons);
		tangents[1] = new GeoLine3D(cons);
		((GeoLine3D) tangents[0]).setStartPoint(P[0]);
		((GeoLine3D) tangents[1]).setStartPoint(P[0]);

		tangents[0 + 2] = new GeoLine3D(cons);
		tangents[1 + 2] = new GeoLine3D(cons);
		((GeoLine3D) tangents[0 + 2]).setStartPoint(P[1]);
		((GeoLine3D) tangents[1 + 2]).setStartPoint(P[1]);

	}

	@Override
	protected AlgoIntersectND createAlgo(Construction cons, GeoPointND p,
			GeoLine line, GeoConicND conic) {
		return new AlgoIntersectLineIncludedConic3D(cons, line, conic);
	}

	/**
	 * Inits the helping interesection algorithm to take the current position of
	 * the lines into account. This is important so the the tangent lines are
	 * not switched after loading a file
	 */
	@Override
	public void initForNearToRelationship() {
		AlgoTangentPoint3D.initForNearToRelationship(tangentPoints,
				tangents[0], algoIntersect);
		AlgoTangentPoint3D.initForNearToRelationship(tangentPoints2,
				tangents[2], algoIntersect2);
	}

	@Override
	protected void updatePolarLines() {
		c[0].polarLine(coords2D[0], polar);
		c[1].polarLine(coords2D[1], polar2);
	}

	private Coords[] coords2D;

	@Override
	protected boolean isIntersectionPointIncident(int index, GeoConicND conic) {
		return conic.isIntersectionPointIncident(coords2D[index],
				Kernel.MIN_PRECISION);
	}

	@Override
	protected void updateTangents(GeoPointND[] tangentPoints, int index) {
		// calc tangents through tangentPoints
		if (!tangentPoints[0].isDefined()) {
			tangents[0 + 2 * index].setUndefined();
		} else {
			// if (P[index].isInfinite()){
			// App.error("\ntP:\n"+tangentPoints[0].getCoords()+"\nP:\n"+P[index].getCoords());
			// ((GeoLine3D)
			// tangents[0+2*index]).setCoord(tangentPoints[0].getCoords(),
			// P[index].getCoords());
			// }else{
			((GeoLine3D) tangents[0 + 2 * index]).setCoord(P[index],
					tangentPoints[0]);
			// }
		}
		if (!tangentPoints[1].isDefined()) {
			tangents[1 + 2 * index].setUndefined();
		} else {
			// if (P[index].isInfinite()){
			// App.error("\ntP:\n"+tangentPoints[1].getCoords()+"\nP:\n"+P[index].getCoords());
			// ((GeoLine3D)
			// tangents[1+2*index]).setCoord(tangentPoints[1].getCoords(),
			// P[index].getCoords());
			// }else{
			((GeoLine3D) tangents[1 + 2 * index]).setCoord(P[index],
					tangentPoints[1]);
			// }
		}
	}

	private double[] polarCoords = new double[3];

	private Coords polarOrigin, polarDirection;

	@Override
	protected void setTangentFromPolar(int i, GeoLine line) {

		if (i == 0 || i == 2) { // for second tangent, calculations are already
								// done
			CoordSys cs = c[i / 2].getCoordSys();
			line.getCoords(polarCoords);
			polarDirection = cs.getVector(-polarCoords[1], polarCoords[0]);
			if (Kernel.isZero(polarCoords[0])) {
				polarOrigin = cs.getPoint(0, -polarCoords[2] / polarCoords[1]);
			} else {
				polarOrigin = cs.getPoint(-polarCoords[2] / polarCoords[0], 0);
			}
		}

		((GeoLine3D) tangents[i]).setCoord(polarOrigin, polarDirection);

	}

	private Coords[][] midpointProjected;

	@Override
	protected boolean checkUndefined() {
		if (super.checkUndefined()) {
			return true;
		}

		// check normals are lin. dep.
		Coords n0 = c[0].getMainDirection();
		Coords n1 = c[1].getMainDirection();
		if (!n0.crossProduct(n1).isZero()) {
			return true;
		}

		// project each others midpoints and check z == 0
		for (int csIndex = 0; csIndex < 2; csIndex++) {
			int mpIndex = 1 - csIndex;
			midpointProjected[csIndex][mpIndex] = c[csIndex].getCoordSys()
					.getNormalProjection(c[mpIndex].getMidpoint3D())[1];
			if (!Kernel.isZero(midpointProjected[csIndex][mpIndex].getZ())) {
				return true;
			}
		}

		// project own midpoints
		for (int csIndex = 0; csIndex < 2; csIndex++) {
			midpointProjected[csIndex][csIndex] = c[csIndex].getCoordSys()
					.getNormalProjection(c[csIndex].getMidpoint3D())[1];
		}

		return false;
	}

	@Override
	protected double getMidpointX(int csIndex, int mpIndex) {
		return midpointProjected[csIndex][mpIndex].getX();
	}

	@Override
	protected double getMidpointY(int csIndex, int mpIndex) {
		return midpointProjected[csIndex][mpIndex].getY();
	}

}

// Local Variables:
// indent-tabs-mode: nil
// c-basic-offset: 4
// tab-width: 4
// End:
// vim: set expandtab shiftwidth=4 softtabstop=4 tabstop=4
