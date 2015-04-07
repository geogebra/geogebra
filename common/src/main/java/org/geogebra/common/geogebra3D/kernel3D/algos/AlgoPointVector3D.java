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

public class AlgoPointVector3D extends AlgoPointVectorND {

	public AlgoPointVector3D(Construction cons, String label, GeoPointND P,
			GeoVectorND v) {
		super(cons, label, P, v);
	}

	@Override
	public final void compute() {
		Q.setCoords(P.getInhomCoordsInD3().add(v.getCoordsInD3()), false);
	}

	@Override
	protected GeoPointND newGeoPoint(Construction cons1) {
		return new GeoPoint3D(cons1);
	}

}
