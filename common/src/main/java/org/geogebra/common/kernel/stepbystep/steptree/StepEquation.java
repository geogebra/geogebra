package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.List;

import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionLine;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steps.SolveTracker;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;

public class StepEquation extends StepSolvable {

	private boolean isInequation;

	private StepEquation(StepExpression LHS, StepExpression RHS, boolean swapped) {
		super(LHS, RHS, swapped);
	}

	public StepEquation(StepExpression LHS, StepExpression RHS) {
		super(LHS, RHS);
	}

	public StepEquation setInequation() {
		this.isInequation = true;
		return this;
	}

	public boolean isInequation() {
		return isInequation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + LHS.hashCode();
		result = prime * result + RHS.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object sn) {
		if (sn instanceof StepEquation) {
			StepEquation se = (StepEquation) sn;
			return swapped == se.swapped
					&& isInequation == se.isInequation
					&& LHS.equals(se.LHS)
					&& RHS.equals(se.RHS);
		}

		return false;
	}

	@Override
	public StepEquation deepCopy() {
		StepEquation newEq = new StepEquation(LHS.deepCopy(), RHS.deepCopy(), swapped);
		newEq.isInequation = isInequation;

		return newEq;
	}

	@Override
	public StepEquation cloneWith(StepExpression newLHS, StepExpression newRHS) {
		StepEquation newEq = new StepEquation(newLHS, newRHS, swapped);
		newEq.isInequation = isInequation;

		return newEq;
	}

	@Override
	public StepSolvable swapSides() {
		return new StepEquation(RHS, LHS, !swapped);
	}

	@Override
	public String toString() {
		if (isInequation) {
			return LHS + " != " + RHS;
		}
		return LHS + " = " + RHS;
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		if (colored && color != 0) {
			return "\\fgcolor{" + getColorHex() + "}{" + toLaTeXString(loc, false) + "}";
		}

		return (swapped ? RHS : LHS).toLaTeXString(loc, colored)
				+ sign()
				+ (swapped ? LHS : RHS).toLaTeXString(loc, colored);
	}

	private String sign() {
		if (isInequation) {
			return " \\neq ";
		}
		return " = ";
	}

	@Override
	public StepEquation regroup() {
		return regroup(null);
	}

	@Override
	public StepEquation regroup(SolutionBuilder sb) {
		return (StepEquation) super.regroup(sb);
	}

	@Override
	public List<StepSolution> solve(StepVariable sv, SolutionBuilder sb, SolveTracker tracker) {
		return StepStrategies.defaultSolve(this, sv, sb, tracker);
	}

	@Override
	public List<StepSolution> solveAndCompareToCAS(Kernel kernel, StepVariable sv,
			SolutionBuilder sb) throws CASException {
		SolveTracker tracker = new SolveTracker();

		return solve(sv, sb, tracker);
	}

	@Override
	public boolean checkSolution(StepVariable variable, StepExpression value, SolutionBuilder steps,
			SolveTracker tracker) {
		SolutionBuilder tempSteps = new SolutionBuilder();

		StepSolvable replaced = replace(variable, value, tempSteps);
		replaced = replaced.expand(tempSteps);

		steps.addGroup(new SolutionLine(SolutionStepType.PLUG_IN_AND_CHECK, value), tempSteps,
				replaced);

		if (!replaced.LHS.equals(replaced.RHS)) {
			if (isEqual(replaced.LHS.getValue(), replaced.RHS.getValue())) {
				Log.error("Regroup failed at: " + this);
				Log.error("For solution: " + variable + " = " + value);
				Log.error("Result: " + replaced);
				Log.error("Whereas numeric evaluation gives equality");

				return true;
			}

			return false;
		}

		return true;
	}

	@Override
	public StepEquation replace(StepExpression from, StepExpression to) {
		return (StepEquation) super.replace(from, to);
	}

	@Override
	public StepEquation replace(StepExpression from, StepExpression to, SolutionBuilder steps) {
		return (StepEquation) super.replace(from, to, steps);
	}
}
