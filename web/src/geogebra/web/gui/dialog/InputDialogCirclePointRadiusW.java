package geogebra.web.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.web.main.AppW;

public class InputDialogCirclePointRadiusW extends InputDialogRadiusW{
	private GeoPoint geoPoint1;

	/**
	 * 
	 * @param app
	 * @param title
	 * @param handler
	 * @param point1
	 * @param kernel
	 */
	public InputDialogCirclePointRadiusW(AppW app, String title,
			InputHandler handler, GeoPoint point1, Kernel kernel) {
		super(app, title, handler, kernel);

		geoPoint1 = point1;
	}

	@Override
	protected GeoElement createOutput(NumberValue num) {
		return kernel.getAlgoDispatcher().Circle(null, geoPoint1, num);
	}

}
