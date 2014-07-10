package geogebra.html5.gui.laf;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.browser.MaterialListElement;
import geogebra.html5.gui.browser.SignInButton;
import geogebra.html5.main.AppWeb;
import geogebra.web.gui.menubar.MainMenu;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;

public class GLookAndFeel {

	public static final int COMMAND_LINE_HEIGHT = 43;
	public static final int MENUBAR_WIDTH_MAX = 204;
	public static final int TOOLBAR_HEIGHT = 53;
	
	public MainMenu getMenuBar(AppW app) {
		return new MainMenu(app);
	    
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

	

	public boolean isEmbedded() {
	    return false;
    }

	public MaterialListElement getMaterialElement(Material m, AppWeb app) {
	    return new MaterialListElement(m, app);
    }

	public SignInButton getSignInButton(App app) {
	    return new SignInButton(app, 0);
    }

}
