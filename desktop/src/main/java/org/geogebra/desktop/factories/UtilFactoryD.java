package org.geogebra.desktop.factories;

import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.URLEncoder;
import org.geogebra.common.util.debug.Log;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org> Desktop implementations for
 *         various utils
 */
public class UtilFactoryD extends org.geogebra.common.factories.UtilFactory {

	@Override
	public HttpRequest newHttpRequest() {
		return new org.geogebra.desktop.util.HttpRequestD();
	}

	@Override
	public URLEncoder newURLEncoder() {
		return new org.geogebra.desktop.util.URLEncoder();
	}

	@Override
	public Log newGeoGebraLogger() {
		return new org.geogebra.desktop.util.GeoGebraLogger();
	}

	@Override
	public Prover newProver() {
		return new org.geogebra.desktop.util.Prover();
	}

}
