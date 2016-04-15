package org.geogebra.web.html5.util;

import org.geogebra.common.util.URLEncoder;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org> Implements URL encoding for web
 */
public class URLEncoderW extends URLEncoder {

	@Override
	public String encode(String decodedURL) {
		return com.google.gwt.http.client.URL.encode(decodedURL);
	}

}
