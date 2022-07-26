package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalFunction;
import org.geogebra.common.plugin.Operation;

public class IntervalConditionalExpression {
	private final ExpressionNode condition;
	private final ExpressionNode body;

	/**
	 *
	 * @param condition of the sampler.
	 * @param body to evaluate with, when condition is true
	 */
	public IntervalConditionalExpression(ExpressionNode condition, ExpressionNode body) {
		this.condition = condition;
		this.body = body;
	}

	public boolean isAccepted(Interval x) {
		return isTrue(x);
	}

	boolean isTrueBetween(Interval x) {
		if (Operation.AND_INTERVAL.equals(condition.getOperation())) {
			return false;
		}
		return isTrue(x.getLow()) ^ isTrue(x.getHigh());
	}

	private boolean isTrue(double value) {
		return isTrue(new Interval(value));
	}

	boolean isTrue(Interval x) {
		return isExpressionTrue(x, condition.getLeft(),
				condition.getOperation(), condition.getRight());
	}

	private boolean isExpressionTrue(Interval x, ExpressionValue left, Operation operation,
			ExpressionValue right) {
		if (operation.equals(Operation.AND_INTERVAL)) {
			ExpressionNode nodeLeft = left.wrap();
			ExpressionNode nodeRight = right.wrap();
			return isExpressionTrue(x, nodeLeft.getLeft(),
					nodeLeft.getOperation(), nodeLeft.getRight())
					&& isExpressionTrue(x, nodeRight.getLeft(),
					nodeRight.getOperation(), nodeRight.getRight());
		}

		return evaluateBoolean(IntervalFunction.evaluate(x, left), operation,
				IntervalFunction.evaluate(x, right));
	}

	private boolean evaluateBoolean(Interval y1, Operation operation, Interval y2) {
		switch (operation) {
		case EQUAL_BOOLEAN:
			return y1.contains(y2);
		case LESS:
			return y1.isLessThan(y2);
		case LESS_EQUAL:
			return y1.isLessThanOrEqual(y2);
		case GREATER:
			return y1.isGreaterThan(y2);
		case GREATER_EQUAL:
			return y2.isLessThanOrEqual(y1);
		case NOT_EQUAL:
			return !y1.contains(y2);

		}
		return false;
	}

	public ExpressionValue getBody() {
		return body;
	}

	public double getSplitValue() {
		return condition.getRight().evaluateDouble();
	}
}
