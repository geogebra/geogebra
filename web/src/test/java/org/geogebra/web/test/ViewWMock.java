package org.geogebra.web.test;

import java.util.Map;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.util.ViewW;

public class ViewWMock extends ViewW {

	/**
	 * @param app application
	 */
	public ViewWMock(AppW app) {
		super(app);
	}

	/**
	 * @param zip GeoGebra file
	 * @return string representation (compatible with {@link #setFileFromJsonString})
	 */
	public static String toJson(GgbFile zip) {
		JSONArray archive = new JSONArray();
		for(Map.Entry<String, String> entry: zip.entrySet()){
			try {
				JSONObject archiveEntry = new JSONObject();
				archiveEntry.put("fileName", entry.getKey());
				archiveEntry.put("fileContent", entry.getValue());
				archive.put(archiveEntry);
			} catch (JSONException e) {
				e.printStackTrace();
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
			e.printStackTrace();
		}
	}
}
