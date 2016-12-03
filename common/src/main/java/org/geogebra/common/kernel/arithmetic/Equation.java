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
import java.util.Iterator;
import java.util.Set;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;

/**
 * stores left and right hand side of an equation as Exprssions
 */
public class Equation extends ValidExpression implements EquationValue {

	private ExpressionNode lhs;
	private ExpressionNode rhs;

	private Polynomial leftPoly, rightPoly; // polynomial in normalForm
	private Polynomial normalForm; // polynomial in normalForm
	private boolean isFunctionDependent; // Equation depends (non-constant) on
											// functions (set in InitEquation)
	private Kernel kernel;

	private boolean forcePlane = false, forceLine = false;
	private boolean forceConic = false, forceImplicitPoly = false;
	private boolean forceQuadric = false, forceSurface = false;
	private ArrayList<ExpressionValue> variableDegrees = null;
	private boolean isPolynomial = true;

	/**
	 * check whether ExpressionNodes are evaluable to instances of Polynomial or
	 * NumberValue and build an Equation out of them
	 * 
	 * @param kernel
	 *            kernel
	 * @param lhs
	 *            LHS
	 * @param rhs
	 *            RHS
	 */
	public Equation(Kernel kernel, ExpressionValue lhs, ExpressionValue rhs) {
		if (lhs.isExpressionNode())
			this.lhs = (ExpressionNode) lhs;
		else
			this.lhs = new ExpressionNode(kernel, lhs);

		if (rhs.isExpressionNode())
			this.rhs = (ExpressionNode) rhs;
		else
			this.rhs = new ExpressionNode(kernel, rhs);

		this.kernel = kernel;
		isFunctionDependent = false;
	}

	/**
	 * @return RHS of this equation
	 */
	public ExpressionNode getRHS() {
		return rhs;
	}

	/**
	 * @param rhs
	 *            new RHS
	 */
	public void setRHS(ExpressionNode rhs) {
		if (rhs != null)
			this.rhs = rhs;
	}

	/**
	 * @return LHS of this equation
	 */
	public ExpressionNode getLHS() {
		return lhs;
	}

	/**
	 * @param lhs
	 *            new LHS
	 */
	public void setLHS(ExpressionNode lhs) {
		if (lhs != null)
			this.lhs = lhs;
	}

	/**
	 * Force this to evaluate to line
	 */
	public void setForceLine() {
		// this expression should be considered as a line, not a plane
		forceLine = true;
	}

	/**
	 * @return true if this is forced to evaluate to line
	 */
	final public boolean isForcedLine() {
		return forceLine;
	}

	/**
	 * Force this to evaluate to plane
	 */
	final public void setForcePlane() {
		// this expression should be considered as a plane, not a line
		forcePlane = true;
	}

	/**
	 * @return true if this is forced to evaluate to plane
	 */
	final public boolean isForcedPlane() {
		return forcePlane;
	}

	/**
	 * Force this to evaluate to quadric
	 */
	final public void setForceQuadric() {
		// this expression should be considered as a quadric, not a conic
		forceQuadric = true;
	}

	/**
	 * Force this to evaluate to quadric
	 */
	final public void setForceSurface() {
		// this expression should be considered as a surface, not implicit poly
		forceSurface = true;
	}

	/**
	 * @return true if this is forced to evaluate to quadric
	 */
	final public boolean isForcedQuadric() {
		return forceQuadric;
	}

	/**
	 * @return true if this is forced to evaluate to quadric
	 */
	final public boolean isForcedSurface() {
		return forceSurface;
	}

	/**
	 * @return true if this is forced to evaluate to conic
	 */
	public boolean isForcedConic() {
		return forceConic;
	}

	/**
	 * Force this to evaluate to conic
	 */
	public void setForceConic() {
		this.forceConic = true;
	}

	/**
	 * @return true if this is forced to evaluate to implicit poly
	 */
	public boolean isForcedImplicitPoly() {
		return forceImplicitPoly;
	}

