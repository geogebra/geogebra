package geogebra.common.cas.giac;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.Evaluate;
import geogebra.common.kernel.AsynchronousCommand;
import geogebra.common.kernel.CASException;
import geogebra.common.kernel.CASGenericInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.AssignmentType;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.Traversing.ArbconstReplacer;
import geogebra.common.kernel.arithmetic.Traversing.PowerRootReplacer;
import geogebra.common.kernel.arithmetic.Traversing.PrefixRemover;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.CASSettings;

/**
 * Platform (Java / GWT) independent part of giac CAS
 */
public abstract class CASgiac implements CASGenericInterface {
	/** parser tools */
	protected CasParserTools parserTools;
	
	/** CAS parser */
	public CASparser casParser;
	/** variable ordering, e.g. for Integral[a*b] */
	protected static StringBuilder varOrder = new StringBuilder(
			"ggbtmpvarx, ggbtmpvary, ggbtmpvarz, ggbtmpvara, "
					+ "ggbtmpvarb, ggbtmpvarc, ggbtmpvard, ggbtmpvare, ggbtmpvarf, "
					+ "ggbtmpvarg, ggbtmpvarh, ggbtmpvari, ggbtmpvarj, ggbtmpvark, "
					+ "ggbtmpvarl, ggbtmpvarm, ggbtmpvarn, ggbtmpvaro, ggbtmpvarp, "
					+ "ggbtmpvarq, ggbtmpvarr, ggbtmpvars, ggbtmpvart, ggbtmpvaru, "
					+ "ggbtmpvarv, ggbtmpvarw");
	private static boolean initialized = false;
	
	/**
	 * @return whether CAS was already loaded (in Web)
	 */
	public boolean isInitialized(){
		return initialized;
	}

	/**
	 * We escape any upper-letter words so Reduce doesn't switch them to /
	 * lower-letter, / however the following function-names should not be
	 * escaped / (note: all functions here must be in lowercase!)
	 */

	private static Evaluate mpreduce;

	/**
	 * Creates new MPReduce CAS
	 * 
	 * @param casParser
	 *            parser
	 */
	public CASgiac(CASparser casParser) {
		this.casParser = casParser;
	}

	/**
	 * @param exp
	 *            MPREduce command
	 * @return value returned from CAS
	 */
	public abstract String evaluateCAS(String exp);

	final public String evaluateRaw(final String input) throws Throwable {
		
		StringBuilder sb = new StringBuilder();

		String exp = input;
		
		/*
		// we need to escape any upper case letters and non-ascii codepoints
		// with '!'
		StringTokenizer tokenizer = new StringTokenizer(exp, "(),;[] ", true);
		while (tokenizer.hasMoreElements()) {
			String t = tokenizer.nextToken();
			if (casParser.getParserFunctions().isReserved(t))
				sb.append(t);
			else {
				for (int i = 0; i < t.length(); ++i) {
					char c = t.charAt(i);
					if (StringUtil.isLetter(c) && (c < 97 || c > 122)) {
						sb.append('!');
						sb.append(c);
					} else {
						switch (c) {
						case '\'':
							sb.append('!');
							sb.append(c);
							break;

						case '\\':
							if (i < (t.length() - 1))
								sb.append(t.charAt(++i));
							break;

						default:
							sb.append(c);
							break;
						}
					}

				}
			}
		}
		exp = sb.toString();
		
		*/
		
		
		App.debug("giac eval: " + exp);
		if (!initialized) {
			App.showAnnouncement("giac loaded"); // for the web
			initialized = true;
		}
		String result = evaluate(exp, getTimeoutMilliseconds());

		sb.setLength(0);
		for (String s : result.split("\n")) {
			s = s.trim();
			if (s.length() == 0)
				continue;
			else if (s.startsWith("***")) { // MPReduce comment
				App.debug("MPReduce comment: " + s);
				continue;
			}else if (s.contains("invalid as")) { // MPReduce comment
				App.debug("MPReduce comment: " + s);
				continue;
			} else if (s.startsWith("Unknown")) {
				App.debug("Assumed " + s);
				continue;
			} else {
				// look for any trailing $
				int len = s.length();
				while (len > 0 && s.charAt(len - 1) == '$')
					--len;

				// remove the !
				for (int i = 0; i < len; ++i) {
					char character = s.charAt(i);
					if (character == '!') {
						if (i + 1 < len) {
							character = s.charAt(++i);
						}
					}
					sb.append(character);
				}
			}
		}

		//result = sb.toString().replaceAll("\\[", "(").replaceAll("\\]", ")");

		result = sb.toString();
		
		// TODO: remove
		// for Unique[] command 
		if (result.startsWith("set[")) {
			App.debug("giac: removing set[...] from "+result);
			result = "{" + result.substring(4, result.length() - 1) + "}";
		}
		
		// eg PartialFractions has with_sqrt(0):; at start
		if (result.startsWith("0,")) {
			App.debug("giac: removing '0,' from "+result);
			result = result.substring(2, result.length());
		}
		
		if (result.startsWith("\"")) {
			// eg "Index outside range : 5, vector size is 3, syntax compatibility mode xcas Error: Invalid dimension"
			// assume error
			App.debug("message from giac (assuming error) "+result);
			result = "?";
		}
		
		// TODO: remove
		App.debug("CASgiac.evaluateRaw: result: " + result);
		return result;
	}

