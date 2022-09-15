package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.operators.IntervalOperands;

public enum IntervalOperation {
	NO_OPERATION {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left, IntervalExpression right) {
			return null;
		}
	},
	PLUS {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.plus(left.value(),
					right.value()));
		}
	},
	MINUS {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.minus(left.value(),
					right.value()));
		}
	},

	MULTIPLY {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.multiply(left.value(),
					right.value()));
		}
		},
	DIVIDE {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.divide(left.value(),
					right.value()));
		}
	},

	SIN {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.sin(left.value()));
		}
	},
	COS {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.cos(left.value()));
		}
	},

	TAN {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.tan(left.value()));
		}
	},
	;

	public abstract IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
		IntervalExpression left, IntervalExpression right);

	static IntervalExpressionValue toValue(Interval interval) {
		return new IntervalFunctionValue(interval);
	}
}
