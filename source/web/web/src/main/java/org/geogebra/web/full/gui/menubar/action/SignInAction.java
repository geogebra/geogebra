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

package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.util.debug.AccessibilityAnalytics;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Opens sign in window.
 */
public final class SignInAction extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(AppWFull app) {
		if (!app.getNetworkOperation().isOnline()) {
			return;
		}

		LogInOperation logInOperation = app.getLoginOperation();
		if (!logInOperation.isLoggedIn()) {
			registerLoginClicked(app);
			logInOperation.showLoginDialog();
		}
	}

	private void registerLoginClicked(AppWFull app) {
		app.getAccessibilityAnalyticsContext()
				.setTrigger(AccessibilityAnalytics.Value.BURGER_MENU)
				.setFlow(AccessibilityAnalytics.Value.DIRECT);
		AccessibilityAnalytics.logLoginClicked(AccessibilityAnalytics.Value.BURGER_MENU);
	}
}
