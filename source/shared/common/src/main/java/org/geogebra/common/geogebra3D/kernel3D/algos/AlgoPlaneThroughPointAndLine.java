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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a plane through a point and a line (or segment, ...)
 *
 * @author Mathieu
 */
public class AlgoPlaneThroughPointAndLine extends AlgoPlaneThroughPoint {

	private GeoLineND line; // input

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param point
	 *            point
	 * @param line
	 *            line in plane
	 */
	public AlgoPlaneThroughPointAndLine(Construction cons, String label,
			GeoPointND point, GeoLineND line) {
		super(cons, point);

		this.line = line;

		setInputOutput(
				new GeoElement[] { (GeoElement) point, (GeoElement) line },
				new GeoElement[] { getPlane() });

		// compute plane
		compute();
		getPlane().setLabel(label);

	}

	@Override
	public final void compute() {

		CoordSys coordsys = getPlane().getCoordSys();

		// recompute the coord sys
		coordsys.resetCoordSys();

		Coords cA = getPoint().getInhomCoordsInD3();
		Coords cB = line.getPointInD(3, 0).getInhomCoordsInSameDimension();
		Coords cC = line.getPointInD(3, 1).getInhomCoordsInSameDimension();

		coordsys.addPoint(cA);
		coordsys.addPoint(cB);
		coordsys.addPoint(cC);

		if (coordsys.makeOrthoMatrix(true, false)) {
			coordsys.setEquationVector(cA, cB, cC);
		}

	}

	@Override
	protected GeoElement getSecondInput() {
		return (GeoElement) line;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("PlaneThroughAB", getPoint().getLabel(tpl),
				getSecondInput().getLabel(tpl));

	}

}
