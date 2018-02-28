package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.List;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steps.RegroupSteps;
import org.geogebra.common.kernel.stepbystep.steps.RegroupTracker;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;
import org.geogebra.common.plugin.Operation;

public abstract class StepExpression extends StepNode {

	@Override
	public abstract StepExpression deepCopy();

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
	 * @param variable
	 *            the name of the variable to be replaced
	 * @param value
	 *            the value to be replaced with
	 * @return the value of the expression after replacement
	 */
	public abstract double getValueAt(StepVariable variable, double value);

	/**
	 * @return the non-variable coefficient of the expression (ex: 3 sqrt(3) x -> 3
	 *         sqrt(3))
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
	public boolean nonSpecialConstant() {
		return this instanceof StepConstant && !specialConstant()
				|| isNegative() && negate() instanceof StepConstant && !negate().specialConstant();
	}

	/**
	 * Special constants are pi and e, and infinity
	 * 
	 * @return whether the current node is a specialConstant
	 */
	public boolean specialConstant() {
		return this instanceof StepConstant &&
				(isEqual(getValue(), Math.PI) || isEqual(getValue(), Math.E) || Double.isInfinite(getValue()));
	}

	/**
	 * @return whether the current node is an integer
	 */
	public boolean isInteger() {
		return nonSpecialConstant() && isEqual(Math.round(getValue()), getValue());
	}

