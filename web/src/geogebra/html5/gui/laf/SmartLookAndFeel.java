package geogebra.html5.gui.laf;

import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.gui.menubar.GeoGebraMenubarW2;
import geogebra.web.main.AppW;

public class SmartLookAndFeel extends GLookAndFeel{
	@Override
    public GeoGebraMenubarW getMenuBar(AppW app) {
	    return new GeoGebraMenubarW2(app);
    }
	
	@Override
    public boolean undoRedoSupported() {
	    return false;
    }
}
