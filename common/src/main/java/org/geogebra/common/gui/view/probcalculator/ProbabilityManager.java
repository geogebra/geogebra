package org.geogebra.common.gui.view.probcalculator;

import java.util.HashMap;
import java.util.stream.Stream;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.AlgoBinomialDist;
import org.geogebra.common.kernel.statistics.AlgoDistribution;
import org.geogebra.common.kernel.statistics.AlgoHyperGeometric;
import org.geogebra.common.kernel.statistics.AlgoInverseBinomial;
import org.geogebra.common.kernel.statistics.AlgoInverseHyperGeometric;
import org.geogebra.common.kernel.statistics.AlgoInverseLogNormal;
import org.geogebra.common.kernel.statistics.AlgoInverseLogistic;
import org.geogebra.common.kernel.statistics.AlgoInversePascal;
import org.geogebra.common.kernel.statistics.AlgoInversePoisson;
import org.geogebra.common.kernel.statistics.AlgoInverseRealDistribution1Param;
import org.geogebra.common.kernel.statistics.AlgoInverseRealDistribution2Params;
import org.geogebra.common.kernel.statistics.AlgoLogNormal;
import org.geogebra.common.kernel.statistics.AlgoLogistic;
import org.geogebra.common.kernel.statistics.AlgoPascal;
import org.geogebra.common.kernel.statistics.AlgoPoisson;
import org.geogebra.common.kernel.statistics.AlgoRealDistribution1Param;
import org.geogebra.common.kernel.statistics.AlgoRealDistribution2Params;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.util.MyMath2;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Class to handle probability calculations and to maintain the fields
 * associated with the various probability distribution used by
 * ProbabilityCalculator.
 *
 * @author G Sturr
 *
 */
public class ProbabilityManager {

	private final App app;
	private final ProbabilityCalculatorView probCalc;
	private HashMap<Dist, String[]> distributionParameterTransKeys;
	private final String[] customValues = {
			"Median", "Scale", "Shape", "Population", "population", "Sample", "sample"
	};

	/**
	 * @param app
	 *            application
	 * @param probCalc
	 *            probability calculator view
	 */
	public ProbabilityManager(App app, ProbabilityCalculatorView probCalc) {
		this.app = app;
		this.probCalc = probCalc;
	}

	/**
	 * Returns true of the given distribution type is discrete
	 *
	 * @param distType
	 *            distribution type
	 * @return whether distribution is discrete
	 */
	public boolean isDiscrete(Dist distType) {
		return distType.equals(Dist.BINOMIAL) || distType.equals(Dist.PASCAL)
				|| distType.equals(Dist.HYPERGEOMETRIC)
				|| distType.equals(Dist.POISSON);
	}

