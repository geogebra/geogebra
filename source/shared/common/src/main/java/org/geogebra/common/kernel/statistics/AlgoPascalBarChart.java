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
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.algos.DrawInformationAlgo;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * @author G. Sturr
 * @version 2011-06-21
 */

public class AlgoPascalBarChart extends AlgoBarChart {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param n
	 *            number of successes
	 * @param p
	 *            probability
	 */
	public AlgoPascalBarChart(Construction cons, String label, GeoNumberValue n,
			GeoNumberValue p) {
		super(cons, label, n, p, null, null, AlgoBarChart.TYPE_BARCHART_PASCAL);
		cons.registerEuclidianViewCE(this);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param n
	 *            number of successes
	 * @param p
	 *            probability
	 * @param isCumulative
	 *            cumulative?
	 */
	public AlgoPascalBarChart(Construction cons, String label, GeoNumberValue n,
			GeoNumberValue p, GeoBoolean isCumulative) {
		super(cons, label, n, p, null, isCumulative,
				AlgoBarChart.TYPE_BARCHART_PASCAL);
		cons.registerEuclidianViewCE(this);
	}

	private AlgoPascalBarChart(GeoNumberValue n, GeoNumberValue p,
			GeoBoolean isCumulative, GeoNumberValue a, GeoNumberValue b,
			double[] vals, double[] borders) {
		super(n, p, null, isCumulative, AlgoBarChart.TYPE_BARCHART_PASCAL, a, b,
				vals, borders);
	}

	@Override
	public Commands getClassName() {
		return Commands.Pascal;
	}

	@Override
	public DrawInformationAlgo copy() {
		GeoBoolean b = (GeoBoolean) this.getIsCumulative();
		if (b != null) {
			b = b.copy();
		}
		return new AlgoPascalBarChart(
				(GeoNumberValue) this.getP1().deepCopy(kernel),
				(GeoNumberValue) this.getP2().deepCopy(kernel), b,
				(GeoNumberValue) this.getA().deepCopy(kernel),
				(GeoNumberValue) this.getB().deepCopy(kernel),
				Cloner.clone(getValues()), Cloner.clone(getLeftBorder()));

	}

}
