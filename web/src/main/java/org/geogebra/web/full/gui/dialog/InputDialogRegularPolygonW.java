package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentInputDialog;
import org.geogebra.web.shared.components.DialogData;

/**
 * Web dialog for regular polygons
 */
public class InputDialogRegularPolygonW extends ComponentInputDialog {
	private GeoPointND geoPoint1;
	private GeoPointND geoPoint2;
	private GeoCoordSys2D direction;
	private EuclidianController ec;

	/**
	 * @param app
	 *            application
	 * @param data
	 * 			  dialog trans keys
	 * @param ec
	 *            controller
	 * @param handler
	 *            input handler
	 * @param point1
	 *            first vertex
	 * @param point2
	 *            second vertex
	 * @param direction
	 *            orientation
	 */
	public InputDialogRegularPolygonW(AppW app, DialogData data, EuclidianController ec,
			InputHandler handler, GeoPointND point1, GeoPointND point2,
			GeoCoordSys2D direction) {
		super(app, data, false, false, handler,
				app.getLocalization().getMenu("Points"), "4",
				1, -1, false);
		geoPoint1 = point1;
		geoPoint2 = point2;
		this.direction = direction;
		this.ec = ec;
	}

	@Override
	public void processInput() {
		DialogManager.makeRegularPolygon(app, ec, getInputText(),
				geoPoint1, geoPoint2, direction, this,
				ok -> {
					if (ok) {
						hide();
					}
				});
	}
}