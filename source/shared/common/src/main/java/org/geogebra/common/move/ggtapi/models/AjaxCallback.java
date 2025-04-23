package org.geogebra.common.move.ggtapi.models;

/**
 * Callback for HTTP requests.
 */
public interface AjaxCallback {
	public void onSuccess(String response);

	public void onError(String error);
}
