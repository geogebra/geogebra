package org.geogebra.web.web.gui.laf;

import org.geogebra.common.main.App;
import org.geogebra.common.main.ExamEnvironment;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.browser.MaterialListElement;
import org.geogebra.web.web.gui.browser.SignInButton;
import org.geogebra.web.web.gui.menubar.MainMenu;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Widget;

public class GLookAndFeel implements GLookAndFeelI{

	public static final int MENUBAR_WIDTH = 270; //TODO make it smaller - wordWrap
	public static final int BROWSE_HEADER_HEIGHT = 61;
	public static final int PROVIDER_PANEL_WIDTH = 70;
	public static final int TOOLBAR_OFFSET = 61;
	public static final int VIEW_ICON_SIZE = 20;
	private HandlerRegistration windowClosingHandler;
	private HandlerRegistration windowCloseHandler;
	
	public MainMenu getMenuBar(AppW app) {
		return new MainMenu(app);
	    
    }

	public boolean undoRedoSupported() {
	    return true;
    }
	
	
	public boolean isSmart() {
		return false;
	}
	
	public boolean isTablet() {
		return false;
	}
	/**
	 * Sets message to be shown when user wants to close the window
	 * (makes no sense for SMART widget)
	 * overridden for SMART and TOUCH - they don't use a windowClosingHandler
	 */
	public void addWindowClosingHandler(final AppW app) {
		if (app.getExam() != null) {
			return;
		}
		// popup when the user wants to exit accidentally
        this.windowClosingHandler = Window.addWindowClosingHandler(new Window.ClosingHandler() {
            public void onWindowClosing(ClosingEvent event) {
            	event.setMessage(app.getLocalization().getPlain("CloseApplicationLoseUnsavedData"));
            }
        });
        
        if (this.windowCloseHandler == null) {
            //onClose is called, if user leaves the page correct
            //not called if browser crashes
            this.windowCloseHandler = Window.addCloseHandler(new CloseHandler<Window>() {
    			
    			@Override
    			public void onClose(CloseEvent<Window> event) {
    				app.getFileManager().deleteAutoSavedFile();
    			}
    		});
        }
	}

	/**
	 * removes the {@link com.google.gwt.user.client.Window.ClosingHandler}
	 * overridden for SMART and TOUCH - they don't use a windowClosingHandler
	 */
	public void removeWindowClosingHandler() {
		if (this.windowClosingHandler != null) {
			this.windowClosingHandler.removeHandler();
		}
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

	public MaterialListElement getMaterialElement(Material m, AppW app, boolean isLocal) {
	    return new MaterialListElement(m, app, isLocal);
    }

	public SignInButton getSignInButton(App app) {
		return new SignInButton(app, Browser.isIE9() ? 2000 : 0);
    }

	@Override
    public boolean registerHandlers(Widget evPanel, EuclidianControllerW euclidiancontroller) {
	    return false;
    }

	@Override
    public boolean autosaveSupported() {
	    return true;
    }

	@Override
    public boolean exportSupported() {
	    return true;
    }

	@Override
    public boolean externalDriveSupported() {
	    return true;
    }

	public boolean supportsGoogleDrive() {
		return true;
	}

	@Override
	public boolean supportsLocalSave() {
		return false;
	}

	public ExamEnvironment getExam() {
		// TODO Auto-generated method stub
		return null;
	}

}
