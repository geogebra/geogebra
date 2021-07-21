package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

public abstract class StepExpression extends StepTransformable
		implements Comparable<StepExpression> {

	/**
	 * Returns the value of arcsin(a), arccos(a) and arctg(a) for simple a-s
	 *
	 * @param so inverse trigonometric expression (such as arccos(1))
	 * @return the value, if it can be evaluated, null otherwise
	 */
	public static StepExpression trigoLookup(StepOperation so) {
		final StepExpression pi = StepConstant.PI;
		final StepExpression zero = StepConstant.create(0);
		final StepExpression one = StepConstant.create(1);
		final StepExpression root2 = root(StepConstant.create(2), 2);
		final StepExpression root3 = root(StepConstant.create(3), 2);

		Map<Operation, StepExpression[]> map = new HashMap<>();

		map.put(Operation.SIN, new StepExpression[] {
				minus(one), minus(divide(root3, 2)), minus(divide(root2, 2)), minus(divide(1, 2)),
				zero, divide(1, 2), divide(root2, 2), divide(root3, 2), one,
				zero, minus(one), zero
		});
		map.put(Operation.COS, new StepExpression[] {
				minus(one), minus(divide(root3, 2)), minus(divide(root2, 2)), minus(divide(1, 2)),
				zero, divide(1, 2), divide(root2, 2), divide(root3, 2), one,
				minus(one), zero, one
		});
		map.put(Operation.TAN, new StepExpression[] {
				minus(root3), minus(one), minus(divide(1, root3)), zero, divide(1, root3),
				one, root3
		});
		map.put(Operation.ARCSIN, new StepExpression[] {
				minus(divide(pi, 2)), minus(divide(pi, 3)), minus(divide(pi, 4)),
				minus(divide(pi, 6)), zero, divide(pi, 6), divide(pi, 4), divide(pi, 3),
				divide(pi, 2),
				pi, divide(multiply(3, pi), 2), multiply(2, pi)
		});
		map.put(Operation.ARCCOS, new StepExpression[] {
				pi, divide(multiply(5, pi), 6), divide(multiply(3, pi), 4),
				divide(multiply(2, pi), 3), divide(pi, 2), divide(pi, 3), divide(pi, 4),
				divide(pi, 6), zero,
				pi, divide(multiply(3, pi), 2), multiply(2, pi)
		});
		map.put(Operation.ARCTAN, new StepExpression[] {
				minus(divide(pi, 3)), minus(divide(pi, 4)),
				minus(divide(pi, 6)), zero, divide(pi, 6), divide(pi, 4), divide(pi, 3)
		});

		StepExpression argument = so.getOperand(0);

		StepExpression[] values = map.get(StepExpression.getInverse(so.getOperation()));
		StepExpression[] results = map.get(so.getOperation());

		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(argument)) {
				return results[i];
			}
		}

		return null;
	}

	public static StepExpression makeProduct(List<StepExpression> bases,
			List<StepExpression> exponents) {
		StepExpression product = null;

		for (int i = 0; i < bases.size(); i++) {
			if (exponents.get(i) != null) {
				product =
						nonTrivialProduct(product, nonTrivialPower(bases.get(i), exponents.get(i)));
			}
		}

		return product;
	}

	public static StepExpression makeFraction(List<StepExpression> basesNumerator,
			List<StepExpression> exponentsNumerator, List<StepExpression> basesDenominator,
			List<StepExpression> exponentsDenominator) {
		return divide(makeProduct(basesNumerator, exponentsNumerator),
				makeProduct(basesDenominator, exponentsDenominator));
	}

	public static StepExpression simplifiedProduct(StepExpression a, StepExpression b) {
		if (a != null && b != null) {
			StepExpression numerator = nonTrivialProduct(a.getNumerator(), b.getNumerator());
			StepExpression denominator = nonTrivialProduct(a.getDenominator(), b.getDenominator());
			return divide(numerator, denominator);
		}

		return multiply(a, b);
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
				return StepConstant.create(1);
			}
		}

		return power(a, b);
	}

	public static StepExpression nonTrivialPower(StepExpression a, double b) {
		return nonTrivialPower(a, StepConstant.create(b));
	}

	public static StepExpression nonTrivialRoot(StepExpression a, StepExpression b) {
		if (a != null && b != null) {
			if (isEqual(b, 1)) {
				return a;
			}
		}

		return root(a, b);
	}

	public static StepExpression nonTrivialSum(StepExpression a, StepExpression b) {
		if (a != null && b != null) {
			if (isEqual(a, 0)) {
				return b;
			} else if (isEqual(b, 0)) {
				return a;
			}
		}

		return add(a, b);
	}

	/**
	 * return a*b, except if: a == 1 -> b, a == -1 -> -b and vice versa
	 */
	public static StepExpression nonTrivialProduct(StepExpression a, StepExpression b) {
		if (a != null && b != null) {
			if (isEqual(a, 1)) {
				return b;
			} else if (isEqual(b, 1)) {
				return a;
			} else if (a.isNegative()) {
				return minus(nonTrivialProduct(a.negate(), b));
			} else if (b.isNegative()) {
				return minus(nonTrivialProduct(a, b.negate()));
			}
		}

		return multiply(a, b);
	}

	public static StepExpression nonTrivialProduct(double a, StepExpression b) {
		return nonTrivialProduct(StepConstant.create(a), b);
	}

	public static Operation getInverse(Operation op) {
		switch (op) {
			case SIN:
				return Operation.ARCSIN;
			case COS:
				return Operation.ARCCOS;
			case TAN:
				return Operation.ARCTAN;
			case ARCSIN:
				return Operation.SIN;
			case ARCCOS:
				return Operation.COS;
			case ARCTAN:
				return Operation.TAN;
			default:
				return Operation.NO_OPERATION;
		}
	}

	@Override
	public abstract StepExpression deepCopy();

	@Override
	public int compareTo(StepExpression se) {
		return hashCode() - se.hashCode();
	}

	/**
	 * !This does not mean that getValue() will not return NaN.
	 * StepArbitraryConstants are constants, but cannot be evaluated
	 *
	 * @return whether this expression contains variables
	 */
	public boolean isConstant() {
		return isConstantIn(null);
	}

	/**
	 * If the expression is a polynomial in var, returns its degree, -1 otherwise
	 *
	 * @param var variable of polynomial
	 * @return degree or -1
	 */
	public abstract int degree(StepVariable var);

	public abstract boolean isConstantIn(StepVariable sv);

	/**
	 * It guarantees that getValue() returns a non-NaN value. Except when division,
	 * Math.pow, or Math.sqrt fails.
	 *
	 * @return whether this expression can be evaluated
	 */
	public abstract boolean canBeEvaluated();

	/**
	 * @return the numeric value of the expression.
	 */
	public abstract double getValue();

	/**
	 * @param variable the name of the variable to be replaced
	 * @param value    the value to be replaced with
	 * @return the value of the expression after replacement
	 */
	public abstract double getValueAt(StepVariable variable, double value);

	/**
	 * @return the non-variable coefficient of the expression (ex: 3 sqrt(3) x -> 3
	 * sqrt(3))
	 */
	public StepExpression getCoefficient() {
		return getCoefficientIn(null);
	}

	/**
	 * @return the variable part of the expression (ex: 3 x (1/sqrt(x)) -> x (1/sqrt(x)))
	 */
	public StepExpression getVariable() {
		return getVariableIn(null);
	}

	public abstract StepExpression getCoefficientIn(StepVariable sv);

	public abstract StepExpression getVariableIn(StepVariable sv);

	/**
	 * @return the StepConstant coefficient of the expression (ex: 3 sqrt(3) -> 3)
	 */
	public abstract StepExpression getIntegerCoefficient();

	/**
	 * @return the non-StepConstant part of the expression (ex: 3 sqrt(3) -> sqrt(3))
	 */
	public abstract StepExpression getNonInteger();

	/**
	 * Non-special constants are StepConstants and minus(StepConstant)s, except for
	 * pi, e, and infinity
	 *
	 * @return whether the current node is a nonSpecialConstant
	 */
	public abstract boolean nonSpecialConstant();

	/**
	 * Special constants are pi and e, and infinity
	 *
	 * @return whether the current node is a specialConstant
	 */
	public abstract boolean specialConstant();

	public boolean isUndefined() {
		return this instanceof StepConstant && Double.isNaN(getValue());
	}

	/**
	 * @return the current node can be proven to be an integer (such as k^5 + 3 - k_1)
	 */
	public abstract boolean proveInteger();

	/**
	 * @return the current node is a simple integer, such as -3 or 42
	 */
	public abstract boolean isInteger();

	public boolean isSquareRoot() {
		return isNthRoot(2);
	}

	/**
	 * @return whether the current node is an n-th root (that is nroot, with an
	 * exponent of n)
	 */
	public boolean isNthRoot(int n) {
		return isOperation(Operation.NROOT) && isEqual(((StepOperation) this).getOperand(1), n);
	}

	public int countNthRoots(int n) {
		if (isOperation(Operation.PLUS)) {
			int count = 0;

			for (StepExpression operand : (StepOperation) this) {
				if (operand.containsNthRoot(n)) {
					count ++;
				}
			}

			return count;
		}

		return isNthRoot(n) ? 1 : 0;
	}

	public StepExpression integersOfSum() {
		if (isOperation(Operation.PLUS)) {
			StepExpression integers = null;
			for (StepExpression operand : (StepOperation) this) {
				if (operand.isInteger()) {
					integers = add(integers, operand);
				}
			}
			return integers;
		}

		return null;
	}

	public StepExpression nonIntegersOfSum() {
		if (isOperation(Operation.PLUS)) {
			StepExpression nonIntegers = null;
			for (StepExpression operand : (StepOperation) this) {
				if (!operand.isInteger()) {
					nonIntegers = add(nonIntegers, operand);
				}
			}
			return nonIntegers;
		}

		return this;
	}

	/**
	 * @param expr expression to find
	 * @return all subexpression which contain expr
	 */
	public StepExpression findExpression(StepExpression expr) {
		if (equals(expr)) {
			return this;
		}

		if (this instanceof StepOperation) {
			StepOperation so = (StepOperation) this;

			if (so.isOperation(Operation.MULTIPLY)) {
				for (StepExpression operand : so) {
					if (operand.equals(expr)) {
						return so;
					}
				}
			} else if (so.isOperation(Operation.MINUS)) {
				return minus(so.getOperand(0).findExpression(expr));
			} else if (so.isOperation(Operation.PLUS)) {
				StepExpression found = null;
				for (StepExpression operand : so) {
					found = add(found, operand.findExpression(expr));
				}
				return found;
			} else if (so.isOperation(Operation.DIVIDE) && so.getOperand(1).isConstant()
					&& so.getOperand(0).equals(expr)) {
				return this;
			}
		}

		return null;
	}

	/**
	 * @param expr expression to find
	 * @return the coefficient of expr in the tree
	 */
	public StepExpression findCoefficient(StepExpression expr) {
		if (equals(expr)) {
			return StepConstant.create(1);
		}

		if (this instanceof StepOperation) {
			StepOperation so = (StepOperation) this;

			if (so.isOperation(Operation.MULTIPLY)) {
				StepExpression coeff = null;
				boolean found = false;

				for (StepExpression operand : so) {
					StepExpression current = operand.findCoefficient(expr);
					if (current == null) {
						coeff = nonTrivialProduct(coeff, operand);
					} else {
						found = true;
						coeff = nonTrivialProduct(coeff, current);
					}
				}

				if (found) {
					return coeff;
				}
			} else if (so.isOperation(Operation.MINUS)) {
				return minus(so.getOperand(0).findCoefficient(expr));
			} else if (so.isOperation(Operation.PLUS)) {
				StepExpression found = null;
				for (StepExpression operand : so) {
					found = add(found, operand.findCoefficient(expr));
				}
				return found;
			} else if (so.isOperation(Operation.DIVIDE) && so.getOperand(1).isConstant()
					&& so.getOperand(0).containsExpression(expr)) {
				StepExpression coefficient = so.getOperand(0).findCoefficient(expr);
				return divide(coefficient, so.getOperand(1));
			}
		}

		return null;
	}

	public StepExpression findConstant() {
		return findConstantIn(null);
	}

	/**
	 * @return subexpression which does not contain any variable part
	 */
	public StepExpression findConstantIn(StepVariable sv) {
		if (isConstantIn(sv)) {
			return this;
		}

		if (this instanceof StepOperation) {
			StepOperation so = (StepOperation) this;

			if (so.isOperation(Operation.MINUS)) {
				return minus(so.getOperand(0).findConstantIn(sv));
			} else if (so.isOperation(Operation.PLUS)) {
				StepExpression found = null;
				for (StepExpression operand : so) {
					found = add(found, operand.findConstantIn(sv));
				}
				return found;
			}
		}

		return null;
	}

	public StepExpression findVariable() {
		return findVariableIn(null);
	}

	public StepExpression findVariableIn(StepVariable sv) {
		if (isConstantIn(sv)) {
			return null;
		}

		if (this instanceof StepOperation) {
			StepOperation so = (StepOperation) this;

			if (so.isOperation(Operation.MINUS)) {
				return minus(so.getOperand(0).findVariableIn(sv));
			} else if (so.isOperation(Operation.PLUS)) {
				StepExpression found = null;
				for (StepExpression operand : so) {
					found = add(found, operand.findVariableIn(sv));
				}
				return found;
			} else if (so.isOperation(Operation.DIVIDE) && so.getOperand(1).isConstant()) {
				StepExpression nominator = so.getOperand(0).findVariableIn(sv);

				if (nominator != null) {
					return divide(nominator, so.getOperand(1));
				}
			}
		}

		return this;
	}

	public int countNonConstOperation(Operation operation, StepVariable variable) {
		if (this instanceof StepOperation) {
			StepOperation so = (StepOperation) this;

			if (so.isOperation(operation) && !so.isConstantIn(variable)) {
				return 1;
			}

			int operations = 0;
			for (StepExpression operand : so) {
				operations += operand.countNonConstOperation(operation, variable);
			}
			return operations;
		}

		return 0;
	}

	@Override
	public boolean contains(Operation op) {
		return countOperation(op) > 0;
	}

	public int countOperation(Operation operation) {
		if (this instanceof StepOperation) {
			StepOperation so = (StepOperation) this;

			if (so.isOperation(operation)) {
				return 1;
			}

			int operations = 0;
			for (StepExpression operand : so) {
				operations += operand.countOperation(operation);
			}
			return operations;
		}

		return 0;
	}

	public StepExpression getPower() {
		if (this instanceof StepOperation) {
			StepOperation so = (StepOperation) this;

			if (so.isOperation(Operation.POWER)) {
				return so.getOperand(1);
			} else if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)
					|| so.isOperation(Operation.MINUS)) {
				StepExpression power = null;
				for (StepExpression operand : so) {
					power = StepHelper.gcd(power, operand.getPower());
				}
				return power;
			}
		}

		return null;
	}

	public boolean isPower() {
		return isConstant() || getPower() != null;
	}

	public StepExpression getRoot() {
		if (this instanceof StepOperation) {
			StepOperation so = (StepOperation) this;

			if (so.isOperation(Operation.NROOT)) {
				return so.getOperand(1);
			} else if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)
					|| so.isOperation(Operation.MINUS)) {
				StepExpression root = StepConstant.create(1);
				for (StepExpression operand : so) {
					root = StepHelper.lcm(root, operand.getRoot());
				}
				return root;
			}
		}

		return null;
	}

	public boolean isRoot() {
		return isConstant() || getRoot() != null;
	}

	/**
	 * Check if this contains expr
	 * for example 3+2+1 contains 1+3
	 *
	 * @param expr expression to check
	 * @return whether this contains expr
	 */
	public boolean containsExpression(StepExpression expr) {
		if (equals(expr) || !isZero(this) && integerDivisible(expr)) {
			return true;
		}

		if (isOperation(Operation.PLUS) && expr.isOperation(Operation.PLUS)) {
			StepExpression[] sortedA = ((StepOperation) this).getSortedOperandList();
			StepExpression[] sortedB = ((StepOperation) expr).getSortedOperandList();

			int j = 0;
			for (StepExpression operand : sortedA) {
				if (j < sortedB.length && operand.equals(sortedB[j])) {
					j++;
				}
			}
			return j == sortedB.length;
		}

		if (isOperation(Operation.MULTIPLY) && expr.isOperation(Operation.MULTIPLY)) {
			StepExpression coeffA = getIntegerCoefficient();
			StepExpression coeffB = expr.getIntegerCoefficient();

			if (coeffA != null && coeffB != null && coeffA.isInteger() && coeffB.isInteger()) {
				if (!coeffA.integerDivisible(coeffB)) {
					return false;
				}

				StepExpression nonIntegerA = getNonInteger();
				StepExpression nonIntegerB = expr.getNonInteger();

				if (nonIntegerA == null) {
					return nonIntegerB == null;
				}

				return nonIntegerA.containsExpression(nonIntegerB);
			}

			StepExpression[] sortedA = ((StepOperation) this).getSortedOperandList();
			StepExpression[] sortedB = ((StepOperation) expr).getSortedOperandList();

			int j = 0;
			for (StepExpression operand : sortedA) {
				if (j < sortedB.length && operand.equals(sortedB[j])) {
					j++;
				}
			}
			return j == sortedB.length;
		}

		if (isOperation(Operation.PLUS) || isOperation(Operation.MULTIPLY)) {
			for (StepExpression operand : (StepOperation) this) {
				if (operand.equals(expr)) {
					return true;
				}
			}
			return false;
		}

		return isOperation(Operation.MINUS)
				&& ((StepOperation) this).getOperand(0).containsExpression(expr);
	}

	/**
	 * this = se * quotient(se) + remainder(se)
	 * simple example: 4a + 4b + 3 = 4(a + b) * 3,
	 * so (4a + 4b + 3).remainder(4) = 3
	 *
	 * @param expr expression to divide with
	 * @return the remainder of this / se
	 */
	public StepExpression remainder(StepExpression expr) {
		if (isInteger() && expr.isInteger()) {
			return StepConstant.create(Math.floor(getValue() % expr.getValue()));
		}

		if (equals(expr)) {
			return StepConstant.create(0);
		}

		if (isOperation(Operation.PLUS)) {
			StepExpression remainder = null;
			for (StepExpression operand : (StepOperation) this) {
				remainder = nonTrivialSum(remainder, operand.remainder(expr));
			}

			return remainder;
		}

		if (isOperation(Operation.MULTIPLY) || isOperation(Operation.MINUS)) {
			StepExpression coeffA = getIntegerCoefficient();
			StepExpression coeffB = expr.getIntegerCoefficient();

			if (coeffA != null && coeffB != null && coeffA.isInteger() && coeffB.isInteger()) {
				StepExpression remainder = getNonInteger().remainder(expr.getNonInteger());

				if (isZero(remainder)) {
					double value = coeffA.getValue()
							- coeffB.getValue() * Math.floor(coeffA.getValue() / coeffB.getValue());

					if (value != 0) {
						return nonTrivialProduct(value, expr.getNonInteger().deepCopy());
					}
				}
			}

			if (containsExpression(expr)) {
				return null;
			}
		}

		return this;
	}

	/**
	 * this = se * quotient(se) + remainder(se)
	 * simple example: 4a + 4b + 3 = 4(a + b) * 3,
	 * so (4a + 4b + 3).quotient(4) = a + b
	 *
	 * @param expr expression to divide with
	 * @return the quotient of this / se
	 */
	public StepExpression quotient(StepExpression expr) {
		if (expr == null) {
			return this;
		}

		if (isInteger() && expr.isInteger()) {
			return StepConstant.create(Math.floor(getValue() / expr.getValue()));
		}

		if (equals(expr)) {
			return StepConstant.create(1);
		}

		if (isOperation(Operation.PLUS)) {
			StepExpression quotient = null;
			for (StepExpression operand : (StepOperation) this) {
				quotient = nonTrivialSum(quotient, operand.quotient(expr));
			}

			return quotient;
		}

		if (isOperation(Operation.MULTIPLY) && expr.isOperation(Operation.MULTIPLY)) {
			StepExpression coeffA = getIntegerCoefficient();
			StepExpression coeffB = expr.getIntegerCoefficient();

			if (coeffA != null && coeffB != null && coeffA.isInteger() && coeffB.isInteger()) {
				StepExpression qutient = getNonInteger().quotient(expr.getNonInteger());

				if (qutient == null) {
					return null;
				}

				return nonTrivialProduct(Math.floor(coeffA.getValue() / coeffB.getValue()),
						qutient);
			}

			StepExpression[] sortedA = ((StepOperation) this).getSortedOperandList();
			StepExpression[] sortedB = ((StepOperation) expr).getSortedOperandList();

			StepExpression quotient = null;

			int j = 0;
			for (StepExpression operand : sortedA) {
				if (j < sortedB.length && operand.equals(sortedB[j])) {
					j++;
				} else {
					quotient = multiply(quotient, operand);
				}
			}

			if (j == sortedB.length) {
				return quotient;
			}
		}

		if (isOperation(Operation.MULTIPLY)) {
			StepExpression quotient = null;

			boolean found = false;
			for (StepExpression operand : (StepOperation) this) {
				if (!found && operand.equals(expr)) {
					found = true;
				} else {
					quotient = multiply(quotient, operand);
				}
			}

			if (found) {
				return quotient;
			}
		}

		return null;
	}

	public StepExpression getCommonProduct(StepExpression expr) {
		List<StepExpression> thisBases = new ArrayList<>();
		List<StepExpression> thisExponents = new ArrayList<>();
		List<StepExpression> thatBases = new ArrayList<>();
		List<StepExpression> thatExponents = new ArrayList<>();

		getBasesAndExponents(thisBases, thisExponents);
		expr.getBasesAndExponents(thatBases, thatExponents);

		StepExpression result = null;
		for (int i = 0; i < thisBases.size(); i++) {
			for (int j = 0; j < thatBases.size(); j++) {
				if (thisBases.get(i).equals(thatBases.get(j))
						&& thisExponents.get(i).equals(thatExponents.get(j))) {
					result = multiply(result,
							nonTrivialPower(thisBases.get(i), thisExponents.get(i)));
					thatBases.set(j, null);
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Finds the common part of the two expressions
	 * For example (3+k).getCommon(3) = 3
	 * (z+y+3).getCommon(z+5) = z+3
	 * (l*k).getCommon(l) = l
	 * (l*k+1).getCommon(l+k+1) = l+1
	 *
	 * @return the common part of the two expressions
	 */
	public StepExpression getCommon(StepExpression expr) {
		if (expr == null) {
			return null;
		}

		if (nonSpecialConstant() && expr.nonSpecialConstant()) {
			if (getValue() < expr.getValue()) {
				return this;
			}
			return expr;
		}

		if (containsExpression(expr)) {
			return expr;
		}

		if (expr.containsExpression(this)) {
			return this;
		}

		if (isOperation(Operation.PLUS) && expr.isOperation(Operation.PLUS)) {
			StepExpression coeffA = integersOfSum();
			StepExpression coeffB = expr.integersOfSum();

			if (coeffA != null && coeffB != null) {
				if (coeffA.isInteger() && coeffB.isInteger()) {
					return add(nonIntegersOfSum().getCommon(expr.nonIntegersOfSum()),
							coeffA.getCommon(coeffB));
				}
			}
		}

		if (expr.isOperation(Operation.PLUS)) {
			StepExpression common = null;
			StepExpression current = this;
			for (StepExpression operand : (StepOperation) expr) {
				if (current.containsExpression(operand)) {
					common = add(common, operand);
					current = (StepExpression) StepStrategies.regroupSums(
							subtract(current, operand), new SolutionBuilder());
				}
			}

			return common;
		}

		if (isOperation(Operation.PLUS)) {
			return expr.getCommon(this);
		}

		return null;
	}

	public boolean integerDivisible(StepExpression expr) {
		return isInteger()
				&& (expr == null || expr.isInteger() && isEqual(getValue() % expr.getValue(), 0));
	}

	public boolean isEven() {
		return isConstant() && isZero(remainder(StepConstant.create(2)));
	}

	public boolean containsSquareRoot() {
		return containsNthRoot(2);
	}

	public boolean containsNthRoot(int n) {
		if (isNthRoot(n)) {
			return true;
		}
		if (isOperation(Operation.MULTIPLY) || isOperation(Operation.MINUS)) {
			for (StepExpression operand : (StepOperation) this) {
				if (operand.containsNthRoot(n)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns -1 if the expression is negative, for all values of the variables,
	 * +1, if it's positive and 0 if it can not decide
	 *
	 * @return the sign of the expression
	 */
	public int sign() {
		if (canBeEvaluated()) {
			return getValue() < 0 ? -1 : 1;
		}

		if (this instanceof StepOperation) {
			StepOperation so = (StepOperation) this;

			switch (so.getOperation()) {
				case PLUS:
					int cnt = 0;
					for (StepExpression operand : so) {
						cnt += operand.sign();
					}
					return cnt / so.noOfOperands();
				case MINUS:
					return -so.getOperand(0).sign();
				case MULTIPLY:
					int sign = 1;
					for (StepExpression operand : so) {
						sign *= operand.sign();
					}
					return sign;
				case DIVIDE:
					return so.getOperand(0).sign() * so.getOperand(1).sign();
				case ABS:
					return 1;
				case POWER:
					if (so.getOperand(0).sign() > 0 || so.getOperand(1).isEven()) {
						return 1;
					}
					return 0;
			}
		}

		return 0;
	}

	/**
	 * A square is an expression of the form a^(2n), or a nonSpecialConstant. This
	 * definition is useful for factoring
	 *
	 * @return whether the current node is a square
	 */
	public boolean isSquare() {
		return nonSpecialConstant() && getValue() > 0
				|| isOperation(Operation.POWER) && ((StepOperation) this).getOperand(1).isEven();
	}

	public boolean isCube() {
		return isOperation(Operation.MINUS) && ((StepOperation) this).getOperand(0).isCube()
				|| nonSpecialConstant()
				|| isOperation(Operation.POWER) && isEqual(((StepOperation) this).getOperand(1), 3);
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
					return StepConstant.create(Math.sqrt(getValue()));
				}
				return root(this, 2);
			}

			StepOperation so = (StepOperation) this;
			return nonTrivialPower(so.getOperand(0), so.getOperand(1).getValue() / 2);
		}

		return null;
	}

	public StepExpression getCubeRoot() {
		if (isCube()) {
			if (nonSpecialConstant()) {
				if (isEqual(Math.cbrt(getValue()), Math.floor(Math.cbrt(getValue())))) {
					return StepConstant.create(Math.cbrt(getValue()));
				}
				return root(this, 3);
			}

			StepOperation so = (StepOperation) this;

			if (isOperation(Operation.MINUS)) {
				return so.getOperand(0).getCubeRoot().negate();
			}

			return nonTrivialPower(so.getOperand(0), so.getOperand(1).getValue() / 3);
		}

		return null;
	}

	public boolean isNaturalLog() {
		return isOperation(Operation.LOG)
				&& ((StepOperation) this).getOperand(0).equals(StepConstant.E);
	}

	public boolean isFraction() {
		return isOperation(Operation.DIVIDE) || isOperation(Operation.MINUS)
				&& ((StepOperation) this).getOperand(0).isFraction();
	}

	@Override
	public StepSolvable toSolvable() {
		return new StepEquation(this, StepConstant.create(0));
	}

	@Override
	public int maxDecimal() {
		if (nonSpecialConstant() && !isInteger()) {
			if (Double.toString(getValue()).split("\\.").length < 2) {
				Log.error(Double.toString(getValue()));
			}
			return Double.toString(getValue()).split("\\.")[1].length();
		}

		if (this instanceof StepOperation) {
			int max = 0;
			for (StepExpression operand : (StepOperation) this) {
				max = Math.max(max, operand.maxDecimal());
			}
			return max;
		}

		return 0;
	}

	@Override
	public boolean containsFractions() {
		if (isOperation(Operation.DIVIDE)) {
			return maxDecimal() == 0;
		}

		if (this instanceof StepOperation) {
			for (StepExpression operand : (StepOperation) this) {
				if (operand.containsFractions()) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public StepExpression regroup() {
		return (StepExpression) super.regroup();
	}

	@Override
	public StepExpression regroup(SolutionBuilder sb) {
		return (StepExpression) super.regroup(sb);
	}

	@Override
	public StepExpression weakRegroup() {
		return (StepExpression) super.weakRegroup();
	}

	@Override
	public StepExpression expand() {
		return (StepExpression) super.expand();
	}

	@Override
	public StepExpression factor() {
		return (StepExpression) super.factor();
	}

	/**
	 * @param from StepExpression to replace
	 * @param to   StepExpression to replace with
	 * @return the expression, replaced
	 */
	public StepExpression replace(StepExpression from, StepExpression to) {
		if (equals(from)) {
			return to.deepCopy();
		}
		if (this instanceof StepOperation) {
			StepOperation so = (StepOperation) this;
			StepExpression[] operands = new StepExpression[so.noOfOperands()];
			for (int i = 0; i < operands.length; i++) {
				operands[i] = so.getOperand(i).replace(from, to);
			}
			return StepOperation.create(so.getOperation(), operands);
		}
		return this;
	}

	/**
	 * @param var variable to group in
	 * @return toConvert in a polynomial format (as an array of coefficients)
	 * toConvert = sum(returned[i] * var^i)
	 */
	public StepExpression[] convertToPolynomial(StepVariable var) {
		StepExpression[] poly = new StepExpression[degree(var) + 1];

		poly[0] = findConstantIn(var);

		for (int pow = 1; pow <= degree(var); pow++) {
			poly[pow] = findCoefficient(pow == 1 ? var : power(var, pow));
		}

		return poly;
	}

	/**
	 * @return the denominator of the expression
	 */
	public StepExpression getDenominator() {
		if (isOperation(Operation.MINUS)) {
			return ((StepOperation) this).getOperand(0).getDenominator();
		} else if (isOperation(Operation.DIVIDE)) {
			return ((StepOperation) this).getOperand(1);
		}

		return null;
	}

	/**
	 * @return the numerator of the expression
	 */
	public StepExpression getNumerator() {
		if (isOperation(Operation.MINUS)) {
			return minus(((StepOperation) this).getOperand(0).getNumerator());
		} else if (isOperation(Operation.DIVIDE)) {
			return ((StepOperation) this).getOperand(0);
		}

		return this;
	}

	public void getBasesAndExponents(List<StepExpression> bases, List<StepExpression> exponents) {
		if (this instanceof StepOperation) {
			StepOperation so = (StepOperation) this;

			switch (so.getOperation()) {
				case MULTIPLY:
					for (StepExpression operand : so) {
						operand.getBasesAndExponents(bases, exponents);
					}
					return;
				case MINUS:
					if (!so.getOperand(0).nonSpecialConstant()) {
						bases.add(StepConstant.create(-1));
						exponents.add(StepConstant.create(1));
						so.getOperand(0).getBasesAndExponents(bases, exponents);
						return;
					}
					break;
				case POWER:
					bases.add(so.getOperand(0));
					exponents.add(so.getOperand(1));
					return;
			}
		}

		bases.add(this);
		exponents.add(StepConstant.create(1));
	}

	public StepExpression reciprocate() {
		if (isOperation(Operation.MINUS)) {
			return negate().reciprocate().negate();
		} else if (isOperation(Operation.DIVIDE)) {
			if (isEqual(((StepOperation) this).getOperand(0), 1)) {
				return ((StepOperation) this).getOperand(1);
			}
			return divide(((StepOperation) this).getOperand(1),
					((StepOperation) this).getOperand(0));
		} else if (isEqual(this, 1) || isEqual(this, -1)) {
			return this;
		} else {
			return divide(StepConstant.create(1), this);
		}
	}

	/**
	 * Tries to negate the expression. Basically, it removes the starting minus, if
	 * there is one.
	 *
	 * @return -this
	 */
	public StepExpression negate() {
		if (isOperation(Operation.MINUS)) {
			return ((StepOperation) this).getOperand(0);
		}

		return minus(this);
	}

	public boolean isNegative() {
		return isOperation(Operation.MINUS);
	}

	public boolean isSum() {
		return isOperation(Operation.PLUS);
	}

}
