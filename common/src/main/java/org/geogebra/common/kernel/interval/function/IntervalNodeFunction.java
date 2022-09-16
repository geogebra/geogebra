package org.geogebra.common.kernel.interval.function;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.node.IntervalExpression;
import org.geogebra.common.kernel.interval.node.IntervalExpressionNode;
import org.geogebra.common.kernel.interval.node.IntervalFunctionVariable;

public class IntervalNodeFunction {
	IntervalExpressionNode node;
	private final IntervalFunctionVariable functionVariable;

	public IntervalNodeFunction(IntervalExpressionNode node,
			IntervalFunctionVariable functionVariable) {
		this.node = node;
		this.functionVariable = functionVariable;
	}

	public Interval value(Interval x) {
		functionVariable.set(x);
		IntervalExpression expression = node.evaluate();
		return expression == null ? IntervalConstants.undefined() : expression.value();
	}

	public IntervalExpressionNode getNode() {
		return node;
	}
}
