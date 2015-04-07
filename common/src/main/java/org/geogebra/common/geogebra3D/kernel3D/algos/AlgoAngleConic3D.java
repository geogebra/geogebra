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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 *
 * @author mathieu
 */
public class AlgoAngleConic3D extends AlgoAngleElement3D {

	private Coords o;

	public AlgoAngleConic3D(Construction cons, String label, GeoConic3D vec) {
		super(cons, label, vec);
	}

	@Override
	protected final Coords getVectorCoords() {

		Coords v = ((GeoConic3D) vec).getEigenvec3D(0).copyVector();
		return v;
	}

	@Override
	protected final Coords getOrigin() {
		return o;
	}

	@Override
	protected final void setOrigin() {
		o = ((GeoConic3D) vec).getMidpoint3D();
	}

}
