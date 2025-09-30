/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.StringUtil;

/**
 * 
 * 
 * @author G. Sturr
 */
public class AlgoZProportion2Test extends AlgoElement {
	// input
	private GeoNumeric proportion;
	private GeoNumeric n;
	private GeoNumeric proportion2;
	private GeoNumeric n_2;
	private GeoText tail;

	private GeoList result; // output
	private double se;

	/**
	 * @param cons
	 *            construction
	 * @param proportion
	 *            sample proportion
	 * @param n
	 *            sample size
	 * @param proportion2
	 *            second sample proportion
	 * @param n_2
	 *            second sample size
	 * @param tail
	 *            &lt; or &gt; for one-sided test, default two-sided
	 */
	public AlgoZProportion2Test(Construction cons, GeoNumeric proportion,
			GeoNumeric n, GeoNumeric proportion2, GeoNumeric n_2,
			GeoText tail) {
		super(cons);
		this.tail = tail;
		this.proportion = proportion;
		this.n = n;
		this.proportion2 = proportion2;
		this.n_2 = n_2;
		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.ZProportion2Test;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[5];
		input[0] = proportion;
		input[1] = n;
		input[2] = proportion2;
		input[3] = n_2;
		input[4] = tail;

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return {P value, Z test statistic}
	 */
	public GeoList getResult() {
		return result;
	}

	/**
	 * @return standard error
	 */
	public double getSE() {
		return se;
	}

	@Override
	public final void compute() {
		if (!StringUtil.isInequality(tail.getTextString())) {
			result.setUndefined();
			return;
		}

		double n1 = n.getDouble();
		double phat1 = proportion.getDouble();
		double n2 = n_2.getDouble();
		double phat2 = proportion2.getDouble();

		double x1 = phat1 * n1;
		double x2 = phat2 * n2;
		double phatTotal = (x1 + x2) / (n1 + n2);
		se = Math.sqrt(phatTotal * (1 - phatTotal) * (1 / n1 + 1 / n2));
		double testStatistic = (phat1 - phat2) / se;

		NormalDistribution normalDist = new NormalDistribution(0, 1);
		double P = 0;
		try {
			P = normalDist.cumulativeProbability(testStatistic);
		} catch (Exception e) {
			result.setUndefined();
			return;
		}

		P = AlgoZMeanTest.adjustPValue(P, testStatistic, tail);

		// put these results into the output list
		result.clear();
		result.addNumber(P, null);
		result.addNumber(testStatistic, null);

	}

}
