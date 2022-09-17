package org.geogebra.common.kernel.interval.function;

import java.util.Objects;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.node.IntervalExpressionNode;
import org.geogebra.common.kernel.interval.node.IntervalFunctionValue;
import org.geogebra.common.kernel.interval.node.IntervalFunctionVariable;
import org.geogebra.common.kernel.interval.node.IntervalNode;
import org.geogebra.common.kernel.interval.node.IntervalOperationSupport;

public class GeoFunctionConverter {
	private final IntervalOperationSupport operationSupport;
	private final UnsupportedOperatorChecker
			operatorChecker = new UnsupportedOperatorChecker();

	public GeoFunctionConverter() {
		operationSupport = new IntervalOperationSupport();
	}

	public IntervalNodeFunction convert(GeoFunction geoFunction) {
		IntervalFunctionVariable functionVariable = new IntervalFunctionVariable();
		IntervalNode expression = convert(
				Objects.requireNonNull(geoFunction.getFunctionExpression()),
				functionVariable);
		return new IntervalNodeFunction(expression.asExpressionNode(), functionVariable);
	}

	private IntervalNode convert(ExpressionNode expressionNode,
			IntervalFunctionVariable functionVariable) {
		IntervalExpressionNode node = new IntervalExpressionNode();
		convertLeft(functionVariable, expressionNode.getLeft(), node);
		node.setOperation(operationSupport.convert(expressionNode.getOperation()));
		convertRight(functionVariable, expressionNode.getRight(), node);
		return node;
	}

	private void convertLeft(IntervalFunctionVariable functionVariable, ExpressionValue left,
			IntervalExpressionNode node) {
		if (left == null) {
			return;
		}

		node.setLeft(nodeValue(functionVariable, left));

	}

	private IntervalNode nodeValue(IntervalFunctionVariable functionVariable,
			ExpressionValue value) {
		return value.isLeaf() ? newLeafValue(value, functionVariable) :
				convert(value.wrap(), functionVariable);
	}

	private IntervalNode newLeafValue(ExpressionValue value,
			IntervalFunctionVariable functionVariable) {
		return value instanceof FunctionVariable
				? functionVariable : newSingletonValue(value.evaluateDouble());
	}

	private void convertRight(IntervalFunctionVariable functionVariable, ExpressionValue right,
			IntervalExpressionNode node) {
		if (right == null) {
			return;
		}

		node.setRight(nodeValue(functionVariable, right));
	}

	private IntervalNode newSingletonValue(double value) {
		return Double.isNaN(value) ? null: new IntervalFunctionValue(new Interval(value));
	}


	/**
	 *
	 * @param geo to check.
	 * @return true if the geo is a function
	 * and supported by our interval arithmetic implementation.
	 */
	public boolean isSupported(GeoElement geo) {
		if (!(geo instanceof GeoFunction)) {
			return false;
		}

		return isOperationSupported(((GeoFunction) geo).getFunctionExpression());
	}

	boolean isOperationSupported(ExpressionNode node) {
		if (node == null) {
			return false;
		}

		return !hasMoreVariables(node) && !node.inspect(operatorChecker);
	}

	private boolean hasMoreVariables(ExpressionNode node) {
		if (node == null) {
			return false;
		}
		return node.inspect(new MultipleVariableChecker());
	}
}
