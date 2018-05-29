package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.plugin.Operation;

import java.util.ArrayList;
import java.util.List;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOne;
import static org.geogebra.common.kernel.stepbystep.steptree.StepOperation.add;

enum FractionSteps implements SimplificationStepGenerator {

	EXPAND_FRACTIONS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb, RegroupTracker tracker) {
			return expandFractions(sn, sb, tracker, false);
		}
	},

	EXPAND_INTEGER_FRACTIONS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			return expandFractions(sn, sb, tracker, true);
		}
	},

	ADD_NUMERATORS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				StepExpression[] remainder = new StepExpression[so.noOfOperands()];
				StepExpression[] newNumerator = new StepExpression[so.noOfOperands()];
				StepExpression newDenominator = null;

				List<StepExpression> fractions = new ArrayList<>();
				for (int i = 0; i < so.noOfOperands(); i++) {
					StepExpression operand = so.getOperand(i);
					StepExpression currentNumerator = operand.getNumerator();
					StepExpression currentDenominator = operand.getDenominator();

					if (newDenominator == null && currentDenominator != null) {
						newDenominator = currentDenominator;
					}

					if (currentDenominator != null && currentDenominator.equals(newDenominator)) {
						newNumerator[i] = currentNumerator;
						fractions.add(operand);
					} else {
						remainder[i] = operand;
					}
				}

				if (fractions.size() > 1) {
					int denominatorColor = tracker.incColorTracker();
					for (StepExpression fraction : fractions) {
						fraction.getNumerator().setColor(tracker.incColorTracker());
						fraction.getDenominator().setColor(denominatorColor);
					}

					StepExpression result = divide(add(newNumerator), newDenominator);
					sb.add(SolutionStepType.ADD_NUMERATORS);
					return add(add(remainder), result);
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

    ADD_FRACTIONS {
        @Override
        public StepTransformable apply(StepTransformable sn, SolutionBuilder sb, RegroupTracker tracker) {
            SimplificationStepGenerator[] fractionAddition = new SimplificationStepGenerator[]{
                    RegroupSteps.REGROUP_SUMS,
                    RegroupSteps.REGROUP_PRODUCTS,
                    RegroupSteps.DISTRIBUTE_MINUS,
                    ExpandSteps.EXPAND_MARKED_PRODUCTS,
                    RegroupSteps.REWRITE_COMPLEX_FRACTIONS,
                    ADD_NUMERATORS,
                    EXPAND_FRACTIONS,
                    RegroupSteps.SIMPLIFY_FRACTIONS
            };

			if (sn.contains(Operation.DIVIDE)) {
				return StepStrategies.implementGroup(sn, SolutionStepType.ADD_FRACTIONS,
						fractionAddition, sb, tracker);
			}

			return sn;
		}
	},

	ADD_INTEGER_FRACTIONS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] fractionAddition = new SimplificationStepGenerator[]{
					RegroupSteps.REGROUP_SUMS,
					RegroupSteps.REGROUP_PRODUCTS,
					RegroupSteps.DISTRIBUTE_MINUS,
					ExpandSteps.EXPAND_MARKED_PRODUCTS,
					RegroupSteps.REWRITE_COMPLEX_FRACTIONS,
					ADD_NUMERATORS,
					EXPAND_INTEGER_FRACTIONS,
					RegroupSteps.SIMPLIFY_FRACTIONS
			};

			if (sn.contains(Operation.DIVIDE)) {
				return StepStrategies.implementGroup(sn, SolutionStepType.ADD_FRACTIONS,
						fractionAddition, sb, tracker);
			}

			return sn;
		}
	};

	@Override
	public boolean isGroupType() {
		return this == ADD_FRACTIONS
				|| this == ADD_INTEGER_FRACTIONS;
	}

	StepTransformable expandFractions(StepTransformable sn, SolutionBuilder sb,
			RegroupTracker tracker, boolean integer) {
		StepTransformable temp = sn.iterateThrough(this, sb, tracker);
		if (!temp.equals(sn)) {
			return temp;
		}

		if (sn.isOperation(Operation.PLUS)) {
			StepOperation so = (StepOperation) sn;

			StepExpression newDenominator = StepConstant.create(1);
			for (StepExpression operand : so) {
				if (operand.getDenominator() != null) {
					newDenominator = StepHelper.LCM(newDenominator, operand.getDenominator());
				}
			}

			if (isOne(newDenominator) || integer && !newDenominator.isInteger()) {
				return sn.iterateThrough(this, sb, tracker);
			}

			int tempTracker = tracker.getColorTracker();
			newDenominator.setColor(tempTracker++);

			StepExpression[] newSum = new StepExpression[so.noOfOperands()];

			boolean wasChanged = false;
			for (int i = 0; i < so.noOfOperands(); i++) {
				StepExpression currentDenominator = so.getOperand(i).getDenominator();
				if (!newDenominator.equals(currentDenominator)) {
					wasChanged = true;

					StepExpression toExpand = newDenominator.quotient(currentDenominator);
					toExpand.setColor(tempTracker++);

					StepExpression oldNumerator;
					if (so.getOperand(i).isNegative()) {
						oldNumerator = so.getOperand(i).negate().getNumerator();
					} else {
						oldNumerator = so.getOperand(i).getNumerator();
					}

					StepExpression numerator = nonTrivialProduct(toExpand, oldNumerator);
					tracker.addMark(numerator, RegroupTracker.MarkType.EXPAND);

					StepExpression newFraction = divide(numerator, newDenominator);

					if (so.getOperand(i).isNegative()) {
						newSum[i] = newFraction.negate();
					} else {
						newSum[i] = newFraction;
					}
				} else {
					newSum[i] = so.getOperand(i);
				}
			}

			if (wasChanged) {
				tracker.setColorTracker(tempTracker);
				sb.add(SolutionStepType.EXPAND_FRACTIONS, newDenominator);

				return new StepOperation(Operation.PLUS, newSum);
			}
		}

		return sn;
	}
}
