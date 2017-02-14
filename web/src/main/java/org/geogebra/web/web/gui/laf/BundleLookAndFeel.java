package org.geogebra.web.web.gui.laf;

import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.browser.SignInButton;
import org.geogebra.web.web.move.ggtapi.operations.BASEURL;

import com.google.gwt.storage.client.Storage;

/**
 * For offline browser
 */
public class BundleLookAndFeel extends GLookAndFeel {
	@Override
	public Versions getVersion(int dim, boolean app) {
		return Versions.WEB_FOR_DESKTOP;
	}

	@Override
	public void addWindowClosingHandler(final AppW app) {
		Localization loc = app.getLocalization();
		addNativeHandler(loc.getPlain("CloseApplicationLoseUnsavedData"),
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
		Storage storage = Storage.getLocalStorageIfSupported();
		if (storage != null) {
			storage.setItem("GeoGebraLangUI", s);
		}
	}

	@Override
	public SignInButton getSignInButton(App app) {
		return new SignInButton(app, Browser.isIE9() ? 2000 : 0,
				BASEURL.getCallbackUrl().replace("file://", "app://"));
	}

	@Override
	public boolean autosaveSupported() {
		return false;
	}

	@Override
	public boolean supportsGoogleDrive() {
		return false;
	}

	@Override
	public boolean forceReTeX() {
		return true;
	}

}
