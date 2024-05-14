package org.geogebra.common.kernel.arithmetic.traversing;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.plugin.Operation;

/**
 * Checker for cases where the variable x in Solve(f(x), x) should use degree as a unit.
 * Accepted values of f should be sin(x), sin(2 * x), but not sin(x * deg) or sin(1 / x).
 * See GGB-2183, APPS-3112, APPS-3452
 */
public final class DegreeVariableChecker implements Inspecting {
	private static final DegreeVariableChecker replacer = new DegreeVariableChecker();
	private static final int NONLINEAR = 4;
	private static final int VARIABLE = 2;
	private static final int ANGLE_UNIT = 1;

	private DegreeVariableChecker() {
		// singleton constructor
	}

	@Override
	public boolean check(ExpressionValue ev) {
		if (ev instanceof ExpressionNode) {
			ExpressionNode en = (ExpressionNode) ev;
			Operation op = en.getOperation();
			return op.hasDegreeInput() && getHighestTermKind(en.getLeftTree()) == VARIABLE;
		}
		return false;
	}

	/**
	 * The basic term kinds are NONLINEAR > VARIABLE > ANGLE_UNIT.
	 * The sorting is important for addition. Actual term kind is a bitmask formed by basic kinds.
	 * Examples:
	 * x * degree -> VARIABLE + ANGLE_UNIT
	 * 1 / x -> NONLINEAR
	 * x + 5deg -> VARIABLE
	 * 2 * x -> VARIABLE
	 * degree+pi -> ANGLE_UNIT
	 * @param en node
	 * @return bitmask from basic term kinds
	 */
	private int getHighestTermKind(ExpressionNode en) {
		switch (en.getOperation()) {
		case NO_OPERATION:
			if (en.getLeft() instanceof MySpecialDouble
					&& ((MySpecialDouble) en.getLeft()).isAngleUnit()) {
				return ANGLE_UNIT;
			}
			return en.getLeft() instanceof FunctionVariable ? VARIABLE : 0;
		case MULTIPLY:
			int leftDeg = getHighestTermKind(en.getLeftTree());
			int rightDeg = getHighestTermKind(en.getRightTree());
			return (leftDeg & rightDeg) > 0 ? NONLINEAR : (leftDeg | rightDeg);
		case PLUS:
		case MINUS:
			return Math.max(getHighestTermKind(en.getLeftTree()),
					getHighestTermKind(en.getRightTree()));
		case DIVIDE:
			return getHighestTermKind(en.getRightTree()) == 0
					? getHighestTermKind(en.getLeftTree()) : NONLINEAR;
		case ABS:
			return getHighestTermKind(en.getLeftTree());
		default:
			if (en.getRightTree() == null) {
				return getHighestTermKind(en.getLeftTree()) == 0 ? 0 : NONLINEAR;
			}
			return getHighestTermKind(en.getLeftTree()) == 0
					&& getHighestTermKind(en.getRightTree()) == 0 ? 0 : NONLINEAR;
		}
	}

	/**
	 * @return checker (see class Javadoc)
	 */
	public static DegreeVariableChecker getInstance() {
		return replacer;
	}
}
