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

package org.geogebra.common.kernel;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoSymbolicI;

/**
 * Interface for CAS parser
 */
public interface CASParserInterface {
	/**
	 * Parses the given expression and resolves variables as GeoDummy objects.
	 * The result is returned as a ValidExpression.
	 * 
	 * @param inValue
	 *            GeoGebraCAS input
	 * @param kernel
	 *            kernel
	 * @param cell
	 *            CAS cell we parse this for
	 * @return parsed expression
	 * @throws CASException
	 *             if something goes wrong (invalid input)
	 */
	ValidExpression parseGeoGebraCASInputAndResolveDummyVars(String inValue,
			Kernel kernel, GeoSymbolicI cell) throws CASException;

	/**
	 * Parses the given expression and returns it as a ValidExpression.
	 * 
	 * @param inValue
	 *            GeoGebraCAS input
	 * @param cell
	 *            CAS cell we parse this for
	 * @return parsed expression
	 * @throws CASException
	 *             something goes wrong
	 */
	ValidExpression parseGeoGebraCASInput(String inValue,
			GeoSymbolicI cell) throws CASException;

	/**
	 * Replace variables with dummy objects
	 * 
	 * @param outputVe
	 *            value to process
	 * @param kernel
	 *            kernel
	 * @return resolved expression
	 */
	ExpressionValue resolveVariablesForCAS(ExpressionValue outputVe,
			Kernel kernel);

	/**
	 * @param string
	 *            internal name of command
	 * @return localized name of command
	 */
	String getTranslatedCASCommand(String string);

}
