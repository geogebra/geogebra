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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSpace;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author mathieu
 */
public class AlgoOrthoLinePointLine3D extends AlgoOrtho {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param point
	 *            point
	 * @param line
	 *            orthogonal line
	 */
	public AlgoOrthoLinePointLine3D(Construction cons, String label,
			GeoPointND point, GeoLineND line) {
		super(cons, label, point, (GeoElement) line);
	}

	@Override
	protected void setSpecificInputOutput() {
		setInputOutput(
				new GeoElement[] { (GeoElement) point, inputOrtho,
						(GeoSpace) cons.getSpace() },
				new GeoElement[] { line });
	}

	@Override
	public Commands getClassName() {
		return Commands.OrthogonalLine;
	}

	private GeoLineND getInputLine() {
		return (GeoLineND) getInputOrtho();
	}

	@Override
	public final void compute() {

		GeoLineND line1 = getInputLine();
		Coords o = line1.getPointInD(3, 0).getInhomCoordsInSameDimension();
		Coords v1 = line1.getPointInD(3, 1).getInhomCoordsInSameDimension()
				.sub(o);
		Coords o2 = getPoint().getInhomCoordsInD3();
		Coords v2 = o2.sub(o);

		Coords v3 = v1.crossProduct(v2);
		Coords v = v3.crossProduct(v1);

		if (v.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
			getLine().setUndefined();
		} else {
			getLine().setCoord(getPoint().getInhomCoordsInD3(), v.normalize());
		}

	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("LineThroughAPerpendicularToBinSpace",
				point.getLabel(tpl), inputOrtho.getLabel(tpl));
	}

}
