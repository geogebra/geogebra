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
	
	public boolean isModern() {
		return false;
	}
	
	public boolean isSmart() {
		return false;
	}
	/**
	 * Sets message to be shown when user wants to closr the window
	 * (makes no sense for SMART widget)
	 */
	public void setCloseMessage(final Localization loc) {
		// popup when the user wants to exit accidentally
        Window.addWindowClosingHandler(new Window.ClosingHandler() {
            public void onWindowClosing(ClosingEvent event) {
            	event.setMessage(loc.getPlain("CloseApplicationLoseUnsavedData"));
            }
        });
	}

	/**
	 * Opens GeoGebraTube material in a new window (or similar for tablet apps / smart widget)
	 * @param id material id
	 */
	public void open(int id, AppW app) {
	    openTubeWindow(id);
    }

	private native void openTubeWindow(int id)/*-{
		$wnd.open("http://www.geogebratube.org/material/show/id/"+id);
	}-*/;
	
	/**
	 * @return app type for API calls
	 */
	public String getType() {
	    return "web";
    }

	public boolean copyToClipboardSupported() {
	    return true;
    }

	public String getLoginListener() {
	    return null;
    }

	public String getInsertWorksheetTitle() {
	    return "View";
    }

}
