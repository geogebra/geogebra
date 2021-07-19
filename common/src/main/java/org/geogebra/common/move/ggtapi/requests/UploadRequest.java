package org.geogebra.common.move.ggtapi.requests;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.Request;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Upload request for GeoGebraTube
 *
 */
public class UploadRequest implements Request {

	private final static String API = "1.0.0";
	private final static String GGB = "geogebra";
	private final static String TASK = "upload";
	private final String type;
	private final String consTitle;
	private String uniqueID;
	private String base64;
	private String visibility;
	private Material parent;

	/**
	 * Used to upload the actual opened application to GeoGebraTube
	 * 
	 * @param tubeID
	 *            tube ID
	 * @param visibility
	 *            visibility
	 * 
	 * @param consTitle
	 *            title of construction
	 * @param base64
	 *            String
	 * @param type
	 *            material type
	 */
	UploadRequest(String tubeID, String visibility, String consTitle,
			String base64, MaterialType type, Material parent) {
		this.consTitle = consTitle;
		this.type = typeString(type);
		this.uniqueID = tubeID;
		this.base64 = base64;
		this.visibility = visibility;
		this.parent = parent;
		if (visibility == null || "".equals(visibility)) {
			this.visibility = "P";
		}
	}

	private static String typeString(MaterialType type) {
		return (type == MaterialType.ggb || type == MaterialType.ggs) ? "applet" : type.toString();
	}

	/**
	 * Used for local files. Files are saved as "private"
	 * 
	 * @param mat
	 *            Material
	 */
	public UploadRequest(Material mat) {
		this.consTitle = mat.getTitle();
		this.type = typeString(mat.getType());
		if (mat.getId() != 0) {
			this.uniqueID = mat.getId() + "";
		}
		this.base64 = mat.getBase64();
		this.visibility = "P";
	}

	/**
	 * UploadRequest to rename files
	 * 
	 * @param newTitle
	 *            String
	 * @param id
	 *            int
	 */
	UploadRequest(String newTitle, int id) {
		this.consTitle = newTitle;
		this.uniqueID = id + "";
		this.type = "applet"; // TODO can this be ignored
	}

	/**
	 * to upload active construction
	 * 
	 * @param tubeID
	 *            tube material ID
	 * @param visibility
	 *            P/O/S (private, open, shared)
	 *
	 * @param filename
	 *            title of construction
	 * @param base64
	 *            String
	 * @param type
	 *            material type
	 * @param parent
	 *            parent material
	 * @return the upload XML as JSON String
	 */
	public static UploadRequest getRequestElement(String tubeID, String visibility,
			String filename, String base64, MaterialType type, Material parent) {
		return new UploadRequest(tubeID, visibility, filename, base64, type,
				parent);
	}

	/**
	 * to upload local files
	 * 
	 * @param mat
	 *            {@link Material}
	 * @return the upload XML as JSON String
	 */
	public static UploadRequest getRequestElement(Material mat) {
		return new UploadRequest(mat);
	}

	/**
	 * to rename files with given id
	 *
	 * @param newTitle
	 *            String
	 * @param id
	 *            int
	 * @return the upload XML as JSON String
	 */
	public static UploadRequest getRequestElement(String newTitle, int id) {
		return new UploadRequest(newTitle, id);
	}

	@Override
	public String toJSONString(ClientInfo client) {
		if (client.getModel() == null
				|| client.getModel().getLoggedInUser() == null) {
			Log.warn("No user.");
			return null;
		}
		try {
			// TODO for save we only need title
			// request
			JSONObject api = new JSONObject();
			api.put("-api", UploadRequest.API);

			// login
			JSONObject login = new JSONObject();
			login.put("-type", UploadRequest.GGB);
			login.put("-token",
					client.getModel().getLoggedInUser().getLoginToken());
			api.put("login", login);

			// task
			JSONObject task = new JSONObject();
			task.put("-type", UploadRequest.TASK);

			if (!StringUtil.emptyOrZero(this.uniqueID)) {
				// ID
				task.put("id", this.uniqueID);
			}

			// type
			task.put("type", this.type);

			// title
			task.put("title", this.consTitle);

			// language
			task.put("language", client.getLanguage());

			// visibility
			if (this.visibility != null) {
				task.put("visibility", this.visibility);
			}

			// settings
			JSONObject settings = new JSONObject();

			// appname
			settings.put("-appname", client.getAppName());
			settings.put("-width", client.getWidth());
			settings.put("-height", client.getHeight());

			if (parent != null) {
				task.put("parent", parent.getId());
				settings.put("-toolbar", parent.getShowToolbar());
				settings.put("-menubar", parent.getShowMenu());
				settings.put("-inputbar", parent.getShowInputbar());
				settings.put("-reseticon", parent.getShowResetIcon());
				settings.put("-shiftdragzoom", parent.getShiftDragZoom());
				settings.put("-rightclick", parent.getRightClick());
				settings.put("-labeldrags", parent.getLabelDrags());
				settings.put("-undoredo", parent.getUndoRedo());
				settings.put("-stylebar", parent.getAllowStylebar());
				settings.put("-zoombuttons", parent.getShowZoomButtons());
			} else {
				boolean isNotes = GeoGebraConstants.NOTES_APPCODE.equals(client.getAppName());
				settings.put("-undoredo", true);
				settings.put("-reseticon", false);
				settings.put("-toolbar", isNotes);
				settings.put("-menubar", false);
				settings.put("-inputbar", false);
				settings.put("-stylebar", isNotes);
				settings.put("-rightclick", isNotes);
				settings.put("-zoombuttons", isNotes);
			}
			task.put("settings", settings);

			// file
			if (this.base64 != null) {
				JSONObject file = new JSONObject();
				file.put("-base64", this.base64);
				task.put("file", file);
				addPhoneTag(task, client);
			}

			api.put("task", task);
			JSONObject request = new JSONObject();
			request.put("request", api);

			return request.toString();
		} catch (Exception e) {
			e.printStackTrace();
			Log.debug("problem building request: " + e.getMessage());
			return null;
		}

	}

	private static void addPhoneTag(JSONObject task, ClientInfo client) {
		if (client == null) {
			Log.debug("Client info missing for upload ");
			return;
		}
		try {
			JSONObject tag = new JSONObject();
			tag.put("-name", client.getType());
			JSONArray tagArray = new JSONArray();

			tagArray.put(tag);
			JSONObject tags = new JSONObject();
			tags.put("tag", tagArray);
			task.put("tags", tags);
		} catch (Exception e) {
			Log.debug("adding phone tag: " + e.getMessage());
		}
	}
}
