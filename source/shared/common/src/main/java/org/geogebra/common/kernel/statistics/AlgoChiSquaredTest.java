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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.debug.Log;

/**
 * Performs a chi square Goodness of Fit test or Test of Independence.
 * 
 * 
 * @author G. Sturr
 */
public class AlgoChiSquaredTest extends AlgoElement {

	private final GeoList geoList1; // input
	private final GeoList geoList2; // input
	private final GeoList result; // output
	private double p;
	private ChiSquaredDistribution chiSquared = null;
	private final GeoNumberValue degreesOfFreedom;

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param geoList
	 *            first list or matrix
	 * @param geoList2
	 *            second list
	 */
	public AlgoChiSquaredTest(Construction cons, GeoList geoList,
			GeoList geoList2, GeoNumberValue degreesOfFreedom) {
		super(cons);
		this.geoList1 = geoList;
		this.geoList2 = geoList2;
		this.degreesOfFreedom = degreesOfFreedom;
		result = new GeoList(cons);

		setInputOutput(); // for AlgoElement

		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.ChiSquaredTest;
	}

	@Override
	protected void setInputOutput() {
		List<GeoElement> inputs = new ArrayList<>(3);
		inputs.add(geoList1);
		if (geoList2 != null) {
			inputs.add(geoList2);
		}
		if (degreesOfFreedom != null) {
			inputs.add(degreesOfFreedom.toGeoElement());
		}
		input = inputs.toArray(new GeoElement[0]);
		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return result
	 */
	public GeoList getResult() {
		return result;
	}

	/**
	 * @param df
	 *            degree of freedom
	 * @return implementation of ChiSquaredDistribution for given degree of
	 *         freedom
	 */
	ChiSquaredDistribution getChiSquaredDistribution(double df) {
		if (chiSquared == null || chiSquared.getDegreesOfFreedom() != df) {
			chiSquared = new ChiSquaredDistribution(df);
		}

		return chiSquared;
	}

	@Override
	public final void compute() {

		final int df;
		int rows = geoList1.size();
		int columns = 0;

		if (!geoList1.isDefined() || rows < 2) {
			result.setUndefined();
			return;
		}

		if (geoList2 != null) {
			if (!geoList2.isDefined() || geoList2.size() != rows) {
				result.setUndefined();
				return;
			}
		}

		double[][] observed = null;
		double[][] expected = null;

		// store observed and expected values in arrays

		// Three cases must be handled:
		// 1) <List of Observed, List of Expected> (the GOF test)
		// 2) <Matrix of Observed, Matrix of Expected>
		// 3) <Matrix of Observed>, here we compute the expected counts based on
		// the hypothesis of independence:
		// expected count = row sum * column sum / grand total)

		// if list1 is not a matrix, then we have the two list case
		if (!geoList1.isMatrix()) {
			if (geoList2 == null) {
				result.setUndefined();
				return;
			}
			columns = 1;
			df = degreesOfFreedom == null ? rows - 1 : (int) degreesOfFreedom.evaluateDouble();
			observed = new double[rows][columns];
			expected = new double[rows][columns];

			for (int i = 0; i < rows; i++) {
				GeoElement geo = geoList1.get(i);
				GeoElement geo2 = geoList2.get(i);
				if (geo instanceof NumberValue && geo2 instanceof NumberValue) {
					observed[i][0] = geo.evaluateDouble();
					expected[i][0] = geo2.evaluateDouble();
				} else {
					result.setUndefined();
					return;
				}
			}
		} else { // list1 is matrix

			columns = ((GeoList) geoList1.get(0)).size();
			observed = new double[rows][columns];
			expected = new double[rows][columns];
			df = (columns - 1) * (rows - 1);

			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {

					// get observed values
					GeoElement geo = ((GeoList) geoList1.get(i)).get(j);
					if (geo instanceof NumberValue) {
						observed[i][j] = geo.evaluateDouble();
					} else {
						result.setUndefined();
						return;
					}

					// get expected values if list2 exists (it must be a matrix)
					if (geoList2 != null) {
						GeoElement geo2 = ((GeoList) geoList2.get(i)).get(j);
						if (geo2 instanceof NumberValue) {
							expected[i][j] = geo2.evaluateDouble();
						} else {
							result.setUndefined();
							return;
						}
					}
				}
			}

			// compute expected values if list2 is not given
			if (geoList2 == null) {

				double[] columnSum = new double[columns];
				for (int j = 0; j < columns; j++) {
					columnSum[j] = 0;
				}

				double[] rowSum = new double[rows];
				double total = 0;
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < columns; j++) {
						rowSum[i] += observed[i][j];
						columnSum[j] += observed[i][j];
						total += observed[i][j];
					}
				}

				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < columns; j++) {
						expected[i][j] = rowSum[i] * columnSum[j] / total;
					}
				}
			}
		}

		// compute test statistic and chi-square contributions
		double[][] diff = new double[rows][columns];
		double testStat = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				diff[i][j] = (observed[i][j] - expected[i][j])
						* (observed[i][j] - expected[i][j]) / expected[i][j];
				testStat += diff[i][j];
			}
		}

		try {
			double leftArea = getChiSquaredDistribution(df)
					.cumulativeProbability(testStat);
			p = 1 - leftArea;
		} catch (RuntimeException e) {
			// catches ArithmeticException, IllegalStateException and
			// ArithmeticException
			Log.debug(e);
			result.setUndefined();
		}

		// put results into the output list
		result.clear();
		result.addNumber(p, null);
		result.addNumber(testStat, null);

	}

}
