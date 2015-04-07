package org.geogebra.web.web.gui.browser;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;

public class TabletSignInButton extends SignInButton {

	public TabletSignInButton(App app) {
		super(app, 0);
	}

	@Override
	public void login() {
		loginNative(((AppW) app).getLocalization().getLocaleStr());
	}

	private native void loginNative(String locale)/*-{
		if ($wnd.android) {
			$wnd.android.login(locale);
		} else {
			@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("External login not possible");
		}
	}-*/;

}
