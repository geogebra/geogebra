package org.geogebra.common.util;

import java.util.HashMap;

public abstract class AsyncOperation<T> {
	protected HashMap<String, Object> properties = new HashMap<String, Object>();

	public AsyncOperation() {

	}

	public abstract void callback(T obj);

}
