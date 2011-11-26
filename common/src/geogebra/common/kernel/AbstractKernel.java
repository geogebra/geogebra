package geogebra.common.kernel;

import geogebra.common.util.LaTeXCache;
import geogebra.common.util.Unicode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.main.AbstractApplication;

public abstract class AbstractKernel {
	
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
	public abstract String format(double d);
	private String casPrintFormPI;
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
	final public void setAngleUnit(int unit) {
		angleUnit = unit;
	}

	final public int getAngleUnit() {
		return angleUnit;
	}
    
   public abstract LaTeXCache newLaTeXCache();
   public abstract String printVariableName(String varStr);
   public abstract AbstractConstruction getConstruction();
   public abstract AbstractApplication getApplication();
   
}
