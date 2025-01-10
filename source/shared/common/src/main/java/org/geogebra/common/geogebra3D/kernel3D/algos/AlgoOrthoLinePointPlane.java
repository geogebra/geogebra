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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordSys;

/**
 * Compute a line through a point and orthogonal to a plane (or polygon, ...)
 *
 * @author Mathieu
 */
public class AlgoOrthoLinePointPlane extends AlgoOrtho {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param point
	 *            point
	 * @param cs
	 *            coordinate system
	 */
	public AlgoOrthoLinePointPlane(Construction cons, String label,
			GeoPointND point, GeoCoordSys2D cs) {
		super(cons, label, point, (GeoElement) cs);
	}

	@Override
	public Commands getClassName() {
		return Commands.OrthogonalLine;
	}

	private GeoCoordSys2D getCS() {
		return (GeoCoordSys2D) getInputOrtho();
	}

	@Override
	public final void compute() {

		CoordSys coordsys = getCS().getCoordSys();

		getLine().setCoord(getPoint().getInhomCoordsInD3(), coordsys.getVz());

	}

}
