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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoAngleVectorND;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 *
 * @author mathieu
 * @version
 */
public abstract class AlgoAngleElement3D extends AlgoAngleVectorND {

	private Coords vn, v2;

	public AlgoAngleElement3D(Construction cons, String label, GeoElement vec) {
		super(cons, label, vec);
	}

	@Override
	final protected GeoAngle newGeoAngle(Construction cons) {
		GeoAngle ret = new GeoAngle3D(cons);
		ret.setDrawable(true);
		return ret;
	}

	protected abstract Coords getVectorCoords();

	protected abstract Coords getOrigin();

	protected abstract void setOrigin();

	@Override
	public final void compute() {

		// vectors directions
		v2 = getVectorCoords();

		// calc angle
		v2.calcNorm();
		double l2 = v2.getNorm();

		double c = v2.getX() / l2; // cosinus of the angle

		getAngle().setValue(AlgoAnglePoints3D.acos(c));

		// normal vector
		vn = AlgoAnglePoints3D.forceNormalVector(Coords.VX, v2);

		// start point
		setOrigin();

	}

	@Override
	public Coords getVn() {
		return vn;
	}

	@Override
	public boolean getCoordsInD3(Coords[] drawCoords) {

		if (!getOrigin().isDefined()) {
			return false;
		}

		drawCoords[0] = getOrigin();
		drawCoords[1] = Coords.VX;
		drawCoords[2] = v2;

		return true;
	}

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec,
			DrawAngle drawable) {

		if (vec.isGeoVector()) {
			if (!getOrigin().isDefined()) {
				return false;
			}
			if (!drawable.inView(getOrigin())) {
				return false;
			}
		}

		if (!drawable.inView(v2)) {
			return false;
		}

		// origin
		m[0] = getOrigin().get()[0];
		m[1] = getOrigin().get()[1];

		// first vec
		firstVec[0] = 1;
		firstVec[1] = 0;

		return true;

	}

}
