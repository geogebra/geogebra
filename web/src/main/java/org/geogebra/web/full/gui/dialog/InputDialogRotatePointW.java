package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.DialogData;

/**
 * Dialog for rotation around a point
 */
public class InputDialogRotatePointW extends InputDialogRotateW {

	private GeoPointND[] points;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog data
	 * @param handler
	 *            input handler
	 * @param polys
	 *            selected polygons
	 * @param points
	 *            selected points
	 * @param selGeos
	 *            selected geos
	 * @param ec
	 *            controller
	 */
	public InputDialogRotatePointW(AppW app, DialogData data,
			InputHandler handler, GeoPolygon[] polys, GeoPointND[] points,
			GeoElement[] selGeos, EuclidianController ec) {
		super(app, data, handler, polys, selGeos, ec);
		this.points = points;
	}

	@Override
	protected void processInput(AsyncOperation<String> callback) {
		DialogManager.rotateObject(app, getInputText(),
				rbClockWise.getValue(), polys,
				new DialogManager.CreateGeoForRotatePoint(points[0]), selGeos,
				ec, this,
				callback);
	}
}