package org.geogebra.common.kernel.stepbystep.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquation;
import org.geogebra.common.kernel.stepbystep.steptree.StepInequality;
import org.geogebra.common.kernel.stepbystep.steptree.StepInterval;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolution;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolvable;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.util.debug.Log;

public class StepStrategies {

	public static StepTransformable convertToFraction(StepTransformable sn, SolutionBuilder sb) {
		return FractionSteps.CONVERT_DECIMAL_TO_FRACTION.apply(sn, sb, new RegroupTracker());
	}

	public static StepTransformable regroupSums(StepTransformable sn, SolutionBuilder sb) {
		return RegroupSteps.REGROUP_SUMS.apply(sn, sb, new RegroupTracker());
	}

	public static StepTransformable weakRegroup(StepTransformable sn, SolutionBuilder sb) {
		return RegroupSteps.WEAK_REGROUP.apply(sn, sb, new RegroupTracker());
	}

	public static StepTransformable decimalRegroup(StepTransformable sn, SolutionBuilder sb) {
		return RegroupSteps.DECIMAL_REGROUP.apply(sn, sb, new RegroupTracker());
	}

	public static StepTransformable defaultRegroup(StepTransformable sn, SolutionBuilder sb) {
		return RegroupSteps.DEFAULT_REGROUP.apply(sn, sb, new RegroupTracker());
	}

	public static StepSolvable solverRegroup(StepSolvable ss, SolutionBuilder sb) {
		return (StepSolvable) RegroupSteps.SOLVER_DEFAULT.apply(ss, sb, new RegroupTracker());
	}

	public static StepSolvable solverDecimalRegroup(StepSolvable ss, SolutionBuilder sb) {
		return (StepSolvable) RegroupSteps.SOLVER_DECIMAL.apply(ss, sb, new RegroupTracker());
	}

	public static StepTransformable addFractions(StepTransformable sn, SolutionBuilder sb) {
		return FractionSteps.ADD_FRACTIONS.apply(sn, sb, new RegroupTracker());
	}

	public static StepTransformable decimalExpand(StepTransformable sn, SolutionBuilder sb) {
		return ExpandSteps.DECIMAL_EXPAND.apply(sn, sb, new RegroupTracker());
	}

	public static StepTransformable defaultExpand(StepTransformable sn, SolutionBuilder sb) {
		return ExpandSteps.DEFAULT_EXPAND.apply(sn, sb, new RegroupTracker());
	}

	public static StepTransformable defaultFactor(StepTransformable sn, SolutionBuilder sb) {
		return FactorSteps.DEFAULT_FACTOR.apply(sn, sb, new RegroupTracker());
	}

	public static StepTransformable weakFactor(StepTransformable sn, SolutionBuilder sb) {
		return FactorSteps.WEAK_FACTOR.apply(sn, sb, new RegroupTracker());
	}

	public static StepTransformable defaultDifferentiate(StepTransformable sn, SolutionBuilder sb) {
		return DifferentiationSteps.DEFAULT_DIFFERENTIATE.apply(sn, sb, new RegroupTracker());
	}

