package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.kernel.stepbystep.solution.HasLaTeX;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

public abstract class StepNode implements HasLaTeX {

	/**
	 * @param s      string to be parsed
	 * @param parser GeoGebra parser
	 * @return the string s, parsed as a StepTree
	 */
	public static StepTransformable getStepTree(String s, Parser parser) {
		if (s.isEmpty()) {
			return null;
		}

		try {
			ExpressionValue ev = parser.parseGeoGebraCAS(s, null);
			StepTransformable sn = convertExpression(ev);
			return cleanupExpression(sn);
		} catch (Throwable t) { // :(
			Log.debug(t);
			return null;
		}
	}

	/**
	 * @param ev ExpressionValue to be converted
	 * @return ev converted to StepTree
	 */
	public static StepTransformable convertExpression(ExpressionValue ev) {
		if (ev instanceof ExpressionNode) {
			ExpressionNode en = (ExpressionNode) ev;
			StepExpression left =
					(StepExpression) convertExpression(en.getLeft());
			StepExpression right =
					(StepExpression) convertExpression(en.getRight());

			switch (((ExpressionNode) ev).getOperation()) {
				case NO_OPERATION:
				case SIN:
				case COS:
				case TAN:
				case CSC:
				case SEC:
				case COT:
				case ARCSIN:
				case ARCCOS:
				case ARCTAN:
					return applyOp(en.getOperation(), left);
				case SQRT:
					return root(left, 2);
				case MINUS:
					return add(left, StepNode.minus(right));
				case PLUSMINUS:
					if (en.getRight() instanceof MyNumberPair) {
						return plusminus(left);
					}
					return add(left, plusminus(right));
				case ABS:
					return abs(left);
				case LOGB:
					return logarithm(left, right);
				case LOG:
					return logarithm(StepConstant.E, left);
				case LOG10:
					return logarithm(StepConstant.create(10), left);
				case LOG2:
					return logarithm(StepConstant.create(2), left);
				case EXP:
					return power(StepConstant.E, left);
				case LESS:
					return new StepInequality(left, right, true, true);
				case LESS_EQUAL:
					return new StepInequality(left, right, true, false);
				case GREATER:
					return new StepInequality(left, right, false, true);
				case GREATER_EQUAL:
					return new StepInequality(left, right, false, false);
				case ARBCONST:
					return new StepArbitraryInteger("k", (int) en.getLeft().evaluateDouble());
				case MULTIPLY:
					if (en.getLeft() instanceof MyDouble && en.getLeft().evaluateDouble() == -1) {
						return minus(right);
					}
				default:
					return StepOperation.create(en.getOperation(), left, right);
			}
		}
		if (ev instanceof Equation) {
			StepNode LHS = convertExpression(((Equation) ev).getLHS());
			StepNode RHS = convertExpression(((Equation) ev).getRHS());
			return new StepEquation((StepExpression) LHS, (StepExpression) RHS);
		}
		if (ev instanceof Variable && "e".equals(((Variable) ev).getName())) {
			return StepConstant.E;
		}
		if (ev instanceof FunctionVariable || ev instanceof Variable) {
			return new StepVariable(ev.toString(StringTemplate.defaultTemplate));
		}
		if (ev instanceof MyDouble) {
			return StepConstant.create(((MyDouble) ev).getDouble());
		}
		return null;
	}

	private static StepTransformable cleanupExpression(StepTransformable sn) {
		if (sn instanceof StepSolvable) {
			return ((StepSolvable) sn).cloneWith(
					(StepExpression) cleanupExpression(((StepSolvable) sn).LHS),
					(StepExpression) cleanupExpression(((StepSolvable) sn).RHS));
		}

		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.getOperation() == Operation.MULTIPLY) {
				if (so.getOperand(0).isNegative()) {
					StepExpression[] result = new StepExpression[so.noOfOperands()];
					result[0] = (StepExpression) cleanupExpression(so.getOperand(0).negate());
					for (int i = 1; i < so.noOfOperands(); i++) {
						result[i] = (StepExpression) cleanupExpression(so.getOperand(i));
					}

					return StepOperation.multiply(result).negate();
				}
			}

			if (so.getOperation() == Operation.NO_OPERATION) {
				return cleanupExpression(((StepOperation) sn).getOperand(0));
			}

			StepExpression[] result = new StepExpression[so.noOfOperands()];
			for (int i = 0; i < so.noOfOperands(); i++) {
				result[i] = (StepExpression) cleanupExpression(so.getOperand(i));
			}

