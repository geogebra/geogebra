package org.geogebra.desktop.move.ggtapi.models;

import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.desktop.main.GeoGebraPreferencesD;

/**
 * @author stefan
 * 
 */
public class AuthenticationModelD extends AuthenticationModel {

	/**
	 * The token value that indicates that the token is not available (login was
	 * not performed yet)
	 */
	public static final String TOKEN_NOT_AVAILABLE = "<n/a>";

	/**
	 * creates a new login model for Web
	 */
	public AuthenticationModelD() {
	}

	@Override
	public void storeLoginToken(String token) {
		GeoGebraPreferencesD.getPref().savePreference(
				GeoGebraPreferencesD.USER_LOGIN_TOKEN, token);
	}

	@Override
	public String getLoginToken() {
		String token = GeoGebraPreferencesD.getPref().loadPreference(
				GeoGebraPreferencesD.USER_LOGIN_TOKEN, TOKEN_NOT_AVAILABLE);
		if (token.equals(TOKEN_NOT_AVAILABLE)) {
			return null;
		}
		return token;
	}

	@Override
	public void clearLoginToken() {
		storeLoginToken(TOKEN_NOT_AVAILABLE);
	}

	@Override
	protected void storeLastUser(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public String loadLastUser() {
		// TODO Auto-generated method stub
		return null;
	}
}
