package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquation;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepSet;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.util.debug.Log;

public class StepStrategies {

	public static StepNode defaultRegroup(StepNode sn, SolutionBuilder sb) {
		RegroupSteps[] defaultStrategy = new RegroupSteps[] { RegroupSteps.CALCULATE_INVERSE_TRIGO,
				RegroupSteps.DISTRIBUTE_ROOT_OVER_FRACTION, RegroupSteps.EXPAND_ROOT, RegroupSteps.COMMON_ROOT,
				RegroupSteps.SIMPLIFY_POWERS_AND_ROOTS, RegroupSteps.SIMPLE_POWERS, RegroupSteps.SIMPLE_ROOTS,
				RegroupSteps.FACTOR_SQUARE, RegroupSteps.SIMPLIFY_POWERS_AND_ROOTS, RegroupSteps.ELIMINATE_OPPOSITES,
				RegroupSteps.DISTRIBUTE_MINUS, RegroupSteps.ELIMINATE_OPPOSITES, RegroupSteps.DOUBLE_MINUS,
				RegroupSteps.SIMPLIFY_FRACTIONS, RegroupSteps.COMMON_FRACTION,
				RegroupSteps.DISTRIBUTE_POWER_OVER_PRODUCT, RegroupSteps.REGROUP_PRODUCTS, RegroupSteps.REGROUP_SUMS,
				RegroupSteps.ADD_FRACTIONS, RegroupSteps.POWER_OF_NEGATIVE, RegroupSteps.RATIONALIZE_DENOMINATORS };

		StepNode result = sn;
		String old = null, current = null;
		do {
			result = implementStrategy(result, sb, defaultStrategy);
			old = current;
			current = result.toString();
		} while (!current.equals(old));

		return result;
	}

	public static StepNode defaultExpand(StepNode sn, SolutionBuilder sb) {
		SimplificationStepGenerator[] expandStrategy = new SimplificationStepGenerator[] { ExpandSteps.EXPAND_POWERS,
				ExpandSteps.EXPAND_PRODUCTS };

		StepNode result = sn;
		String old = null, current = null;
		do {
			result = defaultRegroup(result, sb);
			result = implementStrategy(result, sb, expandStrategy);
			old = current;
			current = result.toString();
		} while (!current.equals(old));

		return result;
	}

	public static StepNode defaultFactor(StepNode sn, SolutionBuilder sb) {
		SimplificationStepGenerator[] defaultStrategy = new SimplificationStepGenerator[] { FactorSteps.FACTOR_COMMON,
				FactorSteps.FACTOR_INTEGER, FactorSteps.COMPLETING_THE_SQUARE, FactorSteps.FACTOR_BINOM_CUBED,
				FactorSteps.FACTOR_BINOM_SQUARED, FactorSteps.FACTOR_BINOM_SQUARED, FactorSteps.FACTOR_USING_FORMULA,
				FactorSteps.REORGANIZE_POLYNOMIAL, FactorSteps.FACTOR_POLYNOMIAL };

		StepNode result = sn;
		String old = null, current = null;
		do {
			result = defaultRegroup(result, sb);
			result = implementStrategy(result, sb, defaultStrategy);
			old = current;
			current = result.toString();
		} while (!current.equals(old));

		return result;
	}

	public static StepNode implementStrategy(StepNode sn, SolutionBuilder sb, SimplificationStepGenerator[] strategy) {
		final boolean printDebug = true;

		int[] colorTracker = new int[] { 1 };
		SolutionBuilder changes = new SolutionBuilder(sb == null ? null : sb.getLocalization());

		StepNode origSn = sn, newSn;

		for (int i = 0; i < strategy.length; i++) {
			newSn = strategy[i].apply(origSn, changes, colorTracker);

			if (printDebug) {
				if (colorTracker[0] > 1) {
					Log.error("changed at " + strategy[i]);
					Log.error("from: " + origSn);
					Log.error("to: " + newSn);
				}
			}

			if (colorTracker[0] > 1) {
				if (sb != null) {
					sb.add(SolutionStepType.SUBSTEP_WRAPPER);
					sb.levelDown();
					sb.add(SolutionStepType.EQUATION, origSn.deepCopy());
					sb.addAll(changes.getSteps());
					sb.add(SolutionStepType.EQUATION, newSn.deepCopy());
					sb.levelUp();
				}

				newSn.cleanColors();
				colorTracker[0] = 1;
			}

			changes.reset();
			origSn = newSn;
		}

		return origSn;
	}

