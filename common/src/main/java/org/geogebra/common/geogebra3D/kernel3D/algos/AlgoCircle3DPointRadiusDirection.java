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
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Compute a circle with point and radius (missing direction)
 *
 * @author matthieu
 * @version
 */
public class AlgoCircle3DPointRadiusDirection extends
		AlgoCircle3DPointDirection {

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param point
	 * @param forAxis
	 * @param radius
	 */
	public AlgoCircle3DPointRadiusDirection(Construction cons, String label,
			GeoPointND point, NumberValue radius, GeoDirectionND forAxis) {
		super(cons, label, point, radius.toGeoElement(), forAxis);

	}

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param point
	 * @param forAxis
	 * @param radius
	 */
	public AlgoCircle3DPointRadiusDirection(Construction cons,
			GeoPointND point, NumberValue radius, GeoDirectionND forAxis) {
		super(cons, point, radius.toGeoElement(), (GeoElement) forAxis);

	}

	@Override
	protected final double getRadius() {

		return ((NumberValue) getSecondInput()).getDouble();

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
		if (getForAxis() instanceof GeoCoordSys2D)
			return "CircleWithCenterARadiusBParallelToC";

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
