package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.*;

public class StepStrategies {

	public static StepNode convertToFraction(StepNode sn, SolutionBuilder sb) {
		return RegroupSteps.CONVERT_DECIMAL_TO_FRACTION.apply(sn, sb, new RegroupTracker());
	}

	public static StepNode decimalRegroup(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
		return RegroupSteps.DECIMAL_REGROUP.apply(sn, sb, tracker);
	}

	public static StepNode decimalRegroup(StepNode sn, SolutionBuilder sb) {
		return decimalRegroup(sn, sb, new RegroupTracker());
	}

	public static StepNode defaultRegroup(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
		return RegroupSteps.DEFAULT_REGROUP.apply(sn, sb, tracker);
	}

	public static StepNode defaultRegroup(StepNode sn, SolutionBuilder sb) {
		return defaultRegroup(sn, sb, new RegroupTracker());
	}

	public static StepNode decimalExpand(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
		return ExpandSteps.DECIMAL_EXPAND.apply(sn, sb, tracker);
	}

	public static StepNode decimalExpand(StepNode sn, SolutionBuilder sb) {
		return decimalExpand(sn, sb, new RegroupTracker());
	}

	public static StepNode defaultExpand(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
		return ExpandSteps.DEFAULT_EXPAND.apply(sn, sb, tracker);
	}

	public static StepNode defaultExpand(StepNode sn, SolutionBuilder sb) {
		return defaultExpand(sn, sb, new RegroupTracker());
	}

	public static StepNode defaultFactor(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
		return FactorSteps.DEFAULT_FACTOR.apply(sn, sb, tracker);
	}

	public static StepNode defaultFactor(StepNode sn, SolutionBuilder sb) {
		return defaultFactor(sn, sb, new RegroupTracker());
	}

	public static StepNode defaultDifferentiate(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
		return DifferentiationSteps.DEFAULT_DIFFERENTIATE.apply(sn, sb, tracker);
	}

	public static StepNode defaultDifferentiate(StepNode sn, SolutionBuilder sb) {
		return defaultDifferentiate(sn, sb, new RegroupTracker());
	}

	public static StepNode implementStrategy(StepNode sn, SolutionBuilder sb, SimplificationStepGenerator[] strategy,
											 RegroupTracker tracker) {
		final boolean printDebug = false;

		SolutionBuilder changes = new SolutionBuilder();
		StepNode newSn;

		for (SimplificationStepGenerator simplificationStep : strategy) {
			newSn = simplificationStep.apply(sn, changes, tracker);

			if (printDebug) {
				if (tracker.wasChanged()) {
					System.out.println("changed at " + simplificationStep);
					System.out.println("from: " + sn);
					System.out.println("to: " + newSn);
				}
			}

			if (tracker.wasChanged()) {
				if (sb != null) {
					if (simplificationStep.isGroupType()) {
						sb.addAll(changes.getSteps());
					} else {
						sb.addSubsteps(sn, newSn, changes);
					}
				}

				tracker.resetTracker();
				newSn.cleanColors();
				return newSn;
			}
		}

		return sn;
	}

	public static StepNode implementStrategy(StepNode sn, SolutionBuilder sb, SimplificationStepGenerator[] strategy) {
		return implementStrategy(sn, sb, strategy, new RegroupTracker());
	}

	public static StepNode implementGroup(StepNode sn, SolutionStepType groupHeader, SimplificationStepGenerator[]
			strategy, SolutionBuilder sb, RegroupTracker tracker) {
		SolutionBuilder tempSteps = new SolutionBuilder();

		String old, current = null;
		StepNode result = sn;
		do {
			result = StepStrategies.implementStrategy(result, tempSteps, strategy, tracker);

			old = current;
			current = result.toString();
		} while (!current.equals(old));

		if (!result.equals(sn)) {
			if (sb != null && groupHeader != null) {
				sb.addGroup(groupHeader, tempSteps, result);
			} else if (sb != null) {
				sb.addAll(tempSteps.getSteps());
			}

			tracker.incColorTracker();
			result.cleanColors();
			return result;
		}

		return sn;
	}

