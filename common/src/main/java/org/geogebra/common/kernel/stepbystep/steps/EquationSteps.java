package org.geogebra.common.kernel.stepbystep.steps;

import java.util.*;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionLine;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.solution.SolutionTable;
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.common.plugin.Operation;

import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.*;

public enum EquationSteps implements SolveStepGenerator {

	FIND_DEFINED_RANGE {
		@Override
		public List<StepSolution>  apply(StepSolvable se, StepVariable variable, SolutionBuilder steps,
										 SolveTracker tracker) {
			if (tracker.shouldCheck() || !tracker.getRestriction().equals(StepInterval.R)
					|| !tracker.getUndefinedPoints().emptySet()) {
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

					List<StepSolution> solutions = new StepInequality(root, StepConstant.create(0), false, false)
							.solve(variable, restrictionsSteps);

					for (StepNode solution : solutions) {
						restriction = intersect(restriction, (StepInterval)
								((StepSolution) solution).getValue(variable));
					}
				}

				if (restriction != null && !StepInterval.R.equals(restriction)) {
					steps.addGroup(SolutionStepType.DETERMINE_THE_DEFINED_RANGE,
							restrictionsSteps, restriction);
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

					List<StepSolution> solutions = new StepEquation(denominator, StepConstant.create(0))
							.solve(variable, undefinedPointsSteps);

					for (StepSolution solution : solutions) {
						undefinedPoints.addElement((StepExpression) solution.getValue(variable));
					}
				}

				if (!undefinedPoints.emptySet()) {
					steps.addGroup(SolutionStepType.FIND_UNDEFINED_POINTS,
							undefinedPointsSteps, undefinedPoints);
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
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps,
										SolveTracker tracker) {
			StepSolution solution = null;

			if (se.getLHS().equals(se.getRHS())) {
				solution = StepSolution.simpleSolution(variable, tracker.getRestriction(), tracker);
				steps.addSubstep(se, solution, SolutionStepType.STATEMENT_IS_TRUE);
			}

			if (se.getLHS().equals(variable) && se.getRHS().isConstantIn(variable)) {
				solution = StepSolution.simpleSolution(variable, se.getRHS(), tracker);
			}

			if (se.getRHS().equals(variable) && se.getLHS().isConstantIn(variable)) {
				solution = StepSolution.simpleSolution(variable, se.getLHS(), tracker);
			}

			if (solution == null && se.getLHS().isConstantIn(variable) && se.getRHS().isConstantIn(variable)) {
				steps.add(SolutionStepType.STATEMENT_IS_FALSE);
				return new ArrayList<>();
			}

			if (solution != null) {
				List<StepSolution> solutions = new ArrayList<>();
				solutions.add(solution);

				return solutions;
			}

			return null;
		}
	},

	REGROUP {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			se.regroup(steps, tracker);
			return null;
		}
	},

