/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * Sum of a list of numbers or grouped data
 * @author Michael Borcherds
 * @version 2008-02-18
 */

public class AlgoSum extends AlgoStats1D {

	

	public AlgoSum(Construction cons, String label, GeoList geoList) {
        super(cons,label,geoList,AlgoStats1D.STATS_SIGMAX);
    }
    
	public AlgoSum(Construction cons, GeoList geoList) {
        super(cons,geoList,AlgoStats1D.STATS_SIGMAX);
    }
    
	public AlgoSum(Construction cons, String label, GeoList geoList, GeoList freq) {
        super(cons,label,geoList,freq,AlgoStats1D.STATS_SIGMAX);
    }
    
	public AlgoSum(Construction cons, GeoList geoList, GeoList freq) {
        super(cons,geoList,freq, AlgoStats1D.STATS_SIGMAX);
    }
	
    public AlgoSum(Construction cons, String label, GeoList geoList, GeoNumeric n) {
        super(cons,label,geoList,n,AlgoStats1D.STATS_SIGMAX);
    }

    @Override
	public Commands getClassName() {
		return Commands.Sum;
	}
}
