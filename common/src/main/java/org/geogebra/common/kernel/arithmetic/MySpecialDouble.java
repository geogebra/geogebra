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
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;

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
	private boolean keepOriginalString;
	private boolean isLetterConstant; // for Pi, Euler, or Degree constant
	private boolean scientificNotation = false;
	private boolean setFromOutside;

	private static MySpecialDouble eulerConstant;

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
		originalString = StringUtil.cannonicNumber(str);

		strToString = originalString;
		if (strToString == null)
			strToString = "0";
		// check if this is a letter constant, e.g. Pi or Euler number
		char firstChar = strToString.charAt(0);
		isLetterConstant = StringUtil.isLetter(firstChar)
				|| firstChar == Unicode.DEGREE_CHAR
				|| strToString.equals(Unicode.EULER_GAMMA_STRING)
				|| strToString.equals("euler_gamma");
		scientificNotation = strToString.indexOf("E") > 0;
		keepOriginalString = !isLetterConstant
				&& (scientificNotation || Double.isInfinite(val));

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
				scientificNotation = strToString.indexOf("E") > 0;
			}
		}
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
	public ExpressionValue deepCopy(Kernel kernel1) {
		if (isEulerConstant())
			return kernel1.getEulerNumber();

		MySpecialDouble ret = new MySpecialDouble(this);
		ret.kernel = kernel1;
		return ret;
	}

	/**
	 * Force this number to keep original input
	 */
	public void setKeepOriginalString() {
		keepOriginalString = true;
	}



	/**
	 * @return true if this equals E (no tolerance)
	 */
	public boolean isEulerConstant() {
		return getDouble() == Math.E;
	}

	@Override
	public String toString(StringTemplate tpl) {
		if (setFromOutside) {
			return super.toString(tpl);
		}
		if (!isLetterConstant) {
			// serializing to CAS -- simply print input

			if (tpl.hasType(StringType.GIAC)) {

				return StringTemplate.convertScientificNotationGiac(originalString);
			}

			// if we are printing result of numeric and user didn't force us to
			// use significant digits
			// print the original string
			if (keepOriginalString
					|| (!tpl.useScientific(kernel.useSignificantFigures) && !strToString
							.contains(".")) || tpl.allowMoreDigits()) {
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

		case GIAC:
			ch = strToString.charAt(0);
			switch (ch) {
			case Unicode.pi:
				return "pi";
			case Unicode.DEGREE_CHAR:
				return "pi/180";
			case Unicode.eulerChar:
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
				return "\\pi";
			case Unicode.DEGREE_CHAR:
				return "^{\\circ}";
			case Unicode.eulerChar:
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

}
