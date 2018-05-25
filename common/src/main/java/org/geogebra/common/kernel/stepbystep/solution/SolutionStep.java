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

	public List<SolutionStep> getSubsteps() {
		return substeps;
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
