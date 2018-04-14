package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.html5.main.AppW;

/**
 * Sphere dialog
 */
public class InputDialogSpherePointW extends InputDialogRadiusW {

	private GeoPointND geoPoint;

	/**
	 * @param app
	 *            application
	 * @param title
	 *            title
	 * @param handler
	 *            input handler
	 * @param center
	 *            sphere cnter
	 * @param kernel
	 *            kernel
	 */
	public InputDialogSpherePointW(AppW app, String title,
            InputHandler handler, GeoPointND center, Kernel kernel) {
	    super(app, title, handler, kernel);
	    this.geoPoint = center;
    }

	@Override
	protected GeoElement createOutput(GeoNumberValue num) {
		return kernel.getManager3D().sphere(null, geoPoint, num);
    }

}
