package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
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
		if (getString(obj, "type").length() > 0) {
			try {
				if ("ggs-template".equals(getString(obj, "type"))) {
					type = MaterialType.ggsTemplate;
				} else {
					type = MaterialType.valueOf(getString(obj, "type"));
				}
			} catch (Throwable t) {
				Log.error("Unknown material type:" + getString(obj, "type"));
			}
		}
		String IDs = getString(obj, "id");
		int id = -1;
		String sharingKey = null;
		try {
			id = Integer.parseInt(IDs);
			sharingKey = getString(obj, "sharing_key");
		} catch (RuntimeException e) {
			sharingKey = IDs;
		}

		Material material = new Material(id, type);

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
		material.setMultiuser(getBoolean(obj, "multiuser", false));
		material.setSharedWithGroup(getBoolean(obj, "shared_with_group", false));
		material.setFileName(getString(obj, "fileUrl"));
		material.setSharingKey(sharingKey);
		material.setURL(getString(obj, "url"));
		String thumbUrl = getString(obj, "thumbUrl");
		material.setThumbnailUrl(
				StringUtil.empty(thumbUrl) ? getString(obj, "thumbnail")
						: thumbUrl.replace("$1", ""));
		material.setLanguage(getString(obj, "language"));

		material.setBase64(getString(obj, "ggbBase64"));
		material.setDeleted(getBoolean(obj, "deleted", false));
		material.setFromAnotherDevice(
				getBoolean(obj, "from_another_device", false));
		material.setIs3d(getStringBoolean(obj, "is3d", false));
		material.setSpreadsheet(getStringBoolean(obj, "spreadsheet", false));
		material.setCas(getStringBoolean(obj, "cas", false));
		material.setGraphics2(getStringBoolean(obj, "graphics2", false));
		material.setConstprot(getStringBoolean(obj, "constprot", false));
		material.setPropcalc(getStringBoolean(obj, "propcalc", false));
		material.setDataanalysis(getStringBoolean(obj, "dataanalysis", false));
		material.setFuncinsp(getStringBoolean(obj, "funcinsp", false));
		material.setMacro(getStringBoolean(obj, "macro", false));
		material.setElemcntApplet(getInt(obj, "elemcnt_applet", -1));
		material.setViewerID(getInt(obj, "viewerID", -1));
		if (setLocalValues) {
			material.setLocalID(getInt(obj, "localID", -1));
			material.setAutosaveTimestamp(getInt(obj, "autoSaveTimestamp", 0));
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
		material.setShowToolbar(getBoolean(settings, "showToolBar", false));
		material.setAllowStylebar(getBoolean(settings, "allowStyleBar", false));
		material.setShowMenu(getBoolean(settings, "showMenuBar", false));
		material.setShowInputbar(getBoolean(settings, "showAlgebraInput", false));
		material.setShiftDragZoom(getBoolean(settings, "enableShiftDragZoom", false));
		material.setRightClick(getBoolean(settings, "enableRightClick", false));
		material.setShowResetIcon(getBoolean(settings, "showResetIcon", false));
		material.setUndoRedo(getBoolean(settings, "enableUndoRedo", false));
		material.setShowZoomButtons(getBoolean(settings, "showZoomButtons", false));
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

	private static boolean getBoolean(JSONObject obj, String string,
			boolean def) {
		if (!obj.has(string)) {
			return def;
		}
		Object str = null;
		try {
			str = obj.get(string);
		} catch (Exception e) {
			// ignore
		}
		if (str == null || "".equals(str)) {
			return def;
		}
		return Boolean.parseBoolean(str.toString());
	}

	private static String getString(JSONObject obj, String string) {
		Object str = obj.opt(string);
		if (str == null || "false".equals(str.toString())) {
			return "";
		}
		return str.toString();
	}

	private static int getInt(JSONObject obj, String string, int def) {
		Object str = obj.opt(string);
		if (str == null || "".equals(str)) {
			return def;
		}
		return Integer.parseInt(str.toString());
	}

	private static boolean getStringBoolean(JSONObject obj, String name,
			boolean def) {
		if (!obj.has(name)) {
			return def;
		}
		String value = null;
		try {
			value = obj.getString(name);
			if ("".equals(value)) {
				return def;
			}
		} catch (Exception e) {
			// ignore
		}
		return "0".equals(value) ? false : true;
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
