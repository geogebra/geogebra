package geogebra.common.cas;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.cas.AsynchronousCommand;
import geogebra.common.kernel.cas.CASGenericInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.CASSettings;
import geogebra.common.main.settings.SettingListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is responsible for dispatching  CAS commands, parsing and processing results
 */
public abstract class CASgeneric implements CASGenericInterface,
		SettingListener {

	/**
	 * Timeout for CAS in milliseconds.
	 */
	private long timeoutMillis = 5000;
	/** CAS result parser */
	public CASparser casParser;
	private Map<String,String> rbCasTranslations; // translates from GeogebraCAS
												// syntax to the internal CAS
												// syntax.

	/**
	 * Creates new CAS
	 * @param casParser parser of CAS results
	 */
	public CASgeneric(CASparser casParser) {
		this.casParser = casParser;
		
	}

	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra
	 * notation.
	 * 
	 * @param casInput
	 *            in GeoGebraCAS syntax
	 * @param arbconst arbitrary constant handler
	 * @return evaluation result
	 * @throws CASException if evaluation fails
	 */
	public abstract String evaluateGeoGebraCAS(ValidExpression casInput,
			MyArbitraryConstant arbconst)
			throws CASException;

	/**
	 * Evaluates an expression in the syntax of the currently active CAS
	 * (MathPiper or Maxima).
	 * 
	 * @param exp
	 *            The expression to be evaluated.
	 * @return result string (null possible)
	 * @throws Throwable if evaluation fails
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
	 * @param commandKey command name suffixed by . and number of arguments, e.g. Derivative.2, Sum.N
	 * @return true if available
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
	
	/**
	 * @return map from GGB CAS syntax to syntax of specific CAS, parameters are represented as %0, %1, ...
	 */
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
			ExpressionNode tmp = null;
			if (!ve.isExpressionNode())
				tmp = new ExpressionNode(kernel, ve);
			else tmp = ((ExpressionNode)ve);
			String body = tmp.getCASstring(casStringType,
					true);
			ArrayList<GeoFunction> derivativeFunctions= new ArrayList<GeoFunction>();
			ArrayList<Integer> derivativeDegrees= new ArrayList<Integer>();
			tmp.collectDerivatives(derivativeFunctions,derivativeDegrees);
			StringTemplate casTpl = StringTemplate.casTemplate;
			for(int i=0;i<derivativeDegrees.size();i++){
				GeoFunction f = derivativeFunctions.get(i);
				StringBuilder sb = new StringBuilder(80);
				sb.append(f.getLabel(casTpl));
				int deg = derivativeDegrees.get(i);
				for(int j=0;j<deg;j++)
					sb.append("'");
				sb.append("(");
				sb.append(f.getVarString(casTpl));
				sb.append("):=df(");
				sb.append(f.getAssignmentLHS(casTpl));
				sb.append(",");
				sb.append(f.getVarString(casTpl));
				sb.append(",");
				sb.append(deg);
				sb.append(")");
				try{
					this.evaluateRaw(sb.toString());
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
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
	 * @param significantNumbers number of significant digits (-1 to use default)
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

	/**
	 * Call CAS asynchronously
	 * @param c command that should receive the result
	 */
	public void evaluateGeoGebraCASAsync(AsynchronousCommand c) {
		AbstractApplication.debug("Only MPReduce supports async calls");
		
	}
	
	/**
	 * This method is called when asynchronous CAS call is finished.
	 * It tells the calling algo to update itself and adds the result to cache if suitable.
	 * @param exp parsed CAS output
	 * @param result2 output as string (for cacheing)
	 * @param exception exception which stopped the computation (null if there wasn't one)
	 * @param c command that called the CAS asynchronously
	 * @param input input string (for cacheing)
	 */
	public void CASAsyncFinished(ValidExpression exp,String result2,
			Throwable exception,AsynchronousCommand c,
			String input){
		String result=result2;
		// pass on exception
		if (exception != null){
			c.handleException(exception,input.hashCode());
			return;
		}
		// check if keep input command was successful
		// e.g. for KeepInput[Substitute[...]]
		// otherwise return input
		if (exp.isKeepInputUsed()
				&& ("?".equals(result))) {
			// return original input
			c.handleCASoutput(exp.toString(StringTemplate.maxPrecision), input.hashCode());
		}

		

		// success
		if (result2 != null) {
			// get names of escaped global variables right
			// e.g. "ggbcasvar1a" needs to be changed to "a"
			// e.g. "ggbtmpvara" needs to be changed to "a"
			result = exp.getKernel().removeCASVariablePrefix(result, " ");
		}

		c.handleCASoutput(result,input.hashCode());
		if(c.useCacheing())
			exp.getKernel().putToCasCache(input, result);
	}

}
