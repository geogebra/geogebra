package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionLine;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.*;

import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.*;

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

    public static List<StepSolution> solveByElimination(StepEquationSystem ses, SolutionBuilder steps) {
        if (ses.getEquations().length != 2) {
            throw new SolveFailedException("incorrect number of equations");
        }

        Set<StepVariable> variables = new HashSet<>();
        ses.getEquation(0).getListOfVariables(variables);
        ses.getEquation(1).getListOfVariables(variables);

        if (variables.size() != 2) {
            throw new SolveFailedException("incorrect number of variables");
        }

        steps.add(SolutionStepType.SOLVE, ses);

        SolutionBuilder tempSteps = new SolutionBuilder();

        StepVariable x = (StepVariable) variables.toArray()[0];
        StepVariable y = (StepVariable) variables.toArray()[1];

        for (int i = 0; i < 2; i++) {
            if (ses.getEquation(i).degree(x) != 1 || ses.getEquation(i).degree(y) != 1) {
                throw new SolveFailedException("nonlinear equation in elimination");
            }

            ses.getEquation(i).reorganize(tempSteps, null, i);
        }

        steps.addGroup(SolutionStepType.REORGANIZE_EXPRESSION, tempSteps, ses);
        tempSteps.reset();

        StepExpression x0 = ses.getEquation(0).getLHS().findCoefficient(x);
        StepExpression y0 = ses.getEquation(0).getLHS().findCoefficient(y);
        StepExpression x1 = ses.getEquation(1).getLHS().findCoefficient(x);
        StepExpression y1 = ses.getEquation(1).getLHS().findCoefficient(y);

        boolean eliminateY = false;

        if (x0.isInteger() && x1.isInteger() && y0.isInteger() && y1.isInteger()) {
            long lcmy = lcm(y0, y1);

            if (isEqual(y0, lcmy) || isEqual(y1, lcmy)) {
                eliminateY = true;
            }
        } else if (y0.isInteger() && y1.isInteger()) {
            eliminateY = true;
        }

        StepVariable eliminate = eliminateY ? y :x;
        StepVariable substitute = eliminateY ? x : y;
        StepExpression eliminateValue = null;
        StepExpression substituteValue = null;

        StepExpression coefficientOne;
        StepExpression coefficientTwo;
        if (eliminateY) {
            StepExpression lcmy = StepHelper.LCM(y0, y1);
            coefficientOne = lcmy.quotient(y0);
            coefficientTwo = lcmy.quotient(y1);
        } else {
            StepExpression lcmx = StepHelper.LCM(x0, x1);
            coefficientOne = lcmx.quotient(x0);
            coefficientTwo = lcmx.quotient(x1);
        }

        if (coefficientOne.isNegative()) {
            coefficientOne = coefficientOne.negate();
        } else {
            coefficientTwo = coefficientTwo.negate();
        }

        ses.getEquation(0).multiply(coefficientOne, steps, null, 0);
        ses.getEquation(1).multiply(coefficientTwo, steps, null, 1);

        StepEquation added = new StepEquation(
                add(ses.getEquation(0).getLHS(), ses.getEquation(1).getLHS()),
                add(ses.getEquation(0).getRHS(), ses.getEquation(1).getRHS())
        );

        tempSteps.add(added);
        added.regroup(tempSteps, null);

        steps.addGroup(new SolutionLine(SolutionStepType.ADD_EQUATIONS, eliminate), tempSteps, added);
        tempSteps.reset();

        List<StepSolution> solutionsAdded = added.solve(substitute, steps);
        if (solutionsAdded.size() == 0) {
            return new ArrayList<>();
        } else if (solutionsAdded.get(0).getValue() instanceof StepLogical) {
            StepSolution solution = ses.getEquation(0).solve(eliminate, steps).get(0);
            solutionsAdded.get(0).addVariableSolutionPair(eliminate, solution.getValue());

            steps.add(SolutionStepType.SOLUTION, solutionsAdded.get(0));
            return solutionsAdded;
        }

        substituteValue = (StepExpression) solutionsAdded.get(0).getValue();

        SolutionBuilder[] options = new SolutionBuilder[2];
        SolutionLine[] headers = new SolutionLine[2];
        for (int i = 0; i < 2; i++) {
            options[i] = new SolutionBuilder();
            headers[i] = new SolutionLine(
                    SolutionStepType.REPLACE_AND_SOLVE, substitute, substituteValue, ses.getEquation(0));

            StepEquation equation = ses.getEquation(i).deepCopy()
                    .replace(substitute, substituteValue, options[i]);
            eliminateValue = (StepExpression)
                    equation.solve(eliminate, options[i]).get(0).getValue();
        }

        StepEquation result = new StepEquation(eliminate, eliminateValue);

        if (options[0].getSteps().getComplexity() < options[1].getSteps().getComplexity()) {
            steps.addGroup(headers[0], options[0], result);
        } else {
            steps.addGroup(headers[1], options[1], result);
        }

        List<StepSolution> solutions = new ArrayList<>();
        solutions.add(new StepSolution());
        solutions.get(0).addVariableSolutionPair(substitute, substituteValue);
        solutions.get(0).addVariableSolutionPair(eliminate, eliminateValue);

        steps.add(SolutionStepType.SOLUTION, solutions.get(0));

        return solutions;
    }

}
