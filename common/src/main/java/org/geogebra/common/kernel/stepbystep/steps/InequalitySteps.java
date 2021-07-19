package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isZero;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.power;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.subtract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.solution.SolutionTable;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquation;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepInequality;
import org.geogebra.common.kernel.stepbystep.steptree.StepInterval;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepSet;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolution;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolvable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.plugin.Operation;

enum InequalitySteps implements SolveStepGenerator<StepInequality> {

	TRIVIAL_INEQUALITY {
		@Override
		public Result apply(StepInequality si, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (si.LHS.equals(variable) && si.RHS.isConstantIn(variable)) {
				if (si.isLessThan()) {
					return new Result(StepSolution.simpleSolution(variable,
							new StepInterval(StepConstant.NEG_INF, si.RHS, false,
									!si.isStrong()), tracker));
				} else {
					return new Result(StepSolution.simpleSolution(variable,
							new StepInterval(si.RHS, StepConstant.POS_INF, !si.isStrong(),
									false), tracker));
				}
			}

			if (si.RHS.equals(variable) && si.LHS.isConstantIn(variable)) {
				if (si.isLessThan()) {
					return new Result(StepSolution.simpleSolution(variable,
							new StepInterval(si.LHS, StepConstant.POS_INF, !si.isStrong(),
									false), tracker));
				} else {
					return new Result(StepSolution.simpleSolution(variable,
							new StepInterval(StepConstant.NEG_INF, si.RHS, false,
									!si.isStrong()), tracker));
				}
			}

			if (si.LHS.equals(si.RHS)) {
				if (!si.isStrong()) {
					return new Result(StepSolution
							.simpleSolution(variable, tracker.getRestriction(), tracker));
				} else {
					return new Result();
				}
			}

			if (si.LHS.canBeEvaluated() && si.RHS.canBeEvaluated()) {
				if (si.isLessThan() == si.LHS.getValue() < si.RHS.getValue()) {
					return new Result(StepSolution
							.simpleSolution(variable, tracker.getRestriction(), tracker));
				} else {
					return new Result();
				}
			}

			return null;
		}
	},

	DIVIDE_BY_COEFFICIENT {
		@Override
		public Result apply(StepInequality ss, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (isZero(ss.RHS) && !isZero(ss.LHS.getCoefficient())) {
				StepExpression coefficient = ss.LHS.getCoefficient();
				coefficient.setColor(1);

				StepSolvable result = ss.cloneWith(ss.LHS.getVariable(), ss.RHS);
				steps.addSubstep(ss, result, SolutionStepType.DIVIDE_BOTH_SIDES, coefficient);

				return new Result(result);
			}

			return null;
		}
	},

	POSITIVE_AND_NEGATIVE {
		@Override
		public Result apply(StepInequality si, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			List<StepSolution> solutions = new ArrayList<>();

			if (si.LHS.sign() > 0 && si.RHS.sign() < 0) {
				if (si.isLessThan()) {
					steps.add(SolutionStepType.POSITIVE_L_NEGATIVE, variable);
				} else {
					steps.add(SolutionStepType.POSTIVE_G_NEGATIVE, variable);
					solutions.add(StepSolution
							.simpleSolution(variable, tracker.getRestriction(), tracker));
				}

				return new Result(solutions);
			}

			if (si.LHS.sign() < 0 && si.RHS.sign() > 0) {
				if (si.isLessThan()) {
					steps.add(SolutionStepType.NEGATIVE_L_POSITIVE, variable);
					solutions.add(StepSolution
							.simpleSolution(variable, tracker.getRestriction(), tracker));
				} else {
					steps.add(SolutionStepType.NEGATIVE_G_POSITIVE, variable);
				}

				return new Result(solutions);
			}

			return null;
		}
	},

