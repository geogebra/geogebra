package geogebra.common.move.ggtapi.models;

import geogebra.common.move.models.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a user in GeoGebraTube.
 * Each user is identified by a user name.
 * 
 * The login token is used for the authorization of the user via the GeoGebraTube API. 
 * 
 * @author stefan
 *
 */
public class GeoGebraTubeUser extends BaseModel {
	private String userName = null;
	private String token = null;
	private int userId = -1;

	private String realName = null;
	
	/**
	 * Creates a new user with the specified login token
	 * 
	 * @param token The login token of the user
	 */
	public GeoGebraTubeUser(String token) {
		this.token = token;
	}
	
	
	/**
	 * @return The Login token of the user
	 */
	public String getLoginToken() {
		return token;
	}
	
	/**
	 * @return The user name of the user
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * Sets the user name for the user. Usually this is done after the user was authorized via the 
	 * GeoGebraTube API and the user name is received as response.
	 * 
	 * @param userName The new user name to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName; 
	}


	/**
	 * Sets the userid from GeoGeoGebraTube
	 * 
	 * @param userId The new userId
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}


	/**
	 * Sets the real name from GeoGeoGebraTube
	 * 
	 * @param realName The new real name of the user
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	/**
	 * @return the userid
	 */
	public int getUserId() {
		return userId;
	}


	/**
	 * @return the real name
	 */
	public String getRealName() {
		return realName;
	}
	
	/**
	 * Copies the user data from the API response to this user.
	 * 
	 * @param response The JSONObject that holds the response from a token login API request 
	 * @return true if the data could be parsed successfully, false otherwise
	 */
	public boolean parseUserDataFromResponse(JSONObject response) {
		try {
			JSONObject userinfo = response.getJSONObject("responses").getJSONObject("response").getJSONObject("userinfo");
			setUserId(userinfo.getInt("user_id"));
			setUserName(userinfo.getString("username"));
			setRealName(userinfo.getString("realname"));
			
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
}
