package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.power;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.plugin.Operation;

enum ExpandSteps implements SimplificationStepGenerator {

	EXPAND_DIFFERENCE_OF_SQUARES {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				if (so.noOfOperands() != 2 || !so.getOperand(0).isSum()
						|| !so.getOperand(1).isSum()) {
					return sn.iterateThrough(this, sb, tracker);
				}

				StepOperation first = (StepOperation) so.getOperand(0);
				StepOperation second = (StepOperation) so.getOperand(1);

				if (first.noOfOperands() != 2 || second.noOfOperands() != 2) {
					return sn.iterateThrough(this, sb, tracker);
				}

				if (first.getOperand(0).equals(second.getOperand(0))
						&& first.getOperand(1).equals(second.getOperand(1).negate())) {
					first.getOperand(0).setColor(tracker.getColorTracker());
					second.getOperand(0).setColor(tracker.incColorTracker());
					first.getOperand(1).setColor(tracker.getColorTracker());
					second.getOperand(1).setColor(tracker.incColorTracker());

					StepExpression[] sum = new StepExpression[2];

					sum[0] = power(first.getOperand(0), 2);
					if (first.getOperand(1).isNegative()) {
						sum[1] = minus(power(first.getOperand(1).negate(), 2));
					} else {
						sum[1] = minus(power(first.getOperand(1), 2));
					}

					sb.add(SolutionStepType.DIFFERENCE_OF_SQUARES);
					return new StepOperation(Operation.PLUS, sum);
				}

				if (first.getOperand(1).equals(second.getOperand(1))
						&& first.getOperand(0).equals(second.getOperand(0).negate())) {
					first.getOperand(1).setColor(tracker.getColorTracker());
					second.getOperand(1).setColor(tracker.incColorTracker());
					first.getOperand(0).setColor(tracker.getColorTracker());
					second.getOperand(0).setColor(tracker.incColorTracker());

					sb.add(SolutionStepType.REORGANIZE_EXPRESSION);

					return new StepOperation(Operation.MULTIPLY,
							add(first.getOperand(1), first.getOperand(0)),
							add(second.getOperand(1), second.getOperand(0)));
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	EXPAND_MARKED_PRODUCTS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			return expandProducts(this, sn, sb, tracker, null);
		}
	},

