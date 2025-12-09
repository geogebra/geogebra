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
import org.geogebra.common.kernel.kernelND.AlgoMidpointND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 *
 * @author mathieu
 */
public class AlgoMidpoint3D extends AlgoMidpointND {
	private Coords tmpCoords;

	/**
	 * @param cons
	 *            construction
	 * @param P
	 *            point
	 * @param Q
	 *            point
	 */
	public AlgoMidpoint3D(Construction cons, GeoPointND P, GeoPointND Q) {
		super(cons, P, Q);
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param segment
	 *            segment
	 */
	public AlgoMidpoint3D(Construction cons, GeoSegmentND segment) {
		super(cons, segment);
	}

	@Override
	protected GeoPointND newGeoPoint(Construction cons1) {
		tmpCoords = Coords.createInhomCoorsInD3();
		return new GeoPoint3D(cons1);
	}

	@Override
	public GeoPoint3D getPoint() {
		return (GeoPoint3D) super.getPoint();
	}

	@Override
	protected void copyCoords(GeoPointND point) {
		getPoint().setCoords(point.getCoordsInD3());
	}

	@Override
	protected void computeMidCoords() {
		tmpCoords.setAdd3(getP().getInhomCoordsInD3(),
				getQ().getInhomCoordsInD3()).mulInside3(0.5);
		getPoint().setCoords(tmpCoords);
	}

}
