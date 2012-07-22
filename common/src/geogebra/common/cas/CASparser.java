/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.common.cas;

import geogebra.common.cas.mpreduce.Ggb2MPReduce;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.VarString;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.Traversing.DerivativeCollector;
import geogebra.common.kernel.arithmetic.Traversing.FunctionExpander;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.cas.CASGenericInterface;
import geogebra.common.kernel.cas.CASParserInterface;
import geogebra.common.kernel.geos.GeoDummyVariable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.parser.ParseException;
import geogebra.common.kernel.parser.Parser;
import geogebra.common.kernel.parser.cashandlers.ParserFunctions;
import geogebra.common.main.App;
import geogebra.common.util.StringUtil;

import java.util.List;
import java.util.Map;

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
	public CASparser(Parser parser,ParserFunctions pf) {	
		this.parser = parser;
		this.parserFunctions = pf;
	}
	
	
	/**
	 * Parses the given expression and returns it as a ValidExpression.
	 * @throws CASException something goes wrong
	 */
	public ValidExpression parseGeoGebraCASInput(String exp) throws CASException {
		try {
			return parser.parseGeoGebraCAS(exp);
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
			App.debug("Parsing failed: "+inValue);
			e.printStackTrace();
			//AbstractApplication.debug(e);
			return null;
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
		FunctionExpander fex = FunctionExpander.getCollector();
		ev.resolveVariables(false);
		ev.traverse(fex);
		ev.getKernel().setResolveUnkownVarsAsDummyGeos(false);
		
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
	 * Tries to convert the given MathPiper string to GeoGebra syntax.
	 * @param exp MathPiper output
	 * @return parsed expression
	 * @throws CASException if parsing goes wrong
	 */
	public ValidExpression parseMathPiper(String exp) throws CASException {
		try {
			return parser.parseMathPiper(exp);
		} catch (Throwable t) {
			throw new CASException(t);
		}
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
	 * Tries to convert the given Maxima string to GeoGebra syntax.
	 * @param exp maxima output
	 * @return parsed output
	 * @throws CASException if parsing goes wrong
	 */
	public ValidExpression parseMaxima(String exp) throws CASException {
		try {
			return parser.parseMaxima(exp);
		} catch (Throwable t) {
			throw new CASException(t);
		}	
	}


	/**
	 * Converts all index characters ('_', '{', '}') in the given String
	 * to "unicode" + charactercode + DELIMITER Strings. This is needed because
	 * MathPiper does not handle indices correctly.
	 * @param str input string with _,{,}
	 * @return string where _,{,} are replaced
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
		Kernel kernel = ve.getKernel();

		try {
			ExpressionNode tmp = null;
			if (!ve.isExpressionNode())
				tmp = new ExpressionNode(kernel, ve);
			else tmp = ((ExpressionNode)ve);
			String body = tmp.getCASstring(casStringType,
					true);
			DerivativeCollector col = DerivativeCollector.getCollector();
			tmp.traverse(col);
			List<GeoElement> derivativeFunctions= col.getFunctions();
			List<Integer> derivativeDegrees= col.getDegrees();
			StringTemplate casTpl = StringTemplate.casTemplate;
			for(int i=0;i<derivativeDegrees.size();i++){
				GeoElement ge = derivativeFunctions.get(i);
				VarString f = (VarString)ge;
				StringBuilder sb = new StringBuilder(80);
				//procedure f'(ggbcasvarx); return sub(locvarx=ggbcasvarx,df(f(locvarx),locvarx,1);
				sb.append("<<procedure ");
				sb.append(ge.getLabel(casTpl));
				int deg = derivativeDegrees.get(i);
				for(int j=0;j<deg;j++)
					sb.append("'");
				sb.append("(");
				sb.append(f.getVarString(casTpl));
				sb.append("); sub(");
				sb.append(f.getVarString(casTpl).replace("ggbcas", "loc"));
				sb.append("=");
				sb.append(f.getVarString(casTpl));
				sb.append(",df(");
				sb.append(ge.getLabel(casTpl));
				sb.append("(");
				sb.append(f.getVarString(casTpl).replace("ggbcas", "loc"));
				sb.append(")");
				sb.append(",");
				sb.append(f.getVarString(casTpl).replaceAll("ggbcas", "loc"));
				sb.append(",");
				sb.append(deg);
				sb.append("));>>");
				try{
					cas.evaluateRaw(sb.toString());
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
			// handle assignments
			String label = ve.getLabel();
			if (label != null) { // is an assignment or a function declaration
				// make sure to escape labels to avoid problems with reserved
				// CAS labels
				label = kernel.printVariableName(casStringType.getStringType(), label);
				if (ve instanceof FunctionNVar) {
					FunctionNVar fun = (FunctionNVar) ve;
					return cas.translateFunctionDeclaration(label,
							fun.getVarString(casStringType), body);
				}
				return cas.translateAssignment(label, body);
			}
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
	public String getTranslatedCASCommand(String command) {
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
		if (rbCasTranslations == null){
			rbCasTranslations = new Ggb2MPReduce().getMap();
			}
		return rbCasTranslations;
	}

}
