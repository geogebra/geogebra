package geogebra.gui.view.probcalculator;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.statistics.AlgoBinomialDist;
import geogebra.common.kernel.statistics.AlgoCauchy;
import geogebra.common.kernel.statistics.AlgoChiSquared;
import geogebra.common.kernel.statistics.AlgoDistribution;
import geogebra.common.kernel.statistics.AlgoExponential;
import geogebra.common.kernel.statistics.AlgoFDistribution;
import geogebra.common.kernel.statistics.AlgoGamma;
import geogebra.common.kernel.statistics.AlgoHyperGeometric;
import geogebra.common.kernel.statistics.AlgoInverseBinomial;
import geogebra.common.kernel.statistics.AlgoInverseCauchy;
import geogebra.common.kernel.statistics.AlgoInverseChiSquared;
import geogebra.common.kernel.statistics.AlgoInverseExponential;
import geogebra.common.kernel.statistics.AlgoInverseFDistribution;
import geogebra.common.kernel.statistics.AlgoInverseGamma;
import geogebra.common.kernel.statistics.AlgoInverseHyperGeometric;
import geogebra.common.kernel.statistics.AlgoInverseLogNormal;
import geogebra.common.kernel.statistics.AlgoInverseLogistic;
import geogebra.common.kernel.statistics.AlgoInverseNormal;
import geogebra.common.kernel.statistics.AlgoInversePascal;
import geogebra.common.kernel.statistics.AlgoInversePoisson;
import geogebra.common.kernel.statistics.AlgoInverseTDistribution;
import geogebra.common.kernel.statistics.AlgoInverseWeibull;
import geogebra.common.kernel.statistics.AlgoLogNormal;
import geogebra.common.kernel.statistics.AlgoLogistic;
import geogebra.common.kernel.statistics.AlgoNormal;
import geogebra.common.kernel.statistics.AlgoPascal;
import geogebra.common.kernel.statistics.AlgoPoisson;
import geogebra.common.kernel.statistics.AlgoTDistribution;
import geogebra.common.kernel.statistics.AlgoWeibull;
import geogebra.common.main.App;
import geogebra.common.main.settings.ProbabilityCalculatorSettings;
import geogebra.common.main.settings.ProbabilityCalculatorSettings.DIST;
import geogebra.common.util.MyMath2;
import geogebra.main.AppD;

import java.util.HashMap;

/**
 * Class to handle probability calculations and to maintain the fields
 * associated with the various probability distribution used by
 * ProbabilityCalculator.
 * 
 * @author G Sturr
 * 
 */
public class ProbabilityManager {

	private AppD app;
	private ProbabilityCalculator probCalc;

	public ProbabilityManager(AppD app, ProbabilityCalculator probCalc) {

		this.app = app;
		this.probCalc = probCalc;

	}

	/**
	 * Returns true of the given distribution type is discrete
	 * 
	 * @param distType
	 * @return
	 */
	public boolean isDiscrete(DIST distType) {
		return distType.equals(DIST.BINOMIAL) || distType.equals(DIST.PASCAL)
				|| distType.equals(DIST.HYPERGEOMETRIC)
				|| distType.equals(DIST.POISSON);

	}

	/**
	 * Creates a hash map that can return a JComboBox menu string for
	 * distribution type constant Key = display type constant Value = menu item
	 * string
	 */
	protected HashMap<DIST, String> getDistributionMap() {

		HashMap<DIST, String> plotMap = new HashMap<DIST, String>();

		plotMap.put(DIST.NORMAL, app.getMenu("Distribution.Normal"));
		plotMap.put(DIST.STUDENT, app.getMenu("Distribution.StudentT"));
		plotMap.put(DIST.CHISQUARE, app.getMenu("Distribution.ChiSquare"));
		plotMap.put(DIST.F, app.getMenu("Distribution.F"));
		plotMap.put(DIST.EXPONENTIAL, app.getMenu("Distribution.Exponential"));
		plotMap.put(DIST.CAUCHY, app.getMenu("Distribution.Cauchy"));
		plotMap.put(DIST.WEIBULL, app.getMenu("Distribution.Weibull"));
		plotMap.put(DIST.LOGISTIC, app.getCommand("Logistic"));
		plotMap.put(DIST.LOGNORMAL, app.getCommand("LogNormal"));

		plotMap.put(DIST.GAMMA, app.getMenu("Distribution.Gamma"));
		plotMap.put(DIST.BINOMIAL, app.getMenu("Distribution.Binomial"));
		plotMap.put(DIST.PASCAL, app.getMenu("Distribution.Pascal"));
		plotMap.put(DIST.POISSON, app.getMenu("Distribution.Poisson"));
		plotMap.put(DIST.HYPERGEOMETRIC,
				app.getMenu("Distribution.Hypergeometric"));

		return plotMap;
	}

