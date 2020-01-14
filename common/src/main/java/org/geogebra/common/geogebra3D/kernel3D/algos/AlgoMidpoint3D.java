/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoMidPoint.java
 *
 * Created on 24. September 2001, 21:37
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
