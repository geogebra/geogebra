package geogebra3D.gui.dialogs;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.gui.dialog.InputDialogRotate;
import geogebra.main.AppD;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;

public class InputDialogRotateAxis extends InputDialogRotate {

	GeoLineND[] lines;
	
	public InputDialogRotateAxis(AppD app, String title, InputHandler handler,
			GeoPolygon[] polys, GeoLineND[] lines, GeoElement[] selGeos, 
			EuclidianController ec) {
		
		super(app, title, handler, polys, selGeos, ec);

		this.lines = lines;

	}

	protected boolean processInput() {

		defaultRotateAngle = DialogManager3D.rotateObject(app,
				inputPanel.getText(), rbClockWise.isSelected(), polys, lines,
				selGeos, (EuclidianController3D) ec);

		return true;
		/*
		 * 
		 * // avoid labeling of num Construction cons =
		 * kernel.getConstruction(); boolean oldVal =
		 * cons.isSuppressLabelsActive(); cons.setSuppressLabelCreation(true);
		 * 
		 * inputText = inputPanel.getText();
		 * 
		 * // negative orientation ? if (rbClockWise.isSelected()) { inputText =
		 * "-(" + inputText + ")"; }
		 * 
		 * boolean success = inputHandler.processInput(inputText);
		 * 
		 * cons.setSuppressLabelCreation(oldVal);
		 * 
		 * if (success) { // GeoElement circle = kernel.Circle(null, geoPoint1,
		 * // ((NumberInputHandler)inputHandler).getNum()); NumberValue num =
		 * ((NumberInputHandler) inputHandler).getNum(); //
		 * geogebra.gui.AngleInputDialog dialog = //
		 * (geogebra.gui.AngleInputDialog) ob[1]; String angleText = getText();
		 * 
		 * // keep angle entered if it ends with 'degrees' if
		 * (angleText.endsWith("\u00b0")) defaultRotateAngle = angleText; else
		 * defaultRotateAngle = "45" + "\u00b0";
		 * 
		 * if (polys.length == 1) {
		 * 
		 * GeoElement[] geos = kernel.Rotate(null, polys[0], num, points[0]); if
		 * (geos != null) { app.storeUndoInfo();
		 * kernel.getApplication().getActiveEuclidianView()
		 * .getEuclidianController() .memorizeJustCreatedGeos(geos); } return
		 * true; } ArrayList<GeoElement> ret = new ArrayList<GeoElement>(); for
		 * (int i = 0; i < selGeos.length; i++) { if (selGeos[i] != geoPoint1) {
		 * if (selGeos[i] instanceof Transformable) {
		 * ret.addAll(Arrays.asList(kernel.Rotate(null, selGeos[i], num,
		 * geoPoint1))); } else if (selGeos[i].isGeoPolygon()) {
		 * ret.addAll(Arrays.asList(kernel.Rotate(null, selGeos[i], num,
		 * geoPoint1))); } } } if (!ret.isEmpty()) { app.storeUndoInfo();
		 * kernel.getApplication().getActiveEuclidianView()
		 * .getEuclidianController().memorizeJustCreatedGeos(ret); } return
		 * true; }
		 * 
		 * return false;
		 */
	}

}
