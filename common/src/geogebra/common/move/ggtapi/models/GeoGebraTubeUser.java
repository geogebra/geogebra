package geogebra.common.move.ggtapi.models;

import geogebra.common.move.models.BaseModel;

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

}
