package geogebra3D.gui.dialogs;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.gui.dialog.InputDialogRotate;
import geogebra.main.AppD;

public class InputDialogRotateAxis extends InputDialogRotate {

	GeoLineND[] lines;

	public InputDialogRotateAxis(AppD app, String title, InputHandler handler,
			GeoPolygon[] polys, GeoLineND[] lines, GeoElement[] selGeos,
			EuclidianController ec) {

		super(app, title, handler, polys, selGeos, ec);

		this.lines = lines;

	}

	protected boolean processInput() {

		String defaultRotateAngle1 = EuclidianController3D.rotateObject(app,
				inputPanel.getText(), rbClockWise.isSelected(), polys, lines,
				selGeos, (EuclidianController3D) ec);
		if (defaultRotateAngle1 != null) {
			defaultRotateAngle = defaultRotateAngle1;
			return true;
		}
		return false;
	}

}
