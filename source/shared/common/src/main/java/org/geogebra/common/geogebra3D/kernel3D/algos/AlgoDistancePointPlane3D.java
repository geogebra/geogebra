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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoDistancePointPlane3D extends AlgoElement3D {

	private GeoPointND point;
	private GeoPlane3D plane;

	private GeoNumeric dist;

	/**
	 * @param c
	 *            construction
	 * @param point
	 *            point
	 * @param plane
	 *            plane
	 */
	public AlgoDistancePointPlane3D(Construction c, GeoPointND point,
			GeoPlaneND plane) {
		super(c);
		this.point = point;
		this.plane = (GeoPlane3D) plane;
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
		input[0] = (GeoElement) point;
		input[1] = plane;

		setOnlyOutput(dist);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getDistance() {
		return dist;
	}

	// calc length of vector v
	@Override
	public void compute() {
		if (!point.isDefined() || !plane.isDefined()) {
			dist.setUndefined();
			return;
		}

		dist.setValue(plane.distance(point));

	}

}
