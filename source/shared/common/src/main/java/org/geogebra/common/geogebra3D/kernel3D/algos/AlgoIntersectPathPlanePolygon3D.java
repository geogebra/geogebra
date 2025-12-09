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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Algo for intersection of a line with the interior of a polygon
 * 
 * @author matthieu
 */
public class AlgoIntersectPathPlanePolygon3D
		extends AlgoIntersectPathLinePolygon3D {
	/** plane */
	protected GeoPlane3D plane;

	/**
	 * common constructor
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            labels
	 * @param plane
	 *            plane
	 * @param p
	 *            polygon
	 */
	public AlgoIntersectPathPlanePolygon3D(Construction c, String[] labels,
			GeoPlane3D plane, GeoElement p) {

		super(c, labels, plane, p);

	}

	/**
	 * common constructor
	 * 
	 * @param c
	 *            construction
	 * @param plane
	 *            plane
	 * @param p
	 *            polygon
	 */
	public AlgoIntersectPathPlanePolygon3D(Construction c, GeoPlane3D plane,
			GeoElement p) {

		super(c, plane, p);

	}

	/**
	 * @param c
	 *            construction
	 */
	public AlgoIntersectPathPlanePolygon3D(Construction c) {
		super(c);
	}

	@Override
	protected void setFirstInput(GeoElement geo) {
		this.plane = (GeoPlane3D) geo;
	}

	@Override
	protected GeoElement getFirstInput() {
		return plane;
	}

	@Override
	protected void addStartEndPoints() {
		// no start/end points
	}

	@Override
	protected void setIntersectionLine() {

		Coords[] intersection = CoordMatrixUtil.intersectPlanes(
				plane.getCoordSys().getMatrixOrthonormal(),
				p.getCoordSys().getMatrixOrthonormal());

		o1 = intersection[0];
		d1 = intersection[1];

		// if (d1.isZero())
		// Log.debug("\np: "+p+"\no1=\n"+o1+"\nd1=\n"+d1);
	}

	@Override
	protected boolean checkParameter(double t1) {
		return true; // nothing to check here
	}

}
