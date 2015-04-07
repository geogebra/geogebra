package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.html5.main.AppW;

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
