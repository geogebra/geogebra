package geogebra.web.gui.dialog;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import geogebra.common.gui.dialog.handler.NumberInputHandler;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.html5.main.AppW;

public class InputDialogRotateAxisW extends InputDialogRotate {

	private GeoLineND[] lines;

	public InputDialogRotateAxisW(AppW app, String title,
            NumberInputHandler handler, GeoPolygon[] polys,
            GeoLineND[] selectedLines, GeoElement[] selGeos,
            EuclidianController ec) {
		super(app, title, handler, polys, selGeos, app.getKernel(), ec);
		this.lines = selectedLines;
    }

	@Override
    protected boolean processInput() {
		String defaultRotateAngle1 = EuclidianController3D.rotateObject(app,
				inputPanel.getText(), rbClockWise.getValue(), polys, lines,
				selGeos, (EuclidianController3D) ec);
		if (defaultRotateAngle1 != null) {
			defaultRotateAngle = defaultRotateAngle1;
			return true;
		}
		return false;
    }

	

}
