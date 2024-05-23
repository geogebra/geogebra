package org.geogebra.web.tablet;

import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.browser.TabletSignInController;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.SignInController;

/**
 * Look and feel for tablet apps
 */
public class TabletLookAndFeel extends GLookAndFeel {

	@Override
	public void addWindowClosingHandler(AppW app) {
		// no close message on tablet
	}

	@Override
	public void removeWindowClosingHandler() {
		// no close message on tablet
	}

	@Override
	public String getType() {
		return "tablet";
	}

	@Override
	public boolean isTablet() {
		return true;
	}

	@Override
	public SignInController getSignInController(final App app) {
		return new TabletSignInController(app);
	}

	@Override
	public boolean supportsGoogleDrive() {
		return false;
	}

	@Override
	public boolean examSupported() {
		return Browser.isAndroid();
	}

	@Override
	public boolean printSupported() {
		return false;
	}

	@Override
	public Platform getPlatform(int dim, String appName) {
		return Platform.IOS_WEBVIEW;
	}

	@Override
	public void toggleFullscreen(boolean full) {
		// tablet, nothing to do
	}

	@Override
	public void storeLanguage(String s) {
		BrowserStorage.LOCAL.setItem("GeoGebraLangUI", s);
	}

	@Override
	public boolean hasHeader() {
		return false;
	}

	@Override
	public boolean hasLoginButton() {
		return true;
	}

	@Override
	public boolean isExternalLoginAllowed() {
		return false;
	}
}
