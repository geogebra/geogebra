/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoInterval;


public class AlgoIntervalMax extends AlgoIntervalAbstract {

	public AlgoIntervalMax(Construction cons, String label, GeoInterval s) {
		super(cons, label, s);
	}

	@Override
	public String getClassName() {
        return "AlgoIntervalMax";
    }

    @Override
	public final void compute() {  	
    	result.setValue(interval.getMax());
    }
    
}