	POSITIVE_AND_ZERO {
		@Override
		public Result apply(StepInequality si, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			List<StepSolution> solutions = new ArrayList<>();

			if (si.LHS.sign() > 0 && isZero(si.RHS)) {
				if (si.isLessThan()) {
					if (si.isStrong()) {
						steps.add(SolutionStepType.POSITIVE_LT_ZERO, variable);
					} else {
						StepEquation equality = new StepEquation(si.LHS, si.RHS);
						steps.add(SolutionStepType.POSITIVE_LE_ZERO, equality);
						return new Result(equality.solve(variable, steps));
					}
				} else {
					if (si.isStrong()) {
						StepEquation equality = new StepEquation(si.LHS, si.RHS);
						steps.add(SolutionStepType.POSITIVE_GT_ZERO, equality);
						List<StepSolution> equalPoints = equality.solve(variable, steps);
						StepSet equalSet = new StepSet();
						for (StepSolution point : equalPoints) {
							equalSet.addElement((StepExpression) point.getValue());
						}
						solutions.add(StepSolution.simpleSolution(variable,
								subtract(tracker.getRestriction(), equalSet), tracker));
					} else {
						steps.add(SolutionStepType.POSITIVE_GE_ZERO, variable);
						solutions.add(StepSolution
								.simpleSolution(variable, tracker.getRestriction(), tracker));
					}
				}

				return new Result(solutions);
			}

			if (isZero(si.LHS) && si.RHS.sign() > 0) {
				if (si.isLessThan()) {
					if (si.isStrong()) {
						StepEquation equality = new StepEquation(si.LHS, si.RHS);
						steps.add(SolutionStepType.ZERO_LT_POSITIVE, equality);
						List<StepSolution> equalPoints = equality.solve(variable, steps);
						StepSet equalSet = new StepSet();
						for (StepSolution point : equalPoints) {
							equalSet.addElement((StepExpression) point.getValue());
						}
						solutions.add(StepSolution.simpleSolution(variable,
								subtract(tracker.getRestriction(), equalSet), tracker));
					} else {
						steps.add(SolutionStepType.ZERO_LE_POSITIVE, variable);
						solutions.add(StepSolution
								.simpleSolution(variable, tracker.getRestriction(), tracker));
					}
				} else {
					if (si.isStrong()) {
						steps.add(SolutionStepType.ZERO_GT_POSITIVE, variable);
					} else {
						StepEquation equality = new StepEquation(si.LHS, si.RHS);
						steps.add(SolutionStepType.ZERO_GE_POSITIVE, equality);
						return new Result(equality.solve(variable, steps));
					}
				}

				return new Result(solutions);
			}

			return null;
		}
	},

	RATIONAL_INEQUALITY {
		@Override
		public Result apply(StepInequality si, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (!isZero(si.RHS) || !(si.LHS.isOperation(Operation.MULTIPLY)
					|| si.LHS.isOperation(Operation.DIVIDE))) {
				return null;
			}

			List<StepExpression> terms = new ArrayList<>();

			StepExpression numerator = si.LHS.getNumerator();
			StepExpression denominator = si.LHS.getDenominator();

			if (numerator.isOperation(Operation.MULTIPLY)) {
				for (StepExpression term : (StepOperation) numerator) {
					terms.add(term);
				}
			} else {
				terms.add(numerator);
			}

			if (denominator != null && denominator.isOperation(Operation.MULTIPLY)) {
				for (StepExpression term : (StepOperation) denominator) {
					terms.add(term);
				}
			} else if (denominator != null) {
				terms.add(denominator);
			}

			List<StepExpression> roots = new ArrayList<>();
			for (StepExpression term : terms) {
				if (term.isConstant()) {
					continue;
				}

				new StepInequality(term, StepConstant.create(0), false, si.isStrong())
						.solve(variable, steps);

				StepEquation equation = new StepEquation(term, StepConstant.create(0));
				for (StepSolution solution : equation.solve(variable)) {
					StepExpression value = (StepExpression) solution.getValue();
					if (value == null || !value.canBeEvaluated()) {
						throw new SolveFailedException("Non-constant root of term in inequality");
					}

					roots.add(value);
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

			SolutionTable table = StepHelper.createSignTable(variable, roots, terms);
			StepHelper.addInequalityRow(table, numerator, denominator);

			steps.add(table);

			return new Result(StepHelper.readSolution(table, si, variable, tracker));
		}
	},

	SOLVE_QUADRATIC {
		@Override
		public Result apply(StepInequality si, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (si.degree(variable) != 2) {
				return null;
			}

			StepEquation equation = new StepEquation(si.LHS, si.RHS);
			List<StepSolution> solutions = equation.solve(variable, steps);

			StepExpression a = si.findCoefficient(power(variable, 2));

			if (solutions.size() == 0) {
				ArrayList<StepSolution> solution = new ArrayList<>();

				if (a.canBeEvaluated() && a.getValue() > 0) {
					steps.add(SolutionStepType.LEADING_COEFFICIENT_POSITIVE, a, si.LHS,
							variable);

					if (!si.isLessThan()) {
						solution.add(StepSolution
								.simpleSolution(variable, tracker.getRestriction(), tracker));
					}
				} else if (a.canBeEvaluated()) {
					steps.add(SolutionStepType.LEADING_COEFFICIENT_NEGATIVE, a, si.LHS,
							variable);

					if (si.isLessThan()) {
						solution.add(StepSolution
								.simpleSolution(variable, tracker.getRestriction(), tracker));
					}
				} else {
					if (si.isLessThan()) {
						tracker.addCondition(
								new StepInequality(a, StepConstant.create(0), false, true));
					} else {
						tracker.addCondition(
								new StepInequality(a, StepConstant.create(0), true, true));
					}

					solution.add(StepSolution
							.simpleSolution(variable, tracker.getRestriction(), tracker));
				}

				return new Result(solution);
			}

			StepExpression x1 = (StepExpression) solutions.get(0).getValue();
			StepExpression x2 = (StepExpression) solutions.get(1).getValue();

			StepExpression newLHS =
					multiply(a, multiply(subtract(variable, x1), subtract(variable, x2)));
			StepSolvable result = si.cloneWith(newLHS, si.RHS);

			steps.addSubstep(si, result, SolutionStepType.FACTOR_QUADRATIC);

			return new Result(result);
		}
	}
}
