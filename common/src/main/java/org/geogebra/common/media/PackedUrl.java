package org.geogebra.common.media;

/**
 * Result of packUrl with error code.
 * 
 * @author laszlo
 *
 */
public class PackedUrl {
	private String url;
	private MebisError error;

	/**
	 * Constructor
	 * 
	 * @param url
	 *            to set.
	 * @param error
	 *            to set.
	 */
	public PackedUrl(String url, MebisError error) {
		this.url = url;
		this.error = error;
	}

	/**
	 * 
	 * @return the URL itself
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 
	 * @return error if any.
	 */
	public MebisError getError() {
		return error;
	}


}