	/**
	 * Force this to evaluate to implicit poly
	 */
	public void setForceImplicitPoly() {
		this.forceImplicitPoly = true;
	}

	/**
	 * Adds/subtracts/muliplies/divides ev to this equation to get lhs + ev =
	 * rhs = ev
	 * 
	 * @param operation
	 *            operation to apply
	 * @param ev
	 *            other operand
	 * @param switchOrder
	 *            true to compute other * this
	 */
	public void applyOperation(Operation operation, ExpressionValue ev,
			boolean switchOrder) {
		ExpressionValue left, right;

		if (ev instanceof Equation) {
			Equation equ = (Equation) ev;
			left = equ.lhs;
			right = equ.rhs;
		} else {
			left = ev;
			right = ev;
		}

		if (switchOrder) {
			// ev <operation> equ
			lhs = new ExpressionNode(kernel, left, operation, lhs);
			rhs = new ExpressionNode(kernel, right, operation, rhs);
		} else {
			// equ <operation> ev
			lhs = new ExpressionNode(kernel, lhs, operation, left);
			rhs = new ExpressionNode(kernel, rhs, operation, right);
		}

	}

	/**
	 * Call this method to check that this is a valid equation. May throw
	 * MyError (InvalidEquation).
	 */
	public void initEquation() {


		EvalInfo info = new EvalInfo(false);
		// resolve variables in lhs
		if (lhs.isLeaf() && lhs.getLeft().isVariable()) {
			// avoid auto creation of GeoElement when lhs is a single variable
			// e.g. A4 = x^2
			Variable leftVar = (Variable) lhs.getLeft();
			lhs.setLeft(leftVar.resolve(false, true)); // don't allow auto
														// creation of variables
		} else {
			// standard case for lhs
			lhs.resolveVariables(info);
		}

		// resolve variables in rhs
		rhs.resolveVariables(info);

		// simplify the both sides to single polynomials
		this.isPolynomial = true;
		this.variableDegrees = null;
		FunctionVariable xVar = new FunctionVariable(kernel, "x"),
				yVar = new FunctionVariable(kernel, "y"),
				zVar = new FunctionVariable(kernel, "z");
		fixStructure(lhs, xVar, yVar, zVar);
		fixStructure(rhs, xVar, yVar, zVar);
		leftPoly = Polynomial.fromNode(lhs, this);
		rightPoly = Polynomial.fromNode(rhs, this);

		// bring to normal form left - right = 0
		normalForm = new Polynomial(kernel, rightPoly);
		normalForm.multiply(-1.0d);
		normalForm.add(leftPoly, this);
	}

	private static void fixStructure(ExpressionNode expression,
			FunctionVariable xVar,
			FunctionVariable yVar, FunctionVariable zVar) {
		// try to replace x(x+1) by x*(x+1)
		undecided.clear();
		expression.replaceXYZnodes(xVar, yVar, zVar, undecided);
		for (ExpressionNode en : undecided)
			en.setOperation(Operation.MULTIPLY);
		undecided.clear();
	}

	private static ArrayList<ExpressionNode> undecided = new ArrayList<ExpressionNode>();

	/**
	 * @param isFunctionDependent
	 *            true iff contains functions
	 */
	public void setFunctionDependent(boolean isFunctionDependent) {
		this.isFunctionDependent = isFunctionDependent;
	}

	/**
	 * @return true iff contains functions
	 */
	public boolean isFunctionDependent() {
		return isFunctionDependent;
	}

	/**
	 * @return LHS-RHS
	 */
	public Polynomial getNormalForm() {
		return normalForm;
	}

	/**
	 * @return the degree of the equation's normalform (max length of variables
	 *         in a Term of the normalform)
	 */
	public int degree() {
		return normalForm.degree();
	}

	/**
	 * @return the max degree on a single var, eg 3 for x^3 y^2
	 */
	public int singleDegree() {
		return normalForm.singleDegree();
	}

