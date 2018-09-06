package org.geogebra.common.kernel.stepbystep.solution;

import org.geogebra.common.main.Localization;

import java.util.ArrayList;
import java.util.List;

public abstract class SolutionStep {

	protected List<SolutionStep> substeps;

	public abstract List<TextElement> getDefault(Localization loc);

	public abstract List<TextElement> getDetailed(Localization loc);

	public void addSubStep(SolutionStep substep) {
		if (substeps == null) {
			substeps = new ArrayList<>();
		}

		substeps.add(substep);
	}

	public abstract SolutionStepType getType();

	public List<SolutionStep> getSubsteps() {
		return substeps;
	}

	// TODO: I should instead think about which ones I do want to collapse....
	private boolean shouldCollapse() {
		return getType() != SolutionStepType.FACTOR
				&& getType() != SolutionStepType.DETERMINE_THE_DEFINED_RANGE
				&& getType() != SolutionStepType.FIND_UNDEFINED_POINTS
				&& getType() != SolutionStepType.SOLVE_FOR
				&& getType() != SolutionStepType.SIMPLIFY
				&& getType() != SolutionStepType.ADD_TO_BOTH_SIDES
				&& getType() != SolutionStepType.SUBTRACT_FROM_BOTH_SIDES;
	}

	public boolean shouldSkip() {
		return getType() == SolutionStepType.DETERMINE_THE_DEFINED_RANGE
				|| getType() == SolutionStepType.FIND_UNDEFINED_POINTS;
	}

	public boolean shouldSkipSubsteps() {
		return getType() == SolutionStepType.ADD_TO_BOTH_SIDES
				|| getType() == SolutionStepType.SUBTRACT_FROM_BOTH_SIDES;
	}

	public SolutionStep cleanupSteps() {
		if (substeps == null) {
			return this;
		}

		for (int i = 0; i < substeps.size(); i++) {
			substeps.set(i, substeps.get(i).cleanupSteps());
		}

		if (getType() == SolutionStepType.GROUP_WRAPPER) {
			if (substeps.get(0).substeps != null && substeps.get(0).substeps.size() == 1) {
				if (substeps.get(0).shouldCollapse()) {
					return substeps.get(0).substeps.get(0);
				}
			}
		}

		return this;
	}

	/**
	 * the complexity of a solution is simply the total number of steps
	 *
	 * @return complexity of solution
	 */
	public int getComplexity() {
		int complexity = 1;

		if (substeps != null) {
			for (SolutionStep step : substeps) {
				complexity += step.getComplexity();
			}
		}

		return complexity;
	}
}
