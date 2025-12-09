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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a line orthogonal to two lines
 *
 * @author matthieu
 */
public class AlgoOrthoLineLineLine extends AlgoOrthoLineLine {

	private GeoLineND line2; // input

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param line1
	 *            first line
	 * @param line2
	 *            second line
	 */
	public AlgoOrthoLineLineLine(Construction cons, String label,
			GeoLineND line1, GeoLineND line2) {
		super(cons, line1);
		this.line2 = line2;

		setInputOutput(
				new GeoElement[] { (GeoElement) line1, (GeoElement) line2 },
				new GeoElement[] { getLine() });

		// compute line
		compute();
		getLine().setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.OrthogonalLine;
	}

	@Override
	protected void setOriginAndDirection2() {
		Coords o2 = line2.getPointInD(3, 0).getInhomCoordsInSameDimension();
		direction2 = line2.getPointInD(3, 1).getInhomCoordsInSameDimension()
				.sub(o2);
		Coords[] points = CoordMatrixUtil.nearestPointsFromTwoLines(origin1,
				direction1, o2, direction2);
		origin = points[0];

	}

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlain("LinePerpendicularToAandB",
				line1.getLabel(tpl), line2.getLabel(tpl));

	}

}
