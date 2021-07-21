package org.geogebra.web.full.gui.browser;

import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.shared.SignInController;

/**
 * Signin button for mebis; uses native JS in parent frame
 */
public class MebisSignInController extends SignInController {

	/**
	 * @param app
	 *            application
	 */
	public MebisSignInController(App app) {
		super(app, 0, null);
	}

	@Override
	public void login() {
		if (!nativeLogin()) {
			super.login();
		}
	}

	@Override
	public void loginFromApp() {
		if (!nativeLogin()) {
			Log.warn("Can't open popup programatically");
		}
	}

	private native boolean nativeLogin() /*-{
		try {
			if ($wnd.parent && $wnd.parent.login) {
				$wnd.parent.login();
				return true;
			}
			return false;
		} catch (ex) {
			//running in CORS iframe
		}
		return false;
	}-*/;

}
