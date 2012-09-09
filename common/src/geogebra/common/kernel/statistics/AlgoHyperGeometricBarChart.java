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
import geogebra.common.kernel.algos.AlgoBarChart;
import geogebra.common.kernel.algos.DrawInformationAlgo;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.util.Cloner;

/**
 * @author G. Sturr
 * @version 2011-06-21
 */

public class AlgoHyperGeometricBarChart extends AlgoBarChart {

	public AlgoHyperGeometricBarChart(Construction cons, String label,
			NumberValue n, NumberValue p, NumberValue sampleSize) {
		super(cons, label, n, p, sampleSize, null,
				AlgoBarChart.TYPE_BARCHART_HYPERGEOMETRIC);
	}

	public AlgoHyperGeometricBarChart(Construction cons, String label,
			NumberValue n, NumberValue p, NumberValue sampleSize,
			GeoBoolean isCumulative) {
		super(cons, label, n, p, sampleSize, isCumulative,
				AlgoBarChart.TYPE_BARCHART_HYPERGEOMETRIC);
	}

	private AlgoHyperGeometricBarChart(NumberValue n, NumberValue p,
			NumberValue sampleSize, GeoBoolean isCumulative, NumberValue a,
			NumberValue b, double[] vals, double[] borders, int N) {
		super(n, p, sampleSize, isCumulative,
				AlgoBarChart.TYPE_BARCHART_HYPERGEOMETRIC, a, b, vals,
				borders, N);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoHyperGeometricBarChart;
	}

	
	public DrawInformationAlgo copy() {
		GeoBoolean b = (GeoBoolean) this.getIsCumulative();
		if (b != null) {
			b = (GeoBoolean) b.copy();
		}
		return new AlgoHyperGeometricBarChart((NumberValue) this.getP1()
				.deepCopy(kernel), (NumberValue) this.getP2().deepCopy(kernel),
				(NumberValue) this.getP3().deepCopy(kernel), b,
				(NumberValue) this.getA().deepCopy(kernel), (NumberValue) this
						.getB().deepCopy(kernel), Cloner.clone(getValues()),
				Cloner.clone(getLeftBorder()), getIntervals());
	}
	
}
