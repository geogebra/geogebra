/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.arithmetic;

import java.math.BigDecimal;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.input.Character;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * MyDouble that returns a certain string in toString(). This is used for
 * example for the degree sign in geogebra.parser.Parser.jj: new
 * MySpecialDouble(kernel, Math.PI / 180.0d, "\u00b0" );
 * 
 * @author Markus Hohenwarter
 */
public class MySpecialDouble extends MyDouble {

	private String strToString;
	private final String originalString;
	private final boolean keepOriginalString;
	private final boolean isLetterConstant; // for Pi, Euler, or Degree constant
	private final boolean scientificNotation;
	private boolean setFromOutside;

	/**
	 * @param kernel
	 *            kernel
	 * @param val
	 *            value
	 * @param str
	 *            string representation
	 */
	public MySpecialDouble(Kernel kernel, double val, String str) {
		super(kernel, val);

		// Reduce can't handle .5*8
		originalString = StringUtil.canonicalNumber(str);

		strToString = originalString;

		// check if this is a letter constant, e.g. Pi or Euler number
		char firstChar = strToString.charAt(0);
		isLetterConstant = Character.isLetter(firstChar)
				|| firstChar == Unicode.DEGREE_CHAR
				|| strToString.equals(Unicode.EULER_GAMMA_STRING)
				|| "euler_gamma".equals(strToString);
		boolean containsE = strToString.indexOf("E") > 0;
		keepOriginalString = !isLetterConstant
				&& (containsE || Double.isInfinite(val));

		if (keepOriginalString) {
			BigDecimal bd = new BigDecimal(strToString);
			// avoid E notation for small values
			double absVal = Math.abs(val);
			if (absVal >= 10E-3 && absVal < 10E7) {
				// from GeoGebraCAS we get a String using 15 significant figures
				// like 3.14160000000000
				// let's remove trailing zeros
				bd = bd.stripTrailingZeros();
				// no E notation
				strToString = bd.toPlainString();
			} else {
				// use E notation if necessary
				strToString = MyDouble.toString(bd);
				containsE = strToString.indexOf("E") > 0;
			}
		}
		scientificNotation = containsE;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param sd
	 *            special double to copy
	 */
	public MySpecialDouble(MySpecialDouble sd) {
		super(sd);
		originalString = sd.originalString;
		strToString = sd.strToString;
		keepOriginalString = sd.keepOriginalString;
		isLetterConstant = sd.isLetterConstant; // for Pi, Euler, or Degree
												// constant
		scientificNotation = sd.scientificNotation;
		setFromOutside = sd.setFromOutside;
	}

	@Override
	public MySpecialDouble deepCopy(Kernel kernel1) {
		if (isEulerConstant()) {
			return kernel1.getEulerNumber();
		}

		MySpecialDouble ret = new MySpecialDouble(this);
		ret.kernel = kernel1;
		return ret;
	}

	/**
	 * @return true if this equals E (no tolerance)
	 */
	public boolean isEulerConstant() {
		return MyDouble.exactEqual(getDouble(), Math.E);
	}

	@Override
	public String toString(StringTemplate tpl) {
		if (setFromOutside) {
			return super.toString(tpl);
		}
		if (!isLetterConstant) {
			// serializing to CAS -- simply print input

			if (tpl.hasCASType()) {
				return tpl.convertScientificNotationGiac(originalString);
			}

			// if we are printing result of numeric and user didn't force us to
			// use significant digits
			// print the original string
			if (keepOriginalString
					|| (!tpl.useScientific(kernel.useSignificantFigures)
							&& !strToString.contains("."))
					|| tpl.allowMoreDigits()) {
				if (scientificNotation) {
					// change 5.1E-20 to 5.1*10^(-20) or 5.1 \cdot 10^{-20}
					return tpl.convertScientificNotation(strToString);
				}
				// keep original string
				return strToString;
			}
			// format double value using kernel settings
			return super.toString(tpl);
		}

		// letter constants for pi, e, or degree character
		StringType printForm = tpl.getStringType();
		char ch;
		switch (printForm) {
		case SCREEN_READER:
			ch = strToString.charAt(0);
			switch (ch) {
			case Unicode.pi:
				return tpl.getPi();
			case Unicode.DEGREE_CHAR:
				return tpl.getDegree();
			case Unicode.EULER_CHAR:
				if (strToString.equals(Unicode.EULER_GAMMA_STRING)) {
					return tpl.getEulerGamma();
				}
				return tpl.getEulerNumber();
			}
			break;
		case GIAC:
			ch = strToString.charAt(0);
			switch (ch) {
			case Unicode.pi:
				return "pi";
			case Unicode.DEGREE_CHAR:
				return "pi/180";
			case Unicode.EULER_CHAR:
				if (strToString.equals(Unicode.EULER_GAMMA_STRING)) {
					return "euler\\_gamma";
				}
				return "e";
			}
			break;

		case LATEX:
			ch = strToString.charAt(0);
			switch (ch) {
			case Unicode.pi:
				return "\\pi ";
			case Unicode.DEGREE_CHAR:
				return "^{\\circ}";
			case Unicode.EULER_CHAR:
				if (strToString.equals(Unicode.EULER_GAMMA_STRING)) {
					// approx value
					return "\\mathit{e_{\\gamma}}";
				}
				return "\\textit{e}";
			// return Unicode.EULER_STRING;
			}
			break;
		default:
			break;
		}

		return strToString;
	}

	@Override
	public void set(double val) {
		super.set(val);
		setFromOutside = true;
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean isDigits() {
		return StringUtil.isDigit(strToString.charAt(0));
	}

	@Override
	public ExpressionValue unaryMinus(Kernel kernel) {
		if (!isLetterConstant && !scientificNotation) {
			return new MySpecialDouble(kernel, -getDouble(), "-" + originalString);
		}
		return new ExpressionNode(kernel, new MinusOne(kernel),
				Operation.MULTIPLY, this);
	}

	public boolean isScientificNotation() {
		return scientificNotation;
	}
}
