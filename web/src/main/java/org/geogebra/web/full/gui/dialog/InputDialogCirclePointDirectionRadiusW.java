package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.DialogData;

/**
 * Dialog for circle with axis, center and radius.
 */
public class InputDialogCirclePointDirectionRadiusW extends InputDialogRadiusW {

	private GeoPointND geoPoint;
	private GeoDirectionND forAxis;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog data
	 * @param handler
	 *            input handler
	 * @param geoPoint
	 *            center point
	 * @param forAxis
	 *            direction
	 * @param kernel
	 *            kernel
	 */
	public InputDialogCirclePointDirectionRadiusW(AppW app, DialogData data,
            NumberInputHandler handler, GeoPointND geoPoint,
            GeoDirectionND forAxis, Kernel kernel) {
		super(app, data, handler, kernel);
		this.geoPoint = geoPoint;
		this.forAxis = forAxis;
    }

	@Override
	protected GeoElement createOutput(GeoNumberValue num) {
		return kernel.getManager3D().circle3D(
				null,
				geoPoint,
				num,
				forAxis);
    }

}
