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
 * Product of list elements
 * @author Michael Borcherds
 * @version 2008-07-27
 */

public class AlgoProduct extends AlgoStats1D {

	

	public AlgoProduct(Construction cons, String label, GeoList geoList) {
        super(cons,label,geoList,AlgoStats1D.STATS_PRODUCT);
    }
    
	public AlgoProduct(Construction cons, String label, GeoList geoList, GeoList freq) {
        super(cons,label,geoList,freq, AlgoStats1D.STATS_PRODUCT);
    }
	
    public AlgoProduct(Construction cons, String label, GeoList geoList, GeoNumeric n) {
        super(cons,label,geoList,n,AlgoStats1D.STATS_PRODUCT);
    }

    @Override
	public Commands getClassName() {
    	return Commands.Product;
    } 
}
