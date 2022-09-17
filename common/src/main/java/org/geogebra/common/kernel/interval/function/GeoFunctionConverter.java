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
	private IntervalOperationSupport operationSupport;
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
		ExpressionValue left = expressionNode.getLeft();
		ExpressionValue right = expressionNode.getRight();
		IntervalExpressionNode node = new IntervalExpressionNode();
		if (left != null) {
			if (left.isLeaf()) {
				if (left instanceof FunctionVariable) {
					node.setLeft(functionVariable);
				} else {
					node.setLeft(newSingletonValue(left.evaluateDouble()));
				}
			} else {
				node.setLeft(convert(left.wrap(), functionVariable));
			}
		}

		node.setOperation(operationSupport.convert(expressionNode.getOperation()));

		if (right != null) {
			if (right.isLeaf()) {
				if (right instanceof FunctionVariable) {
					node.setRight(functionVariable);
				} else {
					node.setRight(newSingletonValue(right.evaluateDouble()));
				}
			} else {
				node.setRight(convert(right.wrap(), functionVariable));
			}
		}


		return node;
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
