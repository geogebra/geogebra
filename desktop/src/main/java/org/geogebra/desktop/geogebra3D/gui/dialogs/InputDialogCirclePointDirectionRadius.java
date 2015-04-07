package org.geogebra.desktop.geogebra3D.gui.dialogs;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.desktop.gui.dialog.InputDialogRadius;
import org.geogebra.desktop.main.AppD;

/**
 * 
 *
 */
public class InputDialogCirclePointDirectionRadius extends InputDialogRadius {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GeoPointND geoPoint;

	private GeoDirectionND forAxis;

	/**
	 * 
	 * @param app
	 * @param title
	 * @param handler
	 * @param point
	 * @param forAxis
	 * @param kernel
	 */
	public InputDialogCirclePointDirectionRadius(AppD app, String title,
			InputHandler handler, GeoPointND point, GeoDirectionND forAxis,
			Kernel kernel) {
		super(app, title, handler, kernel);

		geoPoint = point;
		this.forAxis = forAxis;

	}

	@Override
	protected GeoElement createOutput(NumberValue num) {

		return kernel.getManager3D().Circle3D(null, geoPoint, num, forAxis);
	}

}
