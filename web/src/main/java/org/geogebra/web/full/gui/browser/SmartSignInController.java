package org.geogebra.web.full.gui.browser;

import org.geogebra.common.main.App;
import org.geogebra.web.shared.SignInController;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window.Location;

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
				+ URL.encode(Location.getHref());

		DomGlobal.location.replace(url);
	}

}
