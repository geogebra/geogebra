/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Command.java
 *
 * Created on 05. September 2001, 12:05
 */

package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.Traversing.GeoDummyReplacer;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.input.Character;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * 
 * @author Markus
 */
public class Command extends ValidExpression
		implements ReplaceChildrenByValues, GetItem {

	// list of arguments
	private ArrayList<ExpressionNode> args = new ArrayList<>();
	private String name; // internal command name (in English)

	@Weak
	private Kernel kernel;
	@Weak
	private App app;
	private GeoElementND[] evalGeos; // evaluated Elements
	private Macro macro; // command may correspond to a macro
	private boolean allowEvaluationForTypeCheck = true;
	private StringBuilder sbToString;

	private ValueType lastType = null;

	/**
	 * for commands with different output types and that need to know each
	 * lenght to set labels correctly
	 */
	private int[] outputSizes;

	/**
	 * Creates a new command object.
	 * 
	 * @param kernel
	 *            kernel
	 * @param name
	 *            internal name or translated name
	 * @param translateName
	 *            true to translate name to internal
	 * 
	 */
	public Command(Kernel kernel, String name, boolean translateName) {
		this(kernel, name, translateName, true);
	}

	/**
	 * Creates a new command object.
	 * 
	 * @param kernel
	 *            kernel
	 * @param name
	 *            internal name or translated name
	 * @param translateName
	 *            true to translate name to internal
	 * @param allowEvaluationForTypeCheck
	 *            whether this command is allowed to be evaluated in type checks
	 *            like isTextValue()
	 */
	public Command(Kernel kernel, String name, boolean translateName,
			boolean allowEvaluationForTypeCheck) {
		this.kernel = kernel;
		app = kernel.getApplication();
		this.allowEvaluationForTypeCheck = app.getConfig().isCASEnabled()
				&& allowEvaluationForTypeCheck;

		/*
		 * need to check app.isUsingInternalCommandNames() due to clash with
		 * BinomialDist=Binomial Binomial=BinomialCoefficient Should also allow
		 * other languages to use English names for different commands
		 */

		if (translateName && !kernel.isUsingInternalCommandNames()) {
			// translate command name to internal name
			this.name = app.getReverseCommand(name);
			// in CAS functions get parsed as commands as well and we want to
			// keep the name
			if (this.name == null) {
				this.name = name;
			}
		} else {
			this.name = name;
		}
	}

	/**
	 * @return kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * @param arg
	 *            argument to add
	 */
	public void addArgument(ExpressionNode arg) {
		args.add(arg);
	}

	/**
	 * Returns the name of the variable at the specified argument position. If
	 * there is no variable name at this position, null is returned.
	 * 
	 * @param i
	 *            position
	 * @return name of the variable at the specified argument position
	 */
	public String getVariableName(int i) {
		if (i >= args.size()) {
			return null;
		}

		ExpressionValue ev = args.get(i).unwrap();
		if (ev instanceof Variable) {
			return ((Variable) ev).getName(StringTemplate.defaultTemplate);
		} else if (ev instanceof GeoElement) {
			// XML Handler looks up labels of GeoElements
			// so we may end up having a GeoElement object here
			// return its name to use as local variable name
			GeoElement geo = ((GeoElement) ev);
			if (geo.isLabelSet() || geo.isLocalVariable()) {
				return ((GeoElement) ev).getLabelSimple();
			}

		} else if (ev instanceof FunctionVariable) {
			return ((FunctionVariable) ev).getSetVarString();
		} else if (ev instanceof Function) {
			String str = ev.toString(StringTemplate.defaultTemplate);
			if (str.length() == 1 && Character.isLetter(str.charAt(0))) {
				return str;
			}
		} else if (ExpressionNode.isImaginaryUnit(ev)) {
			return Unicode.IMAGINARY + "";
		} else if (ev instanceof MySpecialDouble) {
			if (((MySpecialDouble) ev).isEulerConstant()) {
				return Unicode.EULER_STRING;
			}
		} else if (ev instanceof ValidExpression) {
			Log.debug(((ValidExpression) ev).getLabel()
					+ " valid expression label");
		}

		return null;
	}

	/**
	 * @return array of arguments
	 */
	public ExpressionNode[] getArguments() {
		return args.toArray(new ExpressionNode[0]);
	}

	/**
	 * @param i
	 *            index
	 * @return i-th argument
	 */
	public ExpressionNode getArgument(int i) {
		return args.get(i);
	}

	/**
	 * @param i
	 *            index
	 * @param en
	 *            argument
	 */
	public void setArgument(int i, ExpressionNode en) {
		args.set(i, en);
	}

	/**
	 * @return number of arguments
	 */
	public int getArgumentNumber() {
		return args.size();
	}

	/**
	 * @return internal command name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return toString(true, false, tpl);
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(false, false, tpl);
	}

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(symbolic, true, tpl);
	}

	private String toString(boolean symbolic, boolean LaTeX,
			StringTemplate tpl) {
		switch (tpl.getStringType()) {
		case GIAC:
			return (kernel.getGeoGebraCAS()).getCASCommand(name, args, symbolic,
					tpl, SymbolicMode.NONE);
		case LATEX:
			if (sbToString == null) {
				sbToString = new StringBuilder();
			}
			sbToString.setLength(0);
			if ("Integral".equals(name)) {
				sbToString.append("\\int");
				Set<GeoElement> vars = getArgument(0)
						.getVariables(SymbolicMode.NONE);
				String var = "x";
				if (vars != null && !vars.isEmpty()) {
					Iterator<GeoElement> ite = vars.iterator();
					while (ite.hasNext()) {
						GeoElement geo = ite.next();
						// get function from construction
						GeoElement geoFunc = getKernel().getConstruction()
								.geoTableVarLookup(geo.getLabel(
										StringTemplate.defaultTemplate));
						GeoElement geoCASCell = getKernel().getConstruction()
								.lookupCasCellLabel(geo.getLabel(
										StringTemplate.defaultTemplate));
						// if input function was without parameter
						// replace geoDummyVariable with function
						if (geo instanceof GeoDummyVariable && geoFunc != null
								&& geoFunc.isGeoFunction()) {
							ExpressionNode funcNode = new ExpressionNode(kernel,
									geoFunc, Operation.FUNCTION,
									((GeoFunction) geoFunc)
											.getFunctionVariables()[0]);
							getArgument(0)
									.traverse(GeoDummyReplacer.getReplacer(
											geo.getLabel(
													StringTemplate.defaultTemplate),
											funcNode, true));
						}
						// if we couldn't find the function
						// just as GeoCasCell
						if (geo instanceof GeoDummyVariable
								&& geoCASCell != null
								&& geoCASCell.isGeoCasCell()
								&& ((GeoCasCell) geoCASCell)
										.getInputVE() instanceof Function) {
							ExpressionNode funcNode = new ExpressionNode(kernel,
									geo, Operation.FUNCTION,
									((GeoCasCell) geoCASCell)
											.getFunctionVariables()[0]);
							String funcStr = getArgument(0)
									.toString(StringTemplate.defaultTemplate);
							// geoDummyVariable wasn't already changed
							if (!funcStr
									.contains("("
											+ ((GeoCasCell) geoCASCell)
													.getFunctionVariables()[0]
											+ ")")) {
								getArgument(0)
										.traverse(GeoDummyReplacer.getReplacer(
												geo.getLabel(
														StringTemplate.defaultTemplate),
												funcNode, true));
							}
						}
						// make sure that we get from set the variable and not
						// the
						// function
						// needed for TRAC-5364
						if (geo instanceof GeoDummyVariable && geoFunc == null
								&& geoCASCell == null) {
							var = geo.toString(tpl);
						}
					}
				}
				switch (getArgumentNumber()) {
				case 1:
					sbToString.append(" ");
					sbToString.append(getArgument(0).toString(tpl));
					break;
				case 2:
					sbToString.append(" ");
					sbToString.append(getArgument(0).toString(tpl));
					var = getArgument(1).toString(tpl);
					break;
				case 3:
					sbToString.append("\\limits_{");
					sbToString.append(getArgument(1).toString(tpl));
					sbToString.append("}^{");
					sbToString.append(getArgument(2).toString(tpl));
					sbToString.append("}");
					sbToString.append(getArgument(0).toString(tpl));
					break;
				case 4:
					sbToString.append("\\limits_{");
					sbToString.append(getArgument(2).toString(tpl));
					sbToString.append("}^{");
					sbToString.append(getArgument(3).toString(tpl));
					sbToString.append("}");
					sbToString.append(getArgument(0).toString(tpl));
					var = getArgument(1).toString(tpl);
					break;
				default:
					break;
				}
				sbToString.append("\\,\\mathrm{d}");
				sbToString.append(var);
				return sbToString.toString();
			} else if ("Sum".equals(name) && getArgumentNumber() == 4) {
				sbToString.append("\\sum_{");
				sbToString.append(args.get(1).toString(tpl));
				sbToString.append("=");
				sbToString.append(args.get(2).toString(tpl));
				sbToString.append("}^{");
				sbToString.append(args.get(3).toString(tpl));
				sbToString.append("}");
				sbToString.append(args.get(0).toString(tpl));
				return sbToString.toString();
			} else if ("Product".equals(name) && getArgumentNumber() == 4) {
				sbToString.append("\\prod_{");
				sbToString.append(args.get(1).toString(tpl));
				sbToString.append("=");
				sbToString.append(args.get(2).toString(tpl));
				sbToString.append("}^{");
				sbToString.append(args.get(3).toString(tpl));
				sbToString.append("}");
				sbToString.append(args.get(0).toString(tpl));
				return sbToString.toString();
			}
		default:
			if (sbToString == null) {
				sbToString = new StringBuilder();
			}
			sbToString.setLength(0);

			// GeoGebra command syntax
			if (tpl.isPrintLocalizedCommandNames()) {
				sbToString.append(app.getLocalization().getCommand(name));
			} else {
				sbToString.append(name);
			}
			if (LaTeX || tpl.hasType(StringType.LATEX)) {
				sbToString.append(" \\left");
			}
			sbToString.append('(');
			int size = args.size();
			for (int i = 0; i < size; i++) {
				sbToString.append(toString(args.get(i), symbolic, LaTeX, tpl));
				// Integral[f,0,1]
				// make sure that we add the parameter of the function too
				if ("Integral".equals(name)) {
					if (i == 0 && args.get(0).isExpressionNode()
							&& args.get(0).getLeft() instanceof GeoCasCell) {
						if (((GeoCasCell) args.get(0).getLeft())
								.isAssignmentVariableDefined()
								&& args.get(0).getRight() == null) {
							sbToString
									.append("("
											+ ((GeoCasCell) args.get(0)
													.getLeft())
															.getFunctionVariable()
											+ ")");
						}
					}
				}
				sbToString.append(',');
			}
			if (size > 0) {
				sbToString.deleteCharAt(sbToString.length() - 1);
			}
			if (LaTeX || tpl.hasType(StringType.LATEX)) {
				sbToString.append(" \\right");
			}
			sbToString.append(')');

			return sbToString.toString();
		}
	}

	private static String toString(ExpressionValue ev, boolean symbolic,
			boolean LaTeX, StringTemplate tpl) {
		if (LaTeX) {
			return ev.toLaTeXString(symbolic, tpl);
		}
		return symbolic ? ev.toString(tpl) : ev.toValueString(tpl);
	}

	/**
	 * @param info
	 *            context for evaluation
	 * @return array of resulting geos
	 */
	public GeoElementND[] evaluateMultiple(EvalInfo info) {
		GeoElementND[] geos = null;
		geos = kernel.getAlgebraProcessor().processCommand(this, info);
		return geos;
	}

	@Override
	public ExpressionValue evaluate(StringTemplate tpl) {
		// not yet evaluated: process command
		if (evalGeos == null) {
			evalGeos = evaluateMultiple(new EvalInfo(false));
		}
		if (evalGeos != null && evalGeos.length >= 1) {
			return evalGeos[0];
		}
		Log.debug("invalid command evaluation: " + name);
		throw new MyError(app.getLocalization(),
				app.getLocalization().getInvalidInputError() + ":\n" + this);

	}

	/**
	 * Like evaluate, but does not necessarily produce GeoElement
	 * 
	 * @param info
	 *            evaluation flags
	 * @return evaluation result
	 */
	public ExpressionValue simplify(EvalInfo info) {
		// not yet evaluated: process command
		ExpressionValue result = kernel.getAlgebraProcessor()
				.simplifyCommand(this, info.withLabels(false));
		if (result instanceof GeoElement) {
			evalGeos = new GeoElement[] { (GeoElement) result };
		}
		if (result != null) {
			return result;
		}
		Log.debug("invalid command evaluation: " + name);
		throw new MyError(app.getLocalization(),
				app.getLocalization().getInvalidInputError() + ":\n" + this);

	}

	@Override
	public void resolveVariables(EvalInfo info) {
		// standard case:
		// nothing to do here: argument variables are resolved
		// while command processing (see evaluate())

		// CAS parsing case: we need to resolve arguments also
		if (info.getSymbolicMode() != SymbolicMode.NONE) {
			for (int i = 0; i < args.size(); i++) {
				args.get(i).resolveVariables(info);
			}

			// avoid evaluation of command
			allowEvaluationForTypeCheck = false;
		}
	}

	// rewritten to cope with {Root[f]}
	// Michael Borcherds 2008-10-02
	@Override
	public boolean isConstant() {

		// not yet evaluated: process command
		if (evalGeos == null) {
			evalGeos = evaluateMultiple(new EvalInfo(false));
		}

		if (evalGeos == null || evalGeos.length == 0) {
			throw new MyError(app.getLocalization(),
					app.getLocalization().getInvalidInputError() + ":\n"
							+ this);
		}

		for (int i = 0; i < evalGeos.length; i++) {
			if (!evalGeos[i].isConstant()) {
				return false;
			}
		}
		return true;

	}

	@Override
	public boolean isLeaf() {
		// return evaluate().isLeaf();
		return true;
	}

	/*
	 * Type checking with evaluate Try to evaluate using GeoGebra if fails, try
	 * with CAS else throw Exception
	 */
	@Override
	public boolean isNumberValue() {
		return evaluatesToNumber(false);
	}

	@Override
	public ValueType getValueType() {
		if ("Sequence".equals(name) || "IterationList".equals(name)
				|| "KeepIf".equals(name)) {
			return ValueType.LIST;
		}
		if ("Function".equals(name)) {
			return ValueType.FUNCTION;
		}
		if ("Surface".equals(name)
				|| ("Curve".equals(name) && args.size() > 5)) {
			return ValueType.PARAMETRIC3D;
		}
		if ("CurveCartesian".equals(name)) {
			return ValueType.PARAMETRIC2D;
		}
		if ("Vector".equals(name) && (args.size() > 0)
				&& args.get(0).getValueType() == ValueType.VECTOR3D) {
			return ValueType.VECTOR3D;
		}
		if ("Vector".equals(name)) {
			return ValueType.NONCOMPLEX2D;
		}
		if ("Evaluate".equals(name) && args.size() > 0) {
			return args.get(0).getValueType();
		}
		Command evaluationCopy = this;
		if ("Sum".equals(name) && args.size() == 4) {
			return args.get(0).getValueType();
		}
		if ("Sum".equals(name) || "Product".equals(name)) {
			evaluationCopy = deepCopy(kernel);
		}
		if (lastType != null) {
			return lastType;
		}

		if (!allowEvaluationForTypeCheck) {
			return ValueType.UNKNOWN;
		}
		try {
			lastType = evaluationCopy.evaluate(StringTemplate.defaultTemplate)
					.getValueType();
		} catch (Throwable ex) {
			if (!kernel.getGeoGebraCAS().isCommandAvailable(this)) {
				return lastType;
			}

			ExpressionValue ev = kernel.getGeoGebraCAS().getCurrentCAS()
					.evaluateToExpression(this, null, kernel);
			if (ev != null) {
				lastType = ev.getValueType();
			} else {
				throw wrapError(ex);
			}
		}
		return lastType;
	}

	private MyError wrapError(Throwable ex) {
		return ex instanceof MyError ? (MyError) ex
				: new MyError(kernel.getLocalization(), ex.getMessage());
	}

	@Override
	public boolean evaluatesToVectorNotPoint() {
		if (!allowEvaluationForTypeCheck) {
			return false;
		}
		try {
			return evaluate(
					StringTemplate.defaultTemplate) instanceof VectorValue;
		} catch (MyError ex) {
			ExpressionValue ev = kernel.getGeoGebraCAS().getCurrentCAS()
					.evaluateToExpression(this, null, kernel);
			if (ev != null) {
				return ev.unwrap().evaluatesToNonComplex2DVector();
			}
			throw ex;
		}
	}

	@Override
	public boolean evaluatesToText() {
		if (!allowEvaluationForTypeCheck) {
			return false;
		}
		if (app.getInternalCommand(name) == null
				&& kernel.getMacro(name) == null) {
			return false;
		}
		try {
			return evaluate(StringTemplate.defaultTemplate).evaluatesToText();
		} catch (MyError ex) {
			ExpressionValue ev = kernel.getGeoGebraCAS().getCurrentCAS()
					.evaluateToExpression(this, null, kernel);
			if (ev != null) {
				return ev.unwrap().evaluatesToText();
			}
			throw ex;
		}
	}

	@Override
	public Command deepCopy(Kernel kernel1) {
		Command c = new Command(kernel1, name, false);
		// copy arguments
		int size = args.size();
		for (int i = 0; i < size; i++) {
			c.addArgument(args.get(i).getCopy(kernel1));
		}
		return c;
	}

	@Override
	public void replaceChildrenByValues(GeoElement geo) {
		int size = args.size();
		for (int i = 0; i < size; i++) {
			args.get(i).replaceChildrenByValues(geo);
		}
	}

	@Override
	public HashSet<GeoElement> getVariables(SymbolicMode mode) {
		HashSet<GeoElement> set = new HashSet<>();
		int size = args.size();
		for (int i = 0; i < size; i++) {
			Set<GeoElement> s = args.get(i).getVariables(mode);
			if (s != null) {
				set.addAll(s);
			}
		}
		return set;
	}

	@Override
	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	@Override
	public int getListDepth() {
		if ("x".equals(getName()) || "y".equals(getName())
				|| "z".equals(getName()) || "If".equals(getName())) {
			return this.getArgument(0).getListDepth();
		}
		// There we might add more commands that evaluate to matrix
		if ("Identity".equals(getName())) {
			return 2;
		}
		if (!allowEvaluationForTypeCheck) {
			return 0;
		}
		try {
			return evaluate(StringTemplate.defaultTemplate).getListDepth();
		} catch (MyError ex) {
			ExpressionValue ev = kernel.getGeoGebraCAS().getCurrentCAS()
					.evaluateToExpression(this, null, kernel);
			if (ev != null) {
				return ev.unwrap().getListDepth();
			}
			throw ex;
		}

	}

	/**
	 * @return macro macro associated with this command
	 */
	public final Macro getMacro() {
		return macro;
	}

	/**
	 * @param macro
	 *            macro associated with this command
	 */
	public final void setMacro(Macro macro) {
		this.macro = macro;
	}

	@Override
	public boolean isTopLevelCommand() {
		return true;
	}

	@Override
	public boolean isTopLevelCommand(String checkName) {
		return name.equals(checkName);
	}

	@Override
	public Command getTopLevelCommand() {
		return this;
	}

	@Override
	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue v = t.process(this);
		if (v != this) {
			return v;
		}
		for (int i = 0; i < args.size(); i++) {
			ExpressionNode en = args.get(i).traverse(t).wrap();
			args.set(i, en);
		}
		return this;
	}

	@Override
	public boolean inspect(Inspecting t) {
		if (t.check(this)) {
			return true;
		}
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).inspect(t)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ExpressionValue getItem(int i) {
		return args.get(i);
	}

	@Override
	public boolean hasCoords() {
		if ("x".equals(name) || "y".equals(name) || "z".equals(name)) {
			return false;
		}
		return true;
	}

	/**
	 * set output sizes
	 * 
	 * @param sizes
	 *            output sizes
	 */
	public void setOutputSizes(int[] sizes) {
		outputSizes = sizes;
	}

	/**
	 * 
	 * @return output sizes
	 */
	public int[] getOutputSizes() {
		return outputSizes;
	}

	@Override
	public int getLength() {
		return getArgumentNumber();
	}

	/**
	 * Replaces all Variable objects with the given varName in the arguments by
	 * the given FunctionVariable object.
	 * 
	 * @param varName
	 *            variable name
	 * @param fVar
	 *            replacement variable
	 * @return number of replacements done
	 */
	public int replaceVariables(String varName, FunctionVariable fVar) {
		int replacements = 0;

		for (ExpressionNode arg : args) {
			replacements += arg.replaceVariables(varName, fVar);
		}

		return replacements;
	}

	@Override
	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	/**
	 * Helps pars x(expr) to either command, x-coord function or multiplication
	 * by x.
	 * 
	 * @param en
	 *            parameter
	 * @param i
	 *            coordinate (0=x,1=y,2=z);
	 * @param mayCheck
	 *            whether we may compute the command to find output type
	 * @param undecided
	 *            array to which we can push the result if not clear whether
	 *            it's multiplication or function
	 * @param kernel
	 *            kernel
	 * @return parsed expression
	 */
	public static ExpressionNode xyzCAS(ValidExpression en, int i,
			boolean mayCheck, ArrayList<ExpressionNode> undecided,
			Kernel kernel) {
		Operation[] ops = new Operation[] { Operation.XCOORD, Operation.YCOORD,
				Operation.ZCOORD };

		ExpressionNode en2;
		if (en.evaluatesToList()) {
			Command cmd = new Command(kernel, "Element", true, mayCheck);
			cmd.addArgument(en.wrap());
			// Element uses 1 for first element
			cmd.addArgument(new MyDouble(kernel, i + 1).wrap());
			en2 = cmd.wrap();
		} else if (en.hasCoords()) {
			en2 = new ExpressionNode(kernel, en.unwrap(), ops[i], null);
			/*
			 * char funName = (char) ('x'+i); Command cmd = new Command(k,
			 * funName+"", true, mayCheck ); cmd.addArgument( en ); en2 =
			 * cmd.wrap();
			 */
		} else {
			char funName = (char) ('x' + i);
			en2 = new ExpressionNode(kernel,
					new FunctionVariable(kernel, funName + ""),
					Operation.MULTIPLY_OR_FUNCTION, en);
			undecided.add(en2);
		}
		// App.printStacktrace("");
		return en2;

	}

	/**
	 * Change command name, useful eg for processing Rotate as RotateText
	 * 
	 * @param string
	 *            new name for this command
	 */
	public void setName(String string) {
		this.name = string;
	}

	/**
	 * @return whether this command has a name of GeoGebra supported command
	 */
	public boolean isAvailable() {
		Commands c = null;
		try {
			c = Commands.valueOf(name);
		} catch (Exception e) {
			// not found
		}
		return c != null;
	}

	public void setAllowEvaluationForTypeCheck(boolean allowEvaluationForTypeCheck) {
		this.allowEvaluationForTypeCheck = allowEvaluationForTypeCheck;
	}
}
