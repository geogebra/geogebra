package org.geogebra.web.test;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ViewW;

public class ViewWMock extends ViewW {

	/**
	 * @param app application
	 */
	public ViewWMock(AppW app) {
		super(app);
	}

	@Override
	public void processJSON(String encoded) {
		try {
			JSONArray array = new JSONArray(encoded);
			prepare(array.length());
			for (int i = 0; i < array.length(); i++) {
				JSONObject content = array.getJSONObject(i);
				putIntoArchiveContent(content.getString("fileName"),
						content.getString("fileContent"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