	/**
	 * Creates a reverse hash map that can return a distribution constant for a
	 * string selected in a JComboBox distribution menu Key = menu item string
	 * Value = display type constant
	 */
	protected HashMap<String, DIST> getReverseDistributionMap() {

		HashMap<DIST, String> plotMap = getDistributionMap();
		HashMap<String, DIST> plotMapReverse = new HashMap<String, DIST>();
		for (DIST key : plotMap.keySet()) {
			plotMapReverse.put(plotMap.get(key), key);
		}

		return plotMapReverse;
	}

	/**
	 * Returns a 2D array of strings used to label the parameter fields for each
	 * type of distribution
	 * 
	 * @param app
	 * @return
	 */
	protected static String[][] getParameterLabelArray(AppD app) {

		String[][] parameterLabels = new String[ProbabilityCalculatorSettings.distCount][4];

		parameterLabels[ProbabilityCalculatorSettings.DIST.NORMAL.ordinal()][0] = app
				.getMenu("Mean.short");
		parameterLabels[ProbabilityCalculatorSettings.DIST.NORMAL.ordinal()][1] = app
				.getMenu("StandardDeviation.short");

		parameterLabels[ProbabilityCalculatorSettings.DIST.STUDENT.ordinal()][0] = app
				.getMenu("DegreesOfFreedom.short");

		parameterLabels[ProbabilityCalculatorSettings.DIST.CHISQUARE.ordinal()][0] = app
				.getMenu("DegreesOfFreedom.short");

		parameterLabels[ProbabilityCalculatorSettings.DIST.F.ordinal()][0] = app
				.getMenu("DegreesOfFreedom1.short");
		parameterLabels[ProbabilityCalculatorSettings.DIST.F.ordinal()][1] = app
				.getMenu("DegreesOfFreedom2.short");

		parameterLabels[ProbabilityCalculatorSettings.DIST.EXPONENTIAL
				.ordinal()][0] = app.getMenu("Mean.short");

		parameterLabels[ProbabilityCalculatorSettings.DIST.CAUCHY.ordinal()][0] = app
				.getMenu("Median");
		parameterLabels[ProbabilityCalculatorSettings.DIST.CAUCHY.ordinal()][1] = app
				.getMenu("Distribution.Scale");

		parameterLabels[ProbabilityCalculatorSettings.DIST.WEIBULL.ordinal()][0] = app
				.getMenu("Distribution.Shape");
		parameterLabels[ProbabilityCalculatorSettings.DIST.WEIBULL.ordinal()][1] = app
				.getMenu("Distribution.Scale");

		parameterLabels[ProbabilityCalculatorSettings.DIST.LOGISTIC.ordinal()][0] = app
				.getMenu("Mean.short");
		parameterLabels[ProbabilityCalculatorSettings.DIST.LOGISTIC.ordinal()][1] = app
				.getMenu("Distribution.Scale");

		parameterLabels[ProbabilityCalculatorSettings.DIST.LOGNORMAL.ordinal()][0] = app
				.getMenu("Mean.short");
		parameterLabels[ProbabilityCalculatorSettings.DIST.LOGNORMAL.ordinal()][1] = app
				.getMenu("StandardDeviation.short");

		parameterLabels[ProbabilityCalculatorSettings.DIST.GAMMA.ordinal()][0] = app
				.getMenu("Alpha.short");
		parameterLabels[ProbabilityCalculatorSettings.DIST.GAMMA.ordinal()][1] = app
				.getMenu("Beta.short");

		parameterLabels[ProbabilityCalculatorSettings.DIST.BINOMIAL.ordinal()][0] = app
				.getMenu("Binomial.number");
		parameterLabels[ProbabilityCalculatorSettings.DIST.BINOMIAL.ordinal()][1] = app
				.getMenu("Binomial.probability");

		parameterLabels[ProbabilityCalculatorSettings.DIST.PASCAL.ordinal()][0] = app
				.getMenu("Binomial.number");
		parameterLabels[ProbabilityCalculatorSettings.DIST.PASCAL.ordinal()][1] = app
				.getMenu("Binomial.probability");

		parameterLabels[ProbabilityCalculatorSettings.DIST.POISSON.ordinal()][0] = app
				.getMenu("Mean.short");

		parameterLabels[ProbabilityCalculatorSettings.DIST.HYPERGEOMETRIC
				.ordinal()][0] = app.getMenu("Hypergeometric.population");
		parameterLabels[ProbabilityCalculatorSettings.DIST.HYPERGEOMETRIC
				.ordinal()][1] = app.getMenu("Hypergeometric.number");
		parameterLabels[ProbabilityCalculatorSettings.DIST.HYPERGEOMETRIC
				.ordinal()][2] = app.getMenu("Hypergeometric.sample");

		return parameterLabels;
	}

