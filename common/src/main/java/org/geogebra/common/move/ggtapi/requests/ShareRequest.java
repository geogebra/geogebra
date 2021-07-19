package org.geogebra.common.move.ggtapi.requests;

import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Request;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;

/**
 * Request for email share.
 *
 */
public class ShareRequest implements Request {

	private final static String API = "1.0.0";
	private final static String TASK = "share";
	private final static String TYPE = "forum";

	private Material material;
	private String recipient;
	private String message;

	/**
	 * @param material
	 *            {@link Material}
	 */
	ShareRequest(Material material, String recipient, String message) {
		this.material = material;
		this.recipient = recipient;
		this.message = message;
	}

	@Override
	public String toJSONString(ClientInfo client) {
		try {
			JSONObject api = new JSONObject();
			api.put("-api", ShareRequest.API);

			// login
			JSONObject login = new JSONObject();
			login.put("-type", ShareRequest.TYPE);
			login.put("-token",
					client.getModel().getLoggedInUser().getLoginToken());
			api.put("login", login);

			// task
			JSONObject task = new JSONObject();
			task.put("-type", ShareRequest.TASK);

			// ID
			task.put("id", Integer.toString(this.material.getId()));
			task.put("recipient", this.recipient);
			task.put("message", this.message);

			api.put("task", task);
			JSONObject request = new JSONObject();
			request.put("request", api);
			return request.toString();
		} catch (Exception e) {
			Log.debug("problem building request: " + e.getMessage());
			return null;
		}
	}

	/**
	 * @param mat
	 *            Material
	 * @param recipient
	 *            email recipient
	 * @param message
	 *            message body
	 * @return ShareRequest
	 */
	public static ShareRequest getRequestElement(Material mat, String recipient,
			String message) {
		return new ShareRequest(mat, recipient, message);
	}
}
