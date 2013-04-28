package geogebra.gui.view.probcalculator;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.statistics.AlgoTMean2Estimate;
import geogebra.common.kernel.statistics.AlgoTMeanEstimate;
import geogebra.common.kernel.statistics.AlgoTTest;
import geogebra.common.kernel.statistics.AlgoTTest2;
import geogebra.common.kernel.statistics.AlgoZMean2Estimate;
import geogebra.common.kernel.statistics.AlgoZMean2Test;
import geogebra.common.kernel.statistics.AlgoZMeanEstimate;
import geogebra.common.kernel.statistics.AlgoZMeanTest;
import geogebra.common.kernel.statistics.AlgoZProportion2Estimate;
import geogebra.common.kernel.statistics.AlgoZProportion2Test;
import geogebra.common.kernel.statistics.AlgoZProportionEstimate;
import geogebra.common.kernel.statistics.AlgoZProportionTest;
import geogebra.main.AppD;

import java.util.ArrayList;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.ChiSquaredDistribution;
import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;

/**
 * @author G.Sturr
 * 
 */
public class StatisticsCalculatorProcessor {

	private AppD app;
	private Construction cons;
	private Kernel kernel;
	private StatisticsCalculator statCalc;
	private StatisticsCollection sc;

	private GeoNumeric n, n2, mean, mean2, sd, sd2, proportion, proportion2,
			nullHyp, level;
	private GeoText tail;
	private GeoBoolean pooled;
	private ArrayList<GeoElement> geos;

	private ChiSquaredDistribution chisquared;

	/**
	 * @param app
	 * @param statCalc
	 * @param sc
	 */
	public StatisticsCalculatorProcessor(AppD app,
			StatisticsCalculator statCalc, StatisticsCollection sc) {

		this.app = app;
		cons = app.getKernel().getConstruction();
		kernel = cons.getKernel();
		this.statCalc = statCalc;
		this.sc = sc;
		createGeos();
	}

	private void createGeos() {

		geos = new ArrayList<GeoElement>();

		n = new GeoNumeric(cons);
		geos.add(n);

		n2 = new GeoNumeric(cons);
		geos.add(n2);

		mean = new GeoNumeric(cons);
		geos.add(mean);

		mean2 = new GeoNumeric(cons);
		geos.add(mean2);

		sd = new GeoNumeric(cons);
		geos.add(sd);

		sd2 = new GeoNumeric(cons);
		geos.add(sd2);

		proportion = new GeoNumeric(cons);
		geos.add(proportion);

		proportion2 = new GeoNumeric(cons);
		geos.add(proportion2);

		nullHyp = new GeoNumeric(cons);
		geos.add(nullHyp);

		level = new GeoNumeric(cons);
		geos.add(level);

		tail = new GeoText(cons);
		geos.add(tail);

		pooled = new GeoBoolean(cons);

		// remove all geos from the construction
		for (GeoElement geo : geos) {
			cons.removeFromConstructionList(geo);
		}

	}

	private void updateGeoValues() {

		level.setValue(sc.level);
		nullHyp.setValue(sc.nullHyp);
		tail.setTextString(sc.tail);
		pooled.setValue(sc.pooled);

		switch (statCalc.getSelectedProcedure()) {

		case ZMEAN_TEST:
		case ZMEAN_CI:
		case TMEAN_TEST:
		case TMEAN_CI:

			mean.setValue(sc.mean);
			sd.setValue(sc.sd);
			n.setValue(sc.n);

			break;

		case ZMEAN2_TEST:
		case ZMEAN2_CI:
		case TMEAN2_TEST:
		case TMEAN2_CI:

			mean.setValue(sc.mean);
			sd.setValue(sc.sd);
			n.setValue(sc.n);
			mean2.setValue(sc.mean2);
			sd2.setValue(sc.sd2);
			n2.setValue(sc.n2);
			pooled.setValue(sc.pooled);

			break;

		case ZPROP_TEST:
		case ZPROP_CI:

			n.setValue(sc.n);
			proportion.setValue(sc.getProportion());

			break;

		case ZPROP2_TEST:
		case ZPROP2_CI:

			n.setValue(sc.n);
			proportion.setValue(sc.getProportion());
			n2.setValue(sc.n2);
			proportion2.setValue(sc.getProportion2());

			break;
		}
	}

