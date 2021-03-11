package org.geogebra.desktop.move.ggtapi.models;

import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.desktop.util.HttpRequestD;

/**
 * API Interface for GeoGebraTube requests and responses
 * 
 * @author stefan
 * 
 */
public class GeoGebraTubeAPID extends GeoGebraTubeAPI {

	@Override
	protected HttpRequest createHttpRequest() {
		return new HttpRequestD();
	}

	/**
	 * Get Singleton GeogebraTubeAPI
	 * 
	 * @param beta
	 *            use beta server?
	 * @param client
	 *            client info
	 */
	public GeoGebraTubeAPID(boolean beta, ClientInfo client) {
		super(beta);
		this.client = client;
		// just for testing (or certificate emergency)
		// HttpRequestD.ignoreSSL();
	}

	@Override
	public boolean parseUserDataFromResponse(GeoGebraTubeUser user,
			String responseStr) {
		try {
			JSONTokener tokener = new JSONTokener(responseStr);
			JSONObject response = new JSONObject(tokener);

			// Check if an error occurred
			if (response.has("error")) {
				return false;
			}
			JSONArray responseArray = response.getJSONObject("responses")
					.getJSONArray("response");
			JSONObject userinfo = ((JSONObject) responseArray.get(0))
					.getJSONObject("userinfo");
			user.setUserId(userinfo.getInt("user_id"));
			user.setUserName(userinfo.getString("username"));
			user.setRealName(userinfo.getString("realname"));
			user.setIdentifier(userinfo.get("identifier").toString());

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
	protected String getToken() {
		return client.getModel().getLoginToken();
	}

}
