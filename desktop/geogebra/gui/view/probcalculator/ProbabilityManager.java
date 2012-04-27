package geogebra.gui.view.probcalculator;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.settings.ProbabilityCalculatorSettings;
import geogebra.main.Application;

import java.util.HashMap;



/**
 * Class to handle probability calculations and to maintain the fields associated
 * with the various probability distribution used by ProbabilityCalculator.
 * 
 * @author G Sturr
 * 
 */
public class ProbabilityManager {

	private Application app;
	private ProbabilityCalculator probCalc;





	public ProbabilityManager(Application app, ProbabilityCalculator probCalc){

		this.app = app;
		this.probCalc = probCalc;

	}
	
	/**
	 * Returns true of the given distribution type is discrete
	 * @param distType
	 * @return
	 */
	public boolean isDiscrete(int distType) {
		return distType == ProbabilityCalculatorSettings.DIST_BINOMIAL 
		|| distType == ProbabilityCalculatorSettings.DIST_PASCAL
		|| distType == ProbabilityCalculatorSettings.DIST_HYPERGEOMETRIC 
		|| distType == ProbabilityCalculatorSettings.DIST_POISSON;

	}
	
	

	/**
	 * Creates a hash map that can return a JComboBox menu string for
	 * distribution type constant 
	 * Key = display type constant 
	 * Value = menu item string
	 */
	protected HashMap<Integer, String> getDistributionMap() {

		HashMap<Integer, String> plotMap = new HashMap<Integer, String>();

		plotMap.put(ProbabilityCalculatorSettings.DIST_NORMAL, app.getMenu("Distribution.Normal"));
		plotMap.put(ProbabilityCalculatorSettings.DIST_STUDENT, app.getMenu("Distribution.StudentT"));
		plotMap.put(ProbabilityCalculatorSettings.DIST_CHISQUARE, app.getMenu("Distribution.ChiSquare"));
		plotMap.put(ProbabilityCalculatorSettings.DIST_F, app.getMenu("Distribution.F"));
		plotMap.put(ProbabilityCalculatorSettings.DIST_EXPONENTIAL, app.getMenu("Distribution.Exponential"));
		plotMap.put(ProbabilityCalculatorSettings.DIST_CAUCHY, app.getMenu("Distribution.Cauchy"));
		plotMap.put(ProbabilityCalculatorSettings.DIST_WEIBULL, app.getMenu("Distribution.Weibull"));
		plotMap.put(ProbabilityCalculatorSettings.DIST_LOGISTIC, app.getCommand("Logistic"));
		plotMap.put(ProbabilityCalculatorSettings.DIST_LOGNORMAL, app.getCommand("LogNormal"));

		plotMap.put(ProbabilityCalculatorSettings.DIST_GAMMA, app.getMenu("Distribution.Gamma"));
		plotMap.put(ProbabilityCalculatorSettings.DIST_BINOMIAL, app.getMenu("Distribution.Binomial"));
		plotMap.put(ProbabilityCalculatorSettings.DIST_PASCAL, app.getMenu("Distribution.Pascal"));
		plotMap.put(ProbabilityCalculatorSettings.DIST_POISSON, app.getMenu("Distribution.Poisson"));
		plotMap.put(ProbabilityCalculatorSettings.DIST_HYPERGEOMETRIC, app.getMenu("Distribution.Hypergeometric"));

		return plotMap;
	}

