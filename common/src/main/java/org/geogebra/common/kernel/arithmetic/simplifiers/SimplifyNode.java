package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;

public interface SimplifyNode {

	/**
	 *
1	 * @param node to check
	 * @return if node could be simplified with this class.
	 */
	boolean isAccepted(ExpressionNode node);

	/**
	 * Apply the simplifier to the node.
	 *
	 * @param node to apply.
	 * @return the simplified node.
	 */
	ExpressionNode apply(ExpressionNode node);

	default String name() {

		String className = getClass().toString();
		String[] tags = className.split("\\.");
		return tags.length > 0 ? tags[tags.length - 1] : "-";
	}
}
