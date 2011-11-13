/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.kernel.Kernel;
import geogebra.util.Unicode;

import java.math.BigDecimal;

/**
 * MyDouble that returns a certain string in toString(). 
 * This is used for example for the degree sign in geogebra.parser.Parser.jj:
 * new MySpecialDouble(kernel, Math.PI / 180.0d,  "\u00b0" );
 * 
 * @author Markus Hohenwarter
 */
public class MySpecialDouble extends MyDouble {
	
	protected String strToString;
	private boolean keepOriginalString;
	private boolean isLetterConstant; // for Pi, Euler, or Degree constant
	private boolean scientificNotation = false;
	
	private static MySpecialDouble eulerConstant;
	
	public MySpecialDouble(Kernel kernel, double val, String strToString) {
		super(kernel, val);
		
		// check if this is a letter constant, e.g. Pi or Euler number
		char firstChar = strToString.charAt(0);
		isLetterConstant = Character.isLetter(firstChar) || firstChar == Unicode.degreeChar;	
		scientificNotation = strToString.indexOf("E") > 0;
		keepOriginalString = !isLetterConstant &&
			(kernel.isKeepCasNumbers() 
			|| scientificNotation
			|| strToString.length() > 16
			|| Double.isInfinite(val));
			
		if (keepOriginalString) {
			BigDecimal bd = new BigDecimal(strToString);
			// avoid E notation for small values
			if (val >= 10E-3 && val < 10E7) {
				// from GeoGebraCAS we get a String using 15 significant figures
				// like 3.14160000000000
				// let's remove trailing zeros
				bd = bd.stripTrailingZeros();
				// no E notation
				strToString = bd.toPlainString();
			} 
			else {
				// use E notation if necessary
				strToString = bd.toString();	
				scientificNotation = strToString.indexOf("E") > 0;
			}
		}
		
		this.strToString = strToString;
	}
	
	/**
	 * Copy constructor.
	 * @param md
	 */
	public MySpecialDouble(MySpecialDouble sd) {
		super(sd);
		strToString = sd.strToString;
		keepOriginalString = sd.keepOriginalString;
		isLetterConstant = sd.isLetterConstant; // for Pi, Euler, or Degree constant
		scientificNotation = sd.scientificNotation;
	}
	
	public ExpressionValue deepCopy(Kernel kernel) {
		if (isEulerConstant())
			return getEulerConstant(kernel);
		
		MySpecialDouble ret = new MySpecialDouble(this);
		ret.kernel = kernel; 
		return ret;
	}  
	
	public void setKeepOriginalString() {
		keepOriginalString = true;
	}

	public static MySpecialDouble getEulerConstant(Kernel kernel) {
		if (eulerConstant == null) {
			eulerConstant = new MySpecialDouble(kernel, Math.E, Unicode.EULER_STRING);
		}
		return eulerConstant;
	}
	
	public boolean isEulerConstant() {
		return getDouble() == Math.E;
	}
	
	
	public String toString() {
		if (!isLetterConstant) {
			if (keepOriginalString) {
				if (scientificNotation)
					// change 5.1E-20 to 5.1*10^(-20) or 5.1 \cdot 10^{-20}
					return kernel.convertScientificNotation(strToString);
				else
					// keep original string
					return strToString;		
			} else { 
				// format double value using kernel settings
				return super.toString();
			}
		}
		
		// letter constants for pi, e, or degree character
		int printForm = kernel.getCASPrintForm();						
		switch (printForm) {
			//case ExpressionNode.STRING_TYPE_JASYMCA:
			case ExpressionNode.STRING_TYPE_MATH_PIPER:
				char ch = strToString.charAt(0);
				switch (ch) {
					case Unicode.piChar:	return "Pi";
					case Unicode.degreeChar:	return "Pi/180";
				} 	
			break;
			
			case ExpressionNode.STRING_TYPE_MAXIMA:
				ch = strToString.charAt(0);
				switch (ch) {
					case Unicode.piChar:	return "%pi";
					case Unicode.degreeChar:	return "%pi/180";
					case Unicode.eulerChar: return "%e";
				} 	
			break;
			
			case ExpressionNode.STRING_TYPE_MPREDUCE:								
				ch = strToString.charAt(0);
				switch (ch) {
					case Unicode.piChar:	return "pi";
					case Unicode.degreeChar:	return "pi/180";
					case Unicode.eulerChar: return "e";
				} 	
			break;
			
			case ExpressionNode.STRING_TYPE_LATEX:
				ch = strToString.charAt(0);
				switch (ch) {
					case Unicode.piChar:	return "\\pi";
					case Unicode.degreeChar:	return "^{\\circ}";
					case Unicode.eulerChar: return Unicode.EULER_STRING; // TODO: find better Latex rendering for "e"
				}
			break;				
		}					
		
		return strToString;	
	}
	
	

}
