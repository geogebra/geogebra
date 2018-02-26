package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.*;

import java.util.ArrayList;
import java.util.List;

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
    }

}
