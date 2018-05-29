package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.common.plugin.Operation;

import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.*;

enum SolveSteps implements SolveStepGenerator {

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
			if (isZero(se.LHS) || isZero(se.RHS)) {
				return new Result(se.weakFactor(steps));
			}

			return null;
		}
	},

	DIFF {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepSolvable result = se;
			int degreeL = se.LHS.degree(variable);
			int degreeR = se.RHS.degree(variable);
			if (degreeL != -1 && degreeR != -1 && degreeR > degreeL) {
				result = result.swapSides();
			}

			return new Result(result.subtract(result.RHS, steps));
		}
	},

	SUBTRACT_COMMON {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepExpression common =
					se.LHS.nonIntegersOfSum().getCommon(se.RHS.nonIntegersOfSum());
			if (!se.LHS.equals(se.RHS) && common != null && !common.isInteger()) {
				return new Result(se.addOrSubtract(common, steps));
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

			StepSolvable result = se;

			if (StepHelper.getCoefficientValue(se.RHS, variable)
					> StepHelper.getCoefficientValue(se.LHS, variable)) {
				result = result.swapSides();
			}

			StepExpression RHSlinear = result.RHS.findExpression(variable);
			result = result.addOrSubtract(RHSlinear, steps);

			StepExpression LHSconstant = result.LHS.findConstantIn(variable);
			result = result.addOrSubtract(LHSconstant, steps);

			StepExpression linearCoefficient = result.LHS.findCoefficient(variable);
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

			StepExpression RHSlinear = se.RHS.findExpression(expression);
			StepSolvable result = se.addOrSubtract(RHSlinear, steps);

			StepExpression LHSconstant =
					subtract(result.LHS, result.LHS.findExpression(expression)).regroup();
			result = result.addOrSubtract(LHSconstant, steps);

			StepExpression linearCoefficient = result.LHS.findCoefficient(expression);
			result = result.multiplyOrDivide(linearCoefficient, steps);

			return new Result(result);
		}
	},

	COMPLETE_THE_SQUARE {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepExpression difference = subtract(se.LHS, se.RHS).regroup();

			if (difference.degree(variable) != 2) {
				return null;
			}

			StepExpression a = difference.findCoefficient(power(variable, 2));
			StepExpression b = difference.findCoefficient(variable);
			StepExpression c = difference.findConstantIn(variable);

			if (isOne(a) && b.isEven() && !isZero(c)) {
				StepExpression RHSVariable = se.RHS.findVariableIn(variable);
				StepSolvable result = se.addOrSubtract(RHSVariable, steps);

				StepExpression LHSConstant = result.LHS.findConstantIn(variable);
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

	TAKE_ROOT {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepExpression diff = subtract(se.LHS, se.RHS).regroup();
			StepExpression noConstDiff = diff.findVariableIn(variable);

			StepSolvable result = se;
			if (noConstDiff.isPower()) {
				StepExpression RHSNonConst = result.RHS.findVariableIn(variable);

				result = result.addOrSubtract(RHSNonConst, steps);
				result = result.addOrSubtract(result.LHS.findConstantIn(variable), steps);
			} else if (!result.LHS.isPower() || !result.RHS.isPower()) {
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

			long root = gcd(result.LHS.getPower(), result.RHS.getPower());

			StepExpression toDivide = result.LHS.getCoefficient();
			result = result.multiplyOrDivide(toDivide, steps);

			if (root % 2 == 0 && result.RHS.sign() < 0) {
				steps.add(SolutionStepType.LEFT_POSITIVE_RIGHT_NEGATIVE);
				return new Result();
			}

			steps.add(SolutionStepType.GROUP_WRAPPER);
			steps.levelDown();

			if (root == 2 && result.RHS.isConstant()) {
				steps.add(SolutionStepType.SQUARE_ROOT);

				StepExpression underSquare = ((StepOperation) result.LHS).getOperand(0);
				if (isEqual(result.RHS, 0)) {
					result = result.cloneWith(underSquare, StepConstant.create(0));
				} else {
					result = result.cloneWith(underSquare, plusminus(root(result.RHS, 2)));
				}
			} else {
				result = result.nthroot(root, steps);
			}

			steps.add(result);
			steps.levelUp();
			return new Result(result);
		}
	},

}
