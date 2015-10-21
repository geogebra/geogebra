package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.move.models.BaseModel;

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
	private String identifier = null;
	private String profileURL;
	private String realName = null;
	private String cookie;
	private String image;
	private String language;
	
	/**
	 * Creates a new user with the specified login token
	 * 
	 * @param token The login token of the user
	 */
	public GeoGebraTubeUser(String token) {
		this.token = token;
	}
	
	public GeoGebraTubeUser(String token, String cookie) {
		this.token = token;
		this.cookie = cookie;
	}
	
	
	/**
	 * @return The Login token of the user
	 */
	public String getLoginToken() {
		return token;
	}
	
	/**
	 * Token needs to be set on cookie authentication
	 * @param token new token
	 */
	public void setToken(String token){
		this.token = token;
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
	 * @return URl to profile page
	 */
	public String getProfileURL() {
		return this.profileURL;
	}

	/**
	 * @param URL of the profile page
	 */
	public void setProfileURL(String URL) {
		this.profileURL = URL;
	}


	/**
	 * @return The login identifier of this user
	 */
	public String getIdentifier() {
		return identifier;
	}


	/**
	 * @param identifier The login identifier of this user
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}


	public boolean hasGoogleDrive() {
		return this.identifier.startsWith("google:");
	}
	
	public boolean hasOneDrive() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getCookie() {
		return this.cookie;
	}

	public void setImageURL(String url) {
		this.image = url;
		
	}

	public String getImageURL() {
		return this.image;
	}

	public String getLanguage() {
		return this.language;
	}
	
	public void setLanguage(String language){
		this.language = language;
	}

}
