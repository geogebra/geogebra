package org.geogebra.web.web.move.ggtapi.models;

import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Request;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;

public class AnimGifRequest implements Request {

	private final static String API = "1.0.0";
	private final static String TYPE = "convertGGBToGIF";

	private String sliderName;
	private int timing;
	private boolean isLoop;
	private String base64;

	/**
	 * @param app
	 *            AppW
	 * @param material
	 *            {@link Material}
	 */
	AnimGifRequest(String base64, String sliderName, int timing, boolean isLoop) {
		this.base64 = base64;
		this.sliderName = sliderName;
		this.timing = timing;
		this.isLoop = isLoop;
	}

	@Override
	public String toJSONString(ClientInfo client) {
		JSONObject request = new JSONObject();
		JSONObject api = new JSONObject();
		StringBuilder params = new StringBuilder();
		try {
			api.put("-api", this.API);

			// login
			JSONObject login = new JSONObject();
			// login.put("-type", new JSONString(this.TYPE));
			// login.put("-token", new JSONString(client.getModel()
			// .getLoggedInUser().getLoginToken()));
			// api.put("login", login);

			// task
			JSONObject task = new JSONObject();
			JSONObject ggbBase64 = new JSONObject();
			ggbBase64.put("-base64", base64);

			task.put("-type", TYPE);
			task.put("file", ggbBase64);
			params.append("--slider=");
			params.append(sliderName);
			params.append(" --loop=");
			params.append(isLoop);
			params.append(" --delay=");
			params.append(timing);
			task.put("params", params.toString());
			api.put("task", task);
			request.put("request", api);
			return request.toString();
		} catch (Exception e) {
			Log.debug("problem building request: " + e.getMessage());
			return null;
		}

	}

	/**
	 * @param app
	 *            AppW
	 * @param sliderName
	 *            The slider of animation steps
	 * @return AnimGifRequest
	 */
	public static AnimGifRequest getRequestElement(String base64,
			String sliderName,
	        int timing, boolean isLoop) {
		return new AnimGifRequest(base64, sliderName, timing, isLoop);
	}
}
