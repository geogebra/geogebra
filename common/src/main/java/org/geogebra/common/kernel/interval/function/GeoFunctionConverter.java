package org.geogebra.common.kernel.interval.function;

import java.util.Objects;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.node.IntervalExpressionNode;
import org.geogebra.common.kernel.interval.node.IntervalFunctionValue;
import org.geogebra.common.kernel.interval.node.IntervalFunctionVariable;
import org.geogebra.common.kernel.interval.node.IntervalNode;
import org.geogebra.common.kernel.interval.node.IntervalOperation;
import org.geogebra.common.kernel.interval.node.IntervalOperationSupport;
import org.geogebra.common.kernel.interval.operators.IntervalNodeEvaluator;
import org.geogebra.common.plugin.Operation;

/**
 * Converts GeoFunction as parameter to IntervalNodeFunction.
 * It builds up an IntervalNode tree of the function from the ExpressionNode tree of the
 * original GeoFunction.
 *
 * Thus, the converted function will be optimized for interval evaluation for further use.
 *
 */
public class GeoFunctionConverter {
	private final IntervalOperationSupport operationSupport;
	private final IntervalNodeEvaluator evaluator;

	/**
	 * Constructor.
	 */
	public GeoFunctionConverter() {
		operationSupport = new IntervalOperationSupport();
		evaluator = new IntervalNodeEvaluator();
	}

	/**
	 * Converts GeoFunction as parameter to IntervalNodeFunction.
	 * @param geoFunction to convert.
	 * @return the converted IntervalNodeFunction.
	 */
	public IntervalNodeFunction convert(GeoFunction geoFunction) {
		IntervalFunctionVariable functionVariable = new IntervalFunctionVariable();
		IntervalNode expression = convert(
				Objects.requireNonNull(geoFunction.getFunctionExpression()),
				functionVariable).simplify();
		if (expression.asExpressionNode() == null) {
			expression = new IntervalExpressionNode(evaluator, expression,
					IntervalOperation.NO_OPERATION);
		}
		return new IntervalNodeFunction(expression.asExpressionNode(), functionVariable);
	}

	private IntervalNode convert(ExpressionNode expressionNode,
			IntervalFunctionVariable functionVariable) {
		IntervalExpressionNode node = new IntervalExpressionNode(evaluator);
		node.setLeft(nodeValue(functionVariable, expressionNode.getLeft()));
		node.setOperation(operationSupport.convert(expressionNode.getOperation()));
		node.setRight(nodeValue(functionVariable, expressionNode.getRight()));
		return node;
	}

	private IntervalNode nodeValue(IntervalFunctionVariable functionVariable,
			ExpressionValue value) {
		if (value == null) {
			return null;
		}
		ExpressionValue unwrapped = value.unwrap();
		return !unwrapped.isExpressionNode()
				? newLeafValue(unwrapped, functionVariable)
				: convert(unwrapped.wrap(), functionVariable);
	}

	private IntervalNode newLeafValue(ExpressionValue value,
			IntervalFunctionVariable functionVariable) {
		return value instanceof FunctionVariable
				? functionVariable : newSingletonValue(value.evaluateDouble());
	}

	private IntervalNode newSingletonValue(double value) {
		return new IntervalFunctionValue(Double.isNaN(value)
				? IntervalConstants.undefined()
				: new Interval(value));
	}

	/**
	 * @param operation to check.
	 * @return if operation has supported by interval arithmetic.
	 */
	public boolean isSupportedOperation(Operation operation) {
		return operationSupport.isSupported(operation);
	}
}