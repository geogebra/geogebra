/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoJoinPointsSegment
 *
 * Created on 21. August 2003
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;

/**
 *
 * @author ggb3D
 * @version
 * 
 *          Calculate the unit ortho vector of a plane (or polygon, ...)
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
