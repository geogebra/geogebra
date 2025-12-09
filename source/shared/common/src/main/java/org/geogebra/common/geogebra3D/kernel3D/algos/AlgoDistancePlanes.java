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
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;

public class AlgoDistancePlanes extends AlgoElement3D {

	private GeoPlaneND g3D;
	private GeoPlaneND h3D;

	private GeoNumeric dist;

	/**
	 * @param c
	 *            construction
	 * @param g3D
	 *            plane
	 * @param h3D
	 *            plane
	 */
	public AlgoDistancePlanes(Construction c, GeoPlaneND g3D, GeoPlaneND h3D) {
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

		dist.setValue(Math.abs(g3D.distanceWithSign(h3D)));

	}

}
