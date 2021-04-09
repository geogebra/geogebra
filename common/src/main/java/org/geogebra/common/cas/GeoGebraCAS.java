package org.geogebra.common.cas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.Traversing.DummyVariableCollector;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.GeoSymbolicI;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.util.MaxSizeHashMap;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

/**
 * This class provides an interface for GeoGebra to use an underlying computer
 * algebra system like Giac.
 * @author Markus Hohenwarter
 */
public class GeoGebraCAS implements GeoGebraCasInterface {

	@Weak
	private App app;
	private CASparser casParser;
	private CASGenericInterface cas;

	private ArrayList<String> varSwaps = new ArrayList<>();
	// these variables are cached to gain some speed in getPolynomialCoeffs
	private Map<String, String[]> getPolynomialCoeffsCache = new MaxSizeHashMap<>(
			Kernel.GEOGEBRA_CAS_CACHE_SIZE);
	private StringBuilder getPolynomialCoeffsSB = new StringBuilder();
	private StringBuilder sbPolyCoeffs = new StringBuilder();
	private int counter = 1;

	/**
	 * Creates new CAS interface
	 * @param kernel kernel
	 */
	public GeoGebraCAS(Kernel kernel) {
		app = kernel.getApplication();
		casParser = new CASparser(kernel.getParser(),
				kernel.getApplication().getParserFunctions());

		// DO NOT init underlying CAS here to avoid hanging animation,
		// see TRAC-1398
		// getCurrentCAS();
	}

	@Override
	public CASparser getCASparser() {
		return casParser;
	}

