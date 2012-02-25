package geogebra.web.util;

public class URLEncoder extends geogebra.common.util.URLEncoder {

	public String encode(String decodedURL) {
		return com.google.gwt.http.client.URL.encode(decodedURL);
	}

}
