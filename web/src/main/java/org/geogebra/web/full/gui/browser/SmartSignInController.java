package org.geogebra.web.full.gui.browser;

import org.geogebra.common.main.App;
import org.geogebra.web.shared.SignInController;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;

/**
 * Signin button for SMART - navigates the whole page to login
 */
public class SmartSignInController extends SignInController {

	/**
	 * @param app
	 *            application
	 */
	public SmartSignInController(App app) {
		super(app, 0, null);
	}

	@Override
	public void login() {
		String url = "https://accounts.geogebra.org/user/signin"
				+ "/caller/web/expiration/600/clientinfo/smart"
				+ "/?lang=" + app.getLocalization().getLocaleStr() + "&url="
				+ Global.encodeURIComponent(DomGlobal.location.href);

		DomGlobal.location.replace(url);
	}

}
