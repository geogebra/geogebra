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
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.debug.Log;

/**
 * Calculates a t-confidence interval estimate of the difference of means.
 * 
 * 
 * @author G. Sturr
 */
public class AlgoTMean2Estimate extends AlgoElement {

	private GeoList geoList1;
	private GeoList geoList2; // input
	private GeoNumeric geoLevel;
	private GeoNumeric geoMean1;
	private GeoNumeric geoSD1;
	private GeoNumeric geoN1;
	private GeoNumeric geoMean2;
	private GeoNumeric geoSD2;
	private GeoNumeric
			geoN2; // input
	private GeoBoolean geoPooled; // input

	private GeoList result; // output

	private double[] val1;
	private double[] val2;
	private double level;
	private double mean1;
	private double var1;
	private double n1;
	private double mean2;
	private double var2;
	private double n2;
	private double me;
	private boolean pooled;
	private SummaryStatistics stats;
	private TDistribution tDist;
	private double difference;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param geoList1
	 *            first sample
	 * @param geoList2
	 *            second sample
	 * @param geoLevel
	 *            level of confidence
	 * @param geoPooled
	 *            pooled?
	 */
	public AlgoTMean2Estimate(Construction cons, String label, GeoList geoList1,
			GeoList geoList2, GeoNumeric geoLevel, GeoBoolean geoPooled) {
		super(cons);
		this.geoList1 = geoList1;
		this.geoList2 = geoList2;
		this.geoLevel = geoLevel;
		this.geoPooled = geoPooled;

		this.geoMean1 = null;
		this.geoSD1 = null;
		this.geoN1 = null;
		this.geoMean2 = null;
		this.geoSD2 = null;
		this.geoN2 = null;

		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
		result.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param geoMean1
	 *            first sample's mean
	 * @param geoSD1
	 *            first sample's standard deviation
	 * @param geoN1
	 *            first sample size
	 * @param geoMean2
	 *            second sample's mean
	 * @param geoSD2
	 *            second sample's standard deviation
	 * @param geoN2
	 *            second sample size
	 * @param geoLevel
	 *            level of confidence
	 * @param geoPooled
	 *            pooled?
	 */
	public AlgoTMean2Estimate(Construction cons, GeoNumeric geoMean1,
			GeoNumeric geoSD1, GeoNumeric geoN1, GeoNumeric geoMean2,
			GeoNumeric geoSD2, GeoNumeric geoN2, GeoNumeric geoLevel,
			GeoBoolean geoPooled) {
		super(cons);
		this.geoList1 = null;
		this.geoList2 = null;
		this.geoLevel = geoLevel;
		this.geoPooled = geoPooled;

		this.geoMean1 = geoMean1;
		this.geoSD1 = geoSD1;
		this.geoN1 = geoN1;
		this.geoMean2 = geoMean2;
		this.geoSD2 = geoSD2;
		this.geoN2 = geoN2;

		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.TMean2Estimate;
	}

	@Override
	protected void setInputOutput() {
		if (geoList1 != null) {
			input = new GeoElement[4];
			input[0] = geoList1;
			input[1] = geoList2;
			input[2] = geoLevel;
			input[3] = geoPooled;
		} else {
			input = new GeoElement[8];
			input[0] = geoMean1;
			input[1] = geoSD1;
			input[2] = geoN1;
			input[3] = geoMean2;
			input[4] = geoSD2;
			input[5] = geoN2;
			input[6] = geoLevel;
			input[7] = geoPooled;
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
	 * Computes approximate degrees of freedom for 2-sample t-estimate. (code
	 * from Apache commons, TTest class)
	 *
	 * @param v1
	 *            first sample variance
	 * @param v2
	 *            second sample variance
	 * @param n1
	 *            first sample n
	 * @param n2
	 *            second sample n
	 * @return approximate degrees of freedom
	 */
	private static double getDegreeOfFreedom(double v1, double v2, double n1,
			double n2, boolean pooled) {

		if (pooled) {
			return n1 + n2 - 2;
		}
		return (((v1 / n1) + (v2 / n2)) * ((v1 / n1) + (v2 / n2)))
				/ ((v1 * v1) / (n1 * n1 * (n1 - 1d))
						+ (v2 * v2) / (n2 * n2 * (n2 - 1d)));
	}

	/**
	 * Computes margin of error for 2-sample t-estimate; this is the half-width
	 * of the confidence interval
	 * 
	 * @param v1
	 *            first sample variance
	 * @param v2
	 *            second sample variance
	 * @param size1
	 *            first sample n
	 * @param size2
	 *            second sample n
	 * @param confLevel
	 *            confidence level
	 * @return margin of error for 2 mean interval estimate
	 * @throws ArithmeticException
	 *             when computation fails
	 */
	private double getMarginOfError(double v1, double size1, double v2, double size2,
			double confLevel, boolean pool) throws ArithmeticException {
		if (pool) {
			double pooledVariance = ((size1 - 1) * v1 + (size2 - 1) * v2)
					/ (size1 + size2 - 2);
			double se = Math.sqrt(pooledVariance * (1d / size1 + 1d / size2));
			tDist = new TDistribution(
					getDegreeOfFreedom(v1, v2, size1, size2, pool));
			double a = tDist.inverseCumulativeProbability((confLevel + 1d) / 2);
			return a * se;

		}
		double se = Math.sqrt((v1 / size1) + (v2 / size2));
		tDist = new TDistribution(
				getDegreeOfFreedom(v1, v2, size1, size2, pool));
		double a = tDist.inverseCumulativeProbability((confLevel + 1d) / 2);
		return a * se;

	}

	@Override
	public final void compute() {

		try {

			// get statistics from sample data input
			if (input.length == 4) {

				int size1 = geoList1.size();
				if (!geoList1.isDefined() || size1 < 2) {
					result.setUndefined();
					return;
				}

				int size2 = geoList2.size();
				if (!geoList2.isDefined() || size2 < 2) {
					result.setUndefined();
					return;
				}

				val1 = new double[size1];
				for (int i = 0; i < size1; i++) {
					GeoElement geo = geoList1.get(i);
					if (geo instanceof NumberValue) {
						val1[i] = geo.evaluateDouble();

					} else {
						result.setUndefined();
						return;
					}
				}

				val2 = new double[size2];
				for (int i = 0; i < size2; i++) {
					GeoElement geo = geoList2.get(i);
					if (geo instanceof NumberValue) {
						val2[i] = geo.evaluateDouble();

					} else {
						result.setUndefined();
						return;
					}
				}

				stats = new SummaryStatistics();
				for (int i = 0; i < val1.length; i++) {
					stats.addValue(val1[i]);
				}

				n1 = stats.getN();
				var1 = stats.getVariance();
				mean1 = stats.getMean();

				stats.clear();
				for (int i = 0; i < val2.length; i++) {
					stats.addValue(val2[i]);
				}

				n2 = stats.getN();
				var2 = stats.getVariance();
				mean2 = stats.getMean();

			} else {
				mean1 = geoMean1.getDouble();
				var1 = geoSD1.getDouble() * geoSD1.getDouble();
				n1 = geoN1.getDouble();

				mean2 = geoMean2.getDouble();
				var2 = geoSD2.getDouble() * geoSD2.getDouble();
				n2 = geoN2.getDouble();
			}

			level = geoLevel.getDouble();
			pooled = geoPooled.getBoolean();

			// validate statistics
			if (level < 0 || level > 1 || var1 < 0 || n1 < 1 || var2 < 0
					|| n2 < 1) {
				result.setUndefined();
				return;
			}

			// get interval estimate
			me = getMarginOfError(var1, n1, var2, n2, level, pooled);

			// return list = {low limit, high limit, difference, margin of
			// error, df }
			difference = mean1 - mean2;
			result.clear();

			result.addNumber(difference - me, null);
			result.addNumber(difference + me, null);
			// result.add(new GeoNumeric(cons, difference));
			// result.add(new GeoNumeric(cons, me));
			// result.add(new GeoNumeric(cons, getDegreeOfFreedom(var1, var2,
			// n1, n2, pooled)));

		} catch (RuntimeException e) {
			// catches ArithmeticException, IllegalStateException and
			// ArithmeticException
			Log.debug(e);
		}

	}

}