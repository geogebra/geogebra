package geogebra3D.gui.dialogs;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.dialog.handler.NumberInputHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.Transformable;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.gui.dialog.DialogManagerD;
import geogebra.gui.dialog.InputDialogD;
import geogebra.main.AppD;
import geogebra3D.App3D;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidianForPlane.EuclidianViewForPlane;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 3D version of the dialog manager.
 */
public class DialogManager3D extends DialogManagerD {
	/**
	 * Construct 3D dialog manager.
	 * 
	 * Use {@link App3D} instead of {@link AppD}
	 * 
	 * @param app Instance of the 3d application object
	 */
	public DialogManager3D(App3D app) {
		super(app);
	}
	
	@Override
	public void showNumberInputDialogCirclePointRadius(String title, GeoPointND geoPoint1,  EuclidianView view) {
		if (((GeoElement) geoPoint1).isGeoElement3D() || (view instanceof EuclidianViewForPlane)) {
			//create a circle parallel to plane containing the view
			showNumberInputDialogCirclePointDirectionRadius(title, geoPoint1, view.getDirection());
		} else {
			//create 2D circle
			super.showNumberInputDialogCirclePointRadius(title, geoPoint1, view);
		}		
	}
	
	/**
	 * @param title 
	 * @param geoPoint 
	 * @param forAxis 
	 * 
	 */
	public void showNumberInputDialogCirclePointDirectionRadius(String title, GeoPointND geoPoint, GeoDirectionND forAxis) {
		NumberInputHandler handler = new NumberInputHandler(app.getKernel().getAlgebraProcessor());
		InputDialogD id = new InputDialogCirclePointDirectionRadius((AppD) app, title, handler, geoPoint, forAxis, app.getKernel());
		id.setVisible(true);
	}
	
	/**
	 * 
	 * @param title
	 * @param geoPoint
	 */
	public void showNumberInputDialogSpherePointRadius(String title, GeoPointND geoPoint) {
		NumberInputHandler handler = new NumberInputHandler(app.getKernel().getAlgebraProcessor());
		InputDialogD id = new InputDialogSpherePointRadius((AppD) app, title, handler, geoPoint, app.getKernel());
		id.setVisible(true);
	}
	
	
	/**
	 * for creating a cone
	 * @param title
	 * @param a basis center
	 * @param b apex point
	 */
	public void showNumberInputDialogConeTwoPointsRadius(String title, GeoPointND a, GeoPointND b) {
		NumberInputHandler handler = new NumberInputHandler(app.getKernel().getAlgebraProcessor());
		InputDialogD id = new InputDialogConeTwoPointsRadius((AppD) app, title, handler, a, b, app.getKernel());
		id.setVisible(true);
	}
	
	/**
	 * for creating a cylinder
	 * @param title
	 * @param a basis center
	 * @param b top center
	 */
	public void showNumberInputDialogCylinderTwoPointsRadius(String title, GeoPointND a, GeoPointND b) {
		NumberInputHandler handler = new NumberInputHandler(app.getKernel().getAlgebraProcessor());
		InputDialogD id = new InputDialogCylinderTwoPointsRadius((AppD) app, title, handler, a, b, app.getKernel());
		id.setVisible(true);
	}
	
	
	public void showNumberInputDialogRotate(String title, GeoPolygon[] polys,
			GeoLineND[] selectedLines, GeoElement[] selGeos,
            EuclidianController3D ec) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialogD id = new InputDialogRotateAxis(((AppD) app), title, handler,
				polys, selectedLines, selGeos, ec);
		id.setVisible(true);

	}
	
	
	
	public static String rotateObject(App app, String inputText,
			boolean clockwise, GeoPolygon[] polys, GeoLineND[] lines,
			GeoElement[] selGeos,
			EuclidianController3D ec) {	
		String defaultRotateAngle = "45" + "\u00b0";		
		String angleText = inputText;
		Kernel kernel = app.getKernel();
		

		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);


		// negative orientation ?
		if (ec.viewOrientationForClockwise(clockwise,lines[0])) {
			inputText = "-(" + inputText + ")";
		}

		GeoElement[] result = kernel.getAlgebraProcessor().processAlgebraCommand(inputText, false);

		cons.setSuppressLabelCreation(oldVal);


		boolean success = result != null && result[0] instanceof GeoNumberValue;

		if (success) {
			// GeoElement circle = kernel.Circle(null, geoPoint1,
			// ((NumberInputHandler)inputHandler).getNum());
			GeoNumberValue num = (GeoNumberValue) result[0];
			// geogebra.gui.AngleInputDialog dialog =
			// (geogebra.gui.AngleInputDialog) ob[1];

			// keep angle entered if it ends with 'degrees'
			if (angleText.endsWith("\u00b0"))
				defaultRotateAngle = angleText;


			if (polys.length == 1) {

				GeoElement[] geos = ec.rotateAroundLine(polys[0], num, lines[0]);
				if (geos != null) {
					app.storeUndoInfo();
					ec.memorizeJustCreatedGeos(geos);
				}
				return defaultRotateAngle;
			}

			
			ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
			for (int i = 0; i < selGeos.length; i++) {
				if (selGeos[i] != lines[0]) {
					if (selGeos[i] instanceof Transformable) {
						ret.addAll(Arrays.asList(ec.rotateAroundLine(selGeos[i], num, lines[0])));
					} else if (selGeos[i].isGeoPolygon()) {
						ret.addAll(Arrays.asList(ec.rotateAroundLine(selGeos[i], num, lines[0])));
					}
				}
			}
			if (!ret.isEmpty()) {
				app.storeUndoInfo();
				ec.memorizeJustCreatedGeos(ret);
			}
			
		}
		return defaultRotateAngle;
	}

	
	
	
	public static class Factory extends DialogManagerD.Factory {
		@Override
		public DialogManagerD create(AppD app) {
			if(!(app instanceof App3D)) {
				throw new IllegalArgumentException();
			}
			
			DialogManager3D dialogManager = new DialogManager3D((App3D)app);
			return dialogManager;
		}
	}
	
	
	
}
