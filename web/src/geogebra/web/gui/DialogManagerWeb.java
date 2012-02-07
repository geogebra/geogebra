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
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.Unicode;

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
    public void showTextCreationDialog(GeoPointND loc) {
	    // TODO Auto-generated method stub
	    
    }

	public static native String promptNative(String question, String def) /*-{
	return $wnd.prompt(question, def);
}-*/;

	public static native boolean confirmNative(String question) /*-{
	return $wnd.confirm(question);
}-*/;


	@Override
    public void showBooleanCheckboxCreationDialog(Point loc, GeoBoolean bool) {
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

	@Override
    protected String prompt(String message, String def) {
	    return promptNative(message, def);
    }

	@Override
    protected boolean confirm(String string) {
	    return confirmNative(string);
    }

	@Override
    public void closeAll() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showRenameDialog(GeoElement geo, boolean b, String label,
            boolean c) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showTextDialog(GeoText geo) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showOptionsDialog(int tabEuclidian) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showPropertiesDialog() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showToolbarConfigDialog() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public NumberValue showNumberInputDialog(String menu, String plain,
            Object object, boolean b, String plain2) {
	    // just needed for 3D
	    return null;
    }


}
