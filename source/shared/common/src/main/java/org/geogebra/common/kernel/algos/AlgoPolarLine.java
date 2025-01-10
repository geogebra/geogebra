/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoPolarLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 */
public class AlgoPolarLine extends AlgoPolarLineND {

	/** Creates new AlgoPolarLine */
	public AlgoPolarLine(Construction cons, String label, GeoConicND c,
			GeoPointND P) {
		super(cons, label, c, P);
	}

	@Override
	protected GeoLineND newGeoLine(Construction cons1) {
		return new GeoLine(cons1);
	}

	// calc polar line of P relative to c
	@Override
	public final void compute() {
		c.polarLine((GeoPoint) P, (GeoLine) polar);
	}

}
