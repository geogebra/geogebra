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

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoPolarLine extends AlgoPolarLineND {

	/** Creates new AlgoPolarLine */
	public AlgoPolarLine(Construction cons, String label, GeoConicND c,
			GeoPointND P) {
		super(cons, label, c, P);
	}

	@Override
	protected GeoLineND newGeoLine(Construction cons) {
		return new GeoLine(cons);
	}

	// calc polar line of P relativ to c
	@Override
	public final void compute() {
		c.polarLine((GeoPoint) P, (GeoLine) polar);
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnPolarLine(geo, this, scope);
	}
}
