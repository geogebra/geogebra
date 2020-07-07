package org.geogebra.web.shared.ggtapi.models;

import org.geogebra.common.main.Localization;
import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GeoGebraTubeAPIWSimple;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.WindowW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Cookies;

/**
 * API Interface for GeoGebraTube requests and responses
 * 
 * @author Matthias Meisinger
 * 
 */
public class GeoGebraTubeAPIW extends GeoGebraTubeAPIWSimple {

	/**
	 * @param beta
	 *            whether to use beta
	 * @param client
	 *            {@link ClientInfo}
	 * @param articleElement
	 *            parameters
	 */
	public GeoGebraTubeAPIW(ClientInfo client, boolean beta,
			AppletParameters articleElement) {
		super(beta, articleElement);
		this.client = client;
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
				user.setUserId(Integer.parseInt(
						((JSONString) userinfo.get("user_id")).stringValue()));
			}
			user.setUserName(
					((JSONString) userinfo.get("username")).stringValue());
			user.setRealName(
					((JSONString) userinfo.get("realname")).stringValue());
			user.setIdentifier(
					((JSONString) userinfo.get("identifier")).stringValue());
			if (userinfo.get("ggt_avatar_url") instanceof JSONString) {
				user.setImageURL(((JSONString) userinfo.get("ggt_avatar_url"))
						.stringValue());
			}
			if (userinfo.get("ggt_profile_url") instanceof JSONString) {
				user.setProfileURL(
						((JSONString) userinfo.get("ggt_profile_url"))
						.stringValue());
			}
			if (userinfo.get("image") instanceof JSONString) {
				user.setImageURL(
						((JSONString) userinfo.get("image")).stringValue());
			}
			if (userinfo.get("lang_ui") instanceof JSONString) {
				user.setLanguage(
						((JSONString) userinfo.get("lang_ui")).stringValue());
			}

			if (userinfo.get("token") instanceof JSONString) {
				user.setToken(
						((JSONString) userinfo.get("token")).stringValue());
			}

			// Further fields are not parsed yet, because they are not needed

			// user.setGGTProfileURL(userinfo.getString("ggt_profile_url"));
			// user.setGroup(userinfo.getString("group"));
			// user.setDateCreated(userinfo.getString("date_created"));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * @param app
	 *            {@link AppW}
	 * @param sliderName
	 *            slider name
	 * @param timing
	 *            delay between frames
	 * @param isLoop
	 *            whether to loop
	 */
	public void exportAnimGif(final AppW app, String sliderName, int timing,
			boolean isLoop) {

		String data = createGifResponsePage(app.getLocalization());
		final JavaScriptObject gifWnd = app.getLAF().isTablet() ? null
				: WindowW.openFromData(data);
		WindowW.postMessage(gifWnd, StringUtil.toHTMLString(
				app.getLocalization().getMenu("AnimatedGIF.Processing")));

		RequestCallback cb = new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {

				if (response != null) {
					try {
						String respData = response.getText();
						if (respData == null || "".equals(respData)) {
							Log.debug("[ANIMGIF] no response");
							return;
						}
						Log.debug("[ANIMGIF] respData is: " + respData);
						JSONValue responseObject = JSONParser
								.parseStrict(respData);
						if (responseObject == null
								|| responseObject.isObject() == null) {
							Log.debug("[ANIMGIF] responseObject is null");
							WindowW.postMessage(gifWnd, StringUtil.toHTMLString(
									app.getLocalization().getPlain(
											"AnimatedGIF.ErrorA", respData)));
							return;
						}

						JSONValue responses = responseObject.isObject()
								.get("responses");

						if (responses == null || responses.isObject() == null) {
							JSONValue error = responseObject.isObject()
									.get("error").isObject().get("-type")
									.isString();
							Log.debug("[ANIMGIF] error is " + error.toString());

							WindowW.postMessage(gifWnd, StringUtil.toHTMLString(
									app.getLocalization().getPlain(
											"AnimatedGIF.ErrorA", " "
													+ error.toString() + " ("
													+ response
															.getStatusCode())));
							return;
						}

						JSONValue base64 = responses.isObject().get("response")
								.isObject().get("value");

						String downloadUrl = StringUtil.gifMarker
								+ base64.isString().stringValue();
						app.getFileManager().exportImage(downloadUrl,
								"ggbanim.gif", "gif");
						WindowW.postMessage(gifWnd,
								StringUtil.toHTMLString(app.getLocalization()
										.getMenu("AnimatedGIF.Success")));
					} catch (Throwable t) {
						Log.debug(t.getMessage());
						Log.debug("'" + response + "'");
					}
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {
				Log.debug("[ANIMGIF] EXCEPTION: " + exception);

			}
		};

		RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, getUrl());

		Log.debug("[URL] " + getUrl());
		String req = AnimGifRequest
				.getRequestElement(app.getGgbApi().getBase64(), sliderName,
						timing, isLoop)
				.toJSONString(client);
		Log.debug("[REQUEST]: " + req);
		try {
			rb.setHeader("Content-type", "text/plain");
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
		sb.append(
				"    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n");
		sb.append("    <title>"
				+ StringUtil.toHTMLString(loc.getMenu("AnimatedGIFExport"))
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
		sb.append(
				"    <iframe src=\"javascript:''\" id=\"__gwt_historyFrame\" tabIndex='-1'"
						+ " style=\"position:absolute;width:0;height:0;border:0\"></iframe>\n");
		sb.append("	<h1>"
				+ StringUtil.toHTMLString(loc.getMenu("CreatingAnimatedGIF"))
				+ "</h1>\n");
		sb.append(
				"	<p id=\"result\">"
						+ StringUtil.toHTMLString(
								loc.getMenu("AnimatedGIF.Calculating"))
						+ "</p>\n");
		sb.append(
				"	<button type=\"button\" id=\"close\" onclick=\"window.close();\""
						+ " style=\"display: none;\">Close</button>\n");
		sb.append("   </body>\n");
		sb.append("</html>\n");

		return sb.toString();
	}

	@Override
	public boolean performCookieLogin(LogInOperation op) {
		String cookie = Cookies.getCookie("SSID");
		if (cookie != null) {
			op.doPerformTokenLogin(new GeoGebraTubeUser(null, cookie), true);
			return true;
		}
		return false;
	}
}
