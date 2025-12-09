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
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author Mathieu
 */
public class AlgoOrthoPlaneBisectorPointPoint extends AlgoOrthoPlane {

	private GeoPointND point1; // input
	private GeoPointND point2; // input

	private Coords normal = new Coords(4);
	private Coords point = Coords.createInhomCoorsInD3();

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param point1
	 *            bisected segment endpoint
	 * @param point2
	 *            bisected segment endpoint
	 */
	public AlgoOrthoPlaneBisectorPointPoint(Construction cons, String label,
			GeoPointND point1, GeoPointND point2) {
		super(cons);
		this.point1 = point1;
		this.point2 = point2;

		setInputOutput(
				new GeoElement[] { (GeoElement) point1, (GeoElement) point2 },
				new GeoElement[] { getPlane() });

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
		return normal.setSub3(point2.getInhomCoordsInD3(),
				point1.getInhomCoordsInD3());
	}

	@Override
	protected Coords getPoint() {
		return point.setAdd3(point1.getInhomCoordsInD3(),
				point2.getInhomCoordsInD3()).mulInside3(0.5);
	}

}
