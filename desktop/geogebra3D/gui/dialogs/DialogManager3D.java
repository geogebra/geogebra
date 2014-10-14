package geogebra3D.gui.dialogs;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.dialog.handler.NumberInputHandler;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.gui.dialog.DialogManagerD;
import geogebra.gui.dialog.InputDialogD;
import geogebra.main.AppD;
import geogebra3D.App3D;
import geogebra3D.euclidianForPlane.EuclidianViewForPlaneD;

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
					view.getDirection());
		} else {
			// create 2D circle
			super.showNumberInputDialogCirclePointRadius(title, geoPoint1, view);
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
			GeoPointND geoPoint, GeoDirectionND forAxis) {
		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
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
			GeoPointND geoPoint) {
		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
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
			GeoPointND a, GeoPointND b) {
		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
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
			GeoPointND a, GeoPointND b) {
		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialogD id = new InputDialogCylinderTwoPointsRadius((AppD) app,
				title, handler, a, b, app.getKernel());
		id.setVisible(true);
	}

	@Override
	public void showNumberInputDialogRotate(String title, GeoPolygon[] polys,
			GeoLineND[] selectedLines, GeoElement[] selGeos,
			EuclidianController ec) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
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
