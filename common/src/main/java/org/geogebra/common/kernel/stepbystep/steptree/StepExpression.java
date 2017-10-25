package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;
import org.geogebra.common.plugin.Operation;

public abstract class StepExpression extends StepNode {

	@Override
	public abstract StepExpression deepCopy();

	/**
	 * @param sn
	 *            the tree to be compared to this
	 * @return 0, if the two trees are equal, 1, if this has a higher priority, -1,
	 *         if lower
	 */
	public int compareTo(StepExpression sn) {
		int a = getSortingPriority(this);
		int b = getSortingPriority(sn);

		if (a == b) {
			if (this.canBeEvaluated()) {
				return Double.compare(getValue(), sn.getValue());
			} else if (this instanceof StepOperation) {
				StepOperation so1 = (StepOperation) this;
				StepOperation so2 = (StepOperation) sn;

				int cmp = Integer.compare(so1.noOfOperands(), so2.noOfOperands());

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

	/**
	 * This priority is used only for sorting and has nothing to do with the
	 * precedence of operations
	 */
	private static int getSortingPriority(StepExpression sn) {
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

	/**
	 * !This does not mean that getValue() will not return NaN.
	 * StepArbitraryConstants are constants, but cannot be evaluated
	 * 
	 * @return whether this expression contains variables
	 */
	public abstract boolean isConstant();

	/**
	 * It guarantees that getValue() returns a non-NaN value. Except when division,
	 * Math.pow, or Math.sqrt fails.
	 * 
	 * @return whether this expression can be evaluated
	 */
	public abstract boolean canBeEvaluated();

	/**
	 * @return the numeric value of the tree.
	 */
	public abstract double getValue();

	/**
	 * @param variable
	 *            the name of the variable to be replaced
	 * @param value
	 *            the value to be replaced with
	 * @return the value of the tree after replacement
	 */
	public abstract double getValueAt(StepVariable variable, double value);

	/**
	 * @return the non-variable coefficient of the tree (ex: 3 sqrt(3) x -> 3
	 *         sqrt(3))
	 */
	public abstract StepExpression getCoefficient();

	/**
	 * @return the variable part of the tree (ex: 3 x (1/sqrt(x)) -> x (1/sqrt(x)))
	 */
	public abstract StepExpression getVariable();

	/**
	 * @return the StepConstant coefficient of the tree (ex: 3 sqrt(3) -> 3)
	 */
	public abstract StepExpression getIntegerCoefficient();

	/**
	 * @return the non-StepConstant part of the tree (ex: 3 sqrt(3) -> sqrt(3))
	 */
	public abstract StepExpression getNonInteger();

	/**
	 * Non-special constants are StepConstants and minus(StepConstant)s, except for
	 * pi and e
	 * 
	 * @return whether the current node is a nonSpecialConstant
	 */
	public boolean nonSpecialConstant() {
		return this instanceof StepConstant && !isEqual(getValue(), Math.PI) && !isEqual(getValue(), Math.E)
				|| isOperation(Operation.MINUS) && ((StepOperation) this).getSubTree(0).nonSpecialConstant();
	}

	/**
	 * Special constants are pi and e
	 * 
	 * @return whether the current node is a specialConstant
	 */
	public boolean specialConstant() {
		return this instanceof StepConstant && (isEqual(getValue(), Math.PI) || isEqual(getValue(), Math.E));
	}

	/**
	 * @return whether the current node is an integer (not Integer..)
	 */
	public boolean isInteger() {
		return this.nonSpecialConstant() && isEqual(Math.round(this.getValue()), this.getValue());
	}

	/**
	 * @return whether the current node is a squreRoot (that is nroot, with an
	 *         exponent of 2)
	 */
	public boolean isSquareRoot() {
		return isOperation(Operation.NROOT) && isEqual(((StepOperation) this).getSubTree(1), 2);
	}

	public boolean containsSquareRoot() {
		if (isSquareRoot()) {
			return true;
		}
		if (this instanceof StepOperation) {
			for (int i = 0; i < ((StepOperation) this).noOfOperands(); i++) {
				if (((StepOperation) this).getSubTree(i).containsSquareRoot()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean integerCoefficients(StepVariable sv) {
		if (isPolynomial(this)) {
			StepExpression[] coefficients = convertToPolynomial(this, sv);

			for (int i = 0; i < coefficients.length; i++) {
				if (coefficients[i] != null && !coefficients[i].isInteger()) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * A square is an expression of the form a^(2n), or a nonSpecialConstant. This
	 * definition is useful for factoring
	 * 
	 * @return whether the current node is a square
	 */
	public boolean isSquare() {
		return nonSpecialConstant() || isOperation(Operation.POWER) && isEven(((StepOperation) this).getSubTree(1));
	}

	public boolean isCube() {
		return isOperation(Operation.MINUS) && ((StepOperation) this).getSubTree(0).isCube() || nonSpecialConstant()
				|| isOperation(Operation.POWER) && isEqual(((StepOperation) this).getSubTree(1), 3);
	}

	/**
	 * Only if isSquare() is true!
	 * 
	 * @return the square root of the expression
	 */
	public StepExpression getSquareRoot() {
		if (isSquare()) {
			if (nonSpecialConstant()) {
				if (isEqual(Math.sqrt(getValue()), Math.floor(Math.sqrt(getValue())))) {
					return new StepConstant(Math.sqrt(getValue()));
				}
				return root(this, 2);
			}

			StepOperation so = (StepOperation) this;
			return nonTrivialPower(so.getSubTree(0), so.getSubTree(1).getValue() / 2);
		}

		return null;
	}

	public StepExpression getCubeRoot() {
		if (isCube()) {
			if (nonSpecialConstant()) {
				if (isEqual(Math.cbrt(getValue()), Math.floor(Math.cbrt(getValue())))) {
					return new StepConstant(Math.cbrt(getValue()));
				}
				return root(this, 3);
			}

			StepOperation so = (StepOperation) this;

			if (isOperation(Operation.MINUS)) {
				return so.getSubTree(0).getCubeRoot().negate();
			}

			return nonTrivialPower(so.getSubTree(0), so.getSubTree(1).getValue() / 3);
		}

		return null;
	}

	/**
	 * @return whether the current node is a trigonometric function
	 */
	public boolean isTrigonometric() {
		return isOperation(Operation.SIN) || isOperation(Operation.COS) || isOperation(Operation.TAN)
				|| isOperation(Operation.CSC) || isOperation(Operation.SEC) || isOperation(Operation.CSC);
	}

	/**
	 * @return whether the current node is an inverse trigonometric function
	 */
	public boolean isInverseTrigonometric() {
		return isOperation(Operation.ARCSIN) || isOperation(Operation.ARCCOS) || isOperation(Operation.ARCTAN);
	}

	/**
	 * @return the tree, regrouped (destroys the tree, use only in assignments)
	 */
	public StepExpression regroup() {
		return regroup(null);
	}

	/**
	 * @param sb
	 *            SolutionBuilder for the regroup steps
	 * @return the tree, regrouped (destroys the tree, use only in assignments)
	 */
	public StepExpression regroup(SolutionBuilder sb) {
		if (this instanceof StepOperation) {
			return (StepExpression) StepStrategies.defaultRegroup(this, sb);
		}

		return this;
	}

	/**
	 * @return the tree, regrouped and expanded (destroys the tree, use only in
	 *         assignments)
	 */
	public StepExpression expand() {
		return expand(null);
	}

	/**
	 * @param sb
	 *            SolutionBuilder for the expansion steps
	 * @return the tree, regrouped and expanded (destroys the tree, use only in
	 *         assignments)
	 */
	public StepExpression expand(SolutionBuilder sb) {
		if (this instanceof StepOperation) {
			return (StepExpression) StepStrategies.defaultExpand(this, sb);
		}

		return this;
	}

	/**
	 * @return the tree, factored (destroys the tree, use only in assignments)
	 */
	public StepExpression factor() {
		return factor(null);
	}

	/**
	 * @param sb
	 *            SolutionBuilder for the factoring steps
	 * @return the tree, factored (destroys the tree, use only in assignments)
	 */
	public StepExpression factor(SolutionBuilder sb) {
		if (this instanceof StepOperation) {
			return (StepExpression) StepStrategies.defaultFactor(this, sb);
		}

		return this;
	}

	/**
	 * @param from
	 *            StepNode to replace
	 * @param to
	 *            StepNode to replace with
	 * @return the tree, replaced
	 */
	public StepExpression replace(StepExpression from, StepExpression to) {
		if (equals(from)) {
			return to;
		}
		if (this instanceof StepOperation) {
			StepOperation so = new StepOperation(((StepOperation) this).getOperation());
			for (int i = 0; i < ((StepOperation) this).noOfOperands(); i++) {
				so.addSubTree(((StepOperation) this).getSubTree(i).replace(from, to));
			}
			return so;
		}
		return this;
	}

	/**
	 * @param toConvert
	 *            StepTree to convert
	 * @param var
	 *            variable to group in
	 * @return toConvert in a polynomial format (as an array of coefficients)
	 *         toConvert = sum(returned[i] * var^i)
	 */
	public static StepExpression[] convertToPolynomial(StepExpression toConvert, StepVariable var) {
		List<StepExpression> poli = new ArrayList<StepExpression>();

		StepExpression temp = StepHelper.findConstant(toConvert);
		poli.add(temp);

		for (int pow = 1; pow <= StepHelper.degree(toConvert); pow++) {
			poli.add(StepHelper.findCoefficient(toConvert, (pow == 1 ? var : power(var, pow))));
		}

		return poli.toArray(new StepExpression[0]);
	}

	/**
	 * a monom is an expression of the form x^n, where n is an integer, or a simple
	 * integer
	 * 
	 * @return whether sn is a monom
	 */
	private static boolean isMonom(StepExpression sn) {
		if (sn.isOperation(Operation.POWER)) {
			StepOperation so = (StepOperation) sn;

			return so.getSubTree(0) instanceof StepVariable && closeToAnInteger(so.getSubTree(1));
		}

		return sn instanceof StepVariable || sn instanceof StepConstant;
	}

	public static boolean isPolynomial(StepExpression sn) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.PLUS)) {
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (!isPolynomial(so.getSubTree(i))) {
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

		return sn.isConstant() || isMonom(sn);
	}

	/**
	 * @param r
	 *            dividend
	 * @param d
	 *            divisor
	 * @param var
	 *            variable
	 * @return the quotient of the two polynomials, null if they can not be divided
	 */
	public static StepExpression polynomialDivision(StepExpression r, StepExpression d, StepVariable var) {
		if (!isPolynomial(r) || !isPolynomial(d)) {
			return null;
		}

		StepExpression[] arrayD = convertToPolynomial(d, var);
		StepExpression[] arrayR = convertToPolynomial(r, var);

		int leadR = arrayR.length - 1;
		int leadD = arrayD.length - 1;

		StepExpression q = new StepConstant(0);

		while ((leadR != 0 || (arrayR[0] != null && arrayR[0].getValue() != 0)) && leadR >= leadD) {
			StepExpression t = multiply(divide(arrayR[leadR], arrayD[leadD]), power(var, leadR - leadD)).regroup();
			q = add(q, t);

			StepExpression[] td = convertToPolynomial(multiply(t, d).expand(null), var);

			for (int i = 0; i < td.length; i++) {
				if (td[i] != null) {
					arrayR[i] = subtract(arrayR[i], td[i]).regroup();
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
	 * 
	 * @param a
	 *            dividend
	 * @param b
	 *            divisor
	 * @return result, if polynomial division was successful, null otherwise
	 */
	public static StepExpression tryToDivide(StepExpression a, StepExpression b) {
		Set<StepVariable> listA = new HashSet<StepVariable>();
		Set<StepVariable> listB = new HashSet<StepVariable>();

		a.getListOfVariables(listA);
		b.getListOfVariables(listB);

		listA.retainAll(listB);

		for (StepVariable sv : listA) {
			StepExpression result = polynomialDivision(a, b, sv);
			if (result != null) {
				return result;
			}
		}

		return null;
	}

	/**
	 * @param sn
	 * @return the denominator of the tree, if it's an integer. 0 otherwise
	 */
	public static long getConstantDenominator(StepExpression sn) {
		if (sn.nonSpecialConstant()) {
			return 1;
		} else if (sn.isOperation(Operation.MINUS)) {
			return getConstantDenominator(((StepOperation) sn).getSubTree(0));
		} else if (sn.isOperation(Operation.DIVIDE)) {
			if (closeToAnInteger(((StepOperation) sn).getSubTree(0))
					&& closeToAnInteger(((StepOperation) sn).getSubTree(1))) {
				return Math.round(((StepOperation) sn).getSubTree(1).getValue());
			}
		}
		return 0;
	}

	/**
	 * @param sn
	 * @return the numerator of the tree.
	 */
	public static StepExpression getNumerator(StepExpression sn) {
		if (sn.nonSpecialConstant()) {
			return sn;
		} else if (sn.isOperation(Operation.MINUS)) {
			return minus(getNumerator(((StepOperation) sn).getSubTree(0)));
		} else if (sn.isOperation(Operation.DIVIDE)) {
			return ((StepOperation) sn).getSubTree(0);
		}
		return sn;
	}

	/**
	 * Returns the value of arcsin(a), arccos(a) and arctg(a) for simple a-s
	 * 
	 * @param so
	 * @return the value, if it can be evaluated, null otherwise
	 */
	public static StepExpression inverseTrigoLookup(StepOperation so) {
		String[] arguments = new String[] { "-1", "-(nroot(3, 2))/(2)", "-(nroot(2, 2))/(2)", "-(1)/(2)", "0",
				"(1)/(2)", "(nroot(2, 2))/(2)", "(nroot(3, 2))/(2)", "1" };
		String[] argumentsTan = new String[] { "", "-nroot(3, 2)", "-1", "-nroot(3, 2)/3", "0", "nroot(3, 2)/3", "1",
				"nroot(3, 2)", "" };

		StepExpression pi = new StepConstant(Math.PI);
		StepExpression[] valuesSinTan = new StepExpression[] { minus(divide(pi, 2)), minus(divide(pi, 3)),
				minus(divide(pi, 4)), minus(divide(pi, 6)), new StepConstant(0), divide(pi, 6), divide(pi, 4),
				divide(pi, 3), divide(pi, 2) };
		StepExpression[] valuesCos = new StepExpression[] { pi, divide(multiply(5, pi), 6), divide(multiply(3, pi), 4),
				divide(multiply(2, pi), 3), divide(pi, 2), divide(pi, 3), divide(pi, 4), divide(pi, 6),
				new StepConstant(0) };

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

	public static void getBasesAndExponents(StepExpression sn, StepExpression currentExp, List<StepExpression> bases,
			List<StepExpression> exponents) {
		if (sn instanceof StepOperation) {
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

	public static StepExpression reciprocate(StepExpression a) {
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
	 * calculates currentFraction * base ^ exponent, and writes it in a nice form
	 * i.e.: makeFraction((x+1)/x, x+1, 1) -> ((x+1)(x+1))/x,
	 * makeFraction((x+1)/(x(x+1)), x, -1) -> (x+1)/(x(x+1)x)
	 */
	public static StepExpression makeFraction(StepExpression currentFraction, StepExpression base,
			StepExpression exponent) {
		StepExpression nominator;
		StepExpression denominator;

		if (currentFraction != null && currentFraction.isOperation(Operation.DIVIDE)) {
			nominator = ((StepOperation) currentFraction).getSubTree(0);
			denominator = ((StepOperation) currentFraction).getSubTree(1);
		} else {
			nominator = currentFraction;
			denominator = null;
		}

		if (exponent.getValue() >= 0) {
			if (!isEqual(exponent.getValue(), 1) && closeToAnInteger(1 / exponent.getValue())) {
				nominator = nonTrivialProduct(nominator, root(base, 1 / exponent.getValue()));
			} else {
				nominator = nonTrivialProduct(nominator, nonTrivialPower(base, exponent));
			}
		} else {
			if (!isEqual(exponent.getValue(), -1) && closeToAnInteger(1 / exponent.getValue())) {
				nominator = nonTrivialProduct(denominator, root(base, -1 / exponent.getValue()));
			} else {
				denominator = nonTrivialProduct(denominator, nonTrivialPower(base, exponent.negate()));
			}
		}

		return divide(nominator, denominator);
	}

	/**
	 * returns a^b, except if b == 1, then it returns a, or if b == 0, then it
	 * returns 1
	 */
	public static StepExpression nonTrivialPower(StepExpression a, StepExpression b) {
		if (a != null && b != null) {
			if (isEqual(b, 1)) {
				return a;
			} else if (isEqual(b, 0)) {
				return new StepConstant(1);
			}
		}

		return power(a, b);
	}

	public static StepExpression nonTrivialPower(StepExpression a, double b) {
		return nonTrivialPower(a, new StepConstant(b));
	}

	/**
	 * return a*b, except if: a == 1 -> b, a == -1 -> -b and vice versa
	 */
	public static StepExpression nonTrivialProduct(StepExpression a, StepExpression b) {
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

	public static StepExpression nonTrivialProduct(double a, StepExpression b) {
		return nonTrivialProduct(new StepConstant(a), b);
	}

	/**
	 * Tries to negate the subtree. Basically, it removes the starting minus, if
	 * there is one.
	 * 
	 * @param sn
	 *            tree to negate
	 * @return -sn
	 */
	public StepExpression negate() {
		if (nonSpecialConstant()) {
			return new StepConstant(-getValue());
		}
		if (isOperation(Operation.MINUS)) {
			return ((StepOperation) this).getSubTree(0);
		}
		if (isOperation(Operation.MULTIPLY) && ((StepOperation) this).getSubTree(0).isNegative()) {
			StepExpression so = ((StepOperation) this).getSubTree(0).negate();
			for (int i = 1; i < ((StepOperation) this).noOfOperands(); i++) {
				so = multiply(so, ((StepOperation) this).getSubTree(i));
			}
			return so;
		}
		if (isOperation(Operation.DIVIDE)) {
			if (((StepOperation) this).getSubTree(0).isNegative()) {
				return divide(((StepOperation) this).getSubTree(0).negate(), ((StepOperation) this).getSubTree(1));
			}
			return minus(this);
		}

		return minus(this);
	}

	public boolean isNegative() {
		return (nonSpecialConstant() && getValue() < 0) || isOperation(Operation.MINUS)
				|| isOperation(Operation.MULTIPLY) && ((StepOperation) this).getSubTree(0).isNegative();
	}

	public static boolean closeToAnInteger(StepExpression sn) {
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

}
