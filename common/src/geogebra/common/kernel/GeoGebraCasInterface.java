package geogebra.common.kernel;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.main.CasType;

import java.util.ArrayList;
import java.util.Set;
/**
 * Interface for GeoGebraCAS
 */
public interface GeoGebraCasInterface {
	/**
	 * Evaluates an expression in the syntax of the currently active CAS
	 * @param str raw input in current CAS format
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
	 * @return whether the two input expressions are structurally equal
	 */
	public boolean isStructurallyEqual(ValidExpression inputVE, String input);
	/**
	 * Sets the currently used CAS for evaluateGeoGebraCAS().
	 * 
	 * @param c
	 *            use CAS_MPREDUCE or CAS_GIAC
	 */
	public void setCurrentCAS(final CasType c);
	/**
	 * Returns whether the given command is available in the underlying CAS.
	 * 
	 * @param cmd
	 *            command with name and number of arguments
	 * @return whether command is available
	 */
	
	public boolean isCommandAvailable(final Command cmd);
	/**
	 * Expands the given MPreduce expression and tries to get its polynomial
	 * coefficients. The coefficients are returned in ascending order. If exp is
	 * not a polynomial, null is returned.
	 * 
	 * example: getPolynomialCoeffs("3*a*x^2 + b"); returns ["b", "0", "3*a"]
	 * @param exp expression
	 * @param variable variable
	 * @return list of strings representing the coefficients
	 */
	public String[] getPolynomialCoeffs(final String exp, final String variable);
	/**
	 * Evaluates an expression in GeoGebraCAS syntax.
	 * 
	 * @param exp
	 *            expression to be evaluated
	 * @param arbConst arbitrary constant handler
	 * @param tpl string template
	 * @return result string in GeoGebra syntax (null possible)
	 * @throws CASException
	 *             if there is a timeout or the expression cannot be evaluated
	 */
	public String evaluateGeoGebraCAS(String exp, MyArbitraryConstant arbConst, StringTemplate tpl)
			throws CASException;
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
	 * @return evaluation result
	 * @throws CASException
	 *             if there is a timeout or the expression cannot be evaluated
	 */
	public String evaluateGeoGebraCAS(ValidExpression exp,
			MyArbitraryConstant arbConst, StringTemplate tpl) throws CASException;
	/**
	 * Returns the CAS command for the currently set CAS using the given key and
	 * command arguments. For example, getCASCommand("Expand.1", {"3*(a+b)"})
	 * returns "ExpandBrackets( 3*(a+b) )" when MathPiper is the currently used
	 * CAS.
	 * @param name command name
	 * @param args arguments
	 * @param symbolic true for symbolic arguments
	 * @param tpl string tmplate for result
	 * @return command formated for current CAS
	 */
	public String getCASCommand(final String name,
			final ArrayList<ExpressionNode> args, final boolean symbolic,
			StringTemplate tpl);

	/**
	 * @param c Asynchronous command
	 */
	public void evaluateGeoGebraCASAsync(final AsynchronousCommand c);

	//String toAssignment(final GeoElement geoElement, final StringTemplate tpl);
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

}
