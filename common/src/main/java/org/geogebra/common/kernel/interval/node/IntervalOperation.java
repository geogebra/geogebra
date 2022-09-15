package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.operators.IntervalOperands;

public enum IntervalOperation {

	NO_OPERATION {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpressionValue left, IntervalExpressionValue right) {
			return null;
		}
	},
	SIN {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpressionValue left,
				IntervalExpressionValue right) {
			return new IntervalFunctionValue(IntervalOperands.sin(left.value()));
		}
	};

	public abstract IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
		IntervalExpressionValue left, IntervalExpressionValue right);
}
