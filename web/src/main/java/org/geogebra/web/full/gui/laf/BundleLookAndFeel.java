package org.geogebra.web.full.gui.laf;

import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.SignInController;
import org.geogebra.web.shared.ggtapi.StaticFileUrls;

import elemental2.dom.DomGlobal;

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

	private void addNativeHandler(String message, String save,
			String noSave, String cancel) {
		if (GeoGebraGlobal.getSetUnsavedMessage() != null) {
			GeoGebraGlobal.getSetUnsavedMessage().call(DomGlobal.window,
					message, save, noSave, cancel);
		}
	}

	@Override
	public void removeWindowClosingHandler() {
		if (GeoGebraGlobal.getSetUnsavedMessage() != null) {
			GeoGebraGlobal.getSetUnsavedMessage().call(DomGlobal.window, null);
		}
	}

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
	public boolean isExternalLoginAllowed() {
		return !NavigatorUtil.isMacOS();
	}

	@Override
	public boolean hasLoginButton() {
		return true;
	}
}
