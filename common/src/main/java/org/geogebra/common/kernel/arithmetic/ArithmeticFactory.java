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
	public <T extends FunctionNVar> T newFunction(
			ExpressionNode expressionNode, FunctionVariable[] variables) {
		return (T) (variables.length == 1
						? new Function(expressionNode, variables[0])
						: new FunctionNVar(expressionNode, variables));
	}
}
