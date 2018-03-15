package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.solution.SolutionTable;
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.common.plugin.Operation;

import java.util.*;

import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.*;

public enum InequalitySteps implements SolveStepGenerator {

    TRIVIAL_SOLUTION {
        @Override
        public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps,
                                        SolveTracker tracker) {
            StepInequality si = (StepInequality) se;

            List<StepSolution> solutions = new ArrayList<>();

            if (si.getLHS().equals(variable) && si.getRHS().isConstantIn(variable)) {
                if (si.isLessThan()) {
                    solutions.add(StepSolution.simpleSolution(variable,
                            new StepInterval(StepConstant.NEG_INF, si.getRHS(), false, !si.isStrong()), tracker));
                } else {
                    solutions.add(StepSolution.simpleSolution(variable,
                            new StepInterval(si.getRHS(), StepConstant.POS_INF, !si.isStrong(), false), tracker));
                }

                return solutions;
            }

            if (si.getRHS().equals(variable) && si.getLHS().isConstantIn(variable)) {
                if (si.isLessThan()) {
                    solutions.add(StepSolution.simpleSolution(variable,
                            new StepInterval(si.getLHS(), StepConstant.POS_INF, !si.isStrong(), false), tracker));
                } else {
                    solutions.add(StepSolution.simpleSolution(variable,
                            new StepInterval(StepConstant.NEG_INF, si.getRHS(), false, !si.isStrong()), tracker));
                }

                return solutions;
            }

            if (si.getLHS().equals(si.getRHS()) && !si.isStrong()) {
                solutions.add(StepSolution.simpleSolution(variable,
                        tracker.getRestriction(), tracker));

                return solutions;
            }

            if (si.getLHS().canBeEvaluated() && si.getRHS().canBeEvaluated() &&
                    si.isLessThan() == si.getLHS().getValue() < si.getRHS().getValue()) {
                solutions.add(StepSolution.simpleSolution(variable,
                        tracker.getRestriction(), tracker));

                return solutions;
            }

            return null;
        }
    },

    POSITIVE_AND_ZERO {
        @Override
        public List<StepSolution> apply(StepSolvable ss, StepVariable variable, SolutionBuilder steps,
                                        SolveTracker tracker) {
            StepInequality si = (StepInequality) ss;

            List<StepSolution> solutions = new ArrayList<>();

            if (si.getLHS().isPositive() && isZero(si.getRHS())) {
                if (si.isLessThan()) {
                    if (si.isStrong()) {
                        steps.add(SolutionStepType.POSITIVE_LT_ZERO, variable);
                    } else {
                        StepEquation equality = new StepEquation(si.getLHS(), si.getRHS());
                        steps.add(SolutionStepType.POSITIVE_LE_ZERO, equality);
                        return equality.solve(variable, steps);
                    }
                } else {
                    if (si.isStrong()) {
                        StepEquation equality = new StepEquation(si.getLHS(), si.getRHS());
                        steps.add(SolutionStepType.POSITIVE_GT_ZERO, equality);
                        List<StepSolution> equalPoints = equality.solve(variable, steps);
                        StepSet equalSet = new StepSet();
                        for (StepSolution point : equalPoints) {
                            equalSet.addElement((StepExpression) point.getValue());
                        }
                        solutions.add(StepSolution.simpleSolution(variable,
                                subtract(tracker.getRestriction(), equalSet), tracker));
                    } else {
                        steps.add(SolutionStepType.POSITIVE_GE_ZERO, variable);
                        solutions.add(StepSolution.simpleSolution(variable, tracker.getRestriction(), tracker));
                    }
                }

                return solutions;
            }

            if (isZero(si.getLHS()) && si.getRHS().isPositive()) {
                if (si.isLessThan()) {
                    if (si.isStrong()) {
                        StepEquation equality = new StepEquation(si.getLHS(), si.getRHS());
                        steps.add(SolutionStepType.ZERO_LT_POSITIVE, equality);
                        List<StepSolution> equalPoints = equality.solve(variable, steps);
                        StepSet equalSet = new StepSet();
                        for (StepSolution point : equalPoints) {
                            equalSet.addElement((StepExpression) point.getValue());
                        }
                        solutions.add(StepSolution.simpleSolution(variable,
                                subtract(tracker.getRestriction(), equalSet), tracker));
                    } else {
                        steps.add(SolutionStepType.ZERO_LE_POSITIVE, variable);
                        solutions.add(StepSolution.simpleSolution(variable, tracker.getRestriction(), tracker));
                    }
                } else {
                    if (si.isStrong()) {
                        steps.add(SolutionStepType.ZERO_GT_POSITIVE, variable);
                    } else {
                        StepEquation equality = new StepEquation(si.getLHS(), si.getRHS());
                        steps.add(SolutionStepType.ZERO_GE_POSITIVE, equality);
                        return equality.solve(variable, steps);
                    }
                }

                return solutions;
            }

            return null;
        }
    },

    FACTOR {
        @Override
        public List<StepSolution> apply(StepSolvable ss, StepVariable variable, SolutionBuilder steps,
                                        SolveTracker tracker) {
            if (isZero(ss.getLHS()) || isZero(ss.getRHS())) {
                ss.factor(steps, false);
            }

            return null;
        }
    },

    RATIONAL_INEQUALITY {
        @Override
        public List<StepSolution> apply(StepSolvable ss, StepVariable variable, SolutionBuilder steps,
                                        SolveTracker tracker) {
            if (!isZero(ss.getRHS()) ||
                    !(ss.getLHS().isOperation(Operation.MULTIPLY) || ss.getLHS().isOperation(Operation.DIVIDE))) {
                return null;
            }

            StepInequality si = (StepInequality) ss;

            List<StepExpression> terms = new ArrayList<>();

            StepExpression numerator = si.getLHS().getNumerator();
            StepExpression denominator = si.getLHS().getDenominator();

            if (numerator.isOperation(Operation.MULTIPLY)) {
                for (StepExpression term : (StepOperation) numerator) {
                    terms.add(term);
                }
            }

            if (denominator != null && denominator.isOperation(Operation.MULTIPLY)) {
                for (StepExpression term : (StepOperation) denominator) {
                    terms.add(term);
                }
            } else if (denominator != null) {
                terms.add(denominator);
            }

            List<StepExpression> roots = new ArrayList<>();
            for (StepExpression term : terms) {
                if (term.isConstant()) {
                    continue;
                }

                if (term.degree(variable) != 1) {
                    throw new SolveFailedException(steps.getSteps());
                }

                new StepInequality(term, StepConstant.create(0), false,
                        si.isStrong()).solve(variable, steps);

                StepEquation equation = new StepEquation(term, StepConstant.create(0));
                for (StepSolution solution : equation.solve(variable)) {
                    roots.add((StepExpression) solution.getValue());
                }
            }

            Collections.sort(roots, new Comparator<StepExpression>() {
                @Override
                public int compare(StepExpression s1, StepExpression s2) {
                    return Double.compare(s1.getValue(), s2.getValue());
                }
            });

            roots.add(0, StepConstant.NEG_INF);
            roots.add(StepConstant.POS_INF);

            SolutionTable table = SolutionTable.createSignTable(variable, roots, terms);
            table.addInequalityRow(numerator, denominator);

            steps.add(table);

            return new ArrayList<>();
        }
    },

    SOLVE_QUADRATIC {
        @Override
        public List<StepSolution> apply(StepSolvable ss, StepVariable variable, SolutionBuilder steps,
                                          SolveTracker tracker) {
            if (ss.degree(variable) != 2) {
                return null;
            }

            return null;
        }
    }


}
