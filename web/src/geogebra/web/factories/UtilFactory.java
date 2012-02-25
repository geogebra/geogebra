package geogebra.web.factories;

import geogebra.common.util.HttpRequest;
import geogebra.common.util.URLEncoder;

import com.google.gwt.http.client.*;

public class UtilFactory extends geogebra.common.factories.UtilFactory {

	public HttpRequest newHttpRequest() {
		return new geogebra.web.util.HttpRequest();
	}

	public URLEncoder newURLEncoder() {
	    return new geogebra.web.util.URLEncoder();
    }
}
