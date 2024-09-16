package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * JSON parser for MAT reply objects
 *
 */
public class JSONParserGGT {
	/**
	 * Parser instance
	 */
	public static final JSONParserGGT prototype = new JSONParserGGT();

	/**
	 * @param obj
	 *            parsed material
	 * @return material object
	 */
	public Material toMaterial(JSONObject obj) {
		return toMaterial(obj, false);
	}

	/**
	 * @param obj
	 *            parsed data
	 * @param setLocalValues
	 *            whether to initialize sync timestamp and local ID
	 * @return material
	 */
	public Material toMaterial(JSONObject obj, boolean setLocalValues) {
		Material.MaterialType type = MaterialType.ggb;
		String typeString = obj.optString("type");
		if (typeString != null && typeString.length() > 0) {
			if ("ggs-template".equals(typeString)) {
				type = MaterialType.ggsTemplate;
			} else {
				try {
					type = MaterialType.valueOf(typeString);
				} catch (Throwable t) {
					Log.error("Unknown material type:" + typeString);
				}
			}
		}

		String IDs = getString(obj, "id");
		String sharingKey = getString(obj, "sharing_key");
		try {
			Integer.parseInt(IDs);
		} catch (RuntimeException e) {
			sharingKey = IDs;
		}

		Material material = new Material(type);

		material.setTitle(getString(obj, "title"));
		material.setDescription(getString(obj, "description"));
		if (!"".equals(getString(obj, "timestamp"))) {
			material.setTimestamp(Long.parseLong(getString(obj, "timestamp")));
		} else if (!"".equals(getString(obj, "date_modified"))) {
			material.setTimestamp(Long.parseLong(getString(obj, "date_modified")));
		}
		if (!"".equals(getString(obj, "modified"))) {
			material.setModified(Long.parseLong(getString(obj, "modified")));
		}
		if (!"".equals(getString(obj, "date_created"))) {
			material.setDateCreated(Long.parseLong(getString(obj, "date_created")));
		}
		if (!"".equals(getString(obj, "syncstamp"))) {
			material.setSyncStamp(Long.parseLong(getString(obj, "syncstamp")));
		}

		material.setVisibility(getString(obj, "visibility"));
		material.setMultiuser(getBoolean(obj, "multiuser", false)); // MOW
		material.setSharedWithGroup(getBoolean(obj, "shared_with_group", false)); // MOW
		String fileUrl = getString(obj, "fileUrl");
		if (fileUrl.isEmpty()) {
			fileUrl = getString(obj, "url");
		}
		material.setFileName(fileUrl);

		material.setSharingKey(sharingKey);
		material.setURL(getString(obj, "url"));
		material.setPreviewURL(obj.optString("previewUrl"));
		String thumbnail = obj.optString("thumbnail");
		if (thumbnail != null && thumbnail.startsWith("data:image/png;base64,")) {
			material.setThumbnailBase64(thumbnail);
		} else {
			String thumbUrl = getString(obj, "thumbUrl").replace("$1", "");
			if (thumbUrl.startsWith("http://") || thumbUrl.startsWith("https://")) {
				material.setThumbnailUrl(thumbUrl);
			} else {
				thumbnail = obj.optString("thumbnail");
				material.setThumbnailUrl(thumbnail);
			}
		}
		material.setLanguage(getString(obj, "language"));

		material.setBase64(getString(obj, "ggbBase64"));
		material.setDeleted(getStringBoolean(obj, "deleted", false));
		material.setFromAnotherDevice(
				getStringBoolean(obj, "from_another_device", false));
		JSONObject settings = obj.optJSONObject("settings");
		if (settings != null) {
			copySettings(settings, material);
		}
		JSONObject views = obj.optJSONObject("views");
		if (views != null) {
			copyViews(views, material);
		} else {
			material.setIs3d(getNumericBoolean(obj, "is3d", false));
			material.setCas(getNumericBoolean(obj, "cas", false));
			material.setSpreadsheet(getNumericBoolean(obj, "spreadsheet", false));
			material.setGraphics2(getNumericBoolean(obj, "graphics2", false));
			material.setConstprot(getNumericBoolean(obj, "constprot", false));
			material.setPropcalc(getNumericBoolean(obj, "propcalc", false));
			material.setDataanalysis(getNumericBoolean(obj, "dataanalysis", false));
			material.setFuncinsp(getNumericBoolean(obj, "funcinsp", false));
			material.setMacro(getNumericBoolean(obj, "macro", false));
		}
		material.setElemcntApplet(getInt(obj, "elemcnt_applet", -1));
		material.setViewerID(getInt(obj, "viewerID", -1));
		if (setLocalValues) {
			material.setLocalID(getInt(obj, "localID", -1));
		}
		if (obj.has("creator")) {
			setCreator(material, obj);
		} else {
			String displayName = getString(obj, "author");
			// creatorId used in MOW, crator_id in Marvl, author_id in Tube
			int userId = getInt(obj, "author_id", getInt(obj, "creator_id",
					getInt(obj, "creatorId", -1)));
			material.setCreator(new UserPublic(userId, displayName));
		}
		return material;
	}

