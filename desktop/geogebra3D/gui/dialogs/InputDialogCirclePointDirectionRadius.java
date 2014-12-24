package geogebra3D.gui.dialogs;

import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.gui.dialog.InputDialogRadius;
import geogebra.main.AppD;

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
