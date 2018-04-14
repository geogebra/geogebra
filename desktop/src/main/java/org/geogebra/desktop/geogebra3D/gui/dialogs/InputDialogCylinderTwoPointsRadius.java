package org.geogebra.desktop.geogebra3D.gui.dialogs;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.desktop.gui.dialog.InputDialogRadiusD;
import org.geogebra.desktop.main.AppD;

/**
 * 
 *
 */
public class InputDialogCylinderTwoPointsRadius extends InputDialogRadiusD {

	private GeoPointND a, b;

	/**
	 * 
	 * @param app
	 * @param title
	 * @param handler
	 * @param kernel
	 */
	public InputDialogCylinderTwoPointsRadius(AppD app, String title,
			InputHandler handler, GeoPointND a, GeoPointND b, Kernel kernel) {
		super(app, title, handler, kernel);

		this.a = a;
		this.b = b;

	}

	@Override
	protected GeoElement createOutput(GeoNumberValue num) {
		return kernel.getManager3D().cylinderLimited(null, a, b, num)[0];
	}

}
