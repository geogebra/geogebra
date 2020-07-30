package org.geogebra.common.kernel;

import java.util.ArrayList;
import java.util.Set;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoSymbolicI;

/**
 * Interface for GeoGebraCAS
 */
public interface GeoGebraCasInterface {
	/**
	 * Evaluates an expression in the syntax of the currently active CAS
	 * 
	 * @param str
	 *            raw input in current CAS format
	 * 
	 * @return str string (null possible)
	 * @throws Throwable
	 *             if there is a timeout or the expression cannot be evaluated
	 */
	public String evaluateRaw(String str) throws Throwable;

	/**
	 * @return current CAS instance
	 */
	public CASGenericInterface getCurrentCAS();

	/**
	 * @return CAS parser
	 */
	public CASParserInterface getCASparser();

	/**
	 * Returns true if the two input expressions are structurally equal. For
	 * example "2 + 2/3" is structurally equal to "2 + (2/3)" but unequal to
	 * "(2 + 2)/3"
	 * 
	 * @param inputVE
	 *            includes internal command names
	 * @param input
	 *            includes localized command names
	 * @param kernel
	 *            kernel
	 * @return whether the two input expressions are structurally equal
	 */
	public boolean isStructurallyEqual(ValidExpression inputVE, String input,
			Kernel kernel);

	/**
	 * Sets the currently used CAS for evaluateGeoGebraCAS().
	 * 
	 */
	public void setCurrentCAS();

	/**
	 * Returns whether the given command is available in the underlying CAS.
	 * 
	 * @param cmd
	 *            command with name and number of arguments
	 * @return whether command is available
	 */

	public boolean isCommandAvailable(final Command cmd);

	/**
	 * Expands the given Giac expression and tries to get its polynomial
	 * coefficients. The coefficients are returned in ascending order. If exp is
	 * not a polynomial, null is returned.
	 * 
	 * example: getPolynomialCoeffs("3*a*x^2 + b"); returns ["b", "0", "3*a"]
	 * 
	 * @param exp
	 *            expression
	 * @param variable
	 *            variable
	 * @return list of strings representing the coefficients
	 */
	public String[] getPolynomialCoeffs(final String exp,
			final String variable);

	/**
	 * Evaluates an expression in GeoGebraCAS syntax.
	 * 
	 * @param exp
	 *            expression to be evaluated
	 * @param arbConst
	 *            arbitrary constant handler
	 * @param tpl
	 *            string template
	 * @param kernel
	 *            kernel
	 * @return result string in GeoGebra syntax (null possible)
	 * @throws CASException
	 *             if there is a timeout or the expression cannot be evaluated
	 *             Note: all other throwables are caught inside and converted to
	 *             CASException
	 */
	public String evaluateGeoGebraCAS(String exp, MyArbitraryConstant arbConst,
			StringTemplate tpl, Kernel kernel) throws CASException;

	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra
	 * notation.
	 * 
	 * @param exp
	 *            Input in GeoGebraCAS syntax
	 * @param arbConst
	 *            Arbitrary constant handler
	 * @param tpl
	 *            String template for result
	 * @param cell
	 *            CAS cell
	 * @param kernel
	 *            kernel
	 * @return evaluation result
	 * @throws CASException
	 *             if there is a timeout or the expression cannot be evaluated
	 */
	public String evaluateGeoGebraCAS(ValidExpression exp,
			MyArbitraryConstant arbConst, StringTemplate tpl, GeoCasCell cell,
			Kernel kernel) throws CASException;

	/**
	 * Returns the CAS command for the currently set CAS using the given key and
	 * command arguments. For example, getCASCommand("Expand.1", {"3*(a+b)"})
	 * returns "expand( 3*(a+b) )" when Giac is the currently used CAS.
	 * 
	 * @param name
	 *            command name
	 * @param args
	 *            arguments
	 * @param symbolic
	 *            true for symbolic arguments
	 * @param tpl
	 *            string tmplate for result
	 * @param mode
	 *            symbolic mode
	 * @return command formated for current CAS
	 */
	public String getCASCommand(final String name,
			final ArrayList<ExpressionNode> args, final boolean symbolic,
			StringTemplate tpl, SymbolicMode mode);

	/**
	 * Returns the internal names of all the commands available in the current
	 * CAS.
	 * 
	 * @return A Set of all internal CAS commands.
	 */
	public Set<String> getAvailableCommandNames();

	/**
	 * Clear cache of this CAS (not the local caches in functions etc.)
	 */
	public void clearCache();

	/**
	 * @param string
	 *            signature, eg Midpoint.2
	 * @return command for particular CAS
	 */
	public String translateCommandSignature(String string);

	/**
	 * Make sure CAS is initialized
	 */
	public void initCurrentCAS();

	/**
	 * @param inValue
	 *            output in GeoGebra syntax
	 * @param geoCasCell
	 *            CAS / AV cell
	 * @param kernel
	 *            kernel
	 * @return parsed expression
	 */
	public ValidExpression parseOutput(String inValue, GeoSymbolicI geoCasCell,
			Kernel kernel);

	/**
	 * If a cas exists, clear the results, otherwise do not initialize it
	 */
	void clearResult();
}
