package org.geogebra.common.move.ggtapi.requests;

import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Request;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;

public class DeleteRequest implements Request {

	private final String API = "1.0.0";
	private final String TASK = "delete";
	private final String TYPE = "forum";

	private Material material;

	/**
	 * @param app
	 *            AppW
	 * @param material
	 *            {@link Material}
	 */
	DeleteRequest(Material material) {
		this.material = material;
	}

	@Override
	public String toJSONString(ClientInfo client) {
		try {
		JSONObject request = new JSONObject();
		JSONObject api = new JSONObject();
			api.put("-api", this.API);

		// login
		JSONObject login = new JSONObject();
		login.put("-type", this.TYPE);
		login.put("-token", client.getModel()
					.getLoggedInUser().getLoginToken());
		api.put("login", login);

		// task
		JSONObject task = new JSONObject();
		task.put("-type", this.TASK);

		// ID
		task.put("id", Integer.toString(this.material.getId()));

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
	 * @param mat
	 *            Material
	 * @return DeleteRequest
	 */
	public static DeleteRequest getRequestElement(Material mat) {
		return new DeleteRequest(mat);
	}
}
