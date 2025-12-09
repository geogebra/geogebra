/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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
