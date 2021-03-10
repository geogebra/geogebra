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

		if (!"".equals(getAppName(obj))) {
			material.setAppName(getAppName(obj));
		}

		material.setVisibility(getString(obj, "visibility"));
		material.setFileName(getString(obj, "fileUrl"));
		material.setSharingKey(sharingKey);
		material.setAuthor(getString(obj, "author"));
		material.setAuthorId(
				getInt(obj, "author_id", getInt(obj, "creator_id", -1)));
		material.setURL(getString(obj, "url"));
		material.setURLdirect(getString(obj, "url_direct"));
		String thumbUrl = getString(obj, "thumbUrl");
		material.setThumbnailUrl(
				StringUtil.empty(thumbUrl) ? getString(obj, "thumbnail")
						: thumbUrl.replace("$1", ""));
		material.setPreviewURL(getString(obj, "previewUrl"));
		material.setLanguage(getString(obj, "language"));
		material.setFeatured(Boolean.parseBoolean(getString(obj, "featured")));
		material.setLikes(getInt(obj, "likes", -1));
		material.setHeight(getInt(obj, "height", 600));
		material.setWidth(getInt(obj, "width", 800));
		material.setInstructionsPost(getString(obj, "instructions_post"));
		material.setInstructionsPre(getString(obj, "instructions_pre"));
		material.setShowToolbar(getBoolean(obj, "toolbar", false));
		material.setAllowStylebar(getBoolean(obj, "stylebar", false));
		material.setShowMenu(getBoolean(obj, "menubar", false));
		material.setShowInputbar(getBoolean(obj, "inputbar", false));
		material.setFavorite(getBoolean(obj, "favorite", false));
		material.setShiftDragZoom(getBoolean(obj, "shiftdragzoom", false));
		material.setRightClick(getBoolean(obj, "rightclick", false));
		material.setShowResetIcon(getBoolean(obj, "reseticon", false));
		material.setUndoRedo(getBoolean(obj, "undoredo", false));
		material.setShowZoomButtons(getBoolean(obj, "zoombuttons", false));
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
		}
		return material;
	}

	private static String getAppName(JSONObject obj) {
		return getString(obj, "appname");
	}

	private static void setCreator(Material material, JSONObject obj) {
		try {
			JSONObject creatorObj = obj.getJSONObject("creator");

			String username = getString(creatorObj, "username");
			int id = getInt(creatorObj, "id", -1);
			String displayname = getString(creatorObj, "displayname");
			material.setCreator(new UserPublic(username, id, displayname));
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
		if (!obj.has(string)) {
			return "";
		}
		Object str = null;
		try {
			str = obj.get(string);
		} catch (Exception e) {
			// ignore
		}
		if (str == null) {
			return "";
		}
		return str.toString();
	}

	private static int getInt(JSONObject obj, String string, int def) {
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
		return Integer.parseInt(str.toString());
	}

	private static long getLong(JSONObject obj, String string, long def) {
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
		return Long.parseLong(str.toString());
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

	private static void addEvent(JSONObject object,
			ArrayList<SyncEvent> events) {

		SyncEvent se = new SyncEvent(getInt(object, "id", 0),
				getLong(object, "ts", 0));
		try {
			if (object.has("deleted")
					&& object.get("deleted") instanceof String) {
				se.setDelete(true);
			}
		} catch (Exception e) {
			Log.debug("error parsing deletion");
		}
		try {
			if (object.has("favorite")
					&& getBoolean(object, "favorite", false)) {
				se.setFavorite(true);
			}
		} catch (Exception e) {
			Log.debug("error parsing favorite");
		}
		try {
			if (object.has("unfavorited")
					&& object.get("unfavorited") instanceof String) {
				se.setUnfavorite(true);
			}
		} catch (Exception e) {
			Log.debug("error parsing unfavorite");
		}
		events.add(se);
	}

	/**
	 * @param events
	 *            output array of events
	 * @param items
	 *            parsed sync items
	 * @throws JSONException
	 *             for malformed JSON
	 */
	public void addEvents(ArrayList<SyncEvent> events, Object items)
			throws JSONException {
		if (items instanceof JSONArray) {
			for (int i = 0; i < ((JSONArray) items).length(); i++) {
				addEvent((JSONObject) ((JSONArray) items).get(i), events);
			}
		} else if (items instanceof JSONObject) {
			addEvent((JSONObject) items, events);
		}
	}

	/**
	 * @param response
	 *            JSON list of materials
	 * @param result
	 *            output array
	 * @return book metadata
	 */
	public ArrayList<Chapter> parseResponse(String response,
			ArrayList<Material> result) {
		Object materialsArray = null;
		ArrayList<Chapter> meta = null;

		if (response != null) {
			JSONObject responseObject = new JSONObject();
			try {
				JSONTokener tokener = new JSONTokener(response);
				responseObject = new JSONObject(tokener);
				if (responseObject.has("responses")) {
					JSONObject materialsObject = (JSONObject) ((JSONObject) responseObject
							.get("responses")).get("response");
					if (materialsObject.has(("meta"))) {
						String content = ((JSONObject) materialsObject
								.get("meta")).get("-content").toString();
						meta = parseMeta(content);

					}

					if (materialsObject.has(("item"))) {
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
			return meta;
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
		return meta;
	}

	private static ArrayList<Chapter> parseMeta(String s) {
		ArrayList<Chapter> ret = new ArrayList<>();
		try {
			JSONTokener tokener = new JSONTokener(s);
			JSONArray parsed = new JSONArray(tokener);

			for (int i = 0; i < parsed.length(); i++) {
				String title = ((JSONObject) parsed.get(i)).get("title")
						.toString();
				JSONArray materials = (JSONArray) ((JSONObject) parsed.get(i))
						.get("materials");
				int[] mats = new int[materials.length()];
				for (int m = 0; m < materials.length(); m++) {
					mats[m] = (int) ((Double) materials.get(m)).doubleValue();
				}
				ret.add(new Chapter(title, mats));
			}
		} catch (Throwable t) {
			// ignore
		}
		return ret;
	}

	private void addToArray(List<Material> result, Object obj) {
		if (!(obj instanceof JSONObject)) {
			return;
		}
		result.add(toMaterial(((JSONObject) obj)));
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
			t.printStackTrace();
		}
		if (mat == null) {
			return null;
		}
		return prototype.toMaterial(mat, setLocalValues);
	}
}