	/**
	 * Creates a hash map that contains the transKeys of the different distribution parameters
	 *
	 * @return map with the transKeys
	 */
	public HashMap<Dist, String[]> getDistributionParameterTransKeys() {
		if (distributionParameterTransKeys == null) {
			distributionParameterTransKeys = new HashMap<>();

			boolean isProbCalc = app.getConfig().hasDistributionView();

			distributionParameterTransKeys.put(
				ProbabilityCalculatorSettings.Dist.NORMAL,
				new String[]{"Mean.short", "StandardDeviation.short"}
			);
			distributionParameterTransKeys.put(
				ProbabilityCalculatorSettings.Dist.STUDENT,
				new String[]{"DegreesOfFreedom.short"}
			);
			distributionParameterTransKeys.put(
				ProbabilityCalculatorSettings.Dist.CHISQUARE,
				new String[]{"DegreesOfFreedom.short"}
			);
			distributionParameterTransKeys.put(
				ProbabilityCalculatorSettings.Dist.F,
				new String[]{"DegreesOfFreedom1.short", "DegreesOfFreedom2.short"}
			);
			distributionParameterTransKeys.put(
				ProbabilityCalculatorSettings.Dist.EXPONENTIAL,
				new String[]{Unicode.lambda + ""}
			);
			distributionParameterTransKeys.put(
				ProbabilityCalculatorSettings.Dist.CAUCHY,
				new String[]{"Median", isProbCalc ? "Scale" : "Distribution.Scale"}
			);
			distributionParameterTransKeys.put(
				ProbabilityCalculatorSettings.Dist.WEIBULL,
				new String[]{"Distribution.Shape", isProbCalc ? "Scale" : "Distribution.Scale"}
			);
			distributionParameterTransKeys.put(
				ProbabilityCalculatorSettings.Dist.LOGISTIC,
				new String[]{"Mean.short", isProbCalc ? "Scale" : "Distribution.Scale"}
			);
			distributionParameterTransKeys.put(
				ProbabilityCalculatorSettings.Dist.LOGNORMAL,
				new String[]{"Mean.short", "StandardDeviation.short"}
			);
			distributionParameterTransKeys.put(
					ProbabilityCalculatorSettings.Dist.BETA,
					new String[]{Unicode.alpha + "", Unicode.beta + ""}
			);
			distributionParameterTransKeys.put(
				ProbabilityCalculatorSettings.Dist.GAMMA,
				new String[]{Unicode.alpha + "", Unicode.beta + ""}
			);
			distributionParameterTransKeys.put(
				ProbabilityCalculatorSettings.Dist.BINOMIAL,
				new String[]{"Binomial.number", "Binomial.probability"}
			);
			distributionParameterTransKeys.put(
				ProbabilityCalculatorSettings.Dist.PASCAL,
				new String[]{"Binomial.number", "Binomial.probability"}
			);
			distributionParameterTransKeys.put(
				ProbabilityCalculatorSettings.Dist.POISSON,
				new String[]{"Mean.short"}
			);
			distributionParameterTransKeys.put(
				ProbabilityCalculatorSettings.Dist.HYPERGEOMETRIC,
				new String[]{
						isProbCalc ? "Distribution.Population" : "Hypergeometric.population",
						"Hypergeometric.number",
						isProbCalc ? "Sample" : "Hypergeometric.sample"
				}
			);
		}
		return distributionParameterTransKeys;
	}

	/**
	 * Checks if transKey contains any of the predefined custom values
	 * @param transKey - the transKey
	 * @return true if transKey contains any of the custom values
	 */
	private boolean isCustom(String transKey) {
		return Stream.of(customValues).anyMatch(transKey::contains);
	}

	/**
	 * Returns a 2D array of strings used to label the parameter fields for each
	 * type of distribution
	 *
	 * @param loc
	 *            localization
	 * @return matrix of strings
	 */
	public String[][] getParameterLabelArray(Localization loc) {
		String[][] parameterLabels = new String[ProbabilityCalculatorSettings.distCount][4];

		getDistributionParameterTransKeys().forEach((dist, transKeys) -> {
			for (int i = 0; i < transKeys.length; i++) {
				parameterLabels[dist.ordinal()][i] = loc.getMenu(transKeys[i]);
			}
		});

		return parameterLabels;
	}

	/**
	 * Returns a 2D array of strings used to label the parameter fields for each
	 * type of distribution. It uses the Parameter prefix where necessary.
	 * @param loc localization
	 * @return matrix of strings
	 */
	public String[][] getParameterLabelArrayPrefixed(Localization loc) {
		String[][] parameterLabels = new String[ProbabilityCalculatorSettings.distCount][3];

		getDistributionParameterTransKeys().forEach((dist, transKeys) -> {
			for (int i = 0; i < transKeys.length; i++) {
				parameterLabels[dist.ordinal()][i] = isCustom(transKeys[i])
						? loc.getMenu(transKeys[i])
						: ProbabilityManager.parameterPrefixed(loc, loc.getMenu(transKeys[i]));
			}
		});

		return parameterLabels;
	}

	private static String parameterPrefixed(Localization localization, String label) {
		return localization.getPlainDefault("ParameterA", "Parameter $0", label);
	}

