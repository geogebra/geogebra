package geogebra.web.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.ClientInfo;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Request;
import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.models.json.JSONString;
import geogebra.html5.main.AppW;

public class AnimGifRequest implements Request {

	private final String API = "1.0.0";
	private final String TYPE = "convertGGBToGIF";

	private AppW app;
	private String sliderName;

	/**
	 * @param app
	 *            AppW
	 * @param material
	 *            {@link Material}
	 */
	AnimGifRequest(AppW app, String sliderName) {
		this.app = app;
		this.sliderName = sliderName;
	}

	@Override
	public String toJSONString(ClientInfo client) {
		JSONObject request = new JSONObject();
		JSONObject api = new JSONObject();
		api.put("-api", new JSONString(this.API));

		// login
		JSONObject login = new JSONObject();
		// login.put("-type", new JSONString(this.TYPE));
		// login.put("-token", new JSONString(client.getModel()
		// .getLoggedInUser().getLoginToken()));
		// api.put("login", login);

		// task
		JSONObject task = new JSONObject();
		JSONObject ggbBase64 = new JSONObject();
		ggbBase64.put("-base64", app.getGgbApi().getBase64());

		task.put("-type", new JSONString(TYPE));
		task.put("file", ggbBase64);
		task.put("slidername", new JSONString(sliderName));

		api.put("task", task);
		request.put("request", api);
		return request.toString();
	}

	/**
	 * @param app
	 *            AppW
	 * @param sliderName
	 *            The slider of animation steps
	 * @return AnimGifRequest
	 */
	public static AnimGifRequest getRequestElement(AppW app, String sliderName) {
		return new AnimGifRequest(app, sliderName);
	}
}
