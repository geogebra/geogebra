package org.geogebra.common.kernel.interval.node;

public class IntervalExpressionNode {
	IntervalExpressionValue left;
	IntervalOperation operation;
	IntervalExpressionValue right;

	public IntervalExpressionNode(IntervalFunctionValue value) {
		this(value, IntervalOperation.NO_OPERATION);
	}

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
		if (isLeaf()) {
			return left;
		}

		return operation.handle(null, left, right);
	}

	public boolean isLeaf() {
		return IntervalOperation.NO_OPERATION.equals(operation) && left.isLeaf();
	}
}
