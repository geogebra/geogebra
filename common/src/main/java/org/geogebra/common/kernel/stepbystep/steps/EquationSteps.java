package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionLine;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.solution.SolutionTable;
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.common.plugin.Operation;

import java.util.*;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.*;

public enum EquationSteps implements SolveStepGenerator {

	FIND_DEFINED_RANGE {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (tracker.shouldCheck() || !tracker.getRestriction().equals(StepInterval.R) ||
					!tracker.getUndefinedPoints().emptySet()) {
				return null;
			}

			try {
				Set<StepExpression> roots = new HashSet<>();
				StepHelper.getRoots(se, roots);

				StepLogical restriction = null;
				SolutionBuilder restrictionsSteps = new SolutionBuilder();

				for (StepExpression root : roots) {
					if (root.isConstantIn(variable)) {
						continue;
					}

					List<StepSolution> solutions =
							new StepInequality(root, StepConstant.create(0), false, false)
									.solve(variable, restrictionsSteps);

					for (StepNode solution : solutions) {
						restriction = intersect(restriction,
								(StepInterval) ((StepSolution) solution).getValue(variable));
					}
				}

				if (restriction != null && !StepInterval.R.equals(restriction)) {
					steps.addGroup(SolutionStepType.DETERMINE_THE_DEFINED_RANGE, restrictionsSteps,
							restriction);
					tracker.addRestriction(restriction);
				}
			} catch (SolveFailedException e) {
				tracker.setShouldCheckSolutions();
				return null;
			}

			try {
				Set<StepExpression> denominators = new HashSet<>();
				StepHelper.getDenominators(se, denominators);

				StepSet undefinedPoints = new StepSet();
				SolutionBuilder undefinedPointsSteps = new SolutionBuilder();

				for (StepExpression denominator : denominators) {
					if (denominator.isConstantIn(variable)) {
						continue;
					}

					List<StepSolution> solutions =
							new StepEquation(denominator, StepConstant.create(0))
									.solve(variable, undefinedPointsSteps);

					for (StepSolution solution : solutions) {
						undefinedPoints.addElement((StepExpression) solution.getValue(variable));
					}
				}

				if (!undefinedPoints.emptySet()) {
					steps.addGroup(SolutionStepType.FIND_UNDEFINED_POINTS, undefinedPointsSteps,
							undefinedPoints);
					tracker.addUndefinedPoints(undefinedPoints);
				}
			} catch (SolveFailedException e) {
				tracker.setShouldCheckSolutions();
			}

			return null;
		}
	},

	TRIVIAL_EQUATIONS {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (se.getLHS().equals(se.getRHS())) {
				StepSolution solution
						= StepSolution.simpleSolution(variable, tracker.getRestriction(), tracker);
				steps.addSubstep(se, solution, SolutionStepType.STATEMENT_IS_TRUE);
				return new Result(solution);
			}

			if (se.getLHS().equals(variable) && se.getRHS().isConstantIn(variable)) {
				return new Result(StepSolution.simpleSolution(variable, se.getRHS(), tracker));
			}

			if (se.getRHS().equals(variable) && se.getLHS().isConstantIn(variable)) {
				return new Result(StepSolution.simpleSolution(variable, se.getLHS(), tracker));
			}

			if (se.getLHS().isConstantIn(variable) &&
					se.getRHS().isConstantIn(variable)) {
				steps.add(SolutionStepType.STATEMENT_IS_FALSE);
				return new Result();
			}

			return null;
		}
	},

	REGROUP {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			return new Result(se.adaptiveRegroup(steps));
		}
	},

	EXPAND {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			return new Result(se.expand(steps));
		}
	},

	FACTOR {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (isZero(se.getLHS()) || isZero(se.getRHS())) {
				return new Result(se.weakFactor(steps));
			}

			return null;
		}
	},

	DIFF {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			return new Result(se.subtract(se.getRHS(), steps));
		}
	},

	SUBTRACT_COMMON {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepExpression common =
					se.getLHS().nonIntegersOfSum().getCommon(se.getRHS().nonIntegersOfSum());
			if (!se.getLHS().equals(se.getRHS()) && common != null && !common.isInteger()) {
				return new Result(se.addOrSubtract(common, steps));
			}

			return null;
		}
	},

	NEGATE_BOTH_SIDES {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if ((se.getLHS().isNegative() && se.getRHS().isNegative()) ||
					(isZero(se.getLHS()) && se.getRHS().isNegative()) ||
					(se.getLHS().isNegative() && isZero(se.getRHS()))) {
				StepExpression newLHS = isZero(se.getLHS()) ? se.getLHS() : se.getLHS().negate();
				StepExpression newRHS = isZero(se.getRHS()) ? se.getRHS() : se.getRHS().negate();

				StepSolvable negated = se.cloneWith(newLHS, newRHS);

				steps.addSubstep(se, negated, SolutionStepType.NEGATE_BOTH_SIDES);

				return new Result(negated);
			}

			return null;
		}
	},

	SOLVE_LINEAR {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			int degree = se.degree(variable);
			if (degree != 0 && degree != 1) {
				return null;
			}

			if (StepHelper.getCoefficientValue(se.getLHS(), variable) <
					StepHelper.getCoefficientValue(se.getRHS(), variable)) {
				se.swapSides();
			}

			StepExpression RHSlinear = se.getRHS().findExpression(variable);
			StepSolvable result = se.addOrSubtract(RHSlinear, steps);

			StepExpression LHSconstant = result.getLHS().findConstantIn(variable);
			result = result.addOrSubtract(LHSconstant, steps);

			StepExpression linearCoefficient = result.getLHS().findCoefficient(variable);
			result = result.multiplyOrDivide(linearCoefficient, steps);

			result.cleanColors();

			return new Result(result);
		}
	},

	SOLVE_LINEAR_IN_EXPRESSION {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepOperation expression = StepHelper.linearInExpression(se, variable);

			if (expression == null) {
				return null;
			}

			StepExpression RHSlinear = se.getRHS().findExpression(expression);
			StepSolvable result = se.addOrSubtract(RHSlinear, steps);

			StepExpression LHSconstant =
					subtract(result.getLHS(), result.getLHS().findExpression(expression)).regroup();
			result = result.addOrSubtract(LHSconstant, steps);

			StepExpression linearCoefficient = result.getLHS().findCoefficient(expression);
			result = result.multiplyOrDivide(linearCoefficient, steps);

			return new Result(result);
		}
	},

	COMPLETE_THE_SQUARE {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepExpression difference = subtract(se.getLHS(), se.getRHS()).regroup();

			if (difference.degree(variable) != 2) {
				return null;
			}

			StepExpression a = difference.findCoefficient(power(variable, 2));
			StepExpression b = difference.findCoefficient(variable);
			StepExpression c = difference.findConstantIn(variable);

			if (isOne(a) && b.isEven() && !isZero(c)) {
				StepExpression RHSVariable = se.getRHS().findVariableIn(variable);
				StepSolvable result = se.addOrSubtract(RHSVariable, steps);

				StepExpression LHSConstant = result.getLHS().findConstantIn(variable);
				StepExpression toComplete = subtract(LHSConstant, power(divide(b, 2), 2))
						.regroup();

				steps.add(SolutionStepType.COMPLETE_THE_SQUARE);

				result = result.addOrSubtract(toComplete, steps);
				result = result.factor(steps);

				return new Result(result);
			}

			return null;
		}
	},

	SOLVE_QUADRATIC {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (se.degree(variable) != 2) {
				return null;
			}

			StepSolvable result = se;

			if (result.getLHS().findCoefficient(power(variable, 2)).isNegative()) {
				result = result.multiply(StepConstant.create(-1), steps);
			}

			StepExpression a = result.getLHS().findCoefficient(power(variable, 2));
			StepExpression b = result.getLHS().findCoefficient(variable);
			StepExpression c = result.getLHS().findConstantIn(variable);

			a.setColor(1);
			b.setColor(2);
			c.setColor(3);

			StepExpression discriminant = subtract(power(b, 2), multiply(4, multiply(a, c)));

			SolutionBuilder tempSteps = new SolutionBuilder();

			tempSteps.add(SolutionStepType.QUADRATIC_FORMULA, variable);
			result = result.cloneWith(variable,
					divide(add(minus(b), plusminus(root(discriminant, 2))), multiply(2, a)));

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

			if (discriminant.sign() <= 0 &&
					!(discriminant.canBeEvaluated() && discriminant.getValue() > 0)) {
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
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepExpression expression = StepHelper.quadraticInExpression(se, variable);

			if (expression == null) {
				return null;
			}

			StepVariable newVariable = new StepVariable("t");

			StepEquation replaced = new StepEquation(se.getLHS().replace(expression, newVariable),
					se.getRHS().replace(expression, newVariable));

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

	SOLVE_PRODUCT {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepOperation product = null;

			if (se.getRHS().isOperation(Operation.MULTIPLY) && isZero(se.getLHS())) {
				product = (StepOperation) se.getRHS();
			} else if (se.getLHS().isOperation(Operation.MULTIPLY) && isZero(se.getRHS())) {
				product = (StepOperation) se.getLHS();
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

	COMMON_DENOMINATOR {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			return new Result((StepSolvable) StepStrategies
					.defaultRegroup(se, steps, new RegroupTracker().unsetIntegerFractions()));
		}
	},

	MULTIPLY_THROUGH {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepExpression commonDenominator = StepConstant.create(1);
			if (!se.getLHS().isConstant() && se.getLHS().getDenominator() != null) {
				commonDenominator = StepHelper.LCM(se.getLHS().getDenominator(), commonDenominator);
			}

			if (!se.getRHS().isConstant() && se.getRHS().getDenominator() != null) {
				commonDenominator = StepHelper.LCM(se.getRHS().getDenominator(), commonDenominator);
			}

			return new Result(se.multiply(commonDenominator, steps));
		}
	},

	RECIPROCATE_EQUATION {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (StepHelper.shouldReciprocate(se.getLHS()) &&
					StepHelper.shouldReciprocate(se.getRHS())) {
				return new Result(se.reciprocate(steps));
			}

			return null;
		}
	},

	SIMPLIFY_TRIGONOMETRIC {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepExpression bothSides = subtract(se.getLHS(), se.getRHS()).regroup();
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
				result = result.addOrSubtract(result.getRHS().findExpression(sineSquared), steps);

				StepExpression newLHS = subtract(result.getLHS(),
						add(result.getLHS().findExpression(sineSquared),
								result.getLHS().findExpression(cosineSquared))).regroup();
				StepExpression newRHS = subtract(result.getRHS(),
						multiply(coeffSineSquared, add(sineSquared, cosineSquared)));

				result = result.cloneWith(newLHS, newRHS);

				result = result.replace(add(sineSquared, cosineSquared),
						StepConstant.create(1), steps);
				result = result.regroup(steps);
			}

			bothSides = subtract(result.getLHS(), result.getRHS()).regroup();

			StepExpression sine = sin(argument);
			StepExpression cosine = cos(argument);

			StepExpression coeffSine = bothSides.findCoefficient(sine);
			StepExpression coeffCosine = bothSides.findCoefficient(cosine);
			coeffSineSquared = bothSides.findCoefficient(sineSquared);
			coeffCosineSquared = bothSides.findCoefficient(cosineSquared);

			if (coeffSine != null && coeffCosine == null && coeffSineSquared != null &&
					coeffCosineSquared != null) {
				result = result.replace(cosineSquared, subtract(1, sineSquared), steps);
				result = result.regroup(steps);
			}

			if (coeffSine == null && coeffCosine != null && coeffSineSquared != null &&
					coeffCosineSquared != null) {
				result = result.replace(sineSquared, subtract(1, cosineSquared), steps);
				result = result.regroup(steps);
			}

			if (coeffSine != null && coeffCosine != null && coeffSineSquared == null &&
					coeffCosineSquared == null) {
				if (!isZero(result.getLHS().findExpression(sine))) {
					result = result.addOrSubtract(result.getLHS().findExpression(cosine), steps);
					result = result.addOrSubtract(result.getRHS().findExpression(sine), steps);
				} else {
					result = result.addOrSubtract(result.getRHS().findExpression(cosine), steps);
				}

				result = result.square(steps, tracker);
			}

			return new Result(result);
		}
	},

	SOLVE_SIMPLE_TRIGONOMETRIC {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (!se.getLHS().isTrigonometric() || !se.getRHS().isConstant()) {
				return null;
			}

			StepOperation trigoVar = (StepOperation) se.getLHS();

			if (!se.getRHS().canBeEvaluated() && trigoVar.getOperation() != Operation.TAN) {
				return new Result();
			}

			if ((trigoVar.isOperation(Operation.SIN) || trigoVar.isOperation(Operation.COS)) &&
					(se.getRHS().getValue() < -1 || se.getRHS().getValue() > 1)) {
				steps.add(SolutionStepType.NO_SOLUTION_TRIGONOMETRIC, trigoVar, variable);
				return new Result();
			}

			Operation op = StepExpression.getInverse(trigoVar.getOperation());
			StepExpression newLHS = trigoVar.getOperand(0);

			if (trigoVar.getOperation() == Operation.TAN) {
				StepExpression newRHS = add(applyOp(op, se.getRHS()),
						multiply(tracker.getNextArbInt(), StepConstant.PI));

				StepSolvable newEq = se.cloneWith(newLHS, newRHS);
				return new Result(newEq.solve(variable, steps, tracker));
			}

			StepExpression firstRHS = add(applyOp(op, se.getRHS()),
					multiply(multiply(2, tracker.getNextArbInt()), StepConstant.PI));

			StepEquation firstBranch = new StepEquation(newLHS, firstRHS);
			List<StepSolution> solutions = firstBranch.solve(variable, steps, tracker);

			if (!isEqual(se.getRHS(), 1) && !isEqual(se.getRHS(), -1)) {
				StepExpression secondRHS = add(applyOp(op, se.getRHS()),
						multiply(multiply(2, tracker.getNextArbInt()), StepConstant.PI));
				StepEquation secondBranch;
				if (trigoVar.getOperation() == Operation.SIN) {
					secondBranch = new StepEquation(subtract(StepConstant.PI, newLHS), secondRHS);
				} else {
					secondBranch = new StepEquation(subtract(multiply(2, StepConstant.PI), newLHS),
							secondRHS);
				}

				solutions.addAll(secondBranch.solve(variable, steps));
			}

			return new Result(solutions);
		}
	},

	SOLVE_IRRATIONAL {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			int sqrtNum = se.countNonConstOperation(Operation.NROOT, variable);

			if (sqrtNum > 3 || sqrtNum == 0) {
				return null;
			}

			StepSolvable result = se;
			if (se.getRHS().countNonConstOperation(Operation.NROOT, variable) >
					se.getLHS().countNonConstOperation(Operation.NROOT, variable)) {
				result.swapSides();
			}

			if (sqrtNum == 1) {
				StepExpression nonIrrational = StepHelper.getNon(result.getLHS(), Operation.NROOT);
				result = result.addOrSubtract(nonIrrational, steps);
				result = result.square(steps, tracker);
			}

			if (sqrtNum == 2) {
				StepExpression diff = subtract(result.getLHS(), result.getRHS()).regroup();
				if (isZero(StepHelper.getNon(diff, Operation.NROOT))) {
					StepExpression nonIrrational =
							StepHelper.getNon(result.getLHS(), Operation.NROOT);
					result = result.addOrSubtract(nonIrrational, steps);
					if (result.getRHS().countNonConstOperation(Operation.NROOT, variable) == 2) {
						StepExpression oneRoot =
								StepHelper.getOne(result.getLHS(), Operation.NROOT);
						result = result.addOrSubtract(oneRoot, steps);
					}
					result = result.square(steps, tracker);
				} else {
					StepExpression rootsRHS = StepHelper.getAll(result.getRHS(), Operation.NROOT);
					result = result.addOrSubtract(rootsRHS, steps);
					StepExpression nonIrrational =
							StepHelper.getNon(result.getLHS(), Operation.NROOT);
					result = result.addOrSubtract(nonIrrational, steps);
					result = result.square(steps, tracker);
				}
			}

			if (sqrtNum == 3) {
				StepExpression nonIrrational = StepHelper.getNon(result.getLHS(), Operation.NROOT);
				result = result.addOrSubtract(nonIrrational, steps);

				while (result.getRHS().countNonConstOperation(Operation.NROOT, variable) > 1) {
					StepExpression oneRoot = StepHelper.getOne(result.getRHS(), Operation.NROOT);
					result = result.addOrSubtract(oneRoot, steps);
				}

				if (result.getLHS().countNonConstOperation(Operation.NROOT, variable) == 3) {
					StepExpression oneRoot = StepHelper.getOne(result.getLHS(), Operation.NROOT);
					result = result.addOrSubtract(oneRoot, steps);
				}

				result = result.square(steps, tracker);
			}

			return new Result(result);
		}
	},

	SOLVE_ABSOLUTE_VALUE {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			int absNum = se.countNonConstOperation(Operation.ABS, variable);

			if (absNum == 0) {
				return null;
			}

			if (!tracker.getRestriction().equals(StepInterval.R)) {
				SolutionBuilder tempSteps = new SolutionBuilder();
				int[] colorTracker = new int[]{0};
				StepExpression LHS = StepHelper
						.swapAbsInTree(se.getLHS(), tracker.getRestriction(), variable, tempSteps,
								colorTracker);
				StepExpression RHS = StepHelper
						.swapAbsInTree(se.getRHS(), tracker.getRestriction(), variable, tempSteps,
								colorTracker);

				StepSolvable result = se.cloneWith(LHS, RHS);

				steps.addSubsteps(se, result, tempSteps);

				result = result.expand(steps);
				return new Result(result);
			}

			StepExpression nonAbsDiff =
					StepHelper.getNon(subtract(se.getLHS(), se.getRHS()).regroup(), Operation.ABS);
			if (absNum == 2 && (isZero(nonAbsDiff))) {
				StepSolvable result = se.addOrSubtract(nonAbsDiff, steps);

				if (result.getRHS().countNonConstOperation(Operation.ABS, variable) >
						result.getLHS().countNonConstOperation(Operation.ABS, variable)) {
					result.swapSides();
				}

				if (result.getLHS().countNonConstOperation(Operation.ABS, variable) == 2) {
					StepExpression oneAbs = StepHelper.getOne(result.getLHS(), Operation.ABS);
					result = result.addOrSubtract(oneAbs, steps);
				}

				if (result.getLHS().isNegative() && result.getRHS().isNegative()) {
					result = result.multiplyOrDivide(StepConstant.create(-1), steps);
				}

				if (result.getLHS().isNegative() || result.getRHS().isNegative()) {
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
					SolutionTable.createSignTable(variable, roots, new ArrayList<>(absoluteValues));
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
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			int plusminusNum = se.countOperation(Operation.PLUSMINUS);

			if (plusminusNum == 0) {
				return null;
			}

			if (plusminusNum == 1) {
				StepExpression argument = null;

				if (se.getLHS().equals(variable) && se.getRHS().isOperation(Operation.PLUSMINUS)) {
					argument = ((StepOperation) se.getRHS()).getOperand(0);
				} else if (se.getRHS().equals(variable) &&
						se.getLHS().isOperation(Operation.PLUSMINUS)) {
					argument = ((StepOperation) se.getLHS()).getOperand(0);
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
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (se.getLHS().isOperation(Operation.ABS) && se.getRHS().isOperation(Operation.ABS) ||
					se.getRHS().isOperation(Operation.ABS) && se.getLHS().isConstantIn(variable) ||
					se.getLHS().isOperation(Operation.ABS) && se.getRHS().isConstantIn(variable)) {

				se.getLHS().setColor(1);
				se.getRHS().setColor(2);

				StepSolvable result = se;
				if (result.getLHS().isOperation(Operation.ABS)) {
					result = result.cloneWith(((StepOperation) result.getLHS()).getOperand(0),
							result.getRHS());
				}
				if (result.getRHS().isOperation(Operation.ABS)) {
					result = result.cloneWith(result.getLHS(),
							((StepOperation) result.getRHS()).getOperand(0));
				}

				if (!isZero(result.getRHS())) {
					StepExpression newRHS = plusminus(result.getRHS());
					result = result.cloneWith(result.getLHS(), newRHS);
					result = result.regroup(steps);
				}

				steps.addSubstep(se, result, SolutionStepType.RESOLVE_ABSOLUTE_VALUES);
				return new Result(result);
			}

			return null;
		}
	},

	TAKE_ROOT {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepExpression diff = subtract(se.getLHS(), se.getRHS()).regroup();
			StepExpression noConstDiff = diff.findVariableIn(variable);

			StepSolvable result = se;
			if (noConstDiff.isPower()) {
				StepExpression RHSNonConst = result.getRHS().findVariableIn(variable);

				result = result.addOrSubtract(RHSNonConst, steps);
				result = result.addOrSubtract(result.getLHS().findConstantIn(variable), steps);
			} else if (!result.getLHS().isPower() || !result.getRHS().isPower()) {
				if (diff.isOperation(Operation.PLUS)) {
					StepOperation so = (StepOperation) diff;

					if (so.noOfOperands() == 2 && so.getOperand(0).isPower() &&
							so.getOperand(1).isPower()) {
						result = result.addOrSubtract(so.getOperand(1), steps);
					} else {
						return null;
					}
				} else {
					return null;
				}
			}

			long root = gcd(result.getLHS().getPower(), result.getRHS().getPower());

			StepExpression toDivide = result.getLHS().getCoefficient();
			result = result.multiplyOrDivide(toDivide, steps);

			if (root % 2 == 0 && result.getRHS().sign() < 0) {
				steps.add(SolutionStepType.LEFT_POSITIVE_RIGHT_NEGATIVE);
				return new Result();
			}

			steps.add(SolutionStepType.GROUP_WRAPPER);
			steps.levelDown();

			if (root == 2 && result.getRHS().isConstant()) {
				steps.add(SolutionStepType.SQUARE_ROOT);

				StepExpression underSquare = ((StepOperation) result.getLHS()).getOperand(0);
				if (isEqual(result.getRHS(), 0)) {
					result = result.cloneWith(underSquare, StepConstant.create(0));
				} else {
					result = result.cloneWith(underSquare, plusminus(root(result.getRHS(), 2)));
				}
			} else {
				result = result.nthroot(root, steps);
			}

			steps.add(result);
			steps.levelUp();
			return new Result(result);
		}
	},

	REDUCE_TO_QUADRATIC {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			int degree = se.degree(variable);

			if (degree % 2 != 0 || !isZero(se.getRHS())) {
				return null;
			}

			for (int i = 1; i < degree; i++) {
				if (i != degree / 2 && !isZero(se.findCoefficient(power(variable, i)))) {
					return null;
				}
			}

			StepExpression coeffHigh = se.getLHS().findCoefficient(power(variable, degree));
			StepExpression coeffLow =
					se.getLHS().findCoefficient(power(variable, ((double) degree) / 2));
			StepExpression constant = se.getLHS().findConstantIn(variable);

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
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (se.degree(variable) != 3) {
				return null;
			}

			StepExpression diff = subtract(se.getLHS(), se.getRHS()).regroup();

			StepExpression cubic = diff.findCoefficient(power(variable, 3));
			StepExpression quadratic = diff.findCoefficient(power(variable, 2));
			StepExpression linear = diff.findCoefficient(variable);
			StepExpression constant = diff.findConstantIn(variable);

			if (!isOne(cubic) || quadratic == null ||
					!power(quadratic, 2).regroup().equals(multiply(3, linear).regroup())) {
				return null;
			}

			StepSolvable result = se.addOrSubtract(se.getRHS(), steps);

			StepExpression toComplete =
					subtract(constant, power(divide(quadratic, 3), 3)).regroup();

			steps.add(SolutionStepType.COMPLETE_THE_CUBE);
			result = result.addOrSubtract(toComplete, steps);
			result = result.cloneWith((StepExpression) result.getLHS().weakFactor(steps),
					result.getRHS());

			return new Result(result);
		}
	};

	public static List<StepSolution> checkSolutions(StepSolvable se, List<StepSolution> solutions,
			SolutionBuilder steps, SolveTracker tracker) {
		if (tracker.getRestriction().equals(StepInterval.R) &&
				tracker.getUndefinedPoints().emptySet() && !tracker.shouldCheck()) {
			return solutions;
		}

		List<StepSolution> finalSolutions = new ArrayList<>();
		for (StepSolution solution : solutions) {
			if (solution.getValue() instanceof StepLogical &&
					!tracker.getUndefinedPoints().emptySet()) {
				StepLogical newValue =
						subtract((StepLogical) solution.getValue(), tracker.getUndefinedPoints());
				StepSolution newSolution =
						StepSolution.simpleSolution(solution.getVariable(), newValue, tracker);

				steps.addSubstep(solution, newSolution, SolutionStepType.EXCLUDE_UNDEFINED_POINTS,
						tracker.getUndefinedPoints());
				finalSolutions.add(newSolution);
				continue;
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
