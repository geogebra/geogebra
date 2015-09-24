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
import java.util.TreeSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.Inequality.IneqType;
import org.geogebra.common.kernel.arithmetic.Traversing.VariablePolyReplacer;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.MaxSizeHashMap;
import org.geogebra.common.util.MyMath;

/**
 * Function of N variables that returns either a number or a boolean. This
 * depends on the expression this function is based on.
 * 
 * @author Markus Hohenwarter + mathieu
 */
public class FunctionNVar extends ValidExpression implements FunctionalNVar,
		VarString {

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
	protected Kernel kernel;

	/**
	 * Creates new Function from expression. Note: call initFunction() after
	 * this constructor.
	 * 
	 * @param expression
	 *            function expression
	 */
	public FunctionNVar(ExpressionNode expression) {
		kernel = expression.getKernel();

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

		for (int i = 0; i < fVars.length; i++) {
			if (fVars[i].toString(StringTemplate.defaultTemplate).equals(var)) {
				return true;
			}
		}
		return false; // if none of function vars equals var
	}

	/**
	 * @return kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	public FunctionNVar deepCopy(Kernel kernel1) {
		return new FunctionNVar(this, kernel1);
	}

	/**
	 * @return function expression
	 */
	final public ExpressionNode getExpression() {
		return expression;
	}

	public void resolveVariables() {
		expression.resolveVariables();
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

	public FunctionNVar getFunction() {
		return this;
	}

	/**
	 * Returns array of all variables
	 * 
	 * @return array of variables
	 */
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
			sb.append(", ");
		}
		sb.append(fVars[fVars.length - 1].toString(tpl));
		return sb;
	}

	/**
	 * Call this function to resolve variables and init the function. May throw
	 * MyError (InvalidFunction).
	 * 
	 * @return whether this is a valid (numeric or boolean) function
	 */
	public boolean initFunction() {
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
		expression.resolveVariables();

		// the idea here was to allow something like: Derivative[f] + 3x
		// but wrapping the GeoFunction objects as ExpressionNodes of type
		// FUNCTION
		// leads to Derivative[f](x) + 3x
		// expression.wrapGeoFunctionsAsExpressionNode();

		// replace all polynomials in expression (they are all equal to "1x" if
		// we got this far)
		// by an instance of MyDouble

		// simplify constant parts in expression
		expression.simplifyConstantIntegers();

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
		// get function variables for x, y, z
		FunctionVariable xVar = null, yVar = null, zVar = null;
		for (FunctionVariable fVar : fVars) {
			if ("x".equals(fVar.toString(StringTemplate.defaultTemplate)))
				xVar = fVar;
			else if ("y".equals(fVar.toString(StringTemplate.defaultTemplate)))
				yVar = fVar;
			else if ("z".equals(fVar.toString(StringTemplate.defaultTemplate)))
				zVar = fVar;
		}

		// try to replace x(x+1) by x*(x+1)
		undecided.clear();
		expression.replaceXYZnodes(xVar, yVar, zVar, undecided);
		for (ExpressionNode en : undecided)
			en.setOperation(Operation.MULTIPLY);
		undecided.clear();
	}

	private static ArrayList<ExpressionNode> undecided = new ArrayList<ExpressionNode>();

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
		} else if (ev instanceof FunctionNVar) {
			expression = ((FunctionNVar) ev).getExpression();
			fVars = ((FunctionNVar) ev).getFunctionVariables();
		} else if (ev instanceof GeoFunction) {
			expression = ((GeoFunction) ev).getFunctionExpression();
			fVars = ((GeoFunction) ev).getFunction().getFunctionVariables();
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Returns whether this function always evaluates to BooleanValue.
	 */
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
		if (isConstantFunction)
			return true;
		for (int i = 0; i < fVars.length; i++)
			if (expression.contains(fVars[i]))
				return false;
		return true; // none of the vars appears in the expression
	}

	public boolean isConstant() {
		return false;
	}

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
	final public double evaluate(double[] vals) {
		if (isBooleanFunction) {
			// BooleanValue
			return evaluateBoolean(vals) ? 1 : 0;
		}
		// NumberValue
		for (int i = 0; i < fVars.length; i++) {
			// Application.debug(fVars[i].toString()+" <= "+vals[i]);
			fVars[i].set(vals[i]);
		}
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
	final public boolean evaluateBoolean(double[] vals) {
		for (int i = 0; i < fVars.length; i++)
			fVars[i].set(vals[i]);
		return ((BooleanValue) expression
				.evaluate(StringTemplate.defaultTemplate)).getBoolean();
	}

	public HashSet<GeoElement> getVariables() {
		return expression.getVariables();
	}

	/**
	 * 
	 * @return GeoElements acting as variables
	 */
	public GeoElement[] getGeoElementVariables() {
		return expression.getGeoElementVariables();
	}

	@Override
	public String toString(StringTemplate tpl) {
		return expression.toString(tpl);
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		return expression.toValueString(tpl);
	}

	final public String toOutputValueString(StringTemplate tpl) {
		return expression.toOutputValueString(tpl);
	}

	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return expression.toLaTeXString(symbolic, tpl);
	}

	/* ***************
	 * CAS Stuff **************
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
		// see http://www.geogebra.org/trac/ticket/1466
		symbolic = symbolic && !expression.containsGeoFunctionNVar();

		// make sure to use temporary variable names
		// e.g. a in Derivative[a*x^2,x] needs to be renamed temporarily when a
		// exists in GeoGebra
		// see http://www.geogebra.org/trac/ticket/929

		StringTemplate tpl = StringTemplate.prefixedDefault;
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
			useCaching = symbolic && !expression.containsCasEvaluableFunction();
		}

		// build command string for CAS
		String expString = symbolic ? casEvalStringSymbolic : expression
				.getCASstring(tpl, false);

		// substitute % by expString in ggbCasCmd
		String casString = ggbCasCmd.replaceAll("%", expString);
		FunctionNVar resultFun = null;

		// eval with CAS
		try {
			if (useCaching) {
				// check if result is in cache
				resultFun = lookupCasEvalMap(casString);
				if (resultFun != null) {
					// System.out.println("caching worked: " + casString +
					// " -> " + resultFun);
					return resultFun;
				}
			}
			App.debug(casString);
			// evaluate expression by CAS
			String result = symbolic ? kernel.evaluateGeoGebraCAS(casString,
					arbconst) : // symbolic
					kernel.evaluateCachedGeoGebraCAS(casString, arbconst); // value
																			// string
			// System.out.println("evaluateGeoGebraCAS: " + casString + " -> " +
			// result);

			// parse CAS result back into GeoGebra
			sb.setLength(0);
			sb.append("f("); // this name is never used, just needed for parsing
			sb.append(getVarString(StringTemplate.defaultTemplate));
			sb.append(") = ");
			sb.append(result);

			// parse result
			if (getVarNumber() == 1) {
				resultFun = (kernel.getParser().parseFunction(sb.toString()));
			} else {
				resultFun = (kernel.getParser()
						.parseFunctionNVar(sb.toString()));
			}

			resultFun.initFunction();
		} catch (Error err) {
			err.printStackTrace();
			resultFun = null;
		} catch (Exception e) {
			e.printStackTrace();
			resultFun = null;
		} catch (Throwable e) {
			resultFun = null;
		}

		// cache result
		if (useCaching && resultFun != null) {
			getCasEvalMap().put(casString, resultFun);
		}

		// System.out.println("NO caching: " + casString + " -> " + resultFun);

		return resultFun;
	}

	private ExpressionNode casEvalExpression;
	private String casEvalStringSymbolic;

	private MaxSizeHashMap<String, FunctionNVar> getCasEvalMap() {
		if (casEvalMap == null) {
			casEvalMap = new MaxSizeHashMap<String, FunctionNVar>(
					MAX_CAS_EVAL_MAP_SIZE);
		}
		return casEvalMap;
	}

	private FunctionNVar lookupCasEvalMap(String casString) {
		if (casEvalMap == null) {
			return null;
		}
		return casEvalMap.get(casString);
	}

	/**
	 * Clears the cache (needed in Web when the CAS loads)
	 * 
	 * @param label
	 *            not used
	 */
	public void clearCasEvalMap(String label) {
		if (casEvalMap == null)
			return;
		casEvalMap.clear();
	}

	private final static int MAX_CAS_EVAL_MAP_SIZE = 100;
	private MaxSizeHashMap<String, FunctionNVar> casEvalMap;

	public boolean isNumberValue() {
		return false;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	@Override
	public String getLabelForAssignment() {
		StringBuilder sb = new StringBuilder();
		// function, e.g. f(x) := 2*x
		sb.append(getLabel());
		sb.append("(");
		sb.append(getVarString(StringTemplate.defaultTemplate));
		sb.append(")");
		return sb.toString();
	}

	public IneqTree getIneqs() {
		return ineqs;
	}

	/**
	 * initializes inequalities
	 * 
	 * @param fe
	 *            expression node
	 * @param functional
	 *            function to which ineqs are associated
	 * @return true if the functions consists of inequalities
	 */
	public boolean initIneqs(ExpressionNode fe, FunctionalNVar functional) {
		if (ineqs == null || fe == getExpression())
			ineqs = new IneqTree();
		boolean b = initIneqs(fe, functional, ineqs, false);
		ineqs.recomputeSize();
		return b;
	}

	private boolean initIneqs(ExpressionNode fe, FunctionalNVar functional,
			IneqTree tree, boolean negate) {
		Operation op = fe.getOperation();
		ExpressionNode leftTree = fe.getLeftTree();
		ExpressionNode rightTree = fe.getRightTree();
		if (op.equals(Operation.GREATER) || op.equals(Operation.GREATER_EQUAL)
				|| op.equals(Operation.LESS) || op.equals(Operation.LESS_EQUAL)) {
			Inequality newIneq = new Inequality(kernel, leftTree, rightTree,
					adjustOp(op, negate), getFunction().getFunctionVariables(),
					functional);
			if (newIneq.getType() != IneqType.INEQUALITY_INVALID) {
				if (newIneq.getType() != IneqType.INEQUALITY_1VAR_X
						&& newIneq.getType() != IneqType.INEQUALITY_1VAR_Y)
					newIneq.getBorder().setInverseFill(newIneq.isAboveBorder());
				tree.setIneq(newIneq);
			}
			return newIneq.getType() != IneqType.INEQUALITY_INVALID;
		} else if (op.equals(Operation.AND)
				|| op.equals(Operation.AND_INTERVAL) || op.equals(Operation.OR)
				|| op.equals(Operation.EQUAL_BOOLEAN)
				|| op.equals(Operation.NOT_EQUAL)) {
			tree.setOperation(adjustOp(op, negate));
			tree.setLeft(new IneqTree());
			tree.setRight(new IneqTree());
			return initIneqs(leftTree, functional, tree.getLeft(), negate)
					&& initIneqs(rightTree, functional, tree.getRight(), negate);
		} else if (op.equals(Operation.NOT)) {
			return initIneqs(leftTree, functional, tree, !negate);
		} else if (op.equals(Operation.IMPLICATION)) {
			tree.setOperation(Operation.OR);
			tree.setLeft(new IneqTree());
			tree.setRight(new IneqTree());
			return initIneqs(leftTree, functional, tree.getLeft(), !negate)
					&& initIneqs(rightTree, functional, tree.getRight(), negate);
		} else if (op.equals(Operation.FUNCTION_NVAR)) {
			FunctionalNVar nv = (FunctionalNVar) leftTree.getLeft();
			ExpressionNode subExpr = nv.getFunction().getExpression()
					.getCopy(kernel);
			FunctionVariable[] subVars = nv.getFunction()
					.getFunctionVariables();
			for (int i = 0; i < subVars.length; i++)
				subExpr.replace(subVars[i],
						((MyList) rightTree.getLeft()).getListElement(i));
			return initIneqs(subExpr, functional, tree, negate);
		} else
			return false;

	}

	private static Operation adjustOp(Operation op, boolean negate) {
		if (negate == false)
			return op;
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
		if (ineqs == null)
			return false;
		return ineqs.updateCoef();
	}

	/**
	 * Evaluates function at given point
	 * 
	 * @param pt
	 *            point for evaluation
	 * @return function value
	 */
	public double evaluate(GeoPoint pt) {
		if (fVars.length == 1
				&& "y".equals(fVars[0].toString(StringTemplate.defaultTemplate)))
			return evaluate(new double[] { pt.y / pt.z });
		return evaluate(new double[] { pt.x / pt.z, pt.y / pt.z });
	}

	/**
	 * Evaluates function at given point as boolean
	 * 
	 * @param pt
	 *            point for evaluation
	 * @return function value
	 */
	public boolean evaluateBoolean(GeoPoint pt) {
		if (fVars.length == 1
				&& "y".equals(fVars[0].toString(StringTemplate.defaultTemplate)))
			return evaluateBoolean(new double[] { pt.y / pt.z });
		return evaluateBoolean(new double[] { pt.x / pt.z, pt.y / pt.z });
	}

	/**
	 * Transletes the function by (vx,vy)
	 * 
	 * @param vx
	 *            x-coord of translation vector
	 * @param vy
	 *            y-coord of translation vector
	 */
	public void translate(double vx, double vy) {

		// translate x
		if (!Kernel.isZero(vx)) {
			translateX(expression, vx, 0);

		}
		if (!Kernel.isZero(vy)) {
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
		if (!Kernel.isZero(vz) && (zIndex = getVarIndex("z")) != -1) {
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

	// replace every x in tree by (x - vx)
	// i.e. replace fVar with (fvar - vx)
	private void translateX(ExpressionNode en, double vx, int varNo) {
		ExpressionValue left = en.getLeft();
		ExpressionValue right = en.getRight();

		// left tree
		if (left == fVars[varNo]) {
			try { // is there a constant number to the right?
				MyDouble num = (MyDouble) right;
				double temp;
				switch (en.getOperation()) {
				case PLUS:
					temp = num.getDouble() - vx;
					if (Kernel.isZero(temp)) {
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
					if (Kernel.isZero(temp)) {
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
			} catch (Exception e) {
				en.setLeft(shiftXnode(vx, varNo));
			}
		} else if (left instanceof ExpressionNode) {
			translateX((ExpressionNode) left, vx, varNo);
		}

		// right tree
		if (right == fVars[varNo]) {
			en.setRight(shiftXnode(vx, varNo));
		} else if (right instanceof ExpressionNode) {
			translateX((ExpressionNode) right, vx, varNo);
		}
	}

	// node for (x - vx)
	private ExpressionNode shiftXnode(double vx, int varNo) {
		ExpressionNode node;
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
			this.initIneqs(expression, this);
			this.translate(s.getX(), s.getY(), s.getZ());
		} else {
			this.translate(-s.getX(), -s.getY());
			this.matrixTransform(r, 0, 0, r);
			this.translate(s.getX(), s.getY());
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
	public void matrixTransform(double a00, double a01, double a10, double a11) {
		ExpressionNode dummy = new ExpressionNode();
		expression.replace(fVars[0], dummy).wrap();
		MyDouble ma00 = new MyDouble(kernel, a00);
		MyDouble ma01 = new MyDouble(kernel, a01);
		MyDouble ma10 = new MyDouble(kernel, a10);
		MyDouble ma11 = new MyDouble(kernel, a11);

		ExpressionNode newX = new ExpressionNode(kernel, ma00,
				Operation.MULTIPLY, fVars[0]).plus(new ExpressionNode(kernel,
				ma01, Operation.MULTIPLY, fVars[1]));
		ExpressionNode newY = new ExpressionNode(kernel, ma10,
				Operation.MULTIPLY, fVars[0]).plus(new ExpressionNode(kernel,
				ma11, Operation.MULTIPLY, fVars[1]));
		expression = expression.replace(fVars[1], newY).wrap();
		expression = expression.replace(dummy, newX).wrap();
		this.initIneqs(expression, this);
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
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				mbTrans[i][j] = new MyDouble(kernel, b[j][i]);
		ExpressionNode newZ = new ExpressionNode(kernel, mbTrans[2][0],
				Operation.MULTIPLY, fVars[0]).plus(
				new ExpressionNode(kernel, mbTrans[2][1], Operation.MULTIPLY,
						fVars[1])).plus(mbTrans[2][2]);
		ExpressionNode newX = new ExpressionNode(kernel, mbTrans[0][0],
				Operation.MULTIPLY, fVars[0]).plus(
				new ExpressionNode(kernel, mbTrans[0][1], Operation.MULTIPLY,
						fVars[1])).plus(mbTrans[0][2]);
		ExpressionNode newY = new ExpressionNode(kernel, mbTrans[1][0],
				Operation.MULTIPLY, fVars[0]).plus(
				new ExpressionNode(kernel, mbTrans[1][1], Operation.MULTIPLY,
						fVars[1])).plus(mbTrans[1][2]);
		expression = expression.replace(fVars[1], newY.divide(newZ)).wrap();
		expression = expression.replace(dummy, newX.divide(newZ)).wrap();
		this.initIneqs(expression, this);
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue ev = t.process(this);
		if (ev != this)
			return ev;
		expression = expression.traverse(t).wrap();
		return this;
	}

	@Override
	public boolean inspect(Inspecting t) {
		return t.check(this) || expression.inspect(t);
	}

	public ExpressionNode getFunctionExpression() {
		return expression;
	}

	public boolean isDefined() {
		return true;
	}

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
		expDeriv.simplifyConstantIntegers();
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
		TreeSet<String> usedNames = new TreeSet<String>();
		for (int i = 0; i < fVars.length; i++) {
			newVars[i] = fVars[i];
			usedNames.add(fVars[i].toString(StringTemplate.defaultTemplate));
		}
		int pos = fVars.length;
		for (int i = 0; i < oldVars.length && pos < length; i++) {
			if (!usedNames.contains(oldVars[i]
					.toString(StringTemplate.defaultTemplate))) {
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
	public final boolean hasCoords(){
		return expression.hasCoords();
	}

	@Override
	public ValueType getValueType() {
		return ValueType.FUNCTION;
	}

}
