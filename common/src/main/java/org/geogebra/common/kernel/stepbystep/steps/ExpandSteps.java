package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.closeToAnInteger;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.power;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.plugin.Operation;

public enum ExpandSteps implements SimplificationStepGenerator {

	EXPAND_PRODUCTS {
		@Override 
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn.isOperation(Operation.MULTIPLY) && tracker.getDenominatorSetting()) {
				StepOperation so = (StepOperation) sn;

				StepExpression firstMultiplicand = null;
				StepOperation secondMultiplicand = null; // must be a sum
				StepExpression remaining = null;

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (firstMultiplicand == null
							&& (secondMultiplicand != null || !so.getSubTree(i).isOperation(Operation.PLUS))) {
						firstMultiplicand = so.getSubTree(i);
					} else if (secondMultiplicand == null && so.getSubTree(i).isOperation(Operation.PLUS)) {
						secondMultiplicand = (StepOperation) so.getSubTree(i);
					} else {
						remaining = multiply(remaining, so.getSubTree(i));
					}
				}

				if (firstMultiplicand != null && secondMultiplicand != null) {
					StepOperation product = new StepOperation(Operation.PLUS);

					if (firstMultiplicand.isOperation(Operation.PLUS)
							&& StepHelper.countOperation(secondMultiplicand, Operation.DIVIDE) == 0) {
						StepOperation firstMultiplicandS = (StepOperation) firstMultiplicand;

						if (firstMultiplicandS.noOfOperands() == 2 && secondMultiplicand.noOfOperands() == 2
								&& firstMultiplicandS.getSubTree(0).equals(secondMultiplicand.getSubTree(0))
								&& firstMultiplicandS.getSubTree(1).equals(secondMultiplicand.getSubTree(1).negate())) {
							firstMultiplicandS.getSubTree(0).setColor(tracker.getColorTracker());
							secondMultiplicand.getSubTree(0).setColor(tracker.incColorTracker());
							firstMultiplicandS.getSubTree(1).setColor(tracker.getColorTracker());
							secondMultiplicand.getSubTree(1).setColor(tracker.incColorTracker());

							product.addSubTree(power(firstMultiplicandS.getSubTree(0), 2));
							if (firstMultiplicandS.getSubTree(1).isNegative()) {
								product.addSubTree(minus(power(firstMultiplicandS.getSubTree(1).negate(), 2)));
							} else {
								product.addSubTree(minus(power(firstMultiplicandS.getSubTree(1), 2)));
							}

							sb.add(SolutionStepType.DIFFERENCE_OF_SQUARES);
						} else {
							for (int i = 0; i < firstMultiplicandS.noOfOperands(); i++) {
								firstMultiplicandS.getSubTree(i).setColor(tracker.incColorTracker());
							}
							for (int i = 0; i < secondMultiplicand.noOfOperands(); i++) {
								secondMultiplicand.getSubTree(i).setColor(tracker.incColorTracker());
							}

							for (int i = 0; i < firstMultiplicandS.noOfOperands(); i++) {
								for (int j = 0; j < secondMultiplicand.noOfOperands(); j++) {
									product.addSubTree(multiply(firstMultiplicandS.getSubTree(i),
											secondMultiplicand.getSubTree(j)));
								}
							}

							sb.add(SolutionStepType.EXPAND_SUM_TIMES_SUM);
						}
					} else {
						firstMultiplicand.setColor(tracker.incColorTracker());
						for (int i = 0; i < secondMultiplicand.noOfOperands(); i++) {
							secondMultiplicand.getSubTree(i).setColor(tracker.incColorTracker());
						}

						for (int i = 0; i < secondMultiplicand.noOfOperands(); i++) {
							product.addSubTree(multiply(firstMultiplicand, secondMultiplicand.getSubTree(i)));
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

				if (so.isOperation(Operation.POWER) && so.getSubTree(0).isOperation(Operation.PLUS)
						&& so.getSubTree(1).getValue() > 0 && closeToAnInteger(so.getSubTree(1))) {
					StepOperation sum = (StepOperation) so.getSubTree(0);

					if (so.getSubTree(1).getValue() + sum.noOfOperands() < 6) {
						return expandUsingFormula(so, sb, tracker);
					}

					StepOperation asMultiplication = new StepOperation(Operation.MULTIPLY);
					for (int i = 0; i < Math.round(so.getSubTree(1).getValue()); i++) {
						asMultiplication.addSubTree(sum.deepCopy());
					}
					return asMultiplication;
				}
			}

	return StepStrategies.iterateThrough(this,sn,sb,tracker);
		}

		private StepExpression expandUsingFormula(StepOperation so, SolutionBuilder sb, RegroupTracker tracker) {
			StepOperation sum = (StepOperation) so.getSubTree(0);

			for (int i = 0; i < sum.noOfOperands(); i++) {
				sum.getSubTree(i).setColor(tracker.incColorTracker());
			}

			StepOperation newSum = new StepOperation(Operation.PLUS);

			if (isEqual(so.getSubTree(1), 2) && sum.noOfOperands() == 2 && sum.getSubTree(1).isNegative()) {
				newSum.addSubTree(power(sum.getSubTree(0), 2));
				newSum.addSubTree(multiply(-2, multiply(sum.getSubTree(0), sum.getSubTree(1).negate())));
				newSum.addSubTree(power(sum.getSubTree(1).negate(), 2));

				sb.add(SolutionStepType.BINOM_SQUARED_DIFF);
			} else if (isEqual(so.getSubTree(1), 2) && sum.noOfOperands() == 2) {
				newSum.addSubTree(power(sum.getSubTree(0), 2));
				newSum.addSubTree(multiply(2, multiply(sum.getSubTree(0), sum.getSubTree(1))));
				newSum.addSubTree(power(sum.getSubTree(1), 2));

				sb.add(SolutionStepType.BINOM_SQUARED_SUM);
			} else if (isEqual(so.getSubTree(1), 2) && sum.noOfOperands() == 3) {
				newSum.addSubTree(power(sum.getSubTree(0), 2));
				newSum.addSubTree(power(sum.getSubTree(1), 2));
				newSum.addSubTree(power(sum.getSubTree(2), 2));
				newSum.addSubTree(multiply(2, multiply(sum.getSubTree(0), sum.getSubTree(1))));
				newSum.addSubTree(multiply(2, multiply(sum.getSubTree(1), sum.getSubTree(2))));
				newSum.addSubTree(multiply(2, multiply(sum.getSubTree(0), sum.getSubTree(2))));

				sb.add(SolutionStepType.TRINOM_SQUARED);
			} else if (isEqual(so.getSubTree(1), 3) && sum.noOfOperands() == 2) {
				newSum.addSubTree(power(sum.getSubTree(0), 3));
				newSum.addSubTree(multiply(3, multiply(power(sum.getSubTree(0), 2), sum.getSubTree(1))));
				newSum.addSubTree(multiply(3, multiply(sum.getSubTree(0), power(sum.getSubTree(1), 2))));
				newSum.addSubTree(power(sum.getSubTree(1), 3));

				sb.add(SolutionStepType.BINOM_CUBED);
			}

			return newSum;
		}
	}
}
