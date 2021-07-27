package org.geogebra.common.move.ggtapi.requests;

import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Request;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;

/**
 * Request for deletion.
 */
public class DeleteRequest implements Request {

	private final static String API = "1.0.0";
	private final static String TASK = "delete";
	private final static String TYPE = "forum";

	private Material material;

	/**
	 * @param material
	 *            {@link Material}
	 */
	DeleteRequest(Material material) {
		this.material = material;
	}

	@Override
	public String toJSONString(ClientInfo client) {
		try {
			JSONObject api = new JSONObject();
			api.put("-api", DeleteRequest.API);

			// login
			JSONObject login = new JSONObject();
			login.put("-type", DeleteRequest.TYPE);
			login.put("-token",
					client.getModel().getLoggedInUser().getLoginToken());
			api.put("login", login);

			// task
			JSONObject task = new JSONObject();
			task.put("-type", DeleteRequest.TASK);

			// ID
			task.put("id", Integer.toString(this.material.getId()));

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
	 * @return DeleteRequest
	 */
	public static DeleteRequest getRequestElement(Material mat) {
		return new DeleteRequest(mat);
	}
}
