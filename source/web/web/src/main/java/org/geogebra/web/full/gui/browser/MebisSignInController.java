package org.geogebra.web.full.gui.browser;

import org.geogebra.web.html5.MebisGlobal;
import org.geogebra.web.html5.gui.laf.SignInControllerI;

/**
 * Signin button for mebis; uses native JS in parent frame
 */
public class MebisSignInController implements SignInControllerI {

	@Override
	public void login() {
		MebisGlobal.nativeLogin();
	}

	@Override
	public void initLoginTimer() {
		// not needed
	}
}
