package org.geogebra.desktop.move.ggtapi.models;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.JSONParserGGT;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.debug.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONParserGGTD extends JSONParserGGT {

	@Override
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

	private void addToArray(List<Material> result, Object obj) {
		if (obj == null) {
			return;
		}
		result.add(toMaterial(new JSONWrapperD((JSONObject) obj)));
	}




}
