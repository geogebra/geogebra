package geogebra.common.gui.dialog;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;

import java.util.ArrayList;

public abstract class DialogManager {
	
	protected AbstractApplication app;

	public DialogManager() {
	}
	
	public DialogManager(AbstractApplication app) {
		this.app = app;
		
	}

	public abstract boolean showFunctionInspector(GeoFunction geoFunction);

	public abstract void showPropertiesDialog(ArrayList<GeoElement> geos);

	public abstract void showRedefineDialog(GeoElement geoElement, boolean b);

	public abstract void showNumberInputDialogSegmentFixed(String menu,
			GeoPoint2 geoPoint2);

	public abstract void showNumberInputDialogAngleFixed(String menu,
			GeoSegment[] selectedSegments, GeoPoint2[] selectedPoints,
			GeoElement[] selGeos);

	public abstract void showTextCreationDialog(GeoPointND loc);

	public abstract boolean showSliderCreationDialog(int x, int y);

	public abstract void showNumberInputDialogRotate(String menu,
			GeoPolygon[] selectedPolygons, GeoPoint2[] selectedPoints,
			GeoElement[] selGeos);

	public abstract void showNumberInputDialogDilate(String menu,
			GeoPolygon[] selectedPolygons, GeoPoint2[] selectedPoints,
			GeoElement[] selGeos);

	public abstract void showNumberInputDialogRegularPolygon(String menu,
			GeoPoint2 geoPoint2, GeoPoint2 geoPoint22);

	public abstract void showBooleanCheckboxCreationDialog(Point loc, GeoBoolean bool);

	public abstract void showNumberInputDialogCirclePointRadius(String menu,
			GeoPointND geoPointND, AbstractEuclidianView view);

	public abstract NumberValue showNumberInputDialog(String title, String message,
			String initText);

	public abstract Object[] showAngleInputDialog(String title, String message,
			String initText);

	public abstract boolean showButtonCreationDialog(int x, int y, boolean textfield);
	
	public static boolean makeRegularPolygon(AbstractApplication app, String inputString, GeoPoint2 geoPoint1, GeoPoint2 geoPoint2) {
		if (inputString == null || "".equals(inputString) ) {
			return false;
		}
		
		Kernel kernel = app.getKernel();
		Construction cons = kernel.getConstruction();

		// avoid labeling of num
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

	    GeoElement[] result = kernel.getAlgebraProcessor().processAlgebraCommand(inputString, false);

		cons.setSuppressLabelCreation(oldVal);
		
	    
	    boolean success = result != null && result[0].isNumberValue();
	    
	    if (!success) {
	    	return false;
	    }


		GeoElement[] geos = kernel.RegularPolygon(null, geoPoint1, geoPoint2, (NumberValue) result[0]);
		GeoElement[] onlypoly = { null };
		if (geos != null) {
			onlypoly[0] = geos[0];
			app.storeUndoInfo();
			app.getActiveEuclidianView().getEuclidianController().memorizeJustCreatedGeos(onlypoly);
		}
		
		return true;

	}

}
