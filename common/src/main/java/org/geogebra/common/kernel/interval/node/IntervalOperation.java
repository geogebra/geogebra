package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.operators.IntervalOperands;
import org.geogebra.common.plugin.Operation;

public enum IntervalOperation {
	UNSUPPORTED {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left, IntervalExpression right) {
			return null;
		}

		@Override
		public Operation op() {
			return Operation.NO_OPERATION;
		}
	},
	NO_OPERATION {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left, IntervalExpression right) {
			return null;
		}

		@Override
		public Operation op() {
			return Operation.NO_OPERATION;
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


		@Override
		public Operation op() {
			return Operation.PLUS;
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


		@Override
		public Operation op() {
			return Operation.MINUS;
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


		@Override
		public Operation op() {
			return Operation.MULTIPLY;
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

		@Override
		public Operation op() {
			return Operation.DIVIDE;
		}
	},

	SIN {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.sin(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.SIN;
		}
	},
	COS {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.cos(left.value()));
		}


		@Override
		public Operation op() {
			return Operation.COS;
		}
	},

	TAN {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.tan(left.value()));
		}


		@Override
		public Operation op() {
			return Operation.TAN;
		}
	},
	SINH {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.sinh(left.value()));
		}


		@Override
		public Operation op() {
			return Operation.SINH;
		}
	},
	COSH {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.cosh(left.value()));
		}


		@Override
		public Operation op() {
			return Operation.COSH;
		}
	},

	TANH {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.tanh(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.TANH;
		}
	},
	CSC {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.csc(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.CSC;
		}
	},
	SEC {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.tan(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.SEC;
		}
	},

	POW {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperands.pow(left.value(), right.value()));
		}


		@Override
		public Operation op() {
			return Operation.POWER;
		}
	},


	;

	public abstract IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
		IntervalExpression left, IntervalExpression right);

	public abstract Operation op();

	static IntervalExpressionValue toValue(Interval interval) {
		return new IntervalFunctionValue(interval);
	}
}
