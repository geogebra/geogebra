/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.util.Cloner;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.algos.DrawInformationAlgo;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * @author G. Sturr
 * @version 2011-06-21
 */

public class AlgoHyperGeometricBarChart extends AlgoBarChart {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param n
	 *            n
	 * @param p
	 *            probability
	 * @param sampleSize
	 *            sample size
	 */
	public AlgoHyperGeometricBarChart(Construction cons, String label,
			GeoNumberValue n, GeoNumberValue p, GeoNumberValue sampleSize) {
		super(cons, label, n, p, sampleSize, null,
				AlgoBarChart.TYPE_BARCHART_HYPERGEOMETRIC);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param n
	 *            n
	 * @param p
	 *            probability
	 * @param sampleSize
	 *            sample size
	 * @param isCumulative
	 *            cumulative?
	 */
	public AlgoHyperGeometricBarChart(Construction cons, String label,
			GeoNumberValue n, GeoNumberValue p, GeoNumberValue sampleSize,
			GeoBoolean isCumulative) {
		super(cons, label, n, p, sampleSize, isCumulative,
				AlgoBarChart.TYPE_BARCHART_HYPERGEOMETRIC);
	}

	private AlgoHyperGeometricBarChart(GeoNumberValue n, GeoNumberValue p,
			GeoNumberValue sampleSize, GeoBoolean isCumulative,
			GeoNumberValue a, GeoNumberValue b, double[] vals, double[] borders) {
		super(n, p, sampleSize, isCumulative,
				AlgoBarChart.TYPE_BARCHART_HYPERGEOMETRIC, a, b, vals, borders);
	}

	@Override
	public Commands getClassName() {
		return Commands.HyperGeometric;
	}

	@Override
	public DrawInformationAlgo copy() {
		GeoBoolean b = (GeoBoolean) this.getIsCumulative();
		if (b != null) {
			b = b.copy();
		}
		return new AlgoHyperGeometricBarChart(
				(GeoNumberValue) this.getP1().deepCopy(kernel),
				(GeoNumberValue) this.getP2().deepCopy(kernel),
				(GeoNumberValue) this.getP3().deepCopy(kernel), b,
				(GeoNumberValue) this.getA().deepCopy(kernel),
				(GeoNumberValue) this.getB().deepCopy(kernel),
				Cloner.clone(getValues()), Cloner.clone(getLeftBorder()));
	}

}
