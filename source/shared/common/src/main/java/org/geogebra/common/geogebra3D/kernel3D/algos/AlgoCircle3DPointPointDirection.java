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
