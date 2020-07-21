package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.TubeAvailabilityCheckEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialRequest.Order;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.ggtapi.requests.DeleteRequest;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.move.ggtapi.requests.ShareRequest;
import org.geogebra.common.move.ggtapi.requests.SyncCallback;
import org.geogebra.common.move.ggtapi.requests.SyncRequest;
import org.geogebra.common.move.ggtapi.requests.UploadRequest;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.debug.Log;

/**
 * @author gabor Common base for GeoGebraTubeApi
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

	protected boolean available = true;
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
			op.stayLoggedOut();
			return;
		}
		performRequest(
				buildTokenLoginRequest(user.getLoginToken(), user.getCookie()),
				true, new AjaxCallback() {
					@Override
					public void onSuccess(String responseStr) {
						try {
							GeoGebraTubeAPI.this.availabilityCheckDone = true;

							GeoGebraTubeAPI.this.available = true;

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
							e.printStackTrace();
						}

					}

					@Override
					public void onError(String error) {
						GeoGebraTubeAPI.this.availabilityCheckDone = true;
						GeoGebraTubeAPI.this.available = false;
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
	protected static final String buildTokenLoginRequest(String token,
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
	public boolean checkAvailable(LogInOperation op) {
		if (this.availabilityCheckDone && op != null) {
			op.onEvent(new TubeAvailabilityCheckEvent(this.available));
		}
		checkIfAvailable(op);
		return this.available;
	}

	/**
	 * @return whether the API is available; assume true if not tested
	 */
	public boolean isAvailable() {
		return this.available;
	}

	/**
	 * Sends a test request to GeoGebraTube to check if it is available The
	 * result is stored in a boolean variable. Subsequent calls to isAvailable()
	 * will return the value of the stored variable and don't send the request
	 * again.
	 */
	private void checkIfAvailable(final LogInOperation op) {
		if (!this.availabilityCheckDone) {
			this.available = false;
		}
		this.availabilityCheckDone = false;
		try {
			performRequest(
					"{\"request\": {\"-api\": \"1.0.0\", \"task\": {\"-type\": \"info\"}}}",
					false, new AjaxCallback() {

						@Override
						public void onSuccess(String response) {
							GeoGebraTubeAPI.this.availabilityCheckDone = true;
							GeoGebraTubeAPI.this.available = true;
							if (op != null) {
								op.onEvent(
										new TubeAvailabilityCheckEvent(true));
							}
						}

						@Override
						public void onError(String error) {
							GeoGebraTubeAPI.this.availabilityCheckDone = true;
							GeoGebraTubeAPI.this.available = false;
							if (op != null) {
								op.onEvent(
										new TubeAvailabilityCheckEvent(true));
							}

						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public void favorite(int id, boolean favorite) {
		performRequest("{\"request\": {" + "\"-api\":\"1.0.0\","
				+ "\"login\": {\"-token\":\"" + getToken() + "\"},"
				+ "\"task\": {\"-type\":\"favorite\", \"id\":\"" + id
				+ "\",\"favorite\":\"" + favorite + "\"}}}", false,
				new AjaxCallback() {

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
	public void uploadRenameMaterial(Material mat, final MaterialCallbackI cb) {
		performRequest(
				UploadRequest.getRequestElement(mat.getTitle(), mat.getId())
						.toJSONString(client),
				cb);
	}

	@Override
	public void uploadLocalMaterial(final Material mat,
			final MaterialCallbackI cb) {
		performRequest(
				UploadRequest.getRequestElement(mat).toJSONString(client), cb);
	}

	@Override
	public void deleteMaterial(Material material, final MaterialCallbackI cb) {
		if (material.getType() == MaterialType.ggsTemplate) {
			getMaterialRestAPI().deleteMaterial(material, cb);
		} else {
			performRequest(
					DeleteRequest.getRequestElement(material).toJSONString(client), cb);
		}
	}

	@Override
	public void shareMaterial(Material material, String to, String message,
			final MaterialCallbackI cb) {
		performRequest(ShareRequest.getRequestElement(material, to, message)
				.toJSONString(client), cb);
	}

	@Override
	public void getUsersMaterials(MaterialCallbackI cb, Order order) {
		performRequest(
				MaterialRequest.forCurrentUser(client).toJSONString(client),
				cb);
	}

	@Override
	public void getUsersOwnMaterials(MaterialCallbackI cb, Order order) {
		performRequest(
				MaterialRequest.forUser(client.getModel().getUserId(), client)
						.toJSONString(client), cb);
	}

	@Override
	public void getSharedMaterials(final MaterialCallbackI cb,
			MaterialRequest.Order order) {
		// not supported
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
				ArrayList<Chapter> meta = JSONParserGGT.prototype
						.parseResponse(response, result);
				if (cb != null) {
					cb.onLoaded(result, meta);
				}

			}

			@Override
			public void onError(String error) {
				cb.onError(new Exception(error));

			}
		});

	}

	/**
	 * @param id
	 *            int
	 * @param cb
	 *            {@link MaterialCallbackI}
	 */
	public void getBookItems(int id, MaterialCallbackI cb) {
		performRequest(MaterialRequest.forBook(id, client).toJSONString(client),
				cb);
	}

	/**
	 * Get all ggb elements from a worksheet.
	 *
	 * @param id
	 *            worksheet id
	 * @param cb
	 *            callback
	 */
	public void getWorksheetItems(int id, MaterialCallbackI cb) {
		performRequest(MaterialRequest.forWorksheet(id, client).toJSONString(client), cb);
	}

	@Override
	public void uploadMaterial(String tubeID, String visibility,
			final String filename, String base64, final MaterialCallbackI cb,
			MaterialType type) {
		if (type == MaterialType.ggsTemplate) {
			getMaterialRestAPI().uploadMaterial(tubeID, visibility, filename, base64, cb, type);
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

	/**
	 * Search for materials containing the String query
	 *
	 * @param query
	 *            search String
	 * @param callback
	 *            {@link MaterialCallbackI}
	 */
	public void search(String query, MaterialCallbackI callback) {
		performRequest(new MaterialRequest(query, client).toJSONString(client),
				callback);
	}

	@Override
	public void getFeaturedMaterials(MaterialCallbackI callback) {
		performRequest(MaterialRequest.forFeatured(client).toJSONString(client),
				callback);
	}

	@Override
	public void getItem(String id, MaterialCallbackI callback) {
		performRequest(MaterialRequest.forId(id, client).toJSONString(client),
				callback);
	}

	@Override
	public boolean isCheckDone() {
		return availabilityCheckDone;
	}

	@Override
	public void sync(long timestamp, final SyncCallback cb) {
		this.performRequest(new SyncRequest(timestamp).toJSONString(client),
				false, new AjaxCallback() {

					@Override
					public void onSuccess(String response) {
						ArrayList<SyncEvent> events = new ArrayList<>();
						try {
							JSONTokener tok = new JSONTokener(response);
							JSONObject responseObj = (JSONObject) ((JSONObject) new JSONObject(
									tok).get("responses"))

											.get("response");
							Object items = responseObj.get("item");

							JSONParserGGT.prototype.addEvents(events, items);

							cb.onSync(events);
						} catch (Exception e) {
							Log.error("SYNC parse error" + e.getMessage());
						}

					}

					@Override
					public void onError(String error) {
						Log.error("SYNCE error" + error);

					}
				});
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
	public boolean performCookieLogin(LogInOperation op) {
		return false;
	}

	@Override
	public void performTokenLogin(LogInOperation logInOperation, String token) {
		if (token != null) {
			logInOperation.performTokenLogin(token, true);
		} else if (!performCookieLogin(logInOperation)) {
			checkAvailable(logInOperation);
		}
	}

	@Override
	public void copy(final Material material, final String title,
			final MaterialCallbackI copyCallback) {
		getItem(material.getSharingKeyOrId(), new MaterialCallbackI() {

			@Override
			public void onLoaded(List<Material> result, ArrayList<Chapter> meta) {
				if (result.size() > 0) {
					uploadMaterial("", result.get(0).getVisibility(),
						title,
						result.get(0).getBase64(), copyCallback, result.get(0).getType(),
						result.get(0));
				} else {
					copyCallback.onError(
							new Exception("No material found: " + material.getSharingKeyOrId()));
				}
			}

			@Override
			public void onError(Throwable exception) {
				copyCallback.onError(exception);
			}
		});
	}

	@Override
	public void setShared(Material m, String groupID, boolean shared,
			AsyncOperation<Boolean> callback) {
		// no group sharing for old materials
	}

	@Override
	public void getGroups(String materialID, AsyncOperation<List<String>> asyncOperation) {
		// no group sharing for old materials
	}

	@Override
	public boolean owns(Material mat) {
		return mat.getAuthorID() <= 0
				|| mat.getAuthorID() == client.getModel().getUserId();
	}

	@Override
	public boolean canUserShare(boolean student) {
		return true;
	}

	@Override
	public boolean anonymousOpen() {
		return true;
	}

	@Override
	public void getTemplateMaterials(MaterialCallbackI cb) {
		getMaterialRestAPI().getTemplateMaterials(cb);
	}

	protected MaterialRestAPI getMaterialRestAPI() {
		// only in web for now
		return null;
	}
}
