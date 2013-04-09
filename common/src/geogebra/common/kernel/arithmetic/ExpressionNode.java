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

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Traversing.Replacer;
import geogebra.common.kernel.arithmetic3D.Vector3DValue;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoDummyVariable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.main.MyError;
import geogebra.common.plugin.Operation;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Tree node for expressions like "3*a - b/5"
 * 
 * @author Markus
 */
public class ExpressionNode extends ValidExpression implements
ExpressionNodeConstants, ReplaceChildrenByValues {

	private App app;
	private Kernel kernel;
	private ExpressionValue left, right;
	private Operation operation = Operation.NO_OPERATION;
	private boolean forceVector = false, forcePoint = false,
			forceFunction = false;
	/** true if this holds text and the text is in LaTeX format */
	public boolean holdsLaTeXtext = false;

	/** for leaf mode */
	public boolean leaf = false;

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
		app = kernel.getApplication();

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
		app = kernel.getApplication();

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
		app = node.app;

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
		//important to check for commands before we call isConstant as that might
		//result in evaluation, FunctionNVar important because of FunctionExpander
		else if (ev.isPolynomialInstance() || (ev instanceof Command) || ev.isConstant() || ev instanceof FunctionNVar
				) {
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
				if (eval.isNumberValue()) {
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
				if (eval.isNumberValue()) {
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
	public final void resolveVariables(boolean forEquation) {
		doResolveVariables(forEquation);
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
		}
	}

	private void doResolveVariables(boolean forEquation) {
		// resolve left wing
		if (left.isVariable()) {
			left = ((Variable) left).resolveAsExpressionValue(forEquation);
		} else {
			left.resolveVariables(forEquation);
		}

		// resolve right wing
		if (right != null) {
			if (right.isVariable()) {
				right = ((Variable) right).resolveAsExpressionValue(forEquation);
			} else {
				right.resolveVariables(forEquation);
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
	 * @return true if there is at least one Polynomial in the tree
	 */
	public boolean includesPolynomial() {
		return getPolynomialVars().size() > 0;
	}

	/**
	 * Returns all polynomial variables (x, y, and/or z) in this tree as a list.
	 * 
	 * @return list with all variables as Strings
	 */
	public TreeSet<String> getPolynomialVars() {
		TreeSet<String> vars = new TreeSet<String>();
		getPolynomialVars(vars);
		return vars;
	}

	/**
	 * Adds all polynomial variables (x, y, and/or z) in this tree to vars.
	 * 
	 * @param vars
	 *            the set to add all variables as Strings
	 */
	private void getPolynomialVars(Set<String> vars) {
		if (left.isExpressionNode()) {
			((ExpressionNode) left).getPolynomialVars(vars);
		} else if (left.isPolynomialInstance()) {
			vars.add(left.toString(StringTemplate.defaultTemplate));
		}else if (left instanceof MyVecNode) {
			((MyVecNode)left).getX().wrap().getPolynomialVars(vars);
			((MyVecNode)left).getY().wrap().getPolynomialVars(vars);
		}

		if (right != null) {
			if (right.isExpressionNode()) {
				((ExpressionNode) right).getPolynomialVars(vars);
			} else if (right.isPolynomialInstance()) {
				vars.add(right.toString(StringTemplate.defaultTemplate));
				// changed to avoid deepcopy in MyList.getMyList() eg
				// Sequence[f(Element[GP,k]),k,1,NP]
				// } else if (right.isListValue()){ //to get polynomial vars in
				// GeoFunctionNVar
				// MyList list=((ListValue)right).getMyList();
			} else if (right instanceof MyList) { // to get polynomial
				// vars in
				// GeoFunctionNVar
				MyList list = (MyList) right;
				for (int i = 0; i < list.size(); i++) {
					ExpressionValue elem = list.getListElement(i);
					if (elem.isExpressionNode()) {
						((ExpressionNode) elem).getPolynomialVars(vars);
					} else if (elem.isPolynomialInstance()) {
						vars.add(elem.toString(StringTemplate.defaultTemplate));
					}
				}
			}
			else if (right instanceof MyVecNode) {
				((MyVecNode)right).getX().wrap().getPolynomialVars(vars);
				((MyVecNode)right).getY().wrap().getPolynomialVars(vars);
			}
		}
	}

	/**
	 * Returns whether this ExpressionNode should evaluate to a GeoVector. This
	 * method returns true when all GeoElements in this tree are GeoVectors and
	 * there are no other constanct VectorValues (i.e. constant points)
	 * 
	 * @return true if this should evaluate to GeoVector
	 */
	public boolean shouldEvaluateToGeoVector() {
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
	 * Replaces all Polynomials in tree by function variable
	 * 
	 * @param x
	 *            replacement variable
	 * 
	 * @return number of replacements done
	 */
	protected int replacePolynomials(FunctionVariable x) {
		int replacements = 0;

		// left tree
		if (left.isExpressionNode()) {
			replacements += ((ExpressionNode) left).replacePolynomials(x);
		} else if (left instanceof MyList) {
			replacements += ((MyList) left).replacePolynomials(x);
		} else if (left.isPolynomialInstance()
				&& x.toString(StringTemplate.defaultTemplate).equals(
						left.toString(StringTemplate.defaultTemplate))) {
			left = x;
			replacements++;
		}

		// right tree
		if (right != null) {
			if (right.isExpressionNode()) {
				replacements += ((ExpressionNode) right).replacePolynomials(x);
			} else if (right instanceof MyList) {
				replacements += ((MyList) right).replacePolynomials(x);
			} else if (right.isPolynomialInstance()
					&& x.toString(StringTemplate.defaultTemplate).equals(
							right.toString(StringTemplate.defaultTemplate))) {
				right = x;
				replacements++;
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
					&& ((ExpressionNode) left).operation == Operation.MULTIPLY_OR_FUNCTION) {
				right = new ExpressionNode(kernel,
						((ExpressionNode) left).getRight(), Operation.POWER,
						right);
				left = ((ExpressionNode) left).getLeft();
				operation = Operation.MULTIPLY;
			}
			break;
		case FACTORIAL:
			if (left.isExpressionNode()
					&& ((ExpressionNode) left).operation == Operation.MULTIPLY_OR_FUNCTION) {
				right = new ExpressionNode(kernel,
						((ExpressionNode) left).getRight(),
						Operation.FACTORIAL, null);
				left = ((ExpressionNode) left).getLeft();
				operation = Operation.MULTIPLY;
			}
		case SQRT_SHORT:
			if (left.isExpressionNode()
					&& ((ExpressionNode) left).operation == Operation.MULTIPLY_OR_FUNCTION) {
				right = ((ExpressionNode) left).getRight();
				left = new ExpressionNode(kernel,
						((ExpressionNode) left).getLeft(), Operation.SQRT, null);
				operation = Operation.MULTIPLY;
			}
		}

		return undecided.size();
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue ev = t.process(this);
		if(ev!=this)
			return ev;
		left = left.traverse(t);
		if (right != null)
			right = right.traverse(t);
		return ev;
	}

	@Override
	public boolean inspect(Inspecting t){
		return t.check(this) || left.inspect(t) || (right!=null && right.inspect(t));
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
	 * @return true when function variable is found in this expression tree.
	 */
	final public boolean containsFunctionVariable() {
		if ((left instanceof FunctionVariable)
				|| (right instanceof FunctionVariable)) {
			return true;
		}

		if ((left instanceof ExpressionNode)
				&& ((ExpressionNode) left).containsFunctionVariable()) {
			return true;
		}
		if ((left instanceof MyVecNode)
				&& ((MyVecNode) left).containsFunctionVariable()) {
			return true;
		}
		if ((right instanceof ExpressionNode)
				&& ((ExpressionNode) right).containsFunctionVariable()) {
			return true;
		}
		if ((right instanceof MyVecNode)
				&& ((MyVecNode) right).containsFunctionVariable()) {
			return true;
		}
		return false;
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
	 */
	protected final void makePolynomialTree(Equation equ) {

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
										.includesPolynomial());
							}
							// we may only make polynomial trees after replacement
							// en.makePolynomialTree(equ);
							ev = en;
						} else if (list.getListElement(i)
								.isPolynomialInstance()) {
							equ.setFunctionDependent(true);
						}
						expr = expr.replace(
								func.getFunctionVariables()[i], ev).wrap();
					}
				} else {
					throw new MyError(app.getLocalization(),
							new String[] { "IllegalArgumentNumber" });
				}
				expr.makePolynomialTree(equ);
				left = expr.left;
				right = expr.right;
				operation = expr.getOperation();
			}
		} else if (operation == Operation.FUNCTION) {
			if (left instanceof GeoFunction) {
				Function func = ((Functional) left).getFunction();
				ExpressionNode expr = func.getExpression().getCopy(kernel);
				if (right instanceof ExpressionNode) {
					if (!equ.isFunctionDependent()) {
						equ.setFunctionDependent(((ExpressionNode) right)
								.includesPolynomial());
					}
					// we may only make polynomial trees after replacement
					// ((ExpressionNode) right).makePolynomialTree(equ);
				} else if (right.isPolynomialInstance()) {
					equ.setFunctionDependent(true);
				}
				expr = expr.replace(func.getFunctionVariable(), right).wrap();
				expr.makePolynomialTree(equ);
				left = expr.left;
				right = expr.right;
				operation = expr.getOperation();
			}
		}
		if (!polynomialOperation(operation)) {
			left = new Polynomial(kernel, new Term( new ExpressionNode(
					kernel, left, operation, right), ""));
			leaf = true;
			operation = Operation.NO_OPERATION;
			right = null;
			return;
		}

		// transfer left subtree
		if (left.isExpressionNode()) {
			((ExpressionNode) left).makePolynomialTree(equ);
		} else if (!(left.isPolynomialInstance())) {
			left = new Polynomial(kernel, new Term( left, ""));
		}

		// transfer right subtree
		if (right != null) {
			if (right.isExpressionNode()) {
				((ExpressionNode) right).makePolynomialTree(equ);
			} else if (right instanceof MyList) {
				MyList list = (MyList) right;
				for (int i = 0; i < list.size(); i++) {
					ExpressionValue ev = list.getListElement(i);
					if (ev instanceof ExpressionNode) {
						((ExpressionNode) ev).makePolynomialTree(equ);
					}
				}
			} else if (!(right.isPolynomialInstance())) {
				right = new Polynomial(kernel, new Term( right, ""));
			}
		}
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
		}
		return false;
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
	final public boolean isVectorValue() {
		if (forcePoint) {
			return false;
		}
		if (forceVector) {
			return true;
		}

		return shouldEvaluateToGeoVector();
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
	final public String getCASstring(StringTemplate tpl, boolean symbolic) {
		String ret = printCASstring(symbolic, tpl);
		return ret;
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

	/*
	 * splits a string up for editing with the Text Tool adapted from
	 * printCASstring()
	 */

	private String printCASstring(boolean symbolic, StringTemplate tpl) {
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
					ret = ((ExpressionNode) left).printCASstring(symbolic, tpl);
				} else {
					ret = symbolic ? left.toString(tpl) : left
							.toValueString(tpl);
				}
			}

			// STANDARD case: no leaf
			else {
				// expression node

				// we could group the factors of all possible numerators here.
				//we won't do that. http://www.geogebra.org/forum/viewtopic.php?f=22&t=29017
				//numerGroup();

				String leftStr = null, rightStr = null;
				if (symbolic && left.isGeoElement()) {
					leftStr = ((GeoElement) left).getLabel(tpl);
				} else if (left.isExpressionNode()) {
					leftStr = ((ExpressionNode) left).printCASstring(symbolic,
							tpl);
				} else {
					leftStr = symbolic ? left.toString(tpl) : left
							.toValueString(tpl);
				}

				if (right != null) {
					if (symbolic && right.isGeoElement()) {
						rightStr = ((GeoElement) right).getLabel(tpl);
					} else if (right.isExpressionNode()) {
						rightStr = ((ExpressionNode) right).printCASstring(
								symbolic, tpl);
					} else {
						rightStr = symbolic ? right.toString(tpl) : right
								.toValueString(tpl);
					}
				}
				ret = operationToString(leftStr, rightStr, !symbolic, tpl);
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
	 * Decide whether the current expression value must be expanded
	 * for OpenGeoProver. This code helps both the Wu and the Area method.
	 * @param ev left or right side of the expression
	 * @return if expansion is required
	 */
	final private boolean expandForOGP(ExpressionValue ev) {
		// The following types of operations and GeoElements are supported.
		// See also the OGP code for the available (parsable) expressions.
		if (operation.equals(Operation.EQUAL_BOOLEAN) && ev instanceof GeoSegment)
			return false; // don't expand "AreEqual[Segment[X,Y],Segment[Z,W]]" format expressions
		return ((operation.equals(Operation.EQUAL_BOOLEAN) || operation.equals(Operation.DIVIDE)
				|| operation.equals(Operation.MULTIPLY) || operation.equals(Operation.MINUS)
				|| operation.equals(Operation.PLUS) || operation.equals(Operation.POWER))
				&& (ev instanceof GeoSegment || ev instanceof GeoPolygon || ev instanceof GeoNumeric));
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
						&&  expandForOGP(right)) {
					rightStr = ((GeoElement) right).getCommandDescription(tpl);
				} else
					rightStr = ((GeoElement) right).getLabel(tpl);
			} else {
				rightStr = right.toString(tpl);
			}
		}
		return operationToString(leftStr, rightStr, false, tpl);
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

		return operationToString(leftStr, rightStr, true, tpl);
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

		return operationToString(leftStr, rightStr, true, tpl);
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

				return checkMathML(ret, tpl);
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
				rightStr = rightStr.substring(3, rightStr.length() - 3);
			}
		}

		// build latex string
		ret = operationToString(leftStr, rightStr, !symbolic, tpl);

		return checkMathML(ret, tpl);
	}

	/**
	 * make sure string wrapped in MathML if necessary eg <ci>x</ci>
	 */
	private static String checkMathML(String str, StringTemplate tpl) {
		if (tpl.hasType(StringType.MATHML) && str.charAt(0) != '<') {
			return "<ci>" + str + "</ci>";
		}

		return str;
	}

	/**
	 * Returns a string representation of this node. Note: STRING_TYPE is used
	 * for LaTeX, MathPiper, Jasymca conform output, valueForm is used by
	 * toValueString(), forLaTeX is used for LaTeX output
	 * 
	 */
	final private String operationToString(String leftStr, String rightStr,
			boolean valueForm, StringTemplate tpl) {
		Localization loc = app.getLocalization();
		ExpressionValue leftEval;
		StringBuilder sb = new StringBuilder();

		StringType stringType = tpl.getStringType();

		switch (operation) {
		case NOT:

			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<not/>", leftStr, null);
			}else if (stringType.equals(StringType.MPREDUCE)) {
				sb.append("snot(");
				sb.append(leftStr);
				sb.append(')');
			} else {

				switch (stringType) {
				case MATHML:

					break;
				case LATEX:
					sb.append("\\neg ");
					break;

				case LIBRE_OFFICE:
					sb.append("neg ");
					break;

				case MATH_PIPER:
					sb.append("Not ");
					break;

				default:
					sb.append(strNOT);
				}
				if (left.isLeaf()) {
					sb.append(leftStr);
				} else {
					sb.append(leftBracket(stringType));
					sb.append(leftStr);
					sb.append(rightBracket(stringType));
				}
			}
			break;

		case OR:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<or/>", leftStr, rightStr);
			} else if (stringType.equals(StringType.MPREDUCE)) {
				appendOp(sb,"sor", leftStr, rightStr);
			}else {
				append(sb, leftStr, left, operation, stringType);
				sb.append(' ');

				switch (stringType) {
				case LATEX:
					if (kernel.isInsertLineBreaks()) {
						sb.append("\\-");
					}
					sb.append("\\vee");
					break;				
				case LIBRE_OFFICE:
					sb.append("or");
					break;

				case MATH_PIPER:
					sb.append("Or");
					break;

				case MPREDUCE:
					sb.append("or ");
					break;

				default:
					sb.append(strOR);
				}

				sb.append(' ');
				append(sb, rightStr, right, operation, stringType);
				// sb.append(rightStr);
			}
			break;
		case AND_INTERVAL:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<and/>", leftStr, rightStr);
				break;
			}else if (stringType.equals(StringType.MPREDUCE)) {
				appendOp(sb,"sand", leftStr, rightStr);
				break;
			} else {
				if(right.isExpressionNode()){
					sb.append(getLeftTree().printCASstring(!valueForm, tpl));
					switch(((ExpressionNode)right).getOperation()){
					case LESS:appendLessSign(sb,stringType);break;
					case LESS_EQUAL:appendLeqSign(sb,stringType);break;
					case GREATER:appendGreaterSign(sb,stringType);break;
					case EQUAL_BOOLEAN:appendEqualSign(sb,stringType);break;
					case NOT_EQUAL:appendNotEqualSign(sb,stringType);break;
					case GREATER_EQUAL:appendGeqSign(sb,stringType);break;
					case IS_SUBSET_OF:appendSubsetSign(sb,stringType);break;
					case IS_SUBSET_OF_STRICT:appendStrictSubsetSign(sb,stringType);break;
					case PARALLEL:appendParallelSign(sb,stringType);break;
					case PERPENDICULAR:appendPerpSign(sb,stringType);break;
					default:App.debug(((ExpressionNode)right).getOperation()+" invalid in chain");
					}
					sb.append(((ExpressionNode)right).getRightTree().printCASstring(!valueForm, tpl));
					break;
				}
			}

		case AND:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<and/>", leftStr, rightStr);
			}else if (stringType.equals(StringType.MPREDUCE)) {
				appendOp(sb,"sand", leftStr, rightStr);
			} else {
				append(sb, leftStr, left, operation, stringType);

				sb.append(' ');
				switch (stringType) {
				case LATEX:
					if (kernel.isInsertLineBreaks()) {
						sb.append("\\-");
					}
					sb.append("\\wedge");
					break;

				case LIBRE_OFFICE:
					sb.append("and");
					break;

				case MATH_PIPER:
					sb.append("And");
					break;

				case MPREDUCE:
					sb.append("and ");
					break;

				default:
					sb.append(strAND);
				}
				sb.append(' ');

				append(sb, rightStr, right, operation, stringType);
			}
			break;

		case IMPLICATION:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<implies/>", leftStr, rightStr);
			}else if (stringType.equals(StringType.MPREDUCE)) {
				appendOp(sb,"simplies", leftStr, rightStr);
			} else {
				if (stringType != StringType.MPREDUCE)
					append(sb, leftStr, left, operation, stringType);

				sb.append(' ');
				switch (stringType) {
				case LATEX:
					if (kernel.isInsertLineBreaks()) {
						sb.append("\\-");
					}
					sb.append("\\to");
					break;

				case LIBRE_OFFICE:
					sb.append("toward"); //don't know if it is correct TAM 5/28/2012
					break;

				default:
					sb.append(strIMPLIES);
				}
				sb.append(' ');

				if (stringType != StringType.MPREDUCE)
					append(sb, rightStr, right, operation, stringType);
			}
			break;

		case EQUAL_BOOLEAN:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<eq/>", leftStr, rightStr);
			} else if (stringType.equals(StringType.OGP)) {
				sb.append("AreEqual[" + leftStr + "," + rightStr + "]");
			} else if (stringType.equals(StringType.MPREDUCE)) {
				appendOp(sb,"sequal", leftStr, rightStr);
			}else {
				append(sb, leftStr, left, operation, stringType);
				// sb.append(leftStr);
				appendEqualSign(sb,stringType);
				append(sb, rightStr, right, operation, stringType);
				// sb.append(rightStr);
			}
			break;

		case NOT_EQUAL:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<neq/>", leftStr, rightStr);
			}else if (stringType.equals(StringType.MPREDUCE)) {
				appendOp(sb,"sunequal", leftStr, rightStr);
			} else {
				append(sb, leftStr, left, operation, stringType);
				// sb.append(leftStr);
				appendNotEqualSign(sb,stringType);
				append(sb, rightStr, right, operation, stringType);
				// sb.append(rightStr);
			}
			break;

		case IS_ELEMENT_OF:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<in/>", leftStr, rightStr);
			} else {
				append(sb, leftStr, left, operation, stringType);
				// sb.append(leftStr);
				sb.append(' ');
				switch (stringType) {
				case LATEX:
					if (kernel.isInsertLineBreaks()) {
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
				append(sb, rightStr, right, operation, stringType);
				// sb.append(rightStr);
			}
			break;

		case IS_SUBSET_OF:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<subset/>", leftStr, rightStr);
			} else {
				append(sb, leftStr, left, operation, stringType);
				// sb.append(leftStr);
				appendSubsetSign(sb,stringType);
				append(sb, rightStr, right, operation, stringType);
				// sb.append(rightStr);
			}
			break;

		case IS_SUBSET_OF_STRICT:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<prsubset/>", leftStr, rightStr);
			} else {
				append(sb, leftStr, left, operation, stringType);
				// sb.append(leftStr);
				appendStrictSubsetSign(sb,stringType);
				append(sb, rightStr, right, operation, stringType);
				// sb.append(rightStr);
			}
			break;

		case SET_DIFFERENCE:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<setdiff/>", leftStr, rightStr);
			} else {
				append(sb, leftStr, left, operation, stringType);
				// sb.append(leftStr);
				sb.append(' ');
				switch (stringType) {
				case LATEX:
					if (kernel.isInsertLineBreaks()) {
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
				if(right.isExpressionNode() && right.wrap().getOperation()==Operation.SET_DIFFERENCE){
					sb.append(leftBracket(stringType));
					sb.append(rightStr);
					sb.append(rightBracket(stringType));
				}else{
					append(sb, rightStr, right, operation, stringType);
				}
				// sb.append(rightStr);
			}
			break;

		case LESS:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<lt/>", leftStr, rightStr);
			} else if (stringType.equals(StringType.MPREDUCE)) {
				appendOp(sb,"sless", leftStr, rightStr);
			}else {
				append(sb, leftStr, left, operation, stringType);
				// sb.append(leftStr);
				appendLessSign(sb,stringType);
				append(sb, rightStr, right, operation, stringType);
				// sb.append(rightStr);
			}
			break;

		case GREATER:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<gt/>", leftStr, rightStr);
			}else if (stringType.equals(StringType.MPREDUCE)) {
				appendOp(sb,"sgreater", leftStr, rightStr);
			} else {
				append(sb, leftStr, left, operation, stringType);
				// sb.append(leftStr);
				appendGreaterSign(sb,stringType);
				append(sb, rightStr, right, operation, stringType);
				// sb.append(rightStr);
			}
			break;

		case LESS_EQUAL:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<leq/>", leftStr, rightStr);
			} else if (stringType.equals(StringType.MPREDUCE)) {
				appendOp(sb,"slessequal", leftStr, rightStr);
			}else {
				append(sb, leftStr, left, operation, stringType);
				// sb.append(leftStr);
				appendLeqSign(sb,stringType);
				append(sb, rightStr, right, operation, stringType);
				// sb.append(rightStr);
			}
			break;

		case GREATER_EQUAL:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<qeq/>", leftStr, rightStr);
			} else if (stringType.equals(StringType.MPREDUCE)) {
				appendOp(sb,"sgreaterequal", leftStr, rightStr);
			} else  {
				append(sb, leftStr, left, operation, stringType);
				// sb.append(leftStr);
				appendGeqSign(sb,stringType);
				append(sb, rightStr, right, operation, stringType);
				// sb.append(rightStr);
			}
			break;

		case PARALLEL:
			if (stringType.equals(StringType.OGP)) {
				sb.append("AreParallel[" + leftStr + "," + rightStr + "]");
				break;
			}
			append(sb, leftStr, left, operation, stringType);
			// sb.append(leftStr);
			appendParallelSign(sb,stringType);
			append(sb, rightStr, right, operation, stringType);
			// sb.append(rightStr);
			break;

		case PERPENDICULAR:
			if (stringType.equals(StringType.OGP)) {
				sb.append("ArePerpendicular[" + leftStr + "," + rightStr + "]");
				break;
			}
			append(sb, leftStr, left, operation, stringType);
			// sb.append(leftStr);
			appendPerpSign(sb,stringType);
			append(sb, rightStr, right, operation, stringType);
			// sb.append(rightStr);
			break;

		case VECTORPRODUCT:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<vectorproduct/>", leftStr, rightStr);
			} else if (stringType.equals(StringType.MPREDUCE)) {
				sb.append("mycross(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(')');

			} else {
				append(sb, leftStr, left, operation, stringType);
				// sb.append(leftStr);
				sb.append(' ');
				switch (stringType) {
				case LATEX:
					if (kernel.isInsertLineBreaks()) {
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
				append(sb, rightStr, right, operation, stringType);
				// sb.append(rightStr);
			}
			break;

		case PLUS:
			switch (stringType) {
			case MATHML:
				mathml(sb, "<plus/>", leftStr, rightStr);
				break;
			case JASYMCA:
			case MATH_PIPER:
			case GIAC:
				sb.append('(');
				sb.append(leftStr);
				sb.append(") + (");
				sb.append(rightStr);
				sb.append(')');
				break;

			case MPREDUCE:
				sb.append("addition(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(')');
				break;

			default:
				// check for 0
				if (valueForm) {
					if (isEqualString(left, 0, !valueForm)) {						
						append(sb, rightStr, right, operation, stringType);
						break;
					} else if (isEqualString(right, 0, !valueForm)) {
						append(sb, leftStr, left, operation, stringType);
						break;
					}
				}

				if (left instanceof Equation) {
					sb.append(leftBracket(stringType));
					sb.append(leftStr);
					sb.append(rightBracket(stringType));
				} else {
					sb.append(leftStr);
				}

				// we need parantheses around right text
				// if right is not a leaf expression or
				// it is a leaf GeoElement without a label (i.e. it is
				// calculated somehow)
				if (left.isTextValue()
						&& (!right.isLeaf() || (right.isGeoElement() && !((GeoElement) right)
								.isLabelSet()))) {
					if (stringType.equals(StringType.LATEX)
							&& kernel.isInsertLineBreaks()) {
						sb.append(" \\-+ ");
					} else {
						sb.append(" + ");
					}
					sb.append(leftBracket(stringType));
					sb.append(rightStr);
					sb.append(rightBracket(stringType));
				} else {
					if (rightStr.charAt(0) == '-') { // convert + - to -
						if (stringType.equals(StringType.LATEX)
								&& kernel.isInsertLineBreaks()) {
							sb.append(" \\-- ");
						} else {
							sb.append(" - ");
						}
						sb.append(rightStr.substring(1));
					} else if (rightStr
							.startsWith(Unicode.RightToLeftUnaryMinusSign)) { // Arabic
						// convert
						// +
						// -
						// to
						// -
						if (stringType.equals(StringType.LATEX)
								&& kernel.isInsertLineBreaks()) {
							sb.append(" \\-- ");
						} else {
							sb.append(" - ");
						}
						sb.append(rightStr.substring(3));
					} else {
						if (stringType.equals(StringType.LATEX)
								&& kernel.isInsertLineBreaks()) {
							sb.append(" \\-+ ");
						} else {
							sb.append(" + ");
						}
						sb.append(rightStr);
					}
				}
				break;
			}
			break;

		case MINUS:
			switch (stringType) {
			case MATHML:
				mathml(sb, "<minus/>", leftStr, rightStr);
				break;
			case JASYMCA:
			case MATH_PIPER:
			case GIAC:
				sb.append('(');
				sb.append(leftStr);
				sb.append(") - (");
				sb.append(rightStr);
				sb.append(')');
				break;

			case MPREDUCE:
				sb.append("subtraction(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(')');
				break;

			default:
				if (left instanceof Equation) {
					sb.append(leftBracket(stringType));
					sb.append(leftStr);
					sb.append(rightBracket(stringType));
				} else {
					sb.append(leftStr);
				}

				// check for 0 at right
				if (valueForm
						&& rightStr
						.equals(loc.unicodeZero + "")) {
					break;
				}

				if (right.isLeaf()
						|| (opID(right) >= Operation.MULTIPLY.ordinal())) { // not
					// +,
					// -

					if (rightStr.charAt(0) == '-') { // convert - - to +
						if (stringType.equals(StringType.LATEX)
								&& kernel.isInsertLineBreaks()) {
							sb.append(" \\-+ ");
						} else {
							sb.append(" + ");
						}
						sb.append(rightStr.substring(1));
					} else if (rightStr
							.startsWith(Unicode.RightToLeftUnaryMinusSign)) { // Arabic
						// convert
						// -
						// -
						// to
						// +
						if (stringType.equals(StringType.LATEX)
								&& kernel.isInsertLineBreaks()) {
							sb.append(" \\-+ ");
						} else {
							sb.append(" + ");
						}
						sb.append(rightStr.substring(3));
					} else {
						if (stringType.equals(StringType.LATEX)
								&& kernel.isInsertLineBreaks()) {
							sb.append(" \\-- ");
						} else {
							sb.append(" - ");
						}
						sb.append(rightStr);
					}
				} else {
					// fix for changing height in Algebra View plus / minus
					if (stringType.equals(StringType.LATEX)
							&& kernel.isInsertLineBreaks()) {
						sb.append(" \\-- ");
					} else {
						sb.append(" - ");
					}
					sb.append(leftBracket(stringType));
					sb.append(rightStr);
					sb.append(rightBracket(stringType));
				}
				break;
			}
			break;

		case MULTIPLY:
			switch (stringType) {

			case MATHML:
				mathml(sb, "<times/>", leftStr, rightStr);
				break;
			default:
				// check for 1 at left
				if (isEqualString(left, 1, !valueForm)) {
					append(sb, rightStr, right, operation, stringType);
					break;
				}
				// check for 1 at right
				else if (isEqualString(right, 1, !valueForm)) {
					append(sb, leftStr, left, operation, stringType);
					break;
				}

				// removed 0 handling due to problems with functions,
				// e.g 0 * x + 1 becomes 0 + 1 and no longer is a function
				// // check for 0 at left
				// else if (valueForm && isEqualString(left, 0, !valueForm)) {
				// sb.append("0");
				// break;
				// }
				// // check for 0 at right
				// else if (valueForm && isEqualString(right, 0, !valueForm)) {
				// sb.append("0");
				// break;
				// }

				// check for degree sign or 1degree or degree1 (eg for Arabic)
				else if (((rightStr.length() == 2) && (((rightStr.charAt(0) == Unicode.degreeChar) && (rightStr
						.charAt(1) == (loc.unicodeZero + 1))) || ((rightStr
								.charAt(0) == Unicode.degreeChar) && (rightStr
										.charAt(1) == (loc.unicodeZero + 1)))))
										|| rightStr.equals(Unicode.degree)) {

					boolean rtl = app.getLocalization().isRightToLeftDigits(tpl);

					if (rtl) {
						sb.append(Unicode.degree);
					}

					if (!left.isLeaf()) {
						sb.append('('); // needed for eg (a+b)\u00b0
					}
					sb.append(leftStr);
					if (!left.isLeaf()) {
						sb.append(')'); // needed for eg (a+b)\u00b0
					}

					if (!rtl) {
						sb.append(Unicode.degree);
					}

					break;
				}

			case JASYMCA:
			case MATH_PIPER:
			case GIAC:
			case LATEX:
			case LIBRE_OFFICE:

				boolean nounary = true;

				// vector * (matrix * vector) needs brackets; always use brackets for internal templates
				if (!tpl.isPrintLocalizedCommandNames() || (left.isListValue() && right.isVectorValue())) {
					sb.append(leftBracket(stringType));
				}

				// left wing
				if (left.isLeaf()
						|| (opID(left) >= Operation.MULTIPLY.ordinal())) { // not
					// +,
					// -
					if (isEqualString(left, -1, !valueForm)) { // unary minus
						nounary = false;
						sb.append('-');
					} else {
						if (leftStr
								.startsWith(Unicode.RightToLeftUnaryMinusSign)) {
							// brackets needed for eg Arabic digits
							sb.append(Unicode.RightToLeftMark);
							sb.append(leftBracket(stringType));
							sb.append(leftStr);
							sb.append(rightBracket(stringType));
							sb.append(Unicode.RightToLeftMark);
						} else {
							sb.append(leftStr);
						}
					}
				} else {
					sb.append(leftBracket(stringType));
					sb.append(leftStr);
					sb.append(rightBracket(stringType));
				}

				// right wing
				int opIDright = opID(right);
				if (right.isLeaf()
						|| (opIDright >= Operation.MULTIPLY.ordinal())) { // not
					// +,
					// -
					boolean showMultiplicationSign = false;
					boolean multiplicationSpaceNeeded = true;
					if (nounary) {
						switch (stringType) {
						case PGF:
						case PSTRICKS:
						case GEOGEBRA_XML:
						case JASYMCA:
						case MATH_PIPER:
						case GIAC:
							showMultiplicationSign = true;
							break;

						case LIBRE_OFFICE:
						case LATEX:
							// check if we need a multiplication sign, see #414
							// digit-digit, e.g. 3 * 5
							// digit-fraction, e.g. 3 * \frac{5}{2}
							char lastLeft = leftStr
							.charAt(leftStr.length() - 1);
							char firstRight = rightStr.charAt(0);
							showMultiplicationSign =
									// left is digit or ends with }, e.g. exponent,
									// fraction
									(StringUtil.isDigit(lastLeft) || (lastLeft == '}'))
									&&
									// right is digit or fraction
									(StringUtil.isDigit(firstRight) || rightStr
											.startsWith("\\frac"));
							break;

						default: // GeoGebra syntax
							char firstLeft = leftStr.charAt(0);
							lastLeft = leftStr.charAt(leftStr.length() - 1);
							firstRight = rightStr.charAt(0);
							// check if we need a multiplication sign, see #414
							// digit-digit, e.g. 3 * 5
							showMultiplicationSign = Character
									.isDigit(lastLeft)
									&& (StringUtil.isDigit(firstRight)
											// 3*E23AB can't be written 3E23AB
											|| (rightStr.charAt(0) == 'E'));
							// check if we need a multiplication space:
							multiplicationSpaceNeeded = showMultiplicationSign;
							if (!multiplicationSpaceNeeded) {
								// check if we need a multiplication space:
								// it's needed except for number * character,
								// e.g. 23x
								// need to check start and end for eg A1 * A2
								boolean leftIsNumber = left.isLeaf()
										&& (StringUtil.isDigit(firstLeft) || (firstLeft == '-'))
										&& StringUtil.isDigit(lastLeft);

								// check if we need a multiplication space:
								// all cases except number * character, e.g. 3x
								multiplicationSpaceNeeded = showMultiplicationSign
										|| !(leftIsNumber && !Character
												.isDigit(firstRight));
							}
						}

						if (stringType.equals(StringType.LATEX)
								&& kernel.isInsertLineBreaks()) {
							sb.append("\\-");
						}

						if (showMultiplicationSign) {
							sb.append(multiplicationSign(stringType));
						} else if (multiplicationSpaceNeeded) {
							// space instead of multiplication sign
							sb.append(multiplicationSpace(stringType));
						}
					}

					boolean rtlMinus;
					// show parentheses around these cases
					if (((rtlMinus = rightStr
							.startsWith(Unicode.RightToLeftUnaryMinusSign)) || (rightStr
									.charAt(0) == '-')) // 2 (-5) or -(-5)
									|| (!nounary && !right.isLeaf() && (opIDright <= Operation.DIVIDE
									.ordinal() // -(x * a) or -(x / a)
											))
											|| (showMultiplicationSign && stringType
													.equals(StringType.GEOGEBRA))) // 3 (5)
					{
						if (rtlMinus) {
							sb.append(Unicode.RightToLeftMark);
						}
						sb.append(leftBracket(stringType));
						sb.append(rightStr);
						sb.append(rightBracket(stringType));
						if (rtlMinus) {
							sb.append(Unicode.RightToLeftMark);
						}
					} else {
						// -1.0 * 5 becomes "-5"
						sb.append(rightStr);
					}
				} else { // right is + or - tree
					if (nounary) {
						switch (stringType) {
						case PGF:
						case PSTRICKS:
						case GEOGEBRA_XML:
						case JASYMCA:
						case MATH_PIPER:
						case GIAC:
							sb.append(multiplicationSign(stringType));
							break;

						default:
							// space instead of multiplication sign
							sb.append(multiplicationSpace(stringType));
						}
					}
					sb.append(leftBracket(stringType));
					sb.append(rightStr);
					sb.append(rightBracket(stringType));
				}
				
				// vector * (matrix * vector) needs brackets; always use brackets for internal templates
				if (!tpl.isPrintLocalizedCommandNames() || (left.isListValue() && right.isVectorValue())) {
					sb.append(rightBracket(stringType));
				}

				break;

			case MPREDUCE:

				if (isEqualString(left, -1, !valueForm)) {
					sb.append("-(");
					sb.append(rightStr);
					sb.append(')');
				} else {
					sb.append("multiplication(");
					sb.append(leftStr);
					sb.append(",");
					sb.append(rightStr);
					sb.append(")");
					break;
				}


			}
			break;

		case DIVIDE:
			switch (stringType) {
			case MATHML:
				mathml(sb, "<divide/>", leftStr, rightStr);
				break;
			case LATEX:
				if ((leftStr.charAt(0) == '-')
						&& (left.isLeaf() || (left instanceof ExpressionNode && isMultiplyOrDivide((ExpressionNode) left)))) {
					sb.append("-\\frac{");
					sb.append(leftStr.substring(1));
					sb.append("}{");
					sb.append(rightStr);
					sb.append("}");
				} else {

					sb.append("\\frac{");
					sb.append(leftStr);
					sb.append("}{");
					sb.append(rightStr);
					sb.append("}");
				}
				break;
			case LIBRE_OFFICE:
				sb.append("{ ");
				sb.append(leftStr);
				sb.append(" } over { ");
				sb.append(rightStr);
				sb.append(" }");
				break;

			case MPREDUCE:
				sb.append("mydivision(");
				sb.append(leftStr);
				sb.append(",");
				sb.append(rightStr);
				sb.append(')');
				break;

			case JASYMCA:
			case MATH_PIPER:
			case GIAC:
			default:
				// check for 1 in denominator
				if (isEqualString(right, 1, !valueForm)) {
					sb.append(leftStr);
					break;
				}

				// left wing
				// put parantheses around +, -, *
				append(sb, leftStr, left, Operation.DIVIDE, stringType);
				sb.append(" / ");

				// right wing
				append(sb, rightStr, right, Operation.POWER, stringType); // not
				// +,
				// -,
				// *,
				// /
			}
			break;

		case POWER:
			/*
			 * support for sin^2(x) for display, too slow and hacky if
			 * (STRING_TYPE.equals(StringType.GEOGEBRA &&
			 * leftStr.startsWith("sin(")) { //&& rightStr.equals("2")) { int
			 * index; try { index = Integer.parseInt(rightStr); } catch
			 * (NumberFormatException nfe) { index = Integer.MAX_VALUE; }
			 * 
			 * if (index > 0 && index != Integer.MAX_VALUE) { sb.append("sin");
			 * sb.append(Unicode.numberToIndex(index));
			 * sb.append(leftStr.substring(3)); // everying except the "sin"
			 * break; }
			 * 
			 * }//
			 */

			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<power/>", leftStr, rightStr);
			} else {

				// everything else

				boolean finished = false;

				// support for sin^2(x) for LaTeX, eg FormulaText[]
				if (stringType.equals(StringType.LATEX)
						&& left.isExpressionNode()) {
					switch (((ExpressionNode) left).getOperation()) {
					// #1592
					case SIN:
					case COS:
					case TAN:
					case SEC:
					case CSC:
					case COT:
					case SINH:
					case COSH:
					case TANH:
					case SECH:
					case CSCH:
					case COTH:
						int index;
						try {
							index = Integer.parseInt(rightStr);
						} catch (NumberFormatException nfe) {
							index = Integer.MAX_VALUE;
						}

						if ((index > 0) && (index != Integer.MAX_VALUE)) {
							int spaceIndex = leftStr.indexOf(' ');
							sb.append(leftStr.substring(0, spaceIndex));
							sb.append(" ^{");
							sb.append(rightStr);
							sb.append("}");
							sb.append(leftStr.substring(spaceIndex + 1)); // everything
							// except
							// the
							// "\\sin "

							finished = true;

							break;
						}

					default:
						// fall through
					}

					if (finished) {
						break;
					}

				}

				switch (stringType) {
				case MPREDUCE:
					if("e".equals(leftStr)){
						sb.append("exp(");
						sb.append(rightStr);
						sb.append(')');
						break;
					}
					sb.append("mypower(");
					sb.append(leftStr);
					sb.append(",");
					sb.append(rightStr);
					sb.append(')');
					break;
				case LATEX:

					// checks if the basis is leaf and if so
					// omits the brackets
					if (left.isLeaf() && (leftStr.charAt(0) != '-')) {
						sb.append(leftStr);
						break;
					}
					// else fall through

				case JASYMCA:
				case MATH_PIPER:
				case GIAC:


				case LIBRE_OFFICE:
				default:

					/*
					 * removed Michael Borcherds 2009-02-08 doesn't work eg m=1
					 * g(x) = (x - 1)^m (x - 3)
					 * 
					 * 
					 * // check for 1 in exponent if (isEqualString(right, 1,
					 * !valueForm)) { sb.append(leftStr); break; } //
					 */

					// left wing
					if ((leftStr.charAt(0) != '-')
							&& // no unary
							(left.isLeaf() || ((opID(left) > Operation.POWER
									.ordinal()) && (opID(left) != Operation.EXP
									.ordinal())))) { // not +, -, *, /, ^,
						// e^x
						sb.append(leftStr);
					} else {
						sb.append(leftBracket(stringType));
						sb.append(leftStr);
						sb.append(rightBracket(stringType));
					}
					break;
				}

				// right wing
				switch (stringType) {
				case LATEX:
				case LIBRE_OFFICE:
					sb.append('^');

					// add brackets for eg a^b^c -> a^(b^c)
					boolean addParentheses = (right.isExpressionNode() && ((ExpressionNode) right)
							.getOperation().equals(Operation.POWER));

					sb.append('{');
					if (addParentheses) {
						sb.append(leftBracket(stringType));
					}
					sb.append(rightStr);
					if (addParentheses) {
						sb.append(rightBracket(stringType));
					}
					sb.append('}');
					break;

				case MPREDUCE:
					break;
				case JASYMCA:
				case GEOGEBRA_XML:
				case MATH_PIPER:
				case GIAC:

					sb.append('^');
					sb.append('(');
					sb.append(rightStr);
					sb.append(')');
					break;

				default:
					if (right.isLeaf()
							|| ((opID(right) > Operation.POWER.ordinal()) && (opID(right) != Operation.EXP
							.ordinal()))) { // not
						// +,
						// -,
						// *,
						// /,
						// ^,
						// e^x
						// Michael Borcherds 2008-05-14
						// display powers over 9 as unicode superscript
						try {
							int i = Integer.parseInt(rightStr);
							String index = "";
							if (i < 0) {
								sb.append('\u207B'); // superscript minus sign
								i = -i;
							}

							if (i == 0) {
								sb.append('\u2070'); // zero
							} else {
								while (i > 0) {
									switch (i % 10) {
									case 0:
										index = "\u2070" + index;
										break;
									case 1:
										index = "\u00b9" + index;
										break;
									case 2:
										index = "\u00b2" + index;
										break;
									case 3:
										index = "\u00b3" + index;
										break;
									case 4:
										index = "\u2074" + index;
										break;
									case 5:
										index = "\u2075" + index;
										break;
									case 6:
										index = "\u2076" + index;
										break;
									case 7:
										index = "\u2077" + index;
										break;
									case 8:
										index = "\u2078" + index;
										break;
									case 9:
										index = "\u2079" + index;
										break;

									}
									i = i / 10;
								}
							}

							sb.append(index);
						} catch (Exception e) {
							sb.append('^');
							sb.append(rightStr);
						}

						/*
						 * 
						 * if (rightStr.length() == 1) { switch
						 * (rightStr.charAt(0)) {
						 * 
						 * case '0': sb.append('\u2070'); break; case '1':
						 * sb.append('\u00b9'); break; case '2':
						 * sb.append('\u00b2'); break; case '3':
						 * sb.append('\u00b3'); break; case '4':
						 * sb.append('\u2074'); break; case '5':
						 * sb.append('\u2075'); break; case '6':
						 * sb.append('\u2076'); break; case '7':
						 * sb.append('\u2077'); break; case '8':
						 * sb.append('\u2078'); break; case '9':
						 * sb.append('\u2079'); break; default: sb.append('^');
						 * sb.append(rightStr); } } else { sb.append('^');
						 * sb.append(rightStr); }
						 */
					} else {
						sb.append('^');
						sb.append(leftBracket(stringType));
						sb.append(rightStr);
						sb.append(rightBracket(stringType));
					}
				}
			}
			break;

		case FACTORIAL:
			switch (stringType) {
			case MATHML:
				mathml(sb, "<factorial/>", leftStr, null);
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
			case MPREDUCE:
				appendReduceFunction(sb, "factorial");
				sb.append(leftStr);
				sb.append(")");
				break;

			default:
				if (((leftStr.charAt(0) != '-') && // no unary
						left.isLeaf())
						|| (opID(left) > Operation.POWER.ordinal())) { // not +,
					// -, *,
					// /, ^
					sb.append(leftStr);
				} else {
					sb.append(leftBracket(stringType));
					sb.append(leftStr);
					sb.append(rightBracket(stringType));
				}
				sb.append('!');
				break;
			}
			break;

		case COS:
			trig(leftStr,sb,"<cos/>","\\cos","Cos(","COS(","cos","cos","cos",
					stringType,tpl.isPrintLocalizedCommandNames(),true);
			break;

		case SIN:
			trig(leftStr,sb,"<sin/>","\\sin","Sin(","SIN(","sin","sin","sin",
					stringType,tpl.isPrintLocalizedCommandNames(),true);
			break;

		case TAN:
			trig(leftStr,sb,"<tan/>","\\tan","Tan(","TAN(","tan","tan","tan",
					stringType,tpl.isPrintLocalizedCommandNames(),true);
			break;

		case CSC:
			trig(leftStr,sb,"<csc/>","\\csc","Csc(","CSC(","csc","csc","csc",
					stringType,tpl.isPrintLocalizedCommandNames(),true);
			break;

		case SEC:
			trig(leftStr,sb,"<sec/>","\\sec","Sec(","SEC(","sec","sec","sec",
					stringType,tpl.isPrintLocalizedCommandNames(),true);
			break;

		case COT:
			trig(leftStr,sb,"<cot/>","\\cot","Cot(","COT(","cot","cot","cot",
					stringType,tpl.isPrintLocalizedCommandNames(),true);
			break;

		case CSCH:
			trig(leftStr,sb,"<csch/>","\\csch","Csch(","CSCH(","csch","csch","func csch",
					stringType,tpl.isPrintLocalizedCommandNames(),false);
			break;

		case SECH:
			trig(leftStr,sb,"<sech/>","\\sech","Sech(","SECH(","sech","sech","func sech",
					stringType,tpl.isPrintLocalizedCommandNames(),false);
			break;

		case COTH:
			trig(leftStr,sb,"<coth/>","\\coth","Coth(","COTH(","coth","coth","coth",
					stringType,tpl.isPrintLocalizedCommandNames(),false);
			break;

		case ARCCOS:
			trig(leftStr,sb,"<arccos/>","\\arccos","ArcCos(","ACOS(",degFix("acos"),"acos","arccos",
					stringType,tpl.isPrintLocalizedCommandNames(),false);
			break;

		case ARCSIN:
			trig(leftStr,sb,"<arcsin/>","\\arcsin","ArcSin(","ASIN(",degFix("asin"),"asin","arcsin",
					stringType,tpl.isPrintLocalizedCommandNames(),false);
			break;

		case ARCTAN:
			trig(leftStr,sb,"<arctan/>","\\arctan","ArcTan(","ATAN(",degFix("atan"),"atan","arctan",
					stringType,tpl.isPrintLocalizedCommandNames(),false);
			break;

		case ARCTAN2:
			if (stringType.equals(StringType.MATHML)) {
				mathml(sb, "<atan/>", leftStr, rightStr);
			} else {
				switch (stringType) {
				case LATEX:
					sb.append("atan2 \\left( ");
					break;
				case LIBRE_OFFICE:
					sb.append("func atan2 left( ");
					break;
				case MATH_PIPER:
					sb.append("ArcTan2(");
					break;

				case PSTRICKS:
					sb.append("ATAN2(");
					break;

				case MPREDUCE:
					sb.append(degFix("myatan2"));
					sb.append("(");
					break;

				default:
					sb.append("atan2(");
				}
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(rightBracket(stringType));
			}
			break;

		case COSH:
			trig(leftStr,sb,"<cosh/>","\\cosh","Cosh(","COSH(","cosh","cosh","cosh",
					stringType,tpl.isPrintLocalizedCommandNames(),false);
			break;

		case SINH:
			trig(leftStr,sb,"<sinh/>","\\sinh","Sinh(","SINH(","sinh","sinh","sinh",
					stringType,tpl.isPrintLocalizedCommandNames(),false);
			break;

		case TANH:
			trig(leftStr,sb,"<tanh/>","\\tanh","Tanh(","TANH(","tanh","tanh","tanh",
					stringType,tpl.isPrintLocalizedCommandNames(),false);
			break;

		case ACOSH:
			trig(leftStr,sb,"<arccosh/>","\\acosh","ArcCosh(","ACOSH(","acosh","acosh","arcosh",
					stringType,tpl.isPrintLocalizedCommandNames(),false);
			break;
		case ASINH:
			trig(leftStr,sb,"<arcsinh/>","\\asinh","ArcSinh(","ASINH(","asinh","asinh","arsinh",
					stringType,tpl.isPrintLocalizedCommandNames(),false);			
			break;

		case ATANH:
			trig(leftStr,sb,"<arctanh/>","\\atanh","ArcTanh(","ATANH(","atanh","atanh","artanh",
					stringType,tpl.isPrintLocalizedCommandNames(),false);
			break;
		case REAL:
			trig(leftStr,sb,"<real/>","\\real","","","myreal","real","real",
					stringType,tpl.isPrintLocalizedCommandNames(),false);
			break;
		case IMAGINARY:
			trig(leftStr,sb,"<imaginary/>","\\imaginary","","","imaginary","imaginary","imaginary",
					stringType,tpl.isPrintLocalizedCommandNames(),false);
			break;
		case FRACTIONAL_PART:
			trig(leftStr,sb,"<todo/>","\\fractionalPart","","","fractionalPart","fractionalPart","fractionalPart","fPart",
					stringType,tpl.isPrintLocalizedCommandNames(),false);
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
			case MPREDUCE:
				appendReduceFunction(sb, "zeta");
				break;	
			default:
				sb.append("zeta(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(stringType));
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
				appendReduceFunction(sb, "Ci");
				break;
			case MPREDUCE:
				appendReduceFunction(sb, "ci");
				break;
			default:
				sb.append("cosIntegral(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(stringType));
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
				appendReduceFunction(sb, "Si");
				break;
			case MPREDUCE:
				appendReduceFunction(sb, "si");
				break;

			default:
				sb.append("sinIntegral(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(stringType));
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
				appendReduceFunction(sb, "Ei");
				break;
			case MPREDUCE:
				appendReduceFunction(sb, "ei");
				break;

			default:
				sb.append("expIntegral(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(stringType));
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
				mathml(sb, "<exp/>", leftStr, null);
				break;
			case LIBRE_OFFICE:
				sb.append("func ");
			case LATEX:

				// add brackets for eg e^b^c -> e^(b^c)
				boolean addParentheses = (left.isExpressionNode() && ((ExpressionNode) left)
						.getOperation().equals(Operation.POWER));

				sb.append("\\mathit{e}^{");
				if (addParentheses) {
					sb.append(leftBracket(stringType));
				}
				sb.append(leftStr);
				if (addParentheses) {
					sb.append(rightBracket(stringType));
				}
				sb.append('}');
				break;

			case MATH_PIPER:
				sb.append("Exp(");
				sb.append(leftStr);
				sb.append(')');
				break;
			case MPREDUCE:
				appendReduceFunction(sb, "exp");
				sb.append(leftStr);
				sb.append(')');
				break;

			case JASYMCA:
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
				mathml(sb, "<ln/>", leftStr, null);
			} else {
				switch (stringType) {
				case LATEX:
					sb.append("\\ln \\left( ");
					break;
				case LIBRE_OFFICE:
					sb.append("ln left ( ");
					break;
				case MATH_PIPER:
					sb.append("Ln(");
					break;
				case MPREDUCE:
					appendReduceFunction(sb, "log");
					break;
				case GIAC:
				case JASYMCA:
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
				sb.append(rightBracket(stringType));
			}
			break;

		case LOGB:
			switch (stringType) {
			case MATHML:
				mathml(sb, "<log/>", "<logbase>", leftStr, "</logbase>", "",
						rightStr, "");
				break;
			case LATEX:
				sb.append("\\log_{");
				sb.append(leftStr);
				sb.append('}');
				sb.append(leftBracket(stringType));
				sb.append(rightStr);
				sb.append(rightBracket(stringType));
				break;
			case LIBRE_OFFICE:
				sb.append("log_{");
				sb.append(leftStr);
				sb.append('}');
				sb.append(leftBracket(stringType));
				sb.append(rightStr);
				sb.append(rightBracket(stringType));
				break;
			case MATH_PIPER:
				// user defined function
				sb.append("logB(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(')');
				break;

			case MPREDUCE:
				sb.append("logb(");
				sb.append(rightStr);
				sb.append(',');
				sb.append(leftStr);
				sb.append(')');
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
				sb.append(leftBracket(stringType));
				sb.append(rightStr);
				sb.append(rightBracket(stringType));
				break;


			case GIAC:
				appendReduceFunction(sb, "Psi");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(')');
				break;
			case MATH_PIPER:
			case MPREDUCE:
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
				sb.append(leftBracket(stringType));
				sb.append(leftStr);
				sb.append(rightBracket(stringType));
				break;
			case MPREDUCE:
				appendReduceFunction(sb, "erf");
				sb.append(leftStr);
				sb.append(')');
				break;

			case LIBRE_OFFICE:
				sb.append("func ");
			case GIAC:
			case MATH_PIPER:
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
				sb.append(leftBracket(stringType));
				sb.append(leftStr);
				sb.append(rightBracket(stringType));
				break;
			case MPREDUCE:
				appendReduceFunction(sb, "psi");
				sb.append(leftStr);
				sb.append(')');
				break;

			case GIAC:
				appendReduceFunction(sb, "Psi");
				sb.append(leftStr);
				sb.append(')');
				break;

			case LIBRE_OFFICE:
				sb.append("func ");
			case MATH_PIPER:
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
				mathml(sb, "<log/>", leftStr, null);
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
			case MATH_PIPER:
			case PGF:
				sb.append("log10("); // user-defined function in Maxima
				sb.append(leftStr);
				sb.append(')');
				break;

			case MPREDUCE:
				if (left instanceof ListValue) {
					sb.append("applyfunction2(logb,");
				} else {
					sb.append("logb(");
				}
				sb.append(leftStr);
				sb.append(",10)");
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
			case MATH_PIPER:
				sb.append("log2(");
				sb.append(leftStr);
				sb.append(')');
				break;

			case MPREDUCE:
				if (left instanceof ListValue) {
					sb.append("applyfunction2(logb,");
				} else {
					sb.append("logb(");
				}
				sb.append(leftStr);
				sb.append(",2)");
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
				mathml(sb, "<root/>", leftStr, null);
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
				sb.append(app.getFunction("nroot"));
				sb.append("(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(')');
				break;
			default: //MAXIMA, MPREDUCE, PSTRICKS, ...
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
				mathml(sb, "<root/>", leftStr, null);
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
			case MATH_PIPER:
				sb.append("Sqrt(");
				sb.append(leftStr);
				sb.append(')');
				break;
			case MPREDUCE:
				appendReduceFunction(sb, "sqrt");
				sb.append(leftStr);
				sb.append(')');
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
				mathml(sb, "<root/>", "<degree>", "3", "</degree>", "",
						leftStr, "");
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
			case MATH_PIPER:
			case MPREDUCE:
				if (left instanceof ListValue) {
					sb.append("applyfunction2(**,");
					sb.append(leftStr);
					sb.append(",1/3)");
				} else {
					sb.append("(");
					sb.append(leftStr);
					sb.append(")^(1/3)");
				}
				break;

			case GIAC:
				sb.append("(");
				sb.append(leftStr);
				sb.append(")^(1/3)");
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
				mathml(sb, "<abs/>", leftStr, null);
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
			case MATH_PIPER:
				sb.append("Abs(");
				sb.append(leftStr);
				sb.append(')');
				break;

			case MPREDUCE:
				appendReduceFunction(sb, "myabs");
				sb.append(leftStr);
				sb.append(')');
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

			case MATH_PIPER:
				sb.append("Sign(");
				break;
			case MPREDUCE:
				appendReduceFunction(sb, "sign");
				break;
			case JASYMCA:
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
				mathml(sb, "<conjugate/>", leftStr, null);
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
			case MATH_PIPER:
				sb.append("Conjugate(");
				sb.append(leftStr);
				sb.append(")");
				break;
			case MPREDUCE:
				appendReduceFunction(sb, "conjugate");
				sb.append(leftStr);
				sb.append(')');
				break;
			default:
				sb.append("conjugate(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case ARG:
			switch (stringType) {
			case MATHML:
				mathml(sb, "<arg/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\arg \\left( ");
				sb.append(leftStr);
				sb.append("\\right)");
				break;
			case MATH_PIPER:
				sb.append("Arg(");
				sb.append(leftStr);
				sb.append(")");
				break;
			case GIAC:
				sb.append("carg(");
				sb.append(leftStr);
				sb.append(')');
				break;
			case MPREDUCE:
				appendReduceFunction(sb, "myarg");
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

		case FLOOR:
			switch (stringType) {
			case MATHML:
				mathml(sb, "<floor/>", leftStr, null);
				break;
			case LATEX:
				if (!app.isHTML5Applet()) {
					// MathQuill doesn't support this
					sb.append("\\left");
				}
				sb.append("\\lfloor ");
				sb.append(leftStr);
				if (!app.isHTML5Applet()) {
					// MathQuill doesn't support this
					sb.append("\\right");
				}
				sb.append("\\rfloor ");
				break;
			case LIBRE_OFFICE:
				sb.append(" left lfloor ");
				sb.append(leftStr);
				sb.append(" right rfloor");
				break;
			case MATH_PIPER:
				sb.append("Floor(");
				sb.append(leftStr);
				sb.append(')');
				break;
			case MPREDUCE:
				appendReduceFunction(sb, "myfloor");
				sb.append(leftStr);
				sb.append(')');
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
				mathml(sb, "<ceiling/>", leftStr, null);
				break;
			case LATEX:
				if (!app.isHTML5Applet()) {
					// MathQuill doesn't support this
					sb.append("\\left");
				}
				sb.append("\\lceil ");
				sb.append(leftStr);
				if (!app.isHTML5Applet()) {
					// MathQuill doesn't support this
					sb.append("\\right");
				}
				sb.append("\\rceil ");
				break;
			case LIBRE_OFFICE:
				sb.append("left lceil ");
				sb.append(leftStr);
				sb.append(" right rceil");
				break;
			case MATH_PIPER:
				sb.append("Ceil(");
				sb.append(leftStr);
				sb.append(')');
				break;
			case MPREDUCE:
				appendReduceFunction(sb, "myceil");
				sb.append(leftStr);
				sb.append(')');
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

			case MATH_PIPER:
				sb.append("Round(");
				break;

			case MPREDUCE:
				appendReduceFunction(sb, "myround");
				break;

			default:
				sb.append("round(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(stringType));
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
			case MATH_PIPER:
				sb.append("Gamma(");
				break;
			case MPREDUCE:
				appendReduceFunction(sb, "gamma");
				break;

			default:
				sb.append("gamma(");
			}
			sb.append(leftStr);
			sb.append(rightBracket(stringType));
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
				sb.append("lower\\_incomplete\\_gamma(");
				break;

			case MPREDUCE:
				appendReduceFunction(sb, "gamma2");
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
			sb.append(rightBracket(stringType));
			break;

		case GAMMA_INCOMPLETE_REGULARIZED:
			switch (stringType) {
			case LATEX:
				sb.append("P \\left( ");
				break;

			case LIBRE_OFFICE:
				sb.append("func gammaRegularized left (");

			case GIAC:
				sb.append("lower\\_incomplete\\_gamma(");
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
			sb.append(rightBracket(stringType));
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
			case MATH_PIPER:
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
			sb.append(rightBracket(stringType));
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
			sb.append(rightBracket(stringType));
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

			case MPREDUCE:
				sb.append("ibeta(");
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
			
			sb.append(rightBracket(stringType));
			break;

		case RANDOM:
			if (valueForm) {
				sb.append(leftStr);
			} else {
				switch (stringType) {
				case MPREDUCE:
					sb.append("myrandom()");
					break;
				case LIBRE_OFFICE:
					sb.append("func ");
				default:
					sb.append("random()");
				}
			}
			break;

		case XCOORD:
			if (valueForm && (leftEval = left.evaluate(tpl)).isVectorValue()) {
				sb.append(kernel.format(((VectorValue) leftEval).getVector()
						.getX(), tpl));
			} else if (valueForm
					&& (leftEval = left.evaluate(tpl)).isVector3DValue()) {
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
					sb.append(rightBracket(stringType));
					break;
				case LIBRE_OFFICE:
					sb.append("func x left (");
					sb.append(leftStr);
					sb.append(rightBracket(stringType));
				case MATH_PIPER:
				case GIAC:
					// we need to protect x(A) as a constant in the CAS
					// see http://www.geogebra.org/trac/ticket/662
					// see http://www.geogebra.org/trac/ticket/922
					sb.append("xcoord(");
					sb.append(leftStr);
					sb.append(')');
					break;
				case MPREDUCE:
					appendReduceFunction(sb, "xcoord");
					sb.append(leftStr);
					sb.append(')');
					break;
				default:
					sb.append("x(");
					sb.append(leftStr);
					sb.append(')');
				}
			}
			break;

		case YCOORD:
			if (valueForm && (leftEval = left.evaluate(tpl)).isVectorValue()) {
				sb.append(kernel.format(((VectorValue) leftEval).getVector()
						.getY(), tpl));
			} else if (valueForm
					&& (leftEval = left.evaluate(tpl)).isVector3DValue()) {
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
					sb.append(rightBracket(stringType));
				case MATH_PIPER:
				case GIAC:
					// we need to protect x(A) as a constant in the CAS
					// see http://www.geogebra.org/trac/ticket/662
					// see http://www.geogebra.org/trac/ticket/922
					sb.append("ycoord(");
					sb.append(leftStr);
					sb.append(')');
					break;
				case MPREDUCE:
					appendReduceFunction(sb, "ycoord");
					sb.append(leftStr);
					sb.append(')');
					break;
				default:
					sb.append("y(");
					sb.append(leftStr);
					sb.append(')');
				}
			}
			break;

		case ZCOORD:
			if (valueForm && (leftEval = left.evaluate(tpl)).isVector3DValue()) {
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
					sb.append(rightBracket(stringType));
				case MATH_PIPER:
				case GIAC:
					// we need to protect x(A) as a constant in the CAS
					// see http://www.geogebra.org/trac/ticket/662
					// see http://www.geogebra.org/trac/ticket/922
					sb.append("zcoord(");
					sb.append(leftStr);
					sb.append(')');
					break;
				case MPREDUCE:
					appendReduceFunction(sb, "zcoord");
					sb.append(leftStr);
					sb.append(')');
					break;
				default:
					sb.append("z(");
					sb.append(leftStr);
					sb.append(')');
				}
			}
			break;

		case FUNCTION:
			if (stringType == StringType.MPREDUCE
			&& right instanceof ListValue) {
				sb.append("applyfunction(" + leftStr + "," + rightStr + ")");
				break;
			}
			// GeoFunction and GeoFunctionConditional should not be expanded
			if (left instanceof GeoFunction) {
				GeoFunction geo = (GeoFunction) left;
				if (geo.isLabelSet()) {
					if (stringType.equals(StringType.LIBRE_OFFICE))
						sb.append("func ");
					sb.append(geo.getLabel(tpl));
					sb.append(leftBracket(stringType));
					sb.append(rightStr);
					sb.append(rightBracket(stringType));
				} else {
					// inline function: replace function var by right side
					FunctionVariable var = geo.getFunction()
							.getFunctionVariable();
					String oldVarStr = var.toString(tpl);
					var.setVarString(rightStr);
					if (stringType.equals(StringType.LIBRE_OFFICE))
						sb.append("func ");
					sb.append(geo.getLabel(tpl));
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
					sb.append(leftBracket(stringType));
					sb.append(leftStr);
					sb.append(rightBracket(stringType));
					break;

				default:
					sb.append(leftStr);
					sb.append(leftBracket(stringType));
					sb.append(rightStr);
					sb.append(rightBracket(stringType));
					break;
				}
			} else {
				// standard case if we get here
				sb.append(leftStr);
				sb.append(leftBracket(stringType));
				sb.append(rightStr);
				sb.append(rightBracket(stringType));
			}
			break;

			// TODO: put back into case FUNCTION_NVAR:, see #1115
		case ELEMENT_OF:
			sb.append(app.getLocalization().getCommand("Element"));
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
						sb.append(leftBracket(stringType));
					}
					sb.append(en.toValueString(tpl));
					if (!stringType.equals(StringType.LATEX)) {
						sb.append(rightBracket(stringType));
					}
				} else {
					sb.append(leftBracket(stringType));
					sb.append(leftStr);
					sb.append(rightBracket(stringType));
				}
			} else {
				// multivariate functions
				if (left.isGeoElement()) {
					sb.append(((GeoElement) left).getLabel(tpl));
				} else {
					sb.append(leftStr);
				}
				//no parameters for LeftSide[a], Derivative[sin(x+y),y], etc
				if(!left.isGeoElement() || ((GeoElement)left).isLabelSet()){
					sb.append(leftBracket(stringType));
					// rightStr is a list of arguments, e.g. {2, 3}
					// drop the curly braces { and }
					// or list( and ) in case of mpreduce
					if (stringType.equals(StringType.MPREDUCE)) {
						sb.append(rightStr.substring(22, rightStr.length() - 2));
					} else {
						sb.append(rightStr.substring(1, rightStr.length() - 1));
					}
					sb.append(rightBracket(stringType));
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
			sb.append(leftBracket(stringType));
			sb.append(rightStr);
			sb.append(rightBracket(stringType));
			break;

		case DERIVATIVE: // e.g. f''
			// labeled GeoElements should not be expanded
			if(tpl.hasType(StringType.MPREDUCE)){
				sb.append("df(");
				sb.append(leftStr);
				break;
			}
			if (left.isGeoElement() && ((GeoElement) left).isLabelSet()) {
				sb.append(((GeoElement) left).getLabel(tpl));
			} else {
				sb.append(leftStr);
			}

			if (right.isNumberValue()) {
				int order = (int) Math.round(((MyDouble) right).getDouble());
				for (; order > 0; order--) {
					sb.append('\'');
				}
			} else {
				sb.append(right);
			}
			break;

		case $VAR_ROW: // e.g. A$1
			if (valueForm || tpl.hasType(StringType.MPREDUCE)) {
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
			if (valueForm || tpl.hasType(StringType.MPREDUCE)) {
				// GeoElement value
				sb.append(leftStr);
			} else {
				//maybe wrongly parsed dynamic reference in CAS -- TODO decide whether we need this
				if(!left.isGeoElement()){
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
			if (valueForm || tpl.hasType(StringType.MPREDUCE)) {
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
			sb.append(app.getPlain("Function.freehand"));
			sb.append('(');
			sb.append(leftStr);
			sb.append(')');
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
				if (stringType == StringType.MPREDUCE) {
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
				sb.append(((MyNumberPair)left).y.toString(tpl));
				sb.append("=");
				sb.append(((MyNumberPair)right).x.toString(tpl));
				sb.append("}^{");
				sb.append(((MyNumberPair)right).y.toString(tpl));
				sb.append("}");
				sb.append(((MyNumberPair)left).x.toString(tpl));
			} else if (stringType == StringType.LIBRE_OFFICE) {
				sb.append("sum from{");
				sb.append(((MyNumberPair)left).y.toString(tpl));
				sb.append("=");
				sb.append(((MyNumberPair)right).x.toString(tpl));
				sb.append("} to{");
				sb.append(((MyNumberPair)right).y.toString(tpl));
				sb.append("}");
				sb.append(((MyNumberPair)left).x.toString(tpl));
			} else {
				if (stringType == StringType.MPREDUCE) {
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
				if (stringType == StringType.MPREDUCE) {
					sb.append("sub(");
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
			if (stringType == StringType.MPREDUCE) {
				sb.append("iffun(");
			}
			else {
				if (tpl.isPrintLocalizedCommandNames()) {
					sb.append(app.getLocalization().getCommand("If"));
				}else{
					sb.append("If");
				}
				sb.append("[");
				sb.append(leftStr);
				sb.append(",");
				sb.append(rightStr);
				sb.append("]");
			}
			break;	
		case IF_ELSE:
			if (stringType == StringType.MPREDUCE) {
				sb.append("ifelsefun(");
			}
			else {
				if (tpl.isPrintLocalizedCommandNames()) {
					sb.append(app.getLocalization().getCommand("If"));
				}else{
					sb.append("If");
				}
				sb.append("[");
				sb.append(leftStr);
				sb.append(",");
				sb.append(rightStr);
				sb.append("]");
			}
			break;


		default:
			sb.append("unhandled operation " + operation);
		}
		return sb.toString();
	}

	private void appendPerpSign(StringBuilder sb, StringType stringType) {
		sb.append(' ');
		switch (stringType) {
		case LATEX:
			if (kernel.isInsertLineBreaks()) {
				sb.append("\\-");
			}
			sb.append("\\perp");
			break;
		case LIBRE_OFFICE:
			sb.append(" ortho ");
			break;
		default:
			sb.append(strPERPENDICULAR);
		}
		sb.append(' ');

	}

	private void appendParallelSign(StringBuilder sb, StringType stringType) {
		sb.append(' ');
		switch (stringType) {
		case LATEX:
			if (kernel.isInsertLineBreaks()) {
				sb.append("\\-");
			}
			sb.append("\\parallel");
			break;
		case LIBRE_OFFICE:
			sb.append(" parallel ");
			break;
		default:
			sb.append(strPARALLEL);
		}
		sb.append(' ');

	}

	private void appendGeqSign(StringBuilder sb, StringType stringType) {
		sb.append(' ');
		switch (stringType) {
		case LATEX:
			if (kernel.isInsertLineBreaks()) {
				sb.append("\\-");
			}
			sb.append("\\geq");
			break;
		case LIBRE_OFFICE:
		case MATH_PIPER:
			sb.append(">=");
			break;

		default:
			sb.append(strGREATER_EQUAL);
		}
		sb.append(' ');

	}

	private void appendLeqSign(StringBuilder sb, StringType stringType) {
		sb.append(' ');
		switch (stringType) {
		case LATEX:
			if (kernel.isInsertLineBreaks()) {
				sb.append("\\-");
			}
			sb.append("\\leq");
			break;
		case LIBRE_OFFICE:
		case MATH_PIPER:
		case MPREDUCE:
			sb.append("<=");
			break;

		default:
			sb.append(strLESS_EQUAL);
		}
		sb.append(' ');

	}

	private void appendGreaterSign(StringBuilder sb, StringType stringType) {
		if (stringType.equals(StringType.LATEX)
				&& kernel.isInsertLineBreaks()) {
			sb.append(" \\->");
		} else {
			sb.append(" > ");
		}

	}

	private void appendLessSign(StringBuilder sb, StringType stringType) {
		if (stringType.equals(StringType.LATEX)
				&& kernel.isInsertLineBreaks()) {
			sb.append(" \\-< ");
		} else {
			sb.append(" < ");
		}

	}

	private void appendStrictSubsetSign(StringBuilder sb, StringType stringType) {
		sb.append(' ');
		switch (stringType) {
		case LATEX:
			if (kernel.isInsertLineBreaks()) {
				sb.append("\\-");
			}
			sb.append("\\subset");
			break;
		case LIBRE_OFFICE:
			sb.append(" subset ");
			break;
		default:
			sb.append(strIS_SUBSET_OF_STRICT);
		}
		sb.append(' ');		
	}

	private void appendSubsetSign(StringBuilder sb, StringType stringType) {
		sb.append(' ');
		switch (stringType) {
		case LATEX:
			if (kernel.isInsertLineBreaks()) {
				sb.append("\\-");
			}
			sb.append("\\subseteq");
			break;
		case LIBRE_OFFICE:
			sb.append(" subseteq ");
			break;
		default:
			sb.append(strIS_SUBSET_OF);
		}
		sb.append(' ');

	}

	private void appendNotEqualSign(StringBuilder sb, StringType stringType) {
		sb.append(' ');
		switch (stringType) {
		case LATEX:
			if (kernel.isInsertLineBreaks()) {
				sb.append("\\-");
			}
			sb.append("\\neq");
			break;
		case LIBRE_OFFICE:
			sb.append("<>");
			break;
		case MATH_PIPER:
			sb.append("!=");
			break;

		default:
			sb.append(strNOT_EQUAL);
		}
		sb.append(' ');

	}

	private void appendEqualSign(StringBuilder sb, StringType STRING_TYPE) {
		sb.append(' ');
		switch (STRING_TYPE) {
		case LATEX:
			if (kernel.isInsertLineBreaks()) {
				sb.append("\\-");
			}
			sb.append("\\stackrel{\\small ?}{=}");
			break;
		case LIBRE_OFFICE:
		case MATH_PIPER:
		case JASYMCA:
		case MPREDUCE:
			sb.append("=");
			break;

		default:
			sb.append(strEQUAL_BOOLEAN);
		}
		sb.append(' ');

	}

	private String degFix(String string) {
		if(kernel.getInverseTrigReturnsAngle()){
			return "deg"+string;
		}
		return string;
	}

	private static void appendOp(StringBuilder sb, String string, String leftStr,
			String rightStr) {
		sb.append(string);
		sb.append('(');
		sb.append(leftStr);
		sb.append(',');
		sb.append(rightStr);
		sb.append(')');

	}
	
	private void trig(String leftStr, StringBuilder sb, String mathml, String latex,
			String mathPiper, String psTricks, String mpReduce, String key,
			String libreOffice,
			StringType STRING_TYPE, boolean localized,boolean needDegrees) {

		// send "key" for Giac
		trig(leftStr, sb, mathml, latex,
				mathPiper,  psTricks,  mpReduce, key,
				libreOffice, key,
				STRING_TYPE, localized, needDegrees);


	}

	private void trig(String leftStr, StringBuilder sb, String mathml, String latex,
			String mathPiper, String psTricks, String mpReduce, String key,
			String libreOffice, String giac,
			StringType STRING_TYPE, boolean localized,boolean needDegrees) {
		if (STRING_TYPE.equals(StringType.MATHML)) {
			mathml(sb, mathml, leftStr, null);
		} else {
			switch (STRING_TYPE) {
			case LATEX:
				if (app.isHTML5Applet()) {
					sb.append("\\" + app.getFunction(key));
				} else if(localized) {
					sb.append("\\operatorname{");
					sb.append(app.getFunction(key));
					sb.append("}");
				} else {
					sb.append(latex);
				}
				sb.append(" \\left( ");
				break;
			case LIBRE_OFFICE:
				if(!libreOffice.equals(app.getFunction(key))){
					sb.append("func ");

				}
				sb.append(app.getFunction(key));
				sb.append(" left( ");
				break;
			case MATH_PIPER:
				sb.append(mathPiper);
				break;

			case GIAC:
				sb.append(giac);
				sb.append('(');
				break;

			case PSTRICKS:
				sb.append(psTricks);
				break;
			case MPREDUCE:
				appendReduceFunction(sb, mpReduce);
				break;
			default:
				if(localized){
					sb.append(app.getFunction(key));
				}
				else{	
					sb.append(key);
				}
				sb.append("(");
			}
			if (needDegrees && STRING_TYPE.equals(StringType.PGF)) {
				sb.append("(" + leftStr + ") 180/pi");
			} else {
				sb.append(leftStr);
			}
			sb.append(rightBracket(STRING_TYPE));
		}

	}

	private void appendReduceFunction(StringBuilder sb, String string) {
		if (left instanceof ListValue) {
			sb.append("applyfunction(");
			sb.append(string);
			sb.append(",");
		} else {
			sb.append(string);
			sb.append('(');
		}

	}

	private static boolean isMultiplyOrDivide(ExpressionNode exp) {
		return exp.getOperation().equals(Operation.MULTIPLY)
				|| exp.getOperation().equals(Operation.DIVIDE);
	}

	private static void mathml(StringBuilder sb, String op, String leftStr,
			String rightStr) {
		mathml(sb, op, "", leftStr, "", "", rightStr, "");
	}

	private static void mathml(StringBuilder sb, String op, String preL,
			String leftStr, String postL, String preR, String rightStr,
			String postR) {
		sb.append("<apply>");
		sb.append(op);
		sb.append(preL);

		if (leftStr.startsWith("<apply>")) {
			sb.append(leftStr);
		} else if (StringUtil.isNumber(leftStr)) {
			sb.append("<cn>");
			sb.append(leftStr);
			sb.append("</cn>");
		} else {
			sb.append("<ci>");
			sb.append(leftStr);
			sb.append("</ci>");
		}

		sb.append(postL);
		sb.append(preR);

		if (rightStr != null) {
			if (rightStr.startsWith("<apply>")) {
				sb.append(rightStr);
			} else if (StringUtil.isNumber(rightStr)) {
				sb.append("<cn>");
				sb.append(rightStr);
				sb.append("</cn>");
			} else {
				sb.append("<ci>");
				sb.append(rightStr);
				sb.append("</ci>");
			}
		}

		sb.append(postR);

		sb.append("</apply>");

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

	public boolean isBooleanValue() {
		return evaluate(StringTemplate.defaultTemplate).isBooleanValue();
	}

	public boolean isListValue() {
		return evaluate(StringTemplate.defaultTemplate).isListValue();
	}

	public boolean isPolynomialInstance() {
		// return evaluate().isPolynomial();
		return false;
	}

	public boolean isTextValue() {
		// should be efficient as it is used in operationToString()
		if (leaf) {
			return left.isTextValue();
		}
		return (operation.equals(Operation.PLUS) && (left.isTextValue() || right
				.isTextValue()));
	}

	final public boolean isExpressionNode() {
		return true;
	}

	public boolean isVector3DValue() {
		return false;
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
		if (ev1.isNumberValue() && ev2.isNumberValue()) {
			return Kernel.isEqual(((NumberValue) ev1).getDouble(),
					((NumberValue) ev2).getDouble(), Kernel.STANDARD_PRECISION);
		} else if (ev1.isTextValue() && ev2.isTextValue()) {
			return ((TextValue) ev1).toValueString(
					StringTemplate.defaultTemplate).equals(
							((TextValue) ev2)
							.toValueString(StringTemplate.defaultTemplate));
		} else if (ev1.isVectorValue() && ev2.isVectorValue()) {
			return ((VectorValue) ev1).getVector().isEqual(
					((VectorValue) ev2).getVector());
		} else if (ev1.isBooleanValue() && ev2.isBooleanValue()) {
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
			if (ev instanceof FunctionVariable || ev instanceof GeoDummyVariable) {
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

	private static String leftBracket(StringType type) {
		//return (type.equals(StringType.LATEX)) ? " \\left( " : "(";

		if (type.equals(StringType.LATEX))
			return " \\left( ";
		else if (type.equals(StringType.LIBRE_OFFICE))
			return " left ( ";
		else 
			return "(";
	}

	private static String rightBracket(StringType type) {
		//return (type.equals(StringType.LATEX)) ? " \\right) " : ")";

		if (type.equals(StringType.LATEX))
			return " \\right)";
		else if (type.equals(StringType.LIBRE_OFFICE))
			return " right )";
		else 
			return ")";
	}

	private static String multiplicationSign(StringType type) {
		switch (type) {
		case LATEX:
			return " \\cdot ";

		case LIBRE_OFFICE:
			return " cdot ";

		case GEOGEBRA:
			return " "; // space for multiplication

		default:
			return " * ";
		}
	}

	private static String multiplicationSpace(StringType type) {
		// wide space for multiplicatoin space in LaTeX
		return (type.equals(StringType.LATEX)) ? " \\; " : " ";
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
				return lc * getRightTree().evaluateNum().getDouble();
			} else if (!getLeftTree().containsFunctionVariable()) {
				return rc * getLeftTree().evaluateNum().getDouble();
			}
			break;

		case DIVIDE:
			if (!getRightTree().containsFunctionVariable()) {
				return lc / getRightTree().evaluateNum().getDouble();
			}
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
	private static void append(StringBuilder sb, String str,
			ExpressionValue ev, Operation op, StringType STRING_TYPE) {
		if (ev.isLeaf() || (opID(ev) >= op.ordinal()) && 
				(!chainedBooleanOp(op) || !chainedBooleanOp(ev.wrap().getOperation()))) {
			sb.append(str);
		} else {
			sb.append(leftBracket(STRING_TYPE));
			sb.append(str);
			sb.append(rightBracket(STRING_TYPE));
		}

	}

	private static boolean chainedBooleanOp(Operation op) {
		switch(op){
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
		}
		return false;
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
		return new ExpressionNode(kernel, this, Operation.LESS_EQUAL, new MyDouble(
				kernel, d));
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
	 * @param n order of polygamma
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
		return new ExpressionNode(kernel, new MyDouble(
				kernel, d), Operation.PLUS, this);
	}

	/**
	 * @param d
	 *            value to multiply
	 * @return result of multiply
	 */
	public ExpressionNode multiply(double d) {
		if (d == 0) {
			// don't use Kernel.isZero() to check == 0 
			// as can lose leading coefficient of polynomial		
			return new ExpressionNode(kernel, 0);
		} else if (Kernel.isEqual(1,  d)) {
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
		} else if (Kernel.isEqual(1,  d)) {
			return this;
		}
		return new ExpressionNode(kernel, new MyDouble(kernel, d), Operation.MULTIPLY, this);
	}

	/**
	 * @param d
	 *            value to multiply
	 * @return result of multiply
	 */
	public ExpressionNode power(double d) {
		if (Kernel.isZero(d)) {
			return new ExpressionNode(kernel, 1);
		} else if (Kernel.isEqual(1,  d)) {
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
			return this;
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
		if (isConstantDouble(v2, 0))
			return v2.wrap();
		if (isConstantDouble(v2, 1))
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
		if(isConstantDouble(v2,0))
			return new ExpressionNode(kernel,1);
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
		switch (this.operation) {
		case GREATER:
			return new ExpressionNode(kernel, left, Operation.LESS_EQUAL, right);
		case GREATER_EQUAL:
			return new ExpressionNode(kernel, left, Operation.LESS, right);
		case LESS:
			return new ExpressionNode(kernel, left, Operation.GREATER_EQUAL,
					right);
		case LESS_EQUAL:
			return new ExpressionNode(kernel, left, Operation.GREATER, right);
		case EQUAL_BOOLEAN:
			return new ExpressionNode(kernel, left, Operation.NOT_EQUAL, right);
		case NOT_EQUAL:
			return new ExpressionNode(kernel, left, Operation.EQUAL_BOOLEAN,
					right);
		default:
			return new ExpressionNode(kernel, this, Operation.NOT, null);
		}
	}

	/**
	 * @param toRoot
	 *            true to replace powers by roots
	 * @param maxRoot do not use roots higher than that
	 * @return this node with replaced powers / roots
	 */
	public boolean replacePowersRoots(boolean toRoot,int maxRoot ) {
		boolean didReplacement = false;

		if (toRoot && getOperation() == Operation.POWER
				&& getRight().isExpressionNode()) {
			boolean hit = false;
			ExpressionNode rightLeaf = (ExpressionNode) getRight();

			// replaces 1 DIVIDE 2 by SQRT 2, and same for CBRT
			if ((rightLeaf.getOperation() == Operation.DIVIDE)) {
				if (rightLeaf.getRight()
						.toString(StringTemplate.defaultTemplate).equals("2")) {
					setOperation(Operation.SQRT);
					hit = true;
				} else if (rightLeaf.getRight()
						.toString(StringTemplate.defaultTemplate).equals("3")) {
					setOperation(Operation.CBRT);
					hit = true;
				}
				else if (!rightLeaf.getRight().unwrap().isExpressionNode() && rightLeaf.getRight().isNumberValue() &&
						Kernel.isInteger(((NumberValue)rightLeaf.getRight()).getDouble()) &&
						((NumberValue)rightLeaf.getRight()).getDouble()<=maxRoot) {
					App.debug(((NumberValue)rightLeaf.getRight()).getDouble());
					setOperation(Operation.NROOT);
					setRight(new MyDouble(kernel,((NumberValue)rightLeaf.getRight()).getDouble()));
					hit = true;
				}
				if (hit) {
					didReplacement = true;
					if (rightLeaf.getLeft()
							.toString(StringTemplate.defaultTemplate)
							.equals("1")) {
						if(operation!=Operation.NROOT)
							setRight(new MyDouble(kernel, Double.NaN));
					} else { // to parse x^(c/2) to sqrt(x^c)
						double c = 1;
						if(rightLeaf.getLeft().isConstant())
							c = rightLeaf.getLeft().evaluateNum().getDouble();
						if(c<0){

							setRight(new ExpressionNode(kernel,getLeft().wrap().power(-c),
									getOperation(),getRight() 
									));
							setOperation(Operation.DIVIDE);
							setLeft(new MyDouble(kernel,1.0));

						}
						else 	
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
			}
			else if (getOperation() == Operation.CBRT) {
				power = new ExpressionNode(kernel, new MyDouble(kernel, 1),
						Operation.DIVIDE, new MyDouble(kernel, 3));
				hit = true;
			}
			else if (getOperation() == Operation.NROOT) {
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
	public ExpressionValue unwrap(){
		if(isLeaf())
			return getLeft();
		return this;
	}

	@Override
	public ExpressionNode wrap(){
		return this;
	}

	@Override
	public boolean hasCoords() {
		if (isLeaf() )
			return left!=null && left.hasCoords();
		return getLeft().hasCoords() || getRight().hasCoords();
	}

	@Override
	public ExpressionNode derivative(FunctionVariable fv) {
		switch (operation) {

		// for eg (x < x1) * (a1 x² + b1 x + c1)
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
				if (Kernel.isZero(right.evaluateNum().getDouble())) {
					return wrap(new MyDouble(kernel, 0d));
				}

				return wrap(left).power(wrap(right).subtract(1)).multiply(left.derivative(fv)).multiply(right);
			}


			return wrap(left).power(right).multiply(wrap(right.derivative(fv)).multiply(wrap(left).ln()).plus(wrap(right).multiply(left.derivative(fv)).divide(left)));

		case NO_OPERATION:
			return wrap(left.derivative(fv));
		case DIVIDE:			
			if (right.isNumberValue() && !right.contains(fv)) {
				return wrap(left).derivative(fv).divide(right);
			}
			return wrap(left.derivative(fv)).multiply(right).subtract(wrap(right.derivative(fv)).multiply(left)).divide(wrap(right).square());
		case MULTIPLY:			
			if (right.isNumberValue() && !right.contains(fv)) {
				return wrap(left).derivative(fv).multiply(right);
			}
			if (left.isNumberValue() && !left.contains(fv)) {
				return wrap(right).derivative(fv).multiply(left);
			}
			return wrap(left).multiply(right.derivative(fv)).plus(wrap(right).multiply(left.derivative(fv)));
		case PLUS:			
			return wrap(left.derivative(fv)).plus(right.derivative(fv));
		case MINUS:			
			return wrap(left.derivative(fv)).subtract(right.derivative(fv));
		case SIN:			
			return new ExpressionNode(kernel, left, Operation.COS, null).multiply((left).derivative(fv));
		case COS:			
			return new ExpressionNode(kernel, left, Operation.SIN, null).multiply((left).derivative(fv)).multiply(-1);
		case TAN:			
			return new ExpressionNode(kernel, left, Operation.SEC, null).square().multiply((left).derivative(fv));
		case SEC:			
			return new ExpressionNode(kernel, left, Operation.SEC, null).multiply(new ExpressionNode(kernel, left, Operation.TAN, null)).multiply((left).derivative(fv));
		case CSC:			
			return new ExpressionNode(kernel, left, Operation.CSC, null).multiply(new ExpressionNode(kernel, left, Operation.COT, null)).multiply((left).derivative(fv)).multiply(-1);
		case COT:			
			return new ExpressionNode(kernel, left, Operation.CSC, null).square().multiply((left).derivative(fv)).multiply(-1);
		case SINH:			
			return new ExpressionNode(kernel, left, Operation.COSH, null).multiply((left).derivative(fv));
		case COSH:			
			return new ExpressionNode(kernel, left, Operation.SINH, null).multiply((left).derivative(fv));
		case TANH:			
			return new ExpressionNode(kernel, left, Operation.SECH, null).square().multiply((left).derivative(fv));
		case SECH:			
			return new ExpressionNode(kernel, left, Operation.SECH, null).multiply(new ExpressionNode(kernel, left, Operation.TANH, null)).multiply((left).derivative(fv)).multiply(-1);
		case CSCH:			
			return new ExpressionNode(kernel, left, Operation.CSCH, null).multiply(new ExpressionNode(kernel, left, Operation.COTH, null)).multiply((left).derivative(fv)).multiply(-1);
		case COTH:			
			return new ExpressionNode(kernel, left, Operation.CSCH, null).square().multiply((left).derivative(fv)).multiply(-1);

		case ARCSIN:
			return wrap(left.derivative(fv)).divide(wrap(left).square().subtractR(1).sqrt());
		case ARCCOS:
			return wrap(left.derivative(fv)).divide(wrap(left).square().subtractR(1).sqrt()).multiply(-1);
		case ARCTAN:
			return wrap(left.derivative(fv)).divide(wrap(left).square().plus(1));

		case ASINH:
			return wrap(left.derivative(fv)).divide(wrap(left).square().plus(1).sqrt());
		case ACOSH:
			// sqrt(x+1)sqrt(x-1) not sqrt(x^2-1) as has wrong domain
			return wrap(left.derivative(fv)).divide(wrap(left).plus(1).sqrt().multiply(wrap(left).subtract(1).sqrt()));
		case ATANH:
			return wrap(left.derivative(fv)).divide(wrap(left).square().subtractR(1));

		case ABS:
			return wrap(left.derivative(fv)).multiply(left).divide(wrap(left).abs());

		case SGN:
			// 0/x
			return wrap(new MyDouble(kernel, 0)).divide(fv);

		case EXP:
			return wrap(left.derivative(fv)).multiply(wrap(left).exp());

		case SI:
			return wrap(left.derivative(fv)).multiply(wrap(left).sin().divide(left));

		case CI:
			return wrap(left.derivative(fv)).multiply(wrap(left).cos().divide(left));

		case EI:
			return wrap(left.derivative(fv)).multiply(wrap(left).exp().divide(left));

		case ERF:
			return wrap(left.derivative(fv)).multiply(wrap(2)).divide(wrap(left).square().exp().multiply(wrap(Math.PI).sqrt()));

		case PSI:
			return wrap(left.derivative(fv)).multiply(wrap(left).polygamma(1));

		case POLYGAMMA:
			if (left.isNumberValue() && !left.contains(fv)) {
				double n = ((NumberValue) left).getDouble();
				return wrap(right.derivative(fv)).multiply(wrap(right).polygamma(n + 1));
			}

			// TODO: general method (not possible?)
			break;

		case IF_ELSE:
			MyNumberPair np = (MyNumberPair) left;

			np = new MyNumberPair(kernel, np.x, np.y.derivative(fv));

			return new ExpressionNode(kernel, np, Operation.IF_ELSE, right.derivative(fv));

		case IF:

			return new ExpressionNode(kernel, left, Operation.IF, right.derivative(fv));

		case LOG:
			// base e (ln)
			return wrap(left.derivative(fv)).divide(left);

		case LOG10:
			return wrap(left.derivative(fv)).divide(left).divide(Math.log(10));

		case LOG2:
			return wrap(left.derivative(fv)).divide(left).divide(Math.log(2));

		case LOGB:
			if (left.isNumberValue() && !left.contains(fv)) {
				return wrap(right.derivative(fv)).divide(right).divide(Math.log(((NumberValue) left).getDouble()));
			}

			// TODO: general method
			break;


		case NROOT:			
			if (right.isNumberValue() && !right.contains(fv)) {
				return wrap(left.derivative(fv)).multiply(wrap(left).nroot(right)).divide(wrap(left).multiply(right));
			}

			// TODO general method
			break;

		case SQRT:
		case SQRT_SHORT:
			return wrap(left.derivative(fv)).multiply(wrap(left).power(-0.5)).divide(2);
		case CBRT:
			// wrong domain
			//return wrap(left.derivative(fv)).multiply(wrap(left).power(-2d/3d)).divide(3);
			// correct domain
			return wrap(left.derivative(fv)).divide(wrap(left).square().cbrt()).divide(3);

		}

		App.error("unhandled operation in derivative() (no CAS version): "+operation.toString());

		// undefined
		return wrap(Double.NaN);
	}

	@Override
	public ExpressionNode integral(FunctionVariable fv) {
		switch (operation) {

		case XCOORD:
		case YCOORD:
		case ZCOORD:
			return new ExpressionNode(kernel, this, Operation.MULTIPLY, fv);

		case POWER:
			// eg x^2
			if (left == fv && !right.contains(fv)) {
				double index = right.evaluateNum().getDouble();
				if (!Double.isNaN(index) && !Double.isInfinite(index)) {

					if (Kernel.isZero(index + 1)) {
						return new ExpressionNode(kernel, left, Operation.LOG, null);
					}
					return wrap(left).power(index + 1).divide(index + 1);
				}
			} else if (!left.contains(fv)) {

				// eg 2^x
				if (right == fv) {
					double base = left.evaluateNum().getDouble();
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

				double index = right.evaluateNum().getDouble();
				if (!Double.isNaN(index) && !Double.isInfinite(index)) {

					double coeff = getLinearCoefficient(fv, left);
					if (!Double.isNaN(coeff)) {

						// (exp)^-1 -> ln(abs(exp))
						if (Kernel.isEqual(index, -1)) {
							return wrap(left).abs().ln().divide(coeff);							
						}
						return wrap(left).power(index + 1).divide(coeff * ((index + 1)));
					}

					coeff = getLinearCoefficientDiv(fv, left);

					if (!Double.isNaN(coeff)) {
						if (Kernel.isEqual(index, -1)) {
							// (exp)^-1 -> ln(abs(exp))
							return wrap(left).abs().ln().multiply(coeff);							
						}
						return wrap(left).power(index + 1).multiply(coeff / ((index + 1)));
					}
				}

			}

			break;

		case NO_OPERATION:
			return wrap(left.integral(fv));
		case DIVIDE:		
			if (right.isNumberValue() && !right.contains(fv)) {
				return wrap(left.integral(fv)).divide(right);
			}

			if (left.isNumberValue()  && !left.contains(fv) && right == fv) {
				// eg 4/x
				return new ExpressionNode(kernel, fv, Operation.LOG, null).multiply(left);
			}
			break;

		case MULTIPLY:			
			if (right.isNumberValue() && !right.contains(fv)) {
				return wrap(left.integral(fv)).multiplyR(right);
			} else if (left.isNumberValue() && !left.contains(fv)) {
				return wrap(right.integral(fv)).multiplyR(left);
			}

			// can't do by parts without simplification (use Polynomial?)
			break;

		case PLUS:			
			return wrap(left.integral(fv)).plus(right.integral(fv));
		case MINUS:			
			return wrap(left.integral(fv)).subtract(right.integral(fv));
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
				return wrap(left).sec().plus(wrap(left).tan()).abs().ln().divide(coeff);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).sec().plus(wrap(left).tan()).abs().ln().multiply(coeff);	
			}

			break;
		case CSC:			
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).cosec().plus(wrap(left).cot()).abs().ln().divide(-coeff);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).cosec().plus(wrap(left).cot()).abs().ln().multiply(-coeff);	
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
				return wrap(left).exp().atan().divide(coeff/2);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).exp().atan().multiply(2 * coeff);	
			}

			break;
		case CSCH:			
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).cosech().plus(wrap(left).coth()).abs().ln().divide(-coeff);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).cosech().plus(wrap(left).coth()).abs().ln().multiply(-coeff);	
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

			np = new MyNumberPair(kernel, np.x, np.y.derivative(fv));

			return new ExpressionNode(kernel, np, Operation.IF_ELSE, right.integral(fv));

		case IF:

			return new ExpressionNode(kernel, left, Operation.IF, right.integral(fv));

		case LOG:
			// base e (ln)
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).ln().multiply(left).subtract(left).divide(coeff);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).ln().multiply(left).subtract(left).multiply(coeff);
			}

			break;

		case LOG10:
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).ln().multiply(left).subtract(left).divide(wrap(10).ln().multiply(coeff));
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).ln().multiply(left).subtract(left).multiply(coeff).divide(wrap(10).ln());
			}

			break;

		case LOG2:
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).ln().multiply(left).subtract(left).divide(wrap(2).ln().multiply(coeff));
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).ln().multiply(left).subtract(left).multiply(coeff).divide(wrap(2).ln());
			}

			break;



		case NROOT:			
			if (right.isNumberValue() && !right.contains(fv)) {
				coeff = getLinearCoefficient(fv, left);

				if (!Double.isNaN(coeff)) {
					return wrap(left).nroot(right).multiply(left).multiply(right).divide((right.evaluateNum().getDouble() + 1) * coeff);
				}

				coeff = getLinearCoefficientDiv(fv, left);

				if (!Double.isNaN(coeff)) {
					return wrap(left).nroot(right).multiply(left).multiply(right).divide((right.evaluateNum().getDouble() + 1) / coeff);
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
				return wrap(left).sqrt().multiply(left).multiply(coeff * 2d / 3d);
			}

			break;
		case CBRT:
			coeff = getLinearCoefficient(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).cbrt().multiply(left).divide(coeff * 4d / 3d);
			}

			coeff = getLinearCoefficientDiv(fv, left);

			if (!Double.isNaN(coeff)) {
				return wrap(left).cbrt().multiply(left).multiply(coeff * 3d / 4d);
			}

			break;

		}

		App.error("unhandled operation in derivative() (no CAS version): "+operation.toString());

		// undefined
		return wrap(Double.NaN);
	}


	/**
	 * @param n n
	 * @return nth root of this
	 */
	public ExpressionNode nroot(ExpressionValue n) {
		return new ExpressionNode(kernel, this, Operation.NROOT, n);
	}

	private ExpressionNode linearIntegral(int i, Operation op, FunctionVariable fv) {
		if (left == fv) {
			return new ExpressionNode(kernel, left, op, null).multiplyR(i);				
		}

		double coeff = getLinearCoefficient(fv, left);

		if (!Double.isNaN(coeff)) {
			return new ExpressionNode(kernel, left, op, null).multiplyR(i).divide(coeff);		
		}

		coeff = getLinearCoefficientDiv(fv, left);

		if (!Double.isNaN(coeff)) {
			return new ExpressionNode(kernel, left, op, null).multiply(coeff).multiplyR(i);		
		}

		App.debug("not linear integral");
		return wrap(Double.NaN);
	}

	/**
	 * get coefficient from simple linear expression with coefficient as divide eg 
	 * 3 * x + 1
	 * 
	 * returns Double.NaN if it's not in the correct form
	 */
	private static double getLinearCoefficient(FunctionVariable fv, ExpressionValue ev2) {

		// just x
		if (ev2 == fv) {
			return 1;
		}

		ExpressionValue ev = ev2;
		double factor = 1;
		Operation op;

		// 3x+1 or 1+3x or 3x-1 or 1-3x
		if (ev.isExpressionNode() && (op = ((ExpressionNode) ev).getOperation()).isPlusorMinus() ) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.left.isNumberValue() && !en.left.contains(fv)) {
				//strip off the "+1" etc
				ev = en.right;
				factor = op.equals(Operation.PLUS) ? 1 : -1;
			} else if (en.right.isNumberValue() && !en.right.contains(fv)) {
				//strip off the "+1" etc
				ev = en.left;
				factor = 1;
			}
		}

		// x+2 or 2-x
		if (ev == fv) {
			return factor;
		}		

		// 3*x or x*3
		if (ev.isExpressionNode() && ((ExpressionNode) ev).getOperation().equals(Operation.MULTIPLY) ) {
			ExpressionNode en = (ExpressionNode) ev;
			if (en.left == fv && en.right.isNumberValue() && !en.right.contains(fv)) {
				return ((NumberValue) en.right).getDouble() * factor;
				//return wrap(en.right).multiply(factor);
			} else if (en.right == fv && en.left.isNumberValue() && !en.left.contains(fv)) {
				return ((NumberValue) en.left).getDouble() * factor;
				//return wrap(en.left).multiply(factor);
			}
		}

		// not (simple) linear
		return Double.NaN;
	}

	/**
	 * get coefficient from simple linear expression with coefficient as divide eg 
	 * x / 3 + 1
	 * 
	 * returns Double.NaN if it's not in the correct form
	 */
	private static double getLinearCoefficientDiv(FunctionVariable fv, ExpressionValue ev2) {

		ExpressionValue ev = ev2;
		double factor = 1;
		Operation op;

		// x/3+1 or 1+x/3 or x/3-1 or 1-x/3
		if (ev.isExpressionNode() && (op = ((ExpressionNode) ev).getOperation()).isPlusorMinus() ) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.left.isNumberValue() && !en.left.contains(fv)) {
				//strip off the "+1" etc
				ev = en.right;
				factor = op.equals(Operation.PLUS) ? 1 : -1;
			} else if (en.right.isNumberValue() && !en.right.contains(fv)) {
				//strip off the "+1" etc
				ev = en.left;
				factor = 1;
			}
		}

		// x/3
		if (ev.isExpressionNode() && ((ExpressionNode) ev).getOperation().equals(Operation.DIVIDE) ) {
			ExpressionNode en = (ExpressionNode) ev;
			if (en.left == fv && en.right.isNumberValue() && !en.right.contains(fv)) {
				return ((NumberValue) en.right).getDouble() * factor;
			}
		}

		// not (simple) linear
		return Double.NaN;
	}

	private ExpressionNode wrap(double n) {
		return wrap(new MyDouble(kernel, n));
	}

	private static ExpressionNode wrap(ExpressionValue ev) {

		if (ev.isExpressionNode()) {
			return (ExpressionNode) ev;
		}

		return new ExpressionNode(ev.getKernel(), ev, Operation.NO_OPERATION, null);
	}

	/**
	 * @return whether the top-level operation is IF / IF_ELSE
	 */
	public boolean isConditional(){
		return operation == Operation.IF || operation == Operation.IF_ELSE;
	}

	/**
	 * Builds an if-else expression based on this condition
	 * @param ifBranch if branch
	 * @param elseBranch else branch
	 * @return if-else expression
	 */
	public ExpressionNode ifElse(ExpressionValue ifBranch, ExpressionValue elseBranch) {
		return new ExpressionNode(kernel,new MyNumberPair(kernel,this,ifBranch),Operation.IF_ELSE,elseBranch);
	}
}
