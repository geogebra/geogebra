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
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a circle with point and radius (missing direction). Never goes to
 * XML, just helper algo.
 *
 * @author Mathieu
 */
public class AlgoCircle3DCenterPointPoint extends AlgoCircle3DPointDirection {

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param center
	 *            center
	 * @param pointThrough
	 *            point on circle
	 * @param forAxis
	 *            point in plane
	 */
	public AlgoCircle3DCenterPointPoint(Construction cons, GeoPointND center,
			GeoPointND pointThrough, GeoPointND forAxis) {
		super(cons, center, pointThrough, forAxis);

	}

	@Override
	protected final double getRadius() {

		GeoPointND pointThrough = (GeoPointND) getSecondInput();
		Coords radius = pointThrough.getInhomCoordsInD3()
				.sub(getCenter().getInhomCoordsInD3());

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
