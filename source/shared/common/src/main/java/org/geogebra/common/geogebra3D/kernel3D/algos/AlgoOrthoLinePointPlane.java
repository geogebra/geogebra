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
