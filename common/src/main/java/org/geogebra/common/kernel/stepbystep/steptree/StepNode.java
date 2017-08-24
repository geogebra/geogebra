package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Variable;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.plugin.Operation;

public abstract class StepNode {

	/**
	 * @param sn the tree to be compared to this
	 * @return whether the two trees are exactly equal (order of operations matters)
	 */
	public abstract boolean equals(StepNode sn);

	/**
	 * @param sn the tree to be compared to this
	 * @return 0, if the two trees are equal, 1, if this has a higher priority, -1, if lower
	 */
	public int compareTo(StepNode sn) {
		int a = getPriority(this);
		int b = getPriority(sn);

		if (a == b) {
			if (this instanceof StepConstant) {
				return Double.compare(getValue(), sn.getValue());
			} else if (this instanceof StepOperation) {
				int cmp = Double.compare(degree(), sn.degree());

				if (cmp != 0) {
					return cmp;
				}

				StepOperation so1 = (StepOperation) this;
				StepOperation so2 = (StepOperation) sn;

				cmp = Integer.compare(so1.noOfOperands(), so2.noOfOperands());

				if (cmp != 0) {
					return cmp;
				}

				for (int i = 0; i < so1.noOfOperands(); i++) {
					cmp = so1.getSubTree(i).compareTo(so2.getSubTree(i));

					if (cmp != 0) {
						return cmp;
					}
				}

				return 0;
			}
			return toString().compareTo(sn.toString());
		}

		return a - b;
	}

	private static int getPriority(StepNode sn) {
		if (sn.nonSpecialConstant()) {
			return 0;
		} else if (sn instanceof StepArbitraryConstant) {
			return 1;
		} else if (sn.specialConstant()) {
			return 2;
		} else if (sn instanceof StepVariable) {
			return 3;
		} else if (sn.isOperation()) {
			return 4;
		}
		return 5;
	}

	public double degree() {
		if (this instanceof StepVariable) {
			return 1;
		} else if (this instanceof StepConstant) {
			return 0;
		} else if (isOperation()) {
			StepOperation so = (StepOperation) this;

			switch (so.getOperation()) {
			case MINUS:
				return so.getSubTree(0).degree();
			case PLUS:
				double max = 0;
				for (int i = 0; i < so.noOfOperands(); i++) {
					double temp = so.getSubTree(i).degree();
					if (temp > max) {
						max = temp;
					}
				}
				return max;
			case POWER:
				return so.getSubTree(0).degree() * so.getSubTree(1).getValue();
			case MULTIPLY:
				double p = 0;
				for (int i = 0; i < so.noOfOperands(); i++) {
					p += so.getSubTree(i).degree();
				}
				return p;
			case DIVIDE:
				return so.getSubTree(0).degree() - so.getSubTree(1).degree();
			case NROOT:
				return so.getSubTree(0).degree() / so.getSubTree(1).getValue();
			}
		}

		return Double.NaN;
	}

	/**
	 * @return deep copy of the tree. Use this, if you want to preserve the tree after a regroup
	 */
	public abstract StepNode deepCopy();

	/**
	 * @return whether this node is an instance of StepOperation
	 */
	public abstract boolean isOperation();

	/**
	 * @param op
	 * @return whether the node is instance of StepOperation and its operation equals to op
	 */
	public abstract boolean isOperation(Operation op);

	/**
	 * @return whether this expression contains variables
	 */
	public abstract boolean isConstant();

	public abstract boolean canBeEvaluated();

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
	public abstract double getValueAt(StepNode variable, double value);

	/**
	 * @return the non-variable coefficient of the tree (ex: 3 sqrt(3) x -> 3 sqrt(3))
	 */
	public abstract StepNode getCoefficient();

	/**
	 * @return the variable part of the tree (ex: 3 x (1/sqrt(x)) -> x (1/sqrt(x)))
	 */
	public abstract StepNode getVariable();

	/**
	 * @return the StepConstant coefficient of the tree (ex: 3 sqrt(3) -> 3)
	 */
	public abstract StepNode getIntegerCoefficient();

