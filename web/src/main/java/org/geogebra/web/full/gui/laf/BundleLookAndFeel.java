package org.geogebra.web.full.gui.laf;

import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.SignInController;
import org.geogebra.web.shared.ggtapi.StaticFileUrls;

/**
 * For offline browser
 */
public class BundleLookAndFeel extends GLookAndFeel {
	@Override
	public Platform getPlatform(int dim, String appName) {
		return Platform.OFFLINE;
	}

	@Override
	public void addWindowClosingHandler(final AppW app) {
		Localization loc = app.getLocalization();
		addNativeHandler(loc.getMenu("CloseApplicationLoseUnsavedData"),
				loc.getMenu("Save"), loc.getMenu("DontSave"),
				loc.getMenu("Cancel"));
	}

	private native void addNativeHandler(String message, String save,
			String noSave, String cancel) /*-{
		if ($wnd.setUnsavedMessage) {
			$wnd.setUnsavedMessage(message, save, noSave, cancel);
		}
	}-*/;

	@Override
	public native void removeWindowClosingHandler() /*-{
		if ($wnd.setUnsavedMessage) {
			$wnd.setUnsavedMessage(null);
		}
	}-*/;

	@Override
	public void storeLanguage(String s) {
		BrowserStorage.LOCAL.setItem("GeoGebraLangUI", s);
	}

	@Override
	public SignInController getSignInController(App app) {
		return new SignInController(app, 0,
				StaticFileUrls.getCallbackUrl().replace("file://", "app://"));
	}

	@Override
	public boolean autosaveSupported() {
		return false;
	}

	@Override
	public boolean isOfflineExamSupported() {
		return true;
	}

	@Override
	public boolean supportsGoogleDrive() {
		return false;
	}

	@Override
	public boolean hasHeader() {
		return false;
	}

	@Override
	public boolean examSupported() {
		return true;
	}

	@Override
	public boolean isExternalLoginAllowed() {
		return !Browser.isMacOS();
	}
}
