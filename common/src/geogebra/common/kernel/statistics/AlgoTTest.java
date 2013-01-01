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
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.util.StringUtil;

import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.StatisticalSummaryValues;
import org.apache.commons.math.stat.inference.TTestImpl;

/**
 * Performs a one sample t-test of a mean.
 * 
 * 
 * @author G. Sturr
 */
public class AlgoTTest extends AlgoElement {

	private GeoList geoList; // input
	private GeoNumeric hypMean, mean, sd, n; // input
	private GeoText tail; // input
	private GeoList result; // output
	private TTestImpl tTestImpl;
	private double[] val;
	private double p, testStat, se;

	public AlgoTTest(Construction cons, String label, GeoList geoList,
			GeoNumeric hypMean, GeoText tail) {
		super(cons);
		this.geoList = geoList;
		this.hypMean = hypMean;
		this.tail = tail;
		this.mean = null;
		this.sd = null;
		this.n = null;
		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
		result.setLabel(label);
	}

	public AlgoTTest(Construction cons, String label, GeoNumeric mean,
			GeoNumeric sd, GeoNumeric n, GeoNumeric hypMean, GeoText tail) {
		this(cons, mean, sd, n, hypMean, tail);
		result.setLabel(label);
	}

	public AlgoTTest(Construction cons, GeoNumeric mean, GeoNumeric sd,
			GeoNumeric n, GeoNumeric hypMean, GeoText tail) {
		super(cons);
		this.geoList = null;
		this.hypMean = hypMean;
		this.tail = tail;
		this.mean = mean;
		this.sd = sd;
		this.n = n;
		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.TTest;
	}

	@Override
	protected void setInputOutput() {

		if (geoList != null) {
			input = new GeoElement[3];
			input[0] = geoList;
			input[1] = hypMean;
			input[2] = tail;

		} else {
			input = new GeoElement[5];
			input[0] = mean;
			input[1] = sd;
			input[2] = n;
			input[3] = hypMean;
			input[4] = tail;
		}

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return result;
	}

	private double adjustedPValue(double p, double testStatistic) {

		// two sided test
		if (StringUtil.isNotEqual(tail.getTextString())) {
			return p;
		}

		// one sided test
		else if ((tail.getTextString().equals(">") && testStatistic > 0)
				|| (tail.getTextString().equals("<") && testStatistic < 0)) {
			return p / 2;
		} else {
			return 1 - p / 2;
		}

	}

	@Override
	public final void compute() {

		if (!(StringUtil.isInequality(tail.getTextString()))) {
			result.setUndefined();
			return;
		}

		// sample data input
		if (input.length == 3) {

			int size = geoList.size();
			if (!geoList.isDefined() || size < 2) {
				result.setUndefined();
				return;
			}

			val = new double[size];

			for (int i = 0; i < size; i++) {
				GeoElement geo = geoList.get(i);
				if (geo.isNumberValue()) {
					NumberValue num = (NumberValue) geo;
					val[i] = num.getDouble();

				} else {
					result.setUndefined();
					return;
				}
			}

			try {

				// get the test statistic and p
				if (tTestImpl == null)
					tTestImpl = new TTestImpl();
				testStat = tTestImpl.t(hypMean.getDouble(), val);
				p = tTestImpl.tTest(hypMean.getDouble(), val);
				p = adjustedPValue(p, testStat);

				// put these results into the output list
				result.clear();
				result.add(new GeoNumeric(cons, p));
				result.add(new GeoNumeric(cons, testStat));

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (MathException e) {
				e.printStackTrace();
			}

			// sample statistics input
		} else {



			// check for valid standard deviation and sample size
			if (sd.getDouble() < 0 || n.getDouble() < 2) {
				result.setUndefined();
				return;
			}

			try {
				StatisticalSummaryValues sumStats = new StatisticalSummaryValues(
						mean.getDouble(), sd.getDouble() * sd.getDouble(),
						(long) n.getDouble(), -1, -1, -1);

				// get the test statistic and p
				if (tTestImpl == null)
					tTestImpl = new TTestImpl();
				testStat = tTestImpl.t(hypMean.getDouble(), sumStats);
				p = tTestImpl.tTest(hypMean.getDouble(), sumStats);
				p = adjustedPValue(p, testStat);

				// put these results into the output list
				result.clear();
				result.add(new GeoNumeric(cons, p));
				result.add(new GeoNumeric(cons, testStat));

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				result.setUndefined();
				return;
				
			} catch (MathException e) {
				e.printStackTrace();
				result.setUndefined();
				return;
			}

		}

	}

	// TODO Consider locusequability

}
