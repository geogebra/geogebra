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
import org.geogebra.common.kernel.FixedPathRegionAlgo;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Finds a point in given region closest to a given point.
 *
 */
public class AlgoClosestPointToRegion3D extends AlgoElement3D
		implements FixedPathRegionAlgo {

	private Region r;
	private GeoPointND P;

	private GeoPointND geoPointOnRegion;

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            output label
	 * @param r
	 *            region
	 * @param P
	 *            source point
	 */
	public AlgoClosestPointToRegion3D(Construction c, String label, Region r,
			GeoPointND P) {
		super(c);
		this.r = r;
		this.P = P;
		if (r.isGeoElement3D()) {
			geoPointOnRegion = new GeoPoint3D(c);
		} else {
			geoPointOnRegion = new GeoPoint(c);
		}
		geoPointOnRegion.setRegion(r);
		setInputOutput(); // for AlgoElement

		// compute length
		compute();
		geoPointOnRegion.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.ClosestPointRegion;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) r;
		input[1] = (GeoElement) P;

		setOnlyOutput(geoPointOnRegion);
		setDependencies(); // done by AlgoElement
	}

	Region getInputRegion() {
		return r;
	}

	GeoPointND getInputPoint() {
		return P;
	}

	public GeoPointND getOutputPoint() {
		return geoPointOnRegion;
	}

	@Override
	public void compute() {
		if (input[0].isDefined() && P.isDefined()) {
			geoPointOnRegion.setCoords(P.getInhomCoordsInD3(), false);
			r.pointChangedForRegion(geoPointOnRegion);
			geoPointOnRegion.updateCoords();
		} else {
			geoPointOnRegion.setUndefined();
		}
	}

	@Override
	public boolean isChangeable(GeoElementND out) {
		return false;
	}

}
