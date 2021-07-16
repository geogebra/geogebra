package org.geogebra.common.gui.view.algebra;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.solution.TextElement;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.main.Localization;

public class StepGuiBuilderCmd {

	private Localization loc;
	private GeoList list;
	private StepVariable variable;

	private String lastRow;

	/**
	 * @param loc
	 *            localization
	 * @param list
	 *            output list
	 * @param variable
	 *            variable
	 */
	public StepGuiBuilderCmd(Localization loc, GeoList list, StepVariable variable) {
		this.loc = loc;
		this.list = list;
		this.variable = variable;
	}

	/**
	 * Build list of step descriptions
	 * 
	 * @param step
	 *            solution steps
	 */
	public void buildList(SolutionStep step) {
		if (step.getType() == SolutionStepType.GROUP_WRAPPER) {
			SolutionStep substep = step.getSubsteps().get(0);

			if (substep.getType() == SolutionStepType.FIND_UNDEFINED_POINTS
					|| substep.getType() == SolutionStepType.DETERMINE_THE_DEFINED_RANGE
					|| substep.getType() == SolutionStepType.PLUG_IN_AND_CHECK) {
				return;
			}
		}

		if (step.getSubsteps() != null) {
			for (SolutionStep substep : step.getSubsteps()) {
				buildList(substep);
			}
		}

		if (step.getType() == SolutionStepType.EQUATION
				&& !step.getDefault(loc).get(0).latex.equals(lastRow)) {
			lastRow = step.getDefault(loc).get(0).latex;
			add(lastRow);
		}

		if (step.getType() == SolutionStepType.NO_REAL_SOLUTION
				&& !step.getDefault(loc).get(0).plain.equals(lastRow)) {
			lastRow = variable.toString() + "\\in \\emptyset";
			add(lastRow);
		}

		if (step.getType() == SolutionStepType.SOLUTIONS) {
			List<TextElement> elements = step.getDefault(loc);
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < elements.size(); i++) {
				if (elements.get(i).latex != null) {
					sb.append(elements.get(i).latex);
				} else {
					sb.append("\\text{").append(elements.get(i).plain).append("}");
				}
			}
			if (!sb.toString().equals(lastRow)) {
				lastRow = sb.toString();
				add(lastRow);
			}
		}
	}

	private void add(String s) {
		GeoText text = new GeoText(list.getConstruction(), s);
		text.setLaTeX(true, false);
		text.setSerifFont(false);
		list.add(text);
	}
}
