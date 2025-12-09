/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.io;

import java.util.ArrayList;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.util.debug.Log;

/**
 * Serializable description of editor state
 */
public class EditorStateDescription {

	private String text;
	private ArrayList<Integer> caretPath;

	/**
	 * @param text
	 *            editor content
	 * @param caretPath
	 *            caret path as indices in tree
	 */
	public EditorStateDescription(String text, ArrayList<Integer> caretPath) {
		this.text = text;
		this.caretPath = caretPath;
	}

	/**
	 * @return serialized editor state
	 */
	public String asJSON() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("content", text);
			JSONArray caretPathJson = new JSONArray();
			for (Integer pathElement : caretPath) {
				caretPathJson.put(pathElement);
			}
			jso.put("caret", caretPathJson);
			return jso.toString();
		} catch (JSONException e) {
			// only if integers are not valid doubles
		}
		return "";
	}

	/**
	 * @param text
	 *            JSON encoded editor state
	 * @return editor content or null if not valid
	 */
	public static EditorStateDescription fromJSON(String text) {
		ArrayList<Integer> caretPath = new ArrayList<>();
		try {
			JSONObject jso = new JSONObject(new JSONTokener(text));
			JSONArray caretPathJson = jso.optJSONArray("caret");
			if (caretPathJson != null) {
				for (int index = 0; index < caretPathJson.length(); index++) {
					caretPath.add(caretPathJson.getInt(index));
				}
			}
			return new EditorStateDescription(jso.getString("content"),
					caretPath);
		} catch (JSONException e) {
			Log.debug(e);
		}
		return null;
	}

	/**
	 * @return editor content
	 */
	public String getContent() {
		return text;
	}

	/**
	 * @return caret path as list of indices in formula tree
	 */
	public ArrayList<Integer> getCaretPath() {
		return caretPath;
	}

}
