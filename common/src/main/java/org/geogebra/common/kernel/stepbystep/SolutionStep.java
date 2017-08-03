package org.geogebra.common.kernel.stepbystep;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.view.algebra.StepGuiBuilder;

public class SolutionStep {
	private String label;
	private SolutionStepTypes type;
	private List<SolutionStep> substeps;

	public SolutionStep(String label, SolutionStepTypes type) {
		this.label = label;
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void getListOfSteps(StepGuiBuilder builder) {
		builder.addLatexRow(getLabel());

		if (substeps == null) {
			return;
		}
		builder.startGroup();
		for (int i = 0; i < substeps.size(); i++) {
			(substeps.get(i)).getListOfSteps(builder);
		}
		builder.endGroup();
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
		return type == SolutionStepTypes.COMMENT;
	}

	public boolean isInstruction() {
		return type == SolutionStepTypes.INSTRUCTION;
	}

	public boolean isEquation() {
		return type == SolutionStepTypes.EQUATION;
	}

	public boolean isSolution() {
		return type == SolutionStepTypes.SOLUTION;
	}
}
