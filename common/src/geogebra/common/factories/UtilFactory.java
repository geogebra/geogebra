package geogebra.common.factories;

import geogebra.common.util.HttpRequest;
import geogebra.common.util.URLEncoder;

public abstract class UtilFactory {
	public static UtilFactory prototype;

	public abstract HttpRequest newHttpRequest();
	public abstract URLEncoder newURLEncoder();
}
