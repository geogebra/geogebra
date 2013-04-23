package geogebra.common.kernel;

import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.main.settings.SettingListener;

import java.util.Collection;

/**
 * Generic interface for language-specific part of CAS which is plugged into
 * GoGebraCAS.
 */
public interface CASGenericInterface extends SettingListener {

	/**
	 * Translates a variable/constant assignment like "x := 3" into the format
	 * expected by the CAS. Function-Assignments have to be translated using @see
	 * translateFunctionDeclaration().
	 * 
	 * @param label
	 *            the label of the assignment, e.g. x
	 * @param body
	 *            the value that will be assigned to the label, e.g. "3"
	 * @return String in CAS format.
	 */
	String translateAssignment(final String label, final String body);

	/**
	 * Initializes CAS. Only needed in Web where we must download it on demand.
	 */
	public void initCAS();

	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra
	 * notation.
	 * 
	 * @param casInput
	 *            in GeoGebraCAS syntax
	 * @param arbconst
	 *            arbitrary constant handler
	 * @param tpl
	 *            string template
	 * @return evaluation result
	 * @throws CASException
	 *             if evaluation fails
	 */
	public abstract String evaluateGeoGebraCAS(ValidExpression casInput,
			MyArbitraryConstant arbconst, StringTemplate tpl)
			throws CASException;

	/**
	 * Evaluates an expression in the syntax of the currently active CAS
	 * (MathPiper or Maxima).
	 * 
	 * @param exp
	 *            The expression to be evaluated.
	 * @return result string (null possible)
	 * @throws Throwable
	 *             if evaluation fails
	 */
	public abstract String evaluateRaw(final String exp) throws Throwable;

	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 */
	public abstract void reset();

	/**
	 * Call CAS asynchronously
	 * 
	 * @param c
	 *            command that should receive the result
	 */
	public void evaluateGeoGebraCASAsync(final AsynchronousCommand c);

	/**
	 * Appends list start marker to the builder (eg {)
	 * @param sbCASCommand string builder
	 */
	void appendListStart(StringBuilder sbCASCommand);
	/**
	 * Appends list start marker to the builder (eg })
	 * @param sbCASCommand string builder
	 */
	void appendListEnd(StringBuilder sbCASCommand);
	
	/**
	 * 
	 * @param inputExpression input
	 * @param arbconst constant handler
	 * @return evaluated input
	 */
	public ExpressionValue evaluateToExpression(
			final ValidExpression inputExpression, MyArbitraryConstant arbconst);

	/**
	 * Load all packages for  given command
	 * @param string command signature (Command.NumberOfArguments)
	 */
	void loadPackagesFor(String string);

	/**
	 * Load packages for Groebner computations
	 */
	void loadGroebner();

	/**
	 * @param exp input
	 * @return output
	 */
	String evaluateCAS(String exp);
	
	/**
	 * @param restrictions list of equations
	 * @param constructRestrictions construct restrictions (hypotheses)
	 * @param vars existing variables
	 * @param varsToEliminate variables to be eliminated
	 * @return locus equation
	 */
	String createLocusEquationScript(
			Collection<StringBuilder> restrictions, 
			String constructRestrictions,
			String vars, String varsToEliminate);

}
