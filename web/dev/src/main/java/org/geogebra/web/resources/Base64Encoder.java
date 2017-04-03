package org.geogebra.web.resources;

public class Base64Encoder {
	/**
	 * 
	 * Returns a base64 encoding of the specified (binary) string
	 * 
	 * @param text
	 *            A binary string (obtained for instance by the FileReader API)
	 * @return a base64 encoded string.
	 */
	public static native String encodeBase64(String text)/*-{
		return btoa(text);
	}-*/;
}
