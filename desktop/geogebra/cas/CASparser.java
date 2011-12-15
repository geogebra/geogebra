/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.cas;

import geogebra.common.cas.CASException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.cas.CASParserInterface;
import geogebra.common.kernel.geos.GeoDummyVariable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.parser.ParseException;
import geogebra.kernel.Kernel;

/**
 * Handles parsing and evaluating of input in the CAS view.
 * 
 * @author Markus Hohenwarter
 */
public class CASparser implements CASParserInterface{
	private Kernel kernel;
	
	public CASparser(Kernel kernel) {	
		this.kernel = kernel;
	}
	
	public Kernel getKernel() {
		return kernel;
	}
	
	/**
	 * Parses the given expression and returns it as a ValidExpression.
	 * @throws CASException something goes wrong
	 */
	public ValidExpression parseGeoGebraCASInput(String exp) throws CASException {
		try {
			return kernel.getParser().parseGeoGebraCAS(exp);
		} catch (ParseException e) {
			throw new CASException(e);
		}
	}
	
	/**
	 * Parses the given expression and resolves variables as GeoDummy objects.
	 * The result is returned as a ValidExpression.
	 */
	public ValidExpression parseGeoGebraCASInputAndResolveDummyVars(String inValue) {
		if (inValue == null || inValue.length() == 0)
			return null;
		
		try {
			// parse input into valid expression
			ValidExpression ve = parseGeoGebraCASInput(inValue);
			
			// resolve Variable objects in ValidExpression as GeoDummy objects
			resolveVariablesForCAS(ve);
			
			return ve;
		//}catch (MaximaVersionUnsupportedExecption e) {
		//	throw e; // propagate exception
		}catch (Throwable e) {
			return null;
		}
	}
	
	/**
	 * Resolves all variables in ValidExpression. Unknown variables are
	 * kept as symbolic variables.
	 */
	public synchronized void resolveVariablesForCAS(ExpressionValue ev) {
		
		// add local variables to kernel, 
		// e.g. f(a,b) := 3*a+c*b has local variables a, b
		boolean isFunction = ev instanceof Function;
		FunctionVariable [] funVars;
		if (isFunction) {
			Construction cmdCons = kernel.getConstruction();  
			funVars = ((Function) ev).getFunctionVariables();
			for (FunctionVariable funVar : funVars) {
				GeoElement localVarGeo = new GeoDummyVariable(cmdCons, funVar.toString());
				cmdCons.addLocalVariable(funVar.toString(), localVarGeo);
			}
		}
		
		// resolve variables of valid expression
		kernel.setResolveUnkownVarsAsDummyGeos(true);
		ev.resolveVariables();
		kernel.setResolveUnkownVarsAsDummyGeos(false);
		
		// remove local variables from kernel
		if (isFunction) {
			Construction cmdCons = kernel.getConstruction();  
			funVars = ((Function) ev).getFunctionVariables();
			for (FunctionVariable funVar : funVars) {	
				cmdCons.removeLocalVariable(funVar.toString());
			}
		}
	}
	
	
	
	/**
	 * Tries to convert the given MathPiper string to GeoGebra syntax.
	 */
	public String toGeoGebraString(ExpressionValue ev) throws CASException {
		try {
			return toString(ev, StringType.GEOGEBRA);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new CASException(e);
		}
	}
	
	/**
	 * Tries to convert the given CAS string to the given syntax.
	 * @param STRING_TYPE one of StringType.GEOGEBRA, STRING_TYPE_GEOGEBRA_XML
	 */
	public String toString(ExpressionValue ev, StringType STRING_TYPE) {
		String GeoGebraString;
		
		if (!ev.isExpressionNode()) {
			ev = new ExpressionNode(kernel, ev);			
		}
		
		ExpressionNode en = (ExpressionNode) ev;
		GeoGebraString = en.getCASstring(STRING_TYPE, true);		
		return GeoGebraString;
	}
	

	
	/**
	 * Tries to convert the given MathPiper string to GeoGebra syntax.
	 */
	public ValidExpression parseMathPiper(String exp) throws CASException {
		try {
			return kernel.getParser().parseMathPiper(exp);
		} catch (Throwable t) {
			throw new CASException(t);
		}
	}
	
