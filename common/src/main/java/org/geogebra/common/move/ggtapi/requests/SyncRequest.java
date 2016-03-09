package org.geogebra.common.move.ggtapi.requests;

import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.Request;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;

/**
 * Upload request for GeoGebraTube
 *
 */
public class SyncRequest implements Request {

	private final String API = "1.0.0";
	private final String GGB = "geogebra";
	private final String TASK = "sync";
	private long timestamp;

	/**
	 * Used to upload the actual opened application to GeoGebraTube
	 * 
	 * @param app
	 *            AppW
	 * @param timestamp
	 *            since when we want to see the events
	 */
	public SyncRequest(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toJSONString(ClientInfo client) {
		try {
		// TODO for save we only need title
		// request
		JSONObject request = new JSONObject();

		JSONObject api = new JSONObject();
			api.put("-api", this.API);

		// login
		JSONObject login = new JSONObject();
			login.put("-type", this.GGB);
			login.put("-token",
					client.getModel().getLoggedInUser().getLoginToken());
		api.put("login", login);

		// task
		JSONObject task = new JSONObject();
			task.put("-type", this.TASK);

		// type
			task.put("timestamp", this.timestamp + "");



		api.put("task", task);
		request.put("request", api);

		return request.toString();
		} catch (Exception e) {
			Log.debug("problem building request: " + e.getMessage());
			return null;
		}

	}
}
