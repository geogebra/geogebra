package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.plugin.Operation;

public class ConditionalEvaluator implements IntervalEvaluator {
	private ExpressionNode node;
	private Operation operation;

	public void setNode(ExpressionNode node) {
		this.node = node;
		operation = node.getOperation();
	}

	@Override
	public boolean isAccepted() {
		return isIfElse();
	}

	private boolean isIfElse() {
		return Operation.IF_ELSE.equals(operation);
	}

	@Override
	public Interval evaluate(Interval x) {
		if (isIfElse()) {
			return evaluateIfElse(x);
		}
		return null;
	}

	private Interval evaluateIfElse(Interval x) {
		ExpressionValue left = node.getLeft();
		if (left instanceof MyNumberPair) {
			MyNumberPair pair = (MyNumberPair) left;
			ExpressionValue condition = pair.getX();
			if (evaluateBoolean(x, condition.wrap())) {
				ExpressionNode ifExpr = pair.getY().wrap();
				return IntervalFunction.evaluate(x, ifExpr);
			} else {
				return IntervalFunction.evaluate(x, node.getRight());
			}
		}

		return null;
	}

	private boolean evaluateBoolean(Interval x, ExpressionNode condition) {
		Operation operation = condition.getOperation();
		ExpressionValue value = condition.getRight();
		switch (operation) {
		case LESS:
			return x.isLessThan(IntervalFunction.evaluate(x, value));
		case GREATER:
			return x.isGreaterThan(IntervalFunction.evaluate(x, value));
		}
		return true;
	}
}