	WEAK_EXPAND_PRODUCTS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			return expandProducts(this, sn, sb, tracker, false);
		}
	},

	STRONG_EXPAND_PRODUCTS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			return expandProducts(this, sn, sb, tracker, true);
		}
	},

	EXPAND_POWERS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn instanceof StepOperation && !sn.isOperation(Operation.ABS)) {
				StepOperation so = (StepOperation) sn;

				if (so.isOperation(Operation.POWER) && so.getOperand(0).isSum()
						&& so.getOperand(1).getValue() > 0 && so.getOperand(1).isInteger()) {
					StepOperation sum = (StepOperation) so.getOperand(0);

					if (so.getOperand(1).getValue() + sum.noOfOperands() < 6) {
						return expandUsingFormula(so, sb, tracker);
					}

					int exponent = (int) Math.round(so.getOperand(1).getValue());
					StepExpression[] asMultiplication = new StepExpression[exponent];
					for (int i = 0; i < exponent; i++) {
						asMultiplication[i] = sum.deepCopy();
					}
					return new StepOperation(Operation.MULTIPLY, asMultiplication);
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}

		private StepExpression expandUsingFormula(StepOperation so, SolutionBuilder sb,
				RegroupTracker tracker) {
			StepOperation sum = (StepOperation) so.getOperand(0);

			for (StepExpression operand : sum) {
				operand.setColor(tracker.incColorTracker());
			}

			if (isEqual(so.getOperand(1), 2) && sum.noOfOperands() == 2
					&& sum.getOperand(1).isNegative()) {
				sb.add(SolutionStepType.BINOM_SQUARED_DIFF);

				return new StepOperation(Operation.PLUS, power(sum.getOperand(0), 2),
						multiply(-2, multiply(sum.getOperand(0), sum.getOperand(1).negate())),
						power(sum.getOperand(1).negate(), 2));
			} else if (isEqual(so.getOperand(1), 2) && sum.noOfOperands() == 2) {
				sb.add(SolutionStepType.BINOM_SQUARED_SUM);

				return new StepOperation(Operation.PLUS, power(sum.getOperand(0), 2),
						multiply(2, multiply(sum.getOperand(0), sum.getOperand(1))),
						power(sum.getOperand(1), 2));
			} else if (isEqual(so.getOperand(1), 2) && sum.noOfOperands() == 3) {
				sb.add(SolutionStepType.TRINOM_SQUARED);

				return new StepOperation(Operation.PLUS, power(sum.getOperand(0), 2),
						power(sum.getOperand(1), 2), power(sum.getOperand(2), 2),
						multiply(2, multiply(sum.getOperand(0), sum.getOperand(1))),
						multiply(2, multiply(sum.getOperand(1), sum.getOperand(2))),
						multiply(2, multiply(sum.getOperand(0), sum.getOperand(2))));
			} else if (isEqual(so.getOperand(1), 3) && sum.noOfOperands() == 2) {
				sb.add(SolutionStepType.BINOM_CUBED);

				return new StepOperation(Operation.PLUS, power(sum.getOperand(0), 3),
						multiply(3, multiply(power(sum.getOperand(0), 2), sum.getOperand(1))),
						multiply(3, multiply(sum.getOperand(0), power(sum.getOperand(1), 2))),
						power(sum.getOperand(1), 3));
			}

			return null;
		}
	},

	DECIMAL_EXPAND {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] expandStrategy = new SimplificationStepGenerator[] {
					RegroupSteps.DECIMAL_REGROUP,
					ExpandSteps.EXPAND_DIFFERENCE_OF_SQUARES,
					ExpandSteps.EXPAND_POWERS,
					ExpandSteps.STRONG_EXPAND_PRODUCTS
			};

			return StepStrategies.implementGroup(sn, null, expandStrategy, sb, tracker);
		}
	},

	DEFAULT_EXPAND {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] expandStrategy = new SimplificationStepGenerator[] {
					RegroupSteps.DEFAULT_REGROUP,
					ExpandSteps.EXPAND_DIFFERENCE_OF_SQUARES,
					ExpandSteps.EXPAND_POWERS,
					ExpandSteps.STRONG_EXPAND_PRODUCTS
			};

			return StepStrategies.implementGroup(sn, null, expandStrategy, sb, tracker);
		}
	};

	@Override
	public boolean isGroupType() {
		return this == DEFAULT_EXPAND || this == DECIMAL_EXPAND;
	}

	private static StepTransformable expandProducts(ExpandSteps step, StepTransformable sn,
			SolutionBuilder sb, RegroupTracker tracker, Boolean strongExpand) {
		if (sn.isOperation(Operation.MULTIPLY)) {
			StepOperation so = (StepOperation) sn;

			StepExpression first = null;
			StepExpression second = null;
			StepExpression remaining = null;

			for (StepExpression operand : so) {
				if (first == null || second == null && !first.isSum() && !operand.isSum()) {
					first = StepOperation.create(Operation.MULTIPLY, first, operand);
				} else if (second == null || !second.isSum() && !operand.isSum()) {
					second = StepOperation.create(Operation.MULTIPLY, second, operand);
				} else {
					remaining = StepOperation.create(Operation.MULTIPLY, remaining, operand);
				}
			}

			if (first != null && second != null && (first.isSum() || second.isSum())) {
				if (!(strongExpand != null && strongExpand || first.isInteger() && remaining == null
						|| tracker.isMarked(sn, RegroupTracker.MarkType.EXPAND))) {
					return sn;
				}

				if (first.isSum()) {
					for (StepExpression operand : (StepOperation) first) {
						operand.setColor(tracker.incColorTracker());
					}
				} else {
					first.setColor(tracker.incColorTracker());
				}

				if (second.isSum()) {
					for (StepExpression operand : (StepOperation) second) {
						operand.setColor(tracker.incColorTracker());
					}
				} else {
					second.setColor(tracker.incColorTracker());
				}

				StepOperation product = null;

				if (first.isSum() && second.isSum()) {
					StepOperation soFirst = (StepOperation) first;
					StepOperation soSecond = (StepOperation) second;

					StepExpression[] terms = new StepExpression[soFirst.noOfOperands()
							* soSecond.noOfOperands()];
					for (int i = 0; i < soFirst.noOfOperands(); i++) {
						for (int j = 0; j < soSecond.noOfOperands(); j++) {
							terms[i * soSecond.noOfOperands() + j] = multiply(
									soFirst.getOperand(i), soSecond.getOperand(j));
						}
					}

					product = new StepOperation(Operation.PLUS, terms);
					sb.add(SolutionStepType.EXPAND_SUM_TIMES_SUM);
				} else if (first.isSum()) {
					StepOperation soFirst = (StepOperation) first;
					StepExpression[] terms = new StepExpression[soFirst.noOfOperands()];

					for (int i = 0; i < soFirst.noOfOperands(); i++) {
						terms[i] = multiply(soFirst.getOperand(i), second);
					}

					product = new StepOperation(Operation.PLUS, terms);
					sb.add(SolutionStepType.EXPAND_SIMPLE_TIMES_SUM, second);
				} else if (second.isSum()) {
					StepOperation soSecond = (StepOperation) second;
					StepExpression[] terms = new StepExpression[soSecond.noOfOperands()];

					for (int i = 0; i < soSecond.noOfOperands(); i++) {
						terms[i] = multiply(first, soSecond.getOperand(i));
					}

					product = new StepOperation(Operation.PLUS, terms);
					sb.add(SolutionStepType.EXPAND_SIMPLE_TIMES_SUM, first);
				}

				return multiply(product, remaining);
			}
		}

		return sn.iterateThrough(step, sb, tracker);
	}
}
