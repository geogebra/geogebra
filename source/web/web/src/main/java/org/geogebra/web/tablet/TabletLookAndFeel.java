/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.tablet;

import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.browser.TabletSignInController;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
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
	public boolean hasLoginButton() {
		return true;
	}

	@Override
	public boolean isExternalLoginAllowed() {
		return false;
	}

	@Override
	public boolean hasHelpMenu() {
		return true;
	}
}
