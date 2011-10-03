/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Kernel.java
 *
 * Created on 30. August 2001, 20:12
 */

package geogebra.kernel;

import geogebra.GeoGebra;
import geogebra.cas.GeoGebraCAS;
import geogebra.euclidian.EuclidianConstants;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.io.MyXMLHandler;
import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.kernel.arithmetic.ExpressionNodeEvaluator;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionalNVar;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.Polynomial;
import geogebra.kernel.cas.AlgoCoefficients;
import geogebra.kernel.cas.AlgoDegree;
import geogebra.kernel.cas.AlgoDependentCasCell;
import geogebra.kernel.cas.AlgoDerivative;
import geogebra.kernel.cas.AlgoExpand;
import geogebra.kernel.cas.AlgoFactor;
import geogebra.kernel.cas.AlgoFactors;
import geogebra.kernel.cas.AlgoIntegral;
import geogebra.kernel.cas.AlgoIntegralDefinite;
import geogebra.kernel.cas.AlgoLengthCurve;
import geogebra.kernel.cas.AlgoLengthCurve2Points;
import geogebra.kernel.cas.AlgoLengthFunction;
import geogebra.kernel.cas.AlgoLengthFunction2Points;
import geogebra.kernel.cas.AlgoLimit;
import geogebra.kernel.cas.AlgoLimitAbove;
import geogebra.kernel.cas.AlgoLimitBelow;
import geogebra.kernel.cas.AlgoPartialFractions;
import geogebra.kernel.cas.AlgoPolynomialDiv;
import geogebra.kernel.cas.AlgoPolynomialMod;
import geogebra.kernel.cas.AlgoSimplify;
import geogebra.kernel.cas.AlgoSolveODECas;
import geogebra.kernel.cas.AlgoTangentCurve;
import geogebra.kernel.cas.AlgoTangentFunctionNumber;
import geogebra.kernel.cas.AlgoTangentFunctionPoint;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.kernel.discrete.AlgoConvexHull;
import geogebra.kernel.discrete.AlgoDelauneyTriangulation;
import geogebra.kernel.discrete.AlgoHull;
import geogebra.kernel.discrete.AlgoMinimumSpanningTree;
import geogebra.kernel.discrete.AlgoShortestDistance;
import geogebra.kernel.discrete.AlgoTravelingSalesman;
import geogebra.kernel.discrete.AlgoVoronoi;
import geogebra.kernel.implicit.AlgoAsymptoteImplicitPoly;
import geogebra.kernel.implicit.AlgoDependentImplicitPoly;
import geogebra.kernel.implicit.AlgoImplicitPolyFunction;
import geogebra.kernel.implicit.AlgoImplicitPolyThroughPoints;
import geogebra.kernel.implicit.AlgoIntersectImplicitpolyParametric;
import geogebra.kernel.implicit.AlgoIntersectImplicitpolys;
import geogebra.kernel.implicit.AlgoTangentImplicitpoly;
import geogebra.kernel.implicit.GeoImplicitPoly;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.optimization.ExtremumFinder;
import geogebra.kernel.parser.Parser;
import geogebra.kernel.statistics.*;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.util.MaxSizeHashMap;
import geogebra.util.ScientificFormat;
import geogebra.util.Unicode;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.TreeSet;

public class Kernel {
	
	/** CAS variable handling */	
	private static final String GGBCAS_VARIABLE_PREFIX = "ggbcasvar";
	private static final String TMP_VARIABLE_PREFIX = "ggbtmpvar";
	private static int kernelInstances = 0;
	private int kernelID;
	private String casVariablePrefix;

	/** standard precision */ 
	public final static double STANDARD_PRECISION = 1E-8;
	public final static double STANDARD_PRECISION_SQRT = 1E-4;
	
	/** minimum precision */
	public final static double MIN_PRECISION = 1E-5;
	private final static double INV_MIN_PRECISION = 1E5; 

	/** maximum reasonable precision */
	public final static double MAX_PRECISION = 1E-12;
	
	/** current working precision */
	public static double EPSILON = STANDARD_PRECISION;
	public static double EPSILON_SQRT = STANDARD_PRECISION_SQRT;

	/** maximum precision of double numbers */
	public final static double MAX_DOUBLE_PRECISION = 1E-15;
	/** reciprocal of maximum precision of double numbers */
	public final static double INV_MAX_DOUBLE_PRECISION = 1E15;	
	
	// maximum CAS results cached
	public static int GEOGEBRA_CAS_CACHE_SIZE = 500;
	
	 // style of point/vector coordinates
	/** A = (3, 2)  and 	B = (3; 90���)*/
    public static final int COORD_STYLE_DEFAULT = 0;		
    /** A(3|2)  	   and	B(3; 90���)*/
	public static final int COORD_STYLE_AUSTRIAN = 1;		
	/** A: (3, 2)   and	B: (3; 90���) */
	public static final int COORD_STYLE_FRENCH = 2;			
	private int coordStyle = 0;

	// STATIC
	final public static int ANGLE_RADIANT = 1;
	final public static int ANGLE_DEGREE = 2;
	final public static int COORD_CARTESIAN = 3;
	final public static int COORD_POLAR = 4;	 
	final public static int COORD_COMPLEX = 5;
	final public static double PI_2 = 2.0 * Math.PI;
	final public static double PI_HALF =  Math.PI / 2.0;
	final public static double SQRT_2_HALF =  Math.sqrt(2.0) / 2.0;
	final public static double PI_180 = Math.PI / 180;
	final public static double CONST_180_PI = 180 / Math.PI;
	//private static boolean KEEP_LEADING_SIGN = true;
	
	//G.Sturr 2009-10-18
	// algebra style 
	final public static int ALGEBRA_STYLE_VALUE = 0;
	final public static int ALGEBRA_STYLE_DEFINITION = 1;
	final public static int ALGEBRA_STYLE_COMMAND = 2;
	private int algebraStyle = Kernel.ALGEBRA_STYLE_VALUE;
	//end G.Sturr
	
	// print precision
	public static final int STANDARD_PRINT_DECIMALS = 2; 
	private double PRINT_PRECISION = 1E-2;
	private NumberFormat nf;
	private ScientificFormat sf;
	public boolean useSignificantFigures = false;
	
	/**
	 * Specifies whether possible line breaks are to be marked
	 * in the String representation of {@link ExpressionNode ExpressionNodes}.
	 */
	private boolean insertLineBreaks = false;

	// angle unit: degree, radians
	private int angleUnit = Kernel.ANGLE_DEGREE;
	
	// rounding hack, see format()
	private static final double ROUND_HALF_UP_FACTOR_DEFAULT = 1.0 + 1E-15;
	private double ROUND_HALF_UP_FACTOR = ROUND_HALF_UP_FACTOR_DEFAULT;
	
	// used to store info when rounding is temporarily changed
	private Stack<Boolean> useSignificantFiguresList;
	private Stack<Integer> noOfSignificantFiguresList;
	private Stack<Integer> noOfDecimalPlacesList;
	
	/* Significant figures
	 * 
	 * How to do:
	 * 
	 * private ScientificFormat sf;
	 * sf = new ScientificFormat(5, 20, false);
	 * String s = sf.format(double)
	 * 
	 * need to address:
	 * 
	 * PRINT_PRECISION 
	 * setPrintDecimals()
	 * getPrintDecimals()
	 * getMaximumFractionDigits()
	 * setMaximumFractionDigits()
	 * 
	 * how to determine whether to use nf or sf
	 */
	
	private int casPrintForm;		
	private String casPrintFormPI; // for pi
	private boolean useTempVariablePrefix;
	private boolean keepCasNumbers;
		
	// before May 23, 2005 the function acos(), asin() and atan()
	// had an angle as result. Now the result is a number.
	// this flag is used to distinguish the different behaviour
	// depending on the the age of saved construction files
	/** if true, cyclometric functions return GeoAngle, if false, they return GeoNumeric**/
	public boolean arcusFunctionCreatesAngle = false;
	
	private boolean translateCommandName = true;
	private boolean useInternalCommandNames = false;
	private boolean undoActive = false;
	private boolean notifyViewsActive = true;
	private boolean notifyConstructionProtocolViewAboutAddRemoveActive = true;
	private boolean viewReiniting = false;
	private boolean allowVisibilitySideEffects = true;

	// this flag was introduced for Copy & Paste
	private boolean saveScriptsToXML = true; 

	private boolean elementDefaultAllowed = false;
	
	// silentMode is used to create helper objects without any side effects	
	// i.e. in silentMode no labels are created and no objects are added to views
	private boolean silentMode = false;
	
	// setResolveUnkownVarsAsDummyGeos
	private boolean resolveUnkownVarsAsDummyGeos = false;
	
	private double xmin, xmax, ymin, ymax, xscale, yscale;
	// for 2nd Graphics View
	private double xmin2, xmax2, ymin2, ymax2, xscale2, yscale2;
	private boolean graphicsView2showing = false;
	
	// Views may register to be informed about 
	// changes to the Kernel
	// (add, remove, update)
	private View[] views = new View[20];
	private int viewCnt = 0;
	
	protected Construction cons;
	protected Application app;	
	protected AlgebraProcessor algProcessor;
	private EquationSolver eqnSolver;
	private RegressionMath regMath;
	private ExtremumFinder extrFinder;
	protected Parser parser;
	private GeoGebraCAS ggbCAS;
	
	// Continuity on or off, default: false since V3.0
	private boolean continuous = false;
	private MacroManager macroManager;
	
	// loading mode: true when a ggb file is being loaded. Devised for backward compatibility. 
	private boolean loadingMode = false;
	
	/** Evaluator for ExpressionNode */
	protected ExpressionNodeEvaluator expressionNodeEvaluator;
	
	
	/** 3D manager */
	private Manager3DInterface manager3D;
				
	public Kernel(Application app) {
		this();
		this.app = app;
		
		newConstruction();
		getExpressionNodeEvaluator();
		
		setManager3D(newManager3D(this));
	}
	
