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
public class AlgoZProportion2Estimate extends AlgoElement {
	// input
	private GeoNumeric proportion;
	private GeoNumeric n;
	private GeoNumeric proportion2;
	private GeoNumeric n_2;
	private GeoNumeric level;

	private GeoList result; // output
	private double se;
	private double me;

	/**
	 * @param cons
	 *            construction
	 * @param proportion
	 *            first sample proportion
	 * @param n
	 *            first sample size
	 * @param proportion2
	 *            second sample proportion
	 * @param n_2
	 *            second sample size
	 * @param level
	 *            confidence level
	 */
	public AlgoZProportion2Estimate(Construction cons, GeoNumeric proportion,
			GeoNumeric n, GeoNumeric proportion2, GeoNumeric n_2,
			GeoNumeric level) {
		super(cons);
		this.proportion = proportion;
		this.n = n;
		this.proportion2 = proportion2;
		this.n_2 = n_2;
		this.level = level;
		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.ZProportion2Estimate;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[5];
		input[0] = proportion;
		input[1] = n;
		input[2] = proportion2;
		input[3] = n_2;
		input[4] = level;

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

	/**
	 * @return standard error
	 */
	public double getSE() {
		return se;
	}

	@Override
	public final void compute() {

		double n1 = n.getDouble();
		double phat1 = proportion.getDouble();
		double n2 = n_2.getDouble();
		double phat2 = proportion2.getDouble();
		double cLevel = level.getDouble();

		NormalDistribution normalDist = new NormalDistribution(0, 1);

		double critZ = 0;

		try {
			critZ = normalDist.inverseCumulativeProbability((1 - cLevel) / 2);
		} catch (Exception e) {
			result.setUndefined();
			return;
		}

		double stat = phat1 - phat2;
		se = Math.sqrt(phat1 * (1 - phat1) / n1 + phat2 * (1 - phat2) / n2);
		double z = Math.abs(critZ);
		me = z * se;

		// put these results into the output list
		result.clear();
		result.addNumber(stat - me, null);
		result.addNumber(stat + me, null);

	}

}
