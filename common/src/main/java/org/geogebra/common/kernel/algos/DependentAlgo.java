package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;

/**
 * Intrface for AlgoDependent* algos
 *
 */
public interface DependentAlgo {

	/**
	 * @return defining expression
	 */
	public ExpressionNode getExpression();
}
