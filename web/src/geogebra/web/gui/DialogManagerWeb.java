package geogebra.web.gui;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.gui.dialog.DialogManager;
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

public class DialogManagerWeb extends DialogManager {

	public DialogManagerWeb(AbstractApplication app) {
	    super(app);
    }

	@Override
    public boolean showFunctionInspector(GeoFunction geoFunction) {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public void showPropertiesDialog(ArrayList<GeoElement> geos) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showRedefineDialog(GeoElement geoElement, boolean b) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showNumberInputDialogSegmentFixed(String menu,
            GeoPoint2 geoPoint2) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showNumberInputDialogAngleFixed(String menu,
            GeoSegment[] selectedSegments, GeoPoint2[] selectedPoints,
            GeoElement[] selGeos) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showTextCreationDialog(GeoPointND loc) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public boolean showSliderCreationDialog(int x, int y) {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public void showNumberInputDialogRotate(String menu,
            GeoPolygon[] selectedPolygons, GeoPoint2[] selectedPoints,
            GeoElement[] selGeos) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showNumberInputDialogDilate(String menu,
            GeoPolygon[] selectedPolygons, GeoPoint2[] selectedPoints,
            GeoElement[] selGeos) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
	public void showNumberInputDialogRegularPolygon(String menu,
			GeoPoint2 geoPoint1, GeoPoint2 geoPoint2) {
		
		String inputString = prompt(menu + " " + app.getPlain("Points"), "4");
		
		makeRegularPolygon(inputString, geoPoint1, geoPoint2);
	}

	public static native String prompt(String question, String def) /*-{
		return $wnd.prompt(question, def);
	}-*/;


	@Override
    public void showBooleanCheckboxCreationDialog(Point loc, GeoBoolean bool) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showNumberInputDialogCirclePointRadius(String menu,
            GeoPointND geoPointND, AbstractEuclidianView view) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public NumberValue showNumberInputDialog(String title, String message,
            String initText) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Object[] showAngleInputDialog(String title, String message,
            String initText) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public boolean showButtonCreationDialog(int x, int y, boolean textfield) {
	    // TODO Auto-generated method stub
	    return false;
    }

}
