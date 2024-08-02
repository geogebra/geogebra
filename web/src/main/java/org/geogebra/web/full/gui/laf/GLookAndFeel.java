package org.geogebra.web.full.gui.laf;

import java.util.Date;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.ResourceAction;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.lang.Language;
import org.geogebra.gwtutil.Cookies;
import org.geogebra.web.full.gui.exam.ExamUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.laf.SignInControllerI;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.SignInController;

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.promise.Promise;
import jsinterop.base.Js;

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
	private EventListener windowClosingHandler;
	private EventListener windowCloseHandler;

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
		if (GlobalScope.examController.isExamActive()) {
			return;
		}
		// popup when the user wants to exit accidentally
		if (windowClosingHandler == null) {
			this.windowClosingHandler = this::askForSave;
			app.getGlobalHandlers().addEventListener(DomGlobal.window,
					"beforeunload", windowClosingHandler);
		}

		if (this.windowCloseHandler == null) {
			// onClose is called, if user leaves the page correct
			// not called if browser crashes
			this.windowCloseHandler = event -> app.getFileManager().deleteAutoSavedFile();
			app.getGlobalHandlers().addEventListener(DomGlobal.window, "unload",
					windowCloseHandler);
		}
	}

	private void askForSave(Event evt) {
		// Message set by browser https://developer.chrome.com/blog/chrome-51-deprecations/
		Js.asPropertyMap(evt).set("returnValue", 1);
		evt.preventDefault();
	}

	/**
	 * removes the 'beforeunload' handler
	 * overridden for SMART and TOUCH - they don't use a windowClosingHandler
	 */
	@Override
	public void removeWindowClosingHandler() {
		if (windowClosingHandler != null) {
			DomGlobal.window.removeEventListener("beforeunload", windowClosingHandler);
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

	@Override
	public SignInControllerI getSignInController(App app) {
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
	public ResourceAction getDisplayAction(ResourceAction action) {
		return action;
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
					System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365);
			Language language1 = Language.fromLanguageTagOrLocaleString(lang);
			Cookies.setCookie("GeoGebraLangUI",
					language1.toLanguageTag(), exp,
					"geogebra.org", "/");
		} else {
			BrowserStorage.LOCAL.setItem("GeoGebraLangUI", lang);
		}
	}

	@Override
	public Promise<String> loadLanguage() {
		String cookieLang = Cookies.getCookie("GeoGebraLangUI");
		if (!StringUtil.empty(cookieLang)) {
			return Promise.resolve(cookieLang);
		} else {
			return Promise.resolve(BrowserStorage.LOCAL.getItem("GeoGebraLangUI"));
		}
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
		return Browser.isNotCrossOriginIframe();
	}

}
