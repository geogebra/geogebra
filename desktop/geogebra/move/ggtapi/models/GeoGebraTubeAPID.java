package geogebra.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.util.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * API Interface for GeoGebraTube requests and responses
 * 
 * @author stefan
 * 
 */
public class GeoGebraTubeAPID extends geogebra.common.move.ggtapi.models.GeoGebraTubeAPI
{
	
	
	@Override
	protected geogebra.common.util.HttpRequest createHttpRequest() {
		return new geogebra.util.HttpRequestD();
	}
	
	/**
	 * Get Singleton GeogebraTubeAPI
	 * 
	 * @return GeogebraTubeAPI singleton
	 */
	public static GeoGebraTubeAPID getInstance() {
		if (instance == null)
		{
			instance = new GeoGebraTubeAPID();
			GeoGebraTubeAPI.url = GeoGebraTubeAPI.test_url;
		}
		return (GeoGebraTubeAPID) instance;
	}
	
	@Override
	public int authorizeUser(GeoGebraTubeUser user) {
		HttpRequest request = performRequest(buildTokenLoginRequest(user.getLoginToken()).toString());
		try{
			if (request.isSuccessful()) {
				JSONTokener tokener = new JSONTokener(request.getResponse());
				JSONObject response = new JSONObject(tokener);
				
				// Check if an error occurred
				if (response.has("error")) {
					return LOGIN_TOKEN_INVALID;
				}
				
				// Parse the userdata from the response
				if (!parseUserDataFromResponse(user, response)) {
					return LOGIN_TOKEN_INVALID;
				}
				
				return LOGIN_TOKEN_VALID;
			} 
		}catch(Exception e){
			e.printStackTrace();
		}
		return LOGIN_REQUEST_FAILED;
	}
	
	public boolean parseUserDataFromResponse(GeoGebraTubeUser user, JSONObject response) {
		try {
			JSONObject userinfo = response.getJSONObject("responses").getJSONObject("response").getJSONObject("userinfo");
			user.setUserId(userinfo.getInt("user_id"));
			user.setUserName(userinfo.getString("username"));
			user.setRealName(userinfo.getString("realname"));
			user.setIdentifier(userinfo.get("identifier").toString());
			
			// Further fields are not parsed yet, because they are not needed
			// This is the complete response with all available fields:
			/*
			<responses>
			  <response>
			    <userinfo>
			      <user_id>4711</user_id>
			      <username>johndoe</username>
			      <ggt_profile_url>http://www.geogebratube.org/user/profile/id/4711
			        </ggt_profile_url>
			      <group>user</group>
			      <date_created>2012-09-18</date_created>
			      <lang_ui>en</lang_ui>
			      <lang_content>en,en_US,it</lang_content>
			      <timezone>America/New_York</timezone>
			      <materials>31</materials>
			      <favorites>4</favorites>
			      <collections>2</collections>
			      <identifier>forum:0815</identifier>
			      <realname>John Doe</realname>
			      <occupation>Maths teacher</occupation>
			      <location>New York</location>
			      <website>www.thisisme.com</website>
			      <profilemessage>Any text</profilemessage>
			    </userinfo>
			  </response>
			</responses>
		*/
			
			
			
//			user.setGGTProfileURL(userinfo.getString("ggt_profile_url"));
//			user.setGroup(userinfo.getString("group"));
//			user.setDateCreated(userinfo.getString("date_created"));
		} catch(JSONException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Builds the request to check if the login token of a user is valid.
	 * This request will send detailed user information as response.
	 * 
	 * @param user The user that should be logged in
	 * @return The JSONObject that contains the request.
	 */
	private JSONObject buildTokenLoginRequest(String token) {
		JSONObject requestJSON = new JSONObject();
		JSONObject apiJSON = new JSONObject();
		JSONObject loginJSON = new JSONObject();
		try{
			loginJSON.put("-token", token);
			loginJSON.put("-getuserinfo", "true");
			apiJSON.put("login", loginJSON);		
			apiJSON.put("-api", "1.0.0");
			requestJSON.put("request", apiJSON);
		}
		catch(JSONException e){
			e.printStackTrace();
		}
		return requestJSON;
	}
}
