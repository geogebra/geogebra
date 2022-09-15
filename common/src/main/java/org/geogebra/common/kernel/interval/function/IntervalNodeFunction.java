package org.geogebra.common.kernel.interval.function;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.node.IntervalExpressionNode;
import org.geogebra.common.kernel.interval.node.IntervalFunctionVariable;

public class IntervalNodeFunction {
	IntervalExpressionNode expression;
	private final IntervalFunctionVariable functionVariable;

	public IntervalNodeFunction(IntervalExpressionNode expression,
			IntervalFunctionVariable functionVariable) {
		this.expression = expression;
		this.functionVariable = functionVariable;
	}

	public Interval value(Interval x) {
		functionVariable.set(x);
		return expression.evaluate().value();
	}
}
