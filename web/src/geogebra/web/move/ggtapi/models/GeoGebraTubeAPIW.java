package geogebra.web.move.ggtapi.models;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.ggtapi.models.AjaxCallback;
import geogebra.common.move.ggtapi.models.Chapter;
import geogebra.common.move.ggtapi.models.ClientInfo;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.MaterialRequest;
import geogebra.common.move.ggtapi.models.SyncEvent;
import geogebra.common.util.StringUtil;
import geogebra.common.util.debug.Log;
import geogebra.html5.main.AppW;
import geogebra.html5.main.GeoGebraTubeAPIWSimple;
import geogebra.html5.util.WindowW;
import geogebra.html5.util.ggtapi.JSONparserGGT;

import java.util.ArrayList;

import com.google.gwt.core.client.JavaScriptObject;
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
public class GeoGebraTubeAPIW extends GeoGebraTubeAPIWSimple {
	private RequestBuilder requestBuilder;
	private ClientInfo client;

	/**
	 * @param url
	 *            String
	 * @param client
	 *            {@link ClientInfo}
	 */
	public GeoGebraTubeAPIW(ClientInfo client, boolean beta) {
		super(beta);
		this.requestBuilder = new RequestBuilder(RequestBuilder.POST, getUrl());
		this.client = client;
	}

	/**
	 * Search for materials containing the String query
	 * 
	 * @param query
	 *            search String
	 * @param callback
	 *            {@link MaterialCallback}
	 */
	public void search(String query, MaterialCallback callback) {
		performRequest(new MaterialRequest(query, client).toJSONString(client),
		        callback);
	}

	/**
	 * Returns materials in the given amount and order
	 * 
	 * @param callback
	 *            {@link MaterialCallback}
	 */
	public void getFeaturedMaterials(MaterialCallback callback) {
		performRequest(
		        MaterialRequest.forFeatured(client).toJSONString(client),
		        callback);
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
	 * @param id
	 *            int
	 * @param callback
	 *            {@link MaterialCallback}
	 */
	public void getItem(String id, MaterialCallback callback) {
		performRequest(MaterialRequest.forId(id, client).toJSONString(client),
		        callback);
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
	 * @param requestString
	 *            String
	 * @param cb
	 *            {@link MaterialCallback}
	 */
	protected void performRequest(String requestString,
	        final MaterialCallback cb) {
		try {
			RequestCallback callback = new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
				        Response response) {
					ArrayList<Material> result = new ArrayList<Material>();
					ArrayList<Chapter> meta = JSONparserGGT.parseResponse(
					        response.getText(), result);
					cb.onLoaded(result, meta);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					cb.onError(exception);

				}
			};
			this.requestBuilder.sendRequest(requestString, callback);
		} catch (RequestException e) {
			// TODO Handle the error!
			e.printStackTrace();
		}
	}

	@Override
	protected geogebra.common.util.HttpRequest createHttpRequest() {
		return new geogebra.html5.util.HttpRequestW();
	}

