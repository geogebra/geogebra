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
 * Compute a circle with point and radius (missing direction)
 *
 * @author matthieu
 * @version
 */
public class AlgoCircle3DCenterPointPoint extends AlgoCircle3DPointDirection {

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param point
	 * @param pointThrough
	 * @param forAxis
	 */
	public AlgoCircle3DCenterPointPoint(Construction cons, GeoPointND center,
			GeoPointND pointThrough, GeoPointND forAxis) {
		super(cons, center, (GeoElement) pointThrough, (GeoElement) forAxis);

	}

	@Override
	protected final double getRadius() {

		GeoPointND pointThrough = (GeoPointND) getSecondInput();
		Coords radius = pointThrough.getInhomCoordsInD3().sub(
				getCenter().getInhomCoordsInD3());

		radius.calcNorm();

		return radius.getNorm();

	}

	@Override
	final protected boolean setCoordSys() {

		coordsys.resetCoordSys();

		coordsys.addPoint(point.getInhomCoordsInD3());
		coordsys.addPoint(((GeoPointND) secondInput).getInhomCoordsInD3());
		coordsys.addPoint(((GeoPointND) forAxis).getInhomCoordsInD3());

		coordsys.makeOrthoMatrix(false, false);

		return true;
	}

	@Override
	public Commands getClassName() {
		return Commands.Circle;
	}

	/**
	 * 
	 * @return command string
	 */
	@Override
	final protected String getCommandString() {
		return "CircleWithCenterAThroughBParallelToABC";
	}

}
