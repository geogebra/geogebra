package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.main.App;

public class EvalInfoFactory {

	/**
	 * TODO used in web because of the create slider button.
	 * 
	 * @param app
	 *            application
	 * @param withSliders
	 *            whether to autocreate sliders; TODO remove this once we
	 * @return evaluation flags for user input in AV
	 */
	public static EvalInfo getEvalInfoForAV(App app, boolean withSliders) {
		return new EvalInfo(true, true).withSliders(withSliders)
				.withFractions(true)
				.addDegree(app.getKernel().getAngleUnitUsesDegrees())
				.withUserEquation(true)
				.withSymbolicMode(app.getKernel().getSymbolicMode())
				.withCopyingPlainVariables(true).withNoRedefinitionAllowed();
	}

	/**
	 * @param app
	 *            application
	 * @return evaluation flags for user input in AV
	 */
	public static EvalInfo getEvalInfoForAV(App app) {
		return getEvalInfoForAV(app, app.getConfig().hasAutomaticSliders());
	}
}