	/**
	 * Tries to convert the given MPReduce string to GeoGebra syntax.
	 */
	public ValidExpression parseMPReduce(String exp) throws CASException {
		try {
			return kernel.getParser().parseMPReduce(exp);
		} catch (Throwable t) {
			throw new CASException(t);
		}		
	}
	
	/**
	 * Tries to convert the given Maxima string to GeoGebra syntax.
	 */
	public ValidExpression parseMaxima(String exp) throws CASException {
		try {
			return kernel.getParser().parseMaxima(exp);
		} catch (Throwable t) {
			throw new CASException(t);
		}	
	}


	/**
	 * Converts all index characters ('_', '{', '}') in the given String
	 * to "unicode" + charactercode + DELIMITER Strings. This is needed because
	 * MathPiper does not handle indices correctly.
	 */
	public synchronized String replaceIndices(String str) {
		int len = str.length();
		StringBuilder replaceIndices = new StringBuilder();
		
		boolean foundIndex = false;

		// convert every single character and append it to sb
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			int code = c;
			
			boolean replaceCharacter = false;			
			switch (c) {
				case '_': // start index
					foundIndex = true;
					replaceCharacter = true;
					
					if (i > 0 && str.charAt(i-1) == '\\'){
						replaceCharacter = false;
						// \\_ is translated to _
						replaceIndices.deleteCharAt(replaceIndices.length()-1);
					}
					break;
										
				case '{': 	
					if (foundIndex) {
						replaceCharacter = true;						
					}					
					break;					
					
				case '}':
					if (foundIndex) {
						replaceCharacter = true;
						foundIndex = false; // end of index
					}					
					break;
					
				default:
					replaceCharacter = false;
			}
			
			if (replaceCharacter) {
				replaceIndices.append(ExpressionNodeConstants.UNICODE_PREFIX);
				replaceIndices.append(code);
				replaceIndices.append(ExpressionNodeConstants.UNICODE_DELIMITER);
			} else {
				replaceIndices.append(c);
			}
		}
					
		return replaceIndices.toString();
	}

	/**
	 * Reverse operation of removeSpecialChars().
	 * @see ExpressionNode#operationToString() for XCOORD, YCOORD
	 */
	public String insertSpecialChars(String str) {
		int prefixLen = ExpressionNodeConstants.UNICODE_PREFIX.length();
		
		if (str.length() < prefixLen) return str;
		
		int len = str.length();
		StringBuilder insertSpecial = new StringBuilder();

		// convert every single character and append it to sb
		char prefixStart = ExpressionNodeConstants.UNICODE_PREFIX.charAt(0);
		boolean prefixFound;
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			prefixFound = false;

			// first character of prefix found
			if (c == prefixStart && i + prefixLen < str.length()) {
				prefixFound = true;
				// check prefix
				int j = i;
				for (int k = 0; k < prefixLen; k++, j++) {
					if (ExpressionNodeConstants.UNICODE_PREFIX.charAt(k) != str
							.charAt(j)) {
						prefixFound = false;
						break;
					}
				}

				if (prefixFound) {
					// try to get the unicode
					int code = 0;
					char digit;
					while (j < len && Character.isDigit(digit = str.charAt(j))) {
						code = 10 * code + (digit - 48);
						j++;
					}

					if (code > 0 && code < 65536) { // valid unicode
						insertSpecial.append((char) code);
						i = j;
					} else { // invalid
						insertSpecial.append(ExpressionNodeConstants.UNICODE_PREFIX);
						i += prefixLen;
					}
				} else {
					insertSpecial.append(c);
				}
			} else {
				insertSpecial.append(c);
			}
		}
		return insertSpecial.toString();
	}

}
