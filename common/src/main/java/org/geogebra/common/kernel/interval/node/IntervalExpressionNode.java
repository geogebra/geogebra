package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;

public class IntervalExpressionNode implements IntervalNode {
	IntervalNode left;
	IntervalOperation operation;
	IntervalNode right;

	public IntervalExpressionNode() {
		this(null, IntervalOperation.NO_OPERATION, null);
	}

	public IntervalExpressionNode(IntervalFunctionValue value) {
		this(value, IntervalOperation.NO_OPERATION);
	}

	public IntervalExpressionNode(IntervalNode left, IntervalOperation operation) {
		this(left, operation, null);
	}

	public IntervalExpressionNode(IntervalNode left, IntervalOperation operation,
			IntervalNode right) {
		this.left = left;
		this.operation = operation;
		this.right = right;
	}

	public IntervalNode evaluate() {
		if (isLeaf()) {
			return left;
		}

		return operation.handle(left, right);
	}

	@Override
	public boolean isLeaf() {
		return IntervalOperation.NO_OPERATION.equals(operation) && hasLeft() && left.isLeaf();
	}

	@Override
	public IntervalExpressionNode asExpressionNode() {
		return this;
	}

	@Override
	public Interval value() {
		IntervalNode node = evaluate();
		return node == null ? IntervalConstants.undefined() : node.value();
	}

	@Override
	public boolean hasFunctionVariable() {
		return hasLeft() && left.hasFunctionVariable()
				|| hasRight() && right.hasFunctionVariable();
	}

	public void setLeft(IntervalNode left) {
		this.left = left;
	}

	public void setOperation(IntervalOperation operation) {
		this.operation = operation;
	}

	public void setRight(IntervalNode right) {
		this.right = right;
	}

	public IntervalNode getLeft() {
		return left;
	}

	public IntervalOperation getOperation() {
		return operation;
	}

	public IntervalNode getRight() {
		return right;
	}

	public boolean hasLeft() {
		return left != null;
	}

	public boolean hasRight() {
		return right != null;
	}
}
