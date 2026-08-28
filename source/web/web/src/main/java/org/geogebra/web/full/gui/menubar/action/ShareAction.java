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

import org.geogebra.common.util.debug.AccessibilityAnalytics;
import org.geogebra.web.full.gui.ShareControllerW;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.bridge.GeoGebraJSNativeBridge;

/**
 * Shares the material.
 */
public final class ShareAction extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(AppWFull app) {
		ShareControllerW shareController = (ShareControllerW) app.getShareController();
		if (GeoGebraJSNativeBridge.get() != null) {
			shareController.getBase64();
		} else {
			shareController.setAnchor(null);
			registerShareClicked(app);
			shareController.share();
		}
	}

	private void registerShareClicked(AppWFull app) {
		app.getAccessibilityAnalyticsContext()
				.setTrigger(AccessibilityAnalytics.Value.BURGER_MENU)
				.setFlow(AccessibilityAnalytics.Value.SHARE);
		AccessibilityAnalytics.logShareClicked(false,
				app.getLoginOperation().isLoggedIn(), app.isSaved());
	}
}
