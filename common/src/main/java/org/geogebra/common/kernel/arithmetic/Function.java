/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.arithmetic;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.roots.RealRootDerivFunction;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * Function of one variable x that returns either a number or a boolean. This
 * depends on the expression this function is based on.
 * 
 * @author Markus Hohenwarter
 */
public class Function extends FunctionNVar implements
		Functional, RealRootDerivFunction {

	/** function expression */
	private Function derivative;

	private static final double MAX_EXPAND_DEGREE = 10;

	/**
	 * Creates new Function from expression where x is the variable. Note: call
	 * {@link #initFunction()} after this constructor.
	 * 
	 * @param expression
	 *            function expression
	 */
	public Function(ExpressionNode expression) {
		super(expression);
	}

	/**
	 * Creates new Function from expression where the function variable in
	 * expression is already known.
	 * 
	 * @param exp
	 *            function expression
	 * @param fVar
	 *            function variable
	 */
	public Function(ExpressionNode exp, FunctionVariable fVar) {
		super(exp, new FunctionVariable[] { fVar });
	}

	/**
	 * Creates a Function that has no expression yet. Use setExpression() to do
	 * this later.
	 * 
	 * @param kernel
	 *            kernel
	 */
	public Function(Kernel kernel) {
		super(kernel);
		fVars = new FunctionVariable[1];
	}

	/**
	 * Copy constructor
	 * 
	 * @param f
	 *            source function
	 * @param kernel
	 *            kernel
	 */
	public Function(Function f, Kernel kernel) {
		super(f.expression.getCopy(kernel));
		fVars = f.fVars; // no deep copy of function variable
		isBooleanFunction = f.isBooleanFunction;
		isConstantFunction = f.isConstantFunction;

		this.kernel = kernel;
	}

	@Override
	public Function deepCopy(Kernel kernel1) {
		return new Function(this, kernel1);
	}

	/**
	 * Use this method only if you really know what you are doing.
	 * 
	 * @param exp
	 *            expression
	 * @param var
	 *            variable
	 */
	public void setExpression(ExpressionNode exp, FunctionVariable var) {
		super.setExpression(exp, new FunctionVariable[] { var });

		derivative = null;
	}

	@Override
	final public Function getFunction() {
		return this;
	}

	/**
	 * @return variable
	 */
	public FunctionVariable getFunctionVariable() {
		return fVars[0];
	}

	@Override
	final public String getVarString(final StringTemplate tpl) {
		if (fVars == null) {
			return tpl.printVariableName("x");
		}
		return fVars[0].toString(tpl);

	}


	/**
	 * Call this function to resolve variables and init the function. May throw
	 * MyError (InvalidFunction).
	 */
	@Override
	public boolean initFunction(boolean simplifyInt) {
		if (fVars == null) {
			// try function variable x
			fVars = new FunctionVariable[] { new FunctionVariable(kernel) };
		}

		return super.initFunction(simplifyInt);
	}

	/**
	 * Initializes function variables without resolving commands in the
	 * expression
	 */
	public void initFunctionVars() {
		if (fVars == null) {
			// try function variable x
			fVars = new FunctionVariable[] { new FunctionVariable(kernel) };
		}
	}

	/**
	 * Returns this function's value at position x.
	 * 
	 * @param x
	 *            position
	 * @return f(x)
	 */
	public double evaluate(double x) {
		if (isBooleanFunction) {
			// BooleanValue
			return evaluateBoolean(x) ? 1 : 0;
		}
		// NumberValue
		fVars[0].set(x);
		return expression.evaluateDouble();

	}

	/**
	 * Returns this function's value at position x. (Note: use this method if
	 * isBooleanFunction() returns true.
	 * 
	 * @param x
	 *            position
	 * @return f(x)
	 */
	final public boolean evaluateBoolean(double x) {
		fVars[0].set(x);
		return expression.evaluateBoolean();
	}

	/**
	 * Shifts the function by vx to right and by vy up
	 * 
	 * @param vx
	 *            horizontal shift
	 * @param vy
	 *            vertical shift
	 */
	@Override
	final public void translate(double vx, double vy) {
		boolean isLeaf = expression.isLeaf();
		ExpressionValue left = expression.getLeft();

		// translate x
		if (!Kernel.isZero(vx)) {
			if (isLeaf && left == fVars[0]) { // special case: f(x) = x
				expression = shiftXnode(vx, 0);
			} else {
				// replace every x in tree by (x - vx)
				// i.e. replace fVar with (fvar - vx)
				translateX(expression, vx, 0);
			}
		}

		// translate y
		if (!Kernel.isZero(vy)) {
				// f(x) = f(x) + vy
			translateY(vy);
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
	 * translates in y-coordinate
	 * 
	 * @param vy
	 *            y-coord difference
	 */
	final public void translateY(double vy) {
		expression = translateY(expression, fVars, vy);

	}
	
	/**
	 * Shifts the function by vy up
	 * 
	 * @param expr
	 *            original expression
	 * @param fVars
	 *            variables
	 * 
	 * @param vy
	 *            vertical translation
	 * @return translated expression
	 */
	final public static ExpressionNode translateY(ExpressionNode expr,
			FunctionVariable[] fVars,
			double vy) {
		ExpressionNode expression = expr.unwrap().wrap();
		// special case: constant
		if (expression.isLeaf() && expression.getLeft() != fVars[0]
				&& (expression.getLeft() instanceof NumberValue)) {
			MyDouble c = ((NumberValue) expression.getLeft()).getNumber();
			c.set(Kernel.checkDecimalFraction(c.getDouble() + vy));
			expression.setLeft(c);
			return expression;
		} else if (expression.getOperation() == Operation.IF) {

			expression.setRight(translateY(expression.getRight().wrap(), fVars,
					vy));
			return expression;
		}
 else if (expression.getOperation() == Operation.IF_ELSE) {

			MyNumberPair left = (MyNumberPair) expression.getLeft();
			left.setY(translateY(left.getY().unwrap().wrap(), fVars, vy));
			expression.setRight(translateY(expression.getRight().wrap(), fVars,
					vy));
			return expression;
		} else if (expression.getOperation() == Operation.IF_LIST) {

			MyList left = (MyList) expression.getRight();
			for(int i = 0; i < left.size();i++){
				left.setListElement(i,
						translateY(left.getListElement(i).unwrap().wrap(),
								fVars, vy));
			}
			
			return expression;
		} else if (expression.getOperation() == Operation.MULTIPLY
				&& ExpressionNode.isConstantDouble(expression.getLeft(), -1)) {
			expression.setRight(translateY(expression.getRight().wrap(), fVars,
					-vy));
			return expression;
		} else if (expression.getOperation() == Operation.PLUS) {
			expression.setRight(translateY(expression.getRight().wrap(), fVars,
					vy));
			return expression;
		} else if (expression.getOperation() == Operation.MINUS) {
			expression.setRight(translateY(expression.getRight().wrap(), fVars,
					-vy));
			return expression;
		}
		return addNumber(expression, vy);

	}

	final private static ExpressionNode addNumber(ExpressionNode expression,
			double n) {
		Kernel kernel = expression.getKernel();
		if (n > 0) {
			return new ExpressionNode(kernel, expression, Operation.PLUS,
					new MyDouble(kernel, n));
		}
		return new ExpressionNode(kernel, expression, Operation.MINUS,
				new MyDouble(kernel, -n));
	}

	/* ********************
	 * POLYNOMIAL FACTORING *******************
	 */

	// remember calculated factors
	// do factoring only if expression changed
	private ExpressionNode factorParentExp;

	// factors of polynomial function
	private LinkedList<PolyFunction> symbolicPolyFactorList;
	private LinkedList<PolyFunction> numericPolyFactorList;
	private boolean symbolicPolyFactorListDefined;

	/**
	 * Returns all non-constant polynomial factors of this function relevant for
	 * root finding. A list of PolyFunction (resp. SymbolicPolyFunction) objects
	 * is returned. Note: may return null if this function is no polynomial.
	 * 
	 * @param rootFindingSimplification
	 *            for root finding factors may be simplified, e.g. sqrt(x) may
	 *            be simplified to x
	 * @param avoidCAS
	 *            true to only use internal code without calling CAS
	 * @return all non-constant polynomial factors of this function
	 * 
	 */
	final public LinkedList<PolyFunction> getPolynomialFactors(
			boolean rootFindingSimplification, boolean avoidCAS) {
		// try to get symbolic polynomial factors
		LinkedList<PolyFunction> result = getSymbolicPolynomialFactors(
				rootFindingSimplification, avoidCAS);

		// if this didn't work try to get numeric polynomial factors
		if (result == null) {
			result = getNumericPolynomialFactors(rootFindingSimplification,
					avoidCAS);
		}
		return result;
	}

	/**
	 * Returns all non-constant polynomial factors of the n-th derivative of
	 * this function relevant for root finding. A list of PolyFunction (resp.
	 * SymbolicPolyFunction) objects is returned. Note: may return null if the
	 * n-th derivative is no polynomial.
	 * 
	 * @param n
	 *            derivative order
	 * 
	 * @param rootFindingSimplification
	 *            for root finding factors may be simplified, e.g. sqrt(x) may
	 *            be simplified to x
	 * @return all non-constant polynomial factors of the n-th derivative
	 */
	final public LinkedList<PolyFunction> getSymbolicPolynomialDerivativeFactors(
			int n, boolean rootFindingSimplification) {
		Function deriv = getDerivative(n, false);
		if (deriv == null)
			return null;

		// try to get symbolic polynomial factors
		return deriv.getSymbolicPolynomialFactors(rootFindingSimplification,
				false);
	}

	/**
	 * Tries to expand this function to a polynomial with numeric coefficients
	 * and returns its n-th derivative as a PolyFunction object. Note: may
	 * return null if the n-th derivative is no polynomial.
	 * 
	 * @param n
	 *            order
	 * @param skipCASfallback
	 *            when true, answer is computed without CAS; in case of failure
	 *            null is returned
	 * @return derivative
	 * 
	 */
	final public PolyFunction getNumericPolynomialDerivative(int n,
			boolean skipCASfallback) {
		// we expand the numerical expression of this function (all variables
		// are
		// replaced by their values) and try to get a polynomial.
		// Then we take the derivative of this polynomial.
		PolyFunction poly = expandToPolyFunction(expression, false,
				skipCASfallback);
		if (poly != null) { // we got a polynomial
			for (int i = 0; i < n; i++) {
				poly = poly.getDerivative();
			}
		}
		return poly;
	}

	/**
	 * Tries to expand this function to a polynomial with numeric coefficients
	 * and returns its integral as a PolyFunction object. (without +c) Note: may
	 * return null if it's not a polynomial.
	 * 
	 * @return integral
	 * 
	 */
	final public PolyFunction getNumericPolynomialIntegral() {
		// we expand the numerical expression of this function (all variables
		// are
		// replaced by their values) and try to get a polynomial.
		// Then we take the integral of this polynomial.
		PolyFunction poly = expandToPolyFunction(expression, false, true);
		if (poly != null) { // we got a polynomial
			poly = poly.getIntegral();

		}
		return poly;
	}

	/**
	 * Returns all symbolic non-constant polynomial factors of this function
	 * relevant for root finding. A list of PolyFunction (resp.
	 * SymbolicPolyFunction) objects is returned. Note: may return null if this
	 * function is no polynomial.
	 * 
	 * @param rootFindingSimplification
	 *            for root finding factors may be simplified, e.g. sqrt(x) may
	 *            be simplified to x
	 * @param assumeFalseIfCASNeeded
	 *            if we can't resolve this as polynomial without CAS and this
	 *            flag is tue, we assume it's not a polynomial
	 * @return all symbolic non-constant polynomial factors of this function
	 */
	public LinkedList<PolyFunction> getSymbolicPolynomialFactors(
			boolean rootFindingSimplification, boolean assumeFalseIfCASNeeded) {
		if (factorParentExp != expression) {
			// new expression
			factorParentExp = expression;

			if (symbolicPolyFactorList == null)
				symbolicPolyFactorList = new LinkedList<PolyFunction>();
			else
				symbolicPolyFactorList.clear();
			symbolicPolyFactorListDefined = addPolynomialFactors(expression,
					symbolicPolyFactorList, true, rootFindingSimplification,
					assumeFalseIfCASNeeded);
		}

		if (symbolicPolyFactorListDefined && symbolicPolyFactorList.size() > 0) {
			return symbolicPolyFactorList;
		}
		return null;
	}

	/**
	 * Returns all numeric non-constant polynomial factors of this function
	 * relevant for root finding. A list of SymbolicPolyFunction objects is
	 * returned. Note: may return null if this function is no polynomial.
	 * 
	 * Note: we use the values of variables here (different to
	 * getSymbolicPolynomialFactors()).
	 * 
	 * @param rootFindingSimplification
	 *            for root finding factors may be simplified, e.g. sqrt(x) may
	 *            be simplified to x
	 */
	private LinkedList<PolyFunction> getNumericPolynomialFactors(
			boolean rootFindingSimplification, boolean avoidCAS) {
		if (numericPolyFactorList == null)
			numericPolyFactorList = new LinkedList<PolyFunction>();
		else
			numericPolyFactorList.clear();

		boolean success = addPolynomialFactors(expression,
				numericPolyFactorList, false, rootFindingSimplification,
				avoidCAS);
		if (success && numericPolyFactorList.size() > 0) {
			return numericPolyFactorList;
		}
		return null;
	}

	/**
	 * Adds all polynomial factors in ev to the given list (ev is an
	 * ExpressionNode in the beginning).
	 * 
	 * @return false when a non-polynomial was found (e.g. sin(x))
	 * @param symbolic
	 *            true for symbolic coefficients, false for numeric coefficients
	 * @param rootFindingSimplification
	 *            for root finding factors may be simplified, e.g. sqrt(x) may
	 *            be simplified to x
	 */
	private boolean addPolynomialFactors(ExpressionValue ev,
			List<PolyFunction> l, boolean symbolic,
			boolean rootFindingSimplification, boolean assumeFalseIfCASNeeded) {
		if (ev.isExpressionNode()) {
			ExpressionNode node = (ExpressionNode) ev;
			if (node.isConditionalDeep()) {
				return false;
			}
			switch (node.getOperation()) {
			case MULTIPLY:
				return addPolynomialFactors(node.getLeft(), l, symbolic,
						rootFindingSimplification, assumeFalseIfCASNeeded)
						&& addPolynomialFactors(node.getRight(), l, symbolic,
								rootFindingSimplification,
								assumeFalseIfCASNeeded);

				// try some simplifications of factors for root finding
			case POWER:
			case DIVIDE:
				if (!rootFindingSimplification)
					break;

				// divide: x in denominator: no polynomial
				// power: x in exponent: no polynomial
				if (node.getRight().contains(fVars[0]))
					return false;

				// power:
				// symbolic: non-zero constants in exponent may be omitted
				// numeric: non-zero values in exponent may be omitted
				if (!symbolic || node.getRight().isConstant()) {
					double rightVal;
					try {
						rightVal = node.getRight().evaluateDouble();
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
					if (node.getOperation().equals(Operation.POWER)) {
						if (Kernel.isZero(rightVal))
							// left^0 = 1
							return addPolynomialFactors(
									new MyDouble(kernel, 1), l, symbolic,
									rootFindingSimplification,
									assumeFalseIfCASNeeded);
						else if (rightVal > 0)
							// left ^ right = 0 <=> left = 0 for right > 0
							return addPolynomialFactors(node.getLeft(), l,
									symbolic, rootFindingSimplification,
									assumeFalseIfCASNeeded);
					} else { // division
						if (Kernel.isZero(rightVal)) {
							// left / 0 = undefined
							return false;
						}
						// left / right = 0 <=> left = 0 for right != null
						return addPolynomialFactors(node.getLeft(), l,
								symbolic, rootFindingSimplification,
								assumeFalseIfCASNeeded);
					}
				}
				break;

			case ABS:
			case SGN:
			case SQRT:
				if (!rootFindingSimplification)
					break;

				// these functions can be omitted as f(x) = 0 iff x = 0
				return addPolynomialFactors(node.getLeft(), l, symbolic,
						rootFindingSimplification, assumeFalseIfCASNeeded);
			default:
				break;
			}
		}

		// if we get here we have to add the ExpressionValue ev
		// add only non constant factors that are relevant for root finding
		if (!ev.isConstant()) {
			// build the factor: expanded ev, get the coefficients and build
			// a polynomial with them
			PolyFunction factor = expandToPolyFunction(ev, symbolic,
					assumeFalseIfCASNeeded);
			if (factor == null)
				return false; // did not work
			l.add(factor);
		}
		return true;
	}

	/**
	 * Expands the given expression and builds a PolyFunction (or
	 * SymbolicPolyFunction) object with the coefficients of the resulting
	 * polynomial.
	 * 
	 * @param ev
	 *            expression value to be expanded
	 * 
	 * @return null when node is not a polynomial
	 * @param symbolic
	 *            true for symbolic coefficients (SymbolicPolyFunction), false
	 *            for numeric coefficients (PolyFunction)
	 * @param assumeFalseIfCASNeeded
	 *            true to assume that function is not polynomial if we couldn't
	 *            prove it's polynomial without CAS
	 */
	public PolyFunction expandToPolyFunction(ExpressionValue ev,
			boolean symbolic, boolean assumeFalseIfCASNeeded) {
		PolyFunction polyFunNoCas = expandToPolyFunctionNoCas(ev, symbolic);
		// TODO: make sure expandToPolyFunctionNoCas does not mess with ev
		// instead of the next line
		initFunction();
		if (polyFunNoCas != null || assumeFalseIfCASNeeded)
			return polyFunNoCas;
		ExpressionNode node;
		if (ev.isExpressionNode()) {
			node = (ExpressionNode) ev;
		} else {
			// wrap expressionValue
			node = new ExpressionNode(kernel, ev);
		}

		// get coefficients as strings

		String function, var;
		StringTemplate tpl = StringTemplate.giacTemplate;
		// See #1322
		try {
			function = node.getCASstring(tpl, symbolic);
			var = fVars[0].toString(tpl);
		} catch (NullPointerException e) {
			// this is not a valid polynomial
			return null;
		} finally {
			// do nothing
		}

		String[] strCoeffs = kernel.getPolynomialCoeffs(function, var);

		if (strCoeffs == null)
			// this is not a valid polynomial
			return null;

		// convert sring coefficients to coefficients of a SymbolicPolyFunction
		// resp. PolyFunction
		int degree = strCoeffs.length - 1;
		if (symbolic) {
			// build SymbolicPolyFunction
			SymbolicPolyFunction symbPolyFun = new SymbolicPolyFunction(degree);
			ExpressionNode[] symbCoeffs = symbPolyFun.getSymbolicCoeffs();
			for (int i = 0; i < strCoeffs.length; i++) {
				symbCoeffs[degree - i] = evaluateToExpressionNode(strCoeffs[i]);
				if (symbCoeffs[degree - i] == null)
					return null;
				symbCoeffs[degree - i].simplifyConstantIntegers();
			}
			return symbPolyFun;
		}
		// build PolyFunction

		PolyFunction polyFun = new PolyFunction(degree);
		for (int i = 0; i < strCoeffs.length; i++) {
			ExpressionNode coeff = evaluateToExpressionNode(strCoeffs[i]);
			if (coeff == null) {
				Log.warn("error in buildPolyFunction:" + strCoeffs[i]);
				return null;
			}
			try {
				polyFun.coeffs[degree - i] = coeff.evaluateDouble();
			} catch (Exception e) {
				Log.warn("error in buildPolyFunction:" + e.getMessage());
				e.printStackTrace();
					return null;
				}
		}
		return polyFun;

	}

	private ExpressionNode zeroExpr = new ExpressionNode(kernel, new MyDouble(
			kernel, 0));

	private PolyFunction expandToPolyFunctionNoCas(ExpressionValue ev,
			boolean symbolic) {
		PolyFunction polyFun = null;
		FunctionVariable xVar = new FunctionVariable(kernel, "x");
		ExpressionValue[][] coeff = null;
		int terms = -1;
		ExpressionValue evCopy = ev.deepCopy(kernel);
		ExpressionNode replaced;
		VariableReplacer varep = VariableReplacer
				.getReplacer(fVars[0].toString(StringTemplate.defaultTemplate),
						xVar, kernel);
		if (evCopy instanceof ExpressionNode) {
			replaced = ((ExpressionNode) evCopy).traverse(varep).wrap();
		} else {
			replaced = (new ExpressionNode(kernel, evCopy)).traverse(varep)
					.wrap();
		}
		Equation equ = new Equation(kernel, replaced, new MyDouble(kernel, 0));

		try {
			coeff = Polynomial.fromNode(replaced, equ).getCoeff();
			terms = coeff.length;
		} catch (Throwable t) {
			Log.warn(ev + " couldn't be transformed to polynomial:"
					+ t.getMessage());
			return null;
		}
		if (!equ.isPolynomial()) {
			return null;
		}
		if (!symbolic) {
			double[] coeffValues = new double[terms];
			for (int i = 0; i < coeff.length; i++) {
				if (coeff[i][0] instanceof ExpressionNode) {
					coeffValues[i] = coeff[i][0].evaluateDouble(); // for ticket
																	// #2276
																	// ---Tam
				} else {
					coeffValues[i] = coeff[i][0] instanceof NumberValue ? ((NumberValue) coeff[i][0])
							.getDouble() : 0;
				}

			}
			polyFun = new PolyFunction(coeffValues);
		} else {
			ExpressionNode[] coeffExpr = new ExpressionNode[terms];
			for (int i = 0; i < coeff.length; i++) {
				coeffExpr[i] = coeff[i][0] == null ? zeroExpr
						: new ExpressionNode(kernel, coeff[i][0]);
			}
			polyFun = new SymbolicPolyFunction(coeffExpr);
		}

		return polyFun;
	}

	/**
	 * Parses given String str and tries to evaluate it to an ExpressionNode.
	 * Returns null if something went wrong.
	 */
	private ExpressionNode evaluateToExpressionNode(String str) {
		try {
			ExpressionNode en = kernel.getParser().parseExpression(str);
			en.resolveVariables(new EvalInfo(false));
			return en;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} catch (Error e) {
			e.printStackTrace();
			return null;
		}
	}

	/* ***************
	 * CALULUS **************
	 */

	/**
	 * Returns n-th derivative of this function wrapped as a GeoFunction object.
	 */
	public GeoFunction getGeoDerivative(int n, boolean fast) {
		if (geoDeriv == null)
			geoDeriv = new GeoFunction(kernel.getConstruction());
		Function deriv = getDerivative(n, fast);
		geoDeriv.setFunction(deriv);
		geoDeriv.setDefined(deriv != null);
		return geoDeriv;
	}

	private GeoFunction geoDeriv;

	/**
	 * Returns n-th derivative of this function
	 * 
	 * @param n
	 *            order
	 * @param fast
	 *            true = don't use CAS
	 * @return derivative
	 */
	final public Function getDerivative(int n, boolean fast) {
		return getDerivative(n, true, fast);
	}

	/**
	 * Returns n-th derivative of this function where fractions are not kept
	 * (faster).
	 * 
	 * @param n
	 *            order
	 * @param fast
	 *            don't use CAS
	 * @return derivative
	 */
	final public Function getDerivativeNoFractions(int n, boolean fast) {
		return getDerivative(n, false, fast);
	}

	/**
	 * 
	 * @param n
	 *            derivative order
	 * @param keepFractions
	 *            true for 123/100, false for 1.23 in coefficients
	 * @param fast
	 *            if true -> use fast non-CAS derivatives
	 * @return n-th derivative
	 */
	final Function getDerivative(int n, boolean keepFractions, boolean fast) {
		// check if it's a polynomial
		PolyFunction polyDeriv = getNumericPolynomialDerivative(n, true);

		// it it is...
		if (polyDeriv != null) {
			// ... we can calculate the derivative without loading the CAS
			// (*much* faster, especially in web)

			// NB keepFractions ignored, so different answer given for f(x) =
			// 3x^2 / 5, f'(x)
			boolean factor = getExpression().inspect(new Inspecting() {

				@Override
				public boolean check(ExpressionValue v) {
					if (v instanceof ExpressionNode
							&& ((ExpressionNode) v).getOperation() == Operation.POWER) {
						if (((ExpressionNode) v).getLeft().unwrap()
								.isExpressionNode()
								&& ((ExpressionNode) v).getRight()
										.evaluateDouble() > Function.MAX_EXPAND_DEGREE) {
							return true;
						}
					}
					return false;
				}
			});
			if (factor) {
				return getDerivativeNoCAS(n);
			}
			Function ret = polyDeriv.getFunction(kernel, getFunctionVariable());

			if (fast) {
				// ret.setSecret();
			}

			return ret;
		}

		if (fast || !kernel.useCASforDerivatives()) {

			return getDerivativeNoCAS(n);

		}

		// get variable string with tmp prefix,
		// e.g. "x" becomes "ggbtmpvarx" here
		String varStr = fVars[0].toString(StringTemplate.prefixedDefault);

		StringBuilder sb = new StringBuilder();
		sb.append("Derivative[");
		if (!keepFractions)
			sb.append("Numeric[");
		sb.append("%");
		if (!keepFractions)
			sb.append("]");
		sb.append(",");
		sb.append(varStr);
		sb.append(",");
		sb.append(n);
		sb.append("]");
		// for derivative we don't need arbconst
		return (Function) evalCasCommand(sb.toString(), true, null);
	}

	/**
	 * @return Function y'(t)/x'(t) needed for parametric derivative
	 * @param funX
	 *            function x(t)
	 * @param funY
	 *            function y(t)
	 */
	public static Function getDerivativeQuotient(Function funX, Function funY) {
		if (funX.fVars == null)
			return null;

		// use fast non-CAS method
		Function xDashed = funX.getDerivativeNoCAS(1);
		Function yDashed = funY.getDerivativeNoCAS(1);

		FunctionVariable fv = xDashed.getFunctionVariable();

		// make sure both functions use same variable
		ExpressionValue yDashedEv = yDashed.getExpression().replace(
				yDashed.getFunctionVariable(), fv);

		ExpressionNode en = new ExpressionNode(funX.getKernel(), yDashedEv,
				Operation.DIVIDE, xDashed.getExpression());

		return new Function(en, fv);
	}

	/**
	 * Creates the difference expression (a - b) and stores the result in
	 * Function c.
	 * 
	 * @param a
	 *            minuend
	 * @param b
	 *            subtrahend
	 * @param c
	 *            difference
	 */
	final public static void difference(Function a, Function b, Function c) {
		// copy only the second function and replace b.fVar by a.fVar
		ExpressionNode left = a.expression;
		ExpressionNode right = b.expression.getCopy(a.kernel);

		// replace b.fVar in right by a.fVar to have only one function
		// variable in our function
		right.replace(b.fVars[0], a.fVars[0]);

		ExpressionNode diffExp = new ExpressionNode(a.kernel, left,
				Operation.MINUS, right);
		c.setExpression(diffExp);
		c.fVars[0] = a.fVars[0];
	}

	/**
	 * Creates the difference expression (a - line) and stores the result in
	 * Function c. This is needed for the intersection of function a and line ax
	 * + by + c = 0. b != 0 is assumed.
	 * 
	 * @param f
	 *            minuend
	 * @param line
	 *            subtrahend (as line)
	 * @param c
	 *            difference
	 */
	final public static void difference(Function f, GeoLine line, Function c) {
		// build expression for line: ax + by + c = 0 (with b != 0)
		// explicit form: line: y = -a/b x - c/b
		// we need f - line: f(x) + a/b x + c/b
		double coeffX = line.getX() / line.getY();
		double coeffConst = line.getZ() / line.getY();

		// build expression f - line: f(x) + a/b x + c/b
		ExpressionNode temp;
		// f(x) + a/b * x
		if (coeffX > 0) {
			temp = new ExpressionNode(f.kernel, f.expression, Operation.PLUS,
					new ExpressionNode(f.kernel,
							new MyDouble(f.kernel, coeffX), Operation.MULTIPLY,
							f.fVars[0]));
		} else {
			temp = new ExpressionNode(f.kernel, f.expression, Operation.MINUS,
					new ExpressionNode(f.kernel,
							new MyDouble(f.kernel, -coeffX),
							Operation.MULTIPLY, f.fVars[0]));
		}

		// f(x) + a/b * x + c/b
		if (coeffConst > 0) {
			temp = new ExpressionNode(f.kernel, temp, Operation.PLUS,
					new MyDouble(f.kernel, coeffConst));
		} else {
			temp = new ExpressionNode(f.kernel, temp, Operation.MINUS,
					new MyDouble(f.kernel, -coeffConst));
		}

		c.setExpression(temp);
		c.fVars[0] = f.fVars[0];
	}

	/**
	 * Tries to build a RealRootDerivFunction out of this function and its
	 * derivative. This can be used for root finding. Note: changes to the
	 * function will not affect the returned RealRootDerivFunction.
	 * 
	 * Switched to fast derivatives because of #4929
	 * 
	 * @return real root function
	 */
	final public RealRootDerivFunction getRealRootDerivFunction() {
		Function deriv = getDerivativeNoFractions(1, true);
		if (deriv == null) {
			return null;
		}
		return new DerivFunction(this, deriv);
	}

	/*
	 * for root finding
	 */
	private static class DerivFunction implements RealRootDerivFunction {

		private Function fun, realRootDerivative;
		private double[] ret = new double[2];

		DerivFunction(Function fun, Function derivative) {
			this.fun = fun;
			this.realRootDerivative = derivative;
		}

		public double[] evaluateDerivFunc(double x) {
			ret[0] = fun.evaluate(x);
			ret[1] = realRootDerivative.evaluate(x);
			return ret;
		}

		public double evaluate(double x) {
			return fun.evaluate(x);
		}

		public double evaluateDerivative(double x) {
			return realRootDerivative.evaluate(x);
		}
	}

	/**
	 * Decides whether function includes division by expression containing
	 * function variable
	 * 
	 * @return true if function includes division by variable
	 */
	public final boolean includesDivisionByVariable() {
		if (expression == null) {
			return false;
		}
		return expression.includesDivisionBy(fVars[0]);
	}

	/**
	 * Decides whether function includes DataFunction, Freehand function
	 * 
	 * @return true if function includes DataFunction, Freehand
	 */
	public final boolean includesFreehandOrDataFunction() {
		if (expression == null) {
			return false;
		}
		return expression.includesFreehandOrData();
	}

	/**
	 * Decides whether function includes eg If[], abs() function
	 * 
	 * @return true if function includes abs(), If[] etc
	 */
	public final boolean includesNonContinuousIntegral() {
		if (expression == null) {
			return false;
		}
		return expression.includesNonContinuousIntegral();
	}

	public GeoFunction getGeoFunction() {
		GeoFunction gf = new GeoFunction(kernel.getConstruction());
		gf.setFunction(this);
		return gf;
	}

	/**
	 * @param n
	 *            order of derivative
	 * @return derivative calculated without the CAS
	 */
	public Function getDerivativeNoCAS(int n) {

		ExpressionNode expDeriv = expression;

		for (int i = 0; i < n; i++) {
			expDeriv = expDeriv.derivative(fVars[0], kernel);
		}
		expDeriv.simplifyConstantIntegers();
		return new Function(expDeriv, fVars[0]);
	}

	/**
	 * @return integral calculated without the CAS (will work only for very
	 *         simple functions eg sin(3x))
	 */
	public Function getIntegralNoCAS() {

		return new Function(expression.integral(fVars[0], kernel), fVars[0]);
	}

	/**
	 * Evaluates polynomial and its derivative
	 */
	public double[] evaluateDerivFunc(double x) {

		double[] ret = new double[2];
		ret[0] = this.evaluate(x);

		if (isBooleanFunction) {
			ret[1] = Double.NaN;
			return ret;
		}

		if (derivative == null) {
			derivative = getDerivative(1, false, true);
		}

		ret[1] = derivative.evaluate(x);

		return ret;

	}

	public double evaluateDerivative(double x) {

		if (isBooleanFunction) {
			return Double.NaN;
		}

		if (derivative == null) {
			derivative = getDerivative(1, false, true);
		}

		return derivative.evaluate(x);
	}

	@Override
	public ExpressionValue derivative(FunctionVariable fv, Kernel kernel0) {
		return expression.derivative(fv, kernel0);
	}

	/**
	 * @param algo
	 *            secret parent algo
	 */
	public void setSecret(AlgoElement algo) {
		getExpression().setSecret(algo);
	}

	@Override
	public void updateCASEvalMap(TreeMap<String, String> map) {
		if (map == null) {
			return;
		}
		kernel.getConstruction().registerFunctionVariable(
				this.fVars[0].getSetVarString());
		for (Entry<String, String> entry : map.entrySet()) {
			GeoFunction gfun = kernel.getAlgebraProcessor()
					.evaluateToFunction(entry.getValue(), true, true);
			if (gfun != null) {
				getCasEvalMap().put(entry.getKey(), gfun.getFunction());
			}
		}
		kernel.getConstruction().registerFunctionVariable(null);
	}

	/**
	 * @param scale
	 *            scale along x-axis
	 */
	public void dilateX(double scale) {
		dilateX(expression, scale, 0);

	}

}
