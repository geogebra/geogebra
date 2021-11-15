package org.geogebra.web.html5.util.file;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.factories.UtilFactoryW;

/**
 * Loads a file and sets a callback.
 */
public class FileLoader {

	public interface Callback {
		void onLoad(String fileContent);
	}

	/**
	 * Loads a file from the fileUrl and sets the callback to execute after loading the file.
	 * @param fileUrl The url of the file.
	 * @param callback The callback to be executed after loading the file.
	 */
	public static void loadFile(String fileUrl, final Callback callback) {
		HttpRequest httpRequest = UtilFactoryW.getPrototype().newHttpRequest();
		httpRequest.sendRequestPost("GET", fileUrl, null, new AjaxCallback() {
			@Override
			public void onSuccess(String response) {
				callback.onLoad(response);
			}

			@Override
			public void onError(String error) {
				Log.error("ERROR: " + error);
			}
		});
	}
}
