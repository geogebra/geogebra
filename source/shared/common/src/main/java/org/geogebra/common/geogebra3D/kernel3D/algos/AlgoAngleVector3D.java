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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 *
 * @author mathieu
 */
public class AlgoAngleVector3D extends AlgoAngleElement3D {

	private Coords o;

	/**
	 * @param cons
	 *            construction
	 * @param vec
	 *            vector
	 */
	public AlgoAngleVector3D(Construction cons, GeoVector3D vec) {
		super(cons, vec);
	}

	@Override
	protected final Coords getVectorCoords() {
		return ((GeoVector3D) vec).getCoordsInD3().copyVector();
	}

	@Override
	protected final Coords getOrigin() {
		return o;
	}

	@Override
	protected final void setOrigin() {
		GeoPointND start = getStartPoint((GeoVector3D) vec);
		if (centerIsNotDrawable(start)) {
			o = Coords.UNDEFINED;
		} else {
			o = start.getInhomCoordsInD3();
		}
	}

}
