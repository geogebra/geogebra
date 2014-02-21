package geogebra.html5.gui.laf;

import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.gui.menubar.GeoGebraMenubarW2;
import geogebra.web.main.AppW;

public class ModernLookAndFeel extends GLookAndFeel{
	public GeoGebraMenubarW getMenuBar(AppW app) {
	    return new GeoGebraMenubarW2(app);
    }
}
