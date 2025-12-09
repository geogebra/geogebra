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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Arc or sector defined by a conic, start- and end-point.
 */
public class AlgoConicPartConicPoints extends AlgoConicPartConicPointsND {

	// temp points
	private GeoPoint P;
	private GeoPoint Q;

	/**
	 * Creates a new arc or sector algorithm. The type is either
	 * GeoConicPart.CONIC_PART_ARC or GeoConicPart.CONIC_PART_ARC
	 */
	public AlgoConicPartConicPoints(Construction cons, String label,
			GeoConicND circle, GeoPointND startPoint, GeoPointND endPoint,
			int type) {
		super(cons, label, circle, startPoint, endPoint, type);

	}

	@Override
	protected void setTempValues() {
		// temp points
		P = new GeoPoint(cons);
		Q = new GeoPoint(cons);
		P.setPath(conic);
		Q.setPath(conic);
	}

	@Override
	protected GeoConicND newGeoConicPart(Construction cons1, int partType) {
		return new GeoConicPart(cons1, partType);
	}

	@Override
	protected void computeParameters() {
		// the temp points P and Q should lie on the conic
		P.setCoords((GeoPoint) startPoint);
		conic.pointChangedUnlimited(P.getCoordsInD2(conic.getCoordSys()), P.getPathParameter());

		Q.setCoords((GeoPoint) endPoint);
		conic.pointChangedUnlimited(Q.getCoordsInD2(conic.getCoordSys()), Q.getPathParameter());
	}

	@Override
	protected double getStartParameter() {
		return P.getPathParameter().t;
	}

	@Override
	protected double getEndParameter() {
		return Q.getPathParameter().t;
	}

	@Override
	public GeoConicPart getConicPart() {
		return (GeoConicPart) super.getConicPart();
	}

}
