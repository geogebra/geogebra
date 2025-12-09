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
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a plane through a point and parallel to another plane (or polygon)
 * 
 *
 * @author Mathieu
 */
public class AlgoPlaneThroughPointAndPlane extends AlgoPlaneThroughPoint {

	private GeoCoordSys2D cs; // input

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param point
	 *            point
	 * @param cs
	 *            parallel plane
	 */
	public AlgoPlaneThroughPointAndPlane(Construction cons, String label,
			GeoPointND point, GeoCoordSys2D cs) {
		super(cons, point);
		this.cs = cs;

		setInputOutput(new GeoElement[] { (GeoElement) point, (GeoElement) cs },
				new GeoElement[] { getPlane() });

		// compute plane
		compute();
		getPlane().setLabel(label);
	}

	@Override
	public final void compute() {

		CoordSys coordsys = getPlane().getCoordSys();

		// recompute the coord sys
		coordsys.resetCoordSys();

		if (!cs.toGeoElement().isDefined()) {
			return;
		}

		Coords o = getPoint().getInhomCoordsInD3();
		coordsys.addPoint(o);

		CoordSys inputCS = cs.getCoordSys();
		coordsys.addVectorWithoutCheckMadeCoordSys(inputCS.getVx());
		coordsys.addVectorWithoutCheckMadeCoordSys(inputCS.getVy());

		coordsys.makeOrthoMatrix(true, false);

		// notice that coordsys.getEquationVector() W value is ignored
		if (cs instanceof GeoPlane3D) {
			coordsys.setEquationVector(o, inputCS.getEquationVector());
		} else {
			coordsys.setEquationVector(o, inputCS.getVz());
		}

	}

	@Override
	protected GeoElement getSecondInput() {
		return (GeoElement) cs;
	}

}
