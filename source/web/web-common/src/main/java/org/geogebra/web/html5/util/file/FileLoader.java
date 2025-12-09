/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.util.file;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.factories.UtilFactoryW;

/**
 * Loads a file and sets a callback.
 */
public class FileLoader {

	/**
	 * File load callback.
	 */
	public interface Callback {
		/**
		 * @param fileContent file content (plain text)
		 */
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
