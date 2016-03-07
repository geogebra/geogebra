package org.geogebra.web.html5.util.ggtapi;

import org.geogebra.common.move.ggtapi.models.JSONWrapper;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class JSONWrapperW implements JSONWrapper {
	private JSONObject impl;

	public JSONWrapperW(JSONObject obj) {
		this.impl = obj;
	}

	public Object get(String string) {
		JSONValue val = impl.get(string);
		if (val.isString() != null) {
			return val.isString().stringValue();
		}
		if (val.isNumber() != null) {
			return val.isNumber().doubleValue();
		}
		if (val.isNumber() != null) {
			return val.isNumber().doubleValue();
		}
		return "";
	}

	public boolean has(String string) {
		// TODO Auto-generated method stub
		return impl.containsKey(string);
	}

}
