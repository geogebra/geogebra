package org.geogebra.web.html5.util.file;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.web.html5.factories.UtilFactoryW;

public class FileLoader {

	public interface Callback {
		void onLoad(String fileContent);
	}

	public static void loadFile(String fileUrl, final Callback callback) {
		HttpRequest httpRequest = UtilFactoryW.getPrototype().newHttpRequest();
		httpRequest.sendRequestPost("GET", fileUrl, null, new AjaxCallback() {
			@Override
			public void onSuccess(String response) {
				callback.onLoad(response);
			}

			@Override
			public void onError(String error) {
				System.err.println("ERROR: " + error);
			}
		});
	}
}
