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
public class AlgoZMeanEstimate extends AlgoElement {

	private GeoNumeric mean;
	private GeoNumeric sd;
	private GeoNumeric n;
	private GeoNumeric level; // input
	GeoList list;
	private GeoList result; // output
	private double me;

	/**
	 * @param cons
	 *            construction
	 * @param mean
	 *            mean
	 * @param sd
	 *            standard deviation
	 * @param n
	 *            n
	 * @param level
	 *            level
	 */
	public AlgoZMeanEstimate(Construction cons, GeoNumeric mean, GeoNumeric sd,
			GeoNumeric n, GeoNumeric level) {
		super(cons);
		this.mean = mean;
		this.sd = sd;
		this.n = n;
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
	 *            sample
	 * @param sd
	 *            standard deviation
	 * @param level
	 *            level of confidence
	 */
	public AlgoZMeanEstimate(Construction cons, String label, GeoList list,
			GeoNumeric sd, GeoNumeric level) {
		super(cons);

		this.list = list;
		this.sd = sd;
		this.level = level;
		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
		result.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.ZMeanEstimate;
	}

	@Override
	protected void setInputOutput() {

		if (list == null) {
			input = new GeoElement[4];
			input[0] = mean;
			input[1] = sd;
			input[2] = n;
			input[3] = level;
		} else {
			input = new GeoElement[3];
			input[0] = list;
			input[1] = sd;
			input[2] = level;
		}

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return margin of error
	 */
	public double getME() {
		return me;
	}

	/**
	 * @return {lower confidence limit, upper confidence limit}.
	 */
	public GeoList getResult() {
		return result;
	}

	@Override
	public final void compute() {

		if (!sd.isDefined() || !level.isDefined()) {
			result.setUndefined();
			return;
		}

		double n1, mean1;
		double sd1 = sd.getDouble();
		double cLevel = level.getDouble();

		if (list == null) {

			if (!n.isDefined() || !mean.isDefined()) {
				result.setUndefined();
				return;
			}

			n1 = n.getDouble();
			mean1 = mean.getDouble();

		} else {

			if (!list.isDefined()) {
				result.setUndefined();
				return;
			}

			n1 = list.size();

			mean1 = list.mean();
		}

		NormalDistribution normalDist = new NormalDistribution(0, 1);

		double critZ = 0;

		try {
			critZ = normalDist.inverseCumulativeProbability((1 - cLevel) / 2);
		} catch (Exception e) {
			result.setUndefined();
			return;
		}

		double se = sd1 / Math.sqrt(n1);
		double z = Math.abs(critZ);
		me = z * se;

		// put these results into the output list
		result.clear();
		result.addNumber(mean1 - me, null);
		result.addNumber(mean1 + me, null);

	}

}
