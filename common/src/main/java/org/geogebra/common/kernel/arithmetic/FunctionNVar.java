/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Inequality.IneqType;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant.ArbconstReplacer;
import org.geogebra.common.kernel.arithmetic.Traversing.CopyReplacer;
import org.geogebra.common.kernel.arithmetic.Traversing.VariablePolyReplacer;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MaxSizeHashMap;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;

import com.google.j2objc.annotations.Weak;

/**
 * Function of N variables that returns either a number or a boolean. This
 * depends on the expression this function is based on.
 * 
 * @author Markus Hohenwarter + mathieu
 */
public class FunctionNVar extends ValidExpression
		implements FunctionalNVar, VarString {

	/** function expression */
	protected ExpressionNode expression;
	/** function variables */
	protected FunctionVariable[] fVars;
	private IneqTree ineqs;

	/** standard case: number function, see initFunction() */
	protected boolean isBooleanFunction = false;

	/** if the function is of type f(x) = c */
	protected boolean isConstantFunction = false;

	/** kernel */
	@Weak
	protected Kernel kernel;
	private final static int MAX_CAS_EVAL_MAP_SIZE = 100;
	private MaxSizeHashMap<String, FunctionNVar> casEvalMap;
	private String shortLHS;
	private ExpressionNode casEvalExpression;
	private String casEvalStringSymbolic;

	private boolean forceInequality;

	private static ArrayList<ExpressionNode> undecided = new ArrayList<>();

	private final static class RandomCheck implements Inspecting {
		protected RandomCheck() {
			// make this visible
		}

		@Override
		public boolean check(ExpressionValue v) {
			return (v.isGeoElement() && ((GeoElement) v).isRandomGeo())
					|| v.isOperation(Operation.RANDOM);
		}
	}

	/**
	 * Creates new Function from expression. Note: call initFunction() after
	 * this constructor.
	 * 
	 * @param kernel
	 *            kernel
	 * @param expression
	 *            function expression
	 */
	public FunctionNVar(Kernel kernel, ExpressionNode expression) {
		this.kernel = kernel;
		this.expression = expression;
	}

	/**
	 * Creates new Function from expression where the function variables in
	 * expression is already known.
	 * 
	 * @param exp
	 *            function expression
	 * @param fVars
	 *            variables
	 */
	public FunctionNVar(ExpressionNode exp, FunctionVariable[] fVars) {
		kernel = exp.getKernel();

		expression = exp;
		this.fVars = fVars;
	}

	/**
	 * Creates a Function that has no expression yet. Use setExpression() to do
	 * this later.
	 * 
	 * @param kernel
	 *            kernel
	 */
	public FunctionNVar(Kernel kernel) {
		this.kernel = kernel;

	}

	/**
	 * Copy expression and variables from template (shallow)
	 * 
	 * @param fn
	 *            template function
	 */
	public void set(FunctionNVar fn) {
		this.expression = fn.getExpression();
		this.fVars = fn.fVars;
		this.isBooleanFunction = fn.isBooleanFunction;
		this.isConstantFunction = fn.isConstantFunction;
		this.ineqs = fn.ineqs;
	}

	/**
	 * copy constructor
	 * 
	 * @param f
	 *            source function
	 * @param kernel
	 *            kernel
	 */
	public FunctionNVar(FunctionNVar f, Kernel kernel) {
		expression = f.expression.getCopy(kernel);
		fVars = f.fVars; // no deep copy of function variable
		isBooleanFunction = f.isBooleanFunction;
		isConstantFunction = f.isConstantFunction;

		this.kernel = kernel;
	}

	/**
	 * Determine whether var is function variable of this function
	 * 
	 * @param var
	 *            variable name
	 * @return true if var is function variable of this function
	 */
	public boolean isFunctionVariable(String var) {
		if (fVars == null) {
			return false;
		}

		for (FunctionVariable fVar : fVars) {
			if (fVar.toString(StringTemplate.defaultTemplate).equals(var)) {
				return true;
			}
		}
		return false; // if none of function vars equals var
	}

	/**
	 * @return kernel
	 */
	@Override
	public Kernel getKernel() {
		return kernel;
	}

	@Override
	public FunctionNVar deepCopy(Kernel kernel1) {
		return new FunctionNVar(this, kernel1);
	}

	/**
	 * @return function expression
	 */
	public ExpressionNode getExpression() {
		return expression;
	}

	@Override
	public void resolveVariables(EvalInfo info) {
		expression.resolveVariables(info);
	}

	/**
	 * Replaces geo and all its dependent geos in this function's expression by
	 * copies of their values.
	 * 
	 * @param geo
	 *            geo to be replaced
	 */
	public void replaceChildrenByValues(GeoElement geo) {
		if (expression != null) {
			expression.replaceChildrenByValues(geo);
		}
	}

	/**
	 * Use this method only if you really know what you are doing.
	 * 
	 * @param exp
	 *            function expression
	 */
	public void setExpression(ExpressionNode exp) {
		expression = exp;
	}

	/**
	 * Use this method only if you really know what you are doing.
	 * 
	 * @param exp
	 *            function expression
	 * @param vars
	 *            variables
	 */
	public void setExpression(ExpressionNode exp, FunctionVariable[] vars) {
		expression = exp;
		fVars = vars;
	}

	@Override
	public FunctionNVar getFunction() {
		return this;
	}

	/**
	 * Returns array of all variables
	 * 
	 * @return array of variables
	 */
	@Override
	public FunctionVariable[] getFunctionVariables() {
		return fVars;
	}

	/**
	 * Returns name of i-th variable
	 * 
	 * @param i
	 *            index
	 * @param tpl
	 *            string template
	 * @return name of i-th variable
	 */
	final public String getVarString(int i, StringTemplate tpl) {
		return fVars[i].toString(tpl);
	}

	/**
	 * Number of arguments of this function, e.g. 2 for f(x,y)
	 * 
	 * @return number of variables
	 */
	final public int getVarNumber() {
		return fVars.length;
	}

	@Override
	public String getVarString(final StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		return appendVarString(sb, tpl).toString();
	}

	/**
	 * Appends varstring to the builder
	 * 
	 * @param sb
	 *            string builder
	 * @param tpl
	 *            string template
	 * @return sb
	 */
	public StringBuilder appendVarString(StringBuilder sb,
			final StringTemplate tpl) {
		for (int i = 0; i < fVars.length - 1; i++) {
			sb.append(fVars[i].toString(tpl));
			sb.append(",");
			tpl.appendOptionalSpace(sb);
		}
		sb.append(fVars[fVars.length - 1].toString(tpl));
		return sb;
	}

	/**
	 * @return init function and simplify int nodes
	 */
	public final boolean initFunction() {
		return initFunction(true);
	}

	/**
	 * Call this function to resolve variables and init the function. May throw
	 * MyError (InvalidFunction).
	 *
	 * @param simplifyInt
	 *            whether int nodes should be simplified
	 *
	 * @return whether this is a valid (numeric or boolean) function
	 */
	public boolean  initFunction(boolean simplifyInt) {
		EvalInfo info = new EvalInfo(false).withSimplifying(simplifyInt);
		return initFunction(info);
	}

	/**
	 * Call this function to resolve variables and init the function. May throw
	 * MyError (InvalidFunction).
	 *
	 * @param info info
	 * @return whether this is a valid (numeric or boolean) function
	 */
	public boolean initFunction(EvalInfo info) {

		// replace function variables in tree
		for (int i = 0; i < fVars.length; i++) {
			FunctionVariable fVar = fVars[i];

			// look for Variable objects with name of function variable and
			// replace them
			// x, y got polynomials while parsing
			VariablePolyReplacer s = VariablePolyReplacer.getReplacer(fVar);
			expression.traverse(s);
			int replacements = s.getReplacements();
			isConstantFunction = isConstantFunction && replacements == 0;
		}

		// replace variable names by objects
		expression.resolveVariables(info);

		// the idea here was to allow something like: Derivative[f] + 3x
		// but wrapping the GeoFunction objects as ExpressionNodes of type
		// FUNCTION
		// leads to Derivative[f](x) + 3x
		// expression.wrapGeoFunctionsAsExpressionNode();

		// replace all polynomials in expression (they are all equal to "1x" if
		// we got this far)
		// by an instance of MyDouble

		// evaluate expression to find out about the type of function
		ExpressionValue ev;
		try {
			ev = expression.evaluate(StringTemplate.defaultTemplate);
		} catch (MyError err) {
			// Evaluation failed: DESPERATE MODE
			try {
				// try to fix structure of expression and then try evaluation
				// again
				fixStructure();
				ev = expression.evaluate(StringTemplate.defaultTemplate);
			} catch (Throwable th) {
				// throw original error when desperate mode failed
				throw err;
			}
		}

		// initialize type as boolean or numeric function
		return initType(ev);
	}

	/**
	 * Tries to fix a structural problem leading to an evaluation error, e.g.
	 * x(x+1) is interpreted as xcoord(x+1). This can be fixed by changing the
	 * structure to x*(x+1) for example.
	 */
	private void fixStructure() {
		FunctionVariable[] xyzVars = getXYZVars(fVars);

		// try to replace x(x+1) by x*(x+1)
		undecided.clear();
		expression.replaceXYZnodes(xyzVars[0], xyzVars[1], xyzVars[2], undecided);
		for (ExpressionNode en : undecided) {
			en.setOperation(Operation.MULTIPLY);
		}
		undecided.clear();
	}

	/**
	 * Returns the last x, y and z variables from the input var array, if they exist.
	 *
	 * @param vars input array
	 * @return x, y and z variables from the input, if they exist
	 */
	public static FunctionVariable[] getXYZVars(FunctionVariable[] vars) {
		// get function variables for x, y, z
		FunctionVariable xVar = null, yVar = null, zVar = null;
		for (FunctionVariable fVar : vars) {
			if ("x".equals(fVar.toString(StringTemplate.defaultTemplate))) {
				xVar = fVar;
			} else if ("y".equals(fVar.toString(StringTemplate.defaultTemplate))) {
				yVar = fVar;
			} else if ("z".equals(fVar.toString(StringTemplate.defaultTemplate))) {
				zVar = fVar;
			}
		}
		return new FunctionVariable[] { xVar, yVar, zVar };
	}

	/**
	 * Receives result of evaluate as input, hence may use instanceof
	 * 
	 * @return whether this is a valid (numeric or boolean) function
	 */
	private boolean initType(ExpressionValue ev) {
		if (ev instanceof BooleanValue) {
			isBooleanFunction = true;
		} else if (ev instanceof NumberValue) {
			isBooleanFunction = false;
		}  else if (ev instanceof GeoFunction && ((GeoFunction) ev).isLabelSet()) {
			// f(x) should be a dependent function
			expression = new ExpressionNode(kernel, ev, Operation.FUNCTION, fVars[0]);
		} else if (ev instanceof GeoFunctionNVar && ((GeoFunctionNVar) ev).isLabelSet()) {
			// f(x, y) should be a dependent function
			MyList args = new MyList(kernel, fVars.length);
			for (FunctionVariable fVar: fVars) {
				args.addListElement(fVar);
			}
			expression = new ExpressionNode(kernel, ev, Operation.FUNCTION_NVAR, args);
		} else if (ev instanceof FunctionalNVar) {
			expression = ((FunctionalNVar) ev).getFunctionExpression();
			fVars = ((FunctionalNVar) ev).getFunctionVariables();
		} else if (ev instanceof MyList && ((MyList) ev).isMatrix()) {
			return true;
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Returns whether this function always evaluates to BooleanValue.
	 */
	@Override
	final public boolean isBooleanFunction() {
		return isBooleanFunction;
	}

	/**
	 * Returns whether this function always evaluates to the same numerical
	 * value, i.e. it is of the form f(x1,...,xn) = c.
	 * 
	 * @return true iff constant
	 */
	final public boolean isConstantFunction() {
		if (isConstantFunction) {
			return true;
		}
		for (FunctionVariable fVar : fVars) {
			if (expression.contains(fVar)) {
				return false;
			}
		}
		return true; // none of the vars appears in the expression
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	/**
	 * Returns this function's value at position.
	 * 
	 * @param vals
	 *            values of variables
	 * @return f(vals)
	 */
	@Override
	final public double evaluate(double[] vals) {
		if (isBooleanFunction) {
			// BooleanValue
			return evaluateBoolean(vals) ? 1 : 0;
		}
		// NumberValue
		if (fVars != null) {
			for (int i = 0; i < fVars.length; i++) {
				fVars[i].set(vals[i]);
			}
		}
		return expression.evaluateDouble();
	}

	@Override
	final public double evaluate(double x, double y) {
		if (isBooleanFunction) {
			// BooleanValue
			return evaluateBoolean(x, y) ? 1 : 0;
		}
		// NumberValue

		fVars[0].set(x);
		fVars[1].set(y);

		return expression.evaluateDouble();
	}

	/**
	 * Returns this function's value at position vals. (Note: use this method if
	 * isBooleanFunction() returns true.
	 * 
	 * @param vals
	 *            values of variables
	 * @return f(vals)
	 */
	final public boolean evaluateBoolean(double... vals) {
		for (int i = 0; i < fVars.length; i++) {
			fVars[i].set(vals[i]);
		}
		return expression.evaluateBoolean();
	}

	@Override
	public HashSet<GeoElement> getVariables(SymbolicMode mode) {
		return expression.getVariables(mode);
	}

	/**
	 * 
	 * @param mode
	 *            symbolic mode
	 * @return GeoElements acting as variables
	 */
	public final GeoElement[] getGeoElementVariables(SymbolicMode mode) {
		return expression.getGeoElementVariables(mode);
	}

	@Override
	public String toString(StringTemplate tpl) {
		return expression.toString(tpl);
	}

	@Override
	final public String toValueString(StringTemplate tpl) {

		return expression.toValueString(tpl);
	}

	@Override
	final public String toOutputValueString(StringTemplate tpl) {
		return expression.toOutputValueString(tpl);
	}

	@Override
	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return expression.toLaTeXString(symbolic, tpl);
	}

	/*
	 * *************** CAS Stuff **************
	 */

	/**
	 * Evaluates this function using the given CAS command. Caching is used for
	 * symbolic CAS evaluations.
	 * 
	 * @param ggbCasCmd
	 *            the GeoGebraCAS command needs to include % in all places where
	 *            the function f should be substituted, e.g. "Derivative(%,x)"
	 * @param symb
	 *            true for symbolic evaluation, false to use values of
	 *            GeoElement variables
	 * @param arbconst
	 *            arbitrary constant handler
	 * @return resulting function
	 */
	final public FunctionNVar evalCasCommand(String ggbCasCmd, boolean symb,
			MyArbitraryConstant arbconst) {
		StringBuilder sb = new StringBuilder(80);
		// remember expression and its CAS string
		boolean useCaching = true;
		boolean symbolic = symb;
		// for multi-variate functions we need to ensure value form,
		// i.e. f(x,m)=x^2+m, g(x)=f(x,2), Derivative[g] gets sent as
		// Derivative[x^2+2] instead of Derivative[f(x,2)]
		// see TRAC-1310
		symbolic = symbolic && !expression.containsGeoFunctionNVar();

		// make sure to use temporary variable names
		// e.g. a in Derivative[a*x^2,x] needs to be renamed temporarily when a
		// exists in GeoGebra
		// see TRAC-2547

		StringTemplate tpl = StringTemplate.numericNoLocal;
		// did expression change since last time?
		// or did symbolic falg change?
		if (casEvalExpression != expression
				|| (symbolic && casEvalStringSymbolic == null)) {
			casEvalExpression = expression;
			if (symbolic) {
				casEvalStringSymbolic = expression.getCASstring(tpl, true);
			}

			// caching should only be done if the expression doesn't contain
			// other functions
			// e.g. this is important for f(x) = x^2, g(x,y) = f(x) + y,
			// Derivative(g(x,y), x)
			// where we cannot cache the derivative of g because g may have
			// changed

			useCaching = !expression.containsCasEvaluableFunction()
					&& !expression.inspect(new RandomCheck());

		}

		// build command string for CAS
		String expString = symbolic ? casEvalStringSymbolic
				: expression.getCASstring(tpl, false);

		// substitute % by expString in ggbCasCmd
		String casString = ggbCasCmd.replaceAll("%", expString);
		FunctionNVar resultFun = null;

		// eval with CAS
		try {
			if (useCaching) {
				// check if result is in cache

				resultFun = lookupCasEvalMap(casString);
				if (resultFun != null) {
					resultFun.getExpression().traverse(
							ArbconstReplacer.getReplacer(arbconst));
					// System.out.println("caching worked: " + casString +
					// " -> " + resultFun);
					return resultFun;
				}
			}
			// evaluate expression by CAS
			String result = symbolic
					? kernel.evaluateGeoGebraCAS(casString, arbconst) : // symbolic
					kernel.evaluateCachedGeoGebraCAS(casString, arbconst); // value
																			// string
			// System.out.println("evaluateGeoGebraCAS: " + casString + " -> " +
			// result);

			// parse CAS result back into GeoGebra
			String tmpLabel = kernel.getConstruction().getConstructionDefaults()
					.getDefaultGeo(ConstructionDefaults.DEFAULT_FUNCTION_NVAR).getFreeLabel("f");
			sb.setLength(0);
			sb.append(tmpLabel); // this name is never used, just needed for parsing
			sb.append("(");
			sb.append(getVarString(StringTemplate.defaultTemplate));
			sb.append(") = ");
			sb.append(result);

			// parse result
			if (getVarNumber() == 1) {
				resultFun = (kernel.getParser().parseFunction(sb.toString()));
			} else {
				resultFun = (kernel.getParser()
						.parseFunctionNVar(sb.toString()));
				resultFun = ensureVarsAreNotNull(resultFun);
			}
			resultFun.initFunction();
		} catch (Throwable e) {
			e.printStackTrace();
			resultFun = null;
		}

		// cache result
		if (useCaching && resultFun != null) {
			getCasEvalMap().put(casString, resultFun);
		}

		// System.out.println("NO caching: " + casString + " -> " + resultFun);

		return resultFun;
	}

	private FunctionNVar ensureVarsAreNotNull(FunctionNVar resultFun) {
		if (resultFun.getFunctionVariables() == null) {
			int dim = getFunctionVariables().length;
			FunctionVariable[] fv = new FunctionVariable[dim];

			for (int i = 0; i < dim; i++) {
				fv[i] = getFunctionVariables()[i].deepCopy(kernel);
			}
			return new FunctionNVar(resultFun.getFunctionExpression(), fv);
		}
		return resultFun;
	}

	/**
	 * 
	 * @return CAS map command -> result
	 */
	protected MaxSizeHashMap<String, FunctionNVar> getCasEvalMap() {
		if (casEvalMap == null) {
			casEvalMap = new MaxSizeHashMap<>(
					MAX_CAS_EVAL_MAP_SIZE);
		}
		return casEvalMap;
	}

	/**
	 * 
	 * @param sb
	 *            XML builder
	 */
	public void printCASevalMapXML(StringBuilder sb) {
		if (casEvalMap != null) {
			sb.append("<casMap>\n");
			for (Entry<String, FunctionNVar> entry : casEvalMap.entrySet()) {
				sb.append("\t<entry key=\"");
				StringUtil.encodeXML(sb, entry.getKey());
				sb.append("\" val=\"");
				StringUtil.encodeXML(sb, entry.getValue().toString(StringTemplate.xmlTemplate));
				sb.append("\"/>\n");
			}
			sb.append("</casMap>\n");
		}
	}

	private FunctionNVar lookupCasEvalMap(String casString) {
		if (casEvalMap == null) {
			return null;
		}
		return casEvalMap.get(casString);
	}

	/**
	 * Clears the cache (needed in Web when the CAS loads)
	 */
	public void clearCasEvalMap() {
		if (casEvalMap == null) {
			return;
		}
		casEvalMap.clear();
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	@Override
	public String getLabelForAssignment() {
		// function, e.g. f(x) := 2*x
		return getLabel()
				+ "("
				+ getVarString(StringTemplate.defaultTemplate)
				+ ")";
	}

	@Override
	public IneqTree getIneqs() {
		return ineqs;
	}

	/**
	 * initializes inequalities
	 * 
	 * @param fe
	 *            expression node
	 * @return true if the functions consists of inequalities
	 */
	public boolean initIneqs(ExpressionNode fe) {
		if (ineqs == null || fe == getExpression()) {
			ineqs = new IneqTree();
		}
		boolean b = initIneqs(fe, ineqs, false);
		ineqs.recomputeSize();
		return b;
	}

	private boolean initIneqs(ExpressionNode fe,
			IneqTree tree, boolean negate) {
		Operation op = fe.getOperation();
		ExpressionNode leftTree = fe.getLeftTree();
		ExpressionNode rightTree = fe.getRightTree();
		if (op.equals(Operation.GREATER) || op.equals(Operation.GREATER_EQUAL)
				|| op.equals(Operation.LESS)
				|| op.equals(Operation.LESS_EQUAL)) {
			Inequality newIneq = new Inequality(kernel, leftTree, rightTree,
					adjustOp(op, negate), getFunction().getFunctionVariables());
			if (newIneq.getType() != IneqType.INEQUALITY_INVALID) {
				if (newIneq.getType() != IneqType.INEQUALITY_1VAR_X
						&& newIneq.getType() != IneqType.INEQUALITY_1VAR_Y) {
					newIneq.getBorder().setInverseFill(newIneq.isAboveBorder());
				}
				tree.setIneq(newIneq);
			}
			return newIneq.getType() != IneqType.INEQUALITY_INVALID;
		} else if (op.equals(Operation.AND) || op.equals(Operation.AND_INTERVAL)
				|| op.equals(Operation.OR) || op.equals(Operation.EQUAL_BOOLEAN)
				|| op.equals(Operation.NOT_EQUAL) || op.equals(Operation.XOR)) {
			tree.setOperation(adjustOp(op, negate));
			tree.setLeft(new IneqTree());
			tree.setRight(new IneqTree());
			return initIneqs(leftTree,  tree.getLeft(), negate)
					&& initIneqs(rightTree,  tree.getRight(),
							negate);
		} else if (op.equals(Operation.NOT)) {
			return initIneqs(leftTree,  tree, !negate);
		} else if (op.equals(Operation.IMPLICATION)) {
			tree.setOperation(adjustOp(Operation.OR, negate));
			tree.setLeft(new IneqTree());
			tree.setRight(new IneqTree());
			return initIneqs(leftTree,  tree.getLeft(), !negate)
					&& initIneqs(rightTree,  tree.getRight(),
							negate);
		} else if (op.equals(Operation.FUNCTION_NVAR)) {
			FunctionalNVar nv = (FunctionalNVar) leftTree.getLeft();
			ExpressionNode subExpr = nv.getFunction().getExpression()
					.getCopy(kernel);
			FunctionVariable[] subVars = nv.getFunction()
					.getFunctionVariables();
			for (int i = 0; i < subVars.length; i++) {
				subExpr.replace(subVars[i],
						((MyList) rightTree.getLeft()).getListElement(i));
			}
			return initIneqs(subExpr,  tree, negate);
		} else {
			return false;
		}

	}

	private static Operation adjustOp(Operation op, boolean negate) {
		if (!negate) {
			return op;
		}
		switch (op) {
		case AND:
			return Operation.OR;
		case OR:
			return Operation.AND;
		case GREATER_EQUAL:
			return Operation.LESS;
		case GREATER:
			return Operation.LESS_EQUAL;
		case LESS_EQUAL:
			return Operation.GREATER;
		case LESS:
			return Operation.GREATER_EQUAL;
		case EQUAL_BOOLEAN:
			return Operation.NOT_EQUAL;
		case NOT_EQUAL:
			return Operation.EQUAL_BOOLEAN;
		default:
			break;
		}
		return Operation.NO_OPERATION;
	}

	/**
	 * updates list of inequalities
	 * 
	 * @return true iff all inequalities are drawable
	 */
	public boolean updateIneqs() {
		if (ineqs == null) {
			return false;
		}
		return ineqs.updateCoef();
	}

	/**
	 * Evaluates function at given point
	 * 
	 * @param pt
	 *            point for evaluation
	 * @return function value
	 */
	public double evaluate(VectorNDValue pt) {
		if (fVars.length == 1 && "y"
				.equals(fVars[0].toString(StringTemplate.defaultTemplate))) {
			return evaluate(new double[] { pt.getPointAsDouble()[1] });
		}
		return evaluate(pt.getPointAsDouble());
	}

	/**
	 * Translates the function by (vx,vy)
	 * 
	 * @param vx
	 *            x-coord of translation vector
	 * @param vy
	 *            y-coord of translation vector
	 */
	public void translate(double vx, double vy) {

		// translate x
		if (!DoubleUtil.isZero(vx)) {
			translateX(expression, vx, 0);
		}
		// translate y
		if (!DoubleUtil.isZero(vy)) {
			translateX(expression, vy, 1);
		}

		// make sure that expression object is changed!
		// this is needed to know that the expression has changed
		if (expression.isLeaf() && expression.getLeft().isExpressionNode()) {
			expression = new ExpressionNode(
					(ExpressionNode) expression.getLeft());
		} else {
			expression = new ExpressionNode(expression);
		}
		invalidateIneqs();
	}

	private void invalidateIneqs() {
		this.ineqs = null;
	}

	/**
	 * translate the expression
	 * 
	 * @param vx
	 *            x-translation
	 * @param vy
	 *            y-translation
	 * @param vz
	 *            z-translation
	 */
	public void translate(double vx, double vy, double vz) {
		int zIndex;
		if (!DoubleUtil.isZero(vz) && (zIndex = getVarIndex("z")) != -1) {
			translateX(expression, vz, zIndex);
		}
		translate(vx, vy);
	}

	private int getVarIndex(String s) {
		for (int i = 0; i < fVars.length; i++) {
			if (s.equals(fVars[i].toString(StringTemplate.defaultTemplate))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * replace every x in tree by (x - vx) // i.e. replace fVar with (fvar - vx)
	 * 
	 * @param en
	 *            node
	 * @param vx
	 *            shift
	 * @param varNo
	 *            variable index
	 */
	protected void translateX(ExpressionNode en, double vx, int varNo) {
		ExpressionValue left = en.getLeft();
		ExpressionValue right = en.getRight();

		// left tree
		if (left == fVars[varNo]) {
			if (right instanceof MyDouble && right.isConstant()) { // is there a
																	// constant
																	// number to
																	// the
																	// right?
				MyDouble num = (MyDouble) right;
				double temp;
				switch (en.getOperation()) {
				case PLUS:
					temp = num.getDouble() - vx;
					if (DoubleUtil.isZero(temp)) {
						expression = expression.replace(en, fVars[varNo])
								.wrap();
					} else if (temp < 0) {
						en.setOperation(Operation.MINUS);
						num.set(-temp);
					} else {
						num.set(temp);
					}
					return;

				case MINUS:
					temp = num.getDouble() + vx;
					if (DoubleUtil.isZero(temp)) {
						expression = expression.replace(en, fVars[varNo])
								.wrap();
					} else if (temp < 0) {
						en.setOperation(Operation.PLUS);
						num.set(-temp);
					} else {
						num.set(temp);
					}
					return;

				default:
					en.setLeft(shiftXnode(vx, varNo));
				}
			} else {
				en.setLeft(shiftXnode(vx, varNo));
			}
		} else {
			translateExpressionX(left, vx, varNo);
		}

		// right tree
		if (right == fVars[varNo]) {
			en.setRight(shiftXnode(vx, varNo));
		} else {
			translateExpressionX(right, vx, varNo);
		}

	}

	private void translateExpressionX(ExpressionValue right, double vx,
			int varNo) {
		if (right instanceof ExpressionNode) {
			translateX((ExpressionNode) right, vx, varNo);
		} else if (right instanceof MyList) {
			for (int i = 0; i < ((MyList) right).size(); i++) {
				translateX(((MyList) right).getListElement(i).wrap(), vx,
						varNo);
			}
		} else if (right instanceof MyVecNode) {
			translateX(((MyVecNode) right).getX().wrap(), vx, varNo);
			translateX(((MyVecNode) right).getY().wrap(), vx, varNo);
		} else if (right instanceof MyVec3DNode) {
			translateX(((MyVec3DNode) right).getX().wrap(), vx, varNo);
			translateX(((MyVec3DNode) right).getY().wrap(), vx, varNo);
			translateX(((MyVec3DNode) right).getZ().wrap(), vx, varNo);
		}

	}

	/**
	 * Dilates node in single direction
	 * 
	 * @param en
	 *            node to dilate
	 * @param vx
	 *            coefficient
	 * @param varNo
	 *            variable in which to dilate
	 */
	protected void dilateX(ExpressionNode en, double vx, int varNo) {
		ExpressionValue left = en.getLeft();
		ExpressionValue right = en.getRight();

		// left tree
		if (left == fVars[varNo]) {
			// is there a constant number to the right?
			if (right instanceof MyDouble && right.isConstant()) {
				MyDouble num = (MyDouble) right;
				double temp;
				switch (en.getOperation()) {
				case MULTIPLY:
					temp = num.getDouble() / vx;
					if (DoubleUtil.isEqual(1, temp)) {
						expression = expression.replace(en, fVars[varNo])
								.wrap();
					} else {
						num.set(temp);
					}
					return;

				case DIVIDE:
					temp = num.getDouble() * vx;
					if (DoubleUtil.isEqual(1, temp)) {
						expression = expression.replace(en, fVars[varNo])
								.wrap();
					} else {
						num.set(temp);
					}
					return;

				default:
					en.setLeft(multXnode(vx, varNo));
				}
			} else {
				en.setLeft(multXnode(vx, varNo));
			}
		} else {
			dilateExpressionX(left, vx, varNo);
		}

		// right tree
		if (right == fVars[varNo]) {
			// is there a constant number to the left?
			if (left instanceof MyDouble && left.isConstant()) { 				
				MyDouble num = (MyDouble) left;
				double temp;
				switch (en.getOperation()) {
				case MULTIPLY:
					temp = num.getDouble() / vx;
					if (DoubleUtil.isEqual(1, temp)) {
						expression = expression.replace(en, fVars[varNo])
								.wrap();
					} else {
						num.set(temp);
					}
					return;

				default:
					en.setRight(multXnode(vx, varNo));
				}
			} else {
				en.setRight(multXnode(vx, varNo));
			}
		} else {
			dilateExpressionX(right, vx, varNo);
		}

	}

	private void dilateExpressionX(ExpressionValue right, double vx,
			int varNo) {
		if (right instanceof ExpressionNode) {
			dilateX((ExpressionNode) right, vx, varNo);
		} else if (right instanceof MyList) {
			for (int i = 0; i < ((MyList) right).size(); i++) {
				dilateX(((MyList) right).getListElement(i).wrap(), vx, varNo);
			}
		} else if (right instanceof MyVecNode) {
			dilateX(((MyVecNode) right).getX().wrap(), vx, varNo);
			dilateX(((MyVecNode) right).getY().wrap(), vx, varNo);
		} else if (right instanceof MyVec3DNode) {
			dilateX(((MyVec3DNode) right).getX().wrap(), vx, varNo);
			dilateX(((MyVec3DNode) right).getY().wrap(), vx, varNo);
			dilateX(((MyVec3DNode) right).getZ().wrap(), vx, varNo);
		}

	}

	// node for (x - vx)
	/**
	 * @param vx0
	 *            shift
	 * @param varNo
	 *            variable index
	 * @return variable with given index shifted by given amount
	 */
	protected ExpressionNode shiftXnode(double vx0, int varNo) {
		ExpressionNode node;
		double vx = DoubleUtil.checkDecimalFraction(vx0);
		if (vx > 0) {
			node = new ExpressionNode(kernel, fVars[varNo], Operation.MINUS,
					new MyDouble(kernel, vx));
		} else {
			node = new ExpressionNode(kernel, fVars[varNo], Operation.PLUS,
					new MyDouble(kernel, -vx));
		}
		return node;
	}

	/**
	 * @param vx0
	 *            coefficient
	 * @param varNo
	 *            variable index
	 * @return node coefficient * variable
	 */
	protected ExpressionNode multXnode(double vx0, int varNo) {
		double vx = DoubleUtil.checkDecimalFraction(1 / vx0);

		return new ExpressionNode(kernel, new MyDouble(kernel, vx),
				Operation.MULTIPLY, fVars[varNo]);

	}

	/**
	 * @param d
	 *            dilate factor
	 * @param s
	 *            coordinates
	 */
	public void dilate3D(NumberValue d, Coords s) {
		double r = 1.0 / d.getDouble();
		int zIndex = this.getVarIndex("z");
		if (zIndex != -1) {
			this.translate(-s.getX(), -s.getY(), -s.getZ());
			ExpressionNode newX = new ExpressionNode(kernel, d,
					Operation.MULTIPLY, fVars[0]);
			ExpressionNode newY = new ExpressionNode(kernel, d,
					Operation.MULTIPLY, fVars[1]);
			ExpressionNode newZ = new ExpressionNode(kernel, d,
					Operation.MULTIPLY, fVars[zIndex]);
			expression = expression.replace(fVars[0], newX).wrap();
			expression = expression.replace(fVars[1], newY).wrap();
			expression = expression.replace(fVars[zIndex], newZ).wrap();
			this.initIneqs(expression);
			this.translate(s.getX(), s.getY(), s.getZ());
		} else {
			this.translate(-s.getX(), -s.getY());
			this.matrixTransform(r, 0, 0, r);
			this.translate(s.getX(), s.getY());
			double dv = d.getDouble();
			expression = expression.multiply(dv).plus(s.getZ() * (1 - dv));
		}
	}

	/**
	 * Transforms this function using matrix {{a00,a01},{a01,a11}}
	 * 
	 * @param a00
	 *            a00
	 * @param a01
	 *            a01
	 * @param a10
	 *            a10
	 * @param a11
	 *            a11
	 */
	public void matrixTransform(double a00, double a01, double a10,
			double a11) {
		ExpressionNode dummy = new ExpressionNode();
		expression.replace(fVars[0], dummy).wrap();
		MyDouble ma00 = new MyDouble(kernel, a00);
		MyDouble ma01 = new MyDouble(kernel, a01);
		MyDouble ma10 = new MyDouble(kernel, a10);
		MyDouble ma11 = new MyDouble(kernel, a11);

		ExpressionNode newX = fVars[0].wrap().multiply(ma00)
				.plus(fVars[1].wrap().multiply(ma01));
		ExpressionNode newY = fVars[0].wrap().multiply(ma10)
				.plus(fVars[1].wrap().multiply(ma11));

		expression = expression
				.traverse(CopyReplacer.getReplacer(fVars[1], newY, kernel))
				.wrap();
		expression = expression
				.traverse(CopyReplacer.getReplacer(dummy, newX, kernel)).wrap();
		invalidateIneqs();
	}

	/**
	 * 
	 * @param a00
	 *            a00
	 * @param a01
	 *            a11
	 * @param a02
	 *            a02
	 * @param a10
	 *            a10
	 * @param a11
	 *            a11
	 * @param a12
	 *            a12
	 * @param a20
	 *            a20
	 * @param a21
	 *            a21
	 * @param a22
	 *            a22
	 */
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		ExpressionNode dummy = new ExpressionNode();
		expression = expression.replace(fVars[0], dummy).wrap();
		double[][] b = MyMath.adjoint(a00, a01, a02, a10, a11, a12, a20, a21,
				a22);
		MyDouble[][] mbTrans = new MyDouble[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				mbTrans[i][j] = new MyDouble(kernel, b[j][i]);
			}
		}
		ExpressionNode newZ = new ExpressionNode(kernel, mbTrans[2][0],
				Operation.MULTIPLY, fVars[0])
						.plus(new ExpressionNode(kernel, mbTrans[2][1],
								Operation.MULTIPLY, fVars[1]))
						.plus(mbTrans[2][2]);
		ExpressionNode newX = new ExpressionNode(kernel, mbTrans[0][0],
				Operation.MULTIPLY, fVars[0])
						.plus(new ExpressionNode(kernel, mbTrans[0][1],
								Operation.MULTIPLY, fVars[1]))
						.plus(mbTrans[0][2]);
		ExpressionNode newY = new ExpressionNode(kernel, mbTrans[1][0],
				Operation.MULTIPLY, fVars[0])
						.plus(new ExpressionNode(kernel, mbTrans[1][1],
								Operation.MULTIPLY, fVars[1]))
						.plus(mbTrans[1][2]);
		expression = expression.traverse(
				CopyReplacer.getReplacer(fVars[1], newY.divide(newZ), kernel))
				.wrap();
		expression = expression.traverse(
				CopyReplacer.getReplacer(dummy, newX.divide(newZ), kernel))
				.wrap();
		this.initIneqs(expression);
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue ev = t.process(this);
		if (ev != this) {
			return ev;
		}
		expression = expression.traverse(t).wrap();
		return this;
	}

	@Override
	public boolean inspect(Inspecting t) {
		return t.check(this) || expression.inspect(t);
	}

	@Override
	public ExpressionNode getFunctionExpression() {
		return expression;
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public void setDefined(boolean b) {
		// nothing to do
	}

	/**
	 * @param fv
	 *            variable with respect to which the derivative is computed
	 * @param n
	 *            order of derivative
	 * @return derivative calculated without the CAS
	 */
	public FunctionNVar getDerivativeNoCAS(FunctionVariable fv, int n) {

		ExpressionNode expDeriv = expression;

		for (int i = 0; i < n; i++) {
			expDeriv = expDeriv.derivative(fv, kernel);
		}
		expDeriv = expDeriv.shallowCopy();
		return new FunctionNVar(expDeriv, fVars);
	}

	/**
	 * @param fv
	 *            variable with respect to which the derivative is computed
	 * @return integral calculated without the CAS (will work only for very
	 *         simple functions eg sin(3x))
	 */
	public FunctionNVar getIntegralNoCAS(FunctionVariable fv) {

		return new FunctionNVar(expression.integral(fv, kernel), fVars);
	}

	/**
	 * Make sure the variable list is at least as long as oldvars
	 * 
	 * @param oldVars
	 *            list of variables to be used for filling in the empty slots
	 */
	public void fillVariables(FunctionVariable[] oldVars) {
		if (oldVars == null) {
			return;
		}
		int length = oldVars.length;
		if (fVars.length >= length) {
			return;
		}
		FunctionVariable[] newVars = new FunctionVariable[length];
		TreeSet<String> usedNames = new TreeSet<>();
		for (int i = 0; i < fVars.length; i++) {
			newVars[i] = fVars[i];
			usedNames.add(fVars[i].toString(StringTemplate.defaultTemplate));
		}
		int pos = fVars.length;
		for (int i = 0; i < oldVars.length && pos < length; i++) {
			if (!usedNames.contains(
					oldVars[i].toString(StringTemplate.defaultTemplate))) {
				newVars[pos] = oldVars[i];
				pos++;
			}
		}
		fVars = newVars;

	}

	@Override
	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	@Override
	public ExpressionValue derivative(FunctionVariable fv, Kernel kernel0) {
		return expression.derivative(fv, kernel0);
	}

	@Override
	public final ExpressionValue integral(FunctionVariable fv, Kernel kernel0) {
		return expression.integral(fv, kernel0);
	}

	@Override
	public final boolean hasCoords() {
		return expression.hasCoords();
	}

	@Override
	public ValueType getValueType() {
		return ValueType.FUNCTION;
	}

	/**
	 * @param map
	 *            map from XML in the string -> string form
	 */
	public void updateCASEvalMap(TreeMap<String, String> map) {
		if (map == null) {
			return;
		}

		for (Entry<String, String> entry : map.entrySet()) {
			FunctionalNVar gfun = kernel.getAlgebraProcessor()
					.evaluateToFunctionNVar(entry.getValue(), true, true);
			if (gfun != null) {
				getCasEvalMap().put(entry.getKey(), gfun.getFunction());
			}
		}
	}

	/**
	 * @param r
	 *            dilation factor
	 * @param S
	 *            dilation center
	 */
	public void dilate(NumberValue r, Coords S) {
		translate(-S.getX(), -S.getY());
		matrixTransform(1 / r.getDouble(), 0, 0, 1 / r.getDouble());
		translate(S.getX(), S.getY());
	}

	/**
	 * @param phi
	 *            angle (in rad)
	 */
	public void rotate(NumberValue phi) {
		double cosPhi = Math.cos(phi.getDouble());
		double sinPhi = Math.sin(phi.getDouble());
		matrixTransform(cosPhi, sinPhi, -sinPhi, cosPhi);
	}

	/**
	 * @param v
	 *            translation coordinates
	 */
	public void translate(Coords v) {
		translate(v.getX(), v.getY());
	}

	private void mirror(double phi) {
		double cosPhi = Math.cos(phi);
		double sinPhi = Math.sin(phi);
		matrixTransform(cosPhi, sinPhi, sinPhi, -cosPhi);
	}

	/**
	 * @param g
	 *            line
	 */
	public void mirror(GeoLine g) {

		double qx, qy;
		if (Math.abs(g.getX()) > Math.abs(g.getY())) {
			qx = g.getZ() / g.getX();
			qy = 0.0d;
		} else {
			qx = 0.0d;
			qy = g.getZ() / g.getY();
		}

		// translate -Q
		translate(qx, qy);

		// S(phi)
		mirror(2.0 * Math.atan2(-g.getX(), g.getY()));

		// translate back +Q
		translate(-qx, -qy);

	}

	/**
	 * @param phi
	 *            angle
	 * @param p
	 *            center
	 */
	public void rotate(NumberValue phi, Coords p) {
		translate(-p.getX(), -p.getY());
		rotate(phi);
		translate(p.getX(), p.getY());

	}

	@Override
	public String getShortLHS() {
		return shortLHS;
	}

	@Override
	public void setShortLHS(String s) {
		this.shortLHS = s;
	}

	@Override
	public void setSecret(AlgoElement algo) {
		getExpression().setSecret(algo);
	}

	@Override
	public boolean isForceInequality() {
		return forceInequality;
	}

	@Override
	public void setForceInequality(boolean forceInequality) {
		this.forceInequality = forceInequality;
	}
}
