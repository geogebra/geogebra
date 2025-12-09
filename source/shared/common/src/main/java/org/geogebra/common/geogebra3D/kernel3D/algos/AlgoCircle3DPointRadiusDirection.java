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
