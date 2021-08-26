package org.geogebra.web.full.gui.laf;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.browser.MebisSignInController;
import org.geogebra.web.shared.SignInController;

/**
 * LAF for Mebis environment
 */
public class MebisLookAndFeel extends GLookAndFeel {

	@Override
	public SignInController getSignInController(App app) {
		return new MebisSignInController(app);
	}

	@Override
	public boolean hasLoginButton() {
		return false;
	}

}
