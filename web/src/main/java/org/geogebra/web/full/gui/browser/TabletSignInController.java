package org.geogebra.web.full.gui.browser;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.SignInController;

/**
 * Signin button for tablets, uses native APIs
 *
 */
public class TabletSignInController extends SignInController {

	/**
	 * @param app
	 *            application
	 */
	public TabletSignInController(App app) {
		super(app, 0, null);
	}

	@Override
	public void login() {
		loginNative(((AppW) app).getLocalization().getLocaleStr());
	}

	private native void loginNative(String locale)/*-{
		if ($wnd.android) {
			$wnd.android.login(locale);
		} else {
			@org.geogebra.common.util.debug.Log::debug(Ljava/lang/Object;)("External login not possible");
		}
	}-*/;

}
