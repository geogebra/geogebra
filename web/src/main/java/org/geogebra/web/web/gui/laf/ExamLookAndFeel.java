package org.geogebra.web.web.gui.laf;

import org.geogebra.web.html5.euclidian.EuclidianControllerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ExamEnvironment;
import org.geogebra.web.web.gui.menubar.MainMenu;

import com.google.gwt.event.shared.HandlerRegistration;
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
	private ExamEnvironment exam;
	
	@Override
	public MainMenu getMenuBar(AppW app) {
		return new MainMenu(app);
	    
    }

	@Override
	public boolean undoRedoSupported() {
	    return true;
    }
	
	
	@Override
	public boolean isSmart() {
		return false;
	}
	
	@Override
	public ExamEnvironment getExam() {
		if (exam == null) {
			exam = new ExamEnvironment();
		}
		return exam;
	}
	
	/**
	 * Sets message to be shown when user wants to close the window
	 * (makes no sense for SMART widget)
	 */
	@Override
	public void addWindowClosingHandler(final AppW app) {
	}

	/**
	 * removes the {@link com.google.gwt.user.client.Window.ClosingHandler}
	 */
	@Override
	public void removeWindowClosingHandler() {
	}
	
	/**
	 * @return app type for API calls
	 */
	@Override
	public String getType() {
	    return "web";
    }

	@Override
	public boolean copyToClipboardSupported() {
	    return true;
    }

	@Override
	public boolean isEmbedded() {
	    return false;
    }

	@Override
    public boolean registerHandlers(Widget evPanel, EuclidianControllerW euclidiancontroller) {
	    return false;
    }

	@Override
	public boolean autosaveSupported() {
		return false;
	}

}
