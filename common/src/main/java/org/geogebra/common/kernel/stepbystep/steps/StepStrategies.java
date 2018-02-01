package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.common.util.debug.Log;

public class StepStrategies {

	public static StepNode defaultRegroup(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
		SimplificationStepGenerator[] defaultStrategy = new SimplificationStepGenerator[] {
				RegroupSteps.CALCULATE_INVERSE_TRIGO,
				RegroupSteps.ELIMINATE_OPPOSITES,
				RegroupSteps.DISTRIBUTE_MINUS,
				RegroupSteps.DOUBLE_MINUS,
				RegroupSteps.POWER_OF_NEGATIVE,
				RegroupSteps.DISTRIBUTE_ROOT_OVER_FRACTION,
				RegroupSteps.DISTRIBUTE_POWER_OVER_PRODUCT,
				RegroupSteps.DISTRIBUTE_POWER_OVER_FRACION,
				RegroupSteps.EXPAND_ROOT,
				RegroupSteps.COMMON_ROOT,
				RegroupSteps.SIMPLIFY_ROOTS,
				RegroupSteps.NEGATIVE_FRACTIONS,
				RegroupSteps.TRIVIAL_FRACTIONS,
				RegroupSteps.REWRITE_COMPLEX_FRACTIONS,
				RegroupSteps.COMMON_FRACTION,
				RegroupSteps.REGROUP_SUMS,
				RegroupSteps.REGROUP_PRODUCTS,
				RegroupSteps.SIMPLE_POWERS,
				RegroupSteps.SIMPLIFY_FRACTIONS,
				RegroupSteps.FACTOR_FRACTIONS_SUBSTEP,
				ExpandSteps.EXPAND_PRODUCTS,
				RegroupSteps.RATIONALIZE_DENOMINATORS,
				FractionSteps.ADD_FRACTIONS
		};

		StepNode result = sn;
		String old, current = null;
		do {
			result = implementStrategy(result, sb, defaultStrategy, tracker);
			old = current;
			current = result.toString();
		} while (!current.equals(old));

		return result;
	}

	public static StepNode defaultRegroup(StepNode sn, SolutionBuilder sb) {
		return defaultRegroup(sn, sb, new RegroupTracker());
	}

	public static StepNode weakRegroup(StepNode sn, SolutionBuilder sb) {
		SimplificationStepGenerator[] weakStrategy = new SimplificationStepGenerator[] {
				RegroupSteps.CALCULATE_INVERSE_TRIGO,
				RegroupSteps.SIMPLIFY_POWER_OF_ROOT,
				RegroupSteps.SIMPLE_POWERS,
				RegroupSteps.SIMPLE_ROOTS,
				RegroupSteps.ELIMINATE_OPPOSITES,
				RegroupSteps.NEGATIVE_FRACTIONS,
				RegroupSteps.DOUBLE_MINUS,
				RegroupSteps.TRIVIAL_FRACTIONS,
				RegroupSteps.REWRITE_COMPLEX_FRACTIONS,
				RegroupSteps.COMMON_FRACTION,
				RegroupSteps.SIMPLIFY_FRACTIONS,
				RegroupSteps.DISTRIBUTE_POWER_OVER_PRODUCT,
				RegroupSteps.MULTIPLY_NEGATIVES,
				RegroupSteps.REGROUP_SUMS,
				RegroupSteps.REGROUP_PRODUCTS,
				RegroupSteps.POWER_OF_NEGATIVE
		};

		StepNode result = sn;
		String old, current = null;
		do {
			result = implementStrategy(result, sb, weakStrategy);
			old = current;
			current = result.toString();
		} while (!current.equals(old));

		return result;
	}


	public static StepNode defaultExpand(StepNode sn, SolutionBuilder sb) {
		SimplificationStepGenerator[] expandStrategy = new SimplificationStepGenerator[] {
				ExpandSteps.EXPAND_DIFFERENCE_OF_SQUARES,
				ExpandSteps.EXPAND_POWERS,
				ExpandSteps.EXPAND_PRODUCTS
		};

		StepNode result = sn;
		String old, current = null;
		do {
			result = defaultRegroup(result, sb);
			result = implementStrategy(result, sb, expandStrategy, new RegroupTracker().setStrongExpand());
			old = current;
			current = result.toString();
		} while (!current.equals(old));

		return result;
	}

	public static StepNode defaultFactor(StepNode sn, SolutionBuilder sb, RegroupTracker tracker, boolean withRegroup) {
		SimplificationStepGenerator[] defaultStrategy = new SimplificationStepGenerator[] {
				FactorSteps.FACTOR_COMMON,
				FactorSteps.FACTOR_INTEGER,
				FactorSteps.FACTOR_BINOM_STRATEGY,
				FactorSteps.FACTOR_BINOM_CUBED,
				FactorSteps.FACTOR_USING_FORMULA,
				FactorSteps.FACTOR_POLYNOMIALS
		};

		StepNode result = sn;
		String old, current = null;

		do {
			result = implementStrategy(result, sb, defaultStrategy, tracker);
			if (withRegroup) {
				result = weakRegroup(result, sb);
			}

			old = current;
			current = result.toString();
		} while (!current.equals(old));

		return result;
	}

	public static StepNode defaultFactor(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
		return defaultFactor(sn, sb, tracker, true);
	}

