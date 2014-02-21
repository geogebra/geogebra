package geogebra.html5.gui.laf;

import geogebra.web.gui.menubar.GeoGebraMenuW;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.main.AppW;

public class GLookAndFeel {

	public GeoGebraMenuW getMenuBar(AppW app) {
	    return new GeoGebraMenubarW(app);
    }

	public boolean undoRedoSupported() {
	    return true;
    }
	
	public boolean isSmart() {
		return false;
	}

}
