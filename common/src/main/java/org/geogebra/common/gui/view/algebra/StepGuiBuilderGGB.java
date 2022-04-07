package org.geogebra.common.gui.view.algebra;

import java.util.List;

import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.solution.TextElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.util.debug.Log;

/**
 * Makes a JSON object with a list of steps
 *
 */
public class StepGuiBuilderGGB implements StepGuiBuilder {

	private Localization loc;
	private StringBuilder sb;
	private Object lastDescrip;

	/**
	 * @param loc
	 *            localization
	 */
	public StepGuiBuilderGGB(Localization loc) {
		this.loc = loc;
		sb = new StringBuilder();
	}

	@Override
	public void buildStepGui(SolutionStep step) {
		try {
			sb.append("{");
			// sb.append("{\\begin{array}{l}");
			buildStepGui(step, sb);

			// remove comma
			sb.setLength(sb.length() - 1);

			sb.append("}");
			// sb.append("\\end{array}}");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.debug(e);
		}
	}

	private void buildStepGui(SolutionStep step, StringBuilder sb2)
			throws JSONException {
		if (step.getType() == SolutionStepType.WRAPPER
				|| step.getType() == SolutionStepType.GROUP_WRAPPER) {
			for (SolutionStep substep : step.getSubsteps()) {
				buildStepGui(substep, sb2);
			}
			return;
		}
		// JSONObject stepJ = new JSONObject();

		String description = descriptionString(step.getDetailed(loc));
		if (description.length() > 0) {

			switch (step.getType()) {
			default:
				Log.error("omitting " + description);
				break;
			case EQUATION:
			case SOLUTION:
			case SOLUTIONS:

				Log.error(description);

				// remove color
				description = description.replaceAll(
						"\\\\fgcolor\\{#......\\}\\{(.*?)\\}",
						"\\{$1\\}");

				Log.error(description);

				if (!removeBraces(description).equals(lastDescrip)) {
					sb2.append("{\"");
					sb2.append(description);
					sb2.append("\"},");

				} else {
					Log.debug("duplicate row " + description);
				}

				lastDescrip = removeBraces(description);

			}
		}

		Log.error(step.getType() + " " + description);
		// stepJ.put("type", step.getType() + "");

		if (step.getSubsteps() != null) {
			// StringBuilder substeps = new StringBuilder();
			for (SolutionStep substep : step.getSubsteps()) {
				buildStepGui(substep, sb2);
			}
			// stepJ.put("substeps", substeps);

		}
		// sb2.append(stepJ);
		// sb2.append("\n");
	}

	/**
	 * remove braces to check equality eg "x" and "{x}"
	 * 
	 */
	private static String removeBraces(String s) {
		return s.replaceAll("\\{", "").replaceAll("\\}", "");
	}

	private static String descriptionString(List<TextElement> list) {
		StringBuilder description = new StringBuilder();
		for (TextElement te : list) {

			if (te.latex != null) {
				description.append(" ").append(te.latex).append(" ");
			} else {
				description.append("\\text{").append(te.plain).append("}");
			}
		}
		return description.toString();
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
