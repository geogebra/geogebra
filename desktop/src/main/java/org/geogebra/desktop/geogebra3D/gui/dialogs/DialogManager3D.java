package org.geogebra.desktop.geogebra3D.gui.dialogs;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.desktop.geogebra3D.App3D;
import org.geogebra.desktop.geogebra3D.euclidianForPlane.EuclidianViewForPlaneD;
import org.geogebra.desktop.gui.dialog.DialogManagerD;
import org.geogebra.desktop.gui.dialog.InputDialogD;
import org.geogebra.desktop.main.AppD;

/**
 * 3D version of the dialog manager.
 */
public class DialogManager3D extends DialogManagerD {
	/**
	 * Construct 3D dialog manager.
	 * 
	 * Use {@link App3D} instead of {@link AppD}
	 * 
	 * @param app
	 *            Instance of the 3d application object
	 */
	public DialogManager3D(App3D app) {
		super(app);
	}

	@Override
	public void showNumberInputDialogCirclePointRadius(String title,
			GeoPointND geoPoint1, EuclidianView view) {
		if (((GeoElement) geoPoint1).isGeoElement3D()
				|| (view instanceof EuclidianViewForPlaneD)) {
			// create a circle parallel to plane containing the view
			showNumberInputDialogCirclePointDirectionRadius(title, geoPoint1,
					view.getDirection(), view.getEuclidianController());
		} else {
			// create 2D circle
			super.showNumberInputDialogCirclePointRadius(title, geoPoint1,
					view);
		}
	}

	/**
	 * @param title
	 * @param geoPoint
	 * @param forAxis
	 * 
	 */
	@Override
	public void showNumberInputDialogCirclePointDirectionRadius(String title,
			GeoPointND geoPoint, GeoDirectionND forAxis,
			EuclidianController ec) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		InputDialogD id = new InputDialogCirclePointDirectionRadius((AppD) app,
				title, handler, geoPoint, forAxis, app.getKernel());
		id.setVisible(true);
	}

	/**
	 * 
	 * @param title
	 * @param geoPoint
	 */
	@Override
	public void showNumberInputDialogSpherePointRadius(String title,
			GeoPointND geoPoint, EuclidianController ec) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		InputDialogD id = new InputDialogSpherePointRadius((AppD) app, title,
				handler, geoPoint, app.getKernel());
		id.setVisible(true);
	}

	/**
	 * for creating a cone
	 * 
	 * @param title
	 * @param a
	 *            basis center
	 * @param b
	 *            apex point
	 */
	@Override
	public void showNumberInputDialogConeTwoPointsRadius(String title,
			GeoPointND a, GeoPointND b, EuclidianController ec) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		InputDialogD id = new InputDialogConeTwoPointsRadius((AppD) app, title,
				handler, a, b, app.getKernel());
		id.setVisible(true);
	}

	/**
	 * for creating a cylinder
	 * 
	 * @param title
	 * @param a
	 *            basis center
	 * @param b
	 *            top center
	 */
	@Override
	public void showNumberInputDialogCylinderTwoPointsRadius(String title,
			GeoPointND a, GeoPointND b, EuclidianController ec) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		InputDialogD id = new InputDialogCylinderTwoPointsRadius((AppD) app,
				title, handler, a, b, app.getKernel());
		id.setVisible(true);
	}

	@Override
	public void showNumberInputDialogRotate(String title, GeoPolygon[] polys,
			GeoLineND[] selectedLines, GeoElement[] selGeos,
			EuclidianController ec) {

		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		InputDialogD id = new InputDialogRotateAxis(((AppD) app), title,
				handler, polys, selectedLines, selGeos, ec);
		id.setVisible(true);

	}

	public static class Factory extends DialogManagerD.Factory {
		@Override
		public DialogManagerD create(AppD app) {
			if (!(app instanceof App3D)) {
				throw new IllegalArgumentException();
			}

			DialogManager3D dialogManager = new DialogManager3D((App3D) app);
			return dialogManager;
		}
	}

}
