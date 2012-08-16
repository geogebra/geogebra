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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoInterval;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


public class AlgoIntervalMin extends AlgoIntervalAbstract {

	public AlgoIntervalMin(Construction cons, String label, GeoInterval s) {
		super(cons, label, s);
	}

	@Override
	public Algos getClassName() {
        return Algos.AlgoIntervalMin;
    }

    @Override
	public final void compute() {    	
    	result.setValue(interval.getMin());
    }

	// TODO Consider locusequability
    
}
