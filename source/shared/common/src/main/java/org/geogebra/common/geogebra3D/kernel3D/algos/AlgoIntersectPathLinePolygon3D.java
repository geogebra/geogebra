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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoIntersectPathLinePolygon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Algo for intersection of a line with the interior of a polygon
 * 
 * @author matthieu
 */
public class AlgoIntersectPathLinePolygon3D
		extends AlgoIntersectPathLinePolygon {

	/**
	 * common constructor
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param geo
	 *            line
	 * @param p
	 *            polygon
	 */
	public AlgoIntersectPathLinePolygon3D(Construction c, String[] labels,
			GeoElement geo, GeoElement p) {

		super(c, labels, geo, p);

	}

	/**
	 * common constructor
	 * 
	 * @param c
	 *            construction
	 * @param geo
	 *            line
	 * @param p
	 *            polygon
	 */
	public AlgoIntersectPathLinePolygon3D(Construction c, GeoElement geo,
			GeoElement p) {

		super(c, geo, p);

	}

	/**
	 * @param c
	 *            construction
	 */
	public AlgoIntersectPathLinePolygon3D(Construction c) {
		super(c);
	}

	@Override
	protected OutputHandler<GeoElement> createOutputSegments() {
		return new OutputHandler<>(() -> {
			GeoSegment3D a = new GeoSegment3D(cons);
			GeoPoint3D aS = new GeoPoint3D(cons);
			aS.setCoords(0, 0, 0, 1);
			GeoPoint3D aE = new GeoPoint3D(cons);
			aE.setCoords(0, 0, 0, 1);
			a.setPoints(aS, aE);
			a.setParentAlgorithm(this);
			setSegmentVisualProperties(a);
			return a;
		});
	}

	@Override
	protected void addCoords(double parameter, Coords coords,
			GeoElementND geo) {
		newCoords.put(parameter, coords.copyVector());
	}

	@Override
	protected void addStartEndPoints() {
		if (g instanceof GeoSegmentND) {
			newCoords.put(0.0, g.getStartInhomCoords());
			newCoords.put(1.0, g.getEndInhomCoords());
		} else if (g instanceof GeoRayND) {
			newCoords.put(0d, g.getStartInhomCoords());
		}
	}

	@Override
	protected boolean checkMidpoint(GeoPolygon poly, Coords a, Coords b) {
		Coords midpoint = poly
				.getNormalProjection(a.copy().addInside(b).mulInside(0.5))[1];
		return poly.isInRegion(midpoint.getX(), midpoint.getY());
	}

}
