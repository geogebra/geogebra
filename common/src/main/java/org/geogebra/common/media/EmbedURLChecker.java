package org.geogebra.common.media;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.HttpRequest;

/**
 * Binding for MARVL's meta API, checks whether a page can be embedded
 */
public class EmbedURLChecker {
	private String baseURL = "https://groot.geogebra.org:5000/v1.0/";

	/**
	 * Result of status check.
	 */
	public static class URLStatus {
		private String errorKey;
		private String url;

		/**
		 * @param errorKey
		 *            key for Localization.getError or null if there is no error
		 */
		protected URLStatus(String errorKey) {
			this.errorKey = errorKey;
		}

		/**
		 * @return key for Localiyation.getError
		 */
		public String getErrorKey() {
			return errorKey;
		}

		/**
		 * @return page URL
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * @param embedUrl
		 *            URL for embedding
		 * @return this
		 */
		public URLStatus withUrl(String embedUrl) {
			this.url = embedUrl;
			return this;
		}

	}

	/**
	 * @param materialsAPIurl
	 *            base URL
	 */
	public EmbedURLChecker(String materialsAPIurl) {
		if (!materialsAPIurl.isEmpty()) {
			this.baseURL = materialsAPIurl;
		}
	}

	/**
	 * @param url
	 *            URL
	 * @param callback
	 *            webpage status handler handler
	 */
	public void checkEmbedURL(final String url,
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
					callback.callback(new URLStatus("InavalidInput"));
				}
			}

			@Override
			public void onError(String error) {
				// network problem
				callback.callback(new URLStatus("InvalidInput"));
			}

		});
	}
}
