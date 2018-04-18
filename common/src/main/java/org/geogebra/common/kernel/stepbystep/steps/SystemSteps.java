package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionLine;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.*;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.*;

import java.util.*;

public class SystemSteps {

    public static List<StepSolution> solveBySubstitution(StepEquationSystem ses,
                                             List<StepVariable> variables, SolutionBuilder steps) {
        SolutionBuilder tempSteps = new SolutionBuilder();
        StepEquationSystem tempSystem = ses.deepCopy();

        int eqIndex = -1, minSolutions = -1, minComplexity = -1;
        StepVariable minVariable = null;
        for (int i = 0; i < ses.size(); i++) {
            for (StepVariable variable : variables) {
                if (ses.getEquation(i).getLHS().isConstantIn(variable)
                        && ses.getEquation(i).getRHS().isConstantIn(variable)) {
                    continue;
                }

                try {
                    List<StepSolution> solutions = tempSystem.getEquation(i)
                            .solve(variable, tempSteps);
                    int complexity = tempSteps.getSteps().getComplexity();

                    if (minSolutions == -1 || minSolutions > solutions.size() ||
                            (minSolutions == solutions.size() && minComplexity > complexity)) {
                        eqIndex = i;
                        minVariable = variable;
                        minSolutions = solutions.size();
                        minComplexity = complexity;
                    }
                } catch (SolveFailedException e) {
                    // nothing
                } finally {
                    tempSteps.reset();
                }
            }
        }

        if (eqIndex == -1) {
            throw new SolveFailedException(steps.getSteps());
        }

        if (ses.size() != 1) {
            steps.add(SolutionStepType.SOLVE, ses);
            steps.levelDown();
        }

        List<StepSolution> solutions = tempSystem.getEquation(eqIndex)
                .solve(minVariable, tempSteps);
        steps.addIfNontrivial(tempSteps);

        if (ses.size() == 1) {
            return solutions;
        }

        if (solutions.size() == 0) {
            steps.add(SolutionStepType.NO_REAL_SOLUTION);
        }

        variables.remove(minVariable);

        List<StepSolution> finalSolutions = new ArrayList<>();
        for (StepSolution solution : solutions) {
            StepEquation[] newEquations = new StepEquation[ses.size() - 1];
            for (int j = 0; j < ses.size(); j++) {
                if (j > eqIndex) {
                    newEquations[j - 1] = tempSystem.getEquation(j).deepCopy();
                } else if (j < eqIndex) {
                    newEquations[j] = tempSystem.getEquation(j).deepCopy();
                }
            }

            List<StepSolution> tempSolutions = substituteAndSolve(newEquations, variables,
                    solution, tempSteps);
            steps.addIfNontrivial(tempSteps);

            StepEquation equation = new StepEquation(minVariable,
                    (StepExpression) solution.getValue());
            for (StepSolution solution1 : tempSolutions) {
                SolutionLine header = new SolutionLine(SolutionStepType.REPLACE_AND_REGROUP,
                        solution1, equation);

                StepSolvable tempEquation = replaceAll(equation.deepCopy(), solution1, tempSteps)
                        .expand(tempSteps, null);

                solution1.addVariableSolutionPair((StepVariable) tempEquation.getLHS(),
                        tempEquation.getRHS());
                finalSolutions.add(solution1);

                steps.addGroup(header, tempSteps, solution1);
            }
        }

        steps.levelUp();
        steps.add(SolutionStepType.SOLUTIONS, finalSolutions.toArray(new StepNode[0]));
        return finalSolutions;
    }

    private static List<StepSolution> substituteAndSolve(StepEquation[] equations,
                     List<StepVariable> variables, StepSolution solution, SolutionBuilder steps) {
        steps.levelDown();
        for (StepEquation equation : equations) {
            if (!contains(equation, solution)) {
                continue;
            }

            steps.add(SolutionStepType.REPLACE_AND_REGROUP, solution, equation);
            steps.levelDown();
            equation.replace(solution.getVariable(), (StepExpression) solution.getValue(), steps);
            equation.regroup(steps, new SolveTracker());
            steps.levelUp();
        }
        steps.levelUp();

        StepEquationSystem newSystem = new StepEquationSystem(equations);
        return solveBySubstitution(newSystem, variables, steps);
    }

