package geogebra.html5.move.ggtapi.models;


import geogebra.common.move.ggtapi.models.AuthenticationModel;
import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.html5.util.JSON;

import com.google.gwt.storage.client.Storage;

/**
 * @author gabor
 *
 */
public class AuthenticationModelWeb extends AuthenticationModel {
	
	private Storage storage = null;
	
	/**
	 * creates a new login model for Web
	 */
	public AuthenticationModelWeb() {
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

	@Override
    public JSONObject getStoredLoginData() {
		if(storage == null){
			return null;
		}
		JSONObject resp = JSONParser.parseToJSONObject(JSON.parse(storage.getItem(GGB_LOGIN_DATA_KEY_NAME)));
		return resp;
	}

	@Override
    public void storeLoginData(JSONObject info) {
		if(storage == null){
			return;
		}
	    storage.setItem(GGB_LOGIN_DATA_KEY_NAME, info.toString());
    }

	@Override
    public void removeStoredLoginData() {
		if(storage == null){
			return;
		}
	    storage.removeItem(GGB_LOGIN_DATA_KEY_NAME);
    }

}
