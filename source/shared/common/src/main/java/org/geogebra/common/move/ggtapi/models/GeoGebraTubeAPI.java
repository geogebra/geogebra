package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;

import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.move.ggtapi.requests.UploadRequest;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.debug.Log;

/**
 * Common base for geogebra.org JSON API
 * @author gabor
 */
public abstract class GeoGebraTubeAPI implements BackendAPI {

	/**
	 * The Standard Result Quantity
	 */
	public static final int STANDARD_RESULT_QUANTITY = 30;

	/** Public url DO NOT CHANGE!s */
	public static final String url = "https://www.geogebra.org/api/json.php";
	/** For beta version */
	public static final String urlBeta = "https://beta.geogebra.org/api/json.php";
	/** Public login url DO NOT CHANGE! */
	public static final String login_url = "https://accounts.geogebra.org/api/index.php";
	/** Login for beta */
	public static final String login_urlBeta = "https://accounts-beta.geogebra.org/api/index.php";

	protected boolean availabilityCheckDone = false;
	protected ClientInfo client;
	private String materialsURL;

	private String loginURL;

	/**
	 * @param beta
	 *            true if beta
	 */
	public GeoGebraTubeAPI(boolean beta) {
		this.materialsURL = beta ? urlBeta : url;
		this.loginURL = beta ? login_urlBeta : login_url;
	}

	/**
	 * @param url1
	 *            materials API url
	 */
	public void setURL(String url1) {
		this.materialsURL = url1;
	}

	/**
	 * @param loginAPIurl
	 *            login API url
	 */
	public void setLoginURL(String loginAPIurl) {
		this.loginURL = loginAPIurl;
	}

	/**
	 * Private method performing the request given by requestString
	 *
	 * @param requestString
	 *            JSON request String for the GeoGebraTubeAPI
	 * @param login
	 *            whether to use login endpoint
	 * @param callback
	 *            callback
	 */
	protected final void performRequest(String requestString, boolean login,
			AjaxCallback callback) {
		String postUrl = login ? getLoginUrl() : getUrl();
		if ("null".equals(postUrl)) {
			return;
		}
		HttpRequest request = createHttpRequest();
		request.sendRequestPost("POST", postUrl, requestString,
				callback);
	}

	/**
	 * @return login API URL
	 */
	@Override
	public final String getLoginUrl() {
		return loginURL;
	}

	/**
	 * @return API url
	 */
	@Override
	public final String getUrl() {
		return materialsURL;
	}

	/**
	 * Creates a new Http request
	 *
	 * @return The new http request
	 */
	protected abstract HttpRequest createHttpRequest();

	@Override
	public final void authorizeUser(final GeoGebraTubeUser user,
			final LogInOperation op, final boolean automatic) {
		if ("".equals(user.getLoginToken())) {
			op.loginCanceled();
			return;
		}
		performRequest(
				buildTokenLoginRequest(user.getLoginToken(), user.getCookie()),
				true, new AjaxCallback() {
					@Override
					public void onSuccess(String responseStr) {
						try {
							GeoGebraTubeAPI.this.availabilityCheckDone = true;

							// Parse the userdata from the response
							if (!parseUserDataFromResponse(user, responseStr)) {
								op.onEvent(new LoginEvent(user, false,
										automatic, responseStr));
								return;
							}

							op.onEvent(new LoginEvent(user, true, automatic,
									responseStr));

							// GeoGebraTubeAPID.this.available = false;
						} catch (Exception e) {
							Log.debug(e);
						}

					}

					@Override
					public void onError(String error) {
						GeoGebraTubeAPI.this.availabilityCheckDone = true;
						op.onEvent(
								new LoginEvent(user, false, automatic, null));
					}
				});
	}