	/**
	 * Returns a GeoGebra inverse probability distribution command
	 *
	 * @param dist
	 *            distribution
	 * @param cons
	 *            construction
	 * @param param1
	 *            distribution parameter 1
	 * @param param2
	 *            distribution parameter 2
	 * @param param3
	 *            distribution parameter 3
	 * @param x
	 *            variable value
	 *
	 * @return AlgoDistribution
	 */
	protected static AlgoDistribution getInverseCommand(Dist dist,
			Construction cons, GeoNumberValue param1, GeoNumberValue param2,
			GeoNumberValue param3, GeoNumberValue x) {

		AlgoDistribution ret = null;

		switch (dist) {
		default:
			// no nothing
			break;
		case NORMAL:
		case F:
		case WEIBULL:
		case GAMMA:
		case BETA:
		case CAUCHY:
			ret = new AlgoInverseRealDistribution2Params(cons, param1, param2, x,
					dist);
			break;
		case LOGNORMAL:
			ret = new AlgoInverseLogNormal(cons, param1, param2, x);
			break;
		case LOGISTIC:
			ret = new AlgoInverseLogistic(cons, param1, param2, x);
			break;
		case STUDENT:
		case CHISQUARE:
		case EXPONENTIAL:
			ret = new AlgoInverseRealDistribution1Param(cons, param1, x,
					dist);
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
			ret = new AlgoInverseHyperGeometric(cons, param1, param2,
					param3, x);
			break;

		}

		if (ret != null) {
			ret.remove();
		} else {
			Log.error("missing case");
		}

		return ret;
	}

	/**
	 * Returns a GeoGebra probability distribution command
	 *
	 * @param dist
	 *            distribution
	 * @param cons
	 *            construction
	 * @param param1
	 *            distribution parameter 1
	 * @param param2
	 *            distribution parameter 2
	 * @param param3
	 *            distribution parameter 3
	 * @param x
	 *            variable value
	 * @param isCumulative
	 *            whether to use cumulative dstribution
	 * @return AlgoDistribution
	 */
	protected static AlgoDistribution getCommand(Dist dist, Construction cons,
			GeoNumberValue param1, GeoNumberValue param2, GeoNumberValue param3,
			GeoNumberValue x, boolean isCumulative) {

		AlgoDistribution ret = null;

		switch (dist) {
		case NORMAL:
			ret = new AlgoRealDistribution2Params(cons, param1, param2, x, null,
					dist);
			break;
		case STUDENT:
			ret = new AlgoRealDistribution1Param(cons, param1, x, null, dist);
			break;
		case CHISQUARE:
			ret = new AlgoRealDistribution1Param(cons, param1, x, null, dist);
			break;
		case F:
			ret = new AlgoRealDistribution2Params(cons, param1, param2, x, null,
					dist);
			break;
		case CAUCHY:
			ret = new AlgoRealDistribution2Params(cons, param1, param2, x, null, dist);
			break;
		case EXPONENTIAL:
			ret = new AlgoRealDistribution1Param(cons, param1, x, null, dist);
			break;
		case BETA:
			ret = new AlgoRealDistribution2Params(cons, param1, param2, x, null, dist);
			break;
		case GAMMA:
			ret = new AlgoRealDistribution2Params(cons, param1, param2, x, null, dist);
			break;
		case WEIBULL:
			ret = new AlgoRealDistribution2Params(cons, param1, param2, x, null, dist);
			break;
		case BINOMIAL:
			ret = new AlgoBinomialDist(cons, param1, param2, x,
					new GeoBoolean(cons, isCumulative));
			break;
		case PASCAL:
			ret = new AlgoPascal(cons, param1, param2, x,
					new GeoBoolean(cons, isCumulative));
			break;
		case POISSON:
			ret = new AlgoPoisson(cons, param1, x,
					new GeoBoolean(cons, isCumulative));
			break;
		case HYPERGEOMETRIC:
			ret = new AlgoHyperGeometric(cons, param1, param2, param3, x,
					new GeoBoolean(cons, isCumulative));
			break;
		case LOGNORMAL:
			ret = new AlgoLogNormal(cons, param1, param2, x, null);
			break;
		case LOGISTIC:
			ret = new AlgoLogistic(cons, param1, param2, x, null);
			break;

		default:
			Log.error("missing case");
		}

		if (ret != null) {
			ret.remove();
		} else {
			Log.error("missing case");
		}

		return ret;

	}

