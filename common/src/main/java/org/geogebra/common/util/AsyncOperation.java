package org.geogebra.common.util;

import java.util.HashMap;

public abstract class AsyncOperation {
	protected HashMap<String, Object> properties = new HashMap<String, Object>();

	public AsyncOperation() {

	}

	public abstract void callback(Object obj);

	public Object getData() {
		return this.properties.get("data");
	}

	public void setData(Object d) {
		this.properties.put("data", d);
	}

	public void setProperty(String propertyName, Object prop) {
		this.properties.put(propertyName, prop);
	}

	public Object getProperty(String propertyName) {
		return this.properties.get(propertyName);
	}

}
