package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.closeToAnInteger;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.power;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

public enum ExpandSteps implements SimplificationStepGenerator {

	EXPAND_PRODUCTS {
		@Override 
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				StepExpression firstMultiplicand = null;
				StepOperation secondMultiplicand = null; // must be a sum
				StepExpression remaining = null;

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (firstMultiplicand == null
							&& (secondMultiplicand != null || !so.getOperand(i).isOperation(Operation.PLUS))) {
						firstMultiplicand = so.getOperand(i);
					} else if (secondMultiplicand == null && so.getOperand(i).isOperation(Operation.PLUS)) {
						secondMultiplicand = (StepOperation) so.getOperand(i);
					} else {
						remaining = multiply(remaining, so.getOperand(i));
					}
				}

				if (firstMultiplicand != null && secondMultiplicand != null) {
					StepOperation product = new StepOperation(Operation.PLUS);

					if (!(firstMultiplicand.isInteger() || tracker.getExpandSettings())) {
						return sn;
					}

					Log.error(firstMultiplicand + "");
					Log.error(tracker.getExpandSettings() + "");

					if (firstMultiplicand.isOperation(Operation.PLUS)
							&& secondMultiplicand.countOperation(Operation.DIVIDE) == 0) {
						StepOperation firstMultiplicandS = (StepOperation) firstMultiplicand;

						if (firstMultiplicandS.noOfOperands() == 2 && secondMultiplicand.noOfOperands() == 2
								&& firstMultiplicandS.getOperand(0).equals(secondMultiplicand.getOperand(0))
								&& firstMultiplicandS.getOperand(1).equals(secondMultiplicand.getOperand(1).negate())) {
							firstMultiplicandS.getOperand(0).setColor(tracker.getColorTracker());
							secondMultiplicand.getOperand(0).setColor(tracker.incColorTracker());
							firstMultiplicandS.getOperand(1).setColor(tracker.getColorTracker());
							secondMultiplicand.getOperand(1).setColor(tracker.incColorTracker());

							product.addOperand(power(firstMultiplicandS.getOperand(0), 2));
							if (firstMultiplicandS.getOperand(1).isNegative()) {
								product.addOperand(minus(power(firstMultiplicandS.getOperand(1).negate(), 2)));
							} else {
								product.addOperand(minus(power(firstMultiplicandS.getOperand(1), 2)));
							}

							sb.add(SolutionStepType.DIFFERENCE_OF_SQUARES);
						} else {
							for (int i = 0; i < firstMultiplicandS.noOfOperands(); i++) {
								firstMultiplicandS.getOperand(i).setColor(tracker.incColorTracker());
							}
							for (int i = 0; i < secondMultiplicand.noOfOperands(); i++) {
								secondMultiplicand.getOperand(i).setColor(tracker.incColorTracker());
							}

							for (int i = 0; i < firstMultiplicandS.noOfOperands(); i++) {
								for (int j = 0; j < secondMultiplicand.noOfOperands(); j++) {
									product.addOperand(multiply(firstMultiplicandS.getOperand(i),
											secondMultiplicand.getOperand(j)));
								}
							}

							sb.add(SolutionStepType.EXPAND_SUM_TIMES_SUM);
						}
					} else {
						firstMultiplicand.setColor(tracker.incColorTracker());
						for (int i = 0; i < secondMultiplicand.noOfOperands(); i++) {
							secondMultiplicand.getOperand(i).setColor(tracker.incColorTracker());
						}

						for (int i = 0; i < secondMultiplicand.noOfOperands(); i++) {
							product.addOperand(multiply(firstMultiplicand, secondMultiplicand.getOperand(i)));
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
						&& so.getOperand(1).getValue() > 0 && closeToAnInteger(so.getOperand(1))) {
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

			for (int i = 0; i < sum.noOfOperands(); i++) {
				sum.getOperand(i).setColor(tracker.incColorTracker());
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
