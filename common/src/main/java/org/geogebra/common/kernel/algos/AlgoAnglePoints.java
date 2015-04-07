/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAnglePoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoAnglePoints extends AlgoAnglePointsND implements
		DrawInformationAlgo {

	public AlgoAnglePoints(Construction cons, String label, GeoPointND A,
			GeoPointND B, GeoPointND C) {
		this(cons, label, A, B, C, null);
	}

	public AlgoAnglePoints(Construction cons, String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation) {
		this(cons, A, B, C, orientation);
		angle.setLabel(label);
	}

	AlgoAnglePoints(Construction cons, AlgoAnglePolygon algoAnglePoly,
			GeoPointND A, GeoPointND B, GeoPointND C) {
		this(cons, A, B, C, null);
		this.algoAnglePoly = algoAnglePoly;
	}

	public AlgoAnglePoints(Construction cons, GeoPointND A, GeoPointND B,
			GeoPointND C) {

		this(cons, A, B, C, null);
	}

	public AlgoAnglePoints(Construction cons, GeoPointND A, GeoPointND B,
			GeoPointND C, GeoDirectionND orientation) {
		super(cons);
		setInput(A, B, C, orientation);
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	/**
	 * used as a helper algo (for AlgoAnglePolygon)
	 * 
	 * @param cons
	 */
	protected AlgoAnglePoints(Construction cons) {
		super(cons);
	}

	public AlgoAnglePoints(GeoPointND A, GeoPointND B, GeoPointND C) {
		super(A.toGeoElement().cons, false);
		this.An = A;
		this.Bn = B;
		this.Cn = C;
	}

	@Override
	public AlgoAnglePoints copy() {
		return new AlgoAnglePoints(An.copy(), Bn.copy(), Cn.copy());
	}

	// calc angle between vectors A-B and C-B
	// angle in range [0, pi]
	@Override
	public void compute() {

		GeoPoint A = (GeoPoint) An;
		GeoPoint B = (GeoPoint) Bn;
		GeoPoint C = (GeoPoint) Cn;

		if (!A.isFinite() || !B.isFinite() || !C.isFinite()) {
			angle.setUndefined(); // undefined
			return;
		}

		// get vectors v=BA and w=BC
		bx = B.inhomX;
		by = B.inhomY;
		vx = A.inhomX - bx;
		vy = A.inhomY - by;
		wx = C.inhomX - bx;
		wy = C.inhomY - by;

		if (Kernel.isZero(vx) && Kernel.isZero(vy) || Kernel.isZero(wx)
				&& Kernel.isZero(wy)) {
			angle.setUndefined();
			return;
		}

		// |v| * |w| * sin(alpha) = det(v, w)
		// cos(alpha) = v . w / (|v| * |w|)
		// tan(alpha) = sin(alpha) / cos(alpha)
		// => tan(alpha) = det(v, w) / v . w
		double det = vx * wy - vy * wx;
		double prod = vx * wx + vy * wy;
		double value = Math.atan2(det, prod);

		angle.setValue(value);
	}

	// TODO Consider locusequability

	// ///////////////////////////////
	// TRICKS FOR XOY PLANE
	// ///////////////////////////////

	@Override
	protected int getInputLengthForXML() {
		return getInputLengthForXMLMayNeedXOYPlane();
	}

	@Override
	protected int getInputLengthForCommandDescription() {
		return getInputLengthForCommandDescriptionMayNeedXOYPlane();
	}

	@Override
	public GeoElement getInput(int i) {
		return getInputMaybeXOYPlane(i);
	}

}
