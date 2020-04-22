package org.geogebra.web.test;

import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

/**
 * This class can process algebra commands as if those were entered into the AV.
 */
public class CommandProcessor {

	private App app;
	private AlgebraProcessor algebraProcessor;

	public CommandProcessor(App app) {
		this.app = app;
		algebraProcessor = app.getKernel().getAlgebraProcessor();
	}

	/**
	 * Use this method when you want to test the commands as if those were inserted in AV.
	 *
	 * @param command
	 *            algebra input to be processed
	 * @return resulting element
	 */

	public <T extends GeoElement> T process(String command) {
		EvalInfo info = EvalInfoFactory.getEvalInfoForAV(app, false);
		T[] geoElements = process(command, info);
		return getFirstElement(geoElements);
	}

	private <T extends GeoElement> T[] process(String command, EvalInfo info) {
		return (T[]) algebraProcessor
				.processAlgebraCommandNoExceptionHandling(
						command,
						false,
						app.getErrorHandler(),
						info,
						null);
	}

	private <T extends GeoElement> T getFirstElement(T[] geoElements) {
		return geoElements.length == 0 ? null : (T) geoElements[0].toGeoElement();
	}
}
