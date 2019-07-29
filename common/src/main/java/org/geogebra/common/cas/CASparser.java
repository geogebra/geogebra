/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package org.geogebra.common.cas;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.cas.giac.Ggb2giac;
import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.CASParserInterface;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.Traversing.NonFunctionCollector;
import org.geogebra.common.kernel.arithmetic.Traversing.NonFunctionReplacer;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolicI;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.kernel.parser.cashandlers.ParserFunctions;
import org.geogebra.common.main.BracketsError;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Handles parsing and evaluating of input in the CAS view.
 * 
 * @author Markus Hohenwarter
 */
public class CASparser implements CASParserInterface {
	private Parser parser;
	private ParserFunctions parserFunctions;
	// it's defined only for command Solve with parametric equation
	private int nrOfVars = 0;
	private Map<String, String> rbCasTranslations; // translates from
	// GeogebraCAS

	/**
	 * Creates new CAS parser
	 * 
	 * @param parser
	 *            parser
	 * @param pf
	 *            parser functions
	 */
	public CASparser(Parser parser, ParserFunctions pf) {
		this.parser = parser;
		this.parserFunctions = pf;
	}

	@Override
	public ValidExpression parseGeoGebraCASInput(final String exp,
			GeoSymbolicI cell) throws CASException {
		CASException c;
		try {
			return parser.parseGeoGebraCAS(exp, cell);
		} catch (ParseException e) {
			Log.debug(exp);
			c = new CASException(e);
			c.setKey("InvalidInput");
			throw c;
		} catch (BracketsError e) {
			c = new CASException(e);
			c.setKey("UnbalancedBrackets");
			throw c;
		}
	}

	@Override
	public ValidExpression parseGeoGebraCASInputAndResolveDummyVars(
			final String inValue, Kernel kernel, GeoSymbolicI cell)
			throws CASException {
		if (inValue == null || inValue.length() == 0) {
			return null;
		}

		try {
			// parse input into valid expression
			ValidExpression ve = parseGeoGebraCASInput(inValue, cell);

			// resolve Variable objects in ValidExpression as GeoDummy objects
			ExpressionValue ev = resolveVariablesForCAS(ve, kernel);

			if (ev instanceof ValidExpression) {
				((ValidExpression) ev).setLabel(ve.getLabel());
				ve = (ValidExpression) ev;

			}

			// resolve Equations as Functions if lhs is y
			if (ve instanceof Function) {
				ve.traverse(Traversing.FunctionCreator.getCreator());
			}

			return ve;
			// }catch (MaximaVersionUnsupportedExecption e) {
			// throw e; // propagate exception
		} catch (CASException ce) {
			throw ce;
		} catch (Throwable e) {
			throw new CASException(e);
		}

	}

	/**
	 * Resolves all variables in ValidExpression. Unknown variables are kept as
	 * symbolic variables. TODO check that we need default template here
	 */
	@Override
	public synchronized ExpressionValue resolveVariablesForCAS(
			ExpressionValue ev, Kernel kernel) {

		// add local variables to kernel,
		// e.g. f(a,b) := 3*a+c*b has local variables a, b
		boolean isFunction = ev instanceof Function;
		FunctionVariable[] funVars = null;
		if (isFunction) {
			Construction cmdCons = kernel.getConstruction();
			funVars = ((Function) ev).getFunctionVariables();
			for (FunctionVariable funVar : funVars) {
				GeoElement localVarGeo = new GeoDummyVariable(cmdCons,
						funVar.toString(StringTemplate.defaultTemplate));
				cmdCons.addLocalVariable(
						funVar.toString(StringTemplate.defaultTemplate),
						localVarGeo);
			}
		}
		// resolve variables of valid expression
		ev.resolveVariables(
				new EvalInfo(false).withSymbolicMode(SymbolicMode.SYMBOLIC));

		Set<String> nonFunctions = new TreeSet<>();
		NonFunctionCollector c = NonFunctionCollector
				.getCollector(nonFunctions);
		NonFunctionReplacer r = NonFunctionReplacer.getCollector(nonFunctions);
		ev.traverse(c);
		ExpressionValue ret = ev.traverse(r);
		// remove local variables
		if (isFunction) {
			Construction cmdCons = kernel.getConstruction();
			for (FunctionVariable funVar : funVars) {
				cmdCons.removeLocalVariable(
						funVar.toString(StringTemplate.defaultTemplate));
			}
		}
		return ret;
	}

	/**
	 * Tries to convert parsed CAS output to GeoGebra syntax.
	 * 
	 * @param ev
	 *            parsed CAS output
	 * @param tpl
	 *            string template
	 * @return GeoGebra string representation of
	 * @throws CASException
	 *             in case the conversion failed
	 */
	public String toGeoGebraString(ExpressionValue ev, StringTemplate tpl)
			throws CASException {
		try {
			return toString(ev, tpl);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new CASException(e);
		}
	}

	/**
	 * Tries to convert the given CAS string to the given syntax.
	 * 
	 * @param ev
	 *            parsed CAS output
	 * @param tpl
	 *            template to be used
	 * @return string representation of ev in given syntax
	 */
	public String toString(ExpressionValue ev, StringTemplate tpl) {
		ExpressionNode expr;
		if (!ev.isExpressionNode()) {
			expr = ev.wrap();
		} else {
			expr = (ExpressionNode) ev;
		}
		String casString = expr.getCASstring(tpl, true);
		if (casString.startsWith("?")) {
			return "?";
		}
		return casString;
	}

