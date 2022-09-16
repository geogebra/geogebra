package org.geogebra.common.kernel.interval.function;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.node.IntervalExpression;
import org.geogebra.common.kernel.interval.node.IntervalExpressionNode;
import org.geogebra.common.kernel.interval.node.IntervalFunctionValue;
import org.geogebra.common.kernel.interval.node.IntervalFunctionVariable;
import org.geogebra.common.kernel.interval.node.IntervalOperation;
import org.geogebra.common.plugin.Operation;

public class GeoFunctionConverter {
	private final Map<Operation, IntervalOperation> operationMap = new HashMap<>();

	public GeoFunctionConverter() {
		for (IntervalOperation operation: IntervalOperation.values()) {
			if (operation != IntervalOperation.UNSUPPORTED) {
				operationMap.put(operation.op(), operation);
			}
		}
	}

	public IntervalNodeFunction convert(GeoFunction geoFunction) {
		IntervalFunctionVariable functionVariable = new IntervalFunctionVariable();
		IntervalExpression expression = convert(geoFunction.getFunctionExpression(),
				functionVariable);
		return new IntervalNodeFunction(expression.wrap(), functionVariable);
	}

	private IntervalExpression convert(ExpressionNode expressionNode,
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

		node.setOperation(operationMap.getOrDefault(expressionNode.getOperation(),
				 IntervalOperation.UNSUPPORTED));

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

	private IntervalExpression newSingletonValue(double value) {
		return Double.isNaN(value) ? null: new IntervalFunctionValue(new Interval(value));
	}
}
