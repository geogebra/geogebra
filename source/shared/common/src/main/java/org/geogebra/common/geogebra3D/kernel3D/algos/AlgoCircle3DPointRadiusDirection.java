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
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Compute a circle with point and radius (missing direction)
 *
 * @author matthieu
 */
public class AlgoCircle3DPointRadiusDirection
		extends AlgoCircle3DPointDirection {

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param point
	 *            center
	 * @param forAxis
	 *            axis direction
	 * @param radius
	 *            radius
	 */
	public AlgoCircle3DPointRadiusDirection(Construction cons, GeoPointND point,
			GeoNumberValue radius, GeoDirectionND forAxis) {
		super(cons, point, radius, forAxis);

	}

	@Override
	protected final double getRadius() {
		return getSecondInput().evaluateDouble();
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
			return "CircleWithCenterARadiusBParallelToC";
		}

		return "CircleWithCenterAandRadiusBAxisParallelToC";
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
