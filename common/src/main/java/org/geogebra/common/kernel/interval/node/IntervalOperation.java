package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.operators.IntervalNodeEvaluator;
import org.geogebra.common.plugin.Operation;

public enum IntervalOperation {
	UNSUPPORTED {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalNode left, IntervalNode right) {
			return null;
		}

		@Override
		public Operation mappedOperation() {
			return Operation.NO_OPERATION;
		}
	},
	NO_OPERATION {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalNode left, IntervalNode right) {
			return null;
		}

		@Override
		public Operation mappedOperation() {
			return Operation.NO_OPERATION;
		}
	},
	ABS {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalNode left, IntervalNode right) {
			return toValue(evaluator.abs(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.ABS;
		}
	},
	ACOS {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalNode left, IntervalNode right) {
			return toValue(evaluator.acos(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.ARCCOS;
		}
	},
	ASIN {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalNode left, IntervalNode right) {
			return toValue(evaluator.asin(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.ARCSIN;
		}
	},
	ATAN {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalNode left, IntervalNode right) {
			return toValue(evaluator.atan(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.ARCTAN;
		}
	},
	COS {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.cos(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.COS;
		}
	},
	COSH {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.cosh(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.COSH;
		}
	},
	COT {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.cot(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.COT;
		}
	},
	CSC {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.csc(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.CSC;
		}
	},
	DIVIDE {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.divide(value(left),
					value(right)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.DIVIDE;
		}
	},
	EXP {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.exp(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.EXP;
		}
	},
	LOG {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.log(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.LOG;
		}
	},
	LOG2 {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.log2(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.LOG2;
		}
	},
	LOG10 {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.log10(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.LOG10;
		}
	},
	MINUS {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.minus(value(left),
					value(right)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.MINUS;
		}
	},
	MULTIPLY {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.multiply(value(left),
					value(right)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.MULTIPLY;
		}
	},
	NROOT {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.nthRoot(value(left),
					value(right)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.NROOT;
		}
	},
	PLUS {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.plus(value(left),
					value(right)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.PLUS;
		}
	},
	POWER {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.handlePower(value(left), value(right), right));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.POWER;
		}
	},

	SEC {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.sec(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.SEC;
		}
	},
	SIN {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.sin(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.SIN;
		}
	},
	SINH {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.sinh(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.SINH;
		}
	},
	SQRT {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.sqrt(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.SQRT;
		}
	},
	TAN {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.tan(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.TAN;
		}
	},
	TANH {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.tanh(value(left)));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.TANH;
		}
	};

	private static Interval value(IntervalNode node) {
		return node == null ? IntervalConstants.undefined() : node.value();
	}

	/**
	 * Executes the operation.
	 * @param evaluator of the node and its operations
	 * @param left {@link IntervalNode} as operand.
	 * @param right {@link IntervalNode} as operand.
	 * @return the value of the operation.
	 */
	public abstract IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
			IntervalNode left, IntervalNode right);

	public abstract Operation mappedOperation();

	static IntervalExpressionValue toValue(Interval interval) {
		return new IntervalFunctionValue(interval);
	}
}