	/**
	 * @return whether the current node is a squreRoot (that is nroot, with an
	 *         exponent of 2)
	 */
	public boolean isSquareRoot() {
		return isOperation(Operation.NROOT) && isEqual(((StepOperation) this).getOperand(1), 2);
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
	 * @param expr
	 *            expression to find
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
			} else if (so.isOperation(Operation.DIVIDE) && so.getOperand(1).isConstant()) {
				StepExpression nominator = so.getOperand(0).findExpression(expr);

				if (nominator != null) {
					return divide(nominator, so.getOperand(1));
				}
			}
		}

		return null;
	}

	/**
	 * @param expr
	 *            expression to find
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
			} else if (so.isOperation(Operation.DIVIDE) && so.getOperand(1).isConstant()) {
				StepExpression nominator = so.getOperand(0).findCoefficient(expr);

				if (nominator != null) {
					return divide(nominator, so.getOperand(1));
				}
			}
		}

		return null;
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
			} else if (so.isOperation(Operation.DIVIDE) && so.getOperand(1).isConstant()) {
				StepExpression nominator = so.getOperand(0).findConstantIn(sv);

				if (nominator != null) {
					return divide(nominator, so.getOperand(1));
				}
			}
		}

		return null;
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
					power = StepHelper.GCD(power, operand.getPower());
				}
				return power;
			}
		}

		return null;
	}

	public boolean isPower() {
		return isConstant() || getPower() != null;
	}

	/**
	 * Check if this contains expr
	 * for example 3+2+1 contains 1+3
	 * @param expr expression to check
	 * @return whether this contains expr
	 */
	public boolean containsExpression(StepExpression expr) {
		if (equals(expr) || !isZero(this) && integerDivisible(expr)) {
			return true;
		}

		if (isOperation(Operation.PLUS) && expr.isOperation(Operation.PLUS)) {
			StepOperation sortedA = ((StepOperation) deepCopy()).sort();
			StepOperation sortedB = ((StepOperation) expr.deepCopy()).sort();

			int j = 0;
			for (StepExpression operand : sortedA) {
				if (j < sortedB.noOfOperands() && operand.equals(sortedB.getOperand(j))) {
					j++;
				}
			}
			return j == sortedB.noOfOperands();
		}

		if (isOperation(Operation.MULTIPLY) && expr.isOperation(Operation.MULTIPLY)) {
			StepExpression coeffA = getIntegerCoefficient();
			StepExpression coeffB = expr.getIntegerCoefficient();

			if (coeffA != null && coeffB != null) {
				if (coeffA.isInteger() && coeffB.isInteger()) {
					return coeffA.integerDivisible(coeffB);
				}
			}

			StepOperation sortedA = ((StepOperation) deepCopy()).sort();
			StepOperation sortedB = ((StepOperation) expr.deepCopy()).sort();

			int j = 0;
			for (StepExpression operand : sortedA) {
				if (j < sortedB.noOfOperands() && operand.equals(sortedB.getOperand(j))) {
					j++;
				}
			}
			return j == sortedB.noOfOperands();
		}

		if (isOperation(Operation.PLUS) || isOperation(Operation.MULTIPLY)) {
			for (StepExpression operand : (StepOperation) this) {
				if (operand.equals(expr)) {
					return true;
				}
			}
			return false;
		}

		if (isOperation(Operation.MINUS)) {
			return ((StepOperation) this).getOperand(0).containsExpression(expr);
		}

		return false;
	}

	/**
	 * this = se * quotient(se) + remainder(se)
	 * simple example: 4a + 4b + 3 = 4(a + b) * 3,
	 * so (4a + 4b + 3).remainder(4) = 3
	 * @param expr expression to divide with
	 * @return the remainder of this / se
	 */
	public StepExpression remainder(StepExpression expr) {
		if (isInteger() && expr.isInteger()) {
			return StepConstant.create(Math.floor(getValue() % expr.getValue()));
		}

		if (isOperation(Operation.PLUS)) {
			StepExpression remainder = null;
			for (StepExpression operand : (StepOperation) this) {
				remainder = nonTrivialSum(remainder, operand.remainder(expr));
			}

			return remainder;
		}

		if (isOperation(Operation.MULTIPLY) || isOperation(Operation.MINUS)) {
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
	 * @param expr expression to divide with
	 * @return the quotient of this / se
	 */
	public StepExpression quotient(StepExpression expr) {
		if (isInteger() && expr.isInteger()) {
			return StepConstant.create(Math.floor(getValue() / expr.getValue()));
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

			if (coeffA != null && coeffB != null) {
				if (coeffA.isInteger() && coeffB.isInteger()) {
					if(coeffA.integerDivisible(coeffB)) {
						return multiply(Math.floor(coeffA.getValue()) / coeffB.getValue(),
								getNonInteger().quotient(expr.getNonInteger()));
					} else {
						return null;
					}
				}
			}

			StepOperation sortedA = ((StepOperation) deepCopy()).sort();
			StepOperation sortedB = ((StepOperation) expr.deepCopy()).sort();

			StepExpression quotient = null;

			int j = 0;
			for (StepExpression operand : sortedA) {
				if (j < sortedB.noOfOperands() && operand.equals(sortedB.getOperand(j))) {
					j++;
				} else {
					quotient = multiply(quotient, operand);
				}
			}
			if (j == sortedB.noOfOperands()) {
				return quotient;
			}
		}

		if (isOperation(Operation.MULTIPLY)) {
			StepExpression quotient = null;

			boolean found = false;
			for (StepExpression operand : (StepOperation) this) {
				if (!found && operand.equals(expr))	{
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

	/**
	 * Finds the common part of the two expressions
	 * For example (3+k).getCommon(3) = 3
	 * (z+y+3).getCommon(z+5) = z+3
	 * (l*k).getCommon(l) = l
	 * (l*k+1).getCommon(l+k+1) = l+1
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
					return add(nonIntegersOfSum().getCommon(expr.nonIntegersOfSum()), coeffA.getCommon(coeffB));
				}
			}
		}

		if (expr.isOperation(Operation.PLUS)) {
			SolutionBuilder tempSteps = new SolutionBuilder();
			RegroupTracker tempTracker = new RegroupTracker();

			StepExpression common = null;
			StepExpression current = deepCopy();
			for (StepExpression operand : (StepOperation) expr) {
				if (current.containsExpression(operand)) {
					common = add(common, operand);
					current = (StepExpression) RegroupSteps.REGROUP_SUMS.apply(subtract(current, operand), tempSteps,
							tempTracker);
					tempTracker.resetTracker();
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
		return isInteger() && (expr == null || expr.isInteger() && isEqual(getValue() % expr.getValue(), 0));
	}

	public boolean isEven() {
		return isConstant() && isZero(remainder(StepConstant.create(2)));
	}

	public boolean containsSquareRoot() {
		if (isSquareRoot()) {
			return true;
		}
		if (isOperation(Operation.MULTIPLY) || isOperation(Operation.MINUS)) {
			for (StepExpression operand : (StepOperation) this) {
				if (operand.containsSquareRoot()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean integerCoefficients(StepVariable sv) {
		if (degree(sv) >= 0) {
			StepExpression[] coefficients = convertToPolynomial(sv);

			for (StepExpression coefficient : coefficients) {
				if (coefficient != null && !coefficient.isInteger()) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * Using some simple heuristics (such as, even powers are positive,
	 * exponentials, sum of positives, etc.), checks if the expression
	 * if positive for all values of the variables.
	 * @return whether the expression is provably positive
	 */
	public boolean isPositive() {
		if (this instanceof StepOperation) {
			StepOperation so = (StepOperation) this;

			switch (so.getOperation()) {
			case PLUS:
			case MULTIPLY:
				for (StepExpression operand : so) {
					if (!operand.isPositive()) {
						return false;
					}
				}
				return true;
			case POWER:
				return so.getOperand(0).isPositive()
						|| so.getOperand(1).isEven();
			case ABS:
				return true;
			}

			return false;
		}

		// StepConstants are always positive!
		return this instanceof StepConstant;
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
		return isOperation(Operation.MINUS) && ((StepOperation) this).getOperand(0).isCube() || nonSpecialConstant()
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

	public boolean isNaturalLog() {
		return isOperation(Operation.LOG) && ((StepOperation) this).getOperand(0).equals(StepConstant.E);
	}

	public boolean isFraction() {
		return isOperation(Operation.DIVIDE) ||
				isOperation(Operation.MINUS) && ((StepOperation) this).getOperand(0).isFraction();
	}

	public int maxDecimal() {
		if (nonSpecialConstant() && !isInteger()) {
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

	public StepExpression convertToFractions(SolutionBuilder sb) {
		return (StepExpression) StepStrategies.convertToFraction(this, sb);
	}

	/**
	 * @return the expression, regrouped
	 */
	public StepExpression regroup() {
		return regroup(null);
	}

	/**
	 * This is the default regroup. Assumes every nonSpecialConstant is an integer.
	 * @param sb SolutionBuilder for the regroup steps
	 * @return the expression, regrouped
	 */
	public StepExpression regroup(SolutionBuilder sb) {
		if (this instanceof StepOperation) {
			return (StepExpression) StepStrategies.defaultRegroup(this, sb);
		}

		return this;
	}

	/**
	 * Numeric regroup. Evaluates expressions like 1/3 and sqrt(2)..
	 * @param sb SolutionBuilder for the regroup steps
	 * @return the expression, regrouped
	 */
	public StepExpression numericRegroup(SolutionBuilder sb) {
		if (this instanceof StepOperation) {
			return (StepExpression) StepStrategies.decimalRegroup(this, sb);
		}

		return this;
	}

	public StepExpression adaptiveRegroup() {
		return adaptiveRegroup(null);
	}

	public StepExpression adaptiveRegroup(SolutionBuilder sb) {
		if (0 < maxDecimal() && maxDecimal() < 5 && containsFractions()) {
			StepExpression temp = convertToFractions(sb);
			return temp.regroup(sb);
		}

		if (maxDecimal() > 0) {
			return numericRegroup(sb);
		}

		return regroup(sb);
	}

	public StepExpression regroupOutput(SolutionBuilder sb) {
		sb.add(SolutionStepType.SIMPLIFY, this);
		return adaptiveRegroup(sb);
	}

	/**
	 * @return the expression, regrouped and expanded
	 */
	public StepExpression expand() {
		return expand(null);
	}

	/**
	 * @param sb
	 *            SolutionBuilder for the expansion steps
	 * @return the expression, regrouped and expanded
	 */
	public StepExpression expand(SolutionBuilder sb) {
		if (this instanceof StepOperation) {
			return (StepExpression) StepStrategies.defaultExpand(this, sb);
		}

		return this;
	}

	public StepExpression expandOutput(SolutionBuilder sb) {
		sb.add(SolutionStepType.EXPAND, this);
		return expand(sb);
	}

	/**
	 * @return the expression, factored
	 */
	public StepExpression factor() {
		return factor(null);
	}

	/**
	 * @param sb
	 *            SolutionBuilder for the factoring steps
	 * @return the expression, factored
	 */
	public StepExpression factor(SolutionBuilder sb) {
		if (this instanceof StepOperation) {
			return (StepExpression) StepStrategies.defaultFactor(this, sb, new RegroupTracker());
		}

		return this;
	}

	public StepExpression factorOutput(SolutionBuilder sb) {
		sb.add(SolutionStepType.FACTOR, this);
		return factor(sb);
	}

	public StepExpression differentiate() {
		return differentiate(null);
	}

	public StepExpression differentiate(SolutionBuilder sb) {
		if (this instanceof StepOperation) {
			return (StepExpression) StepStrategies.defaultDifferentiate(this, sb);
		}

		return this;
	}

	public StepExpression differentiateOutput(SolutionBuilder sb) {
		sb.add(SolutionStepType.DIFFERENTIATE, this);
		return differentiate(sb);
	}

	/**
	 * @param from
	 *            StepExpression to replace
	 * @param to
	 *            StepExpression to replace with
	 * @return the expression, replaced
	 */
	public StepExpression replace(StepExpression from, StepExpression to) {
		if (equals(from)) {
			return to.deepCopy();
		}
		if (this instanceof StepOperation) {
			StepOperation so = new StepOperation(((StepOperation) this).getOperation());
			for (StepExpression operand : (StepOperation) this) {
				so.addOperand(operand.replace(from, to));
			}
			return so;
		}
		return deepCopy();
	}

	/**
	 * @param var
	 *            variable to group in
	 * @return toConvert in a polynomial format (as an array of coefficients)
	 *         toConvert = sum(returned[i] * var^i)
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

	/**
	 * Returns the value of arcsin(a), arccos(a) and arctg(a) for simple a-s
	 * 
	 * @param so inverse trigonometric expression (such as arccos(1))
	 * @return the value, if it can be evaluated, null otherwise
	 */
	public static StepExpression inverseTrigoLookup(StepOperation so) {
		String[] arguments = new String[] { "-1", "-(nroot(3, 2))/(2)", "-(nroot(2, 2))/(2)", "-(1)/(2)", "0",
				"(1)/(2)", "(nroot(2, 2))/(2)", "(nroot(3, 2))/(2)", "1" };
		String[] argumentsTan = new String[] { "", "-nroot(3, 2)", "-1", "-nroot(3, 2)/3", "0", "nroot(3, 2)/3", "1",
				"nroot(3, 2)", "" };

		StepExpression pi = StepConstant.create(Math.PI);
		StepExpression[] valuesSinTan = new StepExpression[] { minus(divide(pi, 2)), minus(divide(pi, 3)),
				minus(divide(pi, 4)), minus(divide(pi, 6)), StepConstant.create(0), divide(pi, 6), divide(pi, 4),
				divide(pi, 3), divide(pi, 2) };
		StepExpression[] valuesCos = new StepExpression[] { pi, divide(multiply(5, pi), 6), divide(multiply(3, pi), 4),
				divide(multiply(2, pi), 3), divide(pi, 2), divide(pi, 3), divide(pi, 4), divide(pi, 6),
				StepConstant.create(0) };

		String currentArgument = so.getOperand(0).toString();
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
		if (isOperation(Operation.DIVIDE)) {
			if (isEqual(((StepOperation) this).getOperand(0), 1)) {
				return ((StepOperation) this).getOperand(1);
			}
			return divide(((StepOperation) this).getOperand(1), ((StepOperation) this).getOperand(0));
		} else if (isEqual(this, 1) || isEqual(this, -1)) {
			return this;
		} else {
			return divide(StepConstant.create(1), this);
		}
	}

	public static StepExpression makeProduct(List<StepExpression> bases, List<StepExpression> exponents) {
		StepExpression product = null;

		for (int i = 0; i < bases.size(); i++) {
			if (exponents.get(i) != null) {
				product = multiply(product, nonTrivialPower(bases.get(i), exponents.get(i)));
			}
		}

		return product;
	}

	public static StepExpression makeFraction(
			List<StepExpression> basesNumerator, List<StepExpression> exponentsNumerator,
			List<StepExpression> basesDenominator, List<StepExpression> exponentsDenominator) {
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
				return a.deepCopy();
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
				return a.deepCopy();
			}
		}

		return root(a, b);
	}

	public static StepExpression nonTrivialSum(StepExpression a, StepExpression b) {
		if (a != null && b != null) {
			if (isEqual(a, 0)) {
				return b.deepCopy();
			} else if (isEqual(b, 0)) {
				return a.deepCopy();
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
				return b.deepCopy();
			} else if (isEqual(b, 1)) {
				return a.deepCopy();
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

	/**
	 * Tries to negate the expression. Basically, it removes the starting minus, if
	 * there is one.
	 * @return -this
	 */
	public StepExpression negate() {
		if (isOperation(Operation.MINUS)) {
			return ((StepOperation) this).getOperand(0).deepCopy();
		}

		return minus(this);
	}

	public boolean isNegative() {
		return isOperation(Operation.MINUS);
	}

	public boolean isSum() {
		return isOperation(Operation.PLUS);
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
