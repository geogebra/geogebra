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

package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.util.debug.AccessibilityAnalytics;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * Sign in menu
 */
public final class SignInMenu extends Submenu {

	/**
	 * @param app
	 *            application
	 */
	public SignInMenu(AppW app) {
		super("signin", app);
	}

	@Override
	public SVGResource getImage() {
		return MaterialDesignResources.INSTANCE.signin_black();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "SignIn";
	}

	@Override
	public void handleHeaderClick() {
		AppW app = getApp();
		if (app.getNetworkOperation().isOnline()
				&& !app.getLoginOperation().isLoggedIn()) {
			registerLoginClicked(app);
			app.getLoginOperation().showLoginDialog();
		}
	}

	private void registerLoginClicked(AppW app) {
		app.getAccessibilityAnalyticsContext()
				.setTrigger(AccessibilityAnalytics.Value.BURGER_MENU)
				.setFlow(AccessibilityAnalytics.Value.DIRECT);
		AccessibilityAnalytics.logLoginClicked(AccessibilityAnalytics.Value.BURGER_MENU);
	}

}
