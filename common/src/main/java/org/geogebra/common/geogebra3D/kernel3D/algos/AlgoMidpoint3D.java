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

/**
 *
 * @author mathieu
 * @version
 */
public class AlgoMidpoint3D extends AlgoMidpointND {

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param P
	 * @param Q
	 */
	AlgoMidpoint3D(Construction cons, String label, GeoPointND P, GeoPointND Q) {
		this(cons, P, Q);
		getPoint().setLabel(label);
	}

	/**
	 * 
	 * @param cons
	 * @param P
	 * @param Q
	 */
	public AlgoMidpoint3D(Construction cons, GeoPointND P, GeoPointND Q) {
		super(cons, P, Q);
	}

	/**
	 * 
	 * @param cons
	 * @param segment
	 */
	public AlgoMidpoint3D(Construction cons, GeoSegmentND segment) {
		super(cons, segment);
	}

	@Override
	protected GeoPointND newGeoPoint(Construction cons) {

		return new GeoPoint3D(cons);
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

		getPoint().setCoords(
				getP().getInhomCoordsInD3().add(getQ().getInhomCoordsInD3())
						.mul(0.5));
	}

	// TODO Consider locusequability

}
