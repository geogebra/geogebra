package org.geogebra.web.touch;

public interface NativeSaveCallback {
	public void onSuccess(String fileID);

	public void onFailure();

}
