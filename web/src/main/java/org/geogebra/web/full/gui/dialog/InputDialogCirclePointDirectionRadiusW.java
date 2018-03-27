package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.html5.main.AppW;

/**
 * Dialog for circle with axis, center and radius.
 */
public class InputDialogCirclePointDirectionRadiusW extends InputDialogRadiusW {

	private GeoPointND geoPoint;
	private GeoDirectionND forAxis;

	/**
	 * @param app
	 *            application
	 * @param title
	 *            title
	 * @param handler
	 *            input handler
	 * @param geoPoint
	 *            center point
	 * @param forAxis
	 *            direction
	 * @param kernel
	 *            kernel
	 */
	public InputDialogCirclePointDirectionRadiusW(AppW app, String title,
            NumberInputHandler handler, GeoPointND geoPoint,
            GeoDirectionND forAxis, Kernel kernel) {
		super(app, title, handler, kernel);
		this.geoPoint = geoPoint;
		this.forAxis = forAxis;
    }

	@Override
	protected GeoElement createOutput(GeoNumberValue num) {
		return kernel.getManager3D().Circle3D(
				null,
				geoPoint,
				num,
				forAxis);
    }

}
