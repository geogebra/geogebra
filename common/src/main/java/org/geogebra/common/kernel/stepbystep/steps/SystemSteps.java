package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SystemSteps {

    public static List<StepSolution> solveBySubstitution(StepEquationSystem ses, SolutionBuilder steps) {
        int n = ses.getEquations().length;
        boolean[] solved = new boolean[n];

        SolutionBuilder tempSteps = new SolutionBuilder();
        StepEquationSystem tempSystem = ses.deepCopy();

        steps.add(SolutionStepType.SOLVE, ses);
        steps.levelDown();

        for (int k = 0; k < n; k++) {
            int eqIndex = -1, minSolutions = -1, minComplexity = -1;
            StepVariable minVariable = null;
            for (int i = 0; i < n; i++) {
                if (solved[i]) {
                    continue;
                }

                Set<StepVariable> variableSet = new HashSet<>();
                tempSystem.getEquation(i).getListOfVariables(variableSet);

                for (StepVariable variable : variableSet) {
                    List<StepSolution> solutions;

                    try {
                        tempSteps.reset();
                        solutions = tempSystem.getEquation(i).solve(variable, tempSteps);
                    } catch (SolveFailedException e) {
                        continue;
                    }

                    int complexity = tempSteps.getSteps().getComplexity();

                    if (minSolutions == -1 || minSolutions > solutions.size() ||
                            (minSolutions == solutions.size() && minComplexity > complexity)) {
                        eqIndex = i;
                        minVariable = variable;
                        minSolutions = solutions.size();
                        minComplexity = complexity;
                    }
                }
            }

            if (eqIndex == -1) {
                throw new SolveFailedException(steps.getSteps());
            }
            solved[eqIndex] = true;

            tempSteps.reset();
            StepSolution[] solutions = tempSystem.getEquation(eqIndex).solve(minVariable, tempSteps)
                    .toArray(new StepSolution[0]);

            if (tempSteps.getSteps().getComplexity() != 2) {
                steps.addAll(tempSteps.getSteps());
            }

            if (solutions.length == 0) {
                steps.add(SolutionStepType.NO_REAL_SOLUTION);
            }

            if (solutions.length == 1) {
                StepExpression solution = (StepExpression) solutions[0].getValue(minVariable);

                StepEquation[] newEquations = new StepEquation[n];
                for (int j = 0; j < n; j++) {
                    if (j == eqIndex) {
                        newEquations[j] = new StepEquation(minVariable, solution);
                    } else {
                        newEquations[j] = tempSystem.getEquation(j).deepCopy();
                    }
                }

                steps.add(SolutionStepType.REPLACE_WITH_AND_REGROUP, minVariable, solution);
                steps.levelDown();
                for (int j = 0; j < n; j++) {
                    if (j == eqIndex) {
                        continue;
                    }

                    steps.add(SolutionStepType.EQUATION, newEquations[j]);
                    steps.levelDown();
                    newEquations[j].replace(minVariable, solution, steps);
                    newEquations[j].regroup(steps, new SolveTracker());
                    steps.levelUp();
                }
                steps.levelUp();

                tempSystem = new StepEquationSystem(newEquations);
                steps.add(SolutionStepType.EQUATION, tempSystem);
            } else {
                for (StepNode solutionNode : solutions) {
                    StepExpression solution = (StepExpression) ((StepSolution) solutionNode).getValue(minVariable);

                    StepEquation[] newEquations = new StepEquation[n];
                    for (int j = 0; j < n; j++) {
                        if (j == eqIndex) {
                            newEquations[j] = new StepEquation(minVariable, solution);
                        } else {
                            newEquations[j] = tempSystem.getEquation(j).deepCopy();
                        }
                    }
                    StepEquationSystem newSes = new StepEquationSystem(newEquations);

                    solveBySubstitution(newSes, steps);
                }

                steps.levelUp();
                return new ArrayList<>();
            }
        }

        steps.levelUp();
        return new ArrayList<>();
    }

}
