package org.geogebra.web.full.gui.browser;

import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.bridge.GeoGebraJSNativeBridge;
import org.geogebra.web.shared.SignInController;

/**
 * Signin button for tablets, uses native APIs
 */
public class TabletSignInController extends SignInController {

	/**
	 * @param app application
	 */
	public TabletSignInController(App app) {
		super(app, 0, null);
	}

	@Override
	public void login() {
		if (GeoGebraJSNativeBridge.get() != null) {
			GeoGebraJSNativeBridge.get().login();
		} else {
			Log.debug("External login not possible");
		}
	}
}