	@Override
	public synchronized CASGenericInterface getCurrentCAS() {
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
	@Override
	public synchronized void initCurrentCAS() {
		if (cas == null) {
			setCurrentCAS();
		}
	}

	@Override
	public synchronized void setCurrentCAS() {
		try {
			cas = getGiac();
			app.getSettings().getCasSettings().addListener(cas);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Giac
	 */
	private synchronized CASGenericInterface getGiac() {
		if (cas == null) {
			cas = app.getCASFactory().newGiac(casParser, app.getKernel());
		}
		return cas;
	}

	@Override
	public String evaluateGeoGebraCAS(ValidExpression casInput,
			MyArbitraryConstant arbconst, StringTemplate tpl, GeoCasCell cell,
			Kernel kernel) throws CASException {
		if (!app.getSettings().getCasSettings().isEnabled()
				&& getCurrentCAS() != null) {
			return "?";
		}
		String result = null;
		CASException exception = null;
		try {
			result = getCurrentCAS().evaluateGeoGebraCAS(casInput, arbconst,
					tpl, cell, kernel);
		} catch (CASException ce) {
			exception = ce;
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
		if (exception != null) {
			throw exception;
		}

		// success
		if (result != null) {
			// get names of escaped global variables right
			// e.g. "ggbcasvar1a" needs to be changed to "a"
			// e.g. "ggbtmpvara" needs to be changed to "a"
			result = Kernel.removeCASVariablePrefix(result, " ");
		}

		return result;
	}

	@Override
	final public String evaluateGeoGebraCAS(String exp,
			MyArbitraryConstant arbconst, StringTemplate tpl, Kernel kernel)
			throws CASException {
		try {
			ValidExpression inVE = casParser.parseGeoGebraCASInput(exp, null);
			String ret = evaluateGeoGebraCAS(inVE, arbconst, tpl, null, kernel);
			if (ret == null) {
				throw new CASException(new Exception(
						Errors.CASGeneralErrorMessage.getError(app.getLocalization())));
			}
			return ret;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new CASException(t);
		}
	}

	@Override
	final public String evaluateRaw(String exp) throws Throwable {
		if (app.getSettings().getCasSettings().isEnabled()) {
			return getCurrentCAS().evaluateRaw(exp);
		}
		return "?";
	}

	/**
	 * Evaluates an expression given in MPReduce/Giac syntax.
	 * @param exp the expression
	 * @return result string (null possible)
	 * @throws CASException if there is a timeout or the expression cannot be evaluated
	 */
	final public String evaluate(String exp) throws CASException {
		if (app.getSettings().getCasSettings().isEnabled()) {
			return getCurrentCAS().evaluateCAS(exp);
		}
		return "?";
	}

	@Override
	final public String[] getPolynomialCoeffs(final String polyExpr,
			final String variable) {
		getPolynomialCoeffsSB.setLength(0);
		getPolynomialCoeffsSB.append(polyExpr);
		getPolynomialCoeffsSB.append(',');
		getPolynomialCoeffsSB.append(variable);

		String[] result = getPolynomialCoeffsCache
				.get(getPolynomialCoeffsSB.toString());
		if (result != null) {
			return result.length == 0 ? null : result;
		}

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

			// not a polynomial -- cache
			if ("{}".equals(tmp)) {
				getPolynomialCoeffsCache.put(getPolynomialCoeffsSB.toString(),
						new String[0]);
				return null;
			}
			// invalid output -- don't cache
			if (CASgiac.isUndefined(tmp)) {
				return null;
			}

			// not a polynomial: result still includes the variable, e.g. "x"
			if (tmp.indexOf(variable) >= 0) {
				return null;
			}

			// get names of escaped global variables right
			// e.g. "ggbcasvara" needs to be changed to "a"
			tmp = Kernel.removeCASVariablePrefix(tmp);

			tmp = tmp.substring(1, tmp.length() - 1); // strip '{' and '}'
			result = tmp.split(",");

			getPolynomialCoeffsCache.put(getPolynomialCoeffsSB.toString(),
					result);
			return result;
		} catch (Throwable e) {
			Log.debug("GeoGebraCAS.getPolynomialCoeffs(): " + e.getMessage());
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
			return ExpressionNode.getLabelOrDefinition(ev, tpl);
		}

		return ev.toValueString(tpl);
	}

	@Override
	final synchronized public String getCASCommand(final String name,
			final ArrayList<ExpressionNode> args, boolean symbolic,
			StringTemplate tpl, SymbolicMode mode) {
		return getCASCommand(name, args, symbolic, tpl, true, mode);
	}

	final synchronized private String getCASCommand(final String name,
			final ArrayList<ExpressionNode> args, boolean symbolic,
			StringTemplate tpl, boolean allowOutsourcing,
			SymbolicMode symbolicMode) {
		// check if completion of variable list is needed
		boolean paramEquExists = checkForParamEquExistance(args, name);
		// check if list of vars needs completion
		boolean varComplNeeded = false;
		String complOfVarsStr = "";

		if (paramEquExists) {
			// store nr of variables from input
			if (args.get(1).getLeft() instanceof MyList) {
				casParser.setNrOfVars(((MyList) args.get(1).getLeft()).size());
			} else {
				casParser.setNrOfVars(1);
			}

			// set of variables in list of equations
			Set<String> varsInEqus = new HashSet<>();
			// set of variables in list of variables
			Set<String> vars = new HashSet<>();
			// get list of equations
			MyList listOfEqus = (MyList) args.get(0).getLeft();
			for (int i = 0; i < listOfEqus.size(); i++) {
				// get variables of current equation
				HashSet<GeoElement> varsInCurrEqu = listOfEqus.getListElement(i)
						.getVariables(symbolicMode);
				// add to set of vars form equations
				for (GeoElement geo : varsInCurrEqu) {
					varsInEqus
							.add(geo.toString(StringTemplate.defaultTemplate));
				}
			}
			// case we have list of vars in input as second argument
			if (args.get(1).getLeft() instanceof MyList) {
				MyList listOfVars = (MyList) args.get(1).getLeft();
				// collect vars from input list of vars
				for (int i = 0; i < listOfVars.size(); i++) {
					vars.add(listOfVars.getItem(i)
							.toString(StringTemplate.defaultTemplate));
				}
			}
			// case input list of vars was one variable
			else {
				vars.add(args.get(1).getLeft()
						.toString(StringTemplate.defaultTemplate));
			}
			// set of vars from equations, but unknown from list of vars
			varsInEqus.removeAll(vars);
			// case the nr of variables was already nr of vars in equations
			if (varsInEqus.isEmpty()) {
				casParser.setNrOfVars(0);
			}
			for (String str : varsInEqus) {
				complOfVarsStr += "," + addCASPrefix(str);

				// get equation of current variable
				ValidExpression node = app.getKernel().getConstruction()
						.geoCeListLookup(str);
				// get variables of obtained equation
				HashSet<GeoElement> varsFromEquOfCurrVars = node == null
						? new HashSet<>()
						: node.getVariables(symbolicMode);
				HashSet<String> stringVarsFromEquOfCurrVars = new HashSet<>(
						varsFromEquOfCurrVars.size());
				// collect labels of variables from obtained equation
				for (GeoElement geo : varsFromEquOfCurrVars) {
					String geoStr = geo
							.toString(StringTemplate.defaultTemplate);
					if (!geoStr.equals(str)) {
						stringVarsFromEquOfCurrVars.add(
								geo.toString(StringTemplate.defaultTemplate));
					}
				}
				// we need only the dependent variables of the current
				// equation
				stringVarsFromEquOfCurrVars.removeAll(vars);
				// the current equation depends only on the input variable
				// list
				if (stringVarsFromEquOfCurrVars.isEmpty()) {
					varComplNeeded = true;
				}
				// we found unknown variable
				else {
					varComplNeeded = false;
					Log.debug(str + " contains unknown variable");
					break;
				}
			}
		}

		// boolean argIsList = false;
		boolean isAssumeInEqus = false;
		boolean skipEqu = false;
		MyList equsForArgs = new MyList(this.app.getKernel());
		StringBuilder assumesForArgs = new StringBuilder();

		StringBuilder sbCASCommand = new StringBuilder(80);
		// build command key as name + "." + args.size()
		sbCASCommand.append(name);
		sbCASCommand.append(".");

		if (args.size() == 1 && "Point".equals(name)) {
			updateArgsAndSbForPoint(args, sbCASCommand);
		} else if (args.size() == 1 && "Area".equals(name)) {
			updateArgsAndSbForArea(args, sbCASCommand, app.getKernel());
		} else if (args.size() == 2 && "Intersect".equals(name)) {
			updateArgsAndSbForIntersect(args, sbCASCommand);
		}
		// case solve with list of equations
		else if ("Solve".equals(name) && args.size() == 2
				&& args.get(0).unwrap() instanceof MyList && !varComplNeeded) {
			// get list of equations from args
			MyList listOfEqus = (MyList) args.get(0).unwrap();
			// case Solve[ <List of Equations>, <List of Variables> ]
			if (args.get(1).unwrap() instanceof MyList) {
				// get list of parameters
				MyList listOfVars = (MyList) args.get(1).unwrap();
				for (int k = 0; k < listOfEqus.size(); k++) {
					// get vars of current equation

					// 2 = 2 should be handled as equation, not assumption
					boolean contains = isEquation(listOfEqus.getListElement(k),
							listOfVars);
					boolean linear = false;
					// check if equation can be used with assume
					if (!contains) {
						linear = isLinear(listOfEqus.getListElement(k),
								symbolicMode);
					}

					// if contains other vars as parameters
					// that means that the current equation is an assumption
					if (!contains && linear) {
						if (!isAssumeInEqus) {
							isAssumeInEqus = true;
							// call Solve.3
							sbCASCommand.append(3);
						}
						// add current equation to assumptions
						ExpressionValue ev = listOfEqus.getListElement(k);
						assumesForArgs.append(toString(ev, symbolic, tpl));
						assumesForArgs.append("),assume(");
					}
					// we found an equation which should be solved
					else if (contains) {
						// add current equation to list of equations
						ExpressionValue ev = listOfEqus.getListElement(k);
						equsForArgs.addListElement(ev);
					}
				}
				if (!isAssumeInEqus
						&& listOfEqus.size() != equsForArgs.size()) {
					skipEqu = true;
				}
			}
			// case Solve[ <List of Equations>, <Variable> ]
			else if (args.get(1).unwrap() instanceof GeoDummyVariable) {
				// get parameter
				GeoDummyVariable var = (GeoDummyVariable) args.get(1).unwrap();
				for (int k = 0; k < listOfEqus.size(); k++) {
					// get current equation
					HashSet<GeoElement> varsInEqu = listOfEqus.getListElement(k)
							.getVariables(symbolicMode);
					Iterator<GeoElement> it = varsInEqu.iterator();
					boolean contains = false;
					// check if current equation contains only var which is not
					// the parameter
					while (it.hasNext()) {
						GeoElement currVar = it.next();
						if (currVar.toString(StringTemplate.defaultTemplate)
								.equals(var.toString(
										StringTemplate.defaultTemplate))) {
							contains = true;
							break;
						}
					}
					boolean linear = false;
					// check if we could use equation with assume
					if (!contains) {
						linear = isLinear(listOfEqus.getItem(k), symbolicMode);
					}
					// the current equation is an assumption
					if (!contains && linear) {
						if (!isAssumeInEqus) {
							isAssumeInEqus = true;
							// call Solve.3
							sbCASCommand.append(3);
						}
						// add current equation to assumptions
						ExpressionValue ev = listOfEqus.getListElement(k);
						assumesForArgs.append(toString(ev, symbolic, tpl));
						assumesForArgs.append("),assume(");
					}
					// the current equation is an equation which should be
					// solved
					else if (contains) {
						// add current equation to the list of equations
						ExpressionValue ev = listOfEqus.getListElement(k);
						equsForArgs.addListElement(ev);
					}
				}
				if (!isAssumeInEqus
						&& listOfEqus.size() != equsForArgs.size()) {
					skipEqu = true;
				}
			}
		}
		// add eg '3'
		else {
			sbCASCommand.append(args.size());
		}

		// remove unwanted part from list of assumptions
		// remove: ",assume("
		if (isAssumeInEqus) {
			assumesForArgs.setLength(assumesForArgs.length() - 9);
		}
		// if nr of arguments wasn't appended, append it
		else if (sbCASCommand.toString().equals("Solve.")) {
			sbCASCommand.append(args.size());
		}

		boolean outsourced = false;
		String translation = null;
		// check if there is support in the outsourced CAS (now SingularWS) for
		// this command:
		if (allowOutsourcing && app.getSingularWS() != null
				&& app.singularWSisAvailable()) {
			translation = app
					.singularWSgetTranslatedCASCommand(sbCASCommand.toString());
			if (translation != null) {
				outsourced = true;
			}
		}

		// get translation ggb -> Giac
		if (!outsourced) {
			translation = translateCommandSignature(sbCASCommand.toString());
		}

		// Try .N translation
		if (translation == null) {
			translation = getVarargTranslation(sbCASCommand, name, args, symbolic, tpl);
			if (translation != null) {
				return translation;
			}
		}

		sbCASCommand.setLength(0);

		// use key as function name
		if (translation == null) {
			Kernel kern = app.getKernel();

			// convert command names x, y, z to xcoord, ycoord, ycoord to
			// protect it in CAS see TRAC-1283
			boolean handled = false;
			if (name.length() == 1) {
				char ch = name.charAt(0);
				if (ch == 'x' || ch == 'y' || ch == 'z') {
					if (args.get(0).evaluatesToList()) {

						sbCASCommand
								.append(toString(args.get(0), symbolic, tpl));
						sbCASCommand.append('[');
						sbCASCommand.append(ch - 'x');
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
			} else {
				GeoElementND ggbResult = computeWithGGB(kern, name, args);
				if (ggbResult != null) {
					return ggbResult.toValueString(tpl);
				}
			}

			// standard case: add ggbcasvar prefix to name for CAS
			if (!handled) {
				// sbCASCommand.append("re(");
				sbCASCommand.append(tpl.printVariableName(name));
				sbCASCommand.append('(');
			}
			for (int i = 0; i < args.size(); i++) {
				ExpressionValue ev = args.get(i);
				sbCASCommand.append(toString(ev, symbolic, tpl));
				sbCASCommand.append(',');
			}
			sbCASCommand.setCharAt(sbCASCommand.length() - 1, ')');
			if (!handled) {
				// sbCASCommand.append(")");
			}
		}

		// translation found:
		// replace %0, %1, etc. in translation by command arguments
		else {
			if ("Evaluate".equals(name) && args.size() == 1
					&& args.get(0).unwrap() instanceof Command
					&& "Evaluate".equals(
					((Command) args.get(0).unwrap()).getName())) {
				return toString(args.get(0), symbolic, tpl);
			}
			for (int i = 0; i < translation.length(); i++) {
				char ch = translation.charAt(i);

				StringTemplate tplToUse = tpl;

				if (ch == '%') {

					if (translation.charAt(i + 1) == '%') {
						// eg %%0
						// numeric = true;
						tplToUse = tpl.deriveNumericGiac();
						i++;
					}

					// get number after %
					i++;
					int pos = translation.charAt(i) - '0';
					ExpressionValue ev;
					if ("Solve".equals(name)) {
						// case we have assumptions in equation list
						if (isAssumeInEqus && args.size() != 3) {
							// append list of equations
							if (pos == 0) {
								sbCASCommand.append(toString(equsForArgs,
										symbolic, tplToUse));
							}
							// append list of assumptions
							else if (pos == 2) {
								sbCASCommand.append(assumesForArgs.toString());
							}
							// append list of variables
							else {
								ev = args.get(pos);
								sbCASCommand.append(
										toString(ev, symbolic, tplToUse));
							}
						} else if (pos == 2 && args.size() == 3
								&& args.get(2).getLeft() instanceof MyList) {
							// case solve with list of assumptions
							// append assume for each assumption
							MyList list = (MyList) args.get(2).getLeft();
							for (int k = 0; k < list.size(); k++) {
								ev = list.getItem(k);
								sbCASCommand.append(
										toString(ev, symbolic, tplToUse));
								sbCASCommand.append("),assume(");
							}
							sbCASCommand.setLength(sbCASCommand.length() - 9);
						} else if (pos >= 0 && pos < args.size()) {
							if (skipEqu && pos == 0) {
								ev = equsForArgs;
							} else {
								ev = args.get(pos);
							}
							// we need completion of variable list
							if (varComplNeeded && pos == 1) {
								String listOfVars = toString(ev, symbolic,
										tplToUse);
								if (!listOfVars.startsWith("{")) {
									// add { with the defined vars by user
									sbCASCommand.append("{");
									sbCASCommand.append(listOfVars);
								} else {
									// add defined vars by user
									sbCASCommand.append(listOfVars);
								}
								// skip unneeded }
								if (listOfVars.endsWith("}")) {
									sbCASCommand.setLength(
											sbCASCommand.length() - 1);
								}
								// add completion of list of vars
								sbCASCommand.append(complOfVarsStr);
								sbCASCommand.append("}");
							} else {
								sbCASCommand.append(
										toString(ev, symbolic, tplToUse));
							}
						}
					} else if (pos >= 0 && pos < args.size()) {
						// success: insert argument(pos)
						ev = args.get(pos);
						// needed for #5506
						if ("SolveODE".equals(name)
								&& ((ExpressionNode) ev)
								.getLeft() instanceof MyList
								&& args.size() > 2) {
							sbCASCommand.append(toString(
									((MyList) (args.get(pos).getLeft()))
											.getListElement(0),
									symbolic, tplToUse));
						} else {

							sbCASCommand
									.append(toString(ev, symbolic, tplToUse));

						}
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
						if (toString(ev, symbolic, tplToUse).matches("[^(),]*")) {
							sbCASCommand
									.append(toString(ev, symbolic, tplToUse));
						} else {
							sbCASCommand.append("x");
						}
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
				String retval = app
						.singularWSdirectCommand(sbCASCommand.toString());
				if (retval == null || "".equals(retval)) {
					// if there was a problem, try again without using Singular:
					return getCASCommand(name, args, symbolic, tpl, false,
							symbolicMode);
				}
				return retval;
			} catch (Throwable e) {
				// try again without Singular:
				return getCASCommand(name, args, symbolic, tpl, false,
						symbolicMode);
			}
		}

		// change variables to y and x for command SolveODE
		if ("SolveODE".equals(name) && args.size() >= 2) {
			return sbCASCommand.toString().replaceAll("unicode39u", "\'");
			// return switchVarsToSolveODE(args, sbCASCommand);
		} else if ("Solutions".equals(name) && args.size() == 1) {
			return switchVarsToSolutions(args, sbCASCommand);
		}

		return sbCASCommand.toString();
	}

	private String getVarargTranslation(StringBuilder builder, String name,
			ArrayList<ExpressionNode> args, boolean symbolic,
			StringTemplate tpl) {
		// build command key as name + ".N"
		builder.setLength(0);
		builder.append(name);
		builder.append(".N");
		String translation = casParser
				.getTranslatedCASCommand(builder.toString());

		boolean isZipCommand = "Zip".equals(name);

		// check for eg Sum.N=sum(%)
		if (translation != null) {
			builder.setLength(0);
			for (int i = 0; i < translation.length(); i++) {
				char ch = translation.charAt(i);
				if (ch == '%') {
					appendArg(builder, args, symbolic, tpl, isZipCommand);
				} else {
					builder.append(ch);
				}
			}
			return builder.toString();
		}
		return null;
	}

	private void appendArg(StringBuilder builder, ArrayList<ExpressionNode> args,
			boolean symbolic, StringTemplate tpl, boolean isZipCommand) {
		if (args.size() == 1) { // might be a list as the argument
			ExpressionValue ev = args.get(0).unwrap();
			String arg = toString(ev, symbolic, tpl);
			builder.append(arg);
		} else {
			CASGenericInterface cas = getCurrentCAS();
			cas.appendListStart(builder);
			for (int j = 0; j < args.size(); j++) {
				ExpressionValue ev = args.get(j);

				// wrap expression in '...' to stop premature
				// evaluation
				// eg Zip(Mod(k, 2), k,{0, -2, -5, 1, -2, -4, 0, 4,
				// 12})
				if (isZipCommand && j == 0) {
					builder.append("'");
				}
				builder.append(toString(ev, symbolic, tpl));
				if (isZipCommand && j == 0) {
					builder.append("'");
				}
				builder.append(',');
			}
			// remove last comma
			builder.setLength(builder.length() - 1);
			cas.appendListEnd(builder);
		}
	}

	/**
	 * variables like i, e have a special meaning in Giac so all GeoGebra
	 * variables need "ggbtmpvar" as a prefix
	 * @param str variable name
	 * @return whether str should have "ggbtmpvar" on the front
	 */
	public static boolean needsTmpPrefix(String str) {
		return !("x".equals(str) || "y".contentEquals(str) || "y'".contentEquals(str)
				|| "y''".contentEquals(str) || "z".equals(str)
				|| str.startsWith(Kernel.TMP_VARIABLE_PREFIX));
	}

	// add ggbtmpvar as prefix if necessary
	private static String addCASPrefix(String str) {
		return needsTmpPrefix(str) ? Kernel.TMP_VARIABLE_PREFIX + str : str;
	}

	private static void updateArgsAndSbForPoint(ArrayList<ExpressionNode> args,
			StringBuilder sbCASCommand) {
		ExpressionValue node = args.get(0).unwrap();
		if (node instanceof MyList) {
			if (((MyList) node).getListElement(0).wrap().getLeft()
					.isNumberValue()) {
				sbCASCommand.append(1);
			} else {
				int size = ((MyList) node).size();
				sbCASCommand.append(size);
				args.clear();
				for (int i = 0; i < size; i++) {
					args.add(((MyList) node).getListElement(i).wrap());
				}
			}
		}

	}

	private static void updateArgsAndSbForArea(ArrayList<ExpressionNode> args,
			StringBuilder sbCASCommand, Kernel kernel) {
		ExpressionValue node = args.get(0).unwrap();
		if (node instanceof EquationValue) {
			sbCASCommand.append(1);
		} else {
			sbCASCommand.setLength(0);
			Log.debug(args.get(0));
			GeoElementND newArg = computeWithGGB(kernel, "Area", args);
			args.clear();
			args.add(newArg.wrap());
			sbCASCommand.append("Evaluate.1");
		}
	}

	private static void updateArgsAndSbForIntersect(
			ArrayList<ExpressionNode> args, StringBuilder sbCASCommand) {
		for (int i = 0; i < 2; i++) {
			ExpressionValue a1 = args.get(i).unwrap();
			if (a1 instanceof GeoPlaneND) {
				args.set(i, asPlane(a1, ((GeoPlaneND) a1).getKernel()));
			}
		}
		sbCASCommand.setLength(0);
		sbCASCommand.append("Intersect.2");
	}

	private static ExpressionNode asPlane(ExpressionValue a1, Kernel kernel) {
		Command cmd = new Command(kernel, "Plane", false);
		cmd.addArgument(a1.wrap());
		return cmd.wrap();
	}

	private static GeoElementND computeWithGGB(Kernel kern, String name,
			ArrayList<ExpressionNode> args) {
		boolean silent = kern.isSilentMode();
		boolean suppressLabels = kern.getConstruction().isSuppressLabelsActive();
		try {
			Commands c = Commands.valueOf(name);
			if (c != null) {

				kern.setSilentMode(true);
				StringBuilder sb = new StringBuilder(name);
				sb.append('[');
				for (int i = 0; i < args.size(); i++) {
					if (i > 0) {
						sb.append(',');
					}
					if (args.get(i).unwrap().isGeoElement()) {
						sb.append(((GeoElement) args.get(i).unwrap())
								.getLabel(StringTemplate.defaultTemplate));
					} else {
						sb.append(args.get(i).toOutputValueString(
								StringTemplate.maxPrecision));
					}
				}
				sb.append(']');

				String command = sb.toString();
				AlgebraProcessor processor = kern.getAlgebraProcessor();
				EvalInfo info = new EvalInfo(false, true)
						.withSliders(false)
						.addDegree(false)
						.withSymbolicMode(SymbolicMode.NONE);
				GeoElementND[] ggbResult = null;
				try {
					ggbResult = processor.processAlgebraCommandNoExceptionHandling(command, false,
							ErrorHelper.silent(), info, null);
				} catch (Exception e) {
					// ignore
				}
				if (ggbResult != null && ggbResult.length > 0
						&& ggbResult[0] != null) {
					return ggbResult[0];
				}
			}
		} catch (Exception e) {
			Log.info(name + " not known command or function");
		} finally {
			kern.setSilentMode(silent);
			kern.getConstruction().setSuppressLabelCreation(suppressLabels);
		}
		return null;
	}

	@Override
	public String translateCommandSignature(String string) {
		String translation = casParser.getTranslatedCASCommand(string);
		if (translation != null) {
			translation = translation.replaceAll("arg0", "arg0" + counter);
			translation = translation.replaceAll("arg1", "arg1" + counter);
			counter++;
		}
		return translation;
	}

	private static boolean isLinear(ExpressionValue listElement,
			SymbolicMode mode) {
		if (listElement.isExpressionNode() && ((ExpressionNode) listElement)
				.getLeft() instanceof Equation) {
			Equation equation = (Equation) ((ExpressionNode) listElement)
					.getLeft();
			HashSet<GeoElement> vars = equation
					.getVariables(mode);
			equation.initEquation();
			// assume can accept only equation in first degree and with one
			// variable
			if (equation.degree() == 1 && vars.size() == 1) {
				return true;
			}
		}
		return false;
	}

	// method to check if we should make completion of variable list
	private static boolean checkForParamEquExistance(
			ArrayList<ExpressionNode> args, String name) {
		// case we have command Solve[<Equation list>, <Variable list>]
		if ("Solve".equals(name) && args.size() == 2) {
			if (args.get(0).getLeft() instanceof MyList
					&& (args.get(1).getLeft() instanceof MyList || args.get(1)
					.getLeft() instanceof GeoDummyVariable)) {
				// list of equations
				MyList listOfEquations = (MyList) args.get(0).getLeft();
				// analyze if first equation is a parametric equation
				if (listOfEquations.getItem(0).isExpressionNode()
						&& ((ExpressionNode) listOfEquations.getItem(0))
						.getLeft() instanceof Equation) {
					Equation equation = (Equation) ((ExpressionNode) listOfEquations
							.getItem(0)).getLeft();
					if (equation.getLHS().evaluatesTo3DVector()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static boolean isEquation(ExpressionValue listElement,
			MyList listOfVars) {

		ExpressionValue ev = listElement.unwrap();

		// eg Solve({c,d},{x,y})
		// where c,d are Algebra View objects
		if (ev instanceof GeoConicND || ev instanceof GeoLineND) {
			if (listOfVars.size() == 2) {
				String var0 = listOfVars.getListElement(0)
						.toValueString(StringTemplate.defaultTemplate);
				String var1 = listOfVars.getListElement(1)
						.toValueString(StringTemplate.defaultTemplate);

				// {x,y}
				// {y,x} not checked
				return "x".equals(var0) && "y".equals(var1);

			}
			return false;
		} else if (ev instanceof GeoQuadricND) {
			if (listOfVars.size() == 3) {
				String var0 = listOfVars.getListElement(0)
						.toValueString(StringTemplate.defaultTemplate);
				String var1 = listOfVars.getListElement(1)
						.toValueString(StringTemplate.defaultTemplate);
				String var2 = listOfVars.getListElement(2)
						.toValueString(StringTemplate.defaultTemplate);

				// {x,y,z}
				// other orders eg {x,z,y} not checked
				return "x".equals(var0) && "y".equals(var1) && "z".equals(var2);

			}
			return false;
		}

		ExpressionValue variableContainer = listElement.unwrap();
		if (variableContainer instanceof GeoSymbolic) {
			variableContainer = ((GeoSymbolic) variableContainer).getValue();
		}

		return containsExpressionVariablesOrFunctionVariablesFromList(
				variableContainer, listOfVars)
				|| variableContainer.isConstant();
	}

	private static boolean containsExpressionVariablesOrFunctionVariablesFromList(
			ExpressionValue expression, MyList listOfVariables) {
		ValidExpression validExpression =
				expression instanceof ValidExpression ? (ValidExpression) expression : null;
		HashSet<GeoElement> variablesInExpression =
				expression.getVariables(SymbolicMode.SYMBOLIC);
		for (int i = 0; i < listOfVariables.size(); i++) {
			String labelOfVariableFromList = getLabel(listOfVariables.getListElement(i));
			if (containsFunctionVariable(validExpression, labelOfVariableFromList)) {
				return true;
			}
			if (containsVariable(variablesInExpression, labelOfVariableFromList)) {
				return true;
			}
		}
		return false;
	}

	private static String getLabel(ExpressionValue expression) {
		return expression.toString(StringTemplate.defaultTemplate);
	}

	private static boolean containsFunctionVariable(
			ValidExpression validExpression, String labelOfVariable) {
		return validExpression != null && validExpression.containsFunctionVariable(labelOfVariable);
	}

	private static boolean containsVariable(Set<GeoElement> variables, String labelOfVariable) {
		if (variables == null) {
			return false;
		}
		for (GeoElement var : variables) {
			if (labelOfVariable.equals(getLabel(var))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * change Solutions({x+y=1, x-y=3}) to Solutions({x+y=1, x-y=3},{x,y})
	 */
	private static String switchVarsToSolutions(ArrayList<ExpressionNode> args,
			StringBuilder sbCASCommand) {

		// eg Solutions(x^2=a)
		// not Solutions({x+y=1, x-y=3})
		if (args.get(0).unwrap() instanceof Equation) {
			return sbCASCommand.toString();
		}

		Set<String> setOfDummyVars = new TreeSet<>();
		args.get(0)
				.traverse(DummyVariableCollector.getCollector(setOfDummyVars));
		String newSbCASCommand = sbCASCommand.toString();
		// equation dependents from one variable
		if (setOfDummyVars.size() == 1) {
			Iterator<String> ite = setOfDummyVars.iterator();
			String var = ite.next();
			// if not x then switch
			if (!"x".equals(var)) {
				newSbCASCommand = newSbCASCommand.replaceFirst(",x\\)",
						"," + Kernel.TMP_VARIABLE_PREFIX + var + ")");
			}
			return newSbCASCommand;
		}
		// equation dependents from more than one variable
		StringBuilder listOfVars = new StringBuilder();
		Iterator<String> ite = setOfDummyVars.iterator();
		// create list of variables
		while (ite.hasNext()) {
			String currVar = ite.next();
			listOfVars.append(",");
			if (needsTmpPrefix(currVar)) {
				listOfVars.append(Kernel.TMP_VARIABLE_PREFIX);
			}
			listOfVars.append(currVar);
		}

		if (listOfVars.length() > 0) {
			listOfVars.deleteCharAt(0);
			newSbCASCommand = newSbCASCommand.replaceFirst(",x\\)",
					",{" + listOfVars.toString() + "})");
		}

		return newSbCASCommand;

	}

	@Override
	final public boolean isCommandAvailable(final Command cmd) {
		StringBuilder sbCASCommand = new StringBuilder();
		sbCASCommand.append(cmd.getName());
		sbCASCommand.append(".");
		sbCASCommand.append(cmd.getArgumentNumber());
		if (casParser.isCommandAvailable(sbCASCommand.toString())) {
			return true;
		}
		Log.debug("NOT AVAILABLE" + sbCASCommand);
		sbCASCommand.setLength(0);
		sbCASCommand.append(cmd.getName());
		sbCASCommand.append(".N");
		if (casParser.isCommandAvailable(sbCASCommand.toString())) {
			return true;
		}
		return false;
	}

	@Override
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
			Log.debug("Invalid selection: " + localizedInput);
			return false;
		}
	}

	@Override
	public Set<String> getAvailableCommandNames() {
		Set<String> cmdSet = new HashSet<>();
		for (String signature : casParser.getTranslationRessourceBundle()
				.keySet()) {
			String cmd = signature.substring(0, signature.indexOf('.'));
			if (!"Evaluate".equals(cmd)) {
				cmdSet.add(cmd);
			}
		}
		return cmdSet;
	}

	@Override
	public void clearCache() {
		getPolynomialCoeffsCache.clear();
	}

	/**
	 * @return swaps in form a -&gt; b
	 */
	public ArrayList<String> getVarSwaps() {
		return varSwaps;
	}

	@Override
	public ValidExpression parseOutput(String inValue, GeoSymbolicI geoCasCell,
			Kernel kernel) {
		try {
			ValidExpression expression = (kernel.getGeoGebraCAS()).getCASparser()
					.parseGeoGebraCASInputAndResolveDummyVars(inValue, kernel,
							geoCasCell);
			expression.traverse(Traversing.GgbVectRemover.getInstance());
			return expression;
		} catch (CASException c) {
			geoCasCell.setError(c.getKey());
			return null;
		} catch (Throwable e) {
			return null;
		}
	}

	@Override
	public synchronized void clearResult() {
		if (cas != null) {
			cas.clearResult();
		}
	}
}