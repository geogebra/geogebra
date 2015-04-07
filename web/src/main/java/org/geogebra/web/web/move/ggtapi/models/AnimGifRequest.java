package org.geogebra.web.web.move.ggtapi.models;

import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Request;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONString;
import org.geogebra.web.html5.main.AppW;

public class AnimGifRequest implements Request {

	private final String API = "1.0.0";
	private final String TYPE = "convertGGBToGIF";

	private AppW app;
	private String sliderName;
	private int timing;
	private boolean isLoop;

	/**
	 * @param app
	 *            AppW
	 * @param material
	 *            {@link Material}
	 */
	AnimGifRequest(AppW app, String sliderName, int timing, boolean isLoop) {
		this.app = app;
		this.sliderName = sliderName;
		this.timing = timing;
		this.isLoop = isLoop;
	}

	@Override
	public String toJSONString(ClientInfo client) {
		JSONObject request = new JSONObject();
		JSONObject api = new JSONObject();
		StringBuilder params = new StringBuilder();
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
		params.append("--slider=");
		params.append(sliderName);
		params.append(" --loop=");
		params.append(isLoop);
		params.append(" --delay=");
		params.append(timing);
		task.put("params", new JSONString(params.toString()));
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
	public static AnimGifRequest getRequestElement(AppW app, String sliderName,
	        int timing, boolean isLoop) {
		return new AnimGifRequest(app, sliderName, timing, isLoop);
	}
}
