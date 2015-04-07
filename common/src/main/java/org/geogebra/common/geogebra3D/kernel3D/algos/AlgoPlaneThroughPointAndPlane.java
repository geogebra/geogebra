/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Compute a plane through a point and parallel to another plane (or polygon)
 * 
 *
 * @author matthieu
 * @version
 */
public class AlgoPlaneThroughPointAndPlane extends AlgoPlaneThroughPoint {

	private GeoCoordSys2D cs; // input

	public AlgoPlaneThroughPointAndPlane(Construction cons, String label,
			GeoPointND point, GeoCoordSys2D cs) {
		super(cons, point);
		this.cs = cs;

		setInputOutput(
				new GeoElement[] { (GeoElement) point, (GeoElement) cs },
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

		if (!cs.toGeoElement().isDefined())
			return;

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

	// TODO Consider locusequability
}
