package org.geogebra.common.cas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.kernel.AsynchronousCommand;
import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.Traversing.DummyVariableCollector;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.MaxSizeHashMap;
import org.geogebra.common.util.debug.Log;

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

	private ArrayList<String> varSwaps = new ArrayList<String>();
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
		if (cas == null) {
			setCurrentCAS();
		}
	}

	public synchronized void setCurrentCAS() {
		try {
			cas = getGiac();
			app.getSettings().getCasSettings().addListener(cas);

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
	 * @return Giac
	 */
	private synchronized CASGenericInterface getGiac() {
		if (cas == null) {
			cas = app.getCASFactory().newGiac(casParser,
					new CasParserToolsImpl('e'), app.getKernel());
		}
		return cas;
	}

	public String evaluateGeoGebraCAS(ValidExpression casInput,
			MyArbitraryConstant arbconst, StringTemplate tpl, GeoCasCell cell, Kernel kernel)
			throws CASException {

		String result = null;
		CASException exception = null;
		try {
			result = getCurrentCAS().evaluateGeoGebraCAS(casInput, arbconst,
					tpl, cell, kernel);
		} catch (CASException ce) {
			exception = ce;
		} finally {
			// do nothing
		}

		// check if keep input command was successful
		// e.g. for KeepInput[Substitute[...]]
		// otherwise return input
		if (cell != null && cell.isKeepInputUsed()
				&& (exception != null || "?".equals(result))) {
			// return original input
			return casInput.toString(tpl);
		}

		// pass on exception
		if (exception != null)
			throw exception;

		// success
		if (result != null) {
			app.getKernel();
			// get names of escaped global variables right
			// e.g. "ggbcasvar1a" needs to be changed to "a"
			// e.g. "ggbtmpvara" needs to be changed to "a"
			result = Kernel.removeCASVariablePrefix(result, " ");
		}

		return result;
	}

	final public String evaluateGeoGebraCAS(String exp,
			MyArbitraryConstant arbconst, StringTemplate tpl, Kernel kernel)
			throws CASException {
		try {
			ValidExpression inVE = casParser.parseGeoGebraCASInput(exp, null);
			String ret = evaluateGeoGebraCAS(inVE, arbconst, tpl, null, kernel);
			if (ret == null)
				throw new CASException(new Exception(app.getLocalization()
						.getError("CAS.GeneralErrorMessage")));
			return ret;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new CASException(t);
		}
	}

	final public String evaluateRaw(String exp) throws Throwable {
		return getCurrentCAS().evaluateRaw(exp);
	}

	/**
	 * Evaluates an expression given in MPReduce/Giac syntax.
	 * 
	 * @return result string (null possible)
	 * @param exp
	 *            the expression
	 * @throws CASException
	 *             if there is a timeout or the expression cannot be evaluated
	 * */
	final public String evaluate(String exp) throws CASException {
		return getCurrentCAS().evaluateCAS(exp);
	}

	// these variables are cached to gain some speed in getPolynomialCoeffs
	private Map<String, String[]> getPolynomialCoeffsCache = new MaxSizeHashMap<String, String[]>(
			Kernel.GEOGEBRA_CAS_CACHE_SIZE);
	private StringBuilder getPolynomialCoeffsSB = new StringBuilder();
	private StringBuilder sbPolyCoeffs = new StringBuilder();

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
		sbPolyCoeffs.append("when(is\\_polynomial("); // first check if
														// expression is
														// polynomial
		sbPolyCoeffs.append(polyExpr);
		sbPolyCoeffs.append(',');
		sbPolyCoeffs.append(variable);
		sbPolyCoeffs.append("),");
		sbPolyCoeffs.append("coeff("); // if it is then return with it's
										// coefficients
		sbPolyCoeffs.append(getPolynomialCoeffsSB.toString());
		sbPolyCoeffs.append("),{})"); // if not return {}

		try {
			// expand expression and get coefficients of
			// "3*a*x^2 + b" in form "{ b, 0, 3*a }"
			String tmp = evaluate(sbPolyCoeffs.toString());

			// no result
			if ("{}".equals(tmp) || "".equals(tmp) || tmp == null)
				return null;

			// not a polynomial: result still includes the variable, e.g. "x"
			if (tmp.indexOf(variable) >= 0)
				return null;

			app.getKernel();
			// get names of escaped global variables right
			// e.g. "ggbcasvara" needs to be changed to "a"
			tmp = Kernel.removeCASVariablePrefix(tmp);

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
		/*
		 * previously this method also replaced f by f(x), but FunctionExpander
		 * takes care of that now
		 */
		if (symbolic) {
			return ev.wrap().toString(tpl);
		}
		return ev.toValueString(tpl);
	}

	final synchronized public String getCASCommand(final String name,
			final ArrayList<ExpressionNode> args, boolean symbolic,
			StringTemplate tpl) {
		return getCASCommand(name, args, symbolic, tpl, true);
	}

	final synchronized private String getCASCommand(final String name,
			final ArrayList<ExpressionNode> args, boolean symbolic,
			StringTemplate tpl, boolean allowOutsourcing) {
		StringBuilder sbCASCommand = new StringBuilder(80);

		// build command key as name + ".N"
		sbCASCommand.setLength(0);
		sbCASCommand.append(name);
		sbCASCommand.append(".N");
		String translation = casParser.getTranslatedCASCommand(sbCASCommand
				.toString());

		// check for eg Sum.N=sum(%)
		if (translation != null) {
			sbCASCommand.setLength(0);
			for (int i = 0; i < translation.length(); i++) {
				char ch = translation.charAt(i);
				if (ch == '%') {
					if (args.size() == 1) { // might be a list as the argument
						ExpressionValue ev = args.get(0).unwrap();
						String str = toString(ev, symbolic, tpl);
						sbCASCommand.append(str);
					} else {
						getCurrentCAS().appendListStart(sbCASCommand);
						for (int j = 0; j < args.size(); j++) {
							ExpressionValue ev = args.get(j);
							sbCASCommand.append(toString(ev, symbolic, tpl));
							sbCASCommand.append(',');
						}
						// remove last comma
						sbCASCommand.setLength(sbCASCommand.length() - 1);
						getCurrentCAS().appendListEnd(sbCASCommand);
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
		boolean argIsList = false;
		if (args.size() == 1 && args.get(0).isExpressionNode()
				&& name.equals("Point")) {
			ExpressionNode node = args.get(0);
			if (node.isLeaf() && node.getLeft() instanceof MyList) {
				if (((ExpressionNode) ((MyList) node.getLeft())
						.getListElement(0)).getLeft().isNumberValue()) {
					sbCASCommand.append(1);
				} else {
					sbCASCommand.append((((MyList) node.getLeft()).size()));
					argIsList = true;
				}
			}
		} else {
			sbCASCommand.append(args.size());
		}

		boolean outsourced = false;
		// check if there is support in the outsourced CAS (now SingularWS) for
		// this command:
		if (allowOutsourcing && App.singularWS != null
				&& App.singularWS.isAvailable()) {
			translation = App.singularWS.getTranslatedCASCommand(sbCASCommand
					.toString());
			if (translation != null) {
				outsourced = true;
			}
		}

		// get translation ggb -> MathPiper/Maxima
		if (!outsourced) {
			translation = casParser.getTranslatedCASCommand(sbCASCommand
					.toString());
		}
		sbCASCommand.setLength(0);

		// no translation found:
		// use key as function name
		if (translation == null) {
			Kernel kern = app.getKernel();
			boolean silent = kern.isSilentMode();
			// convert command names x, y, z to xcoord, ycoord, ycoord to
			// protect it in CAS
			// see http://www.geogebra.org/trac/ticket/1440
			boolean handled = false;
			if (name.length() == 1) {
				char ch = name.charAt(0);
				if (ch == 'x' || ch == 'y' || ch == 'z') {
					if (args.get(0).evaluatesToList()) {

						sbCASCommand
								.append(toString(args.get(0), symbolic, tpl));
						sbCASCommand.append('[');

						switch (ch) {
						case 'x':
						default:
							sbCASCommand.append('0');
							break;
						case 'y':
							sbCASCommand.append('1');
							break;
						case 'z':
							sbCASCommand.append('2');
							break;

						}
						sbCASCommand.append(']');

						return sbCASCommand.toString();

					} else if (args.get(0).hasCoords()) {
						sbCASCommand.append(ch);
						sbCASCommand.append("coord(");
					} else {
						sbCASCommand.append('(');
						sbCASCommand.append(tpl.printVariableName(ch + ""));
						sbCASCommand.append(")*(");
					}
					handled = true;
				}
			} else
				try {
					Commands c = Commands.valueOf(name);
					if (c != null) {

						kern.setSilentMode(true);
						StringBuilder sb = new StringBuilder(name);
						sb.append('[');
						for (int i = 0; i < args.size(); i++) {
							if (i > 0)
								sb.append(',');
							sb.append(args.get(i).toOutputValueString(
									StringTemplate.defaultTemplate));
						}
						sb.append(']');
						App.debug(sb.toString());
						GeoElement[] ggbResult = kern.getAlgebraProcessor()
								.processAlgebraCommandNoExceptionHandling(
										sb.toString(), false, false, false,
										false);
						kern.setSilentMode(silent);
						if (ggbResult != null && ggbResult.length > 0
								&& ggbResult[0] != null)
							return ggbResult[0].toValueString(tpl);
					}
				} catch (Exception e) {
					kern.setSilentMode(silent);
					Log.info(name + " not known command or function");
				}

			// standard case: add ggbcasvar prefix to name for CAS
			if (!handled) {
				sbCASCommand.append(tpl.printVariableName(name));
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
			if ("Evaluate".equals(name) && args.size() == 1
					&& args.get(0).unwrap() instanceof Command
					&& "Evaluate".equals(((Command) args.get(0).unwrap())
							.getName())) {
				return toString(args.get(0), symbolic, tpl);
			}
			for (int i = 0; i < translation.length(); i++) {
				char ch = translation.charAt(i);
				if (ch == '%') {
					// get number after %
					i++;
					int pos = translation.charAt(i) - '0';
					ExpressionValue ev;
					if (argIsList) {
						ev = ((MyList) args.get(0).getLeft())
								.getListElement(pos);
						sbCASCommand.append(toString(ev, symbolic, tpl));
					} else if (pos >= 0 && pos < args.size()) {
						// success: insert argument(pos)
						ev = args.get(pos);
						sbCASCommand.append(toString(ev, symbolic, tpl));
					} else {
						// failed
						sbCASCommand.append(ch);
						sbCASCommand.append(translation.charAt(i));
					}
				// @ is a hack: only use the value if it does not contain ()
				// to avoid (1,2)' in CAS
				} else if (ch == '@') {
					// get number after %
					i++;
					int pos = translation.charAt(i) - '0';
					if (pos >= 0 && pos < args.size()) {
						// success: insert argument(pos)
						ExpressionValue ev = args.get(pos);
						if (toString(ev, symbolic, tpl).matches("[^(),]*"))
							sbCASCommand.append(toString(ev, symbolic, tpl));
						else
							sbCASCommand.append("x");
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

		if (outsourced) {
			try {
				String retval = App.singularWS.directCommand(sbCASCommand
						.toString());
				if (retval == null || retval.equals("")) {
					// if there was a problem, try again without using Singular:
					return getCASCommand(name, args, symbolic, tpl, false);
				}
				return retval;
			} catch (Throwable e) {
				// try again without Singular:
				return getCASCommand(name, args, symbolic, tpl, false);
			}
		}


		// change variables to y and x for command SolveODE
		if (name.equals("SolveODE")) {
			return switchVarsToSolveODE(args, sbCASCommand);
		} else if (name.equals("Solutions") && args.size() == 1) {
			return switchVarsToSolutions(args, sbCASCommand);
		}

		return sbCASCommand.toString();
	}

	private static String switchVarsToSolutions(ArrayList<ExpressionNode> args,
			StringBuilder sbCASCommand) {
		Set<String> setOfDummyVars = new TreeSet<String>();
		args.get(0).traverse(
				DummyVariableCollector.getCollector(setOfDummyVars));
		String newSbCASCommand = sbCASCommand.toString();
		// equation dependents from one variable
		if (setOfDummyVars.size() == 1) {
			Iterator<String> ite = setOfDummyVars.iterator();
			String var = ite.next();
			// if not x then switch
			if (!var.equals("x")) {
				newSbCASCommand = newSbCASCommand.replaceFirst(",x\\)",
						",ggbtmpvar" + var + ")");
			}
			return newSbCASCommand;
		}
		// equation dependents from more than one variable
		StringBuilder listOfVars = new StringBuilder();
		Iterator<String> ite = setOfDummyVars.iterator();
		// create list of variables
		while (ite.hasNext()) {
			String currVar = ite.next();
			if (!currVar.equals("x") && !currVar.equals("y")) {
				listOfVars.append(",ggbtmpvar" + currVar);
			} else {
				listOfVars.append("," + currVar);
			}
		}

		if (listOfVars.length() > 0) {
			listOfVars = listOfVars.deleteCharAt(0);
			newSbCASCommand = newSbCASCommand.replaceFirst(",x\\)", ",{"
					+ listOfVars.toString() + "})");
		}

		return newSbCASCommand;

	}

	private String switchVarsToSolveODE(final ArrayList<ExpressionNode> args,
			StringBuilder sbCASCommand) {
		Set<String> setOfDummyVars = new TreeSet<String>();
		args.get(0).traverse(
				DummyVariableCollector.getCollector(setOfDummyVars));
		String newSbCASCommand = sbCASCommand.toString();
		newSbCASCommand = newSbCASCommand.replaceAll("unicode39u", "'");
		boolean changed = false;
		Iterator<String> ite = setOfDummyVars.iterator();
		while (ite.hasNext()) {
			String currStr = ite.next();
			if (currStr.contains("'")) {
				String tmp = currStr.split("'")[0];
				if (!tmp.equals("y")) {
					newSbCASCommand = newSbCASCommand.replaceAll("ggbtmpvar"
							+ tmp, "y");
					varSwaps.add(tmp + "->y");
					changed = true;
				}
				setOfDummyVars.remove(currStr);
				setOfDummyVars.remove(currStr + "'");
				setOfDummyVars.remove(tmp);
				if (setOfDummyVars.isEmpty()) {
					return newSbCASCommand;
				}
				break;
			}
		}
		int index = 0;
		ite = setOfDummyVars.iterator();
		while (ite.hasNext() && index < 2) {
			if (changed) {
				String currStr = ite.next();
				if (!currStr.equals("x")) {
					newSbCASCommand = newSbCASCommand.replaceAll("ggbtmpvar"
							+ currStr, "x");
					varSwaps.add(currStr + "->x");
				}
				return newSbCASCommand;
			}
			if (setOfDummyVars.size() == 1 && args.size() == 2) {
				String currStr = ite.next();
				if (!currStr.equals("x") && !currStr.equals("y")) {
					newSbCASCommand = newSbCASCommand.replaceAll("ggbtmpvar"
							+ currStr, "x");
					varSwaps.add(currStr + "->x");
				}
				return newSbCASCommand;
			}
			if (index == 0) {
				String currStr = ite.next();
				if (!currStr.equals("x") && !currStr.equals("y")) {
					newSbCASCommand = newSbCASCommand.replaceAll("ggbtmpvar"
							+ currStr, "y");
					varSwaps.add(currStr + "->y");
					index++;
				}
			} else if (index == 1) {
				String currStr = ite.next();
				if (!currStr.equals("x") && !currStr.equals("y")) {
					newSbCASCommand = newSbCASCommand.replaceAll("ggbtmpvar"
							+ currStr, "x");
					varSwaps.add(currStr + "->x");
					index++;
				}
			}
		}
		return newSbCASCommand;
	}

	final public boolean isCommandAvailable(final Command cmd) {
		StringBuilder sbCASCommand = new StringBuilder();
		sbCASCommand.append(cmd.getName());
		sbCASCommand.append(".");
		sbCASCommand.append(cmd.getArgumentNumber());
		if (casParser.isCommandAvailable(sbCASCommand.toString()))
			return true;
		App.debug("NOT AVAILABLE"+sbCASCommand);
		sbCASCommand.setLength(0);
		sbCASCommand.append(cmd.getName());
		sbCASCommand.append(".N");
		if (casParser.isCommandAvailable(sbCASCommand.toString())) {
			return true;
		}
		return false;
	}

	public boolean isStructurallyEqual(final ValidExpression inputVE,
			final String localizedInput, Kernel kernel) {
		try {
			// current input
			String input1normalized = casParser.toString(inputVE,
					StringTemplate.get(StringType.GEOGEBRA_XML));

			// new input
			ValidExpression ve2 = casParser
					.parseGeoGebraCASInputAndResolveDummyVars(localizedInput,
							kernel, null);
			String input2normalized = casParser.toString(ve2,
					StringTemplate.get(StringType.GEOGEBRA_XML));

			// compare if the parsed expressions are equal
			return input1normalized.equals(input2normalized);
		} catch (Throwable th) {
			App.debug("Invalid selection: " + localizedInput);
			return false;
		}
	}

	public void evaluateGeoGebraCASAsync(final AsynchronousCommand c) {
		getCurrentCAS().evaluateGeoGebraCASAsync(c);
	}

	public Set<String> getAvailableCommandNames() {
		Set<String> cmdSet = new HashSet<String>();
		for (String signature : casParser.getTranslationRessourceBundle()
				.keySet()) {
			String cmd = signature.substring(0, signature.indexOf('.'));
			if (!"Evaluate".equals(cmd)) {
				cmdSet.add(cmd);
			}
		}
		return cmdSet;
	}

	public void clearCache() {
		getPolynomialCoeffsCache.clear();
	}

	public ArrayList<String> getVarSwaps() {
		return varSwaps;
	}

}