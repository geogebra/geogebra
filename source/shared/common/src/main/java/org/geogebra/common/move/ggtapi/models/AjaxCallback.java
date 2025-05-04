package org.geogebra.common.move.ggtapi.models;

/**
 * Callback for HTTP requests.
 */
public interface AjaxCallback {
	/**
	 * @param response response text
	 */
	void onSuccess(String response);

	/**
	 * Called if the request status is an error.
	 * @param error error message
	 */
	void onError(String error);
}
