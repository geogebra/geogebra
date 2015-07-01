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

import org.geogebra.common.export.MathmlTemplate;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoFractionText;
import org.geogebra.common.kernel.arithmetic.Traversing.Replacer;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;

/**
 * Tree node for expressions like "3*a - b/5"
 * 
 * @author Markus
 */
public class ExpressionNode extends ValidExpression implements
		ExpressionNodeConstants, ReplaceChildrenByValues {

	private Localization loc;
	private Kernel kernel;
	private ExpressionValue left, right;
	private Operation operation = Operation.NO_OPERATION;
	private boolean forceVector = false, forcePoint = false,
			forceFunction = false;
	/** true if this holds text and the text is in LaTeX format */
	public boolean holdsLaTeXtext = false;

	/** for leaf mode */
	public boolean leaf = false;
	private boolean brackets;

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
	 * */
	public ExpressionNode(Kernel kernel, ExpressionValue left,
			Operation operation, ExpressionValue right) {

		this.kernel = kernel;
		loc = kernel.getLocalization();

		this.operation = operation;
		setLeft(left);
		if (right != null) {
			setRight(right);
		} else { // set dummy value
			setRight(new MyDouble(kernel, Double.NaN));
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

	public ExpressionValue deepCopy(Kernel kernel1) {
		return getCopy(kernel1);
	}

	/**
	 * copy the whole tree structure except leafs
	 * 
	 * @param kernel1
	 *            kernel
	 * @return copy of this node
	 * */
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
			newNode.leaf = leaf;
		} else {
			// something went wrong
			return null;
		}

		// set member vars that are not set by constructors
		newNode.forceVector = forceVector;
		newNode.forcePoint = forcePoint;
		newNode.forceFunction = forceFunction;
		// Application.debug("getCopy() output: " + newNode);
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

		ExpressionValue ret = null;
		// Application.debug("copy ExpressionValue input: " + ev);
		if (ev.isExpressionNode()) {
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
	final private void simplifyAndEvalCommands() {
		// don't evaluate any commands for the CAS here
		if (kernel.isResolveUnkownVarsAsDummyGeos()) {
			return;
		}

		if (left.isExpressionNode()) {
			((ExpressionNode) left).simplifyAndEvalCommands();
		} else if (left instanceof Command) {
			left = ((Command) left).evaluate(StringTemplate.defaultTemplate);
		}

		if (right != null) {
			if (right.isExpressionNode()) {
				((ExpressionNode) right).simplifyAndEvalCommands();
			} else if (right instanceof Command) {
				right = ((Command) right)
						.evaluate(StringTemplate.defaultTemplate);
			}
		}
	}

	/**
	 * Replaces all constant parts in tree by their values
	 */
	final public void simplifyConstantIntegers() {
		if (left.isExpressionNode()) {
			ExpressionNode node = (ExpressionNode) left;
			if (left.isConstant()) {
				ExpressionValue eval = node
						.evaluate(StringTemplate.defaultTemplate);
				if (eval instanceof NumberValue) {
					// we only simplify numbers that have integer values
					if (Kernel.isInteger(((NumberValue) eval).getDouble())) {
						left = eval;
					}
				} else {
					left = eval;
				}
			} else {
				node.simplifyConstantIntegers();
			}
		}

		if ((right != null) && right.isExpressionNode()) {
			ExpressionNode node = (ExpressionNode) right;
			if (right.isConstant()) {
				ExpressionValue eval = node
						.evaluate(StringTemplate.defaultTemplate);
				if (eval instanceof NumberValue) {
					// we only simplify numbers that have integer values
					if (Kernel.isInteger(((NumberValue) eval).getDouble())) {
						right = eval;
					}
				} else {
					right = eval;
				}
			} else {
				node.simplifyConstantIntegers();
			}
		}
	}

	/*
	 * Markus: no longer needed as we have added rules in MathPiper to support
	 * this notation directly Expands equation expressions like (3*x + 2 = 5) /
	 * 2 to (3*x + 2)/2 = 5/2.
	 */
	// final public ExpressionValue expandEquationExpressions() {
	// if (leaf) return this;
	//
	// if (left.isExpressionNode()) {
	// left = ((ExpressionNode) left).expandEquationExpressions();
	// }
	// if (right.isExpressionNode()) {
	// right = ((ExpressionNode) right).expandEquationExpressions();
	// }
	//
	// switch (operation) {
	// case PLUS:
	// case MINUS:
	// case MULTIPLY:
	// case DIVIDE:
	// // equ <operation> val
	// if (left instanceof Equation) {
	// ((Equation) left).applyOperation(operation, right, false);
	// leaf = true;
	// right = null;
	// }
	// // val <operation> equ
	// else if (right instanceof Equation) {
	// ((Equation) right).applyOperation(operation, left, true);
	// left = right;
	// right = null;
	// leaf = true;
	// }
	// break;
	// }
	//
	// return this;
	// }

	// used for 3D
	/*
	 * protected ExpressionValue evaluate(ExpressionValue v){ return
	 * v.evaluate(); }
	 */

	/**
	 * Evaluates this expression
	 * 
	 * @param tpl
	 *            template (needed for possible string concatenation)
	 * @return value
	 */
	@Override
	public ExpressionValue evaluate(StringTemplate tpl) {
		return kernel.getExpressionNodeEvaluator().evaluate(this, tpl);
	}

	/*
	 * public ExpressionValue evaluate(boolean cache) { return
	 * kernel.getExpressionNodeEvaluator().evaluate(this); }
	 */

	/**
	 * look for Variable objects in the tree and replace them by their resolved
	 * GeoElement
	 */
	public final void resolveVariables() {
		doResolveVariables();
		simplifyAndEvalCommands();
		simplifyLeafs();

		// left instanceof NumberValue needed rather than left.isNumberValue()
		// as left can be an
		// ExpressionNode, eg Normal[0,1,x]
		switch (operation) {
		case POWER: // eg e^x
			if ((left instanceof NumberValue)
					&& (((NumberValue) left).getDouble() == Math.E)) {
				GeoElement geo = kernel.lookupLabel("e");
				if ((geo != null) && geo.needsReplacingInExpressionNode()) {

					// replace e^x with exp(x)
					// if e was autocreated
					operation = Operation.EXP;
					left = right;
					kernel.getConstruction().removeLabel(geo);
				}
			}
			break;
		case MULTIPLY: // eg 1 * e or e * 1
		case DIVIDE: // eg 1 / e or e / 1
		case PLUS: // eg 1 + e or e + 1
		case MINUS: // eg 1 - e or e - 1
			if ((left instanceof NumberValue)
					&& (((NumberValue) left).getDouble() == Math.E)) {
				GeoElement geo = kernel.lookupLabel("e");
				if ((geo != null) && geo.needsReplacingInExpressionNode()) {

					// replace 'e' with exp(1)
					// if e was autocreated
					left = new ExpressionNode(kernel,
							new MyDouble(kernel, 1.0), Operation.EXP, null);
					kernel.getConstruction().removeLabel(geo);
				}
			} else if ((right instanceof NumberValue)
					&& (((NumberValue) right).getDouble() == Math.E)) {
				GeoElement geo = kernel.lookupLabel("e");
				if ((geo != null) && geo.needsReplacingInExpressionNode()) {

					// replace 'e' with exp(1)
					// if e was autocreated
					right = new ExpressionNode(kernel,
							new MyDouble(kernel, 1.0), Operation.EXP, null);
					kernel.getConstruction().removeLabel(geo);
				}
			}
			break;
		default:
			break;
		}
	}

	private void doResolveVariables() {
		// resolve left wing
		if (left.isVariable()) {
			left = ((Variable) left).resolveAsExpressionValue();
		} else {
			left.resolveVariables();
		}

		// resolve right wing
		if (right != null) {
			if (right.isVariable()) {
				right = ((Variable) right).resolveAsExpressionValue();
			} else {
				right.resolveVariables();
			}
		}
	}

	/**
	 * look for GeoFunction objects in the tree and replace them by FUNCTION
	 * ExpressionNodes. This makes operations like f + g possible by changing
	 * this to f(x) + g(x)
	 * 
	 * public void wrapGeoFunctionsAsExpressionNode() { Polynomial polyX = new
	 * Polynomial(kernel, "x");
	 * 
	 * // left wing if (left.isExpressionNode()) {
	 * ((ExpressionNode)left).wrapGeoFunctionsAsExpressionNode(); } else if
	 * (left instanceof GeoFunction) { left = new ExpressionNode(kernel, left,
	 * ExpressionNode.FUNCTION, polyX); }
	 * 
	 * // resolve right wing if (right != null) { if (right.isExpressionNode())
	 * { ((ExpressionNode)right).wrapGeoFunctionsAsExpressionNode(); } else if
	 * (right instanceof GeoFunction) { right = new ExpressionNode(kernel,
	 * right, ExpressionNode.FUNCTION, polyX); } } }
	 */

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
			evalToVector = (((ExpressionNode) left).shouldEvaluateToGeoVector());
		} else if (left.isGeoElement()) {
			GeoElement geo = (GeoElement) left;
			evalToVector = geo.isGeoVector() || geo.isNumberValue();
		} else if (left.isNumberValue()) {
			evalToVector = true;
		}

		if ((right != null) && evalToVector) {
			if (right.isExpressionNode()) {
				evalToVector = ((ExpressionNode) right)
						.shouldEvaluateToGeoVector();
			} else if (right.isGeoElement()) {
				GeoElement geo = (GeoElement) right;
				evalToVector = geo.isGeoVector() || geo.isNumberValue();
			} else if (right.isNumberValue()) {
				evalToVector = true;
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
				replacements += ((ExpressionNode) right).replaceVariables(
						varName, fVar);
			} else if (right instanceof MyList) {
				replacements += ((MyList) right)
						.replaceVariables(varName, fVar);
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
	protected int replaceXYZnodes(FunctionVariable xVar, FunctionVariable yVar,
			FunctionVariable zVar, ArrayList<ExpressionNode> undecided) {
		if ((xVar == null) && ((yVar == null) & (zVar == null))) {
			return 0;
		}

		// left tree
		if (left.isExpressionNode()) {
			((ExpressionNode) left)
					.replaceXYZnodes(xVar, yVar, zVar, undecided);
		}
		// right tree
		if ((right != null) && right.isExpressionNode()) {
			((ExpressionNode) right).replaceXYZnodes(xVar, yVar, zVar,
					undecided);
		}

		switch (operation) {
		case XCOORD:
			if (xVar != null) {
				undecided.add(this);
				operation = Operation.MULTIPLY_OR_FUNCTION;
				right = left;
				left = xVar;
			}
			break;

		case YCOORD:
			if (yVar != null) {
				undecided.add(this);
				operation = Operation.MULTIPLY_OR_FUNCTION;
				right = left;
				left = yVar;
			}
			break;

		case ZCOORD:
			if (zVar != null) {
				undecided.add(this);
				operation = Operation.MULTIPLY_OR_FUNCTION;
				right = left;
				left = zVar;
			}
			break;
		case POWER:
			if (left.isExpressionNode()
					&& ((ExpressionNode) left).operation == Operation.MULTIPLY_OR_FUNCTION
					&& !((ExpressionNode) left).hasBrackets()) {
				right = new ExpressionNode(kernel,
						((ExpressionNode) left).getRight(), Operation.POWER,
						right);
				left = ((ExpressionNode) left).getLeft();
				operation = Operation.MULTIPLY;
			}
			break;
		case FACTORIAL:
			if (left.isExpressionNode()
					&& ((ExpressionNode) left).operation == Operation.MULTIPLY_OR_FUNCTION
					&& !((ExpressionNode) left).hasBrackets()) {
				right = new ExpressionNode(kernel,
						((ExpressionNode) left).getRight(),
						Operation.FACTORIAL, null);
				left = ((ExpressionNode) left).getLeft();
				operation = Operation.MULTIPLY;
			}
		case SQRT_SHORT:
			if (left.isExpressionNode()
					&& ((ExpressionNode) left).operation == Operation.MULTIPLY_OR_FUNCTION
					&& !((ExpressionNode) left).hasBrackets()) {
				right = ((ExpressionNode) left).getRight();
				left = new ExpressionNode(kernel,
						((ExpressionNode) left).getLeft(), Operation.SQRT, null);
				operation = Operation.MULTIPLY;
			}
		default:
			break;
		}

		return undecided.size();
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue ev = t.process(this);
		if (ev != this)
			return ev;
		left = left.traverse(t);
		if (right != null)
			right = right.traverse(t);
		// if we did some replacement in a leaf,
		// we might need to update the leaf flag (#3512)
		return ev.unwrap().wrap();
	}

	@Override
	public boolean inspect(Inspecting t) {
		return t.check(this) || left.inspect(t)
				|| (right != null && right.inspect(t));
	}

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
	 * @return expanded polynomial
	 */
	protected final Polynomial makePolynomialTree(Equation equ) {
		Polynomial lt;
		Polynomial rt = null;

		if (operation == Operation.FUNCTION_NVAR) {
			if ((left instanceof FunctionalNVar) && (right instanceof MyList)) {
				MyList list = ((MyList) right);
				FunctionNVar func = ((FunctionalNVar) left).getFunction();
				ExpressionNode expr = func.getExpression().getCopy(kernel);
				if (func.getFunctionVariables().length == list.size()) {
					for (int i = 0; i < list.size(); i++) {
						ExpressionValue ev = list.getListElement(i);
						if (ev instanceof ExpressionNode) {
							ExpressionNode en = ((ExpressionNode) ev)
									.getCopy(kernel);
							if (!equ.isFunctionDependent()) {
								equ.setFunctionDependent(en
										.containsFunctionVariable());
							}
							// we may only make polynomial trees after
							// replacement
							// en.makePolynomialTree(equ);
							ev = en;
						} else if (list.getListElement(i) instanceof FunctionVariable) {
							equ.setFunctionDependent(true);
						}
						expr = expr.replace(func.getFunctionVariables()[i], ev)
								.wrap();
					}
				} else {
					throw new MyError(loc,
							new String[] { "IllegalArgumentNumber" });
				}

				if (equ.isFunctionDependent()) {
					return expr.makePolynomialTree(equ);
				}
			}
		} else if (operation == Operation.FUNCTION) {
			if (left instanceof GeoFunction) {
				Function func = ((Functional) left).getFunction();

				return makePolyTreeFromFunction(func, equ);
			} else if (left instanceof ExpressionNode
					&& ((ExpressionNode) left).getOperation() == Operation.DERIVATIVE) {
				Function base = ((Functional) ((ExpressionNode) left).getLeft())
						.getFunction();
				int deg = (int) Math.ceil(((ExpressionNode) left).getRight()
						.evaluateDouble());
				for (int i = 0; i < deg; i++) {
					base = new Function(
							((Functional) ((ExpressionNode) left).getLeft())
							.getFunction().derivative(
base.getFunctionVariable(), kernel)
									.wrap(), base.getFunctionVariable());
				}

				return makePolyTreeFromFunction(base, equ);
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
			return new Polynomial(kernel, new Term(new ExpressionNode(kernel,
					left, operation, right), ""));
		}

		// transfer left subtree
		if (left.isExpressionNode()) {
			lt = ((ExpressionNode) left).makePolynomialTree(equ);
		} else if (left instanceof FunctionVariable) {
			lt = new Polynomial(kernel,
					((FunctionVariable) left).getSetVarString());
		} else {
			lt = new Polynomial(kernel, new Term(left, ""));
		}

		// transfer right subtree
		if (right != null) {
			if (right.isExpressionNode()) {
				rt = ((ExpressionNode) right).makePolynomialTree(equ);
			} else if (right instanceof FunctionVariable) {
				rt = new Polynomial(kernel,
						((FunctionVariable) right).getSetVarString());
			} else {
				if (right instanceof MyList) {
					MyList list = (MyList) right;
					for (int i = 0; i < list.size(); i++) {
						ExpressionValue ev = list.getListElement(i);
						if (ev instanceof ExpressionNode) {
							((ExpressionNode) ev).makePolynomialTree(equ);
						}
					}
				}
				// both for f(x,x) and x+3 we don't need the second argument
				// wrapped
				return lt.apply(operation, right, equ);
			}
		}
		return lt.apply(operation, rt, equ);
	}

	private Polynomial makePolyTreeFromFunction(Function func, Equation equ) {
		if (right instanceof ExpressionNode) {
			if (!equ.isFunctionDependent()) {
				equ.setFunctionDependent(((ExpressionNode) right)
						.containsFunctionVariable());
			}
			// we may only make polynomial trees after replacement
			// ((ExpressionNode) right).makePolynomialTree(equ);
		} else if (right instanceof FunctionVariable) {
			equ.setFunctionDependent(true);
		}
		if (equ.isFunctionDependent()) {
			ExpressionNode expr = func.getExpression().getCopy(kernel);
			expr = expr.replace(func.getFunctionVariable(), right)
					.wrap();
			return expr.makePolynomialTree(equ);
		}
		return new Polynomial(kernel, new Term(new ExpressionNode(
kernel, left,
				operation, right), ""));
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
	final public boolean isConstant() {
		if (isLeaf()) {
			return left.isConstant();
		}
		return left.isConstant() && right.isConstant();
	}

	/**
	 * returns true, if no variable is a point (GeoPoint)
	 */
	@Override
	final public boolean evaluatesToNonComplex2DVector() {
		if (operation == Operation.RANDOM || operation == Operation.XCOORD
				|| operation == Operation.YCOORD
				|| operation == Operation.ZCOORD || operation == Operation.ABS
				|| operation == Operation.ARG || operation == Operation.ALT) {
			return false;
		}
		if (isLeaf()) {
			return left.evaluatesToNonComplex2DVector();
		}
		// sin(vector), conjugate(vector), ... are complex numbers
		if (Operation.isSimpleFunction(operation)
				|| operation == Operation.CONJUGATE) {
			return false;
		}
		boolean leftVector = left.evaluatesToNonComplex2DVector();
		boolean rightVector = right.evaluatesToNonComplex2DVector();
		boolean ret = leftVector || rightVector;

		if (leftVector
				&& rightVector
				&& (operation == Operation.MULTIPLY || operation == Operation.VECTORPRODUCT)) {
			ret = false;
		}

		return ret;
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
		boolean ret = leftVector || rightVector;

		if (leftVector
				&& rightVector
				&& (operation == Operation.MULTIPLY || operation == Operation.VECTORPRODUCT)) {
			ret = false;
		}

		return ret;
	}

	@Override
	final public boolean evaluatesTo3DVector() {
		if (operation == Operation.RANDOM || operation == Operation.XCOORD
				|| operation == Operation.YCOORD
				|| operation == Operation.ZCOORD) {
			return false;
		}
		if (isLeaf()) {
			return left.evaluatesTo3DVector();
		}

		boolean leftVector = left.evaluatesTo3DVector();
		boolean rightVector = right.evaluatesTo3DVector();
		boolean ret = leftVector || rightVector;

		if (leftVector && rightVector && operation == Operation.MULTIPLY) {
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
		// this expression should be considered as a point, not a vector
		forceFunction = true;
	}

	/**
	 * @return true iff forced to be a function
	 */
	final public boolean isForcedFunction() {
		return forceFunction;
	}

	/**
	 * Returns whether this tree has any operations
	 * 
	 * @return true iff this tree has any operations
	 */
	final public boolean hasOperations() {
		if (leaf) {
			if (left.isExpressionNode()) {
				((ExpressionNode) left).hasOperations();
			} else {
				return false;
			}
		}

		return (right != null);
	}

	/**
	 * Returns all GeoElement objects in the subtree
	 */
	final public HashSet<GeoElement> getVariables() {
		if (leaf) {
			return left.getVariables();
		}

		HashSet<GeoElement> leftVars = left.getVariables();
		HashSet<GeoElement> rightVars = right.getVariables();
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
	 * @return GeoElement variables
	 */
	final public GeoElement[] getGeoElementVariables() {
		HashSet<GeoElement> varset = getVariables();
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
	 * @return true if this is leaf containing only Variable
	 */
	public boolean isSingleVariable() {
		return (isLeaf() && (left instanceof Variable));
	}

	/**
	 * @return true if this is leaf containing only imaginary unit
	 */
	public boolean isImaginaryUnit() {
		return (isLeaf() && (left instanceof GeoVec2D) && ((GeoVec2D) left)
				.isImaginaryUnit());
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

		try {

			if (leaf) { // leaf is GeoElement or not
				/*
				 * if (symbolic) { if (left.isGeoElement()) ret = ((GeoElement)
				 * left).getLabel(); else if (left.isExpressionNode()) ret =
				 * ((ExpressionNode)left).printJSCLString(symbolic); else ret =
				 * left.toString(); } else { ret = left.toValueString(); }
				 */

				if (symbolic && left.isGeoElement()) {
					ret = ((GeoElement) left).getLabel(tpl);
				} else if (left.isExpressionNode()) {
					ret = ((ExpressionNode) left).getCASstring(tpl, symbolic);
				} else {
					ret = symbolic ? left.toString(tpl) : left
							.toValueString(tpl);
				}
			}

			// STANDARD case: no leaf
			else {
				// expression node

				// we could group the factors of all possible numerators here.
				// we won't do that.
				// http://www.geogebra.org/forum/viewtopic.php?f=22&t=29017
				// numerGroup();

				String leftStr = null, rightStr = null;
				if (symbolic && left.isGeoElement()) {
					leftStr = ((GeoElement) left).getLabel(tpl);
				} else if (left.isExpressionNode()) {
					leftStr = ((ExpressionNode) left).getCASstring(tpl,
							symbolic);
				} else {
					leftStr = symbolic ? left.toString(tpl) : left
							.toValueString(tpl);
				}

				if (right != null) {
					if (symbolic && right.isGeoElement()) {
						rightStr = ((GeoElement) right).getLabel(tpl);
					} else if (right.isExpressionNode()) {
						rightStr = ((ExpressionNode) right).getCASstring(tpl,
								symbolic);
					} else {
						rightStr = symbolic ? right.toString(tpl) : right
								.toValueString(tpl);
					}
				}
				ret = ExpressionNode.operationToString(left, right, operation,
						leftStr, rightStr, !symbolic, tpl, kernel);

			}
		} finally {
			// do nothing
		}

		return ret;
	}

	/**
	 * @return a representation of all classes present in the tree
	 */
	final public String getTreeClass() {
		return getTreeClass("");
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
				&& ev instanceof GeoSegment)
			return false; // don't expand "AreEqual[Segment[X,Y],Segment[Z,W]]"
							// format expressions
		return ((operation.equals(Operation.EQUAL_BOOLEAN)
				|| operation.equals(Operation.DIVIDE)
				|| operation.equals(Operation.MULTIPLY)
				|| operation.equals(Operation.MINUS)
				|| operation.equals(Operation.PLUS) || operation
					.equals(Operation.POWER)) && (ev instanceof GeoSegment
				|| ev instanceof GeoPolygon || ev instanceof GeoNumeric));
	}

	/**
	 * @param prefix
	 * @return a representation of all classes present in the tree
	 */
	final private String getTreeClass(String prefix) {

		String ret = "";

		ret += "-\n";

		if (left != null) {
			ret += prefix + "  \\l:";
			if (left instanceof ExpressionNode) {
				ret += ((ExpressionNode) left).getTreeClass(prefix + "   ");
			} else {
				ret += left.getClass();
			}
			ret += "\n";
		}

		if (right != null) {
			ret += prefix + "  \\r:";
			if (right instanceof ExpressionNode) {
				ret += ((ExpressionNode) right).getTreeClass(prefix + "   ");
			} else {
				ret += right.getClass();
			}
			ret += "\n";
		}

		return ret;

	}

	/**
	 * Returns a string representation of this node.
	 */
	@Override
	final public String toString(StringTemplate tpl) {
		if (leaf) { // leaf is GeoElement or not
			if (left.isGeoElement()) {
				return ((GeoElement) left).getLabel(tpl);
			}
			return left.toString(tpl);
		}

		// expression node
		String leftStr = null, rightStr = null;
		if (left.isGeoElement()) {
			if (tpl.getStringType().equals(StringType.OGP)
					&& expandForOGP(left)) {
				leftStr = ((GeoElement) left).getCommandDescription(tpl);
			} else
				leftStr = ((GeoElement) left).getLabel(tpl);
		} else {
			leftStr = left.toString(tpl);
		}

		if (right != null) {
			if (right.isGeoElement()) {
				if (tpl.getStringType().equals(StringType.OGP)
						&& expandForOGP(right)) {
					rightStr = ((GeoElement) right).getCommandDescription(tpl);
				} else
					rightStr = ((GeoElement) right).getLabel(tpl);
			} else {
				rightStr = right.toString(tpl);
			}
		}
		return ExpressionNode.operationToString(left, right, operation,
				leftStr, rightStr, false, tpl, kernel);
	}

	/** like toString() but with current values of variables */
	@Override
	final public String toValueString(StringTemplate tpl) {
		if (isLeaf()) { // leaf is GeoElement or not
			if (left != null) {
				return left.toValueString(tpl);
			}
		}

		// expression node
		String leftStr = left.toValueString(tpl);

		String rightStr = null;
		if (right != null) {
			rightStr = right.toValueString(tpl);
		}

		return ExpressionNode.operationToString(left, right, operation,
				leftStr, rightStr, true, tpl, kernel);
	}

	final public String toOutputValueString(StringTemplate tpl) {
		if (isLeaf()) { // leaf is GeoElement or not
			if (left != null) {
				return left.toOutputValueString(tpl);
			}
		}

		// expression node
		String leftStr = left.toOutputValueString(tpl);

		String rightStr = null;
		if (right != null) {
			rightStr = right.toOutputValueString(tpl);
		}

		return ExpressionNode.operationToString(left, right, operation,
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
	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		String ret;

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
			rightStr = right.toLaTeXString(symbolic, tpl);
			if (((operation == Operation.FUNCTION_NVAR) || (operation == Operation.ELEMENT_OF))
					&& (right instanceof MyList)) {
				// 1 character will be taken from the left and right
				// of rightStr in operationToString, but more
				// is necessary in case of LaTeX, we do that here
				// " \\{ " is put by MyList 5 - 1(escape) -1(operationToString)
				rightStr = rightStr.substring(7, rightStr.length() - 9);
				rightStr = rightStr.concat("}");
			}
		}

		// build latex string
		ret = ExpressionNode.operationToString(left, right, operation, leftStr,
				rightStr, !symbolic, tpl, kernel);

		return checkMathml(ret, tpl);
	}

	/**
	 * make sure string wrapped in MathML if necessary eg <ci>x</ci>
	 */
	private static String checkMathml(String str, StringTemplate tpl) {
		if (tpl.hasType(StringType.MATHML) && str.charAt(0) != '<') {
			return "<ci>" + str + "</ci>";
		}

		return str;
	}

	/**
	 * Returns a string representation of a node.
	 * 
	 * @param left
	 *            left subtree
	 * @param right
	 *            right subtree
	 * @param operation
	 *            operation
	 * @param leftStr
	 *            serialized left subtree
	 * @param rightStr
	 *            serialized right subtree
	 * @param valueForm
	 *            whether to show value or symbols
	 * @param tpl
	 *            string template
	 * @return string representation of a node.
	 */
	final public static String operationToString(ExpressionValue left,
			ExpressionValue right, Operation operation, String leftStr,
			String rightStr, boolean valueForm, StringTemplate tpl,
			Kernel kernel) {
		ExpressionValue leftEval;
		StringBuilder sb = new StringBuilder();

		StringType stringType = tpl.getStringType();
		Localization loc = kernel.getLocalization();
		switch (operation) {
		case NO_OPERATION:
			return leftStr;
		case NOT:
			return tpl.notString(left, leftStr);

		case OR:
			return tpl.orString(left, right, leftStr, rightStr);
		case AND_INTERVAL:
			return tpl.andIntervalString(left, right, leftStr, rightStr,
					valueForm);

		case AND:
			return tpl.andString(left, right, leftStr, rightStr);

		case IMPLICATION:
			if (stringType.equals(StringType.MATHML)) {
				MathmlTemplate.mathml(sb, "<implies/>", leftStr, rightStr);
			} else {

				tpl.append(sb, leftStr, left, operation);

				sb.append(' ');
				switch (stringType) {
				case LATEX:
					if (tpl.isInsertLineBreaks()) {
						sb.append("\\-");
					}
					sb.append("\\to");
					break;

				case LIBRE_OFFICE:
					sb.append("toward"); // don't know if it is correct TAM
											// 5/28/2012
					break;

				default:
					sb.append(strIMPLIES);
				}
				sb.append(' ');

				tpl.append(sb, rightStr, right, operation);
			}
			break;

		case EQUAL_BOOLEAN:
			if (stringType.equals(StringType.MATHML)) {
				MathmlTemplate.mathml(sb, "<eq/>", leftStr, rightStr);
			} else if (stringType.equals(StringType.OGP)) {
				sb.append("AreEqual[" + leftStr + "," + rightStr + "]");
			} else {

				if (tpl.getStringType().equals(StringType.GIAC)) {
					sb.append("when(ggb\\_is\\_zero(simplify(");
					tpl.append(sb, leftStr, left, operation);
					sb.append("-(");
					tpl.append(sb, rightStr, right, operation);
					sb.append("))),true,false)");
				} else {

					tpl.infixBinary(sb, left, right, operation, leftStr,
							rightStr, tpl, tpl.equalSign());
				}
			}
			break;

		case NOT_EQUAL:
			if (stringType.equals(StringType.MATHML)) {
				MathmlTemplate.mathml(sb, "<neq/>", leftStr, rightStr);
			} else {
				tpl.infixBinary(sb, left, right, operation, leftStr, rightStr,
						tpl, tpl.notEqualSign());
			}
			break;

		case IS_ELEMENT_OF:
			if (stringType.equals(StringType.MATHML)) {
				MathmlTemplate.mathml(sb, "<in/>", leftStr, rightStr);
			} else if (stringType.equals(StringType.GIAC)) {
				sb.append("when(count\\_eq(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(")==0,false,true)");
			} else {
				tpl.append(sb, leftStr, left, operation);
				// sb.append(leftStr);
				sb.append(' ');
				switch (stringType) {
				case LATEX:
					if (tpl.isInsertLineBreaks()) {
						sb.append("\\-");
					}
					sb.append("\\in");
					break;
				case LIBRE_OFFICE:
					sb.append(" in ");
					break;
				default:
					sb.append(strIS_ELEMENT_OF);
				}
				sb.append(' ');
				tpl.append(sb, rightStr, right, operation);
				// sb.append(rightStr);
			}
			break;

		case IS_SUBSET_OF:
			if (stringType.equals(StringType.MATHML)) {
				MathmlTemplate.mathml(sb, "<subset/>", leftStr, rightStr);
			} else if (stringType.equals(StringType.GIAC)) {
				sb.append("when((");
				sb.append(leftStr);
				sb.append(") union (");
				sb.append(rightStr);
				sb.append(")==(");
				sb.append(rightStr);
				// {1,2,3,3} union {} = {1,2,3}
				sb.append(") union {},true,false");
			} else {
				tpl.infixBinary(sb, left, right, operation, leftStr, rightStr,
						tpl, tpl.subsetSign());
			}
			break;

		case IS_SUBSET_OF_STRICT:
			if (stringType.equals(StringType.MATHML)) {
				MathmlTemplate.mathml(sb, "<prsubset/>", leftStr, rightStr);
			} else if (stringType.equals(StringType.GIAC)) {
				sb.append("when((");
				sb.append(leftStr);
				sb.append(") union (");
				sb.append(rightStr);
				sb.append(")==(");
				sb.append(rightStr);
				// {1,2,3,3} union {} = {1,2,3}
				sb.append(") union {} && dim(");
				sb.append(leftStr);
				sb.append("union {})<dim(");
				sb.append(rightStr);
				sb.append("union {}),true,false");
			} else {
				tpl.infixBinary(sb, left, right, operation, leftStr, rightStr,
						tpl, tpl.strictSubsetSign());
			}
			break;

		case SET_DIFFERENCE:
			if (stringType.equals(StringType.MATHML)) {
				MathmlTemplate.mathml(sb, "<setdiff/>", leftStr, rightStr);
			} else if (stringType.equals(StringType.GIAC)) {
				sb.append('(');
				sb.append(leftStr);
				sb.append(" minus ");
				sb.append(rightStr);
				sb.append(')');
			} else {
				tpl.append(sb, leftStr, left, operation);
				// sb.append(leftStr);
				sb.append(' ');
				switch (stringType) {
				case LATEX:
					if (tpl.isInsertLineBreaks()) {
						sb.append("\\-");
					}
					sb.append("\\setminus");
					break;
				case LIBRE_OFFICE:
					sb.append(" setminus ");
					break;
				default:
					sb.append(strSET_DIFFERENCE);
				}
				sb.append(' ');
				if (right.isExpressionNode()
						&& right.wrap().getOperation() == Operation.SET_DIFFERENCE) {
					sb.append(tpl.leftBracket());
					sb.append(rightStr);
					sb.append(tpl.rightBracket());
				} else {
					tpl.append(sb, rightStr, right, operation);
				}
				// sb.append(rightStr);
			}
			break;

		case LESS:
			if (stringType.equals(StringType.MATHML)) {
				MathmlTemplate.mathml(sb, "<lt/>", leftStr, rightStr);
			} else {

				tpl.infixBinary(sb, left, right, operation, leftStr, rightStr,
						tpl, tpl.lessSign());
			}
			break;

		case GREATER:
			if (stringType.equals(StringType.MATHML)) {
				MathmlTemplate.mathml(sb, "<gt/>", leftStr, rightStr);
			} else {
				tpl.infixBinary(sb, left, right, operation, leftStr, rightStr,
						tpl, tpl.greaterSign());
			}
			break;

		case LESS_EQUAL:
			if (stringType.equals(StringType.MATHML)) {
				MathmlTemplate.mathml(sb, "<leq/>", leftStr, rightStr);
			} else {
				tpl.infixBinary(sb, left, right, operation, leftStr, rightStr,
						tpl, tpl.leqSign());
			}
			break;

		case GREATER_EQUAL:
			if (stringType.equals(StringType.MATHML)) {
				MathmlTemplate.mathml(sb, "<qeq/>", leftStr, rightStr);
			} else {
				tpl.infixBinary(sb, left, right, operation, leftStr, rightStr,
						tpl, tpl.geqSign());
			}
			break;

		case PARALLEL:
			if (stringType.equals(StringType.OGP)) {
				sb.append("AreParallel[" + leftStr + "," + rightStr + "]");
				break;
			}
			tpl.infixBinary(sb, left, right, operation, leftStr, rightStr, tpl,
					tpl.parallelSign());
			break;

		case PERPENDICULAR:
			if (stringType.equals(StringType.OGP)) {
				sb.append("ArePerpendicular[" + leftStr + "," + rightStr + "]");
				break;
			}
			tpl.infixBinary(sb, left, right, operation, leftStr, rightStr, tpl,
					tpl.perpSign());
			break;

		case VECTORPRODUCT:
			if (stringType.equals(StringType.MATHML)) {
				MathmlTemplate
						.mathml(sb, "<vectorproduct/>", leftStr, rightStr);
			} else if (stringType.equals(StringType.GIAC)) {

				// from Ggb2Giac Cross.2
				sb.append("[[[ggbcrossarg0:=");
				sb.append(leftStr);
				sb.append("], [ggbcrossarg1:=");
				sb.append(rightStr);
				sb.append("]],when(is3dpoint(ggbcrossarg0)||is3dpoint(ggbcrossarg1),point(cross(ggbcrossarg0,ggbcrossarg1)),cross(ggbcrossarg0,ggbcrossarg1))][1]");

			} else {
				tpl.append(sb, leftStr, left, operation);
				// sb.append(leftStr);
				sb.append(' ');
				switch (stringType) {
				case LATEX:
					if (tpl.isInsertLineBreaks()) {
						sb.append("\\-");
					}
					sb.append("\\times");
					break;
				case LIBRE_OFFICE:
					sb.append(" cdot ");
					break;
				default:
					sb.append(strVECTORPRODUCT);
				}
				sb.append(' ');
				tpl.append(sb, rightStr, right, operation);
				// sb.append(rightStr);
			}
			break;
		case PLUS:
			return tpl.plusString(left, right, leftStr, rightStr, valueForm);
		case MINUS:
			return tpl.minusString(left, right, leftStr, rightStr, valueForm,
					loc);

		case MULTIPLY:
			return tpl.multiplyString(left, right, leftStr, rightStr,
					valueForm, loc);
		case DIVIDE:
			return tpl.divideString(left, right, leftStr, rightStr, valueForm);

		case POWER:
			return tpl.powerString(left, right, leftStr, rightStr, valueForm);

		case FACTORIAL:
			switch (stringType) {
			case MATHML:
				MathmlTemplate.mathml(sb, "<factorial/>", leftStr, null);
				break;
			case LIBRE_OFFICE:
				sb.append("fact {");
				if ((leftStr.charAt(0) != '-') && // no unary
						left.isLeaf()) {
					sb.append(leftStr);
				} else {
					sb.append('(');
					sb.append(leftStr);
					sb.append(')');
				}
				sb.append(" }");
				break;

			default:
				if (((leftStr.charAt(0) != '-') && // no unary
						left.isLeaf())
						|| (opID(left) > Operation.POWER.ordinal())) { // not +,
					// -, *,
					// /, ^
					sb.append(leftStr);
				} else {
					sb.append(tpl.leftBracket());
					sb.append(leftStr);
					sb.append(tpl.rightBracket());
				}
				sb.append('!');
				break;
			}
			break;

		case COS:
			trig(kernel, left, leftStr, sb, "<cos/>", "\\cos", "COS(", "cos",
					"cos", tpl, loc, true);
			break;

		case SIN:
			trig(kernel, left, leftStr, sb, "<sin/>", "\\sin", "SIN(", "sin",
					"sin", tpl, loc, true);
			break;

		case TAN:
			trig(kernel, left, leftStr, sb, "<tan/>", "\\tan", "TAN(", "tan",
					"tan", tpl, loc, true);
			break;

		case CSC:
			trig(kernel, left, leftStr, sb, "<csc/>", "\\csc", "CSC(", "csc",
					"csc", tpl, loc, true);
			break;

		case SEC:
			trig(kernel, left, leftStr, sb, "<sec/>", "\\sec", "SEC(", "sec",
					"sec", tpl, loc, true);
			break;

		case COT:
			trig(kernel, left, leftStr, sb, "<cot/>", "\\cot", "COT(", "cot",
					"cot", tpl, loc, true);
			break;

		case CSCH:
			trig(kernel, left, leftStr, sb, "<csch/>", "\\csch", "CSCH(",
					"csch", "func csch", tpl, loc, false);
			break;

		case SECH:
			trig(kernel, left, leftStr, sb, "<sech/>", "\\sech", "SECH(",
					"sech", "func sech", tpl, loc, false);
			break;

		case COTH:
			trig(kernel, left, leftStr, sb, "<coth/>", "\\coth", "COTH(",
					"coth", "coth", tpl, loc, false);
			break;

		case ARCCOS:
			trig(kernel, left, leftStr, sb, "<arccos/>", "\\arccos", "ACOS(",
					"acos", "arccos", degFix("acos", left, kernel), tpl, loc,
					false);
			break;

		case ARCSIN:
			trig(kernel, left, leftStr, sb, "<arcsin/>", "\\arcsin", "ASIN(",
					"asin", "arcsin", degFix("asin", left, kernel), tpl, loc,
					false);
			break;

		case ARCTAN:
			trig(kernel, left, leftStr, sb, "<arctan/>", "\\arctan", "ATAN(",
					"atan", "arctan", degFix("atan", left, kernel), tpl, loc,
					false);
			break;

		case ARCTAN2:
			if (stringType.equals(StringType.MATHML)) {
				MathmlTemplate.mathml(sb, "<atan/>", leftStr, rightStr);
			} else {
				switch (stringType) {
				case LATEX:
					sb.append("atan2 \\left( ");
					break;
				case LIBRE_OFFICE:
					sb.append("func atan2 left( ");
					break;
				case PSTRICKS:
					sb.append("ATAN2(");
					break;

				case GIAC:
					sb.append(degFix("atan2", left, kernel));
					sb.append("(");
					break;

				default:
					sb.append("atan2(");
				}
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(tpl.rightBracket());
			}
			break;

		case COSH:
			trig(kernel, left, leftStr, sb, "<cosh/>", "\\cosh", "COSH(",
					"cosh", "cosh", tpl, loc, false);
			break;

		case SINH:
			trig(kernel, left, leftStr, sb, "<sinh/>", "\\sinh", "SINH(",
					"sinh", "sinh", tpl, loc, false);
			break;

		case TANH:
			trig(kernel, left, leftStr, sb, "<tanh/>", "\\tanh", "TANH(",
					"tanh", "tanh", tpl, loc, false);
			break;

		case ACOSH:
			trig(kernel, left, leftStr, sb, "<arccosh/>", "\\acosh", "ACOSH(",
					"acosh", "arcosh", tpl, loc, false);
			break;
		case ASINH:
			trig(kernel, left, leftStr, sb, "<arcsinh/>", "\\asinh", "ASINH(",
					"asinh", "arsinh", tpl, loc, false);
			break;

		case ATANH:
			trig(kernel, left, leftStr, sb, "<arctanh/>", "\\atanh", "ATANH(",
					"atanh", "artanh", tpl, loc, false);
			break;
		case REAL:
			trig(kernel, left, leftStr, sb, "<real/>", "\\real", "", "real",
					"real", "re", tpl, loc, false);
			break;
		case IMAGINARY:
			trig(kernel, left, leftStr, sb, "<imaginary/>", "\\imaginary", "",
					"imaginary", "imaginary", "im", tpl, loc, false);
			break;
		case FRACTIONAL_PART:
			// trig(leftStr,sb,"<todo/>","\\fractionalPart","","","fractionalPart","fractionalPart","fractionalPart","fPart",
			// Giac: problem with fPart, use custom definition instead, see
			// CASgiacW
			trig(kernel, left, leftStr, sb, "<todo/>", "\\fractionalPart", "",
					"fractionalPart", "fractionalPart", "fractionalPart", tpl,
					loc, false);
			break;
		case ZETA:
			switch (stringType) {
			case LATEX:
				sb.append("\\zeta\\left( ");
				break;

			case LIBRE_OFFICE:
				sb.append("func zeta left (");
				break;
			case GIAC:
				sb.append("Zeta(");
				break;
			default:
				sb.append("zeta(");
			}
			sb.append(leftStr);
			sb.append(tpl.rightBracket());
			break;
		case CI:
			switch (stringType) {
			case LATEX:
				sb.append("\\Ci \\left( ");
				break;

			case LIBRE_OFFICE:
				sb.append("func Ci left (");
				break;

			case GIAC:
				appendReduceFunction(sb, left, "Ci");
				break;
			default:
				sb.append("cosIntegral(");
			}
			sb.append(leftStr);
			sb.append(tpl.rightBracket());
			break;
		case SI:
			switch (stringType) {
			case LATEX:
				sb.append("\\Si \\left( ");
				break;

			case LIBRE_OFFICE:
				sb.append("func Si left (");
				break;

			case GIAC:
				appendReduceFunction(sb, left, "Si");
				break;

			default:
				sb.append("sinIntegral(");
			}
			sb.append(leftStr);
			sb.append(tpl.rightBracket());
			break;
		case EI:
			switch (stringType) {
			case LATEX:
				sb.append("\\Ei \\left( ");
				break;
			case LIBRE_OFFICE:
				sb.append("func Ei left (");
				break;

			case GIAC:
				appendReduceFunction(sb, left, "Ei");
				break;

			default:
				sb.append("expIntegral(");
			}
			sb.append(leftStr);
			sb.append(tpl.rightBracket());
			break;
		case ARBCONST:
			sb.append("arbconst(");
			sb.append(leftStr);
			sb.append(")");
			break;
		case ARBINT:
			sb.append("arbint(");
			sb.append(leftStr);
			sb.append(")");
			break;
		case ARBCOMPLEX:

			sb.append("arbcomplex(");
			sb.append(leftStr);
			sb.append(")");

			break;
		case EXP:
			switch (stringType) {
			case MATHML:
				MathmlTemplate.mathml(sb, "<exp/>", leftStr, null);
				break;
			case LIBRE_OFFICE:
				sb.append("func ");
			case LATEX:

				// add brackets for eg e^b^c -> e^(b^c)
				boolean addParentheses = (left.isExpressionNode() && ((ExpressionNode) left)
						.getOperation().equals(Operation.POWER));

				sb.append("\\mathit{e}^{");
				if (addParentheses) {
					sb.append(tpl.leftBracket());
				}
				sb.append(leftStr);
				if (addParentheses) {
					sb.append(tpl.rightBracket());
				}
				sb.append('}');
				break;

			case GEOGEBRA_XML:
			case GIAC:
				sb.append("exp(");
				sb.append(leftStr);
				sb.append(')');
				break;

			case PSTRICKS:
				sb.append("EXP(");
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				sb.append(Unicode.EULER_STRING);
				if (left.isLeaf()) {
					sb.append("^");
					sb.append(leftStr);
				} else {
					sb.append("^(");
					sb.append(leftStr);
					sb.append(')');
				}
				break;
			}
			break;

		case LOG:
			if (stringType.equals(StringType.MATHML)) {
				MathmlTemplate.mathml(sb, "<ln/>", leftStr, null);
			} else {
				switch (stringType) {
				case LATEX:
					sb.append("\\ln \\left( ");
					break;
				case LIBRE_OFFICE:
					sb.append("ln left ( ");
					break;
				case GIAC:
				case GEOGEBRA_XML:
					sb.append("log(");
					break;

				case PSTRICKS:
				case PGF:
				default:
					sb.append("ln(");
					break;
				}
				sb.append(leftStr);
				sb.append(tpl.rightBracket());
			}
			break;

		case LOGB:
			switch (stringType) {
			case MATHML:
				MathmlTemplate.mathml(sb, "<log/>", "<logbase>", leftStr,
						"</logbase>", "", rightStr, "");
				break;
			case LATEX:
				sb.append("\\log_{");
				sb.append(leftStr);
				sb.append('}');
				sb.append(tpl.leftBracket());
				sb.append(rightStr);
				sb.append(tpl.rightBracket());
				break;
			case LIBRE_OFFICE:
				sb.append("log_{");
				sb.append(leftStr);
				sb.append('}');
				sb.append(tpl.leftBracket());
				sb.append(rightStr);
				sb.append(tpl.rightBracket());
				break;
			case GIAC:
			case PSTRICKS:
			case PGF:
				// ln(x)/ln(b)
				sb.append("ln(");
				sb.append(rightStr);
				sb.append(")/ln(");
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				sb.append("log(");
				sb.append(leftStr);
				sb.append(", ");
				sb.append(rightStr);
				sb.append(')');
				break;

			}
			break;

		case POLYGAMMA:
			switch (stringType) {
			case LATEX:
				sb.append("\\psi_{");
				sb.append(leftStr);
				sb.append('}');
				sb.append(tpl.leftBracket());
				sb.append(rightStr);
				sb.append(tpl.rightBracket());
				break;

			case GIAC:
				// *******************
				// arguments swapped
				// swapped back in CommandDispatcherGiac
				// *******************
				appendReduceFunction(sb, left, "Psi");
				sb.append(rightStr);
				sb.append(',');
				sb.append(leftStr);
				sb.append(')');
				break;
			default:
				sb.append("polygamma(");
				sb.append(leftStr);
				if (stringType.equals(StringType.LIBRE_OFFICE))
					sb.append("\",\"");
				else
					sb.append(", ");
				sb.append(rightStr);
				sb.append(')');
				break;

			}
			break;

		case ERF:
			switch (stringType) {
			case LATEX:
				sb.append("\\erf");
				sb.append(tpl.leftBracket());
				sb.append(leftStr);
				sb.append(tpl.rightBracket());
				break;
			case LIBRE_OFFICE:
				sb.append("func ");
			case GIAC:
			default:
				sb.append("erf(");
				sb.append(leftStr);
				sb.append(')');
				break;

			}
			break;

		case PSI:
			switch (stringType) {
			case LATEX:
				sb.append("\\psi");
				sb.append(tpl.leftBracket());
				sb.append(leftStr);
				sb.append(tpl.rightBracket());
				break;

			case GIAC:
				appendReduceFunction(sb, left, "Psi");
				sb.append(leftStr);
				sb.append(')');
				break;

			case LIBRE_OFFICE:
				sb.append("func ");
			default:
				sb.append("psi(");
				sb.append(leftStr);
				sb.append(')');
				break;

			}
			break;

		case LOG10:
			switch (stringType) {
			case MATHML:
				MathmlTemplate.mathml(sb, "<log/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\log_{10} \\left(");
				sb.append(leftStr);
				sb.append("\\right)");
				break;
			case LIBRE_OFFICE:
				sb.append("log_10 (");
				sb.append(leftStr);
				sb.append(")");
				break;
			case PSTRICKS:
				sb.append("log(");
				sb.append(leftStr);
				sb.append(')');
				break;

			case GIAC:
			case PGF:
				sb.append("log10("); // user-defined function in Maxima
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				sb.append("lg(");
				sb.append(leftStr);
				sb.append(')');
				break;
			}
			break;

		case LOG2:
			switch (stringType) {
			case LATEX:
				sb.append("\\log_{2} \\left(");
				sb.append(leftStr);
				sb.append("\\right)");
				break;
			case LIBRE_OFFICE:
				sb.append("log_2 (");
				sb.append(leftStr);
				sb.append(")");
				break;
			case GIAC:
				sb.append("log(");
				sb.append(leftStr);
				sb.append(")/log(2)");
				break;

			default:
				sb.append("ld(");
				sb.append(leftStr);
				sb.append(')');
				break;
			}
			break;
		case NROOT:
			switch (stringType) {
			case MATHML:
				MathmlTemplate.mathml(sb, "<root/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\sqrt[");
				sb.append(rightStr);
				sb.append("]{");
				sb.append(leftStr);
				sb.append('}');
				break;
			case LIBRE_OFFICE:
				sb.append("nroot{");
				sb.append(rightStr);
				sb.append("},{");
				sb.append(leftStr);
				sb.append('}');
				break;

			case GEOGEBRA:
				sb.append(loc.getFunction("nroot"));
				sb.append("(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(')');
				break;
			case GIAC:
				if (leftStr.equals(Unicode.EULER_STRING)) {
					sb.append("exp(1/(");
					sb.append(rightStr);
					sb.append("))");
				} else {
					// was simplify(surd(, causes problems with output from
					// cubic formula, eg x^3 - 6x^2 - 7x + 9
					sb.append("surd(");
					sb.append(leftStr);
					sb.append(',');
					sb.append(rightStr);
					sb.append(")");
				}
				break;
			default: // MAXIMA, MPREDUCE, PSTRICKS, ...
				sb.append("(");
				sb.append(leftStr);
				sb.append(")^(1/(");
				sb.append(rightStr);
				sb.append("))");
				break;
			}
			break;

		case SQRT_SHORT:
		case SQRT:
			switch (stringType) {
			case MATHML:
				MathmlTemplate.mathml(sb, "<root/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\sqrt{");
				sb.append(leftStr);
				sb.append('}');
				break;
			case LIBRE_OFFICE:
				sb.append("sqrt{");
				sb.append(leftStr);
				sb.append('}');
				break;

			default:
				sb.append("sqrt(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case CBRT:
			switch (stringType) {
			case MATHML:
				MathmlTemplate.mathml(sb, "<root/>", "<degree>", "3",
						"</degree>", "", leftStr, "");
				break;
			case LATEX:
				sb.append("\\sqrt[3]{");
				sb.append(leftStr);
				sb.append('}');
				break;
			case LIBRE_OFFICE:
				sb.append("nroot{3}{");
				sb.append(leftStr);
				sb.append('}');
				break;

			case GIAC:
				// was simplify(surd(, causes problems with output from cubic
				// formula, eg x^3 - 6x^2 - 7x + 9
				sb.append("surd(");
				sb.append(leftStr);
				sb.append(",3)");
				break;
			default:
				sb.append("cbrt(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case ABS:
			switch (stringType) {
			case MATHML:
				MathmlTemplate.mathml(sb, "<abs/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\left|");
				sb.append(leftStr);
				sb.append("\\right|");
				break;
			case LIBRE_OFFICE:
				sb.append("abs{");
				sb.append(leftStr);
				sb.append('}');
				break;
			case GIAC:
				sb.append("normal(ggbabs(");
				sb.append(leftStr);
				sb.append("))");
				break;

			default:
				sb.append("abs(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case SGN:
			switch (stringType) {
			case LATEX:
				sb.append("\\sgn(");
				break;

			case GIAC:
				sb.append("sign(");
				break;

			case LIBRE_OFFICE:
				sb.append("func ");
			default:
				sb.append("sgn(");
			}
			sb.append(leftStr);
			sb.append(')');
			break;

		case CONJUGATE:
			switch (stringType) {
			case MATHML:
				MathmlTemplate.mathml(sb, "<conjugate/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\overline{");
				sb.append(leftStr);
				sb.append("}");
				break;
			case LIBRE_OFFICE:
				sb.append("overline{");
				sb.append(leftStr);
				sb.append("}");
				break;

			case GIAC:
				sb.append("conj(");
				sb.append(leftStr);
				sb.append(')');
				break;
			default:
				if (tpl.isPrintLocalizedCommandNames()) {
					sb.append(loc.getFunction("conjugate"));
				} else {
					sb.append("conjugate");
				}

				sb.append("(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case ARG:
			switch (stringType) {
			case MATHML:
				MathmlTemplate.mathml(sb, "<arg/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\arg \\left( ");
				sb.append(leftStr);
				sb.append("\\right)");
				break;
			case GIAC:
				sb.append("arg(");
				sb.append(leftStr);
				sb.append(')');
				break;

			case LIBRE_OFFICE:
				sb.append("func ");
			default:
				sb.append("arg(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case ALT:
			switch (stringType) {
			case MATHML:
				MathmlTemplate.mathml(sb, "<alt/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\alt \\left( ");
				sb.append(leftStr);
				sb.append("\\right)");
				break;
			case GIAC:
				sb.append("ggbalt(");
				sb.append(leftStr);
				sb.append(')');
				break;

			case LIBRE_OFFICE:
				sb.append("func ");
			default:
				sb.append("alt(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case FLOOR:
			switch (stringType) {
			case MATHML:
				MathmlTemplate.mathml(sb, "<floor/>", leftStr, null);
				break;
			case LATEX:
				if (!kernel.getApplication().isLatexMathQuillStyle(tpl)) {
					// MathQuillGGB doesn't support this
					sb.append("\\left");
				}
				sb.append("\\lfloor ");
				sb.append(leftStr);
				if (!kernel.getApplication().isLatexMathQuillStyle(tpl)) {
					// MathQuillGGB doesn't support this
					sb.append("\\right");
				}
				sb.append("\\rfloor ");
				break;
			case LIBRE_OFFICE:
				sb.append(" left lfloor ");
				sb.append(leftStr);
				sb.append(" right rfloor");
				break;

			default:
				sb.append("floor(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case CEIL:
			switch (stringType) {
			case MATHML:
				MathmlTemplate.mathml(sb, "<ceiling/>", leftStr, null);
				break;
			case LATEX:
				if (!kernel.getApplication().isHTML5Applet()) {
					// MathQuillGGB doesn't support this
					sb.append("\\left");
				}
				sb.append("\\lceil ");
				sb.append(leftStr);
				if (!kernel.getApplication().isHTML5Applet()) {
					// MathQuillGGB doesn't support this
					sb.append("\\right");
				}
				sb.append("\\rceil ");
				break;
			case LIBRE_OFFICE:
				sb.append("left lceil ");
				sb.append(leftStr);
				sb.append(" right rceil");
				break;

			case GIAC:
			case PSTRICKS:
				sb.append("ceiling(");
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				sb.append("ceil(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case ROUND:
			switch (stringType) {
			case LATEX:
				sb.append("\\round \\left( ");
				break;

			case LIBRE_OFFICE:
				sb.append("func round left (");

			default:
				sb.append("round(");
			}
			sb.append(leftStr);
			if (right instanceof NumberValue
					&& (!Double.isNaN(((NumberValue) right).getDouble()) || right
							.isGeoElement())) {
				sb.append(", ");
				sb.append(rightStr);
			}
			sb.append(tpl.rightBracket());
			break;

		case GAMMA:
			switch (stringType) {
			case LATEX:
				sb.append("\\Gamma \\left( ");
				break;
			case LIBRE_OFFICE:
				sb.append("%GAMMA left (");
				break;
			case GIAC:
				sb.append("Gamma(");
				break;

			default:
				sb.append("gamma(");
			}
			sb.append(leftStr);
			sb.append(tpl.rightBracket());
			break;

		case GAMMA_INCOMPLETE:
			switch (stringType) {
			case LATEX:
				sb.append("\\gamma \\left( ");
				break;
			case LIBRE_OFFICE:
				sb.append("%GAMMA left (");
				break;
			case GIAC:
				sb.append("igamma(");
				break;

			default:
				sb.append("gamma(");
			}
			sb.append(leftStr);
			if (stringType.equals(StringType.LIBRE_OFFICE))
				sb.append("\",\"");
			else
				sb.append(", ");
			sb.append(rightStr);
			sb.append(tpl.rightBracket());
			break;

		case GAMMA_INCOMPLETE_REGULARIZED:
			switch (stringType) {
			case LATEX:
				sb.append("P \\left( ");
				break;

			case LIBRE_OFFICE:
				sb.append("func gammaRegularized left (");

			case GIAC:
				sb.append("igamma(");
				break;

			default:
				sb.append("gammaRegularized(");
			}
			sb.append(leftStr);
			if (stringType.equals(StringType.LIBRE_OFFICE))
				sb.append("\",\"");
			else
				sb.append(", ");
			sb.append(rightStr);

			if (stringType == StringType.GIAC) {
				sb.append(",1");
			}

			sb.append(tpl.rightBracket());
			break;

		case BETA:
			switch (stringType) {
			case LATEX:
				sb.append("\\Beta \\left( ");
				break;
			case LIBRE_OFFICE:
				sb.append("%BETA left(");
				break;
			case GIAC:
				sb.append("Beta(");
				break;

			default:
				sb.append("beta(");
			}
			sb.append(leftStr);
			if (stringType.equals(StringType.LIBRE_OFFICE))
				sb.append("\",\"");
			else
				sb.append(", ");
			sb.append(rightStr);
			sb.append(tpl.rightBracket());
			break;

		case BETA_INCOMPLETE:
			switch (stringType) {
			case LATEX:
				sb.append("\\Beta \\left( ");
				break;
			case LIBRE_OFFICE:
				sb.append("%BETA left(");
				break;

			case GIAC:
				sb.append("Beta(");
				break;

			default:
				sb.append("beta(");
			}
			sb.append(leftStr);
			if (stringType.equals(StringType.LIBRE_OFFICE))
				sb.append("\",\"");
			else
				sb.append(", ");
			sb.append(rightStr);
			sb.append(tpl.rightBracket());
			break;

		case BETA_INCOMPLETE_REGULARIZED:
			switch (stringType) {
			case LATEX:
				sb.append("I \\left( ");
				break;

			case LIBRE_OFFICE:
				sb.append("func betaRegularized left (");

			case GIAC:
				sb.append("Beta(");
				break;

			default:
				sb.append("betaRegularized(");
			}
			sb.append(leftStr);
			if (stringType.equals(StringType.LIBRE_OFFICE))
				sb.append("\",\"");
			else
				sb.append(", ");
			sb.append(rightStr);

			if (stringType == StringType.GIAC) {
				sb.append(",1");
			}

			sb.append(tpl.rightBracket());
			break;

		case RANDOM:
			if (valueForm) {
				sb.append(leftStr);
			} else {
				switch (stringType) {

				case GIAC:
					sb.append("rand(0,1)");
					break;
				case LIBRE_OFFICE:
					sb.append("func ");
				default:
					sb.append("random()");
				}
			}
			break;

		case XCOORD:
			if (stringType != StringType.GIAC && valueForm
					&& (leftEval = left.evaluate(tpl)) instanceof VectorValue) {
				sb.append(kernel.format(((VectorValue) leftEval).getVector()
						.getX(), tpl));
			} else if (stringType != StringType.GIAC && valueForm
					&& (leftEval = left.evaluate(tpl)) instanceof Vector3DValue) {
				sb.append(kernel.format(
						((Vector3DValue) leftEval).getPointAsDouble()[0], tpl));
			} else if (valueForm
					&& ((leftEval = left.evaluate(tpl)) instanceof GeoLine)) {
				sb.append(kernel.format(((GeoLine) leftEval).getX(), tpl));
			} else {
				switch (stringType) {
				case LATEX:
					sb.append(" x \\left( ");
					sb.append(leftStr);
					sb.append(tpl.rightBracket());
					break;
				case LIBRE_OFFICE:
					sb.append("func x left (");
					sb.append(leftStr);
					sb.append(tpl.rightBracket());
				case GIAC:
					sb.append("xcoord(");
					sb.append(leftStr);
					sb.append(")");
					break;

				default:
					sb.append("x(");
					sb.append(leftStr);
					sb.append(')');
				}
			}
			break;

		case YCOORD:
			leftEval = left.evaluate(tpl);
			if (stringType != StringType.GIAC && valueForm && leftEval instanceof VectorValue) {
				sb.append(kernel.format(((VectorValue) leftEval).getVector()
						.getY(), tpl));
			} else if (stringType != StringType.GIAC && valueForm
					&& (leftEval = left.evaluate(tpl)) instanceof Vector3DValue) {
				sb.append(kernel.format(
						((Vector3DValue) leftEval).getPointAsDouble()[1], tpl));
			} else if (valueForm
					&& ((leftEval = left.evaluate(tpl)) instanceof GeoLine)) {
				sb.append(kernel.format(((GeoLine) leftEval).getY(), tpl));
			} else {
				switch (stringType) {
				case LATEX:
					sb.append(" y \\left( ");
					sb.append(leftStr);
					sb.append("\\right)");
					break;
				case LIBRE_OFFICE:
					sb.append("func y left (");
					sb.append(leftStr);
					sb.append(tpl.rightBracket());
				case GIAC:
					sb.append("ycoord(");
					sb.append(leftStr);
					sb.append(")");
					break;

				default:
					sb.append("y(");
					sb.append(leftStr);
					sb.append(')');
				}
			}
			break;

		case ZCOORD:
			if (stringType != StringType.GIAC && valueForm
					&& (leftEval = left.evaluate(tpl)) instanceof Vector3DValue) {
				sb.append(kernel.format(
						((Vector3DValue) leftEval).getPointAsDouble()[2], tpl));
			} else if (valueForm
					&& ((leftEval = left.evaluate(tpl)) instanceof GeoLine)) {
				sb.append(kernel.format(((GeoLine) leftEval).getZ(), tpl));
			} else {
				switch (stringType) {
				case LATEX:
					sb.append(" z \\left( ");
					sb.append(leftStr);
					sb.append("\\right)");
					break;
				case LIBRE_OFFICE:
					sb.append("func z left (");
					sb.append(leftStr);
					sb.append(tpl.rightBracket());
				case GIAC:
					sb.append("zcoord(");
					sb.append(leftStr);
					sb.append(")");
					break;

				default:
					sb.append("z(");
					sb.append(leftStr);
					sb.append(')');
				}
			}
			break;

		case MULTIPLY_OR_FUNCTION:
			Log.debug("Operation not resolved");
			// FALL THROUGH
		case FUNCTION:

			if (stringType == StringType.GIAC && right instanceof ListValue) {
				// TODO: does this ever get called?

				ListValue list = (ListValue) right;

				// eg seq(sin({4,5,6}[j]),j,0,2)
				// DON'T USE i (sqrt(-1) in Giac)
				sb.append("seq(");
				sb.append(leftStr);
				sb.append('(');
				sb.append(rightStr);
				sb.append("[j]),j,0,");
				sb.append(list.size() - 1);
				sb.append(')');
				break;
			}

			// GeoFunction and GeoFunctionConditional should not be expanded
			if (left instanceof GeoFunction) {
				GeoFunction geo = (GeoFunction) left;
				if (geo.isLabelSet()) {
					if (stringType.equals(StringType.LIBRE_OFFICE))
						sb.append("func ");
					sb.append(geo.getLabel(tpl));
					sb.append(tpl.leftBracket());
					sb.append(rightStr);
					sb.append(tpl.rightBracket());
				} else {
					// inline function: replace function var by right side
					FunctionVariable var = geo.getFunction()
							.getFunctionVariable();
					String oldVarStr = var.toString(tpl);
					var.setVarString(rightStr);
					if (stringType.equals(StringType.LIBRE_OFFICE))
						sb.append("func ");
					// do not recompute the expression string if we are plugging
					// in the same variable; #3481
					String rhString = oldVarStr.equals(rightStr) ? leftStr
							: geo.getLabel(tpl);
					sb.append(rhString);
					var.setVarString(oldVarStr);
				}
			} else if (valueForm && left.isExpressionNode()) {
				ExpressionNode en = (ExpressionNode) left;
				// left could contain $ nodes to wrap a GeoElement
				// e.g. A1(x) = x^2 and B1(x) = $A$1(x)
				// value form of B1 is x^2 and NOT x^2(x)
				switch (en.operation) {
				case $VAR_ROW:
				case $VAR_COL:
				case $VAR_ROW_COL:
					sb.append(tpl.leftBracket());
					sb.append(leftStr);
					sb.append(tpl.rightBracket());
					break;

				default:
					sb.append(leftStr);
					sb.append(tpl.leftBracket());
					sb.append(rightStr);
					sb.append(tpl.rightBracket());
					break;
				}
			} else {
				// standard case if we get here
				sb.append(leftStr);
				sb.append(tpl.leftBracket());
				sb.append(rightStr);
				sb.append(tpl.rightBracket());
			}
			break;

		// TODO: put back into case FUNCTION_NVAR:, see #1115
		case ELEMENT_OF:
			sb.append(loc.getCommand("Element"));
			sb.append('[');
			if (left.isGeoElement()) {
				sb.append(((GeoElement) left).getLabel(tpl));
			} else {
				sb.append(leftStr);
			}
			sb.append(", ");
			sb.append(((MyList) right).getListElement(0).toString(tpl));
			sb.append(']');
			break;

		case FUNCTION_NVAR:
			if (valueForm) {
				// TODO: avoid replacing of expressions in operationToString
				if ((left instanceof FunctionalNVar)
						&& (right instanceof MyList)) {
					FunctionNVar func = ((FunctionalNVar) left).getFunction();
					ExpressionNode en = func.getExpression().getCopy(kernel);
					for (int i = 0; (i < func.getVarNumber())
							&& (i < ((MyList) right).size()); i++) {
						en.replace(func.getFunctionVariables()[i],
								((MyList) right).getListElement(i));
					}
					// add brackets, see
					// http://www.geogebra.org/trac/ticket/1446
					if (!stringType.equals(StringType.LATEX)) {
						sb.append(tpl.leftBracket());
					}
					sb.append(en.toValueString(tpl));
					if (!stringType.equals(StringType.LATEX)) {
						sb.append(tpl.rightBracket());
					}
				} else if (left instanceof GeoDummyVariable) {
					sb.append(tpl.leftBracket());
					sb.append(leftStr);
					sb.append(tpl.leftBracket());
					sb.append(rightStr.substring(1, rightStr.length() - 1));
					sb.append(tpl.rightBracket());
					sb.append(tpl.rightBracket());
				} else {
					sb.append(tpl.leftBracket());
					sb.append(leftStr);
					sb.append(tpl.rightBracket());
				}
			} else {
				// multivariate functions
				if (left.isGeoElement()) {
					sb.append(((GeoElement) left).getLabel(tpl));
				} else {
					sb.append(leftStr);
				}
				// no parameters for LeftSide[a], Derivative[sin(x+y),y], etc
				// parameters for unknown cas functions
				if (!left.isGeoElement() || ((GeoElement) left).isLabelSet()
						|| left instanceof GeoDummyVariable
						|| left instanceof GeoCasCell) {
					sb.append(tpl.leftBracket());

					// rightStr is a list of arguments, e.g. {2, 3}
					// drop the curly braces { and }
					// or list( and ) in case of mpreduce
					if (tpl.getStringType().equals(StringType.LATEX)) {
						sb.append(rightStr.substring(2, rightStr.length() - 2));
					} else {
						sb.append(rightStr.substring(1, rightStr.length() - 1));
					}
					sb.append(tpl.rightBracket());
				}
			}
			break;

		case VEC_FUNCTION:
			// GeoCurveCartesian should not be expanded
			if (left.isGeoElement()
					&& ((GeoElement) left).isGeoCurveCartesian()) {
				sb.append(((GeoElement) left).getLabel(tpl));
			} else {
				sb.append(leftStr);
			}
			sb.append(tpl.leftBracket());
			sb.append(rightStr);
			sb.append(tpl.rightBracket());
			break;
		case DIFF:
			// we only serialize this temporarily during GIAC parsing, so only
			// default template needed
			// GIAC template added for safety
			if (tpl.hasType(StringType.GIAC)) {
				sb.append("diff(");
			} else {
				sb.append("ggbdiff(");
			}
			sb.append(leftStr);
			sb.append(',');
			sb.append(rightStr);
			sb.append(")");
		case DERIVATIVE: // e.g. f''
			// labeled GeoElements should not be expanded
			if (tpl.hasType(StringType.GIAC)) {
				sb.append("diff(");
				sb.append(leftStr);
				break;
			}
			if (left.isGeoElement() && ((GeoElement) left).isLabelSet()) {
				sb.append(((GeoElement) left).getLabel(tpl));
			} else {
				sb.append(leftStr);
			}

			if (right.unwrap() instanceof NumberValue) {
				int order = (int) Math.round(right.evaluateDouble());
				for (; order > 0; order--) {
					sb.append('\'');
				}
			} else {
				sb.append(right);
			}
			break;

		case $VAR_ROW: // e.g. A$1
			if (valueForm || tpl.hasCASType()) {
				// GeoElement value
				sb.append(leftStr);
			} else {
				// $ for row
				GeoElement geo = (GeoElement) left;
				if (geo.getSpreadsheetCoords() != null) {
					sb.append(geo.getSpreadsheetLabelWithDollars(false, true));
				} else {
					sb.append(leftStr);
				}
			}
			break;

		case $VAR_COL: // e.g. $A1
			if (valueForm || tpl.hasCASType()) {
				// GeoElement value
				sb.append(leftStr);
			} else {
				// maybe wrongly parsed dynamic reference in CAS -- TODO decide
				// whether we need this
				if (!left.isGeoElement()) {
					sb.append('$');
					sb.append(leftStr);
					break;
				}
				// $ for row
				GeoElement geo = (GeoElement) left;
				if (geo.getSpreadsheetCoords() != null) {
					sb.append(geo.getSpreadsheetLabelWithDollars(true, false));
				} else {
					sb.append(leftStr);
				}
			}
			break;

		case $VAR_ROW_COL: // e.g. $A$1
			if (valueForm || tpl.hasCASType()) {
				// GeoElement value
				sb.append(leftStr);
			} else {
				// $ for row
				GeoElement geo = (GeoElement) left;
				if (geo.getSpreadsheetCoords() != null) {
					sb.append(geo.getSpreadsheetLabelWithDollars(true, true));
				} else {
					sb.append(leftStr);
				}
			}
			break;

		case FREEHAND:
			// need to output eg freehand(ggbtmpvarx) so that Derivative fails
			// rather than giving zero
			sb.append(loc.getPlain("Function.freehand"));
			sb.append('(');
			sb.append(leftStr);
			sb.append(')');
			break;
		case DATA:
			// need to output eg freehand(ggbtmpvarx) so that Derivative fails
			// rather than giving zero
			if (tpl.isPrintLocalizedCommandNames()) {
				sb.append(loc.getCommand("DataFunction"));
			} else {
				sb.append("DataFunction");
			}
			sb.append('[');
			if (tpl.hasType(StringType.GEOGEBRA_XML)) {
				sb.append(rightStr);
			}
			sb.append(']');
			break;
		case INTEGRAL:
			if (stringType == StringType.LATEX) {
				sb.append("\\int ");
				sb.append(leftStr);
				sb.append("d");
				sb.append(rightStr);
			} else if (stringType == StringType.LIBRE_OFFICE) {
				sb.append("int ");
				sb.append(leftStr);
				sb.append(" d");
				sb.append(rightStr);
			} else {
				if (stringType == StringType.GIAC) {
					sb.append("int(");
				} else {
					sb.append("gGbInTeGrAl(");
				}
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(")");
				// AbstractApplication.debug(sb);
			}
			break;
		case SUM:
			if (stringType == StringType.LATEX) {
				sb.append("\\sum_{");
				sb.append(((MyNumberPair) left).y.toString(tpl));
				sb.append("=");
				sb.append(((MyNumberPair) right).x.toString(tpl));
				sb.append("}^{");
				sb.append(((MyNumberPair) right).y.toString(tpl));
				sb.append("}");
				sb.append(((MyNumberPair) left).x.toString(tpl));
			} else if (stringType == StringType.LIBRE_OFFICE) {
				sb.append("sum from{");
				sb.append(((MyNumberPair) left).y.toString(tpl));
				sb.append("=");
				sb.append(((MyNumberPair) right).x.toString(tpl));
				sb.append("} to{");
				sb.append(((MyNumberPair) right).y.toString(tpl));
				sb.append("}");
				sb.append(((MyNumberPair) left).x.toString(tpl));
			} else {
				if (stringType == StringType.GIAC) {
					sb.append("sum(");
				} else {
					sb.append("gGbSuM(");
				}
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(")");
				// AbstractApplication.debug(sb);
			}
			break;
		case SUBSTITUTION:
			if (stringType == StringType.LATEX) {
				sb.append("\\left.");
				sb.append(rightStr);
				sb.append("\\right\\mid_{");
				sb.append(leftStr);
				sb.append("}");
			} else if (stringType == StringType.LIBRE_OFFICE) {
				sb.append("left none");
				sb.append(rightStr);
				sb.append("right rline_{");
				sb.append(leftStr);
				sb.append("}");
			} else {
				if (stringType == StringType.GIAC) {
					sb.append("subst(");
				} else {
					sb.append("gGbSuBsTiTuTiOn(");
				}
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(")");
			}
			break;
		case IF:
			if (stringType == StringType.GIAC) {
				sb.append("when(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(",undef)");
			} else {
				if (tpl.isPrintLocalizedCommandNames()) {
					sb.append(loc.getCommand("If"));
				} else {
					sb.append("If");
				}
				sb.append('[');
				sb.append(leftStr);
				sb.append(", ");
				sb.append(rightStr);
				sb.append(']');
			}
			break;
		case IF_ELSE:
			if (stringType == StringType.GIAC) {
				sb.append("when(");
				sb.append(leftStr);
				sb.append(",");
				sb.append(rightStr);
				sb.append(")");
			} else {
				if (tpl.isPrintLocalizedCommandNames()) {
					sb.append(loc.getCommand("If"));
				} else {
					sb.append("If");
				}
				sb.append("[");
				sb.append(leftStr);
				sb.append(", ");
				sb.append(rightStr);
				sb.append("]");
			}
			break;

		case IF_LIST:
			if (stringType == StringType.GIAC) {
				sb.append(loc.getCommand("piecewise("));
			} else if (tpl.isPrintLocalizedCommandNames()) {
				sb.append(loc.getCommand("If"));
				sb.append("[");
			} else {
				sb.append("If");
				sb.append("[");
			}

			MyList cond = (MyList) left;
			MyList fn = (MyList) right;
			for (int i = 0; i < cond.size(); i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(valueForm ? cond.getListElement(i).toValueString(tpl)
						: cond.getListElement(i).toString(tpl));
				sb.append(", ");
				sb.append(valueForm ? fn.getListElement(i).toValueString(tpl)
						: fn.getListElement(i).toString(tpl));
			}
			if (fn.size() > cond.size()) {
				sb.append(", ");
				sb.append(valueForm ? fn.getListElement(fn.size() - 1)
						.toValueString(tpl) : fn.getListElement(fn.size() - 1)
						.toString(tpl));
			}

			sb.append(stringType == StringType.GIAC ? ")" : "]");

			break;

		default:
			sb.append("unhandled operation " + operation);
		}
		return sb.toString();
	}

	private static String degFix(String string, ExpressionValue left,
			Kernel kernel) {
		if (kernel.getInverseTrigReturnsAngle()) {
			return "deg" + string;
		}
		return string;
	}

	private static void trig(Kernel kernel, ExpressionValue left,
			String leftStr, StringBuilder sb, String mathml, String latex,
			String psTricks, String key, String libreOffice,
			StringTemplate tpl, Localization loc, boolean needDegrees) {

		// send "key" for Giac
		trig(kernel, left, leftStr, sb, mathml, latex, psTricks, key,
				libreOffice, key, tpl, loc, needDegrees);

	}

	/**
	 * @param left
	 *            left expression (might need context-aware serialization for
	 *            GIAC)
	 */
	private static void trig(Kernel kernel, ExpressionValue left,
			String leftStr, StringBuilder sb, String mathml, String latex,
			String psTricks, String key, String libreOffice, String giac,
			StringTemplate tpl, Localization loc, boolean needDegrees) {
		if (tpl.hasType(StringType.MATHML)) {
			MathmlTemplate.mathml(sb, mathml, leftStr, null);
		} else {
			switch (tpl.getStringType()) {
			case LATEX:
				if (kernel.getApplication().isLatexMathQuillStyle(tpl)) {
					String translatedKey = loc.getFunction(key);

					// supported operators in MathQuillGGB - TODO: are there
					// more?
					if ("exp lg ln log sin cos tan cot sec csc sinh cosh tanh coth sech csch arcsin arccos arctan asin acos atan asinh acosh atanh arcsinh arccosh arctanh"
							.indexOf(translatedKey) > -1) {

						/*
						 * It is important to do this for MathQuill edited in
						 * Algebra view more times, e.g. f(x)=x, f(x)=x+sin(x),
						 * f(x)=x+sin(x)+cos(x)
						 * 
						 * By the way, if things are translated differently,
						 * then it would probably be better to just use "latex"
						 * instead of \\"translatedKey"
						 */
						sb.append(" \\");
						sb.append(translatedKey);
					} else if ("cossech arcsh arcch arcth argsh argch argth arcos arcosh arsinh artanh arch arsh arth ch sh th cth sen tg asen atg arcsen arctg senh tgh asenh atgh arcsenh arctgh cotg cotgh"
							.indexOf(translatedKey) > -1) {
						// International trigonometric functions - not
						// everything!
						// These are also entered into mathquillggb.js!

						sb.append(" \\");
						sb.append(translatedKey);

						/*
						 * } else if (
						 * "exp lg ln log sin cos tan cot sec csc sinh cosh tanh coth sech csch arcsin arccos arctan asin acos atan asinh acosh atanh arcsinh arccosh arctanh sen tg"
						 * .indexOf(key) > -1) { // This branch might be helping
						 * in cases for what we did not care yet; // allowing
						 * entering the command, but renaming it to a syntax
						 * that is known to MathQuill. // Tested on Spanish,
						 * f(x)=arcos(x)+sin(x) --> f(x)=acos(x)+sin(x) // OK,
						 * but it would be better to support "arcos" // Tested
						 * on Arabic, (x)"Arabic letters" --> f(x)=sin(x)
						 * instead of f(x)=(x)"Arabic letters" // Maybe wrong
						 * than before, maybe better (with Arabic, things looked
						 * nice) // Thus this else branch might not be needed if
						 * we support "arcos" and other forms in the previous
						 * branch sb.append(" \\"); sb.append(key);
						 */

					} else {
						sb.append(" ");
						sb.append(translatedKey);
						sb.append(" ");
					}
				} else if (tpl.isPrintLocalizedCommandNames()) {
					sb.append("\\operatorname{");
					sb.append(loc.getFunction(key));
					sb.append("}");
				} else {
					sb.append(latex);
				}
				sb.append(" \\left( ");
				break;
			case LIBRE_OFFICE:
				if (!libreOffice.equals(loc.getFunction(key))) {
					sb.append("func ");

				}
				sb.append(loc.getFunction(key));
				sb.append(" left( ");
				break;
			case GIAC:
				sb.append(giac);
				sb.append('(');
				break;

			case PSTRICKS:
				sb.append(psTricks);
				break;
			default:
				if (tpl.isPrintLocalizedCommandNames()) {
					sb.append(loc.getFunction(key));
				} else {
					sb.append(key);
				}
				sb.append("(");
			}
			if (needDegrees && tpl.hasType(StringType.PGF)) {
				sb.append("(" + leftStr + ") 180/pi");
			} else {
				sb.append(leftStr);
			}
			sb.append(tpl.rightBracket());
		}

	}

	private static void appendReduceFunction(StringBuilder sb,
			ExpressionValue left, String string) {
		if (left instanceof ListValue) {
			sb.append("applyfunction(");
			sb.append(string);
			sb.append(",");
		} else {
			sb.append(string);
			sb.append('(');
		}

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

	public boolean isNumberValue() {
		return evaluate(StringTemplate.defaultTemplate).isNumberValue();
	}

	@Override
	public boolean evaluatesToList() {
		if (isLeaf()) {
			return left.evaluatesToList();
		}
		// eg. sin(list), f(list)
		if (Operation.isSimpleFunction(operation)
				|| operation == Operation.FUNCTION) {
			return left.evaluatesToList();
		}
		if (operation == Operation.IS_ELEMENT_OF
				|| operation == Operation.IS_SUBSET_OF
				|| operation == Operation.IS_SUBSET_OF_STRICT
				|| operation == Operation.EQUAL_BOOLEAN
				|| operation == Operation.NOT_EQUAL
				|| operation == Operation.FREEHAND
				|| operation == Operation.DATA
				|| operation == Operation.ELEMENT_OF
				|| operation == Operation.FUNCTION_NVAR) {
			return false;
		}
		if (left.evaluatesToList()
				|| (right != null && right.evaluatesToList())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean evaluatesToText() {
		// should be efficient as it is used in operationToString()
		if (leaf) {
			return left.evaluatesToText();
		}
		return (operation.equals(Operation.PLUS) || operation
				.equals(Operation.MULTIPLY))
				&& (left.evaluatesToText() || right.evaluatesToText());
	}

	@Override
	final public boolean isExpressionNode() {
		return true;
	}

	/**
	 * Returns true iff ev1 and ev2 are equal
	 * 
	 * @param ev1
	 *            first value to compare
	 * @param ev2
	 *            second value to compare
	 * @return true iff ev1 and ev2 are equal
	 */
	public static boolean isEqual(ExpressionValue ev1, ExpressionValue ev2) {
		if (ev1 instanceof NumberValue && ev2 instanceof NumberValue) {
			return Kernel.isEqual(((NumberValue) ev1).getDouble(),
					((NumberValue) ev2).getDouble(), Kernel.STANDARD_PRECISION);
		} else if (ev1 instanceof TextValue && ev2 instanceof TextValue) {
			return ((TextValue) ev1).toValueString(
					StringTemplate.defaultTemplate).equals(
					((TextValue) ev2)
							.toValueString(StringTemplate.defaultTemplate));
		} else if (ev1 instanceof VectorValue && ev2 instanceof VectorValue) {
			return ((VectorValue) ev1).getVector().isEqual(
					((VectorValue) ev2).getVector());
		} else if (ev1 instanceof BooleanValue && ev2 instanceof BooleanValue) {
			return ((BooleanValue) ev1).getMyBoolean().getBoolean() == ((BooleanValue) ev2)
					.getMyBoolean().getBoolean();
		} else if (ev1.isGeoElement() && ev2.isGeoElement()) {
			return ((GeoElement) ev1).isEqual(((GeoElement) ev2));
		} else if (ev1 instanceof Functional && ev2 instanceof Functional) {
			return ((Functional) ev1).getGeoFunction().isEqual(
					((Functional) ev2).getGeoFunction());
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
			if (ev instanceof FunctionVariable
					|| ev instanceof GeoDummyVariable) {
				return false;
			} else if (ev instanceof MySpecialDouble) {
				// special doubles like pi, degree, rad need to be kept
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

			NumberValue nv = (NumberValue) ev;
			return nv.getDouble() == val;
		}
		return false;
	}

	@Override
	public boolean isTopLevelCommand() {
		return isLeaf() && (left instanceof Command);
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
			if (this.toString(StringTemplate.defaultTemplate).equals(
					fv.toString(StringTemplate.defaultTemplate))) {
				return 1.0;
			}

			return 0.0;

		}

		Double lc = getLeftTree() == null ? null : getLeftTree()
				.getCoefficient(fv);
		Double rc = getRightTree() == null ? null : getRightTree()
				.getCoefficient(fv);
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

	private static boolean isConstantDouble(ExpressionValue ev, double v) {
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
		if (isConstantDouble(v2, 0))
			return this;
		if (this.isLeaf() && isConstantDouble(left, 0))
			return v2.wrap();
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
		return new ExpressionNode(kernel, this, Operation.LESS, new MyDouble(
				kernel, d));
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
		return new ExpressionNode(kernel, this, Operation.POWER, new MyDouble(
				kernel, -1));
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
		return new ExpressionNode(kernel, new MyDouble(kernel, -1.0),
				Operation.MULTIPLY, this);
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
	public ExpressionNode betaRegularized(ExpressionValue v1, ExpressionValue v2) {
		return new ExpressionNode(kernel, new MyNumberPair(kernel, this, v1),
				Operation.BETA_INCOMPLETE_REGULARIZED, v2);
	}

	/**
	 * @return result of this^2
	 */
	public ExpressionNode square() {
		return new ExpressionNode(kernel, this, Operation.POWER, new MyDouble(
				kernel, 2.0));
	}

	/**
	 * @param v2
	 *            value to subtract
	 * @return result of subtract
	 */
	public ExpressionNode subtract(ExpressionValue v2) {
		if (isConstantDouble(v2, 0))
			return this;
		if (this.isLeaf() && isConstantDouble(left, 0))
			return v2.wrap().reverseSign();
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
		return new ExpressionNode(kernel, this, Operation.PLUS, new MyDouble(
				kernel, d));
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
		if (d == 0 || isConstantDouble(this, 0)) {
			// don't use Kernel.isZero() to check == 0
			// as can lose leading coefficient of polynomial
			return new ExpressionNode(kernel, 0);
		} else if (Kernel.isEqual(1, d)) {
			return this;
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
		if (d == 0) {
			// don't use Kernel.isZero() to check == 0
			// as can lose leading coefficient of polynomial
			return new ExpressionNode(kernel, 0);
		} else if (Kernel.isEqual(1, d)) {
			return this;
		}
		return new ExpressionNode(kernel, new MyDouble(kernel, d),
				Operation.MULTIPLY, this);
	}

	/**
	 * @param d
	 *            value to multiply
	 * @return result of multiply
	 */
	public ExpressionNode power(double d) {
		if (Kernel.isZero(d)) {
			return new ExpressionNode(kernel, 1);
		} else if (Kernel.isEqual(1, d) || isConstantDouble(this, 1)
				|| isConstantDouble(this, 0)) {
			return this;
		}
		return new ExpressionNode(kernel, this, Operation.POWER, new MyDouble(
				kernel, d));
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
		return new ExpressionNode(kernel, this, Operation.MINUS, new MyDouble(
				kernel, d));
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
		if (isConstantDouble(v2, 0) || isConstantDouble(this, 1))
			return v2.wrap();
		if (isConstantDouble(v2, 1) || isConstantDouble(this, 0))
			return this;
		return new ExpressionNode(kernel, v2, Operation.MULTIPLY, this);
	}

	/**
	 * @param v2
	 *            coefficient
	 * @return result of multiplication
	 */
	public ExpressionNode multiplyR(ExpressionValue v2) {
		if (isConstantDouble(v2, 0))
			return v2.wrap();
		if (isConstantDouble(v2, 1))
			return this;
		return new ExpressionNode(kernel, this, Operation.MULTIPLY, v2);
	}

	/**
	 * @param v2
	 *            exponent
	 * @return resulting power
	 */
	public ExpressionNode power(ExpressionValue v2) {
		if (isConstantDouble(v2, 0))
			return new ExpressionNode(kernel, 1);
		if (isConstantDouble(v2, 1))
			return this;
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

		return new ExpressionNode(kernel, this, Operation.DIVIDE, new MyDouble(
				kernel, d));
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
						&& rightLeaf.getRight() instanceof NumberValue
						&& Kernel
								.isInteger(((NumberValue) rightLeaf.getRight())
										.getDouble())
						&& ((NumberValue) rightLeaf.getRight()).getDouble() <= maxRoot) {
					setOperation(Operation.NROOT);
					setRight(new MyDouble(kernel,
							((NumberValue) rightLeaf.getRight()).getDouble()));
					hit = true;
				}
				if (hit) {
					didReplacement = true;
					if (rightLeaf.getLeft()
							.toString(StringTemplate.defaultTemplate)
							.equals("1")) {
						if (operation != Operation.NROOT)
							setRight(new MyDouble(kernel, Double.NaN));
					} else { // to parse x^(c/2) to sqrt(x^c)
						double c = 1;
						if (rightLeaf.getLeft().isConstant())
							c = rightLeaf.getLeft().evaluateDouble();
						if (c < 0) {

							setRight(new ExpressionNode(kernel, getLeft()
									.wrap().power(-c), getOperation(),
									getRight()));
							setOperation(Operation.DIVIDE);
							setLeft(new MyDouble(kernel, 1.0));

						} else
							setLeft(new ExpressionNode(kernel, getLeft(),
									Operation.POWER, rightLeaf.getLeft()));
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
		if (isLeaf())
			return getLeft();
		return this;
	}

	@Override
	public ExpressionNode wrap() {
		return this;
	}

	@Override
	public boolean hasCoords() {
		if (isLeaf())
			return left != null && left.hasCoords();
		if(operation == Operation.FUNCTION_NVAR){
			App.printStacktrace(left+","+left.getClass()+","+left.hasCoords());
		}
		return getLeft().hasCoords() || getRight().hasCoords();
	}

	@Override
	public ExpressionNode derivative(FunctionVariable fv, Kernel kernel) {
		switch (operation) {

		// for eg (x < x1) * (a1 x^2 + b1 x + c1)
		// we need to return 0 for derivative of (x < x1)
		// so that the product rule gives the correct answer
		case LESS:
		case LESS_EQUAL:
		case GREATER:
		case GREATER_EQUAL:
		case NOT:

		case XCOORD:
		case YCOORD:
		case ZCOORD:
			return new ExpressionNode(kernel, 0d);

		case POWER:
			if (right.isNumberValue() && !right.contains(fv)) {
				if (Kernel.isZero(right.evaluateDouble())) {
					return wrap(new MyDouble(kernel, 0d));
				}

				// make sure Tangent[x^(1/3), A] works when x(A)<0
				if (right.isConstant()) {

					double rightDoub = right.evaluateDouble();

					// not an integer, convert to x^(a/b)
					if (!Kernel.isInteger(rightDoub)) {

						double[] fraction = AlgoFractionText.DecimalToFraction(
								rightDoub, Kernel.STANDARD_PRECISION);

						double a = fraction[0];
						double b = fraction[1];

						// App.debug(a + " / " + b);

						if (b == 0) {
							return wrap(new MyDouble(kernel, Double.NaN));
						}

						// a/b-1 = (a-b)/b
						ExpressionNode newPower = wrap(
								new MyDouble(kernel, a - b)).divide(
								new MyDouble(kernel, b));

						// x^(1/b-1) * a / b * x'
						return wrap(left).power(newPower).multiply(a).divide(b)
								.multiply(left.derivative(fv, kernel));
					}
				}

				return wrap(left).power(wrap(right).subtract(1))
						.multiply(left.derivative(fv, kernel)).multiply(right);
			}

			return wrap(left).power(right).multiply(
					wrap(right.derivative(fv, kernel)).multiply(wrap(left).ln()).plus(
							wrap(right).multiply(left.derivative(fv, kernel)).divide(
									left)));

		case NO_OPERATION:
			return wrap(left.derivative(fv, kernel));
		case DIVIDE:
			if (right.isNumberValue() && !right.contains(fv)) {
				return wrap(left).derivative(fv, kernel).divide(right);
			}
			return wrap(left.derivative(fv, kernel)).multiply(right)
					.subtract(wrap(right.derivative(fv, kernel)).multiply(left))
					.divide(wrap(right).square());
		case MULTIPLY:
			if (right.isNumberValue() && !right.contains(fv)) {
				return wrap(left).derivative(fv, kernel).multiply(right);
			}
			if (left.isNumberValue() && !left.contains(fv)) {
				return wrap(right).derivative(fv, kernel).multiply(left);
			}
			return wrap(left).multiply(right.derivative(fv, kernel)).plus(
					wrap(right).multiply(left.derivative(fv, kernel)));
		case PLUS:
			return wrap(left.derivative(fv, kernel)).plus(right.derivative(fv, kernel));
		case MINUS:
			return wrap(left.derivative(fv, kernel)).subtract(right.derivative(fv, kernel));
		case SIN:
			return new ExpressionNode(kernel, left, Operation.COS, null)
					.multiply((left).derivative(fv, kernel));
		case COS:
			return new ExpressionNode(kernel, left, Operation.SIN, null)
					.multiply((left).derivative(fv, kernel)).multiply(-1);
		case TAN:
			return new ExpressionNode(kernel, left, Operation.SEC, null)
					.square().multiply((left).derivative(fv, kernel));
		case SEC:
			return new ExpressionNode(kernel, left, Operation.SEC, null)
					.multiply(
							new ExpressionNode(kernel, left, Operation.TAN,
									null)).multiply((left).derivative(fv, kernel));
		case CSC:
			return new ExpressionNode(kernel, left, Operation.CSC, null)
					.multiply(
							new ExpressionNode(kernel, left, Operation.COT,
									null)).multiply((left).derivative(fv, kernel))
					.multiply(-1);
		case COT:
			return new ExpressionNode(kernel, left, Operation.CSC, null)
					.square().multiply((left).derivative(fv, kernel)).multiply(-1);
		case SINH:
			return new ExpressionNode(kernel, left, Operation.COSH, null)
					.multiply((left).derivative(fv, kernel));
		case COSH:
			return new ExpressionNode(kernel, left, Operation.SINH, null)
					.multiply((left).derivative(fv, kernel));
		case TANH:
			return new ExpressionNode(kernel, left, Operation.SECH, null)
					.square().multiply((left).derivative(fv, kernel));
		case SECH:
			return new ExpressionNode(kernel, left, Operation.SECH, null)
					.multiply(
							new ExpressionNode(kernel, left, Operation.TANH,
									null)).multiply((left).derivative(fv, kernel))
					.multiply(-1);
		case CSCH:
			return new ExpressionNode(kernel, left, Operation.CSCH, null)
					.multiply(
							new ExpressionNode(kernel, left, Operation.COTH,
									null)).multiply((left).derivative(fv, kernel))
					.multiply(-1);
		case COTH:
			return new ExpressionNode(kernel, left, Operation.CSCH, null)
					.square().multiply((left).derivative(fv, kernel)).multiply(-1);

		case ARCSIN:
			return wrap(left.derivative(fv, kernel)).divide(
					wrap(left).square().subtractR(1).sqrt());
		case ARCCOS:
			return wrap(left.derivative(fv, kernel)).divide(
					wrap(left).square().subtractR(1).sqrt()).multiply(-1);
		case ARCTAN:
			return wrap(left.derivative(fv, kernel))
					.divide(wrap(left).square().plus(1));

		case ASINH:
			return wrap(left.derivative(fv, kernel)).divide(
					wrap(left).square().plus(1).sqrt());
		case ACOSH:
			// sqrt(x+1)sqrt(x-1) not sqrt(x^2-1) as has wrong domain
			return wrap(left.derivative(fv, kernel)).divide(
					wrap(left).plus(1).sqrt()
							.multiply(wrap(left).subtract(1).sqrt()));
		case ATANH:
			return wrap(left.derivative(fv, kernel)).divide(
					wrap(left).square().subtractR(1));

		case ABS:
			return wrap(left.derivative(fv, kernel)).multiply(left).divide(
					wrap(left).abs());

		case SGN:
			// 0/x
			return wrap(new MyDouble(kernel, 0)).divide(fv);

		case EXP:
			return wrap(left.derivative(fv, kernel)).multiply(wrap(left).exp());

		case SI:
			return wrap(left.derivative(fv, kernel)).multiply(
					wrap(left).sin().divide(left));

		case CI:
			return wrap(left.derivative(fv, kernel)).multiply(
					wrap(left).cos().divide(left));

		case EI:
			return wrap(left.derivative(fv, kernel)).multiply(
					wrap(left).exp().divide(left));

		case ERF:
			return wrap(left.derivative(fv, kernel)).multiply(wrap(2)).divide(
					wrap(left).square().exp().multiply(wrap(Math.PI).sqrt()));

		case PSI:
			return wrap(left.derivative(fv, kernel)).multiply(wrap(left).polygamma(1));

		case POLYGAMMA:
			if (left.isNumberValue() && !left.contains(fv)) {
				double n = ((NumberValue) left).getDouble();
				return wrap(right.derivative(fv, kernel)).multiply(
						wrap(right).polygamma(n + 1));
			}

			// TODO: general method (not possible?)
			break;

		case IF_ELSE:
			MyNumberPair np = (MyNumberPair) left;

			np = new MyNumberPair(kernel, np.x, np.y.derivative(fv, kernel));

			return new ExpressionNode(kernel, np, Operation.IF_ELSE,
					right.derivative(fv, kernel));

		case IF:

			return new ExpressionNode(kernel, left, Operation.IF,
					right.derivative(fv, kernel));

		case IF_LIST:
			MyList rtDiff = new MyList(kernel);
			MyList rt = (MyList) right;
			for (int i = 0; i < rt.size(); i++) {
				rtDiff.addListElement(rt.getListElement(i).derivative(fv, kernel));
			}
			return new ExpressionNode(kernel, left, Operation.IF_LIST, rtDiff);

		case LOG:
			// base e (ln)
			return wrap(left.derivative(fv, kernel)).divide(left);

		case LOG10:
			return wrap(left.derivative(fv, kernel)).divide(left).divide(Math.log(10));

		case LOG2:
			return wrap(left.derivative(fv, kernel)).divide(left).divide(Math.log(2));

		case LOGB:
			if (left.isNumberValue() && !left.contains(fv)) {
				return wrap(right.derivative(fv, kernel)).divide(right).divide(
						Math.log(((NumberValue) left).getDouble()));
			}

			// TODO: general method
			break;

		case NROOT:
			if (right.isNumberValue() && !right.contains(fv)) {
				return wrap(left.derivative(fv, kernel)).multiply(
						wrap(left).nroot(right)).divide(
						wrap(left).multiply(right));
			}

			// TODO general method
			break;

		case SQRT:
		case SQRT_SHORT:
			return wrap(left.derivative(fv, kernel)).multiply(wrap(left).power(-0.5))
					.divide(2);
		case CBRT:
			// wrong domain
			// return
			// wrap(left.derivative(fv, kernel)).multiply(wrap(left).power(-2d/3d)).divide(3);
			// correct domain
			return wrap(left.derivative(fv, kernel)).divide(wrap(left).square().cbrt())
					.divide(3);

		case FUNCTION:
			if (left instanceof GeoFunction) {
				Function fun = ((GeoFunction) left).getFunction();
				FunctionVariable fv2 = fun.fVars[0];
				ExpressionValue deriv = fun.derivative(fv2, kernel);

				Function fun2 = new Function((ExpressionNode) deriv, fv2);
				GeoFunction geoFun = new GeoFunction(kernel.getConstruction(),
						fun2);

				ExpressionNode ret = new ExpressionNode(kernel, geoFun,
						Operation.FUNCTION, right).multiply(right
						.derivative(fv, kernel));

				return ret;
			}
			break;
		case $VAR_COL:
			break;
		case $VAR_ROW:
			break;
		case $VAR_ROW_COL:
			break;
		case AND:
			break;
		case AND_INTERVAL:
			break;
		case ARBCOMPLEX:
			break;
		case ARBCONST:
			break;
		case ARBINT:
			break;
		case ARCTAN2:
			break;
		case ARG:
			break;
		case ALT:
			break;
		case BETA:
			break;
		case BETA_INCOMPLETE:
			break;
		case BETA_INCOMPLETE_REGULARIZED:
			break;
		case CEIL:
			break;
		case CONJUGATE:
			break;
		case DERIVATIVE:
			break;
		case DIFF:
			break;
		case ELEMENT_OF:
			break;
		case EQUAL_BOOLEAN:
			break;
		case FACTORIAL:
			break;
		case FLOOR:
			break;
		case FRACTIONAL_PART:
			break;
		case FREEHAND:
			break;
		case FUNCTION_NVAR:
			break;
		case GAMMA:
			break;
		case GAMMA_INCOMPLETE:
			break;
		case GAMMA_INCOMPLETE_REGULARIZED:
			break;
		case IMAGINARY:
			break;
		case IMPLICATION:
			break;
		case INTEGRAL:
			break;
		case IS_ELEMENT_OF:
			break;
		case IS_SUBSET_OF:
			break;
		case IS_SUBSET_OF_STRICT:
			break;
		case MULTIPLY_OR_FUNCTION:
			break;
		case NOT_EQUAL:
			break;
		case OR:
			break;
		case PARALLEL:
			break;
		case PERPENDICULAR:
			break;
		case RANDOM:
			break;
		case REAL:
			break;
		case ROUND:
			break;
		case SET_DIFFERENCE:
			break;
		case SUBSTITUTION:
			break;
		case SUM:
			break;
		case VECTORPRODUCT:
			break;
		case VEC_FUNCTION:
			break;
		case ZETA:
			break;
		default:
			break;
		
		}

		App.error("unhandled operation in derivative() (no CAS version): "
				+ operation.toString());

		// undefined
		return wrap(Double.NaN);
	}

	private ExpressionNode wrap(ExpressionValue exp) {
		return exp.wrap();
	}

	@Override
	public ExpressionNode integral(FunctionVariable fv, Kernel kernel) {
		switch (operation) {

		case XCOORD:
		case YCOORD:
		case ZCOORD:
			return new ExpressionNode(kernel, this, Operation.MULTIPLY, fv);

		case POWER:
			// eg x^2
			if (left == fv && !right.contains(fv)) {
				double index = right.evaluateDouble();
				if (!Double.isNaN(index) && !Double.isInfinite(index)) {

					if (Kernel.isZero(index + 1)) {
						return new ExpressionNode(kernel, left, Operation.LOG,
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
						if (Kernel.isEqual(base, 1)) {
							return wrap(fv);
						}

						if (Kernel.isGreater(base, 0)) {
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
						if (Kernel.isEqual(index, -1)) {
							return wrap(left).abs().ln().divide(coeff);
						}
						return wrap(left).power(index + 1).divide(
								coeff * ((index + 1)));
					}

					coeff = getLinearCoefficientDiv(fv, left);

					if (!Double.isNaN(coeff)) {
						if (Kernel.isEqual(index, -1)) {
							// (exp)^-1 -> ln(abs(exp))
							return wrap(left).abs().ln().multiply(coeff);
						}
						return wrap(left).power(index + 1).multiply(
								coeff / ((index + 1)));
					}
				}

			}

			break;

		case NO_OPERATION:
			return wrap(left.integral(fv, kernel));
		case DIVIDE:
			if (right.isNumberValue() && !right.contains(fv)) {
				return wrap(left.integral(fv, kernel)).divide(right);
			}

			if (left.isNumberValue() && !left.contains(fv) && right == fv) {
				// eg 4/x
				return new ExpressionNode(kernel, fv, Operation.LOG, null)
						.multiply(left);
			}
			break;

		case MULTIPLY:
			if (right.isNumberValue() && !right.contains(fv)) {
				return wrap(left.integral(fv, kernel)).multiplyR(right);
			} else if (left.isNumberValue() && !left.contains(fv)) {
				return wrap(right.integral(fv, kernel)).multiplyR(left);
			}

			// can't do by parts without simplification (use Polynomial?)
			break;

		case PLUS:
			return wrap(left.integral(fv, kernel)).plus(right.integral(fv, kernel));
		case MINUS:
			return wrap(left.integral(fv, kernel)).subtract(right.integral(fv, kernel));
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
		case LOGB:

			break;

		case IF_ELSE:
			MyNumberPair np = (MyNumberPair) left;

			np = new MyNumberPair(kernel, np.x, np.y.derivative(fv, kernel));

			return new ExpressionNode(kernel, np, Operation.IF_ELSE,
					right.integral(fv, kernel));

		case IF:

			return new ExpressionNode(kernel, left, Operation.IF,
					right.integral(fv, kernel));

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

		App.error("unhandled operation in integral() (no CAS version): "
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
		if (left == fv) {
			return new ExpressionNode(kernel, left, op, null).multiplyR(i);
		}

		double coeff = getLinearCoefficient(fv, left);

		if (!Double.isNaN(coeff)) {
			return new ExpressionNode(kernel, left, op, null).multiplyR(i)
					.divide(coeff);
		}

		coeff = getLinearCoefficientDiv(fv, left);

		if (!Double.isNaN(coeff)) {
			return new ExpressionNode(kernel, left, op, null).multiply(coeff)
					.multiplyR(i);
		}

		App.debug("not linear integral");
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
		if (ev.isExpressionNode()
				&& (op = ((ExpressionNode) ev).getOperation()).isPlusorMinus()) {
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
		if (ev.isExpressionNode()
				&& ((ExpressionNode) ev).getOperation().equals(
						Operation.MULTIPLY)) {
			ExpressionNode en = (ExpressionNode) ev;
			if (en.left == fv && en.right.isNumberValue()
					&& !en.right.contains(fv)) {
				return ((NumberValue) en.right).getDouble() * factor;
				// return wrap(en.right).multiply(factor);
			} else if (en.right == fv && en.left.isNumberValue()
					&& !en.left.contains(fv)) {
				return ((NumberValue) en.left).getDouble() * factor;
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
		if (ev.isExpressionNode()
				&& (op = ((ExpressionNode) ev).getOperation()).isPlusorMinus()) {
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
		if (ev.isExpressionNode()
				&& ((ExpressionNode) ev).getOperation()
						.equals(Operation.DIVIDE)) {
			ExpressionNode en = (ExpressionNode) ev;
			if (en.left == fv && en.right.isNumberValue()
					&& !en.right.contains(fv)) {
				return ((NumberValue) en.right).getDouble() * factor;
			}
		}

		// not (simple) linear
		return Double.NaN;
	}

	private ExpressionNode wrap(double n) {
		return new MyDouble(kernel, n).wrap();
	}

	/**
	 * @return whether the top-level operation is IF / IF_ELSE
	 */
	public boolean isConditional() {
		return operation == Operation.IF || operation == Operation.IF_ELSE
				|| operation == Operation.IF_LIST;
	}

	public boolean isConditionalOldFashion() {
		return operation == Operation.IF || operation == Operation.IF_ELSE;
	}

	public boolean isConditionalDeep() {
		return isConditional()
				|| (left instanceof ExpressionNode && ((ExpressionNode) left)
						.isConditionalDeep())
				|| (right instanceof ExpressionNode && ((ExpressionNode) right)
						.isConditionalDeep());
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
		return new ExpressionNode(kernel, new MyNumberPair(kernel, this,
				ifBranch), Operation.IF_ELSE, elseBranch);
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
			// case MULTIPLY: return left.evaluateDouble() *
			// right.evaluateDouble();
		case DIVIDE:
			return left.evaluateDouble() / right.evaluateDouble();
		case POWER:
			if (!left.isNumberValue()) {
				return super.evaluateDouble();
			}
			double lt = left.evaluateDouble();
			if (lt < 0
					&& right.isExpressionNode()
					&& ((ExpressionNode) right).getOperation() == Operation.DIVIDE) {
				return ExpressionNodeEvaluator.negPower(lt, right);
			}
			return Math.pow(left.evaluateDouble(), right.evaluateDouble());
		case SIN:
			return Math.sin(left.evaluateDouble());
		case COS:
			return Math.cos(left.evaluateDouble());
		case SQRT:
			return Math.sqrt(left.evaluateDouble());
		default:
			break;
		}
		// TODO: evaluate basic operations here, but make sure errors a thwown
		// wehen necessary
		return super.evaluateDouble();
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

	public boolean hasBrackets() {
		return brackets;
	}

	public void setBrackets(boolean brackets) {
		this.brackets = brackets;
	}

	public ExpressionNode apply(Operation operation2) {
		return new ExpressionNode(kernel, this, operation2, null);
	}

	public ExpressionNode apply(Operation operation2, ExpressionValue arg) {
		return new ExpressionNode(kernel, this, operation2, arg);
	}

	/**
	 * @return whether this contains FVar that is not part of equation or list
	 */
	public boolean containsFreeFunctionVariable(String name) {
		return checkForFreeVars(left, name)
				|| (right != null && checkForFreeVars(right, name))
				|| (operation == Operation.FUNCTION_NVAR
						&& right instanceof MyList && ((ValidExpression) right)
							.containsFunctionVariable(name));
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
		return false;
	}

	@Override
	public boolean evaluatesToNumber(boolean def) {
		if (operation == Operation.RANDOM || operation == Operation.XCOORD
				|| operation == Operation.YCOORD
				|| operation == Operation.ZCOORD || operation == Operation.ABS
				|| operation == Operation.ARG || operation == Operation.ALT) {
			return true;
		}
		if (this.isLeaf() || Operation.isSimpleFunction(this.operation)) {
			return left.evaluatesToNumber(def);
		}
		// both numbers
		if (right != null && left.evaluatesToNumber(def)
				&& right.evaluatesToNumber(def)) {
			return true;
		}
		// number (*) NaN
		if (right != null && left.evaluatesToNumber(def)
				&& !right.evaluatesToNumber(def)) {
			return false;
		}
		// NaN (*) number
		if (right != null && left.evaluatesToNumber(def)
				&& !right.evaluatesToNumber(def)) {
			return this.operation == Operation.POWER;
		}
		// NaN (*) NaN
		if (right != null && !left.evaluatesToNumber(def)
				&& !right.evaluatesToNumber(def)) {
			if (operation == Operation.PLUS || operation == Operation.MINUS) {
				return false;
			}
		}

		return def;
	}

	public HashSet<GeoElement> getUnconditionalVars() {
		// TODO Auto-generated method stub
		if (!this.isConditionalDeep()) {
			return null;
		}
		if (leaf) {
			return left.getVariables();
		}
		if (isConditional()) {
			return new HashSet<GeoElement>();
		}
		HashSet<GeoElement> leftVars = left.getVariables();
		HashSet<GeoElement> rightVars = right.getVariables();
		if (leftVars == null) {
			return rightVars;
		} else if (rightVars == null) {
			return leftVars;
		} else {
			leftVars.addAll(rightVars);
			return leftVars;
		}
	}
	
	public void getFraction(ExpressionValue[] parts, boolean expandPlus){
		ExpressionValue numL, numR, denL = null, denR = null;
		if(left instanceof ExpressionNode){
			((ExpressionNode) left).getFraction(parts, expandPlus);
			numL = parts[0];
			denL = parts[1];
		}else{
			numL = left;
		}
		
		if(right instanceof ExpressionNode){
			((ExpressionNode) right).getFraction(parts, expandPlus);
			numR = parts[0];
			denR = parts[1];
		}else{
			numR = right;
		}
		switch(operation){
		case MULTIPLY:
			parts[0] = numL.wrap().multiply(numR);
			parts[1] = multiplyCheck(denR, denL);
			return;
		case DIVIDE:
			parts[0] = multiplyCheck(denR,numL);
			parts[1] = multiplyCheck(denL,numR);
			return;
		case PLUS:
			if(expandPlus){
				parts[0] = multiplyCheck(denR,numL).wrap().plus(multiplyCheck(denL,numR));
				parts[1] = multiplyCheck(denR,denL);
				return;
			}
		case MINUS:
			if(expandPlus){
				parts[0] = multiplyCheck(denR,numL).wrap().subtract(multiplyCheck(denL,numR));
				parts[1] = multiplyCheck(denR,denL);
				return;
			}
		default:
			parts[0] = this;
			parts[1] = null;
			return;
		}
		
	}

	private ExpressionValue multiplyCheck(ExpressionValue denR,
			ExpressionValue denL) {
		return denL == null ? denR : (denR== null ? denL : denL.wrap().multiply(denR));
	}


	public static ExpressionValue multiplySpecial(ExpressionValue ret,
			ExpressionValue f, Kernel kernel, boolean giacParsing) {
		String leftImg;
		App app = kernel.getApplication();

		// sin x in GGB is function application if "sin" is not a variable
		if (ret instanceof Variable) {
			leftImg = ret.toString(StringTemplate.defaultTemplate);
			Operation op = app.getParserFunctions().get(leftImg, 1);
			if (op != null && kernel.lookupLabel(leftImg) == null
					&& !"x".equals(leftImg) && !"y".equals(leftImg)
					&& !"z".equals(leftImg)) {
				return new ExpressionNode(kernel, f, op, null);

			}
			// x * sin x in GGB is function applied on the right if "sin" is not
			// a variable
		} else if (ret instanceof ExpressionNode
				&& ((ExpressionNode) ret).getOperation() == Operation.POWER
				&& ((ExpressionNode) ret).getLeft() instanceof Variable) {
			leftImg = ((ExpressionNode) ret).getLeft().toString(
					StringTemplate.defaultTemplate);
			Operation op = app.getParserFunctions().get(leftImg, 1);
			if (op != null && kernel.lookupLabel(leftImg) == null
					&& !"x".equals(leftImg) && !"y".equals(leftImg)
					&& !"z".equals(leftImg)) {
				ExpressionValue exponent = ((ExpressionNode) ret).getRight()
						.unwrap();
				if (exponent.isConstant()
						&& Kernel.isEqual(-1,
 exponent.evaluateDouble())) {
					return kernel.inverseTrig(op, f);
				}
				return new ExpressionNode(kernel, f, op, null)
.power(exponent);

			}
			// a * b * f -- check if b*f needs special handling
		} else if (ret instanceof ExpressionNode
				&& ((ExpressionNode) ret).getOperation() == Operation.MULTIPLY
) {
			ExpressionValue bf = multiplySpecial(
					((ExpressionNode) ret).getRight(), f, kernel, giacParsing);
			return bf == null ? null : new ExpressionNode(kernel,
					((ExpressionNode) ret).getLeft(), Operation.MULTIPLY, bf);
		}

		if (giacParsing) {
			// (a)(b) in Giac is function application
			if (ret instanceof Variable) {
				ret = new Command(kernel,
						ret.toString(StringTemplate.defaultTemplate), true,
						true);
				((Command) ret).addArgument(f.wrap());
				return ret;
				// c*(a)(b) in Giac: function applied on right subtree
			}
		}
		return null;
	}
}
