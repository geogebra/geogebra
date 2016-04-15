package org.geogebra.desktop.factories;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.URLEncoder;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.util.HttpRequestD;
import org.geogebra.desktop.util.LoggerD;
import org.geogebra.desktop.util.URLEncoderD;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org> Desktop implementations for
 *         various utils
 */
public class UtilFactoryD extends UtilFactory {

	@Override
	public HttpRequest newHttpRequest() {
		return new HttpRequestD();
	}

	@Override
	public URLEncoder newURLEncoder() {
		return new URLEncoderD();
	}

	@Override
	public Log newGeoGebraLogger() {
		return new LoggerD();
	}

	@Override
	public Prover newProver() {
		return new org.geogebra.desktop.util.ProverD();
	}

}
