package org.geogebra.web.shared.ggtapi.models;

import static elemental2.core.Global.JSON;

import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.web.html5.gui.util.Cookies;
import org.geogebra.web.html5.main.GeoGebraTubeAPIWSimple;
import org.geogebra.web.html5.util.AppletParameters;

import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * API Interface for GeoGebraTube requests and responses
 * 
 * @author Matthias Meisinger
 * 
 */
public class GeoGebraTubeAPIW extends GeoGebraTubeAPIWSimple {

	/**
	 * @param beta
	 *            whether to use beta
	 * @param client
	 *            {@link ClientInfo}
	 * @param articleElement
	 *            parameters
	 */
	public GeoGebraTubeAPIW(ClientInfo client, boolean beta,
			AppletParameters articleElement) {
		super(beta, articleElement);
		this.client = client;
	}

	/**
	 * Copies the user data from the API response to this user.
	 * 
	 * @return true if the data could be parsed successfully, false otherwise
	 */
	@Override
	public boolean parseUserDataFromResponse(GeoGebraTubeUser user,
			String result) {
		try {
			JsPropertyMap<Object> parsedResult = Js.asPropertyMap(JSON.parse(result));
			JsPropertyMap<Object> responses = Js.asPropertyMap(parsedResult.get("responses"));

			JsPropertyMap<Object> response = Js.asPropertyMap(
					Js.asArrayLike(responses.get("response")).getAt(0));
			JsPropertyMap<Object> userinfo = Js.asPropertyMap(response.get("userinfo"));

			user.setUserId(Js.coerceToInt(userinfo.get("user_id")));
			user.setUserName(Js.asString(userinfo.get("username")));
			user.setRealName(Js.asString(userinfo.get("realname")));
			user.setIdentifier(Js.asString(userinfo.get("identifier")));

			if (Js.isTruthy(userinfo.get("ggt_avatar_url"))) {
				user.setImageURL(Js.asString(userinfo.get("ggt_avatar_url")));
			}
			if (Js.isTruthy(userinfo.get("ggt_profile_url"))) {
				user.setProfileURL(Js.asString(userinfo.get("ggt_profile_url")));
			}
			if (Js.isTruthy(userinfo.get("image"))) {
				user.setImageURL(Js.asString(userinfo.get("image")));
			}
			if (Js.isTruthy(userinfo.get("lang_ui"))) {
				user.setLanguage(Js.asString(userinfo.get("lang_ui")));
			}
			if (Js.isTruthy(userinfo.get("token"))) {
				user.setToken(Js.asString(userinfo.get("token")));
			}

			// Further fields are not parsed yet, because they are not needed

			// user.setGGTProfileURL(userinfo.getString("ggt_profile_url"));
			// user.setGroup(userinfo.getString("group"));
			// user.setDateCreated(userinfo.getString("date_created"));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public boolean performCookieLogin(LogInOperation op) {
		String cookie = Cookies.getCookie("SSID");
		if (cookie != null) {
			op.doPerformTokenLogin(new GeoGebraTubeUser(null, cookie), true);
			return true;
		}
		return false;
	}
}
