package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.List;

import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steps.SolveTracker;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;
import org.geogebra.common.main.Localization;

public class StepInequality extends StepSolvable {

	private final boolean lessThan;
	private final boolean strong;

	private StepInequality(StepExpression LHS, StepExpression RHS, boolean lessThan,
			boolean strong, boolean swapped) {
		super(LHS, RHS, swapped);
		this.lessThan = lessThan;
		this.strong = strong;
	}

	public StepInequality(StepExpression LHS, StepExpression RHS, boolean lessThan,
			boolean strong) {
		this(LHS, RHS, lessThan, strong, false);
	}

	public static StepInequality from(String LHS, String op, String RHS, Parser parser) {
		StepExpression _LHS = (StepExpression) StepNode.getStepTree(LHS, parser);
		StepExpression _RHS = (StepExpression) StepNode.getStepTree(RHS, parser);

		boolean lessThan = op.contains("<");
		boolean strong = !op.contains("=");
		return new StepInequality(_LHS, _RHS, lessThan, strong);
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
		result = prime * result + LHS.hashCode();
		result = prime * result + RHS.hashCode();
		result = prime * result + (lessThan ? 1 : 0);
		result = prime * result + (strong ? 1 : 0);
		return result;
	}

	@Override
	public boolean equals(Object sn) {
		if (sn instanceof StepInequality) {
			StepInequality si = (StepInequality) sn;
			return swapped == si.swapped
					&& lessThan == si.lessThan
					&& strong == si.strong
					&& LHS.equals(si.LHS)
					&& RHS.equals(si.RHS);
		}

		return false;
	}

	@Override
	public List<StepSolution> solve(StepVariable sv, SolutionBuilder sb, SolveTracker tracker) {
		return StepStrategies.defaultInequalitySolve(this, sv, sb, tracker);
	}

	@Override
	public List<StepSolution> solveAndCompareToCAS(Kernel kernel, StepVariable sv,
			SolutionBuilder sb) throws CASException {
		return solve(sv, sb, new SolveTracker());
	}

	@Override
	public StepInequality deepCopy() {
		return new StepInequality(LHS.deepCopy(), RHS.deepCopy(), lessThan, strong, swapped);
	}

	@Override
	public StepInequality cloneWith(StepExpression newLHS, StepExpression newRHS) {
		return new StepInequality(newLHS, newRHS, lessThan, strong, swapped);
	}

	@Override
	public StepInequality swapSides() {
		return new StepInequality(RHS, LHS, !lessThan, strong, !swapped);
	}

	@Override
	public String toString() {
		return LHS.toString() + (lessThan ? " <" : " >") + (strong ? " " : "= ") + RHS.toString();
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		return (swapped ? RHS : LHS).toLaTeXString(loc, colored)
				+ (lessThan ^ swapped ? " \\l" : " \\g")
				+ (strong ? "t " : "e ")
				+ (swapped ? LHS : RHS).toLaTeXString(loc, colored);
	}

	@Override
	public boolean checkSolution(StepVariable variable, StepExpression value, SolutionBuilder sb,
			SolveTracker tracker) {
		return true;
	}

	public StepInequality flip() {
		return new StepInequality(LHS, RHS, !lessThan, strong);
	}
}
