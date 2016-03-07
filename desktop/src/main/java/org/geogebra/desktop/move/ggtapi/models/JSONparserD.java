package org.geogebra.desktop.move.ggtapi.models;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.util.debug.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONparserD {

	public static ArrayList<Chapter> parseResponse(String response,
			ArrayList<Material> result) {
		Object materialsArray = null;
		ArrayList<Chapter> meta = null;

		if (response != null) {
			JSONObject responseObject = new JSONObject();
			try {
				JSONTokener tokener = new JSONTokener(response);
				responseObject = new JSONObject(tokener);
			} catch (Throwable t) {
				App.debug(t.getMessage());
				App.debug("'" + response + "'");
			}
			if (responseObject.has("responses")) {
				JSONObject materialsObject = (JSONObject) ((JSONObject) responseObject
						.get("responses")).get("response");
				if (materialsObject.has(("meta"))) {
					String content = ((JSONObject) materialsObject.get("meta"))
							.get("-content").toString();
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
				Object obj = ((JSONArray) materialsArray)
						.get(i);
				addToArray(result, obj);

			}
		}
		// 1 material
		else if (materialsArray instanceof JSONObject) {
			addToArray(result, (JSONObject) materialsArray);
		}
		return meta;
	}

	private static ArrayList<Chapter> parseMeta(String s) {
		ArrayList<Chapter> ret = new ArrayList<Chapter>();
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
					mats[m] = (int) ((Double)materials.get(m)).doubleValue();
				}
				ret.add(new Chapter(title, mats));
			}
		} catch (Throwable t) {

		}
		return ret;
	}

	private static void addToArray(List<Material> result, Object obj) {
		if (obj == null) {
			return;
		}
		result.add(toMaterial((JSONObject) obj));
	}

	public static Material toMaterial(JSONObject obj) {
		Material.MaterialType type = MaterialType.ggb;
		if (getString(obj, "type").length() > 0) {
			try {
				type = MaterialType.valueOf(getString(obj, "type"));
			} catch (Throwable t) {
				Log.error("Unknown material type:" + getString(obj, "type"));
			}
		}
		int ID = Integer.parseInt(obj.get("id").toString());

		Material material = new Material(ID, type);

		material.setTitle(getString(obj, "title"));
		material.setDescription(getString(obj, "description"));
		if (getString(obj, "timestamp") != "") {
			material.setTimestamp(Long.parseLong(getString(obj, "timestamp")));
		}
		if (getString(obj, "modified") != "") {
			material.setModified(Long.parseLong(getString(obj, "modified")));
		}
		if (getString(obj, "syncstamp") != "") {
			material.setSyncStamp(Long.parseLong(getString(obj, "syncstamp")));
		}
		material.setVisibility(getString(obj, "visibility"));
		material.setAuthor(getString(obj, "author"));
		material.setAuthorId(getInt(obj, "author_id", -1));
		material.setURL(getString(obj, "url"));
		material.setURLdirect(getString(obj, "url_direct"));
		material.setThumbnail(getString(obj, "thumbnail"));
		material.setLanguage(getString(obj, "language"));
		material.setFeatured(Boolean.parseBoolean(getString(obj, "featured")));
		material.setLikes(getInt(obj, "likes", -1));
		material.setHeight(getInt(obj, "height", 600));
		material.setWidth(getInt(obj, "width", 800));
		material.setInstructionsPost(getString(obj, "instructions_post"));
		material.setInstructionsPre(getString(obj, "instructions_pre"));
		material.setShowToolbar(getBoolean(obj, "toolbar", false));
		material.setShowMenu(getBoolean(obj, "menubar", false));
		material.setShowInputbar(getBoolean(obj, "inputbar", false));
		material.setFavorite(getBoolean(obj, "favorite", false));
		material.setShiftDragZoom(getBoolean(obj, "shiftdragzoom", false));
		material.setShowResetIcon(getBoolean(obj, "reseticon", false));
		material.setBase64(getString(obj, "ggbBase64"));
		material.setDeleted(getBoolean(obj, "deleted", false));
		material.setFromAnotherDevice(
				getBoolean(obj, "from_another_device", false));
		return material;
	}

	private static boolean getBoolean(JSONObject obj, String string,
			boolean def) {
		if (obj.has(string) && obj.get(string) instanceof Boolean) {
			return ((Boolean) obj.get(string)).booleanValue();
		}
		if (!obj.has(string) || obj.get(string) == null
				|| obj.get(string).toString() == null
				|| "".equals(obj.get(string).toString())) {
			return def;
		}
		return Boolean.parseBoolean(obj.get(string).toString());
	}

	private static String getString(JSONObject obj, String string) {
		if (!obj.has(string)) {
			return "";
		}
		if (obj.get(string) == null) {
			return "";
		}
		return obj.get(string).toString();
	}

	private static int getInt(JSONObject obj, String string, int def) {
		if (!obj.has(string) || obj.get(string) == null
				|| "".equals(obj.get(string).toString())) {
			return def;
		}
		return Integer.parseInt(obj.get(string).toString());
	}
}
