package org.geogebra.common.move.ggtapi.models;


public interface AjaxCallback {
	public void onSuccess(String response);
	public void onError(String error);
}
