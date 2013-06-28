package geogebra.html5.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.LoginModel;

import com.google.gwt.storage.client.Storage;

/**
 * @author gabor
 *
 */
public class LogInModelWeb extends LoginModel {
	
	private Storage storage = null;
	
	/**
	 * creates a new login model for Web
	 */
	public LogInModelWeb() {
		storage = Storage.getLocalStorageIfSupported();
	}

	@Override
	public void storeLoginToken(String token) {
		storage.setItem(GGB_TOKEN_KEY_NAME, token);
	}

	@Override
	public String getLoginToken() {
		return storage.getItem(GGB_TOKEN_KEY_NAME);
	}

	@Override
	public void clearLoginToken() {
		storage.removeItem(GGB_TOKEN_KEY_NAME);
	}

}
