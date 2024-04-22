package org.geogebra.web.shared.ggtapi.models;

import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.MD5EncrypterGWTImpl;
import org.geogebra.gwtutil.Cookies;
import org.geogebra.web.html5.MebisGlobal;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.main.AppW;

import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

/**
 * @author gabor
 *
 */
public class AuthenticationModelW extends AuthenticationModel  {

	private static final String GGB_LAST_USER = "last_user";
	/** token storage */
	private String authToken = null;
	private final AppW app;
	private boolean inited = false;

	/**
	 * creates a new login model for Web
	 * 
	 * @param app
	 *            application
	 */
	public AuthenticationModelW(AppW app) {
		this.app = app;
	}

	@Override
	public void storeLoginToken(String token) {
		if (this.app != null) {
			ensureInited();
			this.app.dispatchEvent(new Event(EventType.LOGIN, null, token));
		}
		this.authToken = token;
		BrowserStorage.LOCAL.setItem(GGB_TOKEN_KEY_NAME, token);
	}

	@Override
	public String getLoginToken() {
		if (authToken != null) {
			return authToken;
		}
		if (BrowserStorage.SESSION.getItem(GGB_TOKEN_KEY_NAME) != null) {
			return BrowserStorage.SESSION.getItem(GGB_TOKEN_KEY_NAME);
		}
		return BrowserStorage.LOCAL.getItem(GGB_TOKEN_KEY_NAME);
	}

	@Override
	public void clearLoginToken() {
		app.getLoginOperation().getGeoGebraTubeAPI().logout(this.authToken);

		this.authToken = null;
		// this should log the user out of other systems too
		ensureInited();
		this.app.dispatchEvent(new Event(EventType.LOGIN, null, ""));
		BrowserStorage.LOCAL.removeItem(GGB_TOKEN_KEY_NAME);
		BrowserStorage.SESSION.removeItem(GGB_TOKEN_KEY_NAME);
		BrowserStorage.LOCAL.removeItem(GGB_LAST_USER);
	}

	private void ensureInited() {
		if (inited || app.getLAF() == null
				|| app.getLAF().getLoginListener() == null) {
			return;
		}
		inited = true;
		app.getGgbApi().registerClientListener("loginListener");
	}

	/**
	 * @param api responsible for mapping JSON to user object
	 * @return whether user data was loaded
	 */
	public boolean loadUserFromSession(BackendAPI api) {
		String sessionUser = BrowserStorage.SESSION.getItem(GGB_LAST_USER);
		if (sessionUser != null) {
			loadUserFromString(sessionUser, api);
			return true;
		}
		return false;
	}

	@Override
	public String getEncoded() {
		String secret = "ef1V8PNj";
		String encrypted = MD5EncrypterGWTImpl
				.encrypt(getLoginToken() + "T" + "1581341456" + secret);
		return DomGlobal.btoa(getLoginToken()) + "|T|" + "1581341456" + "|" + encrypted;
	}

	@Override
	public String getCookie(String cookieName) {
		return Cookies.getCookie(cookieName);
	}

	@Override
	public void refreshToken(HttpRequest request, Runnable afterRefresh) {
		if (app.isMebis()) {
			MebisGlobal.refreshToken(token -> {
				if (Js.isTruthy(token)) {
					request.setAuth(token);
					// just update the token, no need to fire event
					authToken = token;
				}
				afterRefresh.run();
			});
		} else {
			afterRefresh.run();
		}
	}
}
