package org.geogebra.common.kernel.parser;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.ValidExpression;

/**
 * Interface for parser
 *
 */
public interface ParserInterface {
	/**
	 * @param parseString
	 *            string to parse
	 * @return expression
	 * @throws ParseException
	 *             if parsing fails
	 */
	ExpressionNode parseExpression(String parseString) throws ParseException;

	/**
	 * @param string
	 *            string to parse
	 * @return function
	 * @throws ParseException
	 *             if parsing fails
	 */
	Function parseFunction(String string) throws ParseException;

	/**
	 * @param string
	 *            string to parse
	 * @return multivariate function
	 * @throws ParseException
	 *             if parsing fails
	 */
	FunctionNVar parseFunctionNVar(String string) throws ParseException;

	/**
	 * @param str
	 *            string to parse
	 * @return expression
	 * @throws ParseException
	 *             if parsing fails
	 */
	ValidExpression parseGeoGebraExpression(String str) throws ParseException;

	/**
	 * @param str
	 *            string to parse
	 * @return expression
	 */
	ValidExpression parseInputBoxExpression(String str) throws ParseException;

	/**
	 * @param label
	 *            potential label
	 * @return valid label
	 * @throws ParseException
	 *             if parsing fails
	 */
	String parseLabel(String label) throws ParseException;

	ValidExpression parseGeoGebraExpressionLowPrecision(String str) throws ParseException;
}
