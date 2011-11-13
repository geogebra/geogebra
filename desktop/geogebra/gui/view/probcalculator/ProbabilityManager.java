package geogebra.gui.view.probcalculator;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.arithmetic.NumberValue;
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


	// continuous distribution identifiers
	public static final int DIST_NORMAL = 0;
	public static final int DIST_STUDENT = 1;
	public static final int DIST_CHISQUARE = 2;
	public static final int DIST_F = 3;
	public static final int DIST_CAUCHY = 4;
	public static final int DIST_EXPONENTIAL = 5;
	public static final int DIST_GAMMA = 6;
	public static final int DIST_WEIBULL = 7;
	public static final int DIST_LOGISTIC = 8;
	public static final int DIST_LOGNORMAL = 9;
	public static final int DIST_ERLANG = 10;
	//public static final int DIST_UNIFORM = 11;
	//public static final int DIST_TRIANGULAR = 12;

	// discrete distribution identifiers
	public static final int DIST_BINOMIAL = 11;
	public static final int DIST_PASCAL = 12;
	public static final int DIST_HYPERGEOMETRIC = 13;
	public static final int DIST_POISSON = 14;

	public static final int distCount = 15;


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
		return distType == DIST_BINOMIAL 
		|| distType == DIST_PASCAL
		|| distType == DIST_HYPERGEOMETRIC 
		|| distType == DIST_POISSON;

	}
	
	

	/**
	 * Creates a hash map that can return a JComboBox menu string for
	 * distribution type constant 
	 * Key = display type constant 
	 * Value = menu item string
	 */
	protected HashMap<Integer, String> getDistributionMap() {

		HashMap<Integer, String> plotMap = new HashMap<Integer, String>();

		plotMap.put(DIST_NORMAL, app.getMenu("Distribution.Normal"));
		plotMap.put(DIST_STUDENT, app.getMenu("Distribution.StudentT"));
		plotMap.put(DIST_CHISQUARE, app.getMenu("Distribution.ChiSquare"));
		plotMap.put(DIST_F, app.getMenu("Distribution.F"));
		plotMap.put(DIST_EXPONENTIAL, app.getMenu("Distribution.Exponential"));
		plotMap.put(DIST_CAUCHY, app.getMenu("Distribution.Cauchy"));
		plotMap.put(DIST_WEIBULL, app.getMenu("Distribution.Weibull"));
		plotMap.put(DIST_LOGISTIC, app.getCommand("Logistic"));
		plotMap.put(DIST_LOGNORMAL, app.getCommand("LogNormal"));

		plotMap.put(DIST_GAMMA, app.getMenu("Distribution.Gamma"));
		plotMap.put(DIST_BINOMIAL, app.getMenu("Distribution.Binomial"));
		plotMap.put(DIST_PASCAL, app.getMenu("Distribution.Pascal"));
		plotMap.put(DIST_POISSON, app.getMenu("Distribution.Poisson"));
		plotMap.put(DIST_HYPERGEOMETRIC, app.getMenu("Distribution.Hypergeometric"));

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

		String[][] parameterLabels = new String[distCount][4];

		parameterLabels[DIST_NORMAL][0] = app.getMenu("Mean");
		parameterLabels[DIST_NORMAL][1] = app.getMenu("StandardDeviation.short");

		parameterLabels[DIST_STUDENT][0] = app.getMenu("DegreesOfFreedom.short");

		parameterLabels[DIST_CHISQUARE][0] = app.getMenu("DegreesOfFreedom.short");

		parameterLabels[DIST_F][0] = app.getMenu("DegreesOfFreedom1.short");
		parameterLabels[DIST_F][1] = app.getMenu("DegreesOfFreedom2.short");

		parameterLabels[DIST_EXPONENTIAL][0] = app.getMenu("Mean");

		parameterLabels[DIST_CAUCHY][0] = app.getMenu("Median");
		parameterLabels[DIST_CAUCHY][1] = app.getMenu("Distribution.Scale");

		parameterLabels[DIST_WEIBULL][0] = app.getMenu("Distribution.Shape");
		parameterLabels[DIST_WEIBULL][1] = app.getMenu("Distribution.Scale");
		
		parameterLabels[DIST_LOGISTIC][0] = app.getMenu("Mean");
		parameterLabels[DIST_LOGISTIC][1] = app.getMenu("Distribution.Scale");
		
		parameterLabels[DIST_LOGNORMAL][0] = app.getMenu("Mean");
		parameterLabels[DIST_LOGNORMAL][1] = app.getMenu("StandardDeviation.short");

		parameterLabels[DIST_GAMMA][0] = app.getMenu("Alpha.short");
		parameterLabels[DIST_GAMMA][1] = app.getMenu("Beta.short");

		parameterLabels[DIST_BINOMIAL][0] = app.getMenu("Binomial.number");
		parameterLabels[DIST_BINOMIAL][1] = app.getMenu("Binomial.probability");

		parameterLabels[DIST_PASCAL][0] = app.getMenu("Binomial.number");
		parameterLabels[DIST_PASCAL][1] = app.getMenu("Binomial.probability");

		parameterLabels[DIST_POISSON][0] = app.getMenu("Mean");

		parameterLabels[DIST_HYPERGEOMETRIC][0] = app.getMenu("Hypergeometric.population");
		parameterLabels[DIST_HYPERGEOMETRIC][1] = app.getMenu("Hypergeometric.number");
		parameterLabels[DIST_HYPERGEOMETRIC][2] = app.getMenu("Hypergeometric.sample");

		return parameterLabels;
	}

	
	/**
	 * Returns an array of GeoGebra probability distribution command names
	 * indexed by the distribution types
	 * 
	 * @return
	 */
	protected static String[] getCommand(){

		String[] cmd = new String[distCount];
		cmd[DIST_NORMAL] = "Normal";
		cmd[DIST_STUDENT] = "TDistribution";
		cmd[DIST_CHISQUARE] = "ChiSquared";
		cmd[DIST_F] = "FDistribution";
		cmd[DIST_CAUCHY] = "Cauchy";
		cmd[DIST_EXPONENTIAL] = "Exponential";
		cmd[DIST_GAMMA] = "Gamma";
		cmd[DIST_WEIBULL] = "Weibull";
		cmd[DIST_LOGISTIC] = "Logistic";
		cmd[DIST_LOGNORMAL] = "LogNormal";
		
		
		// --- discrete
		cmd[DIST_BINOMIAL] = "BinomialDist";
		cmd[DIST_PASCAL] = "Pascal";
		cmd[DIST_POISSON] = "Poisson";
		cmd[DIST_HYPERGEOMETRIC] = "HyperGeometric";

		return cmd;
	}

	
	/**
	 * Returns an array of GeoGebra inverse probability distribution command names
	 * indexed by the distribution types
	 * 
	 * @return
	 */
	protected static String[] getInverseCommand(){

		String[] inverseCmd = new String[distCount];
		inverseCmd[DIST_NORMAL] = "InverseNormal";
		inverseCmd[DIST_STUDENT] = "InverseTDistribution";
		inverseCmd[DIST_CHISQUARE] = "InverseChiSquare";
		inverseCmd[DIST_F] = "InverseFDistribution";
		inverseCmd[DIST_CAUCHY] = "InverseCauchy";
		inverseCmd[DIST_EXPONENTIAL] = "InverseExponential";
		inverseCmd[DIST_GAMMA] = "InverseGamma";
		inverseCmd[DIST_WEIBULL] = "InverseWeibull";
		//TODO ------------- inverse cmds for these dist.
		inverseCmd[DIST_LOGNORMAL] = "InverseWeibull"; 
		inverseCmd[DIST_LOGISTIC] = "InverseWeibull";
		// --- discrete
		inverseCmd[DIST_BINOMIAL] = "InverseBinomial";
		inverseCmd[DIST_PASCAL] = "InversePascal";
		inverseCmd[DIST_POISSON] = "InversePoisson";
		inverseCmd[DIST_HYPERGEOMETRIC] = "InverseHyperGeometric";

		return inverseCmd;
	}

	/**
	 * Returns an array of the required number of parameters needed for each distribution type. 
	 * The array is indexed by distribution type.
	 * @return
	 */
	protected static int[] getParmCount(){

		int[] parmCount = new int[distCount];
		parmCount[DIST_NORMAL] = 2;
		parmCount[DIST_STUDENT] = 1;
		parmCount[DIST_CHISQUARE] = 1;
		parmCount[DIST_F] = 2;
		parmCount[DIST_CAUCHY] = 2;
		parmCount[DIST_EXPONENTIAL] = 1;
		parmCount[DIST_GAMMA] = 2;
		parmCount[DIST_WEIBULL] = 2;
		parmCount[DIST_LOGNORMAL] = 2;
		parmCount[DIST_LOGISTIC] = 2;
		// --- discrete
		parmCount[DIST_BINOMIAL] = 2;
		parmCount[DIST_PASCAL] = 2;
		parmCount[DIST_POISSON] = 1;
		parmCount[DIST_HYPERGEOMETRIC] = 3;

		return parmCount;
	}



	/** Creates a map that returns default parameter values for each distribution type.
	/*  Key = distribution type constant
	/*  Value = default parameter values for the distribution type 
	 */
	protected static HashMap<Integer,double[]> getDefaultParameterMap(){
		HashMap<Integer,double[]> defaultParameterMap = new HashMap<Integer,double[]>();

		defaultParameterMap.put(DIST_NORMAL, new double[] {0, 1}); // mean = 0, sigma = 1
		defaultParameterMap.put(DIST_STUDENT, new double[] {10}); // df = 10
		defaultParameterMap.put(DIST_CHISQUARE, new double[] {6}); // df = 6

		defaultParameterMap.put(DIST_F, new double[] {5,2}); // df1 = 5, df2 = 2
		defaultParameterMap.put(DIST_EXPONENTIAL, new double[] {1}); // mean = 1
		defaultParameterMap.put(DIST_GAMMA, new double[] {3,2}); // alpha = 3, beta = 2
		defaultParameterMap.put(DIST_CAUCHY, new double[] {0,1}); // median = 0, scale = 1
		defaultParameterMap.put(DIST_WEIBULL, new double[] {5,1}); // shape = 5, scale = 1
		defaultParameterMap.put(DIST_LOGNORMAL, new double[] {0,1}); //  mean = 0, sigma = 1
		defaultParameterMap.put(DIST_LOGISTIC, new double[] {5,2}); // mean = 5, scale = 2
		
		
		defaultParameterMap.put(DIST_BINOMIAL, new double[] {20, 0.5}); // n = 20, p = 0.5
		defaultParameterMap.put(DIST_PASCAL, new double[] {10, 0.5}); // n = 10, p = 0.5
		defaultParameterMap.put(DIST_POISSON, new double[] {4}); // mean = 4
		defaultParameterMap.put(DIST_HYPERGEOMETRIC, new double[] {60, 10, 20}); // pop = 60, n = 10, sample = 20


		return defaultParameterMap;

	}





	/**
	 * Returns the appropriate plot dimensions for a given distribution and parameter set. 
	 * Plot dimensions are returned as an array of double: {xMin, xMax, yMin, yMax} 	 
	 *   
	 * @param distType
	 * @param parms
	 * @return
	 */
	protected double[] getPlotDimensions(int selectedDist, double [] parms, GeoElement densityCurve, boolean isCumulative){

		double xMin = 0, xMax = 0, yMin = 0, yMax = 0;

		// retrieve the parameter values from the parmList geo
		//double [] parms = getCurrentParameters();
		double mean, sigma, v, v2, k, median, scale, shape, mode, n, p, pop, sample, sd, variance;	

		switch(selectedDist){

		case DIST_NORMAL:
			mean = parms[0];
			sigma = parms[1];
			xMin = mean - 5*sigma;
			xMax = mean + 5*sigma;
			yMin = 0;
			yMax = 1.2* ((GeoFunction)densityCurve).evaluate(mean);	
			break;

		case DIST_STUDENT:
			xMin = -5;
			xMax = 5;
			yMin = 0;
			yMax = 1.2* ((GeoFunction)densityCurve).evaluate(0);	
			break;

		case DIST_CHISQUARE:
			k = parms[0];		
			xMin = 0;
			xMax = 4*k;
			yMin = 0;
			if(k>2)
				yMax = 1.2* ((GeoFunction)densityCurve).evaluate(k-2);	
			else
				yMax = 1.2* ((GeoFunction)densityCurve).evaluate(0);	
			break;

		case DIST_F:
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

		case DIST_CAUCHY:
			median = parms[0];
			scale = parms[1];	
			// TODO --- better estimates
			xMin = median - 6*scale;
			xMax = median + 6*scale;
			yMin = 0;
			yMax = 1.2* (1/(Math.PI*scale)); // Cauchy amplitude = 1/(pi*scale)

			break;


		case DIST_EXPONENTIAL:
			double lambda = parms[0];		
			xMin = 0;
			xMax = 4*(1/lambda);   // st dev = 1/lambda	
			yMin = 0;
			yMax = 1.2* lambda;   	
			break;


		case DIST_GAMMA:
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


		case DIST_WEIBULL:
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

		case DIST_LOGNORMAL:
			mean = parms[0];
			sigma = parms[1];
			double var = (Math.exp(sigma*sigma) - 1)*Math.exp(2*mean + sigma*sigma);
			mode = Math.exp(mean - sigma*sigma);
			xMin = 0;
			xMax = mean + 5*Math.sqrt(var);
			yMin = 0;
			yMax = 1.2* ((GeoFunction)densityCurve).evaluate(mode);	
			break;

			
		case DIST_LOGISTIC:
			mean = parms[0];
			scale = parms[1];
			sd = Math.PI*scale/Math.sqrt(3);
			xMin = mean - 5*sd;
			xMax = mean + 5*sd;
			yMin = 0;
			yMax = 1.2* ((GeoFunction)densityCurve).evaluate(mean);	
			break;


		case DIST_PASCAL:
		case DIST_POISSON:
			xMin = probCalc.getDiscreteXMin();
			xMax = probCalc.getDiscreteXMax();
			yMin = 0;	
			yMax = 1.2* getDiscreteYMax(selectedDist, parms, (int)xMin, (int)xMax);
			xMin -= 1;
			
			break;
			
			
		case DIST_BINOMIAL:
		case DIST_HYPERGEOMETRIC:
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

		String[] cmd = this.getCommand();
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

		String[] inverseCmd = this.getInverseCommand();
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
		nv =  app.getKernel().getAlgebraProcessor().evaluateToNumeric(expr, false);	
		double result = nv.getDouble();

		return result;
	}



}
