/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.statistics;

import geogebra.kernel.AlgoDrawInformation;
import geogebra.kernel.AlgoFunctionAreaSums;
import geogebra.kernel.Construction;
import geogebra.kernel.EuclidianViewCE;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.arithmetic.NumberValue;

/**
 * @author G. Sturr
 * @version 2011-06-21
 */

public class AlgoPoissonBarChart extends AlgoFunctionAreaSums implements EuclidianViewCE{

	private static final long serialVersionUID = 1L;

	public AlgoPoissonBarChart(Construction cons, String label, 
			NumberValue mean) {
        super(cons,label, mean, null, null, null, AlgoFunctionAreaSums.TYPE_BARCHART_POISSON);
        cons.registerEuclidianViewCE(this);
    }
	
	
	public AlgoPoissonBarChart(Construction cons, String label, 
			NumberValue mean, GeoBoolean isCumulative) {
        super(cons,label, mean, null, null, isCumulative, AlgoFunctionAreaSums.TYPE_BARCHART_POISSON);
        cons.registerEuclidianViewCE(this);
    }
	
	private AlgoPoissonBarChart( 
			NumberValue mean, GeoBoolean isCumulative,NumberValue a,NumberValue b,double[]vals,
			double[]borders,int N) {
        super(mean, null, null, isCumulative, AlgoFunctionAreaSums.TYPE_BARCHART_POISSON,a,b,vals,borders,N);
    }

    public String getClassName() {
        return "AlgoPoissonBarChart";
    }

	public AlgoDrawInformation copy() {
		GeoBoolean b = (GeoBoolean)this.getIsCumulative();
		if(b!=null)b=(GeoBoolean)b.copy();
		return new AlgoPoissonBarChart(
				(NumberValue)this.getP1().deepCopy(kernel),				
				b,(NumberValue)this.getA().deepCopy(kernel),(NumberValue)this.getB().deepCopy(kernel),
				getValues().clone(),getLeftBorder().clone(),getIntervals());

	}
}

