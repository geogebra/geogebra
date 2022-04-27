package org.geogebra.common.gui.view.algebra;

import java.util.List;

import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.solution.TextElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;

/**
 * Makes a JSON object with a list of steps
 *
 */
public class StepGuiBuilderJson implements StepGuiBuilder {

	private Localization loc;
	private JSONArray sb;

	/**
	 * @param loc
	 *            localization
	 */
	public StepGuiBuilderJson(Localization loc) {
		this.loc = loc;
		sb = new JSONArray();
	}

	@Override
	public void buildStepGui(SolutionStep step) {
		try {
			buildStepGui(step, sb);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.debug(e);
		}
	}

	private void buildStepGui(SolutionStep step, JSONArray sb2) throws JSONException {
		if (step.getType() == SolutionStepType.WRAPPER
				|| step.getType() == SolutionStepType.GROUP_WRAPPER) {
			for (SolutionStep substep : step.getSubsteps()) {
				buildStepGui(substep, sb2);
			}
			return;
		}
		JSONObject stepJ = new  JSONObject();

		JSONArray description = toJSONArray(step.getDetailed(loc));
		if (description.length() > 0) {
			stepJ.put("description", description);
		}
		stepJ.put("type", step.getType() + "");

		if (step.getSubsteps() != null) {
			JSONArray substeps = new JSONArray();
			for (SolutionStep substep : step.getSubsteps()) {
				buildStepGui(substep, substeps);
			}
			stepJ.put("substeps", substeps);

		}
		sb2.put(stepJ);
	}

	private static JSONArray toJSONArray(List<TextElement> list) throws JSONException {
		JSONArray description = new JSONArray();
		for (TextElement te : list) {
			JSONObject obj = new JSONObject();

			if (te.latex != null) {
				obj.put("latex", te.latex);
			} else {
				obj.put("plain", te.plain);
			}
			description.put(obj);
		}
		return description;
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
