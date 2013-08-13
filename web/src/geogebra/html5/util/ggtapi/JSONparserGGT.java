package geogebra.html5.util.ggtapi;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * Utility Class for Parsing POJO's to JSON and vice versa
 * 
 * @author Matthias Meisinger
 * 
 */
public class JSONparserGGT {
	public static List<Material> parseResponse(String response) {
		List<Material> result = new ArrayList<Material>();

		JSONValue materialsArray = null;

		if (response != null) {
			JSONObject responseObject = new JSONObject();
			try {
				responseObject = JSONParser.parseStrict(response).isObject();
			} catch (Throwable t) {
				App.debug(t.getMessage());
				App.debug("'" + response + "'");
			}
			if (responseObject.containsKey("responses")) {
				JSONObject materialsObject = responseObject.get("responses")
				        .isObject().get("response").isObject();

				if (materialsObject.containsKey(("item"))) {
					materialsArray = materialsObject.get("item");
				} else {
					// List is empty
				}
			} else if (responseObject.containsKey("error")) {
				// Show error
			}

		} else {
			// Response String was null
		}
		// 0 materials
		if (materialsArray == null) {
			return result;
		}
		// >1 materials
		if (materialsArray.isArray() != null) {
			for (int i = 0; i < materialsArray.isArray().size(); i++) {
				JSONObject obj = materialsArray.isArray().get(i).isObject();
				addToArray(result, obj);

			}
		}
		// 1 material
		else if (materialsArray.isObject() != null) {
			addToArray(result, materialsArray.isObject());
		}
		return result;
	}

	private static void addToArray(List<Material> result, JSONObject obj) {
		if (obj == null) {
			return;
		}
		result.add(toMaterial(obj));
	}

	private static Material toMaterial(JSONObject obj) {
		Material.MaterialType type = MaterialType.ggb;
		if(getString(obj, "type").length() > 0){
			type = MaterialType.valueOf(getString(obj, "type"));
		}
		int ID = Integer.parseInt(obj.get("id").isString().stringValue());

		Material material = new Material(ID, type);

		material.setTitle(getString(obj, "title"));
		material.setDescription(getString(obj, "description"));
		material.setTimestamp(Long.parseLong(getString(obj, "timestamp")));
		material.setAuthor(getString(obj, "author"));
		material.setAuthorURL(getString(obj, "author_url"));
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
		material.setShiftDragZoom(getBoolean(obj, "shiftdragzoom", false));
		material.setShowResetIcon(getBoolean(obj, "reseticon", false));
		return material;
	}

	private static String getString(JSONObject obj, String string) {
		if (obj.get(string) == null) {
			return "";
		}
		return obj.get(string).isString().stringValue();
	}

	private static int getInt(JSONObject obj, String string, int def) {
		if (obj.get(string) == null
		        || "".equals(obj.get(string).isString().stringValue())) {
			return def;
		}
		return Integer.parseInt(obj.get(string).isString().stringValue());
	}
	
	private static boolean getBoolean(JSONObject obj, String string, boolean def) {
		if (obj.get(string) == null
		        || "".equals(obj.get(string).isString().stringValue())) {
			return def;
		}
		return Boolean.parseBoolean(obj.get(string).isString().stringValue());
	}

	public static Material parseMaterial(String item) {
		JSONValue mat = null;
		try {
			mat = JSONParser.parseStrict(item);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		if (mat == null || mat.isObject() == null) {
			return null;
		}
		return toMaterial(mat.isObject());
	}
}
