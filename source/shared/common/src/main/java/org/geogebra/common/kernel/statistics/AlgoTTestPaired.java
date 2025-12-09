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

import org.apache.commons.math3.stat.inference.TTest;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Performs a paired t-test.
 * 
 * 
 * @author G. Sturr
 */
public class AlgoTTestPaired extends AlgoElement {

	private GeoList geoList0; // input
	private GeoList geoList1; // input
	private GeoText tail; // input
	private GeoList result; // output
	private TTest tTestImpl;
	private double[] val0;
	private double[] val1;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param geoList0
	 *            first sample
	 * @param geoList1
	 *            second sample
	 * @param tail
	 *            one of &lt;, &gt; for one-sided test; two-sided otherwise
	 */
	public AlgoTTestPaired(Construction cons, String label, GeoList geoList0,
			GeoList geoList1, GeoText tail) {
		super(cons);
		this.geoList0 = geoList0;
		this.geoList1 = geoList1;
		this.tail = tail;
		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
		result.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.TTestPaired;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[3];
		input[0] = geoList0;
		input[1] = geoList1;
		input[2] = tail;

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return result;
	}

	private double adjustedPValue(double p, double testStatistic) {
		return AlgoTTest.adjustedPValue(p, testStatistic, tail);
	}

	@Override
	public final void compute() {

		if (!StringUtil.isInequality(tail.getTextString())) {
			result.setUndefined();
			return;
		}

		double p, testStat;

		// sample data input

		int size = geoList0.size();
		if (!geoList1.isDefined() || geoList1.size() != size) {
			result.setUndefined();
			return;
		}

		// create number value arrays
		val0 = new double[size];
		val1 = new double[size];
		GeoElement geo0, geo1;

		for (int i = 0; i < size; i++) {
			geo0 = geoList0.get(i);
			geo1 = geoList1.get(i);
			if (geo0 instanceof NumberValue && geo1 instanceof NumberValue) {
				val0[i] = geo0.evaluateDouble();
				val1[i] = geo1.evaluateDouble();

			} else {
				result.setUndefined();
				return;
			}
		}

		try {

			// get the test statistic and p
			if (tTestImpl == null) {
				tTestImpl = new TTest();
			}
			testStat = tTestImpl.pairedT(val0, val1);
			p = tTestImpl.pairedTTest(val0, val1);
			testStat = tTestImpl.pairedT(val0, val1);
			p = adjustedPValue(p, testStat);

			// put these results into the output list
			result.clear();
			result.addNumber(p, null);
			result.addNumber(testStat, null);

		} catch (RuntimeException e) {
			// catches ArithmeticException, IllegalStateException and
			// ArithmeticException
			Log.debug(e);
		}

	}

}