	/**
	 * Returns a GeoGebra inverse probability distribution command
	 * 
	 * @return AlgoDistribution
	 */
	protected static AlgoDistribution getInverseCommand(DIST d,
			Construction cons, NumberValue param1, NumberValue param2,
			NumberValue param3, NumberValue x) {

		AlgoDistribution ret = null;

		switch (d) {
		case NORMAL:
			ret = new AlgoInverseNormal(cons, param1, param2, x);
			break;
		case LOGNORMAL:
			ret = new AlgoInverseLogNormal(cons, param1, param2, x);
			break;
		case LOGISTIC:
			ret = new AlgoInverseLogistic(cons, param1, param2, x);
			break;
		case STUDENT:
			ret = new AlgoInverseTDistribution(cons, param1, x);
			break;
		case CHISQUARE:
			ret = new AlgoInverseChiSquared(cons, param1, x);
			break;
		case F:
			ret = new AlgoInverseFDistribution(cons, param1, param2, x);
			break;
		case CAUCHY:
			ret = new AlgoInverseCauchy(cons, param1, param2, x);
			break;
		case EXPONENTIAL:
			ret = new AlgoInverseExponential(cons, param1, x);
			break;
		case GAMMA:
			ret = new AlgoInverseGamma(cons, param1, param2, x);
			break;
		case WEIBULL:
			ret = new AlgoInverseWeibull(cons, param1, param2, x);
			break;
		case BINOMIAL:
			ret = new AlgoInverseBinomial(cons, param1, param2, x);
			break;
		case PASCAL:
			ret = new AlgoInversePascal(cons, param1, param2, x);
			break;
		case POISSON:
			ret = new AlgoInversePoisson(cons, param1, x);
			break;
		case HYPERGEOMETRIC:
			ret = new AlgoInverseHyperGeometric(cons, null, param1, param2,
					param3, x);
			break;

		}

		if (ret != null) {
			ret.getConstruction().removeFromConstructionList(ret);
		} else {
			App.error("missing case");
		}

		return ret;
	}

