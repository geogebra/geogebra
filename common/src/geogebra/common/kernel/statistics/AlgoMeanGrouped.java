/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author Kamalaruban Parameswaran
 * @version 2012-03-06
 */

public class AlgoMeanGrouped extends AlgoStats1DGrouped {

	private static final long serialVersionUID = 1L;

	// OK
	public AlgoMeanGrouped(Construction cons, String label, GeoList geoList, GeoList geoList2) {
        super(cons,label,geoList, geoList2,AlgoStats1DGrouped.STATS_MEAN);
    }

	// OK
    public AlgoMeanGrouped(Construction cons, GeoList geoList, GeoList geoList2) {
        super(cons,geoList, geoList2, AlgoStats1DGrouped.STATS_MEAN);
	}

    // OK
	public Algos getClassName() {
        return Algos.AlgoMeanGrouped;
    }

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		// TODO Consider locusequability
		return false;
	}
}
