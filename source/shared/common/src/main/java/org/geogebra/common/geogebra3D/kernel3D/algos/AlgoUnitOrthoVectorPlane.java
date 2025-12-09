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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;

/**
 *
 * @author ggb3D
 * 
 *         Calculate the unit ortho vector of a plane (or polygon, ...)
 * 
 */
public class AlgoUnitOrthoVectorPlane extends AlgoOrthoVectorPlane {

	AlgoUnitOrthoVectorPlane(Construction cons, String label,
			GeoCoordSys2D plane) {

		super(cons, label, plane);

	}

	// /////////////////////////////////////////////
	// COMPUTE

	@Override
	protected void updateCoords() {
		if (plane instanceof GeoPlane3D) {
			vCoords.setValues(plane.getCoordSys().getEquationVector(), 3); // get
																			// (a,
																			// b,
																			// c)
																			// from
																			// ax+by+cz+d=0
			vCoords.normalize();
		} else {
			plane.getCoordSys().getVz().normalized(vCoords);
		}

	}

	@Override
	public Commands getClassName() {

		return Commands.UnitOrthogonalVector;
	}

}
