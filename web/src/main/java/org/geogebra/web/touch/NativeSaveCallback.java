package org.geogebra.web.touch;

public interface NativeSaveCallback {
	void onSuccess(String fileID);

	void onFailure();

}
