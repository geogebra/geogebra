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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoAnglePointsND;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 *
 * @author mathieu
 */
public class AlgoAnglePoints3D extends AlgoAnglePointsND {

	/** vertex coords */
	protected Coords center;
	/** leg1 - vertex */
	protected Coords v1;
	/** leg2 - vertex */
	protected Coords v2;
	/** normal vector */
	protected Coords vn;

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            leg
	 * @param B
	 *            vertex
	 * @param C
	 *            leg
	 */
	AlgoAnglePoints3D(Construction cons, GeoPointND A,
			GeoPointND B, GeoPointND C) {
		this(cons, A, B, C, null);
	}

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            leg
	 * @param B
	 *            vertex
	 * @param C
	 *            leg
	 * @param orientation
	 *            orientation
	 */
	AlgoAnglePoints3D(Construction cons, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation) {
		super(cons, A, B, C, orientation);
	}

	/**
	 * @param cons
	 *            construction
	 */
	AlgoAnglePoints3D(Construction cons) {
		super(cons);
	}

	@Override
	protected GeoAngle newGeoAngle(Construction cons1) {
		GeoAngle ret = new GeoAngle3D(cons);
		ret.setDrawableNoSlider();
		return ret;
	}

	@Override
	public void compute() {
		center = getB().getInhomCoordsInD3();
		v1 = getA().getInhomCoordsInD3().sub(center);
		v2 = getC().getInhomCoordsInD3().sub(center);

		v1.calcNorm();
		double l1 = v1.getNorm();
		v2.calcNorm();
		double l2 = v2.getNorm();

		if (DoubleUtil.isZero(l1) || DoubleUtil.isZero(l2)) {
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
		// v1 = getA().getInhomCoordsInD3().sub(center);
		// v2 = getC().getInhomCoordsInD3().sub(center);
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
		// case where c is a bit more than 1
		if (DoubleUtil.isEqual(c, 1) && c > 1) {
			return 0;
		}

		// case where c is a bit less than -1
		if (DoubleUtil.isEqual(c, -1) && c < -1) {
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
		return new AlgoAnglePoints3D(getA().copy(), getB().copy(),
				getC().copy(), center.copyVector(), v1.copyVector(),
				v2.copyVector(), vn.copyVector());
	}

}
