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

public class AlgoPoissonBarChart extends AlgoBarChart {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param mean
	 *            mean
	 */
	public AlgoPoissonBarChart(Construction cons, String label,
			GeoNumberValue mean) {
		super(cons, label, mean, null, null, null,
				AlgoBarChart.TYPE_BARCHART_POISSON);
		cons.registerEuclidianViewCE(this);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param mean
	 *            mean
	 * @param isCumulative
	 *            cumulative?
	 */
	public AlgoPoissonBarChart(Construction cons, String label,
			GeoNumberValue mean, GeoBoolean isCumulative) {
		super(cons, label, mean, null, null, isCumulative,
				AlgoBarChart.TYPE_BARCHART_POISSON);
		cons.registerEuclidianViewCE(this);
	}

	private AlgoPoissonBarChart(GeoNumberValue mean, GeoBoolean isCumulative,
			GeoNumberValue a, GeoNumberValue b, double[] vals, double[] borders) {
		super(mean, null, null, isCumulative,
				AlgoBarChart.TYPE_BARCHART_POISSON, a, b, vals, borders);
	}

	@Override
	public Commands getClassName() {
		return Commands.Poisson;
	}

	@Override
	public DrawInformationAlgo copy() {
		GeoBoolean b = (GeoBoolean) this.getIsCumulative();
		if (b != null) {
			b = b.copy();
		}
		return new AlgoPoissonBarChart(
				(GeoNumberValue) this.getP1().deepCopy(kernel), b,
				(GeoNumberValue) this.getA().deepCopy(kernel),
				(GeoNumberValue) this.getB().deepCopy(kernel),
				Cloner.clone(getValues()), Cloner.clone(getLeftBorder()));

	}

}
