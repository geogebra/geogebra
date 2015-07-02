package org.geogebra.web.web.move.ggtapi.models;

import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.Request;
import org.geogebra.common.move.ggtapi.models.json.JSONBoolean;
import org.geogebra.common.move.ggtapi.models.json.JSONNumber;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONString;
import org.geogebra.web.html5.main.AppW;

/**
 * Upload request for GeoGebraTube
 *
 */
public class UploadRequest implements Request {

	private final String API = "1.0.0";
	private final String GGB = "geogebra";
	private final String TASK = "upload";
	private final String type;
	private final String consTitle;
	private int uniqueID;
	private String base64;
	private String visibility;

	/**
	 * Used to upload the actual opened application to GeoGebraTube
	 * 
	 * @param app
	 *            AppW
	 * @param consTitle
	 *            title of construction
	 * @param base64
	 *            String
	 */
	UploadRequest(int tubeID, String visibility, String consTitle,
			String base64, MaterialType type) {
		this.consTitle = consTitle;
		this.type = type == MaterialType.ggb ? "applet" : type.name();
		this.uniqueID = tubeID;
		this.base64 = base64;
		this.visibility = visibility;
		if (visibility == null || "".equals(visibility)) {
			this.visibility = "P";
		}
	}

	/**
	 * Used for local files. Files are saved as "private"
	 * 
	 * @param app
	 *            AppW
	 * @param mat
	 *            Material
	 */
	UploadRequest(AppW app, Material mat) {
		this.consTitle = mat.getTitle();
		this.type = mat.getType() == MaterialType.ggb ? "applet" : mat
				.getType().name();
		if (mat.getId() != 0) {
			this.uniqueID = mat.getId();
		}
		this.base64 = mat.getBase64();
		this.visibility = "P";
	}

	/**
	 * UploadRequest to rename files
	 * 
	 * @param app
	 *            AppW
	 * @param newTitle
	 *            String
	 * @param id
	 *            int
	 */
	UploadRequest(AppW app, String newTitle, int id) {
		this.consTitle = newTitle;
		this.uniqueID = id;
		this.type = "applet"; // TODO can this be ignored
	}

	@Override
	public String toJSONString(ClientInfo client) {
		// TODO for save we only need title
		// request
		JSONObject request = new JSONObject();

		JSONObject api = new JSONObject();
		api.put("-api", new JSONString(this.API));

		// login
		JSONObject login = new JSONObject();
		login.put("-type", new JSONString(this.GGB));
		login.put("-token", new JSONString(client.getModel()
		        .getLoggedInUser().getLoginToken()));
		api.put("login", login);

		// task
		JSONObject task = new JSONObject();
		task.put("-type", new JSONString(this.TASK));

		if (this.uniqueID != 0) {
			// ID
			task.put("id", new JSONNumber(this.uniqueID));
		}

		// type
		task.put("type", new JSONString(this.type));

		// title
		task.put("title", new JSONString(this.consTitle));

		// language
		task.put("language", new JSONString(client.getLanguage()));

		// visibility
		if (this.visibility != null) {
			task.put("visibility", new JSONString(this.visibility));
		}

		// settings
		JSONObject settings = new JSONObject();
		settings.put("-toolbar", JSONBoolean.getInstance(false));
		settings.put("-menubar", JSONBoolean.getInstance(false));
		settings.put("-inputbar", JSONBoolean.getInstance(false));
		task.put("settings", settings);

		// file
		if (this.base64 != null) {
			JSONObject file = new JSONObject();
			file.put("-base64", new JSONString(this.base64));
			task.put("file", file);
		}

		api.put("task", task);
		request.put("request", api);

		return request.toString();
	}

	/**
	 * to upload active construction
	 * 
	 * @param app
	 *            AppW
	 * @param filename
	 *            title of construction
	 * @param base64
	 *            String
	 * @return the upload XML as JSON String
	 */
	public static UploadRequest getRequestElement(int tubeID,
	        String visibility, String filename,
 String base64, MaterialType type) {
		return new UploadRequest(tubeID, visibility, filename, base64, type);
	}

	/**
	 * to upload local files
	 * 
	 * @param app
	 *            {@link AppW}
	 * @param mat
	 *            {@link Material}
	 * @return the upload XML as JSON String
	 */
	public static UploadRequest getRequestElement(AppW app, Material mat) {
		return new UploadRequest(app, mat);
	}

	/**
	 * to rename files with given id
	 * 
	 * @param app
	 *            AppW
	 * @param newTitle
	 *            String
	 * @param id
	 *            int
	 * @return the upload XML as JSON String
	 */
	public static UploadRequest getRequestElement(AppW app, String newTitle,
	        int id) {
		return new UploadRequest(app, newTitle, id);
	}
}
