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

package org.geogebra.web.test;

import java.util.Map;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.util.ArchiveEntry;
import org.geogebra.web.html5.util.ArchiveLoader;

public class ArchiveLoaderMock extends ArchiveLoader {

	/**
	 * @param app application
	 */
	public ArchiveLoaderMock(AppW app) {
		super(app);
	}

	/**
	 * @param zip GeoGebra file
	 * @return string representation (compatible with {@link #setFileFromJsonString})
	 */
	public static String toJson(GgbFile zip) {
		JSONArray archive = new JSONArray();
		for (Map.Entry<String, ArchiveEntry> entry: zip.entrySet()) {
			try {
				JSONObject archiveEntry = new JSONObject();
				archiveEntry.put("fileName", entry.getKey());
				archiveEntry.put("fileContent", entry.getValue().string);
				archive.put(archiveEntry);
			} catch (JSONException e) {
				Log.debug(e);
			}
		}
		return archive.toString();
	}

	@Override
	public void processJSON(String encoded) {
		GgbFile archiveContent = new GgbFile();
		setFileFromJsonString(encoded, archiveContent);
		maybeLoadFile(archiveContent);
	}

	@Override
	public void setFileFromJsonString(String encoded, GgbFile archiveContent) {
		try {
			JSONArray array = new JSONArray(encoded);
			for (int i = 0; i < array.length(); i++) {
				JSONObject content = array.getJSONObject(i);
				archiveContent.put(content.getString("fileName"),
						content.getString("fileContent"));
			}
		} catch (JSONException e) {
			Log.debug(e);
		}
	}
}
