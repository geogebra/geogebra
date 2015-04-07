/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

public class AlgoPointVector extends AlgoPointVectorND {

	public AlgoPointVector(Construction cons, String label, GeoPointND P,
			GeoVectorND v) {
		super(cons, label, P, v);
	}

	@Override
	public final void compute() {
		Q.setCoords(((GeoPoint) P).inhomX + ((GeoVector) v).x,
				((GeoPoint) P).inhomY + ((GeoVector) v).y, 1.0);
	}

	@Override
	protected GeoPointND newGeoPoint(Construction cons1) {
		return new GeoPoint(cons1);
	}

}