	public static StepNode defaultSolve(StepEquation se, StepVariable sv, SolutionBuilder sb, SolveTracker tracker) {
		SolveStepGenerator[] strategy = {
				EquationSteps.SOLVE_PRODUCT,
				EquationSteps.REGROUP,
				EquationSteps.FACTOR,
				EquationSteps.SUBTRACT_COMMON,
				EquationSteps.PLUSMINUS,
				EquationSteps.SOLVE_LINEAR,
				EquationSteps.TAKE_ROOT,
				EquationSteps.RECIPROCATE_EQUATION,
				EquationSteps.SOLVE_LINEAR_IN_EXPRESSION,
				EquationSteps.COMMON_DENOMINATOR,
				EquationSteps.MULTIPLY_THROUGH,
				EquationSteps.EXPAND,
				EquationSteps.SOLVE_QUADRATIC,
				EquationSteps.COMPLETE_CUBE,
				EquationSteps.REDUCE_TO_QUADRATIC,
				EquationSteps.SOLVE_ABSOLUTE_VALUE,
				EquationSteps.SOLVE_IRRATIONAL,
				EquationSteps.SIMPLIFY_TRIGONOMETRIC,
				EquationSteps.SOLVE_QUADRATIC_IN_EXPRESSION,
				EquationSteps.SOLVE_SIMPLE_TRIGONOMETRIC,
				EquationSteps.DIFF
		};

		return implementSolveStrategy(se, sv, sb, strategy, tracker);
	}

	public static StepNode defaultInequalitySolve(StepInequality se, StepVariable sv, SolutionBuilder sb,
												  SolveTracker tracker) {
		SolveStepGenerator[] strategy = { EquationSteps.REGROUP, EquationSteps.SUBTRACT_COMMON,
				EquationSteps.SOLVE_LINEAR, EquationSteps.EXPAND
		};

		return implementSolveStrategy(se, sv, sb, strategy, tracker);
	}

	public static StepNode implementSolveStrategy(StepSolvable se, StepVariable variable, SolutionBuilder sb,
			SolveStepGenerator[] strategy, SolveTracker tracker) {
		final boolean printDebug = true;

		SolutionBuilder changes = new SolutionBuilder();

		if (sb != null) {
			sb.add(SolutionStepType.GROUP_WRAPPER);
			sb.levelDown();

			if (tracker.getRestriction().equals(StepInterval.R)) {
				sb.add(SolutionStepType.SOLVE_FOR, se, variable);
			} else {
				sb.add(SolutionStepType.SOLVE_IN, se, tracker.getRestriction());
			}

			sb.levelDown();
		}

		StepNode result = se;
		String old, current = null;
		do {
			boolean changed = false;
			for (int i = 0; i < strategy.length && !changed; i++) {
				result = strategy[i].apply((StepSolvable) result.deepCopy(), variable, changes, tracker);

				if (printDebug) {
					if (changes.getSteps() != null) {
						System.out.println("changed at " + strategy[i]);
						System.out.println("to: " + result);
					}
				}

				if (changes.getSteps().getSubsteps() != null || result instanceof StepSet) {
					if (sb != null) {
						sb.addAll(changes.getSteps());
					}

					result.cleanColors();
					changes.reset();
					changed = true;
				}
			}

			old = current;
			current = result.toString();
		} while (!(result instanceof StepSet) && !current.equals(old));

		if (result instanceof StepSet) {
			StepSet finalSolutions = EquationSteps.checkSolutions(se, (StepSet) result, changes, tracker);

			if (sb != null) {
				sb.levelUp();
				sb.addAll(changes.getSteps());
				sb.levelUp();
			}

			return finalSolutions;
		}

		throw new SolveFailedException(sb.getSteps());
	}

	public static StepNode iterateThrough(SimplificationStepGenerator step, StepNode sn, SolutionBuilder sb,
			RegroupTracker tracker) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			int colorsAtStart = tracker.getColorTracker();

			StepOperation toReturn = null;
			for (int i = 0; i < so.noOfOperands(); i++) {
				StepExpression a = (StepExpression) step.apply(so.getOperand(i), sb, tracker);
				if (toReturn == null && tracker.getColorTracker() > colorsAtStart) {
					toReturn = new StepOperation(so.getOperation());

					for (int j = 0; j < i; j++) {
						toReturn.addOperand(so.getOperand(j));
					}
				}
				if (toReturn != null) {
					toReturn.addOperand(a);
				}
			}

			if (toReturn == null) {
				return so;
			}

			return toReturn;
		} else if (sn instanceof StepSolvable) {
			StepSolvable se = (StepSolvable) sn;

			StepExpression newLHS = (StepExpression) step.apply(se.getLHS(), sb, tracker);
			StepExpression newRHS = (StepExpression) step.apply(se.getRHS(), sb, tracker);

			StepSolvable result = se.deepCopy();
			result.modify(newLHS, newRHS);

			return result;
		}

		return sn;
	}
}