	/**
	 * Returns a GeoGebra probability distribution command
	 * 
	 * @return AlgoDistribution
	 */
	protected static AlgoDistribution getCommand(DIST d, Construction cons,
			NumberValue param1, NumberValue param2, NumberValue param3,
			NumberValue x, boolean isCumulative) {

		AlgoDistribution ret = null;

		switch (d) {
		case NORMAL:
			ret = new AlgoNormal(cons, param1, param2, x);
			break;
		case STUDENT:
			ret = new AlgoTDistribution(cons, param1, x);
			break;
		case CHISQUARE:
			ret = new AlgoChiSquared(cons, param1, x);
			break;
		case F:
			ret = new AlgoFDistribution(cons, param1, param2, x);
			break;
		case CAUCHY:
			ret = new AlgoCauchy(cons, param1, param2, x);
			break;
		case EXPONENTIAL:
			ret = new AlgoExponential(cons, param1, x);
			break;
		case GAMMA:
			ret = new AlgoGamma(cons, param1, param2, x);
			break;
		case WEIBULL:
			ret = new AlgoWeibull(cons, param1, param2, x);
			break;
		case BINOMIAL:
			ret = new AlgoBinomialDist(cons, param1, param2, x, new GeoBoolean(
					cons, isCumulative));
			break;
		case PASCAL:
			ret = new AlgoPascal(cons, param1, param2, x, new GeoBoolean(cons,
					isCumulative));
			break;
		case POISSON:
			ret = new AlgoPoisson(cons, param1, x, new GeoBoolean(cons,
					isCumulative));
			break;
		case HYPERGEOMETRIC:
			ret = new AlgoHyperGeometric(cons, param1, param2, param3, x,
					new GeoBoolean(cons, isCumulative));
			break;
		case LOGNORMAL:
			ret = new AlgoLogNormal(cons, param1, param2, x);
			break;
		case LOGISTIC:
			ret = new AlgoLogistic(cons, param1, param2, x);
			break;

		default:
			App.error("missing case");
		}

		if (ret != null) {
			cons.removeFromConstructionList(ret);
		} else {
			App.error("missing case");
		}

		return ret;

	}

	/**
	 * Returns an array of the required number of parameters needed for each
	 * distribution type. The array is indexed by distribution type.
	 * 
	 * @return
	 */
	protected static int getParmCount(DIST d) {

		switch (d) {
		case GAMMA:
		case WEIBULL:
		case LOGNORMAL:
		case LOGISTIC:
		case BINOMIAL:
		case PASCAL:
		case F:
		case CAUCHY:
		case NORMAL:
			return 2;

		case STUDENT:
		case CHISQUARE:
		case EXPONENTIAL:
		case POISSON:
			return 1;

		case HYPERGEOMETRIC:
			return 3;

		default:
			throw new Error("unknown distribution");

		}
	}

	/**
	 * Creates a map that returns default parameter values for each distribution
	 * type. /* Key = distribution type constant /* Value = default parameter
	 * values for the distribution type
	 */
	protected static double[] getDefaultParameters(DIST d) {
		HashMap<DIST, double[]> defaultParameterMap = new HashMap<DIST, double[]>();

		switch (d) {
		case NORMAL:
			return new double[] { 0, 1 }; // mean = 0, sigma = 1
		case STUDENT:
			return new double[] { 10 }; // df = 10
		case CHISQUARE:
			return new double[] { 6 }; // df = 6

		case F:
			return new double[] { 5, 2 }; // df1 = 5, df2 = 2
		case EXPONENTIAL:
			return new double[] { 1 }; // mean = 1
		case GAMMA:
			return new double[] { 3, 2 }; // alpha = 3, beta = 2
		case CAUCHY:
			return new double[] { 0, 1 }; // median = 0, scale = 1
		case WEIBULL:
			return new double[] { 5, 1 }; // shape = 5, scale = 1
		case LOGNORMAL:
			return new double[] { 0, 1 }; // mean = 0, sigma = 1
		case LOGISTIC:
			return new double[] { 5, 2 }; // mean = 5, scale = 2

		case BINOMIAL:
			return new double[] { 20, 0.5 }; // n = 20, p = 0.5
		case PASCAL:
			return new double[] { 10, 0.5 }; // n = 10, p = 0.5
		case POISSON:
			return new double[] { 4 }; // mean = 4
		case HYPERGEOMETRIC:
			return new double[] { 60, 10, 20 }; // pop = 60, n = 10, sample = 20

		default:
			AppD.error("missing case");
		}

		return null;

	}