    private static boolean contains(StepEquation se, StepSolution ss) {
        for (Map.Entry<StepVariable, StepNode> pair : ss.getVariableSolutionPairs()) {
            if (se.getLHS().isConstantIn(pair.getKey())
                    || se.getRHS().isConstantIn(pair.getKey())) {
                return true;
            }
        }

        return false;
    }

    private static StepEquation replaceAll(StepEquation equation, StepSolution solution,
                                           SolutionBuilder steps) {
        for (Map.Entry<StepVariable, StepNode> pair : solution.getVariableSolutionPairs()) {
            equation.replace(pair.getKey(), (StepExpression) pair.getValue(), steps);
        }

        return equation;
    }

    public static List<StepSolution> solveByElimination(StepEquationSystem ses,
                                            List<StepVariable> variables, SolutionBuilder steps) {
        if (ses.getEquations().length != 2) {
            throw new SolveFailedException("incorrect number of equations");
        }

        if (variables.size() != 2) {
            throw new SolveFailedException("incorrect number of variables");
        }

        steps.add(SolutionStepType.SOLVE, ses);
        steps.levelDown();

        SolutionBuilder tempSteps = new SolutionBuilder();

        StepVariable x = variables.get(0);
        StepVariable y = variables.get(1);

        for (int i = 0; i < 2; i++) {
            ses.getEquation(i).reorganize(tempSteps, null, i);

            if (ses.getEquation(i).degree(x) != 1 || ses.getEquation(i).degree(y) != 1) {
                throw new SolveFailedException("nonlinear equation in elimination");
            }
        }

        steps.addGroup(SolutionStepType.REORGANIZE_EXPRESSION, tempSteps, ses);

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

        StepVariable eliminate = eliminateY ? y : x;
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

        steps.addGroup(SolutionStepType.ADD_EQUATIONS, tempSteps, added, eliminate);

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
            headers[i] = new SolutionLine(SolutionStepType.REPLACE_AND_SOLVE, substitute,
                    substituteValue, ses.getEquation(0));

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

        steps.levelUp();
        steps.add(SolutionStepType.SOLUTION, solutions.get(0));

        return solutions;
    }

