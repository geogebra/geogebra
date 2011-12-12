package geogebra.common.kernel;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

import geogebra.common.GeoGebraConstants;
import geogebra.common.util.AbstractMyMath2;
import geogebra.common.util.GgbMat;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.LaTeXCache;
import geogebra.common.util.MaxSizeHashMap;
import geogebra.common.util.ScientificFormatAdapter;
import geogebra.common.util.Unicode;
import geogebra.common.awt.Color;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.Operation;
import geogebra.common.kernel.arithmetic.Polynomial;
import geogebra.common.kernel.cas.AlgoDependentCasCell;
import geogebra.common.kernel.cas.GeoGebraCasInterface;
import geogebra.common.kernel.commands.AbstractCommandDispatcher;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.geos.AbstractGeoElementSpreadsheet;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoAxis;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicInterface;
import geogebra.common.kernel.geos.GeoConicPartInterface;
import geogebra.common.kernel.geos.GeoDummyVariable;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoInterval;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoLocusInterface;
import geogebra.common.kernel.geos.GeoElementInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.implicit.GeoImplicitPolyInterface;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoRayND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.optimization.ExtremumFinder;
import geogebra.common.kernel.parser.ParserInterface;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.MyError;
import geogebra.common.main.AbstractApplication.CasType;
import geogebra.common.kernel.AbstractAnimationManager;
import geogebra.common.kernel.algos.AlgoAngleNumeric;
import geogebra.common.kernel.algos.AlgoClosestPoint;
import geogebra.common.kernel.algos.AlgoDependentBoolean;
import geogebra.common.kernel.algos.AlgoDependentConic;
import geogebra.common.kernel.algos.AlgoDependentFunction;
import geogebra.common.kernel.algos.AlgoDependentFunctionNVar;
import geogebra.common.kernel.algos.AlgoDependentGeoCopy;
import geogebra.common.kernel.algos.AlgoDependentInterval;
import geogebra.common.kernel.algos.AlgoDependentLine;
import geogebra.common.kernel.algos.AlgoDependentList;
import geogebra.common.kernel.algos.AlgoDependentListExpression;
import geogebra.common.kernel.algos.AlgoDependentNumber;
import geogebra.common.kernel.algos.AlgoDependentPoint;
import geogebra.common.kernel.algos.AlgoDependentText;
import geogebra.common.kernel.algos.AlgoDependentVector;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoIntersectAbstract;
import geogebra.common.kernel.algos.AlgoLinePointVector;
import geogebra.common.kernel.algos.AlgoPointOnPath;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.adapters.Complex;
import geogebra.common.adapters.Geo3DVec;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;



public abstract class AbstractKernel {

	//G.Sturr 2009-10-18
	// algebra style 
	final public static int ALGEBRA_STYLE_VALUE = 0;
	final public static int ALGEBRA_STYLE_DEFINITION = 1;
	final public static int ALGEBRA_STYLE_COMMAND = 2;
	protected int algebraStyle = AbstractKernel.ALGEBRA_STYLE_VALUE;//private
	//end G.Sturr
	
	/**
	 * Specifies whether possible line breaks are to be marked
	 * in the String representation of {@link ExpressionNode ExpressionNodes}.
	 */
	protected boolean insertLineBreaks = false;

	// angle unit: degree, radians
	//private int angleUnit = Kernel.ANGLE_DEGREE;
	
	
	protected boolean viewReiniting = false;//private
	protected boolean undoActive = false;//private
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

	// Views may register to be informed about 
	// changes to the Kernel
	// (add, remove, update)
	protected View[] views = new View[20];
	protected int viewCnt = 0;

	protected Construction cons;


	protected AlgebraProcessor algProcessor;
	/** Evaluator for ExpressionNode */
	protected ExpressionNodeEvaluator expressionNodeEvaluator;
	/** CAS variable handling */	
	private static final String GGBCAS_VARIABLE_PREFIX = "ggbcasvar";
	private static final String TMP_VARIABLE_PREFIX = "ggbtmpvar";
	private static int kernelInstances = 0;
	// Continuity on or off, default: false since V3.0
		private boolean continuous = false;
	private int kernelID;
	private String casVariablePrefix;
	private GeoGebraCasInterface ggbCAS;
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

	/** maximum precision of double numbers */
	public final static double MAX_DOUBLE_PRECISION = 1E-15;
	/** reciprocal of maximum precision of double numbers */
	public final static double INV_MAX_DOUBLE_PRECISION = 1E15;	
	
	// maximum CAS results cached
		public static int GEOGEBRA_CAS_CACHE_SIZE = 500;
		
	// print precision
		public static final int STANDARD_PRINT_DECIMALS = 2; 
		private double PRINT_PRECISION = 1E-2;
		private NumberFormatAdapter nf;
		private ScientificFormatAdapter sf;
		public boolean useSignificantFigures = false;
		
		 // style of point/vector coordinates
		/** A = (3, 2)  and 	B = (3; 90���)*/
	    public static final int COORD_STYLE_DEFAULT = 0;		
	    /** A(3|2)  	   and	B(3; 90���)*/
		public static final int COORD_STYLE_AUSTRIAN = 1;		
		/** A: (3, 2)   and	B: (3; 90���) */
		public static final int COORD_STYLE_FRENCH = 2;			
		private int coordStyle = 0;
	
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

	
	
	// rounding hack, see format()
		private static final double ROUND_HALF_UP_FACTOR_DEFAULT = 1.0 + 1E-15;
		private double ROUND_HALF_UP_FACTOR = ROUND_HALF_UP_FACTOR_DEFAULT;

	private String casPrintFormPI; // for pi
	private boolean useTempVariablePrefix;
	
		
	// before May 23, 2005 the function acos(), asin() and atan()
	// had an angle as result. Now the result is a number.
	// this flag is used to distinguish the different behaviour
	// depending on the the age of saved construction files
	/** if true, cyclometric functions return GeoAngle, if false, they return GeoNumeric**/	
	
	private boolean translateCommandName = true;
	private boolean useInternalCommandNames = false;
	
	
	private boolean notifyConstructionProtocolViewAboutAddRemoveActive = true;
	
	private boolean allowVisibilitySideEffects = true;

