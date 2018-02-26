package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steps.SolveTracker;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;
import org.geogebra.common.main.Localization;

import java.util.List;

public class StepInequality extends StepSolvable {

	private boolean lessThan;
	private boolean strong;

	public StepInequality(StepExpression LHS, StepExpression RHS, boolean lessThan, boolean strong) {
		this.LHS = LHS;
		this.RHS = RHS;
		this.lessThan = lessThan;
		this.strong = strong;
	}

	public boolean isLessThan() {
		return lessThan;
	}

	public boolean isStrong() {
		return strong;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((LHS == null) ? 0 : LHS.hashCode());
		result = prime * result + ((RHS == null) ? 0 : RHS.hashCode());
		result = prime * result + (lessThan ? 1 : 0);
		result = prime * result + (strong ? 1 : 0);
		return result;
	}

	@Override
	public boolean equals(Object sn) {
		if (sn instanceof StepInequality) {
			StepInequality si = (StepInequality) sn;
			return LHS.equals(si.LHS) && RHS.equals(si.RHS) && lessThan == si.lessThan && strong == si.strong;
		}

		return false;
	}

	@Override
	public List<StepSolution> solve(StepVariable sv, SolutionBuilder sb, SolveTracker tracker) {
		return StepStrategies.defaultInequalitySolve(this, sv, sb, tracker);
	}

	@Override
	public List<StepSolution> solveAndCompareToCAS(Kernel kernel, StepVariable sv, SolutionBuilder sb)
			throws CASException {
		return solve(sv, sb, new SolveTracker());
	}

	@Override
	public StepInequality deepCopy() {
		return new StepInequality(LHS.deepCopy(), RHS.deepCopy(), lessThan, strong);
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		return LHS.toLaTeXString(loc, colored) +
				(lessThan ? " \\l" : " \\g") +
				(strong ? "t " : "e ") +
				RHS.toLaTeXString(loc, colored);
	}

	@Override
	public boolean checkSolution(StepVariable variable, StepExpression value,
								 SolutionBuilder sb, SolveTracker tracker) {
		return true;
	}

	public void flip() {
		lessThan = !lessThan;
	}

	public static StepInequality from(String LHS, String op, String RHS,
			Parser parser) {
		StepExpression _LHS = (StepExpression) StepNode.getStepTree(LHS,
				parser);
		StepExpression _RHS = (StepExpression) StepNode.getStepTree(RHS,
				parser);

		boolean lessThan = op.contains("<");
		boolean strong = !op.contains("=");
		return new StepInequality(_LHS, _RHS, lessThan, strong);
	}
}
