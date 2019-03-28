package org.geogebra.web.full.gui.laf;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.browser.MebisSignInButton;
import org.geogebra.web.shared.SignInButton;

/**
 * LAF for Mebis environment
 */
public class MebisLookAndFeel extends GLookAndFeel {

	@Override
	public SignInButton getSignInButton(App app) {
		return new MebisSignInButton(app);
	}

	@Override
	public boolean hasLoginButton() {
		return false;
	}

	@Override
	public String getLicenseURL() {
		return "/static/license.html?";
	}

}
