package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.plugin.Operation;

import java.util.ArrayList;
import java.util.List;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOne;

public enum FractionSteps implements SimplificationStepGenerator {

    EXPAND_FRACTIONS {
        @Override
        public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
            StepNode temp = StepStrategies.iterateThrough(this, sn, sb, tracker);
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

                if (isOne(newDenominator) || tracker.isIntegerFractions() && !newDenominator.isInteger()) {
                    return StepStrategies.iterateThrough(this, sn, sb, tracker);
                }

                StepOperation newSum = new StepOperation(Operation.PLUS);

                int tempTracker = tracker.getColorTracker();
                newDenominator.setColor(tempTracker++);

                boolean wasChanged = false;
                for (StepExpression operand : so) {
                    StepExpression currentDenominator = operand.getDenominator();
                    if (!newDenominator.equals(currentDenominator)) {
                        wasChanged = true;

                        StepExpression toExpand = divide(newDenominator, operand.getDenominator());
                        toExpand = toExpand.regroup();
                        toExpand.setColor(tempTracker++);

                        StepExpression oldNumerator;
                        if (operand.isNegative()) {
                            oldNumerator = operand.negate().getNumerator();
                        } else {
                            oldNumerator = operand.getNumerator();
                        }

                        StepExpression numerator = nonTrivialProduct(toExpand, oldNumerator);
                        tracker.addMark(numerator, RegroupTracker.MarkType.EXPAND);

                        StepExpression newFraction = divide(numerator, newDenominator);

                        if (operand.isNegative()) {
                            newSum.addOperand(newFraction.negate());
                        } else {
                            newSum.addOperand(newFraction);
                        }
                    } else {
                        newSum.addOperand(operand);
                    }
                }

                if (wasChanged) {
                    tracker.setColorTracker(tempTracker);
                    sb.add(SolutionStepType.EXPAND_FRACTIONS, newDenominator);

                    return newSum;
                }
            }

            return sn;
        }
    },

    ADD_NUMERATORS {
        @Override
        public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
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
        public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
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

            return StepStrategies.implementGroup(sn, SolutionStepType.ADD_FRACTIONS, fractionAddition,
                    sb, tracker);
        }
    };

    public int type() {
        if (this == ADD_FRACTIONS) {
            return 1;
        }

        return 0;
    }
}
