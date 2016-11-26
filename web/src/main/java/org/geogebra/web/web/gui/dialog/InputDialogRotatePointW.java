package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.KeyUpHandler;

public class InputDialogRotatePointW extends InputDialogRotate implements KeyUpHandler {


	private GeoPointND[] points;
	
	private Kernel kernel;
	private static String defaultRotateAngle = Unicode.FORTY_FIVE_DEGREES;

	public InputDialogRotatePointW(AppW app, String title,
			InputHandler handler, GeoPolygon[] polys, GeoPointND[] points,
			GeoElement[] selGeos, Kernel kernel, EuclidianController ec) {
		super(app, title, handler, polys, selGeos, kernel, ec);

		this.points = points;	}


	protected void processInput(AsyncOperation<String> callback) {

		DialogManager.rotateObject(app, inputPanel.getText(),
				rbClockWise.getValue(), polys,
				new DialogManager.CreateGeoForRotatePoint(points[0]), selGeos,
				ec, this,
				callback);
		

	}
}