    public static List<StepSolution> cramersRule(StepEquationSystem ses,
                                             List<StepVariable> variables, SolutionBuilder steps) {
        if (ses.getEquations().length != 3) {
            throw new SolveFailedException("incorrect number of equations");
        }

        if (variables.size() != 3) {
            throw new SolveFailedException("incorrect number of variables");
        }

        steps.add(SolutionStepType.SOLVE, ses);
        steps.levelDown();

        SolutionBuilder tempSteps = new SolutionBuilder();

        StepExpression[][] matrix = new StepExpression[3][4];

        for (int i = 0; i < 3; i++) {
            ses.getEquation(i).reorganize(tempSteps, null, i);

            for (int j = 0; j < 3; j++) {
                int degree = ses.getEquation(i).degree(variables.get(j));
                if (degree != 0 && degree != 1) {
                    throw new SolveFailedException("nonlinear equation in elimination");
                }

                matrix[i][j] = ses.getEquation(i).getLHS().findCoefficient(variables.get(j));
            }
            matrix[i][3] = ses.getEquation(i).getRHS();
        }

        steps.addGroup(SolutionStepType.REORGANIZE_EXPRESSION, tempSteps, ses);

        StepMatrix.Determinant[] determinants = new StepMatrix.Determinant[4];

        for (int i = 0; i < 4; i++) {
            StepExpression[][] tempMatrix = new StepExpression[3][3];
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    tempMatrix[j][k] = matrix[j][k == i ? 3 : k];
                }
            }
            determinants[(i + 3) % 4] = new StepMatrix(tempMatrix).getDeterminant();
        }

        steps.add(SolutionStepType.USE_CRAMERS_RULE);
        steps.add(SolutionStepType.DETERMINANTS, determinants);

        StepExpression[] values = new StepExpression[4];
        for (int i = 0; i < 4; i++) {
            values[i] = determinants[i].calculateDeterminant(tempSteps);
            StepEquation output = new StepEquation(
                    new StepVariable("D" + ((i == 0) ? "" : ("_{" + i + "}"))),
                    values[i]);
            steps.addGroup(SolutionStepType.CALCULATE_DETERINANT, tempSteps,
                    output, determinants[i]);
        }

        if (isZero(values[0])) {
            throw new SolveFailedException(steps.getSteps());
        }

        List<StepSolution> solutions = new ArrayList<>();
        solutions.add(new StepSolution());
        for (int i = 1; i < 4; i++) {
            StepEquation equation = new StepEquation(variables.get(i - 1),
                    divide(values[i], values[0])).regroup(tempSteps, null);
            steps.addGroup(SolutionStepType.CRAMER_VARIABLE, tempSteps, equation,
                    variables.get(i - 1), StepConstant.create(i), values[i], values[0]);
            solutions.get(0).addVariableSolutionPair(variables.get(i - 1), equation.getRHS());
        }

        steps.levelUp();
        steps.add(SolutionStepType.SOLUTION, solutions.get(0));

        return solutions;
    }

    public static List<StepSolution> gaussJordanElimination(StepEquationSystem ses,
                                            List<StepVariable> variables, SolutionBuilder steps) {
        steps.add(SolutionStepType.SOLVE, ses);
        steps.levelDown();

        SolutionBuilder tempSteps = new SolutionBuilder();

        StepExpression[][] matrixData = new StepExpression[ses.size()][variables.size() + 1];

        for (int i = 0; i < ses.size(); i++) {
            ses.getEquation(i).reorganize(tempSteps, null, i);

            for (int j = 0; j < variables.size(); j++) {
                int degree = ses.getEquation(i).degree(variables.get(j));
                if (degree != 0 && degree != 1) {
                    throw new SolveFailedException("nonlinear equation in elimination");
                }

                matrixData[i][j] = ses.getEquation(i).getLHS().findCoefficient(variables.get(j));
            }
            matrixData[i][variables.size()] = ses.getEquation(i).getRHS();
        }

        steps.addGroup(SolutionStepType.REORGANIZE_EXPRESSION, tempSteps, ses);

        StepMatrix matrix = new StepMatrix(matrixData);
        matrix.setAugmented();

        steps.addSubstep(ses, matrix, SolutionStepType.WRITE_IN_MATRIX_FORM);

        boolean[] solved = new boolean[variables.size()];
        for (int k = 0; k < variables.size(); k++) {
            for (int i = 0; i < ses.getEquations().length; i++) {
                StepExpression GCD = matrix.get(i, 0);
                for (int j = 1; j <= variables.size(); j++ ) {
                    GCD = StepHelper.GCD(GCD, matrix.get(i, j));
                }
                if (!isOne(GCD)) {
                    matrix = matrix.divideRow(i, GCD, steps);
                }
            }

            int pivoti = -1;
            int pivotj = -1;
            for (int i = 0; i < ses.getEquations().length; i++) {
                for (int j = 0; j < variables.size(); j++) {
                    if (solved[i] || isZero(matrix.get(i, j))) {
                        continue;
                    }

                    if (pivoti == -1) {
                        pivoti = i;
                        pivotj = j;
                    }
                }
            }

            if (pivoti == -1) {
                break;
            }

            solved[pivoti] = true;
            StepExpression pivot = matrix.get(pivoti, pivotj);
            for (int i = 0; i < ses.getEquations().length; i++) {
                if (i == pivoti || isZero(matrix.get(i, pivotj))) {
                    continue;
                }

                StepExpression coefficient = divide(matrix.get(i, pivotj), pivot).regroup();
                matrix = matrix.addRow(pivoti, i, coefficient.negate(), steps);
            }
        }

        StepEquation[] newEquations = new StepEquation[matrix.getHeight()];
        for (int i = 0; i < matrix.getHeight(); i++) {
            StepExpression LHS = StepConstant.create(0);
            for (int j = 0; j < variables.size(); j++) {
                if (!isZero(matrix.get(i, j))) {
                    LHS = nonTrivialSum(LHS, nonTrivialProduct(matrix.get(i, j), variables.get(j)));
                }
            }
            newEquations[i] = new StepEquation(LHS, matrix.get(i, matrix.getWidth() - 1));
        }

        StepEquationSystem newSystem = new StepEquationSystem(newEquations);

        steps.addSubstep(matrix, newSystem, SolutionStepType.WRITE_IN_SYSTEM_FORM);

        List<StepSolution> solutions = solveBySubstitution(newSystem, variables, tempSteps);

        if (tempSteps.getSteps().getComplexity() > 4) {
            steps.add(tempSteps.getSteps());
        }

        steps.levelUp();
        steps.add(SolutionStepType.SOLUTIONS, solutions.toArray(new StepNode[0]));

        return solutions;
    }
}
