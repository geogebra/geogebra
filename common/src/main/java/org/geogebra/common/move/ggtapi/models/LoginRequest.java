package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONString;

/**
 * @author gabor
 * Handles LoginRequests to GGTApi
 *
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
	private JSONObject taskJSON = new JSONObject();
	
	/**
	 * @param userName userName
	 * @param password pwd
	 */
	public LoginRequest(String userName, String password) {
		this.userName = userName;
		this.password = password;   
	}

	public String toJSONString(ClientInfo app) {
		
		
		this.loginJSON.put("-type", new JSONString(loginType));
		this.loginJSON.put("-username", new JSONString(userName));
		this.loginJSON.put("-password", new JSONString(password));
		this.loginJSON.put("-exptime", new JSONString(exptime));
		this.loginJSON.put("-info", new JSONString(info));
		this.apiJSON.put("login", this.loginJSON);		
		this.apiJSON.put("-api", new JSONString(api));
		this.requestJSON.put("request", this.apiJSON);
		
		
		return this.requestJSON.toString();
    }

}
