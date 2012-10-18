package geogebra.common.kernel;

import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.ValidExpression;

/**
 * Interface for CAS parser
 */
public interface CASParserInterface {
	/**
	 * Parses the given expression and resolves variables as GeoDummy objects.
	 * The result is returned as a ValidExpression.
	 * @param inValue GeoGebraCAS input
	 * @return parsed expression
	 * @throws CASException if something goes wrong (invalid input)
	 */
	ValidExpression parseGeoGebraCASInputAndResolveDummyVars(String inValue) throws CASException;
	/**
	 * Parses the given expression and returns it as a ValidExpression.
	 * @param inValue GeoGebraCAS input
	 * @return parsed expression
	 * @throws CASException something goes wrong
	 */
	ValidExpression parseGeoGebraCASInput(final String inValue) throws CASException;

	/**
	 * Replace variables with dummy objects
	 * @param outputVe value to process
	 */
	void resolveVariablesForCAS(ExpressionValue outputVe);

	/**
	 * @param string internal name of command
	 * @return localized name of command
	 */
	String getTranslatedCASCommand(final String string);

}
