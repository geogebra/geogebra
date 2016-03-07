package org.geogebra.web.html5.util.ggtapi;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.JSONParserGGT;
import org.geogebra.common.move.ggtapi.models.Material;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * Utility Class for Parsing POJO's to JSON and vice versa
 * 
 * @author Matthias Meisinger
 * 
 */
public class JSONParserGGTW extends JSONParserGGT {
	@Override
	public ArrayList<Chapter> parseResponse(String response,
			ArrayList<Material> result) {

		JSONValue materialsArray = null;
		ArrayList<Chapter> meta = null;

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
				if (materialsObject.containsKey(("meta"))) {
					JSONValue content = materialsObject.get("meta").isObject()
					        .get("-content");
					meta = parseMeta(content.isString().stringValue());

				}

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
			return meta;
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
		return meta;
	}

	private static ArrayList<Chapter> parseMeta(String s) {
		ArrayList<Chapter> ret = new ArrayList<Chapter>();
		try {
			JSONArray parsed = JSONParser.parseStrict(s).isArray();
			for (int i = 0; i < parsed.size(); i++) {
				String title = parsed.get(i).isObject().get("title").isString()
				        .stringValue();
				JSONArray materials = parsed.get(i).isObject().get("materials")
				        .isArray();
				int[] mats = new int[materials.size()];
				for (int m = 0; m < materials.size(); m++) {
					mats[m] = (int) materials.get(m).isNumber().doubleValue();
				}
				ret.add(new Chapter(title, mats));
			}
		} catch (Throwable t) {

		}
		return ret;
	}

	private void addToArray(List<Material> result, JSONObject obj) {
		if (obj == null) {
			return;
		}
		result.add(toMaterial(new JSONWrapperW(obj)));
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
		return prototype.toMaterial(new JSONWrapperW(mat.isObject()));
	}
}
