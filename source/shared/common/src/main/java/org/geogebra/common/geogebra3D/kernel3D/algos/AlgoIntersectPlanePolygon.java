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

import java.util.TreeMap;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.HasSegments;
import org.geogebra.common.kernel.matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Computes plane x polygon intersection.
 */
public class AlgoIntersectPlanePolygon extends AlgoIntersectLinePolygon3D {

	private GeoPlane3D plane;

	/**
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param plane
	 *            plane
	 * @param p
	 *            polygon
	 */
	public AlgoIntersectPlanePolygon(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolygon p) {
		super(c, labels, plane, p);
	}

	@Override
	protected void setFirstInput(GeoElementND geo) {
		this.plane = (GeoPlane3D) geo;

	}

	@Override
	protected GeoElement getFirstInput() {
		return plane;
	}

	@Override
	protected void setIntersectionLine() {

		Coords[] intersection = CoordMatrixUtil.intersectPlanes(
				plane.getCoordSys().getMatrixOrthonormal(),
				((GeoPolygon) p).getCoordSys().getMatrixOrthonormal());

		o1 = intersection[0];
		d1 = intersection[1];

	}

	@Override
	protected void intersectionsCoords(HasSegments poly,
			TreeMap<Double, Coords> newCoords) {

		// intersection line is contained in polygon plane by definition
		intersectionsCoordsContained(poly, newCoords);
	}

	@Override
	protected boolean checkParameter(double t1) {
		return true;
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

}