	/**
	 * Returns an array of the required number of parameters needed for each
	 * distribution type. The array is indexed by distribution type.
	 *
	 * @param dist
	 *            distribution
	 *
	 * @return number of parameters
	 */
	public static int getParamCount(Dist dist) {

		switch (dist) {
		case BETA:
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
	 *
	 * @param dist distribution
	 * @param cons construction
	 * @return default parameters
	 */
	public static GeoNumberValue[] getDefaultParameters(Dist dist, Construction cons) {
		double[] values = getDefaultParameters(dist);
		GeoNumberValue[] params = new GeoNumberValue[values.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = new GeoNumeric(cons, values[i]);
		}
		return params;
	}

	private static double[] getDefaultParameters(Dist dist) {
		switch (dist) {
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
		case BETA:
			return new double[] { 2, 2 }; // alpha = 2, beta = 2
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
			Log.error("missing case");
		}

		return null;

	}

	/**
	 * Returns the appropriate plot dimensions for a given distribution and
	 * parameter set. Plot dimensions are returned as an array of double: {xMin,
	 * xMax, yMin, yMax}
	 *
	 * @param selectedDist
	 *            distribution
	 * @param params
	 *            parameters
	 * @param densityCurve
	 *            density curve
	 * @param isCumulative
	 *            cumulative?
	 * @return plot width and height
	 */
	public double[] getPlotDimensions(Dist selectedDist, GeoNumberValue[] params,
			GeoFunction densityCurve, boolean isCumulative) {

		double xMin = 0, xMax = 0, yMin = 0, yMax = 0;

		// retrieve the parameter values from the param geos
		double mean, sigma, v, v2, k, median, scale, shape, mode, sd;

		switch (selectedDist) {

		case NORMAL:
			mean = params[0].getDouble();
			sigma = params[1].getDouble();
			xMin = mean - 5 * sigma;
			xMax = mean + 5 * sigma;
			yMin = 0;
			yMax = densityCurve.value(mean);
			break;

		case STUDENT:
			xMin = -5;
			xMax = 5;
			yMin = 0;
			yMax = densityCurve.value(0);
			break;

		case CHISQUARE:
			k = params[0].getDouble();
			xMin = 0;
			xMax = 4 * k;
			yMin = 0;
			if (k > 2) {
				// mode occurs when x = k-2; add 0.1 to handle k near 2
				yMax = densityCurve.value(k - 2 + 0.1);
			} else {
				// mode occurs at x = 0, but we only use x near zero
				yMax = densityCurve.value(0.1);
			}
			break;

		case F:
			v = params[0].getDouble();
			v2 = params[1].getDouble();
			mode = ((v - 2) * v2) / (v * (v2 + 2));

			xMin = 0;

			xMax = getContXMax(densityCurve, 1, .2, -1);

			yMin = 0;
			if (v > 2) {
				yMax = densityCurve.value(mode);
			} else {
				// yMax = 1.2 * ((GeoFunction) densityCurve).evaluate(0.01);
				yMax = 2;
			}

			break;

		case CAUCHY:
			median = params[0].getDouble();
			scale = params[1].getDouble();
			// TODO --- better estimates
			xMin = median - 6 * scale;
			xMax = median + 6 * scale;
			yMin = 0;
			yMax = (1 / (Math.PI * scale)); // Cauchy amplitude =
													// 1/(pi*scale)

			break;

		case EXPONENTIAL:
			double lambda = params[0].getDouble();
			xMin = 0;
			// xMax = 4 * (1 / lambda); // st dev = 1/lambda
			xMax = getContXMax(densityCurve, 1, .2, -1);
			yMin = 0;
			yMax = lambda;
			break;

		case GAMMA:
			double alpha = params[0].getDouble(); // (shape)
			double beta = params[1].getDouble(); // (scale)
			mode = (alpha - 1) * beta;
			mean = alpha * beta;
			sd = Math.sqrt(alpha) * beta;
			xMin = 0;
			xMax = mean + 5 * sd;
			yMin = 0;
			if (alpha > 1) {
				yMax = densityCurve.value(mode);
			} else {
				yMax = densityCurve.value(0);
			}
			break;
		case BETA:
			xMin = 0;
			xMax = 1;
			yMin = 0;
			alpha = params[0].getDouble();
			beta = params[1].getDouble();
			if (alpha > 1 && beta > 1) {
				mode = (alpha - 1) / (alpha + beta - 2);
				yMax = densityCurve.value(mode);
			} else {
				yMax = Math.max(densityCurve.value(0.01), densityCurve.value(0.99));
			}
			break;

		case WEIBULL:
			shape = params[0].getDouble();
			scale = params[1].getDouble();
			median = scale * Math.pow(Math.log(2), 1 / shape);
			xMin = 0;
			xMax = 2 * median;
			yMin = 0;
			// mode for shape >1
			if (shape > 1) {
				mode = scale * Math.pow(1 - 1 / shape, 1 / shape);
				yMax = densityCurve.value(mode);
			} else {
				yMax = 3.3;
			}

			break;

		case LOGNORMAL:
			double meanParam = params[0].getDouble();
			double sdParam = params[1].getDouble();
			double varParam = sdParam * sdParam;

			mean = Math.exp(meanParam + varParam / 2);

			double var = (Math.exp(varParam) - 1)
					* Math.exp(2 * meanParam + varParam);
			sigma = Math.sqrt(var);

			mode = Math.exp(meanParam - varParam);
			xMin = 0;
			xMax = mean + 5 * sigma;

			yMin = 0;
			yMax = densityCurve.value(mode);
			break;

		case LOGISTIC:
			mean = params[0].getDouble();
			scale = params[1].getDouble();
			sd = Math.PI * scale / Math.sqrt(3);
			xMin = mean - 5 * sd;
			xMax = mean + 5 * sd;
			yMin = 0;
			yMax = densityCurve.value(mean);
			break;

		case POISSON:
			// mode = mean = params[0];
			mode = Math.floor(params[0].getDouble());
			xMin = probCalc.getDiscreteXMin();
			xMax = probCalc.getDiscreteXMax();
			yMin = 0;
			yMax = probability(mode, params, Dist.POISSON, false);
			xMin -= 1;

			break;

		case PASCAL:
			// r = params[0]
			// p = params[1]
			// care: p swapped with 1-p from Wikipedia page
			// https://en.wikipedia.org/wiki/Negative_binomial_distribution
			// if r <= 1, mode = 0, else mode = floor((1-p)(r-1)/(p)
			mode = 0;
			if (params[0].getDouble() > 1) {
				mode = Math.floor((1 - params[1].getDouble()) * (params[0].getDouble() - 1)
						/ (params[1].getDouble()));
			}

			xMin = probCalc.getDiscreteXMin();
			xMax = probCalc.getDiscreteXMax();
			yMin = 0;
			yMax = probability(mode, params, Dist.PASCAL, false);
			xMin -= 1;

			break;

		case BINOMIAL:
			// n = params[0]
			// p = params[1]
			// mode = np
			mode = Math.floor((params[0].getDouble() + 1) * params[1].getDouble());

			xMin = probCalc.getDiscreteXMin();
			xMax = probCalc.getDiscreteXMax();
			yMin = 0;
			yMax = probability(mode, params, Dist.BINOMIAL, false);
			xMin -= 1;
			xMax += 1;
			break;

		case HYPERGEOMETRIC:
			// N = params[0];
			// k = params[1];
			// n = params[2];
			// mode = floor(n+1)(k+1)/(N+2);
			mode = Math.floor((params[1].getDouble() + 1) * (params[2].getDouble() + 1)
					/ (params[0].getDouble() + 2));

			xMin = probCalc.getDiscreteXMin();
			xMax = probCalc.getDiscreteXMax();
			yMin = 0;
			yMax = probability(mode, params, Dist.HYPERGEOMETRIC, false);
			xMin -= 1;
			xMax += 1;
			break;

		}

		if (isCumulative) {
			yMin = 0;
			yMax = 1;
		}
		return new double[]{ xMin, xMax, yMin, yMax};
	}

	private static double getContXMax(GeoFunction densityCurve, double startX,
			double stepX, double yMinimum) {

		double defaultYMin = 0.005;

		double yMin = (yMinimum < 0) ? defaultYMin : yMinimum;

		double x = startX;
		double test = densityCurve.value(x);
		while (test > yMin) {
			test = densityCurve.value(x);
			x += stepX;
		}
		return x;
	}

	/**
	 * Returns numerical measures of the given distribution
	 *
	 * @param selectedDist
	 *            distribution
	 * @param params
	 *            parameter values
	 * @return {mean, sigma} Note: if a values is undefined, array with null
	 *         element(s) is returned
	 */
	public Double[] getDistributionMeasures(Dist selectedDist, GeoNumberValue[] params) {

		// in the future, would be nice to return median and mode
		// median can be evaluated numerically with inverseCDF(.5)
		// see
		// http://blogs.sas.com/content/iml/2011/11/09/on-the-median-of-the-chi-square-distribution/
		// for interesting discussion

		Double mean = null, sigma = null;
		double v, v2, k, scale, shape, n, N, p, variance, r;

		switch (selectedDist) {

		default:
		case NORMAL:
			mean = params[0].getDouble();
			sigma = params[1].getDouble();
			break;

		case STUDENT:
			mean = 0d;
			v = params[0].getDouble();
			if (v > 2) {
				sigma = Math.sqrt(v / (v - 2));
			}

			break;

		case CHISQUARE:
			k = params[0].getDouble();
			mean = k;
			sigma = Math.sqrt(2 * k);
			break;

		case F:
			v = params[0].getDouble();
			v2 = params[1].getDouble();
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
			// mean and median are undefined
			break;

		case EXPONENTIAL:
			double lambda = params[0].getDouble();
			mean = 1 / lambda;
			sigma = 1 / lambda;
			break;
		case BETA:
			double alpha = params[0].getDouble(); // (shape)
			double beta = params[1].getDouble(); // (scale)
			mean = alpha / (alpha + beta);
			sigma = Math.sqrt(alpha * beta / (alpha + beta + 1)) / (alpha + beta);
			break;
		case GAMMA:
			shape = params[0].getDouble();
			scale = params[1].getDouble();
			mean = shape * scale;
			sigma = Math.sqrt(shape) * scale;
			break;

		case WEIBULL:
			shape = params[0].getDouble();
			scale = params[1].getDouble();

			mean = scale * MyMath2.gamma(1 + 1 / shape);
			variance = scale * scale * MyMath2.gamma(1 + 2 / shape)
					- mean * mean;
			sigma = Math.sqrt(variance);

			break;

		case LOGNORMAL:

			// TODO: may not be correct
			double meanParam = params[0].getDouble();
			double sdParam = params[1].getDouble();
			double varParam = sdParam * sdParam;

			mean = Math.exp(meanParam + varParam / 2);

			double var = (Math.exp(varParam) - 1)
					* Math.exp(2 * meanParam + varParam);
			sigma = Math.sqrt(var);

			break;

		case LOGISTIC:
			mean = params[0].getDouble();
			scale = params[1].getDouble();
			sigma = Math.PI * scale / Math.sqrt(3);
			break;

		case PASCAL:
			r = params[0].getDouble();
			p = params[1].getDouble();
			// care needed, p and 1-p are swapped from here
			// https://en.wikipedia.org/wiki/Negative_binomial_distribution
			// OK here:
			// http://stat.ethz.ch/R-manual/R-devel/library/stats/html/NegBinomial.html
			mean = r * (1 - p) / p;
			var = r * (1 - p) / p / p;
			sigma = Math.sqrt(var);
			break;

		case POISSON:
			mean = params[0].getDouble();
			sigma = Math.sqrt(mean);
			break;

		case BINOMIAL:
			n = params[0].getDouble();
			p = params[1].getDouble();
			mean = n * p;
			var = n * p * (1 - p);
			sigma = Math.sqrt(var);
			break;

		case HYPERGEOMETRIC:
			N = params[0].getDouble();
			k = params[1].getDouble();
			n = params[2].getDouble();

			mean = n * k / N;
			var = n * k * (N - k) * (N - n) / (N * N * (N - 1));
			sigma = Math.sqrt(var);
			break;

		}

		return new Double[]{ mean, sigma };
	}

	/**
	 * Returns distribution algorithm for the given parameters
	 * @param value variable value
	 * @param params distribution parameters
	 * @param distType distribution type
	 * @param isCumulative whether it's cumulative
	 * @return probability algorithm
	 */
	public AlgoDistribution getDistributionAlgorithm(GeoNumeric value, GeoNumberValue[] params,
			Dist distType, boolean isCumulative) {
		GeoNumberValue param1 = null, param2 = null, param3 = null;

		if (params.length > 0) {
			param1 = params[0];
		}
		if (params.length > 1) {
			param2 = params[1];
		}
		if (params.length > 2) {
			param3 = params[2];
		}

		Construction cons = app.getKernel().getConstruction();
		AlgoDistribution algo = getCommand(distType, cons, param1, param2,
				param3, value, isCumulative);

		return algo;
	}

	/**
	 * If isCumulative = true, returns P(X &lt;= value) for the given distribution
	 * If isCumulative = false, returns P(X = value) for the given distribution
	 *
	 * @param value
	 *            variable value
	 * @param params
	 *            distribution parameters
	 * @param distType
	 *            distribution type
	 * @param isCumulative
	 *            whether it's cumulative
	 * @return probability
	 */
	public double probability(double value, GeoNumberValue[] params, Dist distType,
			boolean isCumulative) {
		GeoNumeric numeric = new GeoNumeric(app.getKernel().getConstruction(), value);
		AlgoDistribution algo = getDistributionAlgorithm(numeric, params, distType, isCumulative);
		return algo.getResult().getDouble();
	}

	/**
	 * Returns an interval probability for the given distribution and
	 * probability mode. If mode == PROB_INTERVAL then P(low &lt;= X &lt;= high) is
	 * returned. If mode == PROB_LEFT then P(low &lt;= X) is returned. If mode ==
	 * PROB_RIGHT then P(X &lt;= high) is returned.
	 *
	 * @param low
	 *            interval start
	 * @param high
	 *            interval end
	 * @param distType
	 *            distribution type
	 * @param params
	 *            distribution parameters
	 * @param probMode
	 *            left / right /interval
	 * @return cumulative probability of an interval
	 */
	public double intervalProbability(double low, double high, Dist distType,
			GeoNumberValue[] params, int probMode) {
		if (probMode == ProbabilityCalculatorView.PROB_LEFT) {
			return probability(high, params, distType, true);
		} else if (probMode == ProbabilityCalculatorView.PROB_RIGHT) {

			if (isDiscrete(distType)) {
				return 1 - probability(low - 1, params, distType, true);
			}

			return 1 - probability(low, params, distType, true);

		} else if (probMode == ProbabilityCalculatorView.PROB_TWO_TAILED) {
			return rightProbability(high, params, distType)
					- probability(low, params, distType, true);
		} else { // ProbabilityCalculator.PROB_INTERVAL

			if (isDiscrete(distType)) {
				return probability(high, params, distType, true)
						- probability(low - 1, params, distType, true);
			}

			return probability(high, params, distType, true)
					- probability(low, params, distType, true);
		}
	}

	public double rightProbability(double high, GeoNumberValue[] params, Dist distType) {
		return 1 - probability(high, params, distType, true);
	}

	/**
	 * Returns an inverse probability for a selected distribution and a given
	 * cumulative (left area) probability.
	 *
	 * @param distType
	 *            distribution type
	 * @param prob
	 *            cumulative probability
	 * @param params
	 *            distribution parameters
	 * @return inverse probability
	 */
	public double inverseProbability(Dist distType, double prob,
			GeoNumberValue[] params) {

		GeoNumberValue param1 = null, param2 = null, param3 = null;

		Construction cons = app.getKernel().getConstruction();

		if (params.length > 0) {
			param1 = params[0];
		}
		if (params.length > 1) {
			param2 = params[1];
		}
		if (params.length > 2) {
			param3 = params[2];
		}

		AlgoDistribution algo = getInverseCommand(distType, cons, param1,
				param2, param3, new GeoNumeric(cons, prob));

		return algo.getResult().getDouble();
	}

}
