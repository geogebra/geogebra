package org.geogebra.web.html5.util;

import org.geogebra.common.util.URLEncoder;

import elemental2.core.Global;

/**
 * Implements URL encoding for web
 * 
 * @author Zoltan Kovacs
 * 
 */
public class URLEncoderW extends URLEncoder {

	@Override
	public String encode(String decodedURL) {
		return Global.encodeURI(decodedURL);
	}

}
