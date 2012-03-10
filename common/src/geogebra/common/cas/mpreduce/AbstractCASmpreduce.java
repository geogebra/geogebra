package geogebra.common.cas.mpreduce;

import geogebra.common.cas.CASException;
import geogebra.common.cas.CASgeneric;
import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasExpressionFactory;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.Evaluate;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.main.AbstractApplication;

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public abstract class AbstractCASmpreduce extends CASgeneric {

	protected CasParserTools parserTools;
	protected static StringBuilder varOrder = new StringBuilder(
			"ggbtmpvarx, ggbtmpvary, ggbtmpvarz, ggbtmpvara, "
					+ "ggbtmpvarb, ggbtmpvarc, ggbtmpvard, ggbtmpvare, ggbtmpvarf, "
					+ "ggbtmpvarg, ggbtmpvarh, ggbtmpvari, ggbtmpvarj, ggbtmpvark, "
					+ "ggbtmpvarl, ggbtmpvarm, ggbtmpvarn, ggbtmpvaro, ggbtmpvarp, "
					+ "ggbtmpvarq, ggbtmpvarr, ggbtmpvars, ggbtmpvart, ggbtmpvaru, "
					+ "ggbtmpvarv, ggbtmpvarw");
	protected int significantNumbers = -1;
	/**
	 * We escape any upper-letter words so Reduce doesn't switch them to /
	 * lower-letter, / however the following function-names should not be
	 * escaped / (note: all functions here must be in lowercase!)
	 */
	final protected Set<String> predefinedFunctions = ExpressionNodeConstants.RESERVED_FUNCTION_NAMES;

	public AbstractCASmpreduce(CASparser casParser) {
		super(casParser);
	}

	public abstract String evaluateMPReduce(String exp);

	@Override
	final public String evaluateRaw(String exp) throws Throwable {
		// we need to escape any upper case letters and non-ascii codepoints
		// with '!'
		StringTokenizer tokenizer = new StringTokenizer(exp, "(),;[] ", true);
		StringBuilder sb = new StringBuilder();
		while (tokenizer.hasMoreElements()) {
			String t = tokenizer.nextToken();
			if (predefinedFunctions.contains(t.toLowerCase()))
				sb.append(t);
			else {
				for (int i = 0; i < t.length(); ++i) {
					char c = t.charAt(i);
					if (Character.isLetter(c) && (c < 97 || c > 122)) {
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

		if(AbstractApplication.dbg!=null)System.out.println("eval with MPReduce: " + exp);
		String result = getMPReduce().evaluate(exp, getTimeoutMilliseconds());

		sb.setLength(0);
		for (String s : result.split("\n")) {
			s = s.trim();
			if (s.length() == 0)
				continue;
			else if (s.startsWith("***")) { // MPReduce comment
				AbstractApplication.debug("MPReduce comment: " + s);
				continue;
			} else if (s.startsWith("Unknown")) {
				AbstractApplication.debug("Assumed " + s);
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

		result = sb.toString();

		// TODO: remove
		if(AbstractApplication.dbg!=null)
			System.out.println("   result: " + result);
		return result;
	}

	@Override
	final public synchronized String evaluateGeoGebraCAS(
			ValidExpression casInput,StringTemplate tpl) throws CASException {
		// KeepInput[] command should set flag keepinput!!:=1
		// so that commands like Substitute can work accordingly
		boolean keepInput = casInput.isKeepInputUsed();
		if (keepInput) {
			// remove KeepInput[] command and take argument
			Command cmd = casInput.getTopLevelCommand();
			if (cmd != null && cmd.getName().equals("KeepInput")) {
				// use argument of KeepInput as casInput
				if (cmd.getArgumentNumber() > 0)
					casInput = cmd.getArgument(0);
			}
		}

		// convert parsed input to MPReduce string
		String mpreduceInput = translateToCAS(casInput,
				StringTemplate.casTemplate);

		// tell MPReduce whether it should use the keep input flag,
		// e.g. important for Substitute
		StringBuilder sb = new StringBuilder();
		sb.append("<<keepinput!!:=");
		sb.append(keepInput ? 1 : 0);
		sb.append("$ numeric!!:=0$ precision 30$ print\\_precision 16$ off complex, rounded, numval, factor, div, combinelogs, expandlogs, pri$ currentx!!:= ");
		sb.append(casParser.getKernel().getCasVariablePrefix());
		sb.append("x; currenty!!:= ");
		sb.append(casParser.getKernel().getCasVariablePrefix());
		sb.append("y;");
		sb.append(mpreduceInput);
		sb.append(">>");

		// evaluate in MPReduce
		String result = evaluateMPReduce(sb.toString());

		if (keepInput) {
			// when keepinput was treated in MPReduce, it is now > 1
			String keepinputVal = evaluateMPReduce("keepinput!!;");
			boolean keepInputUsed = !"1".equals(keepinputVal);
			if (!keepInputUsed)
				result = casParser.toGeoGebraString(casInput,tpl);
		}

		// convert result back into GeoGebra syntax
		if (casInput instanceof FunctionNVar) {
			// function definition f(x) := x^2 should return x^2
			String ret = casInput.toString(StringTemplate.defaultTemplate);
			return ret;
		}
		// standard case
		return toGeoGebraString(result,tpl);
	}

	@Override
	public String translateFunctionDeclaration(String label, String parameters,
			String body) {
		StringBuilder sb = new StringBuilder();
		sb.append(" procedure ");
		sb.append(label);
		sb.append("(");
		sb.append(parameters);
		sb.append("); begin return ");
		sb.append(body);
		sb.append(" end ");

		return sb.toString();
	}

	@Override
	public Map<String, String> initTranslationMap() {
		return new Ggb2MPReduce().getMap();
	}

	/**
	 * Tries to parse a given MPReduce string and returns a String in GeoGebra
	 * syntax.
	 * 
	 * @param mpreduceString
	 *            String in MPReduce syntax
	 * @return String in Geogebra syntax.
	 * @throws CASException
	 *             Throws if the underlying CAS produces an error
	 */
	final public synchronized String toGeoGebraString(String mpreduceString,StringTemplate tpl)
			throws CASException {
		ValidExpression ve = casParser.parseMPReduce(mpreduceString);

		// replace rational exponents by roots or vice versa
		CasExpressionFactory factory = new CasExpressionFactory(ve);
		if (ve.getKernel().getApplication().getSettings().getCasSettings()
				.getShowExpAsRoots())
			factory.replaceExpByRoots();
		else
			factory.replaceRootsByExp();

		return casParser.toGeoGebraString(ve,tpl);
	}

	@Override
	public void unbindVariable(String var) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("clear(");
			sb.append(var);
			sb.append(");");
			getMPReduce().evaluate(sb.toString());

			// TODO: remove
			if(AbstractApplication.dbg!=null)System.out.println("Cleared variable: " + sb.toString());
		} catch (Throwable e) {
			if(AbstractApplication.dbg!=null)System.err
					.println("Failed to clear variable from MPReduce: " + var);
		}
	}

	protected abstract Evaluate getMPReduce();

	@Override
	public synchronized void reset() {

		try {
			getMPReduce().evaluate("resetreduce;");
			getMPReduce().initialize();
			initMyMPReduceFunctions(getMPReduce());
		} catch (Throwable e) {
			AbstractApplication.debug("failed to reset MPReduce");
			e.printStackTrace();
		}
	}

	/**
	 * Sets the number of signficiant figures (digits) that should be used as
	 * print precision for the output of Numeric[] commands.
	 * 
	 * @param significantNumbers
	 */
	@Override
	public void setSignificantFiguresForNumeric(int significantNumbers) {
		if (this.significantNumbers == significantNumbers)
			return;
		this.significantNumbers = significantNumbers;
		try {
			getMPReduce().evaluate("printprecision!!:=" + significantNumbers);
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	protected final synchronized void initMyMPReduceFunctions(
			geogebra.common.cas.Evaluate mpreduce1) throws Throwable {

		// user variable ordering
		String variableOrdering = "ggbcasvarx, ggbcasvary, ggbcasvarz, ggbcasvara, "
				+ "ggbcasvarb, ggbcasvarc, ggbcasvard, ggbcasvare, ggbcasvarf, "
				+ "ggbcasvarg, ggbcasvarh, ggbcasvari, ggbcasvarj, ggbcasvark, "
				+ "ggbcasvarl, ggbcasvarm, ggbcasvarn, ggbcasvaro, ggbcasvarp, "
				+ "ggbcasvarq, ggbcasvarr, ggbcasvars, ggbcasvart, ggbcasvaru, "
				+ "ggbcasvarv, ggbcasvarw";
		// make sure to use current kernel's variable prefix
		variableOrdering = variableOrdering.replace("ggbcasvar", casParser
				.getKernel().getCasVariablePrefix());
		if (varOrder.length() > 0 && variableOrdering.length() > 0) {
			varOrder.append(',');
		}
		varOrder.append(variableOrdering);
		mpreduce1.evaluate("varorder!!:= list(" + varOrder + ");");
		mpreduce1.evaluate("order varorder!!;");
		mpreduce1.evaluate("korder varorder!!;");

		// access functions for elements of a vector
		String xyzCoordFunctions = "procedure ggbcasvarx(a); first(a);"
				+ "procedure ggbcasvary(a); second(a);"
				+ "procedure ggbcasvarz(a); third(a);";
		// make sure to use current kernel's variable prefix
		xyzCoordFunctions = xyzCoordFunctions.replace("ggbcasvar", casParser
				.getKernel().getCasVariablePrefix());
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

}