	/**
	 * Returns the appropriate plot dimensions for a given distribution and
	 * parameter set. Plot dimensions are returned as an array of double: {xMin,
	 * xMax, yMin, yMax}
	 */
	protected double[] getPlotDimensions(DIST selectedDist, double[] parms,
			GeoElement densityCurve, boolean isCumulative) {

		double xMin = 0, xMax = 0, yMin = 0, yMax = 0;

		// retrieve the parameter values from the parmList geo
		// double [] parms = getCurrentParameters();
		double mean, sigma, v, v2, k, median, scale, shape, mode, n, p, pop, sample, sd, variance;

		switch (selectedDist) {

		case NORMAL:
			mean = parms[0];
			sigma = parms[1];
			xMin = mean - 5 * sigma;
			xMax = mean + 5 * sigma;
			yMin = 0;
			yMax = 1.2 * ((GeoFunction) densityCurve).evaluate(mean);
			break;

		case STUDENT:
			xMin = -5;
			xMax = 5;
			yMin = 0;
			yMax = 1.2 * ((GeoFunction) densityCurve).evaluate(0);
			break;

		case CHISQUARE:
			k = parms[0];
			xMin = 0;
			xMax = 4 * k;
			yMin = 0;
			if (k > 2)
				// mode occurs when x = k-2; add 0.1 to handle k near 2
				yMax = 1.2 * ((GeoFunction) densityCurve).evaluate(k - 2 + 0.1);
			else
				// mode occurs at x = 0, but we only use x near zero
				yMax = 1.2 * ((GeoFunction) densityCurve).evaluate(0.1);
			break;

		case F:
			v = parms[0];
			v2 = parms[1];
			mean = v2 > 2 ? v2 / (v2 - 2) : 1;
			mode = ((v - 2) * v2) / (v*(v2 + 2));
			// TODO variance only valid for v2 > 4, need to handle v2<4
			variance = 2 * v2 * v2 * (v + v2 - 2)
					/ (v * (v2 - 2) * (v2 - 2) * (v2 - 4));
			xMin = 0;
			if(v2 > 2){
			xMax = mean + 5 * Math.sqrt(variance);
			}else{
				xMax = 2;
			}
			yMin = 0;
			if (v > 2)
				yMax = 1.2 * ((GeoFunction) densityCurve).evaluate(mode);
			else
				//yMax = 1.2 * ((GeoFunction) densityCurve).evaluate(0.01);
			yMax = 2.5;
			//System.out.println("FMode: " + mode);
			//System.out.println("xMax: " + xMax);
			//System.out.println("yMax: " + yMax);
			break;

		case CAUCHY:
			median = parms[0];
			scale = parms[1];
			// TODO --- better estimates
			xMin = median - 6 * scale;
			xMax = median + 6 * scale;
			yMin = 0;
			yMax = 1.2 * (1 / (Math.PI * scale)); // Cauchy amplitude =
													// 1/(pi*scale)

			break;

		case EXPONENTIAL:
			double lambda = parms[0];
			xMin = 0;
			xMax = 4 * (1 / lambda); // st dev = 1/lambda
			yMin = 0;
			yMax = 1.2 * lambda;
			break;

		case GAMMA:
			double alpha = parms[0]; // (shape)
			double beta = parms[1]; // (scale)
			mode = (alpha - 1) * beta;
			mean = alpha * beta;
			sd = Math.sqrt(alpha) * beta;
			xMin = 0;
			xMax = mean + 5 * sd;
			yMin = 0;
			if (alpha > 1) // mode = (alpha -1)*beta
				yMax = 1.2 * ((GeoFunction) densityCurve).evaluate(mode);
			else
				yMax = 1.2 * ((GeoFunction) densityCurve).evaluate(0);
			break;

		case WEIBULL:
			shape = parms[0];
			scale = parms[1];
			median = scale * Math.pow(Math.log(2), 1 / shape);
			xMin = 0;
			xMax = 2 * median;
			yMin = 0;
			// mode for shape >1
			if (shape > 1) {
				mode = scale * Math.pow(1 - 1 / shape, 1 / shape);
				yMax = 1.2 * ((GeoFunction) densityCurve).evaluate(mode);
			} else {
				yMax = 4;
			}

			break;

		case LOGNORMAL:
			double meanParm = parms[0];
			double sdParm = parms[1];
			double varParm = sdParm * sdParm;

			mean = Math.exp(meanParm + varParm / 2);

			double var = (Math.exp(varParm) - 1)
					* Math.exp(2 * meanParm + varParm);
			sigma = Math.sqrt(var);

			mode = Math.exp(meanParm - varParm);
			xMin = 0;
			xMax = mean + 5 * sigma;

			yMin = 0;
			yMax = 1.2 * ((GeoFunction) densityCurve).evaluate(mode);
			break;

		case LOGISTIC:
			mean = parms[0];
			scale = parms[1];
			sd = Math.PI * scale / Math.sqrt(3);
			xMin = mean - 5 * sd;
			xMax = mean + 5 * sd;
			yMin = 0;
			yMax = 1.2 * ((GeoFunction) densityCurve).evaluate(mean);
			break;

		case PASCAL:
		case POISSON:
			xMin = probCalc.getDiscreteXMin();
			xMax = probCalc.getDiscreteXMax();
			yMin = 0;
			yMax = 1.2 * getDiscreteYMax(selectedDist, parms, (int) xMin,
					(int) xMax);
			xMin -= 1;

			break;

		case BINOMIAL:
		case HYPERGEOMETRIC:
			xMin = probCalc.getDiscreteXMin();
			xMax = probCalc.getDiscreteXMax();
			yMin = 0;
			yMax = 1.2 * getDiscreteYMax(selectedDist, parms, (int) xMin,
					(int) xMax);
			xMin -= 1;
			xMax += 1;
			break;

		}

		if (isCumulative) {
			yMin = 0;
			yMax = 1.2;
		}
		double[] d = { xMin, xMax, yMin, yMax };
		return d;
	}

