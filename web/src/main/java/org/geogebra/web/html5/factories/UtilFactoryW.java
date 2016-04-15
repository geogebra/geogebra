package org.geogebra.web.html5.factories;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.URLEncoder;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.util.HttpRequestW;
import org.geogebra.web.html5.util.debug.LoggerW;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org> Web implementations for various
 *         utils
 */
public class UtilFactoryW extends UtilFactory {

	@Override
	public HttpRequest newHttpRequest() {
		return new HttpRequestW();
	}

	@Override
	public URLEncoder newURLEncoder() {
		return new org.geogebra.web.html5.util.URLEncoder();
	}

	@Override
	public Log newGeoGebraLogger() {
		return new LoggerW();
	}

	@Override
	public Prover newProver() {
		return new org.geogebra.web.html5.util.Prover();
	}
}
