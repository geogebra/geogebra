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
		if (type == SolutionStepTypes.WRAPPER) {
			for (int i = 0; i < substeps.size(); i++) {
				(substeps.get(i)).getListOfSteps(builder);
			}
		} else {
			builder.addLatexRow(getLabel());

			if (substeps != null) {
				builder.startGroup();
				for (int i = 0; i < substeps.size(); i++) {
					(substeps.get(i)).getListOfSteps(builder);
				}
				builder.endGroup();
			}
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

	public SolutionStepTypes getType() {
		return type;
	}
}
