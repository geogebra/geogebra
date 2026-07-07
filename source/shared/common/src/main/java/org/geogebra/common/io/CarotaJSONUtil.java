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

import java.util.function.Consumer;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;

public final class CarotaJSONUtil {
	private CarotaJSONUtil() {
		// utility class
	}

	/**
	 * Apply font size to all text parts that do not specify one.
	 * @param spans parts of text
	 * @param fontSize font size in pixels
	 */
	public static void setExplicitSize(JSONArray spans, int fontSize) {
		try {
			for (int i = 0; i < spans.length(); i++) {
				JSONObject word = spans.optJSONObject(i);
				if (word != null && !word.has("size")) {
					word.put("size", fontSize);
				}
			}
		} catch (JSONException ex) {
			Log.warn("Invalid content");
		}
	}

	/**
	 * Run an action for each table cell.
	 * @param tableContent table content
	 * @param action per-cell action
	 * @throws JSONException if JSON structure is invalid
	 */
	public static void forEachCell(JSONObject tableContent, Consumer<JSONArray> action)
			throws JSONException {
		JSONArray rows = tableContent.getJSONArray("content");
		for (int i = 0; i < rows.length(); i++) {
			JSONArray cells = rows.getJSONArray(i);
			for (int j = 0; j < cells.length(); j++) {
				JSONObject cell = cells.getJSONObject(j);
				if (cell.has("content")) {
					action.accept(cell.getJSONArray("content"));
				}
			}
		}
	}
}
