/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.interval.node;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.operators.IntervalNodeEvaluator;
import org.geogebra.common.plugin.Operation;

/**
 * Interval arithmetic operations.
 */
public enum IntervalOperation {
	UNSUPPORTED {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
				IntervalNode left, IntervalNode right) {
			return toValue(left.value());
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
			return toValue(evaluator.abs(left.value()));
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
			return toValue(evaluator.acos(left.value()));
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
			return toValue(evaluator.asin(left.value()));
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
			return toValue(evaluator.atan(left.value()));
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
			return toValue(evaluator.cos(left.value()));
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
			return toValue(evaluator.cosh(left.value()));
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
			return toValue(evaluator.cot(left.value()));
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
			return toValue(evaluator.csc(left.value()));
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
			return toValue(evaluator.divide(left.value(),
					right.value()));
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
			return toValue(evaluator.exp(left.value()));
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
			return toValue(evaluator.log(left.value()));
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
			return toValue(evaluator.log2(left.value()));
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
			return toValue(evaluator.log10(left.value()));
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
			return toValue(evaluator.minus(left.value(),
					right.value()));
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
			return toValue(evaluator.multiply(left.value(),
					right.value()));
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
			return toValue(evaluator.nthRoot(left.value(),
					right.value()));
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
			return toValue(evaluator.plus(left.value(),
					right.value()));
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
			return toValue(evaluator.handlePower(left.value(), right.value(), right));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.POWER;
		}
	},

	LOGB {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.logBase(left.value(), right.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.LOGB;
		}
	},

	SEC {
		@Override
		public IntervalExpressionValue handle(IntervalNodeEvaluator evaluator, IntervalNode left,
				IntervalNode right) {
			return toValue(evaluator.sec(left.value()));
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
			return toValue(evaluator.sin(left.value()));
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
			return toValue(evaluator.sinh(left.value()));
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
			return toValue(evaluator.sqrt(left.value()));
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
			return toValue(evaluator.tan(left.value()));
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
			return toValue(evaluator.tanh(left.value()));
		}

		@Override
		public Operation mappedOperation() {
			return Operation.TANH;
		}
	};

	/**
	 * Executes the operation.
	 * @param evaluator of the node and its operations
	 * @param left {@link IntervalNode} as operand.
	 * @param right {@link IntervalNode} as operand.
	 * @return the value of the operation.
	 */
	public abstract IntervalExpressionValue handle(IntervalNodeEvaluator evaluator,
			@Nonnull IntervalNode left, IntervalNode right);

	/**
	 * @return corresponding operation for <code>ExpressionValue</code>s
	 */
	public abstract Operation mappedOperation();

	static IntervalExpressionValue toValue(@Nonnull Interval interval) {
		return new IntervalFunctionValue(interval);
	}
}