	/**
	 * Creates a reverse hash map that can return a distribution constant for a
	 * string selected in a JComboBox distribution menu 
	 * Key = menu item string
	 * Value = display type constant
	 */
	protected HashMap<String, Integer> getReverseDistributionMap() {

		HashMap<Integer, String> plotMap = getDistributionMap();
		HashMap<String, Integer> plotMapReverse = new HashMap<String, Integer>();
		for (Integer key : plotMap.keySet()) {
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
	protected static String[][] getParameterLabelArray(Application app){

		String[][] parameterLabels = new String[ProbabilityCalculatorSettings.distCount][4];

		parameterLabels[ProbabilityCalculatorSettings.DIST_NORMAL][0] = app.getMenu("Mean");
		parameterLabels[ProbabilityCalculatorSettings.DIST_NORMAL][1] = app.getMenu("StandardDeviation.short");

		parameterLabels[ProbabilityCalculatorSettings.DIST_STUDENT][0] = app.getMenu("DegreesOfFreedom.short");

		parameterLabels[ProbabilityCalculatorSettings.DIST_CHISQUARE][0] = app.getMenu("DegreesOfFreedom.short");

		parameterLabels[ProbabilityCalculatorSettings.DIST_F][0] = app.getMenu("DegreesOfFreedom1.short");
		parameterLabels[ProbabilityCalculatorSettings.DIST_F][1] = app.getMenu("DegreesOfFreedom2.short");

		parameterLabels[ProbabilityCalculatorSettings.DIST_EXPONENTIAL][0] = app.getMenu("Mean");

		parameterLabels[ProbabilityCalculatorSettings.DIST_CAUCHY][0] = app.getMenu("Median");
		parameterLabels[ProbabilityCalculatorSettings.DIST_CAUCHY][1] = app.getMenu("Distribution.Scale");

		parameterLabels[ProbabilityCalculatorSettings.DIST_WEIBULL][0] = app.getMenu("Distribution.Shape");
		parameterLabels[ProbabilityCalculatorSettings.DIST_WEIBULL][1] = app.getMenu("Distribution.Scale");
		
		parameterLabels[ProbabilityCalculatorSettings.DIST_LOGISTIC][0] = app.getMenu("Mean");
		parameterLabels[ProbabilityCalculatorSettings.DIST_LOGISTIC][1] = app.getMenu("Distribution.Scale");
		
		parameterLabels[ProbabilityCalculatorSettings.DIST_LOGNORMAL][0] = app.getMenu("Mean");
		parameterLabels[ProbabilityCalculatorSettings.DIST_LOGNORMAL][1] = app.getMenu("StandardDeviation.short");

		parameterLabels[ProbabilityCalculatorSettings.DIST_GAMMA][0] = app.getMenu("Alpha.short");
		parameterLabels[ProbabilityCalculatorSettings.DIST_GAMMA][1] = app.getMenu("Beta.short");

		parameterLabels[ProbabilityCalculatorSettings.DIST_BINOMIAL][0] = app.getMenu("Binomial.number");
		parameterLabels[ProbabilityCalculatorSettings.DIST_BINOMIAL][1] = app.getMenu("Binomial.probability");

		parameterLabels[ProbabilityCalculatorSettings.DIST_PASCAL][0] = app.getMenu("Binomial.number");
		parameterLabels[ProbabilityCalculatorSettings.DIST_PASCAL][1] = app.getMenu("Binomial.probability");

		parameterLabels[ProbabilityCalculatorSettings.DIST_POISSON][0] = app.getMenu("Mean");

		parameterLabels[ProbabilityCalculatorSettings.DIST_HYPERGEOMETRIC][0] = app.getMenu("Hypergeometric.population");
		parameterLabels[ProbabilityCalculatorSettings.DIST_HYPERGEOMETRIC][1] = app.getMenu("Hypergeometric.number");
		parameterLabels[ProbabilityCalculatorSettings.DIST_HYPERGEOMETRIC][2] = app.getMenu("Hypergeometric.sample");

		return parameterLabels;
	}

	
	/**
	 * Returns an array of GeoGebra probability distribution command names
	 * indexed by the distribution types
	 * 
	 * @return
	 */
	protected static String[] getCommand(){

		String[] cmd = new String[ProbabilityCalculatorSettings.distCount];
		cmd[ProbabilityCalculatorSettings.DIST_NORMAL] = "Normal";
		cmd[ProbabilityCalculatorSettings.DIST_STUDENT] = "TDistribution";
		cmd[ProbabilityCalculatorSettings.DIST_CHISQUARE] = "ChiSquared";
		cmd[ProbabilityCalculatorSettings.DIST_F] = "FDistribution";
		cmd[ProbabilityCalculatorSettings.DIST_CAUCHY] = "Cauchy";
		cmd[ProbabilityCalculatorSettings.DIST_EXPONENTIAL] = "Exponential";
		cmd[ProbabilityCalculatorSettings.DIST_GAMMA] = "Gamma";
		cmd[ProbabilityCalculatorSettings.DIST_WEIBULL] = "Weibull";
		cmd[ProbabilityCalculatorSettings.DIST_LOGISTIC] = "Logistic";
		cmd[ProbabilityCalculatorSettings.DIST_LOGNORMAL] = "LogNormal";
		
		
		// --- discrete
		cmd[ProbabilityCalculatorSettings.DIST_BINOMIAL] = "BinomialDist";
		cmd[ProbabilityCalculatorSettings.DIST_PASCAL] = "Pascal";
		cmd[ProbabilityCalculatorSettings.DIST_POISSON] = "Poisson";
		cmd[ProbabilityCalculatorSettings.DIST_HYPERGEOMETRIC] = "HyperGeometric";

		return cmd;
	}

	
	/**
	 * Returns an array of GeoGebra inverse probability distribution command names
	 * indexed by the distribution types
	 * 
	 * @return
	 */
	protected static String[] getInverseCommand(){

		String[] inverseCmd = new String[ProbabilityCalculatorSettings.distCount];
		inverseCmd[ProbabilityCalculatorSettings.DIST_NORMAL] = "InverseNormal";
		inverseCmd[ProbabilityCalculatorSettings.DIST_STUDENT] = "InverseTDistribution";
		inverseCmd[ProbabilityCalculatorSettings.DIST_CHISQUARE] = "InverseChiSquared";
		inverseCmd[ProbabilityCalculatorSettings.DIST_F] = "InverseFDistribution";
		inverseCmd[ProbabilityCalculatorSettings.DIST_CAUCHY] = "InverseCauchy";
		inverseCmd[ProbabilityCalculatorSettings.DIST_EXPONENTIAL] = "InverseExponential";
		inverseCmd[ProbabilityCalculatorSettings.DIST_GAMMA] = "InverseGamma";
		inverseCmd[ProbabilityCalculatorSettings.DIST_WEIBULL] = "InverseWeibull";
		//TODO ------------- inverse cmds for these dist.
		inverseCmd[ProbabilityCalculatorSettings.DIST_LOGNORMAL] = "InverseWeibull"; 
		inverseCmd[ProbabilityCalculatorSettings.DIST_LOGISTIC] = "InverseWeibull";
		// --- discrete
		inverseCmd[ProbabilityCalculatorSettings.DIST_BINOMIAL] = "InverseBinomial";
		inverseCmd[ProbabilityCalculatorSettings.DIST_PASCAL] = "InversePascal";
		inverseCmd[ProbabilityCalculatorSettings.DIST_POISSON] = "InversePoisson";
		inverseCmd[ProbabilityCalculatorSettings.DIST_HYPERGEOMETRIC] = "InverseHyperGeometric";

		return inverseCmd;
	}

	/**
	 * Returns an array of the required number of parameters needed for each distribution type. 
	 * The array is indexed by distribution type.
	 * @return
	 */
	protected static int[] getParmCount(){

		int[] parmCount = new int[ProbabilityCalculatorSettings.distCount];
		parmCount[ProbabilityCalculatorSettings.DIST_NORMAL] = 2;
		parmCount[ProbabilityCalculatorSettings.DIST_STUDENT] = 1;
		parmCount[ProbabilityCalculatorSettings.DIST_CHISQUARE] = 1;
		parmCount[ProbabilityCalculatorSettings.DIST_F] = 2;
		parmCount[ProbabilityCalculatorSettings.DIST_CAUCHY] = 2;
		parmCount[ProbabilityCalculatorSettings.DIST_EXPONENTIAL] = 1;
		parmCount[ProbabilityCalculatorSettings.DIST_GAMMA] = 2;
		parmCount[ProbabilityCalculatorSettings.DIST_WEIBULL] = 2;
		parmCount[ProbabilityCalculatorSettings.DIST_LOGNORMAL] = 2;
		parmCount[ProbabilityCalculatorSettings.DIST_LOGISTIC] = 2;
		// --- discrete
		parmCount[ProbabilityCalculatorSettings.DIST_BINOMIAL] = 2;
		parmCount[ProbabilityCalculatorSettings.DIST_PASCAL] = 2;
		parmCount[ProbabilityCalculatorSettings.DIST_POISSON] = 1;
		parmCount[ProbabilityCalculatorSettings.DIST_HYPERGEOMETRIC] = 3;

		return parmCount;
	}



	/** Creates a map that returns default parameter values for each distribution type.
	/*  Key = distribution type constant
	/*  Value = default parameter values for the distribution type 
	 */
	protected static HashMap<Integer,double[]> getDefaultParameterMap(){
		HashMap<Integer,double[]> defaultParameterMap = new HashMap<Integer,double[]>();

		defaultParameterMap.put(ProbabilityCalculatorSettings.DIST_NORMAL, new double[] {0, 1}); // mean = 0, sigma = 1
		defaultParameterMap.put(ProbabilityCalculatorSettings.DIST_STUDENT, new double[] {10}); // df = 10
		defaultParameterMap.put(ProbabilityCalculatorSettings.DIST_CHISQUARE, new double[] {6}); // df = 6

		defaultParameterMap.put(ProbabilityCalculatorSettings.DIST_F, new double[] {5,2}); // df1 = 5, df2 = 2
		defaultParameterMap.put(ProbabilityCalculatorSettings.DIST_EXPONENTIAL, new double[] {1}); // mean = 1
		defaultParameterMap.put(ProbabilityCalculatorSettings.DIST_GAMMA, new double[] {3,2}); // alpha = 3, beta = 2
		defaultParameterMap.put(ProbabilityCalculatorSettings.DIST_CAUCHY, new double[] {0,1}); // median = 0, scale = 1
		defaultParameterMap.put(ProbabilityCalculatorSettings.DIST_WEIBULL, new double[] {5,1}); // shape = 5, scale = 1
		defaultParameterMap.put(ProbabilityCalculatorSettings.DIST_LOGNORMAL, new double[] {0,1}); //  mean = 0, sigma = 1
		defaultParameterMap.put(ProbabilityCalculatorSettings.DIST_LOGISTIC, new double[] {5,2}); // mean = 5, scale = 2
		
		
		defaultParameterMap.put(ProbabilityCalculatorSettings.DIST_BINOMIAL, new double[] {20, 0.5}); // n = 20, p = 0.5
		defaultParameterMap.put(ProbabilityCalculatorSettings.DIST_PASCAL, new double[] {10, 0.5}); // n = 10, p = 0.5
		defaultParameterMap.put(ProbabilityCalculatorSettings.DIST_POISSON, new double[] {4}); // mean = 4
		defaultParameterMap.put(ProbabilityCalculatorSettings.DIST_HYPERGEOMETRIC, new double[] {60, 10, 20}); // pop = 60, n = 10, sample = 20


		return defaultParameterMap;

	}





	/**
	 * Returns the appropriate plot dimensions for a given distribution and parameter set. 
	 * Plot dimensions are returned as an array of double: {xMin, xMax, yMin, yMax} 	 
	 */
	protected double[] getPlotDimensions(int selectedDist, double [] parms, GeoElement densityCurve, boolean isCumulative){

		double xMin = 0, xMax = 0, yMin = 0, yMax = 0;

		// retrieve the parameter values from the parmList geo
		//double [] parms = getCurrentParameters();
		double mean, sigma, v, v2, k, median, scale, shape, mode, n, p, pop, sample, sd, variance;	

		switch(selectedDist){

		case ProbabilityCalculatorSettings.DIST_NORMAL:
			mean = parms[0];
			sigma = parms[1];
			xMin = mean - 5*sigma;
			xMax = mean + 5*sigma;
			yMin = 0;
			yMax = 1.2* ((GeoFunction)densityCurve).evaluate(mean);	
			break;

		case ProbabilityCalculatorSettings.DIST_STUDENT:
			xMin = -5;
			xMax = 5;
			yMin = 0;
			yMax = 1.2* ((GeoFunction)densityCurve).evaluate(0);	
			break;

		case ProbabilityCalculatorSettings.DIST_CHISQUARE:
			k = parms[0];		
			xMin = 0;
			xMax = 4*k;
			yMin = 0;
			if(k>2)
				// mode occurs when x = k-2; add 0.1 to handle k near 2
				yMax = 1.2* ((GeoFunction)densityCurve).evaluate(k-2+0.1);	
			else
				// mode occurs at x = 0, but we only use x near zero
				yMax = 1.2* ((GeoFunction)densityCurve).evaluate(0.1);	
			break;

		case ProbabilityCalculatorSettings.DIST_F:
			v = parms[0];
			v2 = parms[1];
			mean = v2 > 2 ? v2 / (v2 - 2): 1;
			mode = (v-2)/v * v2/(v2+2);
			// TODO variance only valid for v2 > 4, need to handle v2<4
			variance = 2*v*v*(v + v2 - 2)/(v2*(v-2)*(v-2)*(v-4));
			xMin = 0;
			xMax = mean + 5 * Math.sqrt(variance);
			yMin = 0;
			if(v>2)
				yMax = 1.2*((GeoFunction)densityCurve).evaluate(mode);	
			else
				yMax = 1.2*((GeoFunction)densityCurve).evaluate(0.01);	
			//System.out.println("F ymax: " + yMax);
			break;

		case ProbabilityCalculatorSettings.DIST_CAUCHY:
			median = parms[0];
			scale = parms[1];	
			// TODO --- better estimates
			xMin = median - 6*scale;
			xMax = median + 6*scale;
			yMin = 0;
			yMax = 1.2* (1/(Math.PI*scale)); // Cauchy amplitude = 1/(pi*scale)

			break;


		case ProbabilityCalculatorSettings.DIST_EXPONENTIAL:
			double lambda = parms[0];		
			xMin = 0;
			xMax = 4*(1/lambda);   // st dev = 1/lambda	
			yMin = 0;
			yMax = 1.2* lambda;   	
			break;


		case ProbabilityCalculatorSettings.DIST_GAMMA:
			double alpha = parms[0]; // (shape)
			double beta = parms[1];  //(scale)
			mode = (alpha - 1) * beta;
			mean = alpha*beta;
			sd = Math.sqrt(alpha)*beta;
			xMin = 0;
			xMax = mean + 5*sd;  
			yMin = 0;
			if(alpha > 1)  // mode = (alpha -1)*beta
				yMax = 1.2 * ((GeoFunction)densityCurve).evaluate(mode);	
			else
				yMax = 1.2 * ((GeoFunction)densityCurve).evaluate(0);	
			break;


		case ProbabilityCalculatorSettings.DIST_WEIBULL:
			shape = parms[0];	
			scale = parms[1];	
			median = scale*Math.pow(Math.log(2), 1/shape);
			xMin = 0;
			xMax = 2*median;
			yMin = 0;
			// mode for shape >1
			if(shape > 1){
				mode = scale*Math.pow(1 - 1/shape,1/shape);
				yMax = 1.2*((GeoFunction)densityCurve).evaluate(mode);
			}else{
				yMax = 4;
			}

			break;

		case ProbabilityCalculatorSettings.DIST_LOGNORMAL:
			mean = parms[0];
			sigma = parms[1];
			double var = (Math.exp(sigma*sigma) - 1)*Math.exp(2*mean + sigma*sigma);
			mode = Math.exp(mean - sigma*sigma);
			xMin = 0;
			xMax = mean + 5*Math.sqrt(var);
			yMin = 0;
			yMax = 1.2* ((GeoFunction)densityCurve).evaluate(mode);	
			break;

			
		case ProbabilityCalculatorSettings.DIST_LOGISTIC:
			mean = parms[0];
			scale = parms[1];
			sd = Math.PI*scale/Math.sqrt(3);
			xMin = mean - 5*sd;
			xMax = mean + 5*sd;
			yMin = 0;
			yMax = 1.2* ((GeoFunction)densityCurve).evaluate(mean);	
			break;


		case ProbabilityCalculatorSettings.DIST_PASCAL:
		case ProbabilityCalculatorSettings.DIST_POISSON:
			xMin = probCalc.getDiscreteXMin();
			xMax = probCalc.getDiscreteXMax();
			yMin = 0;	
			yMax = 1.2* getDiscreteYMax(selectedDist, parms, (int)xMin, (int)xMax);
			xMin -= 1;
			
			break;
			
			
		case ProbabilityCalculatorSettings.DIST_BINOMIAL:
		case ProbabilityCalculatorSettings.DIST_HYPERGEOMETRIC:
			xMin = probCalc.getDiscreteXMin();
			xMax = probCalc.getDiscreteXMax();
			yMin = 0;	
			yMax = 1.2* getDiscreteYMax(selectedDist, parms,(int)xMin, (int)xMax);
			xMin -= 1;
			xMax +=1;
			break;

		}

		if(isCumulative){
			yMin = 0;
			yMax = 1.2;
		}
		double[] d = {xMin, xMax, yMin, yMax};
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
	private double getDiscreteYMax(int distType, double[] parms, int low, int high){

		double max = 0;

		for(int i=low; i <= high; i++){
			max = Math.max(max, probability(i, parms, distType, false));
		}
		return max;
	}


	/**
	 * If isCumulative = true,  returns P(X <= value) for the given distribution 
	 * If isCumulative = false,  returns P(X = value) for the given distribution 
	 */
	public double probability(double value, double [] parms, int distType, boolean isCumulative ){

		String[] cmd = ProbabilityManager.getCommand();
		double prob = 0;

		try {

			// Build GeoGebra string for exact discrete probability
			// e.g. "Binomial[ parms[0] , parms[1] , value, false ]"

			StringBuilder expr = new StringBuilder();
			expr.append(cmd[distType]);
			expr.append("[");
			for(int i=0; i < parms.length; i++){
				expr.append(parms[i]);
				expr.append(",");
			}
			expr.append(value + "," + isCumulative + "]");

			// Use the string to calculate the probability
			prob = evaluateExpression(expr.toString());	


		} catch (Exception e) {		
			e.printStackTrace();
		}

		return prob;
	}


	/**
	 * Returns an interval probability for the given distribution and probability mode.
	 * If mode == PROB_INTERVAL then P(low <= X <= high) is returned.
	 * If mode == PROB_LEFT then P(low <= X) is returned.
	 * If mode == PROB_RIGHT then P(X <= high) is returned.
	 */
	public double intervalProbability(double low, double high, int distType, double [] parms, int probMode){

		String[] cmd = getCommand();

		String exprHigh = "";
		String exprLow = "";
		double prob = 0;


		try {

			// Build GeoGebra strings for high and low cumulative probabilities
			// e.g. "Normal[ parms[0] , parms[1] , high ]"
			// =================================================

			StringBuilder partialExpr = new StringBuilder();
			partialExpr.append(cmd[distType]);
			partialExpr.append("[");
			for(int i=0; i < parms.length; i++){
				partialExpr.append(parms[i]);
				partialExpr.append(",");
			}

			StringBuilder highExpr = new StringBuilder(partialExpr.toString());
			StringBuilder lowExpr = new StringBuilder(partialExpr.toString());

			highExpr.append(high);
			lowExpr.append(isDiscrete(distType) ? low - 1 : low);

			// for discrete case boolean cumulative must also be included
			if(isDiscrete(distType)){
				highExpr.append(", true");
				lowExpr.append(", true");
			}

			highExpr.append("]");
			lowExpr.append("]");


			//System.out.println(highExpr.toString());
			//System.out.println(lowExpr.toString());


			// Use the strings to calculate the interval probability
			// ========================================

			if(probMode == ProbabilityCalculator.PROB_LEFT)
				prob = evaluateExpression(highExpr.toString());	
			else if(probMode == ProbabilityCalculator.PROB_RIGHT)
				prob = 1 - evaluateExpression(lowExpr.toString());
			else {
				//Application.debug(highExpr.toString()+" "+lowExpr.toString());
				prob = evaluateExpression(highExpr.toString()) - evaluateExpression(lowExpr.toString());
			}


		} catch (Exception e) {		
			e.printStackTrace();
		}

		return prob;
	}



	/**
	 * Returns an inverse probability for a selected distribution and a given
	 * cumulative (left area) probability.
	 * 
	 * @param prob
	 *            cumulative probability
	 */
	protected double inverseProbability(int distType, double prob, double[] parms){

		String[] inverseCmd = ProbabilityManager.getInverseCommand();
		double result = 0;


		try {
			// build geogebra string for calculating inverse prob 
			// e.g. "InverseNormal[ parms[0] , parms[1] , prob ]"
			StringBuilder sb = new StringBuilder();
			sb.append(inverseCmd[distType]);
			sb.append("[");
			for(int i=0; i < parms.length; i++){
				sb.append(parms[i]);
				sb.append(",");
			}
			sb.append(prob);
			sb.append("]");

			// evaluate the expression
			result = evaluateExpression(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Returns the result of substituting a given numeric value into a GeoGebra
	 * function given as a String expression.
	 * 
	 * @param expr
	 * @param x
	 * @return
	 */
	private double evaluateFunction(String expr, double x){

		GeoFunction tempGeo;
		tempGeo = app.getKernel().getAlgebraProcessor().evaluateToFunction(expr, false);	
		double result = tempGeo.evaluate(x);
		tempGeo.remove();

		return result;
	}

	
	/**
	 * Returns the numeric result of evaluating a given GeoGebra expression.
	 * 
	 * @param expr
	 * @return
	 */
	private double evaluateExpression(String expr){

		NumberValue nv;
		Kernel kernel = app.getKernel();
		
		// make sure eg Normal works in Swedish (Normal == PerpendicularLine)
		kernel.setUseInternalCommandNames(true);
		
		nv =  kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);	
		
		kernel.setUseInternalCommandNames(false);
		double result = nv.getDouble();

		return result;
	}



}
