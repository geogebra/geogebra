package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.common.plugin.Operation;

import java.util.ArrayList;
import java.util.List;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOne;
import static org.geogebra.common.kernel.stepbystep.steptree.StepOperation.add;

public enum FractionSteps implements SimplificationStepGenerator {

	EXPAND_FRACTIONS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb, RegroupTracker tracker) {
			StepTransformable temp = StepStrategies.iterateThrough(this, sn, sb, tracker);
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

				if (isOne(newDenominator) ||
						tracker.isIntegerFractions() && !newDenominator.isInteger()) {
					return StepStrategies.iterateThrough(this, sn, sb, tracker);
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
	},

	ADD_NUMERATORS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				StepExpression remainder = null;
				StepExpression newNumerator = null;
				StepExpression newDenominator = null;

				List<StepExpression> fractions = new ArrayList<>();
				for (StepExpression operand : so) {
					StepExpression currentNumerator = operand.getNumerator();
					StepExpression currentDenominator = operand.getDenominator();

					if (newDenominator == null && currentDenominator != null) {
						newDenominator = currentDenominator;
					}

					if (currentDenominator != null && currentDenominator.equals(newDenominator)) {
						newNumerator = add(newNumerator, currentNumerator);
						fractions.add(operand);
					} else {
						remainder = add(remainder, operand);
					}
				}

				if (fractions.size() > 1) {
					for (StepExpression fraction : fractions) {
						fraction.setColor(tracker.getColorTracker());
					}

					StepExpression result = divide(newNumerator, newDenominator);
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.ADD_NUMERATORS, tracker.incColorTracker());
					return add(remainder, result);
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, tracker);
		}
	},

    ADD_FRACTIONS {
        @Override
        public StepTransformable apply(StepTransformable sn, SolutionBuilder sb, RegroupTracker tracker) {
            SimplificationStepGenerator[] fractionAddition = new SimplificationStepGenerator[]{
                    RegroupSteps.REGROUP_SUMS,
                    RegroupSteps.REGROUP_PRODUCTS,
                    RegroupSteps.DISTRIBUTE_MINUS,
                    ExpandSteps.EXPAND_PRODUCTS,
                    RegroupSteps.REWRITE_COMPLEX_FRACTIONS,
                    ADD_NUMERATORS,
                    EXPAND_FRACTIONS,
                    RegroupSteps.SIMPLIFY_FRACTIONS
            };

			if (RegroupSteps.contains(sn, Operation.DIVIDE)) {
				return StepStrategies
						.implementGroup(sn, SolutionStepType.ADD_FRACTIONS, fractionAddition, sb,
								tracker);
			}

			return sn;
		}
	};

	@Override
	public boolean isGroupType() {
		return this == ADD_FRACTIONS;
	}
}
