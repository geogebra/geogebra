package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.solution.SolutionTable;
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.common.plugin.Operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.*;

public enum InequalitySteps implements SolveStepGenerator {

	TRIVIAL_INEQUALITY {
		@Override
		public Result apply(StepSolvable se, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepInequality si = (StepInequality) se;

			List<StepSolution> solutions = new ArrayList<>();

			if (si.getLHS().equals(variable) && si.getRHS().isConstantIn(variable)) {
				if (si.isLessThan()) {
					solutions.add(StepSolution.simpleSolution(variable,
							new StepInterval(StepConstant.NEG_INF, si.getRHS(), false,
									!si.isStrong()), tracker));
				} else {
					solutions.add(StepSolution.simpleSolution(variable,
							new StepInterval(si.getRHS(), StepConstant.POS_INF, !si.isStrong(),
									false), tracker));
				}

				return new Result(solutions);
			}

			if (si.getRHS().equals(variable) && si.getLHS().isConstantIn(variable)) {
				if (si.isLessThan()) {
					solutions.add(StepSolution.simpleSolution(variable,
							new StepInterval(si.getLHS(), StepConstant.POS_INF, !si.isStrong(),
									false), tracker));
				} else {
					solutions.add(StepSolution.simpleSolution(variable,
							new StepInterval(StepConstant.NEG_INF, si.getRHS(), false,
									!si.isStrong()), tracker));
				}

				return new Result(solutions);
			}

			if (si.getLHS().equals(si.getRHS()) && !si.isStrong()) {
				solutions.add(StepSolution
						.simpleSolution(variable, tracker.getRestriction(), tracker));

				return new Result(solutions);
			}

			if (si.getLHS().canBeEvaluated() && si.getRHS().canBeEvaluated() &&
					si.isLessThan() == si.getLHS().getValue() < si.getRHS().getValue()) {
				solutions.add(StepSolution
						.simpleSolution(variable, tracker.getRestriction(), tracker));

				return new Result(solutions);
			}

			return null;
		}
	},

	DIVIDE_BY_COEFFICIENT {
		@Override
		public Result apply(StepSolvable ss, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (isZero(ss.getRHS()) && !isZero(ss.getLHS().getCoefficient())) {
				StepExpression coefficient = ss.getLHS().getCoefficient();
				coefficient.setColor(1);

				StepSolvable result = ss.cloneWith(ss.getLHS().getVariable(), ss.getRHS());
				steps.addSubstep(ss, result, SolutionStepType.DIVIDE_BOTH_SIDES, coefficient);

				return new Result(result);
			}

			return null;
		}
	},

	POSITIVE_AND_NEGATIVE {
		@Override
		public Result apply(StepSolvable ss, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepInequality si = (StepInequality) ss;

			List<StepSolution> solutions = new ArrayList<>();

			if (si.getLHS().sign() > 0 && si.getRHS().sign() < 0) {
				if (si.isLessThan()) {
					steps.add(SolutionStepType.POSITIVE_L_NEGATIVE, variable);
				} else {
					steps.add(SolutionStepType.POSTIVE_G_NEGATIVE, variable);
					solutions.add(StepSolution
							.simpleSolution(variable, tracker.getRestriction(), tracker));
				}

				return new Result(solutions);
			}

			if (si.getLHS().sign() < 0 && si.getRHS().sign() > 0) {
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
		public Result apply(StepSolvable ss, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			StepInequality si = (StepInequality) ss;

			List<StepSolution> solutions = new ArrayList<>();

			if (si.getLHS().sign() > 0 && isZero(si.getRHS())) {
				if (si.isLessThan()) {
					if (si.isStrong()) {
						steps.add(SolutionStepType.POSITIVE_LT_ZERO, variable);
					} else {
						StepEquation equality = new StepEquation(si.getLHS(), si.getRHS());
						steps.add(SolutionStepType.POSITIVE_LE_ZERO, equality);
						return new Result(equality.solve(variable, steps));
					}
				} else {
					if (si.isStrong()) {
						StepEquation equality = new StepEquation(si.getLHS(), si.getRHS());
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

			if (isZero(si.getLHS()) && si.getRHS().sign() > 0) {
				if (si.isLessThan()) {
					if (si.isStrong()) {
						StepEquation equality = new StepEquation(si.getLHS(), si.getRHS());
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
						StepEquation equality = new StepEquation(si.getLHS(), si.getRHS());
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
		public Result apply(StepSolvable ss, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (!isZero(ss.getRHS()) || !(ss.getLHS().isOperation(Operation.MULTIPLY) ||
					ss.getLHS().isOperation(Operation.DIVIDE))) {
				return null;
			}

			StepInequality si = (StepInequality) ss;

			List<StepExpression> terms = new ArrayList<>();

			StepExpression numerator = si.getLHS().getNumerator();
			StepExpression denominator = si.getLHS().getDenominator();

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
					roots.add((StepExpression) solution.getValue());
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

			SolutionTable table = SolutionTable.createSignTable(variable, roots, terms);
			table.addInequalityRow(numerator, denominator);

			steps.add(table);

			return new Result(table.readSolution(si, variable, tracker));
		}
	},

	SOLVE_QUADRATIC {
		@Override
		public Result apply(StepSolvable ss, StepVariable variable,
				SolutionBuilder steps, SolveTracker tracker) {
			if (ss.degree(variable) != 2) {
				return null;
			}

			StepInequality si = (StepInequality) ss;

			StepEquation equation = new StepEquation(si.getLHS(), si.getRHS());
			List<StepSolution> solutions = equation.solve(variable, steps);

			StepExpression a = si.findCoefficient(power(variable, 2));

			if (solutions.size() == 0) {
				ArrayList<StepSolution> solution = new ArrayList<>();

				if (a.canBeEvaluated() && a.getValue() > 0) {
					steps.add(SolutionStepType.LEADING_COEFFICIENT_POSITIVE, a, si.getLHS(),
							variable);

					if (!si.isLessThan()) {
						solution.add(StepSolution
								.simpleSolution(variable, tracker.getRestriction(), tracker));
					}
				} else if (a.canBeEvaluated()) {
					steps.add(SolutionStepType.LEADING_COEFFICIENT_NEGATIVE, a, si.getLHS(),
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
			StepSolvable result = si.cloneWith(newLHS, si.getRHS());

			steps.addSubstep(si, result, SolutionStepType.FACTOR_QUADRATIC);

			return new Result(result);
		}
	}


}