	public static StepNode defaultDifferentiate(StepNode sn, SolutionBuilder sb) {
		SimplificationStepGenerator[] defaultStrategy = new SimplificationStepGenerator[] {
				DifferentiationSteps.CONSTANT_COEFFICIENT,
				DifferentiationSteps.DIFFERENTIATE_SUM,
				DifferentiationSteps.CONSTANT_COEFFICIENT,
				DifferentiationSteps.DIFFERENTIATE_FRACTION,
				DifferentiationSteps.DIFFERENTIATE_POLYNOMIAL,
				DifferentiationSteps.DIFFERENTIATE_EXPONENTIAL,
				DifferentiationSteps.DIFFERENTIATE_PRODUCT,
				DifferentiationSteps.DIFFERENTIATE_ROOT,
				DifferentiationSteps.DIFFERENTIATE_TRIGO,
				DifferentiationSteps.DIFFERENTIATE_LOG,
				DifferentiationSteps.DIFFERENTIATE_INVERSE_TRIGO
		};

		StepNode result = sn;
		String old, current = null;
		do {
			result = defaultRegroup(result, sb);
			result = implementStrategy(result, sb, defaultStrategy);
			old = current;
			current = result.toString();
		} while (!current.equals(old));

		return result;
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
					Log.error("changed at " + simplificationStep);
					Log.error("from: " + sn);
					Log.error("to: " + newSn);
				}
			}

			if (tracker.wasChanged()) {
				if (sb != null) {
					if (simplificationStep.type() == 0) { // group type
						sb.add(SolutionStepType.SUBSTEP_WRAPPER);
						sb.levelDown();
						sb.add(SolutionStepType.EQUATION, sn.deepCopy());
						sb.addAll(changes.getSteps());
						sb.add(SolutionStepType.EQUATION, newSn.deepCopy());
						sb.levelUp();
					} else if (simplificationStep.type() == 1) { // substep type
						sb.add(SolutionStepType.GROUP_WRAPPER);
						sb.levelDown();
						sb.addAll(changes.getSteps());
						sb.add(SolutionStepType.EQUATION, newSn.deepCopy());
						sb.levelUp();
					} else { // strategy type
						sb.addAll(changes.getSteps());
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
				sb.add(groupHeader);
				sb.levelDown();
				sb.addAll(tempSteps.getSteps());
				sb.levelUp();
			} else if (sb != null) {
				sb.addAll(tempSteps.getSteps());
			}

			tracker.incColorTracker();
			result.cleanColors();
			return result;
		}

		return sn;
	}

	public static StepNode defaultSolve(StepEquation se, StepVariable sv, SolutionBuilder sb) {
		SolveStepGenerator[] strategy = {
				EquationSteps.SOLVE_PRODUCT,
				EquationSteps.REGROUP,
				EquationSteps.FACTOR,
				EquationSteps.SUBTRACT_COMMON,
				EquationSteps.PLUSMINUS,
				EquationSteps.SOLVE_LINEAR,
				EquationSteps.TAKE_ROOT,
				EquationSteps.RECIPROCATE_EQUATION,
				EquationSteps.SOLVE_LINEAR_IN_INVERSE,
				EquationSteps.COMMON_DENOMINATOR,
				EquationSteps.MULTIPLY_THROUGH,
				EquationSteps.EXPAND,
				EquationSteps.SOLVE_QUADRATIC,
				EquationSteps.COMPLETE_CUBE,
				EquationSteps.REDUCE_TO_QUADRATIC,
				EquationSteps.SOLVE_ABSOLUTE_VALUE,
				EquationSteps.SOLVE_IRRATIONAL,
				EquationSteps.SIMPLIFY_TRIGONOMETRIC,
				EquationSteps.SOLVE_QUADRATIC_TRIGONOMETRIC,
				EquationSteps.SOLVE_LINEAR_TRIGONOMETRC,
				EquationSteps.SOLVE_SIMPLE_TRIGONOMETRIC,
				EquationSteps.DIFF
		};

		return implementSolveStrategy(se, sv, sb, strategy);
	}

	public static StepNode defaultInequalitySolve(StepInequality se, StepVariable sv, SolutionBuilder sb) {
		SolveStepGenerator[] strategy = { EquationSteps.REGROUP, EquationSteps.SUBTRACT_COMMON,
				EquationSteps.SOLVE_LINEAR, EquationSteps.EXPAND
		};

		return implementSolveStrategy(se, sv, sb, strategy);
	}

	public static StepNode implementSolveStrategy(StepSolvable se, StepVariable variable, SolutionBuilder sb,
			SolveStepGenerator[] strategy) {
		final boolean printDebug = true;

		SolutionBuilder changes = new SolutionBuilder();

		if (sb != null) {
			sb.add(SolutionStepType.GROUP_WRAPPER);
			sb.levelDown();

			if (se.getRestriction().equals(StepInterval.R)) {
				sb.add(SolutionStepType.SOLVE_FOR, se, variable);
			} else {
				sb.add(SolutionStepType.SOLVE_IN, se, se.getRestriction());
			}

			sb.levelDown();
		}

		StepNode result = se;
		String old, current = null;
		do {
			boolean changed = false;
			for (int i = 0; i < strategy.length && !changed; i++) {
				result = strategy[i].apply((StepSolvable) result.deepCopy(), variable, changes);

				if (printDebug) {
					if (changes.getSteps() != null) {
						Log.error("changed at " + strategy[i]);
						Log.error("to: " + result);
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
			StepSet finalSolutions = EquationSteps.checkSolutions(se, (StepSet) result, variable, changes);

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
