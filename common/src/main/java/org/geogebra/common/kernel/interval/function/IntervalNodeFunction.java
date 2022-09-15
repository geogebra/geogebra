package org.geogebra.common.kernel.interval.function;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.node.IntervalExpression;
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
		IntervalExpression expression1 = expression.evaluate();
		return expression1 == null ? IntervalConstants.undefined() : expression1.value();
	}

	public IntervalExpressionNode getExpression() {
		return expression;
	}
}
