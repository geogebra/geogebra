package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.operators.IntervalOperationImpl;
import org.geogebra.common.plugin.Operation;

public enum IntervalOperation {
	UNSUPPORTED {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left, IntervalExpression right) {
			return null;
		}

		@Override
		public Operation op() {
			return Operation.NO_OPERATION;
		}
	},
	NO_OPERATION {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left, IntervalExpression right) {
			return null;
		}

		@Override
		public Operation op() {
			return Operation.NO_OPERATION;
		}
	},
	ABS {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left, IntervalExpression right) {
			return toValue(IntervalOperationImpl.abs(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.ABS;
		}
	},
	ACOS {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left, IntervalExpression right) {
			return toValue(IntervalOperationImpl.acos(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.ARCCOS;
		}
	},
	ATAN {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left, IntervalExpression right) {
			return toValue(IntervalOperationImpl.atan(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.ARCTAN;
		}
	},
	COS {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.cos(left.value()));
		}


		@Override
		public Operation op() {
			return Operation.COS;
		}
	},
	COSH {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.cosh(left.value()));
		}


		@Override
		public Operation op() {
			return Operation.COSH;
		}
	},
	COT {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.cot(left.value()));
		}


		@Override
		public Operation op() {
			return Operation.COT;
		}
	},
	CSC {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.csc(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.CSC;
		}
	},
	DIVIDE {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.divide(left.value(),
					right.value()));
		}

		@Override
		public Operation op() {
			return Operation.DIVIDE;
		}
	},
	EXP {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.exp(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.EXP;
		}
	},
	LOG {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.log(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.LOG;
		}
	},
	LOG2 {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.log2(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.LOG2;
		}
	},
	LOG10 {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.log10(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.LOG10;
		}
	},

	MINUS {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.minus(left.value(),
					right.value()));
		}


		@Override
		public Operation op() {
			return Operation.MINUS;
		}
	},
	MULTIPLY {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.multiply(left.value(),
					right.value()));
		}


		@Override
		public Operation op() {
			return Operation.MULTIPLY;
		}
	},
	NROOT {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.nthRoot(left.value(),
					right.value()));
		}


		@Override
		public Operation op() {
			return Operation.NROOT;
		}
	},
	PLUS {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.plus(left.value(),
					right.value()));
		}


		@Override
		public Operation op() {
			return Operation.PLUS;
		}
	},
	POW {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.pow(left.value(), right.value()));
		}


		@Override
		public Operation op() {
			return Operation.POWER;
		}
	},


	SEC {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.sec(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.SEC;
		}
	},
	SIN {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.sin(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.SIN;
		}
	},

	SINH {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.sinh(left.value()));
		}


		@Override
		public Operation op() {
			return Operation.SINH;
		}
	},
	SQRT {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.sqrt(left.value()));
		}


		@Override
		public Operation op() {
			return Operation.SQRT;
		}
	},
	TAN {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.tan(left.value()));
		}


		@Override
		public Operation op() {
			return Operation.TAN;
		}
	},

	TANH {
		@Override
		public IntervalExpressionValue handle(IntervalExpression left,
				IntervalExpression right) {
			return toValue(IntervalOperationImpl.tanh(left.value()));
		}

		@Override
		public Operation op() {
			return Operation.TANH;
		}
	};

	public abstract IntervalExpressionValue handle(IntervalExpression left, IntervalExpression right);

	public abstract Operation op();

	static IntervalExpressionValue toValue(Interval interval) {
		return new IntervalFunctionValue(interval);
	}
}
