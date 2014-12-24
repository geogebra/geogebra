package geogebra.util;

import java.io.UnsupportedEncodingException;

public class URLEncoder extends geogebra.common.util.URLEncoder {

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
