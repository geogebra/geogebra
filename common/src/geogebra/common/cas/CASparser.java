/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.common.cas;

import geogebra.common.cas.giac.Ggb2giac;
import geogebra.common.cas.mpreduce.Ggb2MPReduce;
import geogebra.common.kernel.CASException;
import geogebra.common.kernel.CASGenericInterface;
import geogebra.common.kernel.CASParserInterface;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.Traversing.NonFunctionCollector;
import geogebra.common.kernel.arithmetic.Traversing.NonFunctionReplacer;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoDummyVariable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.parser.ParseException;
import geogebra.common.kernel.parser.Parser;
import geogebra.common.kernel.parser.cashandlers.ParserFunctions;
import geogebra.common.main.BracketsError;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Handles parsing and evaluating of input in the CAS view.
 * 
 * @author Markus Hohenwarter
 */
public class CASparser implements CASParserInterface{
	private Parser parser;
	private ParserFunctions parserFunctions;
	
	/**
	 * Creates new CAS parser
	 * @param parser parser
	 * @param pf parser functions
	 */
	public CASparser(Parser parser, ParserFunctions pf) {	
		this.parser = parser;
		this.parserFunctions = pf;
	}
	
	
	public ValidExpression parseGeoGebraCASInput(final String exp) throws CASException {
		CASException c;
		try {
			return parser.parseGeoGebraCAS(exp);
		} catch (ParseException e) {
			c =  new CASException(e);
			c.setKey("InvalidInput");
			throw c;
		} catch (BracketsError e) {
			c =  new CASException(e);
			c.setKey("UnbalancedBrackets");
			throw c;
		}
	}
	
