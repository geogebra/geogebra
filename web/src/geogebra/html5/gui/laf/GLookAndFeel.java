package geogebra.html5.gui.laf;

import geogebra.common.main.Localization;
import geogebra.web.gui.menubar.GeoGebraMenuW;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;

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
	
	public void setCloseMessage(final Localization loc) {
		// popup when the user wants to exit accidentally
        Window.addWindowClosingHandler(new Window.ClosingHandler() {
            public void onWindowClosing(ClosingEvent event) {
            	event.setMessage(loc.getPlain("CloseApplicationLoseUnsavedData"));
            }
        });
	}

}
