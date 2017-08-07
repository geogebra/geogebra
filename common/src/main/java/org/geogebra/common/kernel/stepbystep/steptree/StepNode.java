package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.plugin.Operation;

public abstract class StepNode {

	public abstract boolean equals(StepNode sn);

	public abstract StepNode deepCopy();

	/**
	 * @return whether this node is an instance of StepByStepOperation
	 */
	public abstract boolean isOperation();

	public abstract boolean isOperation(Operation op);

	/**
	 * @return whether this expression contains variables
	 */
	public abstract boolean isConstant();

	/**
	 * @return the priority of the top node (1 - addition and subtraction, 2 - multiplication and division, 3 - roots and
	 *         exponents, 4 - constants and variables)
	 */
	public abstract int getPriority();

	/**
	 * @return the numeric value of the tree.
	 */
	public abstract double getValue();

	/**
	 * @param variable - the name of the variable to be replaced
	 * @param value - the value to be replaced with
	 * @return the value of the tree after replacement
	 */
	public abstract double getValueAt(StepVariable variable, double value);

	public abstract StepNode getCoefficient();

	public abstract StepNode getVariable();

	public abstract StepNode getConstantCoefficient();

	/**
	 * @return the tree, formatted in LaTeX
	 */
	public abstract String toLaTeXString();

	public abstract StepNode constantRegroup();

	public abstract StepNode regroup();

	public abstract StepNode expand();

	public abstract StepNode simplify();

	public static StepNode getStepTree(String s, Parser parser) {
		if (s.isEmpty()) {
			return null;
		}

		try {
			ExpressionValue ev = parser.parseGeoGebraExpression(s);
			return convertExpression(ev);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static StepNode convertExpression(ExpressionValue ev) {
		if (ev instanceof ExpressionNode) {
			if (((ExpressionNode) ev).getOperation() == Operation.NO_OPERATION) {
				return convertExpression(((ExpressionNode) ev).getLeft());
			}
			if (((ExpressionNode) ev).getOperation() == Operation.SQRT) {
				return root(convertExpression(((ExpressionNode) ev).getLeft()), 2);
			}
			if (((ExpressionNode) ev).getOperation() == Operation.MINUS) {
				return add(convertExpression(((ExpressionNode) ev).getLeft()), minus(convertExpression(((ExpressionNode) ev).getRight())));
			}
			StepOperation so = new StepOperation(((ExpressionNode) ev).getOperation());
			so.addSubTree(convertExpression(((ExpressionNode) ev).getLeft()));
			so.addSubTree(convertExpression(((ExpressionNode) ev).getRight()));
			return so;
		}
		if (ev instanceof FunctionVariable) {
			return new StepVariable(((FunctionVariable) ev).getSetVarString());
		}
		if (ev instanceof MyDouble) {
			return new StepConstant(((MyDouble) ev).getDouble());
		}
		return null;
	}

	/**
	 * @param sn - tree to add to the current one
	 * @return the root of the new tree
	 */
	public static StepNode add(StepNode a, StepNode b) {
		if (a == null) {
			return b == null ? null : b.deepCopy();
		}
		if (b == null) {
			return a.deepCopy();
		}

		if (a.isOperation(Operation.PLUS)) {
			StepOperation copyofa = (StepOperation) a.deepCopy();

			if (b.isOperation(Operation.PLUS)) {
				for (int i = 0; i < ((StepOperation) b).noOfOperands(); i++) {
					copyofa.addSubTree(((StepOperation) b).getSubTree(i).deepCopy());
				}
			} else {
				copyofa.addSubTree(b.deepCopy());
			}
			
			return copyofa;
		}

		StepOperation so = new StepOperation(Operation.PLUS);
		so.addSubTree(a.deepCopy());
		so.addSubTree(b.deepCopy());
		return so;
	}

	public static StepNode add(StepNode a, double b) {
		return add(a, new StepConstant(b));
	}

	/**
	 * @param sn - the tree to subtract from the current one
	 * @return the root of the new tree
	 */
	public static StepNode subtract(StepNode a, StepNode b) {
		return add(a, minus(b));
	}

	public static StepNode subtract(StepNode a, double b) {
		return subtract(a, new StepConstant(b));
	}

	public static StepNode minus(StepNode a) {
		if (a == null) {
			return null;
		}
		if (a instanceof StepConstant) {
			return new StepConstant(-a.getValue());
		}
		StepOperation so = new StepOperation(Operation.MINUS);
		so.addSubTree(a.deepCopy());
		return so;
	}

	/**
	 * @param sn - the tree to be multiplied with
	 * @return the root of the new tree
	 */
	public static StepNode multiply(StepNode a, StepNode b) {
		if (a == null) {
			return b == null ? null : b.deepCopy();
		}
		if (b == null) {
			return a.deepCopy();
		}

		if (a.isOperation(Operation.MULTIPLY)) {
			StepNode copyofa = a.deepCopy();

			if (b.isOperation(Operation.MULTIPLY)) {
				for (int i = 0; i < ((StepOperation) b).noOfOperands(); i++) {
					((StepOperation) copyofa).addSubTree(((StepOperation) b).getSubTree(i).deepCopy());
				}
			} else {
				((StepOperation) copyofa).addSubTree(b.deepCopy());
			}

			return copyofa;
		}

		StepOperation so = new StepOperation(Operation.MULTIPLY);
		so.addSubTree(a.deepCopy());
		so.addSubTree(b.deepCopy());
		return so;
	}

	public static StepNode multiply(int a, StepNode b) {
		return multiply(new StepConstant(a), b);
	}

	/**
	 * @param sn - the tree to divide the current one with
	 * @return the root of the new tree
	 */
	public static StepNode divide(StepNode a, StepNode b) {
		if (a == null) {
			return null;
		}
		if (b == null) {
			return a.deepCopy();
		}

		StepOperation so = new StepOperation(Operation.DIVIDE);
		so.addSubTree(a.deepCopy());
		so.addSubTree(b.deepCopy());
		return so;
	}

	public static StepNode divide(StepNode a, double b) {
		return divide(a, new StepConstant(b));
	}

	public static StepNode power(StepNode a, StepNode b) {
		if (a == null) {
			return null;
		}
		if (b == null) {
			return a.deepCopy();
		}

		StepOperation so = new StepOperation(Operation.POWER);
		so.addSubTree(a.deepCopy());
		so.addSubTree(b.deepCopy());
		return so;
	}

	public static StepNode power(StepNode a, int b) {
		return power(a, new StepConstant(b));
	}

	public static StepNode root(StepNode a, StepNode b) {
		if (a == null) {
			return null;
		}
		if (b == null) {
			return a.deepCopy();
		}

		StepOperation so = new StepOperation(Operation.NROOT);
		so.addSubTree(a.deepCopy());
		so.addSubTree(b.deepCopy());
		return so;
	}

	public static StepNode root(StepNode a, int b) {
		return root(a, new StepConstant(b));
	}

	public abstract StepNode divideAndSimplify(double x);

}
