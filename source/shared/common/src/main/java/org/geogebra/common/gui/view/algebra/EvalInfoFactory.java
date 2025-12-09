/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
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
				.withSymbolicMode(AlgebraProcessor.getRedefinitionMode(geo, kernel))
				.withLabelRedefinitionAllowedFor(geo.getLabelSimple())
				.withSliders(true)
				.withSymbolic(true)
				.withAnalytics(true);
	}

	/**
	 * @param app application
	 * @return evaluation flags for user input in AV
	 */
	public static EvalInfo getEvalInfoForAV(App app) {
		return getEvalInfoForAV(app, app.getConfig().hasAutomaticSliders());
	}
}