	/**
	 * Returns numerical measures of the given distribution
	 * 
	 * @param selectedDist
	 *            distribution
	 * @param parms
	 *            parameter values
	 * @return {mean, sigma} Note: if a values is undefined, array with null
	 *         element(s) is returned
	 */
	protected Double[] getDistributionMeasures(DIST selectedDist, double[] parms) {

		// in the future, would be nice to return median and mode
		// median can be evaluated numerically with inverseCDF(.5)
		// see
		// http://blogs.sas.com/content/iml/2011/11/09/on-the-median-of-the-chi-square-distribution/
		// for interesting discussion

		Double mean = null, sigma = null;
		double v, v2, k, median, scale, shape, mode, n, N, p, pop, sd, variance, r;

		switch (selectedDist) {

		case NORMAL:
			mean = parms[0];
			sigma = parms[1];
			break;

		case STUDENT:
			mean = 0d;
			v = parms[0];
			if (v > 2) {
				sigma = Math.sqrt(v / (v - 2));
			} else {
				sigma = null; // infinity?
			}

			break;

		case CHISQUARE:
			k = parms[0];
			mean = k;
			sigma = Math.sqrt(2 * k);
			break;

		case F:
			v = parms[0];
			v2 = parms[1];
			if (v2 > 2) {
				mean = v2 / (v2 - 2);
			}

			if (v2 > 4) {
				variance = 2 * v2 * v2 * (v + v2 - 2)
						/ (v * (v2 - 2) * (v2 - 2) * (v2 - 4));
				sigma = Math.sqrt(variance);
			}

			break;

		case CAUCHY:
			median = parms[0];
			scale = parms[1];
			// mean and median are undefined
			break;

		case EXPONENTIAL:
			double lambda = parms[0];
			mean = 1 / lambda;
			sigma = 1 / lambda;
			break;

		case GAMMA:
			double alpha = parms[0]; // (shape)
			double beta = parms[1]; // (scale)
			mode = (alpha - 1) * beta;
			mean = alpha * beta;
			sigma = Math.sqrt(alpha) * beta;
			break;

		case WEIBULL:
			shape = parms[0];
			scale = parms[1];
			median = scale * Math.pow(Math.log(2), 1 / shape);

			mean = scale * MyMath2.gamma(1 + 1 / shape);
			variance = scale * scale * MyMath2.gamma(1 + 2 / shape) - mean
					* mean;
			sigma = Math.sqrt(variance);

			break;

		case LOGNORMAL:

			// TODO: may not be correct
			double meanParm = parms[0];
			double sdParm = parms[1];
			double varParm = sdParm * sdParm;

			mean = Math.exp(meanParm + varParm / 2);

			double var = (Math.exp(varParm) - 1)
					* Math.exp(2 * meanParm + varParm);
			sigma = Math.sqrt(var);

			mode = Math.exp(meanParm - varParm);
			break;

		case LOGISTIC:
			mean = parms[0];
			scale = parms[1];
			sigma = Math.PI * scale / Math.sqrt(3);
			break;

		case PASCAL:
			r = parms[0];
			p = parms[1];
			mean = p * r / (1 - p);
			var = p * r / ((1 - p) * (1 - p));
			sigma = Math.sqrt(var);
			break;

		case POISSON:
			mean = parms[0];
			sigma = Math.sqrt(mean);
			break;

		case BINOMIAL:
			n = parms[0];
			p = parms[1];
			mean = n * p;
			var = n * p * (1 - p);
			sigma = Math.sqrt(var);
			break;

		case HYPERGEOMETRIC:
			N = parms[0];
			k = parms[1];
			n = parms[2];

			mean = n * k / N;
			var = n * k * (N - k) * (N - n) / (N * N * (N - 1));
			sigma = Math.sqrt(var);
			break;

		}

		Double[] d = { mean, sigma };
		return d;
	}

