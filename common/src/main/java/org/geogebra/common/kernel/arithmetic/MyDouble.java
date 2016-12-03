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

package org.geogebra.common.kernel.arithmetic;

import java.math.BigDecimal;
import java.util.HashSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.MyMath2;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * 
 * @author Markus Hohenwarter
 */

public class MyDouble extends ValidExpression implements NumberValue,
		Comparable<Object> {

	private double val;
	private int angleDim = 0;

	/**
	 * kernel
	 */
	protected Kernel kernel;

	/**
	 * Do not use integer operations beyond this bound
	 */
	public static final double LARGEST_INTEGER = 9007199254740992.0; // 0x020000000000000

	/**
	 * @param kernel
	 *            kernel
	 */
	public MyDouble(Kernel kernel) {
		this(kernel, 0.0);
	}

	/**
	 * Creates new MyDouble
	 * 
	 * @param kernel
	 *            kernel
	 * @param x
	 *            value
	 */
	public MyDouble(Kernel kernel, double x) {
		this.kernel = kernel;
		val = x;
	}

	/**
	 * @param d
	 *            MyDouble to copy
	 */
	public MyDouble(MyDouble d) {
		kernel = d.kernel;
		val = d.val;
		angleDim = d.angleDim;
	}

	/**
	 * called from the parser power must be a string of unicode superscript
	 * digits
	 * 
	 * @param kernel
	 *            kernel
	 * @param power
	 *            superscript power
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

	@Override
	public MyDouble deepCopy(Kernel kernel1) {
		MyDouble ret = new MyDouble(this);
		ret.kernel = kernel1;
		return ret;
	}

	/**
	 * @param x
	 *            new value
	 */
	public void set(double x) {
		val = x;
	}

	public void resolveVariables(EvalInfo info) {
		// do nothing
	}

	@Override
	public String toString(StringTemplate tpl) {
		if (angleDim == 1) {
			// // convert to angle value first, see issue 87
			// // http://code.google.com/p/geogebra/issues/detail?id=87
			// double angleVal = Kernel.convertToAngleValue(val);
			// return kernel.formatAngle(angleVal, tpl, false).toString();
			return kernel.formatAngle(val, tpl, true).toString();
		}

		// String ret = kernel.format(Kernel.checkDecimalFraction(val), tpl);
		String ret = kernel.format((val), tpl);

		if (tpl.isNumeric()) {
			return ret;
		}

		// convert eg 0.125 to exact(0.125) so that Giac does an exact
		// calculation with it
		// numbers entered in the CAS View are handled by MySpecialDoule
		// this code is just used when accessing a GeoGebra object
		// eg Input Bar: f(x)=x^-0.5
		// CAS View: Integral[f,1,Infinity]

		if (exactEqual(val, Math.PI)) {
			return "pi";
		}
		if (exactEqual(val, Math.E)) {
			return "e";
		}

		// Note: exact(0.3333333333333) gives 1/3
		if (ret.indexOf('.') > -1) {
			return StringUtil.wrapInExact(ret, tpl);
		}

		return ret;

	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(tpl);
	}

	/**
	 * Switches to angle mode (to use degrees)
	 * 
	 * @return reference to self
	 */
	public MyDouble setAngle() {
		angleDim = 1;
		return this;
	}

	/**
	 * @return whether this is angle
	 */
	public boolean isAngle() {
		return angleDim == 1;
	}

	public int getAngleDim() {
		return angleDim;
	}

	/**
	 * @return random MyDouble
	 */
	final public MyDouble random() {
		val = kernel.getApplication().getRandomNumber();
		angleDim = 0;
		return this;
	}

	/**
	 * c = a + b
	 * 
	 * @param a
	 *            1st summand
	 * @param b
	 *            2nd summand
	 * @param c
	 *            result
	 */
	final public static void add(MyDouble a, NumberValue b, MyDouble c) {
		c.angleDim = a.angleDim == b.getAngleDim() ? a.angleDim : 0;
		c.set(a.val + b.getDouble());
	}

	/**
	 * c = a - b
	 * 
	 * @param a
	 *            subtrahend
	 * @param b
	 *            minuend
	 * @param c
	 *            result
	 */
	final public static void sub(MyDouble a, NumberValue b, MyDouble c) {
		c.angleDim = a.angleDim == b.getAngleDim() ? a.angleDim : 0;
		c.set(a.val - b.getDouble());
	}

	/**
	 * c = a * b
	 * http://functions.wolfram.com/Constants/ComplexInfinity/introductions
	 * /Symbols/ShowAll.html
	 * 
	 * @param a
	 *            1st factor
	 * @param b
	 *            2nd factor
	 * @param c
	 *            result
	 * */
	final public static void mult(MyDouble a, NumberValue b, MyDouble c) {
		c.angleDim = a.angleDim + b.getAngleDim();
		double bval = b.getDouble();
		// ? * anything = ?
		if (Double.isNaN(a.val) || Double.isNaN(bval)) {
			c.set(Double.NaN);
			return;
		}

		// (infinity) * (-infinity) = ?
		if (Double.isInfinite(a.val) && Double.isInfinite(bval)
				&& Math.signum(a.val) != Math.signum(bval)) {
			c.set(Double.NaN);
			return;
		}

		// gives correct answer for eg -3 * infinity
		c.set(a.val * bval);
	}

	/**
	 * c = a * b
	 * http://functions.wolfram.com/Constants/ComplexInfinity/introductions
	 * /Symbols/ShowAll.html
	 * 
	 * @param a
	 *            1st factor
	 * @param b
	 *            2nd factor
	 * @param c
	 *            result
	 * */
	final public static void mult(MyDouble a, double b, MyDouble c) {
		c.angleDim = a.angleDim;

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

	/**
	 * c = a / b
	 * 
	 * @param a
	 *            dividend
	 * @param b
	 *            divisor
	 * @param c
	 *            result
	 */
	final public static void div(MyDouble a, MyDouble b, MyDouble c) {
		c.angleDim = a.angleDim - b.angleDim;
		c.set(a.val / b.val);
	}

	/**
	 * c = pow(a,b)
	 * 
	 * @param a
	 *            base
	 * @param b
	 *            exponent
	 * @param c
	 *            result
	 */
	final public static void pow(MyDouble a, MyDouble b, MyDouble c) {
		c.angleDim = b.angleDim > 0 ? 0 : a.angleDim;

		// Infinity ^ 0 -> NaN
		// http://functions.wolfram.com/Constants/ComplexInfinity/introductions/Symbols/ShowAll.html
		if (Kernel.isZero(b.val)
				&& (Double.isInfinite(a.val) || Double.isNaN(a.val))) {
			c.set(Double.NaN);
			return;
		}

		c.set(Math.pow(a.val, b.val));
	}

	/**
	 * Like Math.pow, but Infinity ^ 0 -> NaN
	 * 
	 * @param a
	 *            base
	 * @param b
	 *            exponent
	 * @return power a^b
	 */
	final public static double pow(double a, double b) {
		

		// Infinity ^ 0 -> NaN
		// http://functions.wolfram.com/Constants/ComplexInfinity/introductions/Symbols/ShowAll.html
		if (Kernel.isZero(b)
				&& (Double.isInfinite(a) || Double.isNaN(a))) {			
			return Double.NaN;
		}

		return Math.pow(a, b);
	}

	/**
	 * c = -pow(-a,b)
	 * 
	 * @param a
	 *            base
	 * @param b
	 *            exponent
	 * @param c
	 *            result
	 */
	final public static void powDoubleSgnChange(MyDouble a, MyDouble b,
			MyDouble c) {
		c.angleDim = b.angleDim > 0 ? 0 : a.angleDim;

		// Infinity ^ 0 -> NaN
		// http://functions.wolfram.com/Constants/ComplexInfinity/introductions/Symbols/ShowAll.html
		if (Kernel.isZero(b.val)
				&& (Double.isInfinite(a.val) || Double.isNaN(a.val))) {
			c.set(Double.NaN);
			return;
		}

		c.set(-Math.pow(-a.val, b.val));
	}

	/**
	 * @return cos(this)
	 */
	final public MyDouble cos() {
		val = Math.cos(val);
		angleDim = 0;
		checkZero();
		return this;
	}

	/**
	 * @return sin(this)
	 */
	final public MyDouble sin() {
		val = Math.sin(val);
		angleDim = 0;
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
		if (Kernel.isEqual(Math.abs(val) % Math.PI, Kernel.PI_HALF)) {
			val = Double.NaN;
		} else {
			val = Math.tan(val);
			checkZero();
		}
		angleDim = 0;
		return this;
	}

	/**
	 * @return acos(this)
	 * @param deg
	 *            whether result should be degrees
	 */
	final public MyDouble acos(boolean deg) {
		angleDim = deg ? 1 : 0;
		set(Math.acos(val));
		return this;
	}

	/**
	 * @return asin(this)
	 * @param deg
	 *            whether result should be degrees
	 */
	final public MyDouble asin(boolean deg) {
		angleDim = deg ? 1 : 0;
		set(Math.asin(val));
		return this;
	}

	/**
	 * @return atan(this)
	 * @param deg
	 *            whether result should be degrees
	 */
	final public MyDouble atan(boolean deg) {
		angleDim = deg ? 1 : 0;
		set(Math.atan(val));
		return this;
	}

	/**
	 * @param y
	 *            y
	 * @param deg
	 *            whether result should be degrees
	 * @return atan2(this,y)
	 */
	final public MyDouble atan2(NumberValue y, boolean deg) {
		angleDim = deg ? 1 : 0;
		set(Math.atan2(val, y.getDouble()));
		return this;
	}

	/**
	 * @return log(this)
	 */
	final public MyDouble log() {
		val = Math.log(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @param base
	 *            logarithm base
	 * @return log_base(this)
	 */
	final public MyDouble log(NumberValue base) {
		val = Math.log(val) / Math.log(base.getDouble());
		angleDim = 0;
		return this;
	}

	/**
	 * @return erf(this)
	 */
	final public MyDouble erf() {
		val = MyMath2.erf(0.0, 1.0, val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return inverf(this)
	 */
	final public MyDouble inverf() {
		val = MyMath2.inverf(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @param order
	 *            order
	 * @return polygamma(this,order)
	 */
	final public MyDouble polygamma(NumberValue order) {
		val = MyMath2.polyGamma(order, val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return psi(this)
	 */
	final public MyDouble psi() {
		val = MyMath2.psi(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return log_10(this)
	 */
	final public MyDouble log10() {
		val = Math.log(val) / MyMath.LOG10;
		angleDim = 0;
		return this;
	}

	/**
	 * @return log_2(this)
	 */
	final public MyDouble log2() {
		val = Math.log(val) / MyMath.LOG2;
		angleDim = 0;
		return this;
	}

	/**
	 * @return exp(this)
	 */
	final public MyDouble exp() {
		val = Math.exp(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return sqrt(this)
	 */
	final public MyDouble sqrt() {
		val = Math.sqrt(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return cbrt(this)
	 */
	final public MyDouble cbrt() {
		val = MyMath.cbrt(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @param a
	 *            difference to be added to value
	 */
	final public void add(double a){
		val += a;
	}
	/**
	 * @return abs(this)
	 */
	final public MyDouble abs() {
		val = Math.abs(val);
		return this;
	}

	/**
	 * @param angleUnit
	 *            angle unit, eg Kernel.ANGLE_DEGREE
	 * @return floor(this)
	 */
	final public MyDouble floor(int angleUnit) {
		// angle in degrees
		// kernel.checkInteger() needed otherwise floor(60degrees) gives
		// 59degrees
		if (angleDim == 1 && angleUnit == Kernel.ANGLE_DEGREE) {
			set(Kernel.PI_180
					* Math.floor(Kernel.checkInteger(val * Kernel.CONST_180_PI)));
		} else {
			// number or angle in radians
			set(Math.floor(Kernel.checkInteger(val)));
		}
		return this;
	}

	/**
	 * @param angleUnit
	 *            angle unit, eg Kernel.ANGLE_DEGREE
	 * @return ceil(this)
	 */
	final public MyDouble ceil(int angleUnit) {
		// angle in degrees
		// kernel.checkInteger() needed otherwise ceil(241deg) fails
		if (angleDim == 1 && angleUnit == Kernel.ANGLE_DEGREE) {
			set(Kernel.PI_180
					* Math.ceil(Kernel.checkInteger(val * Kernel.CONST_180_PI)));
		} else {
			// number or angle in radians
			set(Math.ceil(Kernel.checkInteger(val)));
		}
		return this;
	}

	/**
	 * @param angleUnit
	 *            angle unit, eg Kernel.ANGLE_DEGREE
	 * @return round(this)
	 */
	final public MyDouble round(int angleUnit) {
		// angle in degrees
		if (angleDim == 1 && angleUnit == Kernel.ANGLE_DEGREE) {
			set(Kernel.PI_180 * MyDouble.doRound(val * Kernel.CONST_180_PI));
		} else {
			// number or angle in radians
			set(MyDouble.doRound(val));
		}
		return this;
	}

	/**
	 * For 12.34 round(1) rounds to 1 DP (yields 12.3), round(-1) yields 10
	 * 
	 * @param digits
	 *            number of digits
	 * @param angleUnit
	 *            angle unit, eg Kernel.ANGLE_DEGREE
	 * @return rounded value
	 */
	final public MyDouble round(double digits, int angleUnit) {
		if (!Kernel.isInteger(digits)) {
			set(Double.NaN);
		}
		double pow = Math.pow(10, digits);
		set(val * pow);
		round(angleUnit);
		set(val / pow);
		return this;
	}

	/**
	 * Java quirk/bug Round(NaN) = 0
	 */
	final private static double doRound(double x) {
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
		val = MyMath.sgn(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return cosh(this)
	 */
	final public MyDouble cosh() {
		val = MyMath.cosh(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return sinh(this)
	 */
	final public MyDouble sinh() {
		val = MyMath.sinh(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return tanh(this)
	 */
	final public MyDouble tanh() {
		val = MyMath.tanh(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return acosh(this)
	 */
	final public MyDouble acosh() {
		val = MyMath.acosh(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return asinh(this)
	 */
	final public MyDouble asinh() {
		val = MyMath.asinh(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return csc(this)
	 */
	final public MyDouble csc() {
		val = MyMath.csc(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return sec(this)
	 */
	final public MyDouble sec() {
		val = MyMath.sec(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return cot(this)
	 */
	final public MyDouble cot() {
		val = MyMath.cot(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return csch(this)
	 */
	final public MyDouble csch() {
		val = MyMath.csch(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return sech(this)
	 */
	final public MyDouble sech() {
		val = MyMath.sech(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return coth(this)
	 */
	final public MyDouble coth() {
		val = MyMath.coth(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return atanh(this)
	 */
	final public MyDouble atanh() {
		val = MyMath.atanh(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return atanh(this)
	 */
	final public MyDouble cosineIntegral() {
		val = MyMath2.ci(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return atanh(this)
	 */
	final public MyDouble sineIntegral() {
		val = MyMath2.si(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return atanh(this)
	 */
	final public MyDouble expIntegral() {
		val = MyMath2.ei(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return this!
	 */
	final public MyDouble factorial() {
		val = MyMath2.factorial(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @return gamma(this)
	 */
	final public MyDouble gamma() {
		val = MyMath2.gamma(val);
		angleDim = 0;
		return this;
	}

	/**
	 * @param lt
	 *            function to evaluate
	 * @return value of lt(this)
	 */
	final public MyDouble apply(Evaluatable lt) {
		val = lt.evaluate(val);
		angleDim = 0; // want function to return numbers eg f(x) = sin(x),
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

	final public GeoElement toGeoElement(Construction cons) {
		GeoNumeric num = new GeoNumeric(cons, val);
		return num;
	}

	public boolean isNumberValue() {
		return true;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	/**
	 * parse eg 3.45645% -> 3.45645/100
	 * 
	 * @param app
	 *            application for showing errors
	 * @param str
	 *            string representation ending with %
	 * @return value as fraction
	 */
	public static double parsePercentage(Localization app, String str) {
		return parseDouble(app, str.substring(0, str.length() - 1)) / 100;
	}

	/**
	 * extension of StringUtil.parseDouble() to cope with unicode digits eg
	 * Arabic
	 * 
	 * @param str
	 *            string to be parsed
	 * @param app
	 *            application for showing errors
	 * @return value
	 */
	public static double parseDouble(Localization app, String str) {
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
			throw new MyError(app, new String[] { "InvalidInput", str });
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
	 * @param lt
	 *            lt
	 * @return gammaIncompleteRegularized(lt,this)
	 */
	public ExpressionValue gammaIncompleteRegularized(NumberValue lt) {
		val = MyMath2.gammaIncompleteRegularized(lt.getDouble(), val);
		angleDim = 0;
		return this;
	}

	/**
	 * @param lt
	 *            lt
	 * @return gammaIncomplete(lt,this)
	 */
	public ExpressionValue gammaIncomplete(NumberValue lt) {
		val = MyMath2.gammaIncomplete(lt.getDouble(), val);
		angleDim = 0;
		return this;
	}

	/**
	 * @param lt
	 *            lt
	 * @return beta(lt,this)
	 */
	public ExpressionValue beta(NumberValue lt) {
		val = MyMath2.beta(val, lt.getDouble());
		angleDim = 0;
		return this;
	}

	/**
	 * @param lt
	 *            lt
	 * @return betaIncomplete(lt,this)
	 */
	public ExpressionValue betaIncomplete(VectorValue lt) {
		GeoVec2D vec = lt.getVector();
		val = MyMath2.betaIncomplete(vec.getX(), vec.getY(), val);
		angleDim = 0;
		return this;
	}

	/**
	 * @param lt
	 *            lt
	 * @return betaIncompleteRegularized(lt,this)
	 */
	public ExpressionValue betaIncompleteRegularized(VectorValue lt) {
		GeoVec2D vec = lt.getVector();
		val = MyMath2.betaIncompleteRegularized(vec.getX(), vec.getY(), val);
		angleDim = 0;
		return this;
	}

	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
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
		return Double.valueOf(val).hashCode();
	}

	public boolean isDefined() {
		return !Double.isNaN(val);
	}

	/**
	 * @return fractional part using Wolfram's convention
	 *         (fractionalPart(-0.6)=-0.6)
	 */
	public ExpressionValue fractionalPart() {
		return new MyDouble(kernel, val > 0 ? val - Math.floor(val) : val
				- Math.ceil(val));
	}

	/**
	 * @return rieman zeta of this number
	 */
	public ExpressionValue zeta() {
		return new MyDouble(kernel, MyMath2.zeta(val));
	}

	@Override
	public ExpressionValue derivative(FunctionVariable fv, Kernel kernel0) {
		return new MyDouble(kernel0, 0);
	}

	@Override
	public ExpressionValue integral(FunctionVariable fv, Kernel kernel0) {
		return new ExpressionNode(kernel0, this, Operation.MULTIPLY, fv);
	}

	/**
	 * @param d
	 *            number
	 * @return whether d is valid finite real number
	 */
	public static boolean isFinite(double d) {
		return !Double.isNaN(d) && !Double.isInfinite(d);
	}

	@Override
	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	/**
	 * #5149 Double.toString(x) gives e not E in GWT (2.7 at least) (important
	 * as GeoGebra parses 1e2 as 1 * e * 2)
	 * 
	 * @param x
	 *            x
	 * @return x as String
	 */
	public static String toString(double x) {

		String ret = Double.toString(x);

		if (ret.indexOf('e') > -1) {
			return ret.replace('e', 'E');
		}

		return ret;
	}

	/**
	 * #5149 may not be needed, but best to be safe
	 * 
	 * @param bd
	 *            number
	 * @return bd as String with e replaced by E
	 */
	public static String toString(BigDecimal bd) {
		String ret = bd.toString();

		if (ret.indexOf('e') > -1) {
			return ret.replace('e', 'E');
		}

		return ret;
	}

	public String getLabel(StringTemplate tpl) {
		return toValueString(tpl);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.NUMBER;
	}

	/**
	 * 
	 * @param col
	 *            color component
	 * @return col transformed from double[0,1] to int[0,255] but truncated if
	 *         outside this range
	 */
	public static int normalize0to255(double col) {
		return truncate0to255((int) (col * 255));
	}

	/**
	 * 
	 * @param col
	 *            color component
	 * @return col truncated to the range int[0,255]
	 */
	public static int truncate0to255(int col) {
		if (col < 0) {
			return 0;
		} else if (col > 255) {
			return 255;
		}

		return col;

	}

	/**
	 * Compares two numbers for EXACT equality [without causing a FindBugs
	 * warning]. In most cases you should use Kernel.isEqual()
	 * 
	 * @param a
	 *            first number
	 * @param b
	 *            second number
	 * @return whether those are bitwise equal
	 */
	@SuppressFBWarnings({ "FE_FLOATING_POINT_EQUALITY",
			"OK to compare floats, as even tiny differences should trigger update" })
	public static boolean exactEqual(double a, double b) {
		return a == b;
	}

	/**
	 * works for positive and negative numbers see
	 * http://findbugs.sourceforge.net/bugDescriptions.html#IM_BAD_CHECK_FOR_ODD
	 * 
	 * @param i
	 *            tested number
	 * @return true if i is odd
	 */
	public static boolean isOdd(int i) {
		return (i % 2) != 0;
	}
}
