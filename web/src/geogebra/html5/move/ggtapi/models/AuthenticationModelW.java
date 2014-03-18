package geogebra.html5.move.ggtapi.models;


import geogebra.common.move.ggtapi.models.AuthenticationModel;
import geogebra.common.plugin.Event;
import geogebra.common.plugin.EventType;
import geogebra.web.main.AppW;

import com.google.gwt.storage.client.Storage;

/**
 * @author gabor
 *
 */
public class AuthenticationModelW extends AuthenticationModel {
	
	protected Storage storage = null;
	private String authToken = null;
	private AppW app;
	
	/**
	 * creates a new login model for Web
	 */
	public AuthenticationModelW(AppW app) {
		this.storage = Storage.getLocalStorageIfSupported();
		this.app = app;
	}

	@Override
	public void storeLoginToken(String token) {
		if(this.app!=null){
			ensureInited();
			this.app.dispatchEvent(new Event(EventType.LOGIN,null,token));
		}
		this.authToken = token;
		if(storage == null){
			return;
		}
		storage.setItem(GGB_TOKEN_KEY_NAME, token);
	}

	@Override
	public String getLoginToken() {
		if(authToken!=null){
			return authToken;
		}
		if(storage == null){
			return null;
		}
		return storage.getItem(GGB_TOKEN_KEY_NAME);
	}

	@Override
	public void clearLoginToken() {
		this.authToken = null;
		if(this.app!=null){
			ensureInited();
			this.app.dispatchEvent(new Event(EventType.LOGIN,null,""));
		}
		if(storage == null){
			return;
		}
		storage.removeItem(GGB_TOKEN_KEY_NAME);
	}

	private boolean inited = false;
	private void ensureInited() {
		if(inited || !app.getLAF().isSmart()){
			return;
		}
		inited = true;
	    app.getGgbApi().registerClientListener("loginListener");
    }
}
