package geogebra.common.move.ggtapi.models;

import geogebra.common.util.HttpRequest;

import org.json.JSONObject;
import org.json.JSONTokener;


/**
 * @author gabor
 * Common base for GeoGebraTubeApi
 */
public abstract class GeoGebraTubeAPI {

	/**
	 * The Standard Result Quantity
	 */
	public static final int STANDARD_RESULT_QUANTITY = 30;

	/**
	 * Secure test url
	 */
	public static String test_url = "http://test.geogebratube.org:8080/api/json.php";

	/**
	 * Public url (no SSL)
	 * DO NOT CHANGE!
	 */
	public static String url = "http://www.geogebratube.org/api/json.php";
	/**
	 * Instance of the new GeoGebraTube API D/W/T
	 */
	protected static GeoGebraTubeAPI instance;
	
	static public final int LOGIN_TOKEN_VALID = 0;
	static public final int LOGIN_TOKEN_INVALID = 1;
	static public final int LOGIN_REQUEST_FAILED = -2;
	
	
	/**
	 * Private method performing the request given by requestString
	 * 
	 * @param requestString
	 *          JSON request String for the GeoGebraTubeAPI
	 * @return The HttpRequest object that contains the response and error information 
	 */
	protected HttpRequest performRequest(String requestString)
	{
		HttpRequest request = createHttpRequest();
		request.sendRequestPost(url, requestString);
		return request;
	}
	
	/**
	 * Creates a new Http request
	 * 
	 * @return The new http request
	 */
	protected abstract HttpRequest createHttpRequest();

	
	/**
	 * Sends a request to the GeoGebraTube API to check if the login token which is defined in the specified 
	 * GeoGebraTubeUser is valid.
	 * 
	 * @param user The user that should be authorized.
	 * @return One of the following return codes: LOGIN_TOKEN_VALID, LOGIN_TOKEN_INVALID, LOGIN_REQUEST_FAILED
	 */
	public int authorizeUser(GeoGebraTubeUser user) {
		HttpRequest request = performRequest(buildTokenLoginRequest(user.getLoginToken()).toString());
		if (request.isSuccessful()) {
			JSONTokener tokener = new JSONTokener(request.getResponse());
			JSONObject response = new JSONObject(tokener);
			
			// Check if an error occurred
			if (response.has("error")) {
				return LOGIN_TOKEN_INVALID;
			}
			
			// Parse the userdata from the response
			if (! user.parseUserDataFromResponse(response)) {
				return LOGIN_TOKEN_INVALID;
			}
			
			return LOGIN_TOKEN_VALID;
		} 
		return LOGIN_REQUEST_FAILED;
	}
	
	/**
	 * Builds the request to check if the login token of a user is valid.
	 * This request will send detailed user information as response.
	 * 
	 * @param user The user that should be logged in
	 * @return The JSONObject that contains the request.
	 */
	protected JSONObject buildTokenLoginRequest(String token) {
		JSONObject requestJSON = new JSONObject();
		JSONObject apiJSON = new JSONObject();
		JSONObject loginJSON = new JSONObject();
		
		loginJSON.put("-token", token);
		loginJSON.put("-getuserinfo", "true");
		apiJSON.put("login", loginJSON);		
		apiJSON.put("-api", "1.0.0");
		requestJSON.put("request", apiJSON);
		
		return requestJSON;
	}

}
