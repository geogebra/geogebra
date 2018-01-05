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
import org.geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import org.geogebra.common.kernel.algos.DrawInformationAlgo;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * @author G. Sturr
 * @version 2011-06-21
 */

public class AlgoBernoulliBarChart extends AlgoFunctionAreaSums {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param p
	 *            probability
	 * @param isCumulative
	 *            true for cumulative
	 */
	public AlgoBernoulliBarChart(Construction cons, String label,
			GeoNumberValue p, GeoBoolean isCumulative) {
		super(cons, label, p, isCumulative,
				SumType.BARCHART_BERNOULLI);
	}

	private AlgoBernoulliBarChart(GeoNumberValue p, GeoBoolean isCumulative,
			GeoNumberValue a, GeoNumberValue b, double[] vals, double[] borders,
			int N, Construction cons) {
		super(p, isCumulative, SumType.BARCHART_BERNOULLI, a, b,
				vals, borders, N, cons);
	}

	@Override
	public Commands getClassName() {
		return Commands.Bernoulli;
	}

	@Override
	public DrawInformationAlgo copy() {
		GeoBoolean b = (GeoBoolean) this.getIsCumulative();
		if (b != null) {
			b = b.copy();
		}

		return new AlgoBernoulliBarChart(
				(GeoNumberValue) this.getP1().deepCopy(kernel), b,
				(GeoNumberValue) this.getA().deepCopy(kernel),
				(GeoNumberValue) this.getB().deepCopy(kernel),
				Cloner.clone(getValues()), Cloner.clone(getLeftBorder()),
				getIntervals(), kernel.getConstruction());
	}
}
