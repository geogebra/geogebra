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
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Compute a circle with point and radius (missing direction)
 *
 * @author matthieu
 */
public class AlgoCircle3DPointPointDirection
		extends AlgoCircle3DPointDirection {

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param point
	 *            center
	 * @param pointThrough
	 *            point on circle
	 * @param forAxis
	 *            axis direction
	 */
	public AlgoCircle3DPointPointDirection(Construction cons, GeoPointND point,
			GeoPointND pointThrough, GeoDirectionND forAxis) {
		super(cons, point, pointThrough, forAxis);

	}

	@Override
	protected final double getRadius() {

		GeoPointND pointThrough = (GeoPointND) getSecondInput();
		Coords radius = pointThrough.getInhomCoordsInD3()
				.sub(getCenter().getInhomCoordsInD3());

		// check if direction is compatible (orthogonal) to center-second point
		if (!DoubleUtil.isZero(getDirection().dotproduct(radius))) {
			return Double.NaN;
		}

		radius.calcNorm();

		return radius.getNorm();

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
		if (getForAxis() instanceof GeoCoordSys2D) {
			return "CircleWithCenterAThroughBParallelToC";
		}

		return "CircleWithCenterAThroughBAxisParallelToC";
	}

	@Override
	final protected boolean setCoordSys() {
		if (((GeoDirectionND) getForAxis()).getDirectionInD3() == null) { // e.g.
																			// space
			return false;
		}

		return super.setCoordSys();
	}

}
