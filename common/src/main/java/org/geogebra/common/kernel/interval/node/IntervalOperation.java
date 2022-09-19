package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.operators.IntervalOperationImpl;
import org.geogebra.common.plugin.Operation;

public enum IntervalOperation {
	UNSUPPORTED {
		@Override
		public IntervalExpressionValue handle(IntervalNode left, IntervalNode right) {
			return null;
		}

		@Override
		public Operation mappedOperation() {
			return Operation.NO_OPERATION;
		}
	},
	NO_OPERATION {
		@Override
		public IntervalExpressionValue handle(IntervalNode left, IntervalNode right) {
			return null;
		}

		@Override
		public Operation mappedOperation() {
			return Operation.NO_OPERATION;
		}
	},
	ABS {
		@Override
		public IntervalExpressionValue handle(IntervalNode left, IntervalNode right) {
			return toValue(IntervalOperationImpl.abs(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.ABS;
		}
	},
	ACOS {
		@Override
		public IntervalExpressionValue handle(IntervalNode left, IntervalNode right) {
			return toValue(IntervalOperationImpl.acos(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.ARCCOS;
		}
	},
	ASIN {
		@Override
		public IntervalExpressionValue handle(IntervalNode left, IntervalNode right) {
			return toValue(IntervalOperationImpl.asin(left.value()));
		}
		@Override
		public Operation mappedOperation() {
			return Operation.ARCSIN;
		}
	},
	ATAN {
		@Override
		public IntervalExpressionValue handle(IntervalNode left, IntervalNode right) {
			return toValue(IntervalOperationImpl.atan(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.ARCTAN;
		}
	},
	COS {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.cos(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.COS;
		}
	},
	COSH {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.cosh(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.COSH;
		}
	},
	COT {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.cot(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.COT;
		}
	},
	CSC {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.csc(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.CSC;
		}
	},
	DIVIDE {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.divide(left.value(),
					right.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.DIVIDE;
		}
	},
	EXP {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.exp(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.EXP;
		}
	},
	LOG {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.log(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.LOG;
		}
	},
	LOG2 {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.log2(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.LOG2;
		}
	},
	LOG10 {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.log10(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.LOG10;
		}
	},
	MINUS {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.minus(left.value(),
					right.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.MINUS;
		}
	},
	MULTIPLY {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.multiply(left.value(),
					right.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.MULTIPLY;
		}
	},
	NROOT {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.nthRoot(left.value(),
					right.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.NROOT;
		}
	},
	PLUS {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.plus(left.value(),
					right.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.PLUS;
		}
	},
	POW {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.pow(left.value(), right.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.POWER;
		}
	},

	SEC {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.sec(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.SEC;
		}
	},
	SIN {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.sin(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.SIN;
		}
	},
	SINH {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.sinh(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.SINH;
		}
	},
	SQRT {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.sqrt(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.SQRT;
		}
	},
	TAN {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.tan(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.TAN;
		}
	},
	TANH {
		@Override
		public IntervalExpressionValue handle(IntervalNode left,
				IntervalNode right) {
			return toValue(IntervalOperationImpl.tanh(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.TANH;
		}
	};

	public static boolean hasEquivalent(Operation operation) {
		for (IntervalOperation iop: values()) {
			if (iop.mappedOperation().equals(operation)) {
				return true;
			}
		}
		return false;
	}

	public abstract IntervalExpressionValue handle(IntervalNode left, IntervalNode right);

	public abstract Operation mappedOperation();

	static IntervalExpressionValue toValue(Interval interval) {
		return new IntervalFunctionValue(interval);
	}
}
