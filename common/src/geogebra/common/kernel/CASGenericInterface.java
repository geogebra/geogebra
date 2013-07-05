package geogebra.common.kernel;

import geogebra.common.cas.GeoGebraCAS;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.prover.polynomial.Variable;
import geogebra.common.main.settings.SettingListener;

import java.util.HashMap;

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
	 * @param exp input
	 * @return output
	 */
	String evaluateCAS(String exp);
	
	/**
	 * @param constructRestrictions construct restrictions (hypotheses)
	 * @param vars existing variables
	 * @param varsToEliminate variables to be eliminated
	 * @return locus equation
	 */
	String createLocusEquationScript(
			String constructRestrictions,
			String vars, String varsToEliminate);

	/**
	 * Creates a program to check if an equation system has no solution, using
	 * Groebner basis w.r.t. the revgradlex order.
	 * @param ringVar internal variable name for the ring if the CAS engine needs it
	 * @param idealVar internal variable name for the ideal if the CAS engine needs it
	 * @param dummyVar dummy variable name
	 * @param substitutions e.g [v1=0,v2=1]
	 * @param polys polynomials, e.g. "v1+v2-3*v4-10"
	 * @param freeVars free variables
	 * @param dependantVars dependent variables
	 * @param polysofractf use polynomials of rational functions 
	 * @return the program code
	 */
	public String createGroebnerSolvableScript( 
			HashMap<Variable,Integer>substitutions, String polys,
			String freeVars, String dependantVars, boolean polysofractf);
		
	/**
	 * @param rawResult output from eliminate() and coeffs() commands
	 * @param cas the currently used CAS
	 * @return 2D array of coefficients
	 */
	double[][] getBivarPolyCoefficients(String rawResult, GeoGebraCAS cas);

}
