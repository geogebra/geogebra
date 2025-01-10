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
public class AlgoZProportionTest extends AlgoElement {
	// input
	private GeoNumeric hypProportion;
	private GeoNumeric proportion;
	private GeoNumeric n;
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
	 * @param hypProportion
	 *            hypothesised proportion
	 * @param tail
	 *            &lt; &gt; for one-sided test, default two-sided
	 */
	public AlgoZProportionTest(Construction cons, GeoNumeric proportion,
			GeoNumeric n, GeoNumeric hypProportion, GeoText tail) {
		super(cons);
		this.hypProportion = hypProportion;
		this.tail = tail;
		this.proportion = proportion;
		this.n = n;
		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.ZProportionTest;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[4];
		input[0] = proportion;
		input[1] = n;
		input[2] = hypProportion;
		input[3] = tail;

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
		if (!(StringUtil.isInequality(tail.getTextString()))) {
			result.setUndefined();
			return;
		}

		double n1 = n.getDouble();
		double hyp = hypProportion.getDouble();
		double phat = proportion.getDouble();

		se = Math.sqrt(hyp * (1 - hyp) / n1);
		double testStatistic = (phat - hyp) / se;

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
