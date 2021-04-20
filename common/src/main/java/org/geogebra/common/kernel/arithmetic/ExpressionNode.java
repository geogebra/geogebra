/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * ExpressionNode.java
 *
 * binary tree node for ExpressionValues (NumberValues, VectorValues)
 *
 * Created on 03. Oktober 2001, 09:37
 */

package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Traversing.Replacer;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

/**
 * Tree node for expressions like "3*a - b/5"
 * 
 * @author Markus
 */
public class ExpressionNode extends ValidExpression
		implements ExpressionNodeConstants, ReplaceChildrenByValues {

	private Localization loc;
	@Weak
	private Kernel kernel;
	private ExpressionValue left;
	private ExpressionValue right;
	private Operation operation = Operation.NO_OPERATION;
	private boolean forceVector = false;
	private boolean forcePoint = false;
	private boolean forceFunction = false;
	private boolean forceInequality = false;
	private boolean forceSurface = false;

	/** true if this holds text and the text is in LaTeX format */
	public boolean holdsLaTeXtext = false;

	/** for leaf mode */
	public boolean leaf = false;
	private boolean brackets;
	private ExpressionValue resolve;

	// used by NDerivative / NIntegral / NInvert commands
	// (answer not displayed in Algebra View)
	private AlgoElement secretMaskingAlgo;

	/**
	 * Creates dummy expression node
	 */
	public ExpressionNode() {
	}

	/**
	 * Creates new ExpressionNode
	 * 
	 * @param kernel
	 *            kernel
	 * @param left
	 *            left subexpression
	 * @param operation
	 *            operation
	 * @param right
	 *            right subexpression
	 */
	public ExpressionNode(Kernel kernel, ExpressionValue left,
			Operation operation, ExpressionValue right) {

		this.kernel = kernel;
		loc = kernel.getLocalization();
		this.operation = operation;
		setLeft(left);
		if (right != null) {
			setRight(right);
		} else {
			unsetRight();
		}
	}

	/**
	 * for only one leaf (for wrapping ExpressionValues as ValidExpression)
	 * 
	 * @param kernel
	 *            Kernel
	 * @param leaf
	 *            value to be wrapped
	 */
	public ExpressionNode(Kernel kernel, ExpressionValue leaf) {
		this.kernel = kernel;
		loc = kernel.getLocalization();

		setLeft(leaf);
		this.leaf = true;
	}

	/**
	 * copy constructor: NO deep copy of subtrees is done here! this is needed
	 * for translation of functions
	 * 
	 * @param node
	 *            Node to copy
	 */
	public ExpressionNode(ExpressionNode node) {
		kernel = node.kernel;
		loc = node.loc;

		leaf = node.leaf;
		operation = node.operation;
		secretMaskingAlgo = node.secretMaskingAlgo;
		setLeft(node.left);
		setRight(node.right);
	}

	/**
	 * 
	 * wraps a double in an ExpressionNode
	 * 
	 * @param kernel2
	 *            kernel
	 * @param d
	 *            double
	 */
	public ExpressionNode(Kernel kernel2, double d) {
		this(kernel2, new MyDouble(kernel2, d));
	}

	/**
	 * @return kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * @return current operation
	 */
	final public Operation getOperation() {
		return operation;
	}

	/**
	 * @param op
	 *            new operation
	 */
	public void setOperation(Operation op) {
		operation = op;
	}

	/**
	 * @param flag
	 *            true if holds LaTeX text
	 */
	public void setHoldsLaTeXtext(boolean flag) {
		holdsLaTeXtext = flag;
	}

	/**
	 * @return left subexpression
	 */
	final public ExpressionValue getLeft() {
		return left;
	}

	/**
	 * @param l
	 *            left subexpression
	 */
	final public void setLeft(ExpressionValue l) {
		left = l;
		left.setInTree(true); // needed fot list operations eg k=2 then k {1,2}
	}

	/**
	 * Result is never null; for leaves, left is packed in ExpressionNode in
	 * result.
	 * 
	 * @return left subtree
	 */
	public ExpressionNode getLeftTree() {
		if (left.isExpressionNode()) {
			return (ExpressionNode) left;
		}
		return new ExpressionNode(kernel, left);
	}

	/**
	 * @return right subexpression
	 */
	final public ExpressionValue getRight() {
		return right;
	}

	/**
	 * @param r
	 *            new right subexpression
	 */
	final public void setRight(ExpressionValue r) {
		right = r;
		if (right != null) {
			right.setInTree(true); // needed for list operations eg k=2 then k
			// {1,2}
		}
		leaf = operation == Operation.NO_OPERATION; // right is a dummy MyDouble
		// by
		// default
	}

	/**
	 * @return right subtree
	 */
	public ExpressionNode getRightTree() {
		if (right == null) {
			return null;
		}

		if (right.isExpressionNode()) {
			return (ExpressionNode) right;
		}
		return new ExpressionNode(kernel, right);
	}

	@Override
	public ExpressionNode deepCopy(Kernel kernel1) {
		return getCopy(kernel1);
	}

	/**
	 * copy the whole tree structure except leafs
	 * 
	 * @param kernel1
	 *            kernel
	 * @return copy of this node
	 */
	public ExpressionNode getCopy(Kernel kernel1) {
		// Application.debug("getCopy() input: " + this);
		ExpressionNode newNode = null;
		ExpressionValue lev = null, rev = null;

		if (left != null) {
			lev = copy(left, kernel1);
		}
		if (right != null) {
			rev = copy(right, kernel1);
		}

		if (lev != null) {
			newNode = new ExpressionNode(kernel1, lev, operation, rev);
		} else {
			// something went wrong
			return null;
		}
		newNode.leaf = leaf;
		copyAttributesTo(newNode);
		return newNode;
	}

	/**
	 * @return copy of this, keeping left and right subtrees
	 */
	public ExpressionNode shallowCopy() {
		ExpressionNode newNode = new ExpressionNode(kernel, left, operation, right);
		newNode.leaf = leaf;
		copyAttributesTo(newNode);
		return newNode;
	}

	/**
	 * deep copy except for GeoElements
	 * 
	 * @param ev
	 *            value to copy
	 * @param kernel
	 *            kernel
	 * @return copy of value
	 */
	public static ExpressionValue copy(ExpressionValue ev, Kernel kernel) {
		if (ev == null) {
			return null;
		}

		ExpressionValue ret;
		if (ev instanceof MinusOne) {
			return new MinusOne(kernel);
		} else if (ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;
			ret = en.getCopy(kernel);
		} else if (ev instanceof MyList) {
			MyList en = (MyList) ev;
			ret = en.getCopy(kernel);
		}
		// deep copy
		// important to check for commands before we call isConstant as that
		// might
		// result in evaluation, FunctionNVar important because of
		// FunctionExpander
		else if ((ev.inspect(Inspecting.CommandFinder.INSTANCE))
				|| ev.isConstant() || ev instanceof FunctionNVar
				|| ev instanceof Equation || ev instanceof MyVecNode
				|| ev instanceof MyVec3DNode) {
			ret = ev.deepCopy(kernel);
		} else {
			ret = ev;
		}
		// Application.debug("copy ExpressionValue output: " + ev);
		return ret;
	}

	/**
	 * Replaces all ExpressionNodes in tree that are leafs (=wrappers) by their
	 * leaf objects (of type ExpressionValue).
	 */
	final public void simplifyLeafs() {
		if (left.isExpressionNode()) {
			ExpressionNode node = (ExpressionNode) left;
			if (node.leaf) {
				left = node.left;
				simplifyLeafs();
			}
		}

		if (right != null) {
			if (right.isExpressionNode()) {
				ExpressionNode node = (ExpressionNode) right;
				if (node.leaf) {
					right = node.left;
					simplifyLeafs();
				}
			}
		}
	}

	/**
	 * Replaces all Command objects in tree by their evaluated GeoElement
	 * objects.
	 */
	final private void simplifyAndEvalCommands(EvalInfo info) {
		// don't evaluate any commands for the CAS here
		if (info.getSymbolicMode() != SymbolicMode.NONE) {
			return;
		}

		if (left.isExpressionNode()) {
			((ExpressionNode) left).simplifyAndEvalCommands(info);
		} else if (left instanceof Command) {
			left = ((Command) left).simplify(info);
		}

		if (right != null) {
			if (right.isExpressionNode()) {
				((ExpressionNode) right).simplifyAndEvalCommands(info);
			} else if (right instanceof Command) {
				right = ((Command) right).simplify(info);
			}
		}
	}

	/**
	 * Evaluates this expression
	 * 
	 * @param tpl
	 *            template (needed for possible string concatenation)
	 * @return value
	 */
	@Override
	public ExpressionValue evaluate(StringTemplate tpl) {
		if (resolve instanceof ExpressionNode) {
			resolve = null;
		}
		return kernel.getExpressionNodeEvaluator().evaluate(this, tpl);
	}

	/**
	 * look for Variable objects in the tree and replace them by their resolved
	 * GeoElement
	 */
	@Override
	public final void resolveVariables(EvalInfo info) {
		doResolveVariables(info);
		simplifyAndEvalCommands(info);
		simplifyLeafs();
	}

	private void doResolveVariables(EvalInfo info) {
		// resolve left wing
		if (left.isVariable()) {
			left = ((Variable) left).resolveAsExpressionValue(info.getSymbolicMode(),
					info.isMultipleUnassignedAllowed(), info.isMultiLetterVariablesAllowed());
			if (operation == Operation.POWER
					|| operation == Operation.FACTORIAL) {
				fixPowerFactorial(Operation.MULTIPLY);
				fixPowerFactorialTrig();
			} else if (operation == Operation.SQRT_SHORT) {
				fixSqrtShort(Operation.MULTIPLY);
			} else if (operation == Operation.MULTIPLY &&  isConstantDouble(right, Kernel.PI_180)) {
				fixMultiplyDeg();
			}
			left = groupPowers(left);
		} else {
			left.resolveVariables(info);
		}

		// resolve right wing
		if (right != null) {
			if (right.isVariable()) {
				right = ((Variable) right).resolveAsExpressionValue(info.getSymbolicMode(),
						info.isMultipleUnassignedAllowed(), info.isMultiLetterVariablesAllowed());
				right = groupPowers(right);
			} else {
				right.resolveVariables(info);
			}
		}
	}

	private static ExpressionValue groupPowers(ExpressionValue left) {
		if (left.isOperation(Operation.MULTIPLY)) {
			ArrayList<ExpressionValue> factors = new ArrayList<>();
			left.wrap().collectFactors(factors);
			if (factors.size() > 1) {

				ExpressionValue currentVar = null;
				ExpressionValue product = null;
				int currentPower = 0;
				for (ExpressionValue factor : factors) {
					if (currentVar == null) {
						currentVar = factor;
						currentPower = 1;
					} else if (isSameVar(currentVar, factor)) {
						currentPower++;
					} else {
						product = buildProduct(currentVar.wrap().power(currentPower), product);
						currentVar = factor;
						currentPower = 1;
					}
				}
				product = buildProduct(currentVar.wrap().power(currentPower), product);
				return product;
			}
		}
		return left;
	}

	private static boolean isSameVar(ExpressionValue a, ExpressionValue b) {
		return a.unwrap() instanceof FunctionVariable
				&& b.unwrap() instanceof FunctionVariable
				&& a.toString(StringTemplate.xmlTemplate).equals(
						b.toString(StringTemplate.xmlTemplate));
	}

	private static ExpressionValue buildProduct(ExpressionNode power, ExpressionValue product) {
		return product == null ? power : power.multiply(product);
	}

	/**
	 * Returns whether this ExpressionNode should evaluate to a GeoVector. This
	 * method returns true when all GeoElements in this tree are GeoVectors and
	 * there are no other constanct VectorValues (i.e. constant points)
	 * 
	 * @return true if this should evaluate to GeoVector
	 */
	public boolean shouldEvaluateToGeoVector() {
		if (forcePoint) {
			return false;
		}
		if (forceVector) {
			return true;
		}
		boolean evalToVector = false;

		if (left.isExpressionNode()) {
			evalToVector = (((ExpressionNode) left)
					.shouldEvaluateToGeoVector());
		} else if (left.isGeoElement()) {
			GeoElement geo = (GeoElement) left;
			evalToVector = geo.isGeoVector() || geo.isNumberValue();
		} else if (left.isNumberValue()) {
			evalToVector = true;
		} else if (left instanceof MyVecNDNode) {
			evalToVector = ((MyVecNDNode) left).isCASVector();
		}

		if ((right != null) && evalToVector) {
			if (right.isExpressionNode()) {
				evalToVector = ((ExpressionNode) right)
						.shouldEvaluateToGeoVector();
			} else if (right.isNumberValue()) {
				evalToVector = operation != Operation.VEC_FUNCTION;
			} else if (right.isGeoElement()) {
				GeoElement geo = (GeoElement) right;
				evalToVector = geo.isGeoVector();
			}
		}
		return evalToVector;
	}

	/**
	 * Returns true if this tree includes a division by val
	 * 
	 * @param val
	 *            possible divisor
	 * @return true iff contains division by val
	 */
	final public boolean includesDivisionBy(ExpressionValue val) {
		if (operation == Operation.POWER) {
			// x^-4
			if (left.contains(val) && right.evaluateDouble() < 0) {
				return true;
			}

		}

		if (operation == Operation.DIVIDE) {
			if (right.contains(val)) {
				return true;
			}

			if (left.isExpressionNode()
					&& ((ExpressionNode) left).includesDivisionBy(val)) {
				return true;
			}
		} else {
			if (left.isExpressionNode()
					&& ((ExpressionNode) left).includesDivisionBy(val)) {
				return true;
			}

			if ((right != null) && right.isExpressionNode()
					&& ((ExpressionNode) right).includesDivisionBy(val)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns true if this tree includes eg abs(), If[] function
	 * 
	 * 
	 * @return true iff contains abs(), If[] etc
	 */
	final public boolean includesNonContinuousIntegral() {
		if (Operation.integralIsNonContinuous(operation)) {
			return true;
		}

		if (left.isExpressionNode()
				&& ((ExpressionNode) left).includesNonContinuousIntegral()) {
			return true;
		}

		if ((right != null) && right.isExpressionNode()
				&& ((ExpressionNode) right).includesNonContinuousIntegral()) {
			return true;
		}
		if ((operation == Operation.FUNCTION
				|| operation == Operation.FUNCTION_NVAR)
				&& getLeft() instanceof Functional) {
			return ((Functional) getLeft()).getFunction()
					.includesNonContinuousIntegral();
		}

		return false;
	}

	/**
	 * Returns true if this tree includes Freehand or DataFunction
	 * 
	 * 
	 * @return true iff contains abs(), If[] etc
	 */
	final public boolean includesFreehandOrData() {
		if (Operation.includesFreehandOrData(operation)) {
			return true;
		}

		if (left.isExpressionNode()
				&& ((ExpressionNode) left).includesFreehandOrData()) {
			return true;
		}

		if ((right != null) && right.isExpressionNode()
				&& ((ExpressionNode) right).includesFreehandOrData()) {
			return true;
		}

		return false;
	}

	/**
	 * Replaces all Variable objects with the given varName in tree by the given
	 * FunctionVariable object.
	 * 
	 * Only works if the varName is inserted without CAS prefix
	 * 
	 * @param varName
	 *            variable name
	 * @param fVar
	 *            replacement variable
	 * 
	 * @return number of replacements done
	 */
	public final int replaceVariables(String varName, FunctionVariable fVar) {
		int replacements = 0;

		// left tree
		if (left.isExpressionNode()) {
			replacements += ((ExpressionNode) left).replaceVariables(varName,
					fVar);
		} else if (left instanceof MyList) {
			replacements += ((MyList) left).replaceVariables(varName, fVar);
		} else if (left instanceof Variable) {
			if (varName.equals(((Variable) left)
					.getName(StringTemplate.defaultTemplate))) {
				left = fVar;
				replacements++;
			}
		} else if (left instanceof Command) {
			replacements += ((Command) left).replaceVariables(varName, fVar);
		}
		if (left instanceof GeoDummyVariable) {
			if (varName.equals(((GeoDummyVariable) left)
					.toString(StringTemplate.defaultTemplate))) {
				left = fVar;
				replacements++;
			}
		} else if (left instanceof FunctionVariable) {
			if (varName.equals(((FunctionVariable) left)
					.toString(StringTemplate.defaultTemplate))) {
				left = fVar;
				replacements++;
			}
		}

		// right tree
		if (right != null) {
			if (right.isExpressionNode()) {
				replacements += ((ExpressionNode) right)
						.replaceVariables(varName, fVar);
			} else if (right instanceof MyList) {
				replacements += ((MyList) right).replaceVariables(varName,
						fVar);
			} else if (right instanceof Variable) {
				if (varName.equals(((Variable) right)
						.getName(StringTemplate.defaultTemplate))) {
					right = fVar;
					replacements++;
				}
			} else if (right instanceof GeoDummyVariable) {
				if (varName.equals(((GeoDummyVariable) right)
						.toString(StringTemplate.defaultTemplate))) {
					right = fVar;
					replacements++;
				}
			} else if (right instanceof FunctionVariable) {
				if (varName.equals(((FunctionVariable) right)
						.toString(StringTemplate.defaultTemplate))) {
					right = fVar;
					replacements++;
				}
			}
		}

		return replacements;
	}

	/**
	 * Replaces all XCOORD, YCOORD, ZCOORD nodes by mutliplication nodes, e.g.
	 * x(x+1) becomes x*(x+1). The given function variables for "x", "y", "z"
	 * are used in this process.
	 * 
	 * @param xVar
	 *            variable x
	 * @param yVar
	 *            variable y
	 * @param zVar
	 *            variable z
	 * @param undecided
	 *            list for subexpressions where it's not clear whether they can
	 *            be used for multiplication directly or not
	 * 
	 * @return number of replacements done
	 */
	public int replaceXYZnodes(FunctionVariable xVar, FunctionVariable yVar,
			FunctionVariable zVar, ArrayList<ExpressionNode> undecided) {
		if ((xVar == null) && ((yVar == null) & (zVar == null))) {
			return 0;
		}

		// left tree
		if (left.isExpressionNode()) {
			((ExpressionNode) left).replaceXYZnodes(xVar, yVar, zVar,
					undecided);
		}
		// right tree
		if ((right != null) && right.isExpressionNode()) {
			((ExpressionNode) right).replaceXYZnodes(xVar, yVar, zVar,
					undecided);
		}

		switch (operation) {
		case XCOORD:
			if (xVar != null && !leftHasCoord()) {
				undecided.add(this);
				operation = Operation.MULTIPLY_OR_FUNCTION;
				right = left;
				left = xVar;
			}
			break;

		case YCOORD:
			if (yVar != null && !leftHasCoord()) {
				undecided.add(this);
				operation = Operation.MULTIPLY_OR_FUNCTION;
				right = left;
				left = yVar;
			}
			break;

		case ZCOORD:
			if (zVar != null && !leftHasCoord()) {
				undecided.add(this);
				operation = Operation.MULTIPLY_OR_FUNCTION;
				right = left;
				left = zVar;
			}
			break;
		case POWER:
		case FACTORIAL:
			fixPowerFactorial(Operation.MULTIPLY_OR_FUNCTION);
			break;
		case SQRT_SHORT:
			fixSqrtShort(Operation.MULTIPLY_OR_FUNCTION);
			break;
		default:
			break;
		}

		return undecided.size();
	}

	private boolean leftHasCoord() {
		return left.evaluatesToNDVector()
				|| left.getValueType() == ValueType.COMPLEX
				|| (left.unwrap() instanceof GeoLine);
	}

	private void fixSqrtShort(Operation multiplicativeOperation) {
		if (left.isExpressionNode()
				&& ((ExpressionNode) left).operation == multiplicativeOperation
				&& !((ExpressionNode) left).hasBrackets()) {
			right = ((ExpressionNode) left).getRight();
			left = new ExpressionNode(kernel, ((ExpressionNode) left).getLeft(),
					Operation.SQRT, null);
			operation = Operation.MULTIPLY;
		}
	}

	private void fixPowerFactorial(Operation multiplicativeOperation) {
		if (left.isExpressionNode()
				&& ((ExpressionNode) left).operation == multiplicativeOperation
				&& !((ExpressionNode) left).hasBrackets()) {
			right = new ExpressionNode(kernel,
					((ExpressionNode) left).getRight(), operation, right);
			left = ((ExpressionNode) left).getLeft();
			operation = Operation.MULTIPLY;
		}
	}

	private void fixPowerFactorialTrig() {
		if (isSingleArgumentFunction(left)) {
			ExpressionValue trigArg = ((ExpressionNode) this.left).getLeft();
			Operation leftOperation = ((ExpressionNode) left).operation;
			// sinxyz^2 is parsed as sin(x y z)^2, change to sin(x y z^2)
			if (trigArg.isOperation(Operation.MULTIPLY)) {
				ExpressionNode trigArgExpr = (ExpressionNode) trigArg;
				left = trigArgExpr.getRight().wrap()
						.apply(operation, right).multiply(trigArgExpr.getLeft());
			} else { // sinx^2 is parsed as sin(x)^2, change to sin(x^2)
				this.left = new ExpressionNode(kernel, trigArg, operation, right);
			}
			unsetRight();
			operation = leftOperation;
		}
	}

	private boolean isSingleArgumentFunction(ExpressionValue left) {
		return left.isExpressionNode()
				&& Operation.isSimpleFunction(((ExpressionNode) left).operation)
				&& !((ExpressionNode) left).hasBrackets();
	}

	private void fixMultiplyDeg() {
		if (isSingleArgumentFunction(left)) {
			operation = ((ExpressionNode) left).getOperation();
			left = new ExpressionNode(kernel, ((ExpressionNode) left).getLeft(),
					Operation.MULTIPLY, right);
			unsetRight();
		}
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue ev = t.process(this);
		if (ev != this) {
			return ev;
		}
		if (left != null) {
			left = left.traverse(t);
		}

		if (right != null && !operation.isUnary()) {
			right = right.traverse(t);
		}

		if (isLeaf() && left != null && left.isExpressionNode()) {
			ExpressionNode leftNode = left.wrap();
			right = leftNode.right;
			left = leftNode.left;
			leaf = leftNode.leaf;
			operation = leftNode.operation;
		}
		return this;
	}

	@Override
	public boolean inspect(Inspecting t) {
		return t.check(this) || left.inspect(t)
				|| (right != null && !operation.isUnary() && right.inspect(t));
	}

	@Override
	public void replaceChildrenByValues(GeoElement geo) {
		// left tree
		if (left.isGeoElement()) {
			GeoElement treeGeo = (GeoElement) left;
			if ((left == geo) || treeGeo.isChildOf(geo)) {
				left = treeGeo.copyInternal(treeGeo.getConstruction());
			}
		} else if (left instanceof ReplaceChildrenByValues) {
			((ReplaceChildrenByValues) left).replaceChildrenByValues(geo);
		}

		// right tree
		if (right != null) {
			if (right.isGeoElement()) {
				GeoElement treeGeo = (GeoElement) right;
				if ((right == geo) || treeGeo.isChildOf(geo)) {
					right = treeGeo.copyInternal(treeGeo.getConstruction());
				}
			} else if (right instanceof ReplaceChildrenByValues) {
				((ReplaceChildrenByValues) right).replaceChildrenByValues(geo);
			}
		}
	}

	/**
	 * Returns true when the given object is found in this expression tree.
	 */
	@Override
	final public boolean contains(ExpressionValue ev) {
		if (leaf) {
			return left.contains(ev);
		}
		return left.contains(ev) || right.contains(ev);
	}

	/**
	 * @return true if contains CAS evaluable function
	 */
	final public boolean containsCasEvaluableFunction() {
		if ((left instanceof CasEvaluableFunction)
				|| (right instanceof CasEvaluableFunction)) {
			return true;
		}

		if ((left instanceof ExpressionNode)
				&& ((ExpressionNode) left).containsCasEvaluableFunction()) {
			return true;
		}
		if ((right instanceof ExpressionNode)
				&& ((ExpressionNode) right).containsCasEvaluableFunction()) {
			return true;
		}

		return false;
	}

	/**
	 * @return true when the FunctionNVar is found in this expression tree.
	 */
	final public boolean containsGeoFunctionNVar() {
		if ((left instanceof GeoFunctionNVar)
				|| (right instanceof GeoFunctionNVar)) {
			return true;
		}

		if ((left instanceof ExpressionNode)
				&& ((ExpressionNode) left).containsGeoFunctionNVar()) {
			return true;
		}
		if ((right instanceof ExpressionNode)
				&& ((ExpressionNode) right).containsGeoFunctionNVar()) {
			return true;
		}

		return false;
	}

	/**
	 * transfers every non-polynomial in this tree to a polynomial. This is
	 * needed to enable polynomial simplification by evaluate()
	 * 
	 * @param equ
	 *            equation
	 * @param keepFraction
	 *            whether to use keep coefficients as fractions
	 * @return expanded polynomial
	 */
	protected final Polynomial makePolynomialTree(Equation equ,
			boolean keepFraction) {
		Polynomial lt;
		Polynomial rt = null;

		if (operation == Operation.FUNCTION_NVAR) {
			if ((left instanceof FunctionalNVar) && (right instanceof MyList)) {
				return makePolynomialTreeFromFunctionNVar(
						((FunctionalNVar) left).getFunction(), equ,
						keepFraction);
			}
		} else if (operation == Operation.FUNCTION) {
			if (left instanceof GeoFunction) {
				Function func = ((Functional) left).getFunction();

				return makePolyTreeFromFunction(func, equ, keepFraction);
			} else if (left instanceof ExpressionNode && ((ExpressionNode) left)
					.getOperation() == Operation.DERIVATIVE) {
				Function base = ((Functional) ((ExpressionNode) left).getLeft())
						.getFunction();
				int deg = (int) Math.ceil(
						((ExpressionNode) left).getRight().evaluateDouble());
				for (int i = 0; i < deg; i++) {
					base = new Function(
							((Functional) ((ExpressionNode) left).getLeft())
									.getFunction()
									.derivative(base.getFunctionVariable(),
											kernel)
									.wrap(),
							base.getFunctionVariable());
				}

				return makePolyTreeFromFunction(base, equ, keepFraction);
			}
		}
		if (!polynomialOperation(operation)) {
			if (left instanceof ValidExpression
					&& ((ValidExpression) left).containsFunctionVariable()) {
				equ.setIsPolynomial(false);
			}
			if (right instanceof ValidExpression
					&& ((ValidExpression) right).containsFunctionVariable()) {
				equ.setIsPolynomial(false);
			}
			return new Polynomial(kernel, new Term(
					new ExpressionNode(kernel, left, operation, right), ""));
		}
		ExpressionNode scalarExpanded = VectorArithmetic
				.expandScalarProduct(kernel, left, right, operation);
		if (scalarExpanded != null) {
			return scalarExpanded.makePolynomialTree(equ, keepFraction);
		}
		// transfer left subtree
		if (left.isExpressionNode()) {
			lt = ((ExpressionNode) left).makePolynomialTree(equ, keepFraction);
		} else if (left instanceof FunctionVariable) {
			lt = new Polynomial(kernel,
					((FunctionVariable) left).getSetVarString());
		} else {
			lt = new Polynomial(kernel, new Term(left, ""));
		}

		// transfer right subtree
		if (right != null) {
			if (right.isExpressionNode()) {
				rt = ((ExpressionNode) right).makePolynomialTree(equ,
						keepFraction);
			} else if (right instanceof FunctionVariable) {
				rt = new Polynomial(kernel,
						((FunctionVariable) right).getSetVarString());
			} else {
				if (right instanceof MyList) {
					MyList list = (MyList) right;
					for (int i = 0; i < list.size(); i++) {
						ExpressionValue ev = list.getListElement(i);
						if (ev instanceof ExpressionNode) {
							((ExpressionNode) ev).makePolynomialTree(equ,
									keepFraction);
						}
					}
				}
				// both for f(x,x) and x+3 we don't need the second argument
				// wrapped
				return lt.apply(operation, right, equ, keepFraction);
			}
		}
		return lt.apply(operation, rt, equ, keepFraction);
	}

	private Polynomial makePolynomialTreeFromFunctionNVar(FunctionNVar func,
			Equation equ, boolean keepFraction) {
		MyList list = ((MyList) right);
		ExpressionNode expr = func.getExpression().getCopy(kernel);
		if (func.getFunctionVariables().length == list.size()) {
			for (int i = 0; i < list.size(); i++) {
				ExpressionValue ev = list.getListElement(i);
				if (ev instanceof ExpressionNode) {
					ExpressionNode en = ((ExpressionNode) ev).getCopy(kernel);
					if (!equ.isFunctionDependent()) {
						equ.setFunctionDependent(en.containsFunctionVariable());
					}
					// we may only make polynomial trees after
					// replacement
					// en.makePolynomialTree(equ);
					ev = en;
				} else if (list.getListElement(i) instanceof FunctionVariable) {
					equ.setFunctionDependent(true);
				}
				expr = expr.replace(func.getFunctionVariables()[i], ev).wrap();
			}
		} else {
			throw new MyError(loc, Errors.IllegalArgumentNumber);
		}

		if (equ.isFunctionDependent()) {
			return expr.makePolynomialTree(equ, keepFraction);
		}
		return new Polynomial(kernel, new Term(
				new ExpressionNode(kernel, left, operation, right), ""));
	}

	private Polynomial makePolyTreeFromFunction(Function func, Equation equ,
			boolean keepFraction) {
		if (right instanceof ExpressionNode) {
			if (!equ.isFunctionDependent()) {
				equ.setFunctionDependent(
						((ExpressionNode) right).containsFunctionVariable());
			}
			// we may only make polynomial trees after replacement
			// ((ExpressionNode) right).makePolynomialTree(equ);
		} else if (right instanceof FunctionVariable) {
			equ.setFunctionDependent(true);
		}
		if (equ.isFunctionDependent()) {
			ExpressionNode expr = func.getExpression().getCopy(kernel);
			expr = expr.replace(func.getFunctionVariable(), right).wrap();
			return expr.makePolynomialTree(equ, keepFraction);
		}
		return new Polynomial(kernel, new Term(
				new ExpressionNode(kernel, left, operation, right), ""));
	}

	private static boolean polynomialOperation(Operation operation2) {
		switch (operation2) {
		case NO_OPERATION:
		case PLUS:
		case MINUS:
		case MULTIPLY:
		case MULTIPLY_OR_FUNCTION:
		case DIVIDE:
		case POWER:
		case FUNCTION:
		case FUNCTION_NVAR:
			return true;
		default:
			return false;
		}
	}

	/**
	 * returns true, if there are no variable objects in the subtree
	 */
	@Override
	final public boolean isConstant() {
		if (isLeaf()) {
			return left.isConstant();
		}
		return left.isConstant() && right.isConstant();
	}

	private ExpressionValue computeResolve() {
		Resolution res = new Resolution();
		res.setType(ValueType.resolve(operation, left, right, res));

		return res;
	}

	@Override
	final public boolean evaluatesToVectorNotPoint() {
		if (operation == Operation.RANDOM || operation == Operation.XCOORD
				|| operation == Operation.YCOORD
				|| operation == Operation.ZCOORD || operation == Operation.ABS
				|| operation == Operation.ARG || operation == Operation.ALT) {
			return false;
		}
		if (isLeaf()) {
			return left.evaluatesToVectorNotPoint();
		}
		// sin(vector), conjugate(vector), ... are complex numbers
		if (Operation.isSimpleFunction(operation)
				|| operation == Operation.CONJUGATE) {
			return false;
		}
		boolean leftVector = left.evaluatesToVectorNotPoint();
		boolean rightVector = right.evaluatesToVectorNotPoint();
		if (operation == Operation.PLUS && left.evaluatesToNDVector()) {
			return leftVector && rightVector;
		}
		boolean ret = leftVector || rightVector;

		if (leftVector && rightVector && (operation == Operation.MULTIPLY
				|| operation == Operation.VECTORPRODUCT)) {
			ret = false;
		}

		return ret;
	}

	/**
	 * Force this to evaluate to vector
	 */
	public void setForceVector() {
		// this expression should be considered as a vector, not a point
		forceVector = true;
		ExpressionValue value = unwrap();
		if (value instanceof MyVecNDNode) {
			((MyVecNDNode) value).setVectorPrintingMode();
		}
	}

	/**
	 * @return true if forced to evaluate to vector
	 */
	final public boolean isForcedVector() {
		return forceVector;
	}

	/**
	 * 
	 */
	public void setForcePoint() {
		// this expression should be considered as a point, not a vector
		forcePoint = true;
	}

	/**
	 * @return true iff forced to evaluate to point
	 */
	final public boolean isForcedPoint() {
		return forcePoint;
	}

	/**
	 * Force to evaluate to function
	 */
	public void setForceFunction() {
		forceFunction = true;
	}

	/**
	 * @return true iff forced to be a function
	 */
	final public boolean isForcedFunction() {
		return forceFunction;
	}

	/**
	 * Force to evaluate to inequality
	 */
	public void setForceInequality() {
		forceInequality = true;
	}

	/**
	 * @return true iff forced to be an inequality
	 */
	final public boolean isForceInequality() {
		return forceInequality;
	}

	/**
	 * Returns whether this tree has any operations
	 * 
	 * @return true iff this tree has any operations
	 */
	final public boolean hasOperations() {
		if (leaf) {
			if (left.isExpressionNode()) {
				return ((ExpressionNode) left).hasOperations();
			} else if (left instanceof MyVecNDNode) {
				return true;
			} else {
				return false;
			}
		}

		return (right != null);
	}

	/**
	 * Returns all GeoElement objects in the subtree
	 */
	@Override
	final public HashSet<GeoElement> getVariables(SymbolicMode mode) {
		if (leaf) {
			return left.getVariables(mode);
		}

		HashSet<GeoElement> leftVars = left.getVariables(mode);
		HashSet<GeoElement> rightVars = right.getVariables(mode);
		if (leftVars == null) {
			return rightVars;
		} else if (rightVars == null) {
			return leftVars;
		} else {
			leftVars.addAll(rightVars);
			return leftVars;
		}
	}

	/**
	 * @param mode
	 *            whether to resolve vars in symbolic or numeric mode
	 * @return GeoElement variables
	 */
	final public GeoElement[] getGeoElementVariables(SymbolicMode mode) {
		HashSet<GeoElement> varset = getVariables(mode);
		if (varset == null) {
			return null;
		}
		Iterator<GeoElement> i = varset.iterator();
		GeoElement[] ret = new GeoElement[varset.size()];
		int j = 0;
		while (i.hasNext()) {
			ret[j++] = i.next();
		}
		return ret;
	}

	@Override
	final public boolean isLeaf() {
		return leaf; // || operation == NO_OPERATION;
	}

	/**
	 * @return true if this is leaf containing only GeoElement
	 */
	final public boolean isSingleGeoElement() {
		return leaf && left.isGeoElement();
	}

	/**
	 * @return left subexpression as GeoElement
	 */
	final public GeoElement getSingleGeoElement() {
		return (GeoElement) left;
	}

	/**
	 * @return true if given value is an imaginary unit
	 */
	public static boolean isImaginaryUnit(ExpressionValue value) {
		return (value instanceof GeoVec2D)
				&& ((GeoVec2D) value).isImaginaryUnit();
	}

	/**
	 * @return true iff this node contains MyStringBuffer as subnode
	 */
	public boolean containsMyStringBuffer() {

		if ((left instanceof MyStringBuffer)
				|| (right instanceof MyStringBuffer)) {
			return true;
		}

		boolean ret = false;

		if (left.isExpressionNode()) {
			ret = ret || ((ExpressionNode) left).containsMyStringBuffer();
		}
		if ((right != null) && right.isExpressionNode()) {
			ret = ret || ((ExpressionNode) right).containsMyStringBuffer();
		}

		return ret;
	}

	/**
	 * Returns a string representation of this node that can be used with the
	 * given CAS, e.g. "*" and "^" are always printed.
	 * 
	 * @param symbolic
	 *            true for variable names, false for values of variables
	 * @param tpl
	 *            String template
	 * @return string representation of this node that can be used with given
	 *         CAS
	 */
	public String getCASstring(StringTemplate tpl, boolean symbolic) {
		String ret = null;

		if (leaf) { // leaf is GeoElement or not
			ret = getCasString(left, tpl, symbolic, false);
		}

		// STANDARD case: no leaf
		else {
			// expression node

			// we could group the factors of all possible numerators here.
			// we won't do that.
			// https://help.geogebra.org/topic/factor-disappearing
			// numerGroup();

			String leftStr = getCasString(left, tpl, symbolic, false);
			String rightStr = null;

			if (right != null) {
				rightStr = getCasString(right, tpl, symbolic, shaveBrackets());
			}
			// do not send random() to CAS
			// #4072
			if (operation == Operation.RANDOM) {
				double d = left.evaluateDouble();
				leftStr = kernel.format(d, StringTemplate.defaultTemplate);
				ret = ExpressionSerializer.operationToString(left, right,
						operation, leftStr, rightStr, true, tpl, kernel);
			} else {
				ret = ExpressionSerializer.operationToString(left, right,
						operation, leftStr, rightStr, !symbolic, tpl, kernel);
			}
		}

		return ret;
	}

	/**
	 * @param expr expression
	 * @param tpl template
	 * @param symbolic whether to print label for geos
	 * @param shaveOffBrackets whether to shave off brackets in case expr is a list
	 * @return serialized expression
	 */
	public static String getCasString(ExpressionValue expr, StringTemplate tpl,
			boolean symbolic, boolean shaveOffBrackets) {
		if (symbolic && expr.isGeoElement()) {
			if (((GeoElement) expr).isRandomGeo()) {
				return expr.toValueString(tpl);
			}
			return ((GeoElement) expr).getLabel(tpl);
		} else if (expr.isExpressionNode()) {
			return ((ExpressionNode) expr).getCASstring(tpl, symbolic);
		} else if (expr.isGeoElement()
				&& ((GeoElement) expr).getDefinition() != null) {
			return "(" + ((GeoElement) expr).getDefinition().toValueString(tpl) + ")";
		} else if (shaveOffBrackets) {
			return ((MyList) expr).toString(tpl, !symbolic, false);
		}
		return symbolic ? expr.toString(tpl) : expr.toValueString(tpl);
	}

	/**
	 * Decide whether the current expression value must be expanded for
	 * OpenGeoProver. This code helps both the Wu and the Area method.
	 * 
	 * @param ev
	 *            left or right side of the expression
	 * @return if expansion is required
	 */
	final private boolean expandForOGP(ExpressionValue ev) {
		// The following types of operations and GeoElements are supported.
		// See also the OGP code for the available (parsable) expressions.
		if (operation.equals(Operation.EQUAL_BOOLEAN)
				&& ev instanceof GeoSegment) {
			return false; // don't expand "AreEqual[Segment[X,Y],Segment[Z,W]]"
		}
		// format expressions
		return ((operation.equals(Operation.EQUAL_BOOLEAN)
				|| operation.equals(Operation.DIVIDE)
				|| operation.equals(Operation.MULTIPLY)
				|| operation.equals(Operation.MINUS)
				|| operation.equals(Operation.PLUS)
				|| operation.equals(Operation.POWER))
				&& (ev instanceof GeoSegment || ev instanceof GeoPolygon
						|| ev instanceof GeoNumeric));
	}

	/**
	 * Returns a string representation of this node.
	 */
	@Override
	final public String toString(StringTemplate tpl) {

		if (isSecret()) {
			return secretMaskingAlgo.getDefinition(tpl);
		}

		if (leaf) { // leaf is GeoElement or not
			return getLabelOrDefinition(left, tpl);
		}

		// expression node
		String leftStr = getLabelOrDefinition(left, tpl);
		String rightStr = null;

		if (right != null) {
			if (shaveBrackets()) {
				rightStr = ((MyList) right).toString(tpl, false, false);
			} else {
				rightStr = getLabelOrDefinition(right, tpl);
			}
		}
		if (tpl.getStringType().equals(StringType.OGP)
				&& expandForOGP(left)) {
			if (left instanceof GeoElement) {
				leftStr = ((GeoElement) left).getDefinition(tpl);
			}
			if (right instanceof GeoElement) {
				rightStr = ((GeoElement) right).getDefinition(tpl);
			}
		}
		return ExpressionSerializer.operationToString(left, right, operation,
				leftStr, rightStr, false, tpl, kernel);
	}

	/**
	 * @param left expression
	 * @param tpl template
	 * @return label or symbolic string
	 */
	public static String getLabelOrDefinition(ExpressionValue left, StringTemplate tpl) {
		if (left.isGeoElement()) {
			return ((GeoElement) left).getLabel(tpl);
		}
		return left.toString(tpl);
	}

	private boolean shaveBrackets() {
		return (operation == Operation.FUNCTION_NVAR
				|| operation == Operation.ELEMENT_OF
				|| (operation == Operation.VEC_FUNCTION
						&& left instanceof GeoSurfaceCartesianND))
				&& (right instanceof MyList);
	}

	/** like toString() but with current values of variables */
	@Override
	final public String toValueString(StringTemplate tpl) {
		if (isSecret()) {
			return secretMaskingAlgo.getDefinition(tpl);
		}

		if (isLeaf()) { // leaf is GeoElement or not
			if (left != null) {
				return left.toValueString(tpl);
			}
		}

		// needed for TRAC-4217
		if (tpl.hasCASType() && left instanceof GeoNumeric
				&& !(left instanceof GeoDummyVariable)
				&& ((GeoElement) left).getLabelSimple() != null
				&& ((GeoElement) left).getLabelSimple().startsWith("c_")) {
			((GeoNumeric) left).setSendValueToCas(false);
		}

		// expression node
		String leftStr = left.toValueString(tpl);

		String rightStr = null;
		if (right != null) {
			if (shaveBrackets()) {
				rightStr = ((MyList) right).toString(tpl, true, false);
			} else {
				rightStr = right.toValueString(tpl);
			}
		}

		return ExpressionSerializer.operationToString(left, right, operation,
				leftStr, rightStr, true, tpl, kernel);
	}

	@Override
	final public String toOutputValueString(StringTemplate tpl) {
		if (isSecret()) {
			return secretMaskingAlgo.getDefinition(tpl);
		}
		if (isLeaf()) { // leaf is GeoElement or not
			if (left != null) {
				return left.toOutputValueString(tpl);
			}
		}

		// expression node
		String leftStr = left.toOutputValueString(tpl);

		String rightStr = null;
		if (right != null) {
			if (shaveBrackets()) {
				rightStr = ((MyList) right).toString(tpl, true, false);
			} else {
				rightStr = right.toOutputValueString(tpl);
			}
		}

		return ExpressionSerializer.operationToString(left, right, operation,
				leftStr, rightStr, true, tpl, kernel);
	}

	/**
	 * Returns a string representation of this node in LaTeX syntax. Note: the
	 * resulting string may contain special unicode characters like greek
	 * characters or special signs for integer exponents. These sould be handled
	 * afterwards!
	 * 
	 * @param symbolic
	 *            true for variable names, false for values of variables
	 */
	@Override
	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		String ret;
		if (secretMaskingAlgo != null) {
			return secretMaskingAlgo.getDefinition(tpl);
		}
		if (isLeaf()) { // leaf is GeoElement or not
			if (left != null) {
				ret = left.toLaTeXString(symbolic, tpl);
				return checkMathml(ret, tpl);
			}
		}

		// expression node
		String leftStr = left.toLaTeXString(symbolic, tpl);
		String rightStr = null;
		if (right != null) {

			if (((operation == Operation.FUNCTION_NVAR)
					|| (operation == Operation.ELEMENT_OF))
					&& (right instanceof MyList)) {
				// 1 character will be taken from the left and right
				// of rightStr in operationToString, but more
				// is necessary in case of LaTeX, we do that here
				// " \\{ " is put by MyList 5 - 1(escape) -1(operationToString)
				rightStr = ((MyList) right).toLaTeXStringNoBrackets(symbolic,
						tpl);

			} else {
				rightStr = right.toLaTeXString(symbolic, tpl);
			}
		}

		// build latex string
		ret = ExpressionSerializer.operationToString(left, right, operation,
				leftStr, rightStr, !symbolic, tpl, kernel);

		return checkMathml(ret, tpl);
	}

	/**
	 * make sure string wrapped in MathML if necessary eg <ci>x</ci>
	 */
	private static String checkMathml(String str, StringTemplate tpl) {
		if (tpl.hasType(StringType.CONTENT_MATHML) && str.charAt(0) != '<') {
			return "<ci>" + str + "</ci>";
		}

		return str;
	}

	/**
	 * @param exp
	 *            expression
	 * @return whether top level operation is * or /
	 */
	public static boolean isMultiplyOrDivide(ExpressionNode exp) {
		return exp.getOperation().equals(Operation.MULTIPLY)
				|| exp.getOperation().equals(Operation.DIVIDE);
	}

	/**
	 * return operation number for expression nodes and -1 for other expression
	 * values
	 * 
	 * @param ev
	 *            expression value
	 * @return operation number
	 */
	static public int opID(ExpressionValue ev) {
		if (ev.isExpressionNode()) {
			Operation op = ((ExpressionNode) ev).operation;
			// input (x>y)==(x+y>3) must be kept
			if (op.equals(Operation.GREATER) || op.equals(Operation.LESS)
					|| op.equals(Operation.LESS_EQUAL)
					|| op.equals(Operation.GREATER_EQUAL)) {
				return Operation.NOT_EQUAL.ordinal() - 1;
			}
			return op.ordinal();
		}
		return -1;
	}

	@Override
	public boolean isNumberValue() {
		return this.evaluatesToNumber(false);
	}

	@Override
	final public boolean isExpressionNode() {
		return true;
	}

	/**
	 * Returns true iff ev1 and ev2 are equal.
	 * 
	 * https://www.geogebra.org/m/NTKKwVWK
	 * 
	 * @param ev1
	 *            first value to compare
	 * @param ev2
	 *            second value to compare
	 * @return true iff ev1 and ev2 are equal
	 */
	public static boolean isEqual(ExpressionValue ev1, ExpressionValue ev2) {
		if (ev1 instanceof NumberValue && ev2 instanceof NumberValue) {
			return DoubleUtil.isEqual(ev1.evaluateDouble(),
					ev2.evaluateDouble(), Kernel.STANDARD_PRECISION);
		} else if (ev1 instanceof TextValue && ev2 instanceof TextValue) {
			return ((TextValue) ev1)
					.toValueString(StringTemplate.defaultTemplate)
					.equals(((TextValue) ev2)
							.toValueString(StringTemplate.defaultTemplate));
		} else if (ev1 instanceof VectorValue && ev2 instanceof VectorValue) {
			return ((VectorValue) ev1).getVector()
					.isEqual(((VectorValue) ev2).getVector());
		} else if (ev1 instanceof BooleanValue && ev2 instanceof BooleanValue) {
			return ((BooleanValue) ev1).getMyBoolean()
					.getBoolean() == ((BooleanValue) ev2).getMyBoolean()
							.getBoolean();
		} else if (ev1.isGeoElement() && ev2.isGeoElement()) {
			return ((GeoElement) ev1).isEqual(((GeoElement) ev2));
		} else if (ev1 instanceof Functional && ev2 instanceof Functional) {
			return ((Functional) ev1).getGeoFunction()
					.isEqual(((Functional) ev2).getGeoFunction());
		}

		return false;
	}

	/**
	 * Returns whether the given expression will give the same String output as
	 * val.
	 * 
	 * @param symbolic
	 *            whether we should use the value (true) or the label (false) of
	 *            ev when it is a GeoElement
	 * @param val
	 *            numeric value
	 * @param ev
	 *            expression value to compare with val
	 * @return true iff output of ev and val are the same
	 */
	final public static boolean isEqualString(ExpressionValue ev, double val,
			boolean symbolic) {
		if (ev.isLeaf() && (ev instanceof NumberValue)) {
			// function variables need to be kept
			// special doubles like pi, degree, rad need to be kept
			if (ev instanceof FunctionVariable || ev instanceof GeoDummyVariable
					|| ev instanceof MySpecialDouble) {
				return false;
			}

			// check if ev is a labeled GeoElement
			if (symbolic) {
				if (ev.isGeoElement()) {
					// labeled GeoElement
					GeoElement geo = (GeoElement) ev;
					if (geo.isLabelSet() || geo.isLocalVariable()
							|| !geo.isIndependent()) {
						return false;
					}
				}
			}

			return ev.evaluateDouble() == val;
		}
		return false;
	}

	@Override
	public boolean isTopLevelCommand() {
		return isLeaf() && (left instanceof Command);
	}

	@Override
	public boolean isTopLevelCommand(String checkName) {
		return isLeaf() && (left instanceof ValidExpression)
				&& (((ValidExpression) left).isTopLevelCommand(checkName));
	}

	@Override
	public Command getTopLevelCommand() {
		if (isTopLevelCommand()) {
			return (Command) left;
		}
		return null;
	}

	/**
	 * If the expression is linear in fv, returns the corresponding coefficient.
	 * Otherwise returns null.
	 * 
	 * @param fv
	 *            variable whose coefficient we want
	 * @return coefficient or null
	 */
	public Double getCoefficient(FunctionVariable fv) {
		if (this.isLeaf()) {
			if (this.toString(StringTemplate.defaultTemplate)
					.equals(fv.toString(StringTemplate.defaultTemplate))) {
				return 1.0;
			}

			return 0.0;

		}

		Double lc = getLeftTree().getCoefficient(fv);
		Double rc = getRightTree() == null ? null
				: getRightTree().getCoefficient(fv);
		if ((lc == null) || (rc == null)) {
			return null;
		}
		switch (this.operation) {
		case PLUS:
			return lc + rc;

		case MINUS:
			return lc - rc;

		case MULTIPLY:
			if (!getRightTree().containsFunctionVariable()) {
				return lc * getRightTree().evaluateDouble();
			} else if (!getLeftTree().containsFunctionVariable()) {
				return rc * getLeftTree().evaluateDouble();
			}
			break;

		case DIVIDE:
			if (!getRightTree().containsFunctionVariable()) {
				return lc / getRightTree().evaluateDouble();
			}
			break;
		default:
			break;
		}

		if ((left.contains(fv) || right.contains(fv))) {
			return null;
		}

		return 0.0;
	}

	/*
	 * appends a string to sb, brackets are put around it if the order of
	 * operation dictates
	 */

	/**
	 * @param op
	 *            operation
	 * @return whether this operation returns boolean and can be used in chain
	 *         eg. x < y <=z
	 */
	public static boolean chainedBooleanOp(Operation op) {
		switch (op) {
		case EQUAL_BOOLEAN:
		case NOT_EQUAL:
		case IS_SUBSET_OF:
		case IS_SUBSET_OF_STRICT:
		case LESS:
		case LESS_EQUAL:
		case GREATER:
		case GREATER_EQUAL:
		case PERPENDICULAR:
		case PARALLEL:
			return true;
		default:
			return false;
		}
	}

	/**
	 * @param ev
	 *            expression
	 * @param v
	 *            value
	 * @return whether expression is a constant with given value, possibly
	 *         wrapped
	 */
	public static boolean isConstantDouble(ExpressionValue ev, double v) {
		ExpressionValue base = ev.unwrap();
		return base instanceof MyDouble && base.isConstant()
		// don't use Kernel.isEqual() to check == 0
		// as can lose leading coefficient of polynomial
				&& v == ((MyDouble) base).getDouble();
	}

	/**
	 * @param v2
	 *            value to add
	 * @return result of addition
	 */
	public ExpressionNode plus(ExpressionValue v2) {
		if (isConstantDouble(v2, 0)) {
			return this;
		}
		if (this.isLeaf() && isConstantDouble(left, 0)) {
			return v2.wrap();
		}
		return new ExpressionNode(kernel, this, Operation.PLUS, v2);
	}

	/**
	 * @param v2
	 *            value to compare
	 * @return result this < v2
	 */
	public ExpressionNode lessThan(ExpressionValue v2) {
		return new ExpressionNode(kernel, this, Operation.LESS, v2);
	}

	/**
	 * @param d
	 *            value to compare
	 * @return result this < d
	 */
	public ExpressionNode lessThan(double d) {
		return new ExpressionNode(kernel, this, Operation.LESS,
				new MyDouble(kernel, d));
	}

	/**
	 * @param d
	 *            value to compare
	 * @return result this <= d
	 */
	public ExpressionNode lessThanEqual(double d) {
		return new ExpressionNode(kernel, this, Operation.LESS_EQUAL,
				new MyDouble(kernel, d));
	}

	/**
	 * @return result of erf(this)
	 */
	public ExpressionNode erf() {
		return new ExpressionNode(kernel, this, Operation.ERF, null);
	}

	/**
	 * @return result of abs(this)
	 */
	public ExpressionNode abs() {
		return new ExpressionNode(kernel, this, Operation.ABS, null);
	}

	/**
	 * @return result of sec(this)
	 */
	public ExpressionNode sec() {
		return new ExpressionNode(kernel, this, Operation.SEC, null);
	}

	/**
	 * @return result of tan(this)
	 */
	public ExpressionNode tan() {
		return new ExpressionNode(kernel, this, Operation.TAN, null);
	}

	/**
	 * @return result of sech(this)
	 */
	public ExpressionNode sech() {
		return new ExpressionNode(kernel, this, Operation.SECH, null);
	}

	/**
	 * @return result of tanh(this)
	 */
	public ExpressionNode tanh() {
		return new ExpressionNode(kernel, this, Operation.TANH, null);
	}

	/**
	 * @return result of cosech(this)
	 */
	public ExpressionNode cosech() {
		return new ExpressionNode(kernel, this, Operation.CSCH, null);
	}

	/**
	 * @return result of coth(this)
	 */
	public ExpressionNode coth() {
		return new ExpressionNode(kernel, this, Operation.COTH, null);
	}

	/**
	 * @return result of cosec(this)
	 */
	public ExpressionNode cosec() {
		return new ExpressionNode(kernel, this, Operation.CSC, null);
	}

	/**
	 * @return result of cot(this)
	 */
	public ExpressionNode cot() {
		return new ExpressionNode(kernel, this, Operation.COT, null);
	}

	/**
	 * @return result of this!
	 */
	public ExpressionNode factorial() {
		return new ExpressionNode(kernel, this, Operation.FACTORIAL, null);
	}

	/**
	 * @return result of ln(this)
	 */
	public ExpressionNode ln() {
		return new ExpressionNode(kernel, this, Operation.LOG, null);
	}

	/**
	 * @return result of gamma(this)
	 */
	public ExpressionNode gamma() {
		return new ExpressionNode(kernel, this, Operation.GAMMA, null);
	}

	/**
	 * @param v2
	 *            input
	 * @return result of gamma(v2, this)
	 */
	public ExpressionNode gammaIncompleteReverseArgs(ExpressionValue v2) {
		return new ExpressionNode(kernel, v2, Operation.GAMMA_INCOMPLETE, this);
	}

	/**
	 * @param v2
	 *            input
	 * @return result of gamma(this, v2)
	 */
	public ExpressionNode gammaIncomplete(ExpressionValue v2) {
		return new ExpressionNode(kernel, this, Operation.GAMMA_INCOMPLETE, v2);
	}

	/**
	 * @param v2
	 *            input
	 * @return result of beta(this, v2)
	 */
	public ExpressionNode beta(ExpressionValue v2) {
		return new ExpressionNode(kernel, this, Operation.BETA, v2);
	}

	/**
	 * @return result of exp(this)
	 */
	public ExpressionNode exp() {
		return new ExpressionNode(kernel, this, Operation.EXP, null);
	}

	/**
	 * @param n
	 *            order of polygamma
	 * @return result of polyganma(n, this)
	 */
	public ExpressionNode polygamma(double n) {
		return new ExpressionNode(kernel, wrap(n), Operation.POLYGAMMA, this);
	}

	/**
	 * @return result of sin(this)
	 */
	public ExpressionNode sin() {
		return new ExpressionNode(kernel, this, Operation.SIN, null);
	}

	/**
	 * @return result of sinh(this)
	 */
	public ExpressionNode sinh() {
		return new ExpressionNode(kernel, this, Operation.SINH, null);
	}

	/**
	 * @return result of cosh(this)
	 */
	public ExpressionNode cosh() {
		return new ExpressionNode(kernel, this, Operation.COSH, null);
	}

	/**
	 * @return result of cos(this)
	 */
	public ExpressionNode cos() {
		return new ExpressionNode(kernel, this, Operation.COS, null);
	}

	/**
	 * @return result of 1/this
	 */
	public ExpressionNode reciprocate() {
		return new ExpressionNode(kernel, this, Operation.POWER,
				new MyDouble(kernel, -1));
	}

	/**
	 * @return result of sqrt(this)
	 */
	public ExpressionNode sqrt() {
		return new ExpressionNode(kernel, this, Operation.SQRT, null);
	}

	/**
	 * @return result of cbrt(this)
	 */
	public ExpressionNode cbrt() {
		return new ExpressionNode(kernel, this, Operation.CBRT, null);
	}

	/**
	 * @return result of sqrt(this)
	 */
	public ExpressionNode sgn() {
		return new ExpressionNode(kernel, this, Operation.SGN, null);
	}

	/**
	 * @return result of atan(this)
	 */
	public ExpressionNode atan() {
		return new ExpressionNode(kernel, this, Operation.ARCTAN, null);
	}

	/**
	 * @return result of this * -1
	 */
	public ExpressionNode reverseSign() {
		return new ExpressionNode(kernel, new MinusOne(kernel), Operation.MULTIPLY, this);
	}

	/**
	 * @return result of 0 - this
	 */
	public ExpressionNode reverseSign2() {
		return new ExpressionNode(kernel, new MyDouble(kernel, 0.0),
				Operation.MINUS, this);
	}

	/**
	 * @param v1
	 *            input
	 * @param v2
	 *            input
	 * @return result of betaRegularized(this, v1, v2)
	 */
	public ExpressionNode betaRegularized(ExpressionValue v1,
			ExpressionValue v2) {
		return new ExpressionNode(kernel, new MyNumberPair(kernel, this, v1),
				Operation.BETA_INCOMPLETE_REGULARIZED, v2);
	}

	/**
	 * @return result of this^2
	 */
	public ExpressionNode square() {
		return new ExpressionNode(kernel, this, Operation.POWER,
				new MyDouble(kernel, 2.0));
	}

	/**
	 * @param v2
	 *            value to subtract
	 * @return result of subtract
	 */
	public ExpressionNode subtract(ExpressionValue v2) {
		if (isConstantDouble(v2, 0)) {
			return this;
		}
		if (this.isLeaf() && isConstantDouble(left, 0)) {
			return v2.wrap().reverseSign();
		}
		return new ExpressionNode(kernel, this, Operation.MINUS, v2);
	}

	/**
	 * @param d
	 *            value to add
	 * @return result of add
	 */
	public ExpressionNode plus(double d) {
		if (d == 0) {
			return this;
		}
		return new ExpressionNode(kernel, this, Operation.PLUS,
				new MyDouble(kernel, d));
	}

	/**
	 * @param d
	 *            value to add
	 * @return result of add
	 */
	public ExpressionNode plusR(double d) {
		if (d == 0) {
			return this;
		}
		return new ExpressionNode(kernel, new MyDouble(kernel, d),
				Operation.PLUS, this);
	}

	/**
	 * @param d
	 *            value to multiply
	 * @return result of multiply
	 */
	public ExpressionNode multiply(double d) {
		ExpressionNode specialCase = multiplyOneOrZero(d);
		if (specialCase != null) {
			return specialCase;
		}
		return new ExpressionNode(kernel, this, Operation.MULTIPLY,
				new MyDouble(kernel, d));
	}

	/**
	 * @param d
	 *            value to multiply
	 * @return result of multiply
	 */
	public ExpressionNode multiplyR(double d) {
		ExpressionNode specialCase = multiplyOneOrZero(d);
		if (specialCase != null) {
			return specialCase;
		}
		MyDouble left = d == -1 ? new MinusOne(kernel) : new MyDouble(kernel, d);
		return new ExpressionNode(kernel, left, Operation.MULTIPLY, this);
	}

	private ExpressionNode multiplyOneOrZero(double d) {
		if (d == 0) {
			// don't use Kernel.isZero() to check == 0
			// as can lose leading coefficient of polynomial
			return new ExpressionNode(kernel, 0);
		} else if (1 == d) {
			return this;
		} else if (isConstantDouble(this, 1)) {
			return new ExpressionNode(kernel, d);
		}
		return null;
	}

	/**
	 * @param d
	 *            value to multiply
	 * @return result of multiply
	 */
	public ExpressionNode power(double d) {
		if (DoubleUtil.isZero(d)) {
			return new ExpressionNode(kernel, 1);
		} else if (DoubleUtil.isEqual(1, d) || isConstantDouble(this, 1)
				|| isConstantDouble(this, 0)) {
			return this;
		}
		return new ExpressionNode(kernel, this, Operation.POWER,
				new MyDouble(kernel, d));
	}

	/**
	 * @param d
	 *            value to subtract
	 * @return this - d
	 */
	public ExpressionNode subtract(double d) {
		if (d == 0) {
			return this;
		}
		return new ExpressionNode(kernel, this, Operation.MINUS,
				new MyDouble(kernel, d));
	}

	/**
	 * @param d
	 *            value to subtract
	 * @return d - this
	 */
	public ExpressionNode subtractR(double d) {
		if (d == 0) {
			return new ExpressionNode(kernel, new MyDouble(kernel, -1),
					Operation.MULTIPLY, this);
		}
		return new ExpressionNode(kernel, new MyDouble(kernel, d),
				Operation.MINUS, this);
	}

	/**
	 * @param v2
	 *            coefficient
	 * @return result of multiplication
	 */
	public ExpressionNode multiply(ExpressionValue v2) {
		if (isConstantDouble(v2, 0) || isConstantDouble(this, 1)) {
			return v2.wrap();
		}
		if (isConstantDouble(v2, 1) || isConstantDouble(this, 0)) {
			return this;
		}
		ExpressionValue left = isConstantDouble(v2, -1) ? new MinusOne(kernel) : v2;
		return new ExpressionNode(kernel, left, Operation.MULTIPLY, this);
	}

	/**
	 * @param v2
	 *            coefficient
	 * @return result of multiplication
	 */
	public ExpressionNode multiplyR(ExpressionValue v2) {
		if (isConstantDouble(v2, 0) || isConstantDouble(this, 1)) {
			return v2.wrap();
		}
		if (isConstantDouble(v2, 1) || isConstantDouble(this, 0)) {
			return this;
		}
		return new ExpressionNode(kernel, this, Operation.MULTIPLY, v2);
	}

	/**
	 * @param v2
	 *            exponent
	 * @return resulting power
	 */
	public ExpressionNode power(ExpressionValue v2) {
		if (isConstantDouble(v2, 0)) {
			return new ExpressionNode(kernel, 1);
		}
		if (isConstantDouble(v2, 1)) {
			return this;
		}
		return new ExpressionNode(kernel, this, Operation.POWER, v2);
	}

	/**
	 * @param d
	 *            exponent
	 * @return d ^ this
	 */
	public ExpressionNode powerR(double d) {
		return new ExpressionNode(kernel, new MyDouble(kernel, d),
				Operation.POWER, this);
	}

	/**
	 * @param v2
	 *            divisor
	 * @return result of division
	 */
	public ExpressionNode divide(ExpressionValue v2) {
		return new ExpressionNode(kernel, this, Operation.DIVIDE, v2);
	}

	/**
	 * Like divide, but simplify this/1=this, 0/y=0
	 * 
	 * @param v2
	 *            denominator
	 * @return this / v2
	 */
	public ExpressionNode divideSimplify(ExpressionValue v2) {
		// 0 / y = 0
		if (isConstantDouble(this, 0)) {
			return this;
		}
		// x / 1 = x
		if (isConstantDouble(v2, 1)) {
			return this;
		}
		return new ExpressionNode(kernel, this, Operation.DIVIDE, v2);
	}

	/**
	 * @param d
	 *            divisor
	 * @return result of division
	 */
	public ExpressionNode divide(double d) {
		if (d == 1) {
			// don't use Kernel.isEqual() to check == 1
			// as can lose leading coefficient of polynomial
			return this;
		}
		if (d == -1) {
			// don't use Kernel.isEqual() to check == -1
			// as can lose leading coefficient of polynomial
			return this.multiplyR(-1);
		}

		return new ExpressionNode(kernel, this, Operation.DIVIDE,
				new MyDouble(kernel, d));
	}

	/**
	 * @param v2
	 *            clause
	 * @return result of conjuction
	 */
	public ExpressionNode and(ExpressionValue v2) {
		return new ExpressionNode(kernel, this, Operation.AND, v2);
	}

	/**
	 * @return negation of this expression (optimizes negation of >,<,=>,<=)
	 */
	public ExpressionNode negation() {
		if (Operation.AND_INTERVAL.equals(operation)) {
			// unary, not binary
			return new ExpressionNode(kernel, left.wrap().negation(),
					Operation.OR, right.wrap().negation());
		}
		Operation opNegated = this.operation.negate();

		if (Operation.NOT.equals(opNegated)) {
			// unary, not binary
			return new ExpressionNode(kernel, this, Operation.NOT, null);
		}
		return new ExpressionNode(kernel, left, opNegated, right);
	}

	/**
	 * @param toRoot
	 *            true to replace powers by roots
	 * @param maxRoot
	 *            do not use roots higher than that
	 * @return this node with replaced powers / roots
	 */
	public boolean replacePowersRoots(boolean toRoot, int maxRoot) {
		boolean didReplacement = false;

		if (toRoot && getOperation() == Operation.POWER
				&& getRight().isExpressionNode()) {
			boolean hit = false;
			ExpressionNode rightLeaf = (ExpressionNode) getRight();

			// replaces 1 DIVIDE 2 by SQRT 2, and same for CBRT
			if ((rightLeaf.getOperation() == Operation.DIVIDE)
					&& rightLeaf.isConstant()) {
				if (rightLeaf.getRight()
						.toString(StringTemplate.defaultTemplate).equals("2")) {
					setOperation(Operation.SQRT);
					hit = true;
				} else if (rightLeaf.getRight()
						.toString(StringTemplate.defaultTemplate).equals("3")) {
					setOperation(Operation.CBRT);
					hit = true;
				} else if (!rightLeaf.getRight().unwrap().isExpressionNode()
						&& DoubleUtil.isInteger(
								rightLeaf.getRight().evaluateDouble())
						&& rightLeaf.getRight().evaluateDouble() <= maxRoot) {
					setOperation(Operation.NROOT);
					setRight(new MyDouble(kernel,
							rightLeaf.getRight().evaluateDouble()));
					hit = true;
				}
				if (hit) {
					didReplacement = true;
					if (rightLeaf.getLeft()
							.toString(StringTemplate.defaultTemplate)
							.equals("1")) {
						if (operation != Operation.NROOT) {
							unsetRight();
						}
					} else { // to parse x^(c/2) to sqrt(x^c)
						double c = 1;
						if (rightLeaf.getLeft().isConstant()) {
							c = rightLeaf.getLeft().evaluateDouble();
						}
						if (c < 0) {

							setRight(new ExpressionNode(kernel,
									getLeft().wrap().power(-c), getOperation(),
									getRight()));
							setOperation(Operation.DIVIDE);
							setLeft(new MyDouble(kernel, 1.0));

						} else {
							setLeft(new ExpressionNode(kernel, getLeft(),
									Operation.POWER, rightLeaf.getLeft()));
						}
					}
				}

			}
		} else if (!toRoot) {
			boolean hit = false;
			// replaces SQRT 2 by 1 DIVIDE 2, and same for CBRT
			ExpressionNode power = null;
			if (getOperation() == Operation.SQRT) {
				power = new ExpressionNode(kernel, new MyDouble(kernel, 1),
						Operation.DIVIDE, new MyDouble(kernel, 2));
				hit = true;
			} else if (getOperation() == Operation.CBRT) {
				power = new ExpressionNode(kernel, new MyDouble(kernel, 1),
						Operation.DIVIDE, new MyDouble(kernel, 3));
				hit = true;
			} else if (getOperation() == Operation.NROOT) {
				power = new ExpressionNode(kernel, new MyDouble(kernel, 1),
						Operation.DIVIDE, right);
				hit = true;
			}
			if (hit) {
				didReplacement = true;
				setOperation(Operation.POWER);
				setRight(power);
			}
		}

		return didReplacement;
	}

	private void unsetRight() {
		setRight(new MyDouble(kernel, Double.NaN));
	}

	/**
	 * Replaces one object with another
	 * 
	 * @param oldObj
	 *            object to be replaced
	 * @param newObj
	 *            replacement
	 * @return this node with replaced objects
	 */
	public ExpressionValue replace(ExpressionValue oldObj,
			ExpressionValue newObj) {
		return traverse(Replacer.getReplacer(oldObj, newObj));
	}

	/**
	 * @param fv
	 *            parameter of function, eg 'x' in f(x)=x^2
	 * @return new GeoFunction
	 */
	public GeoFunction buildFunction(FunctionVariable fv) {
		Function tempFun = new Function(this, fv);
		tempFun.initFunction();
		return new GeoFunction(kernel.getConstruction(), tempFun);
	}

	@Override
	public ExpressionValue unwrap() {
		if (isLeaf()) {
			return getLeft();
		}
		return this;
	}

	@Override
	public ExpressionNode wrap() {
		return this;
	}

	@Override
	public boolean hasCoords() {
		if (isLeaf()) {
			return left != null && left.hasCoords();
		}
		return getLeft().hasCoords() || getRight().hasCoords()
				|| operation == Operation.VEC_FUNCTION;
	}

	@Override
	public ExpressionNode derivative(FunctionVariable fv, Kernel kernel0) {
		return Derivative.get(left, right, operation, fv, kernel0);
	}

	private static ExpressionNode wrap(ExpressionValue exp) {
		return exp.wrap();
	}

	@Override
	public ExpressionNode integral(FunctionVariable fv, Kernel kernel0) {

		// symbolic integrals disabled in exam mode

		switch (operation) {

		case XCOORD:
		case YCOORD:
		case ZCOORD:
			return new ExpressionNode(kernel0, this, Operation.MULTIPLY, fv);

		case POWER:
			// eg x^2
			if (left == fv && !right.contains(fv)) {
				double index = right.evaluateDouble();
				if (!Double.isNaN(index) && !Double.isInfinite(index)) {

					if (DoubleUtil.isZero(index + 1)) {
						return new ExpressionNode(kernel0, left, Operation.LOG,
								null);
					}
					return wrap(left).power(index + 1).divide(index + 1);
				}
			} else if (!left.contains(fv)) {

				// eg 2^x
				if (right == fv) {
					double base = left.evaluateDouble();
					if (!Double.isNaN(base) && !Double.isInfinite(base)) {

						// 1^x
						if (DoubleUtil.isEqual(base, 1)) {
							return wrap(fv);
						}

						if (DoubleUtil.isGreater(base, 0)) {
							return this.divide(wrap(left).ln());
						}
					}
				}

			} else if (right.isNumberValue() && !right.contains(fv)) {

				double index = right.evaluateDouble();
				if (!Double.isNaN(index) && !Double.isInfinite(index)) {

					double coeff = getLinearCoefficient(fv, left);
					if (!Double.isNaN(coeff)) {

						// (exp)^-1 -> ln(abs(exp))
						if (DoubleUtil.isEqual(index, -1)) {
							return wrap(left).abs().ln().divide(coeff);
						}
						return wrap(left).power(index + 1)
								.divide(coeff * ((index + 1)));
					}

					coeff = getLinearCoefficientDiv(fv, left);

					if (!Double.isNaN(coeff)) {
						if (DoubleUtil.isEqual(index, -1)) {
							// (exp)^-1 -> ln(abs(exp))
							return wrap(left).abs().ln().multiply(coeff);
						}
						return wrap(left).power(index + 1)
								.multiply(coeff / ((index + 1)));
					}
				}

			}

			break;

		case NO_OPERATION:
			return wrap(left.integral(fv, kernel0));
		case DIVIDE:
			if (right.isNumberValue() && !right.contains(fv)) {
				return wrap(left.integral(fv, kernel0)).divide(right);
			}

			if (left.isNumberValue() && !left.contains(fv) && right == fv) {
				// eg 4/x
				return new ExpressionNode(kernel0, fv, Operation.LOG, null)
						.multiply(left);
			}
			break;

		case MULTIPLY:
			if (right.isNumberValue() && !right.contains(fv)) {
				return wrap(left.integral(fv, kernel0)).multiplyR(right);
			} else if (left.isNumberValue() && !left.contains(fv)) {
				return wrap(right.integral(fv, kernel0)).multiplyR(left);
			}

			// can't do by parts without simplification (use Polynomial?)
			break;

		case PLUS:
			return wrap(left.integral(fv, kernel0))
					.plus(right.integral(fv, kernel0));
		case MINUS:
			return wrap(left.integral(fv, kernel0))
					.subtract(right.integral(fv, kernel0));
		case SIN:
			return linearIntegral(-1, Operation.COS, fv);
		case COS:
			return linearIntegral(1, Operation.SIN, fv);
		case TAN:
			double coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).sec().abs().ln().divide(coeff);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).sec().abs().ln().multiply(coeff);
			}

			break;

		case SEC:
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).sec().plus(wrap(left).tan()).abs().ln()
						.divide(coeff);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).sec().plus(wrap(left).tan()).abs().ln()
						.multiply(coeff);
			}

			break;
		case CSC:
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).cosec().plus(wrap(left).cot()).abs().ln()
						.divide(-coeff);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).cosec().plus(wrap(left).cot()).abs().ln()
						.multiply(-coeff);
			}

			break;
		case COT:
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).sin().abs().ln().divide(coeff);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).sin().abs().ln().multiply(coeff);
			}

			break;
		case SINH:
			return linearIntegral(1, Operation.COSH, fv);
		case COSH:
			return linearIntegral(1, Operation.SINH, fv);
		case TANH:
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).cosh().abs().ln().divide(coeff);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).cosh().abs().ln().multiply(coeff);
			}

			break;
		case SECH:
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).exp().atan().divide(coeff / 2);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).exp().atan().multiply(2 * coeff);
			}

			break;
		case CSCH:
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).cosech().plus(wrap(left).coth()).abs().ln()
						.divide(-coeff);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).cosech().plus(wrap(left).coth()).abs().ln()
						.multiply(-coeff);
			}

			break;
		case COTH:
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).sinh().abs().ln().divide(coeff);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).sinh().abs().ln().multiply(coeff);
			}

			break;

		case EXP:
			return linearIntegral(1, Operation.EXP, fv);

		case ARCSIN:
		case ARCSIND:
		case ARCCOS:
		case ARCTAN:

		case ASINH:
		case ACOSH:
		case ATANH:
		case ABS:
		case SGN:

		case SI:
		case CI:
		case EI:
		case ERF:
		case PSI:
		case POLYGAMMA:
		case LAMBERTW:
		case LOGB:

			break;

		case IF_ELSE:
			MyNumberPair np = (MyNumberPair) left;

			np = new MyNumberPair(kernel0, np.x, np.y.integral(fv, kernel0));

			return new ExpressionNode(kernel0, np, Operation.IF_ELSE,
					right.integral(fv, kernel0));

		case IF:
		case IF_SHORT:
			return new ExpressionNode(kernel0, left, Operation.IF,
					right.integral(fv, kernel0));

		case LOG:
			// base e (ln)
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).ln().multiply(left).subtract(left)
						.divide(coeff);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).ln().multiply(left).subtract(left)
						.multiply(coeff);
			}

			break;

		case LOG10:
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).ln().multiply(left).subtract(left)
						.divide(wrap(10).ln().multiply(coeff));
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).ln().multiply(left).subtract(left)
						.multiply(coeff).divide(wrap(10).ln());
			}

			break;

		case LOG2:
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).ln().multiply(left).subtract(left)
						.divide(wrap(2).ln().multiply(coeff));
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).ln().multiply(left).subtract(left)
						.multiply(coeff).divide(wrap(2).ln());
			}

			break;

		case NROOT:
			if (right.isNumberValue() && !right.contains(fv)) {
				coeff = getLinearCoefficient(fv, left);

				if (!Double.isNaN(coeff)) {
					return wrap(left).nroot(right).multiply(left)
							.multiply(right)
							.divide((right.evaluateDouble() + 1) * coeff);
				}

				coeff = getLinearCoefficientDiv(fv, left);

				if (!Double.isNaN(coeff)) {
					return wrap(left).nroot(right).multiply(left)
							.multiply(right)
							.divide((right.evaluateDouble() + 1) / coeff);
				}
			}

			break;

		case SQRT:
		case SQRT_SHORT:
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).sqrt().multiply(left).divide(coeff * 3d / 2d);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).sqrt().multiply(left)
						.multiply(coeff * 2d / 3d);
			}

			break;
		case CBRT:
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).cbrt().multiply(left).divide(coeff * 4d / 3d);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).cbrt().multiply(left)
						.multiply(coeff * 3d / 4d);
			}

			break;
		default:
			break;

		}

		Log.warn("unhandled operation in integral() (no CAS version): "
				+ operation.toString());

		// undefined
		return wrap(Double.NaN);
	}

	/**
	 * @param n
	 *            n
	 * @return nth root of this
	 */
	public ExpressionNode nroot(ExpressionValue n) {
		return new ExpressionNode(kernel, this, Operation.NROOT, n);
	}

	private ExpressionNode linearIntegral(int i, Operation op,
			FunctionVariable fv) {
		ExpressionValue arg = left.unwrap();
		if (arg == fv) {
			return new ExpressionNode(kernel, arg, op, null).multiplyR(i);
		}

		double coeff = getLinearCoefficient(fv, arg);

		if (!Double.isNaN(coeff)) {
			return new ExpressionNode(kernel, arg, op, null).multiplyR(i)
					.divide(coeff);
		}

		coeff = getLinearCoefficientDiv(fv, arg);

		if (!Double.isNaN(coeff)) {
			return new ExpressionNode(kernel, arg, op, null).multiply(coeff)
					.multiplyR(i);
		}

		Log.warn("not linear integral");
		return wrap(Double.NaN);
	}

	/**
	 * get coefficient from simple linear expression with coefficient as divide
	 * eg 3 * x + 1
	 * 
	 * returns Double.NaN if it's not in the correct form
	 */
	private static double getLinearCoefficient(FunctionVariable fv,
			ExpressionValue ev2) {

		// just x
		if (ev2 == fv) {
			return 1;
		}

		ExpressionValue ev = ev2;
		double factor = 1;
		Operation op;

		// 3x+1 or 1+3x or 3x-1 or 1-3x
		if (ev.isExpressionNode() && (op = ((ExpressionNode) ev).getOperation())
				.isPlusorMinus()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.left.isNumberValue() && !en.left.contains(fv)) {
				// strip off the "+1" etc
				ev = en.right;
				factor = op.equals(Operation.PLUS) ? 1 : -1;
			} else if (en.right.isNumberValue() && !en.right.contains(fv)) {
				// strip off the "+1" etc
				ev = en.left;
				factor = 1;
			}
		}

		// x+2 or 2-x
		if (ev == fv) {
			return factor;
		}

		// 3*x or x*3
		if (ev.isExpressionNode() && ((ExpressionNode) ev).getOperation()
				.equals(Operation.MULTIPLY)) {
			ExpressionNode en = (ExpressionNode) ev;
			if (en.left == fv && en.right.isNumberValue()
					&& !en.right.contains(fv)) {
				return en.right.evaluateDouble() * factor;
				// return wrap(en.right).multiply(factor);
			} else if (en.right == fv && en.left.isNumberValue()
					&& !en.left.contains(fv)) {
				return en.left.evaluateDouble() * factor;
				// return wrap(en.left).multiply(factor);
			}
		}

		// not (simple) linear
		return Double.NaN;
	}

	/**
	 * get coefficient from simple linear expression with coefficient as divide
	 * eg x / 3 + 1
	 * 
	 * returns Double.NaN if it's not in the correct form
	 */
	private static double getLinearCoefficientDiv(FunctionVariable fv,
			ExpressionValue ev2) {

		ExpressionValue ev = ev2;
		double factor = 1;
		Operation op;

		// x/3+1 or 1+x/3 or x/3-1 or 1-x/3
		if (ev.isExpressionNode() && (op = ((ExpressionNode) ev).getOperation())
				.isPlusorMinus()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.left.isNumberValue() && !en.left.contains(fv)) {
				// strip off the "+1" etc
				ev = en.right;
				factor = op.equals(Operation.PLUS) ? 1 : -1;
			} else if (en.right.isNumberValue() && !en.right.contains(fv)) {
				// strip off the "+1" etc
				ev = en.left;
				factor = 1;
			}
		}

		// x/3
		if (ev.isExpressionNode() && ((ExpressionNode) ev).getOperation()
				.equals(Operation.DIVIDE)) {
			ExpressionNode en = (ExpressionNode) ev;
			if (en.left == fv && !en.right.contains(fv)) {
				return en.right.evaluateDouble() * factor;
			}
		}

		// not (simple) linear
		return Double.NaN;
	}

	private ExpressionNode wrap(double n) {
		return new MyDouble(kernel, n).wrap();
	}

	/**
	 * @return whether the top-level operation is IF / IF_ELSE / IF_LIST
	 */
	public boolean isConditional() {
		return operation == Operation.IF || operation == Operation.IF_SHORT
				|| operation == Operation.IF_ELSE
				|| operation == Operation.IF_LIST;
	}

	/**
	 * @return whether this contains any conditional operations
	 */
	public boolean isConditionalDeep() {
		return isConditional()
				|| (left instanceof ExpressionNode
						&& ((ExpressionNode) left).isConditionalDeep())
				|| (right instanceof ExpressionNode
						&& ((ExpressionNode) right).isConditionalDeep());
	}

	/**
	 * Builds an if-else expression based on this condition
	 * 
	 * @param ifBranch
	 *            if branch
	 * @param elseBranch
	 *            else branch
	 * @return if-else expression
	 */
	public ExpressionNode ifElse(ExpressionValue ifBranch,
			ExpressionValue elseBranch) {
		return new ExpressionNode(kernel,
				new MyNumberPair(kernel, this, ifBranch), Operation.IF_ELSE,
				elseBranch);
	}

	@Override
	public double evaluateDouble() {
		if (isLeaf()) {
			return left.evaluateDouble();
		}
		switch (operation) {
		case PLUS:
			return left.evaluateDouble() + right.evaluateDouble();
		case MINUS:
			return left.evaluateDouble() - right.evaluateDouble();
		case MULTIPLY:
			return evaluateMultiplyDouble();
		case DIVIDE:
			return left.evaluateDouble() / right.evaluateDouble();
		case POWER:
			return evaluatePowerDouble();
		case SIN:
			return Math.sin(left.evaluateDouble());
		case COS:
			return Math.cos(left.evaluateDouble());
		case SQRT:
			return Math.sqrt(left.evaluateDouble());
		default:
			break;
		}
		// TODO: evaluate basic operations here, but make sure errors are thrown
		// when necessary
		return super.evaluateDouble();
	}

	private double evaluateMultiplyDouble() {
		double lt = left.evaluateDouble();
		if (!Double.isNaN(lt)) {
			lt *= right.evaluateDouble();
		}
		return !Double.isNaN(lt) ? lt : super.evaluateDouble();
	}

	private double evaluatePowerDouble() {
		if (!left.evaluatesToNumber(false)) {
			return super.evaluateDouble();
		}
		double lt = left.evaluateDouble();
		if (lt < 0 && right.isExpressionNode()) {
			Double negPower = right.wrap().calculateNegPower(lt);
			if (negPower != null) {
				return negPower;
			}
		}
		return Math.pow(left.evaluateDouble(), right.evaluateDouble());
	}

	/**
	 * @param base
	 *            base of power term
	 * @return negPower if exponent is negative fraction
	 */
	public Double calculateNegPower(double base) {
		if (isOperation(Operation.DIVIDE)) {
			return ExpressionNodeEvaluator.negPower(base, this);
		} else if (getOperation() == Operation.MULTIPLY
				&& getLeft() instanceof MinusOne
				&& getRight().isOperation(Operation.DIVIDE)) {
			return 1.0 / ExpressionNodeEvaluator.negPower(base, getRight());
		}
		return null;
	}

	/**
	 * Replaces some CAS commands unknown to GeoGebra This may change the
	 * structure of the ExpressionNode
	 * 
	 * @return the expression node with replaced commands
	 */
	public ExpressionNode replaceCasCommands() {
		return this.traverse(Traversing.CASCommandReplacer.replacer).wrap();
	}

	/**
	 * @return whether this expression was wrapped in brackets when parsed
	 */
	public boolean hasBrackets() {
		return brackets;
	}

	/**
	 * Set the brackets flag, should be called from parser
	 * 
	 * @param brackets
	 *            whether this expression was wrapped in brackets when parsed
	 */
	public void setBrackets(boolean brackets) {
		this.brackets = brackets;
	}

	/**
	 * Apply given unary operation on this node
	 * 
	 * @param operation2
	 *            operation
	 * @return expression node operation2(this)
	 */
	public ExpressionNode apply(Operation operation2) {
		return new ExpressionNode(kernel, this, operation2, null);
	}

	/**
	 * Apply given binary operation on this node and additional argument
	 * 
	 * @param operation2
	 *            operation
	 * @param arg
	 *            second argument
	 * @return expression node opertion2this,arg)
	 */
	public ExpressionNode apply(Operation operation2, ExpressionValue arg) {
		return new ExpressionNode(kernel, this, operation2, arg);
	}

	/**
	 * @param name
	 *            variable name
	 * @return whether this contains FVar that is not part of equation or list
	 */
	public boolean containsFreeFunctionVariable(String name) {
		return checkForFreeVars(left, name)
				|| (right != null && checkForFreeVars(right, name))
				|| ((operation == Operation.FUNCTION_NVAR
						|| operation == Operation.ELEMENT_OF)
						&& right instanceof MyList && ((ValidExpression) right)
								.containsFunctionVariable(name));
	}

	/**
	 * @param vars
	 *            forbidden vars
	 * @return whether one of the forbidden vars appers in expression node
	 */
	public boolean containsFreeFunctionVariableOtherThan(
			FunctionVariable[] vars) {
		return checkForFreeVars(left, vars)
				|| (right != null && checkForFreeVars(right, vars))
				|| ((operation == Operation.FUNCTION_NVAR
						|| operation == Operation.ELEMENT_OF)
						&& right instanceof MyList && ((ValidExpression) right)
								.containsFunctionVariableOtherThan(vars));
	}

	private boolean checkForFreeVars(ExpressionValue ev,
			FunctionVariable[] vars) {
		if (ev instanceof FunctionVariable) {
			return doesNotInclude(vars, ev);

		}
		if (ev instanceof ExpressionNode) {
			return ((ExpressionNode) ev)
					.containsFreeFunctionVariableOtherThan(vars);
		}
		if (ev instanceof MyVecNode) {
			return checkForFreeVars(((MyVecNode) ev).getX(), vars)
					|| checkForFreeVars(((MyVecNode) ev).getY(), vars);
		}
		return false;
	}

	/**
	 * @param vars
	 *            haystack
	 * @param ev
	 *            needle (probably function variable, but not necessary to
	 *            check)
	 * @return whther the array does NOT contain the variable
	 */
	public static boolean doesNotInclude(FunctionVariable[] vars,
			ExpressionValue ev) {
		for (int i = 0; i < vars.length; i++) {
			if (vars[i] == ev) {
				return false;
			}
		}
		return true;
	}

	private boolean checkForFreeVars(ExpressionValue ev, String name) {
		if (ev instanceof FunctionVariable) {
			return name == null
					|| name.equals(((FunctionVariable) ev).getSetVarString());
		}
		if (ev instanceof ExpressionNode) {
			return ((ExpressionNode) ev).containsFreeFunctionVariable(name);
		}
		if (ev instanceof MyVecNode) {
			return checkForFreeVars(((MyVecNode) ev).getX(), name)
					|| checkForFreeVars(((MyVecNode) ev).getY(), name);
		}
		if (ev instanceof MyVec3DNode) {
			return checkForFreeVars(((MyVec3DNode) ev).getX(), name)
					|| checkForFreeVars(((MyVec3DNode) ev).getY(), name);
		}
		return false;
	}

	/**
	 * @return variables that must be defined in order for the result to be
	 *         defined eg. d+If[a>0,b,c] has unconditional variable d
	 * 
	 * 
	 */
	public HashSet<GeoElement> getUnconditionalVars() {
		// TODO Auto-generated method stub
		if (!this.isConditionalDeep()) {
			return null;
		}
		if (leaf) {
			return left.getVariables(SymbolicMode.NONE);
		}
		if (isConditional()) {
			return new HashSet<>();
		}
		HashSet<GeoElement> leftVars = left
				.getVariables(SymbolicMode.NONE);
		HashSet<GeoElement> rightVars = right
				.getVariables(SymbolicMode.NONE);
		if (leftVars == null) {
			return rightVars;
		} else if (rightVars == null) {
			return leftVars;
		} else {
			leftVars.addAll(rightVars);
			return leftVars;
		}
	}

	/**
	 * @param parts
	 *            output parameter
	 * @param expandPlus
	 *            whether to expand a/b+c/d to (ad+bc)/bd
	 */
	public void getFraction(ExpressionValue[] parts, boolean expandPlus) {
		if (this.resolve != null && this.resolve.isExpressionNode()) {
			this.resolve.wrap().getFraction(parts, expandPlus);
			return;
		}
		Fractions.getFraction(parts, this, expandPlus);
	}

	/**
	 * @return true if the ExpressionNode is a GeoSegment on even power
	 */
	public boolean isSegmentSquare() {
		if (this.getLeft() instanceof GeoSegment
				&& this.getOperation() == Operation.POWER
				&& this.getRight() instanceof NumberValue) {
			double d = this.getRight().evaluateDouble();
			if (DoubleUtil.isInteger(d) && d % 2 == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * if expressionNode is GeoSegment
	 * 
	 * @return geoSegment leaf
	 */
	public GeoSegment getGeoSegment() {
		if (this.isGeoElement() && this.left instanceof GeoSegment) {
			return (GeoSegment) this.left;
		}
		return null;
	}

	@Override
	public ValueType getValueType() {
		if (resolve == null) {

			resolve = computeResolve();
		}
		return resolve.getValueType();
	}

	@Override
	public int getListDepth() {
		if (resolve == null) {
			resolve = computeResolve();
		}
		return resolve.getListDepth();
	}

	/**
	 * @return whether this expression has trigonometric operations
	 */
	public boolean has2piPeriodicOperations() {
		return this.inspect(new Inspecting() {

			@Override
			public boolean check(ExpressionValue v) {
				return v.isExpressionNode() && is2piPeriodicOperation(
						((ExpressionNode) v).getOperation());
			}

			private boolean is2piPeriodicOperation(Operation op) {
				return op == Operation.SIN || op == Operation.COS
						|| op == Operation.TAN || op == Operation.COT
						|| op == Operation.SEC || op == Operation.CSC;

			}
		});
	}

	/**
	 * Wraps in unary minus node, for constant MyDouble return just MyDouble
	 * 
	 * @param kernel2
	 *            kernel for result
	 * @param f
	 *            value
	 * @return -f
	 */
	public static ExpressionValue unaryMinus(Kernel kernel2,
			ExpressionValue f) {
		if (f instanceof MyDouble && f.isConstant()
				&& !(f instanceof MyDoubleDegreesMinutesSeconds)) {
			return ((MyDouble) f).unaryMinus(kernel2);
		}
		return new ExpressionNode(kernel2, new MinusOne(kernel2), Operation.MULTIPLY, f);
	}

	/**
	 * @return faster but less accurate version of evaluatesToString: detects
	 *         only a+b+c as TextValue if one of a,b,c is TextValue
	 */
	public boolean isStringAddition() {
		if (getOperation() != Operation.PLUS) {
			return false;
		}
		if (left instanceof TextValue || right instanceof TextValue) {
			return true;
		}
		if (left instanceof ExpressionNode
				&& ((ExpressionNode) left).isStringAddition()) {
			return true;
		}
		if (right instanceof ExpressionNode
				&& ((ExpressionNode) right).isStringAddition()) {
			return true;
		}
		return false;
	}

	/**
	 * GGB-605 set when expression shouldn't be displayed to the user eg
	 * NDerivative
	 * 
	 * @param algo
	 *            secret algo
	 * 
	 * @return this
	 */
	public ExpressionNode setSecret(AlgoElement algo) {
		this.secretMaskingAlgo = algo;
		return this;
	}

	/**
	 * GGB-605
	 * 
	 * @return true if expression shouldn't be displayed to the user
	 */
	public boolean isSecret() {
		return secretMaskingAlgo != null;
	}

	// collect factors of expression recursively
	private void collectFactorCopies(ArrayList<ExpressionNode> factorCopies) {
		ArrayList<ExpressionValue> factors = new ArrayList<>();
		collectFactors(factors);
		for (ExpressionValue factor: factors) {
			factorCopies.add(factor.deepCopy(kernel).wrap());
		}
	}

	private void collectFactors(ArrayList<ExpressionValue> factors) {
		if (!getOperation().equals(Operation.MULTIPLY)) {
			factors.add(this);
			return;
		}

		if (left instanceof ExpressionNode) {
			((ExpressionNode) left).collectFactors(factors);
		} else if (left != null) {
			factors.add(left);
		}

		if (right instanceof ExpressionNode) {
			((ExpressionNode) right).collectFactors(factors);
		} else if (right != null) {
			factors.add(right);
		}
	}

	/**
	 * @return list of factors without power
	 */
	public ArrayList<ExpressionNode> getFactorsWithoutPow() {
		ArrayList<ExpressionNode> factors = new ArrayList<>();
		collectFactorCopies(factors);
		ArrayList<ExpressionNode> factorsWithoutPow = new ArrayList<>(
				factors.size());
		if (!factors.isEmpty()) {
			for (int i = 0; i < factors.size(); i++) {
				if (factors.get(i).getOperation().equals(Operation.POWER)
						&& (factors.get(i).getRight() instanceof MyDouble
								|| factors.get(i)
										.getRight() instanceof MySpecialDouble)) {
					factorsWithoutPow.add(factors.get(i).getLeftTree());
				} else {
					factorsWithoutPow.add(factors.get(i));
				}
			}
		}
		return factorsWithoutPow;
	}

	/**
	 * @param tpl
	 *            template
	 * @return this as fraction
	 */
	public String toFractionString(StringTemplate tpl) {
		initFraction();
		return ((ExpressionNode) resolve).toFractionStringFlat(tpl,
				kernel.getLocalization());
	}

	/**
	 * @return Whether this is a fraction (also true for 1/2+1/3)
	 */
	public boolean isFraction() {
		initFraction();
		return resolve.isOperation(Operation.DIVIDE);
	}
	
	/**
	 * @return whether this is a fraction not containing pi
	 */
	public boolean isFractionNoPi() {
		return isFraction() && !resolve.inspect(Inspecting.SpecialDouble.INSTANCE);
	}

	private void initFraction() {
		if (resolve == null || !resolve.isExpressionNode()) {
			resolve = Fractions.getResolution(this, kernel, true);
		}
	}

	/**
	 * @return simplified fraction if this is one; null otherwise
	 */
	public ExpressionNode asFraction() {
		initFraction();
		if (resolve.isExpressionNode()) {
			return resolve.wrap();
		}
		return null;
	}

	private String toFractionStringFlat(StringTemplate tpl, Localization locale) {
		if (operation == Operation.MULTIPLY && right instanceof MyDouble
				&& MyDouble.exactEqual(right.evaluateDouble(), Math.PI)) {
			return tpl.multiplyString(left, right,
					kernel.format(Math.round(left.evaluateDouble()), tpl),
					right.toValueString(tpl), true, locale);
		}
		if (operation == Operation.DIVIDE) {
			String leftS = left.isExpressionNode()
					? left.wrap().toFractionStringFlat(tpl, locale)
					: left.toValueString(tpl);
			if (leftS.startsWith("-")) {
				return "-" + tpl.divideString(left, right, leftS.substring(1),
						right.toValueString(tpl), true, locale);
			}
			return tpl.divideString(left, right, leftS,
					right.toValueString(tpl), true, locale);
		}

		return toValueString(tpl);
	}

	/**
	 * @return true if it evals to true, false if it's false or invalid
	 */
	public boolean evaluateBoolean() {
		ExpressionValue ev = evaluate(StringTemplate.defaultTemplate);
		return ev instanceof BooleanValue && ((BooleanValue) ev).getBoolean();
	}

	/**
	 * @param valueForm
	 *            whether numerics are written as value or variable name
	 * @return whether this is a product of 1 or more elements, the last of
	 *         which is a number: eg 2 or x*2
	 */
	public boolean endsInNumber(boolean valueForm) {
		if (operation == Operation.NO_OPERATION
				&& getLeft() instanceof NumberValue) {
			return (getLeft() instanceof MyDouble
					&& ((MyDouble) getLeft()).isDigits())
					|| (valueForm && getLeft().isGeoElement()
							&& !((GeoElement) getLeft()).isLabelSet());
		}

		if (operation == Operation.MULTIPLY) {
			return getRight().wrap().endsInNumber(valueForm);
		}

		return false;
	}

	/**
	 * Check whether denominator and numerator are both independent integers
	 * 
	 * @return whether is a simple fraction like 7/2 or (-1)/2 or -(1/2)
	 */
	public boolean isSimpleFraction() {
		ExpressionValue unsigned = getUnsigned(this);
		return unsigned.isExpressionNode() && ((ExpressionNode) unsigned).isUnsignedFraction();
	}

	private boolean isUnsignedFraction() {
		if (operation == Operation.DIVIDE) {
			ExpressionValue leftUnsigned = getUnsigned(left);
			if (leftUnsigned instanceof MyDouble
					&& right.unwrap() instanceof MyDouble) {
				double lt = left.evaluateDouble();
				double rt = right.evaluateDouble();
				return DoubleUtil.isInteger(lt) && DoubleUtil.isInteger(rt);
			}
		}
		return false;
	}

	private ExpressionValue getUnsigned(ExpressionValue expr) {
		if (expr.isOperation(Operation.MULTIPLY)
				&& ExpressionNode.isConstantDouble(expr.wrap().getLeft(),
				-1)) {
			return expr.wrap().getRight();
		}
		return expr;
	}

	/**
	 * Reset resolved value.
	 */
	public void reset() {
		resolve = null;
	}

	/**
	 * @return true if the expression is just a number (or degree)
	 */
	public boolean isSimpleNumber() {
		if (getOperation() == Operation.MULTIPLY) {
			return hasSimpleNumbers();
		}
		ExpressionValue unwrap = unwrap();
		if (unwrap instanceof ExpressionNode) {
			return false;
		}
		if (unwrap instanceof MyDoubleDegreesMinutesSeconds) {
			return false;
		}
		if ((unwrap instanceof MyDouble && !(unwrap instanceof FunctionVariable))
				|| (unwrap instanceof GeoNumeric && !(unwrap instanceof GeoDummyVariable))) {
			double val = evaluateDouble();
			return MyDouble.isFinite(val) && !DoubleUtil.isEqual(val, Math.PI)
					&& !DoubleUtil.isEqual(val, Math.E);
		}
		return false;
	}

	private boolean hasSimpleNumbers() {
		return areLeftAndRightNumbers() && isRightDeg() && !isRightPiOrE();
	}

	private boolean isRightPiOrE() {
		if (getRight() == null) {
			return false;
		}

		double value = getRight().evaluateDouble();
		return DoubleUtil.isEqual(value, Math.PI) || DoubleUtil.isEqual(value, Math.E);
	}

	private boolean areLeftAndRightNumbers() {
		return getLeft().unwrap() instanceof NumberValue && getRight().unwrap() instanceof MyDouble;
	}

	private boolean isRightDeg() {
		return MyDouble.exactEqual(getRight().evaluateDouble(), MyMath.DEG);
	}

	public void setForceSurfaceCartesian() {
		this.forceSurface = true;
	}

	public boolean isForceSurface() {
		return forceSurface;
	}

	/**
	 * Copy all attributes except for those set in constructor and the leaf flag
	 *
	 * @param newNode node that should receive the attributes
	 */
	public void copyAttributesTo(ExpressionNode newNode) {
		newNode.forceVector = forceVector;
		newNode.forcePoint = forcePoint;
		newNode.forceFunction = forceFunction;
		newNode.forceInequality = forceInequality;
		newNode.forceSurface = forceSurface;
		newNode.brackets = brackets;
		newNode.secretMaskingAlgo = secretMaskingAlgo;
		newNode.holdsLaTeXtext = holdsLaTeXtext;
	}

	@Override
	public boolean isOperation(Operation operation) {
		return operation == this.operation;
	}
}