	/**
	 * Returns the maximum probability value for a specified discrete
	 * distribution over the range given by [low,high]
	 * 
	 * @param distType
	 * @param parms
	 * @param low
	 * @param high
	 * @return
	 */
	private double getDiscreteYMax(DIST distType, double[] parms, int low,
			int high) {

		double max = 0;

		for (int i = low; i <= high; i++) {
			max = Math.max(max, probability(i, parms, distType, false));
		}
		return max;
	}

	/**
	 * If isCumulative = true, returns P(X <= value) for the given distribution
	 * If isCumulative = false, returns P(X = value) for the given distribution
	 */
	public double probability(double value, double[] parms, DIST distType,
			boolean isCumulative) {

		MyDouble param1 = null, param2 = null, param3 = null;

		Construction cons = app.getKernel().getConstruction();
		Kernel kernel = app.getKernel();

		if (parms.length > 0) {
			param1 = new MyDouble(kernel, parms[0]);
		}
		if (parms.length > 1) {
			param2 = new MyDouble(kernel, parms[1]);
		}
		if (parms.length > 2) {
			param3 = new MyDouble(kernel, parms[2]);
		}

		AlgoDistribution algo = getCommand(distType, cons, param1, param2,
				param3, new MyDouble(kernel, value), isCumulative);

		return algo.getResult().getDouble();

	}

	/**
	 * Returns an interval probability for the given distribution and
	 * probability mode. If mode == PROB_INTERVAL then P(low <= X <= high) is
	 * returned. If mode == PROB_LEFT then P(low <= X) is returned. If mode ==
	 * PROB_RIGHT then P(X <= high) is returned.
	 */
	public double intervalProbability(double low, double high, DIST distType,
			double[] parms, int probMode) {

		if (probMode == ProbabilityCalculator.PROB_LEFT)

			return probability(high, parms, distType, true);

		else if (probMode == ProbabilityCalculator.PROB_RIGHT) {

			if (isDiscrete(distType)) {
				return 1 - probability(low - 1, parms, distType, true);
			}

			return 1 - probability(low, parms, distType, true);

		} else { // ProbabilityCalculator.PROB_INTERVAL

			if (isDiscrete(distType)) {
				return probability(high, parms, distType, true)
						- probability(low - 1, parms, distType, true);
			}

			return probability(high, parms, distType, true)
					- probability(low, parms, distType, true);

		}

	}

	/**
	 * Returns an inverse probability for a selected distribution and a given
	 * cumulative (left area) probability.
	 * 
	 * @param prob
	 *            cumulative probability
	 */
	protected double inverseProbability(DIST distType, double prob,
			double[] parms) {

		MyDouble param1 = null, param2 = null, param3 = null;

		Construction cons = app.getKernel().getConstruction();
		Kernel kernel = app.getKernel();

		if (parms.length > 0) {
			param1 = new MyDouble(kernel, parms[0]);
		}
		if (parms.length > 1) {
			param2 = new MyDouble(kernel, parms[1]);
		}
		if (parms.length > 2) {
			param3 = new MyDouble(kernel, parms[2]);
		}

		AlgoDistribution algo = getInverseCommand(distType, cons, param1,
				param2, param3, new MyDouble(kernel, prob));

		return algo.getResult().getDouble();
	}

}
