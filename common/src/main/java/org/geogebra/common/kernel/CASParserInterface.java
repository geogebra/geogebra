package org.geogebra.common.kernel;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoCasCell;

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
	 * @return parsed expression
	 * @throws CASException
	 *             if something goes wrong (invalid input)
	 */
	ValidExpression parseGeoGebraCASInputAndResolveDummyVars(String inValue,
			Kernel kernel, GeoCasCell cell) throws CASException;

	/**
	 * Parses the given expression and returns it as a ValidExpression.
	 * 
	 * @param inValue
	 *            GeoGebraCAS input
	 * @return parsed expression
	 * @throws CASException
	 *             something goes wrong
	 */
	ValidExpression parseGeoGebraCASInput(final String inValue, GeoCasCell cell)
			throws CASException;

	/**
	 * Replace variables with dummy objects
	 * 
	 * @param outputVe
	 *            value to process
	 * @return
	 */
	ExpressionValue resolveVariablesForCAS(ExpressionValue outputVe,
			Kernel kernel);

	/**
	 * @param string
	 *            internal name of command
	 * @return localized name of command
	 */
	String getTranslatedCASCommand(final String string);

}
