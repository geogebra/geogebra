package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.plugin.Operation;

import java.util.ArrayList;
import java.util.List;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;

public enum FractionSteps implements SimplificationStepGenerator {

    EXPAND_FRACTIONS {
        @Override
        public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
            if (sn.isOperation(Operation.PLUS)) {
                StepOperation so = (StepOperation) sn;

                StepExpression newDenominator = null;
                for (StepExpression operand : so) {
                    newDenominator = StepHelper.LCM(newDenominator, operand.getDenominator());
                }

                if (newDenominator == null || tracker.isIntegerFractions() && !newDenominator.isInteger()) {
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

                        StepExpression numerator;
                        if (operand.isNegative()) {
                            numerator = operand.negate().getNumerator();
                        } else {
                            numerator = operand.getNumerator();
                        }

                        StepExpression newFraction = divide(nonTrivialProduct(toExpand, numerator), newDenominator);

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

            return StepStrategies.iterateThrough(this, sn, sb, tracker);
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
            SimplificationStepGenerator[] fractionAddition = new SimplificationStepGenerator[] {
                    EXPAND_FRACTIONS,
                    ADD_NUMERATORS,
                    RegroupSteps.REGROUP_PRODUCTS,
                    RegroupSteps.REGROUP_SUMS,
                    RegroupSteps.SIMPLIFY_FRACTIONS,
                    ExpandSteps.EXPAND_PRODUCTS
            };

            RegroupTracker tempTracker = new RegroupTracker();
            if (!tracker.isIntegerFractions()) {
                tempTracker.unsetIntegerFractions();
            }

            SolutionBuilder tempSteps = new SolutionBuilder();

            StepNode tempTree = EXPAND_FRACTIONS.apply(sn.deepCopy(), tempSteps, tempTracker);
            ADD_NUMERATORS.apply(tempTree, tempSteps, tempTracker);

            if (tempTracker.wasChanged()) {
                tempTracker.resetTracker();
                tempTracker.setInNumerator();

                SolutionBuilder additionSteps = new SolutionBuilder();

                StepNode result = sn;
                String old, current = null;

                do {
                    result = StepStrategies.implementStrategy(result, additionSteps, fractionAddition, tempTracker);

                    old = current;
                    current = result.toString();
                } while (!current.equals(old));

                if (sb != null) {
                    sb.add(SolutionStepType.ADD_FRACTIONS);
                    sb.levelDown();
                    sb.addAll(additionSteps.getSteps());
                    sb.levelUp();
                }

                tracker.incColorTracker();

                return result;
            }

            return sn;
        }
    };

    public int type() {
        if (this == ADD_FRACTIONS) {
            return 1;
        }

        return 0;
    }
}