	/**
	 * @param variables
	 *            variable string
	 * @return coefficient
	 */
	public ExpressionValue getCoefficient(String variables) {
		return normalForm.getCoefficient(variables);
	}

	/**
	 * @param variables
	 *            variable string
	 * @return coefficient
	 */
	public double getCoeffValue(String variables) {
		ExpressionValue ev = getCoefficient(variables);

		try {
			NumberValue nv = (NumberValue) ev;
			return nv.getDouble();
		} catch (Exception e) {
			Log.warn("getCoeffValue(" + variables + ") failed:" + e);
			return Double.NaN;
		}
	}

	/**
	 * @return GeoElement variables
	 */
	final public GeoElement[] getGeoElementVariables() {
		Set<GeoElement> varSet;
		Set<GeoElement> leftVars = lhs.getVariables();
		Set<GeoElement> rightVars = rhs.getVariables();
		if (leftVars == null) {
			varSet = rightVars;
		} else if (rightVars == null) {
			varSet = leftVars;
		} else {
			leftVars.addAll(rightVars);
			varSet = leftVars;
		}
		if (varSet == null) {
			return new GeoElement[0];
		}

		Iterator<GeoElement> i = varSet.iterator();
		GeoElement[] ret = new GeoElement[varSet.size()];
		int j = 0;
		while (i.hasNext()) {
			ret[j++] = i.next();
		}
		return ret;
	}

	/**
	 * @param var
	 *            variable in which this could be explicit
	 * @return true if this Equation is explicit (lhs is "+1var" and rhs does
	 *         not contain var) or (rhs is "+1var" and lhs does not contain var)
	 */
	public boolean isExplicit(String var) {
		Polynomial lhsp = leftPoly;
		Polynomial rhsp = rightPoly;

		// var = ... || ... = var
		return (lhsp.length() == 1
				&& lhsp.getCoefficient(var).evaluateDouble() == 1 && !rhsp
					.contains(var))
				|| (rhsp.length() == 1
						&& rhsp.getCoefficient(var).evaluateDouble() == 1 && !lhsp
							.contains(var));
	}

	/**
	 * @return true if this Equation is implicit (not explicit)
	 */
	public boolean isImplicit() {
		return !isExplicit("x") && !isExplicit("y") && !isExplicit("z");
	}

	public boolean contains(ExpressionValue ev) {
		return lhs.contains(ev) || rhs.contains(ev);
	}

	@Override
	public Equation deepCopy(Kernel kernel1) {
		Equation ret = new Equation(kernel1, lhs.getCopy(kernel1),
				rhs.getCopy(kernel1));
		ret.forceConic = forceConic;
		ret.forceLine = forceLine;
		ret.forcePlane = forcePlane;
		ret.forceQuadric = forceQuadric;
		ret.forceImplicitPoly = forceImplicitPoly;
		ret.forceSurface = forceSurface;
		return ret;
	}

	/**
	 * Reset flags for forcing result type
	 */
	public void resetFlags() {
		forceConic = false;
		forceLine = false;
		forcePlane = false;
		forceQuadric = false;
		forceImplicitPoly = false;
		forceSurface = false;
	}

	@Override
	public ExpressionValue evaluate(StringTemplate tpl) {
		boolean oldFlag = kernel.getConstruction().isSuppressLabelsActive();
		kernel.getConstruction().setSuppressLabelCreation(true);
		GeoElement ge = kernel.getAlgebraProcessor().processEquation(this,
				this.wrap(), new EvalInfo(false))[0];
		kernel.getConstruction().setSuppressLabelCreation(oldFlag);
		return ge;
	}

