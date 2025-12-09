/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.operators.IntervalNodeEvaluator;

/**
 * IntervalNode with operation and siblings - inner node of the tree.
 */
public class IntervalExpressionNode implements IntervalNode {
	IntervalNode left;
	IntervalOperation operation;
	IntervalNode right;
	private final IntervalNodeEvaluator evaluator;

	/**
	 * Constructor of an empty node.
	 * @param evaluator of the node and its operations
	 */
	public IntervalExpressionNode(IntervalNodeEvaluator evaluator) {
		this(evaluator, null, IntervalOperation.NO_OPERATION, null);
	}

	/**
	 * Constructor of a node with one leaf.
	 * @param evaluator of the node and its operations
	 * @param value as leaf.
	 */
	public IntervalExpressionNode(IntervalNodeEvaluator evaluator, IntervalFunctionValue value) {
		this(evaluator, value, IntervalOperation.NO_OPERATION);
	}

	/**
	 * Constructor of a node with left subtree and its operation.
	 * @param evaluator of the node and its operations
	 * @param left subtree.
	 * @param operation on left.
	 */
	public IntervalExpressionNode(IntervalNodeEvaluator evaluator, IntervalNode left,
			IntervalOperation operation) {
		this(evaluator, left, operation, null);
	}

	/**
	 * Constructor of a node with both left and right subtrees and their operation.
	 * @param evaluator of the node and its operations
	 * @param left subtree.
	 * @param operation on left.
	 */
	public IntervalExpressionNode(IntervalNodeEvaluator evaluator, IntervalNode left,
			IntervalOperation operation,
			IntervalNode right) {
		this.evaluator = evaluator;
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

		return operation.handle(evaluator, left, right);
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

	@Override
	public IntervalNode simplify() {
		if (left != null) {
			left = left.simplify();
		}
		if (right != null) {
			right = right.simplify();
		}
		// fractions can't be simplified because x^(1/3) is not x^(0.33)
		if (left instanceof IntervalFunctionValue
				&& !isOperation(IntervalOperation.DIVIDE)
				&& (right == null || right instanceof IntervalFunctionValue)) {
			return new IntervalFunctionValue(value());
		}
		return this;
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

	private boolean hasLeft() {
		return left != null;
	}

	private boolean hasRight() {
		return right != null;
	}

	/**
	 * @param operation operation
	 * @return whether this node has given operation (on top level)
	 */
	public boolean isOperation(IntervalOperation operation) {
		return this.operation.equals(operation);
	}

	@Override
	public String toString() {
		return operation.name() + "(" + left + "," + right + ")";
	}
}
