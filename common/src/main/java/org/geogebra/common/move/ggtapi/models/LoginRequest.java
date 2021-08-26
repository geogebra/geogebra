package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;

/**
 * Handles Login requests to geogebra.org JSON API
 * @author gabor
 */
public class LoginRequest implements Request {

	private String password;
	private String userName;
	private static final String api = "1.0.0";
	private JSONObject requestJSON = new JSONObject();
	private JSONObject apiJSON = new JSONObject();
	private JSONObject loginJSON = new JSONObject();
	private String loginType = "forum";
	private String exptime = "365";
	private String info = "GeoGebraWeb Application";

	/**
	 * @param userName
	 *            userName
	 * @param password
	 *            pwd
	 */
	public LoginRequest(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	@Override
	public String toJSONString(ClientInfo app) {

		try {

			this.loginJSON.put("-type", loginType);
			this.loginJSON.put("-username", userName);
			this.loginJSON.put("-password", password);
			this.loginJSON.put("-exptime", exptime);
			this.loginJSON.put("-info", info);
			this.apiJSON.put("login", this.loginJSON);
			this.apiJSON.put("-api", api);
			this.requestJSON.put("request", this.apiJSON);
		} catch (Exception e) {
			Log.debug("problem building request: " + e.getMessage());
		}

		return this.requestJSON.toString();
	}

}
