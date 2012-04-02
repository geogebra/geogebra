package geogebra.factories;

import geogebra.common.util.DebugPrinter;
import geogebra.common.util.HttpRequest;
import geogebra.common.util.URLEncoder;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Desktop implementations for various utils
 */
public class UtilFactory extends geogebra.common.factories.UtilFactory {

	@Override
	public HttpRequest newHttpRequest() {
		return new geogebra.util.HttpRequest();
	}

	@Override
	public URLEncoder newURLEncoder() {
		return new geogebra.util.URLEncoder();
	}

	@Override
	public DebugPrinter newDebugPrinter() {
		return new geogebra.util.DebugPrinter();
	}

}
