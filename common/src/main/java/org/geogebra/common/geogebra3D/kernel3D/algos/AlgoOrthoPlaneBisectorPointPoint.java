/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author matthieu
 * @version
 */
public class AlgoOrthoPlaneBisectorPointPoint extends AlgoOrthoPlane {

	private GeoPointND point1; // input
	private GeoPointND point2; // input

	public AlgoOrthoPlaneBisectorPointPoint(Construction cons, String label,
			GeoPointND point1, GeoPointND point2) {
		super(cons);
		this.point1 = point1;
		this.point2 = point2;

		setInputOutput(new GeoElement[] { (GeoElement) point1,
				(GeoElement) point2 }, new GeoElement[] { getPlane() });

		// compute plane
		compute();
		getPlane().setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.PlaneBisector;
	}

	@Override
	protected Coords getNormal() {
		return point2.getInhomCoordsInD3().sub(point1.getInhomCoordsInD3());
	}

	@Override
	protected Coords getPoint() {
		return point1.getInhomCoordsInD3().add(point2.getInhomCoordsInD3())
				.mul(0.5);
	}

	// TODO Consider locusequability

}
