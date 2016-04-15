package org.geogebra.desktop.util;

import java.io.UnsupportedEncodingException;

import org.geogebra.common.util.URLEncoder;

public class URLEncoderD extends URLEncoder {

	public String encode(String decodedURL) {

		try {
			return java.net.URLEncoder.encode(decodedURL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
