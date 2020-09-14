package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.DialogData;

/**
 * Dialog for cylinder given by top & bottom centers.
 */
public class InputDialogCylinderTwoPointsRadiusW extends InputDialogRadiusW {

	private GeoPointND a;
	private GeoPointND b;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog data
	 * @param handler
	 *            input handler
	 * @param a
	 *            bottom center
	 * @param b
	 *            top center
	 * @param kernel
	 *            kernel
	 */
	public InputDialogCylinderTwoPointsRadiusW(AppW app, DialogData data,
            InputHandler handler, GeoPointND a, GeoPointND b, Kernel kernel) {
	    super(app, data, handler, kernel);
	    this.a = a;
	    this.b = b; 
    }

	@Override
	protected GeoElement createOutput(GeoNumberValue num) {
		return kernel.getManager3D().cylinderLimited(null, a, b, num)[0];
    }

}
