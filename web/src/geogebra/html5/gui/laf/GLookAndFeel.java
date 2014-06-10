package geogebra.html5.gui.laf;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.web.gui.menubar.GeoGebraMenuW;
import geogebra.web.gui.menubar.MainMenu;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;

public class GLookAndFeel {

	public static final int COMMAND_LINE_HEIGHT = 43;
	public static final int MENUBAR_HEIGHT = 0;//35;
	public static final int MENUBAR_WIDTH_MAX = 204;
	public static final int TOOLBAR_HEIGHT = 53;
	public GeoGebraMenuW getMenuBar(AppW app) {
		MainMenu menubar = new MainMenu(app);
	    Window.addResizeHandler(menubar);
	    return menubar;
    }

	public boolean undoRedoSupported() {
	    return true;
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
	public void open(Material m, AppW app) {
	    openTubeWindow(m.getURL());
    }

	private native void openTubeWindow(String url)/*-{
		$wnd.open(url);
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

	public String getInsertWorksheetTitle(Material m) {
	    return "View";
    }

	public boolean isEmbedded() {
	    return false;
    }

	public void login(App app, String loginURL) {
		app.getDialogManager().showLogInDialog();
	    
    }

}
