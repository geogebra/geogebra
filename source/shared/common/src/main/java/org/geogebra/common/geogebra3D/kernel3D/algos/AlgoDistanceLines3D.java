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
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 * Computes distance between lines in 3D.
 */
public class AlgoDistanceLines3D extends AlgoElement3D {

	private GeoLineND g3D;
	private GeoLineND h3D;

	private GeoNumeric dist;

	/**
	 * @param c
	 *            construction
	 * @param g3D
	 *            first line
	 * @param h3D
	 *            second line
	 */
	public AlgoDistanceLines3D(Construction c, GeoLineND g3D, GeoLineND h3D) {
		super(c);
		this.g3D = g3D;
		this.h3D = h3D;
		dist = new GeoNumeric(cons);

		setInputOutput(); // for AlgoElement

		// compute length
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Distance;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) g3D;
		input[1] = (GeoElement) h3D;

		setOnlyOutput(dist);
		setDependencies(); // done by AlgoElement
	}

	GeoLineND getg() {
		return g3D;
	}

	GeoLineND geth() {
		return h3D;
	}

	/**
	 * @return distance
	 */
	public GeoNumeric getDistance() {
		return dist;
	}

	// calc length of vector v
	@Override
	public void compute() {
		if (!g3D.isDefined() || !h3D.isDefined()) {
			dist.setUndefined();
			return;
		}

		if (g3D instanceof GeoLine) {
			dist.setValue(h3D.distance(g3D));
		} else {
			dist.setValue(g3D.distance(h3D));
		}

	}

}
