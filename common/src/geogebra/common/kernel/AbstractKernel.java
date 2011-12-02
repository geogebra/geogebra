package geogebra.common.kernel;


import java.util.Stack;

import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.LaTeXCache;
import geogebra.common.util.MaxSizeHashMap;
import geogebra.common.util.ScientificFormatAdapter;
import geogebra.common.util.Unicode;
import geogebra.common.awt.ColorAdapter;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.cas.GeoGebraCasInterfaceSlim;
import geogebra.common.kernel.commands.AbstractAlgebraProcessor;
import geogebra.common.kernel.geos.GeoElementInterface;
import geogebra.common.kernel.geos.GeoListInterface;
import geogebra.common.kernel.geos.GeoNumericInterface;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.AbstractApplication.CasType;
import geogebra.common.kernel.AbstractAnimationManager;



public abstract class AbstractKernel {
	
	/** CAS variable handling */	
	private static final String GGBCAS_VARIABLE_PREFIX = "ggbcasvar";
	private static final String TMP_VARIABLE_PREFIX = "ggbtmpvar";
	private static int kernelInstances = 0;
	// Continuity on or off, default: false since V3.0
		private boolean continuous = false;
	private int kernelID;
	private String casVariablePrefix;
	private GeoGebraCasInterfaceSlim ggbCAS;
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

		// This is a temporary place for abstract adapter methods which will go into factories later
		// Arpad Fekete, 2011-12-01
		public abstract ColorAdapter getColorAdapter(int red, int green, int blue);
		public abstract NumberFormatAdapter getNumberFormat();
		public abstract NumberFormatAdapter getNumberFormat(String s);
		public abstract GeoElementGraphicsAdapter newGeoElementGraphicsAdapter();


		public abstract ScientificFormatAdapter getScientificFormat(int a, int b, boolean c);
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

		
   public abstract LaTeXCache newLaTeXCache();
   public abstract GeoElementInterface lookupLabel(String label);
   public abstract GeoElementInterface lookupLabel(String label,boolean b);
   public abstract AbstractConstruction getConstruction();
   public abstract AbstractApplication getApplication();
   public abstract void notifyRepaint();
   public abstract void initUndoInfo() ;
   
   public synchronized GeoGebraCasInterfaceSlim getGeoGebraCAS() {
		if (ggbCAS == null) {
			ggbCAS = newGeoGebraCAS();
		}			
		
		return ggbCAS;
	}
   
   abstract public GeoGebraCasInterfaceSlim newGeoGebraCAS();
   final public int getCoordStyle() {
		return coordStyle;
	}
	public void setCoordStyle(int coordStlye) {
		coordStyle = coordStlye;		
	}
	public abstract int getConstructionStep();
	public abstract void storeUndoInfo();
	
	public abstract double[] getViewBoundsForGeo(GeoElementInterface geo);
	public abstract void notifyUpdate(GeoElementInterface geo);
	public abstract AbstractAnimationManager getAnimatonManager();
	public abstract void notifyRename(GeoElementInterface geoElement);
	public abstract void notifyRemove(GeoElementInterface geoElement);
	public abstract void notifyUpdateVisualStyle(GeoElementInterface geoElement);
	public abstract void notifyUpdateAuxiliaryObject(GeoElementInterface geoElement);
	public abstract void notifyAdd(GeoElementInterface geoElement);
	
	public abstract AbstractAlgebraProcessor getAlgebraProcessor();
	
	public abstract GeoNumericInterface newNumeric(AbstractConstruction cons);
	public abstract GeoListInterface newList();
}