			return StepOperation.create(so.getOperation(), result);
		}

		return sn;
	}

	/**
	 * returns the largest b-th power that divides a (for example (8, 2) -> 4, (8,
	 * 3) -> 8, (108, 2) -> 36)
	 *
	 * @param se base
	 * @param b  exponent
	 * @return largest b-th power that divides a
	 */
	public static long largestNthPower(StepExpression se, double b) {
		if (se == null || !se.canBeEvaluated()) {
			return 1;
		}

		if (closeToAnInteger(se.getValue()) && closeToAnInteger(b)) {
			long x = Math.round(se.getValue());
			long y = Math.round(b);

			if (x > 10000) {
				return 1;
			}

			int power = 1;
			int count = 0;

			while (x % 2 == 0) {
				count++;
				x /= 2;
			}

			power *= Math.pow(2, count - count % y);

			for (int i = 3; i < x; i += 2) {
				count = 0;

				while (x % i == 0) {
					count++;
					x /= i;
				}

				power *= Math.pow(i, count - count % y);
			}

			return power;
		}

		return 1;
	}

	/**
	 * returns the largest n, for which x is a perfect nth power
	 */
	public static long getIntegerPower(long x) {
		long temp = x;
		if (temp < 0) {
			temp = -temp;
		}

		if (temp == 1 || temp > 10000) {
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

	public static StepExpression applyOp(Operation op, StepExpression a) {
		if (a == null) {
			return null;
		}

		return StepOperation.create(op, a);
	}

	private static StepExpression applyBinaryOp(Operation op, StepExpression a, StepExpression b) {
		if (a == null) {
			return b == null ? null : b.deepCopy();
		}
		if (b == null) {
			return a.deepCopy();
		}

		return StepOperation.create(op, a.deepCopy(), b.deepCopy());
	}

	private static StepExpression applyNullableBinaryOp(Operation op, StepExpression a,
			StepExpression b) {
		if (a == null) {
			return null;
		}
		if (b == null) {
			return a.deepCopy();
		}

		return StepOperation.create(op, a.deepCopy(), b.deepCopy());
	}

	private static StepLogical doSetOperation(SetOperation op, StepLogical a, StepLogical b) {
		if (a == null) {
			return b == null ? null : b.deepCopy();
		}
		if (b == null) {
			return a.deepCopy();
		}

		StepSetOperation sso = new StepSetOperation(op);
		sso.addOperand(a.deepCopy());
		sso.addOperand(b.deepCopy());

		return sso;
	}

	public static StepExpression add(StepExpression a, StepExpression b) {
		return applyBinaryOp(Operation.PLUS, a, b);
	}

	public static StepExpression add(StepExpression a, double b) {
		return add(a, StepConstant.create(b));
	}

	public static StepExpression subtract(StepExpression a, StepExpression b) {
		return add(a, minus(b));
	}

	public static StepExpression subtract(StepExpression a, double b) {
		return subtract(a, StepConstant.create(b));
	}

	public static StepExpression subtract(double a, StepExpression b) {
		return subtract(StepConstant.create(a), b);
	}

	public static StepExpression minus(StepExpression a) {
		return applyOp(Operation.MINUS, a);
	}

	public static StepExpression multiply(StepExpression a, StepExpression b) {
		return applyBinaryOp(Operation.MULTIPLY, a, b);
	}

	public static StepExpression multiply(double a, StepExpression b) {
		return multiply(StepConstant.create(a), b);
	}

	public static StepExpression divide(StepExpression a, StepExpression b) {
		if (a == null) {
			return b == null ? null : divide(StepConstant.create(1), b.deepCopy());
		}
		if (b == null) {
			return a.deepCopy();
		}

		return new StepOperation(Operation.DIVIDE, a.deepCopy(), b.deepCopy());
	}

	public static StepExpression divide(StepExpression a, double b) {
		return divide(a, StepConstant.create(b));
	}

	public static StepExpression divide(double a, StepExpression b) {
		return divide(StepConstant.create(a), b);
	}

	public static StepExpression divide(double a, double b) {
		return divide(StepConstant.create(a), StepConstant.create(b));
	}

	public static StepExpression power(StepExpression a, StepExpression b) {
		return applyNullableBinaryOp(Operation.POWER, a, b);
	}

	public static StepExpression power(StepExpression a, double b) {
		return power(a, StepConstant.create(b));
	}

	public static StepExpression root(StepExpression a, StepExpression b) {
		return applyNullableBinaryOp(Operation.NROOT, a, b);
	}

	public static StepExpression root(StepExpression a, double b) {
		return root(a, StepConstant.create(b));
	}

	public static StepExpression logarithm(StepExpression a, StepExpression b) {
		return applyNullableBinaryOp(Operation.LOG, a, b);
	}

	public static StepExpression logarithm(double a, StepExpression b) {
		return logarithm(StepConstant.create(a), b);
	}

	public static StepExpression differentiate(StepExpression a, StepVariable b) {
		return applyNullableBinaryOp(Operation.DIFF, a, b);
	}

	public static StepExpression plusminus(StepExpression a) {
		return applyOp(Operation.PLUSMINUS, a);
	}

	public static StepExpression abs(StepExpression a) {
		return applyOp(Operation.ABS, a);
	}

	public static StepExpression sin(StepExpression a) {
		return applyOp(Operation.SIN, a);
	}

	public static StepExpression cos(StepExpression a) {
		return applyOp(Operation.COS, a);
	}

	public static StepExpression tan(StepExpression a) {
		return applyOp(Operation.TAN, a);
	}

	public static StepLogical intersect(StepLogical a, StepLogical b) {
		return doSetOperation(SetOperation.INTERSECT, a, b);
	}

	public static StepLogical union(StepLogical a, StepLogical b) {
		return doSetOperation(SetOperation.UNION, a, b);
	}

	public static StepLogical subtract(StepLogical a, StepLogical b) {
		return doSetOperation(SetOperation.DIFFERENCE, a, b);
	}

	public static long gcd(StepExpression a, StepExpression b) {
		long aVal = 0, bVal = 0;

		if (a != null && a.canBeEvaluated()) {
			aVal = Math.round(a.getValue());
		}

		if (b != null && b.canBeEvaluated()) {
			bVal = Math.round(b.getValue());
		}

		return gcd(aVal, bVal);
	}

	public static long lcm(StepExpression a, StepExpression b) {
		long aVal = 1, bVal = 1;

		if (a != null && a.canBeEvaluated()) {
			aVal = Math.round(a.getValue());
		}

		if (b != null && b.canBeEvaluated()) {
			bVal = Math.round(b.getValue());
		}

		return lcm(aVal, bVal);
	}

	/**
	 * Provides an easy way to compare constants. Instead of
	 * a.equals(StepConstant.create(b)), just use isEqual(a, b)
	 *
	 * @return whether a is a nonSpecialConstant, with value equal to b
	 */
	public static boolean isEqual(StepExpression a, double b) {
		return a.nonSpecialConstant() && isEqual(a.getValue(), b);
	}

	public static boolean isZero(StepExpression a) {
		return a == null || isEqual(a, 0);
	}

	public static boolean isOne(StepExpression a) {
		return a == null || isEqual(a, 1);
	}

	public static boolean isOdd(double d) {
		return isEqual(d % 2, 1);
	}

	public static boolean isOdd(StepExpression se) {
		return se.isInteger() && isOdd(se.getValue());
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

	public static boolean closeToAnInteger(double d) {
		return Math.abs(Math.round(d) - d) < 0.0000001;
	}

	/**
	 * @return deep copy of the tree. Use this, if you want to preserve the tree
	 * after a regroup
	 */
	@Override
	public abstract StepNode deepCopy();

	/**
	 * @return the tree, formatted in LaTeX
	 */
	@Override
	public String toLaTeXString(Localization loc) {
		return toLaTeXString(loc, false);
	}

	/**
	 * @return the tree, formatted in LaTeX, with colors, if colored is set
	 */
	@Override
	public abstract String toLaTeXString(Localization loc, boolean colored);

	/**
	 * Iterates through the tree searching for StepVariables
	 */
	public List<StepVariable> getListOfVariables() {
		Set<StepVariable> variableSet = new HashSet<>();
		getSetOfVariables(variableSet);
		return new ArrayList<>(variableSet);
	}

	protected void getSetOfVariables(Set<StepVariable> variableSet) {
		if (this instanceof StepEquationSystem) {
			for (StepEquation se : ((StepEquationSystem) this).getEquations()) {
				se.getSetOfVariables(variableSet);
			}
		}

		if (this instanceof StepSolvable) {
			StepSolvable ss = (StepSolvable) this;

			ss.LHS.getSetOfVariables(variableSet);
			ss.RHS.getSetOfVariables(variableSet);
		}

		if (this instanceof StepOperation) {
			StepOperation so = (StepOperation) this;
			for (StepExpression operand : so) {
				operand.getSetOfVariables(variableSet);
			}
		}

		if (this instanceof StepVariable) {
			variableSet.add((StepVariable) this);
		}
	}
}