	public static StepNode defaultSolve(StepEquation se, StepVariable variable, SolutionBuilder sb) {
		SolveStepGenerator[] strategy = { EquationSteps.REGROUP, EquationSteps.SUBTRACT_COMMON, EquationSteps.PLUSMINUS,
				EquationSteps.RECIPROCATE_EQUATION, EquationSteps.SOLVE_LINEAR_IN_INVERSE,
				EquationSteps.COMMON_DENOMINATOR, EquationSteps.SOLVE_LINEAR, EquationSteps.TAKE_ROOT,
				EquationSteps.SOLVE_PRODUCT, EquationSteps.EXPAND, EquationSteps.SOLVE_QUADRATIC,
				EquationSteps.COMPLETE_CUBE, EquationSteps.REDUCE_TO_QUADRATIC, EquationSteps.SOLVE_ABSOLUTE_VALUE,
				EquationSteps.SOLVE_IRRATIONAL, EquationSteps.SOLVE_TRIGONOMETRIC,
				EquationSteps.SOLVE_SIMPLE_TRIGONOMETRIC };

		return implementSolveStrategy(se, variable, sb, strategy);
	}

	public static StepNode implementSolveStrategy(StepEquation se, StepVariable variable, SolutionBuilder sb,
			SolveStepGenerator[] strategy) {
		final boolean printDebug = true;
		final int maxRun = 10;

		SolutionBuilder accumulator = sb == null ? new SolutionBuilder(null) : sb;

		SolutionBuilder changes = new SolutionBuilder(sb == null ? null : sb.getLocalization());
		StepNode origSn = se, newSn;

		accumulator.add(SolutionStepType.SOLVE, se);
		accumulator.levelDown();
		for (int j = 0; j < maxRun; j++) {
			boolean changed = false;
			for (int i = 0; i < strategy.length && !changed; i++) {
				Log.error(strategy[i] + "");
				newSn = strategy[i].apply((StepEquation) origSn.deepCopy(), variable, changes);

				if (printDebug) {
					if (changes.getSteps().getSubsteps() != null) {
						Log.error("changed at " + strategy[i]);
						Log.error("from: " + origSn);
						Log.error("to: " + newSn);
					}
				}

				if (newSn instanceof StepSet) {
					accumulator.addAll(changes.getSteps());
					accumulator.levelUp();
					return EquationSteps.checkSolutions(se, (StepSet) newSn, variable, accumulator);
				}

				if (changes.getSteps().getSubsteps() != null) {
					accumulator.addAll(changes.getSteps());
					newSn.cleanColors();
					origSn = newSn;
					changes.reset();
					changed = true;
				}
			}
		}

		return new StepSet();
	}

	public static StepNode iterateThrough(SimplificationStepGenerator step, StepNode sn, SolutionBuilder sb,
			int[] colorTracker) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			int colorsAtStart = colorTracker[0];

			StepOperation toReturn = null;
			for (int i = 0; i < so.noOfOperands(); i++) {
				StepExpression a = (StepExpression) step.apply(so.getSubTree(i), sb, colorTracker);
				if (toReturn == null && colorTracker[0] > colorsAtStart) {
					toReturn = new StepOperation(so.getOperation());

					for (int j = 0; j < i; j++) {
						toReturn.addSubTree(so.getSubTree(j));
					}
				}
				if (toReturn != null) {
					toReturn.addSubTree(a);
				}
			}

			if (toReturn == null) {
				return so;
			}

			return toReturn;
		} else if (sn instanceof StepEquation) {
			StepEquation se = ((StepEquation) sn).deepCopy();

			StepExpression newLHS = (StepExpression) step.apply(se.getLHS(), sb, colorTracker);
			StepExpression newRHS = (StepExpression) step.apply(se.getRHS(), sb, colorTracker);

			se.modify(newLHS, newRHS);

			return se;
		}

		return sn;
	}
}