	/**
	 * Copies the user data from the API response to this user.
	 * 
	 * @return true if the data could be parsed successfully, false otherwise
	 */
	@Override
	public boolean parseUserDataFromResponse(GeoGebraTubeUser user,
	        String result) {
		try {
			JSONValue tokener = JSONParser.parseStrict(result);

			JSONObject response = tokener.isObject();
			JSONObject userinfo = (JSONObject) response.get("responses");

			userinfo = ((JSONArray) userinfo.get("response")).get(0).isObject();
			userinfo = (JSONObject) userinfo.get("userinfo");

			if (userinfo.get("user_id") instanceof JSONNumber) {
				user.setUserId((int) (((JSONNumber) userinfo.get("user_id"))
				        .doubleValue()));
			} else {
				user.setUserId(Integer.valueOf(((JSONString) userinfo
				        .get("user_id")).stringValue()));
			}
			user.setUserName(((JSONString) userinfo.get("username"))
			        .stringValue());
			user.setRealName(((JSONString) userinfo.get("realname"))
			        .stringValue());
			user.setIdentifier(((JSONString) userinfo.get("identifier"))
			        .stringValue());
			if (userinfo.get("image") instanceof JSONString) {
				user.setImageURL(((JSONString) userinfo.get("image"))
				        .stringValue());
			}
			if (userinfo.get("lang_ui") instanceof JSONString) {
				user.setLanguage(((JSONString) userinfo.get("lang_ui"))
				        .stringValue());
			}

			if (userinfo.get("token") instanceof JSONString) {
				user.setToken(((JSONString) userinfo.get("token"))
				        .stringValue());
			}

			// Further fields are not parsed yet, because they are not needed
			// This is the complete response with all available fields:
			/*
			 * <responses> <response> <userinfo> <user_id>4711</user_id>
			 * <username>johndoe</username>
			 * <ggt_profile_url>http://tube.geogebra.org/user/profile/id/4711
			 * </ggt_profile_url> <group>user</group>
			 * <date_created>2012-09-18</date_created> <lang_ui>en</lang_ui>
			 * <lang_content>en,en_US,it</lang_content>
			 * <timezone>America/New_York</timezone> <materials>31</materials>
			 * <favorites>4</favorites> <collections>2</collections>
			 * <identifier>forum:0815</identifier> <realname>John Doe</realname>
			 * <occupation>Maths teacher</occupation> <location>New
			 * York</location> <website>www.thisisme.com</website>
			 * <profilemessage>Any text</profilemessage> </userinfo> </response>
			 * </responses>
			 */

			// user.setGGTProfileURL(userinfo.getString("ggt_profile_url"));
			// user.setGroup(userinfo.getString("group"));
			// user.setDateCreated(userinfo.getString("date_created"));
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Builds the request to check if the login token of a user is valid. This
	 * request will send detailed user information as response.
	 * 
	 * @return The JSONObject that contains the request.
	 */
	@Override
	protected String buildTokenLoginRequest(String token, String cookie) {
		JSONObject requestJSON = new JSONObject();
		JSONObject apiJSON = new JSONObject();
		JSONObject loginJSON = new JSONObject();
		try {
			if (token != null) {
				loginJSON.put("token", new JSONString(token));
			} else {
				loginJSON.put("cookie", new JSONString(cookie));
			}
			loginJSON.put("getuserinfo", new JSONString("true"));
			loginJSON.put("image", new JSONString("true"));
			apiJSON.put("login", loginJSON);
			apiJSON.put("api", new JSONString("1.0.0"));
			requestJSON.put("request", apiJSON);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestJSON.toString();
	}

	/**
	 * Uploads the actual opened application to ggt
	 * 
	 * @param app
	 *            AppW
	 * @param filename
	 *            String
	 * @param cb
	 *            MaterialCallback
	 */
	public void uploadMaterial(App app, int tubeID, String visibility,
	        final String filename,
	        String base64, final MaterialCallback cb) {

		performRequest(
		        UploadRequest.getRequestElement(tubeID, visibility, filename,
		                base64)
.toJSONString(client), cb);

	}

	/**
	 * to rename materials on ggt; TODO no use of base64
	 * 
	 * @param app
	 *            {@link AppW}
	 * @param mat
	 *            {@link Material}
	 * @param cb
	 *            {@link MaterialCallback}
	 */
	public void uploadRenameMaterial(final AppW app, Material mat,
	        final MaterialCallback cb) {
		performRequest(
		        UploadRequest.getRequestElement(app, mat.getTitle(),
		                mat.getId()).toJSONString(client), cb);
	}

	/**
	 * Uploads a local saved file (web - localStorage; touch - device) to ggt
	 * 
	 * @param app
	 *            {@link AppW}
	 * @param mat
	 *            {@link Material}
	 * @param cb
	 *            {@link MaterialCallback}
	 */
	public void uploadLocalMaterial(final AppW app, final Material mat,
	        final MaterialCallback cb) {
		performRequest(
		        UploadRequest.getRequestElement(app, mat).toJSONString(client),
		        cb);
	}

	/**
	 * @param app
	 *            {@link AppW}
	 * @param material
	 *            {@link Material}
	 * @param cb
	 *            {@link MaterialCallback}
	 */
	public void deleteMaterial(AppW app, Material material,
	        final MaterialCallback cb) {
		performRequest(DeleteRequest.getRequestElement(app, material)
		        .toJSONString(client), cb);
	}

	/**
	 * @param userId
	 *            int
	 * @param cb
	 *            {@link MaterialCallback}
	 */
	public void getUsersMaterials(MaterialCallback cb) {
		performRequest(
		        MaterialRequest.forCurrentUser(client).toJSONString(client),
		        cb);
	}

	/**
	 * @param id
	 *            int
	 * @param cb
	 *            {@link MaterialCallback}
	 */
	public void getBookItems(int id, MaterialCallback cb) {
		performRequest(
		        MaterialRequest.forBook(id, client).toJSONString(client), cb);
	}

	public void getWorksheetItems(int id, MaterialCallback cb) {
		performRequest(
		        MaterialRequest.forWorksheet(id, client).toJSONString(client),
		        cb);
	}

	public void sync(AppW app, long timestamp, final SyncCallback cb) {
		this.performRequest(
		        new SyncRequest(app, timestamp).toJSONString(client),
		        false, new AjaxCallback() {

			        @Override
			        public void onSuccess(String response) {
				        ArrayList<SyncEvent> events = new ArrayList<SyncEvent>();
				        try {
					        JSONValue jv = JSONParser.parseStrict(response);
					        JSONValue items = jv.isObject().get("responses")
					                .isObject()
.get("response").isObject()
					                .get("item");
					        JSONArray array = items.isArray();

					        if (array != null) {
						        for (int i = 0; i < array.size(); i++) {
							        addEvent(array.get(i).isObject(), events);
						        }
					        }
 else if (items.isObject() != null) {
						        addEvent(items.isObject(), events);
					        }
					        cb.onSync(events);
				        } catch (Exception e) {
					        Log.error("SYNC parse error" + e.getMessage());
				        }

			        }

			        private void addEvent(JSONObject object,
			                ArrayList<SyncEvent> events) {
				        SyncEvent se = new SyncEvent(Integer.parseInt(object
				                .get("id").isString().stringValue()), Long
				                .parseLong(object.get("ts").isString()
				                        .stringValue()));
				        if (object.get("deleted") != null
				                && object.get("deleted").isString() != null) {
					        se.setDelete(true);
				        }
				        if (object.get("favorite") != null
				                && object.get("favorite").isString()
				                        .stringValue()
				                        .equals("true")) {
					        se.setFavorite(true);
				        }
				        if (object.get("unfavorited") != null
				                && object.get("unfavorited").isString() != null) {
					        se.setUnfavorite(true);
				        }
				        events.add(se);

			        }

					@Override
			        public void onError(String error) {
				        App.error("SYNCE error" + error);

			        }
		        });
	}

	/**
	 * @param app
	 *            {@link AppW}
	 * @param sliderName
	 *            {@link Material}
	 * @param cb
	 *            {@link MaterialCallback}
	 */
	public void exportAnimGif(final AppW app, String sliderName, int timing,
	        boolean isLoop) {

		String data = createGifResponsePage(app.getLocalization());
		final JavaScriptObject gifWnd = WindowW.openFromData(data);
		WindowW.postMessage(gifWnd,
		        StringUtil.toHTMLString(app.getLocalization().getPlain(
		                "AnimatedGIF.Processing")));

		RequestCallback cb = new RequestCallback() {

			public void onResponseReceived(Request request, Response response) {

				if (response != null) {
					try {
						String respData = response.getText();
						if (respData == null || respData.equals("")) {
							App.debug("[ANIMGIF] no response");
							return;
						}
						App.debug("[ANIMGIF] respData is: " + respData);
						JSONValue responseObject = JSONParser
						        .parseStrict(respData);
						if (responseObject == null
						        || responseObject.isObject() == null) {
							App.debug("[ANIMGIF] responseObject is null");
							WindowW.postMessage(gifWnd, StringUtil
							        .toHTMLString(app.getLocalization()
							                .getPlain("AnimatedGIF.ErrorA",
							                        respData)));
							return;
						}

						JSONValue responses = responseObject.isObject().get(
						        "responses");

						if (responses == null || responses.isObject() == null) {
							JSONValue error = responseObject.isObject()
							        .get("error").isObject().get("-type")
							        .isString();
							App.debug("[ANIMGIF] error is " + error.toString());

							WindowW.postMessage(
							        gifWnd,
							        StringUtil
							                .toHTMLString(app
							                        .getLocalization()
							                        .getPlain(
							                "AnimatedGIF.ErrorA",
							                " " + error.toString() + " ("
							                                        + response
							                                                .getStatusCode())));
							return;
						}

						JSONValue base64 = responses.isObject().get("response")
						        .isObject().get("value");

						String downloadUrl = "data:image/gif;base64,"
						        + base64.isString().stringValue();
						AppW.download(downloadUrl, "ggbanim.gif");
						WindowW.postMessage(gifWnd, StringUtil.toHTMLString(app
						        .getLocalization().getPlain(
						                "AnimatedGIF.Success")));
					} catch (Throwable t) {
						App.debug(t.getMessage());
						App.debug("'" + response + "'");
					}
				}
			}

			public void onError(Request request, Throwable exception) {
				App.debug("[ANIMGIF] EXCEPTION: " + exception);

			}
		};

		RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, getUrl());

		App.debug("[URL] " + getUrl());
		String req = AnimGifRequest.getRequestElement(app, sliderName,
		        timing,
		        isLoop)
		        .toJSONString(client);
		App.debug("[REQUEST]: " + req);
		try {
			rb.sendRequest(req, cb);
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static String createGifResponsePage(Localization loc) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("<!doctype html> \n");
	    sb.append("	 <html> \n");
	 	sb.append("  <head>\n");
	    sb.append("    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n");
		sb.append("    <title>"
		        + StringUtil.toHTMLString(loc.getPlain("AnimatedGIFExport"))
		        + "</title>\n");
	    sb.append("    <script>\n");
	    sb.append("	window.addEventListener('message',function(event) {\n");
	    sb.append("		console.log('received response:  ',event.data);\n");
	    sb.append("		var result = document.getElementById('result');\n");
	    sb.append("		result.innerHTML = event.data;\n");
	    sb.append("		var button = document.getElementById('close');\n");
	    sb.append("		button.setAttribute('style', 'display: block');\n");
	    sb.append("		},false);\n");
	    sb.append("    </script>\n");
	    sb.append("  </head>\n");

	    sb.append("  <body>\n");
	    sb.append("    <iframe src=\"javascript:''\" id=\"__gwt_historyFrame\" tabIndex='-1' style=\"position:absolute;width:0;height:0;border:0\"></iframe>\n");
		sb.append("	<h1>"
		        + StringUtil.toHTMLString(loc.getPlain("CreatingAnimatedGIF"))
		        + "</h1>\n");
		sb.append("	<p id=\"result\">"
		        + StringUtil.toHTMLString(loc
		                .getPlain("AnimatedGIF.Calculating")) + "</p>\n");
	    sb.append("	<button type=\"button\" id=\"close\" onclick=\"window.close();\" style=\"display: none;\">Close</button>\n");
	    sb.append("   </body>\n");
	    sb.append("</html>\n");
	    		
	    return sb.toString();
    }

	@Override
	protected String getToken() {
		return client.getModel().getLoginToken();
	}
}
