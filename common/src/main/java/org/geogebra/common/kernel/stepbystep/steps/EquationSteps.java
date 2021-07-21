package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.applyOp;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.cos;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOne;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isZero;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.lcm;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.plusminus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.power;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.root;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.sin;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.subtract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionLine;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.solution.SolutionTable;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquation;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepInequality;
import org.geogebra.common.kernel.stepbystep.steptree.StepInterval;
import org.geogebra.common.kernel.stepbystep.steptree.StepLogical;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepSet;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolution;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolvable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.plugin.Operation;

enum EquationSteps implements SolveStepGenerator<StepEquation> {

	TRIVIAL_EQUATIONS {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (se.LHS.equals(se.RHS)) {
				StepSolution solution
						= StepSolution.simpleSolution(variable, tracker.getRestriction(), tracker);
				steps.addSubstep(se, solution, SolutionStepType.STATEMENT_IS_TRUE);
				return new Result(solution);
			}

			if (se.LHS.equals(variable) && se.RHS.isConstantIn(variable)) {
				return new Result(StepSolution.simpleSolution(variable, se.RHS, tracker));
			}

			if (se.RHS.equals(variable) && se.LHS.isConstantIn(variable)) {
				return new Result(StepSolution.simpleSolution(variable, se.LHS, tracker));
			}

			if (se.LHS.isConstantIn(variable) && se.RHS.isConstantIn(variable)) {
				steps.add(SolutionStepType.STATEMENT_IS_FALSE);
				return new Result();
			}

			return null;
		}
	},

	NEGATE_BOTH_SIDES {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if ((se.LHS.isNegative() && se.RHS.isNegative())
					|| (isZero(se.LHS) && se.RHS.isNegative())
					|| (se.LHS.isNegative() && isZero(se.RHS))) {
				StepExpression newLHS = isZero(se.LHS) ? se.LHS : se.LHS.negate();
				StepExpression newRHS = isZero(se.RHS) ? se.RHS : se.RHS.negate();

				StepSolvable negated = se.cloneWith(newLHS, newRHS);

				steps.addSubstep(se, negated, SolutionStepType.NEGATE_BOTH_SIDES);

				return new Result(negated);
			}

			return null;
		}
	},

	SOLVE_QUADRATIC {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (se.degree(variable) != 2) {
				return null;
			}

			StepSolvable result = se;

			if (result.LHS.findCoefficient(power(variable, 2)).isNegative()) {
				result = result.multiply(StepConstant.create(-1), steps);
			}

			StepExpression a = result.LHS.findCoefficient(power(variable, 2));
			StepExpression b = result.LHS.findCoefficient(variable);
			StepExpression c = result.LHS.findConstantIn(variable);

			a.setColor(1);
			b.setColor(2);
			c.setColor(3);

			StepExpression discriminant = subtract(power(b, 2), multiply(4, multiply(a, c)));

			SolutionBuilder tempSteps = new SolutionBuilder();

			tempSteps.add(SolutionStepType.QUADRATIC_FORMULA, variable);
			result = new StepEquation(variable,
					divide(add(minus(b), plusminus(root(discriminant, 2))), multiply(2, a)));

			tempSteps.add(result);

			result = result.regroup(tempSteps);

			steps.addGroup(new SolutionLine(SolutionStepType.USE_QUADRATIC_FORMULA, a, b, c),
					tempSteps, result);

			discriminant = discriminant.regroup();

			if (discriminant.canBeEvaluated() && discriminant.getValue() < 0) {
				return new Result();
			}

			a.cleanColors();
			b.cleanColors();
			c.cleanColors();

			List<StepSolution> solutions = new ArrayList<>();
			if (!a.isConstant()) {
				solutions
						.add(StepSolution.simpleSolution(variable, divide(c.negate(), b), tracker));
				solutions.get(0).addCondition(new StepEquation(a, StepConstant.create(0)));
				tracker.addCondition(new StepEquation(a, StepConstant.create(0)).setInequation());
			}

			if (discriminant.sign() <= 0
					&& !(discriminant.canBeEvaluated() && discriminant.getValue() > 0)) {
				tracker.addCondition(
						new StepInequality(discriminant, StepConstant.create(0), false, false));
			}

			StepExpression solution1 = divide(add(minus(b), root(discriminant, 2)), multiply(2, a));
			StepExpression solution2 =
					divide(subtract(minus(b), root(discriminant, 2)), multiply(2, a));

			solutions.add(StepSolution.simpleSolution(variable, solution1.adaptiveRegroup(),
					tracker));
			solutions.add(StepSolution.simpleSolution(variable, solution2.adaptiveRegroup(),
					tracker));

			return new Result(solutions);
		}
	},

	SOLVE_QUADRATIC_IN_EXPRESSION {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepExpression expression = StepHelper.quadraticInExpression(se, variable);

			if (expression == null) {
				return null;
			}

			StepVariable newVariable = new StepVariable("t");

			StepEquation replaced = se.replace(expression, newVariable);

			List<StepSolution> tempSolutions = replaced.solve(newVariable, steps);

			List<StepSolution> allSolutions = new ArrayList<>();
			for (StepSolution solution : tempSolutions) {
				StepEquation newEq = new StepEquation(expression,
						(StepExpression) solution.getValue(newVariable));
				allSolutions.addAll(newEq.solve(variable, steps));
			}
			return new Result(allSolutions);
		}
	},

	PRODUCT_IS_ZERO {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepOperation product = null;

			if (se.RHS.isOperation(Operation.MULTIPLY) && isZero(se.LHS)) {
				product = (StepOperation) se.RHS;
			} else if (se.LHS.isOperation(Operation.MULTIPLY) && isZero(se.RHS)) {
				product = (StepOperation) se.LHS;
			}

			if (product != null) {
				steps.add(SolutionStepType.PRODUCT_IS_ZERO);

				List<StepSolution> solutions = new ArrayList<>();
				for (StepExpression operand : product) {
					if (!operand.isConstant()) {
						StepEquation newEq = new StepEquation(operand, StepConstant.create(0));
						solutions.addAll(newEq.solve(variable, steps, new SolveTracker()));
					}
				}
				return new Result(solutions);
			}

			return null;
		}
	},

	FRACTION_IS_ZERO {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepEquation result = null;

			if (se.RHS.isFraction() && isZero(se.LHS)) {
				result = se.cloneWith(se.LHS, se.RHS.getNumerator());
			} else if (se.LHS.isFraction() && isZero(se.RHS)) {
				result = se.cloneWith(se.LHS.getNumerator(), se.RHS);
			}

			if (result != null) {
				steps.addSubstep(se, result, SolutionStepType.FRACTION_IS_ZERO);
				return new Result(result);
			}

			return null;
		}
	},

	MULTIPLY_THROUGH {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepExpression commonDenominator = StepConstant.create(1);
			if (!se.LHS.isConstant() && se.LHS.getDenominator() != null) {
				commonDenominator = se.LHS.getDenominator();
			}

			if (!se.RHS.isConstant() && se.RHS.getDenominator() != null) {
				commonDenominator = StepHelper.lcm(se.RHS.getDenominator(), commonDenominator);
			}

			return new Result(se.multiply(commonDenominator, steps));
		}
	},

	RECIPROCATE_EQUATION {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (StepHelper.shouldReciprocate(se.LHS) && StepHelper.shouldReciprocate(se.RHS)) {
				return new Result(se.reciprocate(steps));
			}

			return null;
		}
	},

	SIMPLIFY_TRIGONOMETRIC {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepExpression bothSides = subtract(se.LHS, se.RHS).regroup();
			StepOperation trigoVar = StepHelper.findTrigonometricExpression(bothSides, variable);

			if (trigoVar == null) {
				return null;
			}

			StepExpression argument = trigoVar.getOperand(0);
			StepExpression sineSquared = power(sin(argument), 2);
			StepExpression cosineSquared = power(cos(argument), 2);

			StepExpression coeffSineSquared = bothSides.findCoefficient(sineSquared);
			StepExpression coeffCosineSquared = bothSides.findCoefficient(cosineSquared);

			StepSolvable result = se;
			if (coeffSineSquared != null && coeffSineSquared.equals(coeffCosineSquared)) {
				result = result.addOrSubtract(result.RHS.findExpression(sineSquared), steps);

				StepExpression newLHS = subtract(result.LHS,
						add(result.LHS.findExpression(sineSquared),
								result.LHS.findExpression(cosineSquared))).regroup();
				StepExpression newRHS = subtract(result.RHS,
						multiply(coeffSineSquared, add(sineSquared, cosineSquared)));

				result = result.cloneWith(newLHS, newRHS);

				result = result.replace(add(sineSquared, cosineSquared),
						StepConstant.create(1), steps);
				result = result.regroup(steps);
			}

			bothSides = subtract(result.LHS, result.RHS).regroup();

			StepExpression sine = sin(argument);
			StepExpression cosine = cos(argument);

			StepExpression coeffSine = bothSides.findCoefficient(sine);
			StepExpression coeffCosine = bothSides.findCoefficient(cosine);
			coeffSineSquared = bothSides.findCoefficient(sineSquared);
			coeffCosineSquared = bothSides.findCoefficient(cosineSquared);

			if (coeffSine != null && coeffCosine == null && coeffSineSquared != null
					&& coeffCosineSquared != null) {
				result = result.replace(cosineSquared, subtract(1, sineSquared), steps);
				result = result.regroup(steps);
			}

			if (coeffSine == null && coeffCosine != null && coeffSineSquared != null
					&& coeffCosineSquared != null) {
				result = result.replace(sineSquared, subtract(1, cosineSquared), steps);
				result = result.regroup(steps);
			}

			if (coeffSine != null && coeffCosine != null && coeffSineSquared == null
					&& coeffCosineSquared == null) {
				if (!isZero(result.LHS.findExpression(sine))) {
					result = result.addOrSubtract(result.LHS.findExpression(cosine), steps);
					result = result.addOrSubtract(result.RHS.findExpression(sine), steps);
				} else {
					result = result.addOrSubtract(result.RHS.findExpression(cosine), steps);
				}

				result = result.power(2, steps, tracker);
			}

			return new Result(result);
		}
	},

	SOLVE_SIMPLE_TRIGONOMETRIC {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (!se.LHS.isTrigonometric() || !se.RHS.isConstantIn(variable)) {
				return null;
			}

			StepOperation trigoVar = (StepOperation) se.LHS;

			if (trigoVar.getOperation() != Operation.TAN) {
				if (!se.RHS.canBeEvaluated()) {
					tracker.addCondition(
							new StepInequality(StepConstant.create(-1), se.RHS, true, false));
					tracker.addCondition(
							new StepInequality(se.RHS, StepConstant.create(1), true, false));
				} else if (se.RHS.getValue() < -1 || se.RHS.getValue() > 1) {
					steps.add(SolutionStepType.NO_SOLUTION_SIN_COS, trigoVar, variable);
					return new Result();
				}
			}

			Operation op = StepExpression.getInverse(trigoVar.getOperation());
			StepExpression newLHS = trigoVar.getOperand(0);

			if (trigoVar.isOperation(Operation.TAN)) {
				StepExpression newRHS = add(applyOp(op, se.RHS),
						multiply(tracker.getNextArbInt(), StepConstant.PI));

				StepSolvable newEq = se.cloneWith(newLHS, newRHS);
				return new Result(newEq.solve(variable, steps, tracker));
			}

			StepExpression firstRHS = add(applyOp(op, se.RHS),
					multiply(multiply(2, tracker.getNextArbInt()), StepConstant.PI));

			StepEquation firstBranch = new StepEquation(newLHS, firstRHS);
			List<StepSolution> solutions = firstBranch.solve(variable, steps, tracker);

			if (!isEqual(se.RHS, 1) && !isEqual(se.RHS, -1)) {
				StepExpression secondRHS = add(applyOp(op, se.RHS),
						multiply(multiply(2, tracker.getNextArbInt()), StepConstant.PI));
				StepEquation secondBranch;
				if (trigoVar.isOperation(Operation.SIN)) {
					secondBranch = new StepEquation(subtract(StepConstant.PI, newLHS), secondRHS);
				} else {
					secondBranch = new StepEquation(subtract(multiply(2, StepConstant.PI), newLHS),
							secondRHS);
				}

				solutions.addAll(secondBranch.solve(variable, steps, tracker));
			}

			return new Result(solutions);
		}
	},

	SOLVE_SIMPLE_INVERSE_TRIGONOMETRIC {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (!se.LHS.isInverseTrigonometric() || !se.RHS.isConstantIn(variable)) {
				return null;
			}

			StepOperation trigoVar = (StepOperation) se.LHS;

			if (trigoVar.isOperation(Operation.ARCSIN)) {
				if (!se.RHS.canBeEvaluated()) {
					tracker.addCondition(new StepInequality(
							divide(StepConstant.PI, 2).negate(), se.RHS, true, false));
					tracker.addCondition(new StepInequality(
							se.RHS, divide(StepConstant.PI, 2), true, false));
				} else if (se.RHS.getValue() < -Math.PI / 2 || se.RHS.getValue() > Math.PI / 2) {
					steps.add(SolutionStepType.NO_SOLUTION_ARCSIN, trigoVar, variable);
					return new Result();
				}
			} else if (trigoVar.isOperation(Operation.ARCCOS)) {
				if (!se.RHS.canBeEvaluated()) {
					tracker.addCondition(
							new StepInequality(StepConstant.create(0), se.RHS, true, false));
					tracker.addCondition(
							new StepInequality(se.RHS, StepConstant.PI, true, false));
				} else if (se.RHS.getValue() < 0 || se.RHS.getValue() > Math.PI) {
					steps.add(SolutionStepType.NO_SOLUTION_ARCCOS, trigoVar, variable);
					return new Result();
				}
			} else { // ARCTAN
				if (!se.RHS.canBeEvaluated()) {
					tracker.addCondition(new StepInequality(
							divide(StepConstant.PI, 2).negate(), se.RHS, true, false));
					tracker.addCondition(new StepInequality(
							se.RHS, divide(StepConstant.PI, 2), true, false));
				} else if (se.RHS.getValue() < -Math.PI / 2 || se.RHS.getValue() > Math.PI / 2) {
					steps.add(SolutionStepType.NO_SOLUTION_ARCTAN, trigoVar, variable);
					return new Result();
				}
			}

			Operation op = StepExpression.getInverse(trigoVar.getOperation());
			StepExpression newLHS = trigoVar.getOperand(0);
			StepExpression newRHS = applyOp(op, se.RHS);

			return new Result(se.cloneWith(newLHS, newRHS));
		}
	},

	SOLVE_SQUARE_ROOTS {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			int lhsNum = se.LHS.countNthRoots(2);
			int rhsNum = se.RHS.countNthRoots(2);
			int sqrtNum = lhsNum + rhsNum;

			if (sqrtNum > 3 || sqrtNum == 0) {
				return null;
			}

			StepSolvable result = se;
			if (rhsNum > lhsNum) {
				result = result.swapSides();
			}

			if (sqrtNum == 1) {
				StepExpression nonIrrational = StepHelper.getNon(result.LHS,
						StepHelper.squareRoot);
				result = result.addOrSubtract(nonIrrational, steps);
				result = result.power(2, steps, tracker);
			}

			if (sqrtNum == 2) {
				StepExpression diff = subtract(result.LHS, result.RHS).regroup();
				if (!isZero(StepHelper.getNon(diff, StepHelper.squareRoot))) {
					StepExpression rootsRHS = StepHelper.getAll(result.RHS,
							StepHelper.squareRoot);
					result = result.addOrSubtract(rootsRHS, steps);
					StepExpression nonIrrational =
							StepHelper.getNon(result.LHS,
									StepHelper.squareRoot);
					result = result.addOrSubtract(nonIrrational, steps);
					result = result.power(2, steps, tracker);
				}
			}

			if (sqrtNum == 3) {
				StepExpression nonIrrational = StepHelper.getNon(result.LHS,
						StepHelper.squareRoot);
				result = result.addOrSubtract(nonIrrational, steps);

				while (result.RHS.countNonConstOperation(Operation.NROOT, variable) > 1) {
					StepExpression oneRoot = StepHelper.getOne(result.RHS, Operation.NROOT);
					result = result.addOrSubtract(oneRoot, steps);
				}

				if (result.LHS.countNonConstOperation(Operation.NROOT, variable) == 3) {
					StepExpression oneRoot = StepHelper.getOne(result.LHS, Operation.NROOT);
					result = result.addOrSubtract(oneRoot, steps);
				}

				result = result.power(2, steps, tracker);
			}

			return new Result(result);
		}
	},

	RAISE_TO_POWER {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepSolvable result = se;

			if (!se.LHS.isRoot() || !se.RHS.isRoot()) {
				StepExpression diff = subtract(se.LHS, se.RHS).regroup();

				if (diff.isOperation(Operation.PLUS)) {
					StepOperation sum = (StepOperation) diff;
					if (sum.noOfOperands() == 2 && sum.getOperand(0).isRoot()
							&& sum.getOperand(1).isRoot()) {
						result = result.addOrSubtract(sum.getOperand(1), steps);
					} else {
						return null;
					}
				} else {
					return null;
				}
			}

			long power = lcm(result.LHS.getRoot(), result.RHS.getRoot());

			if (power <= 1) {
				return null;
			}

			StepExpression toDivide = result.LHS.getCoefficient();
			result = result.multiplyOrDivide(toDivide, steps);

			if (power % 2 == 0 && result.RHS.sign() < 0) {
				steps.add(SolutionStepType.LEFT_POSITIVE_RIGHT_NEGATIVE);
				return new Result();
			}

			result = result.power(power, steps, tracker);
			return new Result(result);
		}
	},

	SOLVE_ABSOLUTE_VALUE {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			int absNum = se.countNonConstOperation(Operation.ABS, variable);

			if (absNum == 0) {
				return null;
			}

			if (!tracker.getRestriction().equals(StepInterval.R)) {
				SolutionBuilder tempSteps = new SolutionBuilder();
				int[] colorTracker = new int[]{0};
				StepExpression LHS = StepHelper
						.swapAbsInTree(se.LHS, tracker.getRestriction(), variable, tempSteps,
								colorTracker);
				StepExpression RHS = StepHelper
						.swapAbsInTree(se.RHS, tracker.getRestriction(), variable, tempSteps,
								colorTracker);

				StepSolvable result = se.cloneWith(LHS, RHS);

				steps.addSubsteps(se, result, tempSteps);

				result = result.expand(steps);
				return new Result(result);
			}

			StepExpression nonAbsDiff =
					StepHelper.getNon(subtract(se.LHS, se.RHS).regroup(), StepHelper.abs);
			if (absNum == 2 && (isZero(nonAbsDiff))) {
				StepSolvable result = se.addOrSubtract(nonAbsDiff, steps);

				if (result.RHS.countNonConstOperation(Operation.ABS, variable) > result.LHS
						.countNonConstOperation(Operation.ABS, variable)) {
					result = result.swapSides();
				}

				if (result.LHS.countNonConstOperation(Operation.ABS, variable) == 2) {
					StepExpression oneAbs = StepHelper.getOne(result.LHS, Operation.ABS);
					result = result.addOrSubtract(oneAbs, steps);
				}

				if (result.LHS.isNegative() && result.RHS.isNegative()) {
					result = result.multiplyOrDivide(StepConstant.create(-1), steps);
				}

				if (result.LHS.isNegative() || result.RHS.isNegative()) {
					throw new SolveFailedException(steps.getSteps());
				}

				return new Result(result);
			}

			Set<StepExpression> absoluteValues = new HashSet<>();
			StepHelper.getAbsoluteValues(se, absoluteValues);

			steps.add(SolutionStepType.ROOTS_AND_SIGN_TABLE);
			steps.levelDown();

			List<StepSolution> tempSolutions = new ArrayList<>();
			for (StepExpression absoluteValue : absoluteValues) {
				StepEquation tempEq = new StepEquation(absoluteValue, StepConstant.create(0));
				tempSolutions.addAll(tempEq.solve(variable, steps));
			}

			steps.levelUp();

			List<StepExpression> roots = new ArrayList<>();
			for (StepSolution ss : tempSolutions) {
				StepExpression rootValue = (StepExpression) ss.getValue(variable);
				if (!rootValue.canBeEvaluated()) {
					throw new SolveFailedException(steps.getSteps());
				}
				if (!roots.contains(rootValue)) {
					roots.add(rootValue);
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

			SolutionTable signTable =
					StepHelper.createSignTable(variable, roots, new ArrayList<>(absoluteValues));
			steps.add(signTable);

			List<StepSolution> solutions = new ArrayList<>();
			for (int i = 1; i < roots.size(); i++) {
				SolveTracker tempTracker = new SolveTracker();
				tempTracker.addRestriction(new StepInterval(roots.get(i - 1), roots.get(i), false,
						i != roots.size() - 1));

				solutions.addAll(se.solve(variable, steps, tempTracker));
			}

			return new Result(solutions);
		}
	},

	SEPARATE_PLUSMINUS {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			int plusminusNum = se.countOperation(Operation.PLUSMINUS);

			if (plusminusNum == 0) {
				return null;
			}

			if (plusminusNum == 1) {
				StepExpression argument = null;

				if (se.LHS.equals(variable) && se.RHS.isOperation(Operation.PLUSMINUS)) {
					argument = ((StepOperation) se.RHS).getOperand(0);
				} else if (se.RHS.equals(variable) && se.LHS.isOperation(Operation.PLUSMINUS)) {
					argument = ((StepOperation) se.LHS).getOperand(0);
				}

				if (argument != null && argument.isConstantIn(variable)) {
					return new Result(
							StepSolution.simpleSolution(variable, argument, tracker),
							StepSolution.simpleSolution(variable, argument.negate(), tracker));
				}
			}

			StepSolvable replacedPlus = StepHelper.replaceWithPlus(se);
			StepSolvable replacedMinus = StepHelper.replaceWithMinus(se);

			List<StepSolution> solutions = new ArrayList<>();
			solutions.addAll(replacedPlus.solve(variable, steps, tracker));
			solutions.addAll(replacedMinus.solve(variable, steps, tracker));

			return new Result(solutions);
		}
	},

	/**
	 * Solve simple absolute value equations, such as |f(x)| = a, and |f(x)| = |g(x)|
	 */
	SOLVE_SIMPLE_ABSOLUTE_VALUE {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (se.LHS.isOperation(Operation.ABS) && se.RHS.isOperation(Operation.ABS)
					|| se.RHS.isOperation(Operation.ABS) && se.LHS.isConstantIn(variable)
					|| se.LHS.isOperation(Operation.ABS) && se.RHS.isConstantIn(variable)) {

				se.LHS.setColor(1);
				se.RHS.setColor(2);

				StepSolvable result = se;
				if (result.LHS.isOperation(Operation.ABS)) {
					result = result.cloneWith(((StepOperation) result.LHS).getOperand(0),
							result.RHS);
				}
				if (result.RHS.isOperation(Operation.ABS)) {
					result = result.cloneWith(result.LHS,
							((StepOperation) result.RHS).getOperand(0));
				}

				if (!isZero(result.RHS)) {
					StepExpression newRHS = plusminus(result.RHS);
					result = result.cloneWith(result.LHS, newRHS);
					result = result.regroup(steps);
				}

				steps.addSubstep(se, result, SolutionStepType.RESOLVE_ABSOLUTE_VALUES);
				return new Result(result);
			}

			return null;
		}
	},

	REDUCE_TO_QUADRATIC {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			int degree = se.degree(variable);

			if (degree % 2 != 0 || !isZero(se.RHS)) {
				return null;
			}

			for (int i = 1; i < degree; i++) {
				if (i != degree / 2 && !isZero(se.findCoefficient(power(variable, i)))) {
					return null;
				}
			}

			StepExpression coeffHigh = se.LHS.findCoefficient(power(variable, degree));
			StepExpression coeffLow =
					se.LHS.findCoefficient(power(variable, ((double) degree) / 2));
			StepExpression constant = se.LHS.findConstantIn(variable);

			StepVariable newVariable = new StepVariable("t");

			steps.add(SolutionStepType.REPLACE_WITH, power(variable, ((double) degree) / 2),
					newVariable);

			StepExpression newEquation = nonTrivialProduct(coeffHigh, power(newVariable, 2));
			newEquation = add(newEquation, nonTrivialProduct(coeffLow, newVariable));
			newEquation = add(newEquation, constant);

			StepEquation newEq = new StepEquation(newEquation, StepConstant.create(0));
			List<StepSolution> tempSolutions = newEq.solve(newVariable, steps, tracker);

			List<StepSolution> solutions = new ArrayList<>();
			for (StepSolution solution : tempSolutions) {
				StepEquation tempEq = new StepEquation(power(variable, ((double) degree) / 2),
						(StepExpression) solution.getValue(newVariable));
				solutions.addAll(tempEq.solve(variable, steps, tracker));
			}

			return new Result(solutions);
		}
	},

	COMPLETE_CUBE {
		@Override
		public Result apply(StepEquation se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (se.degree(variable) != 3) {
				return null;
			}

			StepExpression diff = subtract(se.LHS, se.RHS).regroup();

			StepExpression cubic = diff.findCoefficient(power(variable, 3));
			StepExpression quadratic = diff.findCoefficient(power(variable, 2));
			StepExpression linear = diff.findCoefficient(variable);
			StepExpression constant = diff.findConstantIn(variable);

			if (!isOne(cubic) || quadratic == null
					|| !power(quadratic, 2).regroup().equals(multiply(3, linear).regroup())) {
				return null;
			}

			StepSolvable result = se.addOrSubtract(se.RHS, steps);

			StepExpression toComplete =
					subtract(constant, power(divide(quadratic, 3), 3)).regroup();

			steps.add(SolutionStepType.COMPLETE_THE_CUBE);
			result = result.addOrSubtract(toComplete, steps);
			result = result.cloneWith((StepExpression) result.LHS.weakFactor(steps), result.RHS);

			return new Result(result);
		}
	};

	public static List<StepSolution> checkSolutions(StepSolvable se, List<StepSolution> solutions,
			SolutionBuilder steps, SolveTracker tracker) {
		if (tracker.getRestriction().equals(StepInterval.R)
				&& tracker.getUndefinedPoints().emptySet() && !tracker.shouldCheck()) {
			return solutions;
		}

		List<StepSolution> finalSolutions = new ArrayList<>();
		for (StepSolution solution : solutions) {
			if (solution.getValue() instanceof StepLogical) {
				StepSet containedUndefinedPoints = new StepSet();
				for (StepExpression undefinedPoint : tracker.getUndefinedPoints()) {
					if (((StepLogical) solution.getValue()).contains(undefinedPoint)) {
						containedUndefinedPoints.addElement(undefinedPoint);
					}
				}

				if (!containedUndefinedPoints.emptySet()) {
					StepLogical newValue =
							subtract((StepLogical) solution.getValue(), containedUndefinedPoints);
					StepSolution newSolution =
							StepSolution.simpleSolution(solution.getVariable(), newValue, tracker);

					steps.addSubstep(solution, newSolution,
							SolutionStepType.EXCLUDE_UNDEFINED_POINTS, containedUndefinedPoints);
					finalSolutions.add(newSolution);
					continue;
				}
			}

			if (solution.getValue() instanceof StepExpression) {
				StepExpression value = (StepExpression) solution.getValue();

				if (tracker.getUndefinedPoints().contains(value)) {

					continue;
				}

				if (!tracker.getRestriction().contains(value)) {
					steps.add(SolutionStepType.INVALID_NOT_IN_RANGE, value,
							tracker.getRestriction());
					continue;
				}

				if (tracker.shouldCheck()) {
					if (!se.checkSolution(solution.getVariable(), value, steps, tracker)) {
						continue;
					}
				}
			}

			finalSolutions.add(solution);
		}

		return finalSolutions;
	}
}
