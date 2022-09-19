package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;

/**
 * IntervalNode with operation and siblings - inner node of the tree.
 */
public class IntervalExpressionNode implements IntervalNode {
	IntervalNode left;
	IntervalOperation operation;
	IntervalNode right;

	/**
	 * Constructor of an empty node.
	 */
	public IntervalExpressionNode() {
		this(null, IntervalOperation.NO_OPERATION, null);
	}

	/**
	 * Constructor of a node with one leaf.
	 *
	 * @param value as leaf.
	 */
	public IntervalExpressionNode(IntervalFunctionValue value) {
		this(value, IntervalOperation.NO_OPERATION);
	}

	/**
	 * Constructor of a node with left subtree and its operation.
	 *
	 * @param left subtree.
	 * @param operation on left.
	 */
	public IntervalExpressionNode(IntervalNode left, IntervalOperation operation) {
		this(left, operation, null);
	}

	/**
	 * Constructor of a node with both left and right subtrees and their operation.
	 *
	 * @param left subtree.
	 * @param operation on left.
	 */
	public IntervalExpressionNode(IntervalNode left, IntervalOperation operation,
			IntervalNode right) {
		this.left = left;
		this.operation = operation;
		this.right = right;
	}

	/**
	 *
 	 * @return the evaluated value of this node as root.
	 */
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
