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
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.Traversing.DummyVariableCollector;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
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
		if (app.isExam() && !app.getExam().isCASAllowed()) {
			return "?";
		}
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
		if (!app.isExam() || app.getExam().isCASAllowed()) {
			return getCurrentCAS().evaluateRaw(exp);
		}
		return "?";
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
		if (!app.isExam() || app.getExam().isCASAllowed()) {
			return getCurrentCAS().evaluateCAS(exp);
		}
		return "?";
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

		// check if completion of variable list is needed
		boolean paramEquExists = checkForParamEquExistance(args, name);
		// check if list of vars needs completion
		boolean varComplNeeded = false;
		String complOfVarsStr = "";
		// fix for GGB-134
		app.getKernel().setResolveUnkownVarsAsDummyGeos(true);
		if (paramEquExists) {
			// store nr of variables from input
			if (args.get(1).getLeft() instanceof MyList) {
				casParser.setNrOfVars(((MyList) args.get(1).getLeft()).size());
			} else {
				casParser.setNrOfVars(1);
			}
			// set of variables in list of equations
			Set<String> varsInEqus = new HashSet<String>();
			// set of variables in list of variables
			Set<String> vars = new HashSet<String>();
			// get list of equations
			MyList listOfEqus = (MyList) args.get(0).getLeft();
			for (int i = 0; i < listOfEqus.size(); i++) {
				// get variables of current equation
				HashSet<GeoElement> varsInCurrEqu = listOfEqus
							.getListElement(i).getVariables();
				// add to set of vars form equations
				for (GeoElement geo : varsInCurrEqu) {
					varsInEqus
							.add(geo
								.toString(StringTemplate.defaultTemplate));
				}
			}
			// case we have list of vars in input as second argument
			if (args.get(1).getLeft() instanceof MyList) {
				MyList listOfVars = (MyList) args.get(1).getLeft();
				// collect vars from input list of vars
				for (int i = 0; i < listOfVars.size(); i++) {
					vars.add(listOfVars.getItem(i).toString(
								StringTemplate.defaultTemplate));
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
				if (!str.equals("x") && !str.equals("y")
							&& !str.equals("z")) {
					// add current variable to the completion string
					complOfVarsStr += ",ggbtmpvar" + str;
				} else {
					complOfVarsStr += ", " + str;
				}
				// get equation of current variable
				ValidExpression node = app.getKernel().getConstruction()
							.geoCeListLookup(str);
				// get variables of obtained equation
				HashSet<GeoElement> varsFromEquOfCurrVars = node == null ? new HashSet<GeoElement>()
							: node
							.getVariables();
				HashSet<String> stringVarsFromEquOfCurrVars = new HashSet<String>(
							varsFromEquOfCurrVars.size());
				// collect labels of variables from obtained equation
				for (GeoElement geo : varsFromEquOfCurrVars) {
					String geoStr = geo
								.toString(StringTemplate.defaultTemplate);
					if (!geoStr.equals(str)) {
							stringVarsFromEquOfCurrVars.add(geo
									.toString(StringTemplate.defaultTemplate));
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
					App.debug(str + " contains unknown variable");
					break;
				}
			}
		}

		boolean argIsList = false;
		boolean isAssumeInEqus = false;
		MyList equsForArgs = new MyList(this.app.getKernel());
		StringBuilder assumesForArgs = new StringBuilder();
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
		}
		// case solve with list of equations
		else if (name.equals("Solve") && args.size() == 2
				&& args.get(0).getLeft() instanceof MyList && !varComplNeeded) {
			// get list of equations from args
			MyList listOfEqus = (MyList) args.get(0).getLeft();
			// case Solve[ <List of Equations>, <List of Variables> ]
			if (args.get(1).getLeft() instanceof MyList) {
				// get list of parameters
				MyList listOfVars = (MyList) args.get(1).getLeft();
				for (int k = 0; k < listOfEqus.size(); k++) {
					// get vars of current equation

					// 2 = 2 should be handled as equation, not assumption
					boolean contains = isEquation(listOfEqus.getListElement(k),
							listOfVars);

					// if contains other vars as parameters
					// that means that the current equation is an assumption
					if (!contains) {
						if (!isAssumeInEqus) {
							isAssumeInEqus = true;
							// call Solve.3
							sbCASCommand.append(3);
						}
						// add current equation to assumptions
						ExpressionValue ev = listOfEqus.getListElement(k);
						assumesForArgs.append(toString(ev, symbolic, tpl)
								+ "),assume(");
					}
					// we found an equation which should be solved
					else {
						// add current equation to list of equations
						ExpressionValue ev = listOfEqus.getListElement(k);
						equsForArgs.addListElement(ev);
					}
				}
			}
			// case Solve[ <List of Equations>, <Variable> ]
			else if (args.get(1).getLeft() instanceof GeoDummyVariable) {
				// get parameter
				GeoDummyVariable var = (GeoDummyVariable) args.get(1).getLeft();
				for (int k=0;k<listOfEqus.size();k++) {
					// get current equation
					HashSet<GeoElement> varsInEqu = listOfEqus
							.getListElement(k).getVariables();
					Iterator<GeoElement> it = varsInEqu.iterator();
					boolean contains = false;
					// check if current equation contains only var which is not
					// the parameter
					while (it.hasNext()) {
						GeoElement currVar = it.next();
						if (currVar
								.toString(StringTemplate.defaultTemplate)
								.equals(var
										.toString(StringTemplate.defaultTemplate))) {
							contains = true;
							break;
						}
					}
					// the current equation is an assumption
					if (!contains) {
						if (!isAssumeInEqus) {
							isAssumeInEqus = true;
							// call Solve.3
							sbCASCommand.append(3);
						}
						// add current equation to assumptions
						ExpressionValue ev = listOfEqus.getListElement(k);
						assumesForArgs.append(toString(ev, symbolic, tpl)
								+ "),assume(");
					}
					// the current equation is an equation which should be
					// solved
					else {
						// add current equation to the list of equations
						ExpressionValue ev = listOfEqus.getListElement(k);
						equsForArgs.addListElement(ev);
					}
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
					} else if (name.equals("Solve")) {
						// case we have assumptions in equation list
						if (isAssumeInEqus && args.size() != 3) {
							// append list of equations
							if (pos == 0) {
								sbCASCommand.append(toString(equsForArgs,
										symbolic, tpl));
							}
							// append list of assumptions
							else if (pos == 2) {
								sbCASCommand.append(assumesForArgs.toString());
							}
							// append list of variables
							else {
								ev = args.get(pos);
								sbCASCommand
										.append(toString(ev, symbolic, tpl));
							}
						}
						else if (pos == 2
							&& args.size() == 3
							&& args.get(2).getLeft() instanceof MyList) {
						// case solve with list of assumptions
						// append assume for each assumption
						MyList list = (MyList) args.get(2).getLeft();
						for (int k = 0; k < list.size(); k++) {
							ev = list.getItem(k);
							sbCASCommand.append(toString(ev, symbolic, tpl));
							sbCASCommand.append("),assume(");
						}
						sbCASCommand.setLength(sbCASCommand.length() - 9);
						} else if (pos >= 0 && pos < args.size()) {
							ev = args.get(pos);
							// we need completion of variable list
							if (varComplNeeded && pos == 1) {
								String listOfVars = toString(ev, symbolic, tpl);
								if (!listOfVars.startsWith("{")) {
									// add { with the defined vars by user
									sbCASCommand.append("{" + listOfVars);
								} else {
									// add defined vars by user
									sbCASCommand.append(listOfVars);
								}
								// skip unneeded }
								if (listOfVars.endsWith("}")) {
									sbCASCommand.setLength(sbCASCommand
											.length() - 1);
								}
								// add completion of list of vars
								sbCASCommand.append(complOfVarsStr + "}");
							} else {
								sbCASCommand
										.append(toString(ev, symbolic, tpl));
							}
						}
					} else if (pos >= 0 && pos < args.size()) {
						// success: insert argument(pos)
						ev = args.get(pos);
						// needed for #5506
						if (name.equals("SolveODE")
								&& ((ExpressionNode) ev).getLeft() instanceof MyList
								&& args.size() > 2) {
							sbCASCommand.append(toString(((MyList) (args
									.get(pos).getLeft())).getListElement(0),
									symbolic, tpl));
						} else {
							sbCASCommand.append(toString(ev, symbolic, tpl));
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
		if (name.equals("SolveODE") && args.size() >= 2) {
			return sbCASCommand.toString().replaceAll("unicode39u", "\'");
			// return switchVarsToSolveODE(args, sbCASCommand);
		} else if (name.equals("Solutions") && args.size() == 1) {
			return switchVarsToSolutions(args, sbCASCommand);
		}

		return sbCASCommand.toString();
	}

	// method to check if we should make completion of variable list
	private static boolean checkForParamEquExistance(
			ArrayList<ExpressionNode> args, String name) {
		// case we have command Solve[<Equation list>, <Variable list>]
		if (name.equals("Solve") && args.size() == 2) {
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
		// TODO Auto-generated method stub
		boolean contains = true;
		HashSet<GeoElement> varsInEqu = listElement.getVariables();
		if (varsInEqu != null) {
			contains = false;
			Iterator<GeoElement> it = varsInEqu.iterator();

			// check if current equation contains other vars as
			// parameters
			while (it.hasNext()) {
				GeoElement var = it.next();
				for (int i = 0; i < listOfVars.size(); i++) {
					if (listOfVars
							.getListElement(i)
							.toString(StringTemplate.defaultTemplate)
							.equals(var
									.toString(StringTemplate.defaultTemplate))) {
						contains = true;
						break;
					}
				}
				if (contains) {
					break;
				}
			}
		}
		return contains;
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
						.parseGeoGebraCASInputAndResolveDummyVars(
								localizedInput, kernel, null);

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
		if (!app.isExam() || app.getExam().isCASAllowed()) {
			getCurrentCAS().evaluateGeoGebraCASAsync(c);
		}
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