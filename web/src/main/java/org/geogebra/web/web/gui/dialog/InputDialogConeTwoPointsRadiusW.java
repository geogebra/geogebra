package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.html5.main.AppW;

/**
 * Dialog for cone given by two points and radius
 *
 */
public class InputDialogConeTwoPointsRadiusW extends InputDialogRadiusW {

	private GeoPointND a, b;

	/**
	 * 
	 * @param app
	 *            app
	 * @param title
	 *            dialog title
	 * @param handler
	 *            input handler
	 * @param a
	 *            bottom point
	 * @param b
	 *            vertex
	 * @param kernel
	 *            kernel
	 */
	public InputDialogConeTwoPointsRadiusW(AppW app, String title,
            InputHandler handler, GeoPointND a, GeoPointND b, Kernel kernel) {
	    super(app, title, handler, kernel);
	    this.a = a;
	    this.b = b;
    }

	@Override
	protected GeoElement createOutput(GeoNumberValue num) {
		return kernel.getManager3D().ConeLimited(null, a, b, num)[0];
	}

}
