package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.Set;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Variable;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.kernel.stepbystep.solution.TableElement;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;

public abstract class StepNode implements TableElement {

	protected int color;

	/**
	 * @return deep copy of the tree. Use this, if you want to preserve the tree
	 *         after a regroup
	 */
	public abstract StepNode deepCopy();

	public static String getColorHex(int color) {
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

	protected String getColorHex() {
		return getColorHex(color);
	}

	/**
	 * Recursively sets a color for the tree (i.e. for the root and all of the nodes
	 * under it)
	 * 
	 * @param color
	 *            the color to set
	 */
	public void setColor(int color) {
		this.color = color;
		if (this instanceof StepOperation) {
			for (StepExpression operand : (StepOperation) this) {
				operand.setColor(color);
			}
		} else if (this instanceof StepSolvable) {
			((StepSolvable) this).getLHS().setColor(color);
			((StepSolvable) this).getRHS().setColor(color);
		}
	}

	/**
	 * Sets 0 as the color of the tree
	 */
	public void cleanColors() {
		setColor(0);
	}

	/**
	 * @return the tree, formatted in LaTeX
	 */
	public String toLaTeXString(Localization loc) {
		return toLaTeXString(loc, false);
	}

	public boolean isOperation(Operation op) {
		return this instanceof StepOperation && ((StepOperation) this).getOperation() == op;
	}

	/**
	 * @return the tree, formatted in LaTeX, with colors, if colored is set
	 */
	public abstract String toLaTeXString(Localization loc, boolean colored);

	/**
	 * @param s
	 *            string to be parsed
	 * @param parser
	 *            GeoGebra parser
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
	 * @param ev
	 *            ExpressionValue to be converted
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
			case ARCSIN:
			case ARCCOS:
			case ARCTAN:
				StepExpression arg = (StepExpression) convertExpression(((ExpressionNode) ev).getLeft());
				return applyOp(((ExpressionNode) ev).getOperation(), arg);
			case SQRT:
				return root((StepExpression) convertExpression(((ExpressionNode) ev).getLeft()), 2);
			case MINUS:
				StepExpression left = (StepExpression) convertExpression(((ExpressionNode) ev).getLeft());
				StepExpression right = (StepExpression) convertExpression(((ExpressionNode) ev).getRight());
				return add(left, StepNode.minus(right));
			case ABS:
				return abs((StepExpression) convertExpression(((ExpressionNode) ev).getLeft()));
			case LOGB:
				left = (StepExpression) convertExpression(((ExpressionNode) ev).getLeft());
				right = (StepExpression) convertExpression(((ExpressionNode) ev).getRight());
				return logarithm(left, right);
			case LOG:
				arg = (StepExpression) convertExpression(((ExpressionNode) ev).getLeft());
				return logarithm(StepConstant.E, arg);
			case LOG10:
				arg = (StepExpression) convertExpression(((ExpressionNode) ev).getLeft());
				return logarithm(StepConstant.create(10), arg);
			case LOG2:
				arg = (StepExpression) convertExpression(((ExpressionNode) ev).getLeft());
				return logarithm(StepConstant.create(2), arg);
			case MULTIPLY:
				if (((ExpressionNode) ev).getLeft().isConstant()
						&& ((ExpressionNode) ev).getLeft().evaluateDouble() == -1) {
					return minus((StepExpression) convertExpression(((ExpressionNode) ev).getRight()));
				}
			default:
				StepOperation so = new StepOperation(((ExpressionNode) ev).getOperation());
				so.addOperand((StepExpression) convertExpression(((ExpressionNode) ev).getLeft()));
				so.addOperand((StepExpression) convertExpression(((ExpressionNode) ev).getRight()));
				return so;
			}
		}
		if (ev instanceof Equation) {
			StepNode LHS = convertExpression(((Equation) ev).getLHS());
			StepNode RHS = convertExpression(((Equation) ev).getRHS());
			return new StepEquation((StepExpression) LHS, (StepExpression) RHS);
		}
		if (ev instanceof FunctionVariable || ev instanceof Variable) {
			return new StepVariable(ev.toString(StringTemplate.defaultTemplate));
		}
		if (ev instanceof MyDouble) {
			return StepConstant.create(((MyDouble) ev).getDouble());
		}
		return null;
	}

	/**
	 * Iterates through the tree searching for StepVariables
	 *
	 * @param variableList
	 *            set of variables found in the tree
	 */
	public void getListOfVariables(Set<StepVariable> variableList) {
		if (this instanceof StepSolvable) {
			StepSolvable ss = (StepSolvable) this;

			ss.getLHS().getListOfVariables(variableList);
			ss.getRHS().getListOfVariables(variableList);
		}

		if (this instanceof StepOperation) {
			StepOperation so = (StepOperation) this;
			for (StepExpression operand : so) {
				operand.getListOfVariables(variableList);
			}
		}

		if (this instanceof StepVariable) {
			variableList.add((StepVariable) this);
		}
	}

	/**
	 * returns the largest b-th power that divides a (for example (8, 2) -> 4, (8,
	 * 3) -> 8, (108, 2) -> 36)
	 * 
	 * @param se
	 *            base
	 * @param b
	 *            exponent
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

		StepOperation so = new StepOperation(op);
		so.addOperand(a.deepCopy());
		return so;
	}

	private static StepExpression applyBinaryOp(Operation op, StepExpression a, StepExpression b) {
		if (a == null) {
			return b == null ? null : b.deepCopy();
		}
		if (b == null) {
			return a.deepCopy();
		}

		StepOperation so = new StepOperation(op);
		so.addOperand(a.deepCopy());
		so.addOperand(b.deepCopy());
		return so;
	}

	private static StepExpression applyNullableBinaryOp(Operation op, StepExpression a, StepExpression b) {
		if (a == null) {
			return null;
		}
		if (b == null) {
			return a.deepCopy();
		}

		StepOperation so = new StepOperation(op);
		so.addOperand(a.deepCopy());
		so.addOperand(b.deepCopy());
		return so;
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

	/**
	 * Only use if you are sure you do not want a copy
	 * @return a * b
	 */
	public static StepExpression multiplyNoCopy(StepExpression a, StepExpression b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}

		StepOperation so = new StepOperation(Operation.MULTIPLY);
		so.addOperand(a);
		so.addOperand(b);
		return so;
	}

	public static StepExpression multiply(double a, StepExpression b) {
		return multiply(StepConstant.create(a), b);
	}

	public static StepExpression divide(StepExpression a, StepExpression b) {
		if (a == null) {
			return b == null ? null : divide(StepConstant.create(1), b);
		}
		if (b == null) {
			return a.deepCopy();
		}

		StepOperation so = new StepOperation(Operation.DIVIDE);
		so.addOperand(a.deepCopy());
		so.addOperand(b.deepCopy());
		return so;
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
		long aVal = 0, bVal = 0;

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
	 * @return wether a is a nonSpecialConstant, with value equal to b
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

	public static boolean isEven(double d) {
		return isEqual(d % 2, 0);
	}

	public static boolean isEven(StepExpression se) {
		return se.isInteger() && isEven(se.getValue());
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
}
