package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.*;

import java.util.ArrayList;
import java.util.List;

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

	public static StepNode implementGroup(StepNode sn, SolutionStepType groupHeader, SimplificationStepGenerator[]
			strategy, SolutionBuilder sb, RegroupTracker tracker) {
		final boolean printDebug = false;

		SolutionBuilder changes = new SolutionBuilder();
		SolutionBuilder substeps = new SolutionBuilder();
		sn.cleanColors();

		StepNode current = null, old = sn;
		do {
			tracker.resetTracker();
			for (SimplificationStepGenerator simplificationStep : strategy) {
				current = simplificationStep.apply(old, changes, tracker);

				if (printDebug) {
					if (tracker.wasChanged()) {
						System.out.println("changed at " + simplificationStep);
						System.out.println("from: " + old);
						System.out.println("to: " + current);
					} else {
						//System.out.println("unchanged at " + simplificationStep);
					}
				}

				if (tracker.wasChanged()) {
					if (simplificationStep.isGroupType()) {
						substeps.addAll(changes.getSteps());
					} else {
						substeps.addSubsteps(old, current, changes);
					}

					old = current;

					changes.reset();
					break;
				}
			}
		} while (tracker.wasChanged2());

		if (!sn.equals(current)) {
			if (sb != null && groupHeader != null) {
				sb.addGroup(groupHeader, substeps, current);
			} else if (sb != null) {
				sb.addAll(substeps.getSteps());
			}

			tracker.incColorTracker();
			return current;
		}

		return sn;
	}

	public static List<StepSolution> defaultSolve(StepEquation se, StepVariable sv, SolutionBuilder sb, SolveTracker
			tracker) {
		SolveStepGenerator[] strategy = {
				EquationSteps.FIND_DEFINED_RANGE,
				EquationSteps.SOLVE_PRODUCT,
				EquationSteps.NEGATE_BOTH_SIDES,
				EquationSteps.REGROUP,
				EquationSteps.TRIVIAL_EQUATIONS,
				EquationSteps.SEPARATE_PLUSMINUS,
				EquationSteps.FACTOR,
				EquationSteps.SUBTRACT_COMMON,
				EquationSteps.SOLVE_SIMPLE_ABSOLUTE_VALUE,
				EquationSteps.SOLVE_LINEAR,
				EquationSteps.TAKE_ROOT,
				EquationSteps.RECIPROCATE_EQUATION,
				EquationSteps.SOLVE_LINEAR_IN_EXPRESSION,
				EquationSteps.COMMON_DENOMINATOR,
				EquationSteps.MULTIPLY_THROUGH,
				EquationSteps.EXPAND,
                EquationSteps.COMPLETE_THE_SQUARE,
				EquationSteps.COMPLETE_CUBE,
				EquationSteps.SOLVE_ABSOLUTE_VALUE,
				EquationSteps.SOLVE_IRRATIONAL,
				EquationSteps.SIMPLIFY_TRIGONOMETRIC,
				EquationSteps.SOLVE_QUADRATIC_IN_EXPRESSION,
				EquationSteps.SOLVE_SIMPLE_TRIGONOMETRIC,
				EquationSteps.DIFF,
				EquationSteps.SOLVE_QUADRATIC,
				EquationSteps.REDUCE_TO_QUADRATIC,
		};

		return implementSolveStrategy(se, sv, sb, strategy, tracker);
	}

	public static List<StepSolution> defaultInequalitySolve(StepInequality se, StepVariable sv, SolutionBuilder sb,
												  SolveTracker tracker) {
		SolveStepGenerator[] strategy = {
				InequalitySteps.DIVIDE_BY_COEFFICIENT,
				EquationSteps.SOLVE_LINEAR,
				EquationSteps.REGROUP,
				InequalitySteps.TRIVIAL_INEQUALITY,
				InequalitySteps.POSITIVE_AND_ZERO,
                InequalitySteps.POSITIVE_AND_NEGATIVE,
				EquationSteps.SUBTRACT_COMMON,
				InequalitySteps.FACTOR,
				InequalitySteps.RATIONAL_INEQUALITY,
				EquationSteps.DIFF,
                EquationSteps.COMPLETE_THE_SQUARE,
				InequalitySteps.SOLVE_QUADRATIC,
				EquationSteps.TAKE_ROOT,
				EquationSteps.EXPAND,
		};

		return implementSolveStrategy(se, sv, sb, strategy, tracker);
	}

	public static List<StepSolution> implementSolveStrategy(StepSolvable se, StepVariable variable, SolutionBuilder sb,
			SolveStepGenerator[] strategy, SolveTracker tracker) {
		final boolean printDebug = false;

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

		StepSolvable equation = se.deepCopy();

		List<StepSolution> result = null;
		boolean changed;
		do {
			changed = false;
			for (int i = 0; i < strategy.length && !changed; i++) {
				result = strategy[i].apply(equation, variable, changes, tracker);

				if (printDebug) {
					if (changes.getSteps() != null) {
						System.out.println("changed at " + strategy[i]);
						System.out.println("to: " + equation);
					}
				}

				if (changes.getSteps().getSubsteps() != null || result != null) {
					if (sb != null) {
						sb.addAll(changes.getSteps());
					}

					changes.reset();
					changed = true;
				}
			}
		} while (result == null && changed);

		if (result != null) {
			List<StepSolution> finalSolutions = EquationSteps.checkSolutions(se, result, changes, tracker);

			if (sb != null) {
				sb.addAll(changes.getSteps());
				sb.levelUp();

				if (finalSolutions.size() == 0) {
					sb.add(SolutionStepType.NO_REAL_SOLUTION);
					return new ArrayList<>();
				} else if (finalSolutions.size() == 1) {
					sb.add(SolutionStepType.SOLUTION, finalSolutions.toArray(new StepSolution[] {}));
				} else {
					sb.add(SolutionStepType.SOLUTIONS, finalSolutions.toArray(new StepSolution[] {}));
				}
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

			StepExpression[] toReturn = null;
			for (int i = 0; i < so.noOfOperands(); i++) {
				StepExpression a = (StepExpression) step.apply(so.getOperand(i), sb, tracker);
				if (a.isUndefined()) {
					return a;
				}

				if (toReturn == null && tracker.getColorTracker() > colorsAtStart) {
					toReturn = new StepExpression[so.noOfOperands()];

					for (int j = 0; j < i; j++) {
						toReturn[j] = so.getOperand(j);
					}
				}
				if (toReturn != null) {
					toReturn[i] = a;
				}
			}

			if (toReturn == null) {
				return so;
			}

			return new StepOperation(so.getOperation(), toReturn);
		} else if (sn instanceof StepSolvable) {
			StepSolvable se = (StepSolvable) sn;

			StepExpression newLHS = (StepExpression) step.apply(se.getLHS(), sb, tracker);
			StepExpression newRHS = (StepExpression) step.apply(se.getRHS(), sb, tracker);

			return se.cloneWith(newLHS, newRHS);
		} else if (sn instanceof StepMatrix) {
			StepMatrix sm = (StepMatrix) sn;

			StepMatrix result = sm.deepCopy();
			for (int i = 0; i < sm.getHeight(); i++) {
				for (int j = 0; j < sm.getWidth(); j++) {
					StepExpression elem = (StepExpression) step.apply(sm.get(i, j), sb, tracker);
					result.set(i, j, elem);
				}
			}

			return result;
		}

		return sn;
	}
}