	protected abstract String evaluate(String exp, long timeoutMilliseconds) throws Throwable;

	final public synchronized String evaluateGeoGebraCAS(
			final ValidExpression inputExpression, MyArbitraryConstant arbconst,
			StringTemplate tpl) throws CASException {
		ValidExpression casInput = inputExpression;
		Command cmd = casInput.getTopLevelCommand();
		boolean keepInput = casInput.isKeepInputUsed() || (cmd!=null && cmd.getName().equals("KeepInput"));
		String plainResult = getPlainResult(casInput,keepInput);
			
		if (keepInput) {
			// remove KeepInput[] command and take argument			
			if (cmd != null && cmd.getName().equals("KeepInput")) {
				// use argument of KeepInput as casInput
				if (cmd.getArgumentNumber() > 0) {
					casInput = cmd.getArgument(0);
				}
			}
		}

		// convert result back into GeoGebra syntax
		if (casInput instanceof FunctionNVar) {
			// delayed function definition f(x)::= Derivative[x^2] should return Derivative[x^2]
			if (casInput.getAssignmentType() == AssignmentType.DELAYED){
				return casInput.toString(StringTemplate.numericNoLocal);
			}
			// function definition f(x) := x^2 should return x^2
			// f(x):=Derivative[x^2] should return 2x
			// do not return directly, must check keepinput
			/*plainResult = evaluateMPReduce(plainResult
							+ "("
							+ ((FunctionNVar) casInput).getVarString(StringTemplate.casTemplate)
							+ ")");*/
		}
		
		String result = plainResult;
		if (keepInput) {
			// when keepinput was treated in MPReduce, it is now > 1
			String keepinputVal = evaluateCAS("keepinput!!;");
			boolean keepInputUsed = !"1".equals(keepinputVal);
			if (!keepInputUsed) {
				//return directly, we don't want another toGeoGebraString,
				//because of e and i
				return casParser.toGeoGebraString(casInput, tpl);
			}
		}
		
		// standard case
		if ("".equals(result)) {
			return null;
		}
		return toGeoGebraString(result, arbconst, tpl);

	}
	
	final public synchronized ExpressionValue evaluateToExpression(
			final ValidExpression inputExpression, MyArbitraryConstant arbconst) throws CASException {
		String result = getPlainResult(inputExpression,false);
		// standard case
		if ("".equals(result)) {
			return null;
		}
		return replaceRoots(casParser.parseMPReduce(result),arbconst);

	}

