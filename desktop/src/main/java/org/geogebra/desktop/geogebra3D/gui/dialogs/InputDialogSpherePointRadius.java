package org.geogebra.desktop.geogebra3D.gui.dialogs;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.desktop.gui.dialog.InputDialogRadius;
import org.geogebra.desktop.main.AppD;

/**
 * 
 *
 */
public class InputDialogSpherePointRadius extends InputDialogRadius {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GeoPointND geoPoint;

	/**
	 * 
	 * @param app
	 * @param title
	 * @param handler
	 * @param point
	 * @param kernel
	 */
	public InputDialogSpherePointRadius(AppD app, String title,
			InputHandler handler, GeoPointND point, Kernel kernel) {
		super(app, title, handler, kernel);

		geoPoint = point;

	}

	@Override
	protected GeoElement createOutput(NumberValue num) {
		return kernel.getManager3D().Sphere(null, geoPoint, num);
	}

}