	public ValidExpression parseGeoGebraCASInputAndResolveDummyVars(final String inValue) throws CASException {
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
			if(e instanceof CASException)
				throw (CASException)e;
			throw new CASException(e);
		}
		
	}
	
	/**
	 * Resolves all variables in ValidExpression. Unknown variables are
	 * kept as symbolic variables.
	 * TODO check that we need default template here
	 */
	public synchronized void resolveVariablesForCAS(ExpressionValue ev) {
		
		// add local variables to kernel, 
		// e.g. f(a,b) := 3*a+c*b has local variables a, b
		boolean isFunction = ev instanceof Function;
		FunctionVariable [] funVars;
		if (isFunction) {
			Construction cmdCons = ev.getKernel().getConstruction();  
			funVars = ((Function) ev).getFunctionVariables();
			for (FunctionVariable funVar : funVars) {
				GeoElement localVarGeo = new GeoDummyVariable(cmdCons, funVar.toString(StringTemplate.defaultTemplate));
				cmdCons.addLocalVariable(funVar.toString(StringTemplate.defaultTemplate), localVarGeo);
			}
		}
		// resolve variables of valid expression
		ev.getKernel().setResolveUnkownVarsAsDummyGeos(true);
		ev.resolveVariables(false);
		ev.getKernel().setResolveUnkownVarsAsDummyGeos(false);
		
		Set<String> nonFunctions = new TreeSet<String>(); 
		NonFunctionCollector c = NonFunctionCollector.getCollector(nonFunctions);
		NonFunctionReplacer r = NonFunctionReplacer.getCollector(nonFunctions);
		ev.traverse(c);
		ev.traverse(r);
		//TODO: remove local variables from kernel ?
	}
	
	
	
	/**
	 * Tries to convert parsed CAS output to GeoGebra syntax.
	 * @param ev parsed CAS output
	 * @param tpl string template
	 * @return GeoGebra string representation of 
	 * @throws CASException in case the conversion failed
	 */
	public String toGeoGebraString(ExpressionValue ev,StringTemplate tpl) throws CASException {
		try {
			return toString(ev, tpl);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new CASException(e);
		}
	}
	
	/**
	 * Tries to convert the given CAS string to the given syntax.
	 * @param ev parsed CAS output
	 * @param tpl template to be used
	 * @return string representation of ev in given syntax
	 */
	public String toString(ExpressionValue ev, StringTemplate tpl) {
		String GeoGebraString;
		ExpressionNode expr;
		if (!ev.isExpressionNode()) {
			expr = new ExpressionNode(ev.getKernel(), ev);			
		}
		else
			expr = (ExpressionNode) ev;
		GeoGebraString = expr.getCASstring(tpl, true);		
		return GeoGebraString;
	}
	
	/**
	 * Tries to convert the given MPReduce string to GeoGebra syntax.
	 * @param exp MPReduce output
	 * @return parsed expression
	 * @throws CASException if parsing goes wrong
	 */
	public ValidExpression parseMPReduce(String exp) throws CASException {
		try {
			return parser.parseMPReduce(exp);
		} catch (Throwable t) {
			throw new CASException(t);
		}		
	}
	
	/**
	 * Tries to convert the given Giac string to GeoGebra syntax.
	 * @param exp MPReduce output
	 * @return parsed expression
	 * @throws CASException if parsing goes wrong
	 */
	public ValidExpression parseGiac(String exp) throws CASException {
		try {
			return parser.parseGiac(exp);
		} catch (Throwable t) {
			throw new CASException(t);
		}		
	}
	
	/**
	 * Final automata can be in three states
	 * * NORMAL -- no index being read
	 * * UNDERSCORE -- last character was _
	 * * LONG_INDEX -- it found _{, but not yet }
	 *
	 */
	private enum FA {NORMAL,UNDERSCORE,LONG_INDEX}
	/**
	 * Converts all index characters ('_', '{', '}') in the given String
	 * to "unicode" + charactercode + DELIMITER Strings. This is needed because
	 * MathPiper does not handle indices correctly.
	 * @param str input string with _,{,}
	 * @param replaceUnicode whether unicode characters need to be encoded
	 * @return string where _,{,} are replaced
	 */
	public synchronized String replaceIndices(String str, boolean replaceUnicode) {
		int len = str.length();
		StringBuilder replaceIndices = new StringBuilder();
		
		FA state = FA.NORMAL;  
		// convert every single character and append it to sb
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
						
			switch (state) {
				case NORMAL: // start index
					if(c=='_'){
						if (i > 0 && str.charAt(i-1) == '\\'){
							// \\_ is translated to _
							replaceIndices.deleteCharAt(replaceIndices.length()-1);
							replaceIndices.append('_');
						}else{
							state = FA.UNDERSCORE;
							appendcode(replaceIndices,'_');
						}
					}
					else if (replaceUnicode && c > 127 && c != Unicode.angle) {
						appendcode(replaceIndices, c);
					} else {
						replaceIndices.append(c);
					}
					break;
										
				case UNDERSCORE: 	
					if (c=='{') {						
						state = FA.LONG_INDEX;						
					}else{
						state = FA.NORMAL;
					}
					appendcode(replaceIndices,c);
					break;					
					
				case LONG_INDEX:
					if (c=='}') {						
						state = FA.NORMAL;						
					}
					appendcode(replaceIndices,c);					
					break;
			}			
		}
		
		//App.debug(insertSpecialChars(replaceIndices.toString())+" "+replaceIndices.toString());
					
		return replaceIndices.toString();
	}

	private static void appendcode(StringBuilder replaceIndices, int code) {
		replaceIndices.append(ExpressionNodeConstants.UNICODE_PREFIX);
		replaceIndices.append(code);
		replaceIndices.append(ExpressionNodeConstants.UNICODE_DELIMITER);
		
	}


	/**
	 * Reverse operation of removeSpecialChars().
	 * @param str input string
	 * @return input string with 'replaced by !' etc.
	 */
	// see ExpressionNode#operationToString() for XCOORD, YCOORD
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
					while (j < len && StringUtil.isDigit(digit = str.charAt(j))) {
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


	/**
	 * @return parser functions
	 */
	public ParserFunctions getParserFunctions() {
		return parserFunctions;
	}

	/**
	 * Translates a given expression in the format expected by the cas.
	 * 
	 * @param ve
	 *            the Expression to be translated
	 * @param casStringType
	 *            one of StringType.{MAXIMA, MPREDUCE, MATH_PIPER}
	 * @param cas CAS interface
	 * @return the translated String.
	 */
	public String translateToCAS(ValidExpression ve, StringTemplate casStringType,CASGenericInterface cas) {
	

		try {
			String body = ve.wrap().getCASstring(casStringType,
					false);
			
			
			

			return body;
		} finally {
			//do nothing
		}
	}
	private Map<String,String> rbCasTranslations; // translates from GeogebraCAS
	// syntax to the internal CAS
	// syntax.

	/**
	 * Returns the CAS command for the currently set CAS using the given key.
	 * For example, getCASCommand"Expand.0" returns "ExpandBrackets( %0 )" when
	 * MathPiper is the currently used CAS.
	 * 
	 * @param command
	 *            The command to be translated (should end in ".n", where n is
	 *            the number of arguments to this command).
	 * @return The command in CAS format, where parameter n is written as %n.
	 * 
	 */
	public String getTranslatedCASCommand(final String command) {
		return getTranslationRessourceBundle().get(command);
	}

	/**
	 * Returns whether the CAS command key is available, e.g. "Expand.1"
	 * @param commandKey command name suffixed by . and number of arguments, e.g. Derivative.2, Sum.N
	 * @return true if available
	 */
	final public boolean isCommandAvailable(String commandKey) {
		return getTranslatedCASCommand(commandKey) != null;
	}

	/**
	 * Returns the RessourceBundle that translates from GeogebraCAS commands to
	 * their definition in the syntax of the current CAS. Loads this bundle if
	 * it wasn't loaded yet.
	 * 
	 * @return The current ResourceBundle used for translations.
	 * 
	 */
	synchronized Map<String,String> getTranslationRessourceBundle() {
		if (rbCasTranslations == null) {

			switch (parser.getKernel().getCASType()) {

			case GIAC:
				rbCasTranslations = Ggb2giac.getMap(parser.getKernel().getApplication().isHTML5Applet());
				break;

			case MPREDUCE:
			default:
				rbCasTranslations = Ggb2MPReduce.getMap();
				break;

			}
		}
		return rbCasTranslations;
	}
	

}
