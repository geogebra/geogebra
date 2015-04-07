package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;

public class DialogManager3DW extends DialogManagerW{
	public DialogManager3DW(App app) {
	    super(app);
    }

	/**
	 * 
	 * @param title
	 * @param geoPoint
	 */
	@Override
    public void showNumberInputDialogSpherePointRadius(String title, GeoPointND center){
		NumberInputHandler handler = new NumberInputHandler(app.getKernel().getAlgebraProcessor());
		InputDialogW id = new InputDialogSpherePointW((AppW) app, title, handler, center, app.getKernel());
		id.setVisible(true);
		
	}

	/**
	 * for creating a cone
	 * @param title
	 * @param a basis center
	 * @param b apex point
	 */
	@Override
    public void showNumberInputDialogConeTwoPointsRadius(String title, GeoPointND a, GeoPointND b){
		NumberInputHandler handler = new NumberInputHandler(app.getKernel().getAlgebraProcessor());
		InputDialogW id = new InputDialogConeTwoPointsRadiusW((AppW) app, title, handler, a, b, app.getKernel());
		id.setVisible(true);
	}
	
	

	@Override
    public void showNumberInputDialogCylinderTwoPointsRadius(String title, GeoPointND a, GeoPointND b) {
		NumberInputHandler handler = new NumberInputHandler(app.getKernel().getAlgebraProcessor());
		InputDialogW id = new InputDialogCylinderTwoPointsRadiusW((AppW) app, title, handler, a, b, app.getKernel());
		id.setVisible(true);
	}
	
	
	@Override
    public void showNumberInputDialogCirclePointDirectionRadius(String title, GeoPointND geoPoint, GeoDirectionND forAxis) {
		NumberInputHandler handler = new NumberInputHandler(app.getKernel().getAlgebraProcessor());
		InputDialogW id = new InputDialogCirclePointDirectionRadiusW((AppW) app, title, handler, geoPoint, forAxis, app.getKernel());
		id.setVisible(true);
		
	}
	
	
	@Override
    public void showNumberInputDialogRotate(String title, GeoPolygon[] polys,
		GeoLineND[] selectedLines, GeoElement[] selGeos,
        EuclidianController ec) {

	NumberInputHandler handler = new NumberInputHandler(app.getKernel()
			.getAlgebraProcessor());
	InputDialogW id = new InputDialogRotateAxisW(((AppW) app), title, handler,
			polys, selectedLines, selGeos, ec);
	id.setVisible(true);
		
	}

}
