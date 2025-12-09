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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 *
 * @author Markus
 */
public class AlgoTangentLine extends AlgoTangentLineND {

	private GeoVector direction;

	/** Creates new AlgoTangentLine */
	AlgoTangentLine(Construction cons, String label, GeoLineND g,
			GeoConicND c) {
		super(cons, label, g, c);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param g
	 *            conic
	 * @param c
	 *            parallel line
	 */
	public AlgoTangentLine(Construction cons, String[] labels, GeoLineND g,
			GeoConicND c) {
		super(cons, labels, g, c);
	}

	@Override
	protected void initDiameterAndDirection() {
		// the tangents are computed by intersecting the
		// diameter line of g with c
		diameter = new GeoLine(cons);
		direction = new GeoVector(cons);
		((GeoLine) g).getDirection(direction);
		c.diameterLine(direction, diameter);
		algoIntersect = new AlgoIntersectLineConic(cons, diameter,
				(GeoConic) c);
		// this is only an internal Algorithm that shouldn't be in the
		// construction list
		cons.removeFromConstructionList(algoIntersect);
		tangentPoints = algoIntersect.getIntersectionPoints();
	}

	@Override
	protected void setTangents() {
		tangents = new GeoLine[2];
		tangents[0] = new GeoLine(cons);
		tangents[1] = new GeoLine(cons);
		((GeoLine) tangents[0]).setStartPoint((GeoPoint) tangentPoints[0]);
		((GeoLine) tangents[1]).setStartPoint((GeoPoint) tangentPoints[1]);
	}

	/**
	 * Inits the helping intersection algorithm to take the current position of
	 * the lines into account. This is important so the the tangent lines are
	 * not switched after loading a file
	 */
	@Override
	public void initForNearToRelationship() {
		AlgoTangentPoint.initForNearToRelationship(tangentPoints, tangents[0],
				algoIntersect);
	}

	@Override
	protected void updateDiameterLine() {
		((GeoLine) g).getDirection(direction);
		c.diameterLine(direction, diameter);
	}

	@Override
	protected void updateTangent(int index) {
		GeoLine tangent = (GeoLine) tangents[index];
		GeoLine line = (GeoLine) g;
		GeoPoint point = (GeoPoint) tangentPoints[index];
		// tangent.x = line.x;
		// tangent.y = line.y;
		// tangent.z = -(point.inhomX * line.x + point.inhomY * line.y);
		tangent.setCoords(line.x, line.y,
				-(point.inhomX * line.x + point.inhomY * line.y));
	}

	@Override
	protected void updateTangentParabola() {
		GeoLine tangent0 = (GeoLine) tangents[0];
		GeoLine tangent1 = (GeoLine) tangents[1];
		GeoLine line = (GeoLine) g;
		GeoPoint point0 = (GeoPoint) tangentPoints[0];
		GeoPoint point1 = (GeoPoint) tangentPoints[1];
		// tangent.x = line.x;
		// tangent.y = line.y;
		double z0 = -(point0.inhomX * line.x + point0.inhomY * line.y);
		double z1 = -(point1.inhomX * line.x + point1.inhomY * line.y);

		// parabola will have just 1 tangent
		if (Double.isNaN(z1)) {
			tangent0.setCoords(line.x, line.y, z0);
		} else {
			tangent0.setCoords(line.x, line.y, z1);

		}
		tangent1.setUndefined();
	}
}