	EXPAND {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			se.expand(steps, tracker);
			return null;
		}
	},

	FACTOR {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			if (isZero(se.getLHS()) || isZero(se.getRHS())) {
				se.factor(steps, true);
			}

			return null;
		}
	},

	DIFF {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			se.subtract(se.getRHS(), steps, tracker);
			return null;
		}
	},

	SUBTRACT_COMMON {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			StepExpression common = se.getLHS().nonIntegersOfSum().getCommon(se.getRHS().nonIntegersOfSum());
			if (!se.getLHS().equals(se.getRHS()) && common != null && !common.isInteger()) {
				se.addOrSubtract(common, steps, tracker);
			}

			return null;
		}
	},

	NEGATE_BOTH_SIDES {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable,
										SolutionBuilder steps, SolveTracker tracker) {
			if ((se.getLHS().isNegative() && se.getRHS().isNegative())
					|| (isZero(se.getLHS()) && se.getRHS().isNegative())
					|| (se.getLHS().isNegative() && isZero(se.getRHS()))) {
				StepSolvable original = se.deepCopy();

				StepExpression newLHS = isZero(se.getLHS()) ? se.getLHS() : se.getLHS().negate();
				StepExpression newRHS = isZero(se.getRHS()) ? se.getRHS() : se.getRHS().negate();

				se.modify(newLHS, newRHS);

				steps.addSubstep(original, se, SolutionStepType.NEGATE_BOTH_SIDES);
			}

			return null;
		}
	},

	SOLVE_LINEAR {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			if (!(se.degree(variable) == 0 || se.degree(variable) == 1)) {
				return null;
			}

			if (StepHelper.getCoefficientValue(se.getLHS(), variable) <
					StepHelper.getCoefficientValue(se.getRHS(), variable)) {
				se.swapSides();
			}

			StepExpression RHSlinear = se.getRHS().findExpression(variable);
			se.addOrSubtract(RHSlinear, steps, tracker);

			StepExpression LHSconstant = se.getLHS().findConstantIn(variable);
			se.addOrSubtract(LHSconstant, steps, tracker);

			StepExpression linearCoefficient = se.getLHS().findCoefficient(variable);
			se.multiplyOrDivide(linearCoefficient, steps, tracker);

			se.cleanColors();

			return null;
		}
	},

	SOLVE_LINEAR_IN_EXPRESSION {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			StepOperation expression = StepHelper.linearInExpression(se, variable);

			if (expression == null) {
				return null;
			}

			StepExpression RHSlinear = se.getRHS().findExpression(expression);
			se.addOrSubtract(RHSlinear, steps, tracker);

			StepExpression LHSconstant = subtract(se.getLHS(), se.getLHS().findExpression(expression)).regroup();
			se.addOrSubtract(LHSconstant, steps, tracker);

			StepExpression linearCoefficient = se.getLHS().findCoefficient(expression);
			se.multiplyOrDivide(linearCoefficient, steps, tracker);

			return null;
		}
	},

	COMPLETE_THE_SQUARE {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps,
										SolveTracker tracker) {
			StepExpression difference = subtract(se.getLHS(), se.getRHS()).regroup();

			if (difference.degree(variable) != 2) {
				return null;
			}

			StepExpression a = difference.findCoefficient(power(variable, 2));
			StepExpression b = difference.findCoefficient(variable);
			StepExpression c = difference.findConstantIn(variable);

			if (isOne(a) && b.isEven() && !isZero(c)) {
				StepExpression RHSConstant = se.getRHS().findConstantIn(variable);
				se.addOrSubtract(subtract(se.getRHS(), RHSConstant).regroup(), steps, tracker);

				StepExpression LHSConstant = se.getLHS().findConstantIn(variable);
				StepExpression toComplete = subtract(LHSConstant, power(divide(b, 2), 2)).regroup();

				steps.add(SolutionStepType.COMPLETE_THE_SQUARE);

				se.addOrSubtract(toComplete, steps, tracker);
				se.factor(steps, false);
			}

			return null;
		}
	},

	SOLVE_QUADRATIC {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			StepExpression difference = subtract(se.getLHS(), se.getRHS()).regroup();

			if (difference.degree(variable) == 2) {
				if (!isZero(se.getRHS())) {
					se.subtract(se.getRHS(), steps, tracker);
					return null;
				}
			} else {
				return null;
			}

			if (se.getLHS().findCoefficient(power(variable, 2)).isNegative()) {
				se.multiply(StepConstant.create(-1), steps, tracker);
			}

			StepExpression a = se.getLHS().findCoefficient(power(variable, 2));
			StepExpression b = se.getLHS().findCoefficient(variable);
			StepExpression c = se.getLHS().findConstantIn(variable);

			a.setColor(1);
			b.setColor(2);
			c.setColor(3);

			StepExpression discriminant = subtract(power(b, 2), multiply(4, multiply(a, c)));

			SolutionBuilder tempSteps = new SolutionBuilder();

			tempSteps.add(SolutionStepType.QUADRATIC_FORMULA, variable);
			se.modify(variable,
					divide(add(minus(b), plusminus(root(discriminant, 2))), multiply(2, a)));

			se.regroup(tempSteps, tracker);

			steps.addGroup(new SolutionLine(SolutionStepType.USE_QUADRATIC_FORMULA, a, b, c), tempSteps, se);

			discriminant = discriminant.regroup();

			if (discriminant.canBeEvaluated() && discriminant.getValue() < 0) {
				return new ArrayList<>();
			}

			a.cleanColors();
			b.cleanColors();
			c.cleanColors();

			List<StepSolution> solutions = new ArrayList<>();
			if (!a.isConstant()) {
				solutions.add(StepSolution.simpleSolution(variable, divide(c.negate(), b), tracker));
				solutions.get(0).addCondition(new StepEquation(a, StepConstant.create(0)));
				tracker.addCondition(new StepEquation(a, StepConstant.create(0)).setInequation());
			}

			if (discriminant.sign() <= 0 && !(discriminant.canBeEvaluated() && discriminant.getValue() > 0)) {
				tracker.addCondition(new StepInequality(discriminant, StepConstant.create(0), false, false));
			}

			StepExpression solution1 = divide(add(minus(b), root(discriminant, 2)), multiply(2, a));
			StepExpression solution2 = divide(subtract(minus(b), root(discriminant, 2)), multiply(2, a));

			solutions.add(StepSolution.simpleSolution(variable, solution1.adaptiveRegroup(), tracker));
			solutions.add(StepSolution.simpleSolution(variable, solution2.adaptiveRegroup(), tracker));
			return solutions;
		}
	},

	SOLVE_QUADRATIC_IN_EXPRESSION {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
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
			return allSolutions;
		}
	},

	SOLVE_PRODUCT {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
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
				return solutions;
			}

			return null;
		}
	},

	COMMON_DENOMINATOR {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			StepSolvable commonDenominator = (StepSolvable)
					StepStrategies.defaultRegroup(se, steps, new RegroupTracker().unsetIntegerFractions());

			se.modify(commonDenominator.getLHS(), commonDenominator.getRHS());
			return null;
		}
	},

	MULTIPLY_THROUGH {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			StepExpression commonDenominator = StepConstant.create(1);
			if (!se.getLHS().isConstant() && se.getLHS().getDenominator() != null) {
				commonDenominator = StepHelper.LCM(se.getLHS().getDenominator(), commonDenominator);
			}

			if (!se.getRHS().isConstant() && se.getRHS().getDenominator() != null) {
				commonDenominator = StepHelper.LCM(se.getRHS().getDenominator(), commonDenominator);
			}

			se.multiply(commonDenominator, steps, tracker);
			return null;
		}
	},

	RECIPROCATE_EQUATION {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			if (StepHelper.shouldReciprocate(se.getLHS()) && StepHelper.shouldReciprocate(se.getRHS())) {
				se.reciprocate(steps);
			}

			return null;
		}
	},

	SIMPLIFY_TRIGONOMETRIC {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
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

			if (coeffSineSquared != null && coeffSineSquared.equals(coeffCosineSquared)) {
				se.addOrSubtract(se.getRHS().findExpression(sineSquared), steps, tracker);

				StepExpression newLHS = subtract(se.getLHS(), add(se.getLHS().findExpression(sineSquared),
						se.getLHS().findExpression(cosineSquared))).regroup();
				StepExpression newRHS = subtract(se.getRHS(),
						multiply(coeffSineSquared, add(sineSquared, cosineSquared)));

				se.modify(newLHS, newRHS);

				se.replace(add(sineSquared, cosineSquared), StepConstant.create(1), steps);
				se.regroup(steps, tracker);
			}

			bothSides = subtract(se.getLHS(), se.getRHS()).regroup();

			StepExpression sine = sin(argument);
			StepExpression cosine = cos(argument);

			StepExpression coeffSine = bothSides.findCoefficient(sine);
			StepExpression coeffCosine = bothSides.findCoefficient(cosine);
			coeffSineSquared = bothSides.findCoefficient(sineSquared);
			coeffCosineSquared = bothSides.findCoefficient(cosineSquared);

			if (coeffSine != null && coeffCosine == null && coeffSineSquared != null && coeffCosineSquared != null) {
				se.replace(cosineSquared, subtract(1, sineSquared), steps);
				se.regroup(steps, tracker);
			}

			if (coeffSine == null && coeffCosine != null && coeffSineSquared != null && coeffCosineSquared != null) {
				se.replace(sineSquared, subtract(1, cosineSquared), steps);
				se.regroup(steps, tracker);
			}

			if (coeffSine != null && coeffCosine != null && coeffSineSquared == null && coeffCosineSquared == null) {
				if (!isZero(se.getLHS().findExpression(sine))) {
					se.addOrSubtract(se.getLHS().findExpression(cosine), steps, tracker);
					se.addOrSubtract(se.getRHS().findExpression(sine), steps, tracker);
				} else {
					se.addOrSubtract(se.getRHS().findExpression(cosine), steps, tracker);
				}

				se.square(steps, tracker);
			}

			return null;
		}
	},

	SOLVE_SIMPLE_TRIGONOMETRIC {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			if (!se.getLHS().isTrigonometric() || !se.getRHS().isConstant()) {
				return null;
			}

			StepOperation trigoVar = (StepOperation) se.getLHS();

			if (!se.getRHS().canBeEvaluated() && trigoVar.getOperation() != Operation.TAN) {
				return new ArrayList<>();
			}

			if ((trigoVar.isOperation(Operation.SIN) || trigoVar.isOperation(Operation.COS))
					&& (se.getRHS().getValue() < -1 || se.getRHS().getValue() > 1)) {
				steps.add(SolutionStepType.NO_SOLUTION_TRIGONOMETRIC, trigoVar, variable);
				return new ArrayList<>();
			}

			Operation op = StepExpression.getInverse(trigoVar.getOperation());
			StepExpression newLHS = trigoVar.getOperand(0);

			if (trigoVar.getOperation() == Operation.TAN) {
				StepExpression newRHS = add(applyOp(op, se.getRHS()),
						multiply(tracker.getNextArbInt(), StepConstant.PI));

				StepEquation newEq = new StepEquation(newLHS, newRHS);
				return newEq.solve(variable, steps, tracker);
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
					secondBranch = new StepEquation(subtract(multiply(2, StepConstant.PI), newLHS), secondRHS);
				}

				solutions.addAll(secondBranch.solve(variable, steps));
			}

			return solutions;
		}
	},

	SOLVE_IRRATIONAL {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			int sqrtNum = se.countNonConstOperation(Operation.NROOT, variable);

			if (se.getRHS().countNonConstOperation(Operation.NROOT, variable) >
					se.getLHS().countNonConstOperation(Operation.NROOT, variable)) {
				se.swapSides();
			}

			if (sqrtNum > 3 || sqrtNum == 0) {
				return null;
			}

			if (sqrtNum == 1) {
				StepExpression nonIrrational = StepHelper.getNon(se.getLHS(), Operation.NROOT);
				se.addOrSubtract(nonIrrational, steps, tracker);
				se.square(steps, tracker);
			}

			if (sqrtNum == 2) {
				StepExpression diff = subtract(se.getLHS(), se.getRHS()).regroup();
				if (isZero(StepHelper.getNon(diff, Operation.NROOT))) {
					StepExpression nonIrrational = StepHelper.getNon(se.getLHS(), Operation.NROOT);
					se.addOrSubtract(nonIrrational, steps, tracker);
					if (se.getRHS().countNonConstOperation(Operation.NROOT, variable) == 2) {
						StepExpression oneRoot = StepHelper.getOne(se.getLHS(), Operation.NROOT);
						se.addOrSubtract(oneRoot, steps, tracker);
					}
					se.square(steps, tracker);
				} else {
					StepExpression rootsRHS = StepHelper.getAll(se.getRHS(), Operation.NROOT);
					se.addOrSubtract(rootsRHS, steps, tracker);
					StepExpression nonIrrational = StepHelper.getNon(se.getLHS(), Operation.NROOT);
					se.addOrSubtract(nonIrrational, steps, tracker);
					se.square(steps, tracker);
				}
			}

			if (sqrtNum == 3) {
				StepExpression nonIrrational = StepHelper.getNon(se.getLHS(), Operation.NROOT);
				se.addOrSubtract(nonIrrational, steps, tracker);

				while (se.getRHS().countNonConstOperation(Operation.NROOT, variable) > 1) {
					StepExpression oneRoot = StepHelper.getOne(se.getRHS(), Operation.NROOT);
					se.addOrSubtract(oneRoot, steps, tracker);
				}

				if (se.getLHS().countNonConstOperation(Operation.NROOT, variable) == 3) {
					StepExpression oneRoot = StepHelper.getOne(se.getLHS(), Operation.NROOT);
					se.addOrSubtract(oneRoot, steps, tracker);
				}

				se.square(steps, tracker);
			}

			return null;
		}
	},

	SOLVE_ABSOLUTE_VALUE {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			if (se.countOperation(Operation.ABS) == 0) {
				return null;
			}

			if (!tracker.getRestriction().equals(StepInterval.R)) {
				SolutionBuilder tempSteps = new SolutionBuilder();
				int[] colorTracker = new int[] {0};
				StepExpression LHS = StepHelper.swapAbsInTree(se.getLHS(),
						tracker.getRestriction(), variable, tempSteps, colorTracker);
				StepExpression RHS = StepHelper.swapAbsInTree(se.getRHS(),
						tracker.getRestriction(), variable, tempSteps, colorTracker);

				StepSolvable original = se.deepCopy();
				se.modify(LHS, RHS);

				steps.addSubsteps(original, se, tempSteps);

				se.expand(steps, tracker);
				return null;
			}

			int absNum = se.countNonConstOperation(Operation.ABS, variable);

			StepExpression nonAbsDiff = StepHelper.getNon(subtract(se.getLHS(), se.getRHS()).regroup(), Operation.ABS);
			if (absNum == 2 && (isZero(nonAbsDiff))) {
				se.addOrSubtract(nonAbsDiff, steps, tracker);

				if (se.getRHS().countNonConstOperation(Operation.ABS, variable) >
						se.getLHS().countNonConstOperation(Operation.ABS, variable)) {
					se.swapSides();
				}

				if (se.getLHS().countNonConstOperation(Operation.ABS, variable) == 2) {
					StepExpression oneAbs = StepHelper.getOne(se.getLHS(), Operation.ABS);
					se.addOrSubtract(oneAbs, steps, tracker);
				}

				if (se.getLHS().isNegative() && se.getRHS().isNegative()) {
					se.multiplyOrDivide(StepConstant.create(-1), steps, tracker);
				}

				if (se.getLHS().isNegative() || se.getRHS().isNegative()) {
					throw new SolveFailedException(steps.getSteps());
				}

				return null;
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
			for(StepSolution ss : tempSolutions) {
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

            SolutionTable signTable = SolutionTable.createSignTable(variable, roots,
					new ArrayList<>(absoluteValues));
            steps.add(signTable);

			List<StepSolution> solutions = new ArrayList<>();
			for (int i = 1; i < roots.size(); i++) {
				StepEquation newEq;
				if (se.isSwapped()) {
					newEq = new StepEquation(se.getRHS(), se.getLHS());
				} else {
					newEq = new StepEquation(se.getLHS(), se.getRHS());
				}

				SolveTracker tempTracker = new SolveTracker();
				tempTracker.addRestriction(new StepInterval(roots.get(i - 1), roots.get(i),
						false, i != roots.size() - 1));

				solutions.addAll(newEq.solve(variable, steps, tempTracker));
			}

			return solutions;
		}
	},

	SEPARATE_PLUSMINUS {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps,
										SolveTracker tracker) {
			if (se.countOperation(Operation.PLUSMINUS) == 0) {
				return null;
			}

			StepSolvable replacedPlus = StepHelper.replaceWithPlus(se);
			StepSolvable replacedMinus = StepHelper.replaceWithMinus(se);

			List<StepSolution> solutions = new ArrayList<>();

			solutions.addAll(replacedPlus.solve(variable, steps, tracker));
			solutions.addAll(replacedMinus.solve(variable, steps, tracker));

			return solutions;
		}
	},

	/**
	 * Solve simple absolute value equations, such as |f(x)| = a, and |f(x)| = |g(x)|
	 */
	SOLVE_SIMPLE_ABSOLUTE_VALUE {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			if (se.getLHS().isOperation(Operation.ABS) && se.getRHS().isOperation(Operation.ABS)
					|| se.getRHS().isOperation(Operation.ABS) && se.getLHS().isConstantIn(variable)
					|| se.getLHS().isOperation(Operation.ABS) && se.getRHS().isConstantIn(variable)) {

				se.getLHS().setColor(1);
				se.getRHS().setColor(2);
				StepSolvable original = se.deepCopy();

				if (se.getLHS().isOperation(Operation.ABS)) {
					se.modify(((StepOperation) se.getLHS()).getOperand(0), se.getRHS());
				}
				if (se.getRHS().isOperation(Operation.ABS)) {
					se.modify(se.getLHS(), ((StepOperation) se.getRHS()).getOperand(0));
				}

				if (!isZero(se.getRHS())) {
					StepExpression newRHS = plusminus(se.getRHS());
					se.modify(se.getLHS(), newRHS);
					se.regroup(steps, tracker);
				}

				steps.addSubstep(original, se, SolutionStepType.RESOLVE_ABSOLUTE_VALUES);
			}

			return null;
		}
	},

	TAKE_ROOT {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			StepExpression diff = subtract(se.getLHS(), se.getRHS()).regroup();
			StepExpression constant = diff.findConstantIn(variable);
			StepExpression noConstDiff = subtract(diff, constant).regroup();

			if (noConstDiff.isPower()) {
				StepExpression RHSConstant = se.getRHS().findConstantIn(variable);
				StepExpression RHSNonConst = subtract(se.getRHS(), RHSConstant).regroup();

				se.addOrSubtract(RHSNonConst, steps, tracker);
				se.addOrSubtract(se.getLHS().findConstantIn(variable), steps, tracker);
			} else if (!se.getLHS().isPower() || !se.getRHS().isPower()) {
				if (diff.isOperation(Operation.PLUS)) {
                    StepOperation so = (StepOperation) diff;

                    if (so.noOfOperands() == 2 && so.getOperand(0).isPower() && so.getOperand(1).isPower()) {
                        se.addOrSubtract(so.getOperand(1), steps, tracker);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
			}

			long root = gcd(se.getLHS().getPower(), se.getRHS().getPower());

			StepExpression toDivide = se.getLHS().getCoefficient();
			se.multiplyOrDivide(toDivide, steps, tracker);

			if (isEqual(root % 2, 0) && se.getRHS().isConstant() && se.getRHS().getValue() < 0) {
				steps.add(SolutionStepType.LEFT_POSITIVE_RIGHT_NEGATIVE);
				return new ArrayList<>();
			}

			steps.add(SolutionStepType.GROUP_WRAPPER);
			steps.levelDown();

			if (root == 2 && se.getRHS().isConstant()) {
				steps.add(SolutionStepType.SQUARE_ROOT);

				StepExpression underSquare = ((StepOperation) se.getLHS()).getOperand(0);
				if (isEqual(se.getRHS(), 0)) {
					se.modify(underSquare, StepConstant.create(0));
				} else {
					se.modify(underSquare, plusminus(root(se.getRHS(), 2)));
				}
			} else {
				se.nthroot(root, steps);
			}

			steps.add(se);
			steps.levelUp();
			return null;
		}
	},

	REDUCE_TO_QUADRATIC {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			int degree = se.degree(variable);

			if (degree % 2 != 0) {
				return null;
			}

			for (int i = 1; i < degree; i++) {
				if (i != degree / 2 && !isZero(se.findCoefficient(power(variable, i)))) {
					return null;
				}
			}

			se.subtract(se.getRHS(), steps, tracker);

			StepExpression coeffHigh = se.getLHS().findCoefficient(power(variable, degree));
			StepExpression coeffLow = se.getLHS().findCoefficient(power(variable, ((double) degree) / 2));
			StepExpression constant = se.getLHS().findConstantIn(variable);

			StepVariable newVariable = new StepVariable("t");

			steps.add(SolutionStepType.REPLACE_WITH, power(variable, ((double) degree) / 2),
					newVariable);

			StepExpression newEquation = multiply(coeffHigh, power(newVariable, 2));
			newEquation = add(newEquation, multiply(coeffLow, newVariable));
			newEquation = add(newEquation, constant);

			StepEquation newEq = new StepEquation(newEquation.regroup(), StepConstant.create(0));
			List<StepSolution> tempSolutions = newEq.solve(newVariable, steps, tracker);

			List<StepSolution> solutions = new ArrayList<>();
			for (StepSolution solution : tempSolutions) {
				StepEquation tempEq = new StepEquation(power(variable, ((double) degree) / 2),
						(StepExpression) solution.getValue(newVariable));
				solutions.addAll(tempEq.solve(variable, steps, tracker));
			}

			return solutions;
		}
	},

	COMPLETE_CUBE {
		@Override
		public List<StepSolution> apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
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

			se.addOrSubtract(se.getRHS(), steps, tracker);

			StepExpression toComplete = subtract(constant, power(divide(quadratic, 3), 3)).regroup();

			steps.add(SolutionStepType.COMPLETE_THE_CUBE);
			se.addOrSubtract(toComplete, steps, tracker);

			StepExpression newLHS = (StepExpression) StepStrategies.defaultFactor(
					se.getLHS(), steps, new RegroupTracker().setWeakFactor());
			se.modify(newLHS, se.getRHS());

			return null;
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
			if (solution.getValue() instanceof StepLogical && !tracker.getUndefinedPoints().emptySet()) {
				StepLogical newValue = subtract((StepLogical) solution.getValue(), tracker.getUndefinedPoints());
				StepSolution newSolution = StepSolution.simpleSolution(solution.getVariable(), newValue, tracker);

				steps.addSubstep(solution, newSolution,
						SolutionStepType.EXCLUDE_UNDEFINED_POINTS, tracker.getUndefinedPoints());
				finalSolutions.add(newSolution);
				continue;
			}

			if (solution.getValue() instanceof StepExpression) {
				StepExpression value = (StepExpression) solution.getValue();

				if (tracker.getUndefinedPoints().contains(value)) {

					continue;
				}

				if (!tracker.getRestriction().contains(value)) {
					steps.add(SolutionStepType.INVALID_NOT_IN_RANGE, value, tracker.getRestriction());
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
