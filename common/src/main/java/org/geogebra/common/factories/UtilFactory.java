package org.geogebra.common.factories;

import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.URLEncoder;
import org.geogebra.common.util.debug.Log;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org> Various util-like factories for
 *         common usage
 */
public abstract class UtilFactory {
	/**
	 * UtilFactory prototype for various common utils
	 */
	private static UtilFactory prototype;

	private static final Object lock = new Object();

	public static UtilFactory getPrototype() {
		return prototype;
	}

	public static void setPrototypeIfNull(UtilFactory p) {

		synchronized (lock) {
			if (prototype == null) {
				prototype = p;
			}
		}
	}

	/**
	 * @return HttpRequest object Creates a HttpRequest object
	 */
	public abstract HttpRequest newHttpRequest();

	/**
	 * @return URLEncoder object Creates a URLEncoder object
	 */
	public abstract URLEncoder newURLEncoder();

	/**
	 * @return GeoGebraLogger Creates a GeoGebraLogger object
	 */
	public abstract Log newGeoGebraLogger();

	/**
	 * @return Prover Creates a Prover object
	 */
	public abstract Prover newProver();
}
