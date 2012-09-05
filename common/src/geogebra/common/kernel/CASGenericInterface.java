package geogebra.common.kernel;

import geogebra.common.kernel.arithmetic.AssignmentType;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.main.settings.SettingListener;

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
	 * Unbinds (deletes) variable.
	 * 
	 * @param var
	 *            the name of the variable.
	 */
	public abstract void unbindVariable(final String var);

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
	 * Translates a function definition/function assignment like
	 * "f(x, y) = 3*x^2 + y" into the format expected by the CAS.
	 * Function-Assignments have to be translated using @see
	 * translateAssignment().
	 * 
	 * @param label
	 *            the name of the function, e.g. f
	 * @param parameters
	 *            the parameters of the function, separated by commas, e.g.
	 *            "x, y"
	 * @param body
	 *            the body of the function.
	 * @param type the assignment type
	 * @return String in CAS format.
	 */
	public abstract String translateFunctionDeclaration(final String label,
			final String[] parameters, final String body, AssignmentType type);

	/**
	 * Sets the number of signficiant figures (digits) that should be used as
	 * print precision for the output of Numeric[] commands.
	 * 
	 * @param significantNumbers
	 *            number of significant digits (-1 to use default)
	 */
	public abstract void setSignificantFiguresForNumeric(final int significantNumbers);
}