	public abstract StepNode getNonInteger();

	/**
	 * @return the tree, formatted in LaTeX
	 */
	public abstract String toLaTeXString();

	/**
	 * @return the tree, regrouped (destroys the tree, use only in assignments)
	 */
	public abstract StepNode regroup();

	public abstract StepNode regroup(Boolean[] changed);

	/**
	 * @return the tree, expanded (destroys the tree, use only in assignments)
	 */
	public abstract StepNode expand(Boolean[] changed);

	/**
	 * @return the tree, fully simplified (destroys the tree, use only in assignments)
	 */
	public abstract StepNode simplify();

	public boolean nonSpecialConstant() {
		return this instanceof StepConstant && !isEqual(getValue(), Math.PI) && !isEqual(getValue(), Math.E);
	}

	public boolean specialConstant() {
		return this instanceof StepConstant && (isEqual(getValue(), Math.PI) || isEqual(getValue(), Math.E));
	}

	public StepNode replace(StepNode from, StepNode to) {
		if (equals(from)) {
			return to;
		}
		if (isOperation()) {
			StepOperation so = new StepOperation(((StepOperation) this).getOperation());
			for (int i = 0; i < ((StepOperation) this).noOfOperands(); i++) {
				so.addSubTree(((StepOperation) this).getSubTree(i).replace(from, to));
			}
			return so;
		}
		return this;
	}

	/**
	 * @param s string to be parsed
	 * @param parser GeoGebra parser
	 * @return the string s, parsed as a StepTree
	 */
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

	/**
	 * @param ev ExpressionValue to be converted
	 * @return ev converted to StepTree
	 */
	public static StepNode convertExpression(ExpressionValue ev) {
		if (ev instanceof ExpressionNode) {
			switch (((ExpressionNode) ev).getOperation()) {
			case NO_OPERATION:
				return convertExpression(((ExpressionNode) ev).getLeft());
			case SIN:
			case COS:
			case TAN:
			case CSC:
			case SEC:
			case COT:
				return apply(convertExpression(((ExpressionNode) ev).getLeft()), ((ExpressionNode) ev).getOperation());
			case SQRT:
				return root(convertExpression(((ExpressionNode) ev).getLeft()), 2);
			case MINUS:
				return add(convertExpression(((ExpressionNode) ev).getLeft()), minus(convertExpression(((ExpressionNode) ev).getRight())));
			case ABS:
				return abs(convertExpression(((ExpressionNode) ev).getLeft()));
			case MULTIPLY:
				if (((ExpressionNode) ev).getLeft().isConstant() && ((ExpressionNode) ev).getLeft().evaluateDouble() == -1) {
					return minus(convertExpression(((ExpressionNode) ev).getRight()));
				}
			default:
				StepOperation so = new StepOperation(((ExpressionNode) ev).getOperation());
				so.addSubTree(convertExpression(((ExpressionNode) ev).getLeft()));
				so.addSubTree(convertExpression(((ExpressionNode) ev).getRight()));
				return so;
			}
		}
		if (ev instanceof FunctionVariable || ev instanceof Variable) {
			return new StepVariable(ev.toString(StringTemplate.defaultTemplate));
		}
		if (ev instanceof MyDouble) {
			return new StepConstant(((MyDouble) ev).getDouble());
		}
		return null;
	}

	/**
	 * @param toConvert StepTree to convert
	 * @param var variable to group in
	 * @return toConvert in a polynomial format (as an array of coefficients) toConvert = sum(returned[i] * var^i)
	 */
	public static StepNode[] convertToPolynomial(StepNode toConvert, StepNode var) {
		List<StepNode> poli = new ArrayList<StepNode>();
		StepNode p = toConvert.deepCopy().simplify();

		StepNode temp = StepHelper.findConstant(p);

		poli.add(temp);
		p = StepNode.subtract(p, temp).regroup();

		int pow = 1;
		while (!p.isConstant()) {
			temp = StepHelper.findCoefficient(p, (pow == 1 ? var : StepNode.power(var, pow)));
			poli.add(temp);

			if (temp != null) {
				p = StepNode.subtract(p, StepNode.multiply(temp, (pow == 1 ? var : StepNode.power(var, pow)))).regroup();
			}
			pow++;
		}
		return poli.toArray(new StepNode[0]);
	}

