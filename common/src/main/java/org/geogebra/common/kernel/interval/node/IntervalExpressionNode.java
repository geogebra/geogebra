package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;

public class IntervalExpressionNode implements IntervalExpression {
	IntervalExpression left;
	IntervalOperation operation;
	IntervalExpression right;

	public IntervalExpressionNode() {
		this(null, IntervalOperation.NO_OPERATION, null);
	}

	public IntervalExpressionNode(IntervalFunctionValue value) {
		this(value, IntervalOperation.NO_OPERATION);
	}

	public IntervalExpressionNode(IntervalExpression left, IntervalOperation operation) {
		this(left, operation, null);
	}

	public IntervalExpressionNode(IntervalExpression left, IntervalOperation operation,
			IntervalExpression right) {
		this.left = left;
		this.operation = operation;
		this.right = right;
	}

	public void simplify() {
		left = simplifyTree(left);
		right = simplifyTree(right);
	}

	private IntervalExpression simplifyTree(IntervalExpression tree) {
		if (tree == null || tree.isLeaf()) {
			return tree;
		}

		IntervalExpressionNode node = tree.wrap();

		IntervalExpression left = node.getLeft();
		if (node.hasLeft()) {
			if (left.hasFunctionVariable()) {
				left.wrap().setLeft(simplifyTree(left.wrap().getLeft()));
				left.wrap().setRight(simplifyTree(left.wrap().getRight()));
			} else {
				left.wrap().setLeft(new IntervalFunctionValue(left.value()));
			}
		}

		IntervalExpression right = node.getRight();
		if (node.hasRight()) {
			if (right.hasFunctionVariable()) {
				right.wrap().setLeft(simplifyTree(right.wrap().getLeft()));
				right.wrap().setRight(simplifyTree(right.wrap().getRight()));
			} else {
				left.wrap().setRight(new IntervalFunctionValue(right.value()));
			}
		}
		return node;
	}

	public IntervalExpression evaluate() {
		if (isLeaf()) {
			return left;
		}

		return operation.handle(null, left, right);
	}

	public boolean isLeaf() {
		return IntervalOperation.NO_OPERATION.equals(operation) && hasLeft() && left.isLeaf();
	}

	@Override
	public boolean isNode() {
		return true;
	}

	@Override
	public IntervalExpressionNode wrap() {
		return this;
	}

	@Override
	public IntervalExpression unwrap() {
		return this;
	}

	@Override
	public Interval value() {
		IntervalExpression expression = evaluate();
		return expression == null ? IntervalConstants.undefined() : expression.value();
	}

	@Override
	public boolean hasFunctionVariable() {
		return hasLeft() && left.hasFunctionVariable()
				|| hasRight() && right.hasFunctionVariable();
	}

	public void setLeft(IntervalExpression left) {
		this.left = left;
	}

	public void setOperation(IntervalOperation operation) {
		this.operation = operation;
	}

	public void setRight(IntervalExpression right) {
		this.right = right;
	}

	public IntervalExpression getLeft() {
		return left;
	}

	public IntervalOperation getOperation() {
		return operation;
	}

	public IntervalExpression getRight() {
		return right;
	}

	public boolean hasLeft() {
		return left != null;
	}

	public boolean hasRight() {
		return right != null;
	}
}
