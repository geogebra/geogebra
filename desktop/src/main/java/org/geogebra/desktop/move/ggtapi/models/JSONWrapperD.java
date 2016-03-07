package org.geogebra.desktop.move.ggtapi.models;

import org.geogebra.common.move.ggtapi.models.JSONWrapper;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;

public class JSONWrapperD implements JSONWrapper {
	private JSONObject impl;

	public JSONWrapperD(JSONObject obj) {
		this.impl = obj;
	}

	public boolean has(String string) {
		return impl.has(string);
	}

	public Object get(String string) {
		try {
			return impl.get(string);
		} catch (JSONException e) {
			return null;
		}
	}

}