	public void doCalculate() {

		AlgoElement algo;
		GeoElement[] result;
		GeoList list;

		updateGeoValues();

		switch (statCalc.getSelectedProcedure()) {

		case ZMEAN_TEST:

			algo = new AlgoZMeanTest(cons, mean, sd, n, nullHyp, tail);
			cons.removeFromConstructionList(algo);
			result = algo.getOutput();
			setTestResults(result[0]);
			sc.se = sc.sd / Math.sqrt(sc.n);

			break;

		case ZMEAN_CI:

			algo = new AlgoZMeanEstimate(cons, mean, sd, n, level);
			cons.removeFromConstructionList(algo);
			result = algo.getOutput();
			setIntervalResults(result[0]);
			sc.me = ((AlgoZMeanEstimate) algo).getME();
			sc.se = sc.sd / Math.sqrt(sc.n);

			break;

		case TMEAN_TEST:

			algo = new AlgoTTest(cons, mean, sd, n, nullHyp, tail);
			cons.removeFromConstructionList(algo);
			result = algo.getOutput();
			setTestResults(result[0]);
			sc.se = sc.sd / Math.sqrt(sc.n);
			sc.df = n.getDouble() - 1;
			break;

		case TMEAN_CI:

			algo = new AlgoTMeanEstimate(cons, mean, sd, n, level);
			cons.removeFromConstructionList(algo);
			result = algo.getOutput();
			setIntervalResults(result[0]);
			sc.me = ((AlgoTMeanEstimate) algo).getME();
			sc.se = sc.sd / Math.sqrt(sc.n);
			sc.df = n.getDouble() - 1;
			break;

		case ZMEAN2_TEST:

			algo = new AlgoZMean2Test(cons, mean, sd, n, mean2, sd2, n2, tail);
			cons.removeFromConstructionList(algo);
			result = algo.getOutput();
			setTestResults(result[0]);
			sc.se = ((AlgoZMean2Test) algo).getSE();
			break;

		case ZMEAN2_CI:

			algo = new AlgoZMean2Estimate(cons, mean, sd, n, mean2, sd2, n2,
					level);
			cons.removeFromConstructionList(algo);
			result = algo.getOutput();
			setIntervalResults(result[0]);
			sc.se = ((AlgoZMean2Estimate) algo).getSE();
			sc.me = ((AlgoZMean2Estimate) algo).getME();
			break;

		case TMEAN2_TEST:

			algo = new AlgoTTest2(cons, mean, sd, n, mean2, sd2, n2, tail,
					pooled);
			cons.removeFromConstructionList(algo);
			result = algo.getOutput();
			setTestResults(result[0]);
			setT2Stats();
			break;

		case TMEAN2_CI:

			algo = new AlgoTMean2Estimate(cons, mean, sd, n, mean2, sd2, n2,
					level, pooled);
			cons.removeFromConstructionList(algo);
			result = algo.getOutput();
			setIntervalResults(result[0]);
			setT2Stats();
			break;

		case ZPROP_TEST:

			algo = new AlgoZProportionTest(cons, proportion, n, nullHyp, tail);
			cons.removeFromConstructionList(algo);
			result = algo.getOutput();
			setTestResults(result[0]);

			sc.se = ((AlgoZProportionTest) algo).getSE();

			break;

		case ZPROP_CI:

			algo = new AlgoZProportionEstimate(cons, proportion, n, level);
			cons.removeFromConstructionList(algo);

			result = algo.getOutput();
			setIntervalResults(result[0]);
			sc.se = ((AlgoZProportionEstimate) algo).getSE();
			sc.me = ((AlgoZProportionEstimate) algo).getME();

			break;

		case ZPROP2_TEST:

			algo = new AlgoZProportion2Test(cons, proportion, n, proportion2,
					n2, tail);
			cons.removeFromConstructionList(algo);
			result = algo.getOutput();
			setTestResults(result[0]);

			sc.se = ((AlgoZProportion2Test) algo).getSE();

		case ZPROP2_CI:

			algo = new AlgoZProportion2Estimate(cons, proportion, n,
					proportion2, n2, level);

			result = algo.getOutput();
			setIntervalResults(result[0]);
			sc.me = ((AlgoZProportion2Estimate) algo).getME();
			sc.se = ((AlgoZProportion2Estimate) algo).getSE();

			break;

		case CHISQ_TEST:
			updateChiSq();
			break;

		case GOF_TEST:
			updateGOF();
			break;

		}

	}

	/**
	 * Computes and sets the standard error and degree of freedom for two sample
	 * T procedures
	 */
	private void setT2Stats() {

		double N1 = n.getDouble();
		double N2 = n2.getDouble();
		double SD1 = sd.getDouble();
		double SD2 = sd2.getDouble();

		if (pooled.getBoolean()) {
			double df = N1 + N2 - 2;
			double pooledVariance = (1 / N1 + 1 / N2)
					* ((N1 - 1) * SD1 * SD1 + (N2 - 1) * SD2 * SD2) / df;
			sc.se = Math.sqrt(pooledVariance);
			sc.df = df;
		} else {
			double V1 = SD1 * SD1 / N1;
			double V2 = SD2 * SD2 / N2;
			sc.se = Math.sqrt(V1 + V2);
			sc.df = ((V1 + V2) * (V1 + V2))
					/ ((V1 * V1) / (N1 - 1) + (V2 * V2) / (N2 - 1));

		}
	}

