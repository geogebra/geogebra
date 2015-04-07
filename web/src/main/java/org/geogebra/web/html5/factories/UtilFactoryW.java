package org.geogebra.web.html5.factories;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.URLEncoder;
import org.geogebra.common.util.debug.Log;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org> Web implementations for various
 *         utils
 */
public class UtilFactoryW extends UtilFactory {

	@Override
	public HttpRequest newHttpRequest() {
		return new org.geogebra.web.html5.util.HttpRequestW();
	}

	@Override
	public URLEncoder newURLEncoder() {
		return new org.geogebra.web.html5.util.URLEncoder();
	}

	@Override
	public Log newGeoGebraLogger() {
		return new org.geogebra.web.html5.util.debug.GeoGebraLogger();
	}

	@Override
	public Prover newProver() {
		return new org.geogebra.web.html5.util.Prover();
	}
}
