package org.geogebra.common.kernel.stepbystep;

import java.util.ArrayList;
import java.util.List;

public class SolutionStep {
	private String label;
	private StepTypes type;
	private List<SolutionStep> substeps;

	public SolutionStep(String label, StepTypes type) {
		this.label = label;
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public List<String> getListOfSteps() {
		List<String> steps = new ArrayList<String>();
		addSteps(steps);
		return steps;
	}

	private void addSteps(List<String> steps) {
		steps.add(getLabel());

		if (substeps == null) {
			return;
		}

		for (int i = 0; i < substeps.size(); i++) {
			(substeps.get(i)).addSteps(steps);
		}
	}

	public void addSubStep(SolutionStep s) {
		if (s != null) {
			if (substeps == null) {
				substeps = new ArrayList<SolutionStep>();
			}

			substeps.add(s);
		}
	}

	public List<SolutionStep> getSubsteps() {
		return substeps;
	}

	public boolean isComment() {
		return type == StepTypes.COMMENT;
	}

	public boolean isInstruction() {
		return type == StepTypes.INSTRUCTION;
	}

	public boolean isEquation() {
		return type == StepTypes.EQUATION;
	}

	public boolean isSolution() {
		return type == StepTypes.SOLUTION;
	}
}
