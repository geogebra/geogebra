package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;

/**
 * Interface for nodes and leaves in interval expression tree.
 */
public interface IntervalNode {
	/**
	 *
	 * @return if node is a leaf.
	 */
	boolean isLeaf();

	/**
	 *
	 * @return node as IntervalExpressionNode if it is, null otherwise.
	 */
	IntervalExpressionNode asExpressionNode();

	/**
	 *
	 * @return the evaluated value of the node.
	 */
	Interval value();

	/**
	 *
	 * @return if node or its subtrees have function variable.
	 */
	boolean hasFunctionVariable();

	IntervalNode simplify();
}
