package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.gcd;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.intersect;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOne;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isZero;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.plusminus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.power;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.root;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.subtract;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquation;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepInequality;
import org.geogebra.common.kernel.stepbystep.steptree.StepInterval;
import org.geogebra.common.kernel.stepbystep.steptree.StepLogical;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepSet;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolution;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolvable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.plugin.Operation;

enum SolveSteps implements SolveStepGenerator<StepSolvable> {

	FIND_DEFINED_RANGE {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
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

	CONVERT_OR_SET_APPROXIMATE {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (tracker.isApproximate() != null) {
				return null;
			}

			if (0 < se.maxDecimal() && se.maxDecimal() < 5 && se.containsFractions()) {
				tracker.setApproximate(false);
				return new Result((StepSolvable) se.convertToFractions(steps));
			}

			if (se.maxDecimal() > 0) {
				tracker.setApproximate(true);
			} else {
				tracker.setApproximate(false);
			}

			return null;
		}
	},

	REGROUP {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (tracker.isApproximate()) {
				return new Result(StepStrategies.solverDecimalRegroup(se, steps));
			} else {
				return new Result(StepStrategies.solverRegroup(se, steps));
			}
		}
	},

	SIMPLIFY_FRACTIONS {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			return new Result((StepSolvable)
					FractionSteps.SIMPLIFY_FRACTIONS.apply(se, steps, new RegroupTracker()));
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

			double LHSCoeff = StepHelper.getCoefficientValue(se.LHS, variable);
			double RHSCoeff = StepHelper.getCoefficientValue(se.RHS, variable);
			if (!isEqual(RHSCoeff, 0) && RHSCoeff > LHSCoeff) {
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

			if (isOne(a) && b != null && b.isEven() && !isZero(c)) {
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
			StepSolvable result = se;

			if (se.LHS.isPower() && se.RHS.isPower()) {
				long degreeLHS = se.LHS.degree(variable);
				long degreeRHS = se.RHS.degree(variable);
				if (degreeLHS != -1 && degreeRHS != -1 && degreeRHS > degreeLHS) {
					result = result.swapSides();
				}
			} else {
				StepExpression diff = subtract(se.LHS, se.RHS).regroup();
				StepExpression noConstDiff = diff.findVariableIn(variable);

				if (noConstDiff.isPower()) {
					StepExpression RHSNonConst = result.RHS.findVariableIn(variable);

					result = result.addOrSubtract(RHSNonConst, steps);
					result = result.addOrSubtract(result.LHS.findConstantIn(variable), steps);
				} else {
					return null;
				}
			}

			long root = gcd(result.LHS.getPower(), result.RHS.getPower());
			if (root <= 1) {
				return null;
			}

			StepExpression toDivide = result.LHS.getCoefficient();

			if (result.RHS.isConstantIn(variable)) {
				result = result.multiplyOrDivide(toDivide, steps);
			}

			if (root % 2 == 0 && result.RHS.sign() < 0) {
				steps.add(SolutionStepType.LEFT_POSITIVE_RIGHT_NEGATIVE);
				return new Result();
			}

			steps.add(SolutionStepType.GROUP_WRAPPER);
			steps.levelDown();

			if (root == 2 && result.LHS.isOperation(Operation.POWER) && result.RHS.isConstant()) {
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

			result = result.regroup(steps);
			return new Result(result);
		}
	},

	COMMON_DENOMINATOR {
		@Override
		public Result apply(StepSolvable se, StepVariable variable, SolutionBuilder steps,
				SolveTracker tracker) {
			return new Result((StepSolvable) StepStrategies.addFractions(se, steps));
		}
	},

}
