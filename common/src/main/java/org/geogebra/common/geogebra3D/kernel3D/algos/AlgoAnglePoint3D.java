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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.matrix.Coords;

/**
 *
 * @author mathieu
 */
public class AlgoAnglePoint3D extends AlgoAngleElement3D {

	/**
	 * @param cons
	 *            construction
	 * @param vec
	 *            point
	 */
	public AlgoAnglePoint3D(Construction cons, GeoPoint3D vec) {
		super(cons, vec);
	}

	@Override
	protected final Coords getVectorCoords() {
		Coords v = ((GeoPoint3D) vec).getCoordsInD3().copyVector();
		v.setW(0);
		return v;
	}

	@Override
	protected final Coords getOrigin() {
		return Coords.O;
	}

	@Override
	protected final void setOrigin() {
		// nothing to do here
	}

}
