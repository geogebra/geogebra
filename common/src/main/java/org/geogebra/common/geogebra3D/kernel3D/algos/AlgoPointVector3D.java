/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoPointVectorND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;

public class AlgoPointVector3D extends AlgoPointVectorND {
	private Coords tmpCoords;

	public AlgoPointVector3D(Construction cons, GeoPointND P, GeoVectorND v) {
		super(cons, P, v);
	}

	@Override
	public final void compute() {
		tmpCoords.setAdd3(P.getInhomCoordsInD3(), v.getCoordsInD3());
		Q.setCoords(tmpCoords, false);
	}

	@Override
	protected GeoPointND newGeoPoint(Construction cons1) {
		// create tmpCoords here to avoid null at first compute
		tmpCoords = Coords.createInhomCoorsInD3();
		return new GeoPoint3D(cons1);
	}

}
