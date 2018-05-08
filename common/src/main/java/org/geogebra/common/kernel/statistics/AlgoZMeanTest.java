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
public class AlgoZMeanTest extends AlgoElement {
	// input
	private GeoNumeric hypMean;
	private GeoNumeric mean;
	private GeoNumeric sd;
	private GeoNumeric n;
	private GeoList list;
	private GeoText tail;

	private GeoList result; // output

	/**
	 * @param cons
	 *            construction
	 * @param mean
	 *            sample mean
	 * @param sd
	 *            sample standard deviation
	 * @param n
	 *            sample size
	 * @param hypMean
	 *            hypothesised mean
	 * @param tail
	 *            &lt; or &gt; for one-sided test, default two-sided
	 */
	public AlgoZMeanTest(Construction cons, GeoNumeric mean, GeoNumeric sd,
			GeoNumeric n, GeoNumeric hypMean, GeoText tail) {
		super(cons);
		this.hypMean = hypMean;
		this.tail = tail;
		this.mean = mean;
		this.sd = sd;
		this.n = n;
		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement
		compute();

	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param list
	 *            sample
	 * @param sd
	 *            standard deviation
	 * @param hypMean
	 *            hypothesized mean
	 * @param tail
	 *            &lt; or &gt; for one-sided test, default two-sided
	 */
	public AlgoZMeanTest(Construction cons, String label, GeoList list,
			GeoNumeric sd, GeoNumeric hypMean, GeoText tail) {
		super(cons);
		this.hypMean = hypMean;
		this.tail = tail;
		this.list = list;
		this.sd = sd;
		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
		result.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.ZMeanTest;
	}

	@Override
	protected void setInputOutput() {

		if (list == null) {
			input = new GeoElement[5];
			input[0] = mean;
			input[1] = sd;
			input[2] = n;
			input[3] = hypMean;
			input[4] = tail;
		} else {
			input = new GeoElement[4];
			input[0] = list;
			input[1] = sd;
			input[2] = hypMean;
			input[3] = tail;

		}

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return {P value, Z test statistic}
	 */
	public GeoList getResult() {
		return result;
	}

	@Override
	public final void compute() {

		String testType;
		if (tail.getTextString().equals("<")) {
			testType = "left";
		} else if (tail.getTextString().equals(">")) {
			testType = "right";
		} else if (StringUtil.isNotEqual(tail.getTextString())) {
			testType = "two";
		} else {
			result.setUndefined();
			return;
		}

		double mean1;
		double n1;

		if (list == null) {
			mean1 = mean.getDouble();
			n1 = n.getDouble();
		} else {
			mean1 = list.mean();
			n1 = list.size();
		}

		double hyp = hypMean.getDouble();
		double sd1 = sd.getDouble();

		double se = sd1 / Math.sqrt(n1);
		double testStatistic = (mean1 - hyp) / se;

		NormalDistribution normalDist = new NormalDistribution(0, 1);
		double P = 0;
		try {
			P = normalDist.cumulativeProbability(testStatistic);
		} catch (Exception e) {
			result.setUndefined();
			return;
		}

		if ("right".equals(testType)) {
			P = 1 - P;
		} else if ("two".equals(testType)) {
			if (testStatistic < 0) {
				P = 2 * P;
			} else {
				P = 2 * (1 - P);
			}
		}

		// put these results into the output list
		result.clear();
		result.addNumber(P, null);
		result.addNumber(testStatistic, null);

	}

}
