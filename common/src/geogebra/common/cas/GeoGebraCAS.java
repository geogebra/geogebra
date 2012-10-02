package geogebra.common.cas;

import geogebra.common.cas.mpreduce.CASmpreduce;
import geogebra.common.kernel.AsynchronousCommand;
import geogebra.common.kernel.CASException;
import geogebra.common.kernel.CASGenericInterface;
import geogebra.common.kernel.GeoGebraCasInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.VarString;
import geogebra.common.kernel.arithmetic.AssignmentType;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.Traversing.DerivativeCollector;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.CasType;
import geogebra.common.util.MaxSizeHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class provides an interface for GeoGebra to use an underlying computer
 * algebra system like MPReduce, Maxima or MathPiper.
 * 
 * @author Markus Hohenwarter
 */
public class GeoGebraCAS implements GeoGebraCasInterface {

	private App app;
	private CASparser casParser;
	private CASGenericInterface cas;
	private CASmpreduce casMPReduce;
	private CasType currentCAS = CasType.NO_CAS;

	/**
	 * Creates new CAS interface
	 * 
	 * @param kernel
	 *            kernel
	 */
	public GeoGebraCAS(Kernel kernel) {
		app = kernel.getApplication();
		casParser = new CASparser(kernel.getParser(), kernel.getApplication()
				.getParserFunctions());

		// DO NOT init underlying CAS here to avoid hanging animation,
		// see http://www.geogebra.org/trac/ticket/1565
		// getCurrentCAS();
	}

	public CASparser getCASparser() {
		return casParser;
	}

	public CASGenericInterface getCurrentCAS() {
		if (cas == null) {
			app.setWaitCursor();
			initCurrentCAS();
			app.setDefaultCursor();
		}

		return cas;
	}

	/**
	 * Initializes underlying CAS
	 */
	public synchronized void initCurrentCAS() {
		if (cas == null)
			setCurrentCAS(Kernel.DEFAULT_CAS);
	}

	public StringType getCurrentCASstringType() {
		switch (currentCAS) {
		case MAXIMA:
			return StringType.MAXIMA;

		case MPREDUCE:
			return StringType.MPREDUCE;

		default:
		case MATHPIPER:
			return StringType.MATH_PIPER;
		}
	}

