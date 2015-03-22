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

package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoAnglePointsND;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author mathieu
 */
public class AlgoAnglePoints3D extends AlgoAnglePointsND {

	AlgoAnglePoints3D(Construction cons, String label, GeoPointND A,
			GeoPointND B, GeoPointND C) {
		this(cons, label, A, B, C, null);
	}

	AlgoAnglePoints3D(Construction cons, String label, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation) {
		super(cons, label, A, B, C, orientation);
	}

	AlgoAnglePoints3D(Construction cons) {
		super(cons);
	}

	@Override
	protected GeoAngle newGeoAngle(Construction cons1) {
		GeoAngle ret = new GeoAngle3D(cons);
		ret.setDrawable(true);
		return ret;
	}

	protected Coords center, v1, v2, vn;

	@Override
	public void compute() {
		center = getB().getInhomCoordsInD3();
		v1 = getA().getInhomCoordsInD3().sub(center);
		v2 = getC().getInhomCoordsInD3().sub(center);

		v1.calcNorm();
		double l1 = v1.getNorm();
		v2.calcNorm();
		double l2 = v2.getNorm();

		if (Kernel.isZero(l1) || Kernel.isZero(l2)) {
			getAngle().setUndefined();
			return;
		}

		double c = v1.dotproduct(v2) / (l1 * l2); // cosinus of the angle

		getAngle().setValue(acos(c));

		// normal vector
		setForceNormalVector();

	}

	/**
	 * set normal vector (forced)
	 */
	protected void setForceNormalVector() {
		vn = forceNormalVector(v1, v2);
	}

	@Override
	public Coords getVn() {
		return vn;
	}

	@Override
	public boolean getCoordsInD3(Coords[] drawCoords) {
		//v1 = getA().getInhomCoordsInD3().sub(center);
		//v2 = getC().getInhomCoordsInD3().sub(center);
		drawCoords[0].set(center);
		drawCoords[1].set(v1);
		drawCoords[2].set(v2);

		return true;
	}

	/**
	 * acos, values can be a bit greater than 1 (or lower than 0)
	 * 
	 * @param c
	 *            cosinus of an angle
	 * @return angle between 0 and PI
	 */
	protected static final double acos(double c) {
		if (Kernel.isEqual(c, 1)) { // case where c is a bit more than 1
			return 0;
		}

		if (Kernel.isEqual(c, -1)) { // case where c is a bit less than -1
			return Math.PI;
		}

		return Math.acos(c);

	}

	/**
	 * @param v1
	 *            first vector
	 * @param v2
	 *            second vector
	 * @return vector normal to v1, v2
	 */
	protected static final Coords forceNormalVector(Coords v1, Coords v2) {
		Coords vn = v1.crossProduct4(v2);

		if (vn.isZero()) { // v1 and v2 are dependent
			vn = crossXorY(v1);
		}

		vn.normalize();

		return vn;

	}

	/**
	 * 
	 * @param v1
	 *            vector
	 * @return non zero vector orthogonal to v1 and Ox or Oy
	 */
	protected static final Coords crossXorY(Coords v1) {
		Coords vn = v1.crossProduct4(Coords.VX);
		if (vn.isZero()) {
			vn = v1.crossProduct4(Coords.VY);
		}
		return vn;
	}

	private AlgoAnglePoints3D(GeoPointND A, GeoPointND B, GeoPointND C,
			Coords center, Coords v1, Coords v2, Coords vn) {
		super(A, B, C);
		this.center = center;
		this.v1 = v1;
		this.v2 = v2;
		this.vn = vn;
	}

	@Override
	public AlgoAnglePoints3D copy() {
		return new AlgoAnglePoints3D(getA().copy(), getB().copy(), getC()
				.copy(), center.copyVector(), v1.copyVector(), v2.copyVector(),
				vn.copyVector());
	}

}
