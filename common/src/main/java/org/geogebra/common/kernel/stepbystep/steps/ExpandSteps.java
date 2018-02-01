package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.power;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.plugin.Operation;

public enum ExpandSteps implements SimplificationStepGenerator {

	EXPAND_DIFFERENCE_OF_SQUARES {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				if (so.noOfOperands() != 2 ||
						!so.getOperand(0).isOperation(Operation.PLUS) ||
						!so.getOperand(1).isOperation(Operation.PLUS)) {
					return StepStrategies.iterateThrough(this, sn, sb, tracker);
				}

				StepOperation first = (StepOperation) so.getOperand(0);
				StepOperation second = (StepOperation) so.getOperand(1);

				if (first.noOfOperands() != 2 || second.noOfOperands() != 2) {
					return StepStrategies.iterateThrough(this, sn, sb, tracker);
				}

				if (first.getOperand(0).equals(second.getOperand(0))
						&& first.getOperand(1).equals(second.getOperand(1).negate())) {
					first.getOperand(0).setColor(tracker.getColorTracker());
					second.getOperand(0).setColor(tracker.incColorTracker());
					first.getOperand(1).setColor(tracker.getColorTracker());
					second.getOperand(1).setColor(tracker.incColorTracker());

					StepOperation sum = new StepOperation(Operation.PLUS);

					sum.addOperand(power(first.getOperand(0), 2));
					if (first.getOperand(1).isNegative()) {
						sum.addOperand(minus(power(first.getOperand(1).negate(), 2)));
					} else {
						sum.addOperand(minus(power(first.getOperand(1), 2)));
					}

					sb.add(SolutionStepType.DIFFERENCE_OF_SQUARES);
					return sum;
				}

				if (first.getOperand(1).equals(second.getOperand(1))
						&& first.getOperand(0).equals(second.getOperand(0).negate())) {
					first.getOperand(1).setColor(tracker.getColorTracker());
					second.getOperand(1).setColor(tracker.incColorTracker());
					first.getOperand(0).setColor(tracker.getColorTracker());
					second.getOperand(0).setColor(tracker.incColorTracker());

					StepOperation product = new StepOperation(Operation.MULTIPLY);

					product.addOperand(add(first.getOperand(1), first.getOperand(0)));
					product.addOperand(add(second.getOperand(1), second.getOperand(0)));

					sb.add(SolutionStepType.REORGANIZE_EXPRESSION);
					return product;
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, tracker);
		}
	},

	//TODO: 3x(x+1) to 3xx+3x1 in one step
	EXPAND_PRODUCTS {
		@Override 
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				StepExpression firstMultiplicand = null;
				StepOperation secondMultiplicand = null; // must be a sum
				StepExpression remaining = null;

				for (StepExpression operand : so) {
					if (firstMultiplicand == null
							&& (secondMultiplicand != null || !operand.isOperation(Operation.PLUS))) {
						firstMultiplicand = operand;
					} else if (secondMultiplicand == null && operand.isOperation(Operation.PLUS)) {
						secondMultiplicand = (StepOperation) operand;
					} else {
						remaining = multiply(remaining, operand);
					}
				}

				if (firstMultiplicand != null && secondMultiplicand != null) {
					StepOperation product = new StepOperation(Operation.PLUS);

					if (!(firstMultiplicand.isInteger() || tracker.getExpandSettings() || tracker.isMarked(sn, RegroupTracker.MarkType.EXPAND))) {
						return sn;
					}

					if (firstMultiplicand.isOperation(Operation.PLUS)
							&& secondMultiplicand.countOperation(Operation.DIVIDE) == 0) {
						StepOperation firstMultiplicandS = (StepOperation) firstMultiplicand;

						for (StepExpression operand : firstMultiplicandS) {
							operand.setColor(tracker.incColorTracker());
						}
						for (StepExpression operand : secondMultiplicand) {
							operand.setColor(tracker.incColorTracker());
						}

						for (StepExpression operand1 : firstMultiplicandS) {
							for (StepExpression operand2 : secondMultiplicand) {
								product.addOperand(multiply(operand1, operand2));
							}
						}

						sb.add(SolutionStepType.EXPAND_SUM_TIMES_SUM);
					} else {
						firstMultiplicand.setColor(tracker.incColorTracker());
						for (StepExpression operand : secondMultiplicand) {
							operand.setColor(tracker.incColorTracker());
						}

						for (StepExpression operand : secondMultiplicand) {
							product.addOperand(multiply(firstMultiplicand, operand));
						}
						sb.add(SolutionStepType.EXPAND_SIMPLE_TIMES_SUM, firstMultiplicand);
					}

					return multiply(product, remaining);
				}
			}
			
			return StepStrategies.iterateThrough(this, sn, sb, tracker);
		}
	},

	EXPAND_POWERS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn instanceof StepOperation && !sn.isOperation(Operation.ABS)) {
				StepOperation so = (StepOperation) sn;

				if (so.isOperation(Operation.POWER) && so.getOperand(0).isOperation(Operation.PLUS)
						&& so.getOperand(1).getValue() > 0 && so.getOperand(1).isInteger()) {
					StepOperation sum = (StepOperation) so.getOperand(0);

					if (so.getOperand(1).getValue() + sum.noOfOperands() < 6) {
						return expandUsingFormula(so, sb, tracker);
					}

					StepOperation asMultiplication = new StepOperation(Operation.MULTIPLY);
					for (int i = 0; i < Math.round(so.getOperand(1).getValue()); i++) {
						asMultiplication.addOperand(sum.deepCopy());
					}
					return asMultiplication;
				}
			}

			return StepStrategies.iterateThrough(this,sn,sb,tracker);
		}

		private StepExpression expandUsingFormula(StepOperation so, SolutionBuilder sb, RegroupTracker tracker) {
			StepOperation sum = (StepOperation) so.getOperand(0);

			for (StepExpression operand : sum) {
				operand.setColor(tracker.incColorTracker());
			}

			StepOperation newSum = new StepOperation(Operation.PLUS);

			if (isEqual(so.getOperand(1), 2) && sum.noOfOperands() == 2 && sum.getOperand(1).isNegative()) {
				newSum.addOperand(power(sum.getOperand(0), 2));
				newSum.addOperand(multiply(-2, multiply(sum.getOperand(0), sum.getOperand(1).negate())));
				newSum.addOperand(power(sum.getOperand(1).negate(), 2));

				sb.add(SolutionStepType.BINOM_SQUARED_DIFF);
			} else if (isEqual(so.getOperand(1), 2) && sum.noOfOperands() == 2) {
				newSum.addOperand(power(sum.getOperand(0), 2));
				newSum.addOperand(multiply(2, multiply(sum.getOperand(0), sum.getOperand(1))));
				newSum.addOperand(power(sum.getOperand(1), 2));

				sb.add(SolutionStepType.BINOM_SQUARED_SUM);
			} else if (isEqual(so.getOperand(1), 2) && sum.noOfOperands() == 3) {
				newSum.addOperand(power(sum.getOperand(0), 2));
				newSum.addOperand(power(sum.getOperand(1), 2));
				newSum.addOperand(power(sum.getOperand(2), 2));
				newSum.addOperand(multiply(2, multiply(sum.getOperand(0), sum.getOperand(1))));
				newSum.addOperand(multiply(2, multiply(sum.getOperand(1), sum.getOperand(2))));
				newSum.addOperand(multiply(2, multiply(sum.getOperand(0), sum.getOperand(2))));

				sb.add(SolutionStepType.TRINOM_SQUARED);
			} else if (isEqual(so.getOperand(1), 3) && sum.noOfOperands() == 2) {
				newSum.addOperand(power(sum.getOperand(0), 3));
				newSum.addOperand(multiply(3, multiply(power(sum.getOperand(0), 2), sum.getOperand(1))));
				newSum.addOperand(multiply(3, multiply(sum.getOperand(0), power(sum.getOperand(1), 2))));
				newSum.addOperand(power(sum.getOperand(1), 3));

				sb.add(SolutionStepType.BINOM_CUBED);
			}

			return newSum;
		}
	};

	public int type() {
		return 0;
	}
}
