package org.geogebra.common.media;

/**
 * Result of packUrl with error code.
 * 
 * @author laszlo
 *
 */
public class MebisURL extends VideoURL {
	private MebisError error;

	/**
	 * Constructor
	 * 
	 * @param url
	 *            to set.
	 * @param error
	 *            to set.
	 */
	public MebisURL(String url, MebisError error) {
		super(url, error == MebisError.NONE, MediaFormat.VIDEO_MEBIS);
		this.error = error;
	}

	/**
	 * 
	 * @return error if any.
	 */
	public MebisError getError() {
		return error;
	}
}