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
import geogebra.common.kernel.algos.DrawInformationAlgo;
import geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.util.Cloner;

/**
 * @author G. Sturr
 * @version 2011-06-21
 */

public class AlgoBernoulliBarChart extends AlgoFunctionAreaSums {

	/**
	 * @param cons construction
	 * @param label label
	 * @param p probability 
	 * @param isCumulative true for cumulative
	 */
	public AlgoBernoulliBarChart(Construction cons, String label, 
			NumberValue p, GeoBoolean isCumulative) {
        super(cons,label, p, null, null, isCumulative, AlgoFunctionAreaSums.TYPE_BARCHART_BERNOULLI);
    }
	
	private AlgoBernoulliBarChart( 
			NumberValue p, GeoBoolean isCumulative,NumberValue a,NumberValue b,double[]vals,
			double[]borders,int N) {
        super(p, null, null, isCumulative, AlgoFunctionAreaSums.TYPE_BARCHART_BERNOULLI,a,b,vals,borders,N);
    }
	
    @Override
	public Algos getClassName() {
        return Algos.AlgoBernoulliBarChart;
    }

	public DrawInformationAlgo copy() {		
		GeoBoolean b = (GeoBoolean)this.getIsCumulative();
		if(b!=null)b=(GeoBoolean)b.copy();
		
		return new AlgoBernoulliBarChart(
				(NumberValue)this.getP1().deepCopy(kernel),
				b,(NumberValue)this.getA().deepCopy(kernel),(NumberValue)this.getB().deepCopy(kernel),
				Cloner.clone(getValues()),Cloner.clone(getLeftBorder()),getIntervals());
	}
}