	/**
	 * Tries to convert the given Giac string to GeoGebra syntax.
	 * 
	 * @param exp
	 *            MPReduce output
	 * @return parsed expression
	 * @throws CASException
	 *             if parsing goes wrong
	 */
	public ValidExpression parseGiac(String exp) throws CASException {
		try {
			return parser.parseGiac(exp);
		} catch (Throwable t) {
			throw new CASException(t);
		}
	}

	/**
	 * Final automata can be in three states * NORMAL -- no index being read *
	 * UNDERSCORE -- last character was _ * LONG_INDEX -- it found _{, but not
	 * yet }
	 *
	 */
	private enum FA {
		NORMAL, UNDERSCORE, LONG_INDEX
	}

	/**
	 * Converts all index characters ('_', '{', '}') in the given String to
	 * "unicode" + charactercode + DELIMITER Strings. This is needed so that
	 * labels like a_{12} are preserved
	 * 
	 * @param str
	 *            input string with _,{,}
	 * @param replaceUnicode
	 *            whether unicode characters need to be encoded
	 * @return string where _,{,} are replaced
	 */
	public synchronized String replaceIndices(String str,
			boolean replaceUnicode) {
		int len = str.length();
		StringBuilder replaceIndices = new StringBuilder();

		FA state = FA.NORMAL;
		// convert every single character and append it to sb
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);

			switch (state) {
			case NORMAL: // start index
				if (c == '_') {
					if (i > 0 && str.charAt(i - 1) == '\\') {
						// \\_ is translated to _
						replaceIndices
								.deleteCharAt(replaceIndices.length() - 1);
						replaceIndices.append('_');
					} else {
						state = FA.UNDERSCORE;
						appendcode(replaceIndices, '_');
					}
				} else if (c == Unicode.EULER_CHAR) {
					replaceIndices.append('e');
				} else if (replaceUnicode && c > 127
						&& c != Unicode.MEASURED_ANGLE) {
					appendcode(replaceIndices, c);

					// ' replaced in StringTemplate.addTempVariablePrefix() so
					// that x', y' work #3607
					// } else if (c == '\'') {
					// appendcode(replaceIndices, c);
				} else {
					replaceIndices.append(c);
				}
				break;

			case UNDERSCORE:
				if (c == '{') {
					state = FA.LONG_INDEX;
				} else {
					state = FA.NORMAL;
				}
				appendcode(replaceIndices, c);
				break;

			case LONG_INDEX:
				if (c == '}') {
					state = FA.NORMAL;
				}
				appendcode(replaceIndices, c);
				break;
			}
		}

		// Log.debug(insertSpecialChars(replaceIndices.toString())+"
		// "+replaceIndices.toString());

		return replaceIndices.toString();
	}

	private static void appendcode(StringBuilder replaceIndices, int code) {
		replaceIndices.append(ExpressionNodeConstants.UNICODE_PREFIX);
		replaceIndices.append(code);
		replaceIndices.append(ExpressionNodeConstants.UNICODE_DELIMITER);

	}

	/**
	 * Reverse operation of removeSpecialChars().
	 * 
	 * @param str
	 *            input string
	 * @return input string with 'replaced by !' etc.
	 */
	// see ExpressionNode#operationToString() for XCOORD, YCOORD
	public String insertSpecialChars(String str) {
		int prefixLen = ExpressionNodeConstants.UNICODE_PREFIX.length();

		if (str.length() < prefixLen) {
			return str;
		}

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
					while (j < len
							&& StringUtil.isDigit(digit = str.charAt(j))) {
						code = 10 * code + (digit - 48);
						j++;
					}

					if (code > 0 && code < 65536) { // valid unicode
						insertSpecial.append((char) code);
						i = j;
					} else { // invalid
						insertSpecial
								.append(ExpressionNodeConstants.UNICODE_PREFIX);
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
	 * @return number of variables in Solve command before completion
	 */
	public int getNrOfVars() {
		return nrOfVars;
	}

	/**
	 * @param nrOfVars
	 *            - number of variables in Solve command before completion
	 */
	public void setNrOfVars(int nrOfVars) {
		this.nrOfVars = nrOfVars;
	}

	/**
	 * Translates a given expression in the format expected by the cas.
	 * 
	 * @param ve
	 *            the Expression to be translated
	 * @param casStringType
	 *            one of StringType.{MAXIMA, MPREDUCE, MATH_PIPER}
	 * @param cas
	 *            CAS interface
	 * @return the translated String.
	 */
	public String translateToCAS(ValidExpression ve,
			StringTemplate casStringType, CASGenericInterface cas) {

			String body = ve.wrap().getCASstring(casStringType, false);

			return body;

	}

	// syntax to the internal CAS
	// syntax.

	/**
	 * Returns the CAS command for the currently set CAS using the given key.
	 * For example, getCASCommand"Expand.0" returns "ExpandBrackets( %0 )" when
	 * Giac is the currently used CAS.
	 * 
	 * @param command
	 *            The command to be translated (should end in ".n", where n is
	 *            the number of arguments to this command).
	 * @return The command in CAS format, where parameter n is written as %n.
	 * 
	 */
	@Override
	public String getTranslatedCASCommand(final String command) {
		return getTranslationRessourceBundle().get(command);
	}

	/**
	 * Returns whether the CAS command key is available, e.g. "Expand.1"
	 * 
	 * @param commandKey
	 *            command name suffixed by . and number of arguments, e.g.
	 *            Derivative.2, Sum.N
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
	synchronized Map<String, String> getTranslationRessourceBundle() {
		if (rbCasTranslations == null) {

			rbCasTranslations = Ggb2giac
					.getMap(parser.getKernel().getApplication());
		}
		return rbCasTranslations;
	}

}
