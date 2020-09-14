package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.DialogData;

/**
 * Sphere dialog
 */
public class InputDialogSpherePointW extends InputDialogRadiusW {

	private GeoPointND geoPoint;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog data
	 * @param handler
	 *            input handler
	 * @param center
	 *            sphere cnter
	 * @param kernel
	 *            kernel
	 */
	public InputDialogSpherePointW(AppW app, DialogData data,
            InputHandler handler, GeoPointND center, Kernel kernel) {
	    super(app, data, handler, kernel);
	    this.geoPoint = center;
    }

	@Override
	protected GeoElement createOutput(GeoNumberValue num) {
		return kernel.getManager3D().sphere(null, geoPoint, num);
    }
}