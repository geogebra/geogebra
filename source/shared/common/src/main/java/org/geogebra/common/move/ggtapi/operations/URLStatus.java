package org.geogebra.common.move.ggtapi.operations;

/**
 * Result of status check.
 */
public class URLStatus {
	private String errorKey;
	private String url;

	/**
	 * Constructor
	 */
	public URLStatus() {
		this.errorKey = null;
	}

	/**
	 * @param errorKey key for Localization.getError or null if there is no error
	 */
	public URLStatus(String errorKey) {
		this.errorKey = errorKey;
	}

	/**
	 * @param errorKey key for Localization.getError or null if there is no error
	 */
	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}

	/**
	 * @return key for Localization.getError
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
	 * @param embedUrl URL for embedding
	 * @return this
	 */
	public URLStatus withUrl(String embedUrl) {
		this.url = embedUrl;
		return this;
	}
}
