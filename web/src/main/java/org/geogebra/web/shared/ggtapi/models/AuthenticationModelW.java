package org.geogebra.web.shared.ggtapi.models;

import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.MD5EncrypterGWTImpl;
import org.geogebra.web.full.gui.dialog.SessionExpireNotifyDialog;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.storage.client.Storage;

import elemental2.dom.DomGlobal;

/**
 * @author gabor
 *
 */
public class AuthenticationModelW extends AuthenticationModel implements GTimerListener {

	private static final String GGB_LAST_USER = "last_user";
	/** token storage */
	protected Storage storage = null;
	private String authToken = null;
	private AppW app;
	private boolean inited = false;

	/**
	 * creates a new login model for Web
	 * 
	 * @param app
	 *            application
	 */
	public AuthenticationModelW(AppW app) {
		this.storage = Storage.getLocalStorageIfSupported();
		this.app = app;
	}

	@Override
	public void storeLoginToken(String token) {
		if (this.app != null) {
			ensureInited();
			this.app.dispatchEvent(new Event(EventType.LOGIN, null, token));
		}
		this.authToken = token;
		if (storage == null) {
			return;
		}
		storage.setItem(GGB_TOKEN_KEY_NAME, token);
	}

	@Override
	public String getLoginToken() {
		if (authToken != null) {
			return authToken;
		}
		if (storage == null) {
			return null;
		}
		return storage.getItem(GGB_TOKEN_KEY_NAME);
	}

	@Override
	public void clearLoginToken() {
		app.getLoginOperation().getGeoGebraTubeAPI().logout(this.authToken);

		this.authToken = null;
		// this should log the user out of other systems too
		ensureInited();
		this.app.dispatchEvent(new Event(EventType.LOGIN, null, ""));
		if (storage == null) {
			return;
		}
		storage.removeItem(GGB_TOKEN_KEY_NAME);
		storage.removeItem(GGB_LAST_USER);
	}

	private void ensureInited() {
		if (inited || app.getLAF() == null
				|| app.getLAF().getLoginListener() == null) {
			return;
		}
		inited = true;
		app.getGgbApi().registerClientListener("loginListener");
	}

	@Override
	protected void storeLastUser(String username) {
		if (storage == null) {
			return;
		}
		storage.setItem(GGB_LAST_USER, username);
	}

	@Override
	public String loadLastUser() {
		if (storage == null) {
			return null;
		}
		return storage.getItem(GGB_LAST_USER);
	}

	@Override
	public String getEncoded() {
		String secret = "ef1V8PNj";
		String encrypted = MD5EncrypterGWTImpl
				.encrypt(getLoginToken() + "T" + "1581341456" + secret);
		return DomGlobal.btoa(getLoginToken()) + "|T|" + "1581341456" + "|" + encrypted;
	}

	@Override
	public void onRun() {
		DialogData data = new DialogData(null, "Cancel", "Save");
		new SessionExpireNotifyDialog(app, data).show();
	}
}
