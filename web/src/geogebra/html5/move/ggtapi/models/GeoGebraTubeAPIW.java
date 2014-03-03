package geogebra.html5.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.move.ggtapi.models.LoginRequest;
import geogebra.common.move.ggtapi.models.MaterialRequest;
import geogebra.common.util.HttpRequest;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * API Interface for GeoGebraTube requests and responses
 * 
 * @author Matthias Meisinger
 * 
 */
public class GeoGebraTubeAPIW extends GeoGebraTubeAPI
{
	private RequestBuilder requestBuilder;

	private GeoGebraTubeAPIW(String url)
	{
		this.requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
	}

	/**
	 * Search for materials containing the String query
	 * 
	 * @param query
	 *          search String
	 * @param limit
	 *          maximum Number of returned materials
	 * @return List<Item> Search Results in a List of materials
	 */
	public void search(String query, RequestCallback callback)
	{
		performRequest(new MaterialRequest(query).toJSONString(), callback);
	}

	/**
	 * Returns materials in the given amount and order
	 * 
	 * @return List of materials
	 */
	public void getFeaturedMaterials(RequestCallback callback)
	{
		performRequest(MaterialRequest.forFeatured().toJSONString(), callback);
	}

	// /**
	// * Returns a String-Array of popular tags fetched from the GGT API
	// *
	// */
	// public String[] getPopularTags()
	// {
	// // TODO fetch popular tags from the API
	// return new String[] { "algebra", "dment", "pythagorean", "circle",
	// "triangle", "functions", "jerzy", "geometry", "trigonometry", "3d" };
	// }

	/**
	 * Return a specific Material by its ID
	 * 
	 * @param ID
	 */
	/*public void getItem(String ID, RequestCallback callback)
	{
		// TODO add ID fetching of a specific material!
		performRequest(new MaterialRequest().toJSONString(), callback);
	}*/
	
	/**
	 * Logs in an user to GeoGebraTube
	 * 
	 * @param userName
	 * @param password
	 * @param callback
	 */
	public void logIn(String userName, String password, RequestCallback callback) {
		performRequest(new LoginRequest(userName, password).toJSONString(), callback);
	}

	// /**
	// * Return a list of all Materials from the specified author
	// * ! Should be the same search as for materials!
	// * @param author
	// */
	// public void getAuthorsMaterials(String author, RequestCallback callback)
	// {
	// throw new UnsupportedOperationException();
	// }

	/**
	 * Private method performing the request given by requestString
	 * 
	 * @param requestString
	 *          JSON request String for the GeoGebraTubeAPI
	 * @return the resulting List of Materials
	 * @throws RequestException
	 */
	private void performRequest(String requestString, RequestCallback callback)
	{
		try
		{
			this.requestBuilder.sendRequest(requestString, callback);
		}
		catch (RequestException e)
		{
			// TODO Handle the error!
			e.printStackTrace();
		}
	}
	
	/**
	 * Get Singleton GeogebraTubeAPI
	 * @param url Depends on Touch and Web for now. Later must be changed.
	 * 
	 * @return GeogebraTubeAPI singleton
	 */
	public static GeoGebraTubeAPIW getInstance(String url) {
		if (instance == null)
		{
			instance = new GeoGebraTubeAPIW(url);
		}
		return (GeoGebraTubeAPIW) instance;
	}

	@Override
    protected geogebra.common.util.HttpRequest createHttpRequest() {
		return new geogebra.web.util.HttpRequestW();
    }
	
	@Override
	public int authorizeUser(GeoGebraTubeUser user) {
		HttpRequest request = performRequest(buildTokenLoginRequest(user.getLoginToken()).toString());
		if (request.isSuccessful()) {
			//JSONTokener tokener = new JSONTokener(request.getResponse());
			//JSONObject response = new JSONObject(tokener);
			
			//JSONParser parser = new JSONParser();
			
			JSONValue tokener = JSONParser.parseStrict(request.getResponse());
			
			JSONObject obj;
			if ((obj=tokener.isObject()) != null) {
				if (obj.get("error") != null) {
					return LOGIN_TOKEN_INVALID;
				}
				
				// Parse the userdata from the response
				if (!parseUserDataFromResponse(user, obj)) {
					return LOGIN_TOKEN_INVALID;
				}

				return LOGIN_TOKEN_VALID;
			}
			
			
		} 
		return LOGIN_REQUEST_FAILED;
	}
	
	/**
	 * Copies the user data from the API response to this user.
	 * 
	 * @param response The JSONObject that holds the response from a token login API request 
	 * @return true if the data could be parsed successfully, false otherwise
	 */
	public boolean parseUserDataFromResponse(GeoGebraTubeUser user, JSONObject response) {
		try {
			JSONObject userinfo = (JSONObject)response.get("responses");
			
			userinfo = (JSONObject)userinfo.get("response");
			userinfo = (JSONObject)userinfo.get("userinfo");
			
			user.setUserId(Integer.valueOf(((JSONString) userinfo.get("user_id")).stringValue()));
			user.setUserName(userinfo.get("username").toString());
			user.setRealName(userinfo.get("realname").toString());
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
			loginJSON.put("-token", new JSONString(token));
			loginJSON.put("-getuserinfo", new JSONString("true"));
			apiJSON.put("login", loginJSON);		
			apiJSON.put("-api", new JSONString("1.0.0"));
			requestJSON.put("request", apiJSON);
		}
		catch(JSONException e){
			e.printStackTrace();
		}
		return requestJSON;
	}

	@Override
    public boolean isAvailable() {
	    return true;
    }

	public void getUsersMaterials(int userId, RequestCallback rc) {
		performRequest(MaterialRequest.forUser(userId).toJSONString(), rc);
    }
}