	private String getPlainResult(ValidExpression casInput,
			boolean keepInput) {
		// KeepInput[] command should set flag keepinput!!:=1
		// so that commands like Substitute can work accordingly
		Command cmd = casInput.getTopLevelCommand();
		boolean taylorToStd = true;
		
		if(!keepInput && cmd != null && cmd.getName().equals("TaylorSeries")) {
				taylorToStd = false;
			}
		 else if (cmd != null && "Delete".equals(cmd.getName())
				) {
			String label = 
					cmd.getArgument(0).toString(
							StringTemplate.defaultTemplate);
			GeoElement geo = casInput
					.getKernel()
					.lookupLabel(label);
			if(geo==null)
				geo = casInput
				.getKernel().lookupCasCellLabel(label);
			if (geo != null) {
				geo.remove();
			}
			return "true";
		}
		

		// convert parsed input to MPReduce string
		String mpreduceInput = casParser.translateToCAS(casInput,
				StringTemplate.giacTemplate, this);
		
		App.error(casInput+"\n\n"+mpreduceInput );

		/*
		// tell MPReduce whether it should use the keep input flag,
		// e.g. important for Substitute
		StringBuilder sb = new StringBuilder();
	
		sb.append("<<resetsettings(");
		sb.append(keepInput ? 1 : 0);
		sb.append(",");
		sb.append(taylorToStd ? 1 : 0);
		sb.append(",");
		// sb.append("$ numeric!!:=0$ precision 30$ print\\_precision 16$ on pri, rationalize  $ off complex, rounded, numval, factor, exp, allfac, div, combinelogs, expandlogs, revpri $ currentx!!:= ");
		sb.append("ggbtmpvarx,ggbtmpvary);");
		

		sb.append(mpreduceInput);
		sb.append(">>");*/

		// evaluate in MPReduce
		String plainResult = evaluateCAS(mpreduceInput);
		return plainResult;
	}

	/**
	 * Tries to parse a given MPReduce string and returns a String in GeoGebra
	 * syntax.
	 * 
	 * @param mpreduceString
	 *            String in MPReduce syntax
	 * @param arbconst
	 *            arbitrary constant handler
	 * @param tpl
	 *            template that should be used for serialization. Should be
	 *            casCellTemplate for CAS and defaultTemplate for input bar
	 * @return String in Geogebra syntax.
	 * @throws CASException
	 *             Throws if the underlying CAS produces an error
	 */
	final public synchronized String toGeoGebraString(String mpreduceString,
			MyArbitraryConstant arbconst, StringTemplate tpl)
			throws CASException {
		ExpressionValue ve = replaceRoots(casParser.parseMPReduce(mpreduceString),arbconst);
		//replace rational exponents by roots or vice versa

		

		return casParser.toGeoGebraString(ve, tpl);
	}
	
	private static ExpressionValue replaceRoots(ExpressionValue ve,MyArbitraryConstant arbconst){
		if (ve != null) {
			boolean toRoot = ve.getKernel().getApplication().getSettings()
					.getCasSettings().getShowExpAsRoots();
			ve.traverse(PowerRootReplacer.getReplacer(toRoot));
			if (arbconst != null) {
				arbconst.reset();
				ve.traverse(ArbconstReplacer.getReplacer(arbconst));
			}
			PrefixRemover pr = PrefixRemover.getCollector();
			ve.traverse(pr);
		}
		return ve;
	}

	/**
	 * @return MPReduce evaluator
	 */
	public synchronized void reset() {

	}

	/**
	 * Loads all packages and initializes all the functions which do not depend
	 * on the current kernel.
	 * 
	 * @param mpreduce1
	 *            MPReduce evaluator
	 * @throws Throwable
	 *             from evaluator when some of the initial commands fails
	 */
	protected static final void initStaticMyMPReduceFunctions(Evaluate mpreduce1)
			throws Throwable {
		App.showAnnouncement("CASInitializing"+"desktop giac"); // for the desktop
		initialized = true;
		App.debug("Loading packages...");

		String[] packages = { "rsolve", "numeric", "linalg", "reset", "trigsimp",
				"polydiv", "myvector"};
		for (String p : packages) {
			mpreduce1.evaluate("load_package " + p + ";");
			App.debug("Reduce package " + p + " loaded"+System.currentTimeMillis());
		}

	
		App.debug("Initial procedures in Reduce have been defined");
		App.hideAnnouncement();
		
	}

