package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.DomEvent;

/**
 * Web dialog for regular polygons
 */
public class InputDialogRegularPolygonW extends InputDialogW {
	private GeoPointND geoPoint1;
	private GeoPointND geoPoint2;
	private GeoCoordSys2D direction;
	private EuclidianController ec;

	/**
	 * @param app
	 *            application
	 * @param ec
	 *            controller
	 * @param title
	 *            title
	 * @param handler
	 *            input handler
	 * @param point1
	 *            first vertex
	 * @param point2
	 *            second vertex
	 * @param direction
	 *            orientation
	 */
	public InputDialogRegularPolygonW(AppW app, EuclidianController ec, String title,
			InputHandler handler, GeoPointND point1, GeoPointND point2,
			GeoCoordSys2D direction) {
		super(app, app.getLocalization().getMenu("Points"), title, "4", false,
				handler, true);

		geoPoint1 = point1;
		geoPoint2 = point2;
		this.direction = direction;
		this.ec = ec;
	}

	@Override
	protected void actionPerformed(DomEvent<?> e) {
		Object source = e.getSource();
		try {
			if (source == btOK || sourceShouldHandleOK(source)) {
				processInput();
//				setVisibleForTools(!processInput());
//			} else if (source == btApply) {  //There is no apply button.
//				processInput();
			} else if (source == btCancel) {
				wrappedPopup.hide();
//				setVisibleForTools(false);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
//			setVisibleForTools(false);
		}
	}

	private void processInput() {

		DialogManager.makeRegularPolygon(app, ec, inputPanel.getText(),
				geoPoint1, geoPoint2, direction, this,
				new AsyncOperation<Boolean>() {

					@Override
					public void callback(Boolean ok) {
						if (ok) {
							wrappedPopup.hide();
						}
					}
				});
	}
}
