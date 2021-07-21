package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialSum;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOne;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isZero;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.lcm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionLine;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquation;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquationSystem;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepLogical;
import org.geogebra.common.kernel.stepbystep.steptree.StepMatrix;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolution;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolvable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;

public class SystemSteps {

	public static List<StepSolution> solveBySubstitution(StepEquationSystem ses,
			List<StepVariable> variables, SolutionBuilder steps) {
		SolutionBuilder tempSteps = new SolutionBuilder();
		StepEquationSystem tempSystem = ses.deepCopy();

		int eqIndex = -1, minSolutions = -1, minComplexity = -1;
		StepVariable minVariable = null;
		for (int i = 0; i < ses.size(); i++) {
			for (StepVariable variable : variables) {
				if (ses.getEquation(i).LHS.isConstantIn(variable)
						&& ses.getEquation(i).RHS.isConstantIn(variable)) {
					continue;
				}

				try {
					List<StepSolution> solutions =
							tempSystem.getEquation(i).solve(variable, tempSteps);
					int complexity = tempSteps.getSteps().getComplexity();

					if (minSolutions == -1 || minSolutions > solutions.size()
							|| (minSolutions == solutions.size() && minComplexity > complexity)) {
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

		List<StepSolution> solutions =
				tempSystem.getEquation(eqIndex).solve(minVariable, tempSteps);
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
					newEquations[j - 1] = tempSystem.getEquation(j);
				} else if (j < eqIndex) {
					newEquations[j] = tempSystem.getEquation(j);
				}
			}

			List<StepSolution> tempSolutions =
					substituteAndSolve(newEquations, variables, solution, tempSteps);
			steps.addIfNontrivial(tempSteps);

			StepEquation equation =
					new StepEquation(minVariable, (StepExpression) solution.getValue());
			for (StepSolution solution1 : tempSolutions) {
				SolutionLine header =
						new SolutionLine(SolutionStepType.REPLACE_AND_REGROUP, solution1, equation);

				StepSolvable tempEquation = replaceAll(equation, solution1, tempSteps)
						.expand(tempSteps);

				solution1.addVariableSolutionPair((StepVariable) tempEquation.LHS,
						tempEquation.RHS);
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
		for (int i = 0; i < equations.length; i++) {
			if (!contains(equations[i], solution)) {
				continue;
			}

			steps.add(SolutionStepType.REPLACE_AND_REGROUP, solution, equations[i]);
			steps.levelDown();
			equations[i] = equations[i]
					.replace(solution.getVariable(), (StepExpression) solution.getValue(), steps);
			equations[i] = equations[i].regroup(steps);
			steps.levelUp();
		}
		steps.levelUp();

		StepEquationSystem newSystem = new StepEquationSystem(equations);
		return solveBySubstitution(newSystem, variables, steps);
	}

	private static boolean contains(StepEquation se, StepSolution ss) {
		for (Map.Entry<StepVariable, StepNode> pair : ss.getVariableSolutionPairs()) {
			if (se.LHS.isConstantIn(pair.getKey()) || se.RHS.isConstantIn(pair.getKey())) {
				return true;
			}
		}

		return false;
	}

	private static StepEquation replaceAll(StepEquation equation, StepSolution solution,
			SolutionBuilder steps) {
		StepEquation result = equation;
		for (Map.Entry<StepVariable, StepNode> pair : solution.getVariableSolutionPairs()) {
			result = result.replace(pair.getKey(), (StepExpression) pair.getValue(), steps);
		}

		return result;
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

		StepEquation[] equations = new StepEquation[2];
		for (int i = 0; i < 2; i++) {
			equations[i] = (StepEquation) ses.getEquation(i).reorganize(tempSteps, i);

			if (equations[i].degree(x) != 1 || equations[i].degree(y) != 1) {
				throw new SolveFailedException("nonlinear equation in elimination");
			}
		}

		steps.addGroup(SolutionStepType.REORGANIZE_EXPRESSION, tempSteps,
				new StepEquationSystem(equations));

		StepExpression x0 = equations[0].LHS.findCoefficient(x);
		StepExpression y0 = equations[0].LHS.findCoefficient(y);
		StepExpression x1 = equations[1].LHS.findCoefficient(x);
		StepExpression y1 = equations[1].LHS.findCoefficient(y);

		boolean eliminateY = false;

		if (x0.isInteger() && x1.isInteger() && y0.isInteger() && y1.isInteger()) {
			long lcmy = lcm(y0, y1);

			if (isEqual(y0, lcmy) || isEqual(y1, lcmy)) {
				eliminateY = true;
			}
		} else if (y0.isInteger() && y1.isInteger()) {
			eliminateY = true;
		}

		StepExpression coefficientOne;
		StepExpression coefficientTwo;
		if (eliminateY) {
			StepExpression lcmy = StepHelper.lcm(y0, y1);
			coefficientOne = lcmy.quotient(y0);
			coefficientTwo = lcmy.quotient(y1);
		} else {
			StepExpression lcmx = StepHelper.lcm(x0, x1);
			coefficientOne = lcmx.quotient(x0);
			coefficientTwo = lcmx.quotient(x1);
		}

		if (coefficientOne.isNegative()) {
			coefficientOne = coefficientOne.negate();
		} else {
			coefficientTwo = coefficientTwo.negate();
		}

		equations[0] = (StepEquation) equations[0].multiply(coefficientOne, steps, 0)
				.expand(steps);
		equations[1] = (StepEquation) equations[1].multiply(coefficientTwo, steps, 1)
				.expand(steps);

		StepEquation added =
				new StepEquation(add(equations[0].LHS, equations[1].LHS),
						add(equations[0].RHS, equations[1].RHS));

		tempSteps.add(added);
		added = added.regroup(tempSteps);

		StepVariable eliminate = eliminateY ? y : x;
		StepVariable substitute = eliminateY ? x : y;
		StepExpression eliminateValue = null;
		steps.addGroup(SolutionStepType.ADD_EQUATIONS, tempSteps, added, eliminate);

		List<StepSolution> solutionsAdded = added.solve(substitute, steps);
		if (solutionsAdded.size() == 0) {
			return new ArrayList<>();
		} else if (solutionsAdded.get(0).getValue() instanceof StepLogical) {
			StepSolution solution = equations[0].solve(eliminate, steps).get(0);
			solutionsAdded.get(0).addVariableSolutionPair(eliminate, solution.getValue());

			steps.add(SolutionStepType.SOLUTION, solutionsAdded.get(0));
			return solutionsAdded;
		}

		StepExpression substituteValue = (StepExpression) solutionsAdded.get(0).getValue();

		SolutionBuilder[] options = new SolutionBuilder[2];
		SolutionLine[] headers = new SolutionLine[2];
		for (int i = 0; i < 2; i++) {
			options[i] = new SolutionBuilder();
			headers[i] = new SolutionLine(SolutionStepType.REPLACE_AND_SOLVE, substitute,
					substituteValue, equations[0]);

			StepEquation equation =
					equations[i].replace(substitute, substituteValue, options[i]);
			eliminateValue =
					(StepExpression) equation.solve(eliminate, options[i]).get(0).getValue();
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
			StepSolvable eq = ses.getEquation(i).reorganize(tempSteps, i);

			for (int j = 0; j < 3; j++) {
				int degree = eq.degree(variables.get(j));
				if (degree != 0 && degree != 1) {
					throw new SolveFailedException("nonlinear equation in elimination");
				}

				matrix[i][j] = eq.LHS.findCoefficient(variables.get(j));
			}
			matrix[i][3] = eq.RHS;
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
			StepEquation output =
					new StepEquation(new StepVariable("D" + ((i == 0) ? "" : ("_{" + i + "}"))),
							values[i]);
			steps.addGroup(SolutionStepType.CALCULATE_DETERINANT, tempSteps, output,
					determinants[i]);
		}

		if (isZero(values[0])) {
			throw new SolveFailedException(steps.getSteps());
		}

		List<StepSolution> solutions = new ArrayList<>();
		solutions.add(new StepSolution());
		for (int i = 1; i < 4; i++) {
			StepEquation equation =
					new StepEquation(variables.get(i - 1), divide(values[i], values[0]))
							.regroup(tempSteps);
			steps.addGroup(SolutionStepType.CRAMER_VARIABLE, tempSteps, equation,
					variables.get(i - 1), StepConstant.create(i), values[i], values[0]);
			solutions.get(0).addVariableSolutionPair(variables.get(i - 1), equation.RHS);
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
			StepSolvable eq = ses.getEquation(i).reorganize(tempSteps, i);

			for (int j = 0; j < variables.size(); j++) {
				int degree = eq.degree(variables.get(j));
				if (degree != 0 && degree != 1) {
					throw new SolveFailedException("nonlinear equation in elimination");
				}

				matrixData[i][j] = eq.LHS.findCoefficient(variables.get(j));
			}
			matrixData[i][variables.size()] = eq.RHS;
		}

		steps.addGroup(SolutionStepType.REORGANIZE_EXPRESSION, tempSteps, ses);

		StepMatrix matrix = new StepMatrix(matrixData);
		matrix.setAugmented();

		steps.addSubstep(ses, matrix, SolutionStepType.WRITE_IN_MATRIX_FORM);

		boolean[] solved = new boolean[variables.size()];
		for (int k = 0; k < variables.size(); k++) {
			for (int i = 0; i < ses.getEquations().length; i++) {
				StepExpression GCD = matrix.get(i, 0);
				for (int j = 1; j <= variables.size(); j++) {
					GCD = StepHelper.gcd(GCD, matrix.get(i, j));
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