	/**
	 * @param r dividend
	 * @param d divisor
	 * @param var variable
	 * @return the quotient of the two polynomials, null if they can not be divided
	 */
	public static StepNode polynomialDivision(StepNode r, StepNode d, StepNode var) {
		if (r == null || StepHelper.degree(r) < 1) {
			return null;
		}
		if (d == null || StepHelper.degree(d) < 1) {
			return null;
		}

		StepNode[] arrayD = StepNode.convertToPolynomial(d, var);
		StepNode[] arrayR = StepNode.convertToPolynomial(r, var);

		int leadR = arrayR.length - 1;
		int leadD = arrayD.length - 1;

		StepNode q = new StepConstant(0);

		while ((leadR != 0 || (arrayR[0] != null && arrayR[0].getValue() != 0)) && leadR >= leadD) {
			StepNode t = StepNode.multiply(StepNode.divide(arrayR[leadR], arrayD[leadD]), StepNode.power(var, leadR - leadD)).regroup();
			q = StepNode.add(q, t);

			StepNode[] td = StepNode.convertToPolynomial(StepNode.multiply(t, d).simplify(), var);

			for (int i = 0; i < td.length; i++) {
				if (td[i] != null) {
					arrayR[i] = StepNode.subtract(arrayR[i], td[i]).regroup();
				}
			}

			while (leadR > 0 && (arrayR[leadR] == null || arrayR[leadR].getValue() == 0)) {
				leadR--;
			}
		}

		if (leadR == 0 && (arrayR[0] == null || arrayR[0].getValue() == 0)) {
			return q.regroup();
		}
		return null;
	}

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

	public static StepNode subtract(StepNode a, StepNode b) {
		return add(a, minus(b));
	}

	public static StepNode subtract(StepNode a, double b) {
		return subtract(a, new StepConstant(b));
	}

	public static StepNode subtract(double a, StepNode b) {
		return subtract(new StepConstant(a), b);
	}

	public static StepNode minus(StepNode a) {
		if (a == null) {
			return null;
		}
		if (a.nonSpecialConstant()) {
			return new StepConstant(-a.getValue());
		}
		StepOperation so = new StepOperation(Operation.MINUS);
		so.addSubTree(a.deepCopy());
		return so;
	}

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

	public static StepNode multiply(double a, StepNode b) {
		return multiply(new StepConstant(a), b);
	}

	public static StepNode divide(StepNode a, StepNode b) {
		if (a == null) {
			if (b == null) {
				return null;
			}
			return StepNode.divide(new StepConstant(1), b);
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

	public static StepNode divide(double a, double b) {
		return divide(new StepConstant(a), new StepConstant(b));
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

	public static StepNode power(StepNode a, double b) {
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

	public static StepNode root(StepNode a, double b) {
		return root(a, new StepConstant(b));
	}

	public static StepNode abs(StepNode a) {
		if (a == null) {
			return null;
		}

		StepOperation so = new StepOperation(Operation.ABS);
		so.addSubTree(a.deepCopy());
		return so;
	}

	public static StepNode apply(StepNode a, Operation op) {
		if (a == null) {
			return null;
		}

		StepOperation so = new StepOperation(op);
		so.addSubTree(a.deepCopy());
		return so;
	}

	public static long gcd(long a, long b) {
		if (b == 0) {
			return a;
		}
		return gcd(b, a % b);
	}

	public static long lcm(long a, long b) {
		if (a == 0 || b == 0) {
			return 0;
		}
		return a * b / gcd(a, b);
	}

	public static boolean isEqual(double a, double b) {
		return Math.abs(a - b) < 0.0000001;
	}
}
