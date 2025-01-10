/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.debug.Log;

/**
 * Calculates a one sample t-confidence interval estimate of a mean.
 * 
 * 
 * @author G. Sturr
 */
public class AlgoTMeanEstimate extends AlgoElement {
	// input
	private GeoList geoList;
	private GeoNumeric geoLevel;
	private GeoNumeric geoMean;
	private GeoNumeric geoSD;
	private GeoNumeric geoN;

	private GeoList result; // output

	private double[] val;
	private double level;
	private double mean;
	private double sd;
	private double n;
	private double me;
	private SummaryStatistics stats;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param geoList
	 *            sample
	 * @param geoLevel
	 *            confidence level
	 */
	public AlgoTMeanEstimate(Construction cons, String label, GeoList geoList,
			GeoNumeric geoLevel) {
		super(cons);
		this.geoList = geoList;
		this.geoLevel = geoLevel;
		this.geoMean = null;
		this.geoSD = null;
		this.geoN = null;

		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
		result.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param geoMean
	 *            sample mean
	 * @param geoSD
	 *            sample standard deviation
	 * @param geoN
	 *            sample size
	 * @param geoLevel
	 *            confidence level
	 */
	public AlgoTMeanEstimate(Construction cons, GeoNumeric geoMean,
			GeoNumeric geoSD, GeoNumeric geoN, GeoNumeric geoLevel) {
		super(cons);
		this.geoList = null;
		this.geoLevel = geoLevel;
		this.geoMean = geoMean;
		this.geoSD = geoSD;
		this.geoN = geoN;

		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.TMeanEstimate;
	}

	@Override
	protected void setInputOutput() {

		if (geoList != null) {
			input = new GeoElement[2];
			input[0] = geoList;
			input[1] = geoLevel;

		} else {
			input = new GeoElement[4];
			input[0] = geoMean;
			input[1] = geoSD;
			input[2] = geoN;
			input[3] = geoLevel;
		}

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting list
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

	private static double getMarginOfError(double sd, double n,
			double confLevel) throws ArithmeticException {
		TDistribution tDist = new TDistribution(n - 1);
		double a = tDist.inverseCumulativeProbability((confLevel + 1d) / 2);
		return a * sd / Math.sqrt(n);
	}

	@Override
	public final void compute() {
		try {
			// get statistics from sample data input
			if (input.length == 2) {
				int size = geoList.size();
				if (!geoList.isDefined() || size < 2) {
					result.setUndefined();
					return;
				}

				val = new double[size];
				for (int i = 0; i < size; i++) {
					GeoElement geo = geoList.get(i);
					if (geo instanceof NumberValue) {
						val[i] = geo.evaluateDouble();
					} else {
						result.setUndefined();
						return;
					}
				}

				stats = new SummaryStatistics();
				for (int i = 0; i < val.length; i++) {
					stats.addValue(val[i]);
				}

				n = stats.getN();
				sd = stats.getStandardDeviation();
				mean = stats.getMean();

			} else {
				mean = geoMean.getDouble();
				sd = geoSD.getDouble();
				n = geoN.getDouble();
			}

			level = geoLevel.getDouble();

			// validate statistics
			if (level < 0 || level > 1 || sd < 0 || n < 1) {
				result.setUndefined();
				return;
			}

			// get interval estimate
			me = getMarginOfError(sd, n, level);

			// return list = {low limit, high limit, mean, margin of error, df }
			result.clear();
			boolean oldSuppress = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			result.addNumber(mean - me, null);
			result.addNumber(mean + me, null);
			// result.addNumber( mean, null);
			// result.addNumber( me, null);
			// result.addNumber( n-1, null); // df
			cons.setSuppressLabelCreation(oldSuppress);

		} catch (RuntimeException e) {
			// catches ArithmeticException, IllegalStateException and
			// ArithmeticException
			Log.debug(e);
		}
	}

}