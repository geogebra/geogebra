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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoAnglePointsND;
import org.geogebra.common.kernel.algos.AlgoAnglePolygonND;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;

/**
 *
 * @author mathieu
 */
public class AlgoAnglePolygon3D extends AlgoAnglePolygonND {

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels
	 * @param poly
	 *            polygon
	 */
	public AlgoAnglePolygon3D(Construction cons, String[] labels,
			GeoPolygon poly) {
		this(cons, labels, poly, false);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels
	 * @param poly
	 *            polygon
	 * @param internalAngle
	 *            whether to return internal angles
	 */
	public AlgoAnglePolygon3D(Construction cons, String[] labels, GeoPolygon poly,
			boolean internalAngle) {
		this(cons, labels, poly, null, internalAngle);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels
	 * @param poly
	 *            polygon
	 * @param orientation
	 *            orientation
	 * @param internalAngle
	 *            if angles are internal
	 */
	public AlgoAnglePolygon3D(Construction cons, String[] labels,
			GeoPolygon poly, GeoDirectionND orientation, boolean internalAngle) {
		super(cons, labels, poly, orientation, internalAngle);
	}

	@Override
	protected AlgoAnglePointsND newAlgoAnglePoints(Construction cons1) {
		return new AlgoAnglePoints3DOrientation(cons1, getPolygon(),
				getPolygon().hasReverseNormal());
	}

	@Override
	final protected GeoAngle newGeoAngle(Construction cons1) {
		return GeoAngle3D.newAngle3DWithDefaultInterval(cons1);
	}

}
