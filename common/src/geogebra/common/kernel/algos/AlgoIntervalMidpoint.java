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
import geogebra.common.kernel.geos.GeoInterval;


public class AlgoIntervalMidpoint extends AlgoIntervalAbstract {


	public AlgoIntervalMidpoint(Construction cons, String label, GeoInterval s) {
		super(cons, label, s);
	}

	@Override
	public Commands getClassName() {
        return Commands.Midpoint;
    }

    @Override
	public final void compute() {   	
    	result.setValue(interval.getMidPoint());
    }

	// TODO Consider locusequability
    
}
