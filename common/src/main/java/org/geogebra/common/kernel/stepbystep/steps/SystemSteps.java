package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionLine;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.*;

import java.util.*;

public class SystemSteps {

    public static List<StepSolution> solveBySubstitution(StepEquationSystem ses, SolutionBuilder steps) {
        int n = ses.getEquations().length;

        SolutionBuilder tempSteps = new SolutionBuilder();
        StepEquationSystem tempSystem = ses.deepCopy();

        int eqIndex = -1, minSolutions = -1, minComplexity = -1;
        StepVariable minVariable = null;
        for (int i = 0; i < n; i++) {
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

        if (n != 1) {
            steps.add(SolutionStepType.SOLVE, ses);
            steps.levelDown();
        }

        List<StepSolution> solutions = tempSystem.getEquation(eqIndex).solve(minVariable, steps);

        if (n == 1) {
            return solutions;
        }

        if (solutions.size() == 0) {
            steps.add(SolutionStepType.NO_REAL_SOLUTION);
        }

        List<StepSolution> finalSolutions = new ArrayList<>();
        for (StepSolution solution : solutions) {
            StepEquation[] newEquations = new StepEquation[n - 1];
            for (int j = 0; j < n; j++) {
                if (j > eqIndex) {
                    newEquations[j - 1] = tempSystem.getEquation(j).deepCopy();
                } else if (j < eqIndex) {
                    newEquations[j] = tempSystem.getEquation(j).deepCopy();
                }
            }

            List<StepSolution> tempSolutions = substituteAndSolve(newEquations, solution, steps);

            StepEquation equation = new StepEquation(minVariable, (StepExpression) solution.getValue());
            for (StepSolution solution1 : tempSolutions) {
                SolutionLine line = new SolutionLine(SolutionStepType.REPLACE_AND_REGROUP, solution1, equation);

                tempSteps.reset();
                StepSolvable tempEquation = replaceAll(equation.deepCopy(), solution1).expand(tempSteps, null);

                solution1.addVariableSolutionPair((StepVariable) tempEquation.getLHS(), tempEquation.getRHS());
                finalSolutions.add(solution1);

                steps.addGroup(line, tempSteps, solution1);
            }
        }

        steps.levelUp();
        steps.add(SolutionStepType.SOLUTIONS, finalSolutions.toArray(new StepNode[0]));
        return finalSolutions;
    }

    private static List<StepSolution> substituteAndSolve(StepEquation[] equations,
                                                         StepSolution solution, SolutionBuilder steps) {
        steps.levelDown();
        for (StepEquation equation : equations) {
            steps.add(SolutionStepType.REPLACE_AND_REGROUP, solution, equation);
            steps.levelDown();
            equation.replace(solution.getVariable(), (StepExpression) solution.getValue(), steps);
            equation.regroup(steps, new SolveTracker());
            steps.levelUp();
        }
        steps.levelUp();

        StepEquationSystem newSystem = new StepEquationSystem(equations);
        return solveBySubstitution(newSystem, steps);
    }

    private static StepEquation replaceAll(StepEquation equation, StepSolution solution) {
        for (Map.Entry<StepVariable, StepNode> pair : solution.getVariableSolutionPairs()) {
            equation.replace(pair.getKey(), (StepExpression) pair.getValue());
        }

        return equation;
    }

}