	/**
	 * Sets the currently used CAS for evaluateGeoGebraCAS().
	 * 
	 * @param CAS
	 *            use CAS_MATHPIPER or CAS_MAXIMA
	 */
	public synchronized void setCurrentCAS(final CasType CAS) {
		try {
			switch (CAS) {
			/*
			 * case MAXIMA: cas = getMaxima(); ((CASmaxima) cas).initialize();
			 * currentCAS = CAS; break;
			 */

			default:
				cas = getMPReduce();
				app.getSettings().getCasSettings().addListener(cas);
				currentCAS = CAS;
				break;
			/*
			 * case MATHPIPER: cas = getMathPiper(); currentCAS = CAS; break;
			 */
			}
			/*
			 * }catch (MaximaVersionUnsupportedExecption e){
			 * app.showError("CAS.MaximaVersionUnsupported");
			 * setCurrentCAS(CasType.MPREDUCE);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 */
	public void reset() {
		getCurrentCAS().reset();
	}

	/**
	 * Sets the number of signficiant figures (digits) that should be used as
	 * print precision for the output of Numeric[] commands.
	 * 
	 * @param significantNumbers
	 *            significant figures
	 */
	public void setSignificantFiguresForNumeric(final int significantNumbers) {
		getCurrentCAS().setSignificantFiguresForNumeric(significantNumbers);
	}

	/*
	 * private CASmathpiper getMathPiper() { if
	 * (currentCAS.equals(CasType.MATHPIPER)) return (CASmathpiper) cas; else
	 * return new CASmathpiper(casParser, new CasParserToolsImpl('e')); }
	 */

	/*
	 * private CASmaxima getMaxima() { if (currentCAS.equals(CasType.MAXIMA))
	 * return (CASmaxima) cas; else return new CASmaxima(casParser, new
	 * CasParserToolsImpl('b')); }
	 */

	public synchronized CASmpreduce getMPReduce() {
		if (casMPReduce == null)
			casMPReduce = app.getCASFactory()
					.newMPReduce(casParser, new CasParserToolsImpl('e'),
							app.getKernel());
		return casMPReduce;
	}

	// /**
	// * Returns whether var is a defined variable.
	// */
	// public boolean isVariableBound(String var) {
	// return cas.isVariableBound(var);
	// }

	/**
	 * Unbinds (deletes) variable.
	 * 
	 * @param var
	 *            variable to be unbound
	 */
	public void unbindVariable(final String var) {
		getCurrentCAS().unbindVariable(var);
	}

	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra
	 * notation.
	 * 
	 * @param casInput
	 *            Input in GeoGebraCAS syntax
	 * @return evaluation result
	 * @throws CASException
	 *             if there is a timeout or the expression cannot be evaluated
	 */
	public String evaluateGeoGebraCAS(ValidExpression casInput,
			MyArbitraryConstant arbconst, StringTemplate tpl)
			throws CASException {

		String result = null;
		CASException exception = null;
		try {
			DerivativeCollector col = DerivativeCollector.getCollector();
			casInput.traverse(col);
			List<GeoElement> derivativeFunctions= col.getFunctions();
			List<Integer> derivativeDegrees= col.getDegrees();
			StringTemplate casTpl = StringTemplate.defaultTemplate;
			StringBuilder sb = new StringBuilder(100);
			for(int i=0;i<derivativeDegrees.size();i++){
				sb.setLength(0);
				sb.append(derivativeFunctions.get(i).getLabel(casTpl));
				for(int j=0;j<derivativeDegrees.get(i);j++)
					sb.append("'");
				sb.append("(");
				sb.append(((VarString)derivativeFunctions.get(i)).getVarString(casTpl));
				sb.append("):=Derivative[");
				sb.append(derivativeFunctions.get(i).getLabel(casTpl));
				sb.append("(");
				sb.append(((VarString)derivativeFunctions.get(i)).getVarString(casTpl));
				sb.append("),");
				sb.append(((VarString)derivativeFunctions.get(i)).getVarString(casTpl));
				sb.append(",");
				sb.append(derivativeDegrees.get(i));
				sb.append("]");
				evaluateGeoGebraCAS(sb.toString(), arbconst);
			}
			result = getCurrentCAS().evaluateGeoGebraCAS(casInput, arbconst,
					tpl);
		} catch (CASException ce) {
			exception = ce;
		} finally {
			// do nothing
		}

		// check if keep input command was successful
		// e.g. for KeepInput[Substitute[...]]
		// otherwise return input
		if (casInput.isKeepInputUsed()
				&& (exception != null || "?".equals(result))) {
			// return original input
			return casInput.toString(tpl);
		}

		// pass on exception
		if (exception != null)
			throw exception;

		// success
		if (result != null) {
			// get names of escaped global variables right
			// e.g. "ggbcasvar1a" needs to be changed to "a"
			// e.g. "ggbtmpvara" needs to be changed to "a"
			result = app.getKernel().removeCASVariablePrefix(result, " ");
		}

		return result;
	}

	/**
	 * Evaluates an expression in GeoGebraCAS syntax.
	 * 
	 * @param exp
	 *            expression to be evaluated
	 * @return result string in GeoGebra syntax (null possible)
	 * @throws CASException
	 *             if there is a timeout or the expression cannot be evaluated
	 */
	final public String evaluateGeoGebraCAS(String exp,
			MyArbitraryConstant arbconst) throws CASException {
		try {
			ValidExpression inVE = casParser.parseGeoGebraCASInput(exp);
			String ret = evaluateGeoGebraCAS(inVE, arbconst);
			if (ret == null)
				throw new CASException(new Exception(
						app.getError("CAS.GeneralErrorMessage")));
			return ret;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new CASException(t);
		}
	}

	/**
	 * Evaluates an expression in the syntax of the currently active CAS
	 * 
	 * @return result string (null possible)
	 * @throws Throwable
	 *             if there is a timeout or the expression cannot be evaluated
	 */
	final public String evaluateRaw(String exp) throws Throwable {
		return getCurrentCAS().evaluateRaw(exp);
	}

	/**
	 * Evaluates an expression given in MPReduce syntax.
	 * 
	 * @return result string (null possible)
	 * @param exp
	 *            the expression
	 * @throws CASException
	 *             if there is a timeout or the expression cannot be evaluated
	 * */
	final public String evaluateMPReduce(String exp) throws CASException {
		return getMPReduce().evaluateMPReduce(exp);
	}

	// these variables are cached to gain some speed in getPolynomialCoeffs
	private Map<String, String[]> getPolynomialCoeffsCache = new MaxSizeHashMap<String, String[]>(
			Kernel.GEOGEBRA_CAS_CACHE_SIZE);
	private StringBuilder getPolynomialCoeffsSB = new StringBuilder();
	private StringBuilder sbPolyCoeffs = new StringBuilder();

	/**
	 * Expands the given MPreduce expression and tries to get its polynomial
	 * coefficients. The coefficients are returned in ascending order. If exp is
	 * not a polynomial, null is returned.
	 * 
	 * example: getPolynomialCoeffs("3*a*x^2 + b"); returns ["b", "0", "3*a"]
	 */
	final public String[] getPolynomialCoeffs(final String polyExpr,
			final String variable) {
		getPolynomialCoeffsSB.setLength(0);
		getPolynomialCoeffsSB.append(polyExpr);
		getPolynomialCoeffsSB.append(',');
		getPolynomialCoeffsSB.append(variable);

		String[] result = getPolynomialCoeffsCache.get(getPolynomialCoeffsSB
				.toString());
		if (result != null)
			return result;

		sbPolyCoeffs.setLength(0);
		sbPolyCoeffs.append("coeff(");
		sbPolyCoeffs.append(getPolynomialCoeffsSB.toString());
		sbPolyCoeffs.append(')');

		try {
			// expand expression and get coefficients of
			// "3*a*x^2 + b" in form "{ b, 0, 3*a }"
			String tmp = evaluateMPReduce(sbPolyCoeffs.toString());

			// no result
			if ("{}".equals(tmp) || "".equals(tmp) || tmp == null)
				return null;

			// not a polynomial: result still includes the variable, e.g. "x"
			if (tmp.indexOf(variable) >= 0)
				return null;

			// get names of escaped global variables right
			// e.g. "ggbcasvara" needs to be changed to "a"
			tmp = app.getKernel().removeCASVariablePrefix(tmp);

			tmp = tmp.substring(1, tmp.length() - 1); // strip '{' and '}'
			result = tmp.split(",");

			getPolynomialCoeffsCache.put(getPolynomialCoeffsSB.toString(),
					result);
			return result;
		} catch (Throwable e) {
			App.debug("GeoGebraCAS.getPolynomialCoeffs(): " + e.getMessage());
			// e.printStackTrace();
		}

		return null;
	}

	final private static String toString(final ExpressionValue ev,
			final boolean symbolic, StringTemplate tpl) {
		/* previously this method also replaced f by f(x), but FunctionExpander takes care of that now */
		if (symbolic) {
			return ev.toString(tpl);
		}
		return ev.toValueString(tpl);
	}

	/**
	 * Returns the CAS command for the currently set CAS using the given key and
	 * command arguments. For example, getCASCommand("Expand.1", {"3*(a+b)"})
	 * returns "ExpandBrackets( 3*(a+b) )" when MathPiper is the currently used
	 * CAS.
	 */
	final synchronized public String getCASCommand(final String name,
			final ArrayList<ExpressionNode> args, boolean symbolic,
			StringTemplate tpl) {
		StringBuilder sbCASCommand = new StringBuilder(80);

		// build command key as name + ".N"
		sbCASCommand.setLength(0);
		sbCASCommand.append(name);
		sbCASCommand.append(".N");

		String translation = casParser.getTranslatedCASCommand(sbCASCommand
				.toString());

		// check for eg Sum.N=sum(%)
		if (translation != null) {
			translation = translation.replaceAll("%@", app.getKernel()
					.getCasVariablePrefix());
			sbCASCommand.setLength(0);
			for (int i = 0; i < translation.length(); i++) {
				char ch = translation.charAt(i);
				if (ch == '%') {
					if (args.size() == 1) { // might be a list as the argument
						ExpressionValue ev = args.get(0);
						String str = toString(ev, symbolic, tpl);
						if (ev.isListValue()) {
							// is a list, remove { and }
							// resp. list( ... ) in mpreduce
							if (currentCAS.equals(CasType.MPREDUCE))
								sbCASCommand.append(str.substring(22,
										str.length() - 2));
							else
								sbCASCommand.append(str.substring(1,
										str.length() - 1));
						} else {
							// not a list, just append
							sbCASCommand.append(str);
						}
					} else {
						for (int j = 0; j < args.size(); j++) {
							ExpressionValue ev = args.get(j);
							sbCASCommand.append(toString(ev, symbolic, tpl));
							sbCASCommand.append(',');
						}
						// remove last comma
						sbCASCommand.setLength(sbCASCommand.length() - 1);
					}
				} else {
					sbCASCommand.append(ch);
				}

			}

			return sbCASCommand.toString();
		}

		// build command key as name + "." + args.size()
		// remove 'N'
		sbCASCommand.setLength(sbCASCommand.length() - 1);
		// add eg '3'
		sbCASCommand.append(args.size());

		// get translation ggb -> MathPiper/Maxima
		translation = casParser
				.getTranslatedCASCommand(sbCASCommand.toString());
		sbCASCommand.setLength(0);

		// no translation found:
		// use key as function name
		if (translation == null) {

			// convert command names x, y, z to xcoord, ycoord, ycoord to
			// protect it in CAS
			// see http://www.geogebra.org/trac/ticket/1440
			boolean handled = false;
			if (name.length() == 1) {
				char ch = name.charAt(0);
				if (ch == 'x' || ch == 'y' || ch == 'z') {
					if (args.get(0).isListValue()) {
						sbCASCommand.append("applyfunction(");
						sbCASCommand.append(ch);
						sbCASCommand.append("coord,");
					} else {
						sbCASCommand.append(ch);
						sbCASCommand.append("coord(");
					}
					handled = true;
				}
			}

			// standard case: add ggbcasvar prefix to name for CAS
			if (!handled) {
				sbCASCommand.append(app.getKernel()
						.printVariableName(name, tpl));
				sbCASCommand.append('(');
			}
			for (int i = 0; i < args.size(); i++) {
				ExpressionValue ev = args.get(i);
				sbCASCommand.append(toString(ev, symbolic, tpl));
				sbCASCommand.append(',');
			}
			sbCASCommand.setCharAt(sbCASCommand.length() - 1, ')');
		}

		// translation found:
		// replace %0, %1, etc. in translation by command arguments
		else {
			translation = translation.replaceAll("%@", app.getKernel()
					.getCasVariablePrefix());
			for (int i = 0; i < translation.length(); i++) {
				char ch = translation.charAt(i);
				if (ch == '%') {
					// get number after %
					i++;
					int pos = translation.charAt(i) - '0';
					if (pos >= 0 && pos < args.size()) {
						// success: insert argument(pos)
						ExpressionValue ev = args.get(pos);
						sbCASCommand.append(toString(ev, symbolic, tpl));
					} else {
						// failed
						sbCASCommand.append(ch);
						sbCASCommand.append(translation.charAt(i));
					}
					//@ is a hack: only use the value if it does not contain () to avoid (1,2)' in CAS 
				}else if (ch == '@') {
					// get number after %
					i++;
					int pos = translation.charAt(i) - '0';
					if (pos >= 0 && pos < args.size()) {
						// success: insert argument(pos)
						ExpressionValue ev = args.get(pos);
						if(toString(ev, symbolic, tpl).matches("[^(),]*"))
							sbCASCommand.append(toString(ev, symbolic, tpl));
						else sbCASCommand.append("x");
					} else {
						// failed
						sbCASCommand.append(ch);
						sbCASCommand.append(translation.charAt(i));
					}
				} else {
					sbCASCommand.append(ch);
				}
			}
		}

		return sbCASCommand.toString();
	}

	/**
	 * Returns whether the given command is available in the underlying CAS.
	 * 
	 * @param cmd
	 *            command with name and number of arguments
	 * @return whether command is available
	 */
	final public boolean isCommandAvailable(final Command cmd) {
		StringBuilder sbCASCommand = new StringBuilder();
		sbCASCommand.append(cmd.getName());
		sbCASCommand.append(".");
		sbCASCommand.append(cmd.getArgumentNumber());
		if (casParser.isCommandAvailable(sbCASCommand.toString()))
			return true;

		sbCASCommand.setLength(0);
		sbCASCommand.append(cmd.getName());
		sbCASCommand.append(".N");
		if (casParser.isCommandAvailable(sbCASCommand.toString())) {
			return true;
		}
		return false;
	}

	public final String toAssignment(final GeoElement ge,
			final StringTemplate tpl) {
		String body = ge.getCASString(tpl, false);
		App.debug(body);
		String casLabel = ge.getLabel(tpl);
		if (ge instanceof FunctionalNVar) {
			FunctionVariable[] funVariables = ((FunctionalNVar) ge)
					.getFunction().getFunctionVariables();
			String[] variables = new String[funVariables.length];
			for (int i = 0; i < funVariables.length; i++) {
				variables[i] = funVariables[i].toString(tpl);
			}
			AssignmentType assignmentType = ((FunctionalNVar) ge).getFunction().getAssignmentType();

			return getCurrentCAS().translateFunctionDeclaration(casLabel,
					variables, body, assignmentType);
		}
		return getCurrentCAS().translateAssignment(casLabel, body);
	}

	/**
	 * Returns true if the two input expressions are structurally equal. For
	 * example "2 + 2/3" is structurally equal to "2 + (2/3)" but unequal to
	 * "(2 + 2)/3"
	 * 
	 * @param inputVE
	 *            includes internal command names
	 * @param localizedInput
	 *            includes localized command names
	 */
	public boolean isStructurallyEqual(final ValidExpression inputVE,
			final String localizedInput) {
		try {
			// current input
			String input1normalized = casParser.toString(inputVE,
					StringTemplate.get(StringType.GEOGEBRA_XML));

			// new input
			ValidExpression ve2 = casParser
					.parseGeoGebraCASInputAndResolveDummyVars(localizedInput);
			String input2normalized = casParser.toString(ve2,
					StringTemplate.get(StringType.GEOGEBRA_XML));

			// compare if the parsed expressions are equal
			return input1normalized.equals(input2normalized);
		} catch (Throwable th) {
			System.err.println("GeoGebraCAS.isStructurallyEqual: "
					+ th.getMessage());
			return false;
		}
	}

	public CasType getCurrentCASType() {
		return currentCAS;
	}

	public void evaluateGeoGebraCASAsync(final AsynchronousCommand c) {
		getCurrentCAS().evaluateGeoGebraCASAsync(c);
	}

	public String evaluateGeoGebraCAS(ValidExpression evalVE,
			MyArbitraryConstant arbconst) {
		return evaluateGeoGebraCAS(evalVE, arbconst,
				StringTemplate.numericDefault);
	}

	/**
	 * Returns the internal names of all the commands available in the current
	 * CAS.
	 * 
	 * @return A Set of all internal CAS commands.
	 */
	public Set<String> getAvailableCommandNames() {
		Set<String> cmdSet = new HashSet<String>();
		for (String signature : casParser.getTranslationRessourceBundle()
				.keySet()) {
			String cmd = signature.substring(0, signature.indexOf('.'));
			cmdSet.add(cmd);
		}
		return cmdSet;
	}

}