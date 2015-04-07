package org.geogebra.desktop.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.desktop.main.AppD;

/**
 * 
 *
 */
public class InputDialogCirclePointRadius extends InputDialogRadius {

	private GeoPoint geoPoint1;

	/**
	 * 
	 * @param app
	 * @param title
	 * @param handler
	 * @param point1
	 * @param kernel
	 */
	public InputDialogCirclePointRadius(AppD app, String title,
			InputHandler handler, GeoPoint point1, Kernel kernel) {
		super(app, title, handler, kernel);

		geoPoint1 = point1;
	}

	@Override
	protected GeoElement createOutput(NumberValue num) {
		return kernel.getAlgoDispatcher().Circle(null, geoPoint1, num);
	}

}
