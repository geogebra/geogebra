/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	/**
	 * Parse expression, but use fixed precision for numbers (do not store all digits as string).
	 * @param str input
	 * @return parsed input
	 * @throws ParseException if input uses invalid syntax
	 */
	ValidExpression parseGeoGebraExpressionLowPrecision(String str) throws ParseException;
}
