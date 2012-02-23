package geogebra.web.factories;

import geogebra.common.util.HttpRequest;

import com.google.gwt.http.client.*;

public class UtilFactory extends geogebra.common.factories.UtilFactory {

	public HttpRequest newHttpRequest() {
		return new geogebra.web.util.HttpRequest();
	}
}
