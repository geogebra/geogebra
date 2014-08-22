package geogebra.html5.move.ggtapi.models;

import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.ggtapi.models.AjaxCallback;
import geogebra.common.move.ggtapi.models.ClientInfo;
import geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.move.ggtapi.models.MaterialRequest;
import geogebra.common.move.ggtapi.operations.LogInOperation;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.web.main.AppW;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONNumber;
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
	private ClientInfo client;

	public GeoGebraTubeAPIW(String url, ClientInfo client)
	{
		this.requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		this.client = client;
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
	public void search(String query, MaterialCallback callback)
	{
		performRequest(new MaterialRequest(query, client).toJSONString(), callback);
	}

	/**
	 * Returns materials in the given amount and order
	 * 
	 * @return List of materials
	 */
	public void getFeaturedMaterials(MaterialCallback callback)
	{
		performRequest(MaterialRequest.forFeatured(client).toJSONString(), callback);
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
	public void getItem(int id, MaterialCallback callback)
	{
		// TODO add ID fetching of a specific material!
		performRequest(new MaterialRequest(id, client).toJSONString(), callback);
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

	
	protected void performRequest(String requestString, final MaterialCallback cb)
	{
		try
		{
			RequestCallback callback = new RequestCallback(){

				@Override
                public void onResponseReceived(Request request,
                        Response response) {
	                cb.onLoaded(JSONparserGGT.parseResponse(response.getText()));
                }

				@Override
                public void onError(Request request, Throwable exception) {
	                cb.onError(exception);
	                
                }};
			this.requestBuilder.sendRequest(requestString, callback);
		}
		catch (RequestException e)
		{
			// TODO Handle the error!
			e.printStackTrace();
		}
	}
	
	protected void performUploadRequest(String requestString, final MaterialCallback cb) {
		try {
			this.requestBuilder.sendRequest(requestString, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					cb.onLoaded(JSONparserGGT.parseResponse(response.getText()));
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					cb.onError(exception);
				}
			});
		}
		catch (RequestException e) {
			// TODO Handle the error!
			e.printStackTrace();
		}
	}

	@Override
    protected geogebra.common.util.HttpRequest createHttpRequest() {
		return new geogebra.web.util.HttpRequestW();
    }
	
	@Override
	public void authorizeUser(final GeoGebraTubeUser user, final LogInOperation op, final boolean automatic) {
		String reqStr = buildTokenLoginRequest(user.getLoginToken(), user.getCookie()).toString();
		performRequest(reqStr, true, new AjaxCallback(){

			@Override
            public void onSuccess(String result) {
				try{
					JSONValue tokener = JSONParser.parseStrict(result);
					
					JSONObject obj = tokener.isObject();
					
					if(!!parseUserDataFromResponse(user, obj)){
						op.onEvent(new LoginEvent(user, true, automatic));
					}else{
						op.onEvent(new LoginEvent(user, false, automatic));
					}
				}catch(Throwable t){
					op.onEvent(new LoginEvent(user, false, automatic));
				}
            }

			@Override
            public void onError(String error) {
				op.onEvent(new LoginEvent(user, false, automatic));
	            
            }});
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
			
			userinfo = ((JSONArray)userinfo.get("response")).get(0).isObject();
			userinfo = (JSONObject)userinfo.get("userinfo");
			
			if(userinfo.get("user_id") instanceof JSONNumber){
				user.setUserId((int)(((JSONNumber) userinfo.get("user_id")).doubleValue()));
			}else{
				user.setUserId(Integer.valueOf(((JSONString) userinfo.get("user_id")).stringValue()));
			}
			user.setUserName(((JSONString)userinfo.get("username")).stringValue());
			user.setRealName(((JSONString)userinfo.get("realname")).stringValue());
			user.setIdentifier(((JSONString)userinfo.get("identifier")).stringValue());
			
			if(userinfo.get("token") instanceof JSONString){
				user.setToken(((JSONString)userinfo.get("token")).stringValue());
			}
			
			// Further fields are not parsed yet, because they are not needed
			// This is the complete response with all available fields:
			/*
			<responses>
			  <response>
			    <userinfo>
			      <user_id>4711</user_id>
			      <username>johndoe</username>
			      <ggt_profile_url>http://tube.geogebra.org/user/profile/id/4711
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
	private JSONObject buildTokenLoginRequest(String token, String cookie) {
		JSONObject requestJSON = new JSONObject();
		JSONObject apiJSON = new JSONObject();
		JSONObject loginJSON = new JSONObject();
		try{
			if(token!=null){
				loginJSON.put("token", new JSONString(token));
			}else{
				loginJSON.put("cookie", new JSONString(cookie));
			}
			loginJSON.put("getuserinfo", new JSONString("true"));
			apiJSON.put("login", loginJSON);		
			apiJSON.put("api", new JSONString("1.0.0"));
			requestJSON.put("request", apiJSON);
		}
		catch(JSONException e){
			e.printStackTrace();
		}
		return requestJSON;
	}

	
	public void uploadMaterial(AppW app, String filename, MaterialCallback cb) {
		performUploadRequest(UploadRequest.getRequestElement(app, filename).toJSONString(), cb);//new UploadRequest(app).toJSONString());
	}

	public void getUsersMaterials(int userId, MaterialCallback rc) {
		performRequest(MaterialRequest.forUser(userId, client).toJSONString(), rc);
    }

	public void getBookItems(int id, MaterialCallback rc) {
		performRequest(MaterialRequest.forBook(id, client).toJSONString(), rc);
    }
}
