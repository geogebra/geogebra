package org.geogebra.web.full.gui.laf;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.browser.MebisSignInController;
import org.geogebra.web.html5.gui.laf.SignInControllerI;

/**
 * LAF for Mebis environment
 */
public class MebisLookAndFeel extends GLookAndFeel {

	@Override
	public SignInControllerI getSignInController(App app) {
		return new MebisSignInController();
	}

	@Override
	public boolean hasLoginButton() {
		return false;
	}

}
