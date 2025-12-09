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

package org.geogebra.common.media;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.move.ggtapi.operations.URLChecker;
import org.geogebra.common.move.ggtapi.operations.URLStatus;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.HttpRequest;

/**
 * Binding for MARVL's meta API, checks whether a page can be embedded
 */
public class EmbedURLChecker implements URLChecker {
	private String baseURL = "https://api.geogebra.org/v1.0";

	/**
	 * @param materialsAPIurl
	 *            base URL
	 */
	public EmbedURLChecker(String materialsAPIurl) {
		if (!materialsAPIurl.isEmpty()) {
			this.baseURL = materialsAPIurl;
		}
	}

	@Override
	public void check(final String url,
			final AsyncOperation<URLStatus> callback) {
		HttpRequest xhr = UtilFactory.getPrototype().newHttpRequest();
		xhr.sendRequestPost("GET", baseURL + "/meta?url=" + url, null,
				new AjaxCallback() {
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject json = new JSONObject(new JSONTokener(response));
					if (json.has("error")) {
						// unknown host
						callback.callback(new URLStatus("InvalidInput"));
						return;
					}
					boolean frame = json.getBoolean("frameAllowed");
					callback.callback(
							frame ? new URLStatus(null)
									.withUrl(url)
									: new URLStatus("FrameLoadError"));
				} catch (JSONException e) {
					callback.callback(new URLStatus("InvalidInput"));
				}
			}

			@Override
			public void onError(String error) {
				// network problem
				callback.callback(new URLStatus("InvalidInput"));
			}

		});
	}

	@Override
	public boolean hasFrameOptionCheck() {
		return true;
	}
}
