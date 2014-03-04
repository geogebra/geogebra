package geogebra.html5.gui.laf;

import geogebra.common.main.App;
import geogebra.web.gui.menubar.GeoGebraMenuW;
import geogebra.web.gui.menubar.GeoGebraMenubarSMART;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Window;

/**
 * @author geogebra
 * Look and Feel for SMART
 *
 */
public class SmartLookAndFeel extends GLookAndFeel{
	@Override
    public GeoGebraMenuW getMenuBar(AppW app) {
		GeoGebraMenubarSMART menubar = new GeoGebraMenubarSMART(app);
		Window.addResizeHandler(menubar);
		return menubar;
    }
	
	@Override
    public boolean undoRedoSupported() {
	    return false;
    }
	
	@Override
    public boolean isSmart() {
		return true;
	}
	
	public void setCloseMessage(final App appl) {
		//no message on smart board
	}
}
