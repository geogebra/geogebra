package geogebra.web.gui.laf;

import geogebra.html5.euclidian.EuclidianControllerW;
import geogebra.html5.main.AppW;
import geogebra.web.gui.menubar.MainMenu;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * LookAndFeel for the exam environment.
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 *
 */
public class ExamLookAndFeel extends GLookAndFeel{

	public static final int MENUBAR_WIDTH = 270; //TODO make it smaller - wordWrap
	public static final int BROWSE_HEADER_HEIGHT = 61;
	public static final int PROVIDER_PANEL_WIDTH = 70;
	public static final int PHONE_HEADER_HEIGHT = 43;
	public static final int PHONE_SEARCH_PANEL_HEIGHT = 57;
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
	
	public boolean isExam() {
		return true;
	}
	
	/**
	 * Sets message to be shown when user wants to close the window
	 * (makes no sense for SMART widget)
	 */
	public void addWindowClosingHandler(final AppW app) {
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

	public boolean isEmbedded() {
	    return false;
    }

	@Override
    public boolean registerHandlers(Widget evPanel, EuclidianControllerW euclidiancontroller) {
	    return false;
    }


}