	/**
	 * Computes chi sq test results using code from AlgoChiSquaredTest TODO:
	 * Implement using AlgoChiSquaredTest directly
	 */
	private void updateChiSq() {

		// init sum fields
		for (int j = 0; j < sc.columns; j++) {
			sc.columnSum[j] = 0;
		}
		for (int i = 0; i < sc.rows; i++) {
			sc.rowSum[i] = 0;
		}
		sc.total = 0;

		// compute sums
		for (int i = 0; i < sc.rows; i++) {
			for (int j = 0; j < sc.columns; j++) {

				double value = parseStringData(sc.chiSquareData[i + 1][j + 1]);
				sc.observed[i][j] = value;
				if (!Double.isNaN(sc.observed[i][j])) {
					sc.rowSum[i] += sc.observed[i][j];
					sc.columnSum[j] += sc.observed[i][j];
					sc.total += sc.observed[i][j];
				}
			}
		}

		// compute expected counts
		for (int i = 0; i < sc.rows; i++) {
			for (int j = 0; j < sc.columns; j++) {
				sc.expected[i][j] = sc.rowSum[i] * sc.columnSum[j] / sc.total;
			}
		}

		// compute test statistic and chi-square contributions
		sc.testStat = 0;
		for (int i = 0; i < sc.rows; i++) {
			for (int j = 0; j < sc.columns; j++) {
				sc.diff[i][j] = (sc.observed[i][j] - sc.expected[i][j])
						* (sc.observed[i][j] - sc.expected[i][j])
						/ sc.expected[i][j];
				sc.testStat += sc.diff[i][j];
			}
		}

		// degree of freedom
		sc.df = (sc.columns - 1) * (sc.rows - 1);

		// compute P
		try {
			double leftArea = getChiSquaredDistribution(sc.df)
					.cumulativeProbability(sc.testStat);
			sc.P = 1 - leftArea;

		} catch (IllegalArgumentException e) {
			sc.P = Double.NaN;
			e.printStackTrace();
		} catch (MathException e) {
			sc.P = Double.NaN;
			e.printStackTrace();
		}

	}

	/**
	 * Computes goodness of fit test results TODO: Implement using
	 * AlgoGoodnessOfFitTest directly
	 */
	private void updateGOF() {

		// init sum fields
		for (int j = 0; j < sc.columns; j++) {
			sc.columnSum[j] = 0;
		}
		for (int i = 0; i < sc.rows; i++) {
			sc.rowSum[i] = 0;
		}
		sc.total = 0;

		// compute sums
		for (int i = 0; i < sc.rows; i++) {
			double value = parseStringData(sc.chiSquareData[i + 1][1]);
			sc.observed[i][0] = value;
			value = parseStringData(sc.chiSquareData[i + 1][2]);
			sc.expected[i][0] = value;

			if (!Double.isNaN(sc.observed[i][0])) {
				sc.columnSum[0] += sc.observed[i][0];
			}
			if (!Double.isNaN(sc.observed[i][1])) {
				sc.columnSum[1] += sc.expected[i][0];
			}

		}

		// compute test statistic and chi-square contributions
		sc.testStat = 0;
		for (int i = 0; i < sc.rows; i++) {
			sc.diff[i][0] = (sc.observed[i][0] - sc.expected[i][0])
					* (sc.observed[i][0] - sc.expected[i][0])
					/ sc.expected[i][0];
			sc.testStat += sc.diff[i][0];
			// System.out.println(i + ", " + 0 + "diff: " + sc.diff[i][0]);
		}

		// degree of freedom
		sc.df = sc.rows - 1;

		// compute P
		try {
			double leftArea = getChiSquaredDistribution(sc.df)
					.cumulativeProbability(sc.testStat);
			sc.P = 1 - leftArea;

		} catch (IllegalArgumentException e) {
			sc.P = Double.NaN;
			e.printStackTrace();
		} catch (MathException e) {
			sc.P = Double.NaN;
			e.printStackTrace();
		}
	}

	/**
	 * @param df
	 *            degree of freedom
	 * @return implementation of ChiSquaredDistribution for given degree of
	 *         freedom
	 */
	private ChiSquaredDistribution getChiSquaredDistribution(double df) {
		if (chisquared == null || chisquared.getDegreesOfFreedom() != df)
			chisquared = new ChiSquaredDistributionImpl(df);

		return chisquared;
	}

	private double parseStringData(String s) {

		if (s == null || s.length() == 0) {
			return Double.NaN;
		}

		try {
			String inputText = s.trim();

			// allow input such as sqrt(2)
			NumberValue nv;
			nv = cons.getKernel().getAlgebraProcessor()
					.evaluateToNumeric(inputText, false);
			return nv.getDouble();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return Double.NaN;
	}

	private void setTestResults(GeoElement result) {
		if (result.isDefined() && ((GeoList) result).size() > 0) {
			sc.P = ((GeoNumeric) ((GeoList) result).get(0)).getDouble();
			sc.testStat = ((GeoNumeric) ((GeoList) result).get(1)).getDouble();
		} else {
			sc.P = Double.NaN;
			sc.testStat = Double.NaN;
		}
	}

	private void setIntervalResults(GeoElement result) {
		if (result.isDefined() && ((GeoList) result).size() > 0) {
			sc.lower = ((GeoNumeric) ((GeoList) result).get(0)).getDouble();
			sc.upper = ((GeoNumeric) ((GeoList) result).get(1)).getDouble();
			sc.me = (sc.upper - sc.lower)/2;

		} else {
			sc.lower = Double.NaN;
			sc.upper = Double.NaN;
			sc.me = Double.NaN;
		}
	}

}
