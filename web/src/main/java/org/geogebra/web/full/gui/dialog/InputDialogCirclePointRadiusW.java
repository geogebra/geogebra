package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.web.html5.main.AppW;

/**
 * Dialog for circle with center and radius.
 */
public class InputDialogCirclePointRadiusW extends InputDialogRadiusW {
	private GeoPoint geoPoint1;

	/**
	 * 
	 * @param app
	 *            application
	 * @param title
	 *            title
	 * @param handler
	 *            input handler
	 * @param point1
	 *            start point
	 * @param kernel
	 *            kernel
	 */
	public InputDialogCirclePointRadiusW(AppW app, String title,
			InputHandler handler, GeoPoint point1, Kernel kernel) {
		super(app, title, handler, kernel);

		geoPoint1 = point1;
	}

	@Override
	protected GeoElement createOutput(GeoNumberValue num) {
		return kernel.getAlgoDispatcher().circle(null, geoPoint1, num);
	}

}