	// this flag was introduced for Copy & Paste
	private boolean saveScriptsToXML = true; 

	private boolean elementDefaultAllowed = false;
	
	// silentMode is used to create helper objects without any side effects	
	// i.e. in silentMode no labels are created and no objects are added to views
	private boolean silentMode = false;
	
	// setResolveUnkownVarsAsDummyGeos
	private boolean resolveUnkownVarsAsDummyGeos = false;

	// used to store info when rounding is temporarily changed
		private Stack<Boolean> useSignificantFiguresList;
		private Stack<Integer> noOfSignificantFiguresList;
		private Stack<Integer> noOfDecimalPlacesList;
		private StringBuilder sbBuildExplicitLineEquation = new StringBuilder(50);
		
		
		public AbstractKernel(){
			kernelInstances++;
			kernelID = kernelInstances;
			casVariablePrefix = GGBCAS_VARIABLE_PREFIX + kernelID;
			
			nf = this.getNumberFormat();
			nf.setGroupingUsed(false);
			
			sf = this.getScientificFormat(5, 16, false);

			setCASPrintForm(StringType.GEOGEBRA);
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
		public AlgebraProcessor newAlgebraProcessor(AbstractKernel kernel){
			return new AlgebraProcessor(kernel);
		}
		
		/**
		 * creates the Evaluator for ExpressionNode
		 * @return the Evaluator for ExpressionNode
		 */
		public ExpressionNodeEvaluator newExpressionNodeEvaluator(){
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

		// This is a temporary place for abstract adapter methods which will go into factories later
		// Arpad Fekete, 2011-12-01
		//public abstract ColorAdapter getColorAdapter(int red, int green, int blue);
		public abstract NumberFormatAdapter getNumberFormat();
		public abstract NumberFormatAdapter getNumberFormat(String s);
		public abstract GeoElementGraphicsAdapter newGeoElementGraphicsAdapter();
		public abstract ScientificFormatAdapter getScientificFormat(int a, int b, boolean c);
		
		/* *******************************************
		 *  Methods for MyXMLHandler
		 * ********************************************/
		public boolean handleCoords(GeoElement geo, LinkedHashMap<String, String> attrs) {
			
			if (!(geo instanceof GeoVec3D)) {
				AbstractApplication.debug("wrong element type for <coords>: "
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
			return (Construction)cons;
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
				getApplication().setUnsaved();
			}
		}

		public void setShowOnlyBreakpoints(boolean flag) {
			 ((Construction)cons).setShowOnlyBreakpoints(flag);
		}
		
		final public boolean showOnlyBreakpoints() {
			return ((Construction)cons).showOnlyBreakpoints();
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
			return ((Construction)cons).moveInConstructionList(from, to);
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
		// loading mode: true when a ggb file is being loaded. Devised for backward compatibility.
		private boolean loadingMode;
		public void setLoadingMode(boolean b) {
			loadingMode = b;
		}
		public boolean getLoadingMode() {
			return loadingMode;
		}
		
		public static CasType DEFAULT_CAS = CasType.MPREDUCE; // default

		/**
		 * Sets currently used underlying CAS, e.g. MPReduce or Maxima.
		 * @param casID CasType.MPREDUCE or CAS_MPREDUCE.CAS_Maxima
		 */
		public void setDefaultCAS(CasType casID) {
			DEFAULT_CAS = casID;
			if (ggbCAS != null) ggbCAS.setCurrentCAS(DEFAULT_CAS);
		}
		
		final private static char sign(double x) { //TODO make private
			if (x > 0)
				return '+';
			else
				return '-';
		}
		
		public void setNotifyConstructionProtocolViewAboutAddRemoveActive(boolean flag) {
			notifyConstructionProtocolViewAboutAddRemoveActive = flag;
		}

		public boolean isNotifyConstructionProtocolViewAboutAddRemoveActive() {
			return notifyConstructionProtocolViewAboutAddRemoveActive;
		}
		
		private double[] temp;// = new double[6];



		public final StringBuilder buildImplicitEquation(
			double[] numbers,
			String[] vars,
			boolean KEEP_LEADING_SIGN,
			boolean CANCEL_DOWN,
			char op) {

			sbBuildImplicitEquation.setLength(0);
			sbBuildImplicitEquation.append(buildImplicitVarPart(numbers, vars, KEEP_LEADING_SIGN || (op == '='), CANCEL_DOWN));
			
			if (getCASPrintForm().equals(StringType.MATH_PIPER) && op == '=') {
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
		
		final public String formatPiERaw(double x, NumberFormatAdapter numF) {		
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
							if (!getCASPrintForm().equals(StringType.GEOGEBRA))
								sbFormat.append("*");
							sbFormat.append(casPrintFormPI);
							return sbFormat.toString();
						}
						// odd
						else {		
							// aint * pi/2
							sbFormat.append(aint);
							if (!getCASPrintForm().equals(StringType.GEOGEBRA))
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
			StringType casPrintForm = getCASPrintForm();
			switch (casPrintForm) {
				// number formatting for XML string output
				case GEOGEBRA_XML:
					if (isLongInteger)
						return Long.toString(rounded);
					else
						return Double.toString(x);		
			
				// number formatting for CAS
				case MATH_PIPER:				
				case JASYMCA:		
				case MAXIMA:
				case MPREDUCE:
					if (Double.isNaN(x))
						return " 1/0 ";	
					else if (Double.isInfinite(x)) {
						if (casPrintForm .equals(StringType.MPREDUCE)) 
							return (x<0) ? "-infinity" : "infinity";
						else if (casPrintForm .equals(StringType.MAXIMA)) 
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
			
			if (AbstractApplication.unicodeZero != '0') {
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
			boolean reverseOrder = num.charAt(0) == '-' && getApplication().isRightToLeftDigits();
			
			if (reverseOrder) formatSB.append(Unicode.RightToLeftMark);
			
			for (int i = 0 ; i < num.length() ; i++) {
				char c = num.charAt(i);
				//char c = reverseOrder ? num.charAt(length - 1 - i) : num.charAt(i);
				if (c == '.') c = AbstractApplication.unicodeDecimalPoint;
				else if (c >= '0' && c <= '9') {
					
					c += AbstractApplication.unicodeZero - '0'; // convert to eg Arabic Numeral
					
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
		 * calls formatPiERaw() and converts to localised digits if appropriate
		 */
		final public String formatPiE(double x, NumberFormatAdapter numF) {	
			if (AbstractApplication.unicodeZero != '0') {
				
				String num = formatPiERaw(x, numF);
				
				return internationalizeDigits(num);
				
				
			} else return formatPiERaw(x, numF);
			
		}

		
		private StringBuilder sbBuildImplicitEquation = new StringBuilder(80);
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

		
		// compares double arrays: 
		// yields true if (isEqual(a[i], b[i]) == true) for all i
		final static boolean isEqual(double[] a, double[] b) {
			for (int i = 0; i < a.length; ++i) {
				if (!isEqual(a[i], b[i]))
					return false;
			}
			return true;
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
		/**
		 * Removes the given variableName from ther underlying CAS.
		 */
		public void unbindVariableInGeoGebraCAS(String variableName) {
			if (ggbCAS != null) {
				ggbCAS.unbindVariable(addCASVariablePrefix(variableName));
			}
		}
		
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
		private StringBuilder sbBuildExplicitConicEquation = new StringBuilder(80);

		// y = k x + d
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

		
		/** doesn't show 1 or -1 */
		final public String formatCoeff(double x) { //TODO make private
			if (Math.abs(x) == 1.0) {
				if (x > 0.0)
					return "";
				else
					return "-";
			} else {
				String numberStr = format(x);
				switch (getCASPrintForm()) {
					case MATH_PIPER:
					case MAXIMA:
					case MPREDUCE:
						return numberStr + "*";
						
					default:
						// standard case
						return numberStr;
				}
			}
		}
		
		public final StringBuilder buildExplicitLineEquation(
				double[] numbers,
				String[] vars,
				char op) {
				StringType casPrintForm  = getCASPrintForm();
				double d, dabs, q = numbers[1];		
				sbBuildExplicitLineEquation.setLength(0);
				
				//	BUILD EQUATION STRING                      
				// special case
				// y-coeff is 0: form x = constant
				if (isZero(q)) {
					sbBuildExplicitLineEquation.append("x");
								
					if (casPrintForm .equals(StringType.MATH_PIPER)) {
						sbBuildExplicitLineEquation.append(" == ");
					}
					else {
						sbBuildExplicitLineEquation.append(' ');
						if(numbers[0]<MIN_PRECISION){
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
				if (casPrintForm .equals(StringType.MATH_PIPER)) {
					sbBuildExplicitLineEquation.append(" == ");
				}
				else {
					sbBuildExplicitLineEquation.append(' ');
					if(numbers[1] <MIN_PRECISION){
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
		
	/** if x is nearly zero, 0.0 is returned,
	 *  else x is returned
	 */
	final public static double chop(double x) {
		if (isZero(x))
			return 0.0d;
		else
			return x;
	}
	
	/** is abs(x) < epsilon ? */
	final public static boolean isZero(double x) {
		return -EPSILON < x && x < EPSILON;
	}

	final static boolean isZero(double[] a) {
		for (int i = 0; i < a.length; i++) {
			if (!isZero(a[i]))
				return false;
		}
		return true;
	}

	final public static boolean isInteger(double x) {
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

    final public static double convertToAngleValue(double val) {
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

    /**
	 * Checks if x is close (Kernel.MIN_PRECISION) to a decimal fraction, eg
	 * 2.800000000000001. If it is, the decimal fraction eg 2.8 is returned,
	 * otherwise x is returned.
	 */
	final public static double checkDecimalFraction(double x, double precision) {
		
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
	
	final public static double checkDecimalFraction(double x) {
		return checkDecimalFraction(x, 1);
	}

	/**
	 * Checks if x is very close (1E-8) to an integer. If it is,
	 * the integer value is returned, otherwise x is returnd.
	 */	
	final public static double checkInteger(double x) {		
		double roundVal = Math.round(x);
		if (Math.abs(x - roundVal) < EPSILON)
			return roundVal;
		else
			return x;
	}
	
	final public StringBuilder formatAngle(double phi) {
		// STANDARD_PRECISION * 10 as we need a little leeway as we've converted from radians
		return formatAngle(phi, 10);
	}
	private StringType casPrintForm;
		
	final public String getPiString() {
		return casPrintFormPI;
	}
	public StringType getCASPrintForm(){
		return casPrintForm;
	}
	final public void setCASPrintForm(StringType type) {
		casPrintForm = type;
		
		switch (casPrintForm) {
		case MATH_PIPER:
			casPrintFormPI = "Pi";
			break;
			
		case MAXIMA:
			casPrintFormPI = "%pi";
			break;
			
		case JASYMCA:
		case GEOGEBRA_XML:
			casPrintFormPI = "pi";
			break;
				
		case MPREDUCE:
			casPrintFormPI = "pi";
			break;
		
		case LATEX:
			casPrintFormPI = "\\pi";
			break;
			
			default:
				casPrintFormPI = Unicode.PI_STRING;
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
				if (casPrintForm .equals(StringType.LATEX))
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
			if (casPrintForm .equals(StringType.LATEX))
				sb.append("}");
			else
				sb.append(")");
		}				
		
		return sb.toString();
	}
	final public StringBuilder formatAngle(double phi, double precision) {
		sbFormatAngle.setLength(0);
		switch (casPrintForm) {
			case MATH_PIPER:
			case JASYMCA:
			case MPREDUCE: 
				if (getAngleUnit() == ANGLE_DEGREE) {
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
				
				if (getAngleUnit() == ANGLE_DEGREE) {
					boolean rtl = getApplication().isRightToLeftDigits();
					if (rtl) {
						sbFormatAngle.append(Unicode.degreeChar);
					}
					
					phi = Math.toDegrees(phi);
					
					// make sure 360.0000000002 -> 360
					phi = checkInteger(phi);
					
					if (phi < 0) 
						phi += 360;	
					else if (phi > 360)
						phi = phi % 360;
					// STANDARD_PRECISION * 10 as we need a little leeway as we've converted from radians
					sbFormatAngle.append(format(checkDecimalFraction(phi, precision)));
					
					if (casPrintForm .equals(StringType.GEOGEBRA_XML)) {
						sbFormatAngle.append("*");
					}

					if (!rtl) sbFormatAngle.append(Unicode.degreeChar);

					return sbFormatAngle;
				} 
				else {
					// RADIANS
					sbFormatAngle.append(format(phi));
					
					if (!casPrintForm.equals(StringType.GEOGEBRA_XML)) {
						sbFormatAngle.append(" rad");
					}
					return sbFormatAngle;
				}
		}
		
		
	}
	private StringBuilder sbFormatAngle = new StringBuilder(40);
	
	private boolean arcusFunctionCreatesAngle;
    public void setInverseTrigReturnsAngle(boolean selected) {
		arcusFunctionCreatesAngle = selected;
	}
	
	public boolean getInverseTrigReturnsAngle() {
		return arcusFunctionCreatesAngle;
	} 
	private int angleUnit;
	private boolean keepCasNumbers;
	final public void setAngleUnit(int unit) {
		angleUnit = unit;
	}

	final public int getAngleUnit() {
		return angleUnit;
	}
	
	/**
	 * Returns whether the variable name "z" may be used. Note that the 3D kernel does not
	 * allow this as it uses "z" in plane equations like 3x + 2y + z = 5.
	 * @return whether z may be used as a variable name
	 */
	public boolean isZvarAllowed(){
		return true;
	}
	
	final public static void setEpsilon(double epsilon) {
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
	private static void setEpsilonForPrintPrecision(double printPrecision) {
		if (printPrecision < STANDARD_PRECISION) {
			setEpsilon(printPrecision);
		} else {
			setEpsilon(STANDARD_PRECISION);
		}
	}

	final public static double getEpsilon() {
		return EPSILON;
	}

	final public static void setMinPrecision() {
		setEpsilon(MIN_PRECISION);
	}

	final public static void resetPrecision() {
		setEpsilon(STANDARD_PRECISION);
	}
	
	/**
	 * Returns whether MySpecialDouble objects should keep numbers literally or not. 
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
	 final public String printVariableName(StringType printForm, String label) {
		 switch(printForm){		
			case MPREDUCE:
			case MAXIMA:
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
	 
	 final public String removeCASVariablePrefix(String str) {
		 return removeCASVariablePrefix(str, "");
	 }
	 
	 /**
	 * @return String where CAS variable prefixes are removed again, e.g. "ggbcasvar1a" is turned into "a"
	 * and 
	 */
	 final public String removeCASVariablePrefix(String str, String replace) {
		// e.g. "ggbtmpvar1a" is changed to "a"
		// need a space when called from GeoGebraCAS.evaluateGeoGebraCAS()
		// so that eg Derivative[1/(-x+E2)] works (want 2 E2 not 2E2) #1595, #1616 
		String result = str.replace(casVariablePrefix, replace);
				 
		// e.g. "ggbtmpvara" needs to be changed to "a"
		result = result.replace(TMP_VARIABLE_PREFIX, replace);
		
		return result;
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
			
		
		

		
		/*
		 * returns number of significant digits, or -1 if using decimal places
		 */
		final public int getPrintFigures() {
			if (!useSignificantFigures) return -1;
			return sf.getSigDigits();
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
		
		/**
		 * Returns whether silent mode is turned on.
		 * @see #setSilentMode(boolean)
		 */
		public final boolean isSilentMode() {
			return silentMode;
		}
		
		/**
		 * @return Whether the GeoGebraCAS has been initialized before
		 */
		public synchronized boolean isGeoGebraCASready() {
			return ggbCAS != null;
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
			getConstruction().setSuppressLabelCreation(silentMode);
			
			// no notifying of views
			//ggb3D - 2009-07-17
			//removing :
			//notifyViewsActive = !silentMode;
			//(seems not to work with loading files)


			
			//Application.printStacktrace(""+silentMode);
			
		}
		
		/**
		 * Sets whether unknown variables should be resolved as GeoDummyVariable objects. 
		 */
		public final void setResolveUnkownVarsAsDummyGeos(boolean resolveUnkownVarsAsDummyGeos) {
			this.resolveUnkownVarsAsDummyGeos = resolveUnkownVarsAsDummyGeos;				
		}
		

		/**
		 * Returns whether unkown variables are resolved as GeoDummyVariable objects.
		 * @see #setSilentMode(boolean)
		 */
		public final boolean isResolveUnkownVarsAsDummyGeos() {
			return resolveUnkownVarsAsDummyGeos;
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
		public MaxSizeHashMap<String, String> getCasCache() {
			if (ggbCasCache == null)
				ggbCasCache = new MaxSizeHashMap<String, String>(GEOGEBRA_CAS_CACHE_SIZE);
			return ggbCasCache;
		}
		
		/**
		 * @return Whether kernel is already using CAS caching.
		 */
		public boolean hasCasCache() {
			return ggbCasCache != null;
		}
		
		private double xmin, xmax, ymin, ymax, xscale, yscale;
		// for 2nd Graphics View
		private double xmin2, xmax2, ymin2, ymax2, xscale2, yscale2;
		private boolean graphicsView2showing = false;
		
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

			graphicsView2showing = getApplication().isShowingEuclidianView2();
			notifyEuclidianViewCE();
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
		
		
		protected abstract void notifyEuclidianViewCE();
		
		public double getXmax() {
			if (graphicsView2showing)
				return Math.max(xmax, xmax2);
			else 
				return xmax;
		}
		
		public double getXmin() {
			if (graphicsView2showing)
				return Math.min(xmin, xmin2);
			else
				return xmin;
		}
		
		public double getXscale() {
			if (graphicsView2showing) {
				// xscale = pixel per unit
				// higher xscale means more pixels per unit, i.e. higher precision
				return Math.max(xscale, xscale2);
			}
			else {
				return xscale;
			}
				
		}
		
		public double getYmax() {
			if (graphicsView2showing)
				return Math.max(ymax, ymax2);
			else
				return ymax;
		}
		
		public double getYmin() {
			if (graphicsView2showing)
				return Math.min(ymin, ymin2);
			else
				return ymin;
		}
		
		public double getYscale() {
			if (graphicsView2showing)
				// yscale = pixel per unit
				// higher xscale means more pixels per unit, i.e. higher precision
				return Math.max(yscale, yscale2);
			else
				return yscale;
		}

	/**
	 * @deprecated	
	 * @return
	 */
   public abstract LaTeXCache newLaTeXCache();

   
   public abstract AbstractApplication getApplication();
   
   
   
   
   public synchronized GeoGebraCasInterface getGeoGebraCAS() {
		if (ggbCAS == null) {
			ggbCAS = newGeoGebraCAS();
		}			
		
		return ggbCAS;
	}
   
   abstract public GeoGebraCasInterface newGeoGebraCAS();
   final public int getCoordStyle() {
		return coordStyle;
	}
	public void setCoordStyle(int coordStlye) {
		coordStyle = coordStlye;		
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
		return ((Construction)cons).lookupCasCellLabel(label);
	}
	
	/**
     * Returns a GeoCasCell for the given cas row. 
     * @return may return null
     */
	final public GeoCasCell lookupCasRowReference(String label) {		
		return ((Construction)cons).lookupCasRowReference(label);
	}
	
	/**
	 * Finds element with given the label and possibly creates it
	 * @param label Label of element we are looking for
	 * @param autoCreate true iff new geo should be created if missing
	 * @return GeoElement with given label
	 */
	final public GeoElement lookupLabel(String label, boolean autoCreate) {	
		GeoElement geo = (GeoElement)cons.lookupLabel(label, autoCreate);
				
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
	
	public GeoClass getClassType(String type) throws MyError {   
    	switch (type.charAt(0)) {
		case 'a': //angle    			
			return GeoClass.ANGLE;	    			     		    			
			
		case 'b': //angle
			if (type.equals("boolean"))
				return GeoClass.BOOLEAN;
			else
    			return GeoClass.BUTTON; // "button"
		
		case 'c': // conic
			if (type.equals("conic"))
				return GeoClass.CONIC;   
			else if (type.equals("conicpart"))    					
				return GeoClass.CONICPART;
			else if (type.equals("circle")) { // bug in GeoGebra 2.6c
				return GeoClass.CONIC;
			}
			
		case 'd': // doubleLine 			// bug in GeoGebra 2.6c
			return GeoClass.CONIC;   			
			
		case 'e': // ellipse, emptyset	//  bug in GeoGebra 2.6c
			return GeoClass.CONIC;    			
				
		case 'f': // function
			return GeoClass.FUNCTION;
		
		case 'h': // hyperbola			//  bug in GeoGebra 2.6c
			return GeoClass.CONIC;   			
			
		case 'i': // image,implicitpoly
			if (type.equals("image"))    				
				return GeoClass.IMAGE;
			else if (type.equals("intersectinglines")) //  bug in GeoGebra 2.6c
				return GeoClass.CONIC;
			else if (type.equals("implicitpoly"))
				return GeoClass.IMPLICIT_POLY;
		
		case 'l': // line, list, locus
			if (type.equals("line"))
				return GeoClass.LINE;
			else if (type.equals("list"))
				return GeoClass.LIST;    					
			else 
				return GeoClass.LOCUS;
		
		case 'n': // numeric
			return GeoClass.NUMERIC;
			
		case 'p': // point, polygon
			if (type.equals("point"))
				return GeoClass.POINT;
			else if (type.equals("polygon"))
				return GeoClass.POLYGON;
			else if (type.equals("polyline"))
				return GeoClass.POLYLINE;
			else // parabola, parallelLines, point //  bug in GeoGebra 2.6c
				return GeoClass.CONIC;
			
		case 'r': // ray
			return GeoClass.RAY;
			
		case 's': // segment    			
			return GeoClass.SEGMENT;	    			    			
			
		case 't': 
			if (type.equals("text"))
				return GeoClass.TEXT; // text
			else
    			return GeoClass.TEXTFIELD; // textfield
			
		case 'v': // vector
			return GeoClass.VECTOR;
		
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
			
		case EuclidianConstants.MODE_FREEHAND:
			return "Freehand";
			
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
    
    
    
    
    
	
	
	
	
	/**
     * Finds the polynomial coefficients of
     * the given expression and returns it in ascending order. 
     * If exp is not a polynomial null is returned.
     * 
     * @param exp expression in MPreduce syntax, e.g. "3*a*x^2 + b*x"
     * @param variable e.g "x"
     * @return array of coefficients, e.g. ["0", "b", "3*a"]
     */
    final public String [] getPolynomialCoeffs(String exp, String variable) {
    	return getGeoGebraCAS().getPolynomialCoeffs(exp, variable);
    }
	
	
	
	
	public abstract AbstractAnimationManager getAnimatonManager();
	
	


	public abstract GeoElementInterface Semicircle(String label, GeoPoint2 geoPoint,
			GeoPoint2 geoPoint2);

	public AbstractMyMath2 getMyMath2() {
		return new AbstractMyMath2();
	}

	
	
	 public void removeIntersectionAlgorithm(AlgoIntersectAbstract algo) {
		intersectionAlgos.remove(algo);	 
	 }


	 public void addIntersectionAlgorithm(AlgoIntersectAbstract algo) {
			intersectionAlgos.add(algo);	 
		 }
	 
	 /**
		 * @param geo
		 * @return RealWorld Coordinates of the rectangle covering all euclidian views
		 *  in which <b>geo</b> is shown.<br /> Format: {xMin,xMax,yMin,yMax,xScale,yScale}
		 */
		public double[] getViewBoundsForGeo(GeoElementInterface geo){
			List<Integer> viewSet=geo.getViewSet();
			double[] viewBounds=new double[6];
			for (int i=0;i<6;i++)
				viewBounds[i]=Double.NEGATIVE_INFINITY;
			viewBounds[0]=viewBounds[2]=Double.POSITIVE_INFINITY;
			for(int id:viewSet){
				View view=getApplication().getView(id);
				if (view!=null&&view instanceof EuclidianViewInterfaceSlim){
					EuclidianViewInterfaceSlim ev=(EuclidianViewInterfaceSlim)view;
					viewBounds[0]=Math.min(viewBounds[0],ev.getXmin());
					viewBounds[1]=Math.max(viewBounds[1],ev.getXmax());
					viewBounds[2]=Math.min(viewBounds[2],ev.getYmin());
					viewBounds[3]=Math.max(viewBounds[3],ev.getYmax());
					viewBounds[4]=Math.max(viewBounds[4],ev.getXscale());
					viewBounds[5]=Math.max(viewBounds[5],ev.getYscale());
				}
			}
//			if (viewBounds[0]==Double.POSITIVE_INFINITY){
//				//standard values if no view
//				viewBounds[0]=viewBounds[2]=-10;
//				viewBounds[1]=viewBounds[3]=10;
//				viewBounds[5]=viewBounds[6]=1;
//			}
			return viewBounds;
		}	
		
		final public GeoAxis getXAxis() {
			return ((Construction)cons).getXAxis();
		}
		
		final public GeoAxis getYAxis() {
			return ((Construction)cons).getYAxis();
		}
		
		final public boolean isAxis(GeoElement geo) {
			return (geo == ((Construction)cons).getXAxis() || geo == ((Construction)cons).getYAxis());
		}
		
	    public void updateLocalAxesNames() {
	    	cons.updateLocalAxesNames();
	    }
	/*
	 * to avoid multiple calculations of the intersection points of the same
	 * two objects, we remember all the intersection algorithms created
	 */
	 protected ArrayList<AlgoIntersectAbstract> intersectionAlgos = new ArrayList<AlgoIntersectAbstract>();

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
		
		final public void notifyReset() {
			for (int i = 0; i < viewCnt; ++i) {
				views[i].reset();
			}
		}
		
		protected final void notifyClearView() {
			for (int i = 0; i < viewCnt; ++i) {
				views[i].clearView();
			}
		}
		
		public void clearJustCreatedGeosInViews() {
			for (int i = 0; i < viewCnt; i++) {
				if (views[i] instanceof EuclidianViewInterfaceSlim)
					((EuclidianViewInterfaceSlim)views[i]).getEuclidianController().clearJustCreatedGeos();
			}
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
		private boolean notifyViewsActive = true;
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
		
		/**
		 * Registers an algorithm that needs to be updated when notifyRename(),
		 * notifyAdd(), or notifyRemove() is called.	 
		 */
		public void registerRenameListenerAlgo(AlgoElement algo) {
			if (renameListenerAlgos == null) {
				renameListenerAlgos = new ArrayList<AlgoElement>();
			}
			
			if (!renameListenerAlgos.contains(algo))
				renameListenerAlgos.add(algo);
		}
		
		void unregisterRenameListenerAlgo(AlgoElement algo) {
			if (renameListenerAlgos != null) 
				renameListenerAlgos.remove(algo);
		}
		private ArrayList<AlgoElement> renameListenerAlgos;
		
		private void notifyRenameListenerAlgos() {
			AlgoElement.updateCascadeAlgos(renameListenerAlgos);
		}
			
		final public void notifyAddAll(View view, int consStep) {
			if (!notifyViewsActive) return;
					
			for (GeoElement geo : ((Construction)cons).getGeoSetWithCasCellsConstructionOrder()) {
				// stop when not visible for current construction step
				if (!geo.isAvailableAtConstructionStep(consStep))
					break;
				
				view.add(geo);
			}			
		}
		
		public final void notifyAdd(GeoElementInterface geo) {
			if (notifyViewsActive) {
				for (int i = 0; i < viewCnt; ++i) {
					if (views[i].getViewID() != AbstractApplication.VIEW_CONSTRUCTION_PROTOCOL
						|| isNotifyConstructionProtocolViewAboutAddRemoveActive())
						views[i].add((GeoElement)geo);
				}
			}
			
			notifyRenameListenerAlgos();
		}

		public final void notifyRemove(GeoElementInterface geo) {
			if (notifyViewsActive) {
				for (int i = 0; i < viewCnt; ++i) {
					if (views[i].getViewID() != AbstractApplication.VIEW_CONSTRUCTION_PROTOCOL
							|| isNotifyConstructionProtocolViewAboutAddRemoveActive())
						views[i].remove((GeoElement)geo);
				}
			}
			
			notifyRenameListenerAlgos();
		}

		public final void notifyUpdate(GeoElementInterface geo) {
			if (notifyViewsActive) {
				for (int i = 0; i < viewCnt; ++i) {
					views[i].update((GeoElement)geo);
				}
			}
		}
		
		public final void notifyUpdateVisualStyle(GeoElementInterface geo) {
			if (notifyViewsActive) {
				for (int i = 0; i < viewCnt; ++i) {
					views[i].updateVisualStyle((GeoElement)geo);
				}
			}
		}
		
		public final void notifyUpdateAuxiliaryObject(GeoElementInterface geo) {
			if (notifyViewsActive) {
				for (int i = 0; i < viewCnt; ++i) {
					views[i].updateAuxiliaryObject((GeoElement)geo);
				}
			}
		}

		public final  void notifyRename(GeoElementInterface geo) {
			if (notifyViewsActive) {
				for (int i = 0; i < viewCnt; ++i) {
					views[i].rename((GeoElement)geo);
				}
			}
			
			notifyRenameListenerAlgos();
		}
		
			
		
		public boolean isNotifyViewsActive() {
			return notifyViewsActive && !viewReiniting;
		}

		
		public boolean isViewReiniting() {
			return viewReiniting;
		}
		
		/*/**************************
		 * Undo /Redo
		 */
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
				((Construction)cons).storeUndoInfo();
			}
		}

		public void restoreCurrentUndoInfo() {
			if (undoActive) ((Construction)cons).restoreCurrentUndoInfo();
		}

		public void initUndoInfo() {
			if (undoActive) ((Construction)cons).initUndoInfo();
		}

		public void redo() {
			if (undoActive){			
				notifyReset();
				clearJustCreatedGeosInViews();
				((Construction)cons).redo();	
				notifyReset();
			}
		}

		public void undo() {
			if (undoActive) {			
				notifyReset();
				clearJustCreatedGeosInViews();						
				getApplication().getActiveEuclidianView().getEuclidianController().clearSelections();
				((Construction)cons).undo();
				notifyReset();

				// repaint needed for last undo in second EuclidianView (bugfix)
				if (!undoPossible())
					notifyRepaint();
			}
		}

		public boolean undoPossible() {
			return undoActive && ((Construction)cons).undoPossible();
		}

		public boolean redoPossible() {
			return undoActive && ((Construction)cons).redoPossible();
		}

		
	public abstract AbstractGeoElementSpreadsheet getGeoElementSpreadsheet();

	/**
	 * @deprecated
	 * @param myList
	 * @return
	 */
	public abstract GgbMat getGgbMat(MyList myList);

	/**
	 * Get {@link Kernel#insertLineBreaks insertLineBreaks}.
	 * 
	 * @return {@link Kernel#insertLineBreaks insertLineBreaks}.
	 */
	public boolean isInsertLineBreaks () {
		return insertLineBreaks;
	}
	public String getXMLFileFormat(){
		return GeoGebraConstants.XML_FILE_FORMAT;
	}
	

	/**
	 * Set {@link Kernel#insertLineBreaks insertLineBreaks}.
	 * 
	 * @param insertLineBreaks The value to set {@link Kernel#insertLineBreaks insertLineBreaks} to.
	 */
	public void setInsertLineBreaks (boolean insertLineBreaks) {
		this.insertLineBreaks = insertLineBreaks;
	}

	
	
	public abstract GeoPoint2 [] RootMultiple(String [] labels, GeoFunction f);



	public abstract GeoNumeric getDefaultNumber(boolean geoAngle);

	

	public abstract GeoSegmentND SegmentND(String label, GeoPointND P, GeoPointND Q);
	public abstract Object Ray(String label, GeoPoint2 P, GeoPoint2 Q);//GeoRay
	public abstract Object RayND(String label, GeoPointND P, GeoPointND Q);//GeoRayND
	public abstract Object Ray(String label, GeoPoint2 P, GeoVector v);//GeoRay
	public abstract GeoElement [] PolygonND(String [] labels, GeoPointND [] P);
	public abstract GeoElement [] PolyLineND(String [] labels, GeoPointND [] P);
	
	public abstract GeoConicPartInterface newGeoConicPart(Construction cons, int type);
	public abstract GeoLocusInterface newGeoLocus(Construction cons);
	public abstract GeoImplicitPolyInterface newGeoImplicitPoly(Construction cons);

	// temporary methods just while moving things
	
	/**
	 * @deprecated
	 * @param d
	 * @param e
	 * @param i
	 * @return
	 */
	public abstract String temporaryGetInterGeoStringForAlgoPointOnPath(String classname, AlgoElement algo);


	public abstract ParserInterface getParser();
	/**
	 * @deprecated
	 * @param d
	 * @param e
	 * @param i
	 * @return
	 */
	public abstract GeoConicInterface getGeoConic();
	

	public abstract ExtremumFinder getExtremumFinder();
	/**
	 * @deprecated
	 * @param d
	 * @param e
	 * @param i
	 * @return
	 */
	public abstract GeoPoint2 getGeoPoint(double d, double e, int i);

	

	public abstract EquationSolverInterface getEquationSolver();

	public abstract void resetGeoGebraCAS();

	public abstract void getKernelXML(StringBuilder sb, boolean b);

	public Geo3DVec getGeo3DVec(double x, double y, double z) {
		AbstractApplication.debug("GeoGebraCommon does not support 3D Vectors");
		return null;
	}
	
final public ExpressionNode handleTrigPower(String image, ExpressionNode en, Operation type) {
		
		// sin^(-1)(x) -> ArcSin(x)
		if (image.indexOf(Unicode.Superscript_Minus) > -1) {
			//String check = ""+Unicode.Superscript_Minus + Unicode.Superscript_1 + '(';
			if (image.substring(3, 6).equals(Unicode.superscriptMinusOneBracket)) {
				switch (type) {
				case SIN:
					return new ExpressionNode(this, en, Operation.ARCSIN, null);
				case COS:
					return new ExpressionNode(this, en, Operation.ARCCOS, null);
				case TAN:
					return new ExpressionNode(this, en, Operation.ARCTAN, null);
				default:
						throw new Error("Inverse not supported for trig function"); // eg csc^-1(x)
				}
			}
			else throw new Error("Bad index for trig function"); // eg sin^-2(x)
		}
		
		return new ExpressionNode(this, new ExpressionNode(this, en, type, null), Operation.POWER, convertIndexToNumber(image));
		
		
	}

	final public GeoNumeric convertIndexToNumber(String str) {
		
		int i = 0;
		while (i < str.length() && !Unicode.isSuperscriptDigit(str.charAt(i)))
			i++;
		
		//Application.debug(str.substring(i, str.length() - 1)); 
		MyDouble md = new MyDouble(this, str.substring(i, str.length() - 1)); // strip off eg "sin" at start, "(" at end
		GeoNumeric num = new GeoNumeric(getConstruction(), md.getDouble());
		return num;
	
	}
	
	/***********************************
	 * FACTORY METHODS FOR GeoElements
	 ***********************************/

	/** Point label with cartesian coordinates (x,y)   */
	final public GeoPoint2 Point(String label, double x, double y) {
		GeoPoint2 p = new GeoPoint2(cons);
		p.setCoords(x, y, 1.0);
		p.setMode(COORD_CARTESIAN);
		p.setLabel(label); // invokes add()                
		return p;
	}

	/** Point label with cartesian coordinates (x,y)   */
	final public GeoPoint2 Point(String label, double x, double y, boolean complex) {
		GeoPoint2 p = new GeoPoint2(cons);
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
	
	/** 
	 * Text dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. c = a & b
	 */
	final public GeoBoolean DependentBoolean(
		String label,
		ExpressionNode root) {
		AlgoDependentBoolean algo = new AlgoDependentBoolean((Construction)cons, label, root);
		return algo.getGeoBoolean();		
	}
	
	/** Point on path with cartesian coordinates (x,y)   */
	final public GeoPoint2 Point(String label, Path path, double x, double y, boolean addToConstruction, boolean complex) {
		boolean oldMacroMode = false;
		if (!addToConstruction) {
			oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);		

		}
		AlgoPointOnPath algo = new AlgoPointOnPath(cons, label, path, x, y);
		GeoPoint2 p = algo.getP();        
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
	final public GeoPoint2 Point(String label, Path path, NumberValue param) {						
		// try (0,0)
		AlgoPointOnPath algo = null;
		if(param == null)
			algo = new AlgoPointOnPath(cons, label, path, 0, 0);
		else
			algo = new AlgoPointOnPath(cons, label, path, 0, 0,param);
		GeoPoint2 p = algo.getP(); 
		
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
	final public GeoPoint2 ClosestPoint(String label, Path path, GeoPoint2 p) {						
		AlgoClosestPoint algo = new AlgoClosestPoint(cons, label, path, p);				
		return algo.getP();
	}

	public GeoElement Point(String label, Path path) {

		return Point(label,path,null);
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
	
	/** Converts number to angle */
	final public GeoAngle Angle(String label, GeoNumeric num) {
		AlgoAngleNumeric algo = new AlgoAngleNumeric((Construction)cons, label, num);
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
	final public GeoList List(String label, ArrayList<GeoElement> geoElementList, boolean isIndependent) {
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
			AlgoDependentList algoList = new AlgoDependentList((Construction)cons, label, geoElementList);
			return algoList.getGeoList();
		}		
	}
	
	/**
	 * Creates a dependent list object with the given label, 
	 * e.g. {3, 2, 1} + {a, b, 2}	 
	 */
	final public GeoList ListExpression(String label, ExpressionNode root) {
		AlgoDependentListExpression algo =
			new AlgoDependentListExpression((Construction)cons, label, root);		
		return algo.getList();
	}

	
	/** 
	 * GeoCasCell dependent on other variables,
	 * e.g. m := c + 3
	 * @return resulting casCell created using geoCasCell.copy(). 
	 */
	final public static GeoCasCell DependentCasCell(GeoCasCell geoCasCell) {
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
	final public GeoPoint2 DependentPoint(
		String label,
		ExpressionNode root, boolean complex) {
		AlgoDependentPoint algo = new AlgoDependentPoint((Construction)cons, label, root, complex);
		GeoPoint2 P = algo.getPoint();
		return P;
	}

	/** Vector dependent on arithmetic expression with variables,
	 * represented by a tree. e.g. v = u + 3 w
	 */
	final public GeoVector DependentVector(
		String label,
		ExpressionNode root) {
		AlgoDependentVector algo = new AlgoDependentVector((Construction)cons, label, root);
		GeoVector v = algo.getVector();
		return v;
	}

	/** Line dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. y = k x + d
	 */
	final public GeoLine DependentLine(String label, Equation equ) {
		AlgoDependentLine algo = new AlgoDependentLine((Construction)cons, label, equ);
		GeoLine line = algo.getLine();
		return line;
	}

	

	/** Conic dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. y��� = 2 p x 
	 */
	final public GeoConic DependentConic(String label, Equation equ) {
		AlgoDependentConic algo = new AlgoDependentConic((Construction)cons, label, equ);
		GeoConic conic = algo.getConic();
		return conic;
	}

	/** Function dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. f(x) = a x��� + b x���
	 */
	final public GeoFunction DependentFunction(
		String label,
		Function fun) {
		AlgoDependentFunction algo = new AlgoDependentFunction((Construction)cons, label, fun);
		GeoFunction f = algo.getFunction();
		return f;
	}
	
	/** Multivariate Function depending on coefficients of arithmetic expressions with variables,
	 *  e.g. f(x,y) = a x^2 + b y^2
	 */
	final public GeoFunctionNVar DependentFunctionNVar(
		String label,
		FunctionNVar fun) {
		AlgoDependentFunctionNVar algo = new AlgoDependentFunctionNVar((Construction)cons, label, fun);
		GeoFunctionNVar f = algo.getFunction();
		return f;
	}
	
	/** Interval dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. x > a && x < b
	 */
	final public GeoFunction DependentInterval(
		String label,
		Function fun) {
		AlgoDependentInterval algo = new AlgoDependentInterval((Construction)cons, label, fun);
		GeoFunction f = algo.getFunction();
		return f;
	}
	
	/** Text dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. text = "Radius: " + r
	 */
	final public GeoText DependentText(
		String label,
		ExpressionNode root) {
		AlgoDependentText algo = new AlgoDependentText((Construction)cons, label, root);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	
	/** 
	 * Creates a dependent copy of origGeo with label
	 */
	final public GeoElement DependentGeoCopy(String label, ExpressionNode origGeoNode) {
		AlgoDependentGeoCopy algo = new AlgoDependentGeoCopy((Construction)cons, label, origGeoNode);
		return algo.getGeo();
	}
	
	final public GeoElement DependentGeoCopy(String label, GeoElement origGeoNode) {
		AlgoDependentGeoCopy algo = new AlgoDependentGeoCopy((Construction)cons, label, origGeoNode);
		return algo.getGeo();
	}
	
	/** 
	 * Line named label through Point P with direction of vector v
	 */
	final public GeoLine Line(String label, GeoPoint2 P, GeoVector v) {
		AlgoLinePointVector algo = new AlgoLinePointVector((Construction)cons, label, P, v);
		GeoLine g = algo.getLine();
		return g;
	}

	private GeoVec2D imaginaryUnit;
	public GeoVec2D getImaginaryUnit() {
		if (imaginaryUnit == null) {
			imaginaryUnit = new GeoVec2D(this, 0, 1);
			imaginaryUnit.setMode(COORD_COMPLEX);
		}
		
		return imaginaryUnit;
	}

	/**
	 * @deprecated
	 * @param cons
	 * @return undo manager
	 */
	public abstract AbstractUndoManager getUndoManager(Construction cons);

	/**
	 * @deprecated
	 * @param construction
	 * @return
	 */
	public abstract AbstractConstructionDefaults getConstructionDefaults(
			Construction construction);

	public abstract GeoImplicitPolyInterface ImplicitPoly(String label, Polynomial lhs);

	public abstract GeoElement DependentImplicitPoly(String label, Equation equ);

	/**
	 * @deprecated
	 * @return
	 */
	public abstract AbstractCommandDispatcher getCommandDispatcher();

	public abstract MacroInterface getMacro(String cmdName);

	public abstract GeoElement[] useMacro(String[] labels, MacroInterface macro, GeoElement[] arg);

	public void getKernelXML(StringBuilder sb) {
		// TODO Auto-generated method stub
		
	}

	

}
