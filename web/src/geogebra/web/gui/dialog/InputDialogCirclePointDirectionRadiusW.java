package geogebra.web.gui.dialog;

import geogebra.common.gui.dialog.handler.NumberInputHandler;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.html5.main.AppW;

public class InputDialogCirclePointDirectionRadiusW extends InputDialogRadiusW {

	

	private GeoPointND geoPoint;
	private GeoDirectionND forAxis;

	public InputDialogCirclePointDirectionRadiusW(AppW app, String title,
            NumberInputHandler handler, GeoPointND geoPoint,
            GeoDirectionND forAxis, Kernel kernel) {
		super(app, title, handler, kernel);
		this.geoPoint = geoPoint;
		this.forAxis = forAxis;
    }

	@Override
    protected GeoElement createOutput(NumberValue num) {
		return kernel.getManager3D().Circle3D(
				null,
				geoPoint,
				num,
				forAxis);
    }

}
