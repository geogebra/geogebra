package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Variable;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;

public abstract class StepNode {

	protected int color;

	public abstract boolean equals(StepNode sn);

	/**
	 * @param sn the tree to be compared to this
	 * @return 0, if the two trees are equal, 1, if this has a higher priority, -1, if lower
	 */
	public int compareTo(StepNode sn) {
		int a = getPriority(this);
		int b = getPriority(sn);

		if (a == b) {
			if (this.canBeEvaluated()) {
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
		} else if (sn instanceof StepOperation) {
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
	public abstract String toLaTeXString(Localization loc);

	public abstract String toLaTeXString(Localization loc, boolean colored);

	protected String getColorHex() {
		switch (color % 5) {
		case 1:
			return "#" + StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_RED);
		case 2:
			return "#" + StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_BLUE);
		case 3:
			return "#" + StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_GREEN);
		case 4:
			return "#" + StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_PURPLE);
		case 0:
			return "#" + StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_ORANGE);
		default:
			return "#" + StringUtil.toHexString(GeoGebraColorConstants.GEOGEBRA_OBJECT_BLACK);
		}
	}

	public void setColor(int color) {
		this.color = color;
		if (isOperation()) {
			for (int i = 0; i < ((StepOperation) this).noOfOperands(); i++) {
				((StepOperation) this).getSubTree(i).setColor(color);
			}
		}
	}

	public void cleanColors() {
		setColor(0);
	}

	/**
	 * @return the tree, regrouped (destroys the tree, use only in assignments)
	 */
	public abstract StepNode regroup();

	public abstract StepNode regroup(SolutionBuilder sb);

	/**
	 * @return the tree, regrouped and expanded (destroys the tree, use only in assignments)
	 */
	public abstract StepNode expand(SolutionBuilder sb);

	public boolean nonSpecialConstant() {
		return this instanceof StepConstant && !isEqual(getValue(), Math.PI) && !isEqual(getValue(), Math.E)
				|| isOperation(Operation.MINUS) && ((StepOperation) this).getSubTree(0).nonSpecialConstant();
	}

	public boolean specialConstant() {
		return this instanceof StepConstant && (isEqual(getValue(), Math.PI) || isEqual(getValue(), Math.E));
	}

	public boolean isInteger() {
		return this instanceof StepConstant && isEqual(Math.round(this.getValue()), this.getValue());
	}

	public boolean isSquareRoot() {
		return isOperation(Operation.NROOT) && isEqual(((StepOperation) this).getSubTree(1), 2);
	}

	public boolean isSquare() {
		return isOperation(Operation.POWER) && isEven(((StepOperation) this).getSubTree(1));
	}

	/**
	 * Only if isSquare() is true!
	 * @return the square root of the expression
	 */
	public StepNode getSquareRoot() {
		if (isSquare()) {
			StepOperation so = (StepOperation) this;
			return nonTrivialPower(so.getSubTree(0), so.getSubTree(1).getValue() / 2);
		}

		return null;
	}

	public boolean isTrigonometric() {
		return isOperation(Operation.SIN) || isOperation(Operation.COS) || isOperation(Operation.TAN) || isOperation(Operation.CSC)
				|| isOperation(Operation.SEC) || isOperation(Operation.CSC);
	}

	public boolean isInverseTrigonometric() {
		return isOperation(Operation.ARCSIN) || isOperation(Operation.ARCCOS) || isOperation(Operation.ARCTAN);
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
		if (ev instanceof Equation) {
			StepOperation so = new StepOperation(Operation.EQUAL_BOOLEAN);
			so.addSubTree(convertExpression(((Equation) ev).getLHS()));
			so.addSubTree(convertExpression(((Equation) ev).getRHS()));
			return so;
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
	public static StepNode[] convertToPolynomial(StepNode toConvert, StepVariable var) {
		List<StepNode> poli = new ArrayList<StepNode>();

		StepNode temp = StepHelper.findConstant(toConvert);
		poli.add(temp);

		for (int pow = 1; pow <= StepHelper.degree(toConvert); pow++) {
			poli.add(StepHelper.findCoefficient(toConvert, (pow == 1 ? var : StepNode.power(var, pow))));
		}

		return poli.toArray(new StepNode[0]);
	}

	private static boolean isMonom(StepNode sn) {
		if (sn instanceof StepVariable) {
			return true;
		}

		if (sn.isOperation(Operation.POWER)) {
			StepOperation so = (StepOperation) sn;

			return so.getSubTree(0) instanceof StepVariable && closeToAnInteger(so.getSubTree(1));
		}

		return false;
	}

	private static boolean isPolynomial(StepNode sn) {
		if (sn.isConstant() || isMonom(sn)) {
			return true;
		}

		if(sn.isOperation()) {
			StepOperation so = (StepOperation) sn;
			
			if(so.isOperation(Operation.PLUS)) {
				for(int i = 0; i < so.noOfOperands(); i++) {
					if(!isPolynomial(so.getSubTree(i))) {
						return false;
					}
				}
				return true;
			}

			if (so.isOperation(Operation.MULTIPLY) && so.noOfOperands() == 2) {
				return so.getSubTree(0).isConstant() && isMonom(so.getSubTree(1));
			}

			if (so.isOperation(Operation.MINUS)) {
				return isPolynomial(so.getSubTree(0)) && !so.getSubTree(0).isOperation(Operation.PLUS);
			}
		}

		return false;
	}

	/**
	 * @param r dividend
	 * @param d divisor
	 * @param var variable
	 * @return the quotient of the two polynomials, null if they can not be divided
	 */
	public static StepNode polynomialDivision(StepNode r, StepNode d, StepVariable var) {
		if (!isPolynomial(r) || !isPolynomial(d)) {
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

			StepNode[] td = StepNode.convertToPolynomial(StepNode.multiply(t, d).expand(null), var);

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

	/**
	 * tries to divide a by b
	 * @param a dividend
	 * @param b divisor
	 * @return result, if polynomial division was successful, null otherwise
	 */
	public static StepNode tryToDivide(StepNode a, StepNode b) {
		List<StepVariable> listA = new ArrayList<StepVariable>();
		List<StepVariable> listB = new ArrayList<StepVariable>();

		getListOfVariables(a, listA);
		getListOfVariables(b, listB);

		for (int i = 0; i < listA.size(); i++) {
			for (int j = 0; j < listB.size(); j++) {
				if (listA.get(i).equals(listB.get(j))) {
					StepNode result = polynomialDivision(a, b, listA.get(i));
					if (result != null) {
						return result;
					}
				}
			}
		}

		return null;
	}

	private static void getListOfVariables(StepNode sn, List<StepVariable> variableList) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;
			for (int i = 0; i < so.noOfOperands(); i++) {
				getListOfVariables(so.getSubTree(i), variableList);
			}
		}

		if (sn instanceof StepVariable) {
			for (int i = 0; i < variableList.size(); i++) {
				if (variableList.get(i).equals(sn)) {
					return;
				}
			}

			variableList.add((StepVariable) sn);
		}
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

	public static StepOperation equal(StepNode a, StepNode b) {
		if (a == null) {
			return equal(new StepConstant(0), b);
		}
		if (b == null) {
			return equal(a, new StepConstant(0));
		}

		StepOperation so = new StepOperation(Operation.EQUAL_BOOLEAN);
		so.addSubTree(a.deepCopy());
		so.addSubTree(b.deepCopy());
		return so;
	}

	public static StepNode in(StepNode a, StepNode b) {
		if (a == null || b == null) {
			return null;
		}

		StepOperation so = new StepOperation(Operation.IS_ELEMENT_OF);
		so.addSubTree(a.deepCopy());
		so.addSubTree(b.deepCopy());
		return so;
	}

	public static StepNode invert(StepNode a) {
		if (a == null) {
			return null;
		}

		if (a.isOperation(Operation.DIVIDE)) {
			if (isEqual(((StepOperation) a).getSubTree(0), 1)) {
				return ((StepOperation) a).getSubTree(1);
			}
			return divide(((StepOperation) a).getSubTree(1), ((StepOperation) a).getSubTree(0));
		} else if (isEqual(a, 1) || isEqual(a, -1)) {
			return a;
		} else {
			return divide(new StepConstant(1), a);
		}
	}

	/**
	 * calculates currentFraction * base ^ exponent, and writes it in a nice form i.e.: makeFraction((x+1)/x, x+1, 1) ->
	 * ((x+1)(x+1))/x, makeFraction((x+1)/(x(x+1)), x, -1) -> (x+1)/(x(x+1)x)
	 */
	public static StepNode makeFraction(StepNode currentFraction, StepNode base, StepNode exponent) {
		StepNode nominator;
		StepNode denominator;

		if (currentFraction != null && currentFraction.isOperation(Operation.DIVIDE)) {
			nominator = ((StepOperation) currentFraction).getSubTree(0);
			denominator = ((StepOperation) currentFraction).getSubTree(1);
		} else {
			nominator = currentFraction;
			denominator = null;
		}

		if (exponent.getValue() > 0) {
			if (!isEqual(exponent.getValue(), 1) && closeToAnInteger(1 / exponent.getValue())) {
				nominator = nonTrivialProduct(nominator, StepNode.root(base, 1 / exponent.getValue()));
			} else {
				nominator = nonTrivialProduct(nominator, nonTrivialPower(base, exponent));
			}
		} else {
			if (!isEqual(exponent.getValue(), -1) && closeToAnInteger(1 / exponent.getValue())) {
				nominator = nonTrivialProduct(denominator, StepNode.root(base, -1 / exponent.getValue()));
			} else {
				denominator = nonTrivialProduct(denominator, nonTrivialPower(base, negate(exponent)));
			}
		}

		return divide(nominator, denominator);
	}

	/**
	 * returns a^b, except if b == 1, then it returns a, or if b == 0, then it returns 1
	 */
	public static StepNode nonTrivialPower(StepNode a, StepNode b) {
		if (a != null && b != null) {
			if (isEqual(b, 1)) {
				return a;
			} else if (isEqual(b, 0)) {
				return new StepConstant(1);
			}
		}

		return power(a, b);
	}

	public static StepNode nonTrivialPower(StepNode a, double b) {
		return nonTrivialPower(a, new StepConstant(b));
	}

	/**
	 * return a*b, except if: a == 1 -> b, a == -1 -> -b and vice versa
	 */
	public static StepNode nonTrivialProduct(StepNode a, StepNode b) {
		if (a != null && b != null) {
			if (isEqual(a, 1)) {
				return b;
			} else if (isEqual(a, -1)) {
				return minus(b);
			} else if (isEqual(b, 1)) {
				return a;
			} else if (isEqual(b, -1)) {
				return minus(a);
			}
		}

		return multiply(a, b);
	}

	public static StepNode negate(StepNode sn) {
		if (sn.nonSpecialConstant()) {
			return new StepConstant(-sn.getValue());
		}
		if (sn.isOperation(Operation.MINUS)) {
			return ((StepOperation) sn).getSubTree(0);
		}
		if (sn.isOperation(Operation.MULTIPLY) && isNegative(((StepOperation) sn).getSubTree(0))) {
			StepNode so = negate(((StepOperation) sn).getSubTree(0));
			for (int i = 1; i < ((StepOperation) sn).noOfOperands(); i++) {
				so = StepNode.multiply(so, ((StepOperation) sn).getSubTree(i));
			}
			return so;
		}
		if (sn.isOperation(Operation.DIVIDE)) {
			if (isNegative(((StepOperation) sn).getSubTree(0))) {
				return divide(negate(((StepOperation) sn).getSubTree(0)), ((StepOperation) sn).getSubTree(1));
			}
			return minus(sn);
		}

		return StepNode.minus(sn);
	}

	/**
	 * returns the largest b-th that divides a (for example (8, 2) -> 4, (8, 3) -> 8, (108, 2) -> 36)
	 * 
	 * @param a base
	 * @param b exponent
	 * @return largest b-th power that divides a
	 */
	public static long largestNthPower(double a, double b) {
		if (closeToAnInteger(a) && closeToAnInteger(b)) {
			long x = Math.round(a);
			long y = Math.round(b);

			int power = 1;
			int count = 0;

			while (x % 2 == 0) {
				count++;
				x /= 2;
			}

			count /= y;
			power *= Math.pow(2, count);

			for (int i = 3; i < x; i += 2) {
				count = 0;

				while (x % i == 0) {
					count++;
					x /= i;
				}

				count /= y;
				power *= Math.pow(i, count);
			}

			return power;
		}

		return 1;
	}

	public static long getDenominator(StepNode sn) {
		if (sn.nonSpecialConstant()) {
			return 1;
		} else if (sn.isOperation(Operation.MINUS)) {
			return getDenominator(((StepOperation) sn).getSubTree(0));
		} else if (sn.isOperation(Operation.DIVIDE)) {
			if (closeToAnInteger(((StepOperation) sn).getSubTree(0)) && closeToAnInteger(((StepOperation) sn).getSubTree(1))) {
				return Math.round(((StepOperation) sn).getSubTree(1).getValue());
			}
		}
		return 0;
	}

	public static StepNode getNumerator(StepNode sn) {
		if (sn.nonSpecialConstant()) {
			return sn;
		} else if (sn.isOperation(Operation.MINUS)) {
			return minus(getNumerator(((StepOperation) sn).getSubTree(0)));
		} else if (sn.isOperation(Operation.DIVIDE)) {
			return ((StepOperation) sn).getSubTree(0);
		}
		return null;
	}

	public static StepNode inverseTrigoLookup(StepOperation so) {
		String[] arguments = new String[] { "-1", "-(nroot(3, 2))/(2)", "-(nroot(2, 2))/(2)", "-(1)/(2)", "0", "(1)/(2)",
				"(nroot(2, 2))/(2)", "(nroot(3, 2))/(2)", "1" };
		String[] argumentsTan = new String[] { "", "-nroot(3, 2)", "-1", "-nroot(3, 2)/3", "0", "nroot(3, 2)/3", "1", "nroot(3, 2)", "" };

		StepNode pi = new StepConstant(Math.PI);
		StepNode[] valuesSinTan = new StepNode[] { minus(divide(pi, 2)), minus(divide(pi, 3)), minus(divide(pi, 4)), minus(divide(pi, 6)),
				new StepConstant(0), divide(pi, 6), divide(pi, 4), divide(pi, 3), divide(pi, 2) };
		StepNode[] valuesCos = new StepNode[] { pi, divide(multiply(5, pi), 6), divide(multiply(3, pi), 4), divide(multiply(2, pi), 3),
				divide(pi, 2), divide(pi, 3), divide(pi, 4), divide(pi, 6), new StepConstant(0) };

		String currentArgument = so.getSubTree(0).toString();
		for (int i = 0; i < arguments.length; i++) {
			if (currentArgument.equals(arguments[i])) {
				if (so.isOperation(Operation.ARCSIN)) {
					return valuesSinTan[i];
				} else if (so.isOperation(Operation.ARCCOS)) {
					return valuesCos[i];
				}
			} else if (currentArgument.equals(argumentsTan[i])) {
				if (so.isOperation(Operation.ARCTAN)) {
					return valuesSinTan[i];
				}
			}
		}

		return null;
	}

	/**
	 * returns the largest n, for which x is a perfect nth power
	 */
	public static long getIntegerPower(long x) {
		long temp = x;
		if (temp < 0) {
			temp = -temp;
		}

		if (temp == 1) {
			return 1;
		}

		long power = 0;
		long currentPower;
		for (int i = 2; i <= temp; i++) {
			currentPower = 0;
			while (temp % i == 0) {
				currentPower++;
				temp /= i;
			}
			power = gcd(power, currentPower);
		}
		return power;
	}

	public static void getBasesAndExponents(StepNode sn, StepNode currentExp, List<StepNode> bases, List<StepNode> exponents) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			switch (so.getOperation()) {
			case MULTIPLY:
				for (int i = 0; i < so.noOfOperands(); i++) {
					getBasesAndExponents(so.getSubTree(i), currentExp, bases, exponents);
				}
				return;
			case MINUS:
				if (!so.getSubTree(0).nonSpecialConstant()) {
					bases.add(new StepConstant(-1));
					exponents.add(new StepConstant(1));
					getBasesAndExponents(so.getSubTree(0), currentExp, bases, exponents);
					return;
				}
				break;
			case DIVIDE:
				getBasesAndExponents(so.getSubTree(0), currentExp, bases, exponents);
				getBasesAndExponents(so.getSubTree(1), multiply(-1, currentExp), bases, exponents);
				return;
			case POWER:
				bases.add(so.getSubTree(0));
				exponents.add(multiply(currentExp, so.getSubTree(1)));
				return;
			}
		}

		bases.add(sn);
		exponents.add(currentExp == null ? new StepConstant(1) : currentExp);
	}

	public static boolean isNegative(StepNode sn) {
		return (sn.nonSpecialConstant() && sn.getValue() < 0) || sn.isOperation(Operation.MINUS)
				|| sn.isOperation(Operation.MULTIPLY) && isNegative(((StepOperation) sn).getSubTree(0));
	}

	public static boolean closeToAnInteger(double d) {
		return Math.abs(Math.round(d) - d) < 0.0000001;
	}

	public static boolean closeToAnInteger(StepNode sn) {
		return (sn != null && sn.isOperation(Operation.MINUS) && closeToAnInteger(((StepOperation) sn).getSubTree(0)))
				|| sn instanceof StepConstant && closeToAnInteger(sn.getValue());
	}

	public static Operation getInverse(Operation op) {
		switch (op) {
		case SIN:
			return Operation.ARCSIN;
		case COS:
			return Operation.ARCCOS;
		case TAN:
			return Operation.ARCTAN;
		default:
			return Operation.NO_OPERATION;
		}
	}

	public static long gcd(long a, long b) {
		if (b == 0) {
			return a;
		}
		return gcd(b, a % b);
	}

	public static long gcd(StepNode a, StepNode b) {
		return gcd(Math.round(a.getValue()), Math.round(b.getValue()));
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

	public static boolean isEqual(StepNode a, double b) {
		return a instanceof StepConstant && isEqual(a.getValue(), b);
	}

	public static boolean isEven(double d) {
		return isEqual(d % 2, 0);
	}

	public static boolean isEven(StepNode sn) {
		return sn.canBeEvaluated() && isEqual(sn.getValue() % 2, 0);
	}

	public static boolean isOdd(double d) {
		return isEqual(d % 2, 1);
	}

}
