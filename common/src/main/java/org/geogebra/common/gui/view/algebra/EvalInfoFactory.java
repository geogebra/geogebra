package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;

public class EvalInfoFactory {

	private static final EvalInfo baseAVInfo = new EvalInfo(true, true)
			.withSymbolic(true)
			.withAnalytics(true)
			.withCopyingPlainVariables(true);

	/**
	 * Used in web because of the create slider button.
	 * @param app application
	 * @param withSliders whether to autocreate sliders; TODO remove this once we
	 * @return evaluation flags for user input in AV
	 */
	public static EvalInfo getEvalInfoForAV(App app, boolean withSliders) {
		return baseAVInfo
				.withSliders(withSliders)
				.addDegree(app.getKernel().getAngleUnitUsesDegrees())
				.withSymbolicMode(app.getKernel().getSymbolicMode())
				.withNoRedefinitionAllowed();
	}

	/**
	 * @param kernel kernel
	 * @param geo geo element to redefine
	 * @param redefine whether independent geos may be redefined
	 * @return eval info
	 */
	public static EvalInfo getEvalInfoForRedefinition(Kernel kernel, GeoElement geo,
			boolean redefine) {
		return new EvalInfo(!kernel.getConstruction().isSuppressLabelsActive(), redefine)
				.withSymbolicMode(isSlider(geo) ? SymbolicMode.NONE : kernel.getSymbolicMode())
				.withLabelRedefinitionAllowedFor(geo.getLabelSimple())
				.withSliders(true)
				.withSymbolic(true)
				.withAnalytics(true);
	}

	private static boolean isSlider(GeoElement geo) {
		return geo.isGeoNumeric() && ((GeoNumeric) geo).isSlider();
	}

	/**
	 * @param app application
	 * @return evaluation flags for user input in AV
	 */
	public static EvalInfo getEvalInfoForAV(App app) {
		return getEvalInfoForAV(app, app.getConfig().hasAutomaticSliders());
	}
}
