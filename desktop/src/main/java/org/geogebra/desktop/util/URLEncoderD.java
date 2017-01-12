package org.geogebra.desktop.util;

import java.io.UnsupportedEncodingException;

import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.URLEncoder;

public class URLEncoderD extends URLEncoder {

	@Override
	public String encode(String decodedURL) {

		try {
			return java.net.URLEncoder.encode(decodedURL, Charsets.UTF_8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
