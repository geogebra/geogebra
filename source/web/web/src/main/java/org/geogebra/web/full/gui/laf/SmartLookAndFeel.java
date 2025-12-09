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

package org.geogebra.web.full.gui.laf;

import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.ResourceAction;
import org.geogebra.web.full.gui.browser.SmartSignInController;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.SignInController;

/**
 * @author geogebra
 * Look and Feel for SMART
 *
 */
public class SmartLookAndFeel extends GLookAndFeel {

	@Override
	public boolean undoRedoSupported() {
		return false;
	}

	@Override
	public boolean isSmart() {
		return true;
	}

	@Override
	public boolean isEmbedded() {
		return true;
	}

	@Override
	public void addWindowClosingHandler(AppW app) {
		//no close message for SMART
	}
	
	@Override
	public void removeWindowClosingHandler() {
		//no close message for SMART
	}

	@Override
	public String getType() {
		return "smart";
	}

	@Override
	public boolean copyToClipboardSupported() {
		return false;
	}

	@Override
	public String getLoginListener() {
		return "loginListener";
	}

	@Override
	public SignInController getSignInController(App app) {
		return new SmartSignInController(app);
	}

	@Override
	public boolean autosaveSupported() {
		return false;
	}

	@Override
	public boolean exportSupported() {
		return false;
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
		return Platform.SMART;
	}

	@Override
	public void toggleFullscreen(boolean full) {
		// nothing to do
	}

	@Override
	public ResourceAction getDisplayAction(ResourceAction action) {
		return action == ResourceAction.EDIT
				? ResourceAction.INSERT_ACTIVITY : super.getDisplayAction(action);
	}

	@Override
	public boolean hasLoginButton() {
		return true;
	}
}
