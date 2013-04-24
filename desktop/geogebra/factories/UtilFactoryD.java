package geogebra.factories;

import geogebra.common.util.HttpRequest;
import geogebra.common.util.Prover;
import geogebra.common.util.URLEncoder;
import geogebra.common.util.debug.GeoGebraLogger;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Desktop implementations for various utils
 */
public class UtilFactoryD extends geogebra.common.factories.UtilFactory {

	@Override
	public HttpRequest newHttpRequest() {
		return new geogebra.util.HttpRequest();
	}

	@Override
	public URLEncoder newURLEncoder() {
		return new geogebra.util.URLEncoder();
	}

	@Override
	public GeoGebraLogger newGeoGebraLogger() {
		return new geogebra.util.GeoGebraLogger();
	}

	@Override
	public Prover newProver() {
		return new geogebra.util.Prover();
	}

}
