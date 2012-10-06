/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * MyDouble.java
 *
 * Created on 07. Oktober 2001, 12:23
 */

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.common.util.MyMath;
import geogebra.common.util.MyMath2;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;

import java.util.HashSet;

/**
 * 
 * @author Markus Hohenwarter
 */

public class MyDouble extends ValidExpression implements NumberValue,
		Comparable<Object> {

	private double val;
	private boolean isAngle = false;

	/**
	 * kernel
	 */
	protected Kernel kernel;
	
	/**
	 * Do not use integer operations beyond this bound
	 */
	public static double LARGEST_INTEGER = 9007199254740992.0; // 0x020000000000000

	/**
	 * @param kernel kernel
	 */
	public MyDouble(Kernel kernel) {
		this(kernel, 0.0);
	}

	/** Creates new MyDouble 
	 * @param kernel kernel
	 * @param x value*/
	public MyDouble(Kernel kernel, double x) {
		this.kernel = kernel;
		val = x;
	}

	/**
	 * @param d MyDouble to copy
	 */
	public MyDouble(MyDouble d) {
		kernel = d.kernel;
		val = d.val;
		isAngle = d.isAngle;
	}

	/**
	 * called from the parser power must be a string of unicode superscript
	 * digits
	 * @param kernel kernel
	 * @param power superscript power
	 */
	public MyDouble(Kernel kernel, String power) {
		this.kernel = kernel;

		int sign = 1;
		int start = 0;
		if (power.charAt(0) == Unicode.Superscript_Minus) {
			start = 1;
			sign = -1;
		}

		val = 0;
		for (int i = 0; i < power.length() - start; i++) {
			switch (power.charAt(power.length() - 1 - i)) {
			case Unicode.Superscript_0:
				// val+= 0;
				break;
			case Unicode.Superscript_1:
				val += Math.pow(10, i);
				break;
			case Unicode.Superscript_2:
				val += Math.pow(10, i) * 2;
				break;
			case Unicode.Superscript_3:
				val += Math.pow(10, i) * 3;
				break;
			case Unicode.Superscript_4:
				val += Math.pow(10, i) * 4;
				break;
			case Unicode.Superscript_5:
				val += Math.pow(10, i) * 5;
				break;
			case Unicode.Superscript_6:
				val += Math.pow(10, i) * 6;
				break;
			case Unicode.Superscript_7:
				val += Math.pow(10, i) * 7;
				break;
			case Unicode.Superscript_8:
				val += Math.pow(10, i) * 8;
				break;
			case Unicode.Superscript_9:
				val += Math.pow(10, i) * 9;
				break;
			default: // unexpected character
				val = Double.NaN;
				return;
			}
		}

		val = val * sign;

	}

	public ExpressionValue deepCopy(Kernel kernel1) {
		MyDouble ret = new MyDouble(this);
		ret.kernel = kernel1;
		return ret;
	}

	/**
	 * @param x new value
	 */
	public void set(double x) {
		val = x;
	}

	public void resolveVariables(boolean forEquation) {
		//do nothing
	}

	@Override
	public String toString(StringTemplate tpl) {
		if (isAngle) {
			// convert to angle value first, see issue 87
			// http://code.google.com/p/geogebra/issues/detail?id=87
			double angleVal = Kernel.convertToAngleValue(val);
			return kernel.formatAngle(angleVal,tpl).toString();
		}
		if(!tpl.hasType(StringType.MPREDUCE))
			return kernel.format(val,tpl);
		return kernel.format(val,tpl).replace("E", "e");
	}
	@Override
	final public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	final public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
		return toString(tpl);
	}

	/**
	 * Switches to angle mode (to use degrees)
	 */
	public void setAngle() {
		isAngle = true;
	}

	public boolean isAngle() {
		return isAngle;
	}

	/**
	 * @return random MyDouble
	 */
	final public MyDouble random() {
		val = kernel.getApplication().getRandomNumber();
		isAngle = false;
		return this;
	}

	/** c = a + b 
	 * @param a 1st summand
	 * @param b 2nd summand 
	 * @param c result*/
	final public static void add(MyDouble a, MyDouble b, MyDouble c) {
		c.isAngle = a.isAngle && b.isAngle;
		c.set(a.val + b.val);
	}

	/** c = a - b 
	 * @param a subtrahend
	 * @param b minuend
	 * @param c result*/
	final public static void sub(MyDouble a, MyDouble b, MyDouble c) {
		c.isAngle = a.isAngle && b.isAngle;
		c.set(a.val - b.val);
	}

	/**
	 * c = a * b
	 * http://functions.wolfram.com/Constants/ComplexInfinity/introductions
	 * /Symbols/ShowAll.html
	 * @param a 1st factor
	 * @param b 2nd factor
	 * @param c result
	 * */
	final public static void mult(MyDouble a, MyDouble b, MyDouble c) {
		c.isAngle = a.isAngle || b.isAngle;

		// ? * anything = ?
		if (Double.isNaN(a.val) || Double.isNaN(b.val)) {
			c.set(Double.NaN);
			return;
		}

		// (infinity) * (-infinity) = ?
		if (Double.isInfinite(a.val) && Double.isInfinite(b.val)
				&& Math.signum(a.val) != Math.signum(b.val)) {
			c.set(Double.NaN);
			return;
		}

		// gives correct answer for eg -3 * infinity
		c.set(a.val * b.val);
	}

	/**
	 * c = a * b
	 * http://functions.wolfram.com/Constants/ComplexInfinity/introductions
	 * /Symbols/ShowAll.html
	 * @param a 1st factor
	 * @param b 2nd factor
	 * @param c result
	 * */
	final public static void mult(MyDouble a, double b, MyDouble c) {
		c.isAngle = a.isAngle;

		// ? * anything = ?
		if (Double.isNaN(a.val) || Double.isNaN(b)) {
			c.set(Double.NaN);
			return;
		}

		// (infinity) * (-infinity) = ?
		if (Double.isInfinite(a.val) && Double.isInfinite(b)
				&& Math.signum(a.val) != Math.signum(b)) {
			c.set(Double.NaN);
			return;
		}

		// gives correct answer for eg -3 * infinity
		c.set(a.val * b);
	}

	/** c = a / b 
	 * @param a dividend
	 * @param b divisor
	 * @param c result*/
	final public static void div(MyDouble a, MyDouble b, MyDouble c) {
		c.isAngle = a.isAngle && !b.isAngle;
		c.set(a.val / b.val);
	}

	/** c = pow(a,b) 
	 * @param a base
	 * @param b exponent
	 * @param c result*/
	final public static void pow(MyDouble a, MyDouble b, MyDouble c) {
		c.isAngle = a.isAngle && !b.isAngle;
		c.set(Math.pow(a.val, b.val));
	}
	/** c = -pow(-a,b) 
	 * @param a base
	 * @param b exponent
	 * @param c result*/
	final public static void powDoubleSgnChange(MyDouble a, MyDouble b, MyDouble c) {
		c.isAngle = a.isAngle && !b.isAngle;
		c.set(-Math.pow(-a.val, b.val));
	}
	/**
	 * @return cos(this)
	 */
	final public MyDouble cos() {
		val = Math.cos(val);
		isAngle = false;
		checkZero();
		return this;
	}
	/**
	 * @return sin(this)
	 */
	final public MyDouble sin() {
		val = Math.sin(val);
		isAngle = false;
		checkZero();
		return this;
	}

	/*
	 * make sure cos(2790 degrees) gives zero
	 */
	private void checkZero() {
		if (Kernel.isZero(val))
			val = 0;
	}

	/**
	 * Tangens function
	 * 
	 * @return tangens of value
	 */
	final public MyDouble tan() {
		// Math.tan() gives a very large number for tan(pi/2)
		// but should be undefined for pi/2, 3pi/2, 5pi/2, etc.
		if (Kernel.isEqual(Math.abs(val) % Math.PI,
				Kernel.PI_HALF)) {
			val = Double.NaN;
		} else {
			val = Math.tan(val);
			checkZero();
		}
		isAngle = false;
		return this;
	}
	/**
	 * @return acos(this)
	 */
	final public MyDouble acos() {
		isAngle = kernel.getInverseTrigReturnsAngle();
		set(Math.acos(val));
		return this;
	}
	/**
	 * @return asin(this)
	 */
	final public MyDouble asin() {
		isAngle = kernel.getInverseTrigReturnsAngle();
		set(Math.asin(val));
		return this;
	}
	/**
	 * @return atan(this)
	 */
	final public MyDouble atan() {
		isAngle = kernel.getInverseTrigReturnsAngle();
		set(Math.atan(val));
		return this;
	}
	/**
	 * @param y y
	 * @return atan2(this,y)
	 */
	final public MyDouble atan2(NumberValue y) {
		isAngle = kernel.getInverseTrigReturnsAngle();
		set(Math.atan2(val, y.getDouble()));
		return this;
	}
	/**
	 * @return log(this)
	 */
	final public MyDouble log() {
		val = Math.log(val);
		isAngle = false;
		return this;
	}
	/**
	 * @param base logarithm base
	 * @return log_base(this)
	 */
	final public MyDouble log(NumberValue base) {
		val = Math.log(val) / Math.log(base.getDouble());
		isAngle = false;
		return this;
	}
	/**
	 * @return erf(this)
	 */
	final public MyDouble erf() {
		val = MyMath2.erf(0.0, 1.0, val);
		isAngle = false;
		return this;
	}
	
	/**
	 * @return inverf(this)
	 */
	final public MyDouble inverf() {
		val = MyMath2.inverf(val);
		isAngle = false;
		return this;
	}

	/**
	 * @param order order
	 * @return polygamma(this,order)
	 */
	final public MyDouble polygamma(NumberValue order) {
		val = MyMath2.polyGamma(order, val);
		isAngle = false;
		return this;
	}
	/**
	 * @return psi(this)
	 */
	final public MyDouble psi() {
		val = MyMath2.psi(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return log_10(this)
	 */
	final public MyDouble log10() {
		val = Math.log(val) / MyMath.LOG10;
		isAngle = false;
		return this;
	}
	/**
	 * @return log_2(this)
	 */
	final public MyDouble log2() {
		val = Math.log(val) / MyMath.LOG2;
		isAngle = false;
		return this;
	}
	/**
	 * @return exp(this)
	 */
	final public MyDouble exp() {
		val = Math.exp(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return sqrt(this)
	 */
	final public MyDouble sqrt() {
		val = Math.sqrt(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return cbrt(this)
	 */
	final public MyDouble cbrt() {
		val = MyMath.cbrt(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return abs(this)
	 */
	final public MyDouble abs() {
		val = Math.abs(val);
		return this;
	}
	/**
	 * @return floor(this)
	 */
	final public MyDouble floor() {
		// angle in degrees
		// kernel.checkInteger() needed otherwise floor(60�) gives 59�
		if (isAngle && kernel.getAngleUnit() == Kernel.ANGLE_DEGREE) {
			set(Kernel.PI_180
					* Math.floor(Kernel.checkInteger(val
							* Kernel.CONST_180_PI)));
		} else {
			// number or angle in radians
			set(Math.floor(Kernel.checkInteger(val)));
		}
		return this;
	}
	/**
	 * @return ceil(this)
	 */
	final public MyDouble ceil() {
		// angle in degrees
		// kernel.checkInteger() needed otherwise ceil(241�) fails
		if (isAngle && kernel.getAngleUnit() == Kernel.ANGLE_DEGREE) {
			set(Kernel.PI_180
					* Math.ceil(Kernel.checkInteger(val
							* Kernel.CONST_180_PI)));
		} else {
			// number or angle in radians
			set(Math.ceil(Kernel.checkInteger(val)));
		}
		return this;
	}
	/**
	 * @return round(this)
	 */
	final public MyDouble round() {
		// angle in degrees
		if (isAngle && kernel.getAngleUnit() == Kernel.ANGLE_DEGREE) {
			set(Kernel.PI_180
					* MyDouble.round(val * Kernel.CONST_180_PI));
		} else {
			// number or angle in radians
			set(MyDouble.round(val));
		}
		return this;
	}

	/**
	 * Java quirk/bug Round(NaN) = 0
	 */
	final private static double round(double x) {
		// if (!(Double.isInfinite(x) || Double.isNaN(x)))

		// changed from Math.round(x) as it uses (long) so fails for large
		// numbers
		// also means the check for Infinity / NaN not needed
		return Math.floor(x + 0.5d);

		// else
		// return x;

	}
	/**
	 * @return sgn(this)
	 */
	final public MyDouble sgn() {
		val = MyMath.sgn(kernel, val);
		isAngle = false;
		return this;
	}
	/**
	 * @return cosh(this)
	 */
	final public MyDouble cosh() {
		val = MyMath.cosh(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return sinh(this)
	 */
	final public MyDouble sinh() {
		val = MyMath.sinh(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return tanh(this)
	 */
	final public MyDouble tanh() {
		val = MyMath.tanh(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return acosh(this)
	 */
	final public MyDouble acosh() {
		val = MyMath.acosh(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return asinh(this)
	 */
	final public MyDouble asinh() {
		val = MyMath.asinh(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return csc(this)
	 */
	final public MyDouble csc() {
		val = MyMath.csc(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return sec(this)
	 */
	final public MyDouble sec() {
		val = MyMath.sec(val);
		isAngle = false;
		return this;
	}

	/**
	 * @return cot(this)
	 */
	final public MyDouble cot() {
		val = MyMath.cot(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return csch(this)
	 */
	final public MyDouble csch() {
		val = MyMath.csch(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return sech(this)
	 */
	final public MyDouble sech() {
		val = MyMath.sech(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return coth(this)
	 */
	final public MyDouble coth() {
		val = MyMath.coth(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return atanh(this)
	 */
	final public MyDouble atanh() {
		val = MyMath.atanh(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return atanh(this)
	 */
	final public MyDouble cosineIntegral() {
		val = MyMath2.ci(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return atanh(this)
	 */
	final public MyDouble sineIntegral() {
		val = MyMath2.si(val);
		isAngle = false;
		return this;
	}
	
	/**
	 * @return atanh(this)
	 */
	final public MyDouble expIntegral() {
		val = MyMath2.ei(val);
		isAngle = false;
		return this;
	}
	/**
	 * @return this!
	 */
	final public MyDouble factorial() {
		val = MyMath2.factorial(val);
		isAngle = false;
		return this;
	}

	/**
	 * @return gamma(this)
	 */
	final public MyDouble gamma() {
		val = MyMath2.gamma(val);
		isAngle = false;
		return this;
	}

	/**
	 * @param lt function to evaluate
	 * @return value of lt(this)
	 */
	final public MyDouble apply(Evaluatable lt) {
		val = lt.evaluate(val);
		isAngle = false; // want function to return numbers eg f(x) = sin(x),
							// f(45^o)
		return this;
	}

	/*
	 * interface NumberValue
	 */
	final public MyDouble getNumber() {
		return new MyDouble(this);

		/*
		 * Michael Borcherds 2008-05-20 removed unstable optimisation fails for
		 * eg -2 sin(x) - 5 cos(x) if (isInTree()) { // used in expression node
		 * tree: be careful return new MyDouble(this); } else { // not used
		 * anywhere: reuse this object return this; }
		 */
	}

	public boolean isConstant() {
		return true;
	}

	final public HashSet<GeoElement> getVariables() {
		return null;
	}

	final public boolean isLeaf() {
		return true;
	}

	final public double getDouble() {
		return val;
	}

	final public GeoElement toGeoElement() {
		GeoNumeric num = new GeoNumeric(kernel.getConstruction(),val);
		return num;
	}

	public boolean isNumberValue() {
		return true;
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isBooleanValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}

	public boolean isTextValue() {
		return false;
	}

	final public boolean isExpressionNode() {
		return false;
	}

	public boolean isListValue() {
		return false;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	public boolean isVector3DValue() {
		return false;
	}

	/**
	 * parse eg 3.45645% -> 3.45645/100
	 * @param app application for showing errors
	 * @param str string representation ending with %
	 * @return value as fraction
	 */
	public static double parsePercentage(App app, String str) {
		return parseDouble(app, str.substring(0, str.length() - 1)) / 100;
	}


	/**
	 * extension of StringUtil.parseDouble() to cope with unicode digits eg Arabic
	 * @param str string to be parsed
	 * @param app application for showing errors
	 * @return value
	 */
	public static double parseDouble(App app, String str) {
		StringBuilder sb = new StringBuilder();
		sb.setLength(0);
		for (int i = 0; i < str.length(); i++) {
			int ch = str.charAt(i);
			if (ch <= 0x30) {
				sb.append(str.charAt(i) + ""); // eg .
				continue;
			}

			// check roman first (most common)
			else if (ch <= 0x39)
				ch -= 0x30; // Roman (normal)
			else if (ch <= 0x100) {
				sb.append(str.charAt(i) + ""); // eg E
				continue;
			} else if (ch <= 0x669)
				ch -= 0x660; // Arabic-Indic
			else if (ch == 0x66b) { // Arabic decimal point
				sb.append(".");
				continue;
			} else if (ch <= 0x6f9)
				ch -= 0x6f0;
			else if (ch <= 0x96f)
				ch -= 0x966;
			else if (ch <= 0x9ef)
				ch -= 0x9e6;
			else if (ch <= 0xa6f)
				ch -= 0xa66;
			else if (ch <= 0xaef)
				ch -= 0xae6;
			else if (ch <= 0xb6f)
				ch -= 0xb66;
			else if (ch <= 0xbef)
				ch -= 0xbe6; // Tamil
			else if (ch <= 0xc6f)
				ch -= 0xc66;
			else if (ch <= 0xcef)
				ch -= 0xce6;
			else if (ch <= 0xd6f)
				ch -= 0xd66;
			else if (ch <= 0xe59)
				ch -= 0xe50; // Thai
			else if (ch <= 0xed9)
				ch -= 0xed0;
			else if (ch <= 0xf29)
				ch -= 0xf20; // Tibetan
			else if (ch <= 0x1049)
				ch -= 0x1040; // Mayanmar (Burmese)
			else if (ch <= 0x17e9)
				ch -= 0x17e0; // Khmer
			else if (ch <= 0x1819)
				ch -= 0x1810; // Mongolian
			else if (ch <= 0x1b59)
				ch -= 0x1b50; // Balinese
			else if (ch <= 0x1bb9)
				ch -= 0x1bb0; // Sudanese
			else if (ch <= 0x1c49)
				ch -= 0x1c40; // Lepcha
			else if (ch <= 0x1c59)
				ch -= 0x1c50; // Ol Chiki
			else if (ch <= 0xa8d9)
				ch -= 0xa8d0; // Saurashtra
			else {
				sb.append(str.charAt(i) + ""); // eg -
				continue;
			}
			sb.append(ch + "");
		}
		try {
		return StringUtil.parseDouble(sb.toString());
		} catch (Exception e) {
			// eg try to parse "1.2.3", "1..2"
			throw new MyError(app, "InvalidInput");
		}
		/*
		 * "\u0030"-"\u0039", "\u0660"-"\u0669", "\u06f0"-"\u06f9",
		 * "\u0966"-"\u096f", "\u09e6"-"\u09ef", "\u0a66"-"\u0a6f",
		 * "\u0ae6"-"\u0aef", "\u0b66"-"\u0b6f", "\u0be7"-"\u0bef",
		 * "\u0c66"-"\u0c6f", "\u0ce6"-"\u0cef", "\u0d66"-"\u0d6f",
		 * "\u0e50"-"\u0e59", "\u0ed0"-"\u0ed9", "\u1040"-"\u1049"
		 */

	}
	/**
	 * @param lt lt
	 * @return gammaIncompleteRegularized(lt,this)
	 */
	public ExpressionValue gammaIncompleteRegularized(NumberValue lt) {
		val = MyMath2.gammaIncompleteRegularized(lt.getDouble(),
				val);
		isAngle = false;
		return this;
	}
	/**
	 * @param lt lt
	 * @return gammaIncomplete(lt,this)
	 */
	public ExpressionValue gammaIncomplete(NumberValue lt) {
		val = MyMath2.gammaIncomplete(lt.getDouble(), val);
		isAngle = false;
		return this;
	}
	/**
	 * @param lt lt
	 * @return beta(lt,this)
	 */
	public ExpressionValue beta(NumberValue lt) {
		val = MyMath2.beta(val, lt.getDouble());
		isAngle = false;
		return this;
	}
	/**
	 * @param lt lt
	 * @return betaIncomplete(lt,this)
	 */
	public ExpressionValue betaIncomplete(VectorValue lt) {
		GeoVec2D vec = lt.getVector();
		val = MyMath2.betaIncomplete(vec.getX(), vec.getY(), val);
		isAngle = false;
		return this;
	}
	/**
	 * @param lt lt
	 * @return betaIncompleteRegularized(lt,this)
	 */
	public ExpressionValue betaIncompleteRegularized(VectorValue lt) {
		GeoVec2D vec = lt.getVector();
		val = MyMath2.betaIncompleteRegularized(vec.getX(),
				vec.getY(), val);
		isAngle = false;
		return this;
	}

	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	public Kernel getKernel() {
		return kernel;
	}

	/*
	 * needed for AlgoUnique (non-Javadoc) so that Kernel.isZero() is used
	 */
	public int compareTo(Object arg0) {
		if (arg0 instanceof MyDouble) {
			MyDouble d = (MyDouble) arg0;
			if (Kernel.isEqual(val, d.getDouble())) {
				return 0;
			}
			return val - d.getDouble() < 0 ? -1 : 1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object d) {
		if (d == null) {
			return false;
		}

		if (d instanceof MyDouble) {
			return Kernel.isEqual(((MyDouble) d).getDouble(), val);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) (val*1000);
	}

	public boolean isDefined() {
		return !Double.isNaN(val);
	}

	/**
	 * @return fractional part using Wolfram's convention (fractionalPart(-0.6)=-0.6)
	 */
	public ExpressionValue fractionalPart() {
		return new MyDouble(kernel,val>0?val-Math.floor(val):val-Math.ceil(val));
	}
	/**
	 * @return rieman zeta of this number
	 */
	public ExpressionValue zeta() {
		return new MyDouble(kernel,MyMath2.zeta(val));
	}

}
