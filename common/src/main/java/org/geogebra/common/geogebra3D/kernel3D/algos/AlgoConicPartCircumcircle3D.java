/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicPart3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.algos.AlgoConicPartCircumcircleND;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Circle arc or sector defined by three points.
 */
public class AlgoConicPartCircumcircle3D extends AlgoConicPartCircumcircleND {

	private AlgoCircle3DThreePoints algo;

	public AlgoConicPartCircumcircle3D(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoPointND C, int type) {
		super(cons, label, A, B, C, type);

	}

	@Override
	protected AlgoCircleThreePoints getAlgo() {
		algo = new AlgoCircle3DThreePoints(cons, A, B, C);
		return algo;
	}

	@Override
	protected GeoConicPart3D createConicPart(Construction cons, int type) {
		return new GeoConicPart3D(cons, type);
	}

	@Override
	protected void computeSinglePoint() {
		GeoConic3D.setSinglePoint((GeoConic3D) conicPart,
				A.getInhomCoordsInD(3));
		super.computeSinglePoint();
	}

	@Override
	final public GeoPoint getA() {
		return algo.getPoint2D(0);
	}

	/**
	 * Method for LocusEqu.
	 * 
	 * @return second point.
	 */
	@Override
	final public GeoPoint getB() {
		return algo.getPoint2D(1);
	}

	/**
	 * Method for LocusEqu.
	 * 
	 * @return third point.
	 */
	@Override
	final public GeoPoint getC() {
		return algo.getPoint2D(2);
	}

	@Override
	public GeoConicPart3D getConicPart() {
		return (GeoConicPart3D) super.getConicPart();
	}

}
