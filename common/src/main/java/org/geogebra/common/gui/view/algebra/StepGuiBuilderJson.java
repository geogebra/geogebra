package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.TextElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

import java.util.List;

/**
 * Makes a JSON object with a list of steps
 *
 */
public class StepGuiBuilderJson implements StepGuiBuilder {

	private Localization loc;
	private StringBuilder sb;

	public StepGuiBuilderJson(Localization loc) {
		this.loc = loc;
		sb = new StringBuilder();
	}

	@Override
	public void buildStepGui(SolutionStep step) {
		addRow(step.getDetailed(loc));

		if (step.getSubsteps() != null) {
			for (SolutionStep substep : step.getSubsteps()) {
				buildStepGui(substep);
			}
		}
	}

	public void addRow(List<TextElement> list) {
		for (TextElement te : list) {
			if (sb.length() > 0) {
				sb.append(',');
			}

			sb.append("{ 'text':'");

			if (te.latex != null) {
				sb.append(StringUtil.toJavaString(te.latex));
				sb.append("', latex':true }");
			} else {
				sb.append(StringUtil.toJavaString(te.plain));
				sb.append("', 'plain':true }");
			}
		}
	}

	@Override
	public String toString() {
		return "[" + sb.toString() + "]";
	}
}
