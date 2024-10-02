package org.geogebra.common.kernel.arithmetic;

/**
 * Arithmetic factory
 */
public class ArithmeticFactory {

	/**
	 * @param expressionNode expression node
	 * @param variables variables
	 * @return new function
	 */
	public FunctionNVar newFunction(
			ExpressionNode expressionNode, FunctionVariable[] variables) {
		return variables.length == 1
						? new Function(expressionNode, variables[0])
						: new FunctionNVar(expressionNode, variables);
	}
}
