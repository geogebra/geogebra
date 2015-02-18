package geogebra.common.move.ggtapi.models;

import geogebra.common.move.ggtapi.TubeAvailabilityCheckEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.ggtapi.operations.LogInOperation;
import geogebra.common.util.HttpRequest;
import geogebra.common.util.debug.Log;

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
	public static String test_url = "http://test-tube.geogebra.org/api/json.php";

	/**
	 * Public url (no SSL)
	 * DO NOT CHANGE!
	 */
	public static String url = "https://tube.geogebra.org/api/json.php";
	public static String urlBeta = "http://tube-beta.geogebra.org/api/json.php";
	public static String login_url = "https://accounts.geogebra.org/api/index.php";
	public static String login_urlBeta = "https://login-beta.geogebra.org/api/index.php";
	/**
	 * Instance of the new GeoGebraTube API D/W/T
	 */
	protected static GeoGebraTubeAPI instance;
	
	static public final int LOGIN_TOKEN_VALID = 0;
	static public final int LOGIN_TOKEN_INVALID = 1;
	static public final int LOGIN_REQUEST_FAILED = -2;
	
	protected boolean available = true;
	protected boolean availabilityCheckDone = false;
	
	/**
	 * Private method performing the request given by requestString
	 * 
	 * @param requestString
	 *          JSON request String for the GeoGebraTubeAPI
	 * @return The HttpRequest object that contains the response and error information 
	 */
	protected final void performRequest(String requestString, boolean login, AjaxCallback callback)
	{
		HttpRequest request = createHttpRequest();
		request.sendRequestPost(login ? getLoginUrl() : getUrl(),
				requestString,
				callback);
	}
	
	protected abstract String getLoginUrl();

	protected abstract String getUrl();

	/**
	 * Creates a new Http request
	 * 
	 * @return The new http request
	 */
	protected abstract HttpRequest createHttpRequest();

	protected abstract boolean parseUserDataFromResponse(
			GeoGebraTubeUser user, String response);
	
	/**
	 * Sends a request to the GeoGebraTube API to check if the login token which is defined in the specified 
	 * GeoGebraTubeUser is valid.
	 * 
	 * @param user The user that should be authorized.
	 * @return One of the following return codes: LOGIN_TOKEN_VALID, LOGIN_TOKEN_INVALID, LOGIN_REQUEST_FAILED
	 */
	public final void authorizeUser(final GeoGebraTubeUser user,
			final LogInOperation op, final boolean automatic) {
		performRequest(buildTokenLoginRequest(user.getLoginToken(), user.getCookie()),
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

							op.onEvent(new LoginEvent(user, true, automatic, responseStr));

							// GeoGebraTubeAPID.this.available = false;
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					

					@Override
					public void onError(String error) {
						GeoGebraTubeAPI.this.availabilityCheckDone = true;
						GeoGebraTubeAPI.this.available = false;
						op.onEvent(new LoginEvent(user, false, automatic, null));
					}
				});

	}

	protected abstract String buildTokenLoginRequest(String loginToken, String cookie);

	public boolean checkAvailable(LogInOperation op) {
		if (this.availabilityCheckDone && op != null) {
			op.onEvent(new TubeAvailabilityCheckEvent(this.available));
		}
		checkIfAvailable(op, getClientInfo());
		return this.available;
	}
	
	public boolean isAvailable() {
		return this.available;
	}

	protected String getClientInfo() {
		return "";
	}

	/**
	 * Sends a test request to GeoGebraTube to check if it is available The
	 * result is stored in a boolean variable. Subsequent calls to isAvailable()
	 * will return the value of the stored variable and don't send the request
	 * again.
	 * 
	 * @return boolean if the request was successful.
	 */
	private boolean checkIfAvailable(final LogInOperation op, String clientInfo) {
		if(!this.availabilityCheckDone){
			this.available = false;
		}
		this.availabilityCheckDone = false;
		try {
			performRequest(
					"{\"request\": {\"-api\": \"1.0.0\","+clientInfo+"\"task\": {\"-type\": \"info\"}}}",
					false, new AjaxCallback() {

						@Override
						public void onSuccess(String response) {
							GeoGebraTubeAPI.this.availabilityCheckDone = true;
							GeoGebraTubeAPI.this.available = true;
							if(op!=null){
								op.onEvent(new TubeAvailabilityCheckEvent(true));
							}
						}

						@Override
						public void onError(String error) {
							GeoGebraTubeAPI.this.availabilityCheckDone = true;
							GeoGebraTubeAPI.this.available = false;
							if(op!=null){
								op.onEvent(new TubeAvailabilityCheckEvent(true));
							}

						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return this.available;
	}

	public void setUserLanguage(String lang, String token) {
		performRequest(
				"{\"request\": {"
                        +"\"api\":\"1.0.0\","
                        +"\"login\": {\"token\":\""+token+"\", \"getuserinfo\":\"false\"},"
                        +"\"task\": {\"type\":\"setuserlang\", \"lang\":\""+lang+"\"}}}",
				true, new AjaxCallback() {

					@Override
					public void onSuccess(String response) {
					}

					@Override
					public void onError(String error) {
						Log.error(error);

					}
				});
	}

	public void logout(String token) {
		performRequest(
				"{\"request\": {"
                        +"\"api\":\"1.0.0\","
                        +"\"logout\": {\"token\":\""+token+"\", \"getuserinfo\":\"false\"}}}",
				true, new AjaxCallback() {

					@Override
					public void onSuccess(String response) {
					}

					@Override
					public void onError(String error) {
						Log.error(error);

					}
				});
	}

	protected abstract String getToken();
	public void favorite(int id, boolean favorite) {
		performRequest("{\"request\": {" + "\"-api\":\"1.0.0\","
				+ "\"login\": {\"-token\":\"" + getToken()
 + "\"},"
				+ "\"task\": {\"-type\":\"favorite\", \"id\":\"" + id
				+ "\",\"favorite\":\"" + favorite + "\"}}}", false,
				new AjaxCallback() {

					@Override
					public void onSuccess(String response) {
					}

					@Override
					public void onError(String error) {
						Log.error(error);

					}
				});

	}
}
