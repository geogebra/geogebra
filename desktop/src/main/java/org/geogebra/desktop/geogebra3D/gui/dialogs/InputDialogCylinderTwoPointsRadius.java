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
public class InputDialogCylinderTwoPointsRadius extends InputDialogRadius {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GeoPointND a, b;

	/**
	 * 
	 * @param app
	 * @param title
	 * @param handler
	 * @param point
	 * @param kernel
	 */
	public InputDialogCylinderTwoPointsRadius(AppD app, String title,
			InputHandler handler, GeoPointND a, GeoPointND b, Kernel kernel) {
		super(app, title, handler, kernel);

		this.a = a;
		this.b = b;

	}

	@Override
	protected GeoElement createOutput(NumberValue num) {
		return kernel.getManager3D().CylinderLimited(null, a, b, num)[0];
	}

}
