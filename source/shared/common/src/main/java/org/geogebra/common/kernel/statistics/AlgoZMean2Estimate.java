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

/**
 * 
 * 
 * @author G. Sturr
 */
public class AlgoZMean2Estimate extends AlgoElement {
	// input
	private GeoNumeric mean;
	private GeoNumeric sd;
	private GeoNumeric n;
	private GeoNumeric mean_2;
	private GeoNumeric sd_2;
	private GeoNumeric n_2;
	private GeoNumeric level;
	private GeoList list;
	private GeoList list2;
	private GeoList result; // output
	private double me;
	private double se;

	/**
	 * @param cons
	 *            construction
	 * @param mean
	 *            first sample mean
	 * @param sd
	 *            first sample standard deviation
	 * @param n
	 *            first sample size
	 * @param mean_2
	 *            second sample mean
	 * @param sd_2
	 *            second sample standard deviation
	 * @param n_2
	 *            second sample size
	 * @param level
	 *            level of confidence
	 */
	public AlgoZMean2Estimate(Construction cons, GeoNumeric mean, GeoNumeric sd,
			GeoNumeric n, GeoNumeric mean_2, GeoNumeric sd_2, GeoNumeric n_2,
			GeoNumeric level) {
		super(cons);
		this.mean = mean;
		this.sd = sd;
		this.n = n;
		this.mean_2 = mean_2;
		this.sd_2 = sd_2;
		this.n_2 = n_2;
		this.level = level;
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
	 *            first sample
	 * @param list2
	 *            second sample
	 * @param sd
	 *            standard deviation
	 * @param sd_2
	 *            second standard deviation
	 * @param level
	 *            level of confidence
	 */
	public AlgoZMean2Estimate(Construction cons, String label, GeoList list,
			GeoList list2, GeoNumeric sd, GeoNumeric sd_2, GeoNumeric level) {
		super(cons);

		this.list = list;
		this.sd = sd;
		this.list2 = list2;
		this.sd_2 = sd_2;
		this.level = level;
		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
		result.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.ZMean2Estimate;
	}

	@Override
	protected void setInputOutput() {

		if (list == null) {
			input = new GeoElement[7];
			input[0] = mean;
			input[1] = sd;
			input[2] = n;
			input[3] = mean_2;
			input[4] = sd_2;
			input[5] = n_2;
			input[6] = level;
		} else {
			input = new GeoElement[5];
			input[0] = list;
			input[1] = sd;
			input[2] = list2;
			input[3] = sd_2;
			input[4] = level;
		}

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return {lower confidence limit, upper confidence limit}.
	 */
	public GeoList getResult() {
		return result;
	}

	/**
	 * @return margin of error
	 */
	public double getME() {
		return me;
	}

	public double getSE() {
		return se;
	}

	@Override
	public final void compute() {

		if (!sd.isDefined() || !sd_2.isDefined() || !level.isDefined()) {
			result.setUndefined();
			return;
		}

		double n1, n2, mean1, mean2;
		double sd1 = sd.getDouble();
		double sd2 = sd_2.getDouble();
		double cLevel = level.getDouble();

		if (list == null) {

			if (!n.isDefined() || !n_2.isDefined() || !mean.isDefined()
					|| !mean_2.isDefined()) {
				result.setUndefined();
				return;
			}

			n1 = n.getDouble();
			n2 = n_2.getDouble();
			mean1 = mean.getDouble();
			mean2 = mean_2.getDouble();

		} else {

			if (!list.isDefined() || !list2.isDefined()) {
				result.setUndefined();
				return;
			}

			n1 = list.size();
			n2 = list2.size();

			mean1 = list.mean();
			mean2 = list2.mean();
		}

		NormalDistribution normalDist = new NormalDistribution(0, 1);

		double critZ = 0;

		try {
			critZ = normalDist.inverseCumulativeProbability((1 - cLevel) / 2);
		} catch (Exception e) {
			result.setUndefined();
			return;
		}

		double stat = mean1 - mean2;
		se = Math.sqrt(sd1 * sd1 / n1 + sd2 * sd2 / n2);
		double z = Math.abs(critZ);
		me = z * se;

		// put these results into the output list
		result.clear();
		result.addNumber(stat - me, null);
		result.addNumber(stat + me, null);

	}

}
