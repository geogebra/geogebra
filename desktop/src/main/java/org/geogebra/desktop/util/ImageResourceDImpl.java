package org.geogebra.desktop.util;

public class ImageResourceDImpl implements ImageResourceD {
	private String fn;

	/**
	 * @param fn
	 *            filename
	 */
	public ImageResourceDImpl(String fn) {
		this.fn = fn;
	}

	@Override
	public String getFilename() {
		return fn;
	}

}