	/**
	 * Integral[sin(pi*x)/(pi*x),0,Infinity] Initializes function which depend
	 * on the current kernel.
	 * 
	 * @param mpreduce1
	 *            MPReduceevaluator
	 * @throws Throwable
	 *             from evaluator if some of the initialization commands fails
	 */
	protected final synchronized static void initDependentMyMPReduceFunctions(
			geogebra.common.cas.Evaluate mpreduce1) throws Throwable {

		if (CASgiac.mpreduce != mpreduce1) {
			initStaticMyMPReduceFunctions(mpreduce1); // SLOW in web
		}
		CASgiac.mpreduce = mpreduce1;

		// user variable ordering
		String variableOrdering = "l%x, %x, l%y, %y, l%z, %z, l%a, %a, "
				+ "l%b, %b, l%c, %c, l%d, %d, l%e, %e, l%f, %f, "
				+ "l%g, %g, l%h, %h, l%i, %i, l%j, %j, l%k, %k, "
				+ "l%l, %l, l%m, %m, l%n, %n, l%o, %o, l%p, %p, "
				+ "l%q, %q, l%r, %r, l%s, %s, l%t, %t, l%u, %u, "
				+ "l%v, %v, l%w, %w";
		// make sure to use current kernel's variable prefix
		variableOrdering = variableOrdering.replace("%", Kernel.TMP_VARIABLE_PREFIX);
		if (varOrder.length() > 0 && variableOrdering.length() > 0) {
			varOrder.append(',');
		}
		varOrder.append(variableOrdering);
		mpreduce1.evaluate("varorder!!:= list(" + varOrder + ");");
		//mpreduce1.evaluate("order varorder!!;");
		mpreduce1.evaluate("korder varorder!!;");

		// access functions for elements of a vector
		String xyzCoordFunctions = "procedure ggbcasvarx(a); first(a);"
				+ "procedure ggbcasvary(a); second(a);"
				+ "procedure ggbcasvarz(a); third(a);";
		// make sure to use current kernel's variable prefix
		xyzCoordFunctions = xyzCoordFunctions.replace("ggbcasvar", Kernel.TMP_VARIABLE_PREFIX);
		mpreduce1.evaluate(xyzCoordFunctions);	
	}

	/**
	 * Returns the ordering number of a ggbtmpvar
	 * 
	 * @param ggbtmpvar
	 *            The ggbtmpvar of which the ordering number is needed
	 * @return The ordering number if the given ggbtmpvar
	 * @throws IllegalArgumentException
	 *             if the given {@link String} is not a valid ggbtmpvar
	 */
	public static int getVarOrderingNumber(String ggbtmpvar)
			throws IllegalArgumentException {
		String varOrderNoWhitespaces = varOrder.toString().replaceAll(" ", "");
		String[] vars = varOrderNoWhitespaces.split(",");
		for (int i = 0; i < vars.length; i++) {
			if (ggbtmpvar.equals(vars[i])) {
				return i;
			}
		}
		throw new IllegalArgumentException("The given argument \"" + ggbtmpvar
				+ "\" is not a valid ggbtmpvar.");
	}

	/**
	 * Timeout for CAS in milliseconds.
	 */
	private long timeoutMillis = 5000;

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

	public String translateAssignment(final String label, final String body) {
		// default implementation works for MPReduce and MathPiper
		return label + " := " + body;
	}

	/**
	 * This method is called when asynchronous CAS call is finished. It tells
	 * the calling algo to update itself and adds the result to cache if
	 * suitable.
	 * 
	 * @param exp
	 *            parsed CAS output
	 * @param result2
	 *            output as string (for cacheing)
	 * @param exception
	 *            exception which stopped the computation (null if there wasn't
	 *            one)
	 * @param c
	 *            command that called the CAS asynchronously
	 * @param input
	 *            input string (for cacheing)
	 */
	public void CASAsyncFinished(ValidExpression exp, String result2,
			Throwable exception, AsynchronousCommand c, String input) {
		String result = result2;
		// pass on exception
		if (exception != null) {
			c.handleException(exception, input.hashCode());
			return;
		}
		// check if keep input command was successful
		// e.g. for KeepInput[Substitute[...]]
		// otherwise return input
		if (exp.isKeepInputUsed() && ("?".equals(result))) {
			// return original input
			c.handleCASoutput(exp.toString(StringTemplate.maxPrecision),
					input.hashCode());
		}

		// success
		if (result2 != null) {
			// get names of escaped global variables right
			// e.g. "ggbcasvar1a" needs to be changed to "a"
			// e.g. "ggbtmpvara" needs to be changed to "a"
			result = exp.getKernel().removeCASVariablePrefix(result, " ");
		}

		c.handleCASoutput(result, input.hashCode());
		if (c.useCacheing())
			exp.getKernel().putToCasCache(input, result);
	}
	
	public void appendListStart(StringBuilder sbCASCommand){
		sbCASCommand.append("list(");
	}

	public void appendListEnd(StringBuilder sbCASCommand){
		sbCASCommand.append(")");
	}

}
