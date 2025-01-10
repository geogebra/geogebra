/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
		conic.pointChanged(P);

		Q.setCoords((GeoPoint) endPoint);
		conic.pointChanged(Q);
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
