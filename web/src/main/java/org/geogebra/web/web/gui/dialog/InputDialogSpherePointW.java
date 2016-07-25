package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.html5.main.AppW;

public class InputDialogSpherePointW extends InputDialogRadiusW {

	private GeoPointND geoPoint;

	public InputDialogSpherePointW(AppW app, String title,
            InputHandler handler, GeoPointND center, Kernel kernel) {
	    super(app, title, handler, kernel);
	    this.geoPoint = center;
    }

	@Override
	protected GeoElement createOutput(GeoNumberValue num) {
		return kernel.getManager3D().Sphere(null, geoPoint, num);
    }

}
