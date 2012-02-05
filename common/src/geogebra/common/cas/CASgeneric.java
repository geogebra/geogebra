package geogebra.common.cas;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.cas.AsynchronousCommand;
import geogebra.common.kernel.cas.CASGenericInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.CASSettings;
import geogebra.common.main.settings.SettingListener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class CASgeneric implements CASGenericInterface,
		SettingListener {

	/**
	 * Timeout for CAS in milliseconds.
	 */
	private long timeoutMillis = 5000;

	protected CASparser casParser;
	private Map<String,String> rbCasTranslations; // translates from GeogebraCAS
												// syntax to the internal CAS
												// syntax.

	public CASgeneric(CASparser casParser) {
		this.casParser = casParser;
		
	}

	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra
	 * notation.
	 * 
	 * @param casInput
	 *            in GeoGebraCAS syntax
	 * @return evaluation result
	 * @throws CASException
	 */
	public abstract String evaluateGeoGebraCAS(ValidExpression casInput)
			throws CASException;

	/**
	 * Evaluates an expression in the syntax of the currently active CAS
	 * (MathPiper or Maxima).
	 * 
	 * @param exp
	 *            The expression to be evaluated.
	 * @return result string (null possible)
	 * @throws Throwable
	 */
	public abstract String evaluateRaw(String exp) throws Throwable;

	

	// /**
	// * Returns whether var is a defined variable.
	// * @param var the Variable
	// * @return true if the variable is defined, false otherwise.
	// */
	// public abstract boolean isVariableBound(String var);

	/**
	 * Unbinds (deletes) variable.
	 * 
	 * @param var
	 *            the name of the variable.
	 */
	public abstract void unbindVariable(String var);

	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 */
	public abstract void reset();

	/**
	 * Returns the CAS command for the currently set CAS using the given key.
	 * For example, getCASCommand"Expand.0" returns "ExpandBrackets( %0 )" when
	 * MathPiper is the currently used CAS.
	 * 
	 * @param command
	 *            The command to be translated (should end in ".n", where n is
	 *            the number of arguments to this command).
	 * @return The command in CAS format, where parameter n is written as %n.
	 * 
	 */
	public String getTranslatedCASCommand(String command) {
		return getTranslationRessourceBundle().get(command);
	}

	/**
	 * Returns whether the CAS command key is available, e.g. "Expand.1"
	 */
	final public boolean isCommandAvailable(String commandKey) {
		return getTranslatedCASCommand(commandKey) != null;
	}

	/**
	 * Returns the RessourceBundle that translates from GeogebraCAS commands to
	 * their definition in the syntax of the current CAS. Loads this bundle if
	 * it wasn't loaded yet.
	 * 
	 * @return The current ResourceBundle used for translations.
	 * 
	 */
	private synchronized Map<String,String> getTranslationRessourceBundle() {
		if (rbCasTranslations == null)
			rbCasTranslations = initTranslationMap();
		return rbCasTranslations;
	}
	
	public abstract Map<String,String> initTranslationMap();

	public final String toAssignment(GeoElement ge,StringTemplate tpl) {
		String body = ge.getCASString(tpl,false);
		String casLabel = ge.getLabel(tpl);
		if (ge instanceof FunctionalNVar) {
			String params = ((FunctionalNVar) ge).getFunction().getVarString(tpl);
			return translateFunctionDeclaration(casLabel, params, body);
		}
		return translateAssignment(casLabel, body);
	}

	/**
	 * Translates a given expression in the format expected by the cas.
	 * 
	 * @param ve
	 *            the Expression to be translated
	 * @param casStringType
	 *            one of StringType.{MAXIMA, MPREDUCE, MATH_PIPER}
	 * @return the translated String.
	 */
	protected String translateToCAS(ValidExpression ve, StringTemplate casStringType) {
		Kernel kernel = ve.getKernel();

		try {
			ValidExpression tmp = ve;
			if (!ve.isExpressionNode())
				tmp = new ExpressionNode(kernel, ve);

			String body = ((ExpressionNode) tmp).getCASstring(casStringType,
					true);

			// handle assignments
			String label = ve.getLabel();
			if (label != null) { // is an assignment or a function declaration
				// make sure to escape labels to avoid problems with reserved
				// CAS labels
				label = kernel.printVariableName(casStringType.getStringType(), label);
				if (ve instanceof FunctionNVar) {
					FunctionNVar fun = (FunctionNVar) ve;
					return translateFunctionDeclaration(label,
							fun.getVarString(casStringType), body);
				}
				return translateAssignment(label, body);
			}
			return body;
		} finally {
			//do nothing
		}
	}

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
	public String translateAssignment(String label, String body) {
		// default implementation works for MPReduce and MathPiper
		return label + " := " + body;
	}

	/**
	 * @return CAS timeout in seconds
	 */
	protected long getTimeoutMilliseconds() {
		return timeoutMillis;
	}

	public void settingsChanged(AbstractSettings settings) {
		CASSettings s = (CASSettings) settings;
		timeoutMillis = s.getTimeoutMilliseconds();
	}

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
	 * @return String in CAS format.
	 */
	public abstract String translateFunctionDeclaration(String label,
			String parameters, String body);

	/**
	 * Sets the number of signficiant figures (digits) that should be used as
	 * print precision for the output of Numeric[] commands.
	 * 
	 * @param significantNumbers
	 */
	public abstract void setSignificantFiguresForNumeric(int significantNumbers);

	/**
	 * Returns the internal names of all the commands available in the current
	 * CAS.
	 * 
	 * @return A Set of all internal CAS commands.
	 */
	public Set<String> getAvailableCommandNames() {
		Set<String> cmdSet = new HashSet<String>();
		for (String signature: getTranslationRessourceBundle().keySet()) {
			String cmd = signature.substring(0, signature.indexOf('.'));
			cmdSet.add(cmd);
		}
		return cmdSet;
	}

	public void evaluateGeoGebraCASAsync(String input,
			boolean useCaching, AsynchronousCommand c, int id, boolean oldDigits) {
		AbstractApplication.debug("Only MPReduce supports async calls");
		
	}
	
	public void CASAsyncFinished(ValidExpression exp,String result2,
			boolean useCaching,
			Throwable exception,AsynchronousCommand c,
			int id,boolean oldDigits,String input){
		Kernel.internationalizeDigits = oldDigits;
		String result=result2;

		// check if keep input command was successful
		// e.g. for KeepInput[Substitute[...]]
		// otherwise return input
		if (exp.isKeepInputUsed()
				&& (exception != null || "?".equals(result))) {
			// return original input
			c.handleCASoutput(exp.toString(), id);
		}

		// pass on exception
		if (exception != null){
			c.handleException(exception,id);
		}

		// success
		if (result2 != null) {
			// get names of escaped global variables right
			// e.g. "ggbcasvar1a" needs to be changed to "a"
			// e.g. "ggbtmpvara" needs to be changed to "a"
			result = casParser.getKernel().removeCASVariablePrefix(result, " ");
		}

		c.handleCASoutput(result,id);
		if(useCaching)
			casParser.getKernel().putToCasCache(input, result);
	}

}
