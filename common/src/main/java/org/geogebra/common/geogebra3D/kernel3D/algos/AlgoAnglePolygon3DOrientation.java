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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoAnglePointsND;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;

/**
 *
 * @author mathieu
 */
public class AlgoAnglePolygon3DOrientation extends AlgoAnglePolygon3D {

	private GeoDirectionND orientation;

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels
	 * @param poly
	 *            polygon
	 * @param orientation
	 *            orientation to determine clockwise
	 */
	public AlgoAnglePolygon3DOrientation(Construction cons, String[] labels,
			GeoPolygon poly, GeoDirectionND orientation) {
		super(cons, labels, poly, orientation, false);
	}

	@Override
	protected AlgoAnglePointsND newAlgoAnglePoints(Construction cons1) {
		return new AlgoAnglePoints3DOrientation(cons1, orientation, false);
	}

	@Override
	protected void setPolyAndOrientation(GeoPolygon p,
			GeoDirectionND orientation) {
		super.setPolyAndOrientation(p, orientation);
		this.orientation = orientation;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = poly;
		input[1] = (GeoElement) orientation;

		setDependencies();
	}
}