	public Kernel() {
		kernelInstances++;
		kernelID = kernelInstances;
		casVariablePrefix = GGBCAS_VARIABLE_PREFIX + kernelID;
		
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setGroupingUsed(false);
		
		sf = new ScientificFormat(5, 16, false);
		
		setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA);
	}
	
	/**
	 * @param kernel
	 * @return a new 3D manager
	 */
	protected Manager3DInterface newManager3D(Kernel kernel){
		return null;
	}
	
	/**
	 * sets the 3D manager
	 * @param manager
	 */
	public void setManager3D(Manager3DInterface manager){
		this.manager3D = manager;
	}
	
	/**
	 * @return the 3D manager of this
	 */
	public Manager3DInterface getManager3D(){
		return manager3D;
	}
	
	/**
	 * Returns whether the variable name "z" may be used. Note that the 3D kernel does not
	 * allow this as it uses "z" in plane equations like 3x + 2y + z = 5.
	 * @return whether z may be used as a variable name
	 */
	public boolean isZvarAllowed(){
		return true;
	}
	
	/**
	 * creates the construction cons
	 */
	protected void newConstruction(){
		cons = new Construction(this);	
	}
	
	
	/**
	 * creates a new MyXMLHandler (used for 3D)
	 * @param cons construction used in MyXMLHandler constructor
	 * @return a new MyXMLHandler
	 */
	public MyXMLHandler newMyXMLHandler(Construction cons){
		return newMyXMLHandler(this, cons);		
	}
	
	/**
	 * creates a new MyXMLHandler (used for 3D)
	 * @param kernel
	 * @param cons
	 * @return a new MyXMLHandler
	 */
	public MyXMLHandler newMyXMLHandler(Kernel kernel, Construction cons){
		return new MyXMLHandler(kernel, cons);		
	}
	
	
	/**
	 * creates the Evaluator for ExpressionNode
	 * @return the Evaluator for ExpressionNode
	 */
	protected ExpressionNodeEvaluator newExpressionNodeEvaluator(){
		return new ExpressionNodeEvaluator();
	}
	
	/** return the Evaluator for ExpressionNode
	 * @return the Evaluator for ExpressionNode
	 */
	public ExpressionNodeEvaluator getExpressionNodeEvaluator(){
		if (expressionNodeEvaluator == null)
			expressionNodeEvaluator = newExpressionNodeEvaluator();
		return expressionNodeEvaluator;
	}
	
	/**
	 * Returns this kernel's algebra processor that handles
	 * all input and commands.
	 * @return Algebra processor
	 */	
	public AlgebraProcessor getAlgebraProcessor() {
    	if (algProcessor == null)
    		algProcessor = newAlgebraProcessor(this);
    	return algProcessor;
    }
	
	/**
	 * @param kernel 
	 * @return a new algebra processor (used for 3D)
	 */
	protected AlgebraProcessor newAlgebraProcessor(Kernel kernel){
		return new AlgebraProcessor(kernel);
	}
	
	/**
     * Returns a GeoElement for the given label. 
     * @return may return null
     */
	final public GeoElement lookupLabel(String label) {		
		return lookupLabel(label, false);
	}
	
	/**
     * Returns a GeoCasCell for the given label. 
     * @return may return null
     */
	final public GeoCasCell lookupCasCellLabel(String label) {		
		return cons.lookupCasCellLabel(label);
	}
	
	/**
     * Returns a GeoCasCell for the given cas row. 
     * @return may return null
     */
	final public GeoCasCell lookupCasRowReference(String label) {		
		return cons.lookupCasRowReference(label);
	}
	
	/**
	 * Finds element with given the label and possibly creates it
	 * @param label Label of element we are looking for
	 * @param autoCreate true iff new geo should be created if missing
	 * @return GeoElement with given label
	 */
	final public GeoElement lookupLabel(String label, boolean autoCreate) {	
		GeoElement geo = cons.lookupLabel(label, autoCreate);
				
		if (geo == null && isResolveUnkownVarsAsDummyGeos()) {
			// lookup CAS variables too
			geo = lookupCasCellLabel(label);
			
			// resolve unknown variable as dummy geo to keep its name and 
			// avoid an "unknown variable" error message
			if (geo == null)
				geo = new GeoDummyVariable(cons, label);
		}
		
		return geo;
	}
	
	/**
	 * returns GeoElement at (row,col) in spreadsheet
	 * may return nully 
	 * @param col Spreadsheet column
	 * @param row Spreadsheet row
	 * @return Spreadsheet cell content (may be null)
	 */
	public GeoElement getGeoAt(int col, int row) {
		return lookupLabel(GeoElement.getSpreadsheetCellName(col, row));
	}
	
	final public GeoAxis getXAxis() {
		return cons.getXAxis();
	}
	
	final public GeoAxis getYAxis() {
		return cons.getYAxis();
	}
	
	final public boolean isAxis(GeoElement geo) {
		return (geo == cons.getXAxis() || geo == cons.getYAxis());
	}
	
    public void updateLocalAxesNames() {
    	cons.updateLocalAxesNames();
    }
	
	final public Application getApplication() {
		return app;
	}		
	
	public void setShowOnlyBreakpoints(boolean flag) {
		 cons.setShowOnlyBreakpoints(flag);
	}
	
	final public boolean showOnlyBreakpoints() {
		return cons.showOnlyBreakpoints();
	}
	
	final public EquationSolver getEquationSolver() {
		if (eqnSolver == null)
			eqnSolver = new EquationSolver(this);
		return eqnSolver;
	}
	
	final public ExtremumFinder getExtremumFinder() {
		if (extrFinder == null)
			extrFinder = new ExtremumFinder();
		return extrFinder;
	}
	
	final public RegressionMath getRegressionMath() {
		if (regMath == null)
			regMath = new RegressionMath();
		return regMath;
	}
	
	final public Parser getParser() {
    	if (parser == null)
    		parser = new Parser(this, cons);
    	return parser;
    }	
			
	/** 
	 * Evaluates an expression in GeoGebraCAS syntax.
     * @return result string (null possible)
	 * @throws Throwable 
     */
	final public String evaluateGeoGebraCAS(String exp) throws Throwable {		
		return evaluateGeoGebraCAS(exp, false);
	}	
	
	/** 
	 * Evaluates an expression in GeoGebraCAS syntax where the cache or previous
	 * evaluations is used. Make sure to only use this method when exp only includes 
	 * values and no (used) variable names.
     * @return result string (null possible)
	 * @throws Throwable 
     */
	final public String evaluateCachedGeoGebraCAS(String exp) throws Throwable {		
		return evaluateGeoGebraCAS(exp, true);
	}	
	
	/** 
	 * Evaluates an expression in GeoGebraCAS syntax with.
     * @param useCaching only set to true when exp only includes values and no (used) variable names
     * @return result string (null possible)
	 * @throws Throwable 
     */
	private String evaluateGeoGebraCAS(String exp, boolean useCaching) throws Throwable {
		String result = null;
		if (useCaching && hasCasCache()) {
			result = getCasCache().get(exp);
			if (result != null) {
				// caching worked
				// TODO: remove
				System.out.println("used ggbCasCache: " + exp + " -> " + result);
				return result;
			}
		}
		
		// evaluate in GeoGebraCAS
		result = getGeoGebraCAS().evaluateGeoGebraCAS(exp);
		
		if (useCaching) {
			getCasCache().put(exp, result);
		}
		return result;
	}	
	private MaxSizeHashMap<String, String> ggbCasCache;
	
	/**
	 * @return Hash map for caching CAS results.
	 */
	protected MaxSizeHashMap<String, String> getCasCache() {
		if (ggbCasCache == null)
			ggbCasCache = new MaxSizeHashMap<String, String>(GEOGEBRA_CAS_CACHE_SIZE);
		return ggbCasCache;
	}
	
	/**
	 * @return Whether kernel is already using CAS caching.
	 */
	protected boolean hasCasCache() {
		return ggbCasCache != null;
	}
	
	/** 
	 * Evaluates an expression in MathPiper syntax with.
     * @return result string (null possible)
	 * @throws Throwable 
     *
	final public String evaluateMathPiper(String exp) {
		if (ggbCAS == null) {
			getGeoGebraCAS();		
		}
		
		return ggbCAS.evaluateMathPiper(exp);
	}	*/
	
	/** 
	 * Evaluates an expression in Maxima syntax with.
     * @return result string (null possible)
	 * @throws Throwable 
     *
	final public String evaluateMaxima(String exp) {
		if (ggbCAS == null) {
			getGeoGebraCAS();		
		}
		
		return ggbCAS.evaluateMaxima(exp);
	}*/	
			
	/**
	 * @return Whether the GeoGebraCAS has been initialized before
	 */
	public synchronized boolean isGeoGebraCASready() {
		return ggbCAS != null;
	}
	
	public static int DEFAULT_CAS = Application.CAS_MPREDUCE; // default

	/**
	 * Sets currently used underlying CAS, e.g. MPReduce or Maxima.
	 * @param casID Application.CAS_MPREDUCE or CAS_MPREDUCE.CAS_Maxima
	 */
	public void setDefaultCAS(int casID) {
		DEFAULT_CAS = casID;
		if (ggbCAS != null) ggbCAS.setCurrentCAS(DEFAULT_CAS);
	}
	
	/**
	 * Returns this kernel's GeoGebraCAS object.
	 */
	public synchronized GeoGebraCASInterface getGeoGebraCAS() {
		if (ggbCAS == null) {
			ggbCAS = new geogebra.cas.GeoGebraCAS(this);
			app.enableCAS();
			getAlgebraProcessor().enableCAS();
		}			
		
		return ggbCAS;
	}
	
	/**
	 * Resets the GeoGebraCAS and clears all variables.
	 */
	public void resetGeoGebraCAS() {
		if (ggbCAS == null) return;
		
		// do NOT reset CAS because we are using one static CAS for all applicatin windows
		// see http://www.geogebra.org/trac/ticket/1415
		// instead we clear variable names of this kernel individually below			
		//ggbCAS.reset();

		// CAS reset may not clear user variables right now, 
		// see http://www.geogebra.org/trac/ticket/1249 
		// so we clear all user variable names individually from the CAS
		for (GeoElement geo : cons.getGeoSetWithCasCellsConstructionOrder()) {
			geo.unbindVariableInCAS();			
		}
	}
	
	/**
	 * Removes the given variableName from ther underlying CAS.
	 */
	public void unbindVariableInGeoGebraCAS(String variableName) {
		if (ggbCAS != null) {
			ggbCAS.unbindVariable(addCASVariablePrefix(variableName));
		}
	}
	
	/**
     * Finds the polynomial coefficients of
     * the given expression and returns it in ascending order. 
     * If exp is not a polynomial null is returned.
     * 
     * @param exp expression in MPreduce syntax, e.g. "3*a*x^2 + b*x"
     * @param variable, e.g "x"
     * @return array of coefficients, e.g. ["0", "b", "3*a"]
     */
    final public String [] getPolynomialCoeffs(String exp, String variable) {
    	return getGeoGebraCAS().getPolynomialCoeffs(exp, variable);
    }

	final public void setEpsilon(double epsilon) {
		EPSILON = epsilon;
	
		if (EPSILON > MIN_PRECISION)
			EPSILON = MIN_PRECISION;
		else if (EPSILON < MAX_PRECISION)
			EPSILON = MAX_PRECISION;
		
	}
	
	/**
	 * Sets the working epsilon precision according to the given 
	 * print precision. After this method epsilon will always be
	 * less or equal STANDARD_PRECISION.
	 * @param printPrecision
	 */
	private void setEpsilonForPrintPrecision(double printPrecision) {
		if (printPrecision < STANDARD_PRECISION) {
			setEpsilon(printPrecision);
		} else {
			setEpsilon(STANDARD_PRECISION);
		}
	}

	final public double getEpsilon() {
		return EPSILON;
	}

	final public void setMinPrecision() {
		setEpsilon(MIN_PRECISION);
	}

	final public void resetPrecision() {
		setEpsilon(STANDARD_PRECISION);
	}
	
	/**
	 * Returns whether MySpecialDouble objects should keep numbers literally or not. 
	 * @param keepCasNumbers true = keep literal CAS numbers, false = use kernel number formatting
	 */
	public boolean isKeepCasNumbers() {
		return keepCasNumbers;
	}

	/**
	 * Tells MySpecialDouble objects to keep numbers literally. 
	 * @param keepCasNumbers true = keep literal CAS numbers, false = use kernel number formatting
	 */
	public void setKeepCasNumbers(boolean keepCasNumbers) {
		this.keepCasNumbers = keepCasNumbers;
	}
	
	/**
	 * Tells this kernel about the bounds and the scales for x-Axis and y-Axis used
	 * in EudlidianView. The scale is the number of pixels per unit.
	 * (useful for some algorithms like findminimum). All 
	 */
	final public void setEuclidianViewBounds(int view, double xmin, double xmax, 
			double ymin, double ymax, double xscale, double yscale) {
		
		switch (view) {
			case 1:
				this.xmin = xmin;
				this.xmax = xmax;
				this.ymin = ymin;
				this.ymax = ymax;
				this.xscale = xscale;
				this.yscale = yscale;	
				break;
				
			case 2:
				this.xmin2 = xmin;
				this.xmax2 = xmax;
				this.ymin2 = ymin;
				this.ymax2 = ymax;
				this.xscale2 = xscale;
				this.yscale2 = yscale;	
				break;
		}

		graphicsView2showing = app.isShowingEuclidianView2();
		notifyEuclidianViewCE();
	}	
	
	private void notifyEuclidianViewCE() {
		if (macroManager != null) 
			macroManager.notifyEuclidianViewCE();
		
		cons.notifyEuclidianViewCE();
	}
	
	double getXmax() {
		if (graphicsView2showing)
			return Math.max(xmax, xmax2);
		else 
			return xmax;
	}
	
	double getXmin() {
		if (graphicsView2showing)
			return Math.min(xmin, xmin2);
		else
			return xmin;
	}
	
	double getXscale() {
		if (graphicsView2showing) {
			// xscale = pixel per unit
			// higher xscale means more pixels per unit, i.e. higher precision
			return Math.max(xscale, xscale2);
		}
		else {
			return xscale;
		}
			
	}
	
	double getYmax() {
		if (graphicsView2showing)
			return Math.max(ymax, ymax2);
		else
			return ymax;
	}
	
	double getYmin() {
		if (graphicsView2showing)
			return Math.min(ymin, ymin2);
		else
			return ymin;
	}
	
	double getYscale() {
		if (graphicsView2showing)
			// yscale = pixel per unit
			// higher xscale means more pixels per unit, i.e. higher precision
			return Math.max(yscale, yscale2);
		else
			return yscale;
	}
	
	/**
	 * @param geo
	 * @return RealWorld Coordinates of the rectangle covering all euclidian views
	 *  in which <b>geo</b> is shown.<br /> Format: {xMin,xMax,yMin,yMax,xScale,yScale}
	 */
	public double[] getViewBoundsForGeo(GeoElement geo){
		List<Integer> viewSet=geo.getViewSet();
		double[] viewBounds=new double[6];
		for (int i=0;i<6;i++)
			viewBounds[i]=Double.NEGATIVE_INFINITY;
		viewBounds[0]=viewBounds[2]=Double.POSITIVE_INFINITY;
		for(int id:viewSet){
			View view=app.getView(id);
			if (view!=null&&view instanceof EuclidianViewInterface){
				EuclidianViewInterface ev=(EuclidianViewInterface)view;
				viewBounds[0]=Math.min(viewBounds[0],ev.getXmin());
				viewBounds[1]=Math.max(viewBounds[1],ev.getXmax());
				viewBounds[2]=Math.min(viewBounds[2],ev.getYmin());
				viewBounds[3]=Math.max(viewBounds[3],ev.getYmax());
				viewBounds[4]=Math.max(viewBounds[4],ev.getXscale());
				viewBounds[5]=Math.max(viewBounds[5],ev.getYscale());
			}
		}
//		if (viewBounds[0]==Double.POSITIVE_INFINITY){
//			//standard values if no view
//			viewBounds[0]=viewBounds[2]=-10;
//			viewBounds[1]=viewBounds[3]=10;
//			viewBounds[5]=viewBounds[6]=1;
//		}
		return viewBounds;
	}
	
	/**
	 * 
	 * {@linkplain #getViewBoundsForGeo(GeoElement)}
	 * @see #getViewBoundsForGeo(GeoElement)
	 * @param geo
	 * @return
	 */
	public double getViewsXMin(GeoElement geo){
		return getViewBoundsForGeo(geo)[0];
	}
	
	public double getViewsXMax(GeoElement geo){
		return getViewBoundsForGeo(geo)[1];
	}
	
	public double getViewsYMin(GeoElement geo){
		return getViewBoundsForGeo(geo)[2];
	}
	
	public double getViewsYMax(GeoElement geo){
		return getViewBoundsForGeo(geo)[3];
	}
	
	public double getViewsXScale(GeoElement geo){
		return getViewBoundsForGeo(geo)[4];
	}
	
	public double getViewsYScale(GeoElement geo){
		return getViewBoundsForGeo(geo)[5];
	}
	
	
	/**
	 * Registers an algorithm that needs to be updated when notifyRename(),
	 * notifyAdd(), or notifyRemove() is called.	 
	 */
	void registerRenameListenerAlgo(AlgoElement algo) {
		if (renameListenerAlgos == null) {
			renameListenerAlgos = new ArrayList();
		}
		
		if (!renameListenerAlgos.contains(algo))
			renameListenerAlgos.add(algo);
	}
	
	void unregisterRenameListenerAlgo(AlgoElement algo) {
		if (renameListenerAlgos != null) 
			renameListenerAlgos.remove(algo);
	}
	private ArrayList renameListenerAlgos;
	
	private void notifyRenameListenerAlgos() {
		AlgoElement.updateCascadeAlgos(renameListenerAlgos);
	}	
	
		
	//G.Sturr 2009-10-18
	final public void setAlgebraStyle(int style) {
		algebraStyle = style;
	}

	final public int getAlgebraStyle() {
		return algebraStyle;
	}
	//end G.Sturr
	
	
	final public void setAngleUnit(int unit) {
		angleUnit = unit;
	}

	final public int getAngleUnit() {
		return angleUnit;
	}
	
	final public String getPiString() {
		return casPrintFormPI;
	}
	
	final public void setCASPrintForm(int type) {
		casPrintForm = type;
		
		switch (casPrintForm) {
		case ExpressionNode.STRING_TYPE_MATH_PIPER:
			casPrintFormPI = "Pi";
			break;
			
		case ExpressionNode.STRING_TYPE_MAXIMA:
			casPrintFormPI = "%pi";
			break;
			
		case ExpressionNode.STRING_TYPE_JASYMCA:
		case ExpressionNode.STRING_TYPE_GEOGEBRA_XML:
			casPrintFormPI = "pi";
			break;
				
		case ExpressionNode.STRING_TYPE_MPREDUCE:
			casPrintFormPI = "pi";
			break;
		
		case ExpressionNode.STRING_TYPE_LATEX:
			casPrintFormPI = "\\pi";
			break;
			
			default:
				casPrintFormPI = Unicode.PI_STRING;
		}
	}
	
	final public int getCASPrintForm() {
		return casPrintForm;
	}
	
	/**
	 * Returns whether all variable names are currently prefixed 
	 * by ExpressionNode.TMP_VARIABLE_PREFIX, i.e. "a" becomes "ggbtmpvara"
	 */
	public boolean isUseTempVariablePrefix() {
		return useTempVariablePrefix;
	}

	/**
	 * Sets whether all variable names should be prefixed 
	 * by ExpressionNode.TMP_VARIABLE_PREFIX, i.e. "a" becomes "ggbtmpvara"
	 */
	public void setUseTempVariablePrefix(boolean useTempVariablePrefix) {
		this.useTempVariablePrefix = useTempVariablePrefix;
	}
	
	/**
	 * Retuns variable label depending on isUseTempVariablePrefix() and 
	 * kernel.getCASPrintForm(). A label may be prefixed here by 
	 * ExpressionNode.TMP_VARIABLE_PREFIX or 
	 * @param label
	 * @return
	 */
	public String printVariableName(String label) {
		if (isUseTempVariablePrefix()) {
			return addTempVariablePrefix(label);
		} else {
			return printVariableName(getCASPrintForm(), label);
		}
	}
	
	/**
	 * Returns the label depending on the current print form. When sending variables
	 * to the underlying CAS, we need to make sure that we don't overwrite variable names there,
	 * so we add the prefix ExpressionNodeConstants.GGBCAS_VARIABLE_PREFIX.
	 * 
	 * @param printForm 
	 * @return label depending on kernel.getCASPrintForm()
	 */
	 final public String printVariableName(int printForm, String label) {
		 switch(printForm){		
			case ExpressionNodeConstants.STRING_TYPE_MPREDUCE:
			case ExpressionNodeConstants.STRING_TYPE_MAXIMA:
				// make sure we don't interfer with reserved names
				// or command names in the underlying CAS
				// see http://www.geogebra.org/trac/ticket/1051
				return addCASVariablePrefix(label);
				
			default:
				//standard case
				return label;
		 } 
	 }
	 
	 /**
	  * @return The variable prefix used when variables are sent to the CAS,
	  * e.g. "ggbcasvar1"
	  */
	public final String getCasVariablePrefix() {
		return casVariablePrefix;
	}
	 
	/**
	 * Returns ExpressionNodeConstants.TMP_VARIABLE_PREFIX + label.
	 */
	 private static String addTempVariablePrefix(String label) {
		 StringBuilder sb = new StringBuilder();
		// TMP_VARIABLE_PREFIX  + label
		 sb.append(TMP_VARIABLE_PREFIX);
		 sb.append(label);
		 return sb.toString();		
	 }
	 
	 /**
	 * @return ExpressionNodeConstants.GGBCAS_VARIABLE_PREFIX + kernelID + label.
	 */
	 private String addCASVariablePrefix(String label) {
		 if (label.startsWith(TMP_VARIABLE_PREFIX))
			 return label;
		 else {
			 // casVariablePrefix  + label
			 StringBuilder sb = new StringBuilder();
			 sb.append(casVariablePrefix); // GGBCAS_VARIABLE_PREFIX + kernelID
			 sb.append(label);
			 return sb.toString();	
		 }			
	 }
	 
	 /**
	 * @return String where CAS variable prefixes are removed again, e.g. "ggbcasvar1a" is turned into "a"
	 * and 
	 */
	 final public String removeCASVariablePrefix(String str) {
		// e.g. "ggbtmpvar1a" is changed to "a"
		String result = str.replace(casVariablePrefix, "");
				 
		// e.g. "ggbtmpvara" needs to be changed to "a"
		result = result.replace(TMP_VARIABLE_PREFIX, "");
		return result;
	 }
	 	
	final public int getCurrentCAS() {
		return ((GeoGebraCAS)getGeoGebraCAS()).currentCAS;
	}

	final public void setPrintDecimals(int decimals) {
		if (decimals >= 0) {
			useSignificantFigures = false;
			nf.setMaximumFractionDigits(decimals);
			ROUND_HALF_UP_FACTOR = decimals < 15 ? ROUND_HALF_UP_FACTOR_DEFAULT : 1;
			
			PRINT_PRECISION = Math.pow(10, -decimals);
			setEpsilonForPrintPrecision(PRINT_PRECISION);
			
			// tell CAS to use significant figures:
			// sigFig = min(2*decimal places, 20)
			if (ggbCAS != null) {
				ggbCAS.setSignificantFiguresForNumeric(Math.min(20, 2*decimals));
			}
		}
	}
	
	final public int getPrintDecimals() {
		return nf.getMaximumFractionDigits();
	}
		
	final public void setPrintFigures(int figures) {
		if (figures >= 0) {
			useSignificantFigures = true;
			sf.setSigDigits(figures);
			sf.setMaxWidth(16); // for scientific notation
			ROUND_HALF_UP_FACTOR = figures < 15 ? ROUND_HALF_UP_FACTOR_DEFAULT : 1;
			
			PRINT_PRECISION = MAX_PRECISION;
			setEpsilonForPrintPrecision(PRINT_PRECISION);
			
			// tell CAS to use significant figures for Numeric
			if (ggbCAS != null) {
				ggbCAS.setSignificantFiguresForNumeric(figures);
			}
		}
	}
	
	/**
	 * Sets the print accuracy to at least the given decimals
	 * or significant figures. If the current accuracy is already higher, nothing is changed.
	 * 
	 * @param decimalsOrFigures
	 * @return whether the print accuracy was changed
	 */
	public boolean ensureTemporaryPrintAccuracy(int decimalsOrFigures) {
		if (useSignificantFigures) {
			if (sf.getSigDigits() < decimalsOrFigures) {
				setTemporaryPrintFigures(decimalsOrFigures);
				return true;
			}
		} else {
			// decimals
			if (nf.getMaximumFractionDigits() < decimalsOrFigures) {
				setTemporaryPrintDecimals(decimalsOrFigures);
				return true;
			}
		}
		return false;
	}	
	
	final public void setTemporaryPrintFigures(int figures) {
		storeTemporaryRoundingInfoInList();				
		setPrintFigures(figures);
	}
	
	final public void setTemporaryPrintDecimals(int decimals) {		
		storeTemporaryRoundingInfoInList();		
		setPrintDecimals(decimals);
	}			

	/*
	 * stores information about the current no of decimal places/sig figures used
	 * for when it is (temporarily changed)
	 * needs to be in a list as it can be nested
	 */
	private void storeTemporaryRoundingInfoInList()
	{
		if (useSignificantFiguresList == null) {
			useSignificantFiguresList = new Stack<Boolean>();
			noOfSignificantFiguresList = new Stack<Integer>();
			noOfDecimalPlacesList = new Stack<Integer>();
		}
				
		useSignificantFiguresList.push(Boolean.valueOf(useSignificantFigures));
		noOfSignificantFiguresList.push(new Integer(sf.getSigDigits()));	
		noOfDecimalPlacesList.push(Integer.valueOf(nf.getMaximumFractionDigits()));	
	}
	
	/**
	 *  gets previous values of print acuracy from stacks
	 */
	final public void restorePrintAccuracy()
	{		
		useSignificantFigures = useSignificantFiguresList.pop().booleanValue();		
		int sigFigures = noOfSignificantFiguresList.pop().intValue();
		int decDigits = noOfDecimalPlacesList.pop().intValue();
		
		if (useSignificantFigures)
			setPrintFigures(sigFigures);
		else
			setPrintDecimals(decDigits);	
		
		//Application.debug("list size"+noOfSignificantFiguresList.size());
	}
	
	/*
	 * returns number of significant digits, or -1 if using decimal places
	 */
	final public int getPrintFigures() {
		if (!useSignificantFigures) return -1;
		return sf.getSigDigits();
	}
		
	/**
	 * returns 10^(-PrintDecimals)
	 *
	final public double getPrintPrecision() {
		return PRINT_PRECISION;
	} */
	
	final public int getCoordStyle() {
		return coordStyle;
	}
	public void setCoordStyle(int coordStlye) {
		coordStyle = coordStlye;		
	}

	public void setSaveScriptsToXML(boolean flag) {
		saveScriptsToXML = flag;
	}

	public boolean getSaveScriptsToXML() {
		return saveScriptsToXML;
	}
	
	public void setElementDefaultAllowed(boolean flag) {
		elementDefaultAllowed = flag;
	}
	
	public boolean getElementDefaultAllowed() {
		return elementDefaultAllowed;
	}

	/*
	 * GeoElement specific
	 */
	
	
	public int getClassType(String type) throws MyError {   
    	switch (type.charAt(0)) {
		case 'a': //angle    			
			return GeoElement.GEO_CLASS_ANGLE;	    			     		    			
			
		case 'b': //angle
			if (type.equals("boolean"))
				return GeoElement.GEO_CLASS_BOOLEAN;
			else
    			return GeoElement.GEO_CLASS_BUTTON; // "button"
		
		case 'c': // conic
			if (type.equals("conic"))
				return GeoElement.GEO_CLASS_CONIC;   
			else if (type.equals("conicpart"))    					
				return GeoElement.GEO_CLASS_CONICPART;
			else if (type.equals("circle")) { // bug in GeoGebra 2.6c
				return GeoElement.GEO_CLASS_CONIC;
			}
			
		case 'd': // doubleLine 			// bug in GeoGebra 2.6c
			return GeoElement.GEO_CLASS_CONIC;   			
			
		case 'e': // ellipse, emptyset	//  bug in GeoGebra 2.6c
			return GeoElement.GEO_CLASS_CONIC;    			
				
		case 'f': // function
			return GeoElement.GEO_CLASS_FUNCTION;
		
		case 'h': // hyperbola			//  bug in GeoGebra 2.6c
			return GeoElement.GEO_CLASS_CONIC;   			
			
		case 'i': // image,implicitpoly
			if (type.equals("image"))    				
				return GeoElement.GEO_CLASS_IMAGE;
			else if (type.equals("intersectinglines")) //  bug in GeoGebra 2.6c
				return GeoElement.GEO_CLASS_CONIC;
			else if (type.equals("implicitpoly"))
				return GeoElement.GEO_CLASS_IMPLICIT_POLY;
		
		case 'l': // line, list, locus
			if (type.equals("line"))
				return GeoElement.GEO_CLASS_LINE;
			else if (type.equals("list"))
				return GeoElement.GEO_CLASS_LIST;    					
			else 
				return GeoElement.GEO_CLASS_LOCUS;
		
		case 'n': // numeric
			return GeoElement.GEO_CLASS_NUMERIC;
			
		case 'p': // point, polygon
			if (type.equals("point"))
				return GeoElement.GEO_CLASS_POINT;
			else if (type.equals("polygon"))
				return GeoElement.GEO_CLASS_POLYGON;
			else if (type.equals("polyline"))
				return GeoElement.GEO_CLASS_POLYLINE;
			else // parabola, parallelLines, point //  bug in GeoGebra 2.6c
				return GeoElement.GEO_CLASS_CONIC;
			
		case 'r': // ray
			return GeoElement.GEO_CLASS_RAY;
			
		case 's': // segment    			
			return GeoElement.GEO_CLASS_SEGMENT;	    			    			
			
		case 't': 
			if (type.equals("text"))
				return GeoElement.GEO_CLASS_TEXT; // text
			else
    			return GeoElement.GEO_CLASS_TEXTFIELD; // textfield
			
		case 'v': // vector
			return GeoElement.GEO_CLASS_VECTOR;
		
		default:    			
			throw new MyError(cons.getApplication(), "Kernel: GeoElement of type "
		            + type + " could not be created.");		    		
	}  
	}
	
	
	/**
     * Creates a new GeoElement object for the given type string.
     * @param type: String as produced by GeoElement.getXMLtypeString()
     */
    public GeoElement createGeoElement(Construction cons, String type) throws MyError {    	
    	// the type strings are the classnames in lowercase without the beginning "geo"
    	// due to a bug in GeoGebra 2.6c the type strings for conics
        // in XML may be "ellipse", "hyperbola", ...  
    	    	
    	switch (type.charAt(0)) {
    		case 'a': //angle    			
    			return new GeoAngle(cons);	    			     		    			
    			
    		case 'b': //angle
    			if (type.equals("boolean"))
    				return new GeoBoolean(cons);
    			else
        			return new GeoButton(cons); // "button"
    		
    		case 'c': // conic
    			if (type.equals("conic"))
    				return new GeoConic(cons);   
    			else if (type.equals("conicpart"))    					
    				return new GeoConicPart(cons, 0);
    			else if (type.equals("circle")) { // bug in GeoGebra 2.6c
    				return new GeoConic(cons);
    			}
    			
    		case 'd': // doubleLine 			// bug in GeoGebra 2.6c
    			return new GeoConic(cons);    			
    			
    		case 'e': // ellipse, emptyset	//  bug in GeoGebra 2.6c
				return new GeoConic(cons);     			
    				
    		case 'f': // function
    			return new GeoFunction(cons);
    		
    		case 'h': // hyperbola			//  bug in GeoGebra 2.6c
				return new GeoConic(cons);     			
    			
    		case 'i': // image,implicitpoly
    			if (type.equals("image"))    				
    				return new GeoImage(cons);
    			else if (type.equals("intersectinglines")) //  bug in GeoGebra 2.6c
    				return new GeoConic(cons);
    			else if (type.equals("implicitpoly"))
    				return new GeoImplicitPoly(cons);
    		
    		case 'l': // line, list, locus
    			if (type.equals("line"))
    				return new GeoLine(cons);
    			else if (type.equals("list"))
    				return new GeoList(cons);    					
    			else 
    				return new GeoLocus(cons);
    		
    		case 'n': // numeric
    			return new GeoNumeric(cons);
    			
    		case 'p': // point, polygon
    			if (type.equals("point"))
    				return new GeoPoint(cons);
    			else if (type.equals("polygon"))
    				return new GeoPolygon(cons, null);
    			else if (type.equals("polyline"))
    				return new GeoPolyLine(cons, null);
    			else // parabola, parallelLines, point //  bug in GeoGebra 2.6c
    				return new GeoConic(cons);
    			
    		case 'r': // ray
    			return new GeoRay(cons, null);
    			
    		case 's': // segment    			
    			return new GeoSegment(cons, null, null);	    			    			
    			
    		case 't': 
    			if (type.equals("text"))
    				return new GeoText(cons); // text
    			else
        			return new GeoTextField(cons); // textfield
   			
    		case 'v': // vector
				return new GeoVector(cons);
    		
    		default:    			
    			throw new MyError(cons.getApplication(), "Kernel: GeoElement of type "
    		            + type + " could not be created.");		    		
    	}    		    
    }  
    
    
  
    
    
    
	/* *******************************************
	 *  Methods for EuclidianView/EuclidianView3D
	 * ********************************************/
    

	public String getModeText(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_SELECTION_LISTENER:
			return "Select";

		case EuclidianConstants.MODE_MOVE:
			return "Move";

		case EuclidianConstants.MODE_POINT:
			return "Point";
			
		case EuclidianConstants.MODE_COMPLEX_NUMBER:
			return "ComplexNumber";
			
		case EuclidianConstants.MODE_POINT_ON_OBJECT:
			return "PointOnObject";
			
		case EuclidianConstants.MODE_JOIN:
			return "Join";

		case EuclidianConstants.MODE_SEGMENT:
			return "Segment";

		case EuclidianConstants.MODE_SEGMENT_FIXED:
			return "SegmentFixed";

		case EuclidianConstants.MODE_RAY:
			return "Ray";

		case EuclidianConstants.MODE_POLYGON:
			return "Polygon";

		case EuclidianConstants.MODE_POLYLINE:
			return "PolyLine";

		case EuclidianConstants.MODE_RIGID_POLYGON:
			return "RigidPolygon";

		case EuclidianConstants.MODE_VECTOR_POLYGON:
			return "VectorPolygon";

		case EuclidianConstants.MODE_PARALLEL:
			return "Parallel";

		case EuclidianConstants.MODE_ORTHOGONAL:
			return "Orthogonal";

		case EuclidianConstants.MODE_INTERSECT:
			return "Intersect";

		case EuclidianConstants.MODE_INTERSECTION_CURVE:
			return "IntersectionCurve";
				
		case EuclidianConstants.MODE_LINE_BISECTOR:
			return "LineBisector";

		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			return "AngularBisector";

		case EuclidianConstants.MODE_TANGENTS:
			return "Tangent";

		case EuclidianConstants.MODE_POLAR_DIAMETER:
			return "PolarDiameter";

		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
			return "Circle2";

		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			return "Circle3";

		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
			return "Ellipse3";

		case EuclidianConstants.MODE_PARABOLA:
			return "Parabola";

		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			return "Hyperbola3";

		// Michael Borcherds 2008-03-13
		case EuclidianConstants.MODE_COMPASSES:
			return "Compasses";

		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			return "Conic5";

		case EuclidianConstants.MODE_RELATION:
			return "Relation";

		case EuclidianConstants.MODE_TRANSLATEVIEW:
			return "TranslateView";

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			return "ShowHideObject";

		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			return "ShowHideLabel";

		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			return "CopyVisualStyle";

		case EuclidianConstants.MODE_DELETE:
			return "Delete";

		case EuclidianConstants.MODE_VECTOR:
			return "Vector";

		case EuclidianConstants.MODE_TEXT:
			return "Text";

		case EuclidianConstants.MODE_IMAGE:
			return "Image";

		case EuclidianConstants.MODE_MIDPOINT:
			return "Midpoint";

		case EuclidianConstants.MODE_SEMICIRCLE:
			return "Semicircle";

		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			return "CircleArc3";

		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			return "CircleSector3";

		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			return "CircumcircleArc3";

		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			return "CircumcircleSector3";

		case EuclidianConstants.MODE_SLIDER:
			return "Slider";

		case EuclidianConstants.MODE_MIRROR_AT_POINT:
			return "MirrorAtPoint";

		case EuclidianConstants.MODE_MIRROR_AT_LINE:
			return "MirrorAtLine";

		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
			return "MirrorAtCircle";

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return "TranslateByVector";

		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			return "RotateByAngle";

		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			return "DilateFromPoint";

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
			return "CirclePointRadius";

		case EuclidianConstants.MODE_ANGLE:
			return "Angle";

		case EuclidianConstants.MODE_ANGLE_FIXED:
			return "AngleFixed";

		case EuclidianConstants.MODE_VECTOR_FROM_POINT:
			return "VectorFromPoint";

		case EuclidianConstants.MODE_DISTANCE:
			return "Distance";				

		case EuclidianConstants.MODE_MOVE_ROTATE:
			return "MoveRotate";

		case EuclidianConstants.MODE_ZOOM_IN:
			return "ZoomIn";

		case EuclidianConstants.MODE_ZOOM_OUT:
			return "ZoomOut";

		case EuclidianConstants.MODE_LOCUS:
			return "Locus";
			
		case EuclidianConstants.MODE_AREA:
			return "Area";
			
		case EuclidianConstants.MODE_SLOPE:
			return "Slope";
			
		case EuclidianConstants.MODE_REGULAR_POLYGON:
			return "RegularPolygon";
			
		case EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX:
			return "ShowCheckBox";
			
		case EuclidianConstants.MODE_BUTTON_ACTION:
			return "ButtonAction";
			
		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			return "TextFieldAction";
			
		case EuclidianConstants.MODE_PEN:
			return "Pen";
			
		case EuclidianConstants.MODE_VISUAL_STYLE:
			return "VisualStyle";
			
		case EuclidianConstants.MODE_FITLINE:
			return "FitLine";

		case EuclidianConstants.MODE_CREATE_LIST:
			return "CreateListGraphicsView";

		case EuclidianConstants.MODE_RECORD_TO_SPREADSHEET:
			return "RecordToSpreadsheet";
			
		case EuclidianConstants.MODE_PROBABILITY_CALCULATOR:
			return "ProbabilityCalculator";
		
		case EuclidianConstants.MODE_FUNCTION_INSPECTOR:
			return "FunctionInspector";
												
			
			
		// CAS	
		case EuclidianConstants.MODE_CAS_EVALUATE:
			return "Evaluate";
			
		case EuclidianConstants.MODE_CAS_NUMERIC:
			return "Numeric";
		
		case EuclidianConstants.MODE_CAS_KEEP_INPUT:
			return "KeepInput";
			
		case EuclidianConstants.MODE_CAS_EXPAND:
			return "Expand";
			
		case EuclidianConstants.MODE_CAS_FACTOR:
			return "Factor";		
					
		case EuclidianConstants.MODE_CAS_SUBSTITUTE:
			return "Substitute";		
						
		case EuclidianConstants.MODE_CAS_SOLVE:
			return "Solve";		
						
		case EuclidianConstants.MODE_CAS_DERIVATIVE:
			return "Derivative";
			
		case EuclidianConstants.MODE_CAS_INTEGRAL:
			return "Integral";											
			
		case EuclidianConstants.MODE_ATTACH_DETACH:
			return "AttachDetachPoint";				
			
			
		// Spreadsheet	
		case EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS:
			return "OneVarStats";				
			
		case EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS:
			return "TwoVarStats";				
			
		case EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS:
			return "MultiVarStats";
	
		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:
			return "CreateList";		
			
		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			return "CreateListOfPoints";		
			
		case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX:
			return "CreateMatrix";
			
		case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT:
			return "CreateTable";
			
		case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE:
			return "CreatePolyLine";
			
		case EuclidianConstants.MODE_SPREADSHEET_SUM:
			return "SumCells";
			
		case EuclidianConstants.MODE_SPREADSHEET_AVERAGE:
			return "MeanCells";
			
		case EuclidianConstants.MODE_SPREADSHEET_COUNT:
			return "CountCells";
			
		case EuclidianConstants.MODE_SPREADSHEET_MIN:
			return "MinCells";
			
		case EuclidianConstants.MODE_SPREADSHEET_MAX:
			return "MaxCells";
			
		default:
			return "";
		}
	}
    
    
    
    
    
	/* *******************************************
	 *  Methods for MyXMLHandler
	 * ********************************************/
	public boolean handleCoords(GeoElement geo, LinkedHashMap<String, String> attrs) {
		
		if (!(geo instanceof GeoVec3D)) {
			Application.debug("wrong element type for <coords>: "
					+ geo.getClass());
			return false;
		}
		GeoVec3D v = (GeoVec3D) geo;
		


		try {
			double x = Double.parseDouble((String) attrs.get("x"));
			double y = Double.parseDouble((String) attrs.get("y"));
			double z = Double.parseDouble((String) attrs.get("z"));
			v.hasUpdatePrevilege = true;
			v.setCoords(x, y, z);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
    
    
    
	/* *******************************************
	 *  Construction specific methods
	 * ********************************************/

	/**
	 * Returns the ConstructionElement for the given GeoElement.
	 * If geo is independent geo itself is returned. If geo is dependent
	 * it's parent algorithm is returned.	 
	 */
	public static ConstructionElement getConstructionElement(GeoElement geo) {
		AlgoElement algo = geo.getParentAlgorithm();
		if (algo == null)
			return geo;
		else
			return algo;
	}
	
	/**
	 * Returns the Construction object of this kernel.
	 */
	public Construction getConstruction() {
		return cons;
	}
	
	
	/**
	 * Returns the ConstructionElement for the given construction index.
	 */
	public ConstructionElement getConstructionElement(int index) {
		return cons.getConstructionElement(index);
	}

	public void setConstructionStep(int step) {
		if (cons.getStep() != step) {
			cons.setStep(step);
			app.setUnsaved();
		}
	}

	public int getConstructionStep() {
		return cons.getStep();
	}

	public int getLastConstructionStep() {
		return cons.steps() - 1;
	}
	
	/**
	 * Sets construction step to 
	 * first step of construction protocol. 
	 * Note: showOnlyBreakpoints() is important here
	 */
	public void firstStep() {
		int step = 0;
		
		if (showOnlyBreakpoints()) {
			setConstructionStep(getNextBreakpoint(step));		
		} else {	
			setConstructionStep(step);
    	}
	}
	
	/**
	 * Sets construction step to 
	 * last step of construction protocol. 
	 * Note: showOnlyBreakpoints() is important here
	 */
	public void lastStep() {
		int step = getLastConstructionStep();
		
		if (showOnlyBreakpoints()) {
			setConstructionStep(getPreviousBreakpoint(step));		
		} else {	
			setConstructionStep(step);
    	}
	}
	
	/**
	 * Sets construction step to 
	 * next step of construction protocol. 
	 * Note: showOnlyBreakpoints() is important here
	 */
	public void nextStep() {		
		int step = cons.getStep() + 1;
		
		if (showOnlyBreakpoints()) {
			setConstructionStep(getNextBreakpoint(step));		
		} else {	
			setConstructionStep(step);
    	}
	}
	
	private int getNextBreakpoint(int step) {
		int lastStep = getLastConstructionStep();
		// go to next breakpoint
		while (step <= lastStep) {
			if (cons.getConstructionElement(step).isConsProtocolBreakpoint()) {				
				return step;
			}
				
			step++;
		}
		
		return lastStep;
	}

	/**
	 * Sets construction step to 
	 * previous step of construction protocol	
	 * Note: showOnlyBreakpoints() is important here 
	 */
	public void previousStep() {		
		int step = cons.getStep() - 1;
		
		if (showOnlyBreakpoints()) {
			cons.setStep(getPreviousBreakpoint(step));
		}
    	else {		
    		cons.setStep(step);
    	}
	}
	
	private int getPreviousBreakpoint(int step) {
		// go to previous breakpoint
		while (step >= 0) {
			if (cons.getConstructionElement(step).isConsProtocolBreakpoint())
				return step;				
			step--;
		}
		return -1;
	}
	
	/**
	 * Move object at position from to position to in current construction.
	 */
	public boolean moveInConstructionList(int from, int to) {
		return cons.moveInConstructionList(from, to);
	}

	public void clearConstruction() {		
		if (macroManager != null)
			macroManager.setAllMacrosUnused();
		
		// clear animations
		if (animationManager != null) {
			animationManager.stopAnimation();
			animationManager.clearAnimatedGeos();
		}
				
		cons.clearConstruction();
		notifyClearView();
		notifyRepaint();

		System.gc();
	}

	public void updateConstruction() {
		cons.updateConstruction();
		notifyRepaint();
	}

	/**
	 * Tests if the current construction has no elements. 
	 * @return true if the current construction has no GeoElements; false otherwise.
	 */
	public boolean isEmpty() {
		return cons.isEmpty();
	}

	/* ******************************
	 * redo / undo for current construction
	 * ******************************/

	public void setUndoActive(boolean flag) {
		undoActive = flag;
	}
	
	public boolean isUndoActive() {
		return undoActive;
	}
	
	public void storeUndoInfo() {
		if (undoActive) {
			cons.storeUndoInfo();
		}
	}

	public void restoreCurrentUndoInfo() {
		if (undoActive) cons.restoreCurrentUndoInfo();
	}

	public void initUndoInfo() {
		if (undoActive) cons.initUndoInfo();
	}

	public void redo() {
		if (undoActive){			
			notifyReset();
			clearJustCreatedGeosInViews();
			cons.redo();	
			notifyReset();
		}
	}

	public void undo() {
		if (undoActive) {			
			notifyReset();
			clearJustCreatedGeosInViews();						
			app.getActiveEuclidianView().getEuclidianController().clearSelections();
			cons.undo();
			notifyReset();

			// repaint needed for last undo in second EuclidianView (bugfix)
			if (!undoPossible())
				notifyRepaint();
		}
	}

	public boolean undoPossible() {
		return undoActive && cons.undoPossible();
	}

	public boolean redoPossible() {
		return undoActive && cons.redoPossible();
	}

	/* *******************************************************
	 * methods for view-Pattern (Model-View-Controller)
	 * *******************************************************/

	public void attach(View view) {							
	//	Application.debug("ATTACH " + view + ", notifyActive: " + notifyViewsActive);			
		if (!notifyViewsActive) {			
			viewCnt = oldViewCnt;
		}
		
		// view already attached?
		boolean viewFound = false;
		for (int i = 0; i < viewCnt; i++) {
			if (views[i] == view) {
				viewFound = true;
				break;
			}				
		}
		
		if (!viewFound) {
			// new view
			views[viewCnt++] = view;
		}
				
		//TODO: remove
		System.out.print("  current views:\n");
		for (int i = 0; i < viewCnt; i++) {
			System.out.print(views[i] + "\n");
		}
		System.out.print("\n");
		//Application.debug();
		
		
		if (!notifyViewsActive) {
			oldViewCnt = viewCnt;
			viewCnt = 0;
		}
		
		System.err.println("XXXXXXXXX Number of registered views = "+viewCnt);
		for (int i = 0 ; i < viewCnt ; i++) {
			System.out.println(views[i].getClass());
		}
	}

	public void detach(View view) {    
		// Application.debug("detach " + view);
		
		if (!notifyViewsActive) {
			viewCnt = oldViewCnt;
		}
		
		int pos = -1;
		for (int i = 0; i < viewCnt; ++i) {
			if (views[i] == view) {
				pos = i;
				views[pos] = null; // delete view
				break;
			}
		}
		
		// view found
		if (pos > -1) {						
			// copy following views
			viewCnt--;		
			for (; pos < viewCnt; ++pos) {
				views[pos] = views[pos + 1];
			}
		}
		
		/*
		System.out.print("  current views: ");
		for (int i = 0; i < viewCnt; i++) {
			System.out.print(views[i] + ", ");
		}
		Application.debug();		
		*/
		
		if (!notifyViewsActive) {
			oldViewCnt = viewCnt;
			viewCnt = 0;
		}
		
		System.err.println("XXXXXXXXX Number of registered views = "+viewCnt);
		for (int i = 0 ; i < viewCnt ; i++) {
			System.out.println(views[i].getClass());
		}

	}	
	
	/**
	 * Notify the views that the mode changed.
	 * 
	 * @param mode
	 */
	final public void notifyModeChanged(int mode) {
		for(int i = 0; i < viewCnt; ++i) {
			views[i].setMode(mode);
		}
	}

	final public void notifyAddAll(View view) {
		int consStep = cons.getStep();
		notifyAddAll(view, consStep);
	}
		
	final public void notifyAddAll(View view, int consStep) {
		if (!notifyViewsActive) return;
				
		for (GeoElement geo : cons.getGeoSetWithCasCellsConstructionOrder()) {
			// stop when not visible for current construction step
			if (!geo.isAvailableAtConstructionStep(consStep))
				break;
			
			view.add(geo);
		}			
	}	
	

//	final public void notifyRemoveAll(View view) {
//		Iterator it = cons.getGeoSetConstructionOrder().iterator();
//		while (it.hasNext()) {
//			GeoElement geo = (GeoElement) it.next();
//			view.remove(geo);
//		}	
//	}

	/**
	 * Tells views to update all labeled elements of current construction.
	 *
	final public static void notifyUpdateAll() {
		notifyUpdate(kernelConstruction.getAllGeoElements());
	}*/

	final void notifyAdd(GeoElement geo) {
		if (notifyViewsActive) {
			for (int i = 0; i < viewCnt; ++i) {
				if (views[i].getViewID() != Application.VIEW_CONSTRUCTION_PROTOCOL
					|| notifyConstructionProtocolViewAboutAddRemoveActive)
					views[i].add(geo);
			}
		}
		
		notifyRenameListenerAlgos();
	}

	final void notifyRemove(GeoElement geo) {
		if (notifyViewsActive) {
			for (int i = 0; i < viewCnt; ++i) {
				if (views[i].getViewID() != Application.VIEW_CONSTRUCTION_PROTOCOL
						|| notifyConstructionProtocolViewAboutAddRemoveActive)
					views[i].remove(geo);
			}
		}
		
		notifyRenameListenerAlgos();
	}

	public final void notifyUpdate(GeoElement geo) {
		if (notifyViewsActive) {
			for (int i = 0; i < viewCnt; ++i) {
				views[i].update(geo);
			}
		}
	}
	
	public final void notifyUpdateVisualStyle(GeoElement geo) {
		if (notifyViewsActive) {
			for (int i = 0; i < viewCnt; ++i) {
				views[i].updateVisualStyle(geo);
			}
		}
	}
	
	final void notifyUpdateAuxiliaryObject(GeoElement geo) {
		if (notifyViewsActive) {
			for (int i = 0; i < viewCnt; ++i) {
				views[i].updateAuxiliaryObject(geo);
			}
		}
	}

	final  void notifyRename(GeoElement geo) {
		if (notifyViewsActive) {
			for (int i = 0; i < viewCnt; ++i) {
				views[i].rename(geo);
			}
		}
		
		notifyRenameListenerAlgos();
	}
	
	public void setNotifyViewsActive(boolean flag) {	
		//Application.debug("setNotifyViews: " + flag);
		
		if (flag != notifyViewsActive) {
			notifyViewsActive = flag;
			
			if (flag) {
				//Application.debug("Activate VIEWS");				
				viewReiniting = true;
				
				// "attach" views again
				viewCnt = oldViewCnt;		
								
				// add all geos to all views
				for(int i = 0; i < viewCnt; ++i) {
					notifyAddAll(views[i]);					
				}				
				
				notifyEuclidianViewCE();
				notifyReset();					
				viewReiniting = false;
			} 
			else {
				//Application.debug("Deactivate VIEWS");

				// "detach" views
				notifyClearView();				
				oldViewCnt = viewCnt;
				viewCnt = 0;								
			}					
		}		
	}
	private int oldViewCnt;
	
	public boolean isNotifyViewsActive() {
		return notifyViewsActive && !viewReiniting;
	}

	public void setNotifyConstructionProtocolViewAboutAddRemoveActive(boolean flag) {
		notifyConstructionProtocolViewAboutAddRemoveActive = flag;
	}

	public boolean isNotifyConstructionProtocolViewAboutAddRemoveActive() {
		return notifyConstructionProtocolViewAboutAddRemoveActive;
	}

	public boolean isViewReiniting() {
		return viewReiniting;
	}
		
	private boolean notifyRepaint = true;
		
	public void setNotifyRepaintActive(boolean flag) {
		if (flag != notifyRepaint) {
			notifyRepaint = flag;
			if (notifyRepaint)
				notifyRepaint();
		}
	}
		
	final public boolean isNotifyRepaintActive() {
		return notifyRepaint;
	}
	
	public final void notifyRepaint() {
		if (notifyRepaint) {
			for (int i = 0; i < viewCnt; ++i) {			
				views[i].repaintView();
			}
		} 
	}
	
	final void notifyReset() {
		for (int i = 0; i < viewCnt; ++i) {
			views[i].reset();
		}
	}
	
	final void notifyClearView() {
		for (int i = 0; i < viewCnt; ++i) {
			views[i].clearView();
		}
	}
	
	public void clearJustCreatedGeosInViews() {
		for (int i = 0; i < viewCnt; i++) {
			if (views[i] instanceof EuclidianView)
				((EuclidianView)views[i]).getEuclidianController().clearJustCreatedGeos();
		}
	}
	
	/* **********************************
	 *   MACRO handling
	 * **********************************/			
	
	/**
	 * Creates a new macro within the kernel. A macro is a user defined
	 * command in GeoGebra.
	 */
	public void addMacro(Macro macro) {
		if (macroManager == null) {
			macroManager = new MacroManager();
		}						
		macroManager.addMacro(macro);				
	}
	
	/**
	 * Removes a macro from the kernel.
	 */
	public void removeMacro(Macro macro) {
		if (macroManager != null)								
			macroManager.removeMacro(macro);
	}
	
	/**
	 * Removes all macros from the kernel. 
	 */
	public void removeAllMacros() {
		if (macroManager != null) {								
			app.removeMacroCommands();			
			macroManager.removeAllMacros();			
		}
	}
	
	/**
	 * Sets the command name of a macro. Note: if the given name is
	 * already used nothing is done.
	 * @return if the command name was really set
	 */
	public boolean setMacroCommandName(Macro macro, String cmdName) {
		boolean nameUsed = macroManager.getMacro(cmdName) != null;
		if (nameUsed || cmdName == null || cmdName.length() == 0) 
			return false;
		
		macroManager.setMacroCommandName(macro, cmdName);		
		return true;		
	}
	
	/**
	 * Returns the macro object for a given macro name.
	 * Note: null may be returned.
	 */
	public Macro getMacro(String name) {
		return (macroManager == null) ? null : macroManager.getMacro(name);		
	}		
	
	/**
	 * Returns the number of currently registered macros
	 */
	public int getMacroNumber() {
		if (macroManager == null)
			return 0;
		else
			return macroManager.getMacroNumber();
	}
	
	/**
	 * Returns a list with all currently registered macros.
	 */
	public ArrayList getAllMacros() {
		if (macroManager == null)
			return null;
		else
			return macroManager.getAllMacros();
	}
	
	/**
	 * Returns i-th registered macro
	 */
	public Macro getMacro(int i) {
		try {
			return macroManager.getMacro(i);
		} catch (Exception e) {
			return null;
		}		
	}
	
	/**
	 * Returns the ID of the given macro.
	 */
	public int getMacroID(Macro macro) {
		return (macroManager == null) ? -1 : macroManager.getMacroID(macro);	
	}
	
	/**
	 * Creates a new algorithm that uses the given macro.
	 * @return output of macro algorithm
	 */
	final public GeoElement [] useMacro(String [] labels, Macro macro, GeoElement [] input) {		
		try {
			AlgoMacro algo = new AlgoMacro(cons, labels, macro, input);
			return algo.getOutput();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}						
	}
	
	/**
	 * Returns an XML represenation of the given macros in this kernel.
	 * 
	 * @return
	 */
	public String getMacroXML(ArrayList macros) {
		if (hasMacros())					
			return MacroManager.getMacroXML(macros);
		else
			return "";
	}
	
	/**
	 * Returns whether any macros have been added to this kernel. 
	 * @return whether any macros have been added to this kernel.
	 */
	public boolean hasMacros() {
		return (macroManager != null && macroManager.getMacroNumber() > 0);
	}
	

	/***********************************
	 * FACTORY METHODS FOR GeoElements
	 ***********************************/

	/** Point label with cartesian coordinates (x,y)   */
	final public GeoPoint Point(String label, double x, double y) {
		GeoPoint p = new GeoPoint(cons);
		p.setCoords(x, y, 1.0);
		p.setMode(COORD_CARTESIAN);
		p.setLabel(label); // invokes add()                
		return p;
	}

	/** Point label with cartesian coordinates (x,y)   */
	final public GeoPoint Point(String label, double x, double y, boolean complex) {
		GeoPoint p = new GeoPoint(cons);
		p.setCoords(x, y, 1.0);
		if (complex) {
			p.setMode(COORD_COMPLEX);
			/* removed as this sets the mode back to COORD_CARTESIAN

			// we have to reset the visual style as the constructor
			// did not know that this was a complex number
			//p.setConstructionDefaults(); */
		}
		else
			p.setMode(COORD_CARTESIAN);
		p.setLabel(label); // invokes add()
		return p;
	}

	/** Vector label with cartesian coordinates (x,y)   */
	final public GeoVector Vector(String label, double x, double y) {
		GeoVector v = new GeoVector(cons);
		v.setCoords(x, y, 0.0);
		v.setMode(COORD_CARTESIAN);
		v.setLabel(label); // invokes add()                
		return v;
	}

	/** Line a x + b y + c = 0 named label */
	final public GeoLine Line(
		String label,
		double a,
		double b,
		double c) {
		GeoLine line = new GeoLine(cons, label, a, b, c);
		return line;
	}

	

	/** Conic label with equation ax��� + bxy + cy��� + dx + ey + f = 0  */
	final public GeoConic Conic(
		String label,
		double a,
		double b,
		double c,
		double d,
		double e,
		double f) {
		double[] coeffs = { a, b, c, d, e, f };
		GeoConic conic = new GeoConic(cons, label, coeffs);
		return conic;
	}

	
	/** Implicit Polynomial  */
	final public GeoImplicitPoly ImplicitPoly(String label,Polynomial poly) {
		GeoImplicitPoly implicitPoly = new GeoImplicitPoly(cons, label, poly);
		return implicitPoly;
	}
	
	/** Implicit Polynomial through points */
	final public GeoImplicitPoly ImplicitPoly(String label, GeoList points) {
		AlgoImplicitPolyThroughPoints algo = new AlgoImplicitPolyThroughPoints(cons, label, points);
		GeoImplicitPoly implicitPoly = algo.getImplicitPoly();
		return implicitPoly;
	}
	
	final public GeoImplicitPoly ImplicitPoly(String label,GeoFunctionNVar func){
		AlgoImplicitPolyFunction algo=new AlgoImplicitPolyFunction(cons, label, func);
		GeoImplicitPoly implicitPoly = algo.getImplicitPoly();
		return implicitPoly;
	}

	/** Converts number to angle */
	final public GeoAngle Angle(String label, GeoNumeric num) {
		AlgoAngleNumeric algo = new AlgoAngleNumeric(cons, label, num);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	/** Function in x,  e.g. f(x) = 4 x��� + 3 x���
	 */
	final public GeoFunction Function(String label, Function fun) {
		GeoFunction f = new GeoFunction(cons, label, fun);
		return f;
	}
	
	/** Function in multiple variables,  e.g. f(x,y) = 4 x^2 + 3 y^2
	 */
	final public GeoFunctionNVar FunctionNVar(String label, FunctionNVar fun) {
		GeoFunctionNVar f = new GeoFunctionNVar(cons, label, fun);
		return f;
	}
	
	/** Interval in x,  e.g. x > 3 && x < 6
	 */
	final public GeoInterval Interval(String label, Function fun) {
		GeoInterval f = new GeoInterval(cons, label, fun);
		return f;
	}
	
	final public GeoText Text(String label, String text) {
		GeoText t = new GeoText(cons);
		t.setTextString(text);
		t.setLabel(label);
		return t;
	}
	
	final public GeoBoolean Boolean(String label, boolean value) {
		GeoBoolean b = new GeoBoolean(cons);
		b.setValue(value);
		b.setLabel(label);
		return b;
	}
		
	/**
	 * Creates a free list object with the given
	 * @param label
	 * @param geoElementList list of GeoElement objects
	 * @return
	 */
	final public GeoList List(String label, ArrayList geoElementList, boolean isIndependent) {
		if (isIndependent) {
			GeoList list = new GeoList(cons);		
			int size = geoElementList.size();
			for (int i=0; i < size; i++) {
				list.add((GeoElement) geoElementList.get(i));
			}
			list.setLabel(label);
			return list;
		} 
		else {
			AlgoDependentList algoList = new AlgoDependentList(cons, label, geoElementList);
			return algoList.getGeoList();
		}		
	}
	
	/**
	 * Creates a dependent list object with the given label, 
	 * e.g. {3, 2, 1} + {a, b, 2}	 
	 */
	final public GeoList ListExpression(String label, ExpressionNode root) {
		AlgoDependentListExpression algo =
			new AlgoDependentListExpression(cons, label, root);		
		return algo.getList();
	}
	
	/**
	 * Creates a list object for a range of cells in the spreadsheet. 
	 * e.g. A1:B2
	 */
	final public GeoList CellRange(String label, GeoElement startCell, GeoElement endCell) {
		AlgoCellRange algo =
			new AlgoCellRange(cons, label, startCell, endCell);		
		return algo.getList();
	}
	
	/********************
	 * ALGORITHMIC PART *
	 ********************/

	/** 
	 * If-then-else construct.
	 */
	final public GeoElement If(String label, 
			GeoBoolean condition,
			GeoElement geoIf, GeoElement geoElse) {
		
		// check if geoIf and geoElse are of same type
	/*	if (geoElse == null ||
			geoIf.isNumberValue() && geoElse.isNumberValue() ||
			geoIf.getTypeString().equals(geoElse.getTypeString())) 
		{*/
			AlgoIf algo = new AlgoIf(cons, label, condition, geoIf, geoElse);
			return algo.getGeoElement();			
	/*	}
		else {
			// incompatible types
			Application.debug("if incompatible: " + geoIf + ", " + geoElse);
			return null;
		}	*/			
	}
	
	/** 
	 * If-then-else construct for functions. 
	 *  example: If[ x < 2, x^2, x + 2 ]
	 */
	final public GeoFunction If(String label, 
			GeoFunction boolFun,
			GeoFunction ifFun, GeoFunction elseFun) {
		
		AlgoIfFunction algo = new AlgoIfFunction(cons, label, boolFun, ifFun, elseFun);
		return algo.getGeoFunction();
	}	
	
	/** 
	 * If-then-else construct for functions. 
	 *  example: If[ x < 2, x^2, x + 2 ]
	 */
	final public GeoNumeric CountIf(String label, 
			GeoFunction boolFun,
			GeoList list) {
		
		AlgoCountIf algo = new AlgoCountIf(cons, label, boolFun, list);
		return algo.getResult();
	}	
	
	/** 
	 * Sequence command:
 	 * Sequence[ <expression>, <number-var>, <from>, <to>, <step> ]  
 	 * @return array with GeoList object and its list items
	 */
	final public GeoElement [] Sequence(String label, 
			GeoElement expression, GeoNumeric localVar, 
			NumberValue from, NumberValue to, NumberValue step) {
		
			AlgoSequence algo = new AlgoSequence(cons, label, expression, localVar, from, to, step);
			return algo.getOutput();	
	}	
	
	/** 
	 * Cartesian curve command:
 	 * Curve[ <expression x-coord>, <expression x-coord>, <number-var>, <from>, <to> ]  
	 */
	final public GeoCurveCartesian CurveCartesian(String label, 
			NumberValue xcoord, NumberValue ycoord, 
			GeoNumeric localVar, NumberValue from, NumberValue to) 
	{									
		AlgoCurveCartesian algo = new AlgoCurveCartesian(cons, label, new NumberValue[] {xcoord, ycoord} , localVar, from, to);
		return (GeoCurveCartesian) algo.getCurve();		
	}	
	
	/**
	 * Converts a NumberValue object to an ExpressionNode object. 
	 */
	public ExpressionNode convertNumberValueToExpressionNode(NumberValue nv) {
		GeoElement geo = nv.toGeoElement();
		AlgoElement algo = geo.getParentAlgorithm();
		
		if (algo != null && algo instanceof AlgoDependentNumber) {
			AlgoDependentNumber algoDep = (AlgoDependentNumber) algo;
			return algoDep.getExpression().getCopy(this);
		}
		else {
			return new ExpressionNode(this, geo);
		}		
	}
	
	/** 
	 * GeoCasCell dependent on other variables,
	 * e.g. m := c + 3
	 * @return resulting casCell created using geoCasCell.copy(). 
	 */
	final public GeoCasCell DependentCasCell(GeoCasCell geoCasCell) {
		AlgoDependentCasCell algo = new AlgoDependentCasCell(geoCasCell);
		return algo.getCasCell();
	}
	
	/** Number dependent on arithmetic expression with variables,
	 * represented by a tree. e.g. t = 6z - 2
	 */
	final public GeoNumeric DependentNumber(
		String label,
		ExpressionNode root,
		boolean isAngle) {
		AlgoDependentNumber algo =
			new AlgoDependentNumber(cons, label, root, isAngle);
		GeoNumeric number = algo.getNumber();
		return number;
	}

	/** Point dependent on arithmetic expression with variables,
	 * represented by a tree. e.g. P = (4t, 2s)
	 */
	final public GeoPoint DependentPoint(
		String label,
		ExpressionNode root, boolean complex) {
		AlgoDependentPoint algo = new AlgoDependentPoint(cons, label, root, complex);
		GeoPoint P = algo.getPoint();
		return P;
	}

	/** Vector dependent on arithmetic expression with variables,
	 * represented by a tree. e.g. v = u + 3 w
	 */
	final public GeoVector DependentVector(
		String label,
		ExpressionNode root) {
		AlgoDependentVector algo = new AlgoDependentVector(cons, label, root);
		GeoVector v = algo.getVector();
		return v;
	}

	/** Line dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. y = k x + d
	 */
	final public GeoLine DependentLine(String label, Equation equ) {
		AlgoDependentLine algo = new AlgoDependentLine(cons, label, equ);
		GeoLine line = algo.getLine();
		return line;
	}

	

	/** Conic dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. y��� = 2 p x 
	 */
	final public GeoConic DependentConic(String label, Equation equ) {
		AlgoDependentConic algo = new AlgoDependentConic(cons, label, equ);
		GeoConic conic = algo.getConic();
		return conic;
	}
	
	final public GeoElement  DependentImplicitPoly(String label, Equation equ) {
		AlgoDependentImplicitPoly algo = new AlgoDependentImplicitPoly(cons, label, equ);
		GeoElement geo = algo.getGeo();
		return geo;
	}

	/** Function dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. f(x) = a x��� + b x���
	 */
	final public GeoFunction DependentFunction(
		String label,
		Function fun) {
		AlgoDependentFunction algo = new AlgoDependentFunction(cons, label, fun);
		GeoFunction f = algo.getFunction();
		return f;
	}
	
	/** Multivariate Function depending on coefficients of arithmetic expressions with variables,
	 *  e.g. f(x,y) = a x^2 + b y^2
	 */
	final public GeoFunctionNVar DependentFunctionNVar(
		String label,
		FunctionNVar fun) {
		AlgoDependentFunctionNVar algo = new AlgoDependentFunctionNVar(cons, label, fun);
		GeoFunctionNVar f = algo.getFunction();
		return f;
	}
	
	/** Interval dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. x > a && x < b
	 */
	final public GeoFunction DependentInterval(
		String label,
		Function fun) {
		AlgoDependentInterval algo = new AlgoDependentInterval(cons, label, fun);
		GeoFunction f = algo.getFunction();
		return f;
	}
	
	/** Text dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. text = "Radius: " + r
	 */
	final public GeoText DependentText(
		String label,
		ExpressionNode root) {
		AlgoDependentText algo = new AlgoDependentText(cons, label, root);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	
	/** 
	 * Creates a dependent copy of origGeo with label
	 */
	final public GeoElement DependentGeoCopy(String label, ExpressionNode origGeoNode) {
		AlgoDependentGeoCopy algo = new AlgoDependentGeoCopy(cons, label, origGeoNode);
		return algo.getGeo();
	}
	
	final public GeoElement DependentGeoCopy(String label, GeoElement origGeoNode) {
		AlgoDependentGeoCopy algo = new AlgoDependentGeoCopy(cons, label, origGeoNode);
		return algo.getGeo();
	}
	
	/** 
	 * Name of geo.
	 */
	final public GeoText Name(
		String label,
		GeoElement geo) {
		AlgoName algo = new AlgoName(cons, label, geo);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Object from name
	 */
	final public GeoElement Object(
		String label,
		GeoText text) {
		AlgoObject algo = new AlgoObject(cons, label, text);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Spreadsheet Object from coords
	 */
	final public GeoElement Cell(
		String label,
		NumberValue a, NumberValue b) {
		AlgoCell algo = new AlgoCell(cons, label, a, b);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * ColumnName[]
	 */
	final public GeoText ColumnName(
		String label,
		GeoElement geo) {
		AlgoColumnName algo = new AlgoColumnName(cons, label, geo);
		GeoText t = algo.getGeoText();
		return t;
	}		
	
	/** 
	 * LaTeX of geo.
	 */
	final public GeoText LaTeX(
		String label,
		GeoElement geo, GeoBoolean substituteVars, GeoBoolean showName) {
		AlgoLaTeX algo = new AlgoLaTeX(cons, label, geo, substituteVars, showName);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * LaTeX of geo.
	 */
	final public GeoText LaTeX(
		String label,
		GeoElement geo) {
		AlgoLaTeX algo = new AlgoLaTeX(cons, label, geo);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo) {
		AlgoText algo = new AlgoText(cons, label, geo);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo, GeoBoolean substituteVars) {
		AlgoText algo = new AlgoText(cons, label, geo, substituteVars);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo, GeoPoint p, GeoBoolean substituteVars) {
		AlgoText algo = new AlgoText(cons, label, geo, p, substituteVars);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo, GeoPoint p, GeoBoolean substituteVars, GeoBoolean latex) {
		AlgoText algo = new AlgoText(cons, label, geo, p, substituteVars, latex);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo, GeoPoint p) {
		AlgoText algo = new AlgoText(cons, label, geo, p);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Row of geo.
	 */
	final public GeoNumeric Row(
		String label,
		GeoElement geo) {
		AlgoRow algo = new AlgoRow(cons, label, geo);
		GeoNumeric ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Column of geo.
	 */
	final public GeoNumeric Column(
		String label,
		GeoElement geo) {
		AlgoColumn algo = new AlgoColumn(cons, label, geo);
		GeoNumeric ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * ToNumber
	 */
	final public GeoNumeric LetterToUnicode(
		String label,
		GeoText geo) {
		AlgoLetterToUnicode algo = new AlgoLetterToUnicode(cons, label, geo);
		GeoNumeric ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * ToNumbers
	 */
	final public GeoList TextToUnicode(
		String label,
		GeoText geo) {
		AlgoTextToUnicode algo = new AlgoTextToUnicode(cons, label, geo);
		GeoList ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * ToText(number)
	 */
	final public GeoText UnicodeToLetter(String label, NumberValue a) {
		AlgoUnicodeToLetter algo = new AlgoUnicodeToLetter(cons, label, a);
		GeoText text = algo.getResult();
		return text;
	}
	
	/** 
	 * ToText(list)
	 */
	final public GeoText UnicodeToText(
		String label,
		GeoList geo) {
		AlgoUnicodeToText algo = new AlgoUnicodeToText(cons, label, geo);
		GeoText ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Ordinal(list)
	 */
	final public GeoText Ordinal(
		String label,
		GeoNumeric geo) {
		AlgoOrdinal algo = new AlgoOrdinal(cons, label, geo);
		GeoText ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * returns the current x-axis step
	 * Michael Borcherds 
	 */
	final public GeoNumeric AxisStepX(
		String label) {
		AlgoAxisStepX algo = new AlgoAxisStepX(cons, label);
		GeoNumeric t = algo.getResult();
		return t;
	}
	
	/** 
	 * returns the current y-axis step
	 * Michael Borcherds 
	 */
	final public GeoNumeric AxisStepY(
		String label) {
		AlgoAxisStepY algo = new AlgoAxisStepY(cons, label);
		GeoNumeric t = algo.getResult();
		return t;
	}
	
	/** 
	 * returns the current construction protocol step
	 * Michael Borcherds 2008-05-15
	 */
	final public GeoNumeric ConstructionStep(
		String label) {
		AlgoConstructionStep algo = new AlgoConstructionStep(cons, label);
		GeoNumeric t = algo.getResult();
		return t;
	}
	
	/** 
	 * returns  current construction protocol step for an object
	 * Michael Borcherds 2008-05-15
	 */
	final public GeoNumeric ConstructionStep(
		String label, GeoElement geo) {
		AlgoStepObject algo = new AlgoStepObject(cons, label, geo);
		GeoNumeric t = algo.getResult();
		return t;
	}
	
	/** 
	 * Text dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. c = a & b
	 */
	final public GeoBoolean DependentBoolean(
		String label,
		ExpressionNode root) {
		AlgoDependentBoolean algo = new AlgoDependentBoolean(cons, label, root);
		return algo.getGeoBoolean();		
	}
	
	/** Point on path with cartesian coordinates (x,y)   */
	final public GeoPoint Point(String label, Path path, double x, double y, boolean addToConstruction, boolean complex) {
		boolean oldMacroMode = false;
		if (!addToConstruction) {
			oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);		

		}
		AlgoPointOnPath algo = new AlgoPointOnPath(cons, label, path, x, y);
		GeoPoint p = algo.getP();        
		if (complex) {
			p.setMode(COORD_COMPLEX);
			p.update();
		}
		if (!addToConstruction) {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		return p;
	}
	
	/** Point anywhere on path with    */
	final public GeoPoint Point(String label, Path path, NumberValue param) {						
		// try (0,0)
		AlgoPointOnPath algo = null;
		if(param == null)
			algo = new AlgoPointOnPath(cons, label, path, 0, 0);
		else
			algo = new AlgoPointOnPath(cons, label, path, 0, 0,param);
		GeoPoint p = algo.getP(); 
		
		// try (1,0) 
		if (!p.isDefined()) {
			p.setCoords(1,0,1);
			algo.update();
		}
		
		// try (random(),0)
		if (!p.isDefined()) {
			p.setCoords(Math.random(),0,1);
			algo.update();
		}
				
		return p;
	}

	/** Point anywhere on path with    */
	final public GeoPoint ClosestPoint(String label, Path path, GeoPoint p) {						
		AlgoClosestPoint algo = new AlgoClosestPoint(cons, label, path, p);				
		return algo.getP();
	}

	public GeoElement Point(String label, Path path) {

		return Point(label,path,null);
	}
	
	
	/** Point in region with cartesian coordinates (x,y)   */
	final public GeoPoint PointIn(String label, Region region, double x, double y, boolean addToConstruction, boolean complex) {
		boolean oldMacroMode = false;
		if (!addToConstruction) {
			oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);		

		}
		AlgoPointInRegion algo = new AlgoPointInRegion(cons, label, region, x, y);
		//Application.debug("PointIn - \n x="+x+"\n y="+y);
		GeoPoint p = algo.getP();    
		if (complex) {
			p.setMode(COORD_COMPLEX);
		}
		if (!addToConstruction) {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		return p;
	}
	
	/** Point in region */
	final public GeoPoint PointIn(String label, Region region) {  
		return PointIn(label,region,0,0, true, false); //TODO do as for paths
	}	
	
	/** Point P + v   */
	final public GeoPoint Point(String label, GeoPoint P, GeoVector v) {
		AlgoPointVector algo = new AlgoPointVector(cons, label, P, v);
		GeoPoint p = algo.getQ();        
		return p;
	}

	/** 
	 * Returns the projected point of P on line g (or nearest for a Segment)
	 */
	final public GeoPoint ClosestPoint(GeoPoint P, GeoLine g) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);		
				
		AlgoClosestPoint cp = new AlgoClosestPoint(cons, g, P);
	
		cons.setSuppressLabelCreation(oldMacroMode);
		return cp.getP();
	}

	
	/** 
	 * Midpoint M = (P + Q)/2
	 */
	final public GeoPoint Midpoint(
		String label,
		GeoPoint P,
		GeoPoint Q) {
		AlgoMidpoint algo = new AlgoMidpoint(cons, label, P, Q);
		GeoPoint M = algo.getPoint();
		return M;
	}
	
	/** 
	 * Creates Midpoint M = (P + Q)/2 without label (for use as e.g. start point)
	 */
	final public GeoPoint Midpoint(
		GeoPoint P,
		GeoPoint Q) {

		boolean oldValue = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		GeoPoint midPoint = Midpoint(null, P, Q);
		cons.setSuppressLabelCreation(oldValue);
		return midPoint;
	}
	
	/** 
	 * Midpoint of segment
	 */
	final public GeoPoint Midpoint(
		String label,
		GeoSegment s) {
		AlgoMidpointSegment algo = new AlgoMidpointSegment(cons, label, s);
		GeoPoint M = algo.getPoint();
		return M;
	}

	/** 
	 * Midpoint of interval
	 */
	final public GeoNumeric Midpoint(
		String label,
		GeoInterval s) {
		AlgoIntervalMidpoint algo = new AlgoIntervalMidpoint(cons, label, s);
		GeoNumeric n = algo.getResult();
		return n;
	}

	/** 
	 * Min of interval
	 */
	final public GeoNumeric Min(
		String label,
		GeoInterval s) {
		AlgoIntervalMin algo = new AlgoIntervalMin(cons, label, s);
		GeoNumeric n = algo.getResult();
		return n;
	}

	/** 
	 * Max of interval
	 */
	final public GeoNumeric Max(
		String label,
		GeoInterval s) {
		AlgoIntervalMax algo = new AlgoIntervalMax(cons, label, s);
		GeoNumeric n = algo.getResult();
		return n;
	}

	/** 
		* LineSegment named label from Point P to Point Q
		*/
	final public GeoSegment Segment(
		String label,
		GeoPoint P,
		GeoPoint Q) {
		AlgoJoinPointsSegment algo = new AlgoJoinPointsSegment(cons, label, P, Q);
		GeoSegment s = algo.getSegment();
		return s;
	}

	/** 
	 * Line named label through Points P and Q
	 */
	final public GeoLine Line(String label, GeoPoint P, GeoPoint Q) {
		AlgoJoinPoints algo = new AlgoJoinPoints(cons, label, P, Q);
		GeoLine g = algo.getLine();
		return g;
	}

	/** 
	 * Line named label through Point P with direction of vector v
	 */
	final public GeoLine Line(String label, GeoPoint P, GeoVector v) {
		AlgoLinePointVector algo = new AlgoLinePointVector(cons, label, P, v);
		GeoLine g = algo.getLine();
		return g;
	}

	/** 
	 *  Ray named label through Points P and Q
	 */
	final public GeoRay Ray(String label, GeoPoint P, GeoPoint Q) {
		AlgoJoinPointsRay algo = new AlgoJoinPointsRay(cons, label, P, Q);
		return algo.getRay();
	}

	/** 
	 * Ray named label through Point P with direction of vector v
	 */
	final public GeoRay Ray(String label, GeoPoint P, GeoVector v) {
		AlgoRayPointVector algo = new AlgoRayPointVector(cons, label, P, v);
		return algo.getRay();
	}
	
	/** 
	* Line named label through Point P parallel to Line l
	*/
	final public GeoLine Line(String label, GeoPoint P, GeoLine l) {
		AlgoLinePointLine algo = new AlgoLinePointLine(cons, label, P, l);
		GeoLine g = algo.getLine();
		return g;
	}

	/** 
	* Line named label through Point P orthogonal to vector v
	*/
	final public GeoLine OrthogonalLine(
		String label,
		GeoPoint P,
		GeoVector v) {
		AlgoOrthoLinePointVector algo =
			new AlgoOrthoLinePointVector(cons, label, P, v);
		GeoLine g = algo.getLine();
		return g;
	}

	/** 
	 * Line named label through Point P orthogonal to line l
	 */
	final public GeoLine OrthogonalLine(
		String label,
		GeoPoint P,
		GeoLine l) {
		AlgoOrthoLinePointLine algo = new AlgoOrthoLinePointLine(cons, label, P, l);
		GeoLine g = algo.getLine();
		return g;
	}

	public GeoLineND OrthogonalLine(
			String label,
			GeoPointND P,
			GeoLineND l, 
			GeoDirectionND direction) {
		return OrthogonalLine(label, (GeoPoint) P, (GeoLine) l);
	}


	/** 
	 * Line bisector of points A, B
	 */
	final public GeoLine LineBisector(
		String label,
		GeoPoint A,
		GeoPoint B) {
		AlgoLineBisector algo = new AlgoLineBisector(cons, label, A, B);
		GeoLine g = algo.getLine();
		return g;
	}

	/** 
	  * Line bisector of segment s
	  */
	final public GeoLine LineBisector(String label, GeoSegment s) {
		AlgoLineBisectorSegment algo = new AlgoLineBisectorSegment(cons, label, s);
		GeoLine g = algo.getLine();
		return g;		
	}

	/** 
	 * Angular bisector of points A, B, C
	 */
	final public GeoLine AngularBisector(
		String label,
		GeoPoint A,
		GeoPoint B,
		GeoPoint C) {
		AlgoAngularBisectorPoints algo =
			new AlgoAngularBisectorPoints(cons, label, A, B, C);
		GeoLine g = algo.getLine();
		return g;
	}

	/** 
	 * Angular bisectors of lines g, h
	 */
	final public GeoLine[] AngularBisector(
		String[] labels,
		GeoLine g,
		GeoLine h) {
		AlgoAngularBisectorLines algo =
			new AlgoAngularBisectorLines(cons, labels, g, h);
		GeoLine[] lines = algo.getLines();
		return lines;
	}

	/** 
	 * Vector named label from Point P to Q
	 */
	final public GeoVector Vector(
		String label,
		GeoPoint P,
		GeoPoint Q) {
		AlgoVector algo = new AlgoVector(cons, label, P, Q);
		GeoVector v = (GeoVector) algo.getVector();
		v.setEuclidianVisible(true);
		v.update();
		notifyUpdate(v);
		return v;
	}

	/** 
	* Vector (0,0) to P
	*/
	final public GeoVector Vector(String label, GeoPoint P) {
		AlgoVectorPoint algo = new AlgoVectorPoint(cons, label, P);
		GeoVector v = algo.getVector();
		v.setEuclidianVisible(true);
		v.update();
		notifyUpdate(v);
		return v;
	}

	/** 
	 * Direction vector of line g
	 */
	final public GeoVector Direction(String label, GeoLine g) {
		AlgoDirection algo = new AlgoDirection(cons, label, g);
		GeoVector v = algo.getVector();
		return v;
	}

	/** 
	 * Slope of line g
	 */
	final public GeoNumeric Slope(String label, GeoLine g) {
		AlgoSlope algo = new AlgoSlope(cons, label, g);
		GeoNumeric slope = algo.getSlope();
		return slope;
	}	
	
	/** 
	 * BarChart	
	 */
	final public GeoNumeric BarChart(String label, 
					NumberValue a, NumberValue b, GeoList list) {
		AlgoBarChart algo = new AlgoBarChart(cons, label, a, b, list);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * BarChart	
	 */
	final public GeoNumeric BarChart(String label, 
			GeoList list1, GeoList list2) {
		AlgoBarChart algo = new AlgoBarChart(cons, label, list1, list2);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * BarChart	
	 */
	final public GeoNumeric BarChart(String label, 
			GeoList list1, GeoList list2, NumberValue width) {
		AlgoBarChart algo = new AlgoBarChart(cons, label, list1, list2, width);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * BarChart	
	 */
	final public GeoNumeric BarChart(String label, 
			GeoList list, GeoNumeric a) {
		AlgoBarChart algo = new AlgoBarChart(cons, label, list, a);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * BarChart	
	 */
	final public GeoNumeric BarChart(String label, 
					NumberValue a, NumberValue b, GeoElement expression, GeoNumeric localVar, 
					NumberValue from, NumberValue to, NumberValue step) {
		
		AlgoSequence seq = new AlgoSequence(cons, expression, localVar, from, to, step);
		cons.removeFromConstructionList(seq);
		
		AlgoBarChart algo = new AlgoBarChart(cons, label, a, b, (GeoList)seq.getOutput()[0]);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * BoxPlot
	 */
	final public GeoNumeric BoxPlot(String label, 
			NumberValue a, NumberValue b, GeoList rawData) {
		
		/*
		AlgoListMin min = new AlgoListMin(cons,rawData);
		cons.removeFromConstructionList(min);
		AlgoQ1 Q1 = new AlgoQ1(cons,rawData);
		cons.removeFromConstructionList(Q1);
		AlgoMedian median = new AlgoMedian(cons,rawData);
		cons.removeFromConstructionList(median);
		AlgoQ3 Q3 = new AlgoQ3(cons,rawData);
		cons.removeFromConstructionList(Q3);
		AlgoListMax max = new AlgoListMax(cons,rawData);
		cons.removeFromConstructionList(max);
	
		AlgoBoxPlot algo = new AlgoBoxPlot(cons, label, a, b, (NumberValue)(min.getMin()),
				(NumberValue)(Q1.getQ1()), (NumberValue)(median.getMedian()), (NumberValue)(Q3.getQ3()), (NumberValue)(max.getMax()));
		*/
		
		AlgoBoxPlot algo = new AlgoBoxPlot(cons, label, a, b, rawData);
		
		
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * BoxPlot
	 */
	final public GeoNumeric BoxPlot(String label, 
			NumberValue a, NumberValue b, NumberValue min, NumberValue Q1,
			NumberValue median, NumberValue Q3, NumberValue max) {
		AlgoBoxPlot algo = new AlgoBoxPlot(cons, label, a, b, min, Q1, median, Q3, max);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * Histogram[classList, dataList]
	 */
	final public GeoNumeric Histogram(String label, 
					GeoList list1, GeoList list2, boolean right) {
		AlgoHistogram algo = new AlgoHistogram(cons, label, list1, list2, right);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 *  Histogram[classList, dataList, useDensity, density]
	 */
	final public GeoNumeric Histogram(String label, 
					GeoList list1, GeoList list2, GeoBoolean useDensity, GeoNumeric density, boolean right) {
		AlgoHistogram algo = new AlgoHistogram(cons, label, null, list1, list2, useDensity, density, right);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * Histogram[isCumulative, classList, dataList, useDensity]
	 */
	final public GeoNumeric Histogram(String label, GeoBoolean isCumulative,
					GeoList list1, GeoList list2, GeoBoolean useDensity, boolean right) {
		AlgoHistogram algo = new AlgoHistogram(cons, label, isCumulative, list1, list2, useDensity, null, right);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	
	/** 
	  * Histogram[isCumulative, classList, dataList, useDensity, density]
	 */
	final public GeoNumeric Histogram(String label, GeoBoolean isCumulative,
					GeoList list1, GeoList list2, GeoBoolean useDensity, GeoNumeric density, boolean right) {
		AlgoHistogram algo = new AlgoHistogram(cons, label, isCumulative, list1, list2, useDensity, density, right);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	
	/** 
	 * FrequencyPolygon with list of class boundaries and list of heights
	 */
	final public GeoPolyLine FrequencyPolygon(String label, 
					GeoList list1, GeoList list2) {
		AlgoFrequencyPolygon algo = new AlgoFrequencyPolygon(cons, label, list1, list2);
		GeoPolyLine result = algo.getResult();
		return result;
	}
	
	/** 
	 * FrequencyPolygon with density scale factor  (no cumulative parameter)
	 */
	final public GeoPolyLine FrequencyPolygon(String label, 
					GeoList list1, GeoList list2, GeoBoolean useDensity, GeoNumeric density) {
		AlgoFrequencyPolygon algo = new AlgoFrequencyPolygon(cons, label, null, list1, list2, useDensity, density);
		GeoPolyLine result = algo.getResult();
		return result;
	}
	
	/** 
	 * FrequencyPolygon with density scale factor and cumulative parameter
	 */
	final public GeoPolyLine FrequencyPolygon(String label, GeoBoolean isCumulative,
					GeoList list1, GeoList list2, GeoBoolean useDensity) {
		AlgoFrequencyPolygon algo = new AlgoFrequencyPolygon(cons, label, isCumulative, list1, list2, useDensity, null);
		GeoPolyLine result = algo.getResult();
		return result;
	}
	
	
	/** 
	 * FrequencyPolygon with density scale factor and cumulative parameter
	 */
	final public GeoPolyLine FrequencyPolygon(String label, GeoBoolean isCumulative,
					GeoList list1, GeoList list2, GeoBoolean useDensity, GeoNumeric density) {
		AlgoFrequencyPolygon algo = new AlgoFrequencyPolygon(cons, label, isCumulative, list1, list2, useDensity, density);
		GeoPolyLine result = algo.getResult();
		return result;
	}
	
	
	
	
	/** 
	 * DotPlot
	 * G.Sturr 2010-8-10
	 */
	final public GeoList DotPlot(String label, GeoList list) {
		AlgoDotPlot algo = new AlgoDotPlot(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	
	/** 
	 * ResidualPlot
	 * G.Sturr 2011-2-5
	 */
	final public GeoList ResidualPlot(String label, GeoList list, GeoFunction function) {
		AlgoResidualPlot algo = new AlgoResidualPlot(cons, label, list, function);
		GeoList result = algo.getResult();
		return result;
	}
	
	
	/** 
	 * NormalQuantilePlot
	 * G.Sturr 2011-6-29
	 */
	final public GeoList NormalQuantilePlot(String label, GeoList list) {
		AlgoNormalQuantilePlot algo = new AlgoNormalQuantilePlot(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	
	
	/** 
	 * UpperSum of function f 
	 */
	final public GeoNumeric UpperSum(String label, GeoFunction f, 
					NumberValue a, NumberValue b, NumberValue n) {
		AlgoSumUpper algo = new AlgoSumUpper(cons, label, f, a, b, n);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * TrapezoidalSum of function f 
	 */
	final public GeoNumeric TrapezoidalSum(String label, GeoFunction f, 
					NumberValue a, NumberValue b, NumberValue n) {
		AlgoSumTrapezoidal algo = new AlgoSumTrapezoidal(cons, label, f, a, b, n);
		GeoNumeric sum = algo.getSum();
		return sum;
	}	

	/** 
	 * LowerSum of function f 
	 */
	final public GeoNumeric LowerSum(String label, GeoFunction f, 
					NumberValue a, NumberValue b, NumberValue n) {
		AlgoSumLower algo = new AlgoSumLower(cons, label, f, a, b, n);
		GeoNumeric sum = algo.getSum();
		return sum;
	}	
	/** 
	 * LeftSum of function f 
	 * Ulven 09.02.11
	 */
	final public GeoNumeric LeftSum(String label, GeoFunction f, 
					NumberValue a, NumberValue b, NumberValue n) {
		AlgoSumLeft algo = new AlgoSumLeft(cons, label, f, a, b, n);
		GeoNumeric sum = algo.getSum();
		return sum;
	}	
	
	/** 
	 * RectangleSum of function f 
	 * Ulven 09.02.11
	 */
	final public GeoNumeric RectangleSum(String label, GeoFunction f, 
					NumberValue a, NumberValue b, NumberValue n,NumberValue d) {
		AlgoSumRectangle algo = new AlgoSumRectangle(cons, label, f, a, b, n,d);
		GeoNumeric sum = algo.getSum();
		return sum;
	}	
	
	/**
	 * SumSquaredErrors[<List of Points>,<Function>]
	 * Hans-Petter Ulven
	 * 2010-02-22
	 */
	final public GeoNumeric SumSquaredErrors(String label, GeoList list, GeoFunctionable function) {
		AlgoSumSquaredErrors algo = new AlgoSumSquaredErrors(cons, label, list, function);
		GeoNumeric sse=algo.getsse();
		return sse;
	}	

	/**
	 * RSquare[<List of Points>,<Function>]
	 */
	final public GeoNumeric RSquare(String label, GeoList list, GeoFunctionable function) {
		AlgoRSquare algo = new AlgoRSquare(cons, label, list, function);
		GeoNumeric r2=algo.getRSquare();
		return r2;
	}	

	
	/**
	 * ResidualPlot[<List of Points>,<Function>]
	 */
	final public GeoList ResidualPlot(String label, GeoList list, GeoFunctionable function) {
		AlgoResidualPlot algo = new AlgoResidualPlot(cons, label, list, function);
		GeoList result = algo.getResult();
		return result;
	}	

	
	
	
	/** 
	 * unit vector of line g
	 */
	final public GeoVector UnitVector(String label, GeoLine g) {
		AlgoUnitVectorLine algo = new AlgoUnitVectorLine(cons, label, g);
		GeoVector v = algo.getVector();
		return v;
	}

	/** 
	 * unit vector of vector v
	 */
	final public GeoVector UnitVector(String label, GeoVector v) {
		AlgoUnitVectorVector algo = new AlgoUnitVectorVector(cons, label, v);
		GeoVector u = algo.getVector();
		return u;
	}

	/** 
	 * orthogonal vector of line g
	 */
	final public GeoVector OrthogonalVector(String label, GeoLine g) {
		AlgoOrthoVectorLine algo = new AlgoOrthoVectorLine(cons, label, g);
		GeoVector n = algo.getVector();
		return n;
	}

	/** 
	 * orthogonal vector of vector v
	 */
	final public GeoVector OrthogonalVector(String label, GeoVector v) {
		AlgoOrthoVectorVector algo = new AlgoOrthoVectorVector(cons, label, v);
		GeoVector n = algo.getVector();
		return n;
	}

	/** 
	 * unit orthogonal vector of line g
	 */
	final public GeoVector UnitOrthogonalVector(
		String label,
		GeoLine g) {
		AlgoUnitOrthoVectorLine algo = new AlgoUnitOrthoVectorLine(cons, label, g);
		GeoVector n = algo.getVector();
		return n;
	}

	/** 
	 * unit orthogonal vector of vector v
	 */
	final public GeoVector UnitOrthogonalVector(
		String label,
		GeoVector v) {
		AlgoUnitOrthoVectorVector algo =
			new AlgoUnitOrthoVectorVector(cons, label, v);
		GeoVector n = algo.getVector();
		return n;
	}

	/** 
	 * Length named label of vector v
	 */
	final public GeoNumeric Length(String label, GeoVec3D v) {
		AlgoLengthVector algo = new AlgoLengthVector(cons, label, v);
		GeoNumeric num = algo.getLength();
		return num;
	}

	/** 
	 * Distance named label between points P and Q
	 */
	final public GeoNumeric Distance(
		String label,
		GeoPointND P,
		GeoPointND Q) {
		AlgoDistancePoints algo = new AlgoDistancePoints(cons, label, P, Q);
		GeoNumeric num = algo.getDistance();
		return num;
	}

	/** 
	 * Distance named label between point P and line g
	 */
	final public GeoNumeric Distance(
		String label,
		GeoPoint P,
		GeoElement g) {
		AlgoDistancePointObject algo = new AlgoDistancePointObject(cons, label, P, g);
		GeoNumeric num = algo.getDistance();
		return num;
	}

	/** 
	 * Distance named label between line g and line h
	 */
	final public GeoNumeric Distance(
		String label,
		GeoLine g,
		GeoLine h) {
		AlgoDistanceLineLine algo = new AlgoDistanceLineLine(cons, label, g, h);
		GeoNumeric num = algo.getDistance();
		return num;
	}
	
	/** 
	 * Area named label of  P[0], ..., P[n]
	 */
	final public GeoNumeric Area(String label, GeoPoint [] P) {
		AlgoAreaPoints algo = new AlgoAreaPoints(cons, label, P);
		GeoNumeric num = algo.getArea();
		return num;
	}
	
	/** 
	 * Area named label of  conic
	 */
	final public GeoNumeric Area(String label, GeoConic c) {
		AlgoAreaConic algo = new AlgoAreaConic(cons, label, c);
		GeoNumeric num = algo.getArea();
		return num;
	}
	
	/** 
	 * Mod[a, b]
	 */
	final public GeoNumeric Mod(String label, NumberValue a, NumberValue b) {
		AlgoMod algo = new AlgoMod(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Div[a, b]
	 */
	final public GeoNumeric Div(String label, NumberValue a, NumberValue b) {
		AlgoDiv algo = new AlgoDiv(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Mod[a, b] Polynomial remainder
	 */
	final public GeoFunction Mod(String label, GeoFunction a, GeoFunction b) {
		AlgoPolynomialMod algo = new AlgoPolynomialMod(cons, label, a, b);
		GeoFunction f = algo.getResult();
		return f;
	}
	
	/** 
	 * Div[a, b] Polynomial Division
	 */
	final public GeoFunction Div(String label, GeoFunction a, GeoFunction b) {
		AlgoPolynomialDiv algo = new AlgoPolynomialDiv(cons, label, a, b);
		GeoFunction f = algo.getResult();
		return f;
	}
	
	/** 
	 * Min[a, b]
	 */
	final public GeoNumeric Min(String label, NumberValue a, NumberValue b) {
		AlgoMin algo = new AlgoMin(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Min[list]
	 */
	final public GeoNumeric Min(String label, GeoList list) {
		AlgoListMin algo = new AlgoListMin(cons, label, list);
		GeoNumeric num = algo.getMin();
		return num;
	}

	/**
	 *  Min[function,left,right]
	 *  Ulven 20.02.11
	 *  4.0: Numerical minimum of function in open interval <a,b>
	 */
	final public GeoPoint Min(String label, GeoFunction f, NumberValue a, NumberValue b){
		AlgoFunctionMin algo = new AlgoFunctionMin(cons, label, f, a, b);
		GeoPoint minpoint = algo.getPoint();
		return minpoint;
	}//Min(GeoFunction,a,b)
	
	/** 
	 * Max[a, b]
	 */
	final public GeoNumeric Max(String label, NumberValue a, NumberValue b) {
		AlgoMax algo = new AlgoMax(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Max[list]
	 */
	final public GeoNumeric Max(String label, GeoList list) {
		AlgoListMax algo = new AlgoListMax(cons, label, list);
		GeoNumeric num = algo.getMax();
		return num;
	}

	/**
	 *  Max[function,left,right]
	 *  Ulven 20.02.11
	 *  4.0: Numerical maximum of function in open interval <a,b>
	 */
	final public GeoPoint Max(String label, GeoFunction f, NumberValue a, NumberValue b){
		AlgoFunctionMax algo = new AlgoFunctionMax(cons, label, f, a, b);
		GeoPoint maxpoint = algo.getPoint();
		return maxpoint;
	}//Max(GeoFunction,a,b)
	
	/** 
	 * LCM[a, b]
	 * Michael Borcherds
	 */
	final public GeoNumeric LCM(String label, NumberValue a, NumberValue b) {
		AlgoLCM algo = new AlgoLCM(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * LCM[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric LCM(String label, GeoList list) {
		AlgoListLCM algo = new AlgoListLCM(cons, label, list);
		GeoNumeric num = algo.getLCM();
		return num;
	}
	
	/** 
	 * GCD[a, b]
	 * Michael Borcherds
	 */
	final public GeoNumeric GCD(String label, NumberValue a, NumberValue b) {
		AlgoGCD algo = new AlgoGCD(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * GCD[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric GCD(String label, GeoList list) {
		AlgoListGCD algo = new AlgoListGCD(cons, label, list);
		GeoNumeric num = algo.getGCD();
		return num;
	}
	
	/** 
	 * SigmaXY[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaXY(String label, GeoList list) {
		AlgoListSigmaXY algo = new AlgoListSigmaXY(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SigmaYY[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaYY(String label, GeoList list) {
		AlgoListSigmaYY algo = new AlgoListSigmaYY(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Covariance[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Covariance(String label, GeoList list) {
		AlgoListCovariance algo = new AlgoListCovariance(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	
	/** 
	 * Spearman[list]
	 * G. Sturr
	 */
	final public GeoNumeric Spearman(String label, GeoList list) {
		AlgoSpearman algo = new AlgoSpearman(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Spearman[list, list]
	 * G. Sturr
	 */
	final public GeoNumeric Spearman(String label, GeoList list, GeoList list2) {
		AlgoSpearman algo = new AlgoSpearman(cons, label, list, list2);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	
	
	
	
	/** 
	 * SXX[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SXX(String label, GeoList list) {
		GeoNumeric num;
		GeoElement geo = list.get(0);
		if (geo.isNumberValue())
		{  // list of numbers
			AlgoSXX algo = new AlgoSXX(cons, label, list);
			num = algo.getResult();
		}
		else
		{  // (probably) list of points
			AlgoListSXX algo = new AlgoListSXX(cons, label, list);			
			num = algo.getResult();
		}
		return num;
	}
	
	
	/** 
	 * SXY[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SXY(String label, GeoList list) {
		AlgoListSXY algo = new AlgoListSXY(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SYY[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SYY(String label, GeoList list) {
		AlgoListSYY algo = new AlgoListSYY(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * MeanX[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric MeanX(String label, GeoList list) {
		AlgoListMeanX algo = new AlgoListMeanX(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * MeanY[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric MeanY(String label, GeoList list) {
		AlgoListMeanY algo = new AlgoListMeanY(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SampleSDX[list]
	 * G. Sturr
	 */
	final public GeoNumeric SampleSDX(String label, GeoList list) {
		AlgoListSampleSDX algo = new AlgoListSampleSDX(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	
	/** 
	 * SampleSDY[list]
	 * G. Sturr
	 */
	final public GeoNumeric SampleSDY(String label, GeoList list) {
		AlgoListSampleSDY algo = new AlgoListSampleSDY(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric SDY(String label, GeoList list) {
		AlgoListSDY algo = new AlgoListSDY(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric SDX(String label, GeoList list) {
		AlgoListSDX algo = new AlgoListSDX(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	
	/** 
	 * PMCC[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric PMCC(String label, GeoList list) {
		AlgoListPMCC algo = new AlgoListPMCC(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SigmaXY[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaXY(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListSigmaXY algo = new AlgoDoubleListSigmaXY(cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SigmaXX[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaXX(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListSigmaXX algo = new AlgoDoubleListSigmaXX(cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SigmaYY[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaYY(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListSigmaYY algo = new AlgoDoubleListSigmaYY(cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Covariance[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Covariance(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListCovariance algo = new AlgoDoubleListCovariance(cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SXX[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SXX(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListSXX algo = new AlgoDoubleListSXX(cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SXY[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SXY(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListSXY algo = new AlgoDoubleListSXY(cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * PMCC[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric PMCC(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListPMCC algo = new AlgoDoubleListPMCC(cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * FitLineY[list of coords]
	 * Michael Borcherds
	 */
	final public GeoLine FitLineY(String label, GeoList list) {
		AlgoFitLineY algo = new AlgoFitLineY(cons, label, list);
		GeoLine line = algo.getFitLineY();
		return line;
	}
	
	/** 
	 * FitLineX[list of coords]
	 * Michael Borcherds
	 */
	final public GeoLine FitLineX(String label, GeoList list) {
		AlgoFitLineX algo = new AlgoFitLineX(cons, label, list);
		GeoLine line = algo.getFitLineX();
		return line;
	}
	
	final public GeoLocus Voronoi(String label, GeoList list) {
		AlgoVoronoi algo = new AlgoVoronoi(cons, label, list);
		GeoLocus ret = algo.getResult();
		return ret;
	}
	
	final public GeoLocus Hull(String label, GeoList list, GeoNumeric percent) {
		AlgoHull algo = new AlgoHull(cons, label, list, percent);
		GeoLocus ret = algo.getResult();
		return ret;
	}
	
	final public GeoLocus TravelingSalesman(String label, GeoList list) {
		AlgoTravelingSalesman algo = new AlgoTravelingSalesman(cons, label, list);
		GeoLocus ret = algo.getResult();
		return ret;
	}
	
	final public GeoLocus ConvexHull(String label, GeoList list) {
		AlgoConvexHull algo = new AlgoConvexHull(cons, label, list);
		GeoLocus ret = algo.getResult();
		return ret;
	}
	
	final public GeoLocus MinimumSpanningTree(String label, GeoList list) {
		AlgoMinimumSpanningTree algo = new AlgoMinimumSpanningTree(cons, label, list);
		GeoLocus ret = algo.getResult();
		return ret;
	}
	
	final public GeoLocus ShortestDistance(String label, GeoList list, GeoPointND start, GeoPointND end, GeoBoolean weighted) {
		AlgoShortestDistance algo = new AlgoShortestDistance(cons, label, list, start, end, weighted);
		GeoLocus ret = algo.getResult();
		return ret;
	}
	
	final public GeoLocus DelauneyTriangulation(String label, GeoList list) {
		AlgoDelauneyTriangulation algo = new AlgoDelauneyTriangulation(cons, label, list);
		GeoLocus ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * FitPoly[list of coords,degree]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitPoly(String label, GeoList list, NumberValue degree) {
		AlgoFitPoly algo = new AlgoFitPoly(cons, label, list, degree);
		GeoFunction function = algo.getFitPoly();
		return function;
	}

	/** 
	 * FitExp[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitExp(String label, GeoList list) {
		AlgoFitExp algo = new AlgoFitExp(cons, label, list);
		GeoFunction function = algo.getFitExp();
		return function;
	}
   
	/** 
	 * FitLog[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitLog(String label, GeoList list) {
		AlgoFitLog algo = new AlgoFitLog(cons, label, list);
		GeoFunction function = algo.getFitLog();
		return function;
	}
	/** 
	 * FitPow[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitPow(String label, GeoList list) {
		AlgoFitPow algo = new AlgoFitPow(cons, label, list);
		GeoFunction function = algo.getFitPow();
		return function;
	}

	/** 
	 * FitSin[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitSin(String label, GeoList list) {
		AlgoFitSin algo = new AlgoFitSin(cons, label, list);
		GeoFunction function = algo.getFitSin();
		return function;
	}
	
	/** 
	 * FitLogistic[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitLogistic(String label, GeoList list) {
		AlgoFitLogistic algo = new AlgoFitLogistic(cons, label, list);
		GeoFunction function = algo.getFitLogistic();
		return function;
	}	
	
	/** 
	 * Fit[list of points,list of functions]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction Fit(String label, GeoList ptslist,GeoList funclist) {
		AlgoFit algo = new AlgoFit(cons, label, ptslist,funclist);
		GeoFunction function = algo.getFit();
		return function;
	}	
	
	/** 
	 * Fit[list of points,function]
	 * NonLinear case, one function with glider parameters
	 * Hans-Petter Ulven
	 */
	final public GeoFunction Fit(String label, GeoList ptslist,GeoFunction function) {
		AlgoFitNL algo = new AlgoFitNL(cons, label, ptslist,function);
		GeoFunction geofunction = algo.getFitNL();
		return geofunction;
	}	

	/**
	 * 'FitGrowth[<List of Points>]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitGrowth(String label, GeoList list) {
		AlgoFitGrowth algo = new AlgoFitGrowth(cons, label, list);
		GeoFunction function=algo.getFitGrowth();
		return function;
	}

	/** 
	 * Binomial[n,r]
	 * Michael Borcherds
	 */
	final public GeoNumeric Binomial(String label, NumberValue a, NumberValue b) {
		AlgoBinomial algo = new AlgoBinomial(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * RandomNormal[mean,variance]
	 * Michael Borcherds
	 */
	final public GeoNumeric RandomNormal(String label, NumberValue a, NumberValue b) {
		AlgoRandomNormal algo = new AlgoRandomNormal(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Random[max,min]
	 * Michael Borcherds
	 */
	final public GeoNumeric Random(String label, NumberValue a, NumberValue b) {
		AlgoRandom algo = new AlgoRandom(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * RandomUniform[max,min]
	 * Michael Borcherds
	 */
	final public GeoNumeric RandomUniform(String label, NumberValue a, NumberValue b) {
		AlgoRandomUniform algo = new AlgoRandomUniform(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * RandomBinomial[n,p]
	 * Michael Borcherds
	 */
	final public GeoNumeric RandomBinomial(String label, NumberValue a, NumberValue b) {
		AlgoRandomBinomial algo = new AlgoRandomBinomial(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * RandomPoisson[lambda]
	 * Michael Borcherds
	 */
	final public GeoNumeric RandomPoisson(String label, NumberValue a) {
		AlgoRandomPoisson algo = new AlgoRandomPoisson(cons, label, a);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	
	/** 
	 * InverseNormal[mean,variance,x]
	 * Michael Borcherds
	 */
	final public GeoNumeric InverseNormal(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseNormal algo = new AlgoInverseNormal(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Normal[mean,variance,x]
	 * Michael Borcherds
	 */
	final public GeoNumeric Normal(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoNormal algo = new AlgoNormal(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * TDistribution[degrees of freedom,x]
	 * Michael Borcherds
	 */
	final public GeoNumeric TDistribution(String label, NumberValue a, NumberValue b) {
		AlgoTDistribution algo = new AlgoTDistribution(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseTDistribution(String label, NumberValue a, NumberValue b) {
		AlgoInverseTDistribution algo = new AlgoInverseTDistribution(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	final public GeoNumeric ChiSquared(String label, NumberValue a, NumberValue b) {
		AlgoChiSquared algo = new AlgoChiSquared(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseChiSquared(String label, NumberValue a, NumberValue b) {
		AlgoInverseChiSquared algo = new AlgoInverseChiSquared(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	final public GeoNumeric Exponential(String label, NumberValue a, NumberValue b) {
		AlgoExponential algo = new AlgoExponential(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseExponential(String label, NumberValue a, NumberValue b) {
		AlgoInverseExponential algo = new AlgoInverseExponential(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric FDistribution(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoFDistribution algo = new AlgoFDistribution(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseFDistribution(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseFDistribution algo = new AlgoInverseFDistribution(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric Gamma(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoGamma algo = new AlgoGamma(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseGamma(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseGamma algo = new AlgoInverseGamma(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric Cauchy(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoCauchy algo = new AlgoCauchy(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseCauchy(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseCauchy algo = new AlgoInverseCauchy(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric Weibull(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoWeibull algo = new AlgoWeibull(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseWeibull(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseWeibull algo = new AlgoInverseWeibull(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric Zipf(String label, NumberValue a, NumberValue b, NumberValue c, GeoBoolean isCumulative) {
		AlgoZipf algo = new AlgoZipf(cons, label, a, b, c, isCumulative);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric Zipf(String label, NumberValue a, NumberValue b) {
		AlgoZipfBarChart algo = new AlgoZipfBarChart(cons, label, a, b);
		GeoNumeric num = algo.getSum();
		return num;
	}
	
	final public GeoNumeric Zipf(String label, NumberValue a, NumberValue b, GeoBoolean cumulative) {
		AlgoZipfBarChart algo = new AlgoZipfBarChart(cons, label, a, b, cumulative);
		GeoNumeric num = algo.getSum();
		return num;
	}
	
	final public GeoNumeric InverseZipf(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseZipf algo = new AlgoInverseZipf(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** Pascal[] probability */
	final public GeoNumeric Pascal(String label, NumberValue a, NumberValue b, NumberValue c, GeoBoolean isCumulative) {
		AlgoPascal algo = new AlgoPascal(cons, label, a, b, c, isCumulative);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** Pascal[] bar chart */
	final public GeoNumeric Pascal(String label, NumberValue a, NumberValue b) {
		AlgoPascalBarChart algo = new AlgoPascalBarChart(cons, label, a, b);
		GeoNumeric num = algo.getSum();
		return num;
	}
	/** Pascal[] bar chart with cumulative option */
	final public GeoNumeric Pascal(String label, NumberValue a, NumberValue b, GeoBoolean isCumulative) {
		AlgoPascalBarChart algo = new AlgoPascalBarChart(cons, label, a, b, isCumulative);
		GeoNumeric num = algo.getSum();
		return num;
	}
	
	
	final public GeoNumeric InversePascal(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInversePascal algo = new AlgoInversePascal(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** Poisson[] probability */
	final public GeoNumeric Poisson(String label, NumberValue a, NumberValue b, GeoBoolean isCumulative) {
		AlgoPoisson algo = new AlgoPoisson(cons, label, a, b, isCumulative);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** Poisson[] bar chart */
	final public GeoNumeric Poisson(String label, NumberValue a) {
		AlgoPoissonBarChart algo = new AlgoPoissonBarChart(cons, label, a);
		GeoNumeric num = algo.getSum();
		return num;
	}
	/** Poisson[] bar chart with cumulative option */
	final public GeoNumeric Poisson(String label, NumberValue a, GeoBoolean isCumulative) {
		AlgoPoissonBarChart algo = new AlgoPoissonBarChart(cons, label, a, isCumulative);
		GeoNumeric num = algo.getSum();
		return num;
	}
	
	
	
	final public GeoNumeric InversePoisson(String label, NumberValue a, NumberValue b) {
		AlgoInversePoisson algo = new AlgoInversePoisson(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** HyperGeometric[] probability */
	final public GeoNumeric HyperGeometric(String label, NumberValue a, NumberValue b, NumberValue c, NumberValue d,
			GeoBoolean isCumulative) {
		AlgoHyperGeometric algo = new AlgoHyperGeometric(cons, label, a, b, c, d, isCumulative);
		GeoNumeric num = algo.getResult();
		return num;
	}
	/** HyperGeometric[] bar chart */
	final public GeoNumeric HyperGeometric(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoHyperGeometricBarChart algo = new AlgoHyperGeometricBarChart(cons, label, a, b, c);
		GeoNumeric num = algo.getSum();
		return num;
	}
	/** HyperGeometric[] bar chart with cumulative option */
	final public GeoNumeric HyperGeometric(String label, NumberValue a, NumberValue b, NumberValue c, GeoBoolean isCumulative) {
		AlgoHyperGeometricBarChart algo = new AlgoHyperGeometricBarChart(cons, label, a, b, c, isCumulative);
		GeoNumeric num = algo.getSum();
		return num;
	}
	
	
	
	
	
	final public GeoNumeric InverseHyperGeometric(String label, NumberValue a, NumberValue b, NumberValue c, NumberValue d) {
		AlgoInverseHyperGeometric algo = new AlgoInverseHyperGeometric(cons, label, a, b, c, d);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** Binomial[] probability */
	final public GeoNumeric BinomialDist(String label, NumberValue a, NumberValue b, NumberValue c, GeoBoolean isCumulative) {
		AlgoBinomialDist algo = new AlgoBinomialDist(cons, label, a, b, c, isCumulative);
		GeoNumeric num = algo.getResult();
		return num;
	}
	


	public GeoNumeric Bernoulli(String label, NumberValue probability,
			GeoBoolean cumulative) {
		AlgoBernoulliBarChart algo = new AlgoBernoulliBarChart(cons, label, probability, cumulative);
		GeoNumeric num = algo.getSum();
		return num;
	}
	
	/** Binomial[] bar chart */
	final public GeoNumeric BinomialDist(String label, NumberValue a, NumberValue b) {
		AlgoBinomialDistBarChart algo = new AlgoBinomialDistBarChart(cons, label, a, b);
		GeoNumeric num = algo.getSum();
		return num;
	}
	/** Binomial[] bar chart with cumulative option */
	final public GeoNumeric BinomialDist(String label, NumberValue a, NumberValue b, GeoBoolean isCumulative) {
		AlgoBinomialDistBarChart algo = new AlgoBinomialDistBarChart(cons, label, a, b, isCumulative);
		GeoNumeric num = algo.getSum();
		return num;
	}
	
	
	final public GeoNumeric InverseBinomial(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseBinomial algo = new AlgoInverseBinomial(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	
	
	/** ANOVATest[]  */
	final public GeoList ANOVATest(String label, GeoList dataArrayList) {
		AlgoANOVA algo = new AlgoANOVA(cons, label, dataArrayList);
		GeoList result = algo.getResult();
		return result;
	}
	
	/** TTest[] with sample data */
	final public GeoList TTest(String label, GeoList sampleList, GeoNumeric hypMean, GeoText tail) {
		AlgoTTest algo = new AlgoTTest(cons, label, sampleList, hypMean, tail);
		GeoList result = algo.getResult();
		return result;
	}
	
	/** TTest[] with sample statistics */
	final public GeoList TTest(String label, GeoNumeric mean, GeoNumeric sd, GeoNumeric n, GeoNumeric hypMean, GeoText tail) {
		AlgoTTest algo = new AlgoTTest(cons, label, mean, sd, n, hypMean, tail);
		GeoList result = algo.getResult();
		return result;
	}
	

	/** TTestPaired[] */
	final public GeoList TTestPaired(String label, GeoList sampleList1, GeoList sampleList2, GeoText tail) {
		AlgoTTestPaired algo = new AlgoTTestPaired(cons, label, sampleList1, sampleList2, tail);
		GeoList result = algo.getResult();
		return result;
	}
	
	/** TTest2[] with sample data */
	final public GeoList TTest2(String label, GeoList sampleList1, GeoList sampleList2, GeoText tail, GeoBoolean pooled) {
		AlgoTTest2 algo = new AlgoTTest2(cons, label, sampleList1, sampleList2, tail, pooled);
		GeoList result = algo.getResult();
		return result;
	}
	
	/** TTest2[] with sample statistics */
	final public GeoList TTest2(String label, GeoNumeric mean1, GeoNumeric sd1, GeoNumeric n1, GeoNumeric mean2, 
			GeoNumeric sd2, GeoNumeric n2, GeoText tail, GeoBoolean pooled) {
		AlgoTTest2 algo = new AlgoTTest2(cons, label, mean1, mean2, sd1, sd2, n1, n2, tail, pooled);
		GeoList result = algo.getResult();
		return result;
	}
	
	
	/** TMeanEstimate[] with sample data */
	final public GeoList TMeanEstimate(String label, GeoList sampleList, GeoNumeric level) {
		AlgoTMeanEstimate algo = new AlgoTMeanEstimate(cons, label, sampleList, level);
		GeoList resultList = algo.getResult();
		return resultList;
	}
	
	/** TMeanEstimate[] with sample statistics */
	final public GeoList TMeanEstimate(String label, GeoNumeric mean, GeoNumeric sd, GeoNumeric n, GeoNumeric level) {
		AlgoTMeanEstimate algo = new AlgoTMeanEstimate(cons, label, mean, sd, n, level);
		GeoList resultList = algo.getResult();
		return resultList;
	}
	
	/** TMean2Estimate[] with sample data */
	final public GeoList TMean2Estimate(String label, GeoList sampleList1, GeoList sampleList2, GeoNumeric level, GeoBoolean pooled) {
		AlgoTMean2Estimate algo = new AlgoTMean2Estimate(cons, label, sampleList1, sampleList2, level, pooled);
		GeoList resultList = algo.getResult();
		return resultList;
	}
	
	/** TMean2Estimate[] with sample statistics */
	final public GeoList TMean2Estimate(String label, GeoNumeric mean1, GeoNumeric sd1, GeoNumeric n1, 
			GeoNumeric mean2, GeoNumeric sd2, GeoNumeric n2, GeoNumeric level, GeoBoolean pooled) {
		AlgoTMean2Estimate algo = new AlgoTMean2Estimate(cons, label, mean1, sd1, n1, mean2, sd2, n2,level, pooled);
		GeoList resultList = algo.getResult();
		return resultList;
	}
	
	
	
	/** 
	 * Sort[list]
	 * Michael Borcherds
	 */
	final public GeoList Sort(String label, GeoList list) {
		AlgoSort algo = new AlgoSort(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * OrdinalRank[list]
	 * Michael Borcherds
	 */
	final public GeoList OrdinalRank(String label, GeoList list) {
		AlgoOrdinalRank algo = new AlgoOrdinalRank(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * TiedRank[list]
	 */
	final public GeoList TiedRank(String label, GeoList list) {
		AlgoTiedRank algo = new AlgoTiedRank(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Percentile[list, value]
	 * G. Sturr
	 */
	final public GeoNumeric Percentile(String label, GeoList list, GeoNumeric value) {
		AlgoPercentile algo = new AlgoPercentile(cons, label, list, value);
		GeoNumeric result = algo.getResult();
		return result;
	}
	
	/** 
	 * Shuffle[list]
	 * Michael Borcherds
	 */
	final public GeoList Shuffle(String label, GeoList list) {
		AlgoShuffle algo = new AlgoShuffle(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * PointList[list]
	 * Michael Borcherds
	 */
	final public GeoList PointList(String label, GeoList list) {
		AlgoPointList algo = new AlgoPointList(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * RootList[list]
	 * Michael Borcherds
	 */
	final public GeoList RootList(String label, GeoList list) {
		AlgoRootList algo = new AlgoRootList(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * First[list,n]
	 * Michael Borcherds
	 */
	final public GeoList First(String label, GeoList list, GeoNumeric n) {
		AlgoFirst algo = new AlgoFirst(cons, label, list, n);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * FirstLocus[locus,n]
	 * Michael Borcherds
	 */
	final public GeoList FirstLocus(String label, GeoLocus locus, GeoNumeric n) {
		AlgoFirstLocus algo = new AlgoFirstLocus(cons, label, locus, n);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * First[string,n]
	 * Michael Borcherds
	 */
	final public GeoText First(String label, GeoText list, GeoNumeric n) {
		AlgoFirstString algo = new AlgoFirstString(cons, label, list, n);
		GeoText list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Last[string,n]
	 * Michael Borcherds
	 */
	final public GeoText Last(String label, GeoText list, GeoNumeric n) {
		AlgoLastString algo = new AlgoLastString(cons, label, list, n);
		GeoText list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * First[string,n]
	 * Michael Borcherds
	 */
	final public GeoText Take(String label, GeoText list, GeoNumeric m, GeoNumeric n) {
		AlgoTakeString algo = new AlgoTakeString(cons, label, list, m, n);
		GeoText list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Last[list,n]
	 * Michael Borcherds
	 */
	final public GeoList Last(String label, GeoList list, GeoNumeric n) {
		AlgoLast algo = new AlgoLast(cons, label, list, n);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Take[list,m,n]
	 * Michael Borcherds
	 */
	final public GeoList Take(String label, GeoList list, GeoNumeric m, GeoNumeric n) {
		AlgoTake algo = new AlgoTake(cons, label, list, m, n);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Append[list,object]
	 * Michael Borcherds
	 */
	final public GeoList Append(String label, GeoList list, GeoElement geo) {
		AlgoAppend algo = new AlgoAppend(cons, label, list, geo);
		GeoList list2 = algo.getResult();
		return list2;
	}
	/** 
	 * IndexOf[text,text]
	 */
	final public GeoNumeric IndexOf(String label, GeoText needle, GeoText haystack) {
		AlgoIndexOf algo = new AlgoIndexOf(cons, label, needle, haystack);
		GeoNumeric index = algo.getResult();
		return index;
	}
	/** 
	 * IndexOf[text,text,start]
	 */
	final public GeoNumeric IndexOf(String label, GeoText needle, GeoText haystack,NumberValue start) {
		AlgoIndexOf algo = new AlgoIndexOf(cons, label, needle, haystack,start);
		GeoNumeric index = algo.getResult();
		return index;
	}
	/** 
	 * IndexOf[object,list]
	 */
	final public GeoNumeric IndexOf(String label, GeoElement geo, GeoList list) {
		AlgoIndexOf algo = new AlgoIndexOf(cons, label, geo, list);
		GeoNumeric index = algo.getResult();
		return index;
	}
	/** 
	 * IndexOf[object,list,start]
	 */
	final public GeoNumeric IndexOf(String label, GeoElement geo, GeoList list,NumberValue nv) {
		AlgoIndexOf algo = new AlgoIndexOf(cons, label, geo, list,nv);
		GeoNumeric index = algo.getResult();
		return index;
	}
	
	/** 
	 * Append[object,list]
	 * Michael Borcherds
	 */
	final public GeoList Append(String label, GeoElement geo, GeoList list) {
		AlgoAppend algo = new AlgoAppend(cons, label, geo, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Join[list,list]
	 * Michael Borcherds
	 */
	final public GeoList Join(String label, GeoList list) {
		AlgoJoin algo = new AlgoJoin(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Union[list,list]
	 * Michael Borcherds
	 */
	final public GeoList Union(String label, GeoList list, GeoList list1) {
		AlgoUnion algo = new AlgoUnion(cons, label, list, list1);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	
	/** 
	 * Intersection[list,list]
	 * Michael Borcherds
	 */
	final public GeoList Intersection(String label, GeoList list, GeoList list1) {
		AlgoIntersection algo = new AlgoIntersection(cons, label, list, list1);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Insert[list,list,n]
	 * Michael Borcherds
	 */
	final public GeoList Insert(String label, GeoElement geo, GeoList list, GeoNumeric n) {
		AlgoInsert algo = new AlgoInsert(cons, label, geo, list, n);
		GeoList list2 = algo.getResult();
		return list2;
	}
	

	/** 
	 * RemoveUndefined[list]
	 * Michael Borcherds
	 */
	final public GeoList RemoveUndefined(String label, GeoList list) {
		AlgoRemoveUndefined algo = new AlgoRemoveUndefined(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Keep[boolean condition, list]
	 * Michael Borcherds
	 */
	final public GeoList KeepIf(String label, GeoFunction boolFun, GeoList list) {
		AlgoKeepIf algo = new AlgoKeepIf(cons, label, boolFun, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Defined[object]
	 * Michael Borcherds
	 */
	final public GeoBoolean Defined(String label, GeoElement geo) {
		AlgoDefined algo = new AlgoDefined(cons, label, geo);
		GeoBoolean result = algo.getResult();
		return result;
	}
	
	/** 
	 * IsInteger[number]
	 * Michael Borcherds
	 */
	final public GeoBoolean IsInteger(String label, GeoNumeric geo) {
		AlgoIsInteger algo = new AlgoIsInteger(cons, label, geo);
		GeoBoolean result = algo.getResult();
		return result;
	}
	
	/** 
	 * Mode[list]
	 * Michael Borcherds
	 */
	final public GeoList Mode(String label, GeoList list) {
		AlgoMode algo = new AlgoMode(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * PrimeFactors[list]
	 * Michael Borcherds
	 */
	final public GeoList PrimeFactors(String label, NumberValue num) {
		AlgoPrimeFactors algo = new AlgoPrimeFactors(cons, label, num);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	final public GeoList PrimeFactorisation(String label, NumberValue num) {
		AlgoPrimeFactorization algo = new AlgoPrimeFactorization(cons, label, num);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Invert[matrix]
	 * Michael Borcherds
	 */
	final public GeoList Invert(String label, GeoList list) {
		AlgoInvert algo = new AlgoInvert(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Transpose[matrix]
	 * Michael Borcherds
	 */
	final public GeoList Transpose(String label, GeoList list) {
		AlgoTranspose algo = new AlgoTranspose(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Transpose[matrix]
	 * Michael Borcherds
	 */
	final public GeoList ReducedRowEchelonForm(String label, GeoList list) {
		AlgoReducedRowEchelonForm algo = new AlgoReducedRowEchelonForm(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Transpose[matrix]
	 * Michael Borcherds
	 */
	final public GeoNumeric Determinant(String label, GeoList list) {
		AlgoDeterminant algo = new AlgoDeterminant(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Reverse[list]
	 * Michael Borcherds
	 */
	final public GeoList Reverse(String label, GeoList list) {
		AlgoReverse algo = new AlgoReverse(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Product[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Product(String label, GeoList list) {
		AlgoProduct algo = new AlgoProduct(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Product[list,n]
	 * Zbynek Konecny
	 */
	final public GeoNumeric Product(String label, GeoList list,GeoNumeric n) {
		AlgoProduct algo = new AlgoProduct(cons, label, list,n);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Sum[list]
	 * Michael Borcherds
	 */
	final public GeoElement Sum(String label, GeoList list) {
		AlgoSum algo = new AlgoSum(cons, label, list);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list,n]
	 * Michael Borcherds
	 */
	final public GeoElement Sum(String label, GeoList list, GeoNumeric n) {
		AlgoSum algo = new AlgoSum(cons, label, list, n);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of functions]
	 * Michael Borcherds
	 */
	final public GeoElement SumFunctions(String label, GeoList list) {
		AlgoSumFunctions algo = new AlgoSumFunctions(cons, label, list);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of functions,n]
	 * Michael Borcherds
	 */
	final public GeoElement SumFunctions(String label, GeoList list, GeoNumeric num) {
		AlgoSumFunctions algo = new AlgoSumFunctions(cons, label, list, num);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of points]
	 * Michael Borcherds
	 */
	final public GeoElement SumPoints(String label, GeoList list) {
		AlgoSumPoints algo = new AlgoSumPoints(cons, label, list);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of points,n]
	 * Michael Borcherds
	 */
	final public GeoElement SumPoints(String label, GeoList list, GeoNumeric num) {
		AlgoSumPoints algo = new AlgoSumPoints(cons, label, list, num);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of points]
	 * Michael Borcherds
	 */
	final public GeoElement SumText(String label, GeoList list) {
		AlgoSumText algo = new AlgoSumText(cons, label, list);
		GeoText ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of text,n]
	 * Michael Borcherds
	 */
	final public GeoElement SumText(String label, GeoList list, GeoNumeric num) {
		AlgoSumText algo = new AlgoSumText(cons, label, list, num);
		GeoText ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sample[list,n]
	 * Michael Borcherds
	 */
	final public GeoElement Sample(String label, GeoList list, NumberValue n) {
		AlgoSample algo = new AlgoSample(cons, label, list, n, null);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sample[list,n, withReplacement]
	 * Michael Borcherds
	 */
	final public GeoElement Sample(String label, GeoList list, NumberValue n, GeoBoolean withReplacement) {
		AlgoSample algo = new AlgoSample(cons, label, list, n, withReplacement);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Table[list]
	 * Michael Borcherds
	 */
	final public GeoText TableText(String label, GeoList list, GeoText args) {
		AlgoTableText algo = new AlgoTableText(cons, label, list, args);
		GeoText text = algo.getResult();
		return text;
	}
	
	/** 
	 * StemPlot[list]
	 * Michael Borcherds
	 */
	final public GeoText StemPlot(String label, GeoList list) {
		AlgoStemPlot algo = new AlgoStemPlot(cons, label, list, null);
		GeoText text = algo.getResult();
		return text;
	}
	
	/** 
	 * StemPlot[list, number]
	 * Michael Borcherds
	 */
	final public GeoText StemPlot(String label, GeoList list, GeoNumeric num) {
		AlgoStemPlot algo = new AlgoStemPlot(cons, label, list, num);
		GeoText text = algo.getResult();
		return text;
	}
	
	
	/** 
	 * Frequency[dataList]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label, GeoList dataList) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, null, null, dataList);
		GeoList list = algo.getResult();
		return list;
	}
	
	/** 
	 * Frequency[isCumulative, dataList]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label,GeoBoolean isCumulative, GeoList dataList) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, isCumulative, null, dataList);
		GeoList list = algo.getResult();
		return list;
	}
	
	
	/** 
	 * Frequency[classList, dataList]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label, GeoList classList, GeoList dataList ) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, null, classList, dataList);
		GeoList list = algo.getResult();
		return list;
	}
	
	
	/** 
	 * Frequency[classList, dataList, useDensity]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, null, classList, dataList, useDensity, null);
		GeoList list = algo.getResult();
		return list;
	}

	/** 
	 * Frequency[classList, dataList, useDensity, scaleFactor]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity, GeoNumeric scaleFactor) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, null, classList, dataList, useDensity, scaleFactor);
		GeoList list = algo.getResult();
		return list;
	}
	
	
	/** 
	 * Frequency[isCumulative, classList, dataList]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label, GeoBoolean isCumulative,  GeoList classList, GeoList dataList) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, isCumulative, classList, dataList, null, null);
		GeoList list = algo.getResult();
		return list;
	}
	

	
	/** 
	 * Frequency[isCumulative, classList, dataList, useDensity]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label, GeoBoolean isCumulative,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, isCumulative, classList, dataList, useDensity, null);
		GeoList list = algo.getResult();
		return list;
	}
	
	
	
	/** 
	 * Frequency[isCumulative, classList, dataList, useDensity, scaleFactor]
	 * G. Sturr
	 */
	final public GeoList Frequency(String label, GeoBoolean isCumulative,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity, GeoNumeric scaleFactor) {
		AlgoFrequency algo = new AlgoFrequency(cons, label, isCumulative, classList, dataList, useDensity, scaleFactor);
		GeoList list = algo.getResult();
		return list;
	}
	
	
	/** 
	 * FrequencyTable[dataList]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label, GeoList dataList) {		
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label, null, null, dataList);
		GeoText table = algo.getResult();
		return table;
	}
	
	/** 
	 * FrequencyTable[isCumulative, dataList]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label,GeoBoolean isCumulative, GeoList dataList) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label, isCumulative, null, dataList);
		GeoText table = algo.getResult();
		return table;
	}
	
	
	/** 
	 * FrequencyTable[classList, dataList]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label, GeoList classList, GeoList dataList ) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label, null, classList, dataList);
		GeoText table = algo.getResult();
		return table;
	}
	
	
	/** 
	 * FrequencyTable[classList, dataList, useDensity]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label, null, classList, dataList, useDensity, null);
		GeoText table = algo.getResult();
		return table;
	}

	/** 
	 * FrequencyTable[classList, dataList, useDensity, scaleFactor]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity, GeoNumeric scaleFactor) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label, null, classList, dataList, useDensity, scaleFactor);
		GeoText table = algo.getResult();
		return table;
	}
	
	
	/** 
	 * FrequencyTable[isCumulative, classList, dataList]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label, GeoBoolean isCumulative,  GeoList classList, GeoList dataList) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label, isCumulative, classList, dataList, null, null);
		GeoText table = algo.getResult();
		return table;
	}
	

	
	/** 
	 * FrequencyTable[isCumulative, classList, dataList, useDensity]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label, GeoBoolean isCumulative,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label, isCumulative, classList, dataList, useDensity, null);
		GeoText table = algo.getResult();
		return table;
	}
	
	
	
	/** 
	 * FrequencyTable[isCumulative, classList, dataList, useDensity, scaleFactor]
	 * Zbynek Konecny
	 */
	final public GeoText FrequencyTable(String label, GeoBoolean isCumulative,  GeoList classList, GeoList dataList,
			GeoBoolean useDensity, GeoNumeric scaleFactor) {
		AlgoFrequencyTable algo = new AlgoFrequencyTable(cons, label, isCumulative, classList, dataList, useDensity, scaleFactor);
		GeoText table = algo.getResult();
		return table;
	}
	
		
	/** 
	 * Unique[dataList]
	 * G. Sturr
	 */
	final public GeoList Unique(String label, GeoList dataList) {
		AlgoUnique algo = new AlgoUnique(cons, label, dataList);
		GeoList list = algo.getResult();
		return list;
	}
	
	

	/** 
	 * Classes[dataList, number of classes]
	 * G. Sturr
	 */
	final public GeoList Classes(String label, GeoList dataList, GeoNumeric numClasses) {
		AlgoClasses algo = new AlgoClasses(cons, label, dataList, null, null, numClasses);
		GeoList list = algo.getResult();
		return list;
	}
	
	
	/** 
	 * Classes[dataList, start, width]
	 * G. Sturr
	 */
	final public GeoList Classes(String label, GeoList dataList, GeoNumeric start, GeoNumeric width ) {
		AlgoClasses algo = new AlgoClasses(cons, label, dataList, start, width, null);
		GeoList list = algo.getResult();
		return list;
	}
	
	
	
	/** 
	 * ToFraction[number]
	 * Michael Borcherds
	 */
	final public GeoText FractionText(String label, GeoNumeric num) {
		AlgoFractionText algo = new AlgoFractionText(cons, label, num);
		GeoText text = algo.getResult();
		return text;
	}
	
	/** 
	 * SurdText[number]
	 * Kai Chung Tam
	 */
	final public GeoText SurdText(String label, GeoNumeric num) {
		AlgoSurdText algo = new AlgoSurdText(cons, label, num);
		GeoText text = algo.getResult();
		return text;
	}
	
	
	/** 
	 * SurdText[Point]
	 */
	final public GeoText SurdText(String label, GeoPoint p) {
		AlgoSurdTextPoint algo = new AlgoSurdTextPoint(cons, label, p);
		GeoText text = algo.getResult();
		return text;
	}
	
	
	/** 
	 * Mean[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Mean(String label, GeoList list) {
		AlgoMean algo = new AlgoMean(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoText VerticalText(String label, GeoText args) {
		AlgoVerticalText algo = new AlgoVerticalText(cons, label, args);
		GeoText text = algo.getResult();
		return text;
	}
	
	final public GeoText RotateText(String label, GeoText args, GeoNumeric angle) {
		AlgoRotateText algo = new AlgoRotateText(cons, label, args, angle);
		GeoText text = algo.getResult();
		return text;
	}
	
	
	/** 
	 * Variance[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Variance(String label, GeoList list) {
		AlgoVariance algo = new AlgoVariance(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SampleVariance[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SampleVariance(String label, GeoList list) {
		AlgoSampleVariance algo = new AlgoSampleVariance(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SD[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric StandardDeviation(String label, GeoList list) {
		AlgoStandardDeviation algo = new AlgoStandardDeviation(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SampleSD[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SampleStandardDeviation(String label, GeoList list) {
		AlgoSampleStandardDeviation algo = new AlgoSampleStandardDeviation(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SigmaXX[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaXX(String label, GeoList list) {
		GeoNumeric num;
		GeoElement geo = list.get(0);
		if (geo.isNumberValue())
		{  // list of numbers
			AlgoSigmaXX algo = new AlgoSigmaXX(cons, label, list);
			num = algo.getResult();
		}
		else
		{  // (probably) list of points
			AlgoListSigmaXX algo = new AlgoListSigmaXX(cons, label, list);			
			num = algo.getResult();
		}
		return num;
	}
	
	/** 
	 * Median[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Median(String label, GeoList list) {
		AlgoMedian algo = new AlgoMedian(cons, label, list);
		GeoNumeric num = algo.getMedian();
		return num;
	}
	
	/** 
	 * Q1[list] lower quartile
	 * Michael Borcherds
	 */
	final public GeoNumeric Q1(String label, GeoList list) {
		AlgoQ1 algo = new AlgoQ1(cons, label, list);
		GeoNumeric num = algo.getQ1();
		return num;
	}
	
	/** 
	 * Q3[list] upper quartile
	 * Michael Borcherds
	 */
	final public GeoNumeric Q3(String label, GeoList list) {
		AlgoQ3 algo = new AlgoQ3(cons, label, list);
		GeoNumeric num = algo.getQ3();
		return num;
	}
	
	/** 
	 * GeometricMean[list]
	 * G. Sturr
	 */
	final public GeoNumeric GeometricMean(String label, GeoList list) {
		AlgoGeometricMean algo = new AlgoGeometricMean(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * HarmonicMean[list]
	 * G. Sturr
	 */
	final public GeoNumeric HarmonicMean(String label, GeoList list) {
		AlgoHarmonicMean algo = new AlgoHarmonicMean(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/**
	 * 
	 * @param label
	 * @param list
	 * @return
	 */
	final public GeoNumeric RootMeanSquare(String label, GeoList list) {
		AlgoRootMeanSquare algo = new AlgoRootMeanSquare(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	
	/** 
	 * Iteration[ f(x), x0, n ] 
	 */
	final public GeoNumeric Iteration(String label, GeoFunction f, NumberValue start,
			NumberValue n) {
		AlgoIteration algo = new AlgoIteration(cons, label, f, start, n);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * IterationList[ f(x), x0, n ] 
	 */
	final public GeoList IterationList(String label, GeoFunction f, NumberValue start,
			NumberValue n) {
		AlgoIterationList algo = new AlgoIterationList(cons, label, f, start, n);
		return algo.getResult();				
	}
	
	/** 
	 * RandomElement[list]
	 */
	final public GeoElement RandomElement(String label, GeoList list) {
		AlgoRandomElement algo = new AlgoRandomElement(cons, label, list);
		GeoElement geo = algo.getElement();
		return geo;
	}		
	
	/** 
	 * Element[list, number]
	 */
	final public GeoElement Element(String label, GeoList list, NumberValue n) {
		AlgoListElement algo = new AlgoListElement(cons, label, list, n);
		GeoElement geo = algo.getElement();
		return geo;
	}		
	
	/** 
	 * SelectedElement[list]
	 */
	final public GeoElement SelectedElement(String label, GeoList list) {
		AlgoSelectedElement algo = new AlgoSelectedElement(cons, label, list);
		GeoElement geo = algo.getElement();
		return geo;
	}		
	
	/** 
	 * SelectedElement[list]
	 */
	final public GeoElement SelectedIndex(String label, GeoList list) {
		AlgoSelectedIndex algo = new AlgoSelectedIndex(cons, label, list);
		GeoElement geo = algo.getElement();
		return geo;
	}		
	
	/** 
	 * Element[list, number, number]
	 */
	final public GeoElement Element(String label, GeoList list, NumberValue[] n) {
		AlgoListElement algo = new AlgoListElement(cons, label, list, n, false);
		GeoElement geo = algo.getElement();
		return geo;
	}		
	
	/** 
	 * Length[list]
	 */
	final public GeoNumeric Length(String label, GeoList list) {
		AlgoListLength algo = new AlgoListLength(cons, label, list);
		return algo.getLength();
	}
	
	/** 
	 * Length[locus]
	 */
	final public GeoNumeric Length(String label, GeoLocus locus) {
		AlgoLengthLocus algo = new AlgoLengthLocus(cons, label, locus);
		return algo.getLength();
	}
	
	/** 
	 * Element[text, number]
	 */
	final public GeoElement Element(String label, GeoText text, NumberValue n) {
		AlgoTextElement algo = new AlgoTextElement(cons, label, text, n);
		GeoElement geo = algo.getText();
		return geo;
	}		
	
	/** 
	 * Length[text]
	 */
	final public GeoNumeric Length(String label, GeoText text) {
		AlgoTextLength algo = new AlgoTextLength(cons, label, text);
		return algo.getLength();
	}
	
	// PhilippWeissenbacher 2007-04-10
	
	/**
	 * Perimeter named label of GeoPolygon
	 */
	final public GeoNumeric Perimeter(String label, GeoPolygon polygon) {
	    AlgoPerimeterPoly algo = new AlgoPerimeterPoly(cons, label, polygon);
	    return algo.getCircumference();
	}
	
	/**
	 * Perimeter of Locus
	 */
	final public GeoNumeric Perimeter(String label, GeoLocus locus) {
		AlgoPerimeterLocus algo = new AlgoPerimeterLocus(cons, label, locus);
	    return algo.getResult();
	}
	
	/**
	 * Circumference named label of GeoConic
	 */
	final public GeoNumeric Circumference(String label, GeoConic conic) {
	    AlgoCircumferenceConic algo = new AlgoCircumferenceConic(cons, label, conic);
	    return algo.getCircumference();
	}
	
	/**
	 * Path Parameter for eg point on circle
	 */
	final public GeoNumeric PathParameter(String label, GeoPoint p) {
	    AlgoPathParameter algo = new AlgoPathParameter(cons, label, p);
	    return algo.getResult();
	}
	
	// PhilippWeissenbacher 2007-04-10
		
	/** 
	 * polygon P[0], ..., P[n-1]
	 * The labels name the polygon itself and its segments
	 */
	final public GeoElement [] Polygon(String [] labels, GeoPointND [] P) {
		AlgoPolygon algo = new AlgoPolygon(cons, labels, P);
		return algo.getOutput();
	}
	
	//G.Sturr 2010-3-14
	/** 
	 * Polygon with vertices from geolist 
	 * Only the polygon is labeled, segments are not labeled
	 */
	final public GeoElement [] Polygon(String [] labels, GeoList pointList) {
		AlgoPolygon algo = new AlgoPolygon(cons, labels, pointList);
		return algo.getOutput();
	}
	//END G.Sturr
	
	/** 
	 * polygon P[0], ..., P[n-1]
	 * The labels name the polygon itself and its segments
	 */
	final public GeoElement [] PolyLine(String [] labels, GeoPoint [] P) {
		AlgoPolyLine algo = new AlgoPolyLine(cons, labels, P);
		return algo.getOutput();
	}
	
	final public GeoElement [] PolyLine(String [] labels, GeoList pointList) {
		AlgoPolyLine algo = new AlgoPolyLine(cons, labels, pointList);
		return algo.getOutput();
	}
	
	final public GeoElement [] VectorPolygon(String [] labels, GeoPoint [] points) {
    	boolean oldMacroMode = cons.isSuppressLabelsActive();
    	
    	cons.setSuppressLabelCreation(true);	
    	Circle(null, points[0], new MyDouble(this, points[0].distance(points[1])));
		cons.setSuppressLabelCreation(oldMacroMode);
		
	
	StringBuilder sb = new StringBuilder();
	
	double xA = points[0].inhomX;
	double yA = points[0].inhomY;
	
	for (int i = 1; i < points.length ; i++) {

		double xC = points[i].inhomX;
		double yC = points[i].inhomY;
		
		GeoNumeric nx = new GeoNumeric(cons, null, xC - xA);
		GeoNumeric ny = new GeoNumeric(cons, null, yC - yA);
		
		// make string like this
		// (a+x(A),b+y(A))
		sb.setLength(0);
		sb.append('(');
		sb.append(nx.getLabel());
		sb.append("+x(");
		sb.append(points[0].getLabel());
		sb.append("),");
		sb.append(ny.getLabel());
		sb.append("+y(");
		sb.append(points[0].getLabel());
		sb.append("))");
			
		//Application.debug(sb.toString());

		GeoPoint pp = (GeoPoint)getAlgebraProcessor().evaluateToPoint(sb.toString());
		
		try {
			cons.replace(points[i], pp);
			points[i] = pp;
			//points[i].setEuclidianVisible(false);
			points[i].update();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	points[0].update();
	
	return Polygon(labels, points);
	
	}
	
	final public GeoElement [] RigidPolygon(String [] labels, GeoPoint [] points) {
    	boolean oldMacroMode = cons.isSuppressLabelsActive();
    	
    	cons.setSuppressLabelCreation(true);	
    	GeoConic circle = Circle(null, points[0], new MyDouble(this, points[0].distance(points[1])));
		cons.setSuppressLabelCreation(oldMacroMode);
		
    	GeoPoint p = Point(null, (Path)circle, points[1].inhomX, points[1].inhomY, true, false);
	try {
		cons.replace(points[1], p);
		points[1] = p;
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
	
	StringBuilder sb = new StringBuilder();
	
	double xA = points[0].inhomX;
	double yA = points[0].inhomY;
	double xB = points[1].inhomX;
	double yB = points[1].inhomY;
	
	GeoVec2D a = new GeoVec2D(this, xB - xA, yB - yA ); // vector AB
	GeoVec2D b = new GeoVec2D(this, yA - yB, xB - xA ); // perpendicular to AB
	
	a.makeUnitVector();
	b.makeUnitVector();

	for (int i = 2; i < points.length ; i++) {

		double xC = points[i].inhomX;
		double yC = points[i].inhomY;
		
		GeoVec2D d = new GeoVec2D(this, xC - xA, yC - yA ); // vector AC
		
		setTemporaryPrintFigures(15);
		// make string like this
		// A+3.76UnitVector[Segment[A,B]]+-1.74UnitPerpendicularVector[Segment[A,B]]
		sb.setLength(0);
		sb.append(points[0].getLabel());
		sb.append('+');
		sb.append(format(a.inner(d)));

		// use internal command name
		sb.append("UnitVector[Segment[");
		sb.append(points[0].getLabel());
		sb.append(',');
		sb.append(points[1].getLabel());
		sb.append("]]+");
		sb.append(format(b.inner(d)));
		// use internal command name
		sb.append("UnitOrthogonalVector[Segment[");
		sb.append(points[0].getLabel());
		sb.append(',');
		sb.append(points[1].getLabel());
		sb.append("]]");
		
		restorePrintAccuracy();
					
			
		//Application.debug(sb.toString());

		GeoPoint pp = (GeoPoint)getAlgebraProcessor().evaluateToPoint(sb.toString());
		
		try {
			cons.replace(points[i], pp);
			points[i] = pp;
			points[i].setEuclidianVisible(false);
			points[i].update();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	points[0].update();
	
	return Polygon(labels, points);
	
	}
	
	
	
	/** 
	 * Regular polygon with vertices A and B and n total vertices.
	 * The labels name the polygon itself, its segments and points
	 */
	final public GeoElement [] RegularPolygon(String [] labels, GeoPoint A, GeoPoint B, NumberValue n) {
		AlgoPolygonRegular algo = new AlgoPolygonRegular(cons, labels, A, B, n);
		return algo.getOutput();
	}
	
	
	
	/** 
	 * Creates new point B with distance n from A and  new segment AB 
	 * The labels[0] is for the segment, labels[1] for the new point	 
	 */
	final public GeoElement [] Segment (String [] labels, GeoPoint A, NumberValue n) {
		// this is actually a macro
		String pointLabel = null, segmentLabel = null;
		if (labels != null) {
			switch (labels.length) {
				case 2:
					pointLabel = labels[1];
					
				case 1:
					segmentLabel = labels[0];
					
				default:
			}
		}
		
		// create a circle around A with radius n
		AlgoCirclePointRadius algoCircle = new AlgoCirclePointRadius(cons, A, n);
		cons.removeFromConstructionList(algoCircle);
		// place the new point on the circle
		AlgoPointOnPath algoPoint = new AlgoPointOnPath(cons, pointLabel, algoCircle.getCircle(), A.inhomX+ n.getDouble(), A.inhomY );
		
		// return segment and new point
		GeoElement [] ret = { Segment(segmentLabel, A, algoPoint.getP()),
											algoPoint.getP() };
		return ret;		
	}
	
	/** 
	 * Creates a new point C by rotating B around A using angle alpha and
	 * a new angle BAC. 
	 * The labels[0] is for the angle, labels[1] for the new point	 
	 */
	final public GeoElement [] Angle (String [] labels, GeoPoint B, GeoPoint A, NumberValue alpha) {
		return Angle(labels, B, A, alpha, true);	
	}
	
	/** 
	 * Creates a new point C by rotating B around A using angle alpha and
	 * a new angle BAC (for positive orientation) resp. angle CAB (for negative orientation). 
	 * The labels[0] is for the angle, labels[1] for the new point	 
	 */
	final public GeoElement [] Angle (String [] labels, GeoPoint B, GeoPoint A, NumberValue alpha, boolean posOrientation) {
		// this is actually a macro
		String pointLabel = null, angleLabel = null;
		if (labels != null) {
			switch (labels.length) {
				case 2:
					pointLabel = labels[1];
					
				case 1:
					angleLabel = labels[0];
					
				default:
			}
		}
		
		// rotate B around A using angle alpha
		GeoPoint C = (GeoPoint) Rotate(pointLabel, B, alpha, A)[0];
		
		// create angle according to orientation
		GeoAngle angle;
		if (posOrientation) {
			angle = Angle(angleLabel, B, A, C);
		} else {
			angle = Angle(angleLabel, C, A, B);
		}
		
		//return angle and new point
		GeoElement [] ret = { angle, C };
		return ret;		
	}

	/** 
	 * Angle named label between line g and line h
	 */
	final public GeoAngle Angle(String label, GeoLine g, GeoLine h) {
		AlgoAngleLines algo = new AlgoAngleLines(cons, label, g, h);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	/** 
	 * Angle named label between vector v and vector w
	 */
	final public GeoAngle Angle(
		String label,
		GeoVector v,
		GeoVector w) {
		AlgoAngleVectors algo = new AlgoAngleVectors(cons, label, v, w);
		GeoAngle angle = algo.getAngle();
		return angle;
	}
	
	/** 
	 * Angle named label for a point or a vector
	 */
	final public GeoAngle Angle(
		String label,
		GeoVec3D v) {
		AlgoAngleVector algo = new AlgoAngleVector(cons, label, v);
		GeoAngle angle = algo.getAngle();
		return angle;
	}


	/** 
	 * Angle named label between three points
	 */
	final public GeoAngle Angle(
		String label,
		GeoPoint A,
		GeoPoint B,
		GeoPoint C) {
		AlgoAnglePoints algo = new AlgoAnglePoints(cons, label, A, B, C);
		GeoAngle angle = algo.getAngle();
		return angle;
	}
	
	/** 
	 * all angles of given polygon
	 */
	final public GeoAngle [] Angles(String [] labels, GeoPolygon poly) {
		AlgoAnglePolygon algo = new AlgoAnglePolygon(cons, labels, poly);
		GeoAngle [] angles = algo.getAngles();
		//for (int i=0; i < angles.length; i++) {
		//	angles[i].setAlphaValue(0.0f);
		//}
		return angles;
	}

	/** 
	 * IntersectLines yields intersection point named label of lines g, h
	 */
	public GeoPointND IntersectLines(
		String label,
		GeoLineND g,
		GeoLineND h) {
		AlgoIntersectLines algo = new AlgoIntersectLines(cons, label, (GeoLine) g, (GeoLine) h);
		GeoPoint S = algo.getPoint();
		return S;
	}

	
	/** 
	 * yields intersection points named label of line g and polyLine p
	 */
	final public GeoElement[] IntersectLinePolyLine(
		String[] labels,
		GeoLine g,
		GeoPolyLine p) {
		AlgoIntersectLinePolyLine algo = new AlgoIntersectLinePolyLine(cons, labels, g, p);
		return algo.getOutput();
	}

	
	
	/** 
	 * yields intersection segments named label of line g and polygon p (as region)
	 */
	final public GeoElement[] IntersectLinePolygonalRegion(
		String[] labels,
		GeoLine g,
		GeoPolygon p) {
		AlgoIntersectLinePolygonalRegion algo = new AlgoIntersectLinePolygonalRegion(cons, labels, g, p);
		return algo.getOutput();
	}

	/** 
	 * IntersectLineConic yields intersection points named label1, label2
	 * of line g and conic c
	 * and intersection lines named in lowcase of the label
	 */
	final public GeoLine[] IntersectLineConicRegion(
		String[] labels,
		GeoLine g,
		GeoConic c) {
		AlgoIntersectLineConicRegion algo = new AlgoIntersectLineConicRegion(cons, labels, g, c);
		
		GeoLine[] lines = algo.getIntersectionLines();		
		
		return lines;
	}

	
	/** 
	 * yields intersection points named label of line g and polygon p (as boundary)
	 */
	final public GeoElement[] IntersectLinePolygon(
		String[] labels,
		GeoLine g,
		GeoPolygon p) {
		AlgoIntersectLinePolyLine algo = new AlgoIntersectLinePolyLine(cons, labels, g, p);
		return algo.getOutput();
	}
	
	/** 
	 * Intersects f and g using starting point A (with Newton's root finding)
	 */
	final public GeoPoint IntersectFunctions(
			String label,
			GeoFunction f,
			GeoFunction g, GeoPoint A) {
		AlgoIntersectFunctionsNewton algo = new AlgoIntersectFunctionsNewton(cons, label, f, g, A);
		GeoPoint S = algo.getIntersectionPoint();
		return S;
	}
	
	/** 
	 * Intersects f and l using starting point A (with Newton's root finding)
	 */
	final public GeoPoint IntersectFunctionLine(
			String label,
			GeoFunction f,
			GeoLine l, GeoPoint A) {
				
		AlgoIntersectFunctionLineNewton algo = new AlgoIntersectFunctionLineNewton(cons, label, f, l, A);
		GeoPoint S = algo.getIntersectionPoint();
		return S;
	}
	
	/** 
	 * Intersects f and g in interfal [left,right] numerically
	 */
	final public GeoPoint[] IntersectFunctions(
			String[] labels,
			GeoFunction f,
			GeoFunction g, 
			NumberValue left,
			NumberValue right) {
		AlgoIntersectFunctions algo = new AlgoIntersectFunctions(cons, labels, f, g, left, right);
		GeoPoint[] S = algo.getIntersectionPoints();
		return S;
	}//IntersectFunctions(label,f,g,left,right)
	
	
	/** 
	 * Intersect[polygon,polygon]
	 * G. Sturr
	 */
	final public GeoElement[] IntersectPolygons(String[] labels, GeoPolygon poly0, GeoPolygon poly1) {
		AlgoPolygonIntersection algo = new AlgoPolygonIntersection(cons, labels, poly0, poly1);
		GeoElement[] polygon = algo.getOutput();
		return polygon;
	}
	
	/** 
	 * Union[polygon,polygon]
	 * G. Sturr
	 */
	final public GeoElement[] Union(String[] labels, GeoPolygon poly0, GeoPolygon poly1) {
		AlgoPolygonUnion algo = new AlgoPolygonUnion(cons, labels, poly0, poly1);
		GeoElement[] polygon = algo.getOutput();
		return polygon;
	}
	
	
	
	/*********************************************
	 * CONIC PART
	 *********************************************/

	/** 
	 * circle with midpoint M and radius r
	 */
	final public GeoConic Circle(
		String label,
		GeoPoint M,
		NumberValue r) {
		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, M, r);
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		notifyUpdate(circle);
		return circle;
	}

	/** 
	 * circle with midpoint M and radius BC
	 * Michael Borcherds 2008-03-14
	 */
	final public GeoConic Circle(
			// this is actually a macro
		String label,
		GeoPoint A,
		GeoPoint B,
		GeoPoint C, boolean dummy) {

		AlgoJoinPointsSegment algoSegment = new AlgoJoinPointsSegment(cons, B, C, null);
		cons.removeFromConstructionList(algoSegment);
		
		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, A, algoSegment.getSegment(),true);
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		notifyUpdate(circle);
		return circle;
	}

	/** 
	 * circle with midpoint A and radius the same as circle
	 * Michael Borcherds 2008-03-14
	 */
	final public GeoConic Circle(
			// this is actually a macro
		String label,
		GeoPoint A,
		GeoConic c) {

		AlgoRadius radius = new AlgoRadius(cons, c);
		cons.removeFromConstructionList(radius);
		
		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, A, radius.getRadius());
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		notifyUpdate(circle);
		return circle;
	}

	/** 
	 * circle with midpoint M and radius segment
	 * Michael Borcherds 2008-03-15
	 */
	final public GeoConic Circle(
		String label,
		GeoPoint A,
		GeoSegment segment) {

		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, A, segment, true);
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		notifyUpdate(circle);
		return circle;
	}

	/** 
	 * circle with midpoint M through point P
	 */
	final public GeoConic Circle(String label, GeoPoint M, GeoPoint P) {
		AlgoCircleTwoPoints algo = new AlgoCircleTwoPoints(cons, label, M, P);
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		notifyUpdate(circle);
		return circle;
	}
	
	/** 
	 * semicircle with midpoint M through point P
	 */
	final public GeoConicPart Semicircle(String label, GeoPoint M, GeoPoint P) {
		AlgoSemicircle algo = new AlgoSemicircle(cons, label, M, P);
		return algo.getSemicircle();
	}
	
	/** 
	 * locus line for Q dependent on P. Note: P must be a point
	 * on a path.
	 */
	final public GeoLocus Locus(String label, GeoPoint Q, GeoPoint P) {		
		if (P.getPath() == null || 
			Q.getPath() != null || 
			!P.isParentOf(Q)) return null;
		AlgoLocus algo = new AlgoLocus(cons, label, Q, P);
		return algo.getLocus();
	}
	
	/**
	 * locus line for Q dependent on P. Note: P must be a visible slider
	 */
	final public GeoLocus Locus(String label, GeoPoint Q, GeoNumeric P) {
		if (!P.isSlider() || !P.isDefined() || !P.isAnimatable() || // !P.isSliderable() || !P.isDrawable() ||
			 Q.getPath() != null ||
			!P.isParentOf(Q)) return null;
		AlgoLocusSlider algo = new AlgoLocusSlider(cons, label, Q, P);
		return algo.getLocus();
	}

	/** 
	 * circle with through points A, B, C
	 */
	final public GeoConic Circle(
		String label,
		GeoPoint A,
		GeoPoint B,
		GeoPoint C) {
		AlgoCircleThreePoints algo = new AlgoCircleThreePoints(cons, label, A, B, C);
		GeoConic circle = (GeoConic) algo.getCircle();
		circle.setToSpecific();
		circle.update();
		notifyUpdate(circle);
		return circle;
	}
	
	/** 
	 * incircle with vertex points A, B, C
	 * dsun48 [6/26/2011]
	 */
	final public GeoConic Incircle(
		String label,
		GeoPoint A,
		GeoPoint B,
		GeoPoint C) {
		AlgoIncircle algo = new AlgoIncircle(cons, label, A, B, C);
		GeoConic circle = (GeoConic) algo.getCircle();
		circle.setToSpecific();
		circle.update();
		notifyUpdate(circle);
		return circle;
	}

	/** 
	 * conic arc from conic and parameters
	 */
	final public GeoConicPart ConicArc(String label, GeoConic conic, NumberValue a, NumberValue b) {
		AlgoConicPartConicParameters algo = new AlgoConicPartConicParameters(cons, label, conic, a, b, 															
				GeoConicPart.CONIC_PART_ARC);
		return algo.getConicPart();
	}
	
	/** 
	 * conic sector from conic and points
	 */
	final public GeoConicPart ConicArc(String label, GeoConic conic, GeoPoint P, GeoPoint Q) {
		AlgoConicPartConicPoints algo = new AlgoConicPartConicPoints(cons, label, conic, P, Q, 															
				GeoConicPart.CONIC_PART_ARC);
		return algo.getConicPart();
	}
	
	/** 
	 * conic sector from conic and parameters
	 */
	final public GeoConicPart ConicSector(String label, GeoConic conic, NumberValue a, NumberValue b) {
		AlgoConicPartConicParameters algo = new AlgoConicPartConicParameters(cons, label, conic, a, b, 															
				GeoConicPart.CONIC_PART_SECTOR);
		return algo.getConicPart();
	}
	
	/** 
	 * conic sector from conic and points
	 */
	final public GeoConicPart ConicSector(String label, GeoConic conic, GeoPoint P, GeoPoint Q) {
		AlgoConicPartConicPoints algo = new AlgoConicPartConicPoints(cons, label, conic, P, Q, 															
				GeoConicPart.CONIC_PART_SECTOR);
		return algo.getConicPart();
	}
	
	/** 
	 * circle arc from three points
	 */
	final public GeoConicPart CircumcircleArc(String label, GeoPoint A, GeoPoint B, GeoPoint C) {
		AlgoConicPartCircumcircle algo = new AlgoConicPartCircumcircle(cons, label, A,B, C, 															
				GeoConicPart.CONIC_PART_ARC);
		return algo.getConicPart();
	}
	
	/** 
	 * circle sector from three points
	 */
	final public GeoConicPart CircumcircleSector(String label, GeoPoint A, GeoPoint B, GeoPoint C) {
		AlgoConicPartCircumcircle algo = new AlgoConicPartCircumcircle(cons, label, A,B, C, 															
				GeoConicPart.CONIC_PART_SECTOR);
		return algo.getConicPart();
	}
	
	/** 
	 * circle arc from center and twho points on arc
	 */
	final public GeoConicPart CircleArc(String label, GeoPoint A, GeoPoint B, GeoPoint C) {
		AlgoConicPartCircle algo = new AlgoConicPartCircle(cons, label, A,B, C, 															
				GeoConicPart.CONIC_PART_ARC);
		return algo.getConicPart();
	}
	
	/** 
	 * circle sector from center and twho points on arc
	 */
	final public GeoConicPart CircleSector(String label, GeoPoint A, GeoPoint B, GeoPoint C) {
		AlgoConicPartCircle algo = new AlgoConicPartCircle(cons, label, A,B, C, 															
				GeoConicPart.CONIC_PART_SECTOR);
		return algo.getConicPart();
	}

	/** 
	 * Focuses of conic. returns 2 GeoPoints
	 */
	final public GeoPoint[] Focus(String[] labels, GeoConic c) {
		AlgoFocus algo = new AlgoFocus(cons, labels, c);
		GeoPoint[] focus = algo.getFocus();
		return focus;
	}

	/** 
	 * Vertices of conic. returns 4 GeoPoints
	 */
	final public GeoPoint[] Vertex(String[] labels, GeoConic c) {
		AlgoVertex algo = new AlgoVertex(cons, labels, c);
		GeoPoint[] vertex = algo.getVertex();
		return vertex;
	}
	
	/** 
	 * Vertices of polygon. returns 3+ GeoPoints
	 */
	final public GeoElement[] Vertex(String[] labels, GeoPolyLineInterface p) {
		AlgoVertexPolygon algo = new AlgoVertexPolygon(cons, labels, p);
		GeoElement[] vertex = algo.getVertex();
		return vertex;
	}
	
	/** 
	 * Vertex of polygon. returns a GeoPoint
	 */
	final public GeoPoint Vertex(String label, GeoPolyLineInterface p,NumberValue v) {
		AlgoVertexPolygon algo = new AlgoVertexPolygon(cons, label, p,v);
		GeoPoint vertex = algo.getOneVertex();
		return vertex;
	}

	/** 
	 * Center of conic
	 */
	final public GeoPoint Center(String label, GeoConic c) {
		AlgoCenterConic algo = new AlgoCenterConic(cons, label, c);
		GeoPoint midpoint = algo.getPoint();
		return midpoint;
	}
	
	/** 
	 * Centroid of a 
	 */
	final public GeoPoint Centroid(String label, GeoPolygon p) {
		AlgoCentroidPolygon algo = new AlgoCentroidPolygon(cons, label, p);
		GeoPoint centroid = algo.getPoint();
		return centroid;
	}
	
	/** 
	 * Corner of image
	 */
	final public GeoPoint Corner(String label, GeoImage img, NumberValue number) {
		AlgoImageCorner algo = new AlgoImageCorner(cons, label, img, number);	
		return algo.getCorner();
	}

	/** 
	 * Corner of text Michael Borcherds 2007-11-26
	 */
	final public GeoPoint Corner(String label, GeoText txt, NumberValue number) {
		AlgoTextCorner algo = new AlgoTextCorner(cons, label, txt, number);	
		return algo.getCorner();
	}

	/** 
	 * Corner of Drawing Pad Michael Borcherds 2008-05-10
	 */
	final public GeoPoint CornerOfDrawingPad(String label, NumberValue number, NumberValue ev) {
		AlgoDrawingPadCorner algo = new AlgoDrawingPadCorner(cons, label, number, ev);	
		return algo.getCorner();
	}

	/** 
	 * parabola with focus F and line l
	 */
	final public GeoConic Parabola(
		String label,
		GeoPoint F,
		GeoLine l) {
		AlgoParabolaPointLine algo = new AlgoParabolaPointLine(cons, label, F, l);
		GeoConic parabola = algo.getParabola();
		return parabola;
	}

	/** 
	 * ellipse with foci A, B and length of first half axis a
	 */
	final public GeoConic Ellipse(
		String label,
		GeoPoint A,
		GeoPoint B,
		NumberValue a) {
		AlgoEllipseFociLength algo = new AlgoEllipseFociLength(cons, label, A, B, a);
		GeoConic ellipse = algo.getConic();
		return ellipse;
	}

	/** 
	 * ellipse with foci A, B passing thorugh C
	 * Michael Borcherds 2008-04-06
	 */
	final public GeoConic Ellipse(
		String label,
		GeoPoint A,
		GeoPoint B,
		GeoPoint C) {
		AlgoEllipseFociPoint algo = new AlgoEllipseFociPoint(cons, label, A, B, C);
		GeoConic ellipse = algo.getEllipse();
		return ellipse;
	}

	/** 
	 * hyperbola with foci A, B and length of first half axis a
	 */
	final public GeoConic Hyperbola(
		String label,
		GeoPoint A,
		GeoPoint B,
		NumberValue a) {
		AlgoHyperbolaFociLength algo =
			new AlgoHyperbolaFociLength(cons, label, A, B, a);
		GeoConic hyperbola = algo.getConic();
		return hyperbola;
	}

	/** 
	 * hyperbola with foci A, B passing thorugh C
	 * Michael Borcherds 2008-04-06
	 */
	final public GeoConic Hyperbola(
		String label,
		GeoPoint A,
		GeoPoint B,
		GeoPoint C) {
		AlgoHyperbolaFociPoint algo =
			new AlgoHyperbolaFociPoint(cons, label, A, B, C);
		GeoConic hyperbola = algo.getHyperbola();
		return hyperbola;
	}

	/** 
	 * conic through five points
	 */
	final public GeoConic Conic(String label, GeoPoint[] points) {
		AlgoConicFivePoints algo = new AlgoConicFivePoints(cons, label, points);
		GeoConic conic = algo.getConic();
		return conic;
	}
	
	/**
	 * conic from coefficients
	 * @param labels
	 * @param coeffList
	 * @return
	 */
	final public GeoElement [] Conic(String label, GeoList coeffList) {
		AlgoConicFromCoeffList algo = new AlgoConicFromCoeffList(
				cons, label, coeffList);
		
		return new GeoElement[] {algo.getConic()};
		
	}

	/** 
	 * IntersectLineConic yields intersection points named label1, label2
	 * of line g and conic c
	 */
	final public GeoPoint[] IntersectLineConic(
		String[] labels,
		GeoLine g,
		GeoConic c) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();		
		GeoElement.setLabels(labels, points);	
		return points;
	}


	/** 
	 * IntersectConics yields intersection points named label1, label2, label3, label4
	 * of conics c1, c2
	 */
	public GeoPointND[] IntersectConics(
		String[] labels,
		GeoConicND a,
		GeoConicND b) {
		AlgoIntersectConics algo = getIntersectionAlgorithm((GeoConic) a, (GeoConic) b);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		GeoElement.setLabels(labels, points);
		return points;
	}
	
	/** 
	 * IntersectPolynomials yields all intersection points 
	 * of polynomials a, b
	 */
	final public GeoPoint[] IntersectPolynomials(String[] labels, GeoFunction a, GeoFunction b) {
		
		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false)) {
			
			// dummy point 
			GeoPoint A = new GeoPoint(cons);
			A.setZero();
			//we must check that getLabels() didn't return null
			String label = labels == null ? null : labels[0];						
			AlgoIntersectFunctionsNewton algo = new AlgoIntersectFunctionsNewton(cons, label, a, b, A);
			GeoPoint[] ret = {algo.getIntersectionPoint()};
			return ret;
		}
			
		AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b);
		algo.setPrintedInXML(true);
		algo.setLabels(labels);
		GeoPoint[] points = algo.getIntersectionPoints();		
		return points;
	}
	
	/** 
	 * get only one intersection point of two polynomials a, b 
	 * that is near to the given location (xRW, yRW)	 
	 */
	final public GeoPoint IntersectPolynomialsSingle(
		String label, GeoFunction a, GeoFunction b, 
		double xRW, double yRW) 
	{
		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false)) return null;
			
		AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b);		
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get only one intersection point of two polynomials a, b 
	 * with given index	 
	 */
	final public GeoPoint IntersectPolynomialsSingle(
		String label,
		GeoFunction a,
		GeoFunction b, NumberValue index) {
		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false)) return null;
		
		AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b);		// index - 1 to start at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) index.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * IntersectPolyomialLine yields all intersection points
	 * of polynomial f and line l
	 */
	final public GeoPoint[] IntersectPolynomialLine(
			String[] labels,		
			GeoFunction f,
			GeoLine l) {
				
		if (!f.isPolynomialFunction(false)) {
			
			// dummy point 
			GeoPoint A = new GeoPoint(cons);
			A.setZero();
			//we must check that getLabels() didn't return null
			String label = labels == null ? null : labels[0];						
			AlgoIntersectFunctionLineNewton algo = new AlgoIntersectFunctionLineNewton(cons, label, f, l, A);
			GeoPoint[] ret = {algo.getIntersectionPoint()};
			return ret;

		}

		AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);
		algo.setPrintedInXML(true);
		algo.setLabels(labels);
		GeoPoint[] points = algo.getIntersectionPoints();	
		return points;
	}
	
	/** 
	 * one intersection point of polynomial f and line l near to (xRW, yRW)
	 */
	final public GeoPoint IntersectPolynomialLineSingle(
			String label,		
			GeoFunction f,
			GeoLine l, double xRW, double yRW) {
		
		if (!f.isPolynomialFunction(false)) return null;
			
		AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);
		int index = algo.getClosestPointIndex(xRW, yRW);		
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;		
	}	
	
	/** 
	 * get only one intersection point of a line and a function 
	 */
	final public GeoPoint IntersectPolynomialLineSingle(
			String label,		
		GeoFunction f,
		GeoLine l, NumberValue index) {
			if (!f.isPolynomialFunction(false)) return null;	
			
			AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);		
			AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) index.getDouble() - 1);
			GeoPoint point = salgo.getPoint();
			return point;	
	}	
	
	/** 
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 */
	final public GeoPoint IntersectLineConicSingle(
		String label,
		GeoLine g,
		GeoConic c, double xRW, double yRW) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c);
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get only one intersection point of a line and a conic 
	 */
	final public GeoPoint IntersectLineConicSingle(
		String label,
		GeoLine g,
		GeoConic c, NumberValue index) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c);		// index - 1 to start at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) index.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/** 
	 * get only one intersection point of line/Conic near to a given point
	 */
	final public GeoPoint IntersectLineConicSingle(
			String label, GeoLine a, GeoConic b, GeoPoint refPoint) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm(a, b);		// index - 1 to start at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, refPoint);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	
	/** 
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 */
	final public GeoPoint IntersectConicsSingle(
		String label,
		GeoConic a,
		GeoConic b, double xRW, double yRW) {
		AlgoIntersectConics algo = getIntersectionAlgorithm(a, b);
		int index = algo.getClosestPointIndex(xRW, yRW) ; 				
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get only one intersection point of two conics 
	 */
	final public GeoPoint IntersectConicsSingle(
			String label, GeoConic a, GeoConic b, NumberValue index) {
		AlgoIntersectConics algo = getIntersectionAlgorithm(a, b);		// index - 1 to start at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) index.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	/** 
	 * get only one intersection point of two conics 
	 */
	final public GeoPoint IntersectConicsSingle(
			String label, GeoConic a, GeoConic b, GeoPoint refPoint) {
		AlgoIntersectConics algo = getIntersectionAlgorithm(a, b);		// index - 1 to start at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, refPoint);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	
	/** 
	 * get intersection points of a polynomial and a conic
	 */
	final public GeoPoint[] IntersectPolynomialConic(
		String[] labels,
		GeoFunction f,
		GeoConic c) {
		AlgoIntersectPolynomialConic algo = getIntersectionAlgorithm(f, c);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();		
	//	GeoElement.setLabels(labels, points);	
		algo.setLabels(labels);
		return points;
	}
	
	final public GeoPoint IntersectPolynomialConicSingle(String label,
			GeoFunction f, GeoConic c,NumberValue idx){
		AlgoIntersect algo = getIntersectionAlgorithm(f, c);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) idx.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	final public GeoPoint IntersectPolynomialConicSingle(String label,
			GeoFunction f, GeoConic c,double x,double y){
		AlgoIntersect algo = getIntersectionAlgorithm(f, c);
		int idx=algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get intersection points of a implicitPoly and a line
	 */
	final public GeoPoint[] IntersectImplicitpolyLine(
		String[] labels,
		GeoImplicitPoly p,
		GeoLine l) {
		AlgoIntersectImplicitpolyParametric algo = getIntersectionAlgorithm(p, l);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();		
		algo.setLabels(labels);
		return points;
	}
	
	/** 
	 * get single intersection points of a implicitPoly and a line
	 * @param idx index of choosen point
	 */
	final public GeoPoint IntersectImplicitpolyLineSingle(
		String label,
		GeoImplicitPoly p,
		GeoLine l,NumberValue idx) {
		AlgoIntersect algo = getIntersectionAlgorithm(p, l);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) idx.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get single intersection points of a implicitPoly and a line
	 */
	final public GeoPoint IntersectImplicitpolyLineSingle(
		String label,
		GeoImplicitPoly p,
		GeoLine l,double x,double y) {
		AlgoIntersect algo = getIntersectionAlgorithm(p, l);
		int idx=algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get intersection points of a implicitPoly and a polynomial
	 */
	final public GeoPoint[] IntersectImplicitpolyPolynomial(
		String[] labels,
		GeoImplicitPoly p,
		GeoFunction f) {
		//if (!f.isPolynomialFunction(false))
			//return null;
		AlgoIntersectImplicitpolyParametric algo = getIntersectionAlgorithm(p, f);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();		
		algo.setLabels(labels);
		return points;
	}
	
	/** 
	 * get single intersection points of a implicitPoly and a line
	 * @param idx index of choosen point
	 */
	final public GeoPoint IntersectImplicitpolyPolynomialSingle(
		String label,
		GeoImplicitPoly p,
		GeoFunction f,NumberValue idx) {
		if (!f.isPolynomialFunction(false))
			return null;
		AlgoIntersect algo = getIntersectionAlgorithm(p, f);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) idx.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get single intersection points of a implicitPoly and a line
	 */
	final public GeoPoint IntersectImplicitpolyPolynomialSingle(
		String label,
		GeoImplicitPoly p,
		GeoFunction f,double x,double y) {
		if (!f.isPolynomialFunction(false))
			return null;
		AlgoIntersect algo = getIntersectionAlgorithm(p, f);
		int idx=algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get intersection points of two implicitPolys
	 */
	final public GeoPoint[] IntersectImplicitpolys(
		String[] labels,
		GeoImplicitPoly p1,
		GeoImplicitPoly p2) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, p2);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();		
		algo.setLabels(labels);
		return points;
	}
	
	/** 
	 * get single intersection points of two implicitPolys
	 * @param idx index of choosen point
	 */
	final public GeoPoint IntersectImplicitpolysSingle(
		String label,
		GeoImplicitPoly p1,
		GeoImplicitPoly p2,NumberValue idx) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, p2);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) idx.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get single intersection points of two implicitPolys near given Point (x,y)
	 * @param x 
	 * @param y
	 */
	final public GeoPoint IntersectImplicitpolysSingle(
		String label,
		GeoImplicitPoly p1,
		GeoImplicitPoly p2,double x,double y) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, p2);
		int idx=algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get intersection points of implicitPoly and conic
	 */
	final public GeoPoint[] IntersectImplicitpolyConic(
		String[] labels,
		GeoImplicitPoly p1,
		GeoConic c1) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, c1);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();		
		algo.setLabels(labels);
		return points;
	}
	
	/** 
	 * get single intersection points of implicitPoly and conic
	 * @param idx index of choosen point
	 */
	final public GeoPoint IntersectImplicitpolyConicSingle(
		String label,
		GeoImplicitPoly p1,
		GeoConic c1,NumberValue idx) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, c1);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) idx.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get single intersection points of implicitPolys and conic near given Point (x,y)
	 * @param x 
	 * @param y
	 */
	final public GeoPoint IntersectImplicitpolyConicSingle(
		String label,
		GeoImplicitPoly p1,
		GeoConic c1,double x,double y) {
		AlgoIntersectImplicitpolys algo = getIntersectionAlgorithm(p1, c1);
		int idx=algo.getClosestPointIndex(x, y);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/*
	 * to avoid multiple calculations of the intersection points of the same
	 * two objects, we remember all the intersection algorithms created
	 */
	 private ArrayList intersectionAlgos = new ArrayList();
	 
	 // intersect polynomial and conic
	 AlgoIntersectPolynomialConic getIntersectionAlgorithm(GeoFunction f, GeoConic c) {

		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(f, c);
		if (existingAlgo != null) return (AlgoIntersectPolynomialConic) existingAlgo;
			
	 	// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolynomialConic algo = new AlgoIntersectPolynomialConic(cons, f, c);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	 // intersect line and conic
	 AlgoIntersectLineConic getIntersectionAlgorithm(GeoLine g, GeoConic c) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(g, c);
		if (existingAlgo != null) return (AlgoIntersectLineConic) existingAlgo;
			
	 	// we didn't find a matching algorithm, so create a new one
		AlgoIntersectLineConic algo = new AlgoIntersectLineConic(cons, g, c);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	 // intersect conics
	 AlgoIntersectConics getIntersectionAlgorithm(GeoConic a, GeoConic b) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, b);
		if (existingAlgo != null) return (AlgoIntersectConics) existingAlgo;
		
		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectConics algo = new AlgoIntersectConics(cons, a, b);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	 // intersection of polynomials
	 AlgoIntersectPolynomials getIntersectionAlgorithm(GeoFunction a, GeoFunction b) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, b);
		if (existingAlgo != null) return (AlgoIntersectPolynomials) existingAlgo;
		
		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolynomials algo = new AlgoIntersectPolynomials(cons, a, b);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	 // intersection of polynomials
	 AlgoIntersectPolynomialLine getIntersectionAlgorithm(GeoFunction a, GeoLine l) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, l);
		if (existingAlgo != null) return (AlgoIntersectPolynomialLine) existingAlgo;
		
		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolynomialLine algo = new AlgoIntersectPolynomialLine(cons, a, l);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	// intersection of GeoImplicitPoly, GeoLine
	 AlgoIntersectImplicitpolyParametric getIntersectionAlgorithm(GeoImplicitPoly p, GeoLine l) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p, l);
		if (existingAlgo != null) return (AlgoIntersectImplicitpolyParametric) existingAlgo;
		
		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectImplicitpolyParametric algo = new AlgoIntersectImplicitpolyParametric(cons, p, l);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	// intersection of GeoImplicitPoly, polynomial
	 AlgoIntersectImplicitpolyParametric getIntersectionAlgorithm(GeoImplicitPoly p, GeoFunction f) {
			AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p, f);
			if (existingAlgo != null) return (AlgoIntersectImplicitpolyParametric) existingAlgo;
			
			// we didn't find a matching algorithm, so create a new one
			AlgoIntersectImplicitpolyParametric algo = new AlgoIntersectImplicitpolyParametric(cons, p, f);
			algo.setPrintedInXML(false);
			intersectionAlgos.add(algo); // remember this algorithm
			return algo;
		 }
	 
	// intersection of two GeoImplicitPoly
	 AlgoIntersectImplicitpolys getIntersectionAlgorithm(GeoImplicitPoly p1, GeoImplicitPoly p2) {
			AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p1, p2);
			if (existingAlgo != null) return (AlgoIntersectImplicitpolys) existingAlgo;
			
			// we didn't find a matching algorithm, so create a new one
			AlgoIntersectImplicitpolys algo = new AlgoIntersectImplicitpolys(cons, p1, p2);
			algo.setPrintedInXML(false);
			intersectionAlgos.add(algo); // remember this algorithm
			return algo;
		 }
	 
	 AlgoIntersectImplicitpolys getIntersectionAlgorithm(GeoImplicitPoly p1, GeoConic c1) {
			AlgoElement existingAlgo = findExistingIntersectionAlgorithm(p1, c1);
			if (existingAlgo != null) return (AlgoIntersectImplicitpolys) existingAlgo;
			
			// we didn't find a matching algorithm, so create a new one
			AlgoIntersectImplicitpolys algo = new AlgoIntersectImplicitpolys(cons, p1, c1);
			algo.setPrintedInXML(false);
			intersectionAlgos.add(algo); // remember this algorithm
			return algo;
		 }
	  
	 public AlgoElement findExistingIntersectionAlgorithm(GeoElement a, GeoElement b) {
		int size = intersectionAlgos.size();
		AlgoElement algo;
		for (int i=0; i < size; i++) {
			algo = (AlgoElement) intersectionAlgos.get(i);
			GeoElement [] input = algo.getInput();
			if (a == input[0] && b == input[1] ||
				 a == input[1] && b == input[0])
				// we found an existing intersection algorithm
				return algo;
		}
		return null;
	 }
	 
	 public void removeIntersectionAlgorithm(AlgoIntersectAbstract algo) {
		intersectionAlgos.remove(algo);	 
	 }


	 public void addIntersectionAlgorithm(AlgoIntersectAbstract algo) {
			intersectionAlgos.add(algo);	 
		 }
	 
	 /** 
	 * polar line to P relativ to c
	 */
	final public GeoLine PolarLine(
		String label,
		GeoPoint P,
		GeoConic c) {
		AlgoPolarLine algo = new AlgoPolarLine(cons, label, c, P);
		GeoLine polar = algo.getLine();
		return polar;
	}

	/** 
	 * diameter line conjugate to direction of g relative to c
	 */
	final public GeoLine DiameterLine(
		String label,
		GeoLine g,
		GeoConic c) {
		AlgoDiameterLine algo = new AlgoDiameterLine(cons, label, c, g);
		GeoLine diameter = algo.getDiameter();
		return diameter;
	}

	/** 
	 * diameter line conjugate to v relative to c
	 */
	final public GeoLine DiameterLine(
		String label,
		GeoVector v,
		GeoConic c) {
		AlgoDiameterVector algo = new AlgoDiameterVector(cons, label, c, v);
		GeoLine diameter = algo.getDiameter();
		return diameter;
	}

	/** 
	 * tangents to c through P
	 */
	final public GeoLine[] Tangent(
		String[] labels,
		GeoPoint P,
		GeoConic c) {
		AlgoTangentPoint algo = new AlgoTangentPoint(cons, labels, P, c);
		GeoLine[] tangents = algo.getTangents();		
		return tangents;
	}

	/** 
	 * common tangents to c1 and c2
	 * dsun48 [6/26/2011]
	 */
	final public GeoLine[] CommonTangents(
		String[] labels,
		GeoConic c1,
		GeoConic c2) {
		AlgoCommonTangents algo = new AlgoCommonTangents(cons, labels, c1, c2);
		GeoLine[] tangents = algo.getTangents();		
		return tangents;
	}
	
	/** 
	 * tangents to c parallel to g
	 */
	final public GeoLine[] Tangent(
		String[] labels,
		GeoLine g,
		GeoConic c) {
		AlgoTangentLine algo = new AlgoTangentLine(cons, labels, g, c);
		GeoLine[] tangents = algo.getTangents();
		return tangents;
	}

	/** 
	 * tangent to f in x = a
	 */
	final public GeoLine Tangent(
		String label,
		NumberValue a,
		GeoFunction f) {
		AlgoTangentFunctionNumber algo =
			new AlgoTangentFunctionNumber(cons, label, a, f);
		GeoLine t = algo.getTangent();
		t.setToExplicit();
		t.update();   
		notifyUpdate(t);  
		return t;
	}

	/** 
	 * tangent to f in x = x(P)
	 */
	final public GeoLine Tangent(
		String label,
		GeoPoint P,
		GeoFunction f) {
		AlgoTangentFunctionPoint algo =
			new AlgoTangentFunctionPoint(cons, label, P, f);
		GeoLine t = algo.getTangent();
		t.setToExplicit();
		t.update();     
		notifyUpdate(t);
		return t;
	}
	
	/** 
	 * tangents to p through P
	 */
	final public GeoLine[] Tangent(
		String[] labels,
		GeoPoint R,
		GeoImplicitPoly p) {
		AlgoTangentImplicitpoly algo = new AlgoTangentImplicitpoly(cons, labels, p, R);
		algo.setLabels(labels);
		GeoLine[] tangents = algo.getTangents();		
		return tangents;
	}

	/** 
	 * tangents to p parallel to g
	 */
	final public GeoLine[] Tangent(
		String[] labels,
		GeoLine g,
		GeoImplicitPoly p) {
		AlgoTangentImplicitpoly algo = new AlgoTangentImplicitpoly(cons, labels, p, g);
		algo.setLabels(labels);
		GeoLine[] tangents = algo.getTangents();
		return tangents;
	}
	
	/** 
	 * asymptotes to c
	 */
	final public GeoLine[] Asymptote(String[] labels, GeoConic c) {
		AlgoAsymptote algo = new AlgoAsymptote(cons, labels, c);
		GeoLine[] asymptotes = algo.getAsymptotes();
		return asymptotes;
	}

	/** 
	 * axes of c
	 */
	final public GeoLine[] Axes(String[] labels, GeoConic c) {
		AlgoAxes algo = new AlgoAxes(cons, labels, c);
		GeoLine[] axes = algo.getAxes();
		return axes;
	}

	/** 
	 * first axis of c
	 */
	final public GeoLine FirstAxis(String label, GeoConic c) {
		AlgoAxisFirst algo = new AlgoAxisFirst(cons, label, c);
		GeoLine axis = algo.getAxis();
		return axis;
	}

	/** 
	 * second axis of c
	 */
	final public GeoLine SecondAxis(String label, GeoConic c) {
		AlgoAxisSecond algo = new AlgoAxisSecond(cons, label, c);
		GeoLine axis = algo.getAxis();
		return axis;
	}

	/** 
	 * directrix of c
	 */
	final public GeoLine Directrix(String label, GeoConic c) {
		AlgoDirectrix algo = new AlgoDirectrix(cons, label, c);
		GeoLine directrix = algo.getDirectrix();
		return directrix;
	}

	/** 
	 * linear eccentricity of c
	 */
	final public GeoNumeric Excentricity(String label, GeoConic c) {
		AlgoExcentricity algo = new AlgoExcentricity(cons, label, c);
		GeoNumeric linearEccentricity = algo.getLinearEccentricity();
		return linearEccentricity;
	}

	/** 
	 * eccentricity of c
	 */
	final public GeoNumeric Eccentricity(String label, GeoConic c) {
		AlgoEccentricity algo = new AlgoEccentricity(cons, label, c);
		GeoNumeric eccentricity = algo.getEccentricity();
		return eccentricity;
	}

	/** 
	 * first axis' length of c
	 */
	final public GeoNumeric FirstAxisLength(String label, GeoConic c) {
		AlgoAxisFirstLength algo = new AlgoAxisFirstLength(cons, label, c);
		GeoNumeric length = algo.getLength();
		return length;
	}

	/** 
	 * second axis' length of c
	 */
	final public GeoNumeric SecondAxisLength(String label, GeoConic c) {
		AlgoAxisSecondLength algo = new AlgoAxisSecondLength(cons, label, c);
		GeoNumeric length = algo.getLength();
		return length;
	}

	/** 
	 * (parabola) parameter of c
	 */
	final public GeoNumeric Parameter(String label, GeoConic c) {
		AlgoParabolaParameter algo = new AlgoParabolaParameter(cons, label, c);
		GeoNumeric length = algo.getParameter();
		return length;
	}

	/** 
	 * (circle) radius of c
	 */
	final public GeoNumeric Radius(String label, GeoConic c) {
		AlgoRadius algo = new AlgoRadius(cons, label, c);
		GeoNumeric length = algo.getRadius();
		return length;
	}

	/** 
	 * angle of c (angle between first eigenvector and (1,0))
	 */
	final public GeoAngle Angle(String label, GeoConic c) {
		AlgoAngleConic algo = new AlgoAngleConic(cons, label, c);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	/********************************************************************
	 * TRANSFORMATIONS
	 ********************************************************************/

	/**
	 * translate geoTrans by vector v
	 */
	final public GeoElement [] Translate(String label, GeoElement geoTrans, GeoVec3D v) {
		Transform t = new TransformTranslate(cons, v);
		return t.transform(geoTrans, label);				
	}
	
	/**
	 * translates vector v to point A. The resulting vector is equal
	 * to v and has A as startPoint
	 */
	final public GeoVector Translate(String label, GeoVec3D v, GeoPoint A) {
		AlgoTranslateVector algo = new AlgoTranslateVector(cons, label, v, A);
		GeoVector vec = algo.getTranslatedVector();
		return vec;
	}	

	/**
	 * rotate geoRot by angle phi around (0,0)
	 */
	final public GeoElement [] Rotate(String label, GeoElement geoRot, NumberValue phi) {
		Transform t = new TransformRotate(cons, phi);
		return t.transform(geoRot, label);					
	}


	/**
	 * rotate geoRot by angle phi around Q
	 */
	final public GeoElement [] Rotate(String label, GeoElement geoRot, NumberValue phi, GeoPoint Q) {
		Transform t = new TransformRotate(cons, phi,Q);
		return t.transform(geoRot, label);		
	}
		
	/**
	 * dilate geoRot by r from S
	 */
	final public GeoElement [] Dilate(String label, GeoElement geoDil, NumberValue r, GeoPoint S) {
		Transform t = new TransformDilate(cons, r,S);
		return t.transform(geoDil, label);		
	}
	
	/**
	 * dilate geoRot by r from origin
	 */
	final public GeoElement [] Dilate(String label, GeoElement geoDil, NumberValue r) {
		Transform t = new TransformDilate(cons, r);
		return t.transform(geoDil, label);
	}

	/**
	 * mirror geoMir at point Q
	 */
	final public GeoElement [] Mirror(String label, GeoElement geoMir, GeoPoint Q) {	
		Transform t = new TransformMirror(cons, Q);
		return t.transform(geoMir, label);
	}

	/**
	 * mirror (invert) element Q in circle 
	 * Michael Borcherds 2008-02-10
	 */
	final public GeoElement [] Mirror(String label, GeoElement Q, GeoConic conic) {	
		Transform t = new TransformMirror(cons, conic);
		return t.transform(Q, label);
	}

	/**
	 * apply matrix 
	 * Michael Borcherds 2010-05-27
	 */
	final public GeoElement [] ApplyMatrix(String label, GeoElement Q, GeoList matrix) {	
		Transform t = new TransformApplyMatrix(cons, matrix);
		return t.transform(Q, label);
	}
	
	/**
	 * shear
	 */
	final public GeoElement [] Shear(String label, GeoElement Q, GeoVec3D l, GeoNumeric num) {	
		Transform t = new TransformShearOrStretch(cons, l, num, true);
		return t.transform(Q, label);
	}
	/**
	 * apply matrix 
	 * Michael Borcherds 2010-05-27
	 */
	final public GeoElement [] Stretch(String label, GeoElement Q, GeoVec3D l, GeoNumeric num) {	
		Transform t = new TransformShearOrStretch(cons, l, num, false);
		return t.transform(Q, label);
	}

	/**
	 * mirror geoMir at line g
	 */
	final public GeoElement [] Mirror(String label, GeoElement geoMir, GeoLine g) {
		Transform t = new TransformMirror(cons, g);
		return t.transform(geoMir, label);
		
			
	}			
	
	
	static final int TRANSFORM_TRANSLATE = 0;
	static final int TRANSFORM_MIRROR_AT_POINT = 1;
	static final int TRANSFORM_MIRROR_AT_LINE = 2;	
	static final int TRANSFORM_ROTATE = 3;
	static final int TRANSFORM_ROTATE_AROUND_POINT = 4;
	static final int TRANSFORM_DILATE = 5;
	
	public static boolean keepOrientationForTransformation(int transformationType) {
		switch (transformationType) {
			case TRANSFORM_MIRROR_AT_LINE:	
				return false;
			
			default:
				return true;									
		}
	}
	
	
	/***********************************
	 * CALCULUS
	 ***********************************/
	
	/** function limited to interval [a, b]
	 */
	final public GeoFunction Function(String label, GeoFunction f, 
										NumberValue a, NumberValue b) {
		AlgoFunctionInterval algo = new AlgoFunctionInterval(cons, label, f, a, b);		
		GeoFunction g = algo.getFunction();
		return g;
	}
	
	/**
	 * n-th derivative of multivariate function f
	 */
	final public GeoElement Derivative(
		String label,
		CasEvaluableFunction f, GeoNumeric var,
		NumberValue n) {
		
		AlgoDerivative algo = new AlgoDerivative(cons, label, f, var, n);
		return algo.getResult();	
	}
	
	/**
	 * Tries to expand a function f to a polynomial.
	 */
	final public GeoFunction PolynomialFunction(String label, GeoFunction f) {		
		AlgoPolynomialFromFunction algo = new AlgoPolynomialFromFunction(cons, label, f);
		return algo.getPolynomial();			
	}
	
	/**
	 * Fits a polynomial exactly to a list of coordinates
	 * Michael Borcherds 2008-01-22
	 */
	final public GeoFunction PolynomialFunction(String label, GeoList list) {		
		AlgoPolynomialFromCoordinates algo = new AlgoPolynomialFromCoordinates(cons, label, list);
		return algo.getPolynomial();			
	}

	final public GeoElement Expand(String label, CasEvaluableFunction func) {		
		AlgoExpand algo = new AlgoExpand(cons, label, func);
		return algo.getResult();			
	}
	
	final public GeoElement Simplify(String label, CasEvaluableFunction func) {		
		AlgoSimplify algo = new AlgoSimplify(cons, label, func);
		return algo.getResult();			
	}
	
	final public GeoElement SolveODE(String label, CasEvaluableFunction func) {		
		AlgoSolveODECas algo = new AlgoSolveODECas(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Simplify text, eg "+-x" to "-x"
	 * @author Michael Borcherds 
	 */
	final public GeoElement Simplify(String label, GeoText text) {		
		AlgoSimplifyText algo = new AlgoSimplifyText(cons, label, text);
		return algo.getGeoText();		
	}
	
	final public GeoElement DynamicCoordinates(String label, GeoPoint geoPoint,
			NumberValue num1, NumberValue num2) {
		AlgoDynamicCoordinates algo = new AlgoDynamicCoordinates(cons, label, geoPoint, num1, num2);
		return algo.getPoint();
	}

	final public GeoElement Factor(String label, CasEvaluableFunction func) {		
		AlgoFactor algo = new AlgoFactor(cons, label, func);
		return algo.getResult();			
	}
	
	final public GeoElement CompleteSquare(String label, GeoFunction func) {		
		AlgoCompleteSquare algo = new AlgoCompleteSquare(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Factors
	 * Michael Borcherds 
	 */
	final public GeoList Factors(String label, GeoFunction func) {		
		AlgoFactors algo = new AlgoFactors(cons, label, func);
		return algo.getResult();			
	}
	
	final public GeoLocus SolveODE(String label, FunctionalNVar f, FunctionalNVar g, GeoNumeric x, GeoNumeric y, GeoNumeric end, GeoNumeric step) {		
		AlgoSolveODE algo = new AlgoSolveODE(cons, label, f, g, x, y, end, step);
		return algo.getResult();			
	}
	
	/*
	 * second order ODEs
	 */
	final public GeoLocus SolveODE2(String label, GeoFunctionable f, GeoFunctionable g, GeoFunctionable h, GeoNumeric x, GeoNumeric y, GeoNumeric yDot, GeoNumeric end, GeoNumeric step) {		
		AlgoSolveODE2 algo = new AlgoSolveODE2(cons, label, f, g, h, x, y, yDot, end, step);
		return algo.getResult();			
	}
	
	/**
	 * Asymptotes
	 * Michael Borcherds 
	 */
	final public GeoList AsymptoteFunction(String label, GeoFunction func) {		
		AlgoAsymptoteFunction algo = new AlgoAsymptoteFunction(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Asymptotes to ImplicitPoly
	 * Michael Borcherds 
	 */
	final public GeoList AsymptoteImplicitpoly(String label, GeoImplicitPoly ip) {		
		AlgoAsymptoteImplicitPoly algo = new AlgoAsymptoteImplicitPoly(cons, label, ip);
		return algo.getResult();			
	}
	
	/**
	 * Numerator
	 * Michael Borcherds 
	 */
	final public GeoFunction Numerator(String label, GeoFunction func) {		
		AlgoNumerator algo = new AlgoNumerator(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Denominator
	 * Michael Borcherds 
	 */
	final public GeoFunction Denominator(String label, GeoFunction func) {		
		AlgoDenominator algo = new AlgoDenominator(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Degree
	 * Michael Borcherds 
	 */
	final public GeoNumeric Degree(String label, GeoFunction func) {		
		AlgoDegree algo = new AlgoDegree(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Limit
	 * Michael Borcherds 
	 */
	final public GeoNumeric Limit(String label, GeoFunction func, NumberValue num) {		
		AlgoLimit algo = new AlgoLimit(cons, label, func, num);
		return algo.getResult();			
	}
	
	/**
	 * LimitBelow
	 * Michael Borcherds 
	 */
	final public GeoNumeric LimitBelow(String label, GeoFunction func, NumberValue num) {		
		AlgoLimitBelow algo = new AlgoLimitBelow(cons, label, func, num);
		return algo.getResult();			
	}
	
	/**
	 * LimitAbove
	 * Michael Borcherds 
	 */
	final public GeoNumeric LimitAbove(String label, GeoFunction func, NumberValue num) {		
		AlgoLimitAbove algo = new AlgoLimitAbove(cons, label, func, num);
		return algo.getResult();			
	}
	
	/**
	 * Partial Fractions
	 * Michael Borcherds 
	 */
	final public GeoElement PartialFractions(String label, CasEvaluableFunction func) {		
		AlgoPartialFractions algo = new AlgoPartialFractions(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Coefficients
	 * Michael Borcherds 2008-04-04
	 */
	final public GeoList Coefficients(String label, GeoFunction func) {		
		AlgoCoefficients algo = new AlgoCoefficients(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Coefficients
	 * Michael Borcherds 2008-04-04
	 */
	final public GeoList Coefficients(String label, GeoConic func) {		
		AlgoConicCoefficients algo = new AlgoConicCoefficients(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Taylor series of function f about point x=a of order n
	 */
	final public GeoFunction TaylorSeries(
		String label,
		GeoFunction f,
		NumberValue a, 
		NumberValue n) {
		
		AlgoTaylorSeries algo = new AlgoTaylorSeries(cons, label, f, a, n);
		return algo.getPolynomial();
	}

	/**
	 * Integral of function f
	 */
	final public GeoElement Integral(String label, CasEvaluableFunction f, GeoNumeric var) {
		AlgoIntegral algo = new AlgoIntegral(cons, label, f, var);
		return algo.getResult();
	}
	
	/**
	 * definite Integral of function f from x=a to x=b
	 */
	final public GeoNumeric Integral(String label, GeoFunction f, NumberValue a, NumberValue b) {
		AlgoIntegralDefinite algo = new AlgoIntegralDefinite(cons, label, f, a, b);
		GeoNumeric n = algo.getIntegral();
		return n;
	}

	/**
	 * definite Integral of function f from x=a to x=b 
	 * with option to evaluate  (evaluate == false allows shade-only drawing)
	 */
	final public GeoNumeric Integral(String label, GeoFunction f, NumberValue a, NumberValue b, GeoBoolean evaluate) {
		AlgoIntegralDefinite algo = new AlgoIntegralDefinite(cons, label, f, a, b, evaluate);
		GeoNumeric n = algo.getIntegral();
		return n;
	}
	
	
	/** 
	 * definite integral of function (f - g) in interval [a, b]
	 */
	final public GeoNumeric Integral(String label, GeoFunction f, GeoFunction g,
												NumberValue a, NumberValue b) {
		AlgoIntegralFunctions algo = new AlgoIntegralFunctions(cons, label, f, g, a, b);
		GeoNumeric num = algo.getIntegral();
		return num;
	}		
	
	
	/** 
	 * definite integral of function (f - g) in interval [a, b]
	 * with option to not evaluate  (evaluate == false allows shade-only drawing)
	 */
	final public GeoNumeric Integral(String label, GeoFunction f, GeoFunction g,
												NumberValue a, NumberValue b, GeoBoolean evaluate) {
		AlgoIntegralFunctions algo = new AlgoIntegralFunctions(cons, label, f, g, a, b,evaluate);
		GeoNumeric num = algo.getIntegral();
		return num;
	}		
	
	
	
	
	/**
	 * 
	 */
	final public GeoPoint [] PointsFromList(String [] labels, GeoList list) {
		
		AlgoPointsFromList algo = new AlgoPointsFromList(cons, labels, true, list);
		GeoPoint [] g = algo.getPoints();
		return g;
	}	
	
	/**
	 * all Roots of polynomial f (works only for polynomials and functions
	 * that can be simplified to factors of polynomials, e.g. sqrt(x) to x)
	 */
	final public GeoPoint [] Root(String [] labels, GeoFunction f) {
		// allow functions that can be simplified to factors of polynomials
		if (!f.isPolynomialFunction(true)) return null;
		
		AlgoRootsPolynomial algo = new AlgoRootsPolynomial(cons, labels, f);
		GeoPoint [] g = algo.getRootPoints();
		return g;
	}	
	
	/**
	 * all Complex Roots of polynomial f (works only for polynomials)
	 */
	final public GeoPoint [] ComplexRoot(String [] labels, GeoFunction f) {
		// allow functions that can be simplified to factors of polynomials
		if (!f.isPolynomialFunction(true)) return null;
		
		AlgoComplexRootsPolynomial algo = new AlgoComplexRootsPolynomial(cons, labels, f);
		GeoPoint [] g = algo.getRootPoints();
		return g;
	}	
	
	/**
	 * Root of a function f to given start value a (works only if first derivative of f exists)
	 */
	final public GeoPoint Root(String label, GeoFunction f, NumberValue a) {			 
		AlgoRootNewton algo = new AlgoRootNewton(cons, label, f, a);
		GeoPoint p = algo.getRootPoint();
		return p;
	}	

	/**
	 * Root of a function f in given interval [a, b]
	 */
	final public GeoPoint Root(String label, GeoFunction f, NumberValue a, NumberValue b) {			 
		AlgoRootInterval algo = new AlgoRootInterval(cons, label, f, a, b);
		GeoPoint p = algo.getRootPoint();
		return p;
	}	

	/**
	 * Roots of a function f in given interval [a, b]
	 * Numerical version
	 */
	final public GeoPoint[] Roots(String[] labels, GeoFunction f, NumberValue a, NumberValue b) {			 
		AlgoRoots algo = new AlgoRoots(cons, labels, f, a, b);
		GeoPoint[] pts = algo.getRootPoints();
		return pts;
	}//Roots(label,f,a,b)
	
	/**
	 * all Extrema of function f (works only for polynomials)
	 */
	final public GeoPoint [] Extremum(String [] labels, GeoFunction f) {
		//	check if this is a polynomial at the moment
		if (!f.isPolynomialFunction(true)) return null;
			 
		AlgoExtremumPolynomial algo = new AlgoExtremumPolynomial(cons, labels, f);
		GeoPoint [] g = algo.getRootPoints();
		return g;
	}	
	/**
	 * Numeric search for extremum of function f in interval [left,right]
	 * Ulven 2011-2-5
	 
	final public GeoPoint[] Extremum(String label,GeoFunction f,NumberValue left,NumberValue right) {
		AlgoExtremumNumerical algo=new AlgoExtremumNumerical(cons,label,f,left,right);
		GeoPoint g=algo.getNumericalExtremum();	//All variants return array...
		GeoPoint[] result=new GeoPoint[1];
		result[0]=g;
		return result;
	}//Extremum(label,geofunction,numbervalue,numbervalue)
	*/
	final public GeoPoint[] Extremum(String[] labels,GeoFunction f,NumberValue left,NumberValue right) {
		AlgoExtremumMulti algo=new AlgoExtremumMulti(cons,labels,f,left,right);
		GeoPoint[] gpts=algo.getExtremumPoints();	//All variants return array...
		return gpts;
	}//Extremum(label,geofunction,numbervalue,numbervalue)
	

	/**
	* Trying to maximize dependent variable with respect to independen variable
	* Ulven 2011-2-13
	*
	*/
	final public GeoElement Maximize(String label, GeoElement dep, GeoNumeric indep) {
		AlgoMaximize algo=new AlgoMaximize(cons,label,dep,indep);
		/*
		GeoElement[] geo=new GeoElement[1];
		geo[0]=algo.getMaximized();	//All variants return array...
		*/
		return algo.getResult();//geo;
	}//Maximize(lbl,dep,indep);	
	
	/**
	* Trying to minimize dependent variable with respect to independen variable
	* Ulven 2011-2-13
	*
	*/
	final public GeoElement Minimize(String label, GeoElement dep, GeoNumeric indep) {
		AlgoMinimize algo=new AlgoMinimize(cons,label,dep,indep);	//	true: minimize
		/*GeoElement geo=algo.getMaximized();	//All variants return array...
		 * 
		 */
		return algo.getResult();
	}//Minimize(lbl,dep,indep,minimize);
	
	
	
	/**
	 * all Turning points of function f (works only for polynomials)
	 */
	final public GeoPoint [] TurningPoint(String [] labels, GeoFunction f) {
		//	check if this is a polynomial at the moment
		if (!f.isPolynomialFunction(true)) return null;
			 
		AlgoTurningPointPolynomial algo = new AlgoTurningPointPolynomial(cons, labels, f);
		GeoPoint [] g = algo.getRootPoints();
		return g;
	}	
	/**
	 * Victor Franco Espino 18-04-2007: New commands
	 *
	 * Calculate affine ratio: (A,B,C) = (t(C)-t(A)) : (t(C)-t(B)) 
	 */

	final public GeoNumeric AffineRatio(String label, GeoPoint A, GeoPoint B,
			GeoPoint C) {
		AlgoAffineRatio affine = new AlgoAffineRatio(cons, label, A, B, C);
		GeoNumeric M = affine.getResult();
		return M;

	}

	  

	/**
	 * Calculate cross ratio: (A,B,C,D) = affineRatio(A, B, C) / affineRatio(A, B, D)
	 */

	final public GeoNumeric CrossRatio(String label,GeoPoint A,GeoPoint B,GeoPoint C,GeoPoint D){

		  AlgoCrossRatio cross = new AlgoCrossRatio(cons,label,A,B,C,D);
		  GeoNumeric M = cross.getResult();
		  return M;

	}

	

	/**
	 * Calculate Curvature Vector for function: c(x) = (1/T^4)*(-f'*f'',f''), T = sqrt(1+(f')^2)
	 */

	final public GeoVector CurvatureVector(String label,GeoPoint A,GeoFunction f){

		  AlgoCurvatureVector algo = new AlgoCurvatureVector(cons,label,A,f);
		  GeoVector v = algo.getVector();
		  return v;

	}



	/**

	 * Calculate Curvature Vector for curve: c(t) = ((a'(t)b''(t)-a''(t)b'(t))/T^4) * (-b'(t),a'(t))
     *                                       T = sqrt(a'(t)^2+b'(t)^2)
	 */

	final public GeoVector CurvatureVectorCurve(String label,GeoPoint A,GeoCurveCartesian f){

		  AlgoCurvatureVectorCurve algo = new AlgoCurvatureVectorCurve(cons,label,A,f);
		  GeoVector v = algo.getVector();
		  return v;

	}

	

	/**
	 * Calculate Curvature for function: k(x) = f''/T^3, T = sqrt(1+(f')^2)
	 */

	final public GeoNumeric Curvature(String label,GeoPoint A,GeoFunction f){

		  AlgoCurvature algo = new AlgoCurvature(cons,label,A,f);
		  GeoNumeric k = algo.getResult();
		  return k;

	}

		

	/**
	 * Calculate Curvature for Curve: k(t) = (a'(t)b''(t)-a''(t)b'(t))/T^3, T = sqrt(a'(t)^2+b'(t)^2)
	 */

	final public GeoNumeric CurvatureCurve(String label,GeoPoint A, GeoCurveCartesian f){

		  AlgoCurvatureCurve algo = new AlgoCurvatureCurve(cons,label,A,f);
		  GeoNumeric k = algo.getResult();
		  return k;

	}

	

	/**
	 * Osculating Circle of a function f in point A
	 */

	final public GeoConic OsculatingCircle(String label,GeoPoint A,GeoFunction f){

		  AlgoOsculatingCircle algo = new AlgoOsculatingCircle(cons,label,A,f);
		  GeoConic circle = algo.getCircle();
		  return circle;

	}

	

	/**
	 * Osculating Circle of a curve f in point A
	 */

	final public GeoConic OsculatingCircleCurve(String label,GeoPoint A,GeoCurveCartesian f){

		  AlgoOsculatingCircleCurve algo = new AlgoOsculatingCircleCurve(cons,label,A,f);
		  GeoConic circle = algo.getCircle();
		  return circle;

	}

	

	/**
	 * Calculate Function Length between the numbers A and B: integral from A to B on T = sqrt(1+(f')^2)
	 */

	final public GeoNumeric FunctionLength(String label,GeoFunction f,GeoNumeric A,GeoNumeric B){

		  AlgoLengthFunction algo = new AlgoLengthFunction(cons,label,f,A,B);
		  GeoNumeric length = algo.getLength();
		  return length;

	}

	

	/**
	 * Calculate Function Length between the points A and B: integral from A to B on T = sqrt(1+(f')^2)
	 */

	final public GeoNumeric FunctionLength2Points(String label,GeoFunction f,GeoPoint A,GeoPoint B){

		  AlgoLengthFunction2Points algo = new AlgoLengthFunction2Points(cons,label,f,A,B);
		  GeoNumeric length = algo.getLength();
		  return length;

	}

	

	/**

	 * Calculate Curve Length between the parameters t0 and t1: integral from t0 to t1 on T = sqrt(a'(t)^2+b'(t)^2)

	 */

	final public GeoNumeric CurveLength(String label, GeoCurveCartesian c, GeoNumeric t0,GeoNumeric t1){

		  AlgoLengthCurve algo = new AlgoLengthCurve(cons,label,c,t0,t1);
		  GeoNumeric length = algo.getLength();
		  return length;

	}

	

	/**
	 * Calculate Curve Length between the points A and B: integral from t0 to t1 on T = sqrt(a'(t)^2+b'(t)^2)
	 */
	final public GeoNumeric CurveLength2Points(String label, GeoCurveCartesian c, GeoPoint A,GeoPoint B){
		  AlgoLengthCurve2Points algo = new AlgoLengthCurve2Points(cons,label,c,A,B);
		  GeoNumeric length = algo.getLength();
		  return length;
	}


	/** 
	 * tangent to Curve f in point P: (b'(t), -a'(t), a'(t)*b(t)-a(t)*b'(t))
	 */
	final public GeoLine Tangent(String label,GeoPoint P,GeoCurveCartesian f) {
		AlgoTangentCurve algo = new AlgoTangentCurve(cons, label, P, f);
		GeoLine t = algo.getTangent();
		t.setToExplicit();
		t.update();     
		notifyUpdate(t);
		return t;
	}

	/**
	 * Victor Franco Espino 18-04-2007: End new commands 
	 */


	

	/***********************************
	 * PACKAGE STUFF
	 ***********************************/

	/** if x is nearly zero, 0.0 is returned,
	 *  else x is returned
	 */
	final public double chop(double x) {
		if (isZero(x))
			return 0.0d;
		else
			return x;
	}
	
	/** is abs(x) < epsilon ? */
	final public static boolean isZero(double x) {
		return -EPSILON < x && x < EPSILON;
	}

	final boolean isZero(double[] a) {
		for (int i = 0; i < a.length; i++) {
			if (!isZero(a[i]))
				return false;
		}
		return true;
	}

	final public boolean isInteger(double x) {
		if (x > 1E17)
			return true;
		else
			return isEqual(x, Math.round(x));		
	}

	/**
	 * Returns whether x is equal to y	 
	 * infinity == infinity returns true eg 1/0	 
	 * -infinity == infinity returns false	 eg -1/0
	 * -infinity == -infinity returns true
	 * undefined == undefined returns false eg 0/0	 
	 */
	final public static boolean isEqual(double x, double y) {	
		if (x == y) // handles infinity and NaN cases
			return true;
		else
			return x - EPSILON <= y && y <= x + EPSILON;
	}
	
	final public static boolean isEqual(double x, double y, double eps) {		
		if (x == y) // handles infinity and NaN cases
			return true;
		else
		return x - eps < y && y < x + eps;
	}
	
	/**
	 * Returns whether x is greater than y	 	 
	 */
	final public static boolean isGreater(double x, double y) {
		return x > y + EPSILON;
	}
	
	/**
	 * Returns whether x is greater than y	 	 
	 */
	final public static boolean isGreater(double x, double y,double eps) {
		return x > y + eps;
	}
	/**
	 * Returns whether x is greater than or equal to y	 	 
	 */
	final public static boolean isGreaterEqual(double x, double y) {
		return x + EPSILON > y;
	}

	// compares double arrays: 
	// yields true if (isEqual(a[i], b[i]) == true) for all i
	final boolean isEqual(double[] a, double[] b) {
		for (int i = 0; i < a.length; ++i) {
			if (!isEqual(a[i], b[i]))
				return false;
		}
		return true;
	}
	
    final public double convertToAngleValue(double val) {
		if (val > EPSILON && val < PI_2) return val;
		
    	double value = val % PI_2; 
		if (isZero(value)) {
			if (val < 1.0) value = 0.0;
			else value = PI_2; 
		}
    	else if (value < 0.0)  {
    		value += PI_2;
    	} 
    	return value;
    }

    /*
	// calc acos(x). returns 0 for x > 1 and pi for x < -1    
	final static double trimmedAcos(double x) {
		if (Math.abs(x) <= 1.0d)
			return Math.acos(x);
		else if (x > 1.0d)
			return 0.0d;
		else if (x < -1.0d)
			return Math.PI;
		else
			return Double.NaN;
	}*/

	/** returns max of abs(a[i]) */
	final static double maxAbs(double[] a) {
		double temp, max = Math.abs(a[0]);
		for (int i = 1; i < a.length; i++) {
			temp = Math.abs(a[i]);
			if (temp > max)
				max = temp;
		}
		return max;
	}

	// copy array a to array b
	final static void copy(double[] a, double[] b) {
		for (int i = 0; i < a.length; i++) {
			b[i] = a[i];
		}
	}

	// change signs of double array values, write result to array b
	final static void negative(double[] a, double[] b) {
		for (int i = 0; i < a.length; i++) {
			b[i] = -a[i];
		}
	}

	// c[] = a[] / b
	final static void divide(double[] a, double b, double[] c) {
		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] / b;
		}
	}
	
	// temp for buildEquation    
	private double[] temp;// = new double[6];

	// lhs of implicit equation without constant coeff
	final private StringBuilder buildImplicitVarPart(		
		double[] numbers,
		String[] vars, 
		boolean KEEP_LEADING_SIGN,
		boolean CANCEL_DOWN) {
		
		temp = new double[numbers.length];
			
		int leadingNonZero = -1;
		sbBuildImplicitVarPart.setLength(0);

		for (int i = 0; i < vars.length; i++) {
			if (!isZero(numbers[i])) {
				leadingNonZero = i;
				break;
			}
		}
		
		if (CANCEL_DOWN) {
			// check if integers and divide through gcd
			boolean allIntegers = true;
			for (int i = 0; i < numbers.length; i++) {
				allIntegers = allIntegers && isInteger(numbers[i]);			
			}		
			if (allIntegers) {
				// divide by greates common divisor
				divide(numbers, gcd(numbers), numbers);
			}
		}

		// no left hand side        
		if (leadingNonZero == -1) {
			sbBuildImplicitVarPart.append("0");
			return sbBuildImplicitVarPart;
		}
		
		// don't change leading coefficient
		if (KEEP_LEADING_SIGN) {
			copy(numbers, temp);
		} else {
			if (numbers[leadingNonZero] < 0)
				negative(numbers, temp);
			else
				copy(numbers, temp);
		}

		// BUILD EQUATION STRING                              
		// valid left hand side 
		// leading coefficient
		String strCoeff = formatCoeff(temp[leadingNonZero]);
		sbBuildImplicitVarPart.append(strCoeff);
		sbBuildImplicitVarPart.append(vars[leadingNonZero]);

		// other coefficients on lhs
		String sign;
		double abs;
		for (int i = leadingNonZero + 1; i < vars.length; i++) {
			if (temp[i] < 0.0) {
				sign = " - ";
				abs = -temp[i];
			} else {
				sign = " + ";
				abs = temp[i];
			}

			if (abs >= PRINT_PRECISION || useSignificantFigures) {
				sbBuildImplicitVarPart.append(sign);
				sbBuildImplicitVarPart.append(formatCoeff(abs));
				sbBuildImplicitVarPart.append(vars[i]);
			}
		}
		return sbBuildImplicitVarPart;
	}
	private StringBuilder sbBuildImplicitVarPart = new StringBuilder(80);

	public final StringBuilder buildImplicitEquation(
		double[] numbers,
		String[] vars,
		boolean KEEP_LEADING_SIGN,
		boolean CANCEL_DOWN,
		char op) {

		sbBuildImplicitEquation.setLength(0);
		sbBuildImplicitEquation.append(buildImplicitVarPart(numbers, vars, KEEP_LEADING_SIGN || (op == '='), CANCEL_DOWN));
		
		if (casPrintForm == ExpressionNode.STRING_TYPE_MATH_PIPER && op == '=') {
				sbBuildImplicitEquation.append(" == ");
		} else {
				sbBuildImplicitEquation.append(' ');
				sbBuildImplicitEquation.append(op);
				sbBuildImplicitEquation.append(' ');
		}
		
		// temp is set by buildImplicitVarPart
		sbBuildImplicitEquation.append(format(-temp[vars.length]));

		return sbBuildImplicitEquation;
	}
	private StringBuilder sbBuildImplicitEquation = new StringBuilder(80);

	// lhs of lhs = 0
	final public StringBuilder buildLHS(double[] numbers, String[] vars, boolean KEEP_LEADING_SIGN, boolean CANCEL_DOWN) {
		sbBuildLHS.setLength(0);
		sbBuildLHS.append(buildImplicitVarPart(numbers, vars, KEEP_LEADING_SIGN, CANCEL_DOWN));

		// add constant coeff
		double coeff = temp[vars.length];
		if (Math.abs(coeff) >= PRINT_PRECISION || useSignificantFigures) {
			sbBuildLHS.append(' ');
			sbBuildLHS.append(sign(coeff));
			sbBuildLHS.append(' ');
			sbBuildLHS.append(format(Math.abs(coeff)));
		}
		return sbBuildLHS;
	}
	private StringBuilder sbBuildLHS = new StringBuilder(80);

	// form: y��� = f(x) (coeff of y = 0)
	public final StringBuilder buildExplicitConicEquation(
		double[] numbers,
		String[] vars,
		int pos,
		boolean KEEP_LEADING_SIGN) {
		// y���-coeff is 0
		double d, dabs, q = numbers[pos];
		// coeff of y��� is 0 or coeff of y is not 0
		if (isZero(q))
			return buildImplicitEquation(numbers, vars, KEEP_LEADING_SIGN, true, '=');

		int i, leadingNonZero = numbers.length;
		for (i = 0; i < numbers.length; i++) {
			if (i != pos
				&& // except y��� coefficient                
				(Math.abs(numbers[i]) >= PRINT_PRECISION || useSignificantFigures)) {
				leadingNonZero = i;
				break;
			}
		}

		// BUILD EQUATION STRING                              
		sbBuildExplicitConicEquation.setLength(0);
		sbBuildExplicitConicEquation.append(vars[pos]);
		sbBuildExplicitConicEquation.append(" = ");

		if (leadingNonZero == numbers.length) {
			sbBuildExplicitConicEquation.append("0");
			return sbBuildExplicitConicEquation;
		} else if (leadingNonZero == numbers.length - 1) {
			// only constant coeff
			d = -numbers[leadingNonZero] / q;
			sbBuildExplicitConicEquation.append(format(d));
			return sbBuildExplicitConicEquation;
		} else {
			// leading coeff
			d = -numbers[leadingNonZero] / q;
			sbBuildExplicitConicEquation.append(formatCoeff(d));
			sbBuildExplicitConicEquation.append(vars[leadingNonZero]);

			// other coeffs
			for (i = leadingNonZero + 1; i < vars.length; i++) {
				if (i != pos) {
					d = -numbers[i] / q;
					dabs = Math.abs(d);
					if (dabs >= PRINT_PRECISION || useSignificantFigures) {
						sbBuildExplicitConicEquation.append(' ');
						sbBuildExplicitConicEquation.append(sign(d));
						sbBuildExplicitConicEquation.append(' ');
						sbBuildExplicitConicEquation.append(formatCoeff(dabs));
						sbBuildExplicitConicEquation.append(vars[i]);
					}
				}
			}

			// constant coeff
			d = -numbers[i] / q;
			dabs = Math.abs(d);
			if (dabs >= PRINT_PRECISION || useSignificantFigures) {
				sbBuildExplicitConicEquation.append(' ');
				sbBuildExplicitConicEquation.append(sign(d));
				sbBuildExplicitConicEquation.append(' ');
				sbBuildExplicitConicEquation.append(format(dabs));
			}
			
			//Application.debug(sbBuildExplicitConicEquation.toString());
			
			return sbBuildExplicitConicEquation;
		}
	}
	private StringBuilder sbBuildExplicitConicEquation = new StringBuilder(80);

	// y = k x + d
	final StringBuilder buildExplicitLineEquation(
		double[] numbers,
		String[] vars,
		char op) {

		double d, dabs, q = numbers[1];		
		sbBuildExplicitLineEquation.setLength(0);
		
		//	BUILD EQUATION STRING                      
		// special case
		// y-coeff is 0: form x = constant
		if (isZero(q)) {
			sbBuildExplicitLineEquation.append("x");
						
			if (casPrintForm == ExpressionNode.STRING_TYPE_MATH_PIPER) {
				sbBuildExplicitLineEquation.append(" == ");
			}
			else {
				sbBuildExplicitLineEquation.append(' ');
				if(numbers[0]<Kernel.MIN_PRECISION){
					op = oppositeSign(op);
				}
				sbBuildExplicitLineEquation.append(op);
				sbBuildExplicitLineEquation.append(' ');
			}
			
			sbBuildExplicitLineEquation.append(format(-numbers[2] / numbers[0]));
			return sbBuildExplicitLineEquation;
		}

		// standard case: y-coeff not 0
		sbBuildExplicitLineEquation.append("y");
		if (casPrintForm == ExpressionNode.STRING_TYPE_MATH_PIPER) {
			sbBuildExplicitLineEquation.append(" == ");
		}
		else {
			sbBuildExplicitLineEquation.append(' ');
			if(numbers[1] <Kernel.MIN_PRECISION){
				op = oppositeSign(op);
			}
			sbBuildExplicitLineEquation.append(op);
			sbBuildExplicitLineEquation.append(' ');
		}

		// x coeff
		d = -numbers[0] / q;
		dabs = Math.abs(d);
		if (dabs >= PRINT_PRECISION || useSignificantFigures) {
			sbBuildExplicitLineEquation.append(formatCoeff(d));
			sbBuildExplicitLineEquation.append('x');

			// constant            
			d = -numbers[2] / q;
			dabs = Math.abs(d);
			if (dabs >= PRINT_PRECISION || useSignificantFigures) {
				sbBuildExplicitLineEquation.append(' ');
				sbBuildExplicitLineEquation.append(sign(d));
				sbBuildExplicitLineEquation.append(' ');
				sbBuildExplicitLineEquation.append(format(dabs));
			}
		} else {
			// only constant
			sbBuildExplicitLineEquation.append(format(-numbers[2] / q));
		}
		return sbBuildExplicitLineEquation;
	}
	/**
	 * Inverts the > or < sign
	 * @param op =,<,>,\u2264 or \u2265
	 * @return opposite sign
	 */
	public static char oppositeSign(char op) {
		switch(op) {
		case '=':return '=';
		case '<':return '>';
		case '>':return '<';
		case '\u2264': return '\u2265';
		case '\u2265': return '\u2264';
		default: return '?'; //should never happen
		}
	}

	private StringBuilder sbBuildExplicitLineEquation = new StringBuilder(50);

	/*
	final private String formatAbs(double x) {
		if (isZero(x))
			return "0";
		else
			return formatNF(Math.abs(x));
	}*/

	/** doesn't show 1 or -1 */
	final private String formatCoeff(double x) {
		if (Math.abs(x) == 1.0) {
			if (x > 0.0)
				return "";
			else
				return "-";
		} else {
			String numberStr = format(x);
			switch (casPrintForm) {
				case ExpressionNode.STRING_TYPE_MATH_PIPER:
				case ExpressionNode.STRING_TYPE_MAXIMA:
				case ExpressionNode.STRING_TYPE_MPREDUCE:
					return numberStr + "*";
					
				default:
					// standard case
					return numberStr;
			}
		}
	}
	
	
	
	////////////////////////////////////////////////
	// FORMAT FOR NUMBERS
	////////////////////////////////////////////////
	
	public double axisNumberDistance(double units, NumberFormat numberFormat){

		// calc number of digits
		int exp = (int) Math.floor(Math.log(units) / Math.log(10));
		int maxFractionDigtis = Math.max(-exp, getPrintDecimals());
		
		// format the numbers
		if (numberFormat instanceof DecimalFormat)
			((DecimalFormat) numberFormat).applyPattern("###0.##");	
		numberFormat.setMaximumFractionDigits(maxFractionDigtis);
		
		// calc the distance
		double pot = Math.pow(10, exp);
		double n = units / pot;
		double distance;

		if (n > 5) {
			distance = 5 * pot;
		} else if (n > 2) {
			distance = 2 * pot;
		} else {
			distance = pot;
		}
		
		return distance;
	}
	
	
	private StringBuilder formatSB;
	
	/**
	 * Formats the value of x using the currently set
	 * NumberFormat or ScientificFormat. This method also
	 * takes getCasPrintForm() into account.
	 * 
	 * converts to localised digits if appropriate
	 */
	final public String format(double x) {	
		
		String ret = formatRaw(x);
		
		if (Application.unicodeZero != '0') {
			ret = internationalizeDigits(ret);						
		} 
		
		return ret;
		
	}
	
	// needed so that can be turned off 
	public static boolean internationalizeDigits = true;
	
	/*
	 * swaps the digits in num to the current locale's
	 */
	public String internationalizeDigits(String num) {
		
		if (!internationalizeDigits) return num;
		
		if (formatSB == null) formatSB = new StringBuilder(17);
		else formatSB.setLength(0);
		
		// make sure minus sign works in Arabic
		boolean reverseOrder = num.charAt(0) == '-' && app.isRightToLeftDigits();
		
		if (reverseOrder) formatSB.append(Unicode.RightToLeftMark);
		
		for (int i = 0 ; i < num.length() ; i++) {
			char c = num.charAt(i);
			//char c = reverseOrder ? num.charAt(length - 1 - i) : num.charAt(i);
			if (c == '.') c = Application.unicodeDecimalPoint;
			else if (c >= '0' && c <= '9') {
				
				c += Application.unicodeZero - '0'; // convert to eg Arabic Numeral
				
			}

			// make sure the minus is treated as part of the number in eg Arabic
			//if ( reverseOrder && c=='-'){
			//	formatSB.append(Unicode.RightToLeftUnaryMinusSign);
			//} 
			//else
				formatSB.append(c);
		}
		
		if (reverseOrder) formatSB.append(Unicode.RightToLeftMark);

		
		return formatSB.toString();
		
	}

		

	/**
	 * Formats the value of x using the currently set
	 * NumberFormat or ScientificFormat. This method also
	 * takes getCasPrintForm() into account.
	 */
	final public String formatRaw(double x) {
		
		// format integers without significant figures
		boolean isLongInteger = false;
		long rounded = Math.round(x);
		if (x == rounded && x >= Long.MIN_VALUE && x < Long.MAX_VALUE)
			isLongInteger = true;
		
		switch (casPrintForm) {
			// number formatting for XML string output
			case ExpressionNode.STRING_TYPE_GEOGEBRA_XML:
				if (isLongInteger)
					return Long.toString(rounded);
				else
					return Double.toString(x);		
		
			// number formatting for CAS
			case ExpressionNode.STRING_TYPE_MATH_PIPER:				
			case ExpressionNode.STRING_TYPE_JASYMCA:		
			case ExpressionNode.STRING_TYPE_MAXIMA:
			case ExpressionNode.STRING_TYPE_MPREDUCE:
				if (Double.isNaN(x))
					return " 1/0 ";	
				else if (Double.isInfinite(x)) {
					if (casPrintForm == ExpressionNode.STRING_TYPE_MPREDUCE) 
						return (x<0) ? "-infinity" : "infinity";
					else if (casPrintForm == ExpressionNode.STRING_TYPE_MAXIMA) 
						return (x<0) ? "-inf" : "inf";					
					else
						return Double.toString(x); // "Infinity" or "-Infinity"
 				}
				else if (isLongInteger)
					return Long.toString(rounded);
				else {			
					double abs = Math.abs(x);
					// number small enough that Double.toString() won't create E notation
					if (abs >= 10E-3 && abs < 10E7)
						return Double.toString(x);	

					// number would produce E notation with Double.toString()
					else {						
						// convert scientific notation 1.0E-20 to 1*10^(-20) 
						String scientificStr = Double.toString(x);
						return convertScientificNotation(scientificStr);
					}	
				}
								
			// number formatting for screen output
			default:
				if (Double.isNaN(x))
					return "?";	
				else if (Double.isInfinite(x)) {
					return (x > 0) ? "\u221e" : "-\u221e"; // infinity
				}
				else if (x == Math.PI)
					return casPrintFormPI;
					
					
				// ROUNDING hack							
				// NumberFormat and SignificantFigures use ROUND_HALF_EVEN as 
				// default which is not changeable, so we need to hack this 
				// to get ROUND_HALF_UP like in schools: increase abs(x) slightly
				//    x = x * ROUND_HALF_UP_FACTOR;
				// We don't do this for large numbers as 
				double abs = Math.abs(x);
				if (!isLongInteger && ((abs < 10E7 && nf.getMaximumFractionDigits()<10)||abs < 1000)) {
					// increase abs(x) slightly to round up
					x = x * ROUND_HALF_UP_FACTOR;
				}
	
				if (useSignificantFigures)
					return formatSF(x);
				else			
					return formatNF(x);
			}								
	}
	
	/**
	 * Converts 5.1E-20 to 5.1*10^(-20) or 5.1 \cdot 10^{-20} depending on current print form
	 */
	public String convertScientificNotation(String scientificStr) { 
		StringBuilder sb = new StringBuilder(scientificStr.length() * 2);
		boolean Efound = false;
		for (int i=0; i < scientificStr.length(); i++) {
			char ch = scientificStr.charAt(i);
			if (ch == 'E') {
				if (casPrintForm == ExpressionNode.STRING_TYPE_LATEX)
					sb.append(" \\cdot 10^{");
				else
					sb.append("*10^(");
				Efound = true;
			} 
			else if (ch != '+'){
				sb.append(ch);
			}
		}
		if (Efound) {
			if (casPrintForm == ExpressionNode.STRING_TYPE_LATEX)
				sb.append("}");
			else
				sb.append(")");
		}				
		
		return sb.toString();
	}
	
	
	/**
	 * Uses current NumberFormat nf to format a number.
	 */
	final private String formatNF(double x) {
		// "<=" catches -0.0000000000000005
		// should be rounded to -0.000000000000001 (15 d.p.)
		// but nf.format(x) returns "-0" 
		if (-PRINT_PRECISION / 2 <= x && x < PRINT_PRECISION / 2) {
			// avoid output of "-0" for eg -0.0004
			return "0";
		} else {
			// standard case
			return nf.format(x);
		}
	}

	/**
	 * Uses current ScientificFormat sf to format a number. Makes sure ".123" is
	 * returned as "0.123".
	 */
	final private String formatSF(double x) {
		if (sbFormatSF == null)
			sbFormatSF = new StringBuilder();
		else
			sbFormatSF.setLength(0);

		// get scientific format
		String absStr;
		if (x == 0) {
			// avoid output of "-0.00"
			absStr = sf.format(0);
		}
		else if (x > 0) {
			absStr = sf.format(x);
		} 
		else {
			sbFormatSF.append('-');
			absStr = sf.format(-x);
		}

		// make sure ".123" is returned as "0.123".
		if (absStr.charAt(0) == '.')
			sbFormatSF.append('0');
		sbFormatSF.append(absStr);

		return sbFormatSF.toString();
	}
	private StringBuilder sbFormatSF;
	
	/**
	 * calls formatPiERaw() and converts to localised digits if appropriate
	 */
	final public String formatPiE(double x, NumberFormat numF) {	
		if (Application.unicodeZero != '0') {
			
			String num = formatPiERaw(x, numF);
			
			return internationalizeDigits(num);
			
			
		} else return formatPiERaw(x, numF);
		
	}

	final public String formatPiERaw(double x, NumberFormat numF) {		
		// PI
		if (x == Math.PI) {
			return casPrintFormPI;
		}
				
		// 	MULTIPLES OF PI/2
		// i.e. x = a * pi/2
		double a = 2*x / Math.PI;
		int aint = (int) Math.round(a);
		if (sbFormat == null)
			sbFormat = new StringBuilder();
		sbFormat.setLength(0);
		if (isEqual(a, aint, STANDARD_PRECISION)) {	
			switch (aint) {		
				case 0:
					return "0";		
					
				case 1: // pi/2
					sbFormat.append(casPrintFormPI);
					sbFormat.append("/2");
					return sbFormat.toString();
					
				case -1: // -pi/2
					sbFormat.append('-');
					sbFormat.append(casPrintFormPI);
					sbFormat.append("/2");
					return sbFormat.toString();
					
				case 2: // 2pi/2 = pi
					return casPrintFormPI;
					
				case -2: // -2pi/2 = -pi
					sbFormat.append('-');
					sbFormat.append(casPrintFormPI);
					return sbFormat.toString();
				
				default:
					// 	even
					long half = aint / 2;			
					if (aint == 2 * half) {		
						// half * pi
						sbFormat.append(half);
						if (casPrintForm != ExpressionNode.STRING_TYPE_GEOGEBRA)
							sbFormat.append("*");
						sbFormat.append(casPrintFormPI);
						return sbFormat.toString();
					}
					// odd
					else {		
						// aint * pi/2
						sbFormat.append(aint);
						if (casPrintForm != ExpressionNode.STRING_TYPE_GEOGEBRA)
							sbFormat.append("*");
						sbFormat.append(casPrintFormPI);
						sbFormat.append("/2");
						return sbFormat.toString();
					}
			}									
		}		
		
		// STANDARD CASE
		// use numberformat to get number string
		// checkDecimalFraction() added to avoid 2.19999999999999 when set to 15dp
		String str = numF.format(checkDecimalFraction(x));
		sbFormat.append(str);	
		// if number is in scientific notation and ends with "E0", remove this
		if (str.endsWith("E0"))
			sbFormat.setLength(sbFormat.length() - 2);
		return sbFormat.toString();
	}
	private StringBuilder sbFormat;


	final public String formatSignedCoefficient(double x) {
		if (x == -1.0)
			return "- ";
		if (x == 1.0)
			return "+ ";

		return formatSigned(x).toString();
	}
		
	final public StringBuilder formatSigned(double x) {
		sbFormatSigned.setLength(0);		
		
		if (x >= 0.0d) {
			sbFormatSigned.append("+ ");
			sbFormatSigned.append( format(x));
			return sbFormatSigned;
		} else {
			sbFormatSigned.append("- ");
			sbFormatSigned.append( format(-x));
			return sbFormatSigned;
		}
	}
	private StringBuilder sbFormatSigned = new StringBuilder(40);

	final public StringBuilder formatAngle(double phi) {
		// STANDARD_PRECISION * 10 as we need a little leeway as we've converted from radians
		return formatAngle(phi, 10);
	}
	
	final public StringBuilder formatAngle(double phi, double precision) {
		sbFormatAngle.setLength(0);
		switch (casPrintForm) {
			case ExpressionNode.STRING_TYPE_MATH_PIPER:
			case ExpressionNode.STRING_TYPE_JASYMCA:
			case ExpressionNode.STRING_TYPE_MPREDUCE: 
				if (angleUnit == ANGLE_DEGREE) {
					sbFormatAngle.append("(");
					// STANDARD_PRECISION * 10 as we need a little leeway as we've converted from radians
					sbFormatAngle.append(format(checkDecimalFraction(Math.toDegrees(phi), precision)));
					sbFormatAngle.append("*");
					sbFormatAngle.append("\u00b0");
					sbFormatAngle.append(")");
				} else {
					sbFormatAngle.append(format(phi));					
				}
				return sbFormatAngle;				
				
			default:
				// STRING_TYPE_GEOGEBRA_XML
				// STRING_TYPE_GEOGEBRA

				if (Double.isNaN(phi)) {
					sbFormatAngle.append("?");
					return sbFormatAngle;
				}		
				
				if (angleUnit == ANGLE_DEGREE) {
					boolean rtl = app.isRightToLeftDigits();
					if (rtl) {
						sbFormatAngle.append(Unicode.degreeChar);
					}
					
					phi = Math.toDegrees(phi);
					if (phi < 0) 
						phi += 360;	
					else if (phi > 360)
						phi = phi % 360;
					// STANDARD_PRECISION * 10 as we need a little leeway as we've converted from radians
					sbFormatAngle.append(format(checkDecimalFraction(phi, precision)));
					
					if (casPrintForm == ExpressionNode.STRING_TYPE_GEOGEBRA_XML) {
						sbFormatAngle.append("*");
					}

					if (!rtl) sbFormatAngle.append(Unicode.degreeChar);

					return sbFormatAngle;
				} 
				else {
					// RADIANS
					sbFormatAngle.append(format(phi));
					
					if (casPrintForm != ExpressionNode.STRING_TYPE_GEOGEBRA_XML) {
						sbFormatAngle.append(" rad");
					}
					return sbFormatAngle;
				}
		}
		
		
	}
	private StringBuilder sbFormatAngle = new StringBuilder(40);

	final private static char sign(double x) {
		if (x > 0)
			return '+';
		else
			return '-';
	}

	/**
	 * greatest common divisor
	 */
	final public static long gcd(long m, long n) {
		// Return the GCD of positive integers m and n.
		if (m == 0 || n == 0)
			return Math.max(Math.abs(m), Math.abs(n));

		long p = m, q = n;
		while (p % q != 0) {
			long r = p % q;
			p = q;
			q = r;
		}
		return q;
	}

	/**
	 * Compute greatest common divisor of given doubles.
	 * Note: all double values are cast to long.
	 */
	final public static double gcd(double[] numbers) {
		long gcd = (long) numbers[0];
		for (int i = 0; i < numbers.length; i++) {
			gcd = gcd((long) numbers[i], gcd);
		}
		return Math.abs(gcd);
	}
	
	/**
	 * Round a double to the given scale
	 * e.g. roundToScale(5.32, 1) = 5.0,
	 * 	  roundToScale(5.32, 0.5) = 5.5,
	 * 	  roundToScale(5.32, 0.25) = 5.25,
	 *  	  roundToScale(5.32, 0.1) = 5.3
	 */
	final public static double roundToScale(double x, double scale) {
		if (scale == 1.0)
			return Math.round(x);
		else {
			return Math.round(x / scale) * scale;					
		}				
	}
	
	/**
	 * Checks if x is close (Kernel.MIN_PRECISION) to a decimal fraction,  
	 * eg 2.800000000000001. If it is, the decimal fraction eg 2.8 is returned, 
	 * otherwise x is returned.
	 */	
	/**
	 * Checks if x is close (Kernel.MIN_PRECISION) to a decimal fraction, eg
	 * 2.800000000000001. If it is, the decimal fraction eg 2.8 is returned,
	 * otherwise x is returned.
	 */
	final public double checkDecimalFraction(double x, double precision) {
		
		//Application.debug(precision+" ");
		precision = Math.pow(10, Math.floor(Math.log(Math.abs(precision))/Math.log(10)));
		
		double fracVal = x * INV_MIN_PRECISION;
		double roundVal = Math.round(fracVal);
		//Application.debug(precision+" "+x+" "+fracVal+" "+roundVal+" "+isEqual(fracVal, roundVal, precision)+" "+roundVal / INV_MIN_PRECISION);
		if (isEqual(fracVal, roundVal, STANDARD_PRECISION * precision))
			return roundVal / INV_MIN_PRECISION;
		else
			return x;
	}
	
	final public double checkDecimalFraction(double x) {
		return checkDecimalFraction(x, 1);
	}

	/**
	 * Checks if x is very close (1E-8) to an integer. If it is,
	 * the integer value is returned, otherwise x is returnd.
	 */	
	final public double checkInteger(double x) {		
		double roundVal = Math.round(x);
		if (Math.abs(x - roundVal) < EPSILON)
			return roundVal;
		else
			return x;
	}
			
	/*******************************************************
	 * SAVING
	 *******************************************************/

	private boolean isSaving;
	
	public synchronized boolean isSaving() {
		return isSaving;
	}
	
	public synchronized void setSaving(boolean saving) {
		isSaving = saving;
	}
	
	/**
	 * Returns the kernel settings in XML format.
	 */
	public void getKernelXML(StringBuilder sb, boolean asPreference) {
	
		// kernel settings
		sb.append("<kernel>\n");
	
		// continuity: true or false, since V3.0
		sb.append("\t<continuous val=\"");
		sb.append(isContinuous());
		sb.append("\"/>\n");
		
		if (useSignificantFigures) {
			// significant figures
			sb.append("\t<significantfigures val=\"");
			sb.append(getPrintFigures());
			sb.append("\"/>\n");			
		}
		else
		{
			// decimal places
			sb.append("\t<decimals val=\"");
			sb.append(getPrintDecimals());
			sb.append("\"/>\n");
		}
		
		// angle unit
		sb.append("\t<angleUnit val=\"");
		sb.append(angleUnit == Kernel.ANGLE_RADIANT ? "radiant" : "degree");
		sb.append("\"/>\n");
		
		// algebra style
		sb.append("\t<algebraStyle val=\"");
		sb.append(algebraStyle);
		sb.append("\"/>\n");
		
		// coord style
		sb.append("\t<coordStyle val=\"");
		sb.append(getCoordStyle());
		sb.append("\"/>\n");
		
		// whether return angle from inverse trigonometric functions
		if (!asPreference) {
			sb.append("\t<angleFromInvTrig val=\"");
			sb.append(getInverseTrigReturnsAngle());
			sb.append("\"/>\n");
		}
		
		// animation
		if (isAnimationRunning()) {
			sb.append("\t<startAnimation val=\"");
			sb.append(isAnimationRunning());
			sb.append("\"/>\n");
		}
		
		if (asPreference) {
			sb.append("\t<localization");
			sb.append(" digits=\"");
			sb.append(app.isUsingLocalizedDigits());
			sb.append("\"");
			sb.append(" labels=\"");
			sb.append(app.isUsingLocalizedLabels());
			sb.append("\"");
			sb.append("/>\n");
		}
	
		sb.append("</kernel>\n");
	}
	
	
	/**
	 * Returns whether localized command names are printed. 
	 * @return true for localized command names and false for internal command names
	 */
	public boolean isPrintLocalizedCommandNames() {
		return translateCommandName;
	}

	/**
	 * Sets whether localized command names are printed. 
	 * @param b true to print localized command names and false to print internal command names
	 */
	public void setPrintLocalizedCommandNames(boolean b) {
		translateCommandName = b;
	}
	
	/**
	 * Returns whether the parser should read internal command names and not translate them. 
	 * @return true if internal command names should be read
	 */
	public boolean isUsingInternalCommandNames() {
		return useInternalCommandNames;
	}

	/**
	 * Sets whether the parser should read internal command names and not translate them. 
	 * @param b true if internal command names should be read
	 */
	public void setUseInternalCommandNames(boolean b) {
		useInternalCommandNames = b;		
	}


	/**
	 * States whether the continuity heuristic is active.
	 */
	final public boolean isContinuous() {
		return continuous;
	}

	/**
	 * Turns the continuity heuristic on or off.
	 * Note: the macro kernel always turns continuity off. 
	 */
	public void setContinuous(boolean continuous) {
		this.continuous = continuous;
	}

	public final boolean isAllowVisibilitySideEffects() {
		return allowVisibilitySideEffects;
	}

	public final void setAllowVisibilitySideEffects(
			boolean allowVisibilitySideEffects) {
		this.allowVisibilitySideEffects = allowVisibilitySideEffects;
	}

	public boolean isMacroKernel() {
		return false;
	}
	
	private AnimationManager animationManager;
	
	final public AnimationManager getAnimatonManager() {		
		if (animationManager == null) {
			animationManager = new AnimationManager(this);			
		}
		return animationManager;		
	}
	
	final public boolean isAnimationRunning() {
		return animationManager != null && animationManager.isRunning();
	}
	
	final public boolean isAnimationPaused() {
		return animationManager != null && animationManager.isPaused();
	}
	
	final public boolean needToShowAnimationButton() {
		return animationManager != null && animationManager.needToShowAnimationButton();		
	}
	
	final public void udpateNeedToShowAnimationButton() {
		if (animationManager != null)
			animationManager.updateNeedToShowAnimationButton();		
		
	}	

	/**
	 * Turns silent mode on (true) or off (false). In silent mode, commands can
	 * be used to create objects without any side effects, i.e.
	 * no labels are created, algorithms are not added to the construction
	 * list and the views are not notified about new objects. 
	 */
	public final void setSilentMode(boolean silentMode) {
		
		
		this.silentMode = silentMode;
		
		// no new labels, no adding to construction list
		cons.setSuppressLabelCreation(silentMode);
		
		// no notifying of views
		//ggb3D - 2009-07-17
		//removing :
		//notifyViewsActive = !silentMode;
		//(seems not to work with loading files)


		
		//Application.printStacktrace(""+silentMode);
		
	}
	

	/**
	 * Returns whether silent mode is turned on.
	 * @see setSilentMode()
	 */
	public final boolean isSilentMode() {
		return silentMode;
	}
	
	/**
	 * Sets whether unknown variables should be resolved as GeoDummyVariable objects. 
	 */
	public final void setResolveUnkownVarsAsDummyGeos(boolean resolveUnkownVarsAsDummyGeos) {
		this.resolveUnkownVarsAsDummyGeos = resolveUnkownVarsAsDummyGeos;				
	}
	

	/**
	 * Returns whether unkown variables are resolved as GeoDummyVariable objects.
	 * @see setSilentMode()
	 */
	public final boolean isResolveUnkownVarsAsDummyGeos() {
		return resolveUnkownVarsAsDummyGeos;
	}
	
	final public static String defaultLibraryJavaScript = "function ggbOnInit() {}";
	
	String libraryJavaScript = defaultLibraryJavaScript;

	private boolean wantAnimationStarted = false;
	
	public void resetLibraryJavaScript() {
		libraryJavaScript = defaultLibraryJavaScript;
	}
	
	public void setLibraryJavaScript(String str) {
		Application.debug(str);
		libraryJavaScript = str;
		
		//libraryJavaScript = "function ggbOnInit() {ggbApplet.evalCommand('A=(1,2)');ggbApplet.registerObjectUpdateListener('A','listener');}function listener() {//java.lang.System.out.println('add listener called'); var x = ggbApplet.getXcoord('A');var y = ggbApplet.getYcoord('A');var len = Math.sqrt(x*x + y*y);if (len > 5) { x=x*5/len; y=y*5/len; }ggbApplet.unregisterObjectUpdateListener('A');ggbApplet.setCoords('A',x,y);ggbApplet.registerObjectUpdateListener('A','listener');}";
		//libraryJavaScript = "function ggbOnInit() {ggbApplet.evalCommand('A=(1,2)');}";
	}
	
	//public String getLibraryJavaScriptXML() {
	//	return Util.encodeXML(libraryJavaScript);
	//}
	
	public String getLibraryJavaScript() {
		return libraryJavaScript;
	}
	
	
	
	/** return all points of the current construction */
	public TreeSet getPointSet(){
		return getConstruction().getGeoSetLabelOrder(GeoElement.GEO_CLASS_POINT);
	}
	
	/**
	 * test kernel
	 */
	public static void mainx(String [] args) {
		// create kernel with null application for testing
		Kernel kernel = new Kernel(null);
		Construction cons = kernel.getConstruction();
		
		// create points A and B
		GeoPoint A = new GeoPoint(cons, "A", 0, 1, 1);
		GeoPoint B = new GeoPoint(cons, "B", 3, 4, 1);
		
		// create line g through points A and B
		GeoLine g = kernel.Line("g", A, B);
		
		// print current objects
		System.out.println(A);
		System.out.println(B);
		System.out.println(g);
		
		// change B
		B.setCoords(3, 2, 1);
		B.updateCascade();
		
		// print current objects
		System.out.println("changed " +B);
		System.out.println(g);
	}
	
	
	final public GeoNumeric convertIndexToNumber(String str) {
		
		int i = 0;
		char c;
		while (i < str.length() && !Unicode.isSuperscriptDigit(str.charAt(i)))
			i++;
		
		//Application.debug(str.substring(i, str.length() - 1)); 
		MyDouble md = new MyDouble(this, str.substring(i, str.length() - 1)); // strip off eg "sin" at start, "(" at end
		GeoNumeric num = new GeoNumeric(getConstruction(), md.getDouble());
		return num;

	}
	
	final public ExpressionNode handleTrigPower(String image, ExpressionNode en, int type) {
		
		// sin^(-1)(x) -> ArcSin(x)
		if (image.indexOf(Unicode.Superscript_Minus) > -1) {
			//String check = ""+Unicode.Superscript_Minus + Unicode.Superscript_1 + '(';
			if (image.substring(3, 6).equals(Unicode.superscriptMinusOneBracket)) {
				switch (type) {
				case ExpressionNode.SIN:
					return new ExpressionNode(this, en, ExpressionNode.ARCSIN, null);
				case ExpressionNode.COS:
					return new ExpressionNode(this, en, ExpressionNode.ARCCOS, null);
				case ExpressionNode.TAN:
					return new ExpressionNode(this, en, ExpressionNode.ARCTAN, null);
				default:
						throw new Error("Inverse not supported for trig function"); // eg csc^-1(x)
				}
			}
			else throw new Error("Bad index for trig function"); // eg sin^-2(x)
		}
		
		return new ExpressionNode(this, new ExpressionNode(this, en, type, null), ExpressionNode.POWER, convertIndexToNumber(image));
	}
	
	

	
	
	
	
	
	
	
	
	
	/*
	 * used to delay animation start until everything loaded
	 */
	public void setWantAnimationStarted(boolean b) {
		wantAnimationStarted   = true;		
	}
	
	public boolean wantAnimationStarted() {
		return wantAnimationStarted;
	}

	/**
	 * Determine whether point is inregion
	 * @param label
	 * @param pi
	 * @param region
	 * @return GeoBoolean which is true iff point is in region
	 */
	public GeoBoolean isInRegion(String label,GeoPointND pi, Region region) {
		AlgoIsInRegion algo = new AlgoIsInRegion(cons,label,pi,region);
		return algo.getResult();
	}


	public GeoElement[] Sequence(String label, GeoNumeric upTo) {
		AlgoSequence algo = new AlgoSequence(cons,label,upTo);
		return algo.getOutput();
	}


	public GeoElement[] Zip(String label, GeoElement expression,
			GeoElement[] vars, GeoList[] over) {
		//Application.debug("expr:"+expression+"label:"+label+"var:"+vars+"over:"+over);
		AlgoZip algo = new AlgoZip(cons,label,expression,vars,over);
		return algo.getOutput();
	}
	
	final public GeoPoint Kimberling(String label, GeoPoint A, GeoPoint B, GeoPoint C, NumberValue v) {
		AlgoKimberling algo = new AlgoKimberling(cons, label, A,B,C,v);
		GeoPoint P = algo.getResult();
		return P;
	}


	public GeoTextField textfield(String label,GeoElement geoElement) {
		AlgoTextfield at = new AlgoTextfield(cons,label,geoElement);
		return  at.getResult();		
		
		
	}

	public void setInverseTrigReturnsAngle(boolean selected) {
		arcusFunctionCreatesAngle = selected;
	}
	
	public boolean getInverseTrigReturnsAngle() {
		return arcusFunctionCreatesAngle;
	}
	
	
	/**
	 * 
	 * @param precision
	 * @return a double comparator which says doubles are equal if their diff is less than precision
	 */
	final static public Comparator<Double> DoubleComparator(double precision){
		
		final double eps = precision;
		
		Comparator<Double> ret = new Comparator<Double>() {

			public int compare(Double d1, Double d2) {
				if (Math.abs(d1-d2)<eps)
					return 0;
				else if (d1<d2)
					return -1;
				else
					return 1;
			}
			
		};
		
		return ret;
	}
	
	/**
	 * 
	 * @return default plane (null for 2D implementation, xOy plane for 3D)
	 */
	public GeoPlaneND getDefaultPlane(){
		return null;
	}
	
	public GeoNumeric getDefaultNumber(boolean isAngle){
		return (GeoNumeric)cons.consDefaults.
			getDefaultGeo(isAngle?ConstructionDefaults.DEFAULT_ANGLE:
			ConstructionDefaults.DEFAULT_NUMBER);
	}

	/**
	 * Get {@link Kernel#insertLineBreaks insertLineBreaks}.
	 * 
	 * @return {@link Kernel#insertLineBreaks insertLineBreaks}.
	 */
	public boolean isInsertLineBreaks () {
		return insertLineBreaks;
	}

	/**
	 * Set {@link Kernel#insertLineBreaks insertLineBreaks}.
	 * 
	 * @param insertLineBreaks The value to set {@link Kernel#insertLineBreaks insertLineBreaks} to.
	 */
	public void setInsertLineBreaks (boolean insertLineBreaks) {
		this.insertLineBreaks = insertLineBreaks;
	}


	public GeoElement[] OrthogonalLineToConic(String label, GeoPoint geoPoint,
			GeoConic conic) {
		AlgoOrthoLinePointConic algo = new AlgoOrthoLinePointConic(cons, label, geoPoint, conic);
		GeoElement[] lines = algo.getOutput();
		return lines;
	}


	public void setLoadingMode(boolean b) {
		loadingMode = b;
	}
	public boolean getLoadingMode() {
		return loadingMode;
	}
	
	public String getXMLFileFormat(){
		return GeoGebra.XML_FILE_FORMAT;
	}
	
    private GeoVec2D imaginaryUnit;
    public GeoVec2D getImaginaryUnit() {
    	if (imaginaryUnit == null) {
    		imaginaryUnit = new GeoVec2D(this, 0, 1);
    		imaginaryUnit.setMode(Kernel.COORD_COMPLEX);
    	}
    	
    	return imaginaryUnit;
    }

}