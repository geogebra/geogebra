package geogebra.web.factories;

import geogebra.common.util.DebugPrinter;
import geogebra.common.util.HttpRequest;
import geogebra.common.util.URLEncoder;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Web implementations for various utils
 */
public class UtilFactory extends geogebra.common.factories.UtilFactory {

	@Override
    public HttpRequest newHttpRequest() {
		return new geogebra.web.util.HttpRequest();
	}

	@Override
    public URLEncoder newURLEncoder() {
	    return new geogebra.web.util.URLEncoder();
    }

	@Override
    public DebugPrinter newDebugPrinter() {
	    return new geogebra.web.util.DebugPrinter();
    }
}
