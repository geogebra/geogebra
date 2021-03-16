package org.geogebra.common.gui.view.probcalculator;

import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.AlgoBinomialDist;
import org.geogebra.common.kernel.statistics.AlgoCauchy;
import org.geogebra.common.kernel.statistics.AlgoChiSquared;
import org.geogebra.common.kernel.statistics.AlgoDistribution;
import org.geogebra.common.kernel.statistics.AlgoExponential;
import org.geogebra.common.kernel.statistics.AlgoFDistribution;
import org.geogebra.common.kernel.statistics.AlgoGamma;
import org.geogebra.common.kernel.statistics.AlgoHyperGeometric;
import org.geogebra.common.kernel.statistics.AlgoInverseBinomial;
import org.geogebra.common.kernel.statistics.AlgoInverseCauchy;
import org.geogebra.common.kernel.statistics.AlgoInverseChiSquared;
import org.geogebra.common.kernel.statistics.AlgoInverseExponential;
import org.geogebra.common.kernel.statistics.AlgoInverseFDistribution;
import org.geogebra.common.kernel.statistics.AlgoInverseGamma;
import org.geogebra.common.kernel.statistics.AlgoInverseHyperGeometric;
import org.geogebra.common.kernel.statistics.AlgoInverseLogNormal;
import org.geogebra.common.kernel.statistics.AlgoInverseLogistic;
import org.geogebra.common.kernel.statistics.AlgoInverseNormal;
import org.geogebra.common.kernel.statistics.AlgoInversePascal;
import org.geogebra.common.kernel.statistics.AlgoInversePoisson;
import org.geogebra.common.kernel.statistics.AlgoInverseTDistribution;
import org.geogebra.common.kernel.statistics.AlgoInverseWeibull;
import org.geogebra.common.kernel.statistics.AlgoLogNormal;
import org.geogebra.common.kernel.statistics.AlgoLogistic;
import org.geogebra.common.kernel.statistics.AlgoNormal;
import org.geogebra.common.kernel.statistics.AlgoPascal;
import org.geogebra.common.kernel.statistics.AlgoPoisson;
import org.geogebra.common.kernel.statistics.AlgoTDistribution;
import org.geogebra.common.kernel.statistics.AlgoWeibull;
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
	private final Localization loc;
	private ProbabilityCalculatorView probCalc;

	/**
	 * @param app
	 *            application
	 * @param probCalc
	 *            probability calculator view
	 */
	public ProbabilityManager(App app, ProbabilityCalculatorView probCalc) {
		this.app = app;
		this.loc = app.getLocalization();
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
	 * Creates a hash map that can return a JComboBox menu string for
	 * distribution type constant Key = display type constant Value = menu item
	 * string
	 * 
	 * @return map distribution -&gt; localized name
	 */
	public HashMap<Dist, String> getDistributionMap() {

		HashMap<Dist, String> plotMap = new HashMap<>();

		plotMap.put(Dist.NORMAL, loc.getMenu("Distribution.Normal"));
		plotMap.put(Dist.STUDENT, loc.getMenu("Distribution.StudentT"));
		plotMap.put(Dist.CHISQUARE, loc.getMenu("Distribution.ChiSquare"));
		plotMap.put(Dist.F, loc.getMenu("Distribution.F"));
		plotMap.put(Dist.EXPONENTIAL, loc.getMenu("Distribution.Exponential"));
		plotMap.put(Dist.CAUCHY, loc.getMenu("Distribution.Cauchy"));
		plotMap.put(Dist.WEIBULL, loc.getMenu("Distribution.Weibull"));
		plotMap.put(Dist.LOGISTIC, loc.getCommand("Logistic"));
		plotMap.put(Dist.LOGNORMAL, loc.getCommand("LogNormal"));

		plotMap.put(Dist.GAMMA, loc.getMenu("Distribution.Gamma"));
		plotMap.put(Dist.BINOMIAL, loc.getMenu("Distribution.Binomial"));
		plotMap.put(Dist.PASCAL, loc.getMenu("Distribution.Pascal"));
		plotMap.put(Dist.POISSON, loc.getMenu("Distribution.Poisson"));
		plotMap.put(Dist.HYPERGEOMETRIC,
				loc.getMenu("Distribution.Hypergeometric"));

		return plotMap;
	}

	/**
	 * Creates a reverse hash map that can return a distribution constant for a
	 * string selected in a JComboBox distribution menu Key = menu item string
	 * Value = display type constant
	 * 
	 * @return map localized name -&gt; distribution
	 */
	public HashMap<String, Dist> getReverseDistributionMap() {

		HashMap<Dist, String> plotMap = getDistributionMap();
		HashMap<String, Dist> plotMapReverse = new HashMap<>();
		for (Entry<Dist, String> entry : plotMap.entrySet()) {
			plotMapReverse.put(entry.getValue(), entry.getKey());
		}

		return plotMapReverse;
	}

	/**
	 * Returns a 2D array of strings used to label the parameter fields for each
	 * type of distribution
	 * 
	 * @param loc
	 *            localization
	 * @return matrix of strings
	 */
	public static String[][] getParameterLabelArray(Localization loc) {

		String[][] parameterLabels = new String[ProbabilityCalculatorSettings.distCount][4];

		parameterLabels[ProbabilityCalculatorSettings.Dist.NORMAL
				.ordinal()][0] = loc.getMenu("Mean.short");
		parameterLabels[ProbabilityCalculatorSettings.Dist.NORMAL
				.ordinal()][1] = loc.getMenu("StandardDeviation.short");

		parameterLabels[ProbabilityCalculatorSettings.Dist.STUDENT
				.ordinal()][0] = loc.getMenu("DegreesOfFreedom.short");

		parameterLabels[ProbabilityCalculatorSettings.Dist.CHISQUARE
				.ordinal()][0] = loc.getMenu("DegreesOfFreedom.short");

		parameterLabels[ProbabilityCalculatorSettings.Dist.F.ordinal()][0] = loc
				.getMenu("DegreesOfFreedom1.short");
		parameterLabels[ProbabilityCalculatorSettings.Dist.F.ordinal()][1] = loc
				.getMenu("DegreesOfFreedom2.short");

		parameterLabels[ProbabilityCalculatorSettings.Dist.EXPONENTIAL
				.ordinal()][0] = Unicode.lambda + "";

		parameterLabels[ProbabilityCalculatorSettings.Dist.CAUCHY
				.ordinal()][0] = loc.getMenu("Median");
		parameterLabels[ProbabilityCalculatorSettings.Dist.CAUCHY
				.ordinal()][1] = loc.getMenu("Distribution.Scale");

		parameterLabels[ProbabilityCalculatorSettings.Dist.WEIBULL
				.ordinal()][0] = loc.getMenu("Distribution.Shape");
		parameterLabels[ProbabilityCalculatorSettings.Dist.WEIBULL
				.ordinal()][1] = loc.getMenu("Distribution.Scale");

		parameterLabels[ProbabilityCalculatorSettings.Dist.LOGISTIC
				.ordinal()][0] = loc.getMenu("Mean.short");
		parameterLabels[ProbabilityCalculatorSettings.Dist.LOGISTIC
				.ordinal()][1] = loc.getMenu("Distribution.Scale");

		parameterLabels[ProbabilityCalculatorSettings.Dist.LOGNORMAL
				.ordinal()][0] = loc.getMenu("Mean.short");
		parameterLabels[ProbabilityCalculatorSettings.Dist.LOGNORMAL
				.ordinal()][1] = loc.getMenu("StandardDeviation.short");

		parameterLabels[ProbabilityCalculatorSettings.Dist.GAMMA
				.ordinal()][0] = Unicode.alpha + "";
		parameterLabels[ProbabilityCalculatorSettings.Dist.GAMMA
				.ordinal()][1] = Unicode.beta + "";

		parameterLabels[ProbabilityCalculatorSettings.Dist.BINOMIAL
				.ordinal()][0] = loc.getMenu("Binomial.number");
		parameterLabels[ProbabilityCalculatorSettings.Dist.BINOMIAL
				.ordinal()][1] = loc.getMenu("Binomial.probability");

		parameterLabels[ProbabilityCalculatorSettings.Dist.PASCAL
				.ordinal()][0] = loc.getMenu("Binomial.number");
		parameterLabels[ProbabilityCalculatorSettings.Dist.PASCAL
				.ordinal()][1] = loc.getMenu("Binomial.probability");

		parameterLabels[ProbabilityCalculatorSettings.Dist.POISSON
				.ordinal()][0] = loc.getMenu("Mean.short");

		parameterLabels[ProbabilityCalculatorSettings.Dist.HYPERGEOMETRIC
				.ordinal()][0] = loc.getMenu("Hypergeometric.population");
		parameterLabels[ProbabilityCalculatorSettings.Dist.HYPERGEOMETRIC
				.ordinal()][1] = loc.getMenu("Hypergeometric.number");
		parameterLabels[ProbabilityCalculatorSettings.Dist.HYPERGEOMETRIC
				.ordinal()][2] = loc.getMenu("Hypergeometric.sample");

		return parameterLabels;
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
			ret = new AlgoInverseHyperGeometric(cons, param1, param2,
					param3, x);
			break;

		}

		if (ret != null) {
			ret.getConstruction().removeFromConstructionList(ret);
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
			ret = new AlgoNormal(cons, param1, param2, x, null);
			break;
		case STUDENT:
			ret = new AlgoTDistribution(cons, param1, x, null);
			break;
		case CHISQUARE:
			ret = new AlgoChiSquared(cons, param1, x, null);
			break;
		case F:
			ret = new AlgoFDistribution(cons, param1, param2, x, null);
			break;
		case CAUCHY:
			ret = new AlgoCauchy(cons, param1, param2, x, null);
			break;
		case EXPONENTIAL:
			ret = new AlgoExponential(cons, param1, x, null);
			break;
		case GAMMA:
			ret = new AlgoGamma(cons, param1, param2, x, null);
			break;
		case WEIBULL:
			ret = new AlgoWeibull(cons, param1, param2, x, null);
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
			cons.removeFromConstructionList(ret);
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
	public static int getParmCount(Dist dist) {

		switch (dist) {
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
	 * @param dist
	 *            distribution
	 * @return default parameters
	 */
	public static GeoNumeric[] getDefaultParameters(Dist dist, Construction cons) {
		double[] values = getDefaultParameters(dist);
		GeoNumeric[] params = new GeoNumeric[values.length];
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
	 * @param parms
	 *            parameters
	 * @param densityCurve
	 *            density curve
	 * @param isCumulative
	 *            cumulative?
	 * @return plot width and height
	 */
	public double[] getPlotDimensions(Dist selectedDist, GeoNumberValue[] parms,
			GeoElement densityCurve, boolean isCumulative) {

		double xMin = 0, xMax = 0, yMin = 0, yMax = 0;

		// retrieve the parameter values from the parmList geo
		// double [] parms = getCurrentParameters();
		double mean, sigma, v, v2, k, median, scale, shape, mode, sd;

		switch (selectedDist) {

		case NORMAL:
			mean = parms[0].getDouble();
			sigma = parms[1].getDouble();
			xMin = mean - 5 * sigma;
			xMax = mean + 5 * sigma;
			yMin = 0;
			yMax = 1.2 * ((GeoFunction) densityCurve).value(mean);
			break;

		case STUDENT:
			xMin = -5;
			xMax = 5;
			yMin = 0;
			yMax = 1.2 * ((GeoFunction) densityCurve).value(0);
			break;

		case CHISQUARE:
			k = parms[0].getDouble();
			xMin = 0;
			xMax = 4 * k;
			yMin = 0;
			if (k > 2) {
				// mode occurs when x = k-2; add 0.1 to handle k near 2
				yMax = 1.2 * ((GeoFunction) densityCurve).value(k - 2 + 0.1);
			} else {
				// mode occurs at x = 0, but we only use x near zero
				yMax = 1.2 * ((GeoFunction) densityCurve).value(0.1);
			}
			break;

		case F:
			v = parms[0].getDouble();
			v2 = parms[1].getDouble();
			mean = v2 > 2 ? v2 / (v2 - 2) : 1;
			mode = ((v - 2) * v2) / (v * (v2 + 2));

			xMin = 0;

			xMax = getContXMax((GeoFunction) densityCurve, 1, .2, -1);

			yMin = 0;
			if (v > 2) {
				yMax = 1.2 * ((GeoFunction) densityCurve).value(mode);
			} else {
				// yMax = 1.2 * ((GeoFunction) densityCurve).evaluate(0.01);
				yMax = 2.5;
			}

			break;

		case CAUCHY:
			median = parms[0].getDouble();
			scale = parms[1].getDouble();
			// TODO --- better estimates
			xMin = median - 6 * scale;
			xMax = median + 6 * scale;
			yMin = 0;
			yMax = 1.2 * (1 / (Math.PI * scale)); // Cauchy amplitude =
													// 1/(pi*scale)

			break;

		case EXPONENTIAL:
			double lambda = parms[0].getDouble();
			xMin = 0;
			// xMax = 4 * (1 / lambda); // st dev = 1/lambda
			xMax = getContXMax((GeoFunction) densityCurve, 1, .2, -1);
			yMin = 0;
			yMax = 1.2 * lambda;
			break;

		case GAMMA:
			double alpha = parms[0].getDouble(); // (shape)
			double beta = parms[1].getDouble(); // (scale)
			mode = (alpha - 1) * beta;
			mean = alpha * beta;
			sd = Math.sqrt(alpha) * beta;
			xMin = 0;
			xMax = mean + 5 * sd;
			yMin = 0;
			if (alpha > 1) {
				yMax = 1.2 * ((GeoFunction) densityCurve).value(mode);
			} else {
				yMax = 1.2 * ((GeoFunction) densityCurve).value(0);
			}
			break;

		case WEIBULL:
			shape = parms[0].getDouble();
			scale = parms[1].getDouble();
			median = scale * Math.pow(Math.log(2), 1 / shape);
			xMin = 0;
			xMax = 2 * median;
			yMin = 0;
			// mode for shape >1
			if (shape > 1) {
				mode = scale * Math.pow(1 - 1 / shape, 1 / shape);
				yMax = 1.2 * ((GeoFunction) densityCurve).value(mode);
			} else {
				yMax = 4;
			}

			break;

		case LOGNORMAL:
			double meanParm = parms[0].getDouble();
			double sdParm = parms[1].getDouble();
			double varParm = sdParm * sdParm;

			mean = Math.exp(meanParm + varParm / 2);

			double var = (Math.exp(varParm) - 1)
					* Math.exp(2 * meanParm + varParm);
			sigma = Math.sqrt(var);

			mode = Math.exp(meanParm - varParm);
			xMin = 0;
			xMax = mean + 5 * sigma;

			yMin = 0;
			yMax = 1.2 * ((GeoFunction) densityCurve).value(mode);
			break;

		case LOGISTIC:
			mean = parms[0].getDouble();
			scale = parms[1].getDouble();
			sd = Math.PI * scale / Math.sqrt(3);
			xMin = mean - 5 * sd;
			xMax = mean + 5 * sd;
			yMin = 0;
			yMax = 1.2 * ((GeoFunction) densityCurve).value(mean);
			break;

		case POISSON:
			// mode = mean = parms[0];
			mode = Math.floor(parms[0].getDouble());
			xMin = probCalc.getDiscreteXMin();
			xMax = probCalc.getDiscreteXMax();
			yMin = 0;
			yMax = 1.2 * probability(mode, parms, Dist.POISSON, false);
			xMin -= 1;

			break;

		case PASCAL:
			// r = parms[0]
			// p = parms[1]
			// care: p swapped with 1-p from Wikipedia page
			// https://en.wikipedia.org/wiki/Negative_binomial_distribution
			// if r <= 1, mode = 0, else mode = floor((1-p)(r-1)/(p)
			mode = 0;
			if (parms[0].getDouble() > 1) {
				mode = Math.floor((1 - parms[1].getDouble()) * (parms[0].getDouble() - 1)
						/ (parms[1].getDouble()));
			}

			xMin = probCalc.getDiscreteXMin();
			xMax = probCalc.getDiscreteXMax();
			yMin = 0;
			yMax = 1.2 * probability(mode, parms, Dist.PASCAL, false);
			xMin -= 1;

			break;

		case BINOMIAL:
			// n = parms[0]
			// p = parms[1]
			// mode = np
			mode = Math.floor((parms[0].getDouble() + 1) * parms[1].getDouble());

			xMin = probCalc.getDiscreteXMin();
			xMax = probCalc.getDiscreteXMax();
			yMin = 0;
			yMax = 1.2 * probability(mode, parms, Dist.BINOMIAL, false);
			xMin -= 1;
			xMax += 1;
			break;

		case HYPERGEOMETRIC:
			// N = parms[0];
			// k = parms[1];
			// n = parms[2];
			// mode = floor(n+1)(k+1)/(N+2);
			mode = Math.floor((parms[1].getDouble() + 1) * (parms[2].getDouble() + 1)
					/ (parms[0].getDouble() + 2));

			xMin = probCalc.getDiscreteXMin();
			xMax = probCalc.getDiscreteXMax();
			yMin = 0;
			yMax = 1.2 * probability(mode, parms, Dist.HYPERGEOMETRIC, false);
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
	 * @param parms
	 *            parameter values
	 * @return {mean, sigma} Note: if a values is undefined, array with null
	 *         element(s) is returned
	 */
	public Double[] getDistributionMeasures(Dist selectedDist, GeoNumberValue[] parms) {

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
			mean = parms[0].getDouble();
			sigma = parms[1].getDouble();
			break;

		case STUDENT:
			mean = 0d;
			v = parms[0].getDouble();
			if (v > 2) {
				sigma = Math.sqrt(v / (v - 2));
			}

			break;

		case CHISQUARE:
			k = parms[0].getDouble();
			mean = k;
			sigma = Math.sqrt(2 * k);
			break;

		case F:
			v = parms[0].getDouble();
			v2 = parms[1].getDouble();
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
			double lambda = parms[0].getDouble();
			mean = 1 / lambda;
			sigma = 1 / lambda;
			break;

		case GAMMA:
			double alpha = parms[0].getDouble(); // (shape)
			double beta = parms[1].getDouble(); // (scale)
			mean = alpha * beta;
			sigma = Math.sqrt(alpha) * beta;
			break;

		case WEIBULL:
			shape = parms[0].getDouble();
			scale = parms[1].getDouble();

			mean = scale * MyMath2.gamma(1 + 1 / shape);
			variance = scale * scale * MyMath2.gamma(1 + 2 / shape)
					- mean * mean;
			sigma = Math.sqrt(variance);

			break;

		case LOGNORMAL:

			// TODO: may not be correct
			double meanParm = parms[0].getDouble();
			double sdParm = parms[1].getDouble();
			double varParm = sdParm * sdParm;

			mean = Math.exp(meanParm + varParm / 2);

			double var = (Math.exp(varParm) - 1)
					* Math.exp(2 * meanParm + varParm);
			sigma = Math.sqrt(var);

			break;

		case LOGISTIC:
			mean = parms[0].getDouble();
			scale = parms[1].getDouble();
			sigma = Math.PI * scale / Math.sqrt(3);
			break;

		case PASCAL:
			r = parms[0].getDouble();
			p = parms[1].getDouble();
			// care needed, p and 1-p are swapped from here
			// https://en.wikipedia.org/wiki/Negative_binomial_distribution
			// OK here:
			// http://stat.ethz.ch/R-manual/R-devel/library/stats/html/NegBinomial.html
			mean = r * (1 - p) / p;
			var = r * (1 - p) / p / p;
			sigma = Math.sqrt(var);
			break;

		case POISSON:
			mean = parms[0].getDouble();
			sigma = Math.sqrt(mean);
			break;

		case BINOMIAL:
			n = parms[0].getDouble();
			p = parms[1].getDouble();
			mean = n * p;
			var = n * p * (1 - p);
			sigma = Math.sqrt(var);
			break;

		case HYPERGEOMETRIC:
			N = parms[0].getDouble();
			k = parms[1].getDouble();
			n = parms[2].getDouble();

			mean = n * k / N;
			var = n * k * (N - k) * (N - n) / (N * N * (N - 1));
			sigma = Math.sqrt(var);
			break;

		}

		Double[] d = { mean, sigma };
		return d;
	}

	/**
	 * If isCumulative = true, returns P(X <= value) for the given distribution
	 * If isCumulative = false, returns P(X = value) for the given distribution
	 * 
	 * @param value
	 *            variable value
	 * @param parms
	 *            distribution parameters
	 * @param distType
	 *            distribution type
	 * @param isCumulative
	 *            whether it's cumulative
	 * @return probability
	 */
	public double probability(double value, GeoNumberValue[] parms, Dist distType,
			boolean isCumulative) {

		GeoNumberValue param1 = null, param2 = null, param3 = null;

		Construction cons = app.getKernel().getConstruction();

		if (parms.length > 0) {
			param1 = parms[0];
		}
		if (parms.length > 1) {
			param2 = parms[1];
		}
		if (parms.length > 2) {
			param3 = parms[2];
		}

		AlgoDistribution algo = getCommand(distType, cons, param1, param2,
				param3, new GeoNumeric(cons, value), isCumulative);

		return algo.getResult().getDouble();

	}

	/**
	 * Returns an interval probability for the given distribution and
	 * probability mode. If mode == PROB_INTERVAL then P(low <= X <= high) is
	 * returned. If mode == PROB_LEFT then P(low <= X) is returned. If mode ==
	 * PROB_RIGHT then P(X <= high) is returned.
	 * 
	 * @param low
	 *            interval start
	 * @param high
	 *            interval end
	 * @param distType
	 *            distribution type
	 * @param parms
	 *            distribution parameters
	 * @param probMode
	 *            left / right /interval
	 * @return cumulative probability of an interval
	 */
	public double intervalProbability(double low, double high, Dist distType,
			GeoNumberValue[] parms, int probMode) {
		if (probMode == ProbabilityCalculatorView.PROB_LEFT) {
			return probability(high, parms, distType, true);
		} else if (probMode == ProbabilityCalculatorView.PROB_RIGHT) {

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
	 * @param distType
	 *            distribution type
	 * @param prob
	 *            cumulative probability
	 * @param parms
	 *            distribution parameters
	 * @return inverse probability
	 */
	public double inverseProbability(Dist distType, double prob,
			GeoNumberValue[] parms) {

		GeoNumberValue param1 = null, param2 = null, param3 = null;

		Construction cons = app.getKernel().getConstruction();

		if (parms.length > 0) {
			param1 = parms[0];
		}
		if (parms.length > 1) {
			param2 = parms[1];
		}
		if (parms.length > 2) {
			param3 = parms[2];
		}

		AlgoDistribution algo = getInverseCommand(distType, cons, param1,
				param2, param3, new GeoNumeric(cons, prob));

		return algo.getResult().getDouble();
	}

}
