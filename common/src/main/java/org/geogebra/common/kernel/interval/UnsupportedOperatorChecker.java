package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

/**
 * Checker to determine ia an operation is supported by the interval arithmetic.
 */
public class UnsupportedOperatorChecker implements Inspecting {

	@Override
	public boolean check(ExpressionValue v) {
		ExpressionNode wrap = v.wrap();
		Operation operation = wrap.getOperation();
		switch (operation) {
		case PLUS:
		case MINUS:
		case DIVIDE:
		case NROOT:
		case DIFF:
		case SIN:
		case COS:
		case SEC:
		case COT:
		case CSC:
		case SQRT:
		case TAN:
		case EXP:
		case LOG:
		case ARCCOS:
		case ARCSIN:
		case ARCTAN:
		case ABS:
		case COSH:
		case SINH:
		case TANH:
		case LOG10:
		case LOG2:
		case NO_OPERATION:
			return false;
		case MULTIPLY:
			return checkMultiply(wrap);
		case POWER:
			return checkPower(wrap);
		default:
			return true;
		}
	}

	private boolean checkMultiply(ExpressionNode node) {
		return isVector(node.getLeft()) ^ isVector(node.getRight());
	}

	private boolean isVector(ExpressionValue node) {
		return node.evaluatesToNDVector() || node.evaluatesToNonComplex2DVector();
	}

	private boolean checkPower(ExpressionNode node) {
		double power = node.getRight().evaluateDouble();
		return node.getRightTree().containsFunctionVariable()
				|| !DoubleUtil.isInteger(power) || power < 0
				|| power >= 100;
	}
}
