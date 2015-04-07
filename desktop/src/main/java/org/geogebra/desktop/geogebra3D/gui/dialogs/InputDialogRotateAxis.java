package org.geogebra.desktop.geogebra3D.gui.dialogs;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.desktop.gui.dialog.InputDialogRotate;
import org.geogebra.desktop.main.AppD;

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
