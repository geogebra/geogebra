package org.geogebra.web.web.helper;

import org.geogebra.common.util.debug.Log;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class CodeSplitCallback<T> implements AsyncCallback<T> {

	public void onFailure(Throwable reason) {
		Log.error("Code splitting failure: " + reason.getMessage()
		        + "\nTry reloading the page.");
	}

	public abstract void onSuccess(T result);

}
