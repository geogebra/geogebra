package geogebra.factories;

import geogebra.common.util.HttpRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UtilFactory extends geogebra.common.factories.UtilFactory {

	@Override
	public HttpRequest newHttpRequest() {
		return new geogebra.util.HttpRequest();
	}

}
