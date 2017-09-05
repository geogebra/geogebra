package org.geogebra.common.kernel.stepbystep.solution;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.view.algebra.StepGuiBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.main.Localization;

public class SolutionStep {

	private Localization loc;

	/**
	 * The color of the solution step is either contained in the parameters themselves,
	 * or - when there is no parameter, but there is still need for a color (to signal
	 * for example the regrouping of constants), you have to pass a color. This will be 
	 * represented as a dot after the text of the step.
	 */
	
	private SolutionStepType type;
	private StepNode[] parameters;
	private int color;

	private List<SolutionStep> substeps;

	public SolutionStep(Localization loc, SolutionStepType type, StepNode... parameters) {
		this.loc = loc;

		this.type = type;
		this.parameters = parameters;
	}

	public SolutionStep(Localization loc, SolutionStepType type, int color) {
		this.loc = loc;

		this.type = type;
		this.color = color;
	}

	public String getDefault() {
		return type.getDefaultText(loc, parameters);
	}

	public String getColored() {
		return type.getDetailedText(loc, color, parameters);
	}

	public void getListOfSteps(StepGuiBuilder builder) {
		if (type == SolutionStepType.WRAPPER) {
			for (int i = 0; i < substeps.size(); i++) {
				(substeps.get(i)).getListOfSteps(builder);
			}
		} else if (type == SolutionStepType.SUBSTEP_WRAPPER) {
			builder.addLatexRow(getColored());

			for (int i = 0; i < substeps.size(); i++) {
				(substeps.get(i)).getListOfSteps(builder);
			}
		} else {
			builder.addLatexRow(getColored());

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

	public SolutionStepType getType() {
		return type;
	}
}
