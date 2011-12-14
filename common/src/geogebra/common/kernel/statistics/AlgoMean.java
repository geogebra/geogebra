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
import geogebra.common.kernel.geos.GeoList;

/**
 * Mean of a list
 * @author Michael Borcherds
 * @version 2008-02-18
 */

public class AlgoMean extends AlgoStats1D {

	private static final long serialVersionUID = 1L;

	public AlgoMean(Construction cons, String label, GeoList geoList) {
        super(cons,label,geoList,AlgoStats1D.STATS_MEAN);
    }

    public AlgoMean(Construction cons, GeoList geoList) {
        super(cons,geoList,AlgoStats1D.STATS_MEAN);
	}

	public String getClassName() {
        return "AlgoMean";
    }
}
