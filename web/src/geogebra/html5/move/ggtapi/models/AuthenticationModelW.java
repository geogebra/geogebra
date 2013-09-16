package geogebra.html5.move.ggtapi.models;


import geogebra.common.move.ggtapi.models.AuthenticationModel;

import com.google.gwt.storage.client.Storage;

/**
 * @author gabor
 *
 */
public class AuthenticationModelW extends AuthenticationModel {
	
	private Storage storage = null;
	
	/**
	 * creates a new login model for Web
	 */
	public AuthenticationModelW() {
		storage = Storage.getLocalStorageIfSupported();
	}

	@Override
	public void storeLoginToken(String token) {
		if(storage == null){
			return;
		}
		storage.setItem(GGB_TOKEN_KEY_NAME, token);
	}

	@Override
	public String getLoginToken() {
		if(storage == null){
			return null;
		}
		return storage.getItem(GGB_TOKEN_KEY_NAME);
	}

	@Override
	public void clearLoginToken() {
		if(storage == null){
			return;
		}
		storage.removeItem(GGB_TOKEN_KEY_NAME);
	}
}