	/**
	 * convert worksheet into material
	 * @param parent - parent material
	 * @param element - JSON object holding element attributes
	 * @return material
	 * @throws JSONException - if no such value exists.
	 */
	public static Material worksheetToMaterial(Material parent, JSONObject element)
			throws JSONException {
		Material mat = new Material(parent);
		mat.setType(MaterialType.ggb);
		mat.setThumbnailUrl(element.getString("thumbUrl"));
		mat.setFileName(element.getString("url"));
		mat.setURL(element.getString("url"));
		JSONObject settings = element.optJSONObject("settings");
		if (settings != null) {
			JSONParserGGT.copySettings(settings, mat);
		}
		JSONObject views = element.optJSONObject("views");
		if (views != null) {
			JSONParserGGT.copyViews(views, mat);
		}
		return mat;
	}

	/**
	 * Copy settings from JSON to material
	 * @param settings ggb worksheet element settings
	 * @param material material
	 */
	public static void copySettings(JSONObject settings, Material material) {
		if (!"".equals(getAppName(settings))) {
			material.setAppName(getAppName(settings));
		}
		material.setHeight(getInt(settings, "height", 600));
		material.setWidth(getInt(settings, "width", 800));
		material.setShowToolbar(getStringBoolean(settings, "showToolBar", false));
		material.setAllowStylebar(getStringBoolean(settings, "allowStyleBar", false));
		material.setShowMenu(getStringBoolean(settings, "showMenuBar", false));
		material.setShowInputbar(getStringBoolean(settings, "showAlgebraInput", false));
		material.setShiftDragZoom(getStringBoolean(settings, "enableShiftDragZoom", false));
		material.setRightClick(getStringBoolean(settings, "enableRightClick", false));
		material.setShowResetIcon(getStringBoolean(settings, "showResetIcon", false));
		material.setUndoRedo(getStringBoolean(settings, "enableUndoRedo", false));
		material.setShowZoomButtons(getStringBoolean(settings, "showZoomButtons", false));
	}

	/**
	 * Copy view settings from "views" object to material.
	 * @param views the "views" dictionary of a GeoGebra Applet ("type": "G") JSON object.
	 * @param material the material onto which to copy the view settings.
	 */
	public static void copyViews(JSONObject views, Material material) {
		material.setIs3d(getBoolean(views, "is3D", false));
		material.setSpreadsheet(getBoolean(views, "SV", false));
		material.setCas(getBoolean(views, "CV", false));
		material.setPropcalc(getBoolean(views, "PC", false));
		material.setDataanalysis(getBoolean(views, "DA", false));
		material.setFuncinsp(getBoolean(views, "FI", false));
		material.setMacro(getBoolean(views, "macro", false));
	}

