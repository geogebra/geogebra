package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.solution.TextElement;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.main.Localization;

import java.util.ArrayList;
import java.util.List;

public class StepGuiBuilderCmd implements StepGuiBuilder {

	private Localization loc;
	private int maxRows;
	private StepVariable variable;

	private List<String> rows;
	private String lastRow;

	private String table;

	public StepGuiBuilderCmd(Localization loc, int maxRows, StepVariable variable) {
		this.loc = loc;
		this.maxRows = maxRows;
		this.variable = variable;
	}

	@Override
	public void buildStepGui(SolutionStep step) {
		rows = new ArrayList<>();
		getListOfSteps(step);

		StringBuilder sb = new StringBuilder();

		sb.append("\\begin{array}");
		if (maxRows == -1) {
			sb.append("{l}");
			for (String row : rows) {
				sb.append(row).append(" \\\\ ");
			}
		} else {
			int n = (rows.size() + maxRows - 1) / maxRows;

			sb.append("{*{").append(n).append("}{l}}");
			for (int j = 0; j < maxRows; j++) {
				for (int i = 0; i < n; i++) {
					if (i != 0) {
						sb.append(" & ");
					}
					if (maxRows * i + j < rows.size()) {
						sb.append(rows.get(maxRows * i + j));
					}
				}
				sb.append(" \\\\ ");
			}
		}
		sb.append("\\end{array}");

		table = sb.toString();
	}

	private void getListOfSteps(SolutionStep step) {
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
				getListOfSteps(substep);
			}
		}

		if (step.getType() == SolutionStepType.EQUATION
				&& !step.getDefault(loc).get(0).latex.equals(lastRow)) {
			lastRow = step.getDefault(loc).get(0).latex;
			rows.add(lastRow);
		}

		if (step.getType() == SolutionStepType.NO_REAL_SOLUTION
				&& !step.getDefault(loc).get(0).plain.equals(lastRow)) {
			lastRow = variable.toString() + "\\in \\emptyset";
			rows.add(lastRow);
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
				rows.add(lastRow);
			}
		}
	}

	@Override
	public String toString() {
		return table;
	}
}
