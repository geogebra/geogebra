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
 * @author Michael
 */
public class AlgoPolarPoint extends AlgoPolarPointND {

	/** Creates new AlgoPolarLine */
	public AlgoPolarPoint(Construction cons, String label, GeoConicND c,
			GeoLineND line) {
		super(cons, label, c, line);
	}

	@Override
	protected GeoPointND newGeoPoint(Construction cons1) {
		return new GeoPoint(cons1);
	}

	// calc polar line of P relative to c
	@Override
	public final void compute() {
		c.polarPoint((GeoLine) line, (GeoPoint) polar);
	}

}
