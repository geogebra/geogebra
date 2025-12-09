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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Finds point on one line close to another line.
 *
 */
public class AlgoClosestPointLines3D extends AlgoElement3D {

	private GeoLineND g3D;
	private GeoLineND h3D;

	private GeoPoint3D geoPointOnG;

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            output label
	 * @param g3D
	 *            line where the point should be
	 * @param h3D
	 *            other line
	 */
	public AlgoClosestPointLines3D(Construction c, String label, GeoLineND g3D,
			GeoLineND h3D) {
		super(c);
		this.g3D = g3D;
		this.h3D = h3D;

		geoPointOnG = new GeoPoint3D(c);
		setInputOutput(); // for AlgoElement

		// compute length
		compute();
		geoPointOnG.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.ClosestPoint;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) g3D;
		input[1] = (GeoElement) h3D;

		setOnlyOutput(geoPointOnG);
		setDependencies(); // done by AlgoElement
	}

	GeoLineND getg() {
		return g3D;
	}

	GeoLineND geth() {
		return h3D;
	}

	/**
	 * @return resulting point
	 */
	public GeoPoint3D getPoint() {
		return geoPointOnG;
	}

	@Override
	public void compute() {

		if (!g3D.isDefined() || !h3D.isDefined()) {
			geoPointOnG.setUndefined();
			return;
		}

		Coords[] points = CoordMatrixUtil.nearestPointsFromTwoLines(
				g3D.getStartInhomCoords(), g3D.getDirectionInD3(),
				h3D.getStartInhomCoords(), h3D.getDirectionInD3());

		geoPointOnG.setCoords(points[0], false);

	}

}