	static StepTransformable implementGroup(StepTransformable sn, SolutionStepType groupHeader,
			SimplificationStepGenerator[] strategy, SolutionBuilder sb, RegroupTracker tracker) {
		final boolean printDebug = true;

		SolutionBuilder changes = new SolutionBuilder();
		SolutionBuilder substeps = new SolutionBuilder();

		StepTransformable current = null, old = sn;
		do {
			tracker.resetTracker();
			for (SimplificationStepGenerator simplificationStep : strategy) {
				current = simplificationStep.apply(old, changes, tracker);
				Log.debug(simplificationStep);

				if (tracker.stepAdded()) {
					if (printDebug) {
						Log.debug("changed at " + simplificationStep);
						Log.debug("from: " + old);
						Log.debug("to: " + current);
					}

					if (simplificationStep.isGroupType()) {
						substeps.addAll(changes.getSteps());
					} else {
						substeps.addSubsteps(old, current, changes);
					}
				}

				if (tracker.wasChanged()) {
					old = current;

					old.cleanColors();
					changes.reset();
					break;
				}
			}
		} while (tracker.wasChanged());

		sn.cleanColors();
		if (!sn.equals(current)) {
			current.cleanColors();

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

	static class CacheEntry {
		private StepTransformable result;
		private SolutionStep steps;
	}

	static StepTransformable implementCachedGroup(Map<StepTransformable, CacheEntry> cache,
			StepTransformable sn, SolutionStepType groupHeader,
			SimplificationStepGenerator[] strategy, SolutionBuilder sb, RegroupTracker tracker) {
		CacheEntry entry = cache.get(sn);

		if (entry == null) {
			SolutionBuilder tempSteps = new SolutionBuilder();
			entry = new CacheEntry();

			entry.result = implementGroup(sn, groupHeader, strategy, tempSteps, tracker);
			entry.steps = tempSteps.getSteps();

			cache.put(sn, entry);
		}

		if (sb != null) {
			sb.addAll(entry.steps);
		}
		return entry.result;
	}

	public static List<StepSolution> defaultSolve(StepEquation se, StepVariable sv,
			SolutionBuilder sb, SolveTracker tracker) {
		SolveStepGenerator[] strategy = {
				SolveSteps.FIND_DEFINED_RANGE,
				SolveSteps.CONVERT_OR_SET_APPROXIMATE,
				EquationSteps.NEGATE_BOTH_SIDES,
				SolveSteps.REGROUP,
				EquationSteps.PRODUCT_IS_ZERO,
				EquationSteps.FRACTION_IS_ZERO,
				EquationSteps.TRIVIAL_EQUATIONS,
				EquationSteps.SEPARATE_PLUSMINUS,
				SolveSteps.SIMPLIFY_FRACTIONS,
				SolveSteps.FACTOR,
				SolveSteps.SUBTRACT_COMMON,
				EquationSteps.SOLVE_SIMPLE_ABSOLUTE_VALUE,
				SolveSteps.SOLVE_LINEAR,
				SolveSteps.TAKE_ROOT,
				EquationSteps.RECIPROCATE_EQUATION,
				SolveSteps.SOLVE_LINEAR_IN_EXPRESSION,
				SolveSteps.COMMON_DENOMINATOR,
				EquationSteps.MULTIPLY_THROUGH,
				SolveSteps.EXPAND,
				SolveSteps.COMPLETE_THE_SQUARE,
				EquationSteps.COMPLETE_CUBE,
				EquationSteps.SOLVE_ABSOLUTE_VALUE,
				EquationSteps.RAISE_TO_POWER,
				EquationSteps.SOLVE_SQUARE_ROOTS,
				EquationSteps.SIMPLIFY_TRIGONOMETRIC,
				EquationSteps.SOLVE_QUADRATIC_IN_EXPRESSION,
				EquationSteps.SOLVE_SIMPLE_TRIGONOMETRIC,
				EquationSteps.SOLVE_SIMPLE_INVERSE_TRIGONOMETRIC,
				SolveSteps.DIFF,
				EquationSteps.SOLVE_QUADRATIC,
				EquationSteps.REDUCE_TO_QUADRATIC,
		};

		return implementSolveStrategy(se, sv, sb, strategy, tracker);
	}

	public static List<StepSolution> defaultInequalitySolve(StepInequality se, StepVariable sv,
			SolutionBuilder sb, SolveTracker tracker) {
		SolveStepGenerator[] strategy = {
				SolveSteps.FIND_DEFINED_RANGE,
				SolveSteps.CONVERT_OR_SET_APPROXIMATE,
				SolveSteps.REGROUP,
				InequalitySteps.TRIVIAL_INEQUALITY,
				InequalitySteps.DIVIDE_BY_COEFFICIENT,
				SolveSteps.SOLVE_LINEAR,
				InequalitySteps.POSITIVE_AND_ZERO,
				InequalitySteps.POSITIVE_AND_NEGATIVE,
				SolveSteps.SUBTRACT_COMMON,
				InequalitySteps.RATIONAL_INEQUALITY,
				SolveSteps.FACTOR,
				SolveSteps.COMMON_DENOMINATOR,
				SolveSteps.DIFF,
				SolveSteps.COMPLETE_THE_SQUARE,
				InequalitySteps.SOLVE_QUADRATIC,
				SolveSteps.TAKE_ROOT,
				SolveSteps.EXPAND, 
		};

		return implementSolveStrategy(se, sv, sb, strategy, tracker);
	}

	static List<StepSolution> implementSolveStrategy(StepSolvable se, StepVariable variable,
			SolutionBuilder sb, SolveStepGenerator[] strategy, SolveTracker tracker) {
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

		StepSolvable equation = se;

		List<StepSolution> solutions = null;
		boolean changed;
		int counter = 0;
		do {
			changed = false;
			for (int i = 0; i < strategy.length && !changed; i++) {
				Result result = strategy[i].apply(equation, variable, changes, tracker);

				if (result != null) {
					if (printDebug) {
						Log.debug("changed at " + strategy[i]);
						Log.debug("to: " + result);
					}

					if (result.getSolutions() != null) {
						if (sb != null) {
							sb.addAll(changes.getSteps());
						}

						changes.reset();
						solutions = result.getSolutions();
						changed = true;
					} else {
						changed = !equation.equals(result.getSolvable());
						equation = result.getSolvable();
					}
				}
			}
		} while (solutions == null && changed && counter++ < 10);

		if (solutions != null) {
			List<StepSolution> finalSolutions =
					EquationSteps.checkSolutions(se, solutions, changes, tracker);

			if (sb != null) {
				sb.addAll(changes.getSteps());
				sb.levelUp();

				if (finalSolutions.size() == 0) {
					sb.add(SolutionStepType.NO_REAL_SOLUTION);
					return new ArrayList<>();
				} else if (finalSolutions.size() == 1) {
					sb.add(SolutionStepType.SOLUTION, finalSolutions.toArray(new StepSolution[]{}));
				} else {
					sb.add(SolutionStepType.SOLUTIONS,
							finalSolutions.toArray(new StepSolution[]{}));
				}
				sb.levelUp();
			}

			return finalSolutions;
		}

		throw new SolveFailedException(sb.getSteps());
	}

}
