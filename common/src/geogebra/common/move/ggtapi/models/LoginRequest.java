package geogebra.common.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.models.json.JSONString;

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
	private JSONObject loginTypeJSON = new JSONObject();
	private JSONObject loginUserNameJSON = new JSONObject();
	private JSONObject loginPasswordJSON = new JSONObject();
	private String loginType = "forum";
	private JSONObject exptimeJSON = new JSONObject();
	private String exptime = "365";
	private JSONObject infoJSON = new JSONObject();
	private String info = "GeoGebraWeb Application";
	private JSONObject taskJSON = new JSONObject();
	private JSONObject taskTypeJSON = new JSONObject();
	private String taskType = "login";
	private JSONObject credentialsJSON = new JSONObject();
	private JSONObject credentialsProviderJSON = new JSONObject();
	private String credentialsProvider = "forum";
	private JSONObject credentialsLoginJSON = new JSONObject();
	private JSONObject credentialsPasswordJSON = new JSONObject();
	private JSONObject expirationJSON = new JSONObject();
	private JSONObject expirationTimeJSON = new JSONObject();
	private String expirationTime = "365";

		
	/**
	 * @param userName userName
	 * @param password pwd
	 */
	public LoginRequest(String userName, String password) {
		this.userName = userName;
		this.password = password;   
	}

	public String toJSONString() {
		
		
		this.loginJSON.put("-type", new JSONString(loginType));
		this.loginJSON.put("-username", new JSONString(userName));
		this.loginJSON.put("-password", new JSONString(password));
		this.loginJSON.put("-exptime", new JSONString(exptime));
		this.loginJSON.put("-info", new JSONString(info));
		this.apiJSON.put("login", this.loginJSON);
		
		this.taskJSON.put("-type", new JSONString(taskType));
		this.credentialsJSON.put("-provider", new JSONString(credentialsProvider));
		this.credentialsJSON.put("-login", new JSONString(userName));
		this.credentialsJSON.put("-password", new JSONString(password));		
		this.taskJSON.put("credentials", this.credentialsJSON);
		
		this.expirationJSON.put("-time", new JSONString(expirationTime));
		this.taskJSON.put("expiration",  this.expirationJSON);
		
		
		this.apiJSON.put("task", this.taskJSON);
		
		this.apiJSON.put("-api", new JSONString(api));
		this.requestJSON.put("request", this.apiJSON);
		
		
		return this.requestJSON.toString();
    }

}
