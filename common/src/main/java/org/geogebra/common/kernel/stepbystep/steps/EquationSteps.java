package org.geogebra.common.kernel.stepbystep.steps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.solution.SolutionTable;
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.common.plugin.Operation;

import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.*;

public enum EquationSteps implements SolveStepGenerator {

	REGROUP {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			return se.regroup(steps, tracker);
		}
	},

	EXPAND {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			return se.expand(steps, tracker);
		}
	},

	FACTOR {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			if (isZero(se.getLHS()) || isZero(se.getRHS())) {
				return StepStrategies.defaultFactor(se, steps, new RegroupTracker().setWeakFactor());
			}

			return se;
		}
	},

	DIFF {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			se.subtract(se.getRHS(), steps, tracker);
			return se;
		}
	},

	SUBTRACT_COMMON {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			StepExpression common = se.getLHS().nonIntegersOfSum().getCommon(se.getRHS().nonIntegersOfSum());
			if (!se.getLHS().equals(se.getRHS()) && common != null && !common.isInteger()) {
				se.addOrSubtract(common, steps, tracker);
			}

			return se;
		}
	},

	SOLVE_LINEAR {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			StepExpression diff = subtract(se.getLHS(), se.getRHS()).regroup();

			if (diff.isConstantIn(variable)) {
				return se.trivialSolution(variable, tracker);
			}

			if (!(se.degree(variable) == 0 || se.degree(variable) == 1)) {
				return se;
			}

			if (StepHelper.getCoefficientValue(se.getLHS(), variable) < StepHelper.getCoefficientValue(se.getRHS(),
					variable)) {
				se.swapSides();
			}

			StepExpression RHSlinear = se.getRHS().findExpression(variable);
			se.addOrSubtract(RHSlinear, steps, tracker);

			StepExpression LHSconstant = se.getLHS().findConstantIn(variable);
			se.addOrSubtract(LHSconstant, steps, tracker);

			StepExpression linearCoefficient = se.getLHS().findCoefficient(variable);
			se.multiplyOrDivide(linearCoefficient, steps, tracker);

			se.cleanColors();

			return se.trivialSolution(variable, tracker);
		}
	},

	SOLVE_LINEAR_IN_EXPRESSION {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			StepOperation expression = StepHelper.linearInExpression(se, variable);

			if (expression == null) {
				return se;
			}

			StepExpression RHSlinear = se.getRHS().findExpression(expression);
			se.addOrSubtract(RHSlinear, steps, tracker);

			StepExpression LHSconstant = subtract(se.getLHS(), se.getLHS().findExpression(expression)).regroup();
			se.addOrSubtract(LHSconstant, steps, tracker);

			StepExpression linearCoefficient = se.getLHS().findCoefficient(expression);
			se.multiplyOrDivide(linearCoefficient, steps, tracker);

			return se;
		}
	},

	SOLVE_QUADRATIC {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			if (se.degree(variable) != 2) {
				return se;
			}

			double leftSquareCoeff = StepHelper.getCoefficientValue(se.getLHS(), power(variable, 2));
			double rightSquareCoeff = StepHelper.getCoefficientValue(se.getRHS(), power(variable, 2));

			if (leftSquareCoeff < 0 && isZero(se.getRHS())) {
				se.multiply(StepConstant.create(-1), steps, tracker);
			} else if (rightSquareCoeff < 0 && isZero(se.getLHS())) {
				se.multiply(StepConstant.create(-1), steps, tracker);
				se.swapSides();
			} else if (leftSquareCoeff < rightSquareCoeff) {
				se.swapSides();
			}

			StepExpression difference = subtract(se.getLHS(), se.getRHS()).regroup();

			StepExpression a = difference.findCoefficient(power(variable, 2));
			StepExpression b = difference.findCoefficient(variable);
			StepExpression c = difference.findConstantIn(variable);

			if (isOne(a) && isEven(b) && !isZero(c)) {
				StepExpression RHSConstant = se.getRHS().findConstantIn(variable);
				se.addOrSubtract(subtract(se.getRHS(), RHSConstant).regroup(), steps, tracker);

				StepExpression LHSConstant = se.getLHS().findConstantIn(variable);
				StepExpression toComplete = subtract(LHSConstant, power(divide(b, 2), 2)).regroup();

				steps.add(SolutionStepType.COMPLETE_THE_SQUARE);

				se.addOrSubtract(toComplete, steps, tracker);
				se.factor(steps);

				if (se.getRHS().getValue() < 0) {
					return new StepSet();
				}

				return se;
			}

			if (!isZero(se.getRHS())) {
				se.subtract(se.getRHS(), steps, tracker);
				return se;
			}

			a.setColor(1);
			b.setColor(2);
			c.setColor(3);

			StepExpression discriminant = subtract(power(b, 2), multiply(4, multiply(a, c)));

			steps.add(SolutionStepType.USE_QUADRATIC_FORMULA, a, b, c);
			steps.levelDown();

			steps.add(SolutionStepType.QUADRATIC_FORMULA, variable);
			se.modify(variable,
					divide(add(minus(b), plusminus(root(discriminant, 2))), multiply(2, a)));

			se.regroup(steps, tracker);

			if (discriminant.getValue() > 0) {
				StepExpression solution1 = divide(add(minus(b), root(discriminant, 2)), multiply(2, a));
				StepExpression solution2 = divide(subtract(minus(b), root(discriminant, 2)), multiply(2, a));

				return new StepSet(
						StepSolution.simpleSolution(variable, solution1.adaptiveRegroup(), tracker),
						StepSolution.simpleSolution(variable, solution2.adaptiveRegroup(), tracker));
			}

			return new StepSet();
		}
	},

	SOLVE_QUADRATIC_IN_EXPRESSION {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			StepExpression expression = StepHelper.quadraticInExpression(se, variable);

			if (expression == null) {
				return se;
			}

			StepVariable newVariable = new StepVariable("t");

			StepEquation replaced = new StepEquation(se.getLHS().replace(expression, newVariable),
					se.getRHS().replace(expression, newVariable));

			StepSet allSolutions = new StepSet();
			StepSet tempSolutions = replaced.solve(newVariable, steps);

			for (StepNode solution : tempSolutions.getElements()) {
				StepEquation newEq = new StepEquation(expression,
						(StepExpression) ((StepSolution) solution).getValue(newVariable));
				allSolutions.addAll(newEq.solve(variable, steps));
			}

			return allSolutions;
		}
	},

	SOLVE_PRODUCT {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			StepOperation product = null;

			if (se.getRHS().isOperation(Operation.MULTIPLY) && isZero(se.getLHS())) {
				product = (StepOperation) se.getRHS();
			} else if (se.getLHS().isOperation(Operation.MULTIPLY) && isZero(se.getRHS())) {
				product = (StepOperation) se.getLHS();
			}

			if (product != null) {
				steps.add(SolutionStepType.PRODUCT_IS_ZERO);
				StepSet solutions = new StepSet();

				for (StepExpression operand : product) {
					if (!operand.isConstant()) {
						StepEquation newEq = new StepEquation(operand, StepConstant.create(0));
						solutions.addAll(newEq.solve(variable, steps, tracker));
					}
				}

				return solutions;
			}

			return se;
		}
	},

	COMMON_DENOMINATOR {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			return StepStrategies.defaultRegroup(se, steps, new RegroupTracker().unsetIntegerFractions());
		}
	},

	MULTIPLY_THROUGH {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			StepExpression commonDenominator = StepConstant.create(1);
			if (!se.getLHS().isConstant() && se.getLHS().getDenominator() != null) {
					commonDenominator = StepHelper.LCM(se.getLHS().getDenominator(), commonDenominator);
			}

			if (!se.getRHS().isConstant() && se.getRHS().getDenominator() != null) {
					commonDenominator = StepHelper.LCM(se.getRHS().getDenominator(), commonDenominator);
			}

			se.multiply(commonDenominator, steps, tracker);
			return se;
		}
	},

	RECIPROCATE_EQUATION {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			if (StepHelper.shouldReciprocate(se.getLHS()) && StepHelper.shouldReciprocate(se.getRHS())) {
				se.reciprocate(steps);
			}

			return se;
		}
	},

	SIMPLIFY_TRIGONOMETRIC {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			StepExpression bothSides = subtract(se.getLHS(), se.getRHS()).regroup();
			StepOperation trigoVar = StepHelper.findExpressionInVariable(bothSides, variable);

			if (trigoVar == null || !trigoVar.isTrigonometric()) {
				return se;
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

			return se;
		}
	},

	SOLVE_SIMPLE_TRIGONOMETRIC {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			if (!se.getLHS().isTrigonometric() || !se.getRHS().isConstant()) {
				return se;
			}

			StepOperation trigoVar = (StepOperation) se.getLHS();

			if (!se.getRHS().canBeEvaluated() && trigoVar.getOperation() != Operation.TAN) {
				return new StepSet();
			}

			if ((trigoVar.isOperation(Operation.SIN) || trigoVar.isOperation(Operation.COS))
					&& (se.getRHS().getValue() < -1 || se.getRHS().getValue() > 1)) {
				steps.add(SolutionStepType.NO_SOLUTION_TRIGONOMETRIC, trigoVar, variable);
				return new StepSet();
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
			StepSet solutions = firstBranch.solve(variable, steps, tracker);

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
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			int sqrtNum = se.countNonConstOperation(Operation.NROOT, variable);

			if (se.getRHS().countNonConstOperation(Operation.NROOT, variable) >
					se.getLHS().countNonConstOperation(Operation.NROOT, variable)) {
				se.swapSides();
			}

			if (sqrtNum > 3 || sqrtNum == 0) {
				return se;
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

			return se;
		}
	},

	SOLVE_ABSOLUTE_VALUE {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			if (se.countOperation(Operation.ABS) == 0) {
				return se;
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
				return se;
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

				return se;
			}

			ArrayList<StepExpression> absoluteValues = new ArrayList<>();
			StepHelper.getAbsoluteValues(absoluteValues, se);

			steps.add(SolutionStepType.ROOTS_AND_SIGN_TABLE);
			steps.levelDown();

			StepSet tempSolutions = new StepSet();
			for (StepExpression absoluteValue : absoluteValues) {
				StepEquation tempEq = new StepEquation(absoluteValue, StepConstant.create(0));
				tempSolutions.addAll(tempEq.solve(variable, steps));
			}

			steps.levelUp();

			List<StepExpression> roots = new ArrayList<>();
			for(StepNode sn : tempSolutions.getElements()) {
				roots.add((StepExpression) ((StepSolution) sn).getValue(variable));
			}

			Collections.sort(roots, new Comparator<StepExpression>() {
				@Override
				public int compare(StepExpression s1, StepExpression s2) {
					return Double.compare(s1.getValue(), s2.getValue());
				}
			});

			roots.add(0, StepConstant.NEG_INF);
			roots.add(StepConstant.POS_INF);

            SolutionTable signTable = SolutionTable.createSignTable(variable, roots, absoluteValues);
            steps.add(signTable);

			StepSet solutions = new StepSet();
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

	PLUSMINUS {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
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

				steps.add(SolutionStepType.SUBSTEP_WRAPPER);
				steps.levelDown();
				steps.add(original);
				steps.add(SolutionStepType.RESOLVE_ABSOLUTE_VALUES);
				steps.add(se);
				steps.levelUp();

				return se;
			}

			if (se.getRHS().isOperation(Operation.PLUSMINUS)) {
				StepExpression underPM = ((StepOperation) se.getRHS()).getOperand(0);
				
				if (underPM.isConstantIn(variable) && se.getLHS().equals(variable)) {
					return new StepSet(
							StepSolution.simpleSolution(variable, underPM, tracker),
							StepSolution.simpleSolution(variable, underPM.negate(), tracker));
				}

				StepEquation positiveBranch = new StepEquation(se.getLHS(), underPM);
				StepEquation negativeBranch = new StepEquation(se.getLHS(), underPM.negate());

				StepSet solutions = positiveBranch.solve(variable, steps, tracker);
				solutions.addAll(negativeBranch.solve(variable, steps, tracker));

				return solutions;
			}

			return se;
		}
	},

	TAKE_ROOT {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
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
                        return se;
                    }
                } else {
                    return se;
                }
			}

			long root = gcd(se.getLHS().getPower(), se.getRHS().getPower());

			StepExpression toDivide = se.getLHS().getCoefficient();
			se.multiplyOrDivide(toDivide, steps, tracker);

			if (isEven(root) && se.getRHS().isConstant() && se.getRHS().getValue() < 0) {
				return new StepSet();
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
			return se;
		}
	},

	REDUCE_TO_QUADRATIC {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			int degree = se.degree(variable);

			if (degree % 2 != 0) {
				return se;
			}

			for (int i = 1; i < degree; i++) {
				if (i != degree / 2 && !isZero(se.findCoefficient(power(variable, i)))) {
					return se;
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
			StepSet tempSolutions = newEq.solve(newVariable, steps, tracker);

			StepSet solutions = new StepSet();
			for (StepNode solution : tempSolutions.getElements()) {
				StepEquation tempEq = new StepEquation(power(variable, ((double) degree) / 2),
						(StepExpression) ((StepSolution) solution).getValue(newVariable));
				solutions.addAll(tempEq.solve(variable, steps, tracker));
			}

			return solutions;
		}
	},

	COMPLETE_CUBE {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps, SolveTracker tracker) {
			if (se.degree(variable) != 3) {
				return se;
			}

			StepExpression diff = subtract(se.getLHS(), se.getRHS()).regroup();

			StepExpression cubic = diff.findCoefficient(power(variable, 3));
			StepExpression quadratic = diff.findCoefficient(power(variable, 2));
			StepExpression linear = diff.findCoefficient(variable);
			StepExpression constant = diff.findConstantIn(variable);

			if (!isOne(cubic) || quadratic == null ||
					!power(quadratic, 2).regroup().equals(multiply(3, linear).regroup())) {
				return se;
			}

			se.addOrSubtract(se.getRHS(), steps, tracker);

			StepExpression toComplete = subtract(constant, power(divide(quadratic, 3), 3)).regroup();

			steps.add(SolutionStepType.COMPLETE_THE_CUBE);
			se.addOrSubtract(toComplete, steps, tracker);

			StepExpression newLHS = (StepExpression) StepStrategies.defaultFactor(
					se.getLHS(), steps, new RegroupTracker().setWeakFactor());
			se.modify(newLHS, se.getRHS());

			return se;
		}
	};

	public static StepSet checkSolutions(StepSolvable se, StepSet solutions, SolutionBuilder steps,
										 SolveTracker tracker) {

		if (solutions.size() == 0) {
			steps.add(SolutionStepType.NO_REAL_SOLUTION);
			return solutions;
		} else if (solutions.size() == 1) {
			steps.add(SolutionStepType.SOLUTION, solutions.getElements());
		} else if (solutions.size() > 1) {
			steps.add(SolutionStepType.SOLUTIONS, solutions.getElements());
		}

		SolutionBuilder temp = new SolutionBuilder();
		for (StepNode sol : solutions.getElements()) {
			if (sol instanceof StepSolution) {
				if (!se.checkSolution((StepSolution) sol, temp, tracker)) {
					solutions.remove(sol);
				}
			}
		}

		if (!tracker.getRestriction().equals(StepInterval.R) ||
				tracker.getUndefinedPoints() != null || tracker.shouldCheck()) {
			steps.add(SolutionStepType.CHECK_VALIDITY);
			steps.levelDown();
			steps.addAll(temp.getSteps());
		}

		return solutions;
	}
}
