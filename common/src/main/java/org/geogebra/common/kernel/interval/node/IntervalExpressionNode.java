package org.geogebra.common.kernel.interval.node;

public class IntervalExpressionNode {
	IntervalExpressionValue left;
	IntervalOperation operation;
	IntervalExpressionValue right;

	public IntervalExpressionNode(IntervalExpressionValue left, IntervalOperation operation) {
		this(left, operation, null);
	}

	public IntervalExpressionNode(IntervalExpressionValue left, IntervalOperation operation,
			IntervalExpressionValue right) {
		this.left = left;
		this.operation = operation;
		this.right = right;
	}

	public IntervalExpressionValue evaluate() {
		return operation.handle(null, left, right);
	}
}
