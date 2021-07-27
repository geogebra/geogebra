package org.geogebra.web.full.gui.laf;

import java.util.Date;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.lang.Language;
import org.geogebra.web.full.gui.browser.MaterialListElement;
import org.geogebra.web.full.gui.exam.ExamUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.gui.util.Cookies;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.SignInController;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents different designs/platforms of GeoGebra deployment
 */
public class GLookAndFeel implements GLookAndFeelI {
	/** width of menu */
	public static final int MENUBAR_WIDTH = 270; //TODO make it smaller - wordWrap
	/** height of header in browse gui */
	public static final int BROWSE_HEADER_HEIGHT = 61;
	/** width of panle with file sources in browse gui (GDrive, MAT) */
	public static final int PROVIDER_PANEL_WIDTH = 70;
	/** toolbar height + offset */
	public static final int TOOLBAR_OFFSET = 61;
	/** toolbar height */
	public static final int TOOLBAR_HEIGHT = 53;
	/** size of icons in view submenu of stylebar */
	public static final int VIEW_ICON_SIZE = 20;
	private HandlerRegistration windowClosingHandler;
	private HandlerRegistration windowCloseHandler;

	@Override
	public boolean undoRedoSupported() {
	    return true;
    }

	@Override
	public boolean isSmart() {
		return false;
	}

	@Override
	public boolean isTablet() {
		return false;
	}

	/**
	 * Sets message to be shown when user wants to close the window
	 * (makes no sense for SMART widget)
	 * overridden for SMART and TOUCH - they don't use a windowClosingHandler
	 */
	@Override
	public void addWindowClosingHandler(final AppW app) {
		if (app.getExam() != null) {
			return;
		}
		// popup when the user wants to exit accidentally
		if (windowClosingHandler == null) {
			this.windowClosingHandler = Window
					.addWindowClosingHandler(new Window.ClosingHandler() {
						@Override
						public void onWindowClosing(ClosingEvent event) {
							event.setMessage(app.getLocalization().getMenu(
									"CloseApplicationLoseUnsavedData"));
						}
					});
		}

		if (this.windowCloseHandler == null) {
			// onClose is called, if user leaves the page correct
			// not called if browser crashes
			this.windowCloseHandler = Window
					.addCloseHandler(new CloseHandler<Window>() {

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
	@Override
	public void removeWindowClosingHandler() {
		if (windowClosingHandler != null) {
			windowClosingHandler.removeHandler();
			windowClosingHandler = null;
		}
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
	public String getLoginListener() {
	    return null;
    }

	@Override
	public boolean isEmbedded() {
	    return false;
    }

	/**
	 * @param m
	 *            material
	 * @param app
	 *            app
	 * @param isLocal
	 *            whether his is a local file
	 * @return panel with material preview + actions
	 */
	public MaterialListElement getMaterialElement(Material m, AppW app, boolean isLocal) {
	    return new MaterialListElement(m, app, isLocal);
    }

	@Override
	public SignInController getSignInController(App app) {
		return new SignInController(app, 0, null);
    }

	@Override
	public String getClientId() {
		return GeoGebraConstants.GOOGLE_CLIENT_ID;
	}

	@Override
	public boolean isExternalLoginAllowed() {
		return true;
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
	public boolean supportsGoogleDrive() {
		return true;
	}

	@Override
	public boolean supportsLocalSave() {
		return false;
	}

	@Override
	public boolean examSupported() {
		return false;
	}

	@Override
	public boolean printSupported() {
		return true;
	}

	@Override
	public Platform getPlatform(int dim, String appName) {
		return dim > 2 ? Platform.WEB
				: Platform.WEB_FOR_BROWSER_2D;
	}

	@Override
	public void toggleFullscreen(boolean full) {
		ExamUtil.toggleFullscreen(full);
		Browser.toggleFullscreen(full, null);
	}

	@Override
	public void storeLanguage(String lang) {
		if (Browser.isGeoGebraOrg()) {
			Date exp = new Date(
					System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365);
			Cookies.setCookie("GeoGebraLangUI",
					Language.getClosestGWTSupportedLanguage(lang).getLocaleGWT(), exp,
					"geogebra.org", "/");
		} else {
			BrowserStorage.LOCAL.setItem("GeoGebraLangUI", lang);
		}
	}

	@Override
	public String getFrameStyleName() {
		return "GeoGebra";
	}

	@Override
	public boolean isOfflineExamSupported() {
		return false;
	}

	@Override
	public boolean hasHeader() {
		return true;
	}

	@Override
	public boolean hasLoginButton() {
		// only in web
		return true;
	}

}