	public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> leftVars = lhs.getVariables();
		HashSet<GeoElement> rightVars = rhs.getVariables();
		if (leftVars == null) {
			return rightVars;
		} else if (rightVars == null) {
			return leftVars;
		} else {
			leftVars.addAll(rightVars);
			return leftVars;
		}
	}

	public boolean isConstant() {
		return lhs.isConstant() && rhs.isConstant();
	}

	public boolean isLeaf() {
		return false;
	}

	public boolean isNumberValue() {
		return false;
	}

	public void resolveVariables(EvalInfo info) {
		if ("X".equals(lhs.toString(StringTemplate.defaultTemplate))
				&& kernel.lookupLabel("X") == null) {
			return;
		}
		lhs.resolveVariables(info);
		rhs.resolveVariables(info);
	}

	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();

		if (lhs != null)
			sb.append(lhs.toLaTeXString(symbolic, tpl));
		else
			sb.append('0');
		sb.append(" = ");

		if (rhs != null)
			sb.append(rhs.toLaTeXString(symbolic, tpl));
		else
			sb.append('0');
		return sb.toString();
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();

		if (lhs != null)
			sb.append(lhs.toValueString(tpl));
		else
			sb.append('0');

		if (tpl.getStringType().isGiac()) {
			if (lhs.evaluatesToList() || rhs.evaluatesToList()) {
				// %= stops {1,2}={3,4} being turned into {1=3,2=4}
				sb.append("%=");
			} else {
				sb.append("=");
			}
		} else {
			// equal sign
			sb.append(" = ");
		}

		if (rhs != null) {
			sb.append(rhs.toValueString(tpl));
		} else {
			sb.append('0');
		}
		return sb.toString();
	}

	@Override
	public String toString(StringTemplate tpl) {

		return toString(tpl, this.lhs);
	}

	/**
	 * 
	 * @param tpl
	 *            string template
	 * @param lhs1
	 *            left hand side
	 * @return string description
	 */
	public String toString(StringTemplate tpl, ExpressionNode lhs1) {
		StringBuilder sb = new StringBuilder();

		// left hand side
		if (lhs1 != null)
			sb.append(lhs1.toString(tpl));
		else
			sb.append('0');

		if (tpl.getStringType().isGiac()) {
			if (lhs1.evaluatesToList() || rhs.evaluatesToList()) {
				// %= stops {1,2}={3,4} being turned into {1=3,2=4}
				sb.append("%=");
			} else {
				sb.append("=");
			}
		} else {
			// equal sign
			sb.append(" = ");
		}

		// right hand side
		if (rhs != null)
			sb.append(rhs.toString(tpl));
		else
			sb.append('0');

		return sb.toString();
	}

	@Override
	public String getAssignmentOperator() {
		return ": ";
	}

	@Override
	public String getAssignmentOperatorLaTeX() {
		return ": \\, ";
	}

	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	/**
	 * @return kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue v = t.process(this);
		if(v != this){
			return v;
		}
		lhs = lhs.traverse(t).wrap();
		rhs = rhs.traverse(t).wrap();
		return v;
	}

	@Override
	public boolean inspect(Inspecting t) {
		return t.check(this) || lhs.inspect(t) || rhs.inspect(t);
	}

	/**
	 * 
	 * @return says if the original expression contains "z"
	 */
	public boolean containsZ() {
		return containsVar(lhs, 'z') || containsVar(rhs, 'z');

	}

	private static final boolean containsVar(ExpressionValue v, char var) {
		if (v == null) {
			return false;
		}

		if (v instanceof ExpressionNode) {
			ExpressionNode node = (ExpressionNode) v;
			if (containsVar(node.getLeft(), var)) {
				return true;
			}

			return containsVar(node.getRight(), var);
		}

		if (v instanceof FunctionVariable) {
			String vVar = ((FunctionVariable) v).getSetVarString();
			return vVar.length() == 1 && vVar.charAt(0) == var;
		}

		if (v instanceof MyVecNode) {
			MyVecNode vec = (MyVecNode) v;
			return containsVar(vec.getX(), var) || containsVar(vec.getY(), var);
		}
		if (v instanceof MyVec3DNode) {
			MyVec3DNode vec = (MyVec3DNode) v;
			return containsVar(vec.getX(), var) || containsVar(vec.getY(), var)
					|| containsVar(vec.getZ(), var);
		}
		if (v instanceof MyList) {
			MyList list = (MyList) v;
			for (int i = 0; i < list.size(); i++) {
				if (containsVar(list.getListElement(i), var)) {
					return true;
				}
			}
			return false;
		}
		if (v instanceof Polynomial) {
			return !((Polynomial) v).isFreeOf(var);
		}

		return false;
	}

	/**
	 * @param gn
	 *            sub-expression with variable degree
	 */
	public void addVariableDegree(ExpressionValue gn) {
		if (this.variableDegrees == null) {
			this.variableDegrees = new ArrayList<ExpressionValue>();
		}
		this.variableDegrees.add(gn);
	}

	/**
	 * @return whether this has terms with variable degree (x^n)
	 */
	public boolean hasVariableDegree() {
		return variableDegrees != null;
	}

	/**
	 * @param b
	 *            whether this may be polynomial
	 */
	public void setIsPolynomial(boolean b) {
		this.isPolynomial = b;
	}

	/**
	 * @return whether this is currently polynomial, ie whether all terms are
	 *         x^t where t is either non-negative integer variable or constant
	 */
	public boolean isPolynomial() {
		if (!isPolynomial) {
			return false;
		}
		if (this.variableDegrees == null) {
			return true;
		}
		for (ExpressionValue ev : this.variableDegrees) {
			double exp = ev.evaluateDouble();
			if (!Kernel.isInteger(exp) || Kernel.isGreater(0, exp)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return whether this may be polynomial, ie all terms are x^t but some t
	 *         may be non-integer variable
	 */
	public boolean mayBePolynomial() {
		return isPolynomial;
	}

	@Override
	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.EQUATION;
	}

	/**
	 * @return either this equation or assignment (eg i=7 is assignment; x=y,
	 *         e=x are equations).
	 */
	public ValidExpression equationOrAssignment() {
		if (!rhs.containsFreeFunctionVariable(null)) {
			// assignment, e.g. z = 23
			if (lhs.isSingleVariable()) {
				rhs.setLabel(((Variable) lhs
						.evaluate(StringTemplate.defaultTemplate))
						.getName(StringTemplate.defaultTemplate));
				return rhs;
			}

			// special case: e = 2 should be an assignment
			// but an undefined "e" has been read as the Euler constant already
			else if (Unicode.EULER_STRING.equals(lhs
					.toString(StringTemplate.defaultTemplate))) {
				rhs.setLabel("e");
				return rhs;
			}

			// special case: i = 2 should be an assignment
			// but an undefined "i" has been read as the imaginary unit already
			else if (lhs.isImaginaryUnit()) {
				rhs.setLabel("i");
				return rhs;
			}

			// special case: z = 2 should be an assignment when 3D view is not
			// present
			else if (kernel.isZvarAllowed()
					&& "z".equals(lhs.toString(StringTemplate.defaultTemplate))) {
				rhs.setLabel("z");
				return rhs;
			}

		}
		return this;
	}

	/**
	 * @return degree; overriden by forcedLine, forceConic, forceImplicitPoly
	 */
	public int preferredDegree() {
		if (isForcedLine() || isForcedPlane()) {
			return 1;
		} else if (isForcedConic() || isForcedQuadric()) {
			return 2;
		} else if (isForcedImplicitPoly() || isForcedSurface()) {
			return 7;
		} else {
			return degree();
		}
	}

	public Equation getEquation() {
		return this;
	}

	/**
	 * @param name
	 *            var name
	 * @return whether such variable is free in this equation (eg for x(x=y)*x=1
	 *         we have free x and y is bound inside nested equation)
	 */
	public boolean containsFreeFunctionVariable(String name) {
		return lhs.containsFreeFunctionVariable(name)
				|| rhs.containsFreeFunctionVariable(name);
	}

	/**
	 * @param sb
	 *            xml builder
	 * @param string
	 *            equation style
	 */
	public static void appendType(StringBuilder sb, String string) {
		sb.append("\t<eqnStyle style=\"");
		sb.append(string);
		sb.append("\"/>\n");

	}

} // end of class Equation
