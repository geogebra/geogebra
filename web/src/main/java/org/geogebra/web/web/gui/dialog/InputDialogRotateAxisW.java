package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.web.html5.main.AppW;

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