	/**
	 * Builds the request to check if the login token of a user is valid. This
	 * request will send detailed user information as response.
	 *
	 * @param token
	 *            The user that should be logged in
	 * @param cookie
	 *            cookie (for web)
	 * @return The JSONObject that contains the request.
	 */
	protected static String buildTokenLoginRequest(String token,
			String cookie) {
		JSONObject requestJSON = new JSONObject();
		JSONObject apiJSON = new JSONObject();
		JSONObject loginJSON = new JSONObject();
		try {
			if (token != null) {
				loginJSON.put("token", token);
			} else {
				loginJSON.put("cookie", cookie);
			}
			loginJSON.put("getuserinfo", "true");
			apiJSON.put("login", loginJSON);
			apiJSON.put("api", "1.0.0");
			requestJSON.put("request", apiJSON);
		} catch (Exception e) {
			Log.debug("problem building request: " + e.getMessage());
			return null;
		}
		return requestJSON.toString();
	}

	@Override
	public void setUserLanguage(String lang, String token) {
		performRequest("{\"request\": {" + "\"api\":\"1.0.0\","
				+ "\"login\": {\"token\":\"" + token
				+ "\", \"getuserinfo\":\"false\"},"
				+ "\"task\": {\"type\":\"setuserlang\", \"lang\":\"" + lang
				+ "\"}}}", true, new AjaxCallback() {

			@Override
			public void onSuccess(String response) {
				// yay, it worked
			}

			@Override
			public void onError(String error) {
				Log.error(error);

			}
		});
	}

	@Override
	public void logout(String token) {
		performRequest("{\"request\": {" + "\"api\":\"1.0.0\","
				+ "\"logout\": {\"token\":\"" + token
				+ "\", \"getuserinfo\":\"false\"}}}", true, new AjaxCallback() {

			@Override
			public void onSuccess(String response) {
				// yay, it worked
			}

			@Override
			public void onError(String error) {
				Log.error(error);

			}
		});
	}

	@Override
	public void uploadLocalMaterial(final Material mat,
			final MaterialCallbackI cb) {
		performRequest(
				UploadRequest.getRequestElement(mat).toJSONString(client), cb);
	}

	/**
	 * @param requestString
	 *            json string representing the request
	 * @param cb
	 *            {@link MaterialCallbackI}
	 */
	protected final void performRequest(String requestString,
			final MaterialCallbackI cb) {
		if ("null".equals(getUrl())) {
			return;
		}
		HttpRequest req = createHttpRequest();
		req.sendRequestPost("POST", getUrl(), requestString, new AjaxCallback() {

			@Override
			public void onSuccess(String response) {
				ArrayList<Material> result = new ArrayList<>();
				JSONParserGGT.prototype
						.parseResponse(response, result);
				if (cb != null) {
					cb.onLoaded(result, null);
				}

			}

			@Override
			public void onError(String error) {
				cb.onError(new Exception(error));
			}
		});

	}

	@Override
	public void uploadMaterial(String tubeID, String visibility,
			final String filename, String base64, final MaterialCallbackI cb,
			MaterialType type, boolean isMultiuser) {
		if (type == MaterialType.ggsTemplate) {
			getMaterialRestAPI().uploadMaterial(tubeID, visibility, filename, base64, cb, type,
					isMultiuser);
		} else {
			uploadMaterial(tubeID, visibility, filename, base64, cb, type, null);
		}
	}

	/**
	 * Uploads the actual opened application to ggt
	 *
	 * @param tubeID
	 *            tube id
	 * @param visibility
	 *            visibility string
	 *
	 * @param filename
	 *            String
	 * @param base64
	 *            base64 string
	 * @param cb
	 *            MaterialCallback
	 * @param type
	 *            material type
	 * @param parent
	 *            parent ID
	 */
	public void uploadMaterial(String tubeID, String visibility,
			final String filename, String base64, final MaterialCallbackI cb,
			MaterialType type, Material parent) {
		performRequest(UploadRequest
				.getRequestElement(tubeID, visibility, filename, base64, type,
						parent)
				.toJSONString(client), cb);
	}

	@Override
	public boolean isCheckDone() {
		return availabilityCheckDone;
	}

	/**
	 * @return login token
	 */
	protected String getToken() {
		return client.getModel().getLoginToken();
	}

	@Override
	public void setClient(ClientInfo clientInfo) {
		this.client = clientInfo;
	}

	@Override
	public boolean canUserShare(boolean student) {
		return true;
	}

	@Override
	public boolean anonymousOpen() {
		return true;
	}

	protected MaterialRestAPI getMaterialRestAPI() {
		// only in web for now
		return null;
	}
}
