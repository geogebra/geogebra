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
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.main.App;

import org.apache.commons.math.distribution.PoissonDistribution;



/**
 * 
 * @author G. Sturr
 */

public class AlgoPoisson extends AlgoDistribution {
	
	

	
	public AlgoPoisson(Construction cons, String label, NumberValue a,NumberValue b, GeoBoolean isCumulative) {
		super(cons, label, a, b, null, isCumulative); 
	}

	public AlgoPoisson(Construction cons, NumberValue a,NumberValue b, GeoBoolean isCumulative) {
		super(cons, a, b, null, isCumulative); 
	}

	@Override
	public Commands getClassName() {
		return Commands.Poisson;
	}

	@Override
	@SuppressWarnings("deprecation")
	public final void compute() {


		if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
			double param = a.getDouble();
			double val = b.getDouble();	
			try {
				PoissonDistribution dist = getPoissonDistribution(param);
				if(isCumulative.getBoolean())
					num.setValue(dist.cumulativeProbability(val));  // P(X <= val)
				else
					num.setValue(dist.probability(val));   // P(X = val)
			}
			catch (Exception e) {
				App.debug(e.getMessage());
				num.setUndefined();        			
			}
		} else
			num.setUndefined();
	}       


}