	private static String getAppName(JSONObject obj) {
		return getString(obj, "appname");
	}

	private static void setCreator(Material material, JSONObject obj) {
		try {
			JSONObject creatorObj = obj.getJSONObject("creator");
			int id = getInt(creatorObj, "id", -1);
			String displayName = getString(creatorObj, "displayname");
			if (StringUtil.empty(displayName)) {
				displayName = getString(creatorObj, "name");
			}
			material.setCreator(new UserPublic(id, displayName));
		} catch (Throwable t) {
			Log.debug(t.getMessage());
		}
	}

	private static boolean getStringBoolean(JSONObject obj, String name,
			boolean def) {
		String value = obj.optString(name);
		if (value == null || "".equals(value)) {
			return def;
		}
		return Boolean.parseBoolean(value);
	}

	private static boolean getNumericBoolean(JSONObject obj, String name,
			boolean def) {
		String value = obj.optString(name);
		if (value == null || "".equals(value)) {
			return def;
		}
		return !"0".equals(value);
	}

	private static boolean getBoolean(JSONObject obj, String name, boolean def) {
		return obj.optBoolean(name, def);
	}

	private static String getString(JSONObject obj, String name) {
		String value = obj.optString(name);
		if (value == null || "".equals(value)) {
			return "";
		}
		return value;
	}

	private static int getInt(JSONObject obj, String name, int def) {
		String value = obj.optString(name);
		if (value == null || "".equals(value)) {
			return def;
		}
		return Integer.parseInt(value);
	}

	/**
	 * @param response
	 *            JSON list of materials
	 * @param result
	 *            output array
	 */
	public void parseResponse(String response,
			ArrayList<Material> result) {
		Object materialsArray = null;

		if (response != null) {
			try {
				JSONTokener tokener = new JSONTokener(response);
				JSONObject responseObject = new JSONObject(tokener);
				if (responseObject.has("responses")) {
					JSONObject materialsObject = (JSONObject) ((JSONObject) responseObject
							.get("responses")).get("response");

					if (materialsObject.has("item")) {
						materialsArray = materialsObject.get("item");
					} else {
						// List is empty
					}
				} else if (responseObject.has("error")) {
					// Show error
				}
			} catch (Throwable t) {
				Log.debug(t.getMessage());
				Log.debug("'" + response + "'");
			}

		} else {
			// Response String was null
		}
		// 0 materials
		if (materialsArray == null) {
			return;
		}
		// >1 materials
		if (materialsArray instanceof JSONArray) {
			for (int i = 0; i < ((JSONArray) materialsArray).length(); i++) {
				Object obj;
				try {
					obj = ((JSONArray) materialsArray).get(i);
					addToArray(result, obj);
				} catch (Exception e) {
					Log.debug("problem adding material " + i);
				}

			}
		}
		// 1 material
		else if (materialsArray instanceof JSONObject) {
			addToArray(result, materialsArray);
		}
	}

	private void addToArray(List<Material> result, Object obj) {
		if (!(obj instanceof JSONObject)) {
			return;
		}
		result.add(toMaterial((JSONObject) obj));
	}

	/**
	 * @param item
	 *            material JSON string
	 * @return parsed material
	 */
	public static Material parseMaterial(String item) {
		return parseMaterial(item, false);
	}

	/**
	 * @param item
	 *            material JSON string
	 * @param setLocalValues
	 *            whether to initialize sync timestamp and local ID
	 * @return parsed material
	 */
	public static Material parseMaterial(String item, boolean setLocalValues) {
		JSONObject mat = null;
		try {
			JSONTokener tok = new JSONTokener(item);
			mat = new JSONObject(tok);
		} catch (Throwable t) {
			Log.debug(t);
		}
		if (mat == null) {
			return null;
		}
		return prototype.toMaterial(mat, setLocalValues);
	}
}
