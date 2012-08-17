/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.ChiSquaredDistribution;
import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;
import org.apache.commons.math.stat.inference.TTestImpl;

/**
 * Performs a chi square Goodness of Fit test or Test of Independence.
 * 
 * 
 * @author G. Sturr
 */
public class AlgoChiSquareTest extends AlgoElement {

	private GeoList geoList, geoList2; // input
	private GeoList result; // output
	private TTestImpl tTestImpl;
	private double[] val;
	private double p, testStat;
	private ChiSquaredDistribution chisquared = null;

	public AlgoChiSquareTest(Construction cons, String label, GeoList geoList) {
		this(cons, geoList, null);
		result.setLabel(label);
	}

	public AlgoChiSquareTest(Construction cons, String label, GeoList geoList,
			GeoList geoList2) {
		this(cons, geoList, geoList2);
		result.setLabel(label);
	}

	public AlgoChiSquareTest(Construction cons, GeoList geoList,
			GeoList geoList2) {
		super(cons);
		this.geoList = geoList;
		this.geoList2 = geoList2;
		result = new GeoList(cons);

		setInputOutput(); // for AlgoElement

		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoChiSquareTest;
	}

	@Override
	protected void setInputOutput() {

		if (geoList2 == null) {
			input = new GeoElement[1];
			input[0] = geoList;

		} else {
			input = new GeoElement[2];
			input[0] = geoList;
			input[1] = geoList2;
		}

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

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
		if (chisquared == null || chisquared.getDegreesOfFreedom() != df)
			chisquared = new ChiSquaredDistributionImpl(df);

		return chisquared;
	}

	@Override
	public final void compute() {

		int df;
		int rows = geoList.size();
		int columns = 0;

		
		if (!geoList.isDefined() || rows < 2) {
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
		double[][] diff = null;

		// store observed and expected values in arrays

		if (!geoList.isMatrix()) { // number list: GOF test
			columns = 1;
			df = rows - 1;
			observed = new double[rows][columns];
			expected = new double[rows][columns];
			
			for (int i = 0; i < rows; i++) {
				GeoElement geo = geoList.get(i);
				GeoElement geo2 = geoList2.get(i);
				if (geo.isNumberValue() && geo2.isNumberValue()) {
					observed[i][0] = ((NumberValue) geo).getDouble();
					expected[i][0] = ((NumberValue) geo2).getDouble();
				} else {
					result.setUndefined();
					return;
				}
			}
		}

		else { // matrix: test of independence

			columns = ((GeoList) geoList.get(0)).size();
			observed = new double[rows][columns];
			expected = new double[rows][columns];
			df = (columns - 1) * (rows - 1);

			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {

					// get observed values
					GeoElement geo = ((GeoList) geoList.get(i)).get(j);
					if (geo.isNumberValue()) {
						observed[i][j] = ((NumberValue) geo).getDouble();
					} else {
						result.setUndefined();
						return;
					}

					// get expected values
					if (geoList2 != null) {
						GeoElement geo2 = ((GeoList) geoList2.get(i)).get(j);
						if (geo2.isNumberValue()) {
							expected[i][j] = ((NumberValue) geo2).getDouble();
						} else {
							result.setUndefined();
							return;
						}
					}
				}
			}

			// compute expected values if not given
			if (geoList2 == null) {

				int[] columnSum = new int[rows];
				for (int j = 0; j < columns; j++) {
					columnSum[j] = 0;
				}

				int[] rowSum = new int[columns];
				for (int i = 0; i < rows; i++) {
					rowSum[i] = 0;
				}
				int total = 0;
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
		diff = new double[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				diff[i][j] = (observed[i][j] - expected[i][j])
						* (observed[i][j] - expected[i][j]) / expected[i][j];
				testStat += diff[i][j];
			}
		}

		try {

			// get p
			p = 1 - getChiSquaredDistribution(df).cumulativeProbability(
					testStat);

			// put results into the output list
			result.clear();
			result.add(new GeoNumeric(cons, p));
			result.add(new GeoNumeric(cons, testStat));

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MathException e) {
			e.printStackTrace();
		}

	}

	// TODO Consider locusequability

}
