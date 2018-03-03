package org.geogebra.web.html5.util;

import org.geogebra.common.util.URLEncoder;

/**
 * Implements URL encoding for web
 * 
 * @author Zoltan Kovacs
 * 
 */
public class URLEncoderW extends URLEncoder {

	@Override
	public String encode(String decodedURL) {
		return com.google.gwt.http.client.URL.encode(decodedURL);
	}

}
