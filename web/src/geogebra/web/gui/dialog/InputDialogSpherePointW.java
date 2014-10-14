package geogebra.web.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.html5.main.AppW;

public class InputDialogSpherePointW extends InputDialogRadiusW {

	private GeoPointND geoPoint;

	public InputDialogSpherePointW(AppW app, String title,
            InputHandler handler, GeoPointND center, Kernel kernel) {
	    super(app, title, handler, kernel);
	    this.geoPoint = center;
    }

	@Override
    protected GeoElement createOutput(NumberValue num) {
		return kernel.getManager3D().Sphere(null, geoPoint, num);
    }

}